package com.dzbook.mvp.presenter;

import android.app.Activity;

import com.dzbook.log.DzLog;
import com.dzbook.log.LogConstants;
import com.dzbook.mvp.BasePresenter;
import com.dzbook.mvp.UI.LimitFreeTwoLevelUI;
import com.dzbook.net.hw.HwRequestLib;

import java.util.HashMap;

import hw.sdk.net.bean.store.BeanSubTempletInfo;
import hw.sdk.net.bean.store.BeanTempletsInfo;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * LimitFreeTwoLevelPresenter
 *
 * @author dongdianzhou on 2018/1/15.
 */

public class LimitFreeTwoLevelPresenter extends BasePresenter {

    private LimitFreeTwoLevelUI mUI;

    /**
     * 构造
     *
     * @param limitFreeTwoLevelUI limitFreeTwoLevelUI
     */
    public LimitFreeTwoLevelPresenter(LimitFreeTwoLevelUI limitFreeTwoLevelUI) {
        mUI = limitFreeTwoLevelUI;
    }

    private boolean isActivityEmpty() {
        Activity activity = mUI.getActivity();
        return activity == null;
    }

    /**
     * 获取数据
     *
     * @param id        id
     * @param channelId channel_id
     * @param readPref  readPref
     */
    public void getDataFromNet(final String id, final String channelId, final int readPref) {
        Observable.create(new ObservableOnSubscribe<BeanTempletsInfo>() {
            @Override
            public void subscribe(ObservableEmitter<BeanTempletsInfo> e) {
                try {
                    if (!isActivityEmpty()) {
                        BeanTempletsInfo beanTempletsInfo = HwRequestLib.getInstance().getStoreTwoPageDataFromNet(id, channelId, readPref + "");
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
                handleOnNext(value);
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

    private void handleOnNext(BeanTempletsInfo value) {
        if (value != null && value.isSuccess() && value.isContainTemplet()) {
            mUI.setChannelDatas(value);
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


    /**
     * 频道打点
     *
     * @param subTempletInfo subTempletInfo
     */
    public void logChannel(BeanSubTempletInfo subTempletInfo) {
        HashMap<String, String> map = new HashMap<>();
        map.put("channelid", subTempletInfo.id);
        DzLog.getInstance().logClick(LogConstants.MODULE_NSC, LogConstants.ZONE_NSC_PD1, subTempletInfo.title, map, "");
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
