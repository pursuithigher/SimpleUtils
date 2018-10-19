package com.dzbook.mvp.presenter;

import android.app.Activity;
import android.text.TextUtils;

import com.dzbook.database.bean.BookInfo;
import com.dzbook.database.bean.CatalogInfo;
import com.dzbook.lib.rx.CompositeDisposable;
import com.dzbook.lib.utils.ALog;
import com.dzbook.loader.BookLoader;
import com.dzbook.loader.LoadResult;
import com.dzbook.log.DzLog;
import com.dzbook.log.LogConstants;
import com.dzbook.mvp.BasePresenter;
import com.dzbook.mvp.DialogConstants;
import com.dzbook.mvp.UI.MissingContentUI;
import com.dzbook.net.hw.HwRequestLib;
import com.dzbook.service.RechargeParams;
import com.dzbook.utils.DBUtils;
import com.dzbook.utils.NetworkUtils;
import com.dzbook.utils.TurnPageUtils;
import com.ishugui.R;

import java.util.HashMap;

import hw.sdk.net.bean.reader.MissContentBeanInfo;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

/**
 * MissingContentPresenter
 *
 * @author lizhongzhong 2017/8/16.
 */

public class MissingContentPresenter extends BasePresenter {

    /**
     * 章节信息
     */
    public static final String CHAPTER_INFO = "catalogInfo";

    /**
     * 书本信息
     */
    public static final String BOOK_INFO = "bookInfo";

    private CompositeDisposable composite = new CompositeDisposable();
    private MissingContentUI mUI;
    private CatalogInfo catalogInfo;
    private BookInfo bookInfo;

    /**
     * 构造
     *
     * @param ui ui
     */
    public MissingContentPresenter(MissingContentUI ui) {
        this.mUI = ui;
    }

    /**
     * 获取参数
     */
    public void getParams() {
        catalogInfo = (CatalogInfo) ((Activity) (mUI.getContext())).getIntent().getSerializableExtra(CHAPTER_INFO);
        bookInfo = (BookInfo) ((Activity) (mUI.getContext())).getIntent().getSerializableExtra(BOOK_INFO);

        if (bookInfo == null) {
            mUI.finish();
        } else {
            mUI.setTitle(bookInfo.bookname);
        }
    }


    /**
     * 获取页面信息
     */
    public void setPageInfo() {

        if (catalogInfo != null) {
            //2(缺章,未领取) 3(缺章，已领取) //4(删章)
            if (catalogInfo.isContentEmptyAndReceiveAward()) {
                mUI.setNormalReceiveAwardShow();

            } else if (catalogInfo.isContentEmptyAndAlreadyReceveAward()) {
                mUI.setAlreadyReceveAward();

            } else if (catalogInfo.isContentEmptyChapterDeleted()) {
                mUI.setDeleteChapterReceiveAwardShow();
            }
        }

    }

    /**
     * 加载下一章
     */
    public void loadNextChapter() {

        mUI.showDialogByType(DialogConstants.TYPE_GET_DATA);
        Disposable disposable = loadNextChapterContent().subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribeWith(new DisposableObserver<LoadResult>() {
            @Override
            public void onNext(LoadResult value) {
                mUI.dissMissDialog();

                if (value == null) {
                    ALog.dZz("LoadResult null");
                    return;
                }
                if (value.isSuccess()) {

                    CatalogInfo info = DBUtils.getCatalog(mUI.getHostActivity(), value.mChapter.bookid, value.mChapter.catalogid);
                    mUI.getHostActivity().finish();
                    mUI.intoReaderCatalogInfo(info);

                } else {

                    if (value.status == LoadResult.STATUS_ALREADY_LAST_CHAPTER) {

                        if (catalogInfo != null) {
                            chaseRecommendBooks(catalogInfo.catalogid);
                        } else {
                            mUI.showMessage(R.string.str_last_page);
                        }
                    } else {
                        if (value.status == LoadResult.STATUS_NET_WORK_NOT_USE || value.status == LoadResult.STATUS_NET_WORK_NOT_COOL || value.status == LoadResult.STATUS_ERROR && !NetworkUtils.getInstance().checkNet()) {
                            if (!TextUtils.isEmpty(value.getMessage(mUI.getContext())) && mUI.getHostActivity() != null) {
                                mUI.getHostActivity().showNotNetDialog();
                            }
                        } else {
                            mUI.showMessage(value.getMessage(mUI.getContext()));
                        }
                        ALog.dZz("LoadResult:" + value.status);
                    }

                }
            }

            @Override
            public void onError(Throwable e) {
                mUI.dissMissDialog();
                ALog.dZz("load ex:" + e.getMessage());
            }

            @Override
            public void onComplete() {
                ALog.dZz("load onComplete");
            }
        });

        composite.addAndDisposeOldByKey("loadNextChapter", disposable);

    }

