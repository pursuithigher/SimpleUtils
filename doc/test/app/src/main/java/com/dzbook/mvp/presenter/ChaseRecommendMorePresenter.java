package com.dzbook.mvp.presenter;

import android.content.Intent;
import android.text.TextUtils;

import com.dzbook.lib.rx.CompositeDisposable;
import com.dzbook.lib.utils.ALog;
import com.dzbook.log.DzLog;
import com.dzbook.log.LogConstants;
import com.dzbook.mvp.BasePresenter;
import com.dzbook.mvp.UI.ChaseRecommendMoreUI;
import com.dzbook.net.hw.HwRequestLib;
import com.dzbook.utils.NetworkUtils;

import java.util.HashMap;

import hw.sdk.net.bean.reader.MoreRecommendBook;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

/**
 * ChaseRecommendMorePresenter
 *
 * @author lizhongzhong 2018/3/9.
 */

public class ChaseRecommendMorePresenter extends BasePresenter {

    /**
     * BOOKID
     */
    public static final String CHASE_RECOMMEND_MORE_BOOKID = "chase_recommend_more_bookid";
    /**
     * NAME
     */
    public static final String CHASE_RECOMMEND_MORE_NAME = "chase_recommend_more_name";
    /**
     * TYPE
     */
    public static final String CHASE_RECOMMEND_MORE_TYPE = "chase_recommend_more_type";

    private CompositeDisposable composite = new CompositeDisposable();

    private ChaseRecommendMoreUI mUI;

    private String bookId;
    private String type;
    private String moreName;

    private Integer page = 1;

    private Integer pageSize = 15;

    /**
     * 构造
     *
     * @param ui ui
     */
    public ChaseRecommendMorePresenter(ChaseRecommendMoreUI ui) {
        this.mUI = ui;
    }

    /**
     * getParams
     */
    public void getParams() {
        Intent intent = mUI.getHostActivity().getIntent();
        if (intent != null) {
            bookId = intent.getStringExtra(CHASE_RECOMMEND_MORE_BOOKID);
            moreName = intent.getStringExtra(CHASE_RECOMMEND_MORE_NAME);
            type = intent.getStringExtra(CHASE_RECOMMEND_MORE_TYPE);
            if (TextUtils.isEmpty(bookId)) {
                mUI.showMessage("追更书籍标识为空");
                mUI.myFinish();
                return;
            }

            if (!TextUtils.isEmpty(moreName)) {
                mUI.setMyTitle(moreName);
            }
        }
    }

    /**
     * 获取更多书籍数据
     */
    public void getMoreBooksInfo() {
        page++;
        getChaseRecommendMoreInfo(false, true);
    }

    /**
     * 第一页数据
     *
     * @param isNeedLoadProgress isNeedLoadProgress
     */
    public void getFristRequstChaseRecommendMoreInfo(boolean isNeedLoadProgress) {
        page = 1;
        getChaseRecommendMoreInfo(isNeedLoadProgress, false);
    }

    /**
     * 获取数据
     *
     * @param isNeedLoadProgress isNeedLoadProgress
     * @param isLoadMore         isLoadMore
     */
    public void getChaseRecommendMoreInfo(final boolean isNeedLoadProgress, final boolean isLoadMore) {

        if (!NetworkUtils.getInstance().checkNet()) {
            if (!isLoadMore) {
                mUI.setLoadFail();
            } else {
                mUI.showNoNetView();
            }
            return;
        }

        Disposable disposable = Observable.create(new ObservableOnSubscribe<MoreRecommendBook>() {

            @Override
            public void subscribe(ObservableEmitter<MoreRecommendBook> e) {

                try {
                    //String bookId, int page, int pageSize, int type
                    MoreRecommendBook moreRecommendBook = HwRequestLib.getInstance().moreRecommendBooks(bookId, page, pageSize, Integer.parseInt(type));
                    e.onNext(moreRecommendBook);
                    e.onComplete();
                } catch (Exception ex) {
                    ALog.printStackTrace(ex);
                    e.onError(ex);
                }

            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribeWith(new DisposableObserver<MoreRecommendBook>() {

            @Override
            public void onNext(MoreRecommendBook value) {
                mUI.dismissProgress();
                if (value != null && value.isSuccess()) {

                    mUI.setChaseRecommendMoreInfo(value, isLoadMore);

                } else {
                    if (isNeedLoadProgress) {
                        mUI.setLoadFail();
                    }
                    if (isLoadMore) {
                        page--;
                    }
                }

                mUI.setPullRefreshComplete();
            }

            @Override
            public void onError(Throwable e) {
                if (isNeedLoadProgress) {
                    mUI.setLoadFail();
                }
                if (isLoadMore) {
                    page--;
                }
            }

            @Override
            public void onComplete() {
                mUI.dismissProgress();
            }

            @Override
            protected void onStart() {
                if (isNeedLoadProgress) {
                    mUI.showLoadProgresss();
                }
            }
        });

        composite.addAndDisposeOldByKey("getChaseRecommendBooksInfo", disposable);

    }

    /**
     * 打点
     */
    public void logPv() {
        HashMap<String, String> map = new HashMap<>();
        map.put(LogConstants.KEY_BID, bookId);
        DzLog.getInstance().logPv(mUI.getHostActivity(), map, null);
    }

    /**
     * click 打点
     *
     * @param clickBookId clickBookId
     * @param position    position
     */
    public void logClick(String clickBookId, String position) {
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put(LogConstants.KEY_INDEX, position);
        DzLog.getInstance().logClick(LogConstants.MODULE_YDQZGTJGD, bookId, clickBookId, hashMap, "");
    }

    /**
     * destroy
     */
    public void destroy() {
        composite.disposeAll();
    }

}
