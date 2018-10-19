package com.dzbook.view.search;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dzbook.lib.utils.ALog;
import com.dzbook.utils.DeviceInfoUtils;
import com.dzbook.utils.DimensionPixelUtil;
import com.ishugui.R;

import java.util.ArrayList;

/**
 * 带弹出动画的流式布局 伸缩动画
 *
 * @author winzows
 */
public class ExpandFlowLayout extends ViewGroup {
    private static final String TAG = "ExpandFlowLayout";
    private static final int MAX_LINE = 3;
    private boolean isExpand = false;
    private ImageView mImageView;
    private int threeLineChildViewId;
    private int minEllipsisSize;
    private int imageSize;
    private int widthMeasureSpec, heightMeasureSpec;

    /**
     * 构造
     *
     * @param context  context
     * @param attrs    attrs
     * @param defStyle defStyle
     */
    public ExpandFlowLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        //获取屏幕宽高、设备密度
        mImageView = new ImageView(getContext());
        mImageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        minEllipsisSize = DimensionPixelUtil.dip2px(getContext(), 42);
        imageSize = DimensionPixelUtil.dip2px(getContext(), 42);
        mImageView.setImageResource(R.drawable.hw_type_right_expand_show);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(imageSize, imageSize);
        mImageView.setLayoutParams(params);

