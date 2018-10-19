package com.dzbook.vip.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.dzbook.view.vip.MoreBooksVipView;
import com.dzbook.view.vip.OpenItemVipView;
import com.dzbook.view.vip.SigleBooKVipView;
import com.dzbook.view.vip.TopVipView;

import hw.sdk.net.bean.vip.VipBookInfo;
import hw.sdk.net.bean.vip.VipUserInfoBean;
import hw.sdk.net.bean.vip.VipUserPayBean;

/**
 * VipViewHolder
 *
 * @author gavin
 */
public class VipViewHolder extends RecyclerView.ViewHolder {

    private OpenItemVipView openItemVipView;
    //
    //    private BottomtipsVipView bottomtipsVipView;
    //    private PrivilegeVipView privilegeVipView;
    private TopVipView topVipView;
    private MoreBooksVipView moreBooksVipView;
    private SigleBooKVipView sigleBooKVipView;

    /**
     * 构造
     *
     * @param view view
     */
    public VipViewHolder(View view) {
        super(view);
        if (view instanceof OpenItemVipView) {
            openItemVipView = (OpenItemVipView) view;
        } else if (view instanceof TopVipView) {
            topVipView = (TopVipView) view;
        } else if (view instanceof MoreBooksVipView) {
            moreBooksVipView = (MoreBooksVipView) view;
        } else if (view instanceof SigleBooKVipView) {
            sigleBooKVipView = (SigleBooKVipView) view;
        }
    }

    /**
     * 绑定topVipView信息
     *
     * @param info info
     */
    public void bindTopData(VipUserInfoBean info) {
        if (topVipView != null) {
            topVipView.bindData(info);
        }
    }

    /**
     * openItemVipView绑定信息
     *
     * @param info         info
     * @param userInfoBean userInfoBean
     * @param isDrawLine   isDrawLine
     * @param selectPos    selectPos
     * @param position     position
     */
    public void bindOpenData(VipUserPayBean info, VipUserInfoBean userInfoBean, boolean isDrawLine, int position, int selectPos) {
        if (openItemVipView != null) {
            openItemVipView.bindData(info, userInfoBean, isDrawLine, position, selectPos);
        }
    }

    /**
     * sigleBooKVipView绑定信息
     *
     * @param info info
     */
    public void bindBookData(VipBookInfo.BookBean info) {
        if (sigleBooKVipView != null) {
            sigleBooKVipView.bindData(info);
        }
    }

    /**
     * moreBooksVipView绑定信息
     *
     * @param info info
     */
    public void bindMoreBTitle(VipBookInfo.TitleBean info) {
        if (moreBooksVipView != null) {
            moreBooksVipView.bindData(info);
        }
    }


}
