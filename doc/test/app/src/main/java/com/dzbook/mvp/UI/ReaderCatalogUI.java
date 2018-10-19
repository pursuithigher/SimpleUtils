package com.dzbook.mvp.UI;

import com.dzbook.database.bean.BookMarkNew;
import com.dzbook.database.bean.CatalogInfo;

import java.util.List;

/**
 * 阅读目录
 *
 * @author wxliao on 17/8/18.
 */

public interface ReaderCatalogUI extends BookPageUI {
    /**
     * showScanProgress
     *
     * @param progress progress
     * @param max      max
     */
    void showScanProgress(int progress, int max);

    /**
     * hideScanProgress
     */
    void hideScanProgress();

    /**
     * 设置底部按钮状态
     *
     * @param status     status
     * @param remainSize remainSize
     * @param totalSize  totalSize
     */
    void setPurchasedButtonStatus(int status, int remainSize, int totalSize);

    /**
     * 添加章节数据
     *
     * @param list  list
     * @param clear clear
     */
    void addChapterItem(List<CatalogInfo> list, boolean clear);

    /**
     * 添加书签数据
     *
     * @param list  list
     * @param clear clear
     */
    void addBookMarkItem(List<BookMarkNew> list, boolean clear);

    /**
     * 添加笔记数据
     *
     * @param list  list
     * @param clear clear
     */
    void addBookNoteItem(List<BookMarkNew> list, boolean clear);

    /**
     * 设置选中位置
     *
     * @param catalogId 章节id
     */
    void setSelectionFromTop(final String catalogId);

    /**
     * 刷新目录
     */
    void refreshChapterView();

    /**
     * 刷新书签
     */
    void refreshBookMarkView();

    /**
     * 刷新笔记
     */
    void refreshBookNoteView();

    /**
     * 章节点击
     *
     * @param chapter chapter
     */
    void onChapterItemClick(CatalogInfo chapter);

    /**
     * 书签点击
     *
     * @param bookMark bookMark
     */
    void onBookMarkItemClick(BookMarkNew bookMark);

    /**
     * 书签长按事件
     *
     * @param bookMark bookMark
     */
    void onBookMarkItemLongClick(BookMarkNew bookMark);

    /**
     * 笔记点击事件
     *
     * @param bookNote bookNote
     */
    void onBookNoteItemClick(BookMarkNew bookNote);

    /**
     * 笔记item长按事件
     *
     * @param bookNote bookNote
     */
    void onBookNoteItemLongClick(BookMarkNew bookNote);
}
