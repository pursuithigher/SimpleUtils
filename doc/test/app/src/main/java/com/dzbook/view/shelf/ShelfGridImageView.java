package com.dzbook.view.shelf;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;

import com.dzbook.utils.DimensionPixelUtil;
import com.ishugui.R;

/**
 * ShelfGridImageView
 */
public class ShelfGridImageView extends AppCompatImageView {
    /**
     * 构造
     *
     * @param context context
     */
    public ShelfGridImageView(Context context) {
        this(context, null);
    }

    /**
     * 构造
     *
     * @param context context
     * @param attrs   attrs
     */
    public ShelfGridImageView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        setScaleType(ScaleType.FIT_XY);
        setImageResource(R.drawable.ic_hw_shelf_book_bk);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        heightMeasureSpec = MeasureSpec.makeMeasureSpec(DimensionPixelUtil.dip2px(getContext(), 12), MeasureSpec.EXACTLY);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }
}
