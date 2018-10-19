package com.dzbook.activity.hw;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;

import com.dzbook.BaseTransparencyLoadActivity;
import com.dzbook.lib.utils.ALog;
import com.dzbook.mvp.UI.RealNameAuthUI;
import com.dzbook.mvp.presenter.RealNameAuthPresenter;
import com.dzbook.utils.NetworkUtils;
import com.dzbook.utils.hw.LoginUtils;
import com.dzbook.view.DianZhongCommonTitle;
import com.dzbook.view.common.StatusView;
import com.dzbook.view.realname.CheckSmsCodeView;
import com.dzbook.view.realname.RealAuthDetailView;
import com.dzbook.view.realname.RealAuthSuccessView;
import com.dzbook.view.realname.RealNameBindPhoneView;
import com.dzbook.view.swipeBack.SwipeBackLayout;
import com.ishugui.R;
import com.iss.app.BaseActivity;

import huawei.widget.HwProgressBar;
import hw.sdk.net.bean.BeanSwitchPhoneNum;

/**
 * 华为实名认证
 *
 * @author winzows 2018/4/16
 */

public class RealNameAuthActivity extends BaseTransparencyLoadActivity implements RealNameAuthUI {

    /**
     * bind 成功
     */
    public static final String BIND_SUCCESS = "bind_success";
    private static final String VERIFY_CODE = "verify_code";
    private static final String TAG = "RealNameAuthActivity";
    private DianZhongCommonTitle mCommonTitle;
    private RealNameBindPhoneView viewPhoneNumVerify;
    private RealAuthDetailView realAuthDetailView;
    private RealNameAuthPresenter mPresenter;
    private LinearLayout mLoading;
    private HwProgressBar mLoadView;
    private StatusView statusView;
    private CheckSmsCodeView checkCodeView;
    private RealAuthSuccessView realAuthSuccessView;

    private String phoneNum;
    private String fromTag;


    @Override
    public String getTagName() {
        return TAG;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ac_real_name_auth);
        Window mWindow = getWindow();
        if (mWindow != null) {
            //禁止截屏
            mWindow.addFlags(WindowManager.LayoutParams.FLAG_SECURE);
            mWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        }
    }

    @Override
    protected void initView() {
        mCommonTitle = findViewById(R.id.include_top_title_item);
        viewPhoneNumVerify = findViewById(R.id.view_phone_num_verify);
        realAuthDetailView = findViewById(R.id.realAuthDetailView);
        mLoading = findViewById(R.id.linearlayout_loading);
        mLoadView = findViewById(R.id.loadingview);
        statusView = findViewById(R.id.statusView);
        checkCodeView = findViewById(R.id.checkCodeView);
        realAuthSuccessView = findViewById(R.id.realAuthSuccessView);
        mCommonTitle.setVisibility(View.VISIBLE);
        mPresenter = new RealNameAuthPresenter(this);
        mCommonTitle.setTitle(getResources().getString(R.string.real_name_auth_title));

        viewPhoneNumVerify.setPresenter(mPresenter);
        realAuthDetailView.setPresenter(mPresenter);
        checkCodeView.setPresenter(mPresenter);
        realAuthSuccessView.setPresenter(mPresenter);
    }

    @Override
    protected void initData() {
        mLoadView.setVisibility(View.VISIBLE);
        Intent intent = getIntent();
        if (intent != null) {
            phoneNum = intent.getStringExtra("phone");
            fromTag = intent.getStringExtra("from");
        }

        if (TextUtils.equals(fromTag, VERIFY_CODE)) {
            showCheckCodeView();
            checkCodeView.setPhoneNum(phoneNum);
        } else if (TextUtils.equals(fromTag, BIND_SUCCESS)) {
            showAuthSuccessView();
        } else {
            mCommonTitle.setTitle(getResources().getString(R.string.real_name_auth_title));
            mPresenter.requestData();
            realAuthDetailView.setVisibility(View.GONE);
            viewPhoneNumVerify.setVisibility(View.GONE);
        }
    }

    @Override
    protected void setListener() {
        mCommonTitle.setLeftClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideSoft();
                onBackPressed();
            }
        });
        statusView.setNetErrorClickListener(new StatusView.NetErrorClickListener() {
            @Override
            public void onNetErrorEvent(View v) {
                statusView.setVisibility(View.GONE);
                requestData();
            }
        });

        mSwipeBackLayout.addSwipeListener(new SwipeBackLayout.SwipeListener() {
            @Override
            public void onScrollStateChange(int state, float scrollPercent) {
            }

            @Override
            public void onEdgeTouch(int edgeFlag) {
            }

            @Override
            public void onScrollOverThreshold() {
                hideSoft();
            }
        });
    }

    private void hideSoft() {
        try {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) {
                imm.hideSoftInputFromWindow(viewPhoneNumVerify.getWindowToken(), 0);
                imm.hideSoftInputFromWindow(checkCodeView.getWindowToken(), 0);
            }
        } catch (Throwable e) {
            ALog.printExceptionWz(e);
        }
    }

    /**
     * 强制登录
     *
     * @param context context
     */
    public static void launch(final Context context) {
        launch(context, null);
    }

    /**
     * 登录
     *
     * @param context context
     * @param from    from
     */
    public static void launch(final Context context, final String from) {
        LoginUtils.getInstance().forceLoginCheck(context, new LoginUtils.LoginCheckListener() {
            @Override
            public void loginComplete() {
                Intent intent = new Intent(context, RealNameAuthActivity.class);
                if (!TextUtils.isEmpty(from)) {
                    intent.putExtra("from", from);
                }
                context.startActivity(intent);
                BaseActivity.showActivity(context);
            }
        });
    }

    /**
     * 取消绑定时的页面
     *
     * @param context  上下文
     * @param phoneNum 手机号码
     */
    public static void launchSendVerifyCode(final Context context, final String phoneNum) {
        if (context instanceof Activity) {
            LoginUtils.getInstance().forceLoginCheck(context, new LoginUtils.LoginCheckListener() {
                @Override
                public void loginComplete() {
                    Intent intent = new Intent(context, RealNameAuthActivity.class);
                    intent.putExtra("from", VERIFY_CODE);
                    intent.putExtra("phone", phoneNum);
                    context.startActivity(intent);
                    BaseActivity.showActivity(context);
                }
            });
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (viewPhoneNumVerify != null) {
            viewPhoneNumVerify.hideSoftInput(this);
        }
        if (mPresenter != null) {
            mPresenter.destroy();
        }
    }

    @Override
    public void showSwitchPhoneView() {
        realAuthDetailView.setVisibility(View.VISIBLE);
        statusView.showSuccess();
        viewPhoneNumVerify.setVisibility(View.GONE);
        mLoading.setVisibility(View.GONE);
        mLoadView.setVisibility(View.GONE);
        checkCodeView.setVisibility(View.GONE);
        realAuthSuccessView.setVisibility(View.GONE);
    }

    @Override
    public void showBindPhoneView() {
        viewPhoneNumVerify.setVisibility(View.VISIBLE);
        statusView.showSuccess();
        realAuthDetailView.setVisibility(View.GONE);
        mLoading.setVisibility(View.GONE);
        mLoadView.setVisibility(View.GONE);
        checkCodeView.setVisibility(View.GONE);
        realAuthSuccessView.setVisibility(View.GONE);
    }

    @Override
    public void showErrorView() {
        statusView.showNetError();
        viewPhoneNumVerify.setVisibility(View.GONE);
        realAuthDetailView.setVisibility(View.GONE);
        mLoading.setVisibility(View.GONE);
        mLoadView.setVisibility(View.GONE);
        checkCodeView.setVisibility(View.GONE);
        realAuthSuccessView.setVisibility(View.GONE);
    }

    @Override
    public void onRequestStart() {
        statusView.setVisibility(View.GONE);
        viewPhoneNumVerify.setVisibility(View.GONE);
        realAuthDetailView.setVisibility(View.GONE);
        mLoading.setVisibility(View.VISIBLE);
        mLoadView.setVisibility(View.VISIBLE);
        checkCodeView.setVisibility(View.GONE);
        realAuthSuccessView.setVisibility(View.GONE);
    }

    /**
     * 显示检查验证码view
     */
    public void showCheckCodeView() {
        checkCodeView.setVisibility(View.VISIBLE);
        statusView.setVisibility(View.GONE);
        viewPhoneNumVerify.setVisibility(View.GONE);
        realAuthDetailView.setVisibility(View.GONE);
        mLoading.setVisibility(View.GONE);
        mLoadView.setVisibility(View.GONE);
        realAuthSuccessView.setVisibility(View.GONE);
    }

    @Override
    public void bindSwitchPhoneData(BeanSwitchPhoneNum beanSwitchPhoneNum) {
        realAuthDetailView.bindData(beanSwitchPhoneNum);
    }

    @Override
    public void showAuthSuccessView() {
        checkCodeView.setVisibility(View.GONE);
        statusView.setVisibility(View.GONE);
        viewPhoneNumVerify.setVisibility(View.GONE);
        realAuthDetailView.setVisibility(View.GONE);
        mLoading.setVisibility(View.GONE);
        mLoadView.setVisibility(View.GONE);
        realAuthSuccessView.setVisibility(View.VISIBLE);
    }

    @Override
    public void finishActivity() {
        finish();
    }

    @Override
    public String getPageTag() {
        return fromTag;
    }

    private void requestData() {
        if (NetworkUtils.getInstance().checkNet()) {
            mPresenter.requestData();
        } else {
            showErrorView();
        }
    }

}
