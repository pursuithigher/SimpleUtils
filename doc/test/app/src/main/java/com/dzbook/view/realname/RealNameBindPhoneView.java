package com.dzbook.view.realname;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.dzbook.AppConst;
import com.dzbook.activity.hw.RealNameAuthActivity;
import com.dzbook.lib.net.DzSchedulers;
import com.dzbook.lib.utils.ALog;
import com.dzbook.log.DzLog;
import com.dzbook.log.LogConstants;
import com.dzbook.mvp.presenter.RealNameAuthPresenter;
import com.dzbook.net.hw.HwRequestLib;
import com.dzbook.utils.NetworkUtils;
import com.dzbook.utils.TypefaceUtils;
import com.ishugui.R;
import com.iss.app.BaseActivity;
import com.iss.view.common.ToastAlone;

import hw.sdk.net.bean.BeanLoginVerifyCode;

import static com.dzbook.activity.hw.RealNameAuthActivity.BIND_SUCCESS;

/**
 * 实名认证 绑定手机号页面
 *
 * @author winzows
 */
public class RealNameBindPhoneView extends LinearLayout implements View.OnClickListener {

    private Context mContext;

    private EditText etPhoneNum, etPhoneVerify;

    private Button btGetVerify;

    private Button btSubmit;
    private RealNameAuthPresenter mPresenter;
    private long lastClickTime;
    private MyCountDown myCountDown;

    /**
     * 构造
     *
     * @param context context
     */
    public RealNameBindPhoneView(Context context) {
        this(context, null);
    }

