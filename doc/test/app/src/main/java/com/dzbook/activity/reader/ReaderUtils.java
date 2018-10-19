package com.dzbook.activity.reader;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.dzbook.AppConst;
import com.dzbook.BaseLoadActivity;
import com.dzbook.database.bean.BookInfo;
import com.dzbook.database.bean.BookMarkNew;
import com.dzbook.database.bean.CatalogInfo;
import com.dzbook.event.EventBusUtils;
import com.dzbook.event.EventConstant;
import com.dzbook.lib.net.DzSchedulers;
import com.dzbook.lib.utils.ALog;
import com.dzbook.loader.BookLoader;
import com.dzbook.loader.LoadResult;
import com.dzbook.log.LogConstants;
import com.dzbook.mvp.DialogConstants;
import com.dzbook.mvp.presenter.MissingContentPresenter;
import com.dzbook.r.c.AkDocInfo;
import com.dzbook.r.model.DzLine;
import com.dzbook.service.RechargeParams;
import com.dzbook.utils.DBUtils;
import com.dzbook.utils.SpUtil;
import com.dzbook.utils.TurnPageUtils;
import com.dzbook.utils.WpsModel;
import com.ishugui.R;
import com.iss.app.BaseActivity;
import com.iss.view.common.ToastAlone;

import java.io.File;
import java.util.ArrayList;


/**
 * 阅读器公共类。
 *
 * @author zhenglk 2014-6-6 上午11:29:21
 */
public class ReaderUtils {


    /**
     * 进入阅读器
     *
     * @param context     context
     * @param catalogInfo catalogInfo
     * @param pos         pos
     * @return boolean
     */
    public static boolean intoReader(Context context, CatalogInfo catalogInfo, long pos) {
        if (catalogInfo == null) {
            ToastAlone.showShort(context.getResources().getString(R.string.preload_load_fail));
            return false;
        }
        BookInfo bookInfo = DBUtils.findByBookId(context.getApplicationContext(), catalogInfo.bookid);
        if (bookInfo == null) {
            ToastAlone.showShort(context.getResources().getString(R.string.preload_load_fail));
            return false;
        }

        if (catalogInfo.isContentEmptyDeleted()) {
            //2(缺章,未领取) 3(缺章，已领取) //4(删章)
            Intent intent = new Intent(context, MissingContentActivity.class);
            intent.putExtra(MissingContentPresenter.BOOK_INFO, bookInfo);
            intent.putExtra(MissingContentPresenter.CHAPTER_INFO, catalogInfo);
            context.startActivity(intent);
            BaseActivity.showActivity(context);
            EventBusUtils.sendMessage(EventConstant.CLOSEBOOK_REQUEST_CODE, EventConstant.TYPE_MAINSHELFFRAGMENT, null);
            return false;
        }

        if (!catalogInfo.isAvailable()) {
            ToastAlone.showShort(context.getResources().getString(R.string.preload_load_fail));
            return false;
        }

        return ReaderUtils.openBook(context, catalogInfo, pos);
    }


    /**
     * 阅读器调用，或者sdk回调。
     *
     * @param context 上下文
     * @param catalog 章节信息
     */
    private static boolean openBook(Context context, CatalogInfo catalog, long pos, Object... args) {
        if (catalog == null || null == catalog.path || !new File(catalog.path).exists()) {
            return false;
        }
        BookInfo bookInfo = DBUtils.findByBookId(context, catalog.bookid);
        if (bookInfo == null) {
            return false;
        }
        if (bookInfo.isJump()) {
            openWps(context, bookInfo);
            return true;
        }
        // 尝试进入内置阅读器
        try {
            AkDocInfo docInfo = generateDoc(context, bookInfo, catalog);
            docInfo.currentPos = pos;

            if (null != args && args.length > 0 && args[0] instanceof Integer && context instanceof Activity) {
                ReaderActivity.launchForResult((Activity) context, docInfo, catalog.openFrom, (Integer) args[0]);
                return true;
            } else {
                ReaderActivity.launch(context, docInfo, catalog.openFrom);
                return true;
            }
        } catch (Exception ignore) {
        }

        return false;
    }

    /**
     * 打开wps
     *
     * @param context  context
     * @param bookInfo bookInfo
     */
    public static void openWps(Context context, BookInfo bookInfo) {
        BookInfo tempBookInfo = new BookInfo();
        tempBookInfo.time = System.currentTimeMillis() + "";
        tempBookInfo.bookid = bookInfo.bookid;
        DBUtils.updateBook(context, tempBookInfo);
        WpsModel.openFile4wps(context, bookInfo);
    }

    /**
     * 章节是否可以直接使用。
     * 自有的判断流程：章节内容存在
     *
     * @param chapter chapter
     * @return boolean
     */
    public static boolean allowOpenDirect(CatalogInfo chapter) {
        //自有判断流程
        return null != chapter && chapter.isAvailable();
    }

    /**
     * 服务器有下放的提示语，提示dialog。否则toast提示。
     *
     * @param msg msg
     */
    public static void dialogOrToast(String msg) {
        if (!TextUtils.isEmpty(msg)) {
            ToastAlone.showShort(msg);
        }
    }

