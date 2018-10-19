package com.dzbook.vip.adapter;

import android.content.Context;
import android.view.ViewGroup;

import com.alibaba.android.vlayout.DelegateAdapter;
import com.alibaba.android.vlayout.LayoutHelper;
import com.alibaba.android.vlayout.layout.LinearLayoutHelper;
import com.dzbook.mvp.presenter.MyVipPresenter;
import com.dzbook.view.vip.TopVipView;

import hw.sdk.net.bean.vip.VipUserInfoBean;

/**
 * VipTopAdapter
 *
 * @author gavin
 */
public class VipTopAdapter extends DelegateAdapter.Adapter<VipViewHolder> {

    private Context mContext;
    private MyVipPresenter presenter;

    private VipUserInfoBean info;

    /**
     * 构造
     *
     * @param context   context
     * @param presenter presenter
     * @param info      info
     */
    public VipTopAdapter(Context context, MyVipPresenter presenter, VipUserInfoBean info) {
        mContext = context;
        this.presenter = presenter;
        this.info = info;
    }

    @Override
    public LayoutHelper onCreateLayoutHelper() {
        return new LinearLayoutHelper();
    }

    @Override
    public VipViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new VipViewHolder(new TopVipView(mContext, presenter));
    }

    @Override
    public void onBindViewHolder(VipViewHolder holder, int position) {
        if (info != null) {
            holder.bindTopData(info);
        }
    }

    @Override
    public int getItemCount() {
        return 1;
    }


}