    /**
     * 构造
     *
     * @param context context
     * @param attrs   attrs
     */
    public RealNameBindPhoneView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        initView();
        initData();
        setListener();
    }

    private void initView() {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.view_real_name_bind_phone, this);
        etPhoneNum = view.findViewById(R.id.etPhoneNum);
        etPhoneVerify = view.findViewById(R.id.etVerifyCode);
        btGetVerify = view.findViewById(R.id.btGetVerifyCode);
        btSubmit = view.findViewById(R.id.btSubmit);

        TypefaceUtils.setHwChineseMediumFonts(btSubmit);
        TypefaceUtils.setHwChineseMediumFonts(btGetVerify);
    }

    private void initData() {
        btGetVerify.setClickable(true);
        btGetVerify.setEnabled(true);
        myCountDown = new MyCountDown(60 * 1000, 1000);
    }

    private void setListener() {
        btGetVerify.setOnClickListener(this);
        btSubmit.setOnClickListener(this);

        etPhoneNum.setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    //获得焦点
                    etPhoneNum.setCursorVisible(true);
                } else {
                    //失去焦点
                    etPhoneNum.setCursorVisible(false);
                }
            }
        });

        etPhoneVerify.setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    //获得焦点
                    etPhoneVerify.setCursorVisible(true);
                } else {
                    //失去焦点
                    etPhoneVerify.setCursorVisible(false);
                }
            }
        });

        etPhoneNum.addTextChangedListener(new EtPhoneNumMyTextWatcher());
        etPhoneVerify.addTextChangedListener(new EtPhoneVerifyTextWatcher());
    }


    @Override
    public void onClick(View v) {
        if (v != null) {
            int id = v.getId();
            if (id == R.id.btGetVerifyCode) {
                clickGetVerify();
            } else if (id == R.id.btSubmit) {
                clickPhoneVerifyLogin();
            } else if (id == R.id.imageview_delete) {
                etPhoneNum.setText("");
            }
        }
    }

    private void clickGetVerify() {
        final long thisTime = System.currentTimeMillis();
        if (thisTime - lastClickTime > AppConst.MAX_CLICK_INTERVAL_TIME) {
            lastClickTime = thisTime;

            final String phoneNum = etPhoneNum.getText().toString();

            if (TextUtils.isEmpty(phoneNum) || TextUtils.isEmpty(phoneNum.trim())) {
                ToastAlone.showShort(R.string.real_name_please_input_phone);
                return;
            }

            if (!isMobileNum(phoneNum)) {
                ToastAlone.showShort(R.string.real_name_phone_num_format_error);
                return;
            }

            if (!NetworkUtils.getInstance().checkNet()) {
                if (mContext instanceof BaseActivity) {
                    ((BaseActivity) mContext).showNotNetDialog();
                }
                return;
            }
            btGetVerify.setClickable(false);
            btGetVerify.setEnabled(false);
            //打点
            DzLog.getInstance().logClick(LogConstants.MODULE_SJHDL, LogConstants.ZONE_HQYZM, phoneNum.replace(" ", ""), null, null);

            myCountDown.start();
            DzSchedulers.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        final BeanLoginVerifyCode beanInfo = HwRequestLib.getInstance().getVerifyByPhoneRequest(phoneNum.replace(" ", ""));

                        if (beanInfo != null) {
                            ALog.dWz("BeanLoginVerifyCode " + beanInfo.toString());
                            handleBean(beanInfo);
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

    private void handleBean(BeanLoginVerifyCode beanInfo) {
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

    private void clickPhoneVerifyLogin() {
        final String phoneNum = etPhoneNum.getText().toString().replace(" ", "").trim();
        final String verifyCode = etPhoneVerify.getText().toString().trim();

        if (TextUtils.isEmpty(phoneNum) || TextUtils.isEmpty(phoneNum.trim())) {
            ToastAlone.showShort(R.string.real_name_please_input_phone);
            return;
        }

        if (TextUtils.isEmpty(verifyCode) || TextUtils.isEmpty(verifyCode.trim())) {
            ToastAlone.showShort(R.string.real_name_input_verify_code);
            return;
        }

        if (!NetworkUtils.getInstance().checkNet()) {
            if (mContext instanceof BaseActivity) {
                ((BaseActivity) mContext).showNotNetDialog();
            }
            return;
        }

        hideSoftInput(mContext);

        DzSchedulers.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    BeanLoginVerifyCode beanLoginVerifyCode = HwRequestLib.getInstance().bindVerifyByPhoneRequest(1, phoneNum, verifyCode);
                    ALog.dWz("beanLoginVerifyCode " + beanLoginVerifyCode.toString());
                    if (beanLoginVerifyCode.isSuccess()) {
                        RealNameAuthActivity.launch(getContext(), BIND_SUCCESS);
                        if (mPresenter != null) {
                            mPresenter.finish();
                        }
                    } else {
                        resetCountTime();
                        if (!TextUtils.isEmpty(beanLoginVerifyCode.getRetMsg())) {
                            ToastAlone.showShort(beanLoginVerifyCode.getRetMsg());
                        } else {
                            ToastAlone.showShort(R.string.real_name_verify_code_error);
                        }

                    }
                } catch (Exception e) {
                    ALog.printExceptionWz(e);
                }
            }
        });
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


    /**
     * 验证手机格式
     *
     * @param mobiles mobiles
     * @return boolean
     */
    public boolean isMobileNum(String mobiles) {
        String myMobiles = mobiles.replace(" ", "");
        String telRegex = "^[1][3-9]\\d{9}";
        if (TextUtils.isEmpty(myMobiles)) {
            return false;
        } else {
            return myMobiles.matches(telRegex);
        }
    }

    /**
     * 隐藏键盘
     *
     * @param mContext1 mContext
     */
    public void hideSoftInput(Context mContext1) {
        try {
            InputMethodManager imm = (InputMethodManager) mContext1.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(etPhoneVerify.getWindowToken(), 0);
        } catch (Exception ignore) {
        }
    }

    /**
     * 登录授权回调
     */
    public interface OnAuthCallback {
        /**
         * 登录失败的回调
         */
        void onFail();

        /**
         * 登录成功的回调
         */
        void onSuccess();
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

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (myCountDown != null) {
            myCountDown.cancel();
        }
    }

    /**
     * EtPhoneVerifyTextWatcher
     */
    private class EtPhoneVerifyTextWatcher implements TextWatcher {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            String phoneNum = etPhoneNum.getText().toString().trim();
            String phoneVerify = etPhoneVerify.getText().toString().trim();

            if (!TextUtils.isEmpty(phoneNum) && !TextUtils.isEmpty(phoneVerify)) {
                btSubmit.setEnabled(true);
                btSubmit.setBackgroundResource(R.drawable.selector_hw_red_common1);
            } else {
                btSubmit.setEnabled(false);
                btSubmit.setBackgroundResource(R.drawable.shape_hw_red_common1_default);
            }
        }
    }

    /**
     * EtPhoneNumMyTextWatcher
     */
    private class EtPhoneNumMyTextWatcher implements TextWatcher {

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            String phoneNum = etPhoneNum.getText().toString().trim();
            if (!TextUtils.isEmpty(phoneNum)) {
                if(btGetVerify.isClickable()) {
                    btGetVerify.setEnabled(true);
                    btGetVerify.setBackgroundResource(R.drawable.selector_hw_red_common1);
                }
            } else {
                btGetVerify.setEnabled(false);
                btGetVerify.setBackgroundResource(R.drawable.shape_hw_red_common1_default);
            }
        }
    }

    public void setPresenter(RealNameAuthPresenter mPresenter1) {
        this.mPresenter = mPresenter1;
    }
}
