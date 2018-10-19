package com.dzbook.vip.adapter;

import android.content.Context;
import android.view.ViewGroup;

import com.alibaba.android.vlayout.DelegateAdapter;
import com.alibaba.android.vlayout.LayoutHelper;
import com.alibaba.android.vlayout.layout.LinearLayoutHelper;
import com.dzbook.view.vip.MoreBooksVipView;

import hw.sdk.net.bean.vip.VipBookInfo;

/**
 * VipMoreAdapter
 *
 * @author gavin
 */
public class VipMoreAdapter extends DelegateAdapter.Adapter<VipViewHolder> {

    private Context mContext;
    private VipBookInfo.TitleBean titleBean;
    private int pos;

    /**
     * 构造
     *
     * @param context   context
     * @param titleBean titleBean
     * @param position  position
     */
    public VipMoreAdapter(Context context, VipBookInfo.TitleBean titleBean, int position) {
        mContext = context;
        this.titleBean = titleBean;
        pos = position;
    }

    @Override
    public LayoutHelper onCreateLayoutHelper() {
        return new LinearLayoutHelper();
    }

    @Override
    public VipViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new VipViewHolder(new MoreBooksVipView(mContext, pos));
    }

    @Override
    public void onBindViewHolder(VipViewHolder holder, int position) {
        holder.bindMoreBTitle(titleBean);
    }

    @Override
    public int getItemCount() {
        return 1;
    }

}
