package com.dzbook.recharge.order;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.dzbook.BaseSwipeBackActivity;
import com.dzbook.event.EventConstant;
import com.dzbook.event.EventMessage;
import com.dzbook.lib.utils.ALog;
import com.dzbook.mvp.UI.LotOrderUI;
import com.dzbook.mvp.presenter.LotOrderPresenter;
import com.dzbook.utils.ThirdPartyLog;
import com.dzbook.utils.TypefaceUtils;
import com.dzbook.utils.hw.LoginUtils;
import com.dzbook.view.CustomerGridView;
import com.dzbook.view.DianZhongCommonTitle;
import com.dzbook.view.order.CommonOrdersView;
import com.dzbook.view.order.LotOrderRuleView;
import com.dzpay.recharge.bean.RechargeErrType;
import com.dzpay.recharge.netbean.LotOrderPageBean;
import com.dzpay.recharge.netbean.LotOrderPageBeanInfo;
import com.dzpay.recharge.netbean.OrdersCommonBean;
import com.ishugui.R;
import com.iss.app.BaseActivity;


/**
 * LotOrderPageActivity
 * @author lizz 2017/8/2.
 */

public class LotOrderPageActivity extends BaseSwipeBackActivity implements LotOrderUI {
    /**
     * TAG
     */
    public static final String TAG = "LotOrderPageActivity";
    private DianZhongCommonTitle commonTitle;

    private CommonOrdersView viewCommonOrders;

    private CustomerGridView lvLotOrder;

    private LotOrderRuleView lotOrderRuleView;

    private Button btOrder;

    private LotOrderPresenter mPresenter;

    private LotOrderAdapter lotOrderAdapter;

    private boolean isNeedRefreshUI;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.dz_recharge_lot_order);
    }

    @Override
    protected void initView() {
        commonTitle = findViewById(R.id.commontitle);
        lvLotOrder = findViewById(R.id.grid_lot_order);
        lotOrderRuleView = findViewById(R.id.lot_order_rule_view);
        viewCommonOrders = findViewById(R.id.common_orders);
        btOrder = findViewById(R.id.bt_order);
        TypefaceUtils.setHwChineseMediumFonts(btOrder);
    }

    @Override
    protected void initData() {

        mPresenter = new LotOrderPresenter(this);

        setSwipeBackEnable(false);

        mPresenter.generterTrackId();

        lotOrderAdapter = new LotOrderAdapter(this);
        lotOrderAdapter.setLotOrderUI(this);

        lvLotOrder.setAdapter(lotOrderAdapter);
        mPresenter.getParamsInfo();

        if (null == mPresenter.getParams() || mPresenter.getParams().isEmpty()) {
            ALog.eZz("批量订购页面参数未获取到");
            finish();
            return;
        }

        mPresenter.getLotOrderInfo();
    }

    @Override
    protected void setListener() {
        commonTitle.setLeftClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.dialogCancel(RechargeErrType.SYSTEM_BACK, "订购SYSTEM_BACK");
            }
        });
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
    public void setSingleLotOrderInfo(LotOrderPageBeanInfo beanInfo, boolean isReader) {
        lvLotOrder.setVisibility(View.GONE);
        lotOrderRuleView.setVisibility(View.GONE);

        if (beanInfo != null && beanInfo.lotOrderPageBeans != null && beanInfo.lotOrderPageBeans.size() > 0) {

            LotOrderPageBean item = beanInfo.lotOrderPageBeans.get(0);
            buttonOrderListener(item);
        }
    }

    @Override
    public void setSerialLotOrderInfo(LotOrderPageBeanInfo beanInfo, boolean isReader) {

        LotOrderPageBean item = beanInfo.lotOrderPageBeans.get(0);
        buttonOrderListener(item);

        lotOrderAdapter.addItems(beanInfo.lotOrderPageBeans, true);
    }

    @Override
    public void showDataError() {
        showMessage(R.string.data_error_please_retry);
        finish();
    }

    @Override
    public void onSelected(int position, final LotOrderPageBean bean) {

        if (bean != null) {
            buttonOrderListener(bean);
            ThirdPartyLog.onEvent(getContext(), ThirdPartyLog.CASH_ORDER_LOT);
        }
    }


    private void buttonOrderListener(final LotOrderPageBean bean) {

        OrdersCommonBean ordersInfo = mPresenter.getCommonOrdersInfo(mPresenter.getLotOrderPageBeanInfo(), bean);
        viewCommonOrders.bindLotOrderData(ordersInfo);

        String textRight = bean.isNeedRecharge() ? getString(R.string.recharge_recharge_and_purchase) : getString(R.string.recharge_buy_now);

        btOrder.setText(textRight);

        btOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoginUtils.getInstance().forceLoginCheck(getActivity(), new LoginUtils.LoginCheckListener() {
                    @Override
                    public void loginComplete() {
                        if (!bean.isNeedRecharge()) {
                            mPresenter.toPay(bean, false);
                        } else {
                            mPresenter.toRecharge(bean, "主动进入");
                        }
                    }
                });
            }
        });
    }


    @Override
    protected boolean isCustomPv() {
        return true;
    }

    @Override
    public void onBackPressed() {
        mPresenter.dialogCancel(RechargeErrType.SYSTEM_BACK, "订购SYSTEM_BACK");
    }

    @Override
    public void setSelection(int position) {
        lotOrderAdapter.setSelection(false, position);
    }

    @Override
    public Context getContext() {
        return getActivity();
    }


    @Override
    public void finish() {
        super.finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LotOrderPresenter.onDestroy();
    }

    @Override
    public String getTagName() {
        return TAG;
    }

    @Override
    public BaseActivity getHostActivity() {
        return this;
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
