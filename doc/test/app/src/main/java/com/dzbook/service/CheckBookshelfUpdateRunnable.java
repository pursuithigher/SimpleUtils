package com.dzbook.service;

import android.content.Context;
import android.text.TextUtils;

import com.dzbook.database.bean.BookInfo;
import com.dzbook.database.bean.CatalogInfo;
import com.dzbook.lib.utils.ALog;
import com.dzbook.net.hw.HwRequestLib;
import com.dzbook.utils.DBUtils;

import java.util.List;

import hw.sdk.net.bean.BeanChapterCatalog;
import hw.sdk.net.bean.BeanChapterInfo;

/**
 * CheckBookshelfUpdateRunnable
 * @author admin
 */
public class CheckBookshelfUpdateRunnable implements Runnable {

    private static final String TAG = "CheckBookshelfUpdateRunnable: ";

    private Context mContext;

    private String bookId;

    /**
     * CheckBookshelfUpdateRunnable
     * @param context context
     * @param bookId bookId
     */
    public CheckBookshelfUpdateRunnable(Context context, String bookId) {
        mContext = context;
        this.bookId = bookId;
    }

    @Override
    public void run() {

        synchronized (CheckBookshelfUpdateRunnable.class) {

            String aBookId = this.bookId;
            if (!TextUtils.isEmpty(aBookId)) {

                BookInfo bean = DBUtils.findByBookId(mContext, aBookId);
                if (bean == null) {
                    return;
                }
                int updateStatus = 0;
                try {

                    updateStatus = bean.isUpdate;
                    if (3 == bean.isUpdate) {
                        ALog.iLk("图书--" + bean.bookname + "--正在更新中");
                        return;
                    }
                    ALog.iLk("图书--" + bean.bookname + "--开始更新");
                    // 标记当前目录正在更新

                    BookInfo bookInfo = new BookInfo();
                    bookInfo.bookid = aBookId;
                    bookInfo.isUpdate = 3;
                    DBUtils.updateBook(mContext, bookInfo);
                    // 从数据库取营销id


                    BookInfo mBookInfo = new BookInfo();
                    mBookInfo.bookid = aBookId;

                    String lastCId = "";
                    String chapterNum = "0";
                    CatalogInfo lastChapter = DBUtils.getLastCatalog(mContext, aBookId);
                    if (null != lastChapter && !TextUtils.isEmpty(lastChapter.catalogid)) {
                        lastCId = lastChapter.catalogid;
                        chapterNum = "99999";
                    }

                    List<BeanChapterInfo> chapterInfos = null;
                    try {
                        BeanChapterCatalog beanChapterCatalog = HwRequestLib.getInstance().chapterCatalog(aBookId, lastCId, chapterNum, "", "");
                        if (null != beanChapterCatalog && beanChapterCatalog.isSuccess()) {
                            chapterInfos = beanChapterCatalog.chapterInfoList;
                        }
                    } catch (Exception e) {
                        //重试一次章节目录更新
                        BeanChapterCatalog beanChapterCatalog = HwRequestLib.getInstance().chapterCatalog(aBookId, lastCId, chapterNum, "", "");
                        if (null != beanChapterCatalog && beanChapterCatalog.isSuccess()) {
                            chapterInfos = beanChapterCatalog.chapterInfoList;
                        }
                    }
                    InsertBookInfoDataUtil.appendChapters(mContext, chapterInfos, aBookId, null);

                    ALog.iLk("图书--" + bean.bookname + "--更新完成");

                    mBookInfo.isUpdate = 1;

                    DBUtils.updateBook(mContext, mBookInfo);

                } catch (Exception e) {
                    ALog.eLk(TAG, e.getMessage());

                    BookInfo bookInfo = new BookInfo();
                    bookInfo.bookid = aBookId;


                    ALog.iLk("图书--" + bean.bookname + "--更新失败");
                    // 目录更新失败，标记为不存在更新  还原isUpdate状态
                    if (updateStatus != 0) {
                        bookInfo.isUpdate = updateStatus;
                    } else {
                        bookInfo.isUpdate = 1;
                    }
                    DBUtils.updateBook(mContext, bookInfo);
                }
//            finally {
//                if (MetaUtils.getInstance().isSingle(mContext) && !TextUtils.isEmpty(bookId) && !TextUtils.isEmpty(type)) {
//                    if (isNeedCatalog) {
//                        DBEngine.getInstance().findCatalog(mContext, REMOTE_REQUEST_CODE, bookId, type);
//                    } else {
//                        DBEngine.getInstance().findBook(mContext, REMOTE_REQUEST_CODE, bookId, type);
//                    }
//                }
//            }

                // 设置一小时以后再检查此图书目录更新。
                // BeanShelfBooks.nextCheckTimeSet(bookid,
                // System.currentTimeMillis() + BeanShelfBooks.CHECK_TIME_DIV);
            }
        }
    }
}
