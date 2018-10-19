package com.dzbook.mvp.presenter;

import android.text.TextUtils;

import com.dzbook.AppConst;
import com.dzbook.bean.ShareBeanInfo;
import com.dzbook.database.bean.BookInfo;
import com.dzbook.event.EventBus;
import com.dzbook.lib.rx.CompositeDisposable;
import com.dzbook.lib.utils.ALog;
import com.dzbook.lib.utils.StringUtil;
import com.dzbook.loader.LoadResult;
import com.dzbook.log.DzLog;
import com.dzbook.log.LogConstants;
import com.dzbook.model.UserGrow;
import com.dzbook.mvp.DialogConstants;
import com.dzbook.mvp.UI.BookDetailUI;
import com.dzbook.utils.DBUtils;
import com.dzbook.utils.ListUtils;
import com.dzbook.utils.ShareUtils;
import com.dzbook.utils.SpUtil;
import com.dzbook.utils.ThirdPartyLog;
import com.ishugui.R;
import com.iss.view.common.ToastAlone;

import java.util.ArrayList;
import java.util.HashMap;

import hw.sdk.net.bean.BeanBookInfo;
import hw.sdk.net.bean.BeanChapterInfo;
import hw.sdk.net.bean.bookDetail.BeanBookDetail;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;


/**
 * BookDetailPresenter
 *
 * @author wxliao on 17/7/25.
 */

public class BookDetailPresenter extends BookPagePresenter {
    private CompositeDisposable composite = new CompositeDisposable();

    private BeanBookDetail mBookDetailBean;


    /**
     * 极光推送推送的通知 点击通知 传递过来的书籍id
     */
    private String mBookId;

    private BookDetailUI mUI;


    /**
     * 构造
     *
     * @param ui             ui
     * @param bookDetailBean bookDetailBean
     */
    public BookDetailPresenter(BookDetailUI ui, BeanBookDetail bookDetailBean) {
        super(ui);
        mUI = ui;
        this.mBookDetailBean = bookDetailBean;
        create();
    }

    /**
     * 构造
     *
     * @param ui     ui
     * @param bookId bookId
     */
    public BookDetailPresenter(BookDetailUI ui, String bookId) {
        super(ui);
        mUI = ui;
        mBookId = bookId;
        create();
    }

    /**
     * EventBus
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

    private BeanBookInfo getBookDetailBean() {
        if (null != mBookDetailBean) {
            return mBookDetailBean.book;
        }
        return null;
    }

    private ArrayList<BeanChapterInfo> getChapterList() {
        if (null != mBookDetailBean) {
            return mBookDetailBean.chapters;
        }
        return null;
    }

    /**
     * 获取bookid
     *
     * @return string
     */
    public String getBookId() {
        if (null != mBookDetailBean && null != mBookDetailBean.book) {
            return mBookDetailBean.book.bookId;
        }
        return mBookId;
    }

    /**
     * 打点
     */
    public void pvLog() {
        String bid = getBookId();
        if (!TextUtils.isEmpty(bid)) {
            HashMap<String, String> map = new HashMap<>();
            map.put("bid", bid);
            DzLog.getInstance().logPv(mUI.getHostActivity(), map, null);
        }
    }


    /**
     * MenuInfo
     */
    static class MenuInfo {
        BeanBookInfo detailInfoResBean;
        BookInfo bookInfo;
        boolean isShowFreeStatus;
        int marketStatus;

        public MenuInfo(BeanBookInfo beanBookInfo, BookInfo bookInfo, boolean isShowFreeStatus, int marketStatus) {
            this.detailInfoResBean = beanBookInfo;
            this.bookInfo = bookInfo;
            this.isShowFreeStatus = isShowFreeStatus;
            this.marketStatus = marketStatus;
        }
    }

