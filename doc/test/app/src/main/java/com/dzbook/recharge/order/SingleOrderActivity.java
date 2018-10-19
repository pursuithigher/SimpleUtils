package com.dzbook.recharge.order;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.dzbook.BaseLoadActivity;
import com.dzbook.event.EventConstant;
import com.dzbook.event.EventMessage;
import com.dzbook.mvp.UI.SingleOrderUI;
import com.dzbook.mvp.presenter.SingleOrderPresenter;
import com.dzbook.utils.TypefaceUtils;
import com.dzbook.utils.hw.LoginUtils;
import com.dzbook.view.DianZhongCommonTitle;
import com.dzbook.view.order.CommonOrdersView;
import com.dzpay.recharge.bean.RechargeErrType;
import com.dzpay.recharge.netbean.SingleOrderBeanInfo;
import com.dzpay.recharge.netbean.SingleOrderPageBean;
import com.ishugui.R;
import com.iss.app.BaseActivity;

/**
 * SingleOrderActivity
 * @author lizz 2017/8/4.
 */

public class SingleOrderActivity extends BaseLoadActivity implements SingleOrderUI {

    /**
     * TAG
     */
    public static final String TAG = "SingleOrderActivity";
    private DianZhongCommonTitle commonTitle;

    private Button btOrder;

    private TextView tvLotOrderDiscount;

    private CommonOrdersView commonOrders;

    private SingleOrderPresenter mPresenter;

    private boolean isNeedRefreshUI;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.dz_recharge_single_order);
    }


    @Override
    protected void initData() {
        mPresenter = new SingleOrderPresenter(this);

        mPresenter.generateTrackd();

        mPresenter.getParamsInfo();

        if (null == mPresenter.getParams() || mPresenter.getParams().isEmpty()) {
            return;
        }

        mPresenter.getOrderInfo();

    }

    @Override
    protected void setListener() {

        commonTitle.setLeftClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.dialogCancel(RechargeErrType.VIEW_BACK, getString(R.string.str_single_order_cancel_back), true);
            }
        });

    }

    @Override
    protected void initView() {
        commonTitle = findViewById(R.id.commontitle);
        btOrder = findViewById(R.id.bt_order);
        commonOrders = findViewById(R.id.common_orders);
        tvLotOrderDiscount = findViewById(R.id.tv_lot_order_discount);

        TypefaceUtils.setHwChineseMediumFonts(btOrder);
    }

    @Override
    public String getTagName() {
        return TAG;
    }

    @Override
    protected void onResume() {
        super.onResume();

        mPresenter.onResume();

        mPresenter.setUmengEventSum();
        //日志打点
        mPresenter.pvLog();

        if (isNeedRefreshUI) {
            mPresenter.refreshUIPage();
        }
    }

    @Override
    public void setViewOrderInfo(SingleOrderBeanInfo bean) {

        if (bean != null) {
            if (bean.orderPage != null) {

                SingleOrderPageBean orderPage = bean.orderPage;

                if (orderPage.isSingleBook()) {
                    commonOrders.bindSingleBookData(mPresenter.getCommonOrdersInfo(bean));
                } else {
                    commonOrders.bindSerialBookData(mPresenter.getCommonOrdersInfo(bean));
                }
                setOrderButton(bean);
            }
        }
    }


    private void setOrderButton(final SingleOrderBeanInfo bean) {
        final SingleOrderPageBean orderPage = bean.orderPage;
        if (!TextUtils.isEmpty(bean.orderPage.lotTips)) {
            tvLotOrderDiscount.setVisibility(View.VISIBLE);
            tvLotOrderDiscount.setText(bean.orderPage.lotTips);
        }

        String textRight = orderPage.isNeedRecharge() ? getString(R.string.str_single_order_recharge_pay) : getString(R.string.str_now_buy);
        btOrder.setText(textRight);

        btOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoginUtils.getInstance().forceLoginCheck(getActivity(), new LoginUtils.LoginCheckListener() {
                    @Override
                    public void loginComplete() {
                        if (!orderPage.isNeedRecharge()) {
                            mPresenter.toPay(bean, commonOrders.isAutoOrderChecked());
                        } else {
                            mPresenter.toRecharge(bean, "主动进入", commonOrders.isAutoOrderChecked());
                        }
                    }
                });
            }
        });

        tvLotOrderDiscount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoginUtils.getInstance().forceLoginCheck(getActivity(), new LoginUtils.LoginCheckListener() {
                    @Override
                    public void loginComplete() {
                        if (mPresenter != null) {
                            mPresenter.lotOrder();
                        }
                    }
                });

            }
        });
    }


    @Override
    public void showDataError() {

    }

    @Override
    public void onBackPressed() {
        mPresenter.dialogCancel(RechargeErrType.SYSTEM_BACK, "订购SYSTEM_BACK", true);
    }

    @Override
    public void finishThisActivity(boolean isNeedAnim) {
        if (isNeedAnim) {
           finish();
        } else {
            finishNoAnimation();
        }
    }

    @Override
    protected void onDestroy() {
        if (mPresenter != null) {
            mPresenter.onDestroy();
        }
        super.onDestroy();
    }

    @Override
    public Context getContext() {
        return getActivity();
    }

    @Override
    public BaseActivity getHostActivity() {
        return this;
    }

    @Override
    protected boolean isCustomPv() {
        return true;
    }

    @Override
    public void onEventMainThread(EventMessage event) {
        super.onEventMainThread(event);
        int requestCode = event.getRequestCode();
        switch (requestCode) {
            case EventConstant.CODE_VIP_OPEN_SUCCESS_REFRESH_STATUS: {
                if (getActivity() != null && !getActivity().isFinishing()) {
                    isNeedRefreshUI = true;
                }
                break;
            }
            default:
                break;
        }
    }
}
