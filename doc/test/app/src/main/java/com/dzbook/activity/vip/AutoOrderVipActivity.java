package com.dzbook.activity.vip;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.dzbook.BaseSwipeBackActivity;
import com.dzbook.activity.continuous.AutoOrderVipListActivity;
import com.dzbook.dialog.DialogMenuManage;
import com.dzbook.lib.utils.CompatUtils;
import com.dzbook.mvp.UI.AutoOrderVipStatusUI;
import com.dzbook.mvp.presenter.AutoOrderVipStatusPresenter;
import com.dzbook.utils.DzSpanBuilder;
import com.dzbook.utils.NetworkUtils;
import com.dzbook.utils.ThirdPartyLog;
import com.dzbook.view.DianZhongCommonTitle;
import com.dzbook.view.common.StatusView;
import com.ishugui.R;
import com.iss.app.BaseActivity;

import java.util.ArrayList;
import java.util.List;

import hw.sdk.net.bean.vip.VipAutoRenewStatus;

/**
 * 自动续订
 *
 * @author KongXP 2018/4/21.
 */
public class AutoOrderVipActivity extends BaseSwipeBackActivity implements AutoOrderVipStatusUI {

    /**
     * 用户状态
     */
    private TextView mTvUserStatus;
    /**
     * 截止时间
     */
    private TextView mTvStatusEndTime;
    /**
     * 自动续费费用
     */
    private TextView mTvAutoPayPrice;
    /**
     * 自动续费时间
     */
    private TextView mTvAutoPayTime;

    private TextView mTvAutoOrdersVipMsg;

    private StatusView mStatusView;

    private DialogMenuManage mDialogMenuManage;

    private long lastClickTime = 0;

    private AutoOrderVipStatusPresenter mPresenter;

    private DianZhongCommonTitle mTitleView;

    /**
     * 打开
     *
     * @param activity activity
     */
    public static void launch(Activity activity) {
        Intent intent = new Intent();
        intent.setClass(activity, AutoOrderVipActivity.class);
        activity.startActivity(intent);
        BaseActivity.showActivity(activity);
    }

