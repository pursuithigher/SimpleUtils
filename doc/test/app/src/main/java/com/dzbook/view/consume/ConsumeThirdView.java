package com.dzbook.view.consume;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dzbook.utils.DimensionPixelUtil;
import com.ishugui.R;

import hw.sdk.net.bean.consume.ConsumeThirdBean;

/**
 * ConsumeThirdView
 *
 * @author dongdianzhou on 2017/4/6.
 */

public class ConsumeThirdView extends RelativeLayout {

    private TextView mTvTitle;
    private TextView mTvAmount;
    private TextView mTvConsumeTime;

    /**
     * 构造
     *
     * @param context context
     */
    public ConsumeThirdView(Context context) {
        this(context, null);
    }

    /**
     * 构造
     *
     * @param context context
     * @param attrs
     * @param attrs   context
     * @param attrs
     */
    public ConsumeThirdView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
        initData();
        setListener();
    }

    private void setListener() {

    }

    private void initData() {
        mTvAmount.setVisibility(View.GONE);
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) mTvTitle.getLayoutParams();
        params.topMargin = DimensionPixelUtil.dip2px(getContext(), 11);
        mTvTitle.setLayoutParams(params);
    }

    private void initView() {
        int height = DimensionPixelUtil.dip2px(getContext(), 64);
        int padding = DimensionPixelUtil.dip2px(getContext(), 16);
        LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, height);
        setLayoutParams(params);
        setPadding(padding, 0, padding, 0);
        setGravity(Gravity.CENTER_VERTICAL);

        View view = LayoutInflater.from(getContext()).inflate(R.layout.item_consume_third, this);
        mTvTitle = view.findViewById(R.id.tv_title);
        mTvAmount = view.findViewById(R.id.tv_amount);
        mTvConsumeTime = view.findViewById(R.id.tv_consume_time);
    }


    /**
     * 构造
     *
     * @param thirdBean thirdBean
     */
    public void bindData(ConsumeThirdBean thirdBean) {

        if (thirdBean != null) {
            if (!TextUtils.isEmpty(thirdBean.consumeSum)) {
                mTvAmount.setVisibility(View.VISIBLE);
                mTvAmount.setText(thirdBean.consumeSum);
                RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) mTvTitle.getLayoutParams();
                params.topMargin = DimensionPixelUtil.dip2px(getContext(), 11);
                mTvTitle.setLayoutParams(params);

                int height = DimensionPixelUtil.dip2px(getContext(), 88);
                LayoutParams params1 = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, height);
                setLayoutParams(params1);
            }
            mTvTitle.setText(thirdBean.name);
            mTvConsumeTime.setText(thirdBean.time);
        }
    }
}
