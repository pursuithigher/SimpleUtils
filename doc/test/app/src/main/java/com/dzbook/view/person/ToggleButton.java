package com.dzbook.view.person;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

import com.dzbook.lib.utils.ALog;
import com.ishugui.R;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 * ToggleButton
 *
 * @author dongdianzhou on 2017/4/6.
 */

public class ToggleButton extends View implements View.OnClickListener {
    static final int EVERYT = 15;
    /**
     * 定时器 总的次数
     */
    private static final int COUNT_TIME = 100;

    Paint linePaint = null;
    Paint circlePaint = null;
    float lineTop = 0;
    float lineBottom = 0;
    float lineLeft = 0;
    float lineRight = 0;

    float btnY = 0, btnRadius = 0;
    int circleXMIN = 0, circleXMAX = 0;

    float everyX = 0;
    /**
     * 轮训器 遍历时的index
     */
    private int index = 0;

    /**
     * 定时器的实现
     */
    private Disposable mDisposable;
    private OnToggleChanged listener;
    private int onColor = Color.parseColor("#4ebb7f");
    private int offColor = Color.parseColor("#dadbda");
    private boolean toggleOn = true;
    private int btnWidth = 40;// 宽度
    private int btnHeight = 30;// 高度
    private int circleRadius = 8;// 圆的半径
    private int lineWidth = 2;// 直线高度
    private int circleX;// 圆心X轴坐标
    private boolean changeCompleted = true;
    private boolean isStroke = true;
    private Resources r;

    /**
     * 构造
     *
     * @param context context
     */
    public ToggleButton(Context context) {
        this(context, null, 0);
    }

    /**
     * 构造
     *
     * @param context context
     * @param attrs   attrs
     */
    public ToggleButton(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    /**
     * 构造
     *
     * @param context      context
     * @param attrs        attrs
     * @param defStyleAttr defStyleAttr
     */
    public ToggleButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        doInit(attrs, defStyleAttr);
    }

    public int getOnColor() {
        return onColor;
    }

    /**
     * 设置颜色
     *
     * @param onColor onColor
     */
    public void setOnColor(int onColor) {
        this.onColor = onColor;
        invalidate();
    }

    public int getOffColor() {
        return offColor;
    }

    /**
     * 设置关闭颜色
     *
     * @param offColor offColor
     */
    public void setOffColor(int offColor) {
        this.offColor = offColor;
        invalidate();
    }

    public boolean isToggleOn() {
        return toggleOn;
    }

