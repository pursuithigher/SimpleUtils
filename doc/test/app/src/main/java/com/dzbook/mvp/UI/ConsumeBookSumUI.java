package com.dzbook.mvp.UI;

import android.app.Activity;

import com.dzbook.mvp.BaseUI;

import java.util.List;

import hw.sdk.net.bean.consume.ConsumeBookSumBean;

/**
 * ConsumeBookSumActivity的UI接口
 *
 * @author lizz 2018/4/20.
 */

public interface ConsumeBookSumUI extends BaseUI {

    /**
     * 隐藏加载动画
     */
    void dismissLoadProgress();

    /**
     * 显示加载动画
     */
    void showLoadProgress();

    /**
     * 显示无网画面
     */
    void showNoNetView();

    /**
     * 显示空界面
     */
    void showNoDataView();

    /**
     * 设置消费数据
     *
     * @param list    list
     * @param refresh refresh
     */
    void setBookConsumeSum(List<ConsumeBookSumBean> list, boolean refresh);

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
     * 获取activity实例
     *
     * @return activity
     */
    Activity getActivity();

    /**
     * 显示已加载全部提示
     */
    void showAllTips();


}
