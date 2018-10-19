package com.dzbook.view;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DzFastScroller;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;

import com.dzbook.GlideApp;
import com.dzbook.lib.net.DzSchedulers;
import com.dzbook.lib.utils.ALog;
import com.dzbook.view.common.loading.PullLoadMoreRefreshListener;
import com.dzbook.view.common.loading.RefreshLayout;
import com.ishugui.R;

import huawei.widget.HwProgressBar;
import hw.sdk.HwSdkAppConstant;

/**
 * 上啦加载更多布局
 *
 * @author wangwenzhou 2017/1/16
 */
public class PullLoadMoreRecycleLayout extends LinearLayout {
    private static final int FLING_MIN_AMOUNT = 15;
    private static final int FLING_MAX_AMOUNT = 30;
    private static final String TAG = "PullLoadMoreRecyclerView: ";

    private boolean dragging = false;
    private boolean isPaused = false;

    private RefreshLayout mSwipeRefreshLayout;
    private PullLoadMoreListener mPullLoadMoreListener;
    private HwProgressBar loadingview;
    private boolean hasMore = true;
    private boolean isRefresh = false;
    private boolean isLoadMore = false;
    /**
     * 是否支持全局刷新（有些列表不需要刷新）
     */
    private boolean isAllReference = true;
    private View mFooterView;
    private Context mContext;
    private HeaderAndFooterRecyclerView mRecyclerView;
    private boolean isFirstItemShow = false;
    private boolean isLastItemShow = false;
    private int fastScrollType = 0;


    /**
     * 构造
     *
     * @param context context
     */
    public PullLoadMoreRecycleLayout(Context context) {
        super(context);
        initView(context);
    }

    /**
     * 构造
     *
     * @param context context
     * @param attrs   attrs
     */
    public PullLoadMoreRecycleLayout(Context context, AttributeSet attrs) {
        super(context, attrs);

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.PullLoadMoreRecycleLayout, 0, 0);
        fastScrollType = typedArray.getInteger(R.styleable.PullLoadMoreRecycleLayout_fastscroll_type, 0);
        typedArray.recycle();

