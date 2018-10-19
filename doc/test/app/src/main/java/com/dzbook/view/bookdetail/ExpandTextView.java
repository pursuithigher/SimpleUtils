package com.dzbook.view.bookdetail;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;

import com.dzbook.utils.DeviceInfoUtils;
import com.dzbook.utils.DimensionPixelUtil;
import com.ishugui.R;

import java.util.ArrayList;
import java.util.List;

/**
 * 缩放展开的动画简单Textview
 * <p>
 * Created by igeek on 2016/9/1.
 *
 * @author igeek2014@hotmail.com
 */
public class ExpandTextView extends View implements View.OnClickListener {

    /**
     * LineText静态内部类
     */
    private static class LineText {
        private String text;
        private int startIndex;
        private int endIndex;
        private int lineIndex;
        private int topOffset;
        private int bottomOffset;
        private int paddingTop;
        private int paddingBottom;
        private float width;
        private int height;
        private int baseLine;

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }

        public int getStartIndex() {
            return startIndex;
        }

        public void setStartIndex(int startIndex) {
            this.startIndex = startIndex;
        }

        public int getEndIndex() {
            return endIndex;
        }

        public void setEndIndex(int endIndex) {
            this.endIndex = endIndex;
        }

        public int getLineIndex() {
            return lineIndex;
        }

        public void setLineIndex(int lineIndex) {
            this.lineIndex = lineIndex;
        }

        public int getTopOffset() {
            return topOffset;
        }

        public void setTopOffset(int topOffset) {
            this.topOffset = topOffset;
        }

        public int getPaddingTop() {
            return paddingTop;
        }

        public void setPaddingTop(int paddingTop) {
            this.paddingTop = paddingTop;
        }

        public int getPaddingBottom() {
            return paddingBottom;
        }

        public void setPaddingBottom(int paddingBottom) {
            this.paddingBottom = paddingBottom;
        }

        public float getWidth() {
            return width;
        }

        public void setWidth(float width) {
            this.width = width;
        }

        public int getHeight() {
            return height;
        }

        public void setHeight(int height) {
            this.height = height;
        }

        public int getBaseLine() {
            return baseLine;
        }

        public void setBaseLine(int baseLine) {
            this.baseLine = baseLine;
        }

        public int getBottomOffset() {
            return bottomOffset;
        }

