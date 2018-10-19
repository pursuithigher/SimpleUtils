package com.dzbook.mvp.UI;

import com.dzbook.mvp.BaseUI;

import hw.sdk.net.bean.BeanSwitchPhoneNum;

/**
 * 实名认证
 *
 * @author winzows 2018/4/17
 */

public interface RealNameAuthUI extends BaseUI {
    /**
     * 展示切换
     */
    void showSwitchPhoneView();

    /**
     * 展示绑定
     */
    void showBindPhoneView();

    /**
     * 展示错误页面
     */
    void showErrorView();

    /**
     * start
     */
    void onRequestStart();

    /**
     * 绑定切换手机号的数据
     *
     * @param beanSwitchPhoneNum bean
     */
    void bindSwitchPhoneData(BeanSwitchPhoneNum beanSwitchPhoneNum);

    /**
     * 展示 绑定成功页面
     */
    void showAuthSuccessView();

    /**
     * 关闭页面
     */
    void finishActivity();

    /**
     * 获取页面tag
     *
     * @return 页面tag
     */
    String getPageTag();
}
