package com.dzbook.view.common.loading;

import com.dzbook.view.PullLoadMoreRecycleLayout;

/**
 * PullLoadMoreRefreshListener
 *
 * @author wangwenzhou on 2017/1/6.
 */

public class PullLoadMoreRefreshListener implements RefreshLayout.OnRefreshListener {
    private static final long NUM = 5000L;
    PullLoadMoreRecycleLayout mPullLoadMoreRecyclerViewLinearLayout;

    /**
     * 构造
     *
     * @param pullLoadMoreRecyclerViewLinearLayout pullLoadMoreRecyclerViewLinearLayout
     */
    public PullLoadMoreRefreshListener(PullLoadMoreRecycleLayout pullLoadMoreRecyclerViewLinearLayout) {
        this.mPullLoadMoreRecyclerViewLinearLayout = pullLoadMoreRecyclerViewLinearLayout;
    }

    @Override
    public void onRefresh() {
        if (!mPullLoadMoreRecyclerViewLinearLayout.isRefresh()) {
            mPullLoadMoreRecyclerViewLinearLayout.setIsRefresh(true);
            mPullLoadMoreRecyclerViewLinearLayout.refresh();
        }
        //定时5秒 停止刷新动画
        mPullLoadMoreRecyclerViewLinearLayout.postDelayed(new Runnable() {
            @Override
            public void run() {
                mPullLoadMoreRecyclerViewLinearLayout.setRefreshing(false);
                mPullLoadMoreRecyclerViewLinearLayout.setIsRefresh(false);
            }
        }, NUM);
    }
}
