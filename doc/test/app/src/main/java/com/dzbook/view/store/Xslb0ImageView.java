package com.dzbook.view.store;

import android.content.Context;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

import com.dzbook.mvp.presenter.TempletPresenter;
import com.dzbook.utils.GlideImageLoadUtils;
import com.dzbook.view.RoundRectImageView;
import com.ishugui.R;

import hw.sdk.net.bean.store.BeanTempletInfo;
import hw.sdk.net.bean.store.TempletContant;

/**
 * Xslb0ImageView
 */
public class Xslb0ImageView extends RoundRectImageView {

    private TempletPresenter templetPresenter;
    private int color;
    private long lastClickTime;

    /**
     * 构造
     *
     * @param context context
     */
    public Xslb0ImageView(Context context) {
        this(context, null);
    }

    /**
     * 构造
     *
     * @param context context
     * @param attrs   attrs
     */
    public Xslb0ImageView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        setDrawableRadius(getResources().getDimensionPixelOffset(R.dimen.hw_dp_4));
        color = context.getResources().getColor(R.color.color_100_f8f8f8);
        setImageResource(R.color.color_100_f8f8f8);
        setCircleBackgroundColor(color);
        setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        this.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                long currentClickTime = System.currentTimeMillis();
                if (currentClickTime - lastClickTime > TempletContant.CLICK_DISTANSE) {
                    templetPresenter.skipToXslb();
                    lastClickTime = currentClickTime;
                }
            }
        });
    }

    public void setTempletPresenter(TempletPresenter templetPresenter) {
        this.templetPresenter = templetPresenter;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        float num = 56;
        float scaleSize = 328 / num;
        int measureWidth = MeasureSpec.getSize(widthMeasureSpec);
        if (measureWidth <= 0) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        } else {
            setMeasuredDimension(measureWidth, (int) (measureWidth / scaleSize));
        }
    }

    /**
     * 绑定数据
     *
     * @param templetInfo templetInfo
     */
    public void bindData(BeanTempletInfo templetInfo) {
        if (templetInfo != null && !TextUtils.isEmpty(templetInfo.img)) {
            GlideImageLoadUtils.getInstanse().glideImageLoadFromUrl(getContext(), this, templetInfo.img, color);
        }
    }
}
