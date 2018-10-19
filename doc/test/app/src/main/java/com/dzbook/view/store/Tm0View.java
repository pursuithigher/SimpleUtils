package com.dzbook.view.store;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dzbook.lib.utils.CompatUtils;
import com.dzbook.mvp.presenter.TempletPresenter;
import com.dzbook.utils.DimensionPixelUtil;
import com.dzbook.utils.TypefaceUtils;
import com.ishugui.R;

import hw.sdk.net.bean.store.BeanTempletInfo;
import hw.sdk.net.bean.store.TempletContant;

/**
 * Tm0View
 *
 * @author dongdianzhou on 2018/1/6.
 */

public class Tm0View extends LinearLayout {

    private Context mContext;
    private CountDownTextView mTextView;
    private TextView textViewTime;
    private TempletPresenter mTempletPresenter;

    /**
     * 构造
     *
     * @param context context
     */
    public Tm0View(Context context) {
        super(context);
        mContext = context;
        initView();
        initData();
        setListener();
    }

    /**
     * 构造
     *
     * @param context context
     * @param attrs   attrs
     */
    public Tm0View(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        initView();
        initData();
        setListener();
    }


    /**
     * 构造
     *
     * @param context          context
     * @param templetPresenter templetPresenter
     */
    public Tm0View(Context context, TempletPresenter templetPresenter) {
        super(context);
        mContext = context;
        initView();
        initData();
        setListener();
        mTempletPresenter = templetPresenter;
    }

    private void setListener() {
        mTextView.setCountDownListener(new CountDownTextView.CountDownListener() {
            @Override
            public void countdown() {
                Activity activity = (Activity) getContext();
                if (activity != null && !activity.isFinishing()) {
                    mTempletPresenter.limitFreeCompelete(TempletContant.LIMIT_FREE_COMPELETE);
                }
            }
        });
    }

    private void initData() {
    }

    private void initView() {
        setOrientation(HORIZONTAL);
        int paddingTop = DimensionPixelUtil.dip2px(mContext, 13);
        int paddingBottom = DimensionPixelUtil.dip2px(mContext, 8);
        int paddingLeft = DimensionPixelUtil.dip2px(mContext, 16);
        int paddingRight = DimensionPixelUtil.dip2px(mContext, 18);
        setPadding(paddingLeft, paddingTop, paddingRight, paddingBottom);
        setBackgroundColor(CompatUtils.getColor(getContext(), R.color.color_100_ffffff));
        setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        LayoutInflater.from(getContext()).inflate(R.layout.view_tm0, this);
        mTextView = findViewById(R.id.textview_tm1);
        textViewTime = findViewById(R.id.textview_time);
        TypefaceUtils.setHwChineseMediumFonts(textViewTime);
    }

    /**
     * 绑定数据
     *
     * @param templetInfo templetInfo
     */
    public void bindData(BeanTempletInfo templetInfo) {
        mTextView.bindData(templetInfo.counter);
    }
}
