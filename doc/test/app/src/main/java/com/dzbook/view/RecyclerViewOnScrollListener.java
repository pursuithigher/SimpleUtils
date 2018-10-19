package com.dzbook.view;

import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;

/**
 * RecyclerView滚动监听
 *
 * @author Winzows on 2017/1/6.
 */

public class RecyclerViewOnScrollListener extends RecyclerView.OnScrollListener {
    private PullLoadMoreRecycleLayout mPullLoadMoreRecyclerViewLinearLayout;

    RecyclerViewOnScrollListener(PullLoadMoreRecycleLayout pullLoadMoreRecyclerViewLinearLayout) {
        this.mPullLoadMoreRecyclerViewLinearLayout = pullLoadMoreRecyclerViewLinearLayout;
    }

    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);
        int lastVisibleItem = 0;
        int firstVisibleItem = 0;
        RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
        int totalItemCount = layoutManager.getItemCount();
        if (layoutManager instanceof GridLayoutManager) {
            GridLayoutManager gridLayoutManager = (GridLayoutManager) layoutManager;
            //找到当前布局管理器的最后一项
            lastVisibleItem = gridLayoutManager.findLastVisibleItemPosition();
            firstVisibleItem = gridLayoutManager.findFirstCompletelyVisibleItemPosition();
        } else if (layoutManager instanceof LinearLayoutManager) {
            LinearLayoutManager linearLayoutManager = (LinearLayoutManager) layoutManager;
            lastVisibleItem = linearLayoutManager.findLastVisibleItemPosition();
            firstVisibleItem = linearLayoutManager.findFirstCompletelyVisibleItemPosition();
        } else if (layoutManager instanceof StaggeredGridLayoutManager) {
            StaggeredGridLayoutManager staggeredGridLayoutManager = (StaggeredGridLayoutManager) layoutManager;
            // since may lead to the final item has more than one StaggeredGridLayoutManager the particularity of the so here that is an array
            // this array into an array of position and then take the maximum value that is the last show the position value
            int[] lastPositions = new int[((StaggeredGridLayoutManager) layoutManager).getSpanCount()];
            staggeredGridLayoutManager.findLastVisibleItemPositions(lastPositions);
            lastVisibleItem = findMax(lastPositions);
            firstVisibleItem = staggeredGridLayoutManager.findFirstVisibleItemPositions(lastPositions)[0];
        }

        boolean canRefresh = firstVisibleItem == 0 && mPullLoadMoreRecyclerViewLinearLayout.isAllReference() && !mPullLoadMoreRecyclerViewLinearLayout.isLoadMore();

        mPullLoadMoreRecyclerViewLinearLayout.setCanRefresh(canRefresh);
        mPullLoadMoreRecyclerViewLinearLayout.setFirstItemShow(firstVisibleItem == 0);
        mPullLoadMoreRecyclerViewLinearLayout.setLastItemShow(lastVisibleItem >= totalItemCount - 1 && (dx > 0 || dy > 0));

        //Either horizontal or vertical
        if (!mPullLoadMoreRecyclerViewLinearLayout.isRefresh() && mPullLoadMoreRecyclerViewLinearLayout.isHasMore() && (lastVisibleItem >= totalItemCount - 1) && !mPullLoadMoreRecyclerViewLinearLayout.isLoadMore() && (dx > 0 || dy > 0)) {
            mPullLoadMoreRecyclerViewLinearLayout.setIsLoadMore(true);
            mPullLoadMoreRecyclerViewLinearLayout.loadMore();
        }

    }
    //To find the maximum value in the array

    private int findMax(int[] lastPositions) {

        int max = lastPositions[0];
        for (int value : lastPositions) {
            if (value > max) {
                max = value;
            }
        }
        return max;
    }
}
