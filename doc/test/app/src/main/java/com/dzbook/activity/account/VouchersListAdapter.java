package com.dzbook.activity.account;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.dzbook.view.person.VouchersListView;

import java.util.ArrayList;
import java.util.List;

import hw.sdk.net.bean.vouchers.VouchersListBean;

/**
 * 代金券 adapter
 *
 * @author dongdianzhou on 2017/4/6.
 */
public class VouchersListAdapter extends RecyclerView.Adapter<VouchersListAdapter.VouchersViewHolder> {

    private List<VouchersListBean> adapterList = null;

    VouchersListAdapter() {
        adapterList = new ArrayList<>();
    }

    /**
     * 追加新数据
     *
     * @param list  数据list
     * @param clear 是否清除数据
     */
    public void append(List<VouchersListBean> list, boolean clear) {
        if (clear) {
            adapterList.clear();
        }
        if (list != null && list.size() > 0) {
            adapterList.addAll(list);
        }
        notifyDataSetChanged();
    }

    public List<VouchersListBean> getDataList() {
        return adapterList;
    }

    @Override
    public VouchersViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewtype) {
        return new VouchersViewHolder(new VouchersListView(viewGroup.getContext()));
    }

    @Override
    public void onBindViewHolder(VouchersViewHolder vouchersViewHolder, int position) {
        if (adapterList != null && position < adapterList.size()) {
            VouchersListBean recordBeanInfo = adapterList.get(position);
            if (recordBeanInfo != null) {
                vouchersViewHolder.bindData(recordBeanInfo);
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
    class VouchersViewHolder extends RecyclerView.ViewHolder {

        private VouchersListView vouchersListView;

        VouchersViewHolder(View itemView) {
            super(itemView);
            vouchersListView = (VouchersListView) itemView;
        }

        public void bindData(VouchersListBean recordBeanInfo) {
            vouchersListView.bindData(recordBeanInfo);
        }
    }
}
