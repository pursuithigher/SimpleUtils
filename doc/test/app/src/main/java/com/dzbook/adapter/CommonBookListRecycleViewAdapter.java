package com.dzbook.adapter;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.dzbook.view.common.BookListItemView;

import java.util.ArrayList;
import java.util.List;

import hw.sdk.net.bean.BeanBookInfo;

/**
 * CommonBookListRecycleViewAdapter
 *
 * @author lizz 2018-03-09
 */
public class CommonBookListRecycleViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final String TAG = "CommonBookListRecycleViewAdapter";

    private Activity context;

    private ArrayList<BeanBookInfo> llistBookBean;

    private OnItemClickListener mOnItemClickLitener;

    private boolean isNeedOrderText;

    /**
     * 构造
     *
     * @param context         context
     * @param isNeedOrderText isNeedOrderText
     */
    public CommonBookListRecycleViewAdapter(Activity context, boolean isNeedOrderText) {
        this.context = context;
        this.isNeedOrderText = isNeedOrderText;
        this.llistBookBean = new ArrayList<BeanBookInfo>();
    }

    /**
     * 添加数据
     *
     * @param beanList beanList
     * @param clear    clear
     */
    public void addNetBeanItem(List<BeanBookInfo> beanList, boolean clear) {
        if (clear) {
            llistBookBean.clear();
        }
        if (beanList != null && beanList.size() > 0) {
            for (int i = 0; i < beanList.size(); i++) {
                llistBookBean.add(beanList.get(i));
            }
        }
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return llistBookBean.size();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {

        BookListItemView view = new BookListItemView(context, BookListItemView.RANK_TOP);
        return new BookListItemViewViewHolder(view);
    }

    @Override
    public void onViewRecycled(RecyclerView.ViewHolder holder) {
        if (holder != null && holder instanceof BookListItemViewViewHolder) {
            BookListItemViewViewHolder viewViewHolder = (BookListItemViewViewHolder) holder;
            viewViewHolder.clearImageView();
        }
        super.onViewRecycled(holder);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        BeanBookInfo bean = llistBookBean.get(position);
        viewHolder.itemView.setTag(bean);
        ((BookListItemViewViewHolder) viewHolder).bindData(bean, position, isNeedOrderText, getItemCount());
    }

    /**
     * ViewHolder
     */
    public class BookListItemViewViewHolder extends RecyclerView.ViewHolder {

        private BookListItemView view;

        /**
         * ViewHolder构造
         *
         * @param view view
         */
        public BookListItemViewViewHolder(View view) {
            super(view);
            this.view = (BookListItemView) view;
        }

        /**
         * 绑定数据
         *
         * @param bean      bean
         * @param position  position
         * @param isNeed    isNeed
         * @param itemCount itemCount
         */
        public void bindData(BeanBookInfo bean, final int position, boolean isNeed, int itemCount) {
            if (bean != null) {
                this.view.bindData(bean, position, isNeed, itemCount);
                this.view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mOnItemClickLitener != null) {
                            mOnItemClickLitener.onItemClick(v, (BeanBookInfo) v.getTag(), position);
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

    /**
     * 自定义item点击事件回调
     */
    public interface OnItemClickListener {
        /**
         * 点击事件
         *
         * @param view     view
         * @param bean     bean
         * @param position position
         */
        void onItemClick(View view, BeanBookInfo bean, int position);
    }

    public void setOnItemClickListener(OnItemClickListener clickListener) {
        this.mOnItemClickLitener = clickListener;
    }
}
