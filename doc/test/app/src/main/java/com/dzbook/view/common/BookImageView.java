package com.dzbook.view.common;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.support.annotation.Nullable;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import com.dzbook.lib.utils.CompatUtils;
import com.dzbook.utils.DimensionPixelUtil;
import com.dzbook.view.RoundRectImageView;
import com.ishugui.R;

import java.util.HashMap;
import java.util.Map;

/***
 * 书籍封面 ImageView
 * @author wangjc
 */
public class BookImageView extends RoundRectImageView {
    /**
     * 书架的遮罩图片此处缓存在列表中没有必要每次都去创建
     */
    private static Map<String, Bitmap> sBitmapShelfbookZz = new HashMap<>();

    protected float nameScaleSize;
    protected float lineScaleSize;
    protected float formScaleSize;
    protected float scaleSize;
    protected int bookWidth;
    protected int bookHeight;
    protected int clickWidth;
    protected int clickHeight;
    protected int clickColor = Color.TRANSPARENT;
    protected int desViewZz = R.drawable.ic_main_shelf_bookitem_bookbg;
    protected int checkBoxPadding;
    protected int bookStatusPadding;
    protected int bookRadius;

    protected int linePadding;
    protected int bookLineHeight;
    protected int namePaddingLeft;
    protected int formatTop;
    protected int nameLineTop;
    protected int nameLineStopX;
    protected int lineLeft;
    protected int lineRight;
    protected int lineBottom;
    protected int bookNamePadding;

    private int borderWidth;
    private int maxLines;
    private int finalLeft;
    private int finalTopOne;
    private int finalTopTwo;
    private int maxLinesOne = 0;
    private int maxLinesTwo = 0;
    private int bookStatusHeight;
    private int bookStatusIconPadding;
    private String moreBookName = "";

    private boolean havClick2;
    private boolean havClick;
    private boolean havPress;
    private boolean hadBookStatus;
    private boolean havCheckBox;
    private boolean hadShowCheckBox;
    private boolean isBookAdd;

    private CheckBox checkBoxView;
    private TextView textviewBookstatus;
    private CheckBoxClickListener checkBoxClickListener;

    private volatile Paint clickPaint;
    private RectF clickRect;
    private Paint borderPaint;
    private RectF borderRect;
    private Paint backgroundPaint;
    private RectF backgroundRect;
    private Paint linePaint;
    private Paint bookLinePaint;
    private TextPaint formatPaint;
    private TextPaint bookNamePaint;

    private String bookStatus;
    private String bookName;
    private String formatStr;

