package com.dzbook.templet.adapter;

import android.view.ViewGroup;

import com.alibaba.android.vlayout.LayoutHelper;
import com.alibaba.android.vlayout.layout.LinearLayoutHelper;
import com.dzbook.mvp.presenter.TempletPresenter;
import com.dzbook.view.store.VipStoreTopView;

import hw.sdk.net.bean.store.BeanTempletInfo;

/**
 * Adapter
 *
 * @author dongdianzhou on 2018/2/4.
 */

public class Vpt0Adapter extends DzAdapter<MainStoreViewHolder> {

    private TempletPresenter templetPresenter;

    private boolean isUseLocalData;

    /**
     * 构造函数
     *
     * @param templetInfo      templetInfo
     * @param templetPresenter templetPresenter
     */
    public Vpt0Adapter(BeanTempletInfo templetInfo, TempletPresenter templetPresenter) {
        super(templetInfo);
        this.templetPresenter = templetPresenter;
        isUseLocalData = false;
    }

    public void setUseLocalData(boolean isUseLocalData1) {
        this.isUseLocalData = isUseLocalData1;
    }

    @Override
    public LayoutHelper onCreateLayoutHelper() {
        return new LinearLayoutHelper();
    }

    @Override
    public MainStoreViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MainStoreViewHolder(new VipStoreTopView(parent.getContext(), templetPresenter));
    }

    @Override
    public void onBindViewHolder(MainStoreViewHolder holder, int position) {
        holder.bindVpt0Data(templetInfo, isUseLocalData);
    }

    @Override
    public int getItemCount() {
        return 1;
    }

    @Override
    public int getItemViewType(int position) {
        return StoreAdapterConstant.VIEW_TYPE_VPT0;
    }
}
