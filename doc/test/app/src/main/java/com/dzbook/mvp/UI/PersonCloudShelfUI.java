package com.dzbook.mvp.UI;

import android.app.Activity;

import com.dzbook.mvp.BaseUI;

import java.util.ArrayList;

import hw.sdk.net.bean.BeanBookInfo;

/**
 * 云书架
 *
 * @author dongdianzhou on 2017/11/20.
 */

public interface PersonCloudShelfUI extends BaseUI {
    /**
     * 设置书架数据
     *
     * @param list         list
     * @param isBackSource isBackSource
     */
    void setShelfData(ArrayList<BeanBookInfo> list, boolean isBackSource);

    /**
     * 设置加载更多
     *
     * @param isLoadMore isLoadMore
     */
    void setLoadMore(boolean isLoadMore);

    /**
     * 空界面
     */
    void showEmptyView();

    /**
     * 无网界面
     */
    void showNoNetView();

    /**
     * 通知Adapter刷新
     */
    void referenceAdapter();

    /**
     * 获取activity
     *
     * @return activity
     */
    Activity getActivity();

    /**
     * 加载更多完成
     */
    void compeletePullLoadMore();

    /**
     * 隐藏加载view
     */
    void hideLoadding();

    /**
     * 显示加载动画
     */
    void showLoadding();

    /**
     * 删除书籍弹窗
     *
     * @param beanBookInfo beanBookInfo
     */
    void popDeleteDialog(BeanBookInfo beanBookInfo);

    /**
     * 已显示全部提示
     */
    void showAllTips();

    /**
     * 返回最后一条数据的阅读时间，用于服务端分页
     *
     * @return string
     */
    String getLastItemTime();

    /**
     * 从adapter删除数据
     *
     * @param beanBookInfo beanBookInfo
     */
    void deleteDataFromAdapter(BeanBookInfo beanBookInfo);

    /**
     * 获取item数目
     *
     * @return int
     */
    int getCount();

    /**
     * 网络错误状态
     */
    void initNetErrorStatus();
}
