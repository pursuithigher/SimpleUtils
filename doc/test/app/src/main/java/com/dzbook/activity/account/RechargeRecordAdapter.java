package com.dzbook.activity.account;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.dzbook.view.person.RechargeRecordView;

import java.util.ArrayList;
import java.util.List;

import hw.sdk.net.bean.record.RechargeRecordBean;

/**
 * 充值记录adapter
 *
 * @author dongdianzhou on 2017/4/6.
 */
public class RechargeRecordAdapter extends RecyclerView.Adapter<RechargeRecordAdapter.RechargeRecordViewHolder> {

    private List<RechargeRecordBean> adapterList = null;

    RechargeRecordAdapter() {
        adapterList = new ArrayList<>();
    }

    /**
     * 追加新数据
     *
     * @param list  数据list
     * @param clear 是否清除数据
     */
    public void append(List<RechargeRecordBean> list, boolean clear) {
        if (clear) {
            adapterList.clear();
        }
        if (list != null && list.size() > 0) {
            adapterList.addAll(list);
        }
        notifyDataSetChanged();
    }

    public List<RechargeRecordBean> getDataList() {
        return adapterList;
    }

    @Override
    public RechargeRecordViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewtype) {
        return new RechargeRecordViewHolder(new RechargeRecordView(viewGroup.getContext()));
    }

    @Override
    public void onBindViewHolder(RechargeRecordViewHolder rechargeRecordViewHolder, int position) {
        if (adapterList != null && position < adapterList.size()) {
            RechargeRecordBean recordBeanInfo = adapterList.get(position);
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
     * holder
     */
    class RechargeRecordViewHolder extends RecyclerView.ViewHolder {

        private RechargeRecordView rechargeRecordView;

        RechargeRecordViewHolder(View itemView) {
            super(itemView);
            rechargeRecordView = (RechargeRecordView) itemView;
        }

        public void bindData(RechargeRecordBean recordBeanInfo, boolean isShowEndLine) {
            rechargeRecordView.bindData(recordBeanInfo, isShowEndLine);
        }
    }
}
