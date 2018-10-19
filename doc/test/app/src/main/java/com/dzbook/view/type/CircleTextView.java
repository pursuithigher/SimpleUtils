package com.dzbook.view.type;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * CircleTextView
 *
 * @author Winzows  2018/3/2
 */

@SuppressLint("AppCompatCustomView")
public class CircleTextView extends TextView {
    private int fillColor = Color.RED;
    private Paint fillPaint = new Paint();

    /**
     * 构造
     *
     * @param context context
     */
    public CircleTextView(Context context) {
        super(context);
    }

    /**
     * 构造
     *
     * @param context context
     * @param attrs   attrs
     */
    public CircleTextView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    /**
     * 构造
     *
     * @param context      context
     * @param attrs        attrs
     * @param defStyleAttr defStyleAttr
     */
    public CircleTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        int width = getWidth();
        int height = getWidth();
        int radius = Math.min(width, height) / 2;
        fillPaint.setColor(fillColor);
        //抗锯齿
        fillPaint.setAntiAlias(true);
        canvas.drawCircle((float) getWidth() / 2, (float) getWidth() / 2, (float) radius, fillPaint);
        super.onDraw(canvas);
    }

    /**
     * 设置颜色
     *
     * @param color color
     */
    public void setColor(int color) {
        fillColor = color;
        postInvalidate();
    }
}
