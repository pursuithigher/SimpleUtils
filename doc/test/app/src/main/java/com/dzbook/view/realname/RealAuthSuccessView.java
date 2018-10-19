package com.dzbook.view.realname;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dzbook.mvp.presenter.RealNameAuthPresenter;
import com.dzbook.utils.TypefaceUtils;
import com.ishugui.R;

/**
 * 实名认证 切换手机号的view
 *
 * @author winzows 2018/4/17
 */

public class RealAuthSuccessView extends RelativeLayout {
    private TextView mTv, tvTips;

    private RealNameAuthPresenter mPresenter;

    /**
     * 构造
     *
     * @param context context
     */
    public RealAuthSuccessView(Context context) {
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
    public RealAuthSuccessView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
        initListener();
    }


    private void initView() {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.view_switch_success, this);
        mTv = view.findViewById(R.id.tvSubmit);
        tvTips = view.findViewById(R.id.tvTips);
        TypefaceUtils.setHwChineseMediumFonts(mTv);
        TypefaceUtils.setHwChineseMediumFonts(tvTips);
    }


    private void initListener() {
        mTv.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
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
