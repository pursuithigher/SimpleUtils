package com.dzbook.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.dzbook.view.CenterItemView;

import java.util.ArrayList;
import java.util.List;

import hw.sdk.net.bean.ActivityCenterBean;

/**
 * 活动中心
 *
 * @author gavin
 */
public class ActivityCenterAdapter extends RecyclerView.Adapter<ActivityCenterAdapter.CenterViewHolder> {

    private List<ActivityCenterBean.CenterInfoBean> list;


    /**
     * 构造函数
     */
    public ActivityCenterAdapter() {
        list = new ArrayList<>();
    }

    /**
     * 添加数据
     *
     * @param data item数据
     */
    public void addItems(ArrayList<ActivityCenterBean.CenterInfoBean> data) {
        this.list.addAll(data);
        notifyDataSetChanged();
    }

    @Override
    public CenterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new CenterViewHolder(new CenterItemView(parent.getContext()));
    }

    @Override
    public void onBindViewHolder(CenterViewHolder holder, int position) {
        if (position < list.size()) {
            ActivityCenterBean.CenterInfoBean bean = list.get(position);
            if (bean != null) {
                holder.bindData(bean);
            }
        }
    }

    @Override
    public int getItemCount() {
        return this.list.size();
    }


    /**
     * holder
     */
    class CenterViewHolder extends RecyclerView.ViewHolder {

        private CenterItemView mCenterView;

        CenterViewHolder(View itemView) {
            super(itemView);
            mCenterView = (CenterItemView) itemView;
        }

        public void bindData(ActivityCenterBean.CenterInfoBean bean) {
            mCenterView.bindData(bean);
        }
    }
}
