package com.dzbook.mvp.presenter;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;

import com.dzbook.activity.reader.ReaderUtils;
import com.dzbook.database.bean.BookInfo;
import com.dzbook.database.bean.CatalogInfo;
import com.dzbook.event.type.LoaderStatus;
import com.dzbook.lib.rx.CompositeDisposable;
import com.dzbook.lib.utils.ALog;
import com.dzbook.loader.BookLoader;
import com.dzbook.loader.LoadResult;
import com.dzbook.log.LogConstants;
import com.dzbook.mvp.DialogConstants;
import com.dzbook.mvp.UI.BookPageUI;
import com.dzbook.net.hw.HwRequestLib;
import com.dzbook.service.InsertBookInfoDataUtil;
import com.dzbook.service.RechargeParams;
import com.dzbook.utils.DBUtils;
import com.dzbook.utils.ListUtils;
import com.dzbook.utils.NetworkUtils;
import com.dzbook.utils.WhiteListWorker;
import com.ishugui.R;
import com.iss.app.BaseActivity;
import com.iss.view.common.ToastAlone;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import hw.sdk.net.bean.BeanBookInfo;
import hw.sdk.net.bean.BeanChapterInfo;
import hw.sdk.net.bean.bookDetail.BeanBookDetail;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.observers.DisposableObserver;

/**
 * BookPagePresenter
 *
 * @author wxliao on 17/8/16.
 */

public class BookPagePresenter {
    protected final CompositeDisposable composite = new CompositeDisposable();

    private BookPageUI pageUI;

    /**
     * 构造
     *
     * @param ui ui
     */
    public BookPagePresenter(BookPageUI ui) {
        pageUI = ui;
    }

    /**
     * 接收并处理章节加载过程中的Event
     *
     * @param loaderStatus loaderStatus
     */
    public void onEventMainThread(LoaderStatus loaderStatus) {
        //        if (loaderStatus == null) {
        //            return;
        //        }
        //        switch (loaderStatus.status) {
        //            case DzpayConstants.DIALOG_DISMISS:
        //                Log.e("liaowenxin", "onEventMainThread dismiss dialog");
        //                pageUI.dissMissDialog();
        //                break;
        //            case DzpayConstants.DIALOG_SHOW:
        //                String tips = null;
        //                int msTime = -1;
        //                if (loaderStatus.parm != null) {
        //                    tips = loaderStatus.parm.get(MsgResult.STATUS_CHANGE_MSG);
        //                    String time = loaderStatus.parm.get(MsgResult.STATUS_CHANGE_TIMER);
        //                    if (!TextUtils.isEmpty(time)) {
        //                        msTime = Integer.parseInt(time);
        //                    }
        //                }
        //
        //                if (!TextUtils.isEmpty(tips)) {
        //                    if (msTime > 0) {
        //                        pageUI.showDialog(msTime, tips);
        //                    } else {
        //                        pageUI.showDialogByType(DialogConstants.TYPE_GET_DATA, tips);
        //                    }
        //                } else {
        //                    pageUI.showDialogByType(DialogConstants.TYPE_GET_DATA);
        //                }
        //                break;
        //        }
    }

    /**
     * 加载一个章节的Observable，加载章节之前，需要保证书籍和章节信息先入库
     * 主要用于详情页和详情目录页
     *
     * @param activity     activity
     * @param beanBookInfo 详情页的bean，携带书籍信息
     * @param chapterList  详情页的章节列表
     * @param indexChapter indexChapter
     * @param rechargeFrom 支付参数用来追踪来源的页面区域
     * @return Observable
     */
    protected Observable<LoadResult> getSingleChapterObservable(final BaseActivity activity, final BeanBookInfo beanBookInfo, final ArrayList<BeanChapterInfo> chapterList, final BeanChapterInfo indexChapter, final String rechargeFrom) {
        return Observable.create(new ObservableOnSubscribe<LoadResult>() {
            @Override
            public void subscribe(ObservableEmitter<LoadResult> e) {
                if (beanBookInfo == null || ListUtils.isEmpty(chapterList)) {
                    e.onError(new RuntimeException("数据不全"));
                    return;
                }

                String bookId = beanBookInfo.bookId;
                if (TextUtils.isEmpty(bookId)) {
                    e.onError(new RuntimeException("数据不全"));
                    return;
                }

                getSingleChapterBookHandle(activity, beanBookInfo, chapterList, indexChapter, e, rechargeFrom);
            }
        });
    }

