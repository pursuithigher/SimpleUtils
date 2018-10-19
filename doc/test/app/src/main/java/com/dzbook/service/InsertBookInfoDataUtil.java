package com.dzbook.service;

import android.content.Context;
import android.os.Looper;
import android.text.TextUtils;

import com.dzbook.database.bean.BookInfo;
import com.dzbook.database.bean.CatalogInfo;
import com.dzbook.lib.net.DzSchedulers;
import com.dzbook.lib.utils.ALog;
import com.dzbook.log.LogConstants;
import com.dzbook.model.UserGrow;
import com.dzbook.net.hw.HwRequestLib;
import com.dzbook.utils.DBUtils;
import com.dzbook.utils.ListUtils;
import com.dzbook.utils.WhiteListWorker;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import hw.sdk.net.bean.BeanBookInfo;
import hw.sdk.net.bean.BeanChapterInfo;
import hw.sdk.net.bean.BeanSingleBookInfo;
import hw.sdk.net.bean.store.BeanGetBookInfo;

/**
 * InsertBookInfoDataUtil
 *
 * @author 插入数据库操作
 */
public class InsertBookInfoDataUtil {


    /**
     * 当点击免费阅读或者直接点击章节或者直接点击下载按钮向数据库中插入书籍信息和章节信息
     *
     * @param context         上下文
     * @param listChapterInfo 章节列表
     * @param bookInfo        图书信息
     * @param addBook         是否在书架上显示
     * @param selectChapter   选中的章节。内置章节列表时，先内置选中章节以及以后的部分章节，然后异步内置后续章节。值为空时，直接内置全本。
     * @return BookInfo
     */
    public static BookInfo appendBookAndChapters(Context context, ArrayList<BeanChapterInfo> listChapterInfo,
                                                 BeanBookInfo bookInfo, boolean addBook, BeanChapterInfo selectChapter) {
        return appendBookAndChapters(context, listChapterInfo, bookInfo, addBook, selectChapter, null);
    }

    /**
     * appendBookAndChapters
     *
     * @param context         context
     * @param listChapterInfo listChapterInfo
     * @param bookInfo        bookInfo
     * @param addBook         addBook
     * @param selectChapter   selectChapter
     * @param readerFrom      readerFrom
     * @return BookInfo
     */
    public static BookInfo appendBookAndChapters(Context context, ArrayList<BeanChapterInfo> listChapterInfo,
                                                 BeanBookInfo bookInfo, boolean addBook, BeanChapterInfo selectChapter, String readerFrom) {
        ALog.iLk("appendBookAndChapters-1");

        if (null == context) {
            return null;
        }
        context = context.getApplicationContext();
        // 首先判断是否书籍的bookid是已经在BookInfo数据库中存在
        BookInfo bookInfoBean = DBUtils.findByBookId(context, bookInfo.bookId);
        if (null == bookInfoBean) {

            BookInfo book = initBookInfo(listChapterInfo, bookInfo, addBook, false, readerFrom);
            if (addBook) {
                // 加入书架，同步成长值
                UserGrow.userGrowOnceToday(context, UserGrow.USER_GROW_ADD_BOOK);
            }
            DBUtils.insertBook(context, book);

            bookInfoBean = book;
        } else if (!TextUtils.equals(bookInfoBean.price, bookInfo.price) || addBook) {
            BookInfo book = new BookInfo();
            // 书籍信息写入数据库
            if (!TextUtils.isEmpty(readerFrom)) {
                book.readerFrom = readerFrom;
            }
            if (addBook) {
                book.isAddBook = 2;
                bookInfoBean.isAddBook = 2;
            }

            book.bookid = bookInfo.bookId;
            book.price = bookInfo.price;
            DBUtils.updateBook(context, book);
        }

        appendChapters(context, listChapterInfo, bookInfo.bookId, selectChapter);

        return bookInfoBean;
    }


