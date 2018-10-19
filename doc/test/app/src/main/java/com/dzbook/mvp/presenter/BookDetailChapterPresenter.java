package com.dzbook.mvp.presenter;

import com.dzbook.database.bean.BookInfo;
import com.dzbook.event.EventBus;
import com.dzbook.log.LogConstants;
import com.dzbook.mvp.DialogConstants;
import com.dzbook.mvp.UI.BookDetailChapterUI;
import com.dzbook.net.hw.HwRequestLib;
import com.dzbook.utils.DBUtils;

import java.util.ArrayList;

import hw.sdk.net.bean.BeanBlock;
import hw.sdk.net.bean.BeanBookInfo;
import hw.sdk.net.bean.BeanChapterCatalog;
import hw.sdk.net.bean.BeanChapterInfo;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

/**
 * BookDetailChapterPresenter
 *
 * @author wxliao on 17/8/15.
 */

public class BookDetailChapterPresenter extends BookPagePresenter {
    private BookDetailChapterUI mUI;
    private BookInfo bookInfo;
    private BeanBookInfo mBeanBookInfo;

    /**
     * 构造
     *
     * @param ui            ui
     * @param mBeanBookInfo mBeanBookInfo
     */
    public BookDetailChapterPresenter(BookDetailChapterUI ui, BeanBookInfo mBeanBookInfo) {
        super(ui);
        mUI = ui;
        this.mBeanBookInfo = mBeanBookInfo;
        bookInfo = DBUtils.findByBookId(ui.getHostActivity(), mBeanBookInfo.bookId);
        if (bookInfo == null) {
            bookInfo = new BookInfo();
            bookInfo.bookid = mBeanBookInfo.bookId;
            bookInfo.setRechargeParams(mBeanBookInfo.payTips);
            bookInfo.isdefautbook = 1;
        }
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

    /**
     * 获取更多章节信息
     *
     * @param startChapterId startChapterId
     * @param isInit         isInit
     * @param loadingType    loadingType
     * @param blockBean      blockBean
     */
    public void getMoreChapters(final String startChapterId, final boolean isInit, @DialogConstants.DialogType final int loadingType, final BeanBlock blockBean) {
        Disposable disposable = Observable.create(new ObservableOnSubscribe<BeanChapterCatalog>() {
            @Override
            public void subscribe(ObservableEmitter<BeanChapterCatalog> e) throws Exception {
                String needBlockList = "";
                String chapterNum = "51";
                String bookId = "";
                if (null != bookInfo) {
                    bookId = bookInfo.bookid;
                }
                if (isInit) {
                    needBlockList = "1";
                    chapterNum = "50";
                }
                String endChapterId = "";
                if (blockBean != null) {
                    chapterNum = "";
                    endChapterId = blockBean.endId;
                }

                BeanChapterCatalog beanChapterCatalog = HwRequestLib.getInstance().chapterCatalog(bookId, startChapterId, chapterNum, endChapterId, needBlockList);
                if (beanChapterCatalog != null && beanChapterCatalog.isSuccess()) {
                    e.onNext(beanChapterCatalog);
                } else {
                    e.onNext(null);
                }
                e.onComplete();
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribeWith(new DisposableObserver<BeanChapterCatalog>() {
            @Override
            public void onNext(BeanChapterCatalog value) {
                if (value != null) {
                    mUI.addItem(value, isInit, blockBean);
                }
            }

            @Override
            public void onError(Throwable e) {
                mUI.dissMissDialog();
                mUI.initNetErrorStatus();
            }

            @Override
            public void onComplete() {
                mUI.dissMissDialog();
            }

            @Override
            protected void onStart() {
                super.onStart();
                mUI.showDialogByType(loadingType);
            }
        });

        composite.addAndDisposeOldByKey("getMoreChapters", disposable);
    }

    /**
     * 加载章节
     *
     * @param chapterList  chapterList
     * @param indexChapter indexChapter
     */
    public void loadChapter(final ArrayList<BeanChapterInfo> chapterList, final BeanChapterInfo indexChapter) {
        Disposable disposable = getSingleChapterObservable(mUI.getHostActivity(), mBeanBookInfo, chapterList, indexChapter, LogConstants.ORDER_SOURCE_FROM_VALUE_2).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribeWith(getChapterLoadObserver(DialogConstants.TYPE_GET_DATA));
        composite.addAndDisposeOldByKey("loadChapter", disposable);
    }

}
