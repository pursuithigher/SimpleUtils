package com.dzbook.mvp.UI;

import com.dzbook.mvp.BaseUI;

import java.util.List;

import hw.sdk.net.bean.record.RechargeRecordBean;

/**
 * 充值记录
 *
 * @author by dongdianzhou on 2017/11/21.
 */

public interface PersonRechargeRecordUI extends BaseUI {

    /**
     * 无网络
     */
    void showNoNetView();

    /**
     * 设置充值记录列表数据
     *
     * @param list    list
     * @param refresh refresh
     */
    void setRecordList(List<RechargeRecordBean> list, boolean refresh);

    /**
     * 空界面
     */
    void showEmptyView();

    /**
     * 完成加载更锁
     */
    void stopLoadMore();

    /**
     * 设置能否加载更多
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
     * 加载全部提示
     */
    void showAllTips();
}
