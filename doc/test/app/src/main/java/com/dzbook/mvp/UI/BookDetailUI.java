package com.dzbook.mvp.UI;

import com.dzbook.database.bean.BookInfo;

import hw.sdk.net.bean.BeanBookInfo;
import hw.sdk.net.bean.bookDetail.BeanBookDetail;

/**
 * BookDetailActivity的UI接口
 *
 * @author wxliao on 17/7/25.
 */

public interface BookDetailUI extends BookPageUI {

    /**
     * 设置页面数据
     *
     * @param bookDetailBean bookDetailBean
     */
    void setPageData(BeanBookDetail bookDetailBean);

    /**
     * 刷新底部菜单按钮状态
     *
     * @param beanBookInfo     beanBookInfo
     * @param marketStatus     marketStatus
     * @param bookInfo         bookInfo
     * @param isShowFreeStatus isShowFreeStatus
     */
    void refreshMenu(BeanBookInfo beanBookInfo, int marketStatus, BookInfo bookInfo, boolean isShowFreeStatus);

    /**
     * 设置加入书架按钮显示状态
     *
     * @param enable enable
     */
    void setBookShelfMenu(boolean enable);

    /**
     * 显示加载动画
     */
    void showLoadDataDialog();

    /**
     * 隐藏加载动画
     */
    void dismissLoadDataDialog();

    /**
     * 书籍已下架状态展示
     */
    void setDeletePage();

    /**
     * 网络错误页
     */
    void setErrPage();
}
