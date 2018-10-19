package com.dzbook.mvp.presenter;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;

import com.dzbook.AppConst;
import com.dzbook.AppContext;
import com.dzbook.activity.Main2Activity;
import com.dzbook.activity.reader.ReaderUtils;
import com.dzbook.database.bean.BookInfo;
import com.dzbook.database.bean.CatalogInfo;
import com.dzbook.event.EventConstant;
import com.dzbook.lib.net.DzSchedulers;
import com.dzbook.lib.utils.ALog;
import com.dzbook.lib.utils.SDCardUtil;
import com.dzbook.loader.BookLoader;
import com.dzbook.loader.LoadResult;
import com.dzbook.log.DzLog;
import com.dzbook.log.LogConstants;
import com.dzbook.mvp.BasePresenter;
import com.dzbook.mvp.DialogConstants;
import com.dzbook.mvp.UI.MainShelfUI;
import com.dzbook.net.hw.HwRequestLib;
import com.dzbook.service.InsertBookInfoDataUtil;
import com.dzbook.service.RechargeParams;
import com.dzbook.utils.DBUtils;
import com.dzbook.utils.DeviceUtils;
import com.dzbook.utils.FileUtils;
import com.dzbook.utils.NetworkUtils;
import com.dzbook.utils.SpUtil;
import com.dzbook.utils.ThirdPartyLog;
import com.dzbook.utils.WhiteListWorker;
import com.dzbook.utils.hw.PermissionUtils;
import com.dzbook.view.BookView;
import com.dzbook.view.common.ShelfGridBookImageView;
import com.dzbook.view.shelf.ShelfGridView;
import com.dzbook.view.shelf.ShelfListItemView;
import com.dzbook.web.ActionEngine;
import com.ishugui.R;
import com.iss.app.BaseActivity;
import com.iss.view.common.ToastAlone;

import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import hw.sdk.net.bean.BeanBookInfo;
import hw.sdk.net.bean.BeanChapterCatalog;
import hw.sdk.net.bean.BeanChapterInfo;
import hw.sdk.net.bean.cloudshelf.BeanCloudShelfLoginSyncInfo;
import hw.sdk.net.bean.shelf.BeanBookUpdateInfo;
import hw.sdk.net.bean.shelf.BeanShelfBookItem;
import hw.sdk.net.bean.store.BeanGetBookInfo;
import hw.sdk.net.bean.store.TempletContant;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

/**
 * MainShelfPresenter
 *
 * @author lizhongzhong 2017/4/6.
 */

public class MainShelfPresenter extends BasePresenter {
    private static final String TAG = "MainShelfPresenter";
    private static long clickDelayTime = 0;
    long[] mHits = new long[2];

    private BookView mBookView;

    private MainShelfUI mUI;
    private Activity activity;

    /**
     * 构造
     *
     * @param activity activity
     * @param ui       ui
     */
    public MainShelfPresenter(Activity activity, MainShelfUI ui) {
        mUI = ui;
        this.activity = activity;
        mBookView = new BookView(activity);
    }

    /**
     * 进入书架时，初始化正在更新目录状态为未更新
     */
    public void initBookUpdatingToNoUpdate() {
        DzSchedulers.execute(new Runnable() {
            @Override
            public void run() {
                try {

                    List<BookInfo> bookInfos = DBUtils.findAllBooks(activity);
                    if (null != bookInfos && bookInfos.size() > 0) {
                        for (BookInfo bookInfo : bookInfos) {
                            if (bookInfo != null && bookInfo.isUpdate == 3) {
                                bookInfo.isUpdate = 1;
                                DBUtils.updateBook(activity, bookInfo);
                            }
                        }
                    }

                } catch (Throwable e) {
                    ThirdPartyLog.reportError(new Throwable("dz:initBookUpdatingToNoUpdate", e));
                }
            }
        });
    }

    /**
     * 获取到书架中数据
     *
     * @param isUpdateShelf :是否更新书架：onresume是true其他是false
     */
    public void getBookFromLocal(final boolean isUpdateShelf) {
        DzSchedulers.child(new Runnable() {
            @Override
            public void run() {
                List<BookInfo> books = null;
                try {
                    String booksSort = SpUtil.getinstance(mUI.getContext()).getString(SpUtil.SHELF_BOOK_SORT, "0");
                    books = getBookFromLocalBySort(booksSort);
                    boolean isDeleteLocalBook = deleteLocalAndNoExistBook(books);
                    if (isDeleteLocalBook) {
                        books = getBookFromLocalBySort(booksSort);
                    }
                } catch (Throwable e) {
                    ThirdPartyLog.reportError(new Throwable("dz:getBookFromLocal", e));
                }
                if (books != null) {
                    mUI.setBookShlefData(books);
                }
                if (isUpdateShelf) {
                    getShelfUpdateAndNotify(books, false, true);
                    deleteAllNoAddShelfBookFromLocal();
                }
            }
        });
    }

