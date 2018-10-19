package com.dzbook.mvp.presenter;

import android.os.SystemClock;
import android.text.TextUtils;

import com.dzbook.activity.reader.ReaderUtils;
import com.dzbook.database.bean.BookInfo;
import com.dzbook.database.bean.BookMarkNew;
import com.dzbook.database.bean.CatalogInfo;
import com.dzbook.event.EventBus;
import com.dzbook.event.engine.DBEngine;
import com.dzbook.event.type.BookMarkEvent;
import com.dzbook.event.type.BookNoteEvent;
import com.dzbook.loader.LoadResult;
import com.dzbook.log.LogConstants;
import com.dzbook.mvp.DialogConstants;
import com.dzbook.mvp.UI.ReaderCatalogUI;
import com.dzbook.r.c.AkDocInfo;
import com.dzbook.utils.DBUtils;
import com.dzbook.utils.NetworkUtils;
import com.dzbook.utils.ThirdPartyLog;
import com.dzbook.view.reader.ReaderChapterView;
import com.ishugui.R;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

/**
 * ReaderCatalogPresenter
 *
 * @author wxliao on 17/8/18.
 */

public class ReaderCatalogPresenter extends BookPagePresenter {
    long[] mHits = new long[2];

    private BookInfo mBookInfo;
    private AkDocInfo mDoc;
    private ReaderCatalogUI mUI;

    private ArrayList<String> purchasedIdList = new ArrayList<>();
    private int purchaseSize;

    /**
     * 构造函数
     *
     * @param ui       ui
     * @param doc      doc
     * @param bookInfo bookInfo
     */
    public ReaderCatalogPresenter(ReaderCatalogUI ui, AkDocInfo doc, BookInfo bookInfo) {
        super(ui);
        mUI = ui;
        mDoc = doc;
        mBookInfo = bookInfo;
        create();
    }


    /**
     * 注册EventBus
     */
    public void create() {
        EventBus.getDefault().register(this);
    }


    /**
     * destroy
     */
    public void destroy() {
        EventBus.getDefault().unregister(this);
        composite.disposeAll();
    }

    /**
     * 接收并处理章节加载完成后的事件，用于更新下载已购章节的进度
     *
     * @param loadResult loadResult
     */
    public void onEventMainThread(LoadResult loadResult) {
        if (loadResult == null) {
            return;
        }

        CatalogInfo chapter = loadResult.mChapter;
        if (chapter == null || mBookInfo == null || !TextUtils.equals(chapter.bookid, mBookInfo.bookid)) {
            return;
        }

        mUI.refreshChapterView();

        purchasedIdList.remove(loadResult.mChapter.catalogid);

        if (purchasedIdList.size() == 0) {
            mUI.setPurchasedButtonStatus(ReaderChapterView.SERVER_ENABLE, purchasedIdList.size(), purchaseSize);
        } else {
            mUI.setPurchasedButtonStatus(ReaderChapterView.SERVER_LOADING, purchasedIdList.size(), purchaseSize);
        }
    }

    /**
     * 刷新书签
     *
     * @param event event
     */
    public void onEventMainThread(BookMarkEvent event) {
        if (event == null) {
            return;
        }
        mUI.refreshBookMarkView();
    }

    /**
     * 刷新笔记
     *
     * @param event event
     */
    public void onEventMainThread(BookNoteEvent event) {
        if (event == null) {
            return;
        }
        mUI.refreshBookNoteView();
    }

    /**
     * 获取图书章节
     */
    public void getChapterTask() {
        if (2 == mBookInfo.bookfrom) {
            // 本地图书，扫描目录
            //            String bookId = mDoc.path;
            //            final File file = new File(mDoc.path);
            //            long time = SpUtil.getinstance(mUI.getHostActivity()).getLocalBookScanTime(bookId);
            //            if (reset || time == 0 || time < file.lastModified()) {
            //                Disposable disposable = getScanChapterObservable(mDoc.path)
            //                        .subscribeOn(Schedulers.io())
            //                        .observeOn(AndroidSchedulers.mainThread())
            //                        .subscribeWith(new DisposableObserver<CatalogInfo>() {
            //                            @Override
            //                            public void onNext(CatalogInfo value) {
            //                                mUI.showScanProgress((int) value.currentPos, (int) file.length());
            //                            }
            //
            //                            @Override
            //                            public void onError(Throwable e) {
            //                                mUI.hideScanProgress();
            //                                mUI.setPurchasedButtonStatus(ReaderChapterView.LOCAL_ENABLE, purchasedIdList.size(), purchaseSize);
            //                            }
            //
            //                            @Override
            //                            public void onComplete() {
            //                                mUI.hideScanProgress();
            //                                mUI.setPurchasedButtonStatus(ReaderChapterView.LOCAL_ENABLE, purchasedIdList.size(), purchaseSize);
            //                                initChapterFromDb();
            //                            }
            //
            //                            @Override
            //                            protected void onStart() {
            //                                super.onStart();
            //                                mUI.showScanProgress(0, (int) file.length());
            //                                mUI.setPurchasedButtonStatus(ReaderChapterView.LOCAL_DISABLE, purchasedIdList.size(), purchaseSize);
            //                            }
            //                        });
            //                composite.addAndDisposeOldByKey("getCatalogTask", disposable);
            //            } else {
            //                mUI.setPurchasedButtonStatus(ReaderChapterView.LOCAL_ENABLE, purchasedIdList.size(), purchaseSize);
            //                initChapterFromDb();
            //            }
            mUI.setPurchasedButtonStatus(ReaderChapterView.LOCAL_DISABLE, purchasedIdList.size(), purchaseSize);
            initChapterFromDb();
        } else {
            // 非导入图书，网络图书。
            boolean isNoDownloadSubChapter = false;

            mUI.setPurchasedButtonStatus(ReaderChapterView.SERVER_ENABLE, purchasedIdList.size(), purchaseSize);
            initChapterFromDb();

        }
    }

