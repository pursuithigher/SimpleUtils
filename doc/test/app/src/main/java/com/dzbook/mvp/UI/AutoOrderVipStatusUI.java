package com.dzbook.mvp.UI;

import android.app.Activity;

import com.dzbook.mvp.BaseUI;

import hw.sdk.net.bean.vip.VipAutoRenewStatus;

/**
 * AutoOrderVipActivity的UI接口
 *
 * @author KongXP 2018/4/21.
 */

public interface AutoOrderVipStatusUI extends BaseUI {

    /**
     * 获取activity实例
     *
     * @return activity
     */
    Activity getActivity();

    /**
     * 设置vip订单状态信息
     *
     * @param bean bean
     */
    void setVipOrderStatusInfo(VipAutoRenewStatus bean);

    /**
     * 隐藏加载动画
     */
    void dismissLoadProgress();

    /**
     * 显示加载画面
     */
    void showLoadProgress();

    /**
     * 显示无网界面
     */
    void showNoNetView();

    /**
     * 显示无数据界面
     */
    void showNoDataView();

    /**
     * 是否显示无网弹窗
     */
    void isShowNotNetDialog();
}
