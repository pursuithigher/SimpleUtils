package com.dzbook.view.realname;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dzbook.AppConst;
import com.dzbook.activity.hw.RealNameAuthActivity;
import com.dzbook.event.EventBusUtils;
import com.dzbook.event.EventConstant;
import com.dzbook.lib.net.DzSchedulers;
import com.dzbook.lib.utils.ALog;
import com.dzbook.mvp.presenter.RealNameAuthPresenter;
import com.dzbook.net.hw.HwRequestLib;
import com.dzbook.utils.NetworkUtils;
import com.dzbook.utils.TypefaceUtils;
import com.ishugui.R;
import com.iss.app.BaseActivity;
import com.iss.view.common.ToastAlone;

import hw.sdk.net.bean.BeanLoginVerifyCode;

/**
 * 更换实名认证绑定的手机号 二级页面 发短信验证码 来判断能否修改
 *
 * @author winzows 2018/4/17
 */

public class CheckSmsCodeView extends RelativeLayout {
    /**
     * 更换号码
     */
    public static final String SWITCH_PHONE = "check_verify";
    private Button btGetVerify;
    private TextView tvPhoneNum, tvTips;
    private Button mButton;
    private EditText etPhoneVerify;
    private long lastClickTime;
    private MyCountDown myCountDown;
    private Context mContext;
    private RealNameAuthPresenter mPresenter;

    /**
     * 构造
     *
     * @param context context
     */
    public CheckSmsCodeView(Context context) {
        super(context);
        this.mContext = context;
        initView();
        initListener();
    }