    /**
     * 服务器有下放的提示语，提示dialog。否则toast提示。
     *
     * @param activity    activity
     * @param msg         msg
     * @param isRecommend 书籍下架有两种方式：阅读器外面显示toast，阅读器里面显示dialog
     * @param bookId      用于请求推荐书籍列表
     */
    public static void dialogOrToast(Activity activity, String msg, boolean isRecommend, String bookId) {
        if (null != activity && TextUtils.equals(msg, activity.getString(R.string.book_down_shelf)) && isRecommend) {
            //书籍下架单独处理
            TurnPageUtils.toRecommentPage(activity, bookId, "", -1, "", 0);
        } else if (!TextUtils.isEmpty(msg)) {
            ToastAlone.showShort(msg);
        }
    }


    /**
     * 继续阅读
     *
     * @param context context
     */
    public static void continueReadBook(final Activity context) {
        final String bookId = SpUtil.getinstance(AppConst.getApp()).getString(SpUtil.RECENT_READER);
        if (TextUtils.isEmpty(bookId)) {
            return;
        }
        ALog.cmtDebug("continueReadBook");
        DzSchedulers.child(new Runnable() {
            @Override
            public void run() {
                if (context instanceof BaseActivity) {
                    ((BaseActivity) context).showDialogByType(DialogConstants.TYPE_GET_DATA);
                }
                BookInfo firstBook = DBUtils.findByBookId(context, bookId);
                if (null == firstBook || TextUtils.isEmpty(firstBook.bookid) || TextUtils.isEmpty(firstBook.currentCatalogId)) {
                    if (context instanceof BaseActivity) {
                        ((BaseActivity) context).dissMissDialog();
                    }
                    return;
                }
                CatalogInfo catalog = DBUtils.getCatalog(context, firstBook.bookid, firstBook.currentCatalogId);
                try {
                    if (null == catalog || !catalog.isAvailable()) {
                        LoadResult loadResult = BookLoader.getInstance().fastReadChapter(context, firstBook.bookid, firstBook.currentCatalogId, false);
                        if (loadResult.isSuccess()) {
                            catalog = DBUtils.getCatalog(context, firstBook.bookid, loadResult.mChapter.catalogid);
                        }
                    }

                    if (null == catalog || !catalog.isAvailable()) {
                        ToastAlone.showShort(R.string.preload_load_fail);
                    } else {
                        ReaderUtils.intoReader(context, catalog, catalog.currentPos);
                    }
                } catch (Exception e) {
                    ALog.printStackTrace(e);
                } finally {
                    if (context instanceof BaseActivity) {
                        ((BaseActivity) context).dissMissDialog();
                    }
                }
            }
        });
    }

    /**
     * 继续阅读
     *
     * @param activity activity
     * @param bookInfo bookInfo
     */
    public static void continueReadBook(final BaseLoadActivity activity, final BookInfo bookInfo) {

        final CatalogInfo catalog = DBUtils.getCatalog(activity, bookInfo.bookid, bookInfo.currentCatalogId);
        if (null != catalog) {
            if (catalog.isAvailable()) {
                //ALog.eDongdz("当前的章节：" + catalog.currentPos);
                ReaderUtils.intoReader(activity, catalog, catalog.currentPos);
            } else {
                if ("0".equals(catalog.isdownload)) {
                    CatalogInfo cinfo = new CatalogInfo(bookInfo.bookid, catalog.catalogid);
                    cinfo.isdownload = "1";
                    DBUtils.updateCatalog(activity, cinfo);
                }

                loadSingle(activity, bookInfo, catalog);
            }
        } else {
            ToastAlone.showShort(activity.getResources().getString(R.string.preload_loading_fail));
        }
    }

    private static void loadSingle(final BaseLoadActivity activity, final BookInfo bookInfo, final CatalogInfo catalogInfo) {
        activity.showDialogByType(DialogConstants.TYPE_GET_DATA);
        RechargeParams rechargeParams = new RechargeParams("3", bookInfo);
        rechargeParams.setOperateFrom(activity.getName());
        rechargeParams.setPartFrom(LogConstants.ORDER_SOURCE_FROM_VALUE_6);

        activity.loadChapter(activity, catalogInfo, bookInfo, rechargeParams);
    }

    /**
     * bookInfo 转 docInfo
     *
     * @param context  context
     * @param bookInfo bookInfo
     * @param chapter  chapter
     * @return docInfo
     */
    public static AkDocInfo generateDoc(Context context, BookInfo bookInfo, CatalogInfo chapter) {
        AkDocInfo docInfo = new AkDocInfo();
        docInfo.bookId = bookInfo.bookid;
        docInfo.bookName = bookInfo.bookname;
        docInfo.chapterId = chapter.catalogid;
        docInfo.chapterName = chapter.catalogname;
        docInfo.path = chapter.path;
        docInfo.currentPos = chapter.currentPos;
        docInfo.isStoreBook = 2 != bookInfo.bookfrom;
        docInfo.chapterStartPos = chapter.startPos;
        docInfo.chapterEndPos = chapter.endPos;

        ArrayList<BookMarkNew> noteList = BookMarkNew.getBookNoteByChapter(context, bookInfo.bookid, chapter.catalogid);
        for (BookMarkNew bean : noteList) {
            docInfo.addLine(new DzLine(bean.startPos, bean.endPos, bean.showText, bean.noteText), false);
        }
        return docInfo;
    }
}
