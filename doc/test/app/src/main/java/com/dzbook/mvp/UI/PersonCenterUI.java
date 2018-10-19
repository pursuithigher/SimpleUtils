package com.dzbook.mvp.UI;

import android.app.Activity;
import android.support.v4.app.Fragment;

import com.dzbook.mvp.BaseUI;

/**
 * 我的页面
 *
 * @author dongdianzhou on 2017/4/5.
 */

public interface PersonCenterUI extends BaseUI {
    /**
     * 登录成功后刷新view
     * @param isReferenceUserInfo isReferenceUserInfo
     */
    void referenceTopView(boolean isReferenceUserInfo);

    /**
     * 获取fragment实例
     *
     * @return fragment
     */
    Fragment getFragment();

    /**
     * 获取Activity实例
     *
     * @return Activity
     */
    Activity getActivity();

    /**
     * 显示加载进度弹窗
     *
     * @param message message
     */
    void showLoadingDialog(String message);

    /**
     * 隐藏载进度弹窗
     */
    void dismissLoadingDialog();

    /**
     * 刷新账户View
     */
    void referencePriceView();

    /**
     * 刷新用户信息
     */
    void refreshUserInfo();

    /**
     * 需要强制登录
     */
    void needLogin();

    /**
     * Token
     */
    void appTokenInvalid();
}