        mImageView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleExpand();
            }
        });
    }

    /**
     * 弹开
     */
    public void toggleExpand() {
        if(!isExpand) {
            isExpand = !isExpand;
            requestLayout();
            mImageView.setVisibility(GONE);
        }
    }

    /**
     * 构造
     *
     * @param context context
     * @param attrs   attrs
     */
    public ExpandFlowLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    /**
     * 构造
     *
     * @param context context
     */
    public ExpandFlowLayout(Context context) {
        this(context, null);
    }


    /**
     * 决定内部子view的宽和高
     * 决定自身宽和高
     */
    @Override
    protected void onMeasure(int widthMeasureSpec1, int heightMeasureSpec1) {
        //确定此容器的宽高
        int widthMode = MeasureSpec.getMode(widthMeasureSpec1);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec1);

        int heightMode = MeasureSpec.getMode(heightMeasureSpec1);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec1);

        this.heightMeasureSpec = heightMeasureSpec1;
        this.widthMeasureSpec = widthMeasureSpec1;

        int lineNum = 1;
        measureChild(mImageView, widthMeasureSpec1, heightMeasureSpec1);

        //测量子View的宽高
        int childCount = getChildCount();
        View child;
        //子view摆放的起始位置

        //一行view中将最大的高度存于此变量，用于子view进行换行时高度的计算
        int maxHeightInLine = 0;
        //存储所有行的高度相加，用于确定此容器的高度
        int allHeight = 0;
        int parentWidth = widthSize - getPaddingRight() - getPaddingLeft();

        int useWidth = 0;
        for (int i = 0, childIndexInLine = 0; i < childCount - 1; i++, childIndexInLine++) {
            child = getChildAt(i);
            if (isExpand && i == threeLineChildViewId) {
                @SuppressLint("DrawAllocation")
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
                child.setLayoutParams(params);
            }

            //测量子View宽高
            measureChild(child, widthMeasureSpec1, heightMeasureSpec1);
            //两两对比，取得一行中最大的高度
            int childHeight = child.getMeasuredHeight();
            int childWidth = child.getMeasuredWidth();

            if (childHeight > maxHeightInLine) {
                maxHeightInLine = childHeight;
            }

            useWidth += childWidth;
            //换行
            if (useWidth > parentWidth) {
                if (!isExpand && lineNum >= MAX_LINE) {
                    break;
                }
                childIndexInLine = 0;
                useWidth = childWidth;
                //因为换行了，所以每行的最大高度置当前child高度。
                maxHeightInLine = childHeight;

                //累积行的总高度
                allHeight += maxHeightInLine;
                lineNum++;
            }
            if (ALog.getDebugMode()) {
                ALog.dWz(TAG, "onMeasure childText=" + getViewTag(child) + "\tindex=" + i + "\tlineNum=" + lineNum + "\tuseWidth=" + useWidth + "\tparentWidth=" + parentWidth);
            }
        }

        allHeight += maxHeightInLine;

        if (widthMode != MeasureSpec.EXACTLY) {
            //如果没有指定宽，则默认为屏幕宽
            widthSize = DeviceInfoUtils.getInstanse().getWidthReturnInt();
        }

        //如果没有指定高度
        if (heightMode != MeasureSpec.EXACTLY) {
            heightSize = allHeight + getPaddingBottom() + getPaddingTop();
        }
        setMeasuredDimension(widthSize, heightSize);
    }

    /**
     * 控制子控件的位置
     */
    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        if (changed) {
            if (getChildCount() == 0) {
                return;
            }
            realLayout(l, r);
        }
    }

    private void realLayout(int l, int r) {
        int layoutIndex = 1;
        //摆放子view
        View child;
        View image = getChildAt(getChildCount() - 1);
        //初始子view摆放的左上位置
        int right = r - l - getPaddingRight();
        int childX = getPaddingLeft();
        int childY = getPaddingTop();
        //一行view中将最大的高度存于此变量，用于子view进行换行时高度的计算
        for (int i = 0, len = getChildCount() - 1; i < len; i++) {
            child = getChildAt(i);
            int childWidth = child.getMeasuredWidth();
            int childHeight = child.getMeasuredHeight();
            int temp = childX + childWidth;

            if (isExpand) {
                if (temp > right) {
                    childX = getPaddingLeft();
                    childY = childY + childHeight;
                }
            } else {
                if (layoutIndex < MAX_LINE) {
                    if (temp > right) {
                        layoutIndex++;
                        childX = getPaddingLeft();
                        childY = childY + childHeight;
                    }
                } else if (layoutIndex == MAX_LINE) {
                    if (temp > right - imageSize - minEllipsisSize) {
                        int canUseWidth = right - imageSize - childX;
                        if (canUseWidth > 0) {
                            threeLineChildViewId = i;

                            LayoutParams layoutParams = child.getLayoutParams();
                            layoutParams.width = canUseWidth;
                            child.setLayoutParams(layoutParams);

                            measureChild(child, widthMeasureSpec, heightMeasureSpec);
                            child.layout(childX, childY, childX + canUseWidth, childY + childHeight);
                        }
                        if (ALog.getDebugMode()) {
                            ALog.dWz(TAG, "onLayout\tchildText=" + getViewTag(child) + "\tx=" + childX + "\ty=" + childY + "\tw=" + childWidth + "\th" + childHeight);
                        }
                        break;
                    }
                }
            }

            child.layout(childX, childY, childX + childWidth, childY + childHeight);
            if (ALog.getDebugMode()) {
                ALog.dWz(TAG, "onLayout\tchildText=" + getViewTag(child) + "\tx=" + childX + "\ty=" + childY + "\tw=" + childWidth + "\th" + childHeight);
            }
            //当前子view的起始left为 上一个子view的宽度+水平间距
            childX = childX + childWidth;
        }

        if (!isExpand) {
            drawImageView(layoutIndex, image, right, childY);
        }

        ALog.dWz(TAG, "allHeight onLayout: " + getHeight() + " top: " + childY);
    }

    private CharSequence getViewTag(View view) {
        if (null == view) {
            return "null";
        } else if (view instanceof TextView) {
            return ((TextView) view).getText();
        } else if (view instanceof ImageView) {
            return "ImageIcon";
        } else {
            return String.valueOf(view.getTag());
        }
    }

    private boolean isLastText(int index) {
        return index + 2 >= getChildCount();
    }

    /**
     * 画 小三角
     *
     * @param layoutIndex 第几行
     * @param image       小三角
     * @param right       右边距
     * @param top         顶端
     */
    private void drawImageView(int layoutIndex, View image, int right, int top) {
        if (threeLineChildViewId != 0 && layoutIndex >= MAX_LINE && !isLastText(threeLineChildViewId)) {
            //设置tag 标记 曾经显示过
            image.layout(right - image.getMeasuredWidth(), top, right, top + image.getMeasuredHeight());
        }
    }

    /**
     * 添加ImageView
     *
     * @param viewList viewList
     */
    public void addView(ArrayList<View> viewList) {
        removeAllViews();
        for (View view : viewList) {
            addView(view);
        }
        addView(mImageView);
    }

}