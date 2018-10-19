package com.dzbook.mvp.presenter;

import com.dzbook.lib.rx.CompositeDisposable;
import com.dzbook.lib.utils.ALog;
import com.dzbook.mvp.BasePresenter;
import com.dzbook.mvp.UI.RankTopUI;
import com.dzbook.net.hw.HwRequestLib;
import com.dzbook.utils.NetworkUtils;
import com.ishugui.R;

import hw.sdk.net.bean.BeanRankTopResBeanInfo;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

/**
 * 排行榜
 *
 * @author lizhongzhong 2018/3/8.
 */
public class RankTopPresenter extends BasePresenter {

    private CompositeDisposable composite = new CompositeDisposable();

    private RankTopUI mUI;

    private Integer page = 1;

    private Integer pageSize = 20;

    /**
     * 存储点击排行加载失败的的数据
     */
    private String parentId, subId;

    /**
     * 构造函数
     *
     * @param ui ui
     */
    public RankTopPresenter(RankTopUI ui) {
        this.mUI = ui;
    }

    /**
     * 获取首页排行榜信息
     *
     * @param isPullDownRefresh isPullDownRefresh
     */
    public void getFirstPageRankTopInfo(boolean isPullDownRefresh) {
        page = 1;
        getRankTopInfo("", "", true, false, false, isPullDownRefresh);
    }

    /**
     * 获取排行信息
     *
     * @param parentId1 parentId
     * @param subId1    subId
     */
    public void getClickRankTopInfo(String parentId1, String subId1) {
        page = 1;
        getRankTopInfo(parentId1, subId1, false, false, true, false);
    }

    /**
     * 加载更多
     *
     * @param parentId1 parentId
     * @param subId1    subId
     */
    public void getClickRankTopLoadMoreInfo(String parentId1, String subId1) {
        page++;
        getRankTopInfo(parentId1, subId1, false, true, false, false);
    }

    /**
     * 获取排行信息
     *
     * @param parentId1         parentId
     * @param subId1            subId
     * @param isFirstLoad       isFirstLoad
     * @param isLoadMore        isLoadMore
     * @param isClickRank       isClickRank
     * @param isPullDownRefresh isPullDownRefresh
     */
    public void getRankTopInfo(final String parentId1, final String subId1, final boolean isFirstLoad, final boolean isLoadMore, final boolean isClickRank, final boolean isPullDownRefresh) {

        RankTopPresenter.this.parentId = parentId1;
        RankTopPresenter.this.subId = subId1;

        if (!NetworkUtils.getInstance().checkNet()) {
            if (!isLoadMore) {
                mUI.setLoadFail(isFirstLoad);
            }
            mUI.showNoNetView();
            return;
        }

        Disposable disposable = Observable.create(new ObservableOnSubscribe<BeanRankTopResBeanInfo>() {

            @Override
            public void subscribe(ObservableEmitter<BeanRankTopResBeanInfo> e) {

                try {
                    BeanRankTopResBeanInfo bookStoreRankTopData = HwRequestLib.getInstance().getBookStoreRankTopData(parentId, subId, page, pageSize);

                    e.onNext(bookStoreRankTopData);
                    e.onComplete();
                } catch (Exception ex) {
                    ALog.printStackTrace(ex);
                    e.onError(ex);
                }

            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribeWith(new DisposableObserver<BeanRankTopResBeanInfo>() {

            @Override
            public void onNext(BeanRankTopResBeanInfo value) {
                mUI.dismissProgress();
                if (value != null && value.isSuccess()) {

                    if (isFirstLoad) {
                        if (value.rankTopResBean != null && value.rankTopResBean.size() > 0) {
                            mUI.setFirstLoadRankTopInfo(value);
                        } else {
                            mUI.setLoadFail(true);
                        }

                    } else if (isClickRank) {

                        mUI.setClickRankTopInfo(value.rankBooks);

                    } else if (isLoadMore) {
                        mUI.setLoadMoreRankTopInfo(value);
                    }

                } else {
                    if (!isLoadMore) {
                        mUI.setLoadFail(isFirstLoad);
                    } else {
                        mUI.showMessage(R.string.load_data_failed);
                    }
                    page--;
                }
                mUI.setPullRefreshComplete();
            }

            @Override
            public void onError(Throwable e) {
                mUI.dismissProgress();
                if (!isLoadMore) {
                    mUI.setLoadFail(isFirstLoad);
                } else {
                    mUI.showMessage(R.string.load_data_failed);
                }
                page--;
                mUI.setPullRefreshComplete();
            }

            @Override
            public void onComplete() {
                mUI.dismissProgress();
            }

            @Override
            protected void onStart() {
                if (!isPullDownRefresh && !isLoadMore) {
                    mUI.showLoadProgresss();
                }
            }
        });

        composite.addAndDisposeOldByKey("getRankTopInfo", disposable);
    }

    public String getLoadFailParentId() {
        return parentId;
    }

    public String getLoadFailSubId() {
        return subId;
    }

    public void setLoadParentId(String parentId1) {
        this.parentId = parentId1;
    }

    public void setLoadSubId(String subId1) {
        this.subId = subId1;
    }

    /**
     * destroy
     */
    public void destroy() {
        composite.disposeAll();
    }

    /**
     * 移除头布局
     */
    public void removeRecycleViewHeader() {
        mUI.removeRecycleViewHeader();
    }


}
