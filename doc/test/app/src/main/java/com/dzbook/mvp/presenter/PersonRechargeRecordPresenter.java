package com.dzbook.mvp.presenter;

import com.dzbook.mvp.BasePresenter;
import com.dzbook.mvp.UI.PersonRechargeRecordUI;
import com.dzbook.net.hw.HwRequestLib;
import com.ishugui.R;

import hw.sdk.net.bean.record.RechargeRecordBeanInfo;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

/**
 * PersonRechargeRecordPresenter
 *
 * @author dongdianzhou on 2017/11/21.
 */

public class PersonRechargeRecordPresenter extends BasePresenter {

    private PersonRechargeRecordUI mUI;

    /**
     * 页码
     */
    private int page = 1;
    /**
     * 每页长度
     */
    private String num = "20";

    /**
     * 构造
     *
     * @param personRechargeRecordUI personRechargeRecordUI
     */
    public PersonRechargeRecordPresenter(PersonRechargeRecordUI personRechargeRecordUI) {
        mUI = personRechargeRecordUI;
    }

    /**
     * 获取记录数据
     *
     * @param isFirstLoad isFirstLoad
     * @param isReference isReference
     */
    public void getRechargeRecordDataFromNet(final boolean isReference, final boolean isFirstLoad) {

        Disposable disposable = Observable.create(new ObservableOnSubscribe<RechargeRecordBeanInfo>() {

            @Override
            public void subscribe(ObservableEmitter<RechargeRecordBeanInfo> e) throws Exception {

                RechargeRecordBeanInfo bean = null;
                try {
                    bean = HwRequestLib.getInstance().getRechargeRecordInfo(String.valueOf(page), num);
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
                e.onNext(bean);
                e.onComplete();

            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribeWith(new DisposableObserver<RechargeRecordBeanInfo>() {

            @Override
            public void onNext(RechargeRecordBeanInfo result) {
                mUI.dismissLoadProgress();

                if (result != null && result.isSuccess()) {
                    if (result.isExistData()) {
                        mUI.setHasMore(true);
                        mUI.setRecordList(result.recordBeans, isReference);
                    } else {
                        if (isReference) {
                            mUI.showEmptyView();
                        } else {
                            //                            mUI.showMessage(R.string.no_more_data);
                            mUI.showAllTips();
                        }
                        mUI.setHasMore(false);
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

    /**
     * destroy
     */
    public void destroy() {
        composite.disposeAll();
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

}