    /**
     * 构造
     *
     * @param context context
     * @param attrs   attrs
     */
    public CheckSmsCodeView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        initView();
        initListener();
    }

    private void initView() {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.view_check_sms_code, this);
        tvPhoneNum = view.findViewById(R.id.tvPhoneNum);
        btGetVerify = view.findViewById(R.id.btGetVerifyCode);
        mButton = view.findViewById(R.id.btSubmit);
        etPhoneVerify = view.findViewById(R.id.etVerifyCode);
        tvTips = view.findViewById(R.id.tvTips);
        btGetVerify.setClickable(true);
        btGetVerify.setEnabled(true);
        btGetVerify.setText(getContext().getText(R.string.real_name_get_verify_code));
        myCountDown = new MyCountDown(60 * 1000, 1000);

        TypefaceUtils.setHwChineseMediumFonts(tvPhoneNum);
        TypefaceUtils.setHwChineseMediumFonts(btGetVerify);
        TypefaceUtils.setHwChineseMediumFonts(mButton);
        TypefaceUtils.setHwChineseMediumFonts(tvTips);
    }

    private void initListener() {
        mButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                final String verifyCode = etPhoneVerify.getText().toString();
                if (TextUtils.isEmpty(verifyCode)) {
                    ToastAlone.showShort(R.string.real_name_input_verify_code);
                } else {
                    hideSoftInput(getContext());
                    DzSchedulers.execute(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                BeanLoginVerifyCode loginVerifyCode = HwRequestLib.getInstance().bindVerifyByPhoneRequest(2, null, verifyCode);
                                ALog.dWz("loginVerifyCode " + loginVerifyCode);
                                if (loginVerifyCode.isSuccess()) {
                                    EventBusUtils.sendMessage(EventConstant.REQUESTCODE_SWITCH_PHONE_SUCCESS, EventConstant.TYPE_REAL_SWITCH_NAME, null);
                                    RealNameAuthActivity.launch(getContext(), SWITCH_PHONE);
                                    if (mPresenter != null) {
                                        mPresenter.finish();
                                    }
                                } else {
                                    String msg = loginVerifyCode.getRetMsg();
                                    if (!TextUtils.isEmpty(msg)) {
                                        ToastAlone.showShort(msg);
                                    } else {
                                        ToastAlone.showShort(R.string.real_name_phone_check_error);
                                    }
                                }
                            } catch (Exception e) {
                                ALog.printExceptionWz(e);
                            }
                        }
                    });
                }
            }
        });

        btGetVerify.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                final long thisTime = System.currentTimeMillis();
                if (thisTime - lastClickTime > AppConst.MAX_CLICK_INTERVAL_TIME) {
                    lastClickTime = thisTime;


                    if (!NetworkUtils.getInstance().checkNet()) {
                        if (mContext instanceof BaseActivity) {
                            ((BaseActivity) mContext).showNotNetDialog();
                        }
                        return;
                    }
                    btGetVerify.setClickable(false);
                    btGetVerify.setEnabled(false);
                    myCountDown.start();
                    DzSchedulers.execute(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                final BeanLoginVerifyCode beanInfo = HwRequestLib.getInstance().getVerifyByPhoneRequest("");

                                if (beanInfo != null) {
                                    ALog.dWz("BeanLoginVerifyCode " + beanInfo.toString());
                                    if (beanInfo.isSuccess()) {
                                        ToastAlone.showShort(R.string.send_success);
                                    } else {
                                        resetCountTime();
                                        if (!TextUtils.isEmpty(beanInfo.message)) {
                                            ToastAlone.showShort(beanInfo.message);
                                        } else {
                                            ToastAlone.showShort(R.string.get_sms_verify_fail_please_retry);
                                        }
                                    }
                                }
                            } catch (Exception e) {
                                resetCountTime();
                                ALog.printStackTrace(e);
                                ToastAlone.showShort(R.string.get_sms_verify_fail_please_retry);
                            }
                        }
                    });

                }
            }
        });
    }

    /**
     * 计时器
     */
    private class MyCountDown extends CountDownTimer {


        public MyCountDown(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(final long millisUntilFinished) {
            handleOnTick(millisUntilFinished);
        }

        @Override
        public void onFinish() {
            if (btGetVerify != null) {
                btGetVerify.setClickable(true);
                btGetVerify.setEnabled(true);
                btGetVerify.setText(mContext.getText(R.string.real_name_get_verify_code));
                btGetVerify.setBackgroundResource(R.drawable.selector_hw_red_common1);
            }
        }

    }

    private void handleOnTick(final long millisUntilFinished) {
        if (btGetVerify != null) {
            btGetVerify.post(new Runnable() {
                @SuppressLint("SetTextI18n")
                @Override
                public void run() {
                    String millStr = (int) (millisUntilFinished / 1000) + "s";
                    btGetVerify.setText(mContext.getText(R.string.real_name_get_verify_code_retry) + millStr);
                    btGetVerify.setBackgroundResource(R.drawable.shape_hw_red_common1_default);
                }
            });
        }
    }


    private void resetCountTime() {
        if (myCountDown != null) {
            myCountDown.cancel();
        }
        if (btGetVerify != null) {
            btGetVerify.post(new Runnable() {
                @Override
                public void run() {
                    btGetVerify.setClickable(true);
                    btGetVerify.setEnabled(true);
                    btGetVerify.setText(mContext.getText(R.string.real_name_get_verify_code));
                    btGetVerify.setBackgroundResource(R.drawable.selector_hw_red_common1);
                }
            });
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        hideSoftInput(getContext());
        if (myCountDown != null) {
            myCountDown.cancel();
            myCountDown = null;
        }
    }

    /**
     * 隐藏键盘
     *
     * @param mContext1 mContext1
     */
    public void hideSoftInput(Context mContext1) {
        try {
            InputMethodManager imm = (InputMethodManager) mContext1.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(etPhoneVerify.getWindowToken(), 0);
        } catch (Exception ignore) {
        }
    }

    /**
     * 校验回调
     */
    public interface OnVerifyCodeCallback {

        /**
         * 校验通过
         */
        void onSuccess();

        /**
         * 校验失败
         */
        void onFail();

    }

    /**
     * 设置手机号码
     *
     * @param phoneNum 手机号码
     */
    public void setPhoneNum(String phoneNum) {
        if (tvPhoneNum != null && !TextUtils.isEmpty(phoneNum)) {
            tvPhoneNum.setText(phoneNum);
        }
    }

    public void setPresenter(RealNameAuthPresenter mPresenter1) {
        this.mPresenter = mPresenter1;
    }
}
