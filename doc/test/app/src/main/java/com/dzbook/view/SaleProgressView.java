package com.dzbook.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import com.ishugui.R;

import java.text.DecimalFormat;

/**
 * SaleProgressView
 *
 * @author zh on 2017/8/19.
 */
public class SaleProgressView extends View {
    private static final float NUMBER = 0.5f;

    //商品总数
    private int totalCount;
    //当前卖出数
    private int currentCount;
    //动画需要的
    private int progressCount;
    //售出比例
    private float scale;
    //边框颜色
    private int sideColor;
    //文字颜色
    private int textColor;
    //边框粗细
    private float sideWidth;
    //边框所在的矩形
    private Paint sidePaint;
    //背景矩形
    private RectF bgRectF;
    private float radius;
    private int width;
    private int height;
    private PorterDuffXfermode mPorterDuffXfermode;
    private Paint srcPaint;
    private Bitmap fgSrc;
    private Bitmap bgSrc;

    private float textSize;

    private Paint textPaint;
    private float baseLineY;
    private Bitmap bgBitmap;
    private boolean isNeedAnim;

    /**
     * 构造
     *
     * @param context context
     */
    public SaleProgressView(Context context) {
        this(context, null);
    }

    /**
     * 构造
     *
     * @param context context
     * @param attrs   attrs
     */
    public SaleProgressView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initAttrs(context, attrs);
        initPaint();
    }

    private void initAttrs(Context context, AttributeSet attrs) {
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.SaleProgressView);
        sideColor = ta.getColor(R.styleable.SaleProgressView_progress_sideColor, 0xfffdc0bf);
        textColor = ta.getColor(R.styleable.SaleProgressView_progress_textColor, 0xffff3c32);
        sideWidth = ta.getDimension(R.styleable.SaleProgressView_progress_sideWidth, dp2px(2));
        textSize = ta.getDimension(R.styleable.SaleProgressView_progress_textSize, sp2px(16));
        isNeedAnim = ta.getBoolean(R.styleable.SaleProgressView_progress_isNeedAnim, true);
        ta.recycle();
    }

    private void initPaint() {
        sidePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        sidePaint.setStyle(Paint.Style.STROKE);
        sidePaint.setStrokeWidth(sideWidth);
        sidePaint.setColor(sideColor);

        srcPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

        textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setStyle(Paint.Style.FILL);
        textPaint.setTextSize(textSize);

        mPorterDuffXfermode = new PorterDuffXfermode(PorterDuff.Mode.SRC_IN);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        final float num = 2.0f;
        //获取View的宽高
        width = getMeasuredWidth();
        height = getMeasuredHeight();

        //圆角半径
        radius = height / num;
        //留出一定的间隙，避免边框被切掉一部分
        if (bgRectF == null) {
            bgRectF = new RectF(sideWidth, sideWidth, width - sideWidth, height - sideWidth);
        }

        if (baseLineY == 0.0f) {
            Paint.FontMetricsInt fm = textPaint.getFontMetricsInt();
            baseLineY = height / 2 - (fm.descent / 2 + fm.ascent / 2);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (!isNeedAnim) {
            progressCount = currentCount;
        }

        if (totalCount == 0) {
            scale = 0.0f;
        } else {
            scale = Float.parseFloat(new DecimalFormat("0.00").format((float) progressCount / (float) totalCount));
        }

        drawSide(canvas);
        drawBg(canvas);
        drawFg(canvas);
        drawText(canvas);

        //这里是为了演示动画方便，实际开发中进度只会增加
        if (progressCount != currentCount) {
            if (progressCount < currentCount) {
                progressCount++;
            } else {
                progressCount--;
            }
            postInvalidate();
        }

    }

    //绘制背景边框
    private void drawSide(Canvas canvas) {
        canvas.drawRoundRect(bgRectF, radius, radius, sidePaint);
    }

    //绘制背景
    private void drawBg(Canvas canvas) {
        if (bgBitmap == null) {
            bgBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        }
        Canvas bgCanvas = new Canvas(bgBitmap);
        if (bgSrc == null) {
            bgSrc = BitmapFactory.decodeResource(getResources(), R.drawable.icon_progress_bk);
        }
        bgCanvas.drawRoundRect(bgRectF, radius, radius, srcPaint);

        srcPaint.setXfermode(mPorterDuffXfermode);
        bgCanvas.drawBitmap(bgSrc, null, bgRectF, srcPaint);

        canvas.drawBitmap(bgBitmap, 0, 0, null);
        srcPaint.setXfermode(null);
    }

    //绘制进度条
    private void drawFg(Canvas canvas) {
        if (scale == 0.0f) {
            return;
        }
        Bitmap fgBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas fgCanvas = new Canvas(fgBitmap);
        if (fgSrc == null) {
            fgSrc = BitmapFactory.decodeResource(getResources(), R.drawable.icon_progress_fg);
        }
        fgCanvas.drawRoundRect(new RectF(sideWidth, sideWidth, (width - sideWidth) * scale, height - sideWidth), radius, radius, srcPaint);

        srcPaint.setXfermode(mPorterDuffXfermode);
        fgCanvas.drawBitmap(fgSrc, null, bgRectF, srcPaint);

        canvas.drawBitmap(fgBitmap, 0, 0, null);
        srcPaint.setXfermode(null);
    }

    //绘制文字信息
    private void drawText(Canvas canvas) {
        String scaleText = new DecimalFormat("#%").format(scale);
        String saleText = "已抢" + scaleText; //String.format("已抢%s件", progressCount)
        Bitmap textBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas textCanvas = new Canvas(textBitmap);
        textPaint.setColor(textColor);
        textCanvas.drawText(saleText, dp2px(10), baseLineY, textPaint);

        //        if (scale < 0.8f) {
        //            textCanvas.drawText(saleText, dp2px(10), baseLineY, textPaint);
        //            textCanvas.drawText(scaleText, width - scaleTextWidth - dp2px(10), baseLineY, textPaint);
        //        } else if (scale < 1.0f) {
        //            textCanvas.drawText(nearOverText, width / 2 - nearOverTextWidth / 2, baseLineY, textPaint);
        //            textCanvas.drawText(scaleText, width - scaleTextWidth - dp2px(10), baseLineY, textPaint);
        //        } else {
        //            textCanvas.drawText(overText, width / 2 - overTextWidth / 2, baseLineY, textPaint);
        //        }

        textPaint.setXfermode(mPorterDuffXfermode);
        textPaint.setColor(Color.WHITE);
        textCanvas.drawRoundRect(new RectF(sideWidth, sideWidth, (width - sideWidth) * scale, height - sideWidth), radius, radius, textPaint);
        canvas.drawBitmap(textBitmap, 0, 0, null);
        textPaint.setXfermode(null);
    }

    private int dp2px(float dpValue) {
        float scale1 = getContext().getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale1 + NUMBER);
    }

    private int sp2px(float spValue) {
        float scale1 = getContext().getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * scale1 + NUMBER);
    }

    /**
     * setTotalAndCurrentCount
     *
     * @param totalCount1   totalCount
     * @param currentCount1 currentCount
     */
    public void setTotalAndCurrentCount(int totalCount1, int currentCount1) {
        this.totalCount = totalCount1;
        if (currentCount1 > totalCount1) {
            currentCount1 = totalCount1;
        }
        this.currentCount = currentCount1;
        postInvalidate();
    }
}
