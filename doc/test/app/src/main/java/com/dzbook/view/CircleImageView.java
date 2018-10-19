package com.dzbook.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.RectF;
import android.os.Build;
import android.support.annotation.ColorInt;
import android.support.annotation.ColorRes;
import android.util.AttributeSet;

import com.dzbook.lib.utils.CompatUtils;
import com.ishugui.R;

/**
 * 圆角image
 *
 * @author Winzows on 2017/11/29.
 */

@SuppressLint("AppCompatCustomView")
public class CircleImageView extends BaseImageView {

    /**
     * 构造
     *
     * @param context context
     */
    public CircleImageView(Context context) {
        super(context);

        init();
    }

    /**
     * 构造
     *
     * @param context context
     * @param attrs   attrs
     */
    public CircleImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    /**
     * 构造
     *
     * @param context  context
     * @param attrs    attrs
     * @param defStyle defStyle
     */
    public CircleImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.CircleImageView, defStyle, 0);

        mBorderWidth = a.getDimensionPixelSize(R.styleable.CircleImageView_civ_border_width, DEFAULT_BORDER_WIDTH);
        mBorderColor = a.getColor(R.styleable.CircleImageView_civ_border_color, DEFAULT_BORDER_COLOR);
        mBorderOverlay = a.getBoolean(R.styleable.CircleImageView_civ_border_overlay, DEFAULT_BORDER_OVERLAY);

        // Look for deprecated civ_fill_color if civ_circle_background_color is not set
        if (a.hasValue(R.styleable.CircleImageView_civ_circle_background_color)) {
            mCircleBackgroundColor = a.getColor(R.styleable.CircleImageView_civ_circle_background_color, DEFAULT_CIRCLE_BACKGROUND_COLOR);
        } else if (a.hasValue(R.styleable.CircleImageView_civ_fill_color)) {
            mCircleBackgroundColor = a.getColor(R.styleable.CircleImageView_civ_fill_color, DEFAULT_CIRCLE_BACKGROUND_COLOR);
        }

        a.recycle();

        init();
    }


    @Override
    protected void onDraw(Canvas canvas) {
        if (mDisableCircularTransformation) {
            super.onDraw(canvas);
            return;
        }

        if (mBitmap == null) {
            return;
        }

        if (mCircleBackgroundColor != Color.TRANSPARENT) {
            canvas.drawCircle(mDrawableRect.centerX(), mDrawableRect.centerY(), mDrawableRadius, mCircleBackgroundPaint);
        }
        canvas.drawCircle(mDrawableRect.centerX(), mDrawableRect.centerY(), mDrawableRadius, mBitmapPaint);
        if (mBorderWidth > 0) {
            canvas.drawCircle(mBorderRect.centerX(), mBorderRect.centerY(), mBorderRadius, mBorderPaint);
        }
    }

    private void init() {
        super.setScaleType(SCALE_TYPE);
        mReady = true;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            setOutlineProvider(new OutlineProvider());
        }

        if (mSetupPending) {
            setup();
            mSetupPending = false;
        }
    }


    /**
     * 背景
     *
     * @param circleBackgroundRes circleBackgroundRes
     */
    public void setCircleBackgroundColorResource(@ColorRes int circleBackgroundRes) {
        setCircleBackgroundColor(CompatUtils.getColor(getContext(), circleBackgroundRes));
    }

    /**
     * Return the color drawn behind the circle-shaped drawable.
     *
     * @return The color drawn behind the drawable
     * @deprecated Use {@link #getCircleBackgroundColor()} instead.
     */
    @Deprecated
    public int getFillColor() {
        return getCircleBackgroundColor();
    }

    /**
     * Set a color to be drawn behind the circle-shaped drawable. Note that
     * this has no effect if the drawable is opaque or no drawable is set.
     *
     * @param fillColor The color to be drawn behind the drawable
     * @deprecated Use {@link #setCircleBackgroundColor(int)} instead.
     */
    @Deprecated
    public void setFillColor(@ColorInt int fillColor) {
        setCircleBackgroundColor(fillColor);
    }


    @Override
    public void setup() {
        final float num = 2.0f;
        superSetUp();
        if (mDrawableRect == null) {
            mDrawableRect = new RectF();
        }

        if (mBorderRect == null) {
            mBorderRect = new RectF();
        }
        mBorderRect.set(calculateBounds());
        mBorderRadius = Math.min((mBorderRect.height() - mBorderWidth) / num, (mBorderRect.width() - mBorderWidth) / num);

        mDrawableRect.set(mBorderRect);
        if (!mBorderOverlay && mBorderWidth > 0) {
            mDrawableRect.inset(mBorderWidth - 1.0f, mBorderWidth - 1.0f);
        }
        mDrawableRadius = Math.min(mDrawableRect.height() / num, mDrawableRect.width() / num);

        applyColorFilter();
        updateShaderMatrix();
        invalidate();
    }

    private RectF calculateBounds() {
        int availableWidth = getWidth() - getPaddingLeft() - getPaddingRight();
        int availableHeight = getHeight() - getPaddingTop() - getPaddingBottom();

        int sideLength = Math.min(availableWidth, availableHeight);

        float left = getPaddingLeft() + (availableWidth - sideLength) / 2f;
        float top = getPaddingTop() + (availableHeight - sideLength) / 2f;

        return new RectF(left, top, left + sideLength, top + sideLength);
    }

}