    /**
     * 书籍处理
     *
     * @param activity     activity
     * @param beanBookInfo beanBookInfo
     * @param indexChapter indexChapter
     * @param e            e
     * @param rechargeFrom rechargeFrom
     */
    private void getSingleChapterBookHandle(BaseActivity activity, final BeanBookInfo beanBookInfo, ArrayList<BeanChapterInfo> chapterList, final BeanChapterInfo indexChapter, ObservableEmitter<LoadResult> e, final String rechargeFrom) {

        BookInfo bookInfo = InsertBookInfoDataUtil.appendBookAndChapters(activity, chapterList, beanBookInfo, false, null);

        CatalogInfo toLoadChapter = DBUtils.getCatalog(activity, bookInfo.bookid, indexChapter.chapterId);
        if (toLoadChapter == null) {
            String startChapter = "";
            CatalogInfo lastCatalog = DBUtils.getLastCatalog(activity, bookInfo.bookid);
            if (lastCatalog != null) {
                startChapter = lastCatalog.catalogid;
            }
            List<BeanChapterInfo> chapters = BookLoader.getInstance().getChaptersFromServer(bookInfo, startChapter, "0");
            if (chapters != null && chapters.size() > 0) {
                InsertBookInfoDataUtil.appendChapters(activity, chapters, bookInfo.bookid, null);
            }
        }

        toLoadChapter = DBUtils.getCatalog(activity, bookInfo.bookid, indexChapter.chapterId);
        if (toLoadChapter == null) {
            e.onNext(new LoadResult(LoadResult.STATUS_ERROR));
            e.onComplete();
        } else {
            RechargeParams rechargeParams = new RechargeParams(RechargeParams.READACTION_SINGLE, bookInfo);
            rechargeParams.setOperateFrom(activity.getName());
            rechargeParams.setPartFrom(rechargeFrom);

            LoadResult result = BookLoader.getInstance().loadOneChapter(activity, bookInfo, toLoadChapter, rechargeParams);
            if (result != null) {
                result.mChapter = toLoadChapter;
            }
            e.onNext(result);
            e.onComplete();
        }

    }


    /**
     * 加载一个章节的Observable
     * 用于阅读器和阅读器的目录页面
     *
     * @param activity     activity
     * @param bookInfo     bookInfo
     * @param catalogInfo  catalogInfo
     * @param rechargeFrom rechargeFrom
     * @return Observable
     */
    protected Observable<LoadResult> getSingleChapterObservable(final BaseActivity activity, final BookInfo bookInfo, final CatalogInfo catalogInfo, final String rechargeFrom) {
        return Observable.create(new ObservableOnSubscribe<LoadResult>() {
            @Override
            public void subscribe(ObservableEmitter<LoadResult> e) {
                RechargeParams rechargeParams = new RechargeParams("3", bookInfo);
                rechargeParams.setOperateFrom(activity.getName());
                rechargeParams.setPartFrom(LogConstants.ORDER_SOURCE_FROM_VALUE_5);
                rechargeParams.isReader = true;
                LoadResult result = BookLoader.getInstance().loadOneChapter(activity, bookInfo, catalogInfo, rechargeParams);
                if (result != null) {
                    result.mChapter = catalogInfo;
                }
                e.onNext(result);
                e.onComplete();
            }
        });
    }


    /**
     * 批量下载的Observable
     * 书籍详情页
     *
     * @param bookDetailBean bookDetailBean
     * @param activity       activity
     * @return Observable
     */
    protected Observable<LoadResult> getBulkChaptersObservable(final BaseActivity activity, final BeanBookDetail bookDetailBean) {
        return Observable.create(new ObservableOnSubscribe<LoadResult>() {
            @Override
            public void subscribe(ObservableEmitter<LoadResult> e) {
                if (bookDetailBean == null || bookDetailBean.book == null || ListUtils.isEmpty(bookDetailBean.chapters)) {
                    e.onError(new RuntimeException("数据不全"));
                    return;
                }
                BeanBookInfo beanBookInfo = bookDetailBean.book;

                JSONObject whiteObj = WhiteListWorker.getWhiteObj();
                String readFrom = null;
                if (null != whiteObj) {
                    readFrom = whiteObj.toString();
                }

                BookInfo bookInfo = DBUtils.findByBookId(activity, beanBookInfo.bookId);
                if (bookInfo == null) {

                    bookInfo = InsertBookInfoDataUtil.initBookInfo(bookDetailBean.chapters, beanBookInfo, false, false, readFrom);
                    DBUtils.insertBook(activity, bookInfo);
                } else {
                    BookInfo mBookInfo = new BookInfo();
                    mBookInfo.bookid = beanBookInfo.bookId;
                    mBookInfo.price = beanBookInfo.price;
                    DBUtils.updateBook(activity, mBookInfo);
                }

                LoadResult result = bookBulkChapters(activity, bookDetailBean, bookInfo);

                e.onNext(result);
                e.onComplete();
            }
        });
    }

