package com.dzbook.view.PageView;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;

import com.dzbook.GlideApp;
import com.dzbook.templet.adapter.DzDelegateAdapter;


/**
 * PageRecyclerView
 */
public class PageRecyclerView extends RecyclerView {

    private static final int FLING_MIN_AMOUNT = 15;
    private static final int FLING_MAX_AMOUNT = 30;

    private boolean dragging = false;
    private boolean isPaused = false;
    private OnLoadNextListener mLoadNextListener;

    private OnScrollViewListener mScrollViewListener;

    /**
     * 构造
     *
     * @param context context
     */
    public PageRecyclerView(Context context) {
        super(context);
        init();
    }

    /**
     * 构造
     *
     * @param context context
     * @param attrs   attrs
     */
    public PageRecyclerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    /**
     * 构造
     *
     * @param context  context
     * @param attrs    attrs
     * @param defStyle defStyle
     */
    public PageRecyclerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();

    }

    public void setScrollViewListener(OnScrollViewListener scrollViewListener) {
        this.mScrollViewListener = scrollViewListener;
    }

    /**
     * setState
     *
     * @param state state
     */
    public void setState(PageState state) {
        RecyclerView.Adapter adapter = getAdapter();
        if (adapter == null) {
            return;
        }
        if (adapter instanceof PageRecyclerAdapter) {
            ((PageRecyclerAdapter) adapter).setState(state);
        } else if (adapter instanceof DzDelegateAdapter) {
            ((DzDelegateAdapter) adapter).setState(state);
        }
    }

    /**
     * getState
     *
     * @return PageState
     */
    public PageState getState() {
        RecyclerView.Adapter adapter = getAdapter();
        if (adapter instanceof PageRecyclerAdapter) {
            return ((PageRecyclerAdapter) adapter).getState();
        } else if (adapter instanceof DzDelegateAdapter) {
            return ((DzDelegateAdapter) adapter).getState();
        }
        return null;
    }


    private void init() {
        addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (mScrollViewListener != null) {
                    mScrollViewListener.onScrolled(recyclerView, dx, dy);
                }
            }

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (mScrollViewListener != null) {
                    mScrollViewListener.onScrollStateChanged(recyclerView, newState);
                }
                RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
                int visibleItemCount = layoutManager.getChildCount();
                int totalItemCount = layoutManager.getItemCount();
                int lastVisibleItemPosition = 0;
                if (totalItemCount > 0) {
                    if (layoutManager instanceof LinearLayoutManager) {
                        lastVisibleItemPosition = ((LinearLayoutManager) layoutManager).findLastVisibleItemPosition();
                    }
                }

                if (visibleItemCount > 0) {
                    if ((getState() == PageState.Loadable || getState() == PageState.Failed) && newState == RecyclerView.SCROLL_STATE_IDLE && lastVisibleItemPosition >= (totalItemCount - 1)) {
                        if (mLoadNextListener != null) {
                            setState(PageState.Loading);
                            mLoadNextListener.onLoadNext();
                        }
                    }
                }
                //                switch (newState) {
                //                    case SCROLL_STATE_IDLE:
                //                        if (getContext() != null) {
                //                            Glide.with(getContext()).resumeRequests();
                //                        }
                //                        break;
                //                    default:
                //                        if (getContext() != null) {
                //                            Glide.with(getContext()).pauseRequests();
                //                        }
                //                        break;
                //                }

            }
        });

        disableScrollLoad();
    }


    /**
     * disableScrollLoad
     */
    public void disableScrollLoad() {
        addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                scrollStateStateChanged(newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                scrolled(dy);
            }
        });
    }

    private void scrolled(int dy) {
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

    private void scrollStateStateChanged(int newState) {
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


    public void setLoadNextListener(OnLoadNextListener listener) {
        mLoadNextListener = listener;
    }

}
