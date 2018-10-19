package com.dzbook.view.common;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;

import com.dzbook.utils.DimensionPixelUtil;

/***
 * 书籍封面 ImageView __ 列表模式使用
 */
public class ShelfListBookImageView extends BookImageView {

    private Paint backgroundPaint;
    private RectF backgroundRect;

    /**
     * 构造
     *
     * @param context context
     */
    public ShelfListBookImageView(Context context) {
        super(context);
        init();
    }

    /**
     * 构造
     *
     * @param context context
     * @param attrs   attrs
     */
    public ShelfListBookImageView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        resetBookSize();
        init();
    }

    private void init() {
        backgroundPaint = new Paint();
        backgroundPaint.setStyle(Paint.Style.FILL);
        backgroundPaint.setColor(Color.parseColor("#d8d8d8"));
        backgroundPaint.setAntiAlias(true);

        backgroundRect = new RectF();
    }

    private void resetBookSize() {
        bookWidth = DimensionPixelUtil.dip2px(getContext(), 56);
        bookHeight = DimensionPixelUtil.dip2px(getContext(), 75);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        backgroundRect.left = getPaddingLeft();
        backgroundRect.top = getPaddingTop();
        backgroundRect.right = getMeasuredWidth() - getPaddingRight();
        backgroundRect.bottom = getMeasuredHeight() - getPaddingRight();
        canvas.drawRoundRect(backgroundRect, bookRadius, bookRadius, backgroundPaint);
        super.onDraw(canvas);
    }
}
