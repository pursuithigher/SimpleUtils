package com.dzbook.mvp.UI;

import android.app.Activity;

import java.util.List;

import hw.sdk.net.bean.store.BeanTempletInfo;

/**
 * CommonTwoLevelActivity的UI接口
 *
 * @author dongdianzhou on 2018/1/15.
 */

public interface CommonTwoLevelUI {
    /**
     * 获取activity实例
     *
     * @return activity
     */
    Activity getActivity();

    /**
     * 设置数据
     *
     * @param section section
     */
    void setTempletDatas(List<BeanTempletInfo> section);

    /**
     * 隐藏加载动画
     */
    void hideLoading();

    /**
     * 显示无网界面
     */
    void showNoNetView();

    /**
     * 显示空页面
     */
    void showEmptyView();

}
