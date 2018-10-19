package com.dzbook.adapter.shelf;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.dzbook.database.bean.BookInfo;
import com.dzbook.view.shelf.ShelfGridView;
import com.dzbook.view.shelf.ShelfListItemView;
import com.dzbook.view.shelf.ShelfSignInView;

import java.util.List;

import hw.sdk.net.bean.shelf.BeanBookUpdateInfo;

/**
 * ShelfViewHolder
 */
public class ShelfViewHolder extends RecyclerView.ViewHolder {

    private ShelfSignInView shelfSignInView;
    private ShelfGridView shelfGridBookImageView;
    private ShelfListItemView shelfListItemVIew2;

    /**
     * 构造
     *
     * @param itemView itemView
     */
    public ShelfViewHolder(View itemView) {
        super(itemView);
        if (itemView instanceof ShelfSignInView) {
            shelfSignInView = (ShelfSignInView) itemView;
        } else if (itemView instanceof ShelfGridView) {
            shelfGridBookImageView = (ShelfGridView) itemView;
        } else if (itemView instanceof ShelfListItemView) {
            shelfListItemVIew2 = (ShelfListItemView) itemView;
        }
    }

    /**
     * 绑定数据
     *
     * @param updateInfo updateInfo
     */
    public void bindSignInData(BeanBookUpdateInfo updateInfo) {
        if (shelfSignInView != null) {
            shelfSignInView.bindSignInData(updateInfo);
        }
    }

    /**
     * 绑定数据
     *
     * @param list    list
     * @param isShelf isShelf
     */
    public void bindGridBookInfo(List<BookInfo> list, boolean isShelf) {
        if (shelfGridBookImageView != null) {
            shelfGridBookImageView.bindData(list, isShelf);
        }
    }

    /**
     * 绑定数据
     *
     * @param bookInfo bookInfo
     * @param isShelf  isShelf
     */
    public void bindListBookInfo(BookInfo bookInfo, boolean isShelf) {
        if (shelfListItemVIew2 != null) {
            shelfListItemVIew2.bindData(bookInfo, isShelf);
        }
    }

    /**
     * 清除图片
     */
    public void clearGridImageView() {
        if (shelfGridBookImageView != null) {
            shelfGridBookImageView.clearGridImageView();
        }
    }

    /**
     * 清除图片
     */
    public void clearListImageView() {
        if (shelfListItemVIew2 != null) {
            shelfListItemVIew2.clearListImageView();
        }
    }
}
