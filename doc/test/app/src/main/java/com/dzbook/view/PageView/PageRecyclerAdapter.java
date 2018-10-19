package com.dzbook.view.PageView;


import android.support.v7.widget.RecyclerView;

/**
 * PageRecyclerAdapter
 *
 * @param <VH>
 * @author wxliao on 2015/7/17.
 */
public abstract class PageRecyclerAdapter<VH extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<VH> {

    protected PageState mState = PageState.Loadable;

    /**
     * setState
     *
     * @param state state
     */
    protected void setState(PageState state) {
        mState = state;
        notifyDataSetChanged();
    }

    protected PageState getState() {
        return mState;
    }
}
