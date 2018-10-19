package com.dzbook.view;

import android.support.v4.widget.SwipeRefreshLayout;

/**
 * 下拉刷新
 *
 * @author wangwenzhou on 2017/1/6.
 */

public class PullLoadMoreSwipeRefreshListener implements SwipeRefreshLayout.OnRefreshListener {
    PullLoadMoreRecycleLayout mPullLoadMoreRecyclerViewLinearLayout;

    /**
     * PullLoadMoreSwipeRefreshListener
     * @param pullLoadMoreRecyclerViewLinearLayout pullLoadMoreRecyclerViewLinearLayout
     */
    public PullLoadMoreSwipeRefreshListener(PullLoadMoreRecycleLayout pullLoadMoreRecyclerViewLinearLayout) {
        this.mPullLoadMoreRecyclerViewLinearLayout = pullLoadMoreRecyclerViewLinearLayout;
    }

    @Override
    public void onRefresh() {
        if (!mPullLoadMoreRecyclerViewLinearLayout.isRefresh()) {
            mPullLoadMoreRecyclerViewLinearLayout.setIsRefresh(true);
            mPullLoadMoreRecyclerViewLinearLayout.refresh();
        }
        mPullLoadMoreRecyclerViewLinearLayout.postDelayed(new Runnable() {
            @Override
            public void run() {
                mPullLoadMoreRecyclerViewLinearLayout.setRefreshing(false);
                mPullLoadMoreRecyclerViewLinearLayout.setIsRefresh(false);
            }
        }, 5000L);//定时5秒 停止刷新动画
    }
}
