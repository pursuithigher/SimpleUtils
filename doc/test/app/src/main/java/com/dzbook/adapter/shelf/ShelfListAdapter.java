package com.dzbook.adapter.shelf;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.view.ViewGroup;

import com.alibaba.android.vlayout.DelegateAdapter;
import com.alibaba.android.vlayout.LayoutHelper;
import com.alibaba.android.vlayout.layout.LinearLayoutHelper;
import com.dzbook.database.bean.BookInfo;
import com.dzbook.mvp.presenter.MainShelfPresenter;
import com.dzbook.view.shelf.ShelfListFreeView;
import com.dzbook.view.shelf.ShelfListItemView;

/**
 * Adapter
 */
public class ShelfListAdapter extends DelegateAdapter.Adapter<ShelfViewHolder> {

    private Context mContext;
    private Fragment fragment;
    private MainShelfPresenter presenter;

    private BookInfo bookInfo;
    private boolean isShelf = true;

    /**
     * 构造
     *
     * @param context   context
     * @param fragment  fragment
     * @param presenter presenter
     * @param bookInfo  bookInfo
     * @param isShelf   isShelf
     */
    public ShelfListAdapter(Context context, Fragment fragment, MainShelfPresenter presenter, BookInfo bookInfo, boolean isShelf) {
        mContext = context;
        this.fragment = fragment;
        this.presenter = presenter;
        this.bookInfo = bookInfo;
        this.isShelf = isShelf;
    }

    @Override
    public LayoutHelper onCreateLayoutHelper() {
        return new LinearLayoutHelper();
    }

    @Override
    public ShelfViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == ShelfConstant.SHELF_VIEW_TYPE_LIST_ADD) {
            return new ShelfViewHolder(new ShelfListFreeView(mContext, presenter));
        } else if (viewType == ShelfConstant.SHELF_VIEW_TYPE_LIST_COMMON) {
            return new ShelfViewHolder(new ShelfListItemView(mContext, fragment, presenter));
        }
        return null;
    }

    @Override
    public void onBindViewHolder(ShelfViewHolder holder, int position) {
        if (bookInfo != null) {
            holder.bindListBookInfo(bookInfo, isShelf);
        }
    }

    @Override
    public void onViewRecycled(ShelfViewHolder holder) {
        holder.clearListImageView();
        super.onViewRecycled(holder);
    }

    @Override
    public int getItemCount() {
        return 1;
    }

    @Override
    public int getItemViewType(int position) {
        return bookInfo.isAddButton() ? ShelfConstant.SHELF_VIEW_TYPE_LIST_ADD : ShelfConstant.SHELF_VIEW_TYPE_LIST_COMMON;
    }
}