    /**
     * 书籍详情页面获取信息的Observable
     *
     * @param bookId  bookId
     * @param context context
     * @return Observable
     */
    protected Observable<BeanBookDetail> getBookDetailObservable(final Context context, final String bookId) {
        return Observable.create(new ObservableOnSubscribe<BeanBookDetail>() {
            @Override
            public void subscribe(ObservableEmitter<BeanBookDetail> e) {
                try {
                    BeanBookDetail bookDetailBean = HwRequestLib.getInstance().bookdetailRequest(bookId);
                    e.onNext(bookDetailBean);
                    e.onComplete();
                } catch (Exception ex) {
                    ALog.printStackTrace(ex);
                    e.onError(ex);
                }
            }
        });
    }

    /**
     * 执行加入书架逻辑的Observable
     *
     * @param beanBookInfo   beanBookInfo
     * @param bookDetailBean bookDetailBean
     * @param context        context
     * @return Observable
     */
    protected Observable<BookInfo> getAddToShelfObservable(final Context context, final BeanBookInfo beanBookInfo, final BeanBookDetail bookDetailBean) {
        return Observable.create(new ObservableOnSubscribe<BookInfo>() {
            @Override
            public void subscribe(ObservableEmitter<BookInfo> e) {
                if (beanBookInfo == null || bookDetailBean == null) {
                    e.onError(new RuntimeException("数据不全"));
                    return;
                }

                JSONObject whiteObj = WhiteListWorker.getWhiteObj();
                String readFrom = null;
                if (null != whiteObj) {
                    readFrom = whiteObj.toString();
                }

                BookInfo bookInfo = InsertBookInfoDataUtil.appendBookAndChapters(context.getApplicationContext(), bookDetailBean.chapters, beanBookInfo, true, null, readFrom);

                e.onNext(bookInfo);
                e.onComplete();
            }
        });
    }


    /**
     * 免费试读的Observable，加载章节之前，需要保证书籍和章节信息先入库
     *
     * @param activity       activity
     * @param bookDetailBean bookDetailBean
     * @return Observable
     */
    protected Observable<LoadResult> getFreeReadingObservable(final BaseActivity activity, final BeanBookDetail bookDetailBean) {
        return Observable.create(new ObservableOnSubscribe<LoadResult>() {
            @Override
            public void subscribe(ObservableEmitter<LoadResult> e) {
                if (bookDetailBean == null || bookDetailBean.book == null || ListUtils.isEmpty(bookDetailBean.chapters)) {
                    e.onError(new RuntimeException("数据不全"));
                    return;
                }

                BeanBookInfo beanBookInfo = bookDetailBean.book;

                String bookId = beanBookInfo.bookId;
                BookInfo bookInfo = DBUtils.findByBookId(activity, bookId);
                if (bookInfo == null) {
                    //免费试读 阅读第一个章节 快速打开
                    LoadResult loadResult = BookLoader.getInstance().fastReadChapter(activity, beanBookInfo.bookId, false);
                    e.onNext(loadResult);
                    e.onComplete();
                    return;
                } else {
                    BookInfo mBookInfo = new BookInfo();
                    mBookInfo.bookid = bookId;
                    mBookInfo.price = beanBookInfo.price;
                    DBUtils.updateBook(activity, mBookInfo);
                }

                InsertBookInfoDataUtil.appendChapters(activity, bookDetailBean.chapters, bookId, null);
                CatalogInfo toLoadChapter = DBUtils.getCatalog(activity, bookInfo.bookid, bookInfo.currentCatalogId);

                if (toLoadChapter == null) {
                    //免费试读 阅读第一个章节 快速打开
                    LoadResult loadResult = BookLoader.getInstance().fastReadChapter(activity, beanBookInfo.bookId, false);
                    e.onNext(loadResult);
                    e.onComplete();
                } else {
                    RechargeParams rechargeParams = new RechargeParams(RechargeParams.READACTION_SINGLE, bookInfo);
                    rechargeParams.setOperateFrom(activity.getName());
                    rechargeParams.setPartFrom(LogConstants.ORDER_SOURCE_FROM_VALUE_10);


                    LoadResult result = BookLoader.getInstance().loadOneChapter(activity, bookInfo, toLoadChapter, rechargeParams);
                    if (result != null) {
                        result.mChapter = toLoadChapter;
                    }
                    e.onNext(result);
                    e.onComplete();
                }
            }
        });

    }

