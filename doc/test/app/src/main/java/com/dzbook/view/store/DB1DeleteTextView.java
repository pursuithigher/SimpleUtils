package com.dzbook.view.store;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;

import com.dzbook.lib.utils.CompatUtils;
import com.dzbook.utils.DimensionPixelUtil;
import com.ishugui.R;

/**
 * DB1DeleteTextView
 */
public class DB1DeleteTextView extends AppCompatTextView {

    private Paint textPaint;

    /**
     * 构造
     *
     * @param context context
     */
    public DB1DeleteTextView(Context context) {
        this(context, null);
    }

    /**
     * 构造
     *
     * @param context context
     * @param attrs   attrs
     */
    public DB1DeleteTextView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        textPaint = new Paint();
        textPaint.setColor(CompatUtils.getColor(getContext(), R.color.color_30_1A1A1A));

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int width = canvas.getWidth();
        int height = canvas.getHeight();
        int lineHeight = DimensionPixelUtil.dip2px(getContext(), 1);
        canvas.drawRect(0, height / 2 - lineHeight / 2, width, height / 2 + lineHeight, textPaint);
    }
}