    /**
     * 231接口：书架更新+通知活动
     *
     * @param books            books
     * @param isChildThread    isChildThread
     * @param isReferenceShelf :是否刷新书架
     */
    public void getShelfUpdateAndNotify(final List<BookInfo> books, final boolean isReferenceShelf, boolean isChildThread) {
        if (isChildThread) {
            requestShelfUpdate(books, isReferenceShelf);
        } else {
            DzSchedulers.child(new Runnable() {
                @Override
                public void run() {
                    requestShelfUpdate(books, isReferenceShelf);
                }
            });
        }
    }

    /**
     * 请求书架更新
     *
     * @param books            books
     * @param isReferenceShelf isReferenceShelf
     */
    private void requestShelfUpdate(List<BookInfo> books, boolean isReferenceShelf) {
        String makeUpFunction = "f0";
        PermissionUtils permissionUtils = new PermissionUtils();
        if (!permissionUtils.checkNotifyPermission(mUI.getActivity())) {
            makeUpFunction += ",f2";
        }
        ArrayList<BeanShelfBookItem> shelfBookItems = getBookShelfUpdateBooksIds(books);
        if (!TextUtils.isEmpty(makeUpFunction)) {
            if (isReferenceShelf) {
                long requestTime = System.currentTimeMillis();
                AppContext.setShelfBookUpdateRequestTime(requestTime);
                shelfBookUpdate(isReferenceShelf, shelfBookItems, makeUpFunction);
            } else {
                long requestTime = System.currentTimeMillis();
                if (requestTime - AppContext.getShelfBookUpdateRequestTime() > 1000 * 60 * 10) {
                    AppContext.setShelfBookUpdateRequestTime(requestTime);
                    shelfBookUpdate(isReferenceShelf, shelfBookItems, makeUpFunction);
                }
            }
        }
    }

    private boolean isActivityEmpty() {
        Activity activity1 = mUI.getActivity();
        return activity1 == null;
    }

