package com.dzbook.templet.adapter;

import android.support.v7.widget.RecyclerView;

import com.alibaba.android.vlayout.DelegateAdapter;

import hw.sdk.net.bean.store.BeanTempletInfo;

/**
 * Adapter
 *
 * @param <VH> <VH>
 * @author dongdianzhou on 2018/3/15.
 */

public abstract class DzAdapter<VH extends RecyclerView.ViewHolder> extends DelegateAdapter.Adapter<VH> {

    BeanTempletInfo templetInfo;

    /**
     * 构造函数
     *
     * @param templetInfo templetInfo
     */
    public DzAdapter(BeanTempletInfo templetInfo) {
        this.templetInfo = templetInfo;
    }

    public BeanTempletInfo getTempletInfo() {
        return templetInfo;
    }

    public void setTempletInfo(BeanTempletInfo templetInfo) {
        this.templetInfo = templetInfo;
    }
}
