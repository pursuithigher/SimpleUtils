package com.dzbook.vip.adapter;

import android.content.Context;
import android.view.ViewGroup;

import com.alibaba.android.vlayout.DelegateAdapter;
import com.alibaba.android.vlayout.LayoutHelper;
import com.alibaba.android.vlayout.layout.LinearLayoutHelper;
import com.dzbook.view.vip.BottomtipsVipView;

/**
 * VipBottomAdapter
 *
 * @author gavin
 */
public class VipBottomAdapter extends DelegateAdapter.Adapter<VipViewHolder> {

    private Context mContext;

    /**
     * 构造
     *
     * @param context context
     */
    public VipBottomAdapter(Context context) {
        mContext = context;
    }

    @Override
    public LayoutHelper onCreateLayoutHelper() {
        return new LinearLayoutHelper();
    }

    @Override
    public VipViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new VipViewHolder(new BottomtipsVipView(mContext));
    }

    @Override
    public void onBindViewHolder(VipViewHolder holder, int position) {
    }

    @Override
    public int getItemCount() {
        return 1;
    }


}