        public void setBottomOffset(int bottomOffset) {
            this.bottomOffset = bottomOffset;
        }
    }


    //行文本记录集
    private List<LineText> lineTexts = new ArrayList<LineText>();
    //最大显示文本行数
    private int maxLines;
    //目标文本行
    private int targetLine;
    //收缩收起时候的提示图标
    private Drawable expandDrawable;
    //展开时候的提示图标
    private Drawable shrinkDrawable;
    //提示图标的宽度
    private int drawableWidth;
    //提示图标的高度
    private int drawableHeight;
    //最大显示文本行对应的本视图高度
    private int maxLinesHeight;
    //展开时候的视图高度
    private int expandHeight;
    //当前视图的高度
    private int viewHeight;
    //收缩行结尾提示语文本宽度
    private float ellipsizWidth;
    //收缩行结尾提示语文本绘制水平起点
    private float ellipsizStartX;

    //文本字体大小
    private int textSize;
    //文本颜色
    private int textColor;
    //当前文本
    private String text;
    private String ellipsizText = "...";
    //收缩行文本
    private String shrinkLineText;
    //动画显示时间
    private int animDuration;
    //是否能够显示 ellipsizText 【需要收缩行当前文本的宽度】
    private boolean showEllipsizText = false;
    private boolean showTipDrawalbe = false;
    private boolean needMeasure = true;

    private StaticLayout layout;
    private TextPaint textPaint;
    private int drawableDisplace;
    private int defaultSpace = 0;


    /**
     * 构造
     *
     * @param context context
     */
    public ExpandTextView(Context context) {
        this(context, null);
    }

    /**
     * 构造
     *
     * @param context context
     * @param attrs   attrs
     */
    public ExpandTextView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    /**
     * 构造
     *
     * @param context      context
     * @param attrs        attrs
     * @param defStyleAttr defStyleAttr
     */
    public ExpandTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.dzExpandTextView);

        maxLines = ta.getInt(R.styleable.dzExpandTextView_maxLines, -1);
        animDuration = ta.getInt(R.styleable.dzExpandTextView_animDuration, 300);
        textSize = (int) ta.getDimension(R.styleable.dzExpandTextView_dzTextSize, 14);
        textColor = ta.getColor(R.styleable.dzExpandTextView_dzTextColor, 14);
        drawableWidth = ta.getDimensionPixelSize(R.styleable.dzExpandTextView_drawableWidth, 14);
        drawableHeight = ta.getDimensionPixelSize(R.styleable.dzExpandTextView_drawableHeight, 14);
        drawableDisplace = ta.getDimensionPixelSize(R.styleable.dzExpandTextView_drawableDisplace, 0);
        expandDrawable = ta.getDrawable(R.styleable.dzExpandTextView_expandDrawable);
        shrinkDrawable = ta.getDrawable(R.styleable.dzExpandTextView_shrinkDrawable);

        ta.recycle();

        textPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        textPaint.density = context.getResources().getDisplayMetrics().density;
        textPaint.setColor(textColor);
        textPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        textPaint.setTextSize(textSize);
        defaultSpace = DimensionPixelUtil.dip2px(getContext(), 2);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int width = MeasureSpec.getSize(widthMeasureSpec);

        if (needMeasure && (!TextUtils.isEmpty(text))) {
            needMeasure = false;
            measureHeightState(text, width);
        } else {
            heightMeasureSpec = MeasureSpec.makeMeasureSpec(viewHeight, MeasureSpec.EXACTLY);
            setMeasuredDimension(widthMeasureSpec, heightMeasureSpec);
        }

    }

    private void measureHeightState(String text1, int width) {
        final float num = 1.35f;
        width = width - getPaddingLeft() - getPaddingRight();
        if (width < 0) {
            //解决monkey 诡异bug 量宽度 量出个0来。。。。
            width = DeviceInfoUtils.getInstanse().getWidthReturnInt() - DimensionPixelUtil.dip2px(getContext(), 32);
        }
        layout = new StaticLayout(text1, textPaint, width, Layout.Alignment.ALIGN_NORMAL, num, 0f, true);
        final int lineCount = layout.getLineCount();
        maxLines = (maxLines == -1 || maxLines > lineCount) ? lineCount : maxLines;

        int textHeight = 0;

        List<LineText> tempLines = new ArrayList<LineText>();

        for (int index = 0; index < lineCount; index++) {
            int start = layout.getLineStart(index);
            int end = layout.getLineEnd(index);
            LineText lineText = new LineText();
            lineText.setStartIndex(start);
            lineText.setEndIndex(end - 1);
            lineText.setText(text1.substring(start, end));
            lineText.setTopOffset(layout.getLineTop(index));
            lineText.setBottomOffset(layout.getLineBottom(index));
            lineText.setBaseLine(layout.getLineBaseline(index) + getPaddingTop());
            lineText.setWidth(layout.getLineWidth(index));
            lineText.setHeight(lineText.getBottomOffset() - lineText.getTopOffset());
            tempLines.add(lineText);

            if (index < maxLines) {
                maxLinesHeight += lineText.getHeight();
            }

            textHeight += lineText.getHeight();
        }

        maxLinesHeight += getPaddingTop() + getPaddingBottom();
        expandHeight += getPaddingTop() + getPaddingBottom();

        ellipsizWidth = textPaint.measureText(ellipsizText);

        showMoreLines(width, lineCount, tempLines);

        expandHeight += textHeight + ((expandDrawable != null && showTipDrawalbe) ? drawableHeight : 0);

        viewHeight = maxLines == lineCount ? expandHeight : maxLinesHeight;

        targetLine = maxLines;

        lineTexts = tempLines;


        if (viewHeight < expandHeight) {
            setClickable(true);
            setOnClickListener(this);
        } else {
            setClickable(false);
        }

    }

    private void showMoreLines(int width, int lineCount, List<LineText> tempLines) {
        if (maxLines < lineCount) {

            showTipDrawalbe = expandDrawable != null && shrinkDrawable != null;

            float textWidth = tempLines.get(maxLines - 1).getWidth();
            float contentWidth = width - getPaddingLeft() - getPaddingRight();
            float toMarginRight = ellipsizWidth + (showTipDrawalbe ? drawableWidth : 0);

            String ellipsizLineText = tempLines.get(maxLines - 1).getText();

            if (contentWidth - textWidth < toMarginRight) {
                showEllipsizText = true;
                String subString;
                for (int index = ellipsizLineText.length() - 1; index > 0; index--) {
                    subString = ellipsizLineText.substring(0, index);
                    float subStrWidth = textPaint.measureText(subString);
                    if (contentWidth - subStrWidth >= toMarginRight) {
                        ellipsizStartX = subStrWidth + getPaddingLeft();
                        shrinkLineText = subString;
                        break;
                    }
                }
            } else {
                showEllipsizText = false;
                shrinkLineText = ellipsizLineText;
            }
        } else {
            showTipDrawalbe = false;
            showEllipsizText = false;
        }
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (lineTexts.size() == 0) {
            return;
        }

        for (int index = 0; index < targetLine; index++) {

            LineText lineText = lineTexts.get(index);

            if (index < targetLine - 1) {
                canvas.drawText(lineText.getText(), getPaddingLeft(), lineText.getBaseLine(), textPaint);
            } else {
                if (targetLine == maxLines && maxLines < lineTexts.size()) {
                    //收缩转态
                    if (showEllipsizText) {
                        canvas.drawText(ellipsizText, ellipsizStartX, lineText.getBaseLine(), textPaint);
                    }
                    canvas.drawText(shrinkLineText, getPaddingLeft(), lineText.getBaseLine(), textPaint);
                    if (showTipDrawalbe) {
                        int left = getWidth() - drawableWidth - getPaddingRight();
                        int top = getHeight() - drawableHeight - getPaddingBottom();
                        canvas.drawBitmap(drawabletoZoomBitmap(shrinkDrawable, drawableWidth, drawableHeight), left, top + drawableDisplace - defaultSpace, null);
                    }
                } else if (targetLine == lineTexts.size()) {
                    //展开状态
                    float willDrawTxtWidth = 0;
                    String willDrawText = lineText.getText();
                    if (!TextUtils.isEmpty(willDrawText)) {
                        willDrawTxtWidth = textPaint.measureText(willDrawText);
                        canvas.drawText(willDrawText, getPaddingLeft(), lineText.getBaseLine(), textPaint);
                    }
                    if (showTipDrawalbe) {
                        int left = getWidth() - drawableWidth - getPaddingRight();
                        int top = getHeight() - drawableHeight - getPaddingBottom();
                        if (willDrawTxtWidth + getPaddingRight() > left) {
                            top += drawableHeight;
                        }
                        canvas.drawBitmap(drawabletoZoomBitmap(expandDrawable, drawableWidth, drawableHeight), left, top + drawableDisplace - drawableHeight + defaultSpace, null);
                    }
                }
            }
        }

    }

    @Override
    public void onClick(View view) {
        if (maxLines == lineTexts.size()) {
            return;
        }

        if (targetLine == maxLines) {
            targetLine = lineTexts.size();
            startDrawAnim(maxLinesHeight, expandHeight);
        } else if (targetLine == lineTexts.size()) {
            targetLine = maxLines;
            startDrawAnim(expandHeight, maxLinesHeight);
        }
    }

    private void startDrawAnim(int startHeight, int endHeight) {
        ObjectAnimator animator = ObjectAnimator.ofInt(this, "viewHeight", startHeight, endHeight);
        animator.setDuration(animDuration);
        //        animator.setInterpolator(new AccelerateDecelerateInterpolator());
        animator.start();
    }


    public int getViewHeight() {
        return viewHeight;
    }

    /**
     * 设置高度
     *
     * @param viewHeight viewHeight
     */
    public void setViewHeight(int viewHeight) {
        this.viewHeight = viewHeight;
        requestLayout();
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    /**
     * drawlable 缩放
     *
     * @param drawable drawable
     * @param h        h
     * @param w        w
     * @return Bitmap
     */
    public static Bitmap drawabletoZoomBitmap(Drawable drawable, int w, int h) {
        // 取 drawable 的长宽
        int width = drawable.getIntrinsicWidth();
        int height = drawable.getIntrinsicHeight();
        // drawable转换成bitmap
        Bitmap oldbmp = drawabletoBitmap(drawable);
        // 创建操作图片用的Matrix对象
        Matrix matrix = new Matrix();
        // 计算缩放比例
        float sx = (float) w / width;
        float sy = (float) h / height;
        // 设置缩放比例
        matrix.postScale(sx, sy);
        // 建立新的bitmap，其内容是对原bitmap的缩放后的图
        Bitmap newbmp = Bitmap.createBitmap(oldbmp, 0, 0, width, height, matrix, true);
        return newbmp;
    }

    /**
     * Drawable转换成Bitmap
     *
     * @param drawable drawable
     * @return drawable
     */
    public static Bitmap drawabletoBitmap(Drawable drawable) {
        // 取 drawable 的长宽
        int width = drawable.getIntrinsicWidth();
        int height = drawable.getIntrinsicHeight();
        // 取 drawable 的颜色格式
        Bitmap.Config config = drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888 : Bitmap.Config.RGB_565;
        Bitmap bitmap = Bitmap.createBitmap(width, height, config);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        drawable.draw(canvas);
        return bitmap;
    }


}
