package com.dzbook.mvp.presenter;

import android.content.Intent;
import android.text.TextUtils;

import com.dzbook.activity.CenterDetailActivity;
import com.dzbook.activity.detail.BookDetailActivity;
import com.dzbook.activity.reader.ChaseRecommendMoreActivity;
import com.dzbook.lib.rx.CompositeDisposable;
import com.dzbook.lib.utils.ALog;
import com.dzbook.log.DzLog;
import com.dzbook.log.LogConstants;
import com.dzbook.mvp.BasePresenter;
import com.dzbook.mvp.UI.ChaseRecommendUI;
import com.dzbook.net.hw.HwRequestLib;
import com.dzbook.utils.ListUtils;
import com.dzbook.utils.NetworkUtils;
import com.dzbook.utils.SpUtil;

import java.util.HashMap;

import hw.sdk.net.bean.reader.BeanBookRecomment;
import hw.sdk.net.bean.reader.BeanRecommentBookInfo;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

/**
 * author lizhongzhong 2018/3/9.
 */

public class ChaseRecommendPresenter extends BasePresenter {

    /**
     * 推荐书籍 id
     */
    public static final String CHASE_RECOMMEND_BOOK_ID = "chase_recommend_bookId";
    /**
     * 推荐书籍 书名
     */
    public static final String CHASE_RECOMMEND_BOOK_NAME = "chase_recommend_bookName";
    /**
     * 推荐书籍 更新状态
     */
    public static final String CHASE_RECOMMEND_BOOK_STATUS = "chase_recommend_book_status";
    /**
     * 推开书籍 最后一章id
     */
    public static final String CHASE_RECOMMEND_LAST_CHPTERID = "chase_recommend_last_chapterid";

    private ChaseRecommendUI mUI;

    private CompositeDisposable composite = new CompositeDisposable();

    private String lastChapterId;
    private String bookId;
    private String bookName;
    private int bookStatus;


    /**
     * 构造
     *
     * @param ui ui
     */
    public ChaseRecommendPresenter(ChaseRecommendUI ui) {
        this.mUI = ui;
    }


    /**
     * 获取参数
     */
    public void getParams() {
        Intent intent = mUI.getHostActivity().getIntent();
        if (intent != null) {
            lastChapterId = intent.getStringExtra(CHASE_RECOMMEND_LAST_CHPTERID);
            bookId = intent.getStringExtra(CHASE_RECOMMEND_BOOK_ID);
            bookName = intent.getStringExtra(CHASE_RECOMMEND_BOOK_NAME);
            bookStatus = intent.getIntExtra(CHASE_RECOMMEND_BOOK_STATUS, -1);
            if (!TextUtils.isEmpty(bookName)) {
                mUI.setTitle(bookName);
            }
        }
        if (TextUtils.isEmpty(bookId)) {
            mUI.showMessage("追更书籍标识为空");
            mUI.myFinish();
            return;
        }

    }

