package com.dzbook.view;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dzbook.lib.utils.CompatUtils;
import com.dzbook.utils.DimensionPixelUtil;
import com.dzbook.utils.TypefaceUtils;
import com.ishugui.R;

/**
 * 点众通用的普通title
 *
 * @author dongdianzhou on 2017/4/5.
 */
public class DianZhongCommonTitle extends LinearLayout {

    /**
     * 右侧显示模式
     */
    public static final int COMMON_RIGHT_SHOW_MODE_NONE = 0;
    /**
     * 右侧显示模式
     */
    public static final int COMMON_RIGHT_SHOW_MODE_TITLE = 1;
    /**
     * 右侧显示模式
     */
    public static final int COMMON_RIGHT_SHOW_MODE_DES = 2;

    // 返回图标
    private TextView mTextViewTitle;
    // 标题
    //    private Paint mLinePaint;
    // 横线
    private ImageView mImageViewBack;

    // 右侧关闭文案
    private ImageView mImageViewRightOper;
    // 右侧图标
    private TextView mTextViewOper;

    private String title;
    private String oper;
    private Drawable rightDrawable;
    private Drawable leftDrawable;
    private boolean isShowRightOper;
    private int commonoperShowMode = COMMON_RIGHT_SHOW_MODE_NONE;
    private int iconSize;
    private int titlePadding;

    /**
     * 需不需要设置背景
     */
    private boolean hasBack;

    /**
     * 构造
     *
     * @param context context
     */
    public DianZhongCommonTitle(Context context) {
        super(context);
        init();
    }

    /**
     * 构造
     *
     * @param context context
     * @param attrs   attrs
     */
    public DianZhongCommonTitle(Context context, AttributeSet attrs) {
        super(context, attrs);
        initAttrs(attrs);
        init();
    }

    private void initAttrs(AttributeSet attrs) {
        TypedArray array = getContext().obtainStyledAttributes(attrs, R.styleable.DianZhongCommonTitle, 0, 0);
        title = array.getString(R.styleable.DianZhongCommonTitle_common_title);
        oper = array.getString(R.styleable.DianZhongCommonTitle_common_oper_title);
        commonoperShowMode = array.getInt(R.styleable.DianZhongCommonTitle_showright, 0);
        rightDrawable = array.getDrawable(R.styleable.DianZhongCommonTitle_common_oper_des);
        leftDrawable = array.getDrawable(R.styleable.DianZhongCommonTitle_common_left_pic);
        isShowRightOper = array.getBoolean(R.styleable.DianZhongCommonTitle_showright, false);
        hasBack = array.getBoolean(R.styleable.DianZhongCommonTitle_hasBack, true);
        array.recycle();
    }

    private void init() {
        this.setOrientation(LinearLayout.HORIZONTAL);
        this.setMinimumHeight(getContext().getResources().getDimensionPixelSize(R.dimen.common_item_height));
        if (hasBack) {
            this.setBackgroundResource(R.color.color_100_f2f2f2);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && getContext() instanceof Activity) {
                Window window = ((Activity) getContext()).getWindow();
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                window.setStatusBarColor(CompatUtils.getColor(getContext(), R.color.color_100_f2f2f2));
            }
        }

        iconSize = DimensionPixelUtil.dip2px(getContext(), 48);
        titlePadding = DimensionPixelUtil.dip2px(getContext(), 16);
        LinearLayout.LayoutParams layoutParams = null;

