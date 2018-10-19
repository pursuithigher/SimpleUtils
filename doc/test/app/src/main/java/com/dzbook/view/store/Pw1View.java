package com.dzbook.view.store;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dzbook.utils.DimensionPixelUtil;
import com.ishugui.R;

import hw.sdk.net.bean.store.BeanTempletInfo;

/**
 * Pw1View
 *
 * @author dongdianzhou on 2018/1/6.
 */

public class Pw1View extends LinearLayout {

    private TextView mTextView;

    /**
     * 构造
     *
     * @param context context
     */
    public Pw1View(Context context) {
        this(context, null);
    }

    /**
     * 构造
     *
     * @param context context
     * @param attrs   attrs
     */
    public Pw1View(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView();
        initData();
        setListener();
    }

    private void setListener() {

    }

    private void initData() {

    }

    private void initView() {
        setOrientation(VERTICAL);
        setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, DimensionPixelUtil.dip2px(getContext(), 48)));
        setGravity(Gravity.CENTER);
        LayoutInflater.from(getContext()).inflate(R.layout.view_pw1, this);
        mTextView = findViewById(R.id.textview);
    }

    /**
     * 绑定数据
     *
     * @param templetInfo templetInfo
     */
    public void bindData(BeanTempletInfo templetInfo) {
        mTextView.setText(templetInfo.title);
    }
}
