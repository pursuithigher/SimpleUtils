package com.dzbook.mvp.UI;

import android.app.Activity;

import com.dzbook.mvp.BaseUI;

import java.util.ArrayList;

import hw.sdk.net.bean.ActivityCenterBean;

/**
 * ActivityCenterUI的UI接口
 *
 * @author dongdianzhou 2017/11/20
 */

public interface ActivityCenterUI extends BaseUI {


    /**
     * 设置数据
     *
     * @param list list
     */
    void setData(ArrayList<ActivityCenterBean.CenterInfoBean> list);

    /**
     * 显示空页面
     *
     * @param resID resID
     */
    void showEmptyView(int resID);

    /**
     * 显示过期界面
     */
    void showExpiredView();

    /**
     * 显示无网络界面
     */
    void showNoNetView();

    /**
     * 获取activity实例
     *
     * @return Activity
     */
    Activity getActivity();

    /**
     * 隐藏加载动画
     */
    void dismissLoadingView();
}
