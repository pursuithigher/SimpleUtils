package com.dzbook.templet.adapter;

import android.view.ViewGroup;

import com.alibaba.android.vlayout.LayoutHelper;
import com.alibaba.android.vlayout.layout.LinearLayoutHelper;
import com.dzbook.mvp.presenter.TempletPresenter;
import com.dzbook.view.store.Tm0View;

import hw.sdk.net.bean.store.BeanTempletInfo;

/**
 * Adapter
 *
 * @author dongdianzhou on 2018/2/4.
 */

public class Tm0Adapter extends DzAdapter<MainStoreViewHolder> {

    private TempletPresenter mPresenter;

    /**
     * 构造函数
     *
     * @param templetInfo      templetInfo
     * @param templetPresenter templetPresenter
     */
    public Tm0Adapter(BeanTempletInfo templetInfo, TempletPresenter templetPresenter) {
        super(templetInfo);
        mPresenter = templetPresenter;
    }

    @Override
    public LayoutHelper onCreateLayoutHelper() {
        return new LinearLayoutHelper();
    }

    @Override
    public MainStoreViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MainStoreViewHolder(new Tm0View(parent.getContext(), mPresenter));
    }

    @Override
    public void onBindViewHolder(MainStoreViewHolder holder, int position) {
        holder.bindTm0Data(templetInfo);
    }

    @Override
    public int getItemCount() {
        return 1;
    }

    @Override
    public int getItemViewType(int position) {
        return StoreAdapterConstant.VIEW_TYPE_TM0;
    }
}
