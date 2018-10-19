package com.dzbook.templet.adapter;

import android.view.ViewGroup;

import com.alibaba.android.vlayout.LayoutHelper;
import com.alibaba.android.vlayout.layout.LinearLayoutHelper;
import com.dzbook.view.PageView.PageState;
import com.dzbook.view.store.LD0View;

import hw.sdk.net.bean.store.BeanTempletInfo;

/**
 * Adapter
 *
 * @author dongdianzhou on 2018/2/4.
 */

public class Ld0Adapter extends DzAdapter<MainStoreViewHolder> {
    private PageState mState = PageState.Loadable;

    /**
     * 构造函数
     *
     * @param templetInfo templetInfo
     */
    public Ld0Adapter(BeanTempletInfo templetInfo) {
        super(templetInfo);
    }

    public void setState(PageState state) {
        mState = state;
    }

    @Override
    public LayoutHelper onCreateLayoutHelper() {
        return new LinearLayoutHelper();
    }

    @Override
    public MainStoreViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MainStoreViewHolder(new LD0View(parent.getContext()));
    }

    @Override
    public void onBindViewHolder(MainStoreViewHolder holder, int position) {
        holder.bindLd0Data(mState);
    }

    @Override
    public int getItemCount() {
        return 1;
    }

    @Override
    public int getItemViewType(int position) {
        return StoreAdapterConstant.VIEW_TYPE_LD0;
    }
}
