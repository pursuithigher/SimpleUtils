package com.dzbook.view.shelf;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.RelativeLayout;

import com.dzbook.database.bean.BookInfo;
import com.dzbook.mvp.presenter.MainShelfPresenter;
import com.dzbook.utils.DimensionPixelUtil;
import com.dzbook.view.common.ShelfGridBookImageView;
import com.ishugui.R;

import java.util.ArrayList;
import java.util.List;

import hw.sdk.utils.UiHelper;

/**
 * 网格视图
 *
 * @author gavin
 */
public class ShelfGridView extends RelativeLayout {

    private Context mContext;
    private Fragment fragment;
    private MainShelfPresenter shelfPresenter;
    private int gridItemNum = 3;
    private GridLayout gridLayout;
    private int width = 0;
    private List<ShelfGridBookImageView> imageViews = new ArrayList<>();

    /**
     * 构造
     *
     * @param context context
     */
    public ShelfGridView(Context context) {
        super(context);
    }

    /**
     * 构造
     *
     * @param context        context
     * @param fragment       fragment
     * @param shelfPresenter shelfPresenter
     * @param gridItemNum    gridItemNum
     */
    public ShelfGridView(Context context, Fragment fragment, MainShelfPresenter shelfPresenter, int gridItemNum) {
        super(context);
        mContext = context;
        this.fragment = fragment;
        this.shelfPresenter = shelfPresenter;
        this.gridItemNum = gridItemNum;
        initView();
        initData();
    }

    private void initData() {
        for (int i = 0; i < gridItemNum; i++) {
            ShelfGridBookImageView bookImageView = new ShelfGridBookImageView(mContext, fragment, shelfPresenter);
            imageViews.add(bookImageView);
        }
    }

    private void initView() {
        width = UiHelper.getScreenWidth(mContext) - DimensionPixelUtil.dip2px(mContext, 48 + 21 * (gridItemNum - 1));
        int height = width / gridItemNum * 120 / 90 + DimensionPixelUtil.dip2px(mContext, 35);
        LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, height);
        setLayoutParams(params);
        setGravity(Gravity.CENTER_VERTICAL);
        View view = LayoutInflater.from(mContext).inflate(R.layout.view_shelf_grid, this);
        gridLayout = view.findViewById(R.id.gl_book_shelf);
        gridLayout.setColumnCount(gridItemNum);
    }

    /**
     * 绑定数据
     *
     * @param list    list
     * @param isShelf isShelf
     */
    public void bindData(List<BookInfo> list, boolean isShelf) {
        gridLayout.removeAllViews();
        if (imageViews.size() != 0) {
            final float num = 10.5f;
            int padding = (int) DimensionPixelUtil.dip2px(mContext, num);
            for (int i = 0; i < list.size(); i++) {
                BookInfo bookInfo = list.get(i);
                imageViews.get(i).bindData(bookInfo, isShelf);
                GridLayout.LayoutParams params = new GridLayout.LayoutParams();
                params.setMargins(padding, 0, padding, 0);
                params.width = width / gridItemNum;
                gridLayout.addView(imageViews.get(i), params);
            }
        }
        //        gridLayout.removeAllViews();
        //        for (int i = 0; i < list.size(); i++) {
        ////            ShelfGridBookImageView bookImageView = new ShelfGridBookImageView(mContext, fragment, shelfPresenter);
        //            BookInfo bookInfo = list.get(i);
        //            imageViews.get(i).bindData(bookInfo, isShelf);
        //        }
    }

    public GridLayout getItem() {
        return gridLayout;
    }

    /**
     * 清除图片
     */
    public void clearGridImageView() {
        if (imageViews != null && imageViews.size() > 0) {
            for (int i = 0; i < imageViews.size(); i++) {
                imageViews.get(i).clearGridImageView();
            }
        }
    }
}
