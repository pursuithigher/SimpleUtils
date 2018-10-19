package com.dzbook.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.ProgressBar;

import com.dzbook.utils.DimensionPixelUtil;
import com.ishugui.R;

/**
 * 插件进度条
 *
 * @author kongxp
 */
public class PluginDLProgress extends ProgressBar {
    String text;
    Paint mPaint;
    private Typeface mHwChineseMedium;

    /**
     * 构造
     *
     * @param context context
     */
    public PluginDLProgress(Context context) {
        super(context);
        System.out.println("1");
        initText();
    }

    /**
     * 构造
     *
     * @param context  context
     * @param attrs    attrs
     * @param defStyle defStyle
     */
    public PluginDLProgress(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        System.out.println("2");
        initText();
    }


    /**
     * 构造
     *
     * @param context context
     * @param attrs   attrs
     */
    public PluginDLProgress(Context context, AttributeSet attrs) {
        super(context, attrs);
        System.out.println("3");
        initText();
    }

    @Override
    public synchronized void setProgress(int progress) {
        setText(progress);
        super.setProgress(progress);

    }

    @Override
    protected synchronized void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Rect rect = new Rect();
        this.mPaint.getTextBounds(this.text, 0, this.text.length(), rect);
        int x = (getWidth() / 2) - rect.centerX();
        int y = (getHeight() / 2) - rect.centerY();
        canvas.drawText(this.text, x, y, this.mPaint);
    }

    /**
     * 初始化，画笔
     */
    private void initText() {
        this.mPaint = new Paint();
        this.mPaint.setColor(Color.parseColor("#000000"));
        this.mPaint.setTextSize(DimensionPixelUtil.sp2px(getContext(), 15));
        this.mPaint.setAntiAlias(true);

        if (mHwChineseMedium == null) {
            mHwChineseMedium = Typeface.create("HwChinese-medium", Typeface.NORMAL);
        }

        if (mHwChineseMedium == null) {
            return;
        }
        mPaint.setTypeface(mHwChineseMedium);
    }

    /**
     * 设置文字内容
     *
     * @param progress progress
     */
    public synchronized void setText(int progress) {
        this.text = String.format(getResources().getString(R.string.str_percent), String.valueOf(progress));
    }

}