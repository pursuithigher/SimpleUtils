package com.dzbook.activity.account;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.dzbook.mvp.presenter.ConsumeSecondPresenter;
import com.dzbook.view.consume.ConsumeSecondView;

import java.util.List;
import java.util.Vector;

import hw.sdk.net.bean.consume.ConsumeSecondBean;

/**
 * 消费记录二级 adapter
 *
 * @author lizz 2018/4/20.
 */
public class ConsumeSecondAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Activity mActivity;

    private List<ConsumeSecondBean> mList;

    private ConsumeSecondPresenter mPresenter;

    ConsumeSecondAdapter(Activity activity, ConsumeSecondPresenter presenter) {
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
    public void addItems(List<ConsumeSecondBean> list, boolean clear) {
        if (clear) {
            mList.clear();
        }
        if (list != null && list.size() > 0) {
            mList.addAll(list);
        }
        notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ConsumeSecondViewHolder(new ConsumeSecondView(mActivity));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ConsumeSecondBean bean = mList.get(position);
        ((ConsumeSecondViewHolder) holder).bindData(bean, position == mList.size() - 1);
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    /**
     * holder
     */
    class ConsumeSecondViewHolder extends RecyclerView.ViewHolder {

        private ConsumeSecondView consumeSecond;

        ConsumeSecondViewHolder(View itemView) {
            super(itemView);
            consumeSecond = (ConsumeSecondView) itemView;
        }

        public void bindData(ConsumeSecondBean secondBean, boolean isHideEndLine) {
            if (mPresenter != null) {
                consumeSecond.setPresenter(mPresenter);
            }
            consumeSecond.bindData(secondBean, isHideEndLine);
        }
    }
}