        initView(context);
    }

    /**
     * 刷新
     *
     * @param allReference allReference
     */
    public void setAllReference(boolean allReference) {
        isAllReference = allReference;
        mSwipeRefreshLayout.setCanRefresh(allReference);
    }

    public boolean isAllReference() {
        return isAllReference;
    }

    /**
     * setOnScrollListener
     *
     * @param recyclerViewOnScrollListener recyclerViewOnScrollListener
     * @deprecated Use {@link #addOnScrollListener(RecyclerView.OnScrollListener)}
     */

    public void setOnScrollListener(RecyclerViewOnScrollListener recyclerViewOnScrollListener) {
        if (mRecyclerView != null) {
            mRecyclerView.setOnScrollListener(recyclerViewOnScrollListener);
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private void initView(Context context) {
        mContext = context;
        LayoutInflater.from(context).inflate(R.layout.pull_loadmore_layout_swiperefreshlayout, this, true);
        mSwipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);

        mSwipeRefreshLayout.setRefreshListener(new PullLoadMoreRefreshListener(this));
        mRecyclerView = findViewById(R.id.recycler_view);
        loadingview = findViewById(R.id.loadingview);

        mRecyclerView.setVerticalScrollBarEnabled(true);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.addOnScrollListener(new RecyclerViewOnScrollListener(this));
        mRecyclerView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return isRefresh;
            }
        });

        if (fastScrollType == 1) {
            Resources resources = getContext().getResources();
            StateListDrawable verticalThumbDrawable = (StateListDrawable) resources.getDrawable(R.drawable.shap_thumb_drawable);
            Drawable verticalTrackDrawable = resources.getDrawable(R.drawable.shap_line_drawable);
            StateListDrawable horizontalThumbDrawable = (StateListDrawable) resources.getDrawable(R.drawable.shap_thumb_drawable);
            Drawable horizontalTrackDrawable = resources.getDrawable(R.drawable.shap_line_drawable);
            initFastScroller(verticalThumbDrawable, verticalTrackDrawable, horizontalThumbDrawable, horizontalTrackDrawable);
        }

        disableScrollLoad();

        mFooterView = findViewById(R.id.footer_linearlayout);
        mFooterView.setVisibility(View.GONE);
    }

    @SuppressLint("VisibleForTests")
    void initFastScroller(StateListDrawable verticalThumbDrawable, Drawable verticalTrackDrawable, StateListDrawable horizontalThumbDrawable, Drawable horizontalTrackDrawable) {
        Resources resources = getContext().getResources();
        new DzFastScroller(mRecyclerView, verticalThumbDrawable, verticalTrackDrawable, horizontalThumbDrawable, horizontalTrackDrawable, resources.getDimensionPixelSize(android.support.v7.recyclerview.R.dimen.fastscroll_default_thickness), resources.getDimensionPixelSize(android.support.v7.recyclerview.R.dimen.fastscroll_minimum_range), resources.getDimensionPixelOffset(android.support.v7.recyclerview.R.dimen.fastscroll_margin));
    }

    /**
     * 添加滚动监听
     */
    public void disableScrollLoad() {
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                Context context = getContext();
                if (context == null) {
                    return;
                }
                if (context instanceof Activity && ((Activity) context).isFinishing()) {
                    return;
                }

                dragging = newState == RecyclerView.SCROLL_STATE_DRAGGING;
                if (newState == RecyclerView.SCROLL_STATE_IDLE || newState == RecyclerView.SCROLL_STATE_DRAGGING) {
                    GlideApp.with(context).resumeRequests();
                    isPaused = false;
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                Context context = getContext();
                if (context == null) {
                    return;
                }
                if (context instanceof Activity && ((Activity) context).isFinishing()) {
                    return;
                }
                if (!dragging) {
                    int amount = Math.abs(dy);
                    if (isPaused && amount < FLING_MIN_AMOUNT) {
                        GlideApp.with(context).resumeRequests();
                        isPaused = false;
                    } else if (!isPaused && amount > FLING_MAX_AMOUNT) {
                        GlideApp.with(context).pauseRequests();
                        isPaused = true;
                    }
                }
            }
        });
    }

    /**
     * 设置LinearLayout布局结构
     */
    public void setLinearLayout() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mContext);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(linearLayoutManager);
    }

    /**
     * 设置LgridLayout布局结构
     *
     * @param spanCount spanCount
     */
    public void setGridLayout(int spanCount) {
        GridLayoutManager gridLayoutManager = new GridLayoutManager(mContext, spanCount);
        gridLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(gridLayoutManager);
    }


    /**
     * setStaggeredGridLayout
     *
     * @param spanCount spanCount
     */
    public void setStaggeredGridLayout(int spanCount) {
        StaggeredGridLayoutManager staggeredGridLayoutManager = new StaggeredGridLayoutManager(spanCount, LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(staggeredGridLayoutManager);
    }


    /**
     * 设置可否刷新
     *
     * @param enable enable
     */
    public void setCanRefresh(boolean enable) {
        mSwipeRefreshLayout.setEnabled(enable);
    }

    public boolean getPullRefreshEnable() {
        return mSwipeRefreshLayout.isEnabled();
    }

    public RecyclerView.LayoutManager getLayoutManager() {
        return mRecyclerView.getLayoutManager();
    }


    /**
     * 加载更多
     */
    public void loadMore() {
        if (mPullLoadMoreListener != null && hasMore) {
            mFooterView.setVisibility(View.VISIBLE);
            loadingview.setVisibility(VISIBLE);

            if (HwSdkAppConstant.isAbKey()) {
                DzSchedulers.mainDelay(new Runnable() {
                    @Override
                    public void run() {
                        mPullLoadMoreListener.onLoadMore();
                    }
                }, 1500);
            } else {
                mPullLoadMoreListener.onLoadMore();
            }

        }
    }


    /**
     * 加载完成
     */
    public void setPullLoadMoreCompleted() {
        DzSchedulers.mainDelay(new Runnable() {
            @Override
            public void run() {
                isRefresh = false;
                mSwipeRefreshLayout.setRefreshing(false);

                isLoadMore = false;
                mFooterView.setVisibility(View.GONE);
                loadingview.setVisibility(GONE);
            }
        }, 100);
    }

    /**
     * 设置可否刷新
     *
     * @param isRefreshing isRefreshing
     */
    public void setRefreshing(final boolean isRefreshing) {
        mSwipeRefreshLayout.post(new Runnable() {

            @Override
            public void run() {
                mSwipeRefreshLayout.setRefreshing(isRefreshing);
            }
        });
    }

    public void setOnPullLoadMoreListener(PullLoadMoreListener listener) {
        mPullLoadMoreListener = listener;
    }

    /**
     * 刷新
     */
    public void refresh() {
        mRecyclerView.setVisibility(View.VISIBLE);
        if (mPullLoadMoreListener != null) {
            mPullLoadMoreListener.onRefresh();
        }
    }

    /**
     * 滚动到顶部
     */
    public void scrollToTop() {
        mRecyclerView.scrollToPosition(0);
    }


    /**
     * 设置adapter
     *
     * @param adapter adapter
     */
    public void setAdapter(RecyclerView.Adapter adapter) {
        if (adapter != null) {
            mRecyclerView.setAdapter(adapter);
        }
    }

    public boolean isLoadMore() {
        return isLoadMore;
    }

    public void setIsLoadMore(boolean isLoadMore) {
        this.isLoadMore = isLoadMore;
    }

    public boolean isRefresh() {
        return isRefresh;
    }

    public void setIsRefresh(boolean isRefresh) {
        this.isRefresh = isRefresh;
    }

    public boolean isHasMore() {
        return hasMore;
    }

    public void setHasMore(boolean hasMore) {
        this.hasMore = hasMore;
    }

    /**
     * 设置是否禁用下拉刷新功能 默认是禁用的
     */
    public void setRefreshDisable() {
        mSwipeRefreshLayout.setCanRefresh(false);
    }

    /**
     * 设置要显示那一行。。。没有偏移
     *
     * @param selectPosition selectPosition
     */
    public void setSelectionFromTop(int selectPosition) {
        ALog.dWz(TAG, "selectPosition：" + selectPosition);
        mRecyclerView.scrollToPosition(selectPosition);
        RecyclerView.LayoutManager layoutManager = mRecyclerView.getLayoutManager();
        if (layoutManager instanceof LinearLayoutManager) {
            ((LinearLayoutManager) layoutManager).scrollToPositionWithOffset(selectPosition, 0);
        }
    }

    /**
     * 平滑的滑动到某个item
     *
     * @param selectPosition selectPosition
     */
    public void scrollToPosition(int selectPosition) {
        mRecyclerView.smoothScrollToPosition(selectPosition);
    }

    /**
     * 是否滑动到顶部
     *
     * @return boolean
     */
    public boolean getFirstItemIsShow() {
        return isFirstItemShow;
    }

    /**
     * 线性布局里 判断第一个item是否完全展示出来
     *
     * @param isShow isShow
     */
    public void setFirstItemShow(boolean isShow) {
        this.isFirstItemShow = isShow;
    }

    /**
     * 线性布局里 判断最后一个item是否完全展示出来
     *
     * @param isShow isShow
     */
    public void setLastItemShow(boolean isShow) {
        this.isLastItemShow = isShow;
    }

    public boolean getLastItemShow() {
        return isLastItemShow;
    }

    /**
     * 加载更多接口
     */
    public interface PullLoadMoreListener {
        /**
         * 刷新
         */
        void onRefresh();

        /**
         * 加载更多
         */
        void onLoadMore();
    }

    /**
     * 移除头布局
     *
     * @param view view
     */
    public void removeHeaderView(View view) {
        mRecyclerView.removeHeaderView(view);
    }

    /**
     * 添加头布局
     *
     * @param view view
     */
    public void addHeaderView(View view) {
        mRecyclerView.addHeaderView(view);
    }

    /**
     * 添加脚布局
     *
     * @param view view
     */
    public void addFooterView(View view) {
        mRecyclerView.addFooterView(view);
    }

    /**
     * 移除所有头布局
     */
    public void removeAllHeaderView() {
        mRecyclerView.removeAllHeaderView();
    }

    /**
     * 移除脚布局
     *
     * @param view view
     */
    public void removeFooterView(View view) {
        mRecyclerView.removeFooterView(view);
    }

    /**
     * 移除所有脚布局
     */
    public void removeAllFooterAllView() {
        mRecyclerView.removeAllFooterAllView();
    }


    public void setFooterView(View footerView) {
        this.mFooterView = footerView;
    }

    public RecyclerView.Adapter getAdapter() {
        return mRecyclerView.getAdapter();
    }

    /**
     * setItemDivider
     *
     * @param itemDivider itemDivider
     */
    public void setItemDivider(RecyclerView.ItemDecoration itemDivider) {
        mRecyclerView.addItemDecoration(itemDivider);
    }

    /**
     * 添加滚动监听
     *
     * @param listener listener
     */
    public void addOnScrollListener(RecyclerView.OnScrollListener listener) {
        this.mRecyclerView.addOnScrollListener(listener);
    }

    public RecyclerView getRecyclerView() {
        return mRecyclerView;
    }

    /**
     * 是否有header
     *
     * @return true 有 false 没有header
     */
    public boolean hasHeader() {
        return mRecyclerView.getHeaderSize() > 0;
    }
}
