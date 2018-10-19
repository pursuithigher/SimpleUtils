package com.dzbook.mvp.presenter;

import android.view.View;

/**
 * PersonCenterPresenter
 *
 * @author dongdianzhou on 2017/4/5.
 */

public interface PersonCenterPresenter {

    /**
     * 登录
     */
    void login();

    /**
     * 跳转值AccountActivity
     */
    void intentToAccountActivity();

    /**
     * 跳转到CloudSelfActivity
     */
    void intentToCloudSelfActivity();

    /**
     * 跳转到SystemSetActivity
     */
    void intentToSystemSetActivity();

    /**
     * 跳转到GiftActivity
     */
    void intentToGiftActivity();

    /**
     * 跳转到MyReadTimeActivity
     */
    void intentToMyReadTimeActivity();

    /**
     * 跳转到BookCommentPerson
     */
    void intentToBookCommentPerson();

    /**
     * 跳转到FeedBackActivity
     */
    void intentToFeedBackActivity();

    /**
     * 跳转到MyVipActivity
     */
    void intentToMyVipActivity();

    /**
     * 充值
     */
    void dzRechargePay();

    /**
     * 从网上获取用户信息
     */
    void getUserInfoFromNet();

    /**
     * 从服务端获取阅读时长和用户信息
     */
    void getReaderTimeAndUserInfoFromNet();

    /**
     * 跳转到HwAccountCenter
     */
    void intentToHwAccountCenter();

    /**
     * 无网弹窗
     */
    void showNotNetDialog();

    /**
     * 关闭软键盘
     *
     * @param view view
     */
    void hideSoft(View view);
}