    /**
     * 获取数据
     */
    public void getChaseRecommendBooksInfo() {

        if (!NetworkUtils.getInstance().checkNet()) {
            mUI.setLoadFail();
            return;
        }

        Disposable disposable = Observable.create(new ObservableOnSubscribe<BeanBookRecomment>() {

            @Override
            public void subscribe(ObservableEmitter<BeanBookRecomment> e) {

                try {
                    BeanBookRecomment beanBookRecomment = HwRequestLib.getInstance().bookRecommentRequest(bookId);

                    e.onNext(beanBookRecomment);
                    e.onComplete();
                } catch (Exception ex) {
                    ALog.printStackTrace(ex);
                    e.onError(ex);
                }

            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribeWith(new DisposableObserver<BeanBookRecomment>() {

            @Override
            public void onNext(BeanBookRecomment value) {
                mUI.dismissProgress();
                if (value != null && value.isSuccess()) {

                    if (!ListUtils.isEmpty(value.data)) {
                        mUI.setChaseRecommendInfo(bookId, value);
                    } else {
                        mUI.setLoadFail();
                    }
                } else {
                    mUI.setLoadFail();
                }
            }

            @Override
            public void onError(Throwable e) {
                mUI.setLoadFail();
            }

            @Override
            public void onComplete() {
                mUI.showSuccess();
                mUI.dismissProgress();
            }

            @Override
            protected void onStart() {
                mUI.showLoadProgresss();
            }
        });

        composite.addAndDisposeOldByKey("getChaseRecommendBooksInfo", disposable);
    }


    /**
     * destroy
     */
    public void destroy() {
        composite.disposeAll();
    }

    /**
     * 处理跳转
     *
     * @param bean bean
     */
    public void lauchChaseMore(BeanRecommentBookInfo bean) {
        if (bean != null) {
            //跳转类型，1.BS类型跳转url 2.CS类型本地跳转
            if (bean.isBsJump()) {
                ChaseRecommendMoreActivity.lauchMore(mUI.getHostActivity(), bean.name, bookId, bean.moreType + "");
            } else {
                CenterDetailActivity.show(mUI.getContext(), bean.url, bean.name, "", false, "", "", "");
            }
        }
    }

    /**
     * 跳转BookDetailActivity
     *
     * @param bookId1   bookId1
     * @param bookName1 bookName1
     */
    public void bookDetailLauch(String bookId1, String bookName1) {
        BookDetailActivity.launch(mUI.getHostActivity(), bookId1, bookName1);
    }


    /**
     * 打点
     */
    public void logZgtsjl() {
        //上行追更log给大数据  //连载未完结的书籍才会追更
        if (!TextUtils.isEmpty(bookId) && bookStatus == 2) {
            HashMap<String, String> map = new HashMap<>(5);
            map.put("bookid", bookId);
            map.put("chapterid", lastChapterId);

            String gtCid = SpUtil.getinstance(mUI.getContext()).getString(SpUtil.PUSH_CLIENTID, "");
            map.put(LogConstants.KEY_GT_CID, gtCid);

            DzLog.getInstance().logEvent(LogConstants.EVENT_ZGTSJL, map, "");
        }
    }


    /**
     * 打点
     *
     * @param isClickMore   isClickMore
     * @param clickBookId   clickBookId
     * @param recommendBean recommendBean
     */
    public void logYdqZgTJ(boolean isClickMore, String clickBookId, BeanRecommentBookInfo recommendBean) {

        if (!TextUtils.isEmpty(bookId) && recommendBean != null) {
            HashMap<String, String> map = new HashMap<>(5);
            if (!isClickMore) {
                map.put(LogConstants.KEY_BID, clickBookId);
            }

            String zone = "";
            if (!TextUtils.isEmpty(recommendBean.logName)) {
                if (recommendBean.logName.contains(LogConstants.ZONE_TLXSJ)) {
                    if (isClickMore) {
                        zone = LogConstants.ZONE_GD_TLXSJ;
                    } else {
                        zone = LogConstants.ZONE_TLXSJ;
                    }
                } else if (recommendBean.logName.contains(LogConstants.ZONE_ZZQTSJ)) {
                    if (isClickMore) {
                        zone = LogConstants.ZONE_GD_ZZQTSJ;
                    } else {
                        zone = LogConstants.ZONE_ZZQTSJ;
                    }
                } else if (recommendBean.logName.contains(LogConstants.ZONE_VIPRMSJ)) {
                    if (isClickMore) {
                        zone = LogConstants.ZONE_GD_VIPRMSJ;
                    } else {
                        zone = LogConstants.ZONE_VIPRMSJ;
                    }
                }
            }
            DzLog.getInstance().logClick(LogConstants.MODULE_YDQZGTJ, zone, bookId, map, null);
        }

    }

    /**
     * 打点
     */
    public void logPv() {
        HashMap<String, String> map = new HashMap<>();
        if (!TextUtils.isEmpty(bookId)) {
            map.put(LogConstants.KEY_BID, bookId);
        }
        DzLog.getInstance().logPv(mUI.getHostActivity(), map, null);
    }
}
