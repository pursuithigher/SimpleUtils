package com.dzbook.mvp.presenter;

import android.app.Activity;

import com.dzbook.activity.account.ConsumeBookSumAdapter;
import com.dzbook.activity.account.ConsumeSecondActivity;
import com.dzbook.lib.utils.ALog;
import com.dzbook.mvp.BasePresenter;
import com.dzbook.mvp.UI.ConsumeBookSumUI;
import com.dzbook.net.hw.HwRequestLib;
import com.dzbook.utils.hw.LoginCheckUtils;
import com.dzbook.utils.hw.LoginUtils;
import com.ishugui.R;

import java.util.ArrayList;
import java.util.List;

import hw.sdk.net.bean.consume.ConsumeBookSumBeanInfo;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

/**
 * ConsumeBookSumPresenter
 *
 * @author lizz 2018/4/20.
 **/
public class ConsumeBookSumPresenter extends BasePresenter {

    private ConsumeBookSumUI mUI;
    private int index = 1;
    private String totalNum = "20";
    private LoginCheckUtils loginCheckUtils = null;

    /**
     * 构造
     *
     * @param ui ui
     */
    public ConsumeBookSumPresenter(ConsumeBookSumUI ui) {
        this.mUI = ui;
    }

    /**
     * 加载更多
     */
    public void loadMoreNetConsumeBookData() {
        index = index + 1;
        getNetConsumeBookData(false);
    }

    /**
     * 加载数据
     *
     * @param isFirstLoad isFirstLoad
     */
    public void getNetConsumeBookData(final boolean isFirstLoad) {
        getNetConsumeBookData(isFirstLoad, false);
    }

    /**
     * 加载数据
     *
     * @param isFirstLoad         isFirstLoad
     * @param isTokenInvalidRetry isTokenInvalidRetry
     */
    public void getNetConsumeBookData(final boolean isFirstLoad, final boolean isTokenInvalidRetry) {
        if (isFirstLoad) {
            index = 1;
        }

        Disposable disposable = Observable.create(new ObservableOnSubscribe<ConsumeBookSumBeanInfo>() {

            @Override
            public void subscribe(ObservableEmitter<ConsumeBookSumBeanInfo> e) throws Exception {
                ConsumeBookSumBeanInfo sumBeanInfo = null;
                try {
                    sumBeanInfo = HwRequestLib.getInstance().getBookConsumeSummaryInfo(index + "", totalNum);
                } catch (Exception e1) {
                    ALog.printStackTrace(e1);
                }
                e.onNext(sumBeanInfo);
                e.onComplete();

            }
        }).observeOn(AndroidSchedulers.mainThread()).subscribeOn(Schedulers.io()).subscribeWith(new DisposableObserver<ConsumeBookSumBeanInfo>() {

            @Override
            public void onNext(ConsumeBookSumBeanInfo beanInfo) {
                if (isFirstLoad) {
                    mUI.dismissLoadProgress();
                }
                if (beanInfo != null && beanInfo.isSuccess()) {
                    if (beanInfo.isExistData()) {
                        mUI.setBookConsumeSum(beanInfo.consumeSumBeans, isFirstLoad);
                    } else {
                        if (isFirstLoad) {
                            mUI.showNoDataView();
                        } else {
                            mUI.setHasMore(false);
                            //                            mUI.showMessage(R.string.no_more_data);
                            mUI.showAllTips();
                        }
                    }
                } else {

                    if (!isTokenInvalidRetry && beanInfo != null && beanInfo.isTokenExpireOrNeedLogin()) {
                        tokenInvalidRetry(isFirstLoad);
                        return;
                    }

                    if (isFirstLoad) {
                        mUI.showNoNetView();
                    } else {
                        mUI.setHasMore(true);
                        mUI.showMessage(R.string.request_data_failed);
                    }
                }
            }

            @Override
            public void onError(Throwable e) {
                if (isFirstLoad) {
                    mUI.showNoNetView();
                    mUI.dismissLoadProgress();
                }
            }

            @Override
            public void onComplete() {
                if (!isFirstLoad) {
                    mUI.stopLoadMore();
                }
            }

            @Override
            protected void onStart() {
                if (isFirstLoad) {

                    mUI.showLoadProgress();
                }
            }
        });

        composite.addAndDisposeOldByKey("getNetConsumeBookData", disposable);

    }

    /**
     * 跳转ConsumeSecondActivity
     *
     * @param type   type
     * @param nextId nextId
     */
    public void launchSecondConsume(String type, String nextId) {
        ConsumeSecondActivity.launch(mUI.getActivity(), nextId, type);
    }


    /**
     * 获取其他消费数据
     *
     * @return list
     */
    public List<ConsumeBookSumAdapter.OtherConsumeBean> getOtherConsumeData() {
        List<ConsumeBookSumAdapter.OtherConsumeBean> beans = new ArrayList<>();
        beans.add(new ConsumeBookSumAdapter.OtherConsumeBean(R.drawable.ic_activity_public, "活动", "2"));
        beans.add(new ConsumeBookSumAdapter.OtherConsumeBean(R.drawable.ic_vip_public, "VIP", "3"));
        return beans;
    }


    /**
     * destroy
     */
    public void destroy() {
        composite.disposeAll();
        if (loginCheckUtils != null) {
            loginCheckUtils.resetAgainObtainListener();
            loginCheckUtils.disHuaWeiConnect();
        }
    }

    /**
     * 只重试一次
     */
    private void tokenInvalidRetry(final boolean isFirstLoad) {
        mUI.showLoadProgress();
        if (loginCheckUtils == null) {
            loginCheckUtils = LoginCheckUtils.getInstance();
        }
        loginCheckUtils.againObtainAppToken((Activity) mUI.getContext(), new LoginUtils.LoginCheckListenerSub() {
            @Override
            public void loginFail() {
                mUI.dismissLoadProgress();
                if (isFirstLoad) {
                    mUI.showNoNetView();
                } else {
                    mUI.setHasMore(true);
                    mUI.showMessage(R.string.request_data_failed);
                }
            }

            @Override
            public void loginComplete() {
                getNetConsumeBookData(isFirstLoad, true);
            }
        });
    }
}
