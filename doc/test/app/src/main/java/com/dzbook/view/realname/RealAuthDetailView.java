package com.dzbook.view.realname;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dzbook.activity.hw.RealNameAuthActivity;
import com.dzbook.mvp.presenter.RealNameAuthPresenter;
import com.dzbook.utils.TypefaceUtils;
import com.ishugui.R;

import hw.sdk.net.bean.BeanSwitchPhoneNum;

/**
 * 实名认证 切换手机号的view
 *
 * @author winzows 2018/4/17
 */

public class RealAuthDetailView extends RelativeLayout {
    private TextView tvPhoneNum;
    private TextView mTv, tvTips;
    private String phoneNum;

    private RealNameAuthPresenter mPresenter;

    /**
     * 构造
     *
     * @param context context
     */
    public RealAuthDetailView(Context context) {
        super(context);
        initView();
        initListener();
    }

    /**
     * 构造
     *
     * @param context context
     * @param attrs   attrs
     */
    public RealAuthDetailView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
        initListener();
    }


    private void initView() {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.view_real_auth_detail, this);
        tvPhoneNum = view.findViewById(R.id.tvPhoneNum);
        mTv = view.findViewById(R.id.tvSubmit);
        tvTips = view.findViewById(R.id.tvTips);

        TypefaceUtils.setHwChineseMediumFonts(mTv);
        TypefaceUtils.setHwChineseMediumFonts(tvPhoneNum);
        TypefaceUtils.setHwChineseMediumFonts(tvTips);
    }

    /**
     * 绑定数据
     *
     * @param beanSwitchPhoneNum 参数
     */
    public void bindData(BeanSwitchPhoneNum beanSwitchPhoneNum) {
        if (beanSwitchPhoneNum != null) {
            this.phoneNum = beanSwitchPhoneNum.phoneNum;
            tvPhoneNum.setText(phoneNum);
        }
    }

    private void initListener() {
        mTv.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                RealNameAuthActivity.launchSendVerifyCode(getContext(), phoneNum);
                if (mPresenter != null) {
                    mPresenter.finish();
                }
            }
        });
    }

    public void setPresenter(RealNameAuthPresenter mPresenter1) {
        this.mPresenter = mPresenter1;
    }
}
