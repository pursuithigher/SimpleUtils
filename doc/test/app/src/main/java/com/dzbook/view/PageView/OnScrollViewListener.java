package com.dzbook.view.PageView;

import android.support.v7.widget.RecyclerView;

/**
 * OnScrollViewListener
 *
 * @author dongdianzhou on 2018/1/18.
 */

public interface OnScrollViewListener {

    /**
     * onScrollStateChanged
     *
     * @param recyclerView recyclerView
     * @param newState     newState
     */
    void onScrollStateChanged(RecyclerView recyclerView, int newState);

    /**
     * onScrolled
     *
     * @param recyclerView recyclerView
     * @param dx           dx
     * @param dy           dy
     */
    void onScrolled(RecyclerView recyclerView, int dx, int dy);
}