    @Override
    public String getTagName() {
        return "AutoOrderVipActivity";
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ac_auto_order_vip_status);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mPresenter != null) {
            mPresenter.destroy();
        }

        if (mDialogMenuManage != null) {
            mDialogMenuManage.dismiss();
            mDialogMenuManage = null;
        }
    }

    @Override
    protected void initView() {
        mTitleView = findViewById(R.id.commontitle);
        mTitleView.setRightIconVisibility(View.GONE);
        mTvUserStatus = findViewById(R.id.tv_user_status);
        mTvStatusEndTime = findViewById(R.id.tv_status_end_time);
        mTvAutoPayPrice = findViewById(R.id.tv_auto_renew_price);
        mTvAutoPayTime = findViewById(R.id.tv_auto_renew_time);
        mTvAutoOrdersVipMsg = findViewById(R.id.tv_auto_orders_vip_msg);

        mStatusView = findViewById(R.id.defaultview_nonet);
    }

    @Override
    protected void initData() {
        mPresenter = new AutoOrderVipStatusPresenter(this, this);

        if (!NetworkUtils.getInstance().checkNet()) {
            showNoNetView();
            return;
        }

        mPresenter.getAutoOrderVipStatus();
    }

    @Override
    protected void setListener() {
        mTitleView.setLeftClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ThirdPartyLog.onEventValueOldClick(getContext(), ThirdPartyLog.CONTINUOUS_MONTHLY_STATUS_BACK, null, 1);
                finish();

            }
        });
        mTitleView.setRightClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                long currentClickTime = System.currentTimeMillis();
                if (currentClickTime - lastClickTime > 0) {
                    ThirdPartyLog.onEventValueOldClick(getContext(), ThirdPartyLog.AUTO_ORDER_VIP_TITLE_ID, null, 1);
                    if (mDialogMenuManage == null) {
                        List<String> list = new ArrayList<>();
                        list.add(getResources().getString(R.string.continuous_monthly_status_see_data));
                        list.add(getResources().getString(R.string.continuous_monthly_status_cancel));
                        mDialogMenuManage = new DialogMenuManage(AutoOrderVipActivity.this, 157);
                        mDialogMenuManage.setData(list);
                        mDialogMenuManage.setItemClickListener(new DialogMenuManage.OnItemClickListener() {
                            @Override
                            public void clickIndex(int index) {
                                switch (index) {
                                    case 0:
                                        ThirdPartyLog.onEventValueOldClick(AutoOrderVipActivity.this, ThirdPartyLog.CONTINUOUS_MONTHLY_STATUS_SEE_DATE, ThirdPartyLog.CONTINUOUS_MONTHLY_STATUS_SEE_DATE, 1);
                                        Intent intent = new Intent(AutoOrderVipActivity.this, AutoOrderVipListActivity.class);
                                        startActivity(intent);
                                        break;
                                    case 1:
                                        ThirdPartyLog.onEventValueOldClick(AutoOrderVipActivity.this, ThirdPartyLog.CONTINUOUS_MONTHLY_STATUS_CANCEL, ThirdPartyLog.CONTINUOUS_MONTHLY_STATUS_CANCEL, 1);
                                        mPresenter.cancelContinueMonthInfo();
                                        break;
                                    default:
                                        break;
                                }
                            }
                        });
                    }
                    mDialogMenuManage.setBackgroundDrawable(new ColorDrawable());
                    mDialogMenuManage.setFocusable(true);
                    mDialogMenuManage.setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
                    mDialogMenuManage.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
                    mDialogMenuManage.showAsDropDown(v, 0, 0);
                }
                lastClickTime = currentClickTime;
            }
        });
        mStatusView.setNetErrorClickListener(new StatusView.NetErrorClickListener() {
            @Override
            public void onNetErrorEvent(View v) {
                if (!NetworkUtils.getInstance().checkNet()) {
                    showNoNetView();
                    return;
                }
                mPresenter.getAutoOrderVipStatus();
            }
        });
    }

    @Override
    public void setVipOrderStatusInfo(VipAutoRenewStatus bean) {
        if (bean.isAutoOrderVipOpenSucess()) {
            mTvUserStatus.setText(R.string.vip_already_opened);
        }
        mTvStatusEndTime.setText(bean.deadLine);

        DzSpanBuilder spanBuilder = new DzSpanBuilder();
        spanBuilder.appendColor(bean.autoCost, CompatUtils.getColor(this, R.color.color_50_000000)).appendStrike(bean.oldCost);
        mTvAutoPayPrice.setText(spanBuilder);
        mTvAutoPayTime.setText(bean.autoRenewTime);
        mTvAutoOrdersVipMsg.setText(bean.opendTips);
        mTitleView.setRightIconVisibility(View.VISIBLE);
    }


    @Override
    public void dismissLoadProgress() {
        if (mStatusView.getVisibility() == View.VISIBLE) {
            mStatusView.setVisibility(View.GONE);
        }
    }

    @Override
    public void showLoadProgress() {
        if (mStatusView.getVisibility() == View.GONE) {
            mStatusView.setVisibility(View.VISIBLE);
            mStatusView.showLoading();
        }
    }

    @Override
    public void showNoDataView() {
        mStatusView.showEmpty(getResources().getString(R.string.hua_wei_no_monthly_vip), CompatUtils.getDrawable(this, R.drawable.hw_empty_default));
        mTitleView.setRightIconVisibility(View.GONE);
    }

    @Override
    public void showNoNetView() {
        mStatusView.showNetError();
        mTitleView.setRightIconVisibility(View.GONE);
    }

    @Override
    public void isShowNotNetDialog() {
        AutoOrderVipActivity.this.showNotNetDialog();
    }
}
