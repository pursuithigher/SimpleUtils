package com.dzbook.mvp.UI;

import android.app.Activity;

import hw.sdk.net.bean.shelf.BeanShelfActivityInfo;
import hw.sdk.net.bean.store.BeanTempletsInfo;

/**
 * MainStoreFragment的UI接口
 *
 * @author dongdianzhou on 2018/1/11.
 */

public interface MainStoreUI {

    /**
     * 获取activity实例
     *
     * @return activity
     */
    Activity getActivity();

    /**
     * 显示加载动画
     */
    void showLoading();

    /**
     * 隐藏加载动画
     */
    void hideLoading();

    /**
     * 设置频道数据
     *
     * @param beanTempletsInfo beanTempletsInfo
     */
    void setChannelDatas(BeanTempletsInfo beanTempletsInfo);

    /**
     * 显示无网界面
     */
    void showNoNetView();

    /**
     * 显示空页面
     */
    void showEmptyView();

    /**
     * 弹出活动弹窗
     *
     * @param activity activity
     */
    void setNotiAndActivityData(BeanShelfActivityInfo activity);

    /**
     * 吐司弹出提示信息
     *
     * @param retMsg retMsg
     */
    void showMessage(String retMsg);
}
