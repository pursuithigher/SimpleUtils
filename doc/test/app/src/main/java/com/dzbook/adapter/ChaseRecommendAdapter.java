package com.dzbook.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.dzbook.activity.CenterDetailActivity;
import com.dzbook.activity.reader.ChaseRecommendMoreActivity;
import com.dzbook.view.BookLinearLayout;
import com.dzbook.view.ChaseRecommendTopView;

import java.util.ArrayList;
import java.util.List;

import hw.sdk.net.bean.reader.BeanBookRecomment;
import hw.sdk.net.bean.reader.BeanRecommentBookInfo;

/**
 * ChaseRecommendAdapter
 *
 * @author lizhongzhong 2018/3/9.
 */

public class ChaseRecommendAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<Item> items;

    private Context mContext;

    private String bookId;

    /**
     * 构造
     *
     * @param mContext mContext
     */
    public ChaseRecommendAdapter(Context mContext) {
        this.mContext = mContext;
        items = new ArrayList<>();
    }

    /**
     * 添加数据
     *
     * @param id       bookId
     * @param beanList beanList
     * @param beanInfo beanInfo
     * @param clear    clear
     */
    public void addItemData(String id, List<BeanRecommentBookInfo> beanList, BeanBookRecomment beanInfo, boolean clear) {
        if (clear) {
            items.clear();
        }
        this.bookId = id;

        if (beanList != null && beanList.size() > 0) {
            //第一行数据展示 顶部
            items.add(new Item(Item.TYPE_TOP_ITEM, null, beanInfo));

            for (int i = 0; i < beanList.size(); i++) {
                items.add(new Item(Item.TYPE_BOOK_ITEM, beanList.get(i), null));
            }
        }
        notifyDataSetChanged();

    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        if (viewType == Item.TYPE_BOOK_ITEM) {
            BookLinearLayout bookLinearLayout = new BookLinearLayout(mContext);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            bookLinearLayout.setLayoutParams(layoutParams);
            return new ChaseRecommendItemViewHolder(bookLinearLayout);

        } else if (viewType == Item.TYPE_TOP_ITEM) {
            View view = new ChaseRecommendTopView(mContext);
            return new ChaseRecommendTopViewHolder(view);
        }

        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        int type = getItemViewType(position);
        if (type == Item.TYPE_BOOK_ITEM) {
            if (holder instanceof ChaseRecommendItemViewHolder) {
                BeanRecommentBookInfo item = items.get(position).bean;
                ((ChaseRecommendItemViewHolder) holder).bindData(item);
            }
        } else if (type == Item.TYPE_TOP_ITEM) {
            if (holder instanceof ChaseRecommendTopViewHolder) {
                BeanBookRecomment item = items.get(position).beanInfo;
                ((ChaseRecommendTopViewHolder) holder).bindData(item);
            }
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    /**
     * ViewHolder
     */
    public class ChaseRecommendItemViewHolder extends RecyclerView.ViewHolder {

        private BookLinearLayout itemView;

        /**
         * 构造
         *
         * @param itemView itemView
         */
        public ChaseRecommendItemViewHolder(View itemView) {
            super(itemView);
            this.itemView = (BookLinearLayout) itemView;
        }

        /**
         * 绑定数据
         *
         * @param bean bean
         */
        public void bindData(final BeanRecommentBookInfo bean) {
            if (bean.moreType == 1) {
                itemView.setOrientation(LinearLayout.VERTICAL);
            } else {
                itemView.setOrientation(LinearLayout.HORIZONTAL);
            }
            itemView.setTextLeft(bean.name);
            itemView.bindData(bean.isMore(), "", bean.books);
            itemView.setOnMoreClickListener(new BookLinearLayout.OnMoreClickListener() {
                @Override
                public void onClick() {
                    //点击更多
                    if (bean.isBsJump()) {
                        ChaseRecommendMoreActivity.lauchMore(mContext, bean.name, ChaseRecommendAdapter.this.bookId, bean.moreType + "");
                    } else {
                        CenterDetailActivity.show(mContext, bean.url, bean.name, "", false, "", "", "");
                    }
                }
            });
        }
    }

    /**
     * ViewHolder
     */
    public class ChaseRecommendTopViewHolder extends RecyclerView.ViewHolder {

        private ChaseRecommendTopView itemView;

        /**
         * ViewHolder构造
         *
         * @param itemView itemView
         */
        public ChaseRecommendTopViewHolder(View itemView) {
            super(itemView);
            this.itemView = (ChaseRecommendTopView) itemView;
        }

        /**
         * 绑定数据
         *
         * @param bean bean
         */
        public void bindData(BeanBookRecomment bean) {
            itemView.bindData(bean);
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (position < items.size()) {
            return items.get(position).type;
        }

        return -1;
    }

    /**
     * Item
     */
    public static class Item {
        /**
         * type top
         */
        public static final int TYPE_TOP_ITEM = 0x00;
        /**
         * type book
         */
        public static final int TYPE_BOOK_ITEM = 0x01;

        BeanRecommentBookInfo bean;
        BeanBookRecomment beanInfo;
        int type;

        /**
         * 构造
         *
         * @param type     type
         * @param bean     bean
         * @param beanInfo beanInfo
         */
        public Item(int type, BeanRecommentBookInfo bean, BeanBookRecomment beanInfo) {
            this.type = type;
            this.bean = bean;
            this.beanInfo = beanInfo;
        }

    }

}
