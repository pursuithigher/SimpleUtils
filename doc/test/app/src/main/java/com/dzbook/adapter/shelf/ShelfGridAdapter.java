package com.dzbook.adapter.shelf;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.view.ViewGroup;

import com.alibaba.android.vlayout.DelegateAdapter;
import com.alibaba.android.vlayout.LayoutHelper;
import com.alibaba.android.vlayout.layout.LinearLayoutHelper;
import com.dzbook.database.bean.BookInfo;
import com.dzbook.mvp.presenter.MainShelfPresenter;
import com.dzbook.view.shelf.ShelfGridView;

import java.util.List;

/**
 * ShelfGridAdapter
 *
 * @author gavin
 */
public class ShelfGridAdapter extends DelegateAdapter.Adapter<ShelfViewHolder> {

    private Context mContext;
    private Fragment fragment;
    private MainShelfPresenter shelfPresenter;

    private List<BookInfo> list;
    private boolean isShelf;

    private int gridItemNum = 3;

    /**
     * 构造
     *
     * @param context        context
     * @param list           list
     * @param fragment       fragment
     * @param shelfPresenter shelfPresenter
     * @param isShelf        isShelf
     * @param gridItemNum    gridItemNum
     */
    public ShelfGridAdapter(Context context, List<BookInfo> list, Fragment fragment, MainShelfPresenter shelfPresenter, boolean isShelf, int gridItemNum) {
        mContext = context;
        this.fragment = fragment;
        this.shelfPresenter = shelfPresenter;
        this.list = list;
        this.isShelf = isShelf;
        this.gridItemNum = gridItemNum;
    }

    @Override
    public LayoutHelper onCreateLayoutHelper() {
        return new LinearLayoutHelper();
    }

    @Override
    public ShelfViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ShelfViewHolder(new ShelfGridView(mContext, fragment, shelfPresenter, gridItemNum));
    }

    @Override
    public void onViewRecycled(ShelfViewHolder holder) {
        holder.clearGridImageView();
        super.onViewRecycled(holder);
    }

    @Override
    public void onBindViewHolder(ShelfViewHolder holder, int position) {
        if (list != null) {
            holder.bindGridBookInfo(list, isShelf);
        }
    }

    @Override
    public int getItemCount() {
        return 1;
    }

    @Override
    public int getItemViewType(int position) {
        return ShelfConstant.SHELF_VIEW_TYPE_GRID;
    }
}
