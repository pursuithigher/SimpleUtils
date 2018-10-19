package com.dzbook.mvp.UI;

import android.app.Activity;

import hw.sdk.net.bean.store.BeanTempletsInfo;

/**
 * LimitFreeTwoLevelActivity的UI接口
 *
 * @author dongdianzhou on 2018/1/15.
 */

public interface LimitFreeTwoLevelUI {
    /**
     * 获取activity实例
     *
     * @return activity
     */
    Activity getActivity();

    /**
     * 设置数据
     *
     * @param templetsInfo templetsInfo
     */
    void setChannelDatas(BeanTempletsInfo templetsInfo);

    /**
     * 隐藏加载动画
     */
    void hideLoading();

    /**
     * 无网界面
     */
    void showNoNetView();

    /**
     * 空界面
     */
    void showEmptyView();
}