    private void initChapterFromDb() {
        ArrayList<CatalogInfo> chapterList = null;
        // 使用当前章，前后各获取50章，用于初始展示。
        if (mBookInfo != null) {
            CatalogInfo mCatalogInfo = DBUtils.getCatalog(mUI.getHostActivity(), mBookInfo.bookid, mBookInfo.currentCatalogId);
            if (null == mCatalogInfo) {
                mCatalogInfo = DBUtils.getCatalogFirst(mUI.getHostActivity(), mBookInfo.bookid);
            }
            // 获取当前章节前后 各 50 章，进行预展示。
            if (null != mCatalogInfo) {
                chapterList = DBUtils.getCatalogByBookIdByRange(mUI.getHostActivity(), mBookInfo.bookid, mCatalogInfo);
            }
        }

        mUI.addChapterItem(chapterList, true);

        if (mBookInfo != null) {
            mUI.setSelectionFromTop(mBookInfo.currentCatalogId);
            getAllChapter();
        }
    }


    /**
     * 处理购买点击事件
     */
    public void handlePurchasedClick() {
        if (mBookInfo == null || mBookInfo.bookfrom == 2) {
            return;
        }
        if (!NetworkUtils.getInstance().checkNet()) {
            mUI.getHostActivity().showNotNetDialog();
            return;
        }

        ThirdPartyLog.onEventValueOldClick(mUI.getHostActivity(), ThirdPartyLog.READER_UMENG_ID, ThirdPartyLog.DOWNLOAD_FOLLOWING_CHAPTERS_VALUE, 1);

        Disposable disposable = getPurchasedObservable(mUI.getHostActivity(), mBookInfo).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribeWith(new DisposableObserver<LoadResult>() {
            @Override
            public void onNext(LoadResult value) {
                mUI.dissMissDialog();

                if (!NetworkUtils.getInstance().checkNet()) {
                    mUI.getHostActivity().showNotNetDialog();
                    return;
                }

                if (value == null) {
                    mUI.getHostActivity().showNotNetDialog();
                    return;
                }

                if (value.isSuccess()) {
                    List<String> loadList = value.idList;
                    if (loadList == null || loadList.size() == 0) {
                        mUI.showMessage(R.string.no_download_already_order_chapter);
                        return;
                    }

                    purchasedIdList.clear();
                    purchasedIdList.addAll(loadList);
                    purchaseSize = loadList.size();
                    mUI.setPurchasedButtonStatus(ReaderChapterView.SERVER_LOADING, purchasedIdList.size(), purchaseSize);

                } else {
                    if (value.status == LoadResult.STATUS_NET_WORK_NOT_USE || value.status == LoadResult.STATUS_NET_WORK_NOT_COOL || value.status == LoadResult.STATUS_ERROR && !NetworkUtils.getInstance().checkNet()) {
                        if (!TextUtils.isEmpty(value.getMessage(mUI.getContext())) && mUI.getHostActivity() != null) {
                            mUI.getHostActivity().showNotNetDialog();
                        }
                    } else {
                        mUI.showMessage(value.getMessage(mUI.getContext()));
                    }
                }

            }

            @Override
            public void onError(Throwable e) {
                mUI.dissMissDialog();
            }

            @Override
            public void onComplete() {

            }

            @Override
            protected void onStart() {
                super.onStart();
                mUI.showDialogByType(DialogConstants.TYPE_GET_DATA);
            }
        });
        composite.addAndDisposeOldByKey("handlePurchasedClick", disposable);
    }

    /**
     * 处理章节点击
     *
     * @param chapter chapter
     */
    public void handleChapterClick(CatalogInfo chapter) {
        handleChapterClick(chapter, 0);
    }

