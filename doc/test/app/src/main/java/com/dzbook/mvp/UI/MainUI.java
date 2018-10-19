package com.dzbook.mvp.UI;

import android.app.Activity;

import com.dzbook.mvp.BaseUI;

/**
 * Main2Activity的UI接口
 *
 * @author dongdianzhou on 2017/11/28.
 */

public interface MainUI extends BaseUI {

    /**
     * 获取activity实例
     *
     * @return activity
     */
    Activity getActivity();

    /**
     * 设置选中tab
     *
     * @param i tab位置信息
     */
    void setBookStoreTableHost(int i);
}
