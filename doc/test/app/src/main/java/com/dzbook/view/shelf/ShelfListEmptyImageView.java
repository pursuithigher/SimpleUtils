package com.dzbook.view.shelf;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;

import com.dzbook.utils.DimensionPixelUtil;

/**
 * 空列表
 */
public class ShelfListEmptyImageView extends AppCompatImageView {
    /**
     * 构造
     *
     * @param context context
     */
    public ShelfListEmptyImageView(Context context) {
        this(context, null);
    }

    /**
     * 构造
     *
     * @param context context
     * @param attrs   attrs
     */
    public ShelfListEmptyImageView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        heightMeasureSpec = MeasureSpec.makeMeasureSpec(DimensionPixelUtil.dip2px(getContext(), 4), MeasureSpec.EXACTLY);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }
}
