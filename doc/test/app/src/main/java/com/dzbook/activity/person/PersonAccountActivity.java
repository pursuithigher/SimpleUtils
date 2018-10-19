package com.dzbook.activity.person;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.dzbook.BaseSwipeBackActivity;
import com.dzbook.activity.hw.OldUserAssetsActivity;
import com.dzbook.dialog.common.DialogLoading;
import com.dzbook.event.EventConstant;
import com.dzbook.event.EventMessage;
import com.dzbook.log.DzLog;
import com.dzbook.log.LogConstants;
import com.dzbook.mvp.UI.PersonAccountUI;
import com.dzbook.mvp.presenter.PersonAccountPresenter;
import com.dzbook.mvp.presenter.PersonAccountPresenterImpl;
import com.dzbook.utils.SpUtil;
import com.dzbook.utils.ThirdPartyLog;
import com.dzbook.view.DianZhongCommonTitle;
import com.dzbook.view.person.PersonCommonView;
import com.ishugui.R;
import com.iss.app.BaseActivity;

import hw.sdk.utils.UiHelper;

/**
 * 账户
 *
 * @author dongdianzhou on 2017/4/5.
 */
public class PersonAccountActivity extends BaseSwipeBackActivity implements View.OnClickListener, PersonAccountUI {

    private static final int MAX_CLICK_INTERVAL_TIME = 1000;
    private long lastClickTime = 0;


    private DianZhongCommonTitle mCommonTitle;
    private TextView mTextViewContent;
    private TextView mTvVouchers;
    private TextView mTvJumpVouchers;
    private TextView mTvRecharge;

    private DialogLoading dialogLoading;

    private PersonCommonView mCommonviewConsumeRecord;
    private PersonCommonView mCommonviewTopUpRecord;
    private PersonAccountPresenter mPresenter;


    /**
     * 启动
     *
     * @param activity activity
     */
    public static void launch(Activity activity) {
        Intent intent = new Intent();
        intent.setClass(activity, PersonAccountActivity.class);
        activity.startActivity(intent);
        BaseActivity.showActivity(activity);
    }

    @Override
    public String getTagName() {
        return "PersonAccountActivity";
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personaccount);
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUserPriceInfo();
    }

    @Override
    protected void initView() {
        super.initView();
        mCommonTitle = findViewById(R.id.commontitle);
        mTextViewContent = findViewById(R.id.textview_content);
        mTvJumpVouchers = findViewById(R.id.tv_jump_vouchers);
        mTvRecharge = findViewById(R.id.textview_recharge);
        mTvVouchers = findViewById(R.id.tv_vouchers);
        dialogLoading = new DialogLoading(this);
        mCommonviewConsumeRecord = findViewById(R.id.commonview_consume_record);
        mCommonviewTopUpRecord = findViewById(R.id.commonview_top_up_record);

    }

    @Override
    protected void initData() {
        super.initData();
        mPresenter = new PersonAccountPresenterImpl(this);

        //修改按钮宽度为屏幕50%
        int screenWidth = UiHelper.getScreenWidth(this);
        ViewGroup.LayoutParams params = mTvRecharge.getLayoutParams();
        params.width = (int) (screenWidth * 0.5);
        mTvRecharge.setLayoutParams(params);
    }

    @Override
    protected void setListener() {
        super.setListener();
        mTvRecharge.setOnClickListener(this);
        mTvJumpVouchers.setOnClickListener(this);

        mCommonTitle.setLeftClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        //消费记录
        mCommonviewConsumeRecord.setOnClickListener(this);
        //充值记录
        mCommonviewTopUpRecord.setOnClickListener(this);

        mTvJumpVouchers.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        long thisClickTime = System.currentTimeMillis();
        if (thisClickTime - lastClickTime < MAX_CLICK_INTERVAL_TIME) {
            return;
        }
        lastClickTime = thisClickTime;

        switch (v.getId()) {
            case R.id.textview_recharge:
                mPresenter.dzRechargePay();
                ThirdPartyLog.onEventValueOldClick(getContext(), ThirdPartyLog.PERSON_CENTER_MYACCOUNT_MENU_UMENG_ID, ThirdPartyLog.PERSON_CENTER_MYACCOUNT_RECHARGE_VALUE, 1);
                break;
            case R.id.commonview_consume_record:
                mPresenter.intentToConsumeRecordActivity();
                break;
            case R.id.commonview_top_up_record:
                ThirdPartyLog.onEventValueOldClick(getContext(), ThirdPartyLog.PERSON_CENTER_MYACCOUNT_MENU_UMENG_ID, ThirdPartyLog.PERSON_CENTER_MYACCOUNT_RECHARGERECORD_VALUE, 1);
                mPresenter.intentToRechargeRecord();
                break;
            case R.id.tv_jump_vouchers:
                mPresenter.intentToVouchersList();
                break;
            default:
                break;
        }
    }

    @Override
    public Context getContext() {
        return this;
    }

    @Override
    public void referencePriceView() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                SpUtil spUtil = SpUtil.getinstance(getContext());
                String price = spUtil.getUserRemainPrice();
                String vouchers = spUtil.getUserVouchers();

                mTextViewContent.setText(price);
                mTvVouchers.setText(vouchers);
                if (TextUtils.equals("0", vouchers)) {
                    mTvJumpVouchers.setVisibility(View.GONE);
                } else {
                    mTvJumpVouchers.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    @Override
    public void setUserPriceInfo() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                SpUtil spUtil = SpUtil.getinstance(getContext());
                String price = spUtil.getUserRemainPrice();
                String vouchers = spUtil.getUserVouchers();
                if (TextUtils.isEmpty(price)) {
                    price = "--";
                }
                mTextViewContent.setText(price);
                mTvVouchers.setText(vouchers);
                if (TextUtils.equals("0", vouchers)) {
                    mTvJumpVouchers.setVisibility(View.GONE);
                } else {
                    mTvJumpVouchers.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    @Override
    public void showLoadingDialog() {
        if (dialogLoading != null && !dialogLoading.isShowing() && !isFinishing()) {
            dialogLoading.show();
        }
    }

    @Override
    public void hideLoadingDialog() {
        if (dialogLoading != null && dialogLoading.isShowing() && !isFinishing()) {
            dialogLoading.dismiss();
        }
    }

}
