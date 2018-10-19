package com.dzbook.view.text;

import android.annotation.SuppressLint;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Parcel;
import android.text.ParcelableSpan;
import android.text.TextPaint;
import android.text.style.MetricAffectingSpan;

/**
 * 自定义 span工具类
 *
 * @author caimt
 */
public class HwStyleSpan extends MetricAffectingSpan implements ParcelableSpan {
    /**
     * 华为字体
     */
    public static final int MEDIUM = 100;
    private static final float TEXT_SKEWX = -0.25f;
    private static Typeface mHwChineseMedium;

    private final int mStyle;

    /**
     * 构造
     *
     * @param style An integer constant describing the style for this span. Examples
     *              include bold, italic, and normal. Values are constants defined
     *              in {@link android.graphics.Typeface}.
     */
    public HwStyleSpan(int style) {
        mStyle = style;
    }

    /**
     * 构造
     *
     * @param src src
     */
    public HwStyleSpan(Parcel src) {
        mStyle = src.readInt();
    }

    @Override
    public int getSpanTypeId() {
        return getSpanTypeIdInternal();
    }

    /**
     * 获取 SpanTypeIdInternal
     *
     * @return SpanTypeIdInternal
     */
    public int getSpanTypeIdInternal() {
//        return TextUtils.STYLE_SPAN;
        /**
         * hide方法 直接返回具体值
         */
        return 7;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        writeToParcelInternal(dest, flags);
    }

    /**
     * 写入
     *
     * @param dest  dest
     * @param flags flags
     */
    public void writeToParcelInternal(Parcel dest, int flags) {
        dest.writeInt(mStyle);
    }

    /**
     * Returns the style constant defined in {@link android.graphics.Typeface}.
     *
     * @return style
     */
    public int getStyle() {
        return mStyle;
    }

    @Override
    public void updateDrawState(TextPaint ds) {
        apply(ds, mStyle);
    }

    @Override
    public void updateMeasureState(TextPaint paint) {
        apply(paint, mStyle);
    }

    @SuppressLint("WrongConstant")
    private static void apply(Paint paint, int style) {
        if (style == MEDIUM) {
            if (mHwChineseMedium == null) {
                mHwChineseMedium = Typeface.create("HwChinese-medium", Typeface.NORMAL);
            }
            paint.setTypeface(mHwChineseMedium);
            return;
        }
        int oldStyle;

        Typeface old = paint.getTypeface();
        if (old == null) {
            oldStyle = 0;
        } else {
            oldStyle = old.getStyle();
        }

        int want = oldStyle | style;

        Typeface tf;
        if (old == null) {
            tf = Typeface.defaultFromStyle(want);
        } else {
            tf = Typeface.create(old, want);
        }

        int fake = want & ~tf.getStyle();

        if ((fake & Typeface.BOLD) != 0) {
            paint.setFakeBoldText(true);
        }

        if ((fake & Typeface.ITALIC) != 0) {
            paint.setTextSkewX(TEXT_SKEWX);
        }

        paint.setTypeface(tf);
    }
}
