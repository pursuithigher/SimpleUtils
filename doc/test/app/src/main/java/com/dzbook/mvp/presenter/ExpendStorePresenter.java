package com.dzbook.mvp.presenter;

import com.dzbook.mvp.BasePresenter;
import com.dzbook.mvp.UI.ExpendStoreUI;
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
 * 通用推展页
 *
 * @author gavin
 */
public class ExpendStorePresenter extends BasePresenter {

    private ExpendStoreUI expendStoreUI;

    /**
     * 构造
     *
     * @param mUI ui
     */
    public ExpendStorePresenter(ExpendStoreUI mUI) {
        this.expendStoreUI = mUI;
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
        return expendStoreUI.getActivity() == null;
    }

    /**
     * 获取数据
     *
     * @param type type
     * @param id   id
     */
    public void getDataFromNet(final String type, final String id) {
        Observable.create(new ObservableOnSubscribe<BeanTempletsInfo>() {

            @Override
            public void subscribe(ObservableEmitter<BeanTempletsInfo> e) {
                try {
                    if (!isActivityEmpty()) {
                        SpUtil spUtil = SpUtil.getinstance(expendStoreUI.getActivity());
                        int readPref = spUtil.getPersonReadPref();
                        BeanTempletsInfo result = HwRequestLib.getInstance().getStorePageDataFromNet(expendStoreUI.getActivity(), id, readPref + "", type);
                        e.onNext(result);
                    }
                } catch (Exception ex) {
                    e.onError(ex);
                }
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<BeanTempletsInfo>() {
            @Override
            public void onSubscribe(Disposable d) {
                expendStoreUI.showLoadding();

                if (!d.isDisposed()) {
                    composite.addAndDisposeOldByKey("requestExpendData", d);
                }
            }

            @Override
            public void onNext(BeanTempletsInfo value) {
                handleOnNext(value);
            }


            @Override
            public void onError(Throwable e) {
                expendStoreUI.hideLoading();
                expendStoreUI.showNoNetView();
            }

            @Override
            public void onComplete() {

            }
        });
    }

    private void handleOnNext(BeanTempletsInfo value) {
        if (value != null && value.isSuccess() && value.isContainTemplet()) {
            expendStoreUI.setTempletDatas(value.getSection());
        } else {
            expendStoreUI.showEmptyView();
            expendStoreUI.hideLoading();
        }
    }
}
