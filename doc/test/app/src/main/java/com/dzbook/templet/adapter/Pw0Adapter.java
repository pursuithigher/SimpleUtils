package com.dzbook.templet.adapter;

import android.view.ViewGroup;

import com.alibaba.android.vlayout.LayoutHelper;
import com.alibaba.android.vlayout.layout.LinearLayoutHelper;
import com.dzbook.view.store.Pw0View;

import hw.sdk.net.bean.store.BeanTempletInfo;

/**
 * Adapter
 *
 * @author dongdianzhou on 2018/2/4.
 */

public class Pw0Adapter extends DzAdapter<MainStoreViewHolder> {


    /**
     * 构造函数
     *
     * @param templetInfo templetInfo
     */
    public Pw0Adapter(BeanTempletInfo templetInfo) {
        super(templetInfo);
    }

    @Override
    public LayoutHelper onCreateLayoutHelper() {
        return new LinearLayoutHelper();
    }

    @Override
    public MainStoreViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MainStoreViewHolder(new Pw0View(parent.getContext()));
    }

    @Override
    public void onBindViewHolder(MainStoreViewHolder holder, int position) {
        holder.bindPw0Data(templetInfo);
    }

    @Override
    public int getItemCount() {
        return 1;
    }

    @Override
    public int getItemViewType(int position) {
        return StoreAdapterConstant.VIEW_TYPE_PW0;
    }
}
