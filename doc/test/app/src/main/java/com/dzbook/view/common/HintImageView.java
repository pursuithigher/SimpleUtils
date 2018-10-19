package com.dzbook.view.common;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.View;

import com.dzbook.utils.DimensionPixelUtil;
import com.ishugui.R;

/***
 * 图文view
 */
public class HintImageView extends View {

    private TextPaint hintPaint;
    private Paint bitmapPaint;
    private Drawable bitmapDrawable;
    private Bitmap bitmap;
    private RectF bitmapRect;
    private String hint;
    private int hintLeft;
    private int hintTop;
    private int padding;
    private int hintSize;
    private int hintColor;

    /**
     * 构造
     *
     * @param context context
     */
    public HintImageView(Context context) {
        super(context);
        init();
    }

    /**
     * 构造
     *
     * @param context context
     * @param attrs   attrs
     */
    public HintImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initAttr(attrs);
        init();
    }

    private void initAttr(AttributeSet attrs) {
        TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.HintImageView, 0, 0);
        if (a != null) {
            bitmapDrawable = a.getDrawable(R.styleable.HintImageView_hint_image_drawable);
            hint = a.getString(R.styleable.HintImageView_hint_image_hint);
            hintSize = a.getDimensionPixelSize(R.styleable.HintImageView_hint_image_hint_size, DimensionPixelUtil.dip2px(getContext(), 15));
            hintColor = a.getColor(R.styleable.HintImageView_hint_image_hint_color, Color.parseColor("#1a1a1a"));
            padding = a.getDimensionPixelSize(R.styleable.HintImageView_hint_image_padding, DimensionPixelUtil.dip2px(getContext(), 10));
            a.recycle();
        }
    }

    private void init() {
        bitmapRect = new RectF();
        bitmapPaint = new Paint();
        bitmapPaint.setAntiAlias(true);

        hintPaint = new TextPaint();
        hintPaint.setAntiAlias(true);
        hintPaint.setColor(hintColor);
        hintPaint.setTextSize(hintSize);

        bitmap = ((BitmapDrawable) bitmapDrawable).getBitmap();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        resize();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        drawBitmap(canvas);
        drawHint(canvas);
    }

    private void resize() {
        int aBitWidth = bitmap.getWidth();
        int aBitHeight = bitmap.getHeight();
        int aHintWidth = (int) hintPaint.measureText(hint);
        int aHintHeight = (int) (hintPaint.descent() - hintPaint.ascent());
        int finalWidth = aBitWidth > aHintWidth ? aBitWidth : aHintWidth;
        int finalHeight = aBitHeight + aHintHeight + padding;
        setMeasuredDimension(finalWidth, finalHeight);
        // 图片
        bitmapRect.left = (float) (finalWidth - aBitWidth) / 2;
        bitmapRect.top = 0;
        bitmapRect.right = bitmapRect.left + aBitWidth;
        bitmapRect.bottom = aBitHeight;
        hintLeft = (finalWidth - aHintWidth) / 2;
        hintTop = finalHeight - aHintHeight + padding;
    }

    private void drawBitmap(Canvas canvas) {
        canvas.drawBitmap(bitmap, null, bitmapRect, bitmapPaint);
    }

    private void drawHint(Canvas canvas) {
        canvas.drawText(hint, 0, hint.length(), hintLeft, hintTop, hintPaint);
    }
}
