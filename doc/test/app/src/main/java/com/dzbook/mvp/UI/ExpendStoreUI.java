package com.dzbook.mvp.UI;

import android.app.Activity;

import com.dzbook.mvp.BaseUI;

import java.util.List;

import hw.sdk.net.bean.store.BeanTempletInfo;

/**
 * ExpendStoreCommonActivity的UI接口
 *
 * @author gavin
 */
public interface ExpendStoreUI extends BaseUI {
    /**
     * 获取activity实例
     *
     * @return activity
     */
    Activity getActivity();

    /**
     * 显示无网络界面
     */
    void showNoNetView();

    /**
     * 展示无数据的空界面
     */
    void showEmptyView();

    /**
     * 显示加载动画
     */
    void showLoadding();

    /**
     * 隐藏加载动画
     */
    void hideLoading();

    /**
     * 设置数据
     *
     * @param section section
     */
    void setTempletDatas(List<BeanTempletInfo> section);
}
