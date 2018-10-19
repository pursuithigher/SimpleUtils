package com.dzbook.view.common;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

/***
 * 黑色背景View
 */
public class BlackBackView extends View {

    private Paint mPaint;
    private int mAlpha = 0;

    /**
     * 构造
     *
     * @param context context
     */
    public BlackBackView(Context context) {
        super(context);
        init();
    }

    /**
     * 构造
     *
     * @param context context
     * @param attrs   attrs
     */
    public BlackBackView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        mPaint = new Paint();
        mPaint.setColor(Color.BLACK);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        mPaint.setAlpha(mAlpha);
        canvas.drawRect(0, 0, getMeasuredWidth(), getMeasuredHeight(), mPaint);
    }

    /**
     * 设置透明度
     *
     * @param alpha 透明度
     */
    public void setAlpha(int alpha) {
        mAlpha = alpha;
        invalidate();
    }
}
