package com.dzbook.view;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * SelfAdapterGridLayoutManager
 *
 * @author dongdianzhou on 2017/9/12.
 */

public class SelfAdapterGridLayoutManager extends GridLayoutManager {

    /**
     * 构造
     *
     * @param context   context
     * @param spanCount spanCount
     */
    public SelfAdapterGridLayoutManager(Context context, int spanCount) {
        super(context, spanCount);
    }

    @Override
    public void onMeasure(RecyclerView.Recycler recycler, RecyclerView.State state, int widthSpec, int heightSpec) {
        int itemnum = getItemCount();
        if (itemnum > 0) {
            int height = 0;
            for (int i = 0; i < itemnum; i++) {
                if (i < state.getItemCount()) {
                    View view = recycler.getViewForPosition(i);
                    if (view != null) {
                        measureChild(view, widthSpec, heightSpec);
                        height += view.getMeasuredHeight();
                    }
                }
            }
            if (height != 0) {
                heightSpec = View.MeasureSpec.makeMeasureSpec(height / getSpanCount(), View.MeasureSpec.AT_MOST);
            }
        }
        super.onMeasure(recycler, state, widthSpec, heightSpec);
    }
}
