package com.dzbook.fragment;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.dzbook.view.person.GiftListViewItem;

import java.util.ArrayList;
import java.util.List;

import hw.sdk.net.bean.gift.GiftListBean;

/**
 * 礼物列表Adapter
 *
 * @author KongXP on 2018/4/25.
 */

public class GiftListAdapter extends RecyclerView.Adapter<GiftListAdapter.RechargeRecordViewHolder> {

    private List<GiftListBean> adapterList = null;

    /**
     * 构造
     */
    public GiftListAdapter() {
        adapterList = new ArrayList<>();
    }

    /**
     * 追加新数据
     *
     * @param list  数据list
     * @param clear 是否清空数据
     */
    public void append(List<GiftListBean> list, boolean clear) {
        if (clear) {
            adapterList.clear();
        }
        if (list != null && list.size() > 0) {
            adapterList.addAll(list);
        }
        notifyDataSetChanged();
    }

    public List<GiftListBean> getDataList() {
        return adapterList;
    }

    @Override
    public RechargeRecordViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewtype) {
        return new RechargeRecordViewHolder(new GiftListViewItem(viewGroup.getContext()));
    }

    @Override
    public void onBindViewHolder(RechargeRecordViewHolder rechargeRecordViewHolder, int position) {
        if (adapterList != null && position < adapterList.size()) {
            GiftListBean recordBeanInfo = adapterList.get(position);
            if (recordBeanInfo != null) {
                rechargeRecordViewHolder.bindData(recordBeanInfo, position == adapterList.size() - 1);
            }
        }
    }

    @Override
    public int getItemCount() {
        if (adapterList != null) {
            return adapterList.size();
        }
        return 0;
    }

    /**
     * 清除数据
     */
    public void clearData() {
        if (adapterList != null) {
            adapterList.clear();
        }
    }

    /**
     * 充值记录ViewHolder
     */
    class RechargeRecordViewHolder extends RecyclerView.ViewHolder {

        private GiftListViewItem rechargeRecordView;

        public RechargeRecordViewHolder(View itemView) {
            super(itemView);
            rechargeRecordView = (GiftListViewItem) itemView;
        }

        public void bindData(GiftListBean recordBeanInfo, boolean isShowEndLine) {
            rechargeRecordView.bindData(recordBeanInfo, isShowEndLine);
        }
    }
}
