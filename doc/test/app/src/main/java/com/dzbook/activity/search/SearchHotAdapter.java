package com.dzbook.activity.search;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.dzbook.mvp.presenter.SearchPresenterImpl;
import com.dzbook.view.search.SearchHotItemView;

import java.util.ArrayList;
import java.util.List;

import hw.sdk.net.bean.seach.BeanKeywordHotVo;

/**
 * 热门搜索adapter
 *
 * @author dongdianzhou on 2017/9/7.
 */
public class SearchHotAdapter extends RecyclerView.Adapter<SearchHotAdapter.SearchHotViewHolder> {

    private List<BeanKeywordHotVo> list;

    private SearchPresenterImpl mSearchPresenter;

    private boolean isNeedExchange;

    /**
     * 构造
     */
    public SearchHotAdapter() {
        list = new ArrayList<>();
    }

    /**
     * 设置 presenter
     *
     * @param searchPresenter searchPresenter
     * @param needExchange    isNeedExchange
     */
    public void setSearchPresenter(SearchPresenterImpl searchPresenter, boolean needExchange) {
        this.mSearchPresenter = searchPresenter;
        this.isNeedExchange = needExchange;
    }

    /**
     * 添加数据
     *
     * @param data 数据
     */
    public void addItems(List<BeanKeywordHotVo> data) {
        this.list.clear();
        this.list.addAll(data);
        notifyDataSetChanged();
    }

    @Override
    public SearchHotViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new SearchHotViewHolder(new SearchHotItemView(parent.getContext()));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    @Override
    public void onBindViewHolder(SearchHotViewHolder holder, int position) {
        if (position < list.size()) {
            BeanKeywordHotVo bean = list.get(position);
            if (bean != null) {
                holder.bindData(bean, position);
            }
        }
    }

    /**
     * holder
     */
    class SearchHotViewHolder extends RecyclerView.ViewHolder {

        private SearchHotItemView itemView;

        public SearchHotViewHolder(View itemView) {
            super(itemView);
            this.itemView = (SearchHotItemView) itemView;
        }

        public void bindData(BeanKeywordHotVo bean, int position) {
            if (itemView != null) {
                itemView.setSearchPresenter(mSearchPresenter);
                itemView.bindData(bean, position, isNeedExchange);
            }
        }
    }
}
