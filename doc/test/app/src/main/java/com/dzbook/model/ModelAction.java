package com.dzbook.model;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.bumptech.glide.Glide;
import com.dzbook.AppConst;
import com.dzbook.activity.Main2Activity;
import com.dzbook.activity.detail.BookDetailActivity;
import com.dzbook.activity.reader.ReaderUtils;
import com.dzbook.database.bean.BookInfo;
import com.dzbook.database.bean.CatalogInfo;
import com.dzbook.event.EventBusUtils;
import com.dzbook.event.EventConstant;
import com.dzbook.lib.utils.ALog;
import com.dzbook.lib.utils.SDCardUtil;
import com.dzbook.log.LogConstants;
import com.dzbook.r.c.DzThread;
import com.dzbook.utils.DBUtils;
import com.dzbook.utils.FileUtils;
import com.ishugui.R;
import com.iss.app.BaseActivity;
import com.iss.view.common.ToastAlone;

import java.io.File;

/**
 * umeng 工具类
 *
 * @author zhenglk on 14/11/15.
 */
public class ModelAction {

    /**
     * 搜索
     */
    public static final int TO_SEARCH = 1;

    /**
     * 签到
     */
    public static final int TO_SIGN = 2;
    /**
     * 阅读
     */
    public static final int TO_READER = 3;
    /**
     * 书城
     */
    public static final int TO_BOOKSTORE = 4;

    /**
     * 点击新书推荐消息和书籍章节更新消息的时候是否点击返回键返回BookstoreActivity
     *
     * @param activity    activity
     * @param isStartMain isStartMain
     */
    public static void checkElseGoHome(Activity activity, boolean isStartMain) {
        if (null == activity) {
            return;
        }

        if (AppConst.getLaunchMode() == LogConstants.LAUNCH_GLOBAL_SEARCH) {
            return;
        }

        if (!AppConst.isIsMainActivityActive() || isStartMain) {
            Intent intentBookStore = new Intent(activity, Main2Activity.class);
            activity.startActivity(intentBookStore);
            EventBusUtils.sendMessage(EventConstant.FINISH_ACTIVITY_REQUEST_CODE, activity.getClass().getName(), null);
        }
    }

    /**
     * 点击新书推荐消息和书籍章节更新消息的时候是否点击返回键返回BookstoreActivity
     *
     * @param activity activity
     */
    public static void checkElseGoHome(Activity activity) {
        checkElseGoHome(activity, false);
    }


    /**
     * goReaderSmart
     *
     * @param context       context
     * @param goWhere       goWhere
     * @param reqId         reqId
     * @param bookId        bookId
     * @param chapterId     chapterId
     * @param pos           pos
     * @param isOpenByShelf isOpenByShelf
     */
    public static void goReaderSmart(final Activity context, int goWhere, int reqId, final String bookId, final String chapterId, long pos, final boolean isOpenByShelf) {
        goReaderSmart(context, goWhere, reqId, bookId, chapterId, pos, isOpenByShelf, AppConst.FROM_DEFAULT);
    }

    /**
     * goReaderSmart
     *
     * @param context       context
     * @param goWhere       goWhere
     * @param reqId         reqId
     * @param bookId        bookId
     * @param chapterId     chapterId
     * @param pos           pos
     * @param isOpenByShelf isOpenByShelf
     * @param fromWhere     原始是从哪里打开的 h5唤起？剪切板？
     * @return
     */
    public static void goReaderSmart(final Activity context, int goWhere, int reqId, final String bookId, final String chapterId, long pos, final boolean isOpenByShelf, final int fromWhere) {
        ALog.dLk("goReaderSmart goWhere=" + goWhere + " reqId=" + reqId + " bookId=" + bookId + " chapterId=" + chapterId + " pos=" + pos + " isOpenByShelf=" + isOpenByShelf + " fromWhere=" + fromWhere);
        if (null == context || TextUtils.isEmpty(bookId)) {
            return;
        }
        final BookInfo bookInfo = DBUtils.findByBookId(context, bookId);
        if (null != bookInfo) {
            if (goReaderDirectIfExist(context, bookId, chapterId, fromWhere, bookInfo)) {
                return;
            } else if (bookInfo.isLocalBook()) {
                return;
            }
        }

        Intent intent = new Intent(context, BookDetailActivity.class);
        intent.putExtra("bookId", bookId);
        context.startActivityForResult(intent, reqId);
        BaseActivity.showActivity(context);
    }

