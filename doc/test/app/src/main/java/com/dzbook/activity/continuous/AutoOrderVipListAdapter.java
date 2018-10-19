package com.dzbook.activity.continuous;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.dzbook.view.vip.AutoOrderVipView;

import java.util.ArrayList;
import java.util.List;

import hw.sdk.net.bean.vip.VipContinueOpenHisBean;

/**
 * 会员历史记录
 *
 * @author KongXP on 2018/4/23.
 */
public class AutoOrderVipListAdapter extends RecyclerView.Adapter<AutoOrderVipListAdapter.AutoOrderVipListViewHolder> {

    private List<VipContinueOpenHisBean> adapterList;

    /**
     * 构造
     */
    public AutoOrderVipListAdapter() {
        adapterList = new ArrayList<>();
    }

    /**
     * 追加新数据
     *
     * @param list  数据list
     * @param clear clear
     */
    public void append(List<VipContinueOpenHisBean> list, boolean clear) {
        if (clear) {
            adapterList.clear();
        }
        if (list != null && list.size() > 0) {
            adapterList.addAll(list);
        }
        notifyDataSetChanged();
    }

    public List<VipContinueOpenHisBean> getDataList() {
        return adapterList;
    }

    @Override
    public AutoOrderVipListViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewtype) {
        return new AutoOrderVipListViewHolder(new AutoOrderVipView(viewGroup.getContext()));
    }

    @Override
    public void onBindViewHolder(AutoOrderVipListViewHolder autoOrderVipListViewHolder, int position) {
        if (adapterList != null && position < adapterList.size()) {
            VipContinueOpenHisBean recordBeanInfo = adapterList.get(position);
            if (recordBeanInfo != null) {
                autoOrderVipListViewHolder.bindData(recordBeanInfo);
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
     * holder
     */
    class AutoOrderVipListViewHolder extends RecyclerView.ViewHolder {

        private AutoOrderVipView autoOrderVipListView;

        public AutoOrderVipListViewHolder(View itemView) {
            super(itemView);
            autoOrderVipListView = (AutoOrderVipView) itemView;
        }

        public void bindData(VipContinueOpenHisBean recordBeanInfo) {
            autoOrderVipListView.bindData(recordBeanInfo);
        }
    }
}