    /**
     * 下载后续已购章节
     *
     * @param activity activity
     * @param bookInfo bookInfo
     * @return Observable
     */
    protected Observable<LoadResult> getPurchasedObservable(final Activity activity, final BookInfo bookInfo) {
        return Observable.create(new ObservableOnSubscribe<LoadResult>() {

            @Override
            public void subscribe(ObservableEmitter<LoadResult> e) {
                CatalogInfo currentCatalog = null;

                currentCatalog = DBUtils.getCatalog(pageUI.getContext(), bookInfo.bookid, bookInfo.currentCatalogId);
                currentCatalog = DBUtils.getFirstNoDownloadCatalog(pageUI.getContext(), currentCatalog);
                if (currentCatalog == null) {
                    e.onNext(new LoadResult(LoadResult.STATUS_SUCCESS, new ArrayList<String>()));
                    e.onComplete();
                    return;

                }
                LoadResult loadResult = BookLoader.getInstance().loadPurchasedChapters(activity, bookInfo, currentCatalog);
                e.onNext(loadResult);
                e.onComplete();
            }
        });
    }

    //    Pattern p = Pattern.compile(REGULAR_STR);

    //    /**
    //     * 扫描本地书籍章节信息的Observable,扫到一个章节发送一次数据，并在扫描完成以后，将扫描到的章节集体入库
    //     *
    //     * @param path
    //     * @return
    //     */
    //    protected Observable<CatalogInfo> getScanChapterObservable(final String path) {
    //        return Observable.create(new ObservableOnSubscribe<CatalogInfo>() {
    //            @Override
    //            public void subscribe(ObservableEmitter<CatalogInfo> e) {
    //                String bookId = path;
    //                DBUtils.deleteCatalogByBoodId(pageUI.getContext(), bookId);
    //
    //                ArrayList<CatalogInfo> list = new ArrayList<>();
    //
    //                UtilFileReadLine raf = null;
    //
    //                try {
    //                    raf = new UtilFileReadLine(new File(path));
    //
    //                    UtilFileReadLine.RafLine line;
    //
    //                    while ((line = raf.readRafLine()) != null) {
    //                        Matcher m = p.matcher(line.line);
    //                        if (m.find()) {
    //                            long currentPos = line.start;
    //                            String chapterName = line.line;
    //
    //                            CatalogInfo bean = new CatalogInfo(path, currentPos + "");
    //                            bean.catalogname = chapterName;
    //                            bean.currentPos = currentPos;
    //                            bean.ispay = "1";
    //                            bean.isalreadypay = "0";
    //                            bean.isdownload = "0";
    //                            bean.path = path;
    //
    //                            list.add(bean);
    //
    //                            e.onNext(bean);
    //                        }
    //                    }
    //                } catch (IOException ex) {
    //                    ex.printStackTrace();
    //                } finally {
    //                    if (raf != null) {
    //                        raf.recycle();
    //                    }
    //                }
    //
    //                if (!e.isDisposed()) {
    //                    if (list.size() > 0) {
    //                        DBUtils.insertLotCatalog(pageUI.getContext(), list);
    //                    }
    //
    //                    SpUtil.getinstance(pageUI.getContext()).setLocalBookScanTime(bookId, System.currentTimeMillis());
    //                }
    //
    //
    //                e.onComplete();
    //            }
    //        });
    //    }

    /**
     * 章节加载逻辑完成后，后续处理的Observer
     *
     * @param loadingType loadingType
     * @return DisposableObserver
     */
    protected DisposableObserver<LoadResult> getChapterLoadObserver(@DialogConstants.DialogType final int loadingType) {
        return getChapterLoadObserver(loadingType, 0);
    }

