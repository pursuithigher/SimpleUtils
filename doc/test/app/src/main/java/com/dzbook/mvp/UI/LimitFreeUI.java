package com.dzbook.mvp.UI;

import android.app.Activity;

import com.dzbook.mvp.BaseUI;

import java.util.List;

import hw.sdk.net.bean.store.BeanTempletInfo;

/**
 * LimitFreeActivity的UI接口
 */
public interface LimitFreeUI extends BaseUI {
    /**
     * 获取activity实例
     *
     * @return activity
     */
    Activity getActivity();

    /**
     * 无网络
     */
    void showNoNetView();

    /**
     * 无数据界面
     */
    void showEmptyView();

    /**
     * 加载动画
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