    /**
     * 刷新菜单
     */
    public void refreshMenu() {

        Observable<MenuInfo> observable = Observable.create(new ObservableOnSubscribe<MenuInfo>() {
            @Override
            public void subscribe(ObservableEmitter<MenuInfo> e) {
                BeanBookInfo beanBookInfo = null;
                if (mBookDetailBean != null) {
                    beanBookInfo = mBookDetailBean.book;
                }

                if (beanBookInfo == null) {
                    e.onNext(null);
                } else {
                    boolean isShowFreeStatus = false;
                    BookInfo bookInfo = DBUtils.findByBookId(mUI.getHostActivity(), beanBookInfo.bookId);
                    if (bookInfo != null) {
                        isShowFreeStatus = bookInfo.isFreeStatus(mUI.getHostActivity());
                    }

                    e.onNext(new MenuInfo(beanBookInfo, bookInfo, isShowFreeStatus, beanBookInfo.control));
                }
                e.onComplete();
            }
        });

        Disposable disposable = observable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribeWith(new DisposableObserver<MenuInfo>() {
            @Override
            public void onNext(MenuInfo value) {
                if (value != null) {
                    mUI.refreshMenu(value.detailInfoResBean, value.marketStatus, value.bookInfo, value.isShowFreeStatus);
                }
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        });

        composite.addAndDisposeOldByKey("refreshMenu", disposable);
    }


    /**
     * 批量下载
     *
     * @param downBookAll downBookAll
     */
    public void download(boolean downBookAll) {
        BeanBookInfo beanBookInfo = getBookDetailBean();
        if (null == beanBookInfo) {
            return;
        }

        DzLog.getInstance().logClick(LogConstants.MODULE_SJXQ, LogConstants.ZONE_SJXQ_PLXZ, beanBookInfo.bookId, null, "");
        ThirdPartyLog.onEvent(mUI.getHostActivity(), ThirdPartyLog.DTL_LOT);
        ThirdPartyLog.onEventValueOldClick(mUI.getHostActivity(), ThirdPartyLog.BOOK_DETAIL_UMENG_ID, ThirdPartyLog.BOOK_DETAIL_DOWNLOAD_VALUE, 1);

        Disposable disposable = getBulkChaptersObservable(mUI.getHostActivity(), mBookDetailBean).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribeWith(getChapterLoadObserver(DialogConstants.TYPE_GET_DATA));

        composite.addAndDisposeOldByKey("downloadBook", disposable);
    }


    /**
     * 免费试读
     */
    public void freeReading() {
        BeanBookInfo beanBookInfo = getBookDetailBean();
        if (null == beanBookInfo) {
            return;
        }

        DzLog.getInstance().logClick(LogConstants.MODULE_SJXQ, LogConstants.ZONE_SJXQ_MFSD, beanBookInfo.bookId, null, "");
        ThirdPartyLog.onEvent(mUI.getHostActivity(), ThirdPartyLog.DTL_FREE);
        ThirdPartyLog.onEventValueOldClick(mUI.getHostActivity(), ThirdPartyLog.BOOK_DETAIL_UMENG_ID, ThirdPartyLog.BOOK_DETAIL_START_READER_VALUE, 1);

        Disposable disposable = getFreeReadingObservable(mUI.getHostActivity(), mBookDetailBean).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribeWith(getChapterLoadObserver(DialogConstants.TYPE_GET_DATA));

        composite.addAndDisposeOldByKey("freeReading", disposable);
    }


    /**
     * 分享
     */
    public void share() {
        BeanBookInfo beanBookInfo = getBookDetailBean();
        if (null == beanBookInfo) {
            return;
        }

        DzLog.getInstance().logClick(LogConstants.MODULE_SJXQ, LogConstants.ZONE_SJXQ_FX, beanBookInfo.bookId, null, "");

        ThirdPartyLog.onEventValueOldClick(mUI.getHostActivity(), ThirdPartyLog.BOOK_DETAIL_UMENG_ID, ThirdPartyLog.BOOK_DETAIL_SHARE_VALUE, 1);

        String shareurl = SpUtil.getinstance(AppConst.getApp()).getSpReaderShareurl();
        String sharedUrl = shareurl + "?bookId=" + beanBookInfo.bookId;
        String introduction = StringUtil.delSpaceAndLn(beanBookInfo.introduction);
        String bookName = beanBookInfo.bookName;
        String coverUrl = beanBookInfo.coverWap;

        ShareBeanInfo shareBeanInfo = new ShareBeanInfo();
        shareBeanInfo.setShareParam(bookName, introduction, sharedUrl, coverUrl);
        ALog.dZz("分享参数：" + shareBeanInfo.toString());
        ShareUtils.goToShare(mUI.getHostActivity(), shareBeanInfo, ShareUtils.DIALOG_SHOW_FROM_BOOK_DETAIL);
    }


    /**
     * 添加到书架
     *
     * @param logMap logMap
     */
    public void addToShelf(HashMap<String, String> logMap) {
        BeanBookInfo beanBookInfo = getBookDetailBean();
        if (null == beanBookInfo) {
            return;
        }

        DzLog.getInstance().logClick(LogConstants.MODULE_SJXQ, LogConstants.ZONE_SJXQ_JRSJ, beanBookInfo.bookId, logMap, null);

        ThirdPartyLog.onEvent(mUI.getHostActivity(), ThirdPartyLog.DTL_ADD_BOOK);
        ThirdPartyLog.onEventValueOldClick(mUI.getHostActivity(), ThirdPartyLog.BOOK_DETAIL_UMENG_ID, ThirdPartyLog.BOOK_DETAIL_JOIN_BOOKSHELF_VALUE, 1);


        Disposable disposable = getAddToShelfObservable(mUI.getContext(), beanBookInfo, mBookDetailBean).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribeWith(new DisposableObserver<BookInfo>() {
            @Override
            public void onNext(BookInfo value) {
                if (value != null) {
                    mUI.setBookShelfMenu(false);
                    // 加入书架，同步成长值
                    ToastAlone.showShort(mUI.getContext().getResources().getString(R.string.add_bookshelf_success));
                    UserGrow.userGrowOnceToday(mUI.getHostActivity(), UserGrow.USER_GROW_ADD_BOOK);
                } else {
                    ToastAlone.showShort(R.string.add_bookshelf_fail);
                }
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        });
        composite.addAndDisposeOldByKey("addToShelf", disposable);
    }


    /**
     * 处理最新章节的点击逻辑
     *
     * @param chapterInfo chapterInfo
     */
    public void handleLastChapterClick(BeanChapterInfo chapterInfo) {
        BeanBookInfo beanBookInfo = getBookDetailBean();
        if (null == beanBookInfo) {
            return;
        }

        ThirdPartyLog.onEventValueOldClick(mUI.getHostActivity(), ThirdPartyLog.BOOK_DETAIL_UMENG_ID, ThirdPartyLog.BOOK_DETAIL_UPDATA_VALUE, 1);


        Observable<LoadResult> observable = getSingleChapterObservable(mUI.getHostActivity(), beanBookInfo, getChapterList(), chapterInfo, LogConstants.ORDER_SOURCE_FROM_VALUE_3);

        Disposable disposable = observable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribeWith(getChapterLoadObserver(DialogConstants.TYPE_GET_DATA));

        composite.addAndDisposeOldByKey("handleLastChapterClick", disposable);

    }

    /**
     * 获取书籍详情
     *
     * @param bookId bookId
     */
    public void getBookDetail(String bookId) {
        Disposable disposable = getBookDetailObservable(mUI.getContext(), bookId).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribeWith(new DisposableObserver<BeanBookDetail>() {
            @Override
            public void onNext(BeanBookDetail result) {
                mUI.dismissLoadDataDialog();
                if (null != result && result.isSuccess()) {
                    mBookDetailBean = result;
                    mUI.setPageData(mBookDetailBean);
                    BeanBookInfo bookInfo = result.book;
                    if (null != bookInfo) {
                        if (ListUtils.isEmpty(mBookDetailBean.chapters)) {
                            //为了更明白分析问题 加上这句以防服务器没有查询到章节
                            ToastAlone.showShort(R.string.chapter_list_error);
                        }
                    }

                    refreshMenu();
                } else if (null != mBookDetailBean && mBookDetailBean.isDelect) {
                    mUI.setDeletePage();
                } else {
                    mUI.setErrPage();
                }
            }

            @Override
            public void onError(Throwable e) {
                mUI.setErrPage();
            }

            @Override
            public void onComplete() {

            }

            @Override
            protected void onStart() {
                mUI.showLoadDataDialog();
            }
        });

        composite.addAndDisposeOldByKey("getBookDetail", disposable);
    }

    /**
     * 判断详情页获取到的bean是否可用
     *
     * @param detailBean
     * @return
     */
    private boolean isDetailInfoAvailable(BeanBookDetail detailBean) {
        if (detailBean == null || detailBean.book == null) {
            return false;
        }

        String bid = detailBean.book.bookId;
        if (TextUtils.isEmpty(bid)) {
            return false;
        }

        if (detailBean.book.isDeleteOrUndercarriage()) {
            ToastAlone.showShort(mUI.getHostActivity().getString(R.string.book_down_shelf));
            return false;
        }

        return true;
    }

    /**
     * 是否vip用户免费阅读书籍
     *
     * @return boolean
     */
    public boolean isVipFreeReadBook() {
        BeanBookInfo beanBookInfo = getBookDetailBean();
        if (beanBookInfo == null) {
            return false;
        }
        return beanBookInfo.isVip == 1 && beanBookInfo.isVipBook == 1;
    }

}
