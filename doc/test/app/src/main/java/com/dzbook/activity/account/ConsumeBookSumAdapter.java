package com.dzbook.activity.account;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.dzbook.mvp.presenter.ConsumeBookSumPresenter;
import com.dzbook.view.consume.ConsumeBookSumView;
import com.dzbook.view.consume.ConsumeOtherSumView;

import java.util.List;
import java.util.Vector;

import hw.sdk.net.bean.consume.ConsumeBookSumBean;

/**
 * 消费记录 Adapter
 *
 * @author lizz 2018/4/20.
 */
public class ConsumeBookSumAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Activity mActivity;

    private Vector<Item> mList;

    private ConsumeBookSumPresenter mPresenter;

    /**
     * ConsumeBookSumAdapter
     * @param activity activity
     * @param presenter presenter
     */
    public ConsumeBookSumAdapter(Activity activity, ConsumeBookSumPresenter presenter) {
        mList = new Vector<>();
        mActivity = activity;
        this.mPresenter = presenter;
    }

    /**
     * 添加数据
     *
     * @param list  list
     * @param clear clear
     */
    public void addItems(List<ConsumeBookSumBean> list, boolean clear) {
        if (clear) {
            mList.clear();
        }
        if (list != null && list.size() > 0) {
            for (ConsumeBookSumBean bean : list) {
                mList.add(new Item(Item.TYPE_BOOK, bean));
            }
        }
        notifyDataSetChanged();
    }

    /**
     * 添加其他数据
     *
     * @param list list
     */
    public void addOtherItems(List<OtherConsumeBean> list) {
        mList.clear();
        if (list != null && list.size() > 0) {
            for (OtherConsumeBean bean : list) {
                mList.add(new Item(Item.TYPE_OHTER, bean));
            }
        }
        notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        switch (viewType) {
            case Item.TYPE_BOOK:
                view = new ConsumeBookSumView(mActivity);
                return new ConsumeRecordSumViewHolder(view);
            default:
                view = new ConsumeOtherSumView(mActivity);
                return new ConsumeRecordOtherViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        int type = getItemViewType(position);
        switch (type) {
            case Item.TYPE_OHTER:
                if (holder instanceof ConsumeRecordOtherViewHolder) {
                    Item item = mList.get(position);
                    ((ConsumeRecordOtherViewHolder) holder).bindData(item.mOther, position == mList.size() - 1);
                }
                break;
            default:
                if (holder instanceof ConsumeRecordSumViewHolder) {
                    Item item = mList.get(position);
                    ((ConsumeRecordSumViewHolder) holder).bindData(item.mBean, position == mList.size() - 1);
                }
                break;
        }
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }


    @Override
    public int getItemViewType(int position) {
        if (position < mList.size()) {
            return mList.get(position).type;
        }
        return -10;
    }


    /**
     * ConsumeRecordSumViewHolder
     */
    class ConsumeRecordSumViewHolder extends RecyclerView.ViewHolder {

        private ConsumeBookSumView recordView;

        public ConsumeRecordSumViewHolder(View itemView) {
            super(itemView);
            recordView = (ConsumeBookSumView) itemView;
        }

        public void bindData(ConsumeBookSumBean recordBeanInfo, boolean isHideEndLine) {
            recordView.setMainShelfPresenter(mPresenter);
            recordView.bindData(recordBeanInfo, isHideEndLine);
        }
    }

    /**
     * ConsumeRecordOtherViewHolder
     */
    class ConsumeRecordOtherViewHolder extends RecyclerView.ViewHolder {

        private ConsumeOtherSumView otherSumView;

        public ConsumeRecordOtherViewHolder(View itemView) {
            super(itemView);
            otherSumView = (ConsumeOtherSumView) itemView;
        }

        public void bindData(OtherConsumeBean consumeBean, boolean isShowEndLine) {
            otherSumView.setMainShelfPresenter(mPresenter);
            otherSumView.bindData(consumeBean, isShowEndLine);
        }
    }

    /**
     * Item
     */
    public static class Item {
        static final int TYPE_OHTER = 0x01;
        static final int TYPE_BOOK = 0x02;
        ConsumeBookSumBean mBean;
        OtherConsumeBean mOther;
        private int type;


        Item(int type, OtherConsumeBean other) {
            this.type = type;
            this.mOther = other;
        }

        Item(int type, ConsumeBookSumBean bean) {
            this.type = type;
            this.mBean = bean;
        }
    }

    /**
     * OtherConsumeBean
     */
    public static class OtherConsumeBean {
        /**
         * mIconRes
         */
        public int mIconRes;
        /**
         * mTitle
         */
        public String mTitle;
        /**
         * typeId
         */
        public String typeId;

        /**
         * OtherConsumeBean
         * @param iconRes iconRes
         * @param title title
         * @param typeId typeId
         */
        public OtherConsumeBean(int iconRes, String title, String typeId) {
            this.mIconRes = iconRes;
            this.mTitle = title;
            this.typeId = typeId;
        }
    }
}

