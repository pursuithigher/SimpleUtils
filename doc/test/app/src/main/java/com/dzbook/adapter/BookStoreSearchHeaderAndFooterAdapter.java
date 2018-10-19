package com.dzbook.adapter;

import android.support.v4.util.SparseArrayCompat;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.Adapter;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.View;
import android.view.ViewGroup;

/**
 * 自定义RecycleViewAdapter可以动态添加删除header和footer
 *
 * @param <T>
 * @author wangwenzhou on 2017/1/12.
 */

public class BookStoreSearchHeaderAndFooterAdapter<T extends Adapter<ViewHolder>> extends Adapter {

    private int baseItemTypeHeader = 10000000;
    private int baseItemTypeFooter = 20000000;

    private SparseArrayCompat<View> mHeaderViews = new SparseArrayCompat<View>();
    private SparseArrayCompat<View> mFooterViews = new SparseArrayCompat<View>();

    private T mRealAdapter;


    /**
     * 构造
     *
     * @param mRealAdapter mRealAdapter
     */
    public BookStoreSearchHeaderAndFooterAdapter(T mRealAdapter) {
        super();
        this.mRealAdapter = mRealAdapter;
    }

    @Override
    public int getItemCount() {
        return getRealItemCount() + getHeadersCount() + getFootersCount();
    }

    @Override
    public int getItemViewType(int position) {
        if (isHeaderPosition(position)) {
            return mHeaderViews.keyAt(position);
        }
        if (isFooterPosition(position)) {
            return mFooterViews.keyAt(position - getHeadersCount() - getRealItemCount());
        }
        return mRealAdapter.getItemViewType(position - getHeadersCount());
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //如果是头部
        if (isHeaderType(viewType)) {
            int headerPosition = mHeaderViews.indexOfKey(viewType);
            View headerView = mHeaderViews.valueAt(headerPosition);
            return createHeaderAndFooterViewHolder(headerView);
        }
        //如果是尾部
        if (isFooterType(viewType)) {
            int footerPosition = mFooterViews.indexOfKey(viewType);
            View footerView = mFooterViews.valueAt(footerPosition);
            return createHeaderAndFooterViewHolder(footerView);
        }
        return mRealAdapter.onCreateViewHolder(parent, viewType);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if (!isHeaderPosition(position) && !isFooterPosition(position)) {
            final int realPosition = position - getHeadersCount();
            mRealAdapter.onBindViewHolder(holder, realPosition);
        }
    }


    /**
     * 添加头布局
     *
     * @param view view
     */
    public void addHeaderView(View view) {
        mHeaderViews.put(baseItemTypeHeader++, view);
        notifyDataSetChanged();
    }

    /**
     * 添加脚布局
     *
     * @param view view
     */
    public void addFooterView(View view) {
        mFooterViews.put(baseItemTypeFooter++, view);
        notifyDataSetChanged();
    }

    /**
     * 移除头布局
     *
     * @param view view
     */
    public void removeHeaderView(View view) {
        int index = mHeaderViews.indexOfValue(view);
        if (index < 0) {
            return;
        }
        mHeaderViews.removeAt(index);
        notifyDataSetChanged();
    }

    /**
     * 移除所有头布局
     */
    public void removeAllHeaderView() {
        mHeaderViews.clear();
        notifyDataSetChanged();
    }

    /**
     * 移除脚布局
     *
     * @param view view
     */
    public void removeFooterView(View view) {
        int index = mFooterViews.indexOfValue(view);
        if (index < 0) {
            return;
        }
        mFooterViews.removeAt(index);
        notifyDataSetChanged();
    }

    /**
     * 移除所有脚布局
     */
    public void removeAllFooterView() {
        mFooterViews.clear();
        notifyDataSetChanged();
    }


    public int getHeaderSize() {
        return mHeaderViews.size();
    }

    private RecyclerView.ViewHolder createHeaderAndFooterViewHolder(View view) {
        return new RecyclerView.ViewHolder(view) {
        };
    }

    private boolean isHeaderPosition(int position) {
        return position < getHeadersCount();
    }

    private boolean isFooterPosition(int position) {
        return position >= getHeadersCount() + getRealItemCount();
    }

    private boolean isHeaderType(int viewType) {
        return mHeaderViews.indexOfKey(viewType) >= 0;
    }

    private boolean isFooterType(int viewType) {
        return mFooterViews.indexOfKey(viewType) >= 0;
    }

    private int getRealItemCount() {
        return mRealAdapter.getItemCount();
    }

    public int getHeadersCount() {
        return mHeaderViews.size();
    }

    public int getFootersCount() {
        return mFooterViews.size();
    }

}
