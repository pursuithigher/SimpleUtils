package com.dzbook.mvp.presenter;

import com.dzbook.mvp.BasePresenter;
import com.dzbook.mvp.UI.PersonVouchersListUI;
import com.dzbook.net.hw.HwRequestLib;
import com.ishugui.R;

import hw.sdk.net.bean.vouchers.VouchersListBeanInfo;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

/**
 * PersonVouchersListPresenter
 *
 * @author KongXP on 2018/4/25.
 */

public class PersonVouchersListPresenter extends BasePresenter {

    private PersonVouchersListUI mUI;

    /**
     * 页码
     */
    private int page = 1;

    /**
     * 构造
     *
     * @param personRechargeRecordUI personRechargeRecordUI
     */
    public PersonVouchersListPresenter(PersonVouchersListUI personRechargeRecordUI) {
        mUI = personRechargeRecordUI;
    }

    /**
     * 获取记录数据
     *
     * @param isFirstLoad 石首市首次加载
     * @param refresh     是否刷新
     */
    public void getVouchersListDataFromNet(final boolean refresh, final boolean isFirstLoad) {

        Disposable disposable = Observable.create(new ObservableOnSubscribe<VouchersListBeanInfo>() {

            @Override
            public void subscribe(ObservableEmitter<VouchersListBeanInfo> e) throws Exception {

                VouchersListBeanInfo bean = null;
                try {
                    bean = HwRequestLib.getInstance().getVouchersListInfo(String.valueOf(page));
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
                e.onNext(bean);
                e.onComplete();
            }

        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribeWith(new DisposableObserver<VouchersListBeanInfo>() {
            @Override
            public void onNext(VouchersListBeanInfo result) {
                handleOnNext(result, refresh);
            }

            @Override
            public void onError(Throwable e) {
                mUI.showNoNetView();
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

        composite.addAndDisposeOldByKey("getVouchersListDataFromNet", disposable);
    }

    private void handleOnNext(VouchersListBeanInfo result, boolean refresh) {
        mUI.dismissLoadProgress();
        if (result != null && result.isSuccess()) {
            if (result.isExistData()) {
                mUI.setHasMore(true);
                mUI.setRecordList(result.vouchersListBeans, refresh);
            } else {
                mUI.setHasMore(false);
                if (refresh) {
                    mUI.showEmptyView();
                } else {
                    //                            mUI.showMessage(R.string.no_more_data);
                    mUI.showAllTips();

                }
            }
        } else {
            if (refresh) {
                mUI.showNoNetView();
            } else {
                mUI.setHasMore(true);
                mUI.showMessage(R.string.request_data_failed);
            }
        }
        mUI.stopLoadMore();
    }

    /**
     * 重置索引
     *
     * @param isAdd isAdd
     */
    public void resetIndex(boolean isAdd) {
        if (isAdd) {
            page += 1;
            return;
        }
        page = 1;
    }


    /**
     * destroy
     */
    public void destroy() {
        composite.disposeAll();
    }
}
