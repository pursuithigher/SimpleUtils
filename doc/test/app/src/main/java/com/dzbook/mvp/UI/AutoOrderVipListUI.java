package com.dzbook.mvp.UI;

import com.dzbook.mvp.BaseUI;

import java.util.List;

import hw.sdk.net.bean.vip.VipContinueOpenHisBean;

/**
 * AutoOrderVipListActivity的UI接口
 *
 * @author by KongXP on 2018/4/29.
 */

public interface AutoOrderVipListUI extends BaseUI {
    /**
     * 显示无网络界面
     */
    void showNoNetView();

    /**
     * 设置vip列表数据
     *
     * @param list    list
     * @param refresh refresh
     */
    void setVipList(List<VipContinueOpenHisBean> list, boolean refresh);

    /**
     * 显示空页面
     */
    void showEmptyView();

    /**
     * 停止加载更多
     */
    void stopLoadMore();

    /**
     * 设置能否上拉加载更多
     *
     * @param hasMore hasMore
     */
    void setHasMore(boolean hasMore);

    /**
     * 显示加载画面
     */
    void showLoadProgress();

    /**
     * 隐藏加载画面
     */
    void dismissLoadProgress();
}
