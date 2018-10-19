package com.dzbook.mvp.UI;

import com.dzbook.mvp.BaseUI;

import java.util.List;

import hw.sdk.net.bean.gift.GiftListBean;

/**
 * GiftExchangeFragment的UI接口
 *
 * @author by KongXP on 2018/4/25.
 */

public interface GiftUI extends BaseUI {
    /**
     * 显示无网络界面
     */
    void showNoNetView();

    /**
     * 设置数据
     *
     * @param list    list
     * @param refresh refresh
     */
    void setRecordList(List<GiftListBean> list, boolean refresh);

    /**
     * 展示无数据的空界面
     */
    void showEmptyView();

    /**
     * 停止加载更多
     */
    void stopLoadMore();

    /**
     * 设置是否有更多数据
     *
     * @param hasMore hasMore
     */
    void setHasMore(boolean hasMore);

    /**
     * 显示加载动画
     */
    void showLoadProgress();

    /**
     * 隐藏加载动画
     */
    void dismissLoadProgress();

    /**
     * setResultMsg
     *
     * @param pResult pResult
     */
    void setResultMsg(String pResult);

    /**
     * 显示已加载全部提示
     */
    void showAllTips();
}
