package com.dzbook.view.consume;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dzbook.mvp.presenter.ConsumeBookSumPresenter;
import com.dzbook.utils.DimensionPixelUtil;
import com.dzbook.utils.GlideImageLoadUtils;
import com.ishugui.R;

import hw.sdk.net.bean.consume.ConsumeBookSumBean;

/**
 * ConsumeBookSumView
 *
 * @author lizz 2018/4/20.
 */

public class ConsumeBookSumView extends RelativeLayout {

    private Context mContext;

    private TextView tvBookName, tvConsumeAmount, tvConsumeLastTime;

    private ImageView ivBookIcon, ivBookConsumeArrow;

    private View mViewLine;

    private ConsumeBookSumPresenter mPresenter;

    private ConsumeBookSumBean mBean;

    /**
     * 构造
     *
     * @param context context
     */
    public ConsumeBookSumView(Context context) {
        this(context, null);
    }

    /**
     * 构造
     *
     * @param context context
     * @param attrs   attrs
     */
    public ConsumeBookSumView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        initView();
        initData();
        setListener();
    }

    public void setMainShelfPresenter(ConsumeBookSumPresenter mPresenter1) {
        this.mPresenter = mPresenter1;
    }

    private void initView() {
        setBackgroundResource(R.drawable.selector_hw_list_item);
        int height = DimensionPixelUtil.dip2px(mContext, 88);
        int padding = DimensionPixelUtil.dip2px(mContext, 16);
        LayoutParams layoutParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, height);
        setLayoutParams(layoutParams);

        View view = LayoutInflater.from(mContext).inflate(R.layout.item_consume_book_sum, this);
        setPadding(padding, 0, padding, 0);

        tvBookName = view.findViewById(R.id.tv_book_name);
        tvConsumeAmount = view.findViewById(R.id.tv_consume_amount);
        tvConsumeLastTime = view.findViewById(R.id.tv_consume_last_time);
        ivBookIcon = view.findViewById(R.id.iv_book_icon);
        ivBookConsumeArrow = view.findViewById(R.id.iv_book_consume_arrow);
        mViewLine = view.findViewById(R.id.view_line);
    }

    private void initData() {
        ivBookConsumeArrow.setVisibility(View.GONE);
    }

    private void setListener() {
        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mBean != null && mPresenter != null && !TextUtils.isEmpty(mBean.nextId)) {
                    mPresenter.launchSecondConsume("1", mBean.nextId);
                }
            }
        });
    }

    /**
     * 绑定数据
     *
     * @param bean          bean
     * @param isHideEndLine isHideEndLine
     */
    public void bindData(ConsumeBookSumBean bean, boolean isHideEndLine) {
        if (bean != null) {
            mBean = bean;
            if (!TextUtils.isEmpty(bean.nextId)) {
                ivBookConsumeArrow.setVisibility(View.VISIBLE);
            } else {
                ivBookConsumeArrow.setVisibility(View.GONE);
            }
            GlideImageLoadUtils.getInstanse().glideImageLoadFromUrl((Activity) mContext, ivBookIcon, bean.coverWap);
            tvBookName.setText(bean.bookName);
            tvConsumeAmount.setText(bean.consumeSum);
            tvConsumeLastTime.setText(bean.lastConsumeTime);
            int visibility = isHideEndLine ? GONE : VISIBLE;
            if (mViewLine.getVisibility() != visibility) {
                mViewLine.setVisibility(visibility);
            }
        }
    }

}
