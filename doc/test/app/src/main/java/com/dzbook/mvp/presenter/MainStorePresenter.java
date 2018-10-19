package com.dzbook.mvp.presenter;

import android.app.Activity;
import android.text.TextUtils;

import com.dzbook.AppConst;
import com.dzbook.AppContext;
import com.dzbook.database.bean.HttpCacheInfo;
import com.dzbook.log.DzLog;
import com.dzbook.log.LogConstants;
import com.dzbook.mvp.UI.MainStoreUI;
import com.dzbook.net.hw.HwRequestLib;
import com.dzbook.net.hw.RequestCall;
import com.dzbook.utils.DBUtils;
import com.dzbook.utils.ListUtils;
import com.dzbook.utils.NetworkUtils;
import com.dzbook.utils.SpUtil;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import hw.sdk.net.bean.shelf.BeanBookUpdateInfo;
import hw.sdk.net.bean.store.BeanSubTempletInfo;
import hw.sdk.net.bean.store.BeanTempletsInfo;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * MainStorePresenter
 *
 * @author Winzows on 2017/8/3.
 */

public class MainStorePresenter {

    private MainStoreUI mUI;

    /**
     * 构造
     *
     * @param mainStoreUI mainStoreUI
     */
    public MainStorePresenter(MainStoreUI mainStoreUI) {
        mUI = mainStoreUI;
    }

    private boolean isActivityEmpty() {
        Activity activity = mUI.getActivity();
        return activity == null;
    }

    /**
     * 获取数据
     *
     * @param channelId channelId
     * @param readPref  readPref
     */
    public void getDataFromNet(final String channelId, final int readPref) {
        if (AppContext.getBeanTempletsInfo() != null) {
            loadStoreDataFromNet(AppContext.getBeanTempletsInfo());
            return;
        }
        if (NetworkUtils.getInstance().checkNet()) {
            Observable.create(new ObservableOnSubscribe<BeanTempletsInfo>() {
                @Override
                public void subscribe(ObservableEmitter<BeanTempletsInfo> e) {
                    try {
                        if (!isActivityEmpty()) {
                            BeanTempletsInfo beanTempletsInfo = HwRequestLib.getInstance().getStorePageDataFromNet(mUI.getActivity(), channelId, readPref + "", "");
                            e.onNext(beanTempletsInfo);
                            operSpecialChannel(beanTempletsInfo);
                        }
                    } catch (Exception ex) {
                        e.onError(ex);
                    }
                }
            }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<BeanTempletsInfo>() {
                @Override
                public void onSubscribe(Disposable d) {

                }

                @Override
                public void onNext(BeanTempletsInfo value) {
                    loadStoreDataFromNet(value);
                }

                @Override
                public void onError(Throwable e) {
                    mUI.hideLoading();
                    loadStoreDataFromCanche();
                    HashMap<String, String> map = new HashMap<>();
                    map.put("is_success", "2");
                    map.put("has_data", "2");
                    DzLog.getInstance().logEvent(LogConstants.EVENT_258DTXFJG, map, "");
                }

                @Override
                public void onComplete() {

                }
            });
        } else {
            loadStoreDataFromCanche();
        }

    }

    /**
     * 操作特殊的频道：vip：限免存储特殊数据
     *
     * @param beanTempletsInfo
     */
    private void operSpecialChannel(BeanTempletsInfo beanTempletsInfo) {
        if (beanTempletsInfo == null || !beanTempletsInfo.isContainChannel()) {
            return;
        }
        ArrayList<BeanSubTempletInfo> list = beanTempletsInfo.getValidChannels();
        for (BeanSubTempletInfo sub : list) {
            if (sub != null) {
                SpUtil spUtil = SpUtil.getinstance(mUI.getActivity());
                if ("5".equals(sub.type)) {
                    spUtil.setString(SpUtil.BOOK_STORE_VIP_ID, sub.id);
                    spUtil.setString(SpUtil.BOOK_STORE_VIP_TYPE, sub.type);
                }
                if ("6".equals(sub.type)) {
                    spUtil.setString(SpUtil.BOOK_STORE_LIMITFREE_ID, sub.id);
                    spUtil.setString(SpUtil.BOOK_STORE_LIMITFREE_TYPE, sub.type);
                }
            }
        }
    }

    /**
     * 加载来自网络的书城数据
     *
     * @param value
     */
    private void loadStoreDataFromNet(BeanTempletsInfo value) {
        if (value != null && value.isSuccess() && value.isContainChannel()) {
            mUI.setChannelDatas(value);
            HashMap<String, String> map = new HashMap<>();
            map.put("is_success", "1");
            map.put("has_data", "1");
            DzLog.getInstance().logEvent(LogConstants.EVENT_258DTXFJG, map, "");
        } else {
            mUI.hideLoading();
            loadStoreDataFromCanche();
            HashMap<String, String> map = new HashMap<>();
            map.put("is_success", "1");
            map.put("has_data", "2");
            DzLog.getInstance().logEvent(LogConstants.EVENT_258DTXFJG, map, "");
        }
        delayGetActivityDataFromNet();
    }

