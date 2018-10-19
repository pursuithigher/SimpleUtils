package com.dzbook.recharge.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;

import com.dzbook.BaseSwipeBackActivity;
import com.dzbook.adapter.RechargeWayMoneyAdapter;
import com.dzbook.event.EventConstant;
import com.dzbook.event.EventMessage;
import com.dzbook.mvp.UI.RechargeListUI;
import com.dzbook.mvp.presenter.RechargeListPresenter;
import com.dzbook.utils.SpUtil;
import com.dzbook.utils.TypefaceUtils;
import com.dzbook.view.CustomerGridView;
import com.dzbook.view.DianZhongCommonTitle;
import com.dzbook.view.ElasticScrollView;
import com.dzbook.view.common.StatusView;
import com.dzbook.view.order.CommonOrdersView;
import com.dzbook.view.order.OrderTitle;
import com.dzpay.recharge.netbean.OrdersCommonBean;
import com.dzpay.recharge.netbean.RechargeListBeanInfo;
import com.dzpay.recharge.netbean.RechargeProductBean;
import com.ishugui.R;
import com.iss.app.BaseActivity;

/**
 * RechargeListActivity
 * @author lizz 2017/8/28.
 */

public class RechargeListActivity extends BaseSwipeBackActivity implements RechargeListUI {

    /**
     * TAG =
     */
    public static final String TAG = "RechargeListActivity";

    private RechargeListPresenter mPresenter;

    private StatusView statusView;

    private DianZhongCommonTitle mTitleView;

    private ElasticScrollView scrollviewRechargeList;

    private CommonOrdersView rechargeOrdersView;

    private CustomerGridView gridRecharge;

    private RechargeWayMoneyAdapter rechargeWayMoneyAdapter;

    private Button buttonRecharge;

    private View viewOrderLine;
    private OrderTitle packOrderTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dz_recharge_list1);
    }

    @Override
    protected void initView() {
        statusView = findViewById(R.id.defaultview_nonet);
        packOrderTitle = findViewById(R.id.pack_order_title);
        mTitleView = findViewById(R.id.commontitle);
        scrollviewRechargeList = findViewById(R.id.scrollview_recharge_list);
        rechargeOrdersView = findViewById(R.id.order_info);
        gridRecharge = findViewById(R.id.grid_recharge);
        buttonRecharge = findViewById(R.id.button_recharge);
        viewOrderLine = findViewById(R.id.view_order_line);

        TypefaceUtils.setHwChineseMediumFonts(buttonRecharge);
    }

    @Override
    protected void initData() {

        setSwipeBackEnable(false);

        mPresenter = new RechargeListPresenter(this);
        SpUtil.getinstance(this).setOpenRechargelistTimes();
        mPresenter.generateTrackd();

        if (mPresenter.getIntent() == null) {
            finishActivity();
            return;
        }

        mPresenter.getParamInfo();
        mPresenter.getRechargeInfo();

        rechargeWayMoneyAdapter = new RechargeWayMoneyAdapter(this);
        gridRecharge.setAdapter(rechargeWayMoneyAdapter);
    }

    @Override
    protected void setListener() {

        statusView.setNetErrorClickListener(new StatusView.NetErrorClickListener() {
            @Override
            public void onNetErrorEvent(View v) {
                statusView.setVisibility(View.GONE);
                if (mPresenter != null) {
                    mPresenter.getRechargeInfo();
                }
            }
        });
        gridRecharge.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                rechargeWayMoneyAdapter.setSelectionPosition(position);
                RechargeProductBean bean = (RechargeProductBean) parent.getItemAtPosition(position);
                setSelectedRechargeProduct(bean);
            }
        });

        mTitleView.setLeftClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mPresenter != null) {
                    mPresenter.dzLogCancel();
                    mPresenter.dzObserverCancel("返回键取消");
                }
            }
        });
    }

    @Override
    public void setRechargeListData(RechargeListBeanInfo beanInfo, int defaultSelectedPosition, RechargeProductBean product) {
        rechargeWayMoneyAdapter.setSelectionPosition(defaultSelectedPosition);
        rechargeWayMoneyAdapter.addItems(beanInfo.productBeans, true);
        setSelectedRechargeProduct(product);
    }

    @Override
    public void setSelectedRechargeProduct(final RechargeProductBean product) {
        buttonRecharge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (mPresenter != null) {
                    mPresenter.buttonRecharge(product);
                }
            }
        });
    }

    @Override
    public void setRequestDataSuccess() {
        scrollviewRechargeList.setVisibility(View.VISIBLE);
        statusView.showSuccess();
    }

    @Override
    public void showLoadProgress() {
        statusView.setVisibility(View.VISIBLE);
        statusView.showLoading();
    }

    @Override
    public void setNetErrorShow() {
        statusView.setVisibility(View.VISIBLE);
        statusView.showNetError();
    }

    @Override
    public void setOrdersInfo(OrdersCommonBean bean) {
        rechargeOrdersView.setVisibility(View.VISIBLE);
        viewOrderLine.setVisibility(View.VISIBLE);
        rechargeOrdersView.bindRechargeOrdersData(bean);
    }

    @Override
    public void isShowNotNetDialog() {
        RechargeListActivity.this.showNotNetDialog();
    }

    @Override
    public void setPackBookOrderInfo(String title, String costPrice, String payPrice, String balance) {
        packOrderTitle.setVisibility(View.VISIBLE);
        packOrderTitle.bindData(title, costPrice, payPrice, balance);
        scrollviewRechargeList.smoothScrollTo(0, 0);
    }

    @Override
    public BaseActivity getHostActivity() {
        return this;
    }


    @Override
    protected void onResume() {
        super.onResume();
        //打点日志
        if (mPresenter != null) {
            mPresenter.dzPvLog();
            mPresenter.onResume();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mPresenter != null) {
            mPresenter.destroy();
        }

    }


    @Override
    public String getTagName() {
        return TAG;
    }


    @Override
    public void finishActivity() {
        super.finish();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        if (mPresenter != null) {
            mPresenter.dzLogCancel();
            mPresenter.dzObserverCancel("充值SYSTEM_BACK");
        }
    }


    @Override
    protected boolean isCustomPv() {
        return true;
    }

    @Override
    public void onEventMainThread(EventMessage event) {
        super.onEventMainThread(event);
        int requestCode = event.getRequestCode();
        String type = event.getType();
        if (EventConstant.TYPE_RECHARGE_LIST.equals(type)) {
            switch (requestCode) {
                case EventConstant.LOGIN_SUCCESS_FINISH_RECHARGE_PREGRESS_REQUESTCODE: {
                    if (getActivity() != null && !getActivity().isFinishing()) {
                        mPresenter.rechargeSuccessObserver();
                    }
                    break;
                }
                case EventConstant.LOGIN_CANCEL_FINISH_RECHARGE_PREGRESS_REQUESTCODE: {
                    if (getActivity() != null && !getActivity().isFinishing()) {
                        mPresenter.rechargeSuccessObserver();
                    }
                    break;
                }
                default:
                    break;
            }
        }
    }

}