    /**
     * 处理章节点击
     *
     * @param chapter    chapter
     * @param currentPos currentPos
     */
    public void handleChapterClick(CatalogInfo chapter, long currentPos) {
        System.arraycopy(mHits, 1, mHits, 0, mHits.length - 1);
        mHits[mHits.length - 1] = SystemClock.uptimeMillis();
        if (mHits[1] >= (mHits[0] + 500)) {
            if (mBookInfo == null || chapter == null) {
                return;
            }
            if (ReaderUtils.allowOpenDirect(chapter)) {
                //本地txt书籍,跳到记录位置
                if (mBookInfo.bookfrom == 2 && mBookInfo.format == 2) {
                    ReaderUtils.intoReader(mUI.getHostActivity(), chapter, chapter.currentPos);
                } else {
                    ReaderUtils.intoReader(mUI.getHostActivity(), chapter, currentPos);
                }
                //            ReaderUtils.intoReader(mUI.getHostActivity(), chapter, chapter.currentPos);
                mUI.getHostActivity().finish();
                mUI.getHostActivity().overridePendingTransition(R.anim.push_up_in, R.anim.push_up_out);
            } else {
                if (!NetworkUtils.getInstance().checkNet()) {
                    mUI.getHostActivity().showNotNetDialog();
                    return;
                }

                Disposable disposable = getSingleChapterObservable(mUI.getHostActivity(), mBookInfo, chapter, LogConstants.ORDER_SOURCE_FROM_VALUE_5).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribeWith(getChapterLoadObserver(DialogConstants.TYPE_GET_DATA, currentPos));
                composite.addAndDisposeOldByKey("handleChapterClick", disposable);
            }
        }
    }

    /**
     * 获取书签
     */
    public void getBookMarkTask() {
        ArrayList<BookMarkNew> bookMarkList = BookMarkNew.getBookMarkByBook(mUI.getHostActivity(), mDoc.bookId);
        mUI.addBookMarkItem(bookMarkList, true);
    }

    /**
     * 获取笔记
     */
    public void getBookNoteTask() {
        ArrayList<BookMarkNew> bookNoteList = BookMarkNew.getBookNoteByBook(mUI.getHostActivity(), mDoc.bookId);
        mUI.addBookNoteItem(bookNoteList, true);
    }

    /**
     * 在展示当前章节的前后50个章节后，在获取所有章节进行刷新
     */
    private void getAllChapter() {
        Disposable disposable = Observable.create(new ObservableOnSubscribe<List<CatalogInfo>>() {

            @Override
            public void subscribe(ObservableEmitter<List<CatalogInfo>> e) {
                ArrayList<CatalogInfo> list = DBUtils.getCatalogByBookId(mUI.getHostActivity(), mBookInfo.bookid);
                e.onNext(list);
                e.onComplete();
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribeWith(new DisposableObserver<List<CatalogInfo>>() {
            @Override
            public void onNext(List<CatalogInfo> value) {
                mUI.addChapterItem(value, true);
                mUI.setSelectionFromTop(mBookInfo.currentCatalogId);
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        });

        composite.addAndDisposeOldByKey("getAllCatalog", disposable);
    }

    //    public static final int updata_catalog_requestCode = 10018;

    /**
     * 更新书籍的目录
     */
    public void updateBook() {
        //更新书籍目录
        if (mBookInfo != null && mBookInfo.bookfrom == 1) {
            DBEngine.getInstance().updataBook(mUI.getHostActivity(), mDoc.bookId);
        }
    }

    //    private ArrayList<BookMark> convertCatalogToMark(ArrayList<CatalogInfo> catalogList) {
    //
    //        ArrayList<BookMark> list = new ArrayList<>();
    //
    //        if (catalogList != null && catalogList.size() > 0) {
    //            for (CatalogInfo catalogInfo : catalogList) {
    //                BookMark bookMark = new BookMark();
    //                bookMark.type = 1;
    //                bookMark.bookId = mBookInfo.bookid;
    //                bookMark.bookName = mBookInfo.bookname;
    //                bookMark.chapterId = catalogInfo.catalogid;
    //                bookMark.chapterName = catalogInfo.catalogname;
    //                bookMark.path = catalogInfo.path;
    //                bookMark.showText = catalogInfo.catalogname;
    //                bookMark.ispay = catalogInfo.ispay;
    //                if (mBookInfo.bookfrom == 2 && mBookInfo.format == 2) {
    //                    try {
    //                        bookMark.startPos = catalogInfo.currentPos;
    //                    } catch (Exception e) {
    //                        ALog.printStackTrace(e);
    //                    }
    //                }
    //                list.add(bookMark);
    //            }
    //        }
    //
    //        return list;
    //    }
}
