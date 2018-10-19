package com.dzbook.view.store;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.Nullable;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;

import com.dzbook.lib.utils.CompatUtils;
import com.dzbook.utils.DimensionPixelUtil;
import com.ishugui.R;

/**
 * CountDownTextView
 *
 * @author dongdianzhou on 2018/1/29.
 */

public class CountDownTextView extends View {
    private static final int COUNTER_DELAY = 1000;

    private CountDownListener countDownListener;

    private TextPaint paint;

    private long counter;

    private String text;

    private float leading;
    /**
     * 0：正常
     * 1：加粗
     * 2：字重
     */
    private int isBold = 0;

    private Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            setText();
        }
    };

    /**
     * 构造
     *
     * @param context context
     */
    public CountDownTextView(Context context) {
        this(context, null);
    }

    /**
     * 构造
     *
     * @param context context
     * @param attrs   attrs
     */
    public CountDownTextView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView();
        initData(attrs);
        setListener();
    }

    private void setListener() {
    }


    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mHandler.removeMessages(0);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        mHandler.sendEmptyMessage(0);
    }

    /**
     * 绑定数据
     *
     * @param startCounter startCounter
     */
    public void bindData(long startCounter) {
        if (startCounter > 0) {
            counter = startCounter;
            mHandler.removeMessages(0);
            setText();
        } else {
            setVisibility(View.INVISIBLE);
        }

    }

    private void setText() {
        long dis = (counter - System.currentTimeMillis()) / 1000;
        if (dis > 0) {
            text = getTime(dis);
            setVisibility(View.VISIBLE);
            mHandler.sendEmptyMessageDelayed(0, COUNTER_DELAY);
        } else {
            if (countDownListener != null) {
                countDownListener.countdown();
            }
            setVisibility(View.INVISIBLE);
            mHandler.removeMessages(0);
        }
        invalidate();
    }


    private void initData(AttributeSet attrs) {
        paint = new TextPaint();
        paint.setStyle(Paint.Style.FILL);
        paint.setAntiAlias(true);
        TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.CountDownTextView, 0, 0);
        if (a != null) {
            isBold = a.getInt(R.styleable.CountDownTextView_is_bold, 0);
            int defaultColor = CompatUtils.getColor(getContext(), R.color.color_fb934e);
            int color = a.getColor(R.styleable.CountDownTextView_countdown_color, defaultColor);
            paint.setColor(color);
            int textsize = a.getInt(R.styleable.CountDownTextView_countdown_size, 14);
            paint.setTextSize(DimensionPixelUtil.dip2px(getContext(), textsize));
            a.recycle();
        }
        if (isBold == 0) {
            paint.setTypeface(Typeface.DEFAULT);
        } else if (isBold == 1) {
            paint.setTypeface(Typeface.DEFAULT_BOLD);
        } else if (isBold == 2) {
            paint.setTypeface(Typeface.create("HwChinese-medium", Typeface.NORMAL));
        }
        this.setWillNotDraw(false);
        this.setDrawingCacheEnabled(true);
        this.setClickable(true);
    }

    private void initView() {

    }

    /**
     * 获取时间
     *
     * @param time s为单位
     * @return String
     */
    public String getTime(long time) {
        long dayS = 60 * 60 * 24;
        long hourS = 60 * 60;
        long minS = 60;
        int day = (int) (time / dayS);
        int hour = (int) ((time - day * dayS) / hourS);
        int min = (int) ((time - day * dayS - hour * hourS) / minS);
        int ss = (int) (time - day * dayS - hour * hourS - min * minS);
        String dayStr = "";
        String hourStr = "";
        String minStr = "";
        String ssStr = "";
        if (day < 10) {
            dayStr = "0" + day;
        } else {
            dayStr = day + "";
        }
        if (hour < 10) {
            hourStr = "0" + hour;
        } else {
            hourStr = hour + "";
        }
        if (min < 10) {
            minStr = "0" + min;
        } else {
            minStr = min + "";
        }
        if (ss < 10) {
            ssStr = "0" + ss;
        } else {
            ssStr = ss + "";
        }
        if (day != 0) {
            return dayStr + "天" + hourStr + ":" + minStr + ":" + ssStr;
        } else {
            return hourStr + ":" + minStr + ":" + ssStr;
        }
    }

    /**
     * 接口
     */
    public interface CountDownListener {

        /**
         * 倒计时
         */
        void countdown();
    }


    public void setCountDownListener(CountDownListener countDownListener) {
        this.countDownListener = countDownListener;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (!TextUtils.isEmpty(text)) {
            int width = (int) paint.measureText(text);
            Paint.FontMetrics metrics = paint.getFontMetrics();
            int height = (int) (metrics.bottom - metrics.top);
            leading = (float) height / 2 + (Math.abs(metrics.ascent) - metrics.descent) / 2;
            widthMeasureSpec = MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY);
            heightMeasureSpec = MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY);
        } else {
            widthMeasureSpec = MeasureSpec.makeMeasureSpec(100, MeasureSpec.EXACTLY);
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (!TextUtils.isEmpty(text)) {
            canvas.drawText(text, 0, leading, paint);
        }
    }
}