    private OnClickListener clickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            if (checkBoxClickListener != null) {
                checkBoxClickListener.onClick(v);
            }
        }
    };

    /**
     * 构造
     *
     * @param context context
     */
    public BookImageView(Context context) {
        this(context, null);
    }

    /**
     * 构造
     *
     * @param context context
     * @param attrs   attrs
     */
    public BookImageView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initAttrs(attrs);
        init();
        initData();
    }

    private void initAttrs(AttributeSet attrs) {
        bookRadius = getResources().getDimensionPixelOffset(R.dimen.hw_dp_4);
        checkBoxPadding = getResources().getDimensionPixelOffset(R.dimen.hw_dp_4);
        bookStatusPadding = getResources().getDimensionPixelOffset(R.dimen.hw_dp_4);
        bookWidth = getResources().getDimensionPixelOffset(R.dimen.hw_dp_90);
        bookHeight = getResources().getDimensionPixelOffset(R.dimen.hw_dp_120);

        TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.BookImageView, 0, 0);
        if (a != null) {
            synchronized (this) {
                clickColor = a.getColor(R.styleable.BookImageView_book_click_color, Color.TRANSPARENT);
            }
            havClick = a.getBoolean(R.styleable.BookImageView_book_havclick, false);
            havPress = a.getBoolean(R.styleable.BookImageView_book_havPress, false);
            desViewZz = a.getResourceId(R.styleable.BookImageView_book_des_view_zz, R.drawable.ic_main_shelf_bookitem_bookbg);
            bookStatusPadding = a.getDimensionPixelSize(R.styleable.BookImageView_book_bookstatus_padding, bookStatusPadding);
            checkBoxPadding = a.getDimensionPixelSize(R.styleable.BookImageView_book_checkbox_padding, checkBoxPadding);
            bookRadius = a.getDimensionPixelSize(R.styleable.BookImageView_book_radius_size, bookRadius);
            a.recycle();
        }
    }

    private void init() {
        // 约定2像素
        borderWidth = 1;
        this.setPadding(0, 0, 0, 0);
        this.setWillNotDraw(false);
        this.setDrawingCacheEnabled(true);
        this.setClickable(true);
        this.setDrawableRadius(bookRadius);
        if (!sBitmapShelfbookZz.containsKey(desViewZz + "")) {
            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), desViewZz);
            sBitmapShelfbookZz.put(desViewZz + "", bitmap);
        }
    }

    private void initData() {
        // 背景颜色
        backgroundPaint = new Paint();
        backgroundPaint.setStyle(Paint.Style.FILL);
        backgroundPaint.setColor(Color.parseColor("#d8d8d8"));
        backgroundPaint.setAntiAlias(true);
        backgroundRect = new RectF();
        // 竖直方向的横线
        linePaint = new Paint();
        linePaint.setColor(Color.parseColor("#14000000"));
        linePaint.setAntiAlias(true);
        linePaint.setStrokeWidth(borderWidth);
        // 书籍边框
        borderPaint = new Paint();
        borderPaint.setStyle(Paint.Style.STROKE);
        borderPaint.setColor(Color.parseColor("#14000000"));
        borderPaint.setAntiAlias(true);
        borderPaint.setStrokeWidth(borderWidth);
        borderRect = new RectF();
        // 基本变量（仅供内部使用，进制外部篡改）
        scaleSize = bookWidth * 1.0f / bookHeight;
        linePadding = DimensionPixelUtil.dip2px(getContext(), 6);
        bookLineHeight = DimensionPixelUtil.dip2px(getContext(), 3);
        namePaddingLeft = DimensionPixelUtil.dip2px(getContext(), 12);
        bookNamePadding = DimensionPixelUtil.dip2px(getContext(), 17);
        bookStatusHeight = DimensionPixelUtil.dip2px(getContext(), 16);
        bookStatusIconPadding = DimensionPixelUtil.dip2px(getContext(), 4);
        nameScaleSize = 78.0f / 120;
        lineScaleSize = 88.0f / 120;
        formScaleSize = 104.0f / 120;
    }

    private void initClickPaint() {
        if (clickPaint == null) {
            synchronized (this) {
                if (clickPaint == null) {
                    Paint paint = new Paint();
                    paint.setStyle(Paint.Style.FILL);
                    paint.setColor(clickColor);
                    paint.setAlpha(0);
                    paint.setAntiAlias(true);
                    clickPaint = paint;
                }
            }
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldW, int oldH) {
        super.onSizeChanged(w, h, oldW, oldH);
        clickWidth = w;
        clickHeight = h;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        // 父容器存在RelativeLayout 的情况，会进行多次测量
        if (MeasureSpec.getMode(widthMeasureSpec) != MeasureSpec.EXACTLY
                || MeasureSpec.getSize(widthMeasureSpec) == bookWidth) {
            setMeasuredDimension(bookWidth, bookHeight);
        } else {
            int measureWidth = MeasureSpec.getSize(widthMeasureSpec);
            if (measureWidth <= 0) {
                super.onMeasure(widthMeasureSpec, heightMeasureSpec);
            } else {
                setMeasuredDimension(measureWidth, (int) (measureWidth / scaleSize));
            }
        }
        if (maxLines <= 0) {
            initBookDefaultParam();
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        // 0、如果是bookAdd这种样式，只需要绘制背景颜色和边框即可
        if (isBookAdd) {
            drawBookBackgroundColor(canvas);
            drawBorder(canvas);
            return;
        }
        // 1、根据drawable判定是否加载：背景颜色、竖直方向横线、书名、书名横幅、格式 （第一层）
        if (getDrawable() == null) {
            drawBookBackgroundColor(canvas);
            drawLineY(canvas);
            drawBookName(canvas);
            drawBookNameLine(canvas);
            drawFormat(canvas);
        }

        // 2、硬性绘制： super绘制drawable封面、边框 (第三层)
        super.onDraw(canvas);

        // 3、根据外部数据判断：绘制单选框、status状态、点击效果 (第四层)
        drawBookStatus(canvas);
        drawCheckBox(canvas);
        drawClickBack(canvas);
        drawBorder(canvas);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (havClick) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    initClickPaint();
                    clickPaint.setAlpha(48);
                    invalidate();
                    break;
                case MotionEvent.ACTION_CANCEL:
                case MotionEvent.ACTION_UP:
                    initClickPaint();
                    if (!havClick2) {
                        clickPaint.setAlpha(0);
                    }
                    invalidate();
                    break;
                default:
                    break;
            }
            return super.dispatchTouchEvent(event);
        } else if (havPress) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    initClickPaint();
                    clickPaint.setColor(CompatUtils.getColor(getContext(), R.color.color_05_black));
                    invalidate();
                    break;
                case MotionEvent.ACTION_CANCEL:
                case MotionEvent.ACTION_UP:
                    initClickPaint();
                    clickPaint.setColor(Color.TRANSPARENT);
                    invalidate();
                    break;
                default:
                    break;
            }
            return super.dispatchTouchEvent(event);
        } else {
            return false;
        }
    }

    private void drawBorder(Canvas canvas) {
        borderRect.left = borderWidth / 2f;
        borderRect.top = borderWidth / 2f;
        borderRect.right = getMeasuredWidth() - borderWidth / 2;
        borderRect.bottom = getMeasuredHeight() - borderWidth / 2;
        canvas.drawRoundRect(borderRect, bookRadius, bookRadius, borderPaint);
    }

    private void drawLineY(Canvas canvas) {
        if (lineLeft == 0) {
            lineLeft = borderWidth + linePadding;
        }
        if (lineRight == 0) {
            lineRight = borderWidth + linePadding;
        }
        if (lineBottom == 0) {
            lineBottom = getMeasuredHeight() - borderWidth;
        }
        canvas.drawLine(lineLeft, borderWidth, lineRight, lineBottom, linePaint);
    }

    private void drawBookBackgroundColor(Canvas canvas) {
        backgroundRect.left = getPaddingLeft();
        backgroundRect.top = getPaddingTop();
        backgroundRect.right = getMeasuredWidth() - getPaddingRight();
        backgroundRect.bottom = getMeasuredHeight() - getPaddingRight();
        backgroundPaint.setColor(0xFFF8F8F8);
        canvas.drawRoundRect(backgroundRect, bookRadius, bookRadius, backgroundPaint);
    }

    private void drawBookStatus(Canvas canvas) {
        if (hadBookStatus) {
            initBookStatus();
            canvas.save();
            canvas.translate(
                    getPaddingLeft(),
                    bookStatusPadding);
            textviewBookstatus.draw(canvas);
            canvas.restore();
        }
    }

    private void drawFormat(Canvas canvas) {
        if (TextUtils.isEmpty(formatStr)) {
            return;
        }
        if (formatPaint == null) {
            formatPaint = new TextPaint();
            formatPaint.setAntiAlias(true);
            formatPaint.setColor(Color.parseColor("#ED9B99"));
            formatPaint.setTextSize(DimensionPixelUtil.dip2px(getContext(), 9));
            formatPaint.setTypeface(Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD));
            formatPaint.setTextAlign(Paint.Align.LEFT);
        }
        // 以底部为基准进行计算
        if (formatTop == 0) {
            formatTop = (int) (getMeasuredHeight() * formScaleSize);
        }
        canvas.drawText(formatStr, 0, formatStr.length(), namePaddingLeft, formatTop, formatPaint);
    }

    private void drawBookNameLine(Canvas canvas) {
        if (TextUtils.isEmpty(bookName)) {
            return;
        }
        if (bookLinePaint == null) {
            bookLinePaint = new Paint();
            bookLinePaint.setColor(Color.parseColor("#ED9B99"));
            bookLinePaint.setAntiAlias(true);
            bookLinePaint.setStrokeWidth(bookLineHeight);
        }
        if (nameLineTop == 0) {
            nameLineTop = (int) (getMeasuredHeight() * lineScaleSize);
        }
        if (nameLineStopX == 0) {
            nameLineStopX = getMeasuredWidth() - getPaddingRight() - borderWidth / 2;
        }
        canvas.drawLine(getPaddingLeft() + borderWidth / 2, nameLineTop, nameLineStopX, nameLineTop, bookLinePaint);
    }

    private void drawCheckBox(Canvas canvas) {
        if (hadShowCheckBox) {
            if (checkBoxView == null) {
                initCheckBox();
            }
            checkBoxView.setChecked(havCheckBox);
            canvas.save();
            canvas.translate(
                    getMeasuredWidth() - checkBoxView.getMeasuredWidth() - checkBoxPadding,
                    getMeasuredHeight() - checkBoxView.getMeasuredHeight() - checkBoxPadding);
            checkBoxView.draw(canvas);
            canvas.restore();
        }
    }

    private void drawClickBack(Canvas canvas) {
        if (havClick || havClick2 || havPress) {
            if (clickRect == null) {
                clickRect = new RectF();
            }
            initClickPaint();
            int paddingBottom = getPaddingBottom();
            int paddingRight = getPaddingRight();
            clickRect.set(0, getPaddingTop(), clickWidth - paddingRight, clickHeight - paddingBottom);
            canvas.drawRoundRect(clickRect, bookRadius, bookRadius, clickPaint);
        }
    }

    /**
     * setChecked
     * @param isCheck isCheck
     */
    public void setChecked(boolean isCheck) {
        hadShowCheckBox = true;
        havCheckBox = isCheck;
        invalidate();
    }

    /**
     * hideCheck
     */
    public void hideCheck() {
        hadShowCheckBox = false;
        havCheckBox = false;
    }

    /**
     * setBookStatus
     * @param bookStatus bookStatus
     */
    public void setBookStatus(String bookStatus) {
        hadBookStatus = !TextUtils.isEmpty(bookStatus);
        if (textviewBookstatus != null) {
            textviewBookstatus.setText(bookStatus);
        } else {
            this.bookStatus = bookStatus;
        }
    }

    public void setCheckBoxClickListener(CheckBoxClickListener checkBoxClickListener) {
        this.checkBoxClickListener = checkBoxClickListener;
    }

    /**
     * CheckBoxClickListener
     */
    public interface CheckBoxClickListener {
        /**
         *  onClick
         * @param v v
         */
        void onClick(View v);
    }

    @SuppressLint("InflateParams")
    private void initBookStatus() {
        if (textviewBookstatus == null && hadBookStatus) {
            LayoutInflater aInflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            if (aInflater != null) {
                textviewBookstatus = (TextView) aInflater.inflate(R.layout.item_book_status, null);
                textviewBookstatus.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, bookStatusHeight));
                if (!TextUtils.isEmpty(bookStatus)) {
                    textviewBookstatus.setText(bookStatus);
                }
                textviewBookstatus.setMaxWidth(getMeasuredWidth());
                textviewBookstatus.setPadding(bookStatusIconPadding, 0, 2 * bookStatusIconPadding, 0);
                textviewBookstatus.setIncludeFontPadding(false);
                textviewBookstatus.setGravity(Gravity.CENTER | Gravity.LEFT);
                textviewBookstatus.setTextColor(Color.WHITE);
                textviewBookstatus.setTextSize(10);
                textviewBookstatus.setMaxLines(1);
                textviewBookstatus.setEllipsize(TextUtils.TruncateAt.END);
                textviewBookstatus.setBackgroundResource(R.drawable.ic_hw_rectangle);
            }
        }
        if (hadBookStatus) {
            textviewBookstatus.measure(-1, -1);
            int width = textviewBookstatus.getMeasuredWidth();
            if (width > getMeasuredWidth()) {
                width = getMeasuredWidth();
            }
            textviewBookstatus.layout(
                    0,
                    0,
                    width,
                    bookStatusHeight);
        }
    }

    @SuppressLint("InflateParams")
    private void initCheckBox() {
        if (checkBoxView == null && hadShowCheckBox) {
            LayoutInflater aInflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            if (aInflater != null) {
                checkBoxView = (CheckBox) aInflater.inflate(R.layout.item_check_box, null);
                checkBoxView.measure(-1, -1);
                checkBoxView.layout(
                        0,
                        0,
                        checkBoxView.getMeasuredWidth(),
                        checkBoxView.getMeasuredHeight());
                checkBoxView.setOnClickListener(clickListener);
            }
        }
    }

    public void setCheckBoxPadding(int checkBoxPadding) {
        this.checkBoxPadding = checkBoxPadding;
    }

    public void setBookStatusPadding(int bookStatusPadding) {
        this.bookStatusPadding = bookStatusPadding;
    }

    public void setHavClick(boolean havClick) {
        this.havClick = havClick;
    }

    /**
     *  setHavClick2
     * @param aHavClick havClick
     */
    public void setHavClick2(boolean aHavClick) {
        this.havClick2 = aHavClick;
        initClickPaint();
        if (havClick2) {
            clickPaint.setAlpha(48);
        } else {
            clickPaint.setAlpha(0);
        }
    }

    /**
     * setStyleIsBookAdd
     * @param aIsBookAdd isBookAdd
     */
    public void setStyleIsBookAdd(boolean aIsBookAdd) {
        if (this.isBookAdd != aIsBookAdd) {
            this.isBookAdd = aIsBookAdd;
            invalidate();
        }
    }

    public void setForm(String form) {
        formatStr = form;
    }

    private void drawBookName(Canvas canvas) {
        if (TextUtils.isEmpty(bookName)) {
            return;
        }
        if (maxLines == 1) {
            canvas.drawText(bookName, 0, bookName.length(), finalLeft, finalTopOne, bookNamePaint);
        } else {
            if (maxLinesTwo == 0) {
                canvas.drawText(bookName, 0, maxLinesOne, finalLeft, finalTopOne, bookNamePaint);
                canvas.drawText(bookName, maxLinesOne, bookName.length(), finalLeft, finalTopTwo, bookNamePaint);
            } else {
                canvas.drawText(bookName, 0, maxLinesOne, finalLeft, finalTopOne, bookNamePaint);
                canvas.drawText(moreBookName, 0, moreBookName.length(), finalLeft, finalTopTwo, bookNamePaint);
            }
        }
    }

    private void initBookDefaultParam() {
        if (getMeasuredWidth() <= 0 || getMeasuredHeight() <= 0) {
            return;
        }
        if (!TextUtils.isEmpty(bookName)) {
            if (bookNamePaint == null) {
                bookNamePaint = new TextPaint();
                bookNamePaint.setAntiAlias(true);
                bookNamePaint.setColor(Color.parseColor("#1A1A1A"));
                bookNamePaint.setTextSize(DimensionPixelUtil.dip2px(getContext(), 11));
            }

            String moreStr = "...";
            int maxWidth = getMeasuredWidth() - bookNamePadding;
            maxLines = 1;
            finalTopOne = (int) (getMeasuredHeight() * nameScaleSize);
            finalLeft = namePaddingLeft;
            StringBuilder name = new StringBuilder(bookName);
            final int length = name.length();
            float[] widths = new float[length];
            bookNamePaint.getTextWidths(bookName, widths);
            float measureTxtWidth = bookNamePaint.measureText(bookName);
            if (measureTxtWidth > maxWidth) {
                maxLines = 2;
            }
            maxLinesOne = 0;
            maxLinesTwo = 0;
            if (maxLines != 1) {
                int width = 0;
                for (int i = 0; i < widths.length; i++) {
                    width += widths[i];
                    if (width > maxWidth && maxLinesOne == 0) {
                        maxLinesOne = i;
                        width = (int) widths[i];
                    } else if (width > maxWidth) {
                        maxLinesTwo = i;
                        break;
                    }
                }
                if (maxLinesTwo == 0) {
                    finalTopOne = (int) (getMeasuredHeight() * nameScaleSize) - DimensionPixelUtil.dip2px(getContext(), 13);
                    finalTopTwo = (int) (getMeasuredHeight() * nameScaleSize);
                } else {
                    moreBookName = bookName.substring(maxLinesOne, maxLinesTwo) + moreStr;
                    for (int i = 0; i < 3; i++) {
                        if (bookNamePaint.measureText(moreBookName) > maxWidth) {
                            moreBookName = moreBookName.substring(0, moreBookName.length() - 4);
                            moreBookName = moreBookName + moreStr;
                        }
                    }
                    finalTopOne = (int) (getMeasuredHeight() * nameScaleSize) - DimensionPixelUtil.dip2px(getContext(), 13);
                    finalTopTwo = (int) (getMeasuredHeight() * nameScaleSize);
                }
            }
        }
    }

    /**
     * setBookName
     * @param bookName bookName
     */
    public void setBookName(String bookName) {
        if (!TextUtils.isEmpty(bookName)) {
            this.bookName = bookName.trim();
        } else {
            this.bookName = "";
        }
        initBookDefaultParam();

    }

    public String getBookName() {
        return this.bookName;
    }

    public String getBookForm() {
        return this.formatStr;
    }

    @Override
    public void requestLayout() {
        // layout控制
        if (getMeasuredWidth() <= 0 && getMeasuredHeight() <= 0) {
            super.requestLayout();
        }
    }
}
