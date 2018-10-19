package com.dzbook.view.book;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;

import com.dzbook.r.util.ConvertUtils;

import java.util.ArrayList;

/**
 * BookTextInfoView
 */
public class BookTextInfoView extends View {
    private static final int MAX_DESC_LINE = 3;

    private boolean isTextBreak;

    private ArrayList<String> descLineList;
    private Paint descPaint;
    private float descLineHeight;
    private Paint.FontMetrics descMetrics;

    private String breakTitleStr;
    private Paint titlePaint;
    private float titleLineHeight;
    private Paint.FontMetrics titleMetrics;

    private String breakAuthorStr;
    private Paint authorPaint;
    private float authorLineHeight;
    private Paint.FontMetrics authorMetrics;
    private String descStr;
    private String titleStr;
    private String authorStr;

    private int descToTitleSpace;

    /**
     * 构造
     *
     * @param context context
     */
    public BookTextInfoView(Context context) {
        this(context, null);
    }

    /**
     * 构造
     *
     * @param context context
     * @param attrs   attrs
     */
    public BookTextInfoView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        descToTitleSpace = ConvertUtils.dp2px(context, 13);

        descLineList = new ArrayList<>();
        descPaint = new Paint();
        descPaint.setAntiAlias(true);
        int descSize = ConvertUtils.sp2px(context, 13);
        descPaint.setTextSize(descSize);
        descMetrics = descPaint.getFontMetrics();
        descLineHeight = descMetrics.bottom - descMetrics.top;
        descPaint.setColor(0x7F000000);

        titlePaint = new Paint();
        titlePaint.setAntiAlias(true);
        int titleSize = ConvertUtils.sp2px(context, 15);
        titlePaint.setTextSize(titleSize);
        titleMetrics = titlePaint.getFontMetrics();
        titleLineHeight = titleMetrics.bottom - titleMetrics.top;
        titlePaint.setColor(0xFFD0021B);

        authorPaint = new Paint();
        authorPaint.setAntiAlias(true);
        int authorSize = ConvertUtils.sp2px(context, 13);
        authorPaint.setTextSize(authorSize);
        authorMetrics = authorPaint.getFontMetrics();
        authorLineHeight = authorMetrics.bottom - authorMetrics.top;
        authorPaint.setColor(0x7F000000);
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        //        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        //        width = widthSize;
        //        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        //        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        //        if (heightMode == MeasureSpec.EXACTLY || heightMode == MeasureSpec.AT_MOST) {
        //            height = heightSize;
        //        } else {
        //            int descLines = Math.max(MIN_DESC_LINE, descLineList.size());
        //            height = getPaddingTop() + getPaddingBottom() + descToTitleSpace + (int) (titleLineHeight + authorLineHeight + descLines * descLineHeight + 0.5f);
        //            if (!isTextBreak) {
        //                breakText();
        //            }
        //        }
        //        setMeasuredDimension(widthSize, height);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (!isTextBreak) {
            breakText();
        }
    }


    /**
     * 设置文本
     *
     * @param title  title
     * @param desc   desc
     * @param author author
     */
    public void setText(String title, String desc, String author) {
        if (TextUtils.equals(titleStr, title) && TextUtils.equals(descStr, desc) && TextUtils.equals(authorStr, author)) {
            return;
        }

        titleStr = title;
        descStr = desc;
        authorStr = author;
        isTextBreak = false;
        postInvalidate();
    }

    private void breakText() {
        descLineList.clear();
        int contentWidth = getMeasuredWidth() - getPaddingLeft() - getPaddingRight();
        if (!TextUtils.isEmpty(descStr)) {
            String str = descStr;
            ArrayList<String> list = new ArrayList<>();
            while (descPaint.measureText(str) > contentWidth || list.size() < MAX_DESC_LINE) {
                int count = descPaint.breakText(str, true, contentWidth, null);
                String subStr = str.substring(0, count);
                list.add(subStr);
                str = str.substring(count);
            }
            list.add(str);

            int size = Math.min(MAX_DESC_LINE, list.size());
            for (int i = 0; i < size; i++) {
                descLineList.add(list.get(i));
            }
        }

        if (!TextUtils.isEmpty(titleStr) && titlePaint.measureText(titleStr) > contentWidth) {
            int count = titlePaint.breakText(titleStr, true, contentWidth, null);
            breakTitleStr = titleStr.substring(0, count);
        } else {
            breakTitleStr = titleStr;
        }

        if (!TextUtils.isEmpty(authorStr) && authorPaint.measureText(authorStr) > contentWidth) {
            int count = authorPaint.breakText(authorStr, true, contentWidth, null);
            breakAuthorStr = authorStr.substring(0, count);
        } else {
            breakAuthorStr = authorStr;
        }

        isTextBreak = true;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        float startY = 0;
        float startX = getPaddingLeft();

        if (!TextUtils.isEmpty(breakTitleStr)) {
            canvas.drawText(breakTitleStr, startX, startY - titleMetrics.top, titlePaint);
        }

        startY = startY + titleLineHeight + descToTitleSpace;

        int length = descLineList.size();
        if (length > 0) {
            for (int i = 0; i < length; i++) {
                canvas.drawText(descLineList.get(i), startX, startY - descMetrics.top, descPaint);
                startY = startY + descLineHeight;
            }
        }

        if (!TextUtils.isEmpty(breakAuthorStr)) {
            canvas.drawText(breakAuthorStr, startX, getMeasuredHeight() - getPaddingBottom() - authorLineHeight - authorMetrics.top, authorPaint);
        }

    }
}