    /**
     * 加载来自缓存的数据
     */
    private void loadStoreDataFromCanche() {
        Observable.create(new ObservableOnSubscribe<BeanTempletsInfo>() {
            @Override
            public void subscribe(ObservableEmitter<BeanTempletsInfo> e) {
                try {
                    if (!isActivityEmpty()) {
                        HttpCacheInfo cacheInfo = DBUtils.findHttpCacheInfo(mUI.getActivity(), RequestCall.STORE_DATA_URL);
                        BeanTempletsInfo result = null;
                        if (cacheInfo != null && !TextUtils.isEmpty(cacheInfo.response)) {
                            if (!TextUtils.isEmpty(cacheInfo.response)) {
                                result = new BeanTempletsInfo();
                                JSONObject jsonObj = new JSONObject(cacheInfo.response);
                                result.parseJSON(jsonObj);
                            }
                        }
                        e.onNext(result);
                    }
                } catch (Exception ex) {
                    e.onError(ex);
                }
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<BeanTempletsInfo>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(BeanTempletsInfo value) {
                if (value != null && value.isSuccess()) {
                    if (value.isContainChannel()) {
                        mUI.setChannelDatas(value);
                    }
                    if (!ListUtils.isEmpty(value.whiteUrlList)) {
                        AppContext.setWhiteUrlList(value.whiteUrlList);
                    }
                } else {
                    if (NetworkUtils.getInstance().checkNet()) {
                        mUI.showEmptyView();
                    } else {
                        mUI.showNoNetView();
                    }
                }
            }

            @Override
            public void onError(Throwable e) {
                if (NetworkUtils.getInstance().checkNet()) {
                    mUI.showEmptyView();
                } else {
                    mUI.showNoNetView();
                }
            }

            @Override
            public void onComplete() {

            }
        });

    }

    /**
     * 延时请求广告的数据，并在书城展示
     */
    private void delayGetActivityDataFromNet() {
        Observable.timer(2, TimeUnit.SECONDS).subscribe(new Consumer<Long>() {
            @Override
            public void accept(Long aLong) throws Exception {
                Observable.create(new ObservableOnSubscribe<BeanBookUpdateInfo>() {
                    @Override
                    public void subscribe(ObservableEmitter<BeanBookUpdateInfo> e) throws Exception {
                        try {
                            if (!isActivityEmpty()) {
                                int pref = SpUtil.getinstance(AppConst.getApp()).getPersonReadPref();
                                BeanBookUpdateInfo bookUpdateInfo = HwRequestLib.getInstance().shelfBookUpdate(pref + "", "f1", null);
                                e.onNext(bookUpdateInfo);
                            }
                        } catch (Exception ex) {
                            e.onError(ex);
                        }
                    }
                }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<BeanBookUpdateInfo>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(BeanBookUpdateInfo value) {
                        if (value.isSuccess()) {
                            if (!TextUtils.isEmpty(value.city)) {
                                SpUtil.getinstance(AppConst.getApp()).setClientCity(value.city);
                            }
                            if (!TextUtils.isEmpty(value.prov)) {
                                SpUtil.getinstance(AppConst.getApp()).setClientProvince(value.prov);
                            }
                            if (!TextUtils.isEmpty(value.rdShareUrl)) {
                                SpUtil.getinstance(AppConst.getApp()).setString(SpUtil.SP_READER_SHAREURL, value.rdShareUrl);
                            }
                            if (!TextUtils.isEmpty(value.downloadUrl)) {
                                SpUtil.getinstance(AppConst.getApp()).setString(SpUtil.SP_READER_DOWNLOADURL, value.downloadUrl);
                            }

                            if (value.isContainActivity()) {
                                mUI.setNotiAndActivityData(value.activity);
                            }
                        } else {
                            if (!isActivityEmpty()) {
                                mUI.showMessage(value.getRetMsg());
                            }
                        }
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
            }
        });
    }

    /**
     * 频道切换日志
     *
     * @param subTempletInfo subTempletInfo
     */
    public void logChannel(BeanSubTempletInfo subTempletInfo) {
        HashMap<String, String> map = new HashMap<>();
        map.put("channelid", subTempletInfo.id);
        DzLog.getInstance().logClick(LogConstants.MODULE_NSC, LogConstants.ZONE_NSC_PD0, subTempletInfo.title, map, "");
    }

}
