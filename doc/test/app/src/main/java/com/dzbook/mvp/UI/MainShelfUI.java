package com.dzbook.mvp.UI;

import android.app.Activity;

import com.dzbook.database.bean.BookInfo;
import com.dzbook.mvp.BaseUI;

import java.util.List;

import hw.sdk.net.bean.cloudshelf.BeanCloudShelfLoginSyncInfo;
import hw.sdk.net.bean.shelf.BeanBookUpdateInfo;

/**
 * author lizhongzhong 2017/4/6.
 */

public interface MainShelfUI extends BaseUI {

    /**
     * 打开书架编辑模式
     *
     * @param bookid bookid
     */
    void openManager(String bookid);

    /**
     * 设置书架显示模式
     *
     * @param mode mode
     */
    void setBookShlefMode(int mode);

    /**
     * 设置选中第一条
     */
    void setRecycleViewSelection();

    /**
     * 设置书架数据
     *
     * @param books books
     */
    void setBookShlefData(List<BookInfo> books);

    /**
     * 正在刷新时，延时可刷新状态
     */
    void hideReferenceDelay();

    /**
     * 获取书架适配器数据
     *
     * @return list
     */
    List<BookInfo> getShelfAdapterDatas();

    /**
     * 设置选中状态
     *
     * @param isAllSelected isAllSelected
     */
    void setAllItemSelectStatus(boolean isAllSelected);

    /**
     * 切换回普通模式
     *
     * @param isReferenceShelfData isReferenceShelfData
     */
    void backToCommonMode(boolean isReferenceShelfData);

    /**
     * 刷新书架书籍信息
     *
     * @param beanInfo beanInfo
     */
    void syncCloudBookShelfSuccess(BeanCloudShelfLoginSyncInfo beanInfo);

    /**
     * 更新书架数据信息
     *
     * @param books books
     * @param value value
     */
    void updateShelfData(List<BookInfo> books, BeanBookUpdateInfo value);

    /**
     * 获取activity实例
     *
     * @return activity
     */
    Activity getActivity();

    /**
     * 跟新签到信息
     *
     * @param value value
     */
    void updateShelfSignIn(BeanBookUpdateInfo value);

    /**
     * 弹出排序弹窗
     */
    void popSortDialog();

    /**
     * 弹出删除书籍弹窗
     */
    void popDeleteBookDialog();

    /**
     * 检查通知权限
     *
     * @param appOpenCount app打开次数
     * @param frequency    频率 几天？
     * @param cnMsg        cnMsg
     */
    void needShowSetNotifyDialogIfNeed(int appOpenCount, int frequency, String cnMsg);
}