    /**
     * 追加章节信息。
     *
     * @param ctx             上下文
     * @param listChapterInfo 章节列表
     * @param bookId          图书id
     * @param selectChapter   选中的章节。内置章节列表时，先内置选中章节以及以后的部分章节，然后异步内置后续章节。值为空时，直接内置全本。
     * @return boolean
     */
    public static boolean appendChapters(Context ctx, final List<BeanChapterInfo> listChapterInfo, final String bookId, BeanChapterInfo selectChapter) {
        if (null == ctx || TextUtils.isEmpty(bookId)) {
            return false;
        }

        if (listChapterInfo == null || listChapterInfo.size() == 0) {
            return false;
        }
        final Context context = ctx.getApplicationContext();

        HwRequestLib.flog("appendChapters 0 bookid=" + bookId);
        // 1:如果书籍在BookInfo数据库中存在 则查询数据库中的章节集合 来判断这本书 章节是否已经更新
        // 2:还可以根据服务器返回的stauts:连载/单本（计费状态0/1）来确定 由于服务器现在数据有误 所以用第1种
        // 判断章节集合大小是否一致 如果不一致 则此本书章节已经更新了

        if (listChapterInfo.isEmpty()) {
            return false;
        }

        int size = listChapterInfo.size();

        BeanChapterInfo lastChapter = listChapterInfo.get(size - 1);

        CatalogInfo catalog = DBUtils.getCatalog(context, lastChapter.bookId, lastChapter.chapterId);

        if (catalog == null) {
            int blockSize = size;

            if (selectChapter != null) {
                for (int i = 0; i < blockSize; i++) {
                    BeanChapterInfo netBean = listChapterInfo.get(i);
                    if (netBean != null && TextUtils.equals(selectChapter.chapterId, netBean.chapterId)) {
                        blockSize = Math.min(i + 20, size);
                        break;
                    }
                }
            }

            List<CatalogInfo> beanList = new ArrayList<>();
            for (int i = 0; i < blockSize; i++) {
                CatalogInfo catalogInfo = initCatalogInfo(listChapterInfo.get(i), bookId);
                beanList.add(catalogInfo);
            }
            DBUtils.insertLotCatalog(context, beanList);

            final int start = blockSize;
            if (start < size) {
                BookInfo mBookInfo1 = new BookInfo();
                mBookInfo1.bookid = bookId;
                mBookInfo1.hasRead = 1;
                mBookInfo1.isUpdate = 3; //锁定目录正在更新中 防止检查更新的时候插入重复章节信息
                DBUtils.updateBook(context, mBookInfo1);

                DzSchedulers.child(new Runnable() {
                    @Override
                    public void run() {
                        //批量插入数据库
                        try {
                            Thread.sleep(3500L);
                            List<CatalogInfo> list = new ArrayList<CatalogInfo>();
                            for (int i = start; i < listChapterInfo.size(); i++) {
                                CatalogInfo catalogInfo = initCatalogInfo(listChapterInfo.get(i), bookId);
                                list.add(catalogInfo);
                            }
                            ALog.iLk("start:" + start + "--2--" + listChapterInfo.size());
                            //批量插入数据库
                            boolean isUpdata = DBUtils.insertLotCatalog(context, list);
                            ALog.cmtDebug("isUpdata:" + isUpdata);
                            //解开锁
                            BookInfo mBookInfo = new BookInfo();
                            mBookInfo.bookid = list.get(0).bookid;
                            mBookInfo.hasRead = 2;
                            mBookInfo.isUpdate = 1;
                            DBUtils.updateBook(context, mBookInfo);
                        } catch (Exception e) {
                            ALog.printStackTrace(e);
                        }
                    }
                });
            }
        }
//        int blockSize = 0;
//        if (listChapterInfo != null && listChapterInfo.size() > 0) {
//            ChapterInfo chapterInfo = listChapterInfo.get(listChapterInfo.size() - 1);
//            boolean isNewChapter = false;
//            if (null != chapterInfo) {
//                CatalogInfo catalog = DBUtils.getCatalog(context, chapterInfo.bookId, chapterInfo.chapterId);
//                if (null == catalog) {
//                    isNewChapter = true;
//                }
//            }
//            List<CatalogInfo> beanList = new ArrayList<CatalogInfo>();
//            if (isNewChapter) {
//                //有新的章节需要插入
//                final int size = listChapterInfo.size();
//                blockSize = size;
//                // 本书章节已经更新
//                for (int i = 0; i < blockSize; i++) {
//                    ChapterInfo netBean = listChapterInfo.get(i);
//                    if (null != netBean) {
//                        if (null != selectChapter && null != selectChapter.getChapterId() && selectChapter.getChapterId().equals(netBean.getChapterId())) {
//                            blockSize = Math.min(i + 20, size);
//                        }
//                    }
//                }
//
//            }
//            if (blockSize > 0) {
//                for (int i = 0; i < blockSize; i++) {
//                    CatalogInfo catalogInfo = initCatalogInfo(listChapterInfo.get(i), bookId);
//                    beanList.add(catalogInfo);
//                }
//                DBUtils.insertLotCatalog(context, beanList);
//            }
//            final int start = blockSize;
//            ALog.iLk("start:" + start + "--1--" + listChapterInfo.size());
//            if (start < listChapterInfo.size() && start > 0) {
//                BookInfo mBookInfo1 = new BookInfo();
//                mBookInfo1.bookid = bookId;
//                mBookInfo1.isUpdate = 3; //锁定目录正在更新中 防止检查更新的时候插入重复章节信息
//                DBUtils.updateBook(context, mBookInfo1);
//                new Thread(new Runnable() {
//                    @Override
//                    public void run() {
//                        //批量插入数据库
//                        try {
//                            Thread.sleep(3500L);
//                            List<CatalogInfo> list = new ArrayList<CatalogInfo>();
//                            for (int i = start; i < listChapterInfo.size(); i++) {
//                                CatalogInfo catalogInfo = initCatalogInfo(listChapterInfo.get(i), bookId);
//                                list.add(catalogInfo);
//                            }
//                            ALog.iLk("start:" + start + "--2--" + listChapterInfo.size());
//                            //批量插入数据库
//                            boolean isUpdata = DBUtils.insertLotCatalog(context, list);
//                            //解开锁
//                            BookInfo mBookInfo = new BookInfo();
//                            mBookInfo.bookid = list.get(0).bookid;
//                            mBookInfo.isUpdate = 1;
//                            DBUtils.updateBook(context, mBookInfo);
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                        }
//                    }
//                }).start();
//            }
//        }
        return true;
    }

    /**
     * initCatalogInfo
     *
     * @param chapterBean chapterBean
     * @param bookId      bookId
     * @return CatalogInfo
     */
    public static CatalogInfo initCatalogInfo(BeanChapterInfo chapterBean, String bookId) {

        CatalogInfo catalogInfo = new CatalogInfo(bookId, chapterBean.chapterId);

        if ("0".equals(chapterBean.isCharge)) {
            catalogInfo.setIspay("1"); // 是否收费(0代表收费,1代表不收费)
        } else {
            catalogInfo.setIspay("0"); // 是否收费(0代表收费,1代表不收费)
        }

        catalogInfo.isread = "1"; // 是否已读(0代表已读,1代表未读)
//        catalogInfo.isalreadypay = "1"; // 是否已扣费(0代表已扣费,1代表未扣费)
        catalogInfo.isdownload = "1"; // 是否已下载(0代表已下载,1代表未下载)
        catalogInfo.catalogname = chapterBean.chapterName; // 章节名称
//        catalogInfo.isupload = "1";// 是否已上传 0(已上传) 1(未上传)
//        catalogInfo.ispayupload = "1";// 是否已上传 0(已上传) 1(未上传)
//        catalogInfo.payUrl = chapterBean.getUrl();
//        if (!TextUtils.isEmpty(chapterBean.getNew_url())) {
//            catalogInfo.newUrl = chapterBean.getNew_url();// 新增字段)支付url
//            catalogInfo.isNewPayUrl = "0";// 新增字段)支付url
//        } else {
//            catalogInfo.newUrl = "-1";
//            catalogInfo.isNewPayUrl = "1";// 新增字段)支付url
//        }
//        catalogInfo.catalogfrom = chapterBean.getSource();// 来源
//        catalogInfo.preIsdownload = "1";

        return catalogInfo;
    }

    /**
     * 书籍插入数据库（native处理）
     *
     * @param context         ：上下文
     * @param beanGetBookInfo ：书籍列表
     * @param ghType          :固话type：WhiteListWorker.BOOK_LING_QU_VALUE
     */
    public static void insertNativeBook(Context context, BeanGetBookInfo beanGetBookInfo, String ghType) {
        insertWebBook(context, beanGetBookInfo, ghType, "");
    }

    /**
     * 书籍插入数据库（web处理）
     *
     * @param context         ：上下文
     * @param beanGetBookInfo ：书籍列表
     * @param ghType          :固话type：WhiteListWorker.BOOK_LING_QU_VALUE
     * @param ghWeb           ：固话web：
     */
    public static void insertWebBook(final Context context, final BeanGetBookInfo beanGetBookInfo, final String ghType, final String ghWeb) {
        if (Looper.getMainLooper() == Looper.myLooper()) {
            DzSchedulers.child(new Runnable() {
                @Override
                public void run() {
                    insertWebBookInner(context, beanGetBookInfo, ghType, ghWeb);
                }
            });
        } else {
            insertWebBookInner(context, beanGetBookInfo, ghType, ghWeb);
        }
    }

    /**
     * 书籍插入数据库（web处理）
     *
     * @param context：上下文
     * @param beanGetBookInfo：书籍列表
     * @param ghType               :固话type：WhiteListWorker.BOOK_LING_QU_VALUE
     * @param ghWeb：固话web：
     */
    private static void insertWebBookInner(final Context context, final BeanGetBookInfo beanGetBookInfo, final String ghType, final String ghWeb) {
        if (beanGetBookInfo.isContainItems()) {
            //批量插入 效率高
            final List<BookInfo> bookInfos = new ArrayList<BookInfo>();
            final List<CatalogInfo> chapterList = new ArrayList<CatalogInfo>();
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put(LogConstants.GH_TYPE, ghType);
                jsonObject = WhiteListWorker.setPnPi(context, jsonObject);
                if (!TextUtils.isEmpty(ghWeb)) {
                    jsonObject.put(LogConstants.GH_WEB, ghWeb);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            for (int i = 0; i < beanGetBookInfo.books.size(); i++) {
                BeanSingleBookInfo packBookInfo = beanGetBookInfo.books.get(i);
                BeanBookInfo objectbook = packBookInfo.bookInfo;
                ArrayList<BeanChapterInfo> chapterList1 = packBookInfo.chapterList;
                BookInfo bookInfo = initBookInfo(chapterList1, objectbook, true, false);
                int size = beanGetBookInfo.books.size();
                bookInfo.time = String.valueOf(System.currentTimeMillis() + (size - i) + 500);
                bookInfo.readerFrom = jsonObject.toString();
                //书籍信息
                bookInfos.add(bookInfo);
                //caimt 章节信息
                if (!ListUtils.isEmpty(chapterList1)) {
                    for (int k = 0; k < chapterList1.size(); k++) {
                        CatalogInfo catalogInfo = initCatalogInfo(chapterList1.get(k), objectbook.bookId);
                        chapterList.add(catalogInfo);
                    }
                }
            }
            //批量插入
            DBUtils.insertBooks(context, bookInfos);
            DBUtils.insertLotCatalog(context, chapterList);
            ALog.cmtDebug("packbook:更新书籍成功");
        }
    }

    /**
     * initBookInfo
     *
     * @param currentCatalogId currentCatalogId
     * @param bookInfo         bookInfo
     * @param addBook          addBook
     * @param isdefautbook     isdefautbook
     * @param readerFrom       readerFrom
     * @return BookInfo
     */
    public static BookInfo initBookInfo(String currentCatalogId, BeanBookInfo bookInfo, boolean addBook, boolean isdefautbook, String readerFrom) {
        // 将书籍信息插入数据库中
        BookInfo book = new BookInfo();
        if (!TextUtils.isEmpty(readerFrom)) {
            book.readerFrom = readerFrom;
        }
        book.bookid = bookInfo.bookId;
        book.author = bookInfo.author;
        book.time = System.currentTimeMillis() + "";
        // 来源 2(本地) 1(网络)
        book.bookfrom = 1;
        book.control = bookInfo.control;
        book.isUpdate = 1;
        if (bookInfo.isSingleBook()) {
            book.bookstatus = 1;
        } else {
            book.bookstatus = 2;
            book.isEnd = 1;
        }
        book.price = bookInfo.price;
        book.bookname = bookInfo.bookName;
        book.coverurl = bookInfo.coverWap;
        //自有支付系统添加的字段
        book.setRechargeParams(bookInfo.payTips, 1);
        // 书架默认(2:true,1:false)
        book.isdefautbook = isdefautbook ? 2 : 1;
        // 是否显示在书架上 2:true,1:false
        book.isAddBook = addBook ? 2 : 1;
        // 判断是否这本书籍是否支付过(2代表已支付过,1代表未支付过)
        book.payStatus = 1;
        // 判断是否这本书籍是否确认订购过(2代表已确认订购过,1代表未确认订购过)
        book.confirmStatus = 1;

        //书籍是否支持横屏模式 1.支持，2：不支持
        if (bookInfo.isSupportH == 0) {
            //设置默认值
            book.isSupportH = 1;
        } else {
            book.isSupportH = bookInfo.isSupportH;
        }

        if (!TextUtils.isEmpty(currentCatalogId)) {
            book.currentCatalogId = currentCatalogId;
        }
        //为了目录更新 是否打开过 2(未打开过) 1(已经打开过)
        book.hasRead = 2;
        return book;
    }

    /**
     * BookInfo
     *
     * @param listChapterInfo listChapterInfo
     * @param bookInfo        bookInfo
     * @param addBook         addBook
     * @param isdefautbook    isdefautbook
     * @param readerFrom      readerFrom
     * @return BookInfo
     */
    public static BookInfo initBookInfo(ArrayList<BeanChapterInfo> listChapterInfo,
                                        BeanBookInfo bookInfo, boolean addBook, boolean isdefautbook, String readerFrom) {
        String currentCatalogId = "";
        // 当前阅读章节(默认第一张，由阅读器更新)
        if (listChapterInfo != null && listChapterInfo.size() > 0) {
            currentCatalogId = listChapterInfo.get(0).chapterId;
        }
        return initBookInfo(currentCatalogId, bookInfo, addBook, isdefautbook, readerFrom);

    }

    /**
     * initBookInfo
     *
     * @param listChapterInfo listChapterInfo
     * @param bookInfo        bookInfo
     * @param addBook         addBook
     * @param isdefautbook    isdefautbook
     * @return BookInfo
     */
    public static BookInfo initBookInfo(ArrayList<BeanChapterInfo> listChapterInfo,
                                        BeanBookInfo bookInfo, boolean addBook, boolean isdefautbook) {
        return initBookInfo(listChapterInfo, bookInfo, addBook, isdefautbook, null);
    }


    //================================================信息流 插入书籍 开始。。。。===================================================================

    /**
     * appendFastBookData
     *
     * @param context       上下文
     * @param chapterList   章节列表
     * @param bookBean      图书信息
     * @param addBook       是否在书架上显示
     * @param selectChapter 选中的章节。内置章节列表时，先内置选中章节以及以后的部分章节，然后异步内置后续章节。值为空时，直接内置全本。
     * @return boolean
     */
    public static boolean appendFastBookData(Context context, ArrayList<BeanChapterInfo> chapterList,
                                             BeanBookInfo bookBean, boolean addBook, BeanChapterInfo selectChapter) {
        ALog.iLk("appendFastBookData-1");
        ArrayList<BeanChapterInfo> list = new ArrayList<>();
        BeanBookInfo bookDetailBean = new BeanBookInfo();
        bookDetailBean.author = bookBean.author;
        bookDetailBean.bookId = bookBean.bookId;
        bookDetailBean.bookName = bookBean.bookName;
        bookDetailBean.coverWap = bookBean.coverWap;
        bookDetailBean.price = bookBean.price;
        bookDetailBean.unit = bookBean.unit;
        bookDetailBean.control = bookBean.control;
        bookDetailBean.payTips = bookBean.payTips;
//        bookDetailBean.payTips = bookBean.pay_tips;

        if (chapterList != null && chapterList.size() > 0) {
            for (BeanChapterInfo bean : chapterList) {
                if (bean != null) {
                    BeanChapterInfo chapterInfo = new BeanChapterInfo();
                    chapterInfo.chapterId = bean.chapterId;
                    chapterInfo.chapterName = bean.chapterName;
                    chapterInfo.isCharge = bean.isCharge;
//                    chapterInfo.source = bean.source;
//                    chapterInfo.url = bean.url;
//                    chapterInfo.new_url = bean.new_url;
                    list.add(chapterInfo);
                }
            }
        }
        appendBookAndChapters(context, list, bookDetailBean, addBook, selectChapter);
        return true;
    }
    //================================================信息流 插入书籍 结束。。。。===================================================================

}
