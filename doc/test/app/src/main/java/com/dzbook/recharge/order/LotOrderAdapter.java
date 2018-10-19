package com.dzbook.recharge.order;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.dzbook.mvp.UI.LotOrderUI;
import com.dzpay.recharge.netbean.LotOrderPageBean;

import java.util.ArrayList;
import java.util.List;

/**
 * LotOrderAdapter
 * @author lizhongzhong 2017/8/3.
 */

public class LotOrderAdapter extends BaseAdapter {

    private List<LotOrderPageBean> mList;

    private int selectPosition = 0;

    private LotOrderUI lotOrderUI;

    private Context mContext;

    /**
     * LotOrderAdapter
     * @param context context
     */
    public LotOrderAdapter(Context context) {
        mList = new ArrayList<>();
        this.mContext = context;
    }


    public void setLotOrderUI(LotOrderUI orderUi) {
        this.lotOrderUI = orderUi;
    }

    /**
     * addItems
     * @param list list
     * @param isClear list
     */
    public void addItems(List<LotOrderPageBean> list, boolean isClear) {

        if (isClear) {
            mList.clear();
        }

        if (list != null && list.size() > 0) {
            mList.addAll(list);
        }

        setSelection(true, selectPosition);

        notifyDataSetChanged();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LotOrderItemView view;
        if (convertView == null) {
            view = new LotOrderItemView(mContext);
        } else {
            view = (LotOrderItemView) convertView;
        }

        if (lotOrderUI != null) {
            view.setLotOrderUI(lotOrderUI);
        }
        view.bindData(mList.get(position), position, isSelected(position));

        return view;
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    /**
     * 选中位置
     *
     * @param position position
     * @param isSelectDefault isSelectDefault
     */
    public void setSelection(boolean isSelectDefault, int position) {

        if (null == mList) {
            selectPosition = -1;
        } else {
            selectPosition = Math.min(position, mList.size() - 1);
        }

        if (!isSelectDefault) {
            notifyDataSetChanged();
        }
    }

    private boolean isSelected(int position) {

        return position == selectPosition;
    }


}
