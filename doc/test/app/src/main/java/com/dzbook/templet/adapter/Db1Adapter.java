package com.dzbook.templet.adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.view.ViewGroup;

import com.alibaba.android.vlayout.LayoutHelper;
import com.alibaba.android.vlayout.layout.LinearLayoutHelper;
import com.dzbook.mvp.presenter.TempletPresenter;
import com.dzbook.view.store.Db1View;

import hw.sdk.net.bean.store.BeanTempletInfo;

/**
 * Adapter
 *
 * @author dongdianzhou on 2018/2/4.
 */

public class Db1Adapter extends DzAdapter<MainStoreViewHolder> {

    private Fragment mFragment;
    private TempletPresenter mPresenter;

    private int templetPosition;

    /**
     * 构造函数
     *
     * @param templetInfo      templetInfo
     * @param fragment         fragment
     * @param templetPresenter templetPresenter
     * @param context          context
     * @param templetPosition  templetPosition
     */
    public Db1Adapter(BeanTempletInfo templetInfo, Fragment fragment, TempletPresenter templetPresenter, Context context, int templetPosition) {
        super(templetInfo);
        mFragment = fragment;
        mPresenter = templetPresenter;
        this.templetPosition = templetPosition;
    }

    @Override
    public LayoutHelper onCreateLayoutHelper() {
        LinearLayoutHelper layoutHelper = new LinearLayoutHelper();
        //        int paddingLeft = DimensionPixelUtil.dip2px(mContext, 16);
        //        int paddingTop = DimensionPixelUtil.dip2px(mContext, 8);
        //        int paddingBottom = DimensionPixelUtil.dip2px(mContext, 5);
        //        layoutHelper.setPadding(paddingLeft, 0, paddingLeft, 0);
        return layoutHelper;
    }

    @Override
    public void onViewRecycled(MainStoreViewHolder holder) {
        holder.clearDb1ImageView();
        super.onViewRecycled(holder);
    }

    @Override
    public MainStoreViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MainStoreViewHolder(new Db1View(parent.getContext(), mFragment, mPresenter));
    }

    @Override
    public void onBindViewHolder(MainStoreViewHolder holder, int position) {
        holder.bindDb1Data(templetInfo, templetPosition);
    }

    @Override
    public int getItemCount() {
        return 1;
    }

    @Override
    public int getItemViewType(int position) {
        return StoreAdapterConstant.VIEW_TYPE_DB1;
    }
}
