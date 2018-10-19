package com.dzbook.mvp.presenter;

import android.text.TextUtils;

import com.dzbook.mvp.BasePresenter;
import com.dzbook.mvp.UI.VipStoreUI;
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
 * VipStorePresenter
 */
public class VipStorePresenter extends BasePresenter {

    private VipStoreUI vipStoreUI;

    /**
     * 构造函数
     *
     * @param vipStoreUI vipStoreUI
     */
    public VipStorePresenter(VipStoreUI vipStoreUI) {
        this.vipStoreUI = vipStoreUI;
    }

    /**
     * 销毁
     */
    public void destroy() {
        if (composite != null) {
            composite.disposeAll();
        }
    }

    private boolean isActivityEmpty() {
        return vipStoreUI.getActivity() == null;
    }

    /**
     * 加载网络数据
     */
    public void getVipDataFromNet() {
        Observable.create(new ObservableOnSubscribe<BeanTempletsInfo>() {

            @Override
            public void subscribe(ObservableEmitter<BeanTempletsInfo> e) {
                try {
                    if (!isActivityEmpty()) {
                        SpUtil spUtil = SpUtil.getinstance(vipStoreUI.getActivity());
                        String vipId = spUtil.getString(SpUtil.BOOK_STORE_VIP_ID);
                        String vipType = spUtil.getString(SpUtil.BOOK_STORE_VIP_TYPE);
                        int readPref = spUtil.getPersonReadPref();
                        if (TextUtils.isEmpty(vipType)) {
                            vipType = "5";
                        }
                        BeanTempletsInfo result = HwRequestLib.getInstance().getStorePageDataFromNet(vipStoreUI.getActivity(), vipId, readPref + "", vipType);
                        e.onNext(result);
                    }
                } catch (Exception ex) {
                    e.onError(ex);
                }
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<BeanTempletsInfo>() {
            @Override
            public void onSubscribe(Disposable d) {
                vipStoreUI.showLoadding();
                if (!d.isDisposed()) {
                    composite.addAndDisposeOldByKey("requestVipData", d);
                }
            }

            @Override
            public void onNext(BeanTempletsInfo value) {
                if (value != null && value.isSuccess() && value.isContainTemplet()) {
                    vipStoreUI.setTempletDatas(value.getSection());
                } else {
                    vipStoreUI.hideLoading();
                    vipStoreUI.showEmptyView();
                }
            }


            @Override
            public void onError(Throwable e) {
                vipStoreUI.hideLoading();
                vipStoreUI.showNoNetView();
            }

            @Override
            public void onComplete() {

            }
        });
    }
}