        // 1、图标
        layoutParams = new LinearLayout.LayoutParams(iconSize, iconSize, 0);
        mImageViewBack = new ImageView(getContext());
        mImageViewBack.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        mImageViewBack.setBackgroundResource(R.drawable.selector_hw_list_item_04_cornor);
        if (leftDrawable != null) {
            mImageViewBack.setImageDrawable(leftDrawable);
        } else {
            Drawable drawable = CompatUtils.getDrawable(getContext(), R.drawable.hw_back);
            mImageViewBack.setImageDrawable(drawable);
        }
        this.addView(mImageViewBack, layoutParams);
        // 2、标题
        layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT, 1);
        mTextViewTitle = new TextView(getContext());
        mTextViewTitle.setTextSize(18);
        mTextViewTitle.setEllipsize(TextUtils.TruncateAt.MIDDLE);
        mTextViewTitle.setPadding(0, 0, 0, 0);
        mTextViewTitle.setGravity(Gravity.CENTER_VERTICAL);
        mTextViewTitle.setSingleLine();
        mTextViewTitle.setText(title);
        mTextViewTitle.setTextColor(CompatUtils.getColor(getContext(), R.color.color_100_1a1a1a));
        TypefaceUtils.setHwChineseMediumFonts(mTextViewTitle);
        //        setTitleBold(true);
        addView(mTextViewTitle, layoutParams);
        // 3、划线
        //        mLinePaint = new Paint(getChildCount());
        //        mLinePaint.setColor(getResources().getColor(R.color.color_100_FFFFFF));
        // 4、右侧文本
        intRightTxt();
        // 5、右侧图标
        intRightImg();
    }

    /**
     * 初始化右侧文本
     */
    private void intRightTxt() {
        if (mTextViewOper != null && (isShowRightOper || commonoperShowMode == COMMON_RIGHT_SHOW_MODE_TITLE)) {
            mTextViewOper.setText(oper);
        } else if (isShowRightOper || commonoperShowMode == COMMON_RIGHT_SHOW_MODE_TITLE) {
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT, 0);
            mTextViewOper = new TextView(getContext());
            mTextViewOper.setTextSize(16);
            mTextViewOper.setEllipsize(TextUtils.TruncateAt.MIDDLE);
            mTextViewOper.setPadding(0, 0, 5, 0);
            mTextViewOper.setGravity(Gravity.LEFT | Gravity.CENTER_VERTICAL);
            mTextViewOper.setBackground(null);
            mTextViewOper.setSingleLine();
            mTextViewOper.setText(oper);
            addView(mTextViewOper, layoutParams);
        }
    }

    /**
     * 初始化右侧默认图
     */
    private void intRightImg() {
        if (mImageViewRightOper != null && (rightDrawable != null || commonoperShowMode == COMMON_RIGHT_SHOW_MODE_DES)) {
            mImageViewRightOper.setImageDrawable(rightDrawable);
        } else if (rightDrawable != null || commonoperShowMode == COMMON_RIGHT_SHOW_MODE_DES) {
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(iconSize, iconSize, 0);
            mImageViewRightOper = new ImageView(getContext());
            mImageViewRightOper.setScaleType(ImageView.ScaleType.CENTER);
            mImageViewRightOper.setImageDrawable(rightDrawable);
            addView(mImageViewRightOper, layoutParams);
        }
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        //        canvas.drawRect(0, getMeasuredHeight() - 2, getMeasuredWidth(), getMeasuredHeight(), mLinePaint);
    }

    private void setCommonOperVisible() {
        switch (commonoperShowMode) {
            case COMMON_RIGHT_SHOW_MODE_DES:
                intRightImg();
                break;
            case COMMON_RIGHT_SHOW_MODE_TITLE:
                intRightTxt();
                break;
            default:
                break;
        }
    }

    /**
     * 左侧点击监听
     *
     * @param onClickListener onClickListener
     */
    public void setLeftClickListener(OnClickListener onClickListener) {
        mImageViewBack.setOnClickListener(onClickListener);
    }

    /**
     * 右侧点击监听
     *
     * @param onClickListener onClickListener
     */
    public void setRightClickListener(OnClickListener onClickListener) {
        if (mTextViewOper != null) {
            mTextViewOper.setOnClickListener(onClickListener);
        }
        if (mImageViewRightOper != null) {
            mImageViewRightOper.setOnClickListener(onClickListener);
        }
    }

    /**
     * 右侧文本点击
     *
     * @param onClickListener onClickListener
     */
    public void setRightTextClickListener(OnClickListener onClickListener) {
        setRightClickListener(onClickListener);
    }

    /**
     * she HI标题
     *
     * @param title title
     */
    public void setTitle(String title) {
        if (mTextViewTitle != null) {
            mTextViewTitle.setText(title);
        }
    }

    /**
     * 获取标题
     *
     * @return title
     */
    public String getTitle() {
        if (mTextViewTitle != null) {
            return mTextViewTitle.getText().toString();
        }
        return "";
    }

    /**
     * 设置右侧title
     *
     * @param aOper oper
     */
    public void setRightOperTitle(String aOper) {
        if (mTextViewOper != null) {
            mTextViewOper.setText(aOper);
        }
    }

    /**
     * 设置右侧图标
     *
     * @param res res
     */
    public void setRightOperDrawable(int res) {
        if (mImageViewRightOper != null) {
            mImageViewRightOper.setImageResource(res);
        }
    }

    /**
     * 右侧显示
     *
     * @param showMode showMode
     */
    public void setRightOperVisible(int showMode) {
        commonoperShowMode = showMode;
        setCommonOperVisible();
    }

    /**
     * setTitleBarGravity
     * @param gravity gravity
     */
    public void setTitleBarGravity(int gravity) {
        if (mTextViewTitle.getGravity() != gravity) {
            mTextViewTitle.setGravity(gravity);
        }
    }

    public TextView getTitleText() {
        return mTextViewTitle;
    }

    /**
     * getmLeftIcon
      * @return ImageView
     */
    public ImageView getmLeftIcon() {
        return mImageViewBack;
    }

    /**
     * setTitleBold
     * @param bold bold
     */
    public void setTitleBold(boolean bold) {
        TextPaint paint = mTextViewTitle.getPaint();
        if (bold) {
            paint.setFakeBoldText(true);
        } else {
            paint.setFakeBoldText(false);
        }
    }

    /**
     * addRightView
     * @param rightView rightView
     */
    public void addRightView(View rightView) {
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT, 0);
        addView(rightView, getChildCount() - 1, layoutParams);
    }

    /**
     * setLiftIconVisibility
     * @param visibility visibility
     */
    public void setLiftIconVisibility(int visibility) {
        if (visibility == View.GONE) {
            mTextViewTitle.setPadding(titlePadding, 0, 0, 0);
            mImageViewBack.setVisibility(visibility);
        } else {
            mTextViewTitle.setPadding(0, 0, 0, 0);
        }
    }

    /**
     * setRightIconVisibility
     * @param visibility visibility
     */
    public void setRightIconVisibility(int visibility) {

        if (mTextViewOper != null) {
            mTextViewOper.setPadding(0, 0, titlePadding, 0);
            mTextViewOper.setVisibility(visibility);
        }
        if (mImageViewRightOper != null) {
            mImageViewRightOper.setPadding(0, 0, titlePadding, 0);
            mImageViewRightOper.setVisibility(visibility);
        }
    }

    public ImageView getImageViewRightOper() {
        return mImageViewRightOper;
    }

    /**
     * setImageViewRightOperAlphe
     * @param alphe  alphe
     * @param clickable clickable
     */
    public void setImageViewRightOperAlphe(float alphe, boolean clickable) {
        if (mImageViewRightOper != null) {
            mImageViewRightOper.setAlpha(alphe);
            mImageViewRightOper.setEnabled(clickable);
        }
    }

}
