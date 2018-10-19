package com.dzbook.activity.person;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.dzbook.mvp.presenter.PersonCloudShelfPresenter;
import com.dzbook.view.person.CloudShelfView;

import java.util.ArrayList;
import java.util.List;

import hw.sdk.net.bean.BeanBookInfo;

/**
 * 云书架 adapter
 *
 * @author dongdianzhou on 2017/11/20.
 */
public class CloudShelfAdapter extends RecyclerView.Adapter<CloudShelfAdapter.CloudShelfViewHolder> {

    private List<BeanBookInfo> list;

    private PersonCloudShelfPresenter mPresenter;

    /**
     * 构造
     */
    public CloudShelfAdapter() {
        list = new ArrayList<>();
    }

    public void setPersonCloudShelfPresenter(PersonCloudShelfPresenter presenter) {
        this.mPresenter = presenter;
    }

    /**
     * 添加云书架数据
     *
     * @param data    数据
     * @param isClear 删除前，是否清空
     */
    public void addItems(ArrayList<BeanBookInfo> data, boolean isClear) {
        if (isClear) {
            this.list.clear();
        }
        this.list.addAll(data);
        notifyDataSetChanged();
    }

    @Override
    public CloudShelfViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new CloudShelfViewHolder(new CloudShelfView(parent.getContext()));
    }

    @Override
    public void onBindViewHolder(CloudShelfViewHolder holder, int position) {
        if (position < list.size()) {
            BeanBookInfo bean = list.get(position);
            if (bean != null) {
                holder.bindData(bean, position == list.size() - 1);
            }
        }
    }

    @Override
    public void onViewRecycled(CloudShelfViewHolder holder) {
        holder.clearImageView();
        super.onViewRecycled(holder);
    }

    @Override
    public int getItemCount() {
        return this.list.size();
    }

    /**
     * 返回最后一条数据的阅读时间，用于服务端分页
     *
     * @return string
     */
    public String getLastItemTime() {
        if (list != null && list.size() > 0) {
            BeanBookInfo bookInfo = list.get(list.size() - 1);
            if (bookInfo != null) {
                return String.valueOf(bookInfo.timeTips);
            }
        }
        return "";
    }

    /**
     * 删除一本书
     *
     * @param beanBookInfo beanBookInfo
     * @return 删除后的list size
     */
    public int deleteDataFromAdapter(BeanBookInfo beanBookInfo) {
        if (list != null && list.size() > 0) {
            for (int i = 0; i < list.size(); i++) {
                BeanBookInfo bookInfo = list.get(i);
                if (bookInfo != null && bookInfo.bookId.equals(beanBookInfo.bookId)) {
                    list.remove(bookInfo);
                    notifyDataSetChanged();
                }
            }
            return list.size();
        }
        return 0;
    }

    /**
     * holder
     */
    class CloudShelfViewHolder extends RecyclerView.ViewHolder {

        private CloudShelfView mShelfView;

        public CloudShelfViewHolder(View itemView) {
            super(itemView);
            mShelfView = (CloudShelfView) itemView;
        }

        public void bindData(BeanBookInfo bean, boolean isShowEndLine) {
            mShelfView.setPersonCloudShelfPresenter(mPresenter);
            mShelfView.bindData(bean, isShowEndLine);
        }

        public void clearImageView() {
            mShelfView.clearImageView();
        }
    }
}
