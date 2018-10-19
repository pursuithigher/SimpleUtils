package com.dzbook.view;

import android.content.Context;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * 可以禁止下拉刷新的SwipeRefreshLayout
 *
 * @author wangwenzhou on 2017/1/11.
 */

public class PullLoadMoreSwipeLayout extends SwipeRefreshLayout {
    /**
     * 能否刷新
     */
    public boolean isCanRefresh = true;

    /**
     * 构造
     *
     * @param context context
     */
    public PullLoadMoreSwipeLayout(Context context) {
        super(context);
    }

    /**
     * 构造
     *
     * @param context context
     * @param attrs   attrs
     */
    public PullLoadMoreSwipeLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        if (!isCanRefresh) {
            return false;
        }
        return super.onInterceptTouchEvent(event);
    }

    public void setCanRefresh(boolean canRefresh) {
        this.isCanRefresh = canRefresh;
    }
}
