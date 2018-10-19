package com.dzbook.mvp.UI;


import hw.sdk.net.bean.BeanBlock;
import hw.sdk.net.bean.BeanChapterCatalog;

/**
 * BookDetailChapterActivity的UI接口
 *
 * @author wxliao on 17/8/15.
 */

public interface BookDetailChapterUI extends BookPageUI {

    /**
     * 添加数据接口
     *
     * @param chapterCatalog chapterCatalog
     * @param isInit         isInit
     * @param blockBean      blockBean
     */
    void addItem(BeanChapterCatalog chapterCatalog, boolean isInit, BeanBlock blockBean);

    /**
     * 设置跳转位置
     *
     * @param position position
     */
    void setListPosition(final int position);

    /**
     * 网络错误状态
     */
    void initNetErrorStatus();
}