    private void doInit(AttributeSet attrs, int defStyleAttr) {
        TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.ToggleButton, defStyleAttr, 0);
        if (a != null) {
            onColor = a.getColor(R.styleable.ToggleButton_onColor, Color.parseColor("#4ebb7f"));
            offColor = a.getColor(R.styleable.ToggleButton_offColor, Color.parseColor("#dadbda"));
            btnWidth = a.getInteger(R.styleable.ToggleButton_btnWidth, btnWidth);
            btnHeight = a.getInteger(R.styleable.ToggleButton_btnHeight, btnHeight);
            circleRadius = a.getInteger(R.styleable.ToggleButton_circleRadius, circleRadius);
            lineWidth = a.getInteger(R.styleable.ToggleButton_lineHeight, lineWidth);
            isStroke = a.getBoolean(R.styleable.ToggleButton_isStroke, true);
            //            ALog.eDongdz("doInit:toggleOff:isStroke" + isStroke);
            a.recycle();
        }
        r = Resources.getSystem();
        setOnClickListener(this);
        circleX = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, btnWidth - circleRadius, r.getDisplayMetrics());

        linePaint = new Paint();
        linePaint.setAntiAlias(true);
        linePaint.setStyle(Paint.Style.FILL);
        linePaint.setStrokeWidth(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, lineWidth, r.getDisplayMetrics()));

        circlePaint = new Paint();
        circlePaint.setAntiAlias(true);
        circlePaint.setStyle(isStroke ? Paint.Style.STROKE : Paint.Style.FILL);
        circlePaint.setStrokeWidth(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, lineWidth > circleRadius ? circleRadius : lineWidth, r.getDisplayMetrics()));
        float num = 2;
        lineTop = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, (btnHeight - lineWidth) / num, r.getDisplayMetrics());
        lineBottom = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, (btnHeight + lineWidth) / num, r.getDisplayMetrics());
        lineRight = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, btnWidth, r.getDisplayMetrics());

        btnY = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, btnHeight / num, r.getDisplayMetrics());
        btnRadius = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, circleRadius, r.getDisplayMetrics());

        circleXMIN = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, circleRadius + lineWidth / num, r.getDisplayMetrics());
        circleXMAX = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, btnWidth - circleRadius, r.getDisplayMetrics());
        float num2 = 180;
        everyX = (circleXMAX - circleXMIN) / (num2 / EVERYT);
        if (everyX < 1) {
            everyX = 1;
        }
    }


    /**
     * 设置开关
     *
     * @param toggleOn toggleOn
     */
    public void setToggleOn(boolean toggleOn) {
        this.toggleOn = toggleOn;
        circleX = toggleOn ? circleXMAX : circleXMIN;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        int color = toggleOn ? onColor : offColor;

        linePaint.setColor(color);

        if (circleX > circleXMIN) {
            canvas.drawRect(lineLeft, lineTop, circleX - btnRadius, lineBottom, linePaint);// circle左边的线
        }
        if (circleX < circleXMAX) {
            canvas.drawRect(circleX + btnRadius, lineTop, lineRight, lineBottom, linePaint);// circle右边的线
        }

        circlePaint.setColor(color);
        canvas.drawCircle(circleX, btnY, btnRadius, circlePaint);

        //        ALog.eDongdz("onDraw: toggle=" + toggleOn + " isStroke=" + isStroke + " circleX=" + circleX);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        final int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        final int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        if (widthMode == MeasureSpec.UNSPECIFIED || widthMode == MeasureSpec.AT_MOST) {
            int widthSize = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, btnWidth + lineWidth, r.getDisplayMetrics());
            widthMeasureSpec = MeasureSpec.makeMeasureSpec(widthSize, MeasureSpec.EXACTLY);
        }
        if (heightMode == MeasureSpec.UNSPECIFIED || heightMode == MeasureSpec.AT_MOST) {
            int heightSize = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, btnHeight, r.getDisplayMetrics());
            heightMeasureSpec = MeasureSpec.makeMeasureSpec(heightSize, MeasureSpec.EXACTLY);
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    public void onClick(View v) {
        if (changeCompleted) {
            toggleOn = !toggleOn;

            changeCompleted = false;
            if (mDisposable != null) {
                mDisposable.dispose();
            }

            Observable.interval(0, 15, TimeUnit.MILLISECONDS)
                    //设置总共发送的次数
                    .take(COUNT_TIME + 1).map(new Function<Long, Long>() {
                @Override
                public Long apply(Long aLong) {
                    //aLong从0开始
                    return COUNT_TIME - aLong;
                }
            }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<Long>() {
                @Override
                public void onSubscribe(Disposable d) {
                    mDisposable = d;
                }

                @Override
                public void onNext(Long value) {

                    circleX += toggleOn ? everyX : -everyX;
                    // 越界处理。
                    if (circleX < circleXMIN) {
                        circleX = circleXMIN;
                    } else if (circleX > circleXMAX) {
                        circleX = circleXMAX;
                    }
                    // 这里注意要使用整形比较，否则可能会出现浮点比较得不到等值情况。
                    if (circleX == circleXMIN || circleX == circleXMAX) {
                        changeCompleted = true;
                        if (mDisposable != null) {
                            mDisposable.dispose();
                            ALog.dWz("mDisposable dispose！");
                        }
                    }

                    postInvalidate();
                    ALog.dWz("ToggleButton value:" + value + " index:" + index);
                }

                @Override
                public void onError(Throwable e) {
                    ALog.printExceptionWz(e);
                    if (mDisposable != null) {
                        mDisposable.dispose();
                    }
                }

                @Override
                public void onComplete() {
                    if (mDisposable != null) {
                        mDisposable.dispose();
                    }
                }
            });

            if (listener != null) {
                listener.onToggle(toggleOn);
            }
        }
    }

    /**
     * OnToggleChanged
     */
    public interface OnToggleChanged {
        /**
         * onToggle
         *
         * @param on on
         */
        void onToggle(boolean on);
    }

    /**
     * 状态改变
     *
     * @param onToggleChanged onToggleChanged
     */
    public void setOnToggleChanged(OnToggleChanged onToggleChanged) {
        listener = onToggleChanged;
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (mDisposable != null) {
            mDisposable.dispose();
        }
    }
}
