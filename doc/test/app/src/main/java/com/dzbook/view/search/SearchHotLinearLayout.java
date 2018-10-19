package com.dzbook.view.search;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

/**
 * SearchHotLinearLayout
 *
 * @author dongdz on 2016/8/1.
 */
public class SearchHotLinearLayout extends LinearLayout {

    /**
     * 构造
     *
     * @param context context
     */
    public SearchHotLinearLayout(Context context) {
        super(context);
    }

    /**
     * 构造
     *
     * @param context context
     * @param attrs   attrs
     */
    public SearchHotLinearLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    /**
     * 构造
     *
     * @param context      context
     * @param attrs        attrs
     * @param defStyleAttr defStyleAttr
     */
    public SearchHotLinearLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int count = getChildCount();
        if (count == 0) {
            throw new RuntimeException("必须包含子view");
        }
        int sizeWidth = MeasureSpec.getSize(widthMeasureSpec);
        int surplusViewWidth = 0;
        View view = getChildAt(0);
        for (int i = 1; i < count; i++) {
            int viewWidth = getChildWidth(getChildAt(i), widthMeasureSpec, heightMeasureSpec);
            surplusViewWidth = surplusViewWidth + viewWidth;
        }
        measureChild(view, MeasureSpec.makeMeasureSpec(sizeWidth - surplusViewWidth, MeasureSpec.AT_MOST), heightMeasureSpec);
        setMeasuredDimension(sizeWidth, MeasureSpec.getSize(heightMeasureSpec));
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int count = getChildCount();
        if (count == 0) {
            throw new RuntimeException("必须包含子view");
        }
        int viewMeasureHeight = b - t;
        View view = getChildAt(0);
        int viewT = viewMeasureHeight / 2 - view.getMeasuredHeight() / 2;
        MarginLayoutParams layoutparams = (MarginLayoutParams) view.getLayoutParams();
        view.layout(l + layoutparams.leftMargin, viewT, l + layoutparams.leftMargin + view.getMeasuredWidth(), viewT + view.getMeasuredHeight());
        int lastViewL = l + layoutparams.leftMargin + view.getMeasuredWidth() + layoutparams.rightMargin;
        for (int i = 1; i < count; i++) {
            view = getChildAt(i);
            layoutparams = (MarginLayoutParams) view.getLayoutParams();
            viewT = viewMeasureHeight / 2 - view.getMeasuredHeight() / 2;
            view.layout(lastViewL + layoutparams.leftMargin, viewT, lastViewL + layoutparams.leftMargin + view.getMeasuredWidth(), viewT + view.getMeasuredHeight());
            lastViewL = lastViewL + layoutparams.leftMargin + view.getMeasuredWidth() + layoutparams.rightMargin;
        }
    }

    /**
     * 计算view的宽度（包含margin）
     *
     * @param view
     * @param widthMeasureSpec
     * @param heightMeasureSpec
     * @return
     */
    private int getChildWidth(View view, int widthMeasureSpec, int heightMeasureSpec) {
        if (view.getVisibility() == VISIBLE || view.getVisibility() == INVISIBLE) {
            measureChild(view, widthMeasureSpec, heightMeasureSpec);
            MarginLayoutParams viewMarginParams = (MarginLayoutParams) view.getLayoutParams();
            return view.getMeasuredWidth() + viewMarginParams.leftMargin + viewMarginParams.rightMargin;
        } else {
            return 0;
        }
    }
}
