package com.dzbook.adapter.shelf;

import android.content.Context;
import android.view.ViewGroup;

import com.alibaba.android.vlayout.DelegateAdapter;
import com.alibaba.android.vlayout.LayoutHelper;
import com.alibaba.android.vlayout.layout.LinearLayoutHelper;
import com.dzbook.view.shelf.ShelfListEmptyImageView;

/**
 * Adapter
 */
public class ShelfListEmptyAdapter extends DelegateAdapter.Adapter<ShelfViewHolder> {

    private Context mContext;

    /**
     * 构造
     *
     * @param context context
     */
    public ShelfListEmptyAdapter(Context context) {
        mContext = context;
    }

    @Override
    public LayoutHelper onCreateLayoutHelper() {
        return new LinearLayoutHelper();
    }

    @Override
    public ShelfViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ShelfViewHolder(new ShelfListEmptyImageView(mContext));
    }

    @Override
    public void onBindViewHolder(ShelfViewHolder holder, int position) {
    }

    @Override
    public int getItemCount() {
        return 1;
    }

    @Override
    public int getItemViewType(int position) {
        return ShelfConstant.SHELF_VIEW_TYPE_LIST_EMPTY;
    }
}
