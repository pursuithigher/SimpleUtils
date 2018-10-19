package com.dzbook.adapter;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;

import com.dzbook.activity.detail.BookDetailActivity;
import com.dzbook.mvp.presenter.SearchPresenterImpl;
import com.dzbook.utils.ThirdPartyLog;
import com.dzbook.view.common.BookListItemView;

import java.util.ArrayList;
import java.util.List;

import hw.sdk.net.bean.BeanBookInfo;

/**
 * 搜索页面adapter的真实实现类
 *
 * @author wangwenzhou on 2017/1/12.
 */

public class BookstoreSearchRecycleViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final String TAG = "BookstoreSearchAdapter";
    /**
     * 上下文
     */
    private Activity mContext;

    /**
     * 搜索结果适配器
     */
    private List<BeanBookInfo> searchBeanInfos;

    private SearchPresenterImpl searchPresenter;

    /**
     * 构造
     *
     * @param context context
     */
    public BookstoreSearchRecycleViewAdapter(Activity context) {
        this.mContext = context;
        this.searchBeanInfos = new ArrayList<BeanBookInfo>();
    }

    public void setSearchPresenter(SearchPresenterImpl searchPresenter) {
        this.searchPresenter = searchPresenter;
    }

    /**
     * 添加数据
     *
     * @param list  list
     * @param clear clear
     */
    public void addItem(List<BeanBookInfo> list, boolean clear) {
        if (clear) {
            searchBeanInfos.clear();
        }
        if (list != null && list.size() > 0) {
            for (int i = 0; i < list.size(); i++) {
                searchBeanInfos.add(list.get(i));
            }
        }
        notifyDataSetChanged();
    }

    @Override
    public void onViewRecycled(RecyclerView.ViewHolder holder) {
        if (holder != null && holder instanceof ViewHolder) {
            ((ViewHolder) holder).clearImageView();
        }
        super.onViewRecycled(holder);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        BookListItemView view = new BookListItemView(mContext, BookListItemView.RANK_TOP);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        BeanBookInfo bean = this.searchBeanInfos.get(position);
        viewHolder.itemView.setTag(bean);
        String searchKey = "";
        if (null != searchPresenter) {
            searchKey = searchPresenter.getSearchKey();
        }
        ((ViewHolder) viewHolder).bindData(searchKey, bean, position, false, getItemCount());
    }

    @Override
    public int getItemCount() {
        return searchBeanInfos.size();
    }

    /**
     * ViewHolder
     */
    public class ViewHolder extends RecyclerView.ViewHolder {

        private BookListItemView view;

        /**
         * ViewHolder
         *
         * @param itemView itemView
         */
        public ViewHolder(View itemView) {
            super(itemView);
            this.view = (BookListItemView) itemView;
        }

        /**
         * 绑定数据
         *
         * @param searchKey       searchKey
         * @param bean            bean
         * @param position        position
         * @param isNeedOrderText isNeedOrderText
         * @param itemCount       itemCount
         */
        public void bindData(String searchKey, final BeanBookInfo bean, final int position, boolean isNeedOrderText, int itemCount) {
            if (bean != null) {
                this.view.bindData(searchKey, bean, position, isNeedOrderText, itemCount);
                this.view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ThirdPartyLog.onEventValueOldClick(mContext, ThirdPartyLog.SEACH_PAGE_RESULT_ID, null, 1);
                        if (bean != null && !TextUtils.isEmpty(bean.bookId)) {
                            if (searchPresenter != null) {
                                searchPresenter.addResultClickLog(bean.bookId, bean.index);
                            }
                            BookDetailActivity.launch(mContext, bean.bookId, bean.bookName);
                        }
                    }
                });
            }
        }

        /**
         * 清除图片
         */
        public void clearImageView() {
            if (this.view != null) {
                this.view.clearImageView();
            }
        }
    }

}
