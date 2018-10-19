package com.dzbook.mvp.UI;

import android.app.Activity;

import com.dzbook.mvp.BaseUI;

import hw.sdk.net.bean.bookDetail.BeanCommentMore;

/**
 * BookCommentMoreActivity的UI接口
 *
 * @author Winzows on 2017/11/27.
 */

public interface BookCommentDetailUI extends BaseUI {

    /**
     * 获取activity实例
     *
     * @return activity
     */
    Activity getActivity();

    /**
     * 填充数据
     *
     * @param value    value
     * @param dataType dataType
     */
    void fillData(BeanCommentMore value, int dataType);

    /**
     * 错误界面
     */
    void onError();

    /**
     * 显示主View
     */
    void showView();

    /**
     * 显示空界面
     */
    void showEmpty();

    /**
     * 没有更多
     */
    void noMore();

    /**
     * 定制加载
     */
    void stopLoad();
}
