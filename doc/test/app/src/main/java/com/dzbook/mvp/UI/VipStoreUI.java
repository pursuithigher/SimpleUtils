package com.dzbook.mvp.UI;

import android.app.Activity;

import com.dzbook.mvp.BaseUI;

import java.util.List;

import hw.sdk.net.bean.store.BeanTempletInfo;

/**
 * VipStoreUI
 */
public interface VipStoreUI extends BaseUI {
    /**
     * 获取
     * activity实例
     *
     * @return activity
     */
    Activity getActivity();

    /**
     * 无网界面
     */
    void showNoNetView();

    /**
     * 空界面
     */
    void showEmptyView();

    /**
     * 加载动画
     */
    void showLoadding();

    /**
     * 隐藏加载
     */
    void hideLoading();

    /**
     * 设置数据
     *
     * @param section section
     */
    void setTempletDatas(List<BeanTempletInfo> section);
}
