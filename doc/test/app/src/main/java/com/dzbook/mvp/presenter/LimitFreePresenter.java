package com.dzbook.mvp.presenter;

import android.text.TextUtils;

import com.dzbook.mvp.BasePresenter;
import com.dzbook.mvp.UI.LimitFreeUI;
import com.dzbook.net.hw.HwRequestLib;
import com.dzbook.utils.SpUtil;

import hw.sdk.net.bean.store.BeanTempletsInfo;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * 限免
 */
public class LimitFreePresenter extends BasePresenter {

    private LimitFreeUI limitFreeUI;

    /**
     * 构造
     *
     * @param limitFreeUI limitFreeUI
     */
    public LimitFreePresenter(LimitFreeUI limitFreeUI) {
        this.limitFreeUI = limitFreeUI;
    }

    /**
     * destroy
     */
    public void destroy() {
        if (composite != null) {
            composite.disposeAll();
        }
    }

    private boolean isActivityEmpty() {
        return limitFreeUI.getActivity() == null;
    }

    /**
     * 获取数据
     */
    public void getLimitFreeDataFromNet() {
        Observable.create(new ObservableOnSubscribe<BeanTempletsInfo>() {

            @Override
            public void subscribe(ObservableEmitter<BeanTempletsInfo> e) {
                try {
                    if (!isActivityEmpty()) {
                        SpUtil spUtil = SpUtil.getinstance(limitFreeUI.getActivity());
                        String limitFreeId = spUtil.getString(SpUtil.BOOK_STORE_LIMITFREE_ID);
                        String limitFreeType = spUtil.getString(SpUtil.BOOK_STORE_LIMITFREE_TYPE);
                        if (TextUtils.isEmpty(limitFreeType)) {
                            limitFreeType = "6";
                        }
                        int readPref = spUtil.getPersonReadPref();
                        BeanTempletsInfo result = HwRequestLib.getInstance().getStorePageDataFromNet(limitFreeUI.getActivity(), limitFreeId, readPref + "", limitFreeType);
                        e.onNext(result);
                    }
                } catch (Exception ex) {
                    e.onError(ex);
                }
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<BeanTempletsInfo>() {
            @Override
            public void onSubscribe(Disposable d) {
                limitFreeUI.showLoadding();
                if (!d.isDisposed()) {
                    composite.addAndDisposeOldByKey("requestLimitFreeData", d);
                }
            }

            @Override
            public void onNext(BeanTempletsInfo value) {
                if (value != null && value.isSuccess() && value.isContainTemplet()) {
                    limitFreeUI.setTempletDatas(value.getSection());
                } else {
                    limitFreeUI.showEmptyView();
                    limitFreeUI.hideLoading();
                }
            }


            @Override
            public void onError(Throwable e) {
                limitFreeUI.hideLoading();
                limitFreeUI.showNoNetView();
            }

            @Override
            public void onComplete() {

            }
        });
    }
}