    /**
     * 接收奖励
     */
    public void receiveAward() {

        mUI.showDialogByType(DialogConstants.TYPE_GET_DATA);
        //基地
        Disposable disposable = receiveAwardRequest().subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribeWith(new DisposableObserver<LoadResult>() {
            @Override
            public void onNext(LoadResult value) {
                mUI.dissMissDialog();

                if (value == null) {
                    ALog.dZz("LoadResult null");
                    mUI.getHostActivity().showNotNetDialog();
                    return;
                }
                if (value.isSuccess()) {

                    if (value.status == LoadResult.STATUS_NET_WORK_NOT_USE || value.status == LoadResult.STATUS_NET_WORK_NOT_COOL || value.status == LoadResult.STATUS_ERROR && !NetworkUtils.getInstance().checkNet()) {
                        if (!TextUtils.isEmpty(value.getMessage(mUI.getContext())) && mUI.getHostActivity() != null) {
                            mUI.getHostActivity().showNotNetDialog();
                        }
                    } else {
                        mUI.showMessage(value.getMessage(mUI.getContext()));
                    }
                    mUI.setAlreadyReceveAward();

                } else {
                    if (value.status == LoadResult.STATUS_NET_WORK_NOT_USE || value.status == LoadResult.STATUS_NET_WORK_NOT_COOL || value.status == LoadResult.STATUS_ERROR && !NetworkUtils.getInstance().checkNet()) {
                        if (!TextUtils.isEmpty(value.getMessage(mUI.getContext())) && mUI.getHostActivity() != null) {
                            mUI.getHostActivity().showNotNetDialog();
                        }
                    } else {
                        mUI.showMessage(value.getMessage(mUI.getContext()));
                    }
                    ALog.dZz("LoadResult:" + value.status);
                }
            }

            @Override
            public void onError(Throwable e) {
                mUI.dissMissDialog();
                ALog.eZz("load ex:" + e.getMessage());
            }

            @Override
            public void onComplete() {
                ALog.dZz("load onComplete");
            }
        });

        composite.addAndDisposeOldByKey("missContentReceiveAward", disposable);
    }


    private Observable<LoadResult> receiveAwardRequest() {
        return Observable.create(new ObservableOnSubscribe<LoadResult>() {
            @Override
            public void subscribe(ObservableEmitter<LoadResult> e) {
                if (catalogInfo == null) {
                    e.onError(new RuntimeException("数据不全"));
                    return;
                }
                //日志
                dzLogReceive();

                LoadResult loadResult = new LoadResult(LoadResult.STATUS_ERROR);

                try {
                    MissContentBeanInfo beanInfo = HwRequestLib.getInstance().receiveMissContentAwardBeanInfo(catalogInfo.bookid, catalogInfo.catalogid);

                    if (beanInfo.isSuccess()) {
                        ALog.dZz("miss content award tips:" + beanInfo.tips + ",award:" + beanInfo.amount);

                        if (beanInfo.amount > 0) {
                            loadResult = new LoadResult(LoadResult.STATUS_SUCCESS, beanInfo.tips);
                        } else {
                            loadResult = new LoadResult(LoadResult.STATUS_ERROR, beanInfo.tips);
                        }
                    }

                } catch (Exception e1) {
                    ALog.printStack(e1);
                }

                e.onNext(loadResult);
                e.onComplete();
            }
        });
    }

