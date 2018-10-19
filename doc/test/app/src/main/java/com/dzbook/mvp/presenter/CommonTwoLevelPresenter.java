package com.dzbook.mvp.presenter;

import android.app.Activity;

import com.dzbook.log.DzLog;
import com.dzbook.log.LogConstants;
import com.dzbook.mvp.BasePresenter;
import com.dzbook.mvp.UI.CommonTwoLevelUI;
import com.dzbook.net.hw.HwRequestLib;

import java.util.HashMap;

import hw.sdk.net.bean.store.BeanTempletsInfo;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * CommonTwoLevelPresenter
 *
 * @author dongdianzhou on 2018/1/15.
 */

public class CommonTwoLevelPresenter extends BasePresenter {

    private CommonTwoLevelUI mUI;

    /**
     * 构造
     *
     * @param commonTwoLevelUI commonTwoLevelUI
     */
    public CommonTwoLevelPresenter(CommonTwoLevelUI commonTwoLevelUI) {
        mUI = commonTwoLevelUI;
    }

    private boolean isActivityEmpty() {
        Activity activity = mUI.getActivity();
        return activity == null;
    }

    /**
     * 获取数据
     *
     * @param id       id
     * @param readPref readPref
     */
    public void getDataFromNet(final String id, final int readPref) {
        Observable.create(new ObservableOnSubscribe<BeanTempletsInfo>() {
            @Override
            public void subscribe(ObservableEmitter<BeanTempletsInfo> e) {
                try {
                    if (!isActivityEmpty()) {
                        BeanTempletsInfo beanTempletsInfo = HwRequestLib.getInstance().getStoreTwoPageDataFromNet(id, "", readPref + "");
                        e.onNext(beanTempletsInfo);
                    }
                } catch (Exception ex) {
                    e.onError(ex);
                }
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<BeanTempletsInfo>() {
            @Override
            public void onSubscribe(Disposable d) {
                if (!d.isDisposed()) {
                    composite.addAndDisposeOldByKey("requestLimitData", d);
                }
            }

            @Override
            public void onNext(BeanTempletsInfo value) {
                if (value.isSuccess() && value.isContainTemplet()) {
                    mUI.setTempletDatas(value.getSection());
                    HashMap<String, String> map = new HashMap<>();
                    map.put("is_success", "1");
                    map.put("has_data", "1");
                    DzLog.getInstance().logEvent(LogConstants.EVENT_259DTXFJG, map, "");
                } else {
                    mUI.hideLoading();
                    mUI.showEmptyView();
                    HashMap<String, String> map = new HashMap<>();
                    map.put("is_success", "1");
                    map.put("has_data", "2");
                    DzLog.getInstance().logEvent(LogConstants.EVENT_259DTXFJG, map, "");
                }
            }

            @Override
            public void onError(Throwable e) {
                mUI.hideLoading();
                mUI.showNoNetView();
                HashMap<String, String> map = new HashMap<>();
                map.put("is_success", "2");
                map.put("has_data", "2");
                DzLog.getInstance().logEvent(LogConstants.EVENT_259DTXFJG, map, "");
            }

            @Override
            public void onComplete() {

            }
        });
    }

    /**
     * destroy
     */
    public void destroy() {
        if (composite != null) {
            composite.disposeAll();
        }
    }
}
