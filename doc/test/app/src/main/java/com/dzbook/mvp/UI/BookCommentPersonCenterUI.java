package com.dzbook.mvp.UI;

import android.app.Activity;

import com.dzbook.mvp.BaseUI;

import hw.sdk.net.bean.bookDetail.BeanCommentMore;

/**
 * 评论interface
 *
 * @author Winzows on 2017/12/8.
 */
public interface BookCommentPersonCenterUI extends BaseUI {

    /**
     * 获取activity
     *
     * @return activity
     */
    Activity getActivity();

    /**
     * 添加数据
     *
     * @param value    value
     * @param dataType dataType
     */
    void fillData(BeanCommentMore value, int dataType);

    /**
     * 错误
     */
    void onError();

    /**
     * 显示view
     */
    void showView();

    /**
     * 显示空页面
     */
    void showEmpty();

    /**
     * 显示没有更多数据
     */
    void noMore();

    /**
     * 停止加载
     */
    void stopLoad();

    /**
     * 网络错误状态
     */
    void initNetErrorStatus();
}
