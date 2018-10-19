package com.dzbook.view.consume;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dzbook.mvp.presenter.ConsumeSecondPresenter;
import com.dzbook.utils.DimensionPixelUtil;
import com.ishugui.R;

import hw.sdk.net.bean.consume.ConsumeSecondBean;

/**
 * ConsumeSecondView
 *
 * @author dongdianzhou on 2017/4/6.
 */

public class ConsumeSecondView extends RelativeLayout {

    private TextView mTvTitle;
    private TextView mTvAmount;
    private TextView mTvConsumeTime;
    private ImageView ivConsumeArrow;
    private View mViewLine;

    private ConsumeSecondPresenter mPresenter;

    private ConsumeSecondBean mBean;

    /**
     * 构造
     *
     * @param context context
     */
    public ConsumeSecondView(Context context) {
        this(context, null);
    }

    /**
     * 构造
     *
     * @param context context
     * @param attrs   attrs
     */
    public ConsumeSecondView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
        initData();
        setListener();
    }

    public void setPresenter(ConsumeSecondPresenter presenter) {
        this.mPresenter = presenter;
    }

    private void setListener() {
        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mPresenter != null && !TextUtils.isEmpty(mBean.consumeId)) {
                    mPresenter.launchThirdConsume(mBean.consumeId, mBean.bookId);
                }
            }
        });
    }

    private void initData() {
        mTvAmount.setVisibility(View.GONE);
        ivConsumeArrow.setVisibility(View.GONE);
    }

    private void initView() {
        setBackgroundResource(R.drawable.selector_hw_list_item);
        int height = DimensionPixelUtil.dip2px(getContext(), 88);
        int padding = DimensionPixelUtil.dip2px(getContext(), 16);
        LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, height);
        setLayoutParams(params);
        setPadding(padding, 0, padding, 0);
        setGravity(Gravity.CENTER_VERTICAL);

        View view = LayoutInflater.from(getContext()).inflate(R.layout.item_consume_second, this);
        mTvTitle = view.findViewById(R.id.tv_title);
        mTvAmount = view.findViewById(R.id.tv_amount);
        mTvConsumeTime = view.findViewById(R.id.tv_consume_time);
        ivConsumeArrow = view.findViewById(R.id.iv_consume_arrow);
        mViewLine = findViewById(R.id.view_line);
    }


    /**
     * 绑定数据
     *
     * @param secondBean    secondBean
     * @param isHideEndLine isHideEndLine
     */
    public void bindData(ConsumeSecondBean secondBean, boolean isHideEndLine) {

        if (secondBean != null) {
            mBean = secondBean;
            if (!TextUtils.isEmpty(secondBean.consumeSum)) {
                mTvAmount.setVisibility(View.VISIBLE);
                mTvAmount.setText(secondBean.consumeSum);
            }
            if (!TextUtils.isEmpty(secondBean.consumeId)) {
                ivConsumeArrow.setVisibility(View.VISIBLE);
            }
            mTvTitle.setText(secondBean.name);
            mTvConsumeTime.setText(secondBean.time);
            int visibility = isHideEndLine ? GONE : VISIBLE;
            if (mViewLine.getVisibility() != visibility) {
                mViewLine.setVisibility(visibility);
            }
        }
    }
}
