package com.dzbook.view.tips;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import com.dzbook.utils.ListUtils;
import com.ishugui.R;

import java.util.ArrayList;
import java.util.List;

/**
 * TipFlowLayout
 */
public class TipFlowLayout extends ViewGroup {

    /**
     * 靠左放置标签
     */
    public static final int START_FROM_LEFT = 1;

    /**
     * 居中放置标签
     */
    public static final int START_FROM_CENTER = 0;

    /**
     * 靠右放置标签
     */
    public static final int START_FROM_RIGHT = 2;

    private int lineMode = START_FROM_CENTER;
    // 水平间距，单位为px
    private int horizontalSpacing = 25;
    // 竖直间距，单位为px
    private int verticalSpacing = 45;
    // 行集合
    private List<Line> lines = new ArrayList<Line>();
    // 当前的行
    private Line line;
    // 当前行使用的空间
    private int lineUsedSize = 0;
    private int maxLine = Integer.MAX_VALUE;

    /**
     * 构造
     *
     * @param context context
     */
    public TipFlowLayout(Context context) {
        this(context, null);
    }

    /**
     * 构造
     *
     * @param context context
     * @param attrs   attrs
     */
    public TipFlowLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    /**
     * 构造
     *
     * @param context      context
     * @param attrs        attrs
     * @param defStyleAttr defStyleAttr
     */
    public TipFlowLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initData(attrs);
    }

    private void initData(AttributeSet attrs) {
        TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.TipFlowView, 0, 0);
        if (a != null) {
            lineMode = a.getInt(R.styleable.TipFlowView_line_mode, 0);
            horizontalSpacing = a.getInt(R.styleable.TipFlowView_horizontal_spacing, 25);
            verticalSpacing = a.getInt(R.styleable.TipFlowView_vertical_spacing, 45);
            maxLine = a.getInt(R.styleable.TipFlowView_max_line, 0);
            a.recycle();
        }
    }

    /**
     * 计算出所有子控件的宽和高，从而确定当前父布局的宽和高
     *
     * @param widthMeasureSpec
     * @param heightMeasureSpec
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        // 实际可以用的宽和高(去除 padding 内边距)
        int width = MeasureSpec.getSize(widthMeasureSpec) - getPaddingLeft() - getPaddingRight();
        int height = MeasureSpec.getSize(heightMeasureSpec) - getPaddingBottom() - getPaddingTop();
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        // Line初始化
        restoreLine();
        initLine();

        for (int count = getChildCount(), i = 0; i < count; i++) {
            View child = getChildAt(i);
            // 测量所有的childView
            int childWidthMeasureSpec = MeasureSpec.makeMeasureSpec(width, widthMode == MeasureSpec.EXACTLY ? MeasureSpec.AT_MOST : widthMode);
            int childHeightMeasureSpec = MeasureSpec.makeMeasureSpec(height, heightMode == MeasureSpec.EXACTLY ? MeasureSpec.AT_MOST : heightMode);
            //也可以 measureChild(child, childWidthMeasureSpec, childHeightMeasureSpec);
            child.measure(childWidthMeasureSpec, childHeightMeasureSpec);
            // 计算当前行已使用的宽度
            int measuredWidth = child.getMeasuredWidth();
            // 如果添加进去后宽度超过可用的宽度，需要换行，否则childView继续添加到当前的行上
            if (lineUsedSize + measuredWidth > width) {
                // 先换行，先将上一行保存到 lines 集合中，再换行
                saveAndNewLine();
            }
            //存储当前行已使用的宽度
            line.setUsedLineSize(lineUsedSize += measuredWidth + horizontalSpacing);
            //继续添加到当前行 line
            line.addChild(child);
        }

        // 如果有最后一行（未填满）把它记录到集合中
        if (line != null && !lines.contains(line)) {
            saveAndNewLine();
        }

        // 把所有行的高度加上
        int totalHeight = 0;
        for (Line curLine : lines) {
            totalHeight += curLine.getHeight();
        }
        // 加上行的竖直间距
        totalHeight += verticalSpacing * (lines.size() - 1);
        // 加上上下padding
        totalHeight += getPaddingBottom();
        totalHeight += getPaddingTop();

        /**
         * 设置自身尺寸，设置布局的宽高，宽度直接采用父 View 传递过来的最大宽度，而不用考虑子view是否填满宽度
         * 因为该布局的特性就是填满一行后，再换行。
         * 高度根据设置的模式来决定采用所有子View的高度之和还是采用父view传递过来的高度
         */
        setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec), resolveSize(totalHeight, heightMeasureSpec));
    }

    /**
     * 指定所有childView的位置，调用Line对象中的layout方法。
     *
     * @param changed
     * @param l
     * @param t
     * @param r
     * @param b
     */
    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int paddingLeft = getPaddingLeft();
        int paddingTop = getPaddingTop();
        int totalUsableWidth = getMeasuredWidth() - paddingLeft - paddingTop;
        for (Line curLine : lines) {
            curLine.layout(lineMode, paddingLeft, paddingTop, totalUsableWidth, horizontalSpacing);
            // 计算下一行 Y 轴起点坐标
            paddingTop = paddingTop + curLine.getHeight() + verticalSpacing;
        }
    }

    /**
     * 与当前ViewGroup对应的LayoutParams
     */
    @Override
    public LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new MarginLayoutParams(getContext(), attrs);
    }

    private void initLine() {
        if (line == null) {
            // 创建新一行
            line = new Line(this);
        }
    }

    private void restoreLine() {
        lines.clear();
        line = new Line(this);
        lineUsedSize = 0;
    }

    /**
     * 换行，先将上一行保存到 lines 集合中，再换行
     */
    private void saveAndNewLine() {
        //过滤 当前行最后一个子控件末尾 horizontalSpacing
        if (!ListUtils.isEmpty(lines) && lines.size() >= maxLine) {
            return;
        }
        if (lineUsedSize > 0) {
            line.setUsedLineSize(lineUsedSize - horizontalSpacing);
        }
        // 把之前的行记录下来加入到行集合中
        if (line != null) {
            lines.add(line);
        }
        //重置已用宽度为0
        lineUsedSize = 0;
        // 创建新的一行
        line = new Line(this);
    }

    /**
     * line
     */
    public static class Line {
        // 子控件集合
        private List<View> childList = new ArrayList<View>();
        // 行高
        private int height;
        // 当前行已经使用的宽度
        private int lineUsedSize = 0;
        private ViewGroup viewGroup;

        /**
         * 构造
         *
         * @param viewGroup viewGroup
         */
        public Line(ViewGroup viewGroup) {
            this.viewGroup = viewGroup;

        }

        /**
         * 添加childView
         *
         * @param childView 子控件
         */
        public void addChild(View childView) {
            childList.add(childView);
            // 更新行高为当前最高的一个childView的高度
            if (height < childView.getMeasuredHeight()) {
                height = childView.getMeasuredHeight();
            }
        }

        /**
         * 设置childView的绘制区域
         *
         * @param left              左上角x轴坐标
         * @param top               左上角y轴坐标
         * @param horizontalSpacing horizontalSpacing
         * @param lineMode          lineMode
         * @param totalUsableWidth  totalUsableWidth
         */
        public void layout(int lineMode, int left, int top, int totalUsableWidth, int horizontalSpacing) {
            // 当前childView的左上角x轴坐标
            switch (lineMode) {
                case TipFlowLayout.START_FROM_LEFT:
                    for (int i = 0; i < childList.size(); i++) {
                        View view = childList.get(i);
                        int offsetY = offsetY(viewGroup, view, top);
                        // 设置childView的绘制区域
                        view.layout(left, top + offsetY, left + view.getMeasuredWidth(), top + offsetY + view.getMeasuredHeight());
                        // 计算下一个childView的位置
                        left += view.getMeasuredWidth() + horizontalSpacing;
                    }
                    break;

                case TipFlowLayout.START_FROM_CENTER:
                    int square = (horizontalSpacing * (childList.size() - 1) + totalUsableWidth - lineUsedSize) / (childList.size() + 1);
                    for (View view : childList) {
                        // 设置childView的绘制区域
                        left += square;
                        view.layout(left, top, left + view.getMeasuredWidth(), top + view.getMeasuredHeight());
                        // 计算下一个childView的位置
                        left += view.getMeasuredWidth();
                    }
                    break;

                case TipFlowLayout.START_FROM_RIGHT:
                    left += totalUsableWidth - lineUsedSize;
                    if (childList.size() > 0) {
                        for (int index = childList.size() - 1; index >= 0; index--) {
                            // 设置childView的绘制区域
                            int offsetY = offsetY(viewGroup, childList.get(index), top);
                            childList.get(index).layout(left, top + offsetY, left + childList.get(index).getMeasuredWidth(), top + offsetY + childList.get(index).getMeasuredHeight());
                            // 计算下一个childView的位置
                            left += childList.get(index).getMeasuredWidth() + horizontalSpacing;
                        }
                    }
                    break;
                default:
                    break;
            }

        }


        /**
         * Y偏移
         *
         * @param viewGroup1 viewGroup
         * @param view       view
         * @param top        top
         * @return int
         */
        public int offsetY(ViewGroup viewGroup1, View view, int top) {
            if (null == viewGroup1 || null == view) {
                return 0;
            }
            int childHeight = view.getMeasuredHeight();
            int fatherHeight = viewGroup1.getMeasuredHeight();
            if (fatherHeight < childHeight) {
                return 0;
            }
            int offset = (fatherHeight - childHeight) / 2;
            return offset;
        }

        public int getHeight() {
            return height;
        }

        public int getChildCount() {
            return childList.size();
        }

        public void setUsedLineSize(int lineUsedSize1) {
            this.lineUsedSize = lineUsedSize1;
        }
    }

}
