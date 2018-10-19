package com.dzbook.mvp.UI;

import android.app.Activity;

import com.dzbook.mvp.BaseUI;

/**
 * 我的账户
 *
 * @author dongdianzhou on 2017/4/6.
 */

public interface PersonAccountUI extends BaseUI {
    /**
     * 获取
     * activity实例  * @return activity
     * @return Activity
     */
    Activity getActivity();

    /**
     * 账户看点代金券信息
     */
    void referencePriceView();

    /**
     * 账户看点代金券信息
     */
    void setUserPriceInfo();

    /**
     * 显示加载进度弹窗
     */
    void showLoadingDialog();

    /**
     * 隐藏载进度弹窗
     */
    void hideLoadingDialog();
}
