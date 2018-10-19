package com.dzbook.mvp.UI;

import android.app.Activity;

import com.dzbook.mvp.BaseUI;

import java.util.List;

import hw.sdk.net.bean.consume.ConsumeSecondBean;

/**
 * ConsumeSecondActivity的UI接口
 *
 * @author lizz 2018/4/20.
 */

public interface ConsumeSecondUI extends BaseUI {

    /**
     * 隐藏加载动画
     */
    void dismissLoadProgress();

    /**
     * 显示加载动画
     */
    void showLoadProgress();

    /**
     * 显示无网络界面
     */
    void showNoNetView();

    /**
     * 展示无数据的空界面
     */
    void showNoDataView();

    /**
     * 设置消费数据
     *
     * @param list    list
     * @param refresh refresh
     */
    void setBookConsumeSum(List<ConsumeSecondBean> list, boolean refresh);

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
     * 关闭activity
     */
    void finish();

    /**
     * 显示已加载全部提示
     */
    void showAllTips();

}
