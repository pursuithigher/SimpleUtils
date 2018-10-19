package com.dzbook.mvp.UI;

import com.dzbook.mvp.BaseUI;

import java.util.List;

import hw.sdk.net.bean.vouchers.VouchersListBean;

/**
 * 代金券
 *
 * @author by KongXP on 2018/4/25.
 */

public interface PersonVouchersListUI extends BaseUI {

    /**
     * 无网界面
     */
    void showNoNetView();

    /**
     * 设置列表数据
     *
     * @param list    list
     * @param refresh refresh
     */
    void setRecordList(List<VouchersListBean> list, boolean refresh);

    /**
     * 空界面
     */
    void showEmptyView();

    /**
     * 加载更过完毕
     */
    void stopLoadMore();

    /**
     * 设置能否加载更多
     *
     * @param hasMore hasMore
     */
    void setHasMore(boolean hasMore);

    /**
     * 加载动画
     */
    void showLoadProgress();

    /**
     * 隐藏加载动画
     */
    void dismissLoadProgress();

    /**
     * 一加载全部提示
     */
    void showAllTips();

}
