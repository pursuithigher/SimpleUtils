package com.dzbook.activity.account;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.dzbook.view.consume.ConsumeThirdView;

import java.util.List;
import java.util.Vector;

import hw.sdk.net.bean.consume.ConsumeThirdBean;

/**
 * 消费记录三级Adapter
 *
 * @author lizz 2018/4/20.
 */
public class ConsumeThirdAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Activity mActivity;

    private List<ConsumeThirdBean> mList;

    ConsumeThirdAdapter(Activity activity) {
        mList = new Vector<>();
        mActivity = activity;
    }

    /**
     * 添加数据
     *
     * @param list  list
     * @param clear clear
     */
    public void addItems(List<ConsumeThirdBean> list, boolean clear) {
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
        return new ConsumeThirdViewHolder(new ConsumeThirdView(mActivity));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ConsumeThirdBean bean = mList.get(position);
        ((ConsumeThirdViewHolder) holder).bindData(bean);

    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    /**
     * holder
     */
    class ConsumeThirdViewHolder extends RecyclerView.ViewHolder {

        private ConsumeThirdView consumeSecond;

        ConsumeThirdViewHolder(View itemView) {
            super(itemView);
            consumeSecond = (ConsumeThirdView) itemView;
        }

        public void bindData(ConsumeThirdBean secondBean) {
            consumeSecond.bindData(secondBean);
        }
    }
}

