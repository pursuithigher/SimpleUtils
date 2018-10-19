package com.dzbook.utils;

import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.StrikethroughSpan;
import android.text.style.StyleSpan;

/**
 * 创建一段复杂格式的文本
 *
 * @author zhenglk 15/9/9.
 */
public class DzSpanBuilder extends SpannableStringBuilder {

    /**
     * Create a new DzSpanBuilder with empty contents
     */
    public DzSpanBuilder() {
        super();
    }

    /**
     * Create a new DzSpanBuilder containing a copy of the
     * specified text, including its spans if any.
     *
     * @param text text
     */
    public DzSpanBuilder(CharSequence text) {
        super(text);
    }

    /**
     * Create a new DzSpanBuilder containing a copy of the
     * specified slice of the specified text, including its spans if any.
     *
     * @param text  text
     * @param start start
     * @param end   end
     */
    public DzSpanBuilder(CharSequence text, int start, int end) {
        super(text, start, end);
    }

    /**
     * 追加带有删除线的文本
     *
     * @param text 文本
     * @return 自身
     */
    public DzSpanBuilder appendStrike(CharSequence text) {
        if (!TextUtils.isEmpty(text)) {
            StrikethroughSpan span = new StrikethroughSpan();
            SpannableString spanString = new SpannableString(text);
            spanString.setSpan(span, 0, text.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            append(spanString);
        }
        return this;
    }

    /**
     * 追加带有色值的文本
     *
     * @param text 文本
     * @return 自身
     */
    public DzSpanBuilder appendBold(CharSequence text) {
        if (!TextUtils.isEmpty(text)) {
            final StyleSpan span = new StyleSpan(android.graphics.Typeface.BOLD);
            SpannableString spanString = new SpannableString(text);
            spanString.setSpan(span, 0, text.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            append(spanString);
        }
        return this;
    }

    /**
     * 追加 加粗文本
     *
     * @param text  文本
     * @param color 颜色值
     * @return 自身
     */
    public DzSpanBuilder appendColor(CharSequence text, int color) {
        if (!TextUtils.isEmpty(text)) {
            ForegroundColorSpan span = new ForegroundColorSpan(color);
            SpannableString spanString = new SpannableString(text);
            spanString.setSpan(span, 0, text.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            append(spanString);
        }
        return this;
    }


    /**
     * 追加 字体大小
     *
     * @param text 文本
     * @param size 字体大小 像素
     * @return 自身
     */
    public DzSpanBuilder appendFontSize(CharSequence text, int size) {
        if (!TextUtils.isEmpty(text)) {
            AbsoluteSizeSpan span = new AbsoluteSizeSpan(size);
            SpannableString spanString = new SpannableString(text);
            spanString.setSpan(span, 0, text.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            append(spanString);
        }
        return this;
    }

    @Override
    public DzSpanBuilder append(CharSequence text) {
        super.append(text);
        return this;
    }

    @Override
    public DzSpanBuilder append(CharSequence text, int start, int end) {
        super.append(text, start, end);
        return this;
    }

    @Override
    public DzSpanBuilder append(char text) {
        super.append(text);
        return this;
    }

    @Override
    public DzSpanBuilder replace(int start, int end, CharSequence tb) {
        super.replace(start, end, tb);
        return this;
    }

    @Override
    public DzSpanBuilder replace(int start, int end, CharSequence tb, int tbstart, int tbend) {
        super.replace(start, end, tb, tbstart, tbend);
        return this;
    }

    @Override
    public SpannableStringBuilder insert(int where, CharSequence tb, int start, int end) {
        super.insert(where, tb, start, end);
        return this;
    }

    @Override
    public SpannableStringBuilder insert(int where, CharSequence tb) {
        super.insert(where, tb);
        return this;
    }

    @Override
    public SpannableStringBuilder delete(int start, int end) {
        super.delete(start, end);
        return this;
    }
}