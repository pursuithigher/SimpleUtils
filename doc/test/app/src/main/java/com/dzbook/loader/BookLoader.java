package com.dzbook.loader;

import android.app.Activity;
import android.content.Context;
import android.os.SystemClock;
import android.text.TextUtils;

import com.dzbook.activity.reader.ReaderUtils;
import com.dzbook.database.bean.BookInfo;
import com.dzbook.database.bean.CatalogInfo;
import com.dzbook.event.EventBus;
import com.dzbook.lib.net.DzSchedulers;
import com.dzbook.lib.net.OkhttpUtils;
import com.dzbook.lib.utils.ALog;
import com.dzbook.lib.utils.UtilTimeOffset;
import com.dzbook.log.DzLog;
import com.dzbook.log.LogConstants;
import com.dzbook.mvp.DialogConstants;
import com.dzbook.net.hw.HwRequestLib;
import com.dzbook.service.InsertBookInfoDataUtil;
import com.dzbook.service.PayUploadData;
import com.dzbook.service.RechargeParams;
import com.dzbook.utils.DBUtils;
import com.dzbook.utils.FileUtils;
import com.dzbook.utils.HwLog;
import com.dzbook.utils.NetworkUtils;
import com.dzbook.utils.NumberUtils;
import com.dzbook.utils.SpUtil;
import com.dzbook.utils.hw.LoginUtils;
import com.dzpay.recharge.netbean.LoadChaptersCommonBeanInfo;
import com.dzpay.recharge.netbean.LotPayOrderBeanInfo;
import com.dzpay.recharge.netbean.PayOrderChapterBeanInfo;
import com.dzpay.recharge.netbean.SingleOrderBeanInfo;
import com.iss.app.BaseActivity;
import com.iss.view.common.ToastAlone;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import hw.sdk.net.bean.BeanBookInfo;
import hw.sdk.net.bean.BeanChapterInfo;
import hw.sdk.net.bean.FastOpenBook;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;
import okhttp3.OkHttpClient;


/**
 * BookLoader
 *
 * @author wxliao on 17/8/1.
 */

public class BookLoader extends BaseLoader {
    private static volatile Context mContext;

    private static final String TAG = "BookLoader";


    private static volatile BookLoader instance;

    private OkHttpClient mClient;

    private LoaderForDz dzLoader;

    private BulkLoadWorker bulkLoadWorker;

    private BookLoader() {
        super(mContext);
        bulkLoadWorker = new BulkLoadWorker();


        mClient = OkhttpUtils.generateClient();

        dzLoader = new LoaderForDz(mContext, mClient);
    }

    /**
     * 获取BookLoader实例
     *
     * @return 实例
     */
    public static BookLoader getInstance() {
        if (mContext == null) {
            throw new RuntimeException("需要先调用 register(Context context) 初始化");
        }
        if (instance == null) {
            synchronized (BookLoader.class) {
                if (instance == null) {
                    instance = new BookLoader();
                }
            }
        }
        return instance;
    }

    /**
     * 初始化
     *
     * @param context context
     */
    public static void init(Context context) {
        mContext = context;
    }

    public LoaderForDz getDzLoader() {
        return dzLoader;
    }

    /**
     * 快速打开书籍 使用位置：
     * 1：书籍详情页面 免费试读第一章
     * 2：云书架 加入书架后继续阅读 阅读第一章
     * 3：信息流列表直接点开 开始阅读
     *
     * @param bookId  bookId
     * @param context context
     * @param addBook addBook
     * @return LoadResult
     */
    public LoadResult fastReadChapter(Context context, String bookId, boolean addBook) {
        return fastReadChapter(context, bookId, "", addBook);
    }

    /**
     * 快速打开书籍 使用位置：
     * 1：书籍详情页面 免费试读第一章
     * 2：云书架 加入书架后继续阅读 阅读第一章
     * 3：信息流列表直接点开 开始阅读
     *
     * @param bookId    bookId
     * @param context   context
     * @param addBook   addBook
     * @param chapterId chapterId
     * @return LoadResult
     */
    public LoadResult fastReadChapter(Context context, String bookId, String chapterId, boolean addBook) {

        try {
            // 使用快速加载接口，一次性获取图书信息，章节列表信息，章节内容信息
            FastOpenBook fastOpenBook = HwRequestLib.getInstance().fastOpenBookRequest(bookId, chapterId);
            return insertLocal(context, fastOpenBook, addBook);

        } catch (Exception e) {
            ALog.printStackTrace(e);
        }

        return new LoadResult(LoadResult.STATUS_ERROR);
    }

    /**
     * 封装核心插入本地的方法
     *
     * @param context      context
     * @param fastOpenBook 快速bean
     * @param addBook      是否加入书架
     * @return LoadResult
     */
    public LoadResult insertLocal(Context context, FastOpenBook fastOpenBook, boolean addBook) {
        LoadResult loadResult = new LoadResult(LoadResult.STATUS_ERROR);
        if (fastOpenBook != null && fastOpenBook.isAValid()) {
            BeanBookInfo book = fastOpenBook.book;
            ArrayList<BeanChapterInfo> chapterList = fastOpenBook.chapterList;
            InsertBookInfoDataUtil.appendFastBookData(context, chapterList, book, addBook, null);

            int listSize = book.contentList.size();
            for (int i = 0; i < listSize; i++) {
                BeanChapterInfo curBook = book.contentList.get(i);
                String pageContent = curBook.content;
                String chapterId = curBook.chapterId;
                if (!TextUtils.isEmpty(chapterId)) {
                    String path = BookLoader.BOOK_DIR_PATH + book.bookId + "/" + chapterId + ".kf";
                    boolean isSave = FileUtils.writeToLocalContent(pageContent, path);

                    if (isSave) {
                        CatalogInfo catalogInfo = new CatalogInfo(book.bookId, chapterId);
                        catalogInfo.dlTime = UtilTimeOffset.getDateFormatSev();
                        catalogInfo.isdownload = "0";
                        catalogInfo.path = path;
                        DBUtils.updateCatalog(mContext, catalogInfo);
                        //第一个章节
                        if (TextUtils.equals(chapterId, book.contentList.get(0).chapterId)) {
                            loadResult = new LoadResult(LoadResult.STATUS_SUCCESS);
                            loadResult.mChapter = catalogInfo;
                        }
                    }
                }
            }
        }
        return loadResult;
    }

    /**
     * 单章加载
     *
     * @param context        context
     * @param bookInfo       bookInfo
     * @param catalogInfo    catalogInfo
     * @param rechargeParams rechargeParams
     * @return LoadResult
     */
    public LoadResult loadOneChapter(final Activity context, final BookInfo bookInfo, final CatalogInfo catalogInfo, final RechargeParams rechargeParams) {

        LoadResult loadResult = loadOneChapterInner(context, bookInfo, catalogInfo, rechargeParams);

        if (!loadResult.isSuccess()) {
            //处理需要登录的流程
            try {
                final String resultKey = "RESULT_KEY";
                final Map<String, LoadResult> map = new HashMap<String, LoadResult>(16);
                final CountDownLatch latch = new CountDownLatch(1);

                if (loadResult.isNeedLogin) {
                    addloadLog("需要登录");
                    if (!NetworkUtils.getInstance().checkNet()) {
                        if (context instanceof BaseActivity) {
                            ((BaseActivity) context).showNotNetDialog();
                        }
                    } else {
                        LoginUtils.getInstance().forceLoginCheck(context, new LoginUtils.LoginCheckListenerSub() {
                            @Override
                            public void loginComplete() {
                                addloadLog("登陆完成");
                                DzSchedulers.child(new Runnable() {
                                    @Override
                                    public void run() {
                                        LoadResult mLoginSuccessLoadResult = loadOneChapterInner(context, bookInfo, catalogInfo, rechargeParams);
                                        map.put(resultKey, mLoginSuccessLoadResult);
                                        addloadLog("登录完成加载完成");
                                        latch.countDown();
                                    }
                                });
                            }

                            @Override
                            public void loginFail() {
                                addloadLog("登录失败");
                                latch.countDown();
                            }
                        });
                    }
                    boolean wait = latch.await(90, TimeUnit.SECONDS);
                    addloadLog("登录流程处理完，返回结果 wait=" + wait);
                    if (map.containsKey(resultKey)) {
                        loadResult = map.get(resultKey);
                    } else {
                        loadResult.message = "";
                    }
                    addloadLog("登录流程处理完，返回结果loadResult");
                }

            } catch (Exception e) {
                ALog.printStackTrace(e);
            }
        }

        return loadResult;
    }

    /**
     * 单章加载
     *
     * @param context        context
     * @param bookInfo       bookInfo
     * @param catalogInfo    catalogInfo
     * @param rechargeParams rechargeParams
     * @return LoadResult
     */
    private LoadResult loadOneChapterInner(final Activity context, final BookInfo bookInfo, final CatalogInfo catalogInfo, final RechargeParams rechargeParams) {
        if (bookInfo.isShowOffShelf(mContext)) {
            return new LoadResult(LoadResult.STATUS_ERROR_BOOK_OFF);
        }

        //自有支付

        HwRequestLib.flog("loadOneChapter start，bookId:" + bookInfo.bookid + ",bookName:" + bookInfo.bookname + ",chapterId:" + catalogInfo.catalogid);

        addloadLog("自有支付");
        final String descFrom = PayUploadData.loadRechargeSingle(mContext);
        LoadResult loadResult = dzLoader.dzRechargePay(context, bookInfo, catalogInfo, descFrom, rechargeParams);

        if (loadResult.isSuccess()) {

            SingleOrderBeanInfo orderBeanInfo = null;
            String json = "";
            try {
                json = loadResult.json;
                orderBeanInfo = new SingleOrderBeanInfo().parseJSON(new JSONObject(json));

                //设置预加载数量
                if (orderBeanInfo != null) {
                    addloadLog("设置自有支付预加载数量:" + orderBeanInfo.preloadNum);
                    SpUtil.getinstance(context).setDzPayPreloadNum(orderBeanInfo.preloadNum);

                }
            } catch (Exception e) {
                ALog.printStack(e);
            }

            if (orderBeanInfo == null || orderBeanInfo.chapterInfos == null || orderBeanInfo.chapterInfos.size() <= 0) {
                return new LoadResult(LoadResult.STATUS_ERROR_236);
            }

            addloadLog("开始处理或下载当前章节");
            loadResult = loadDzPayChapter(orderBeanInfo.chapterInfos, bookInfo, catalogInfo.catalogid);
            loadResult.json = json;
            if (loadResult.isSuccess()) {
                //异步处理 章节更新和下载
                if (orderBeanInfo.isExistChapterInfos()) {
                    addloadLog("异步处理 章节更新和下载,章节数量:" + orderBeanInfo.chapterInfos.size() + ",章节信息为：" + orderBeanInfo.chapterInfos.toString());
                    asyncHandleLotChapterAndAppendChaptes(context, orderBeanInfo.chapterInfos, bookInfo, catalogInfo, false);
                }
            }

            try {
                for (PayOrderChapterBeanInfo bean : orderBeanInfo.chapterInfos) {

                    //1：全本；2：批量章节；3：单章
                    String buyType = bookInfo.isSingleBook() ? "1" : "3";
                    String sumCost = NumberUtils.numberConversion(bean.rCost + bean.vCost, 100);
                    String vCost = NumberUtils.numberConversion(bean.vCost, 100);
                    String rCost = NumberUtils.numberConversion(bean.rCost, 100);
                    HwLog.buyBook(bookInfo.bookid, bookInfo.bookname, buyType, "1", sumCost, vCost, rCost, "0");
                }
            } catch (Exception e) {
                ALog.printStackTrace(e);
            }
        }

        return loadResult;
    }

    /**
     * 批量下载
     *
     * @param context        context
     * @param bookInfo       bookInfo
     * @param catalogInfo    catalogInfo
     * @param rechargeParams rechargeParams
     * @return LoadResult
     */

    public LoadResult loadBulkChapters(Activity context, BookInfo bookInfo, CatalogInfo catalogInfo, RechargeParams rechargeParams) {
        if (bookInfo.isShowOffShelf(mContext)) {
            return new LoadResult(LoadResult.STATUS_ERROR_BOOK_OFF);
        }

        if (bookInfo.isVipFree(mContext)) {
            return new LoadResult(LoadResult.STATUS_ERROR_BULK_DISABLE_FOR_VIP);
        }

        if (bookInfo.isFreeStatus(mContext)) {
            return new LoadResult(LoadResult.STATUS_ERROR_BULK_DISABLE);
        }


        addloadLog("自有支付——批量下载/全本下载");

        String descFrom = PayUploadData.loadRecharge(mContext);
        LoadResult loadResult = dzLoader.dzRechargePay(context, bookInfo, catalogInfo, descFrom, rechargeParams);

        if (loadResult.isSuccess()) {

            LotPayOrderBeanInfo orderBeanInfo = null;
            try {

                orderBeanInfo = new LotPayOrderBeanInfo().parseJSON(new JSONObject(loadResult.json));

            } catch (Exception e) {
                ALog.printStack(e);
            }

            if (orderBeanInfo == null || orderBeanInfo.chapterInfos == null || orderBeanInfo.chapterInfos.size() <= 0) {
                return new LoadResult(LoadResult.STATUS_ERROR_236);
            }


            loadResult = loadDzPayChapter(orderBeanInfo.chapterInfos, bookInfo, catalogInfo.catalogid);
            if (loadResult.isSuccess()) {
                //异步处理 章节更新和下载

                if (orderBeanInfo.isExistChapterData()) {
                    addloadLog("异步处理 章节更新和下载,章节数量:" + orderBeanInfo.chapterInfos.size() + ",章节信息为：" + orderBeanInfo.chapterInfos.toString());
                    asyncHandleLotChapterAndAppendChaptes(context, orderBeanInfo.chapterInfos, bookInfo, catalogInfo, true);
                }

            }

            //                return loadResult;
        }
        return loadResult;
    }

    /**
     * 加载付费章节
     *
     * @param list      list
     * @param bookInfo  bookInfo
     * @param chapterId chapterId
     * @return LoadResult
     */
    public LoadResult loadDzPayChapter(ArrayList<PayOrderChapterBeanInfo> list, BookInfo bookInfo, String chapterId) {

        PayOrderChapterBeanInfo toLoadChapter = null;

        int length = list.size();
        for (int i = 0; i < length; i++) {
            PayOrderChapterBeanInfo bean = list.get(i);
            if (TextUtils.equals(bean.chapterId, chapterId)) {
                toLoadChapter = bean;
                break;
            }
        }

        if (toLoadChapter != null) {
            addloadLog("开始处理或下载当前章节,当前章节状态：" + toLoadChapter.chapterStatus + ",1.正常 2.被删除 3.缺内容，未领取 4.缺内容，已领取");
            LoadResult loadResult = new LoadResult(LoadResult.STATUS_ERROR);
            if (toLoadChapter.chapterStatus == 1) {
                CatalogInfo bean = DBUtils.getCatalog(mContext, bookInfo.bookid, toLoadChapter.chapterId);
                loadResult = dzLoader.loadCdnFile(bookInfo, bean, toLoadChapter.cdnUrl, toLoadChapter.backupUrls);
            } else if (toLoadChapter.chapterStatus == 2) {
                //单章加载即使服务器返回的是删除状态  客户端不做删除 只做章节更新为已经删除状态，为了避免用户体验问题
                CatalogInfo updateChapter = new CatalogInfo(bookInfo.bookid, toLoadChapter.chapterId);
                updateChapter.isdownload = "4";
                DBUtils.updateCatalog(mContext, updateChapter);
                loadResult = new LoadResult(LoadResult.STATUS_SUCCESS);
            } else if (toLoadChapter.chapterStatus == 3) {
                CatalogInfo updateChapter = new CatalogInfo(bookInfo.bookid, toLoadChapter.chapterId);
                updateChapter.isdownload = "2";
                DBUtils.updateCatalog(mContext, updateChapter);
                loadResult = new LoadResult(LoadResult.STATUS_SUCCESS);
            } else if (toLoadChapter.chapterStatus == 4) {
                CatalogInfo updateChapter = new CatalogInfo(bookInfo.bookid, toLoadChapter.chapterId);
                updateChapter.isdownload = "3";
                DBUtils.updateCatalog(mContext, updateChapter);
                loadResult = new LoadResult(LoadResult.STATUS_SUCCESS);
            }
            return loadResult;
        }
        return new LoadResult(LoadResult.STATUS_ERROR);
    }

    /**
     * 异步处理章节下载 为了保证下载第一个章节后跳转阅读器
     * 为了保证书籍详情页面批量下载100章节之后拼接后续章节
     *
     * @param context        context
     * @param list           list
     * @param bookInfo       bookInfo
     * @param catalogInfo    catalogInfo
     * @param appendChapters 是否需要插入所有章节信息
     */
    public void asyncHandleLotChapterAndAppendChaptes(final Activity context, final ArrayList<PayOrderChapterBeanInfo> list, final BookInfo bookInfo, final CatalogInfo catalogInfo, final boolean appendChapters) {

        DzSchedulers.child(new Runnable() {
            @Override
            public void run() {

                if (appendChapters) {
                    //拼接本地最后章节之后所有的章节信息
                    addloadLog("110接口拼接本地最后章节之后所有的章节信息");

                    String startChapter = "";
                    CatalogInfo lastCatalog = DBUtils.getLastCatalog(context, bookInfo.bookid);
                    if (lastCatalog != null) {
                        startChapter = lastCatalog.catalogid;
                    }
                    List<BeanChapterInfo> chapters = BookLoader.getInstance().getChaptersFromServer(bookInfo, startChapter, "0");
                    if (chapters != null && chapters.size() > 0) {
                        InsertBookInfoDataUtil.appendChapters(context, chapters, bookInfo.bookid, null);
                    }
                }

                //批量下载的章节  这里需要采用批量更新  速度快
                //                ArrayList<PayOrderChapterBeanInfo> bulkList = new ArrayList<>();
                int length = list.size();
                for (int i = 0; i < length; i++) {
                    PayOrderChapterBeanInfo bean = list.get(i);
                    if (!TextUtils.equals(bean.chapterId, catalogInfo.catalogid)) {

                        addDzPayChapterToLoadQueue(bookInfo, bean);
                    }
                }


            }
        });
    }

    /**
     * 预加载，带重试
     *
     * @param context   context
     * @param bookInfo  bookInfo
     * @param catalogId catalogId
     */
    public void preLoadWithRetry(Context context, BookInfo bookInfo, String catalogId) {
        if (context == null || bookInfo == null || TextUtils.isEmpty(catalogId)) {
            return;
        }
        if (bookInfo.bookfrom != 1) {
            //非网络书籍，不需要预加载
            return;
        }
        Context applicationContext = context.getApplicationContext();
        if (bookInfo.isShowOffShelf(applicationContext)) {
            //下架书籍，不需要预加载
            return;
        }
        CatalogInfo nextChapter = DBUtils.getNextCatalog(applicationContext, bookInfo.bookid, catalogId);
        //暂时不加，如果同步没问题，这里可以考虑放开
//        if (nextChapter == null) {
//            new CheckBookshelfUpdateRunnable(applicationContext, bookInfo.bookid).run();
//            nextChapter = DBUtils.getNextCatalog(applicationContext, bookInfo.bookid, catalogId);
//        }
        if (nextChapter == null) {
            return;
        }
        int retryTimes = 2;
        for (int i = 0; i < retryTimes; i++) {
            boolean success = preLoad(applicationContext, bookInfo, nextChapter);
            if (success) {
                break;
            } else {
                SystemClock.sleep(1000);
            }
        }
    }

    /**
     * 预加载
     *
     * @param context     context
     * @param bookInfo    bookInfo
     * @param catalogInfo 当前阅读章节的下一个章节
     * @return 是否成功
     */
    private boolean preLoad(Context context, BookInfo bookInfo, CatalogInfo catalogInfo) {
        addloadLog("自有预加载开始");

        //更新章节数据
        HwRequestLib.flog("自有预加载 start，bookId:" + bookInfo.bookid + ",bookName:" + bookInfo.bookname + ",chapterId:" + catalogInfo.catalogid);

        try {

            int preloadNum = SpUtil.getinstance(context).getDzPayPreloadNum();
            addloadLog("自有预加载，预加载数量为：" + preloadNum);

            ArrayList<CatalogInfo> needDownList = DBUtils.getDzPayNeedDownChapters(context, catalogInfo, preloadNum);

            if (null == needDownList || needDownList.isEmpty()) {
                addloadLog("自有预加载，预加载数量为：" + preloadNum + ",数据库查询出的数量为0章");
                return true;
            }

            addloadLog("自有预加载，预加载数量为：" + preloadNum + ",数据库查询出的数量为：" + needDownList.size() + ",章节集合为：" + needDownList.toString() + "，----->>>>>>> autoPay:" + bookInfo.payRemind);

            ArrayList<String> ids = new ArrayList<>();
            for (CatalogInfo bean : needDownList) {
                ids.add(bean.catalogid);
            }

            LoadChaptersCommonBeanInfo preloadInfo = HwRequestLib.getInstance().preloadLotChapterBeanInfo(bookInfo.bookid, ids, bookInfo.payRemind + "");
            if (preloadInfo == null) {
                return false;
            }
            if (preloadInfo.isSuccess()) {
                //更新预加载数量
                addloadLog("自有预加载，服务器返回数据:" + preloadInfo.toString());

                if (preloadInfo.preloadNum > 0) {
                    SpUtil.getinstance(context).setDzPayPreloadNum(preloadInfo.preloadNum);
                }

                if (preloadInfo.isExistChapterData()) {

                    addloadLog("自有预加载，237接口返回需要预加载数量为：" + preloadInfo.chapterInfos.size() + ",下发章节信息为：" + preloadInfo.chapterInfos.toString());

                    for (PayOrderChapterBeanInfo orderChapter : preloadInfo.chapterInfos) {
                        addDzPayChapterToLoadQueue(bookInfo, orderChapter);
                    }
                }

                try {
                    for (PayOrderChapterBeanInfo bean : preloadInfo.chapterInfos) {
                        String buyType = bookInfo.isSingleBook() ? "1" : "3";
                        String sumCost = NumberUtils.numberConversion(bean.rCost + bean.vCost, 100);
                        String vCost = NumberUtils.numberConversion(bean.vCost, 100);
                        String rCost = NumberUtils.numberConversion(bean.rCost, 100);
                        HwLog.buyBook(bookInfo.bookid, bookInfo.bookname, buyType, "1", sumCost, vCost, rCost, "0");
                    }
                } catch (Exception e) {
                    ALog.printStackTrace(e);
                }


            }
            return true;
        } catch (Exception e) {
            ALog.printStack(e);
        }
        return false;
    }

    /**
     * 下载已购章节
     *
     * @param context     context
     * @param bookInfo    bookInfo
     * @param catalogInfo catalogInfo
     * @return 如果返回数据为null，说明获取数据失败，直接提示错误
     * 如果List不为null，但是List里没有数据，说明没有后续可下载章节
     */
    public LoadResult loadPurchasedChapters(Activity context, BookInfo bookInfo, CatalogInfo catalogInfo) {
        if (mContext == null) {
            mContext = context.getApplicationContext();
        }

        if (bookInfo.isShowOffShelf(mContext)) {
            return new LoadResult(LoadResult.STATUS_ERROR_BOOK_OFF);
        }

        if (bookInfo.isFreeStatus(mContext)) {
            return new LoadResult(LoadResult.STATUS_ERROR_BULK_DISABLE);
        }

        if (catalogInfo == null) {
            return new LoadResult(LoadResult.STATUS_ERROR_CHAPTER);
        }

        ArrayList<String> idList = null;
        try {
            LoadChaptersCommonBeanInfo beanInfo = HwRequestLib.getInstance().loadAlreadyOrderChapterBeanInfo(bookInfo.bookid, catalogInfo.catalogid);
            if (beanInfo != null && beanInfo.isSuccess()) {
                idList = new ArrayList<>();
                //过滤已经下载了的章节
                List<String> catalogIds = beanInfo.getCatalogIds();
                if (catalogIds != null && catalogIds.size() > 0) {
                    Map<String, CatalogInfo> map = DBUtils.getMapCatalogByBookIdLimitCatalog(mContext, catalogInfo);

                    for (PayOrderChapterBeanInfo orderChapter : beanInfo.chapterInfos) {
                        if (map.containsKey(orderChapter.chapterId) && !map.get(orderChapter.chapterId).isAvailable()) {
                            idList.add(orderChapter.chapterId);
                            addDzPayChapterToLoadQueue(bookInfo, orderChapter);
                        }
                    }
                }
            }
        } catch (Exception e) {
            ALog.printStackTrace(e);
        }
        return new LoadResult(LoadResult.STATUS_SUCCESS, idList);
    }

    private void addDzPayChapterToLoadQueue(BookInfo bookInfo, PayOrderChapterBeanInfo orderChapter) {
        if (!bulkLoadWorker.isStarted()) {
            bulkLoadWorker.start();
        }

        bulkLoadWorker.enqueue(new BulkLoadInfo(bookInfo, orderChapter.chapterId, orderChapter.cdnUrl, orderChapter.backupUrls, orderChapter.chapterStatus));
    }

    /**
     * 清除下载队列
     */
    public void clearLoadQueue() {
        if (bulkLoadWorker != null) {
            bulkLoadWorker.clear();
        }
    }

    /**
     * BulkLoadWorker
     */
    class BulkLoadWorker implements Runnable {
        BulkLoadInfo bean;
        private BlockingQueue<BulkLoadInfo> queue = new LinkedBlockingQueue<>();
        private Set<String> taskIds = Collections.newSetFromMap(new ConcurrentHashMap<String, Boolean>());
        private volatile boolean started;

        void enqueue(BulkLoadInfo bean1) {
            if (taskIds.add(bean1.getUniqueId())) {
                try {
                    queue.put(bean1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        boolean isStarted() {
            synchronized (this) {
                return started;
            }
        }

        void start() {
            synchronized (this) {
                DzSchedulers.child(this);
                started = true;
            }
        }

        void clear() {
            queue.clear();
            taskIds.clear();
        }


        @Override
        public void run() {
            try {
                while ((bean = queue.take()) != null) {

                    final BookInfo bookInfo = bean.bookInfo;
                    final CatalogInfo catalogInfo = DBUtils.getCatalog(mContext, bookInfo.bookid, bean.catalogId);
                    if (catalogInfo != null) {
                        if (bean.chapterStatus == 1) {
                            LoadResult loadResult = dzLoader.loadCdnFile(bookInfo, catalogInfo, bean.cdnUrl, bean.backUrls);
                            loadResult.mChapter = catalogInfo;
                            EventBus.getDefault().post(loadResult);
                        } else if (bean.chapterStatus == 2) {
                            DBUtils.deleteCatalog(mContext, bookInfo.bookid, bean.catalogId);

                            dzEventDeleteChapter(bean.bookInfo, bean.catalogId);

                            LoadResult loadResult = new LoadResult(LoadResult.STATUS_SUCCESS);
                            loadResult.mChapter = catalogInfo;
                            EventBus.getDefault().post(loadResult);
                        } else if (bean.chapterStatus == 3) {
                            CatalogInfo chapter = new CatalogInfo(bookInfo.bookid, bean.catalogId);
                            chapter.isdownload = "2";
                            DBUtils.updateCatalog(mContext, chapter);

                            LoadResult loadResult = new LoadResult(LoadResult.STATUS_SUCCESS);
                            loadResult.mChapter = catalogInfo;
                            EventBus.getDefault().post(loadResult);
                        } else if (bean.chapterStatus == 4) {
                            CatalogInfo chapter = new CatalogInfo(bookInfo.bookid, bean.catalogId);
                            chapter.isdownload = "3";
                            DBUtils.updateCatalog(mContext, chapter);

                            LoadResult loadResult = new LoadResult(LoadResult.STATUS_SUCCESS);
                            loadResult.mChapter = catalogInfo;
                            EventBus.getDefault().post(loadResult);
                        }
                    }

                    taskIds.remove(bean.getUniqueId());
                }
            } catch (Exception e) {
                ALog.printStackTrace(e);
                synchronized (this) {
                    started = false;
                }
            }

        }
    }

    /**
     * 后台删章打点
     *
     * @param bookInfo
     * @param catalogid
     */
    private void dzEventDeleteChapter(BookInfo bookInfo, String catalogid) {
        HashMap<String, String> map = new HashMap<String, String>();

        map.put(LogConstants.KEY_QNR_BID, bookInfo.bookid);
        map.put(LogConstants.KEY_QNR_CID, catalogid);

        DzLog.getInstance().logEvent(LogConstants.EVENT_HTSZ, map, null);
    }


    /**
     * BulkLoadInfo
     */
    static class BulkLoadInfo {
        BookInfo bookInfo;
        String catalogId;

        String cdnUrl;
        List<String> backUrls;
        int chapterStatus;

        /**
         * 自有支付
         *
         * @param bookInfo
         * @param catalogId
         * @param cdnUrl
         */
        public BulkLoadInfo(BookInfo bookInfo, String catalogId, String cdnUrl, List<String> backUrls, int chapterStatus) {
            this.bookInfo = bookInfo;
            this.catalogId = catalogId;
            this.cdnUrl = cdnUrl;
            this.backUrls = backUrls;
            this.chapterStatus = chapterStatus;
        }

        public String getUniqueId() {
            return bookInfo.bookid + "+" + catalogId;
        }
    }


    /**
     * 单章订购页面点击批量订购
     *
     * @param activity  activity
     * @param bookId    bookId
     * @param chapterId chapterId
     */
    public void singleOrderToLotOrder(final BaseActivity activity, final String bookId, final String chapterId) {

        Observable.create(new ObservableOnSubscribe<LoadResult>() {
            @Override
            public void subscribe(ObservableEmitter<LoadResult> e) {

                BookInfo bookInfo = DBUtils.findByBookId(activity, bookId);

                CatalogInfo currentCatalog = DBUtils.getCatalog(activity, bookInfo.bookid, chapterId);

                if (currentCatalog == null) {
                    ALog.dZz("SingleOrderPresenter lotOrder currentCatalog为空");
                    e.onNext(new LoadResult(LoadResult.STATUS_ERROR));
                    e.onComplete();
                    return;
                }

                RechargeParams rechargeParams = new RechargeParams("4", bookInfo);
                rechargeParams.setOperateFrom(activity.getName());
                rechargeParams.setPartFrom(LogConstants.ORDER_SOURCE_FROM_VALUE_1);
                rechargeParams.isReader = true;

                CatalogInfo noDownloadCatalog = DBUtils.getFirstNoDownloadCatalog(activity, currentCatalog);
                LoadResult result = BookLoader.getInstance().loadBulkChapters(activity, bookInfo, noDownloadCatalog, rechargeParams);
                if (result != null) {
                    result.mChapter = currentCatalog;
                }
                e.onNext(result);
                e.onComplete();

            }

        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribeWith(new DisposableObserver<LoadResult>() {

            @Override
            public void onNext(LoadResult value) {
                if (null != activity) {
                    activity.dissMissDialog();
                }
                if (value == null) {
                    ALog.dZz("LoadResult null");
                    activity.showNotNetDialog();
                    return;
                }
                if (value.isSuccess()) {
                    CatalogInfo info = DBUtils.getCatalog(activity, value.mChapter.bookid, value.mChapter.catalogid);
                    ReaderUtils.intoReader(activity, info, info.currentPos);

                } else {
                    ALog.dZz("LoadResult:" + value.status);
                    if (value.isNetError() && !NetworkUtils.getInstance().checkNet()) {
                        if (!TextUtils.isEmpty(value.getMessage(activity)) && activity != null) {
                            activity.showNotNetDialog();
                        }
                    } else {
                        ToastAlone.showShort(value.getMessage(activity));
                    }
                }
            }

            @Override
            public void onError(Throwable e) {
                ALog.dZz("load ex:" + e.getMessage());
                activity.dissMissDialog();
                activity.showNotNetDialog();
            }

            @Override
            public void onComplete() {
                ALog.dZz("load onComplete");
                activity.dissMissDialog();
            }

            @Override
            protected void onStart() {
                activity.showDialogByType(DialogConstants.TYPE_GET_DATA);
            }
        });
    }


}
