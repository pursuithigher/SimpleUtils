package com.dzbook.mvp.presenter;

import com.dzbook.mvp.BasePresenter;
import com.dzbook.mvp.UI.AutoOrderVipListUI;
import com.dzbook.net.hw.HwRequestLib;
import com.ishugui.R;

import hw.sdk.net.bean.vip.VipContinueOpenHisBeanInfo;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

/**
 * 自动续订 Presenter
 *
 * @author KongXP on 2018/4/29.
 */

public class AutoOrderVipListPresenter extends BasePresenter {

    private AutoOrderVipListUI mUI;

    /**
     * 页码
     */
    private int page = 1;

    /**
     * 构造
     *
     * @param ui ui实例
     */
    public AutoOrderVipListPresenter(AutoOrderVipListUI ui) {
        mUI = ui;
    }

    /**
     * 获取记录数据
     *
     * @param isReference isReference
     * @param isFirstLoad isFirstLoad
     */
    public void getVipStateDataFromNet(final boolean isReference, final boolean isFirstLoad) {

        Disposable disposable = Observable.create(new ObservableOnSubscribe<VipContinueOpenHisBeanInfo>() {

            @Override
            public void subscribe(ObservableEmitter<VipContinueOpenHisBeanInfo> e) throws Exception {

                VipContinueOpenHisBeanInfo bean = null;
                try {
                    bean = HwRequestLib.getInstance().getVipContinueOpenHisInfo(String.valueOf(page));
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
                e.onNext(bean);
                e.onComplete();
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribeWith(new DisposableObserver<VipContinueOpenHisBeanInfo>() {

            @Override
            public void onNext(VipContinueOpenHisBeanInfo result) {
                mUI.dismissLoadProgress();
                if (result != null && result.isSuccess()) {
                    if (result.isExsitData()) {
                        mUI.setHasMore(true);
                        mUI.setVipList(result.vipContinueOpenHisBeans, isReference);
                    } else {
                        mUI.setHasMore(false);
                        if (isReference) {
                            mUI.showEmptyView();
                        } else {
                            mUI.showMessage(R.string.no_more_data);
                        }
                    }
                } else {
                    if (isReference) {
                        mUI.showNoNetView();
                    } else {
                        mUI.setHasMore(true);
                        mUI.showMessage(R.string.request_data_failed);
                    }
                }
                mUI.stopLoadMore();
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {

            }

            @Override
            protected void onStart() {
                if (isFirstLoad) {
                    mUI.showLoadProgress();
                }
            }
        });

        composite.addAndDisposeOldByKey("getVipStateDataFromNet", disposable);
    }

    /**
     * 重置index
     *
     * @param isAdd 是否自加
     */
    public void resetIndex(boolean isAdd) {
        if (isAdd) {
            page += 1;
            return;
        }
        page = 1;
    }

    /**
     * 析构
     */
    public void destroy() {
        composite.disposeAll();
    }
}