    private static boolean goReaderDirectIfExist(Activity context, String bookId, String chapterId, int fromWhere, BookInfo bookInfo) {
        if (null != bookInfo && !TextUtils.isEmpty(bookInfo.currentCatalogId)) {
            if (TextUtils.isEmpty(chapterId)) {
                chapterId = bookInfo.currentCatalogId;
            }
            /**
             * 如果唤起的代码 是h5或者剪切板 本地有这本书 并且有阅读记录 那就直接跳到这个章节。
             */
            if (fromWhere != AppConst.FROM_DEFAULT) {
                chapterId = bookInfo.currentCatalogId;
            }
        }

        if (!TextUtils.isEmpty(chapterId)) {
            CatalogInfo catalog = DBUtils.getCatalog(context, bookId, chapterId);
            if (null != catalog && catalog.isAvailable()) {
                ReaderUtils.intoReader(context, catalog, catalog.currentPos);
                return true;
            }
        }
        return false;
    }

    /**
     * 退出应用。
     *
     * @param activity activity
     * @param isExit   isExit
     */
    public static void exitApp(final Activity activity, boolean isExit) {
        ToastAlone.cancel();

        DzThread.clear();

        //        if (TinkerHelper.isPatchUpdate()) {
        //            DeviceInfoUtils.getInstanse().killAllProcess();
        //        } else {

        activity.finish();
        activity.overridePendingTransition(0, R.anim.goldav_app_exit);
        // 友盟统计
        if (isExit) {
            System.exit(0);
        }
        //        }

    }

    /**
     * 删除不在书架上的图书，存在占空文件则减小占空文件。
     *
     * @param context 上下文
     * @param limit   limit
     */
    public static void readyEnoughSpace(Context context, int limit) {
        if (SDCardUtil.getInstance().isSDCardCanWrite(limit)) {
            return;
        }
        try {
            String basePath = SDCardUtil.getInstance().getSDCardRootDir() + "";
            //对安卓6.0机型做适配 明明删除了 却提示没删除，估计删除时崩溃了
            long totalSDFreeSize = FileUtils.getSDFreeSize();
            int moreSize = (int) FileUtils.delAllFileReturnSize(10 * 1024 * 1024, basePath + "/DCIM/.thumbnails", basePath + "/LOST.DIR", basePath + "/DownLoad", basePath + "/downLoad", basePath + "/downloaded_rom", basePath + "/360Download", basePath + "/UCDownloads", basePath + "/360Brower", basePath + "/QQBrowser");
            ALog.i("size--", moreSize / (1024 * 1024) + "M");
            if (moreSize >= 10 * 1024 * 1024) {
                return;
            }
            long totalSDFreeSizeEnd = FileUtils.getSDFreeSize();
            float num = 1024;
            float freeSize = (totalSDFreeSizeEnd - totalSDFreeSize) / (num * 1024);
            ALog.i("size--", "总共删除用户文件--getSDFreeSize--" + freeSize + "M");
            ALog.i("size--", "总共删除用户文件" + ((float) moreSize) / (1024 * 1024) + "M");
            if (freeSize >= 10) {
                return;
            }
            File emptyFile = new File(SDCardUtil.getInstance().getSDCardAndroidRootDir() + "/" + FileUtils.APP_ASSIGN_FILE_SZIE_PATH);
            if (emptyFile.exists()) {
                FileUtils.setAssignSizeFile(emptyFile);
            }

            File files = new File(SDCardUtil.getInstance().getSDCardAndroidRootDir() + "/" + FileUtils.APP_BOOK_DIR_PATH);
            if (files.isDirectory() && files.exists()) {
                File[] listFiles = files.listFiles();
                if (listFiles != null && listFiles.length > 0) {
                    for (File child : listFiles) {
                        if (child.exists() && child.isDirectory()) {
                            // 清理文件
                            String filename = child.getName();
                            BookInfo bookinfo = DBUtils.findByBookId(context, filename);
                            if (bookinfo == null) {
                                FileUtils.delFolder(child.getAbsolutePath());
                            }
                        }
                    }
                }
            }
            //清除缓存图片
            Glide.get(context).clearDiskCache();
        } catch (Exception e) {
            ALog.printStackTrace(e);
        }
    }
}