    private Observable<LoadResult> loadNextChapterContent() {
        return Observable.create(new ObservableOnSubscribe<LoadResult>() {
            @Override
            public void subscribe(ObservableEmitter<LoadResult> e) {
                if (catalogInfo == null) {
                    e.onError(new RuntimeException("数据不全"));
                    return;
                }

                //日志
                dzLogLoadNext();

                RechargeParams rechargeParams = new RechargeParams("3", bookInfo);
                rechargeParams.setOperateFrom(mUI.getHostActivity().getName());
                rechargeParams.setPartFrom(LogConstants.ORDER_SOURCE_FROM_VALUE_10);

                CatalogInfo mCatalogInfo = DBUtils.getNextCatalog(mUI.getContext(), catalogInfo.bookid, catalogInfo.catalogid);
                if (mCatalogInfo == null) {
                    e.onNext(new LoadResult(LoadResult.STATUS_ALREADY_LAST_CHAPTER));
                    e.onComplete();
                    return;
                }

                LoadResult result = BookLoader.getInstance().loadOneChapter(mUI.getHostActivity(), bookInfo, mCatalogInfo, rechargeParams);
                if (result != null) {
                    result.mChapter = mCatalogInfo;
                }


                e.onNext(result);
                e.onComplete();
            }
        });
    }


    /**
     * 打点
     */
    public void dzEvenLog() {
        if (catalogInfo != null) {
            HashMap<String, String> map = new HashMap<String, String>();

            String status = "";
            //2(缺章,未领取) 3(缺章，已领取) //4(删章)
            if (TextUtils.equals(catalogInfo.isdownload, "2")) {
                status = LogConstants.QNR_STATUS_VALULE_1;
            } else if (TextUtils.equals(catalogInfo.isdownload, "3")) {
                status = LogConstants.QNR_STATUS_VALULE_2;
            } else if (TextUtils.equals(catalogInfo.isdownload, "4")) {
                status = LogConstants.QNR_STATUS_VALULE_3;
            }
            map.put(LogConstants.KEY_QNR_STATUS, status);
            map.put(LogConstants.KEY_QNR_BID, catalogInfo.bookid);
            map.put(LogConstants.KEY_QNR_CID, catalogInfo.catalogid);

            DzLog.getInstance().logEvent(LogConstants.EVENT_QNR, map, null);
        }
    }


    /**
     * destory
     */
    public void destory() {
        composite.disposeAll();
    }

    //领取
    private void dzLogReceive() {
        if (catalogInfo != null) {
            HashMap<String, String> map = new HashMap<String, String>();
            map.put(LogConstants.KEY_ORDER_BID, catalogInfo.bookid);
            map.put(LogConstants.KEY_ORDER_CID, catalogInfo.catalogid);
            DzLog.getInstance().logClick(LogConstants.MODULE_QNR, LogConstants.ZONE_QNR_LQ, catalogInfo.bookid, map, null);
        }
    }

    //下一章
    private void dzLogLoadNext() {
        if (catalogInfo != null) {
            HashMap<String, String> map = new HashMap<String, String>();
            map.put(LogConstants.KEY_ORDER_BID, catalogInfo.bookid);
            map.put(LogConstants.KEY_ORDER_CID, catalogInfo.catalogid);
            DzLog.getInstance().logClick(LogConstants.MODULE_QNR, LogConstants.ZONE_QNR_XYZ, catalogInfo.bookid, map, null);
        }
    }

    /**
     * 追更推荐
     */
    private void chaseRecommendBooks(final String lastChapterId) {
        if (null != bookInfo) {
            TurnPageUtils.toRecommentPage(mUI.getContext(), bookInfo.bookid, bookInfo.bookname, bookInfo.bookstatus, lastChapterId, bookInfo.bookfrom);
        }
    }
}
