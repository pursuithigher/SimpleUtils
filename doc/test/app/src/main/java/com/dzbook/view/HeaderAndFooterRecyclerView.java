package com.dzbook.view;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;

import com.dzbook.adapter.BookStoreSearchHeaderAndFooterAdapter;

/**
 * RecyclerView
 *
 * @author wangwenzhou on 2017/1/13.
 */

public class HeaderAndFooterRecyclerView extends RecyclerView {
    protected BookStoreSearchHeaderAndFooterAdapter mAdapter;
    protected Adapter mRealAdapter;

    private final DataObserver mDataObserver = new DataObserver();

    /**
     * 构造
     *
     * @param context context
     */
    public HeaderAndFooterRecyclerView(Context context) {
        super(context);
    }

    /**
     * 构造
     *
     * @param context context
     * @param attrs   attrs
     */
    public HeaderAndFooterRecyclerView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    /**
     * 构造
     *
     * @param context  context
     * @param attrs    attrs
     * @param defStyle defStyle
     */
    public HeaderAndFooterRecyclerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void setAdapter(Adapter adapter) {
        mRealAdapter = adapter;

        if (adapter instanceof BookStoreSearchHeaderAndFooterAdapter) {
            mAdapter = (BookStoreSearchHeaderAndFooterAdapter) adapter;
        } else {
            mAdapter = new BookStoreSearchHeaderAndFooterAdapter<Adapter<ViewHolder>>(adapter);
        }
        super.setAdapter(mAdapter);
        mRealAdapter.registerAdapterDataObserver(mDataObserver);
        mDataObserver.onChanged();
    }

    /**
     * 添加头视图
     *
     * @param view view
     */
    public void addHeaderView(View view) {
        if (null == view) {
            throw new IllegalArgumentException("the view to add must not be null !");
        } else if (mAdapter == null) {
            throw new IllegalStateException("u must set a adapter !");
        } else {
            mAdapter.addHeaderView(view);
        }
    }

    /**
     * 添加脚布局
     *
     * @param view view
     */
    public void addFooterView(View view) {
        if (null == view) {
            throw new IllegalArgumentException("the view to add must not be null !");
        } else if (mAdapter == null) {
            throw new IllegalStateException("u must set a adapter !");
        } else {
            mAdapter.addFooterView(view);
        }
    }

    /**
     * 移除头布局
     *
     * @param view view
     */
    public void removeHeaderView(View view) {
        if (null == view) {
            throw new IllegalArgumentException("the view to remove must not be null !");
        } else if (mAdapter == null) {
            throw new IllegalStateException("uu must set a adapter  !");
        } else {
            mAdapter.removeHeaderView(view);
        }
    }

    /**
     * 移除脚布局
     *
     * @param view view
     */
    public void removeFooterView(View view) {
        if (null == view) {
            throw new IllegalArgumentException("the view to remove must not be null !");
        } else if (mAdapter == null) {
            throw new IllegalStateException("u must set a adapter !");
        } else {
            mAdapter.removeFooterView(view);
        }
    }

    /**
     * 移除全部Header
     */
    public void removeAllHeaderView() {
        mAdapter.removeAllHeaderView();
    }

    /**
     * 移除全部FooterView
     */
    public void removeAllFooterAllView() {
        mAdapter.removeAllFooterView();
    }

    public int getHeaderSize() {
        return mAdapter.getHeaderSize();
    }

    /**
     * 数据监听
     */
    private class DataObserver extends AdapterDataObserver {

        @Override
        public void onChanged() {
            if (mAdapter == null) {
                return;
            }
            if (mRealAdapter != mAdapter) {
                mAdapter.notifyDataSetChanged();
            }
            int itemCount = 0;
            Adapter adapter = getAdapter();
            if (adapter instanceof BookStoreSearchHeaderAndFooterAdapter) {
                itemCount += ((BookStoreSearchHeaderAndFooterAdapter) adapter).getHeadersCount() + ((BookStoreSearchHeaderAndFooterAdapter) adapter).getFootersCount();
            }
            itemCount += mRealAdapter.getItemCount();
            if (itemCount == 0) {
                if (getVisibility() != INVISIBLE) {
                    setVisibility(INVISIBLE);
                }
            } else {
                if (getVisibility() != VISIBLE) {
                    setVisibility(VISIBLE);
                }
            }
        }

    }

}
