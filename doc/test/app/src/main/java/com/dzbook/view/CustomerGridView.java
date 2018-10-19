package com.dzbook.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.GridView;

/**
 * CustomerGridView
 */
public class CustomerGridView extends GridView {

    /**
     * 构造
     *
     * @param context context
     * @param attrs   attrs
     */
    public CustomerGridView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    /**
     * 构造
     *
     * @param context  context
     * @param attrs    attrs
     * @param defStyle defStyle
     */
    public CustomerGridView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void onWindowVisibilityChanged(int visibility) {
        super.onWindowVisibilityChanged(visibility);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, expandSpec);
    }

}