    /**
     * 章节加载逻辑完成后，后续处理的Observer
     *
     * @param loadingType loadingType
     * @param currentPos  currentPos
     * @return DisposableObserver
     */
    protected DisposableObserver<LoadResult> getChapterLoadObserver(@DialogConstants.DialogType final int loadingType, final long currentPos) {
        return new DisposableObserver<LoadResult>() {

            @Override
            public void onNext(final LoadResult value) {
                pageUI.dissMissDialog();

                if (value == null) {
                    ALog.dLwx("LoadResult null");
                    pageUI.getHostActivity().showNotNetDialog();
                    return;
                }
                if (value.isSuccess()) {
                    CatalogInfo info = DBUtils.getCatalog(pageUI.getContext(), value.mChapter.bookid, value.mChapter.catalogid);
                    if (null != info) {
                        info.currentPos = currentPos;
                        pageUI.intoReaderCatalogInfo(info);
                    }

                } else {
                    ALog.dLwx("LoadResult:" + value.status);

                    showResultType(value);
                }
            }

            @Override
            public void onError(Throwable e) {
                ALog.eLwx("load ex:" + e.getMessage());
                pageUI.dissMissDialog();
                if (pageUI.getHostActivity() != null) {
                    pageUI.getHostActivity().showNotNetDialog();
                }
            }

            @Override
            public void onComplete() {
                ALog.dLwx("load onComplete");
                pageUI.dissMissDialog();
            }

            @Override
            protected void onStart() {
                super.onStart();
                pageUI.showDialogByType(loadingType);
            }
        };
    }

    private void showResultType(LoadResult value) {
        if (value.mChapter != null) {
            //            if (!value.isCanceled()) {
            //                // FIXME: cmt 2018/4/24 章节下载错误 待取数sdk完善
            //            }
            if (value.status == LoadResult.STATUS_NET_WORK_NOT_USE || value.status == LoadResult.STATUS_NET_WORK_NOT_COOL || value.status == LoadResult.STATUS_ERROR && !NetworkUtils.getInstance().checkNet()) {
                if (!TextUtils.isEmpty(value.getMessage(pageUI.getContext())) && pageUI.getHostActivity() != null) {
                    pageUI.getHostActivity().showNotNetDialog();
                }
            } else {
                ReaderUtils.dialogOrToast(pageUI.getHostActivity(), value.getMessage(pageUI.getContext()), true, value.mChapter.bookid);
            }
        } else {
            if (value.status == LoadResult.STATUS_NET_WORK_NOT_USE || value.status == LoadResult.STATUS_NET_WORK_NOT_COOL || value.status == LoadResult.STATUS_ERROR && !NetworkUtils.getInstance().checkNet()) {
                if (!TextUtils.isEmpty(value.getMessage(pageUI.getContext())) && pageUI.getHostActivity() != null) {
                    pageUI.getHostActivity().showNotNetDialog();
                }
            } else {
                ToastAlone.showShort(value.getMessage(pageUI.getContext()));
            }
        }
    }


    private LoadResult bookBulkChapters(BaseActivity activity, BeanBookDetail bookDetailBean, BookInfo bookInfo) {
        InsertBookInfoDataUtil.appendChapters(activity, bookDetailBean.chapters, bookInfo.bookid, null);

        CatalogInfo currentCatalog = DBUtils.getCatalog(activity, bookInfo.bookid, bookInfo.currentCatalogId);

        LoadResult result = new LoadResult(LoadResult.STATUS_ERROR);
        if (currentCatalog == null) {
            ALog.eZz("章节信息为空");
            return result;
        }

        RechargeParams rechargeParams = new RechargeParams("4", bookInfo);
        rechargeParams.setOperateFrom(activity.getName());
        rechargeParams.setPartFrom(LogConstants.ORDER_SOURCE_FROM_VALUE_1);

        CatalogInfo noDownloadCatalog = DBUtils.getFirstNoDownloadCatalog(activity, currentCatalog);

        if (noDownloadCatalog == null) {
            return new LoadResult(LoadResult.STATUS_ERROR, activity.getResources().getString(R.string.followed_by_no_cacheable_chapter));
        }

        result = BookLoader.getInstance().loadBulkChapters(activity, bookInfo, noDownloadCatalog, rechargeParams);

        if (result != null) {
            result.mChapter = currentCatalog;
        }

        return result;
    }
}
