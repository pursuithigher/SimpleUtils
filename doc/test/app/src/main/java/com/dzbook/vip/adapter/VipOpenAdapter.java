package com.dzbook.vip.adapter;

import android.content.Context;
import android.view.ViewGroup;

import com.alibaba.android.vlayout.DelegateAdapter;
import com.alibaba.android.vlayout.LayoutHelper;
import com.alibaba.android.vlayout.layout.LinearLayoutHelper;
import com.dzbook.mvp.presenter.MyVipPresenter;
import com.dzbook.view.vip.OpenItemVipView;

import java.util.List;

import hw.sdk.net.bean.vip.VipUserInfoBean;
import hw.sdk.net.bean.vip.VipUserPayBean;

/**
 * VipOpenAdapter
 *
 * @author gavin
 */
public class VipOpenAdapter extends DelegateAdapter.Adapter<VipViewHolder> {

    private Context mContext;
    private MyVipPresenter presenter;


    private List<VipUserPayBean> list;
    private VipUserInfoBean userInfoBean;
    private int selectPos = -1;

    /**
     * 构造
     *
     * @param context      context
     * @param presenter    presenter
     * @param list         list
     * @param userInfoBean userInfoBean
     */
    public VipOpenAdapter(Context context, MyVipPresenter presenter, List<VipUserPayBean> list, VipUserInfoBean userInfoBean) {
        mContext = context;
        this.presenter = presenter;
        this.list = list;
        this.userInfoBean = userInfoBean;
    }

    @Override
    public LayoutHelper onCreateLayoutHelper() {
        return new LinearLayoutHelper();
    }

    @Override
    public VipViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new VipViewHolder(new OpenItemVipView(mContext, presenter));
    }

    @Override
    public void onBindViewHolder(VipViewHolder holder, int position) {
        if (list != null) {
            boolean isDrawLine = false;
            VipUserPayBean bean = list.get(position);
            if (position == list.size() - 1) {
                isDrawLine = true;
            }

            holder.bindOpenData(bean, userInfoBean, isDrawLine, position, selectPos);
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    /**
     * 用于刷新item按下背景
     *
     * @param position position
     */
    public void refreshItemBg(int position) {
        selectPos = position;
        this.notifyDataSetChanged();
    }
}
