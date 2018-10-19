package com.dzbook.vip.adapter;

import android.content.Context;

import com.alibaba.android.vlayout.DelegateAdapter;
import com.alibaba.android.vlayout.VirtualLayoutManager;
import com.dzbook.mvp.presenter.MyVipPresenter;

import java.util.LinkedList;
import java.util.List;

import hw.sdk.net.bean.vip.VipBookInfo;
import hw.sdk.net.bean.vip.VipUserInfoBean;
import hw.sdk.net.bean.vip.VipUserPayBean;

/**
 * VipDelegateAdapter
 *
 * @author gavin
 */
public class VipDelegateAdapter extends DelegateAdapter {

    private Context mContext;

    private MyVipPresenter mPresenter;
    private VipOpenAdapter mOpenAdapter;

    /**
     * 构造
     *
     * @param layoutManager layoutManager
     * @param context       context
     * @param presenter     presenter
     */
    public VipDelegateAdapter(VirtualLayoutManager layoutManager, Context context, MyVipPresenter presenter) {
        super(layoutManager);
        mContext = context;
        mPresenter = presenter;
    }

    /**
     * 绑定数据
     *
     * @param userInfoBean    userInfoBean
     * @param vipUserPayBeans vipUserPayBeans
     * @param vipBookInfoList vipBookInfoList
     */
    public void addItems(VipUserInfoBean userInfoBean, List<VipUserPayBean> vipUserPayBeans, List<VipBookInfo> vipBookInfoList) {
        List<DelegateAdapter.Adapter> adapters = new LinkedList<>();
        mOpenAdapter = new VipOpenAdapter(mContext, mPresenter, vipUserPayBeans, userInfoBean);
        adapters.add(new VipTopAdapter(mContext, mPresenter, userInfoBean));
        adapters.add(mOpenAdapter);
        adapters.add(new VipPrivilegeAdapter(mContext));
        if (vipBookInfoList != null) {
            for (int i = 0; i < vipBookInfoList.size(); i++) {
                adapters.add(new VipMoreAdapter(mContext, vipBookInfoList.get(i).titleBean, i));
                adapters.add(new VipSigleBookAdapter(mContext, vipBookInfoList.get(i).beanBookInfoList));
            }
        }
        adapters.add(new VipBottomAdapter(mContext));
        setAdapters(adapters);
        notifyDataSetChanged();
    }

    /**
     * 用于刷新item按下背景
     *
     * @param position position
     */
    public void refreshItemBg(int position) {
        mOpenAdapter.refreshItemBg(position);
    }

}