    /**
     * 书架更新231接口
     *
     * @param isReferenceShelf isReferenceShelf
     * @param shelfBookItems   shelfBookItems
     * @param makeUpFunction   makeUpFunction
     */
    private void shelfBookUpdate(final boolean isReferenceShelf, final ArrayList<BeanShelfBookItem> shelfBookItems, final String makeUpFunction) {
        Observable.create(new ObservableOnSubscribe<BeanBookUpdateInfo>() {
            @Override
            public void subscribe(ObservableEmitter<BeanBookUpdateInfo> e) {
                try {
                    if (!isActivityEmpty()) {
                        int pref = SpUtil.getinstance(AppConst.getApp()).getPersonReadPref();
                        BeanBookUpdateInfo bookUpdateInfo = HwRequestLib.getInstance().shelfBookUpdate(pref + "", makeUpFunction, shelfBookItems);
                        if (bookUpdateInfo != null) {
                            if (!TextUtils.isEmpty(bookUpdateInfo.city)) {
                                SpUtil.getinstance(AppConst.getApp()).setClientCity(bookUpdateInfo.city);
                            }
                            if (!TextUtils.isEmpty(bookUpdateInfo.prov)) {
                                SpUtil.getinstance(AppConst.getApp()).setClientProvince(bookUpdateInfo.prov);
                            }
                            if (bookUpdateInfo.isContainBooks()) {
                                updateLocalBookStatus(mUI.getActivity(), bookUpdateInfo.updateList);
                            }
                            //储存下一次的更新书架书籍数量，双控制，书架保证最少50，max_num大于50以max_num为准
                            SpUtil spUtil = SpUtil.getinstance(activity);
                            spUtil.setUpdateBookNum(bookUpdateInfo.maxNum);
                            spUtil.setLong(SpUtil.SP_MAX_READING_TIME, bookUpdateInfo.maxReadTime);
                            if (bookUpdateInfo.hasSignIn == 1) {
                                SpUtil.getinstance(activity).markTodayByKey(SpUtil.SP_USER_SIGN);
                            }
                        }
                        e.onNext(bookUpdateInfo);
                    }
                } catch (Exception ex) {
                    e.onError(ex);
                }
            }
        }).observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<BeanBookUpdateInfo>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(BeanBookUpdateInfo value) {
                if (isReferenceShelf) {
                    mUI.hideReferenceDelay();
                }
                if (value.isSuccess()) {
                    if (value.isContainBooks()) {
                        String booksSort = SpUtil.getinstance(activity).getString("books_sort", "0");
                        List<BookInfo> list = getBookFromLocalBySort(booksSort);
                        mUI.updateShelfData(list, value);
                    } else {
                        mUI.updateShelfSignIn(value);
                    }

                    if (value.needShowSetNotifyDialogIfNeed()) {
                        mUI.needShowSetNotifyDialogIfNeed(value.checkNotifyAppOpenCount, value.checkNotifyFrequency, value.cnMsg);
                    }
                } else {
                    if (!isActivityEmpty()) {
                        mUI.showMessage(value.getRetMsg());
                    }
                }
            }

            @Override
            public void onError(Throwable e) {
                mUI.updateShelfSignIn(null);
            }

            @Override
            public void onComplete() {

            }
        });
    }

    /**
     * 老用户没有回传男生女生信息 如果没回传过 并且又记录的男女生偏好 回传
     */
    public void dzLogAndUmengLog() {
        SpUtil spUtil = SpUtil.getinstance(mUI.getContext());
        if (!spUtil.getPersonExistsReadPref()) {
            String userId = spUtil.getUserID();
            // 1: 男 2:女 0:跳过
            int ph = spUtil.getPersonReadPref();
            if (!TextUtils.isEmpty(userId) && ph >= 0) {
                spUtil.setPersonExistsReadPref(true);
                HashMap<String, String> map = new HashMap<>();
                map.put("yhph", ph + "");
                DzLog.getInstance().logEvent(LogConstants.EVENT_PHSZHC, map, null);
            }
        }

        ThirdPartyLog.onEventValueOldClick(activity, "dz_" + ThirdPartyLog.BOOK_SHELF_OPEN_UMENG_ID, null, 1);
    }

    /**
     * 检查并操作文件跟目录
     */
    public void checkFileAndOperFileRoot() {
        SpUtil.getinstance(mUI.getContext()).setBoolean("issdcard", SDCardUtil.getInstance().isSDCardAvailable());
        FileUtils.handleAppFileRootDirectory();
    }

    /**
     * 跳至书架管理模式
     *
     * @param bookid bookid
     */
    public void skipToShelfManagerMode(String bookid) {
        mUI.openManager(bookid);
    }

    /**
     * 免费书城
     */
    public void skipToSpecialOfferBookActivity() {
        ActionEngine.getInstance().toFree(activity);
    }


    /**
     * 跳至ReaderActivity
     *
     * @param mBookInfo         mBookInfo
     * @param imageViewCover    imageViewCover
     * @param shelfListItemView shelfListItemView
     */
    public void skipToReaderActivity(BookInfo mBookInfo, ImageView imageViewCover, ShelfListItemView shelfListItemView) {
        if (null == mBookInfo) {
            return;
        }
        if (mBookInfo.isJump()) {
            ReaderUtils.openWps(mUI.getContext(), mBookInfo);
            return;
        }
        DzLog.getInstance().logClick(LogConstants.MODULE_SJ, LogConstants.ZONE_SJ_BOOK, mBookInfo.bookid, null, null);
        ThirdPartyLog.onEventValue(mUI.getContext(), ThirdPartyLog.SHELF_ALL_CLICK, ThirdPartyLog.SHELF_BOOK, 1);
        ThirdPartyLog.onEventValueOldClick(mUI.getContext(), ThirdPartyLog.BOOK_SHELF_UMENG_ID, ThirdPartyLog.BOOK_SHELF_READERINTO_VALUE, 1);
        if (mBookView.isOpen().get()) {
            mBookView.isOpen().set(false);
        }

        skipToBookReader(mBookInfo, imageViewCover, shelfListItemView);
    }

    /**
     * 跳至ReaderActivity
     *
     * @param mBookInfo         mBookInfo
     * @param imageViewCover    imageViewCover
     * @param shelfListItemView shelfListItemView
     */
    public void skipToBookReader(BookInfo mBookInfo, ImageView imageViewCover, ShelfListItemView shelfListItemView) {
        CatalogInfo cate = DBUtils.getCatalog(mUI.getContext(), mBookInfo.bookid, mBookInfo.currentCatalogId);
        // 本地图书，在重新扫描目录的时候，可能会丢失当前目录。
        if (mBookInfo.isLocalBook() && cate == null) {
            cate = createLocalBooKCatalog(mBookInfo);
        }
        if (cate != null) {
            if (cate.isAvailable()) {
                gotoReader((Main2Activity) mUI.getContext(), mBookInfo, cate, imageViewCover);
            } else {
                Main2Activity ac = (Main2Activity) mUI.getContext();
                if (null != imageViewCover && imageViewCover.getParent() instanceof ShelfGridBookImageView) {
                    ShelfGridBookImageView bookImageView = (ShelfGridBookImageView) imageViewCover.getParent();
                    bookImageView.showLoading();
                } else {
                    if (shelfListItemView != null) {
                        shelfListItemView.showLoaddingView();
                    }
                }
                //更新下载状态
                updateCatalogDownStatus(mBookInfo, cate);
                serviceLoadChapter(ac, mBookInfo, cate, imageViewCover, shelfListItemView);
            }
        } else {
            //cate为null：并且是网络图书 110接口去获取目录
            if (mBookInfo.bookfrom == 2) {
                ToastAlone.showShort(R.string.local_book_no_chapters_please_retry_add);
                mUI.dissMissDialog();
                return;
            }
            getCatalogBy110InterFace(mBookInfo, imageViewCover, shelfListItemView);
        }
    }

    /**
     * 获取mBookView的id
     *
     * @return string
     */
    public String getBookViewId() {
        if (mBookView != null) {
            return mBookView.getBookId();
        }
        return "";
    }

    /**
     * closedBookDirect
     */
    public void closedBookDirect() {
        mBookView.startCloseBookDirect(EventConstant.REQUESTCODE_CLOSEDBOOK, EventConstant.TYPE_MAINSHELFFRAGMENT);
    }


    /**
     * onResume方法时 关闭动画
     *
     * @param rvBookshelf rvBookshelf
     */
    public void closedBookAnim(RecyclerView rvBookshelf) {

        String booksSort = SpUtil.getinstance(mUI.getContext()).getString(SpUtil.SHELF_BOOK_SORT, "0");
        if ("1".equals(booksSort)) {
            mBookView.startCloseBookAnimation(null, EventConstant.REQUESTCODE_CLOSEDBOOK, EventConstant.TYPE_MAINSHELFFRAGMENT);
        } else if ("0".equals(booksSort)) {
            boolean hasClose = false;
            RecyclerView.LayoutManager layoutManager = rvBookshelf.getLayoutManager();
            int size = layoutManager.getChildCount();
            for (int i = 0; i < size; i++) {
                View view = layoutManager.getChildAt(i);
                ImageView imageViewCover = null;
                if (view instanceof ShelfListItemView) {
                    BookInfo bookInfo = ((ShelfListItemView) view).getBookInfo();
                    if (bookInfo != null && TextUtils.equals(bookInfo.bookid, mBookView.getBookId())) {
                        imageViewCover = ((ShelfListItemView) view).getImageViewBookCover();
                    }
                } else if (view instanceof ShelfGridView) {
                    for (int j = 0; j < ((ShelfGridView) view).getItem().getChildCount(); j++) {
                        BookInfo bookInfo = ((ShelfGridBookImageView) ((ShelfGridView) view).getItem().getChildAt(j)).getBookInfo();
                        if (bookInfo != null && TextUtils.equals(bookInfo.bookid, mBookView.getBookId())) {
                            imageViewCover = ((ShelfGridBookImageView) ((ShelfGridView) view).getItem().getChildAt(j)).bookImageView;
                        }
                    }
                }
                if (imageViewCover != null) {
                    mBookView.startCloseBookAnimation(imageViewCover, EventConstant.REQUESTCODE_CLOSEDBOOK, EventConstant.TYPE_MAINSHELFFRAGMENT);
                    hasClose = true;
                    break;
                }
            }
            if (!hasClose) {
                mBookView.startCloseBookAnimation(null, EventConstant.REQUESTCODE_CLOSEDBOOK, EventConstant.TYPE_MAINSHELFFRAGMENT);
            }
        }
    }


    /**
     * 设置选中状态
     */
    public void refreshBookShelfSelection() {
        mUI.setRecycleViewSelection();
    }

    /**
     * 删除全部
     *
     * @param list list
     */
    public void deleteAllSelectBooks(List<BookInfo> list) {
        Main2Activity asa = (Main2Activity) mUI.getContext();
        if (asa != null) {
            asa.showDialogByType(DialogConstants.TYPE_GET_DATA, mUI.getContext().getString(R.string.str_deletebooks));
        }

        deleteBookFromDb(list);

        dzLogDeleteBook(list);
        //批量删除
        deleteBookFromLocal(list);

        if (asa != null) {
            asa.dissMissDialog();
        }
        mUI.backToCommonMode(true);

        //        ToastAlone.showShort(R.string.book_delected);
    }

    /**
     * 同步书架
     *
     * @param json json
     */
    public void syncCloudBookShelf(final String json) {
        try {
            if (!TextUtils.isEmpty(json)) {
                JSONObject jsonObject = new JSONObject(json);
                final BeanCloudShelfLoginSyncInfo loginSyncInfo = new BeanCloudShelfLoginSyncInfo();
                loginSyncInfo.parseJSON(jsonObject);
                if (loginSyncInfo.hasBookIds()) {
                    Observable.create(new ObservableOnSubscribe<BeanGetBookInfo>() {
                        @Override
                        public void subscribe(ObservableEmitter<BeanGetBookInfo> e) {
                            try {
                                if (!isActivityEmpty()) {
                                    BeanGetBookInfo getBookInfo = HwRequestLib.getInstance().getCloudShelfBookDetail(loginSyncInfo.bookIds);

                                    if (getBookInfo != null && getBookInfo.isContainItems()) {
                                        InsertBookInfoDataUtil.insertNativeBook(mUI.getActivity(), getBookInfo, WhiteListWorker.CLOUD_SYNC_VALUE);
                                    }

                                    e.onNext(getBookInfo);
                                }
                            } catch (Exception ex) {
                                e.onError(ex);
                            }
                        }
                    }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<BeanGetBookInfo>() {
                        @Override
                        public void accept(BeanGetBookInfo beanGetBookInfo) throws Exception {
                            if (beanGetBookInfo != null && beanGetBookInfo.isContainItems()) {
                                mUI.syncCloudBookShelfSuccess(loginSyncInfo);
                            }
                        }
                    });
                }
            }
        } catch (Exception e) {
            ALog.printStackTrace(e);
        }
    }

    /**
     * 读取书架中的需要更新的书籍id列表 //前面50本
     *
     * @param list
     * @return
     */
    private ArrayList<BeanShelfBookItem> getBookShelfUpdateBooksIds(List<BookInfo> list) {
        if (list == null) {
            list = mUI.getShelfAdapterDatas();
        }
        if (list == null) {
            list = DBUtils.findBookShelfUpdateBooks(mUI.getContext());
        }
        if (list != null && list.size() > 0) {
            ArrayList<BeanShelfBookItem> shelfBookItems = new ArrayList<>();
            Map<String, CatalogInfo> map = DBUtils.getAllLaterCatalogInfos(mUI.getContext());
            int size = list.size();
            for (int i = 0; i < size; i++) {
                BookInfo bookInfo = list.get(i);
                if (bookInfo != null && bookInfo.bookfrom == 1 && 1 == bookInfo.hasRead) {
                    BeanShelfBookItem shelfBookItem = new BeanShelfBookItem();
                    shelfBookItem.setBookId(bookInfo.bookid);
                    CatalogInfo catalogInfo = map.get(bookInfo.bookid);
                    if (null != catalogInfo) {
                        shelfBookItem.setChapterId(catalogInfo.catalogid);
                    }
                    shelfBookItems.add(shelfBookItem);
                }
            }
            return shelfBookItems;
        }
        return null;
    }

    /**
     * 更新231接口请求的书籍的数据库状态
     *
     * @param shelfBookUpdateBean
     */
    private void updateLocalBookStatus(Context context, ArrayList<BeanBookInfo> shelfBookUpdateBean) {
        List<BookInfo> bookInfos = new ArrayList<BookInfo>();
        for (int i = 0; i < shelfBookUpdateBean.size(); i++) {
            BeanBookInfo beanBookInfo = shelfBookUpdateBean.get(i);
            BookInfo bookInfo = DBUtils.findByBookId(context, beanBookInfo.bookId);
            if (bookInfo != null && bookInfo.isUpdate != 3) {
                BookInfo newBookInfo = new BookInfo();
                newBookInfo.bookid = bookInfo.bookid;
                // 是否书籍已经更新 3(更新中)2(书籍已更新)1(书籍未更新)
                if (1 == beanBookInfo.newChapter) {
                    newBookInfo.isUpdate = 2;
                }
                newBookInfo.control = beanBookInfo.control;
                bookInfos.add(newBookInfo);
            }
        }
        if (null != bookInfos && bookInfos.size() > 0) {
            DBUtils.updateBooks(context, bookInfos);
        }
    }

    /**
     * 进入阅读器前没有目录，通过110接口获取到书籍的目录并下载加载
     *
     * @param bookInfo
     * @param imageViewCover
     * @param shelfListItemView
     */
    private void getCatalogBy110InterFace(final BookInfo bookInfo, final ImageView imageViewCover, final ShelfListItemView shelfListItemView) {
        final Main2Activity main2Activity = (Main2Activity) mUI.getContext();
        if (imageViewCover != null && imageViewCover.getParent() instanceof ShelfGridBookImageView) {
            ((ShelfGridBookImageView) imageViewCover.getParent()).showLoading();
        } else {
            if (shelfListItemView != null) {
                shelfListItemView.showLoaddingView();
            }
        }
        DzSchedulers.child(new Runnable() {
            @Override
            public void run() {
                CatalogInfo cate = null;
                try {
                    // 去子线程，获取章节列表
                    String bookId = "";
                    if (null != bookInfo) {
                        bookId = bookInfo.bookid;
                        BeanChapterCatalog beanChapterCatalog = HwRequestLib.getInstance().chapterCatalog(bookId, "", "", bookInfo.currentCatalogId, "");
                        if (null != beanChapterCatalog && beanChapterCatalog.isSuccess()) {
                            List<BeanChapterInfo> chapterInfoList = beanChapterCatalog.chapterInfoList;
                            if (null != chapterInfoList && !chapterInfoList.isEmpty()) {
                                List<BeanChapterInfo> listChapterInfo = new ArrayList<>(chapterInfoList);
                                InsertBookInfoDataUtil.appendChapters(main2Activity, listChapterInfo, bookInfo.bookid, null);
                            }
                            cate = DBUtils.getCatalog(main2Activity, bookInfo.bookid, bookInfo.currentCatalogId);
                            if (null == cate) {
                                cate = DBUtils.getFirstCatalog(main2Activity, bookInfo.bookid);
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (null != cate) {
                    serviceLoadChapter(main2Activity, bookInfo, cate, imageViewCover, shelfListItemView);
                } else {
                    ToastAlone.showShort(main2Activity.getResources().getString(R.string.preload_loading_fail));
                    main2Activity.dissMissDialog();
                }
            }
        });
    }


    /**
     * 校验章节是否支付并进行章节下载加载
     *
     * @param ac
     * @param bookInfo
     * @param catalogInfo
     * @param ivBookImg
     */
    private void serviceLoadChapter(final BaseActivity ac, final BookInfo bookInfo, final CatalogInfo catalogInfo, final ImageView ivBookImg, final ShelfListItemView shelfListItemView) {

        getLoadSingleChapterObservable(ac, bookInfo, catalogInfo).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribeWith(new DisposableObserver<LoadResult>() {

            @Override
            public void onNext(LoadResult value) {
                if (ivBookImg.getParent() instanceof ShelfGridBookImageView) {
                    ShelfGridBookImageView bookImageView = (ShelfGridBookImageView) ivBookImg.getParent();
                    bookImageView.stopLoading();
                } else {
                    if (shelfListItemView != null) {
                        shelfListItemView.hideLoaddingView();
                    }
                }
                if (value == null) {
                    ALog.dZz("LoadResult null");
                    if (ac != null) {
                        ac.showNotNetDialog();
                    }
                    return;
                }
                if (value.isSuccess()) {
                    final CatalogInfo info = DBUtils.getCatalog(ac, catalogInfo.bookid, catalogInfo.catalogid);
                    gotoReader(ac, bookInfo, info, ivBookImg);
                } else {
                    ALog.dZz("LoadResult:" + value.status);
                    //                    if (!value.isCanceled()) {
                    //                        // FIXME: cmt 2018/4/24 章节下载错误 待取数sdk完善
                    //                    }
                    if (value.status == LoadResult.STATUS_NET_WORK_NOT_USE || value.status == LoadResult.STATUS_NET_WORK_NOT_COOL || value.status == LoadResult.STATUS_ERROR && !NetworkUtils.getInstance().checkNet()) {
                        if (!TextUtils.isEmpty(value.getMessage(mUI.getContext())) && ac != null) {
                            long current = System.currentTimeMillis();
                            if (current - clickDelayTime > TempletContant.CLICK_DISTANSE) {
                                ac.showNotNetDialog();
                                clickDelayTime = current;
                            }
                        }
                    } else {
                        ReaderUtils.dialogOrToast(value.getMessage(mUI.getContext()));
                    }
                }
            }

            @Override
            public void onError(Throwable e) {
                ALog.eZz("load ex:" + e.getMessage());
                ac.dissMissDialog();
            }

            @Override
            public void onComplete() {
                ALog.dZz("load onComplete");
            }

        });
    }

    /**
     * 打开阅读器，开始阅读。
     *
     * @param ac
     * @param bookInfo
     * @param info
     * @param ivBookImg
     */
    private void gotoReader(final BaseActivity ac, final BookInfo bookInfo, final CatalogInfo info, final ImageView ivBookImg) {
        final BookInfo mBookInfo = new BookInfo();
        mBookInfo.time = System.currentTimeMillis() + "";
        mBookInfo.bookid = bookInfo.bookid;
        DBUtils.updateBook(ac, mBookInfo);
        if (Build.VERSION.SDK_INT < 11 || DeviceUtils.getMemoryTotalSize() < 512) {

            ReaderUtils.intoReader(ac, info, info.currentPos);

        } else {
            ac.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    System.arraycopy(mHits, 1, mHits, 0, mHits.length - 1);
                    mHits[mHits.length - 1] = SystemClock.uptimeMillis();
                    if (mHits[1] >= (mHits[0] + 500)) {
                        Bundle bundle = new Bundle();
                        bundle.putSerializable(EventConstant.CATALOG_INFO, info);
                        if (!mBookView.startOpenBookAnimation(ivBookImg, EventConstant.REQUESTCODE_OPENBOOK, EventConstant.TYPE_MAINSHELFFRAGMENT, bundle, mBookInfo.bookid)) {
                            ReaderUtils.intoReader(ac, info, info.currentPos);
                        }
                    }
                }
            });
        }
    }


    /**
     * 更新章节的下载状态
     *
     * @param bookBean
     * @param catalog
     */
    private void updateCatalogDownStatus(BookInfo bookBean, CatalogInfo catalog) {
        if (catalog != null && "0".equals(catalog.isdownload)) {
            catalog.isdownload = "1";

            CatalogInfo cinfo = new CatalogInfo(bookBean.bookid, catalog.catalogid);
            cinfo.isdownload = catalog.isdownload;
            DBUtils.updateCatalog(mUI.getContext(), cinfo);
        }
    }

    /**
     * 根据书籍创建本地图书的目录
     *
     * @param bookBean
     */
    private CatalogInfo createLocalBooKCatalog(BookInfo bookBean) {
        CatalogInfo cate = new CatalogInfo(bookBean.bookid, bookBean.currentCatalogId);//-1表示本地图书还没有被扫描
        try {
            //当用户看本地图书时 解析目录出现异常时 就默认设置一个
            cate.currentPos = Long.parseLong(bookBean.currentCatalogId);
        } catch (NumberFormatException e) {
            cate.currentPos = 0;
        }
        cate.path = bookBean.bookid;
        cate.catalogname = bookBean.bookname;
        cate.ispay = "1";
        cate.isdownload = "0";
        return cate;
    }

    /**
     * 删除所有没有加入书架的本地数据库中的书
     */
    private void deleteAllNoAddShelfBookFromLocal() {
        ArrayList<BookInfo> bookInfos = DBUtils.findAllBooksNoAdd(activity);
        if (bookInfos != null && bookInfos.size() > 0) {

            for (BookInfo info : bookInfos) {
                ALog.iZz("图书:" + info.bookname + "作者:" + info.author + " 书籍id:" + info.bookid + "不在书架   ");
            }
            //已经等了完成的 使用的是云书架 所以应该删除掉没有加入书架的书籍
            if (SpUtil.getinstance(activity).getAccountLoginStatus()) {
                DBUtils.deleteMoreBook(activity, bookInfos);
            }
            DBUtils.deleteMoreBookCatalogByBoodIds(activity, bookInfos);
        }
    }

    /**
     * 不同的排序返回书籍列表
     *
     * @param booksSort：0：时间，1：书名
     * @return
     */
    private List<BookInfo> getBookFromLocalBySort(String booksSort) {
        List<BookInfo> books = null;
        if (TextUtils.equals(booksSort, "0")) {
            books = DBUtils.findAllBooks(mUI.getContext());
        } else if (TextUtils.equals(booksSort, "1")) {
            books = DBUtils.findAllBooksSortByName(mUI.getContext());
        }
        return books;
    }

    /**
     * 删除本地且目录中不存在的书籍
     *
     * @param books
     * @return
     */
    private boolean deleteLocalAndNoExistBook(List<BookInfo> books) {
        boolean isReQuery = false;
        if (books != null && books.size() > 0) {
            for (BookInfo b : books) {
                //判断如果是本地的书籍 则判断本地书籍是否存在,如果不存在则删除数据库记录
                if (b.isLocalBook()) {
                    CatalogInfo catalog = DBUtils.getCatalog(mUI.getContext(), b.bookid, b.currentCatalogId);
                    if (catalog != null && !TextUtils.isEmpty(catalog.path) && !new File(catalog.path).exists()) {
                        isReQuery = true;
                        DBUtils.deleteBook(mUI.getContext(), b);
                        DBUtils.deleteCatalogByBoodId(mUI.getContext(), b.bookid);
                    }
                }
            }
        }
        return isReQuery;
    }

    private void dzLogDeleteBook(List<BookInfo> list) {
        for (final BookInfo bookinfo : list) {
            if (bookinfo.isAddBook == 2) {
                // 2017/11/30 放在子线程打点
                DzSchedulers.child(new Runnable() {
                    @Override
                    public void run() {
                        HashMap<String, String> map = new HashMap<>();
                        map.put("mode", "1");
                        map.put("bid", bookinfo.bookid);
                        if (bookinfo.isMustDeleteBook(activity)) {
                            //强制删除
                            map.put("type", "3");
                        } else if (bookinfo.isShowOffShelf(activity)) {
                            //下架
                            map.put("type", "2");
                        } else {
                            map.put("type", "1");
                        }
                        DzLog.getInstance().logEvent(LogConstants.EVENT_SCSJ, map, null);
                    }
                });
            }

            SpUtil.getinstance(activity).removeLocalBookScanTime(bookinfo.bookid);
            SpUtil.getinstance(activity).removeLocalBookScanState(bookinfo.bookid);
        }
    }

    /**
     * 从本地删除书籍
     */
    private void deleteBookFromLocal(final List<BookInfo> list) {
        DzSchedulers.child(new Runnable() {
            @Override
            public void run() {
                if (list != null && list.size() > 0) {
                    for (BookInfo bookInfo : list) {
                        if (bookInfo != null) {
                            File file = new File(SDCardUtil.getInstance().getSDCardAndroidRootDir() + "/" + FileUtils.APP_BOOK_DIR_PATH + bookInfo.bookid);
                            FileUtils.delFolder(file.getAbsolutePath());
                        }
                    }
                }
            }
        });
    }

    private void deleteBookFromDb(final List<BookInfo> list) {
        DzSchedulers.child(new Runnable() {
            @Override
            public void run() {

                if (list != null && list.size() > 0) {
                    DBUtils.deleteMoreBook(activity, list);
                    DBUtils.deleteMoreBookCatalogByBoodIds(activity, list);
                }
            }
        });
    }


    /**
     * 单章加载
     *
     * @param ac          ac
     * @param bookInfo    bookInfo
     * @param catalogInfo catalogInfo
     * @return Observable
     */
    protected Observable<LoadResult> getLoadSingleChapterObservable(final BaseActivity ac, final BookInfo bookInfo, final CatalogInfo catalogInfo) {
        return Observable.create(new ObservableOnSubscribe<LoadResult>() {
            @Override
            public void subscribe(ObservableEmitter<LoadResult> e) {

                final RechargeParams rechargeParams = new RechargeParams("3", bookInfo);
                rechargeParams.setOperateFrom(((Main2Activity) mUI.getContext()).getActivity().getName());
                rechargeParams.setPartFrom(LogConstants.ORDER_SOURCE_FROM_VALUE_10);

                LoadResult result = BookLoader.getInstance().loadOneChapter(activity, bookInfo, catalogInfo, rechargeParams);
                if (result != null) {
                    result.mChapter = catalogInfo;
                }
                e.onNext(result);
                e.onComplete();
            }
        });

    }

}
