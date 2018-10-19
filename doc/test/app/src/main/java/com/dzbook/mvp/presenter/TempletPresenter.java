package com.dzbook.mvp.presenter;

import android.content.Intent;
import android.os.SystemClock;
import android.text.TextUtils;

import com.dzbook.AppConst;
import com.dzbook.activity.CenterDetailActivity;
import com.dzbook.activity.MainTypeActivity;
import com.dzbook.activity.RankTopActivity;
import com.dzbook.activity.detail.BookDetailActivity;
import com.dzbook.activity.store.CommonTwoLevelActivity;
import com.dzbook.activity.store.ExpendStoreCommonActivity;
import com.dzbook.activity.store.LimitFreeActivity;
import com.dzbook.activity.store.LimitFreeTwoLevelActivity;
import com.dzbook.activity.store.VipStoreActivity;
import com.dzbook.database.bean.HttpCacheInfo;
import com.dzbook.fragment.main.MainStoreFragment;
import com.dzbook.log.DzLog;
import com.dzbook.log.LogConstants;
import com.dzbook.model.ModelAction;
import com.dzbook.mvp.UI.TempletUI;
import com.dzbook.net.LoadBookByNet;
import com.dzbook.net.hw.HwRequestLib;
import com.dzbook.net.hw.RequestCall;
import com.dzbook.pay.LoadBookListener;
import com.dzbook.utils.DBUtils;
import com.dzbook.utils.HwLog;
import com.dzbook.utils.NetworkUtils;
import com.dzbook.utils.hw.LoginUtils;
import com.dzbook.web.ActionEngine;
import com.iss.app.BaseActivity;

import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.List;

import hw.sdk.HwSdkAppConstant;
import hw.sdk.net.bean.HwPublicBean;
import hw.sdk.net.bean.store.BeanSubTempletInfo;
import hw.sdk.net.bean.store.BeanTempletActionInfo;
import hw.sdk.net.bean.store.BeanTempletInfo;
import hw.sdk.net.bean.store.BeanTempletsInfo;
import hw.sdk.net.bean.store.TempletContant;
import hw.sdk.net.bean.store.TempletMapping;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * TempletPresenter
 *
 * @author dongdianzhou on 2018/1/12.
 */

public class TempletPresenter {
    /**
     * 点击打点记录
     */

    /**
     * banner位
     */
    public static final int LOG_CLICK_ACTION_BN0 = 0;
    /**
     * 分类
     */
    public static final int LOG_CLICK_ACTION_FL0 = 2;
    /**
     * 限免
     */
    public static final int LOG_CLICK_ACTION_XM0 = 4;
    /**
     * 普通书籍类型
     */
    public static final int LOG_CLICK_ACTION_SJ0 = 5;
    /**
     * 普通书籍类型
     */
    public static final int LOG_CLICK_ACTION_SJ3 = 8;
    /**
     * 单本书籍
     */
    public static final int LOG_CLICK_ACTION_DB0 = 9;
    /**
     * 单本书籍
     */
    public static final int LOG_CLICK_ACTION_DB1 = 10;
    /**
     * 点击更多
     */
    public static final int LOG_CLICK_MORE = 1001;
    /**
     * 点击书籍
     */
    public static final int LOG_CLICK_BOOK = 1003;
    /**
     * 领取书籍
     */
    public static final int LOG_CLICK_GET_BOOK = 1005;
    /**
     * 点击书籍详情
     */
    public static final int LOG_CLICK_BOOK_DETAIL = 1006;
    /**
     * banner和分类
     */
    public static final int LOG_CLICK_NONE = 1001;
    private TempletUI templetUI;

    /**
     * 构造器
     *
     * @param templetUI templetUI
     */
    public TempletPresenter(TempletUI templetUI) {
        this.templetUI = templetUI;
    }

    /**
     * 获取频道数据
     *
     * @param templetId      templetId
     * @param id             id
     * @param personReadPref personReadPref
     * @param pageType       pageType
     * @param channelType    channelType
     */
    public void getSingleChannelData(String templetId, String id, int personReadPref, String pageType, String channelType) {
        if (TextUtils.isEmpty(pageType)) {
            pageType = LogConstants.MODULE_NSC;
        }
        if (NetworkUtils.getInstance().checkNet()) {
            getSingleChannelDataFromNet(templetId, id, personReadPref, pageType, channelType);
        } else {
            getSingleChannelDataFromCanche(id);
        }
    }

    /**
     * 下拉刷新不需要走缓存
     *
     * @param templetId      templetId
     * @param id             id
     * @param personReadPref personReadPref
     * @param pageType       pageType
     */
    public void referenceChannelData(String templetId, String id, int personReadPref, String pageType) {
        if (TextUtils.isEmpty(pageType)) {
            pageType = LogConstants.MODULE_NSC;
        }
        getSingleChannelDataFromNet(templetId, id, personReadPref, pageType, "");
    }

    /**
     * 单个频道读取缓存
     *
     * @param channelId channelId
     */
    public void getSingleChannelDataFromCanche(final String channelId) {
        Observable.create(new ObservableOnSubscribe<BeanTempletsInfo>() {
            @Override
            public void subscribe(ObservableEmitter<BeanTempletsInfo> e) {
                try {
                    if (!isActivityEmpty()) {
                        HttpCacheInfo cacheInfo = DBUtils.findHttpCacheInfo(templetUI.getActivity(), RequestCall.STORE_DATA_URL + channelId);
                        BeanTempletsInfo result = null;
                        if (cacheInfo != null && !TextUtils.isEmpty(cacheInfo.response)) {
                            result = new BeanTempletsInfo();
                            JSONObject jsonObj = new JSONObject(cacheInfo.response);
                            result.parseJSON(jsonObj);
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
                templetUI.setLoadDataState(1);
            }

            @Override
            public void onNext(BeanTempletsInfo value) {
                templetUI.setLoadDataState(0);
                if (value != null && value.isSuccess() && value.isContainTemplet()) {
                    templetUI.setTempletDatas(value.getSection(), true);
                } else {
                    setStatus(value, true);
                }
            }

            @Override
            public void onError(Throwable e) {
                templetUI.setLoadDataState(0);
                setStatus(null, true);
            }

            @Override
            public void onComplete() {

            }
        });
    }

    private boolean isActivityEmpty() {
        return templetUI.getActivity() == null;
    }

    private void getSingleChannelDataFromNet(final String templetId, final String id, final int personReadPref, final String pageType, final String channelType) {
        Observable.create(new ObservableOnSubscribe<BeanTempletsInfo>() {
            @Override
            public void subscribe(ObservableEmitter<BeanTempletsInfo> e) {
                try {
                    if (!isActivityEmpty()) {
                        if (LogConstants.MODULE_NSC.equals(pageType)) {
                            BeanTempletsInfo beanTempletsInfo = HwRequestLib.getInstance().getStorePageDataFromNet(templetUI.getActivity(), id, personReadPref + "", channelType);
                            e.onNext(beanTempletsInfo);
                        } else if (LogConstants.MODULE_NSCXMZYM.equals(pageType) || LogConstants.MODULE_NSCZYM.equals(pageType)) {
                            BeanTempletsInfo beanTempletsInfo = HwRequestLib.getInstance().getStoreTwoPageDataFromNet(templetId, id, personReadPref + "");
                            e.onNext(beanTempletsInfo);
                        }
                    }
                } catch (Exception ex) {
                    e.onError(ex);
                }
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new TempletsInfoObserver(templetUI, pageType, id));
    }

    /**
     * 获取到loading页的信息
     *
     * @param section section
     * @return BeanTempletInfo
     */
    public BeanTempletInfo getLoadingItem(List<BeanTempletInfo> section) {
        if (section != null && section.size() > 0) {
            for (BeanTempletInfo temp : section) {
                if (temp != null && temp.viewType == TempletMapping.VIEW_TYPE_LD0) {
                    return temp;
                }
            }
        }
        return null;
    }

    /**
     * 获取下一页数据
     *
     * @param nextPageUrl nextPageUrl
     */
    public void getNextPageData(final String nextPageUrl) {
        Observable.create(new ObservableOnSubscribe<BeanTempletsInfo>() {
            @Override
            public void subscribe(ObservableEmitter<BeanTempletsInfo> e) {
                try {
                    if (HwSdkAppConstant.isAbKey()) {
                        SystemClock.sleep(1500);
                    }
                    if (!isActivityEmpty()) {
                        BeanTempletsInfo beanTempletsInfo = HwRequestLib.getInstance().getStoreMoreDataFromNet(nextPageUrl);
                        e.onNext(beanTempletsInfo);
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
                    templetUI.setTempletDatas(value.getSection(), false);
                } else {
                    templetUI.setPageState(true);
                    templetUI.hideLoading();
                    setStatus(value, true);
                }
            }

            @Override
            public void onError(Throwable e) {
                templetUI.setPageState(true);
                templetUI.hideLoading();
                setStatus(null, true);
            }

            @Override
            public void onComplete() {

            }
        });
    }

    /**
     * banner及分类action
     *
     * @param subTempletInfo  subTempletInfo
     * @param templetInfo     templetInfo
     * @param templetPosition templetPosition
     * @param logClickAction  logClickAction
     */
    public void setActionClick(BeanSubTempletInfo subTempletInfo, BeanTempletInfo templetInfo, int templetPosition, int logClickAction) {
        switch (subTempletInfo.type) {
            case "5":
                skipToVipStoreActivity();
                break;
            case "4":
                skipToTypeActivity();
                break;
            case "3":
                skipToRankActivity();
                break;
            case "6":
                skipToLimitFreeActivity();
                break;
            case "7":
                skipToCommonStoreActivity(subTempletInfo.action.dataId, subTempletInfo.type, subTempletInfo.title);
                break;
            default:
                actionOper(subTempletInfo.action, subTempletInfo.title);
                break;
        }
        logClick(logClickAction, subTempletInfo, templetPosition);
        boolean isBook = TempletContant.ACTION_TYPE_BOOK.equals(subTempletInfo.action.type);
        logHw(templetInfo, subTempletInfo, templetPosition, logClickAction, TempletPresenter.LOG_CLICK_NONE, isBook);
    }


    /**
     * item的action处理
     *
     * @param actionInfo actionInfo
     * @param title      title
     */
    public void actionOper(BeanTempletActionInfo actionInfo, String title) {
        if (actionInfo != null) {
            if (!TextUtils.isEmpty(actionInfo.type)) {
                if (TempletContant.ACTION_TYPE_URL.equals(actionInfo.type)) {
                    //url
                    Intent intent = new Intent(templetUI.getActivity(), CenterDetailActivity.class);
                    intent.putExtra("url", actionInfo.url);
                    intent.putExtra("paramAppend", true);
                    intent.putExtra("web", ActionEngine.WEB_HDZX);
                    intent.putExtra("p_type", actionInfo.pType);
                    intent.putExtra("need_param", "1");
                    intent.putExtra(LogConstants.KEY_OPERATE_FROM, MainStoreFragment.TAG);
                    intent.putExtra(LogConstants.KEY_PART_FROM, LogConstants.RECHARGE_SOURCE_FROM_VALUE_5);
                    templetUI.getActivity().startActivity(intent);
                    BaseActivity.showActivity(templetUI.getActivity());
                } else if (TempletContant.ACTION_TYPE_TWONATIVE.equals(actionInfo.type)) {
                    //二级页面
                    CommonTwoLevelActivity.launch(templetUI.getActivity(), title, actionInfo.dataId);
                } else if (TempletContant.ACTION_TYPE_BOOK.equals(actionInfo.type)) {
                    //书籍详情
                    BookDetailActivity.launch(templetUI.getActivity(), actionInfo.dataId, "");
                } else if (TempletContant.ACTION_TYPE_READER.equals(actionInfo.type)) {
                    //信息流
                    ModelAction.goReaderSmart(templetUI.getActivity(), AppConst.GO_READER, -1, actionInfo.dataId, null, 0, false);
                }
            }
        }
    }

    /**
     * 跳转书籍详情页
     *
     * @param id    id
     * @param title title
     */
    public void skipToBookDetail(String id, String title) {
        BookDetailActivity.launch(templetUI.getActivity(), id, title);
    }

    /**
     * 跳到限免的具体模块
     *
     * @param title title
     * @param id    id
     * @param tabID tabID
     */
    public void skipToLimitFree(String title, String id, String tabID) {
        LimitFreeTwoLevelActivity.launch(templetUI.getActivity(), title, id, tabID);
    }

    /**
     * 领取书籍
     *
     * @param actionInfo       actionInfo
     * @param bookId           bookId
     * @param loadBookListener loadBookListener
     */
    public void getBookFromNet(final BeanTempletActionInfo actionInfo, final String bookId, final LoadBookListener loadBookListener) {
        if (!isActivityEmpty()) {
            if (!NetworkUtils.getInstance().checkNet()) {
                if (templetUI.getActivity() instanceof BaseActivity) {
                    ((BaseActivity) templetUI.getActivity()).showNotNetDialog();
                }
            } else {
                LoginUtils.getInstance().forceLoginCheck(templetUI.getActivity(), new LoginUtils.LoginCheckListener() {
                    @Override
                    public void loginComplete() {
                        LoadBookByNet.getInstance().addBookToShelf(templetUI.getActivity(), actionInfo.dataId, bookId, LoadBookByNet.GET_BOOK_FROM_NET_STORE, loadBookListener);
                    }
                });
            }
        }
    }

    /**
     * 限免二级页面的限免结束处理
     *
     * @param state state
     */
    public void limitFreeCompelete(String state) {
        templetUI.limitfreeCompelete(state);
    }

    /**
     * 领取书籍成功：修改对应的状态
     *
     * @param subTempletInfo subTempletInfo
     */
    public void getBookSuccess(BeanSubTempletInfo subTempletInfo) {
        templetUI.getBookSuccess(subTempletInfo);
    }

    /**
     * 栏目打点
     *
     * @param action          action
     * @param templetPosition templetPosition
     * @param subTempletInfo  subTempletInfo
     */
    public void logClick(int action, BeanSubTempletInfo subTempletInfo, int templetPosition) {
        String module = templetUI.getLogModule();
        String adid = templetUI.getLogAdid();
        String zone = "";
        HashMap<String, String> map = new HashMap<>();
        switch (action) {
            case LOG_CLICK_ACTION_BN0:
                zone = LogConstants.ZONE_NSC_BN0;
                break;
            case LOG_CLICK_ACTION_FL0:
                zone = LogConstants.ZONE_NSC_FL0;
                break;
            default:
                break;
        }
        map.put("channelid", templetUI.getChannelID());
        map.put("itemid", subTempletInfo.id);
        if (subTempletInfo.action != null) {
            map.put("actiontype", subTempletInfo.action.type);
            switch (subTempletInfo.action.type) {
                case "0":
                case "1":
                case "6":
                    map.put("actionId", subTempletInfo.action.url);
                    break;
                case "2":
                case "3":
                case "4":
                case "5":
                    map.put("actionId", subTempletInfo.action.dataId);
                    break;
                default:
                    break;
            }
        }
        map.put("position", templetPosition + "");
        DzLog.getInstance().logClick(module, zone, adid, map, "");
    }

    /**
     * 限免的书籍打点
     *
     * @param action          action
     * @param subAction       subAction
     * @param templetInfo     templetInfo
     * @param templetPosition templetPosition
     */
    public void logXMBookClick(int action, int subAction, BeanTempletInfo templetInfo, int templetPosition) {
        String module = templetUI.getLogModule();
        String adid = templetUI.getLogAdid();
        String zone = "";
        HashMap<String, String> map = new HashMap<>();
        switch (action) {
            case LOG_CLICK_ACTION_XM0:
                zone = LogConstants.ZONE_NSC_XM0;
                map.put("channelid", templetUI.getChannelID());
                switch (subAction) {
                    case LOG_CLICK_MORE:
                        map.put("actiontype", "1");
                        break;
                    case LOG_CLICK_BOOK:
                        map.put("actiontype", "3");
                        break;
                    default:
                        break;
                }
                map.put("templetid", templetInfo.id);
                map.put("templettitle", templetInfo.title);
                map.put("tabid", templetInfo.tabId);
                map.put("position", templetPosition + "");
                break;
            default:
                break;
        }
        DzLog.getInstance().logClick(module, zone, adid, map, "");
    }

    /**
     * SJ等普通书籍的打点
     *
     * @param action          action
     * @param subAction       subAction
     * @param templetInfo     templetInfo
     * @param bookid          bookid
     * @param templetPosition templetPosition
     */
    public void logCommonBookClick(int action, int subAction, BeanTempletInfo templetInfo, String bookid, int templetPosition) {
        String module = templetUI.getLogModule();
        String adid = templetUI.getLogAdid();
        String zone = "";
        HashMap<String, String> map = new HashMap<>();
        switch (action) {
            case LOG_CLICK_ACTION_SJ0:
                zone = LogConstants.ZONE_NSC_SJ0;
                break;
            case LOG_CLICK_ACTION_SJ3:
                zone = LogConstants.ZONE_NSC_SJ3;
                break;
            default:
                break;
        }
        map.put("channelid", templetUI.getChannelID());
        switch (subAction) {
            case LOG_CLICK_MORE:
                map.put("actiontype", "1");
                break;
            case LOG_CLICK_BOOK:
                map.put("actiontype", "3");
                break;
            default:
                break;
        }
        map.put("templetid", templetInfo.id);
        map.put("templettitle", templetInfo.title);
        map.put("bookid", bookid);
        map.put("position", templetPosition + "");
        DzLog.getInstance().logClick(module, zone, adid, map, "");
    }

    /**
     * 单本书的打点
     *
     * @param action          action
     * @param subAction       subAction
     * @param bookid          bookid
     * @param templetPosition templetPosition
     */
    public void logSingleBookClick(int action, int subAction, String bookid, int templetPosition) {
        String module = templetUI.getLogModule();
        String adid = templetUI.getLogAdid();
        String zone = "";
        HashMap<String, String> map = new HashMap<>();
        switch (action) {
            case LOG_CLICK_ACTION_DB0:
                zone = LogConstants.ZONE_NSC_DB0;
                break;
            case LOG_CLICK_ACTION_DB1:
                zone = LogConstants.ZONE_NSC_DB1;
                break;
            default:
                break;
        }
        map.put("channelid", templetUI.getChannelID());
        switch (subAction) {
            case LOG_CLICK_GET_BOOK:
                map.put("actiontype", "1");
                break;
            case LOG_CLICK_BOOK_DETAIL:
                map.put("actiontype", "2");
                break;
            default:
                break;
        }
        map.put("bookid", bookid);
        map.put("position", templetPosition + "");
        DzLog.getInstance().logClick(module, zone, adid, map, "");
    }

    /**
     * Click日志
     *
     * @param clickType clickType
     */
    public void logVip(int clickType) {
        String module = templetUI.getLogModule();
        String adid = "";
        if (clickType == 0) {
            adid = "myvip";
        } else if (clickType == 1) {
            adid = "flxh";
        } else if (clickType == 2) {
            adid = "flqb";
        }
        DzLog.getInstance().logClick(module, LogConstants.ZONE_NSC_VPT0, adid, null, "");
    }

    /**
     * 华为打点vip
     *
     * @param isVipFl   isVipFl
     * @param clickType ：0：我的vip：1：虚幻，2：分类
     */
    public void logHwVip(boolean isVipFl, int clickType) {
        String channelId = templetUI.getChannelID();
        String channelTitle = templetUI.getLogAdid();
        String channelPosition = templetUI.getChannelPosition();
        String columeID = LogConstants.COLUME_NAME_CLASSIFY;
        String templetName = LogConstants.COLUME_NAME_CLASSIFY;
        String templetType = LogConstants.COLUME_TEMP_CLASSIFY;
        String contentType = isVipFl ? LogConstants.CONTENT_BOOK_LIST : LogConstants.CONTENT_VIP_INFO;
        String contentId = "";
        String contentName = "";
        if (clickType == 0) {
            contentId = LogConstants.COLUME_ID_MYVIP;
            contentName = LogConstants.COLUME_ID_MYVIP;
        } else if (clickType == 1) {
            contentId = LogConstants.COLUME_ID_XH;
            contentName = LogConstants.COLUME_ID_XH;
        } else if (clickType == 2) {
            contentId = LogConstants.COLUME_ID_VIPFL;
            contentName = LogConstants.COLUME_ID_VIPFL;
        }
        HwLog.columnClick(HwLog.getLogLinkedHashMap().get("store"), channelId, channelTitle, channelPosition, columeID, templetName, String.valueOf(0), templetType, contentId, contentName, contentType);
    }

    /**
     * 华为打点
     *
     * @param templetInfo     templetInfo
     * @param subTempletInfo  subTempletInfo
     * @param templetPosition templetPosition
     * @param logAction       logAction
     * @param logType         logType
     * @param isBook          isBook
     */
    public void logHw(BeanTempletInfo templetInfo, BeanSubTempletInfo subTempletInfo, int templetPosition, int logAction, int logType, boolean isBook) {
        String channelId = templetUI.getChannelID();
        String channelTitle = templetUI.getLogAdid();
        String channelPosition = templetUI.getChannelPosition();
        String templetName = "";
        String templetType = "";
        String contentType = "";
        if (logAction == LOG_CLICK_ACTION_BN0) {
            templetInfo.id = LogConstants.COLUME_NAME_BANNER;
            templetName = LogConstants.COLUME_NAME_BANNER;
            templetType = LogConstants.COLUME_TEMP_BANNER;
            contentType = isBook ? LogConstants.CONTENT_TEMP_BOOK : LogConstants.CONTENT_TEMP_ACTIVITY;
        } else if (logAction == LOG_CLICK_ACTION_FL0) {
            templetInfo.id = LogConstants.COLUME_NAME_CLASSIFY;
            templetName = LogConstants.COLUME_NAME_CLASSIFY;
            templetType = LogConstants.COLUME_TEMP_CLASSIFY;
            contentType = isBook ? LogConstants.CONTENT_TEMP_BOOK : LogConstants.CONTENT_TEMP_ACTIVITY;
        } else if (logAction == LOG_CLICK_ACTION_SJ0 || logAction == LOG_CLICK_ACTION_SJ3 || logAction == LOG_CLICK_ACTION_XM0) {
            templetName = templetInfo.title;
            templetType = LogConstants.COLUME_TEMP_BOOK;
            contentType = logType != LOG_CLICK_MORE ? LogConstants.CONTENT_TEMP_BOOK : LogConstants.CONTENT_TEMP_MORE;
        }
        if (subTempletInfo != null) {
            HwLog.columnClick(HwLog.getLogLinkedHashMap().get("store"), channelId, channelTitle, channelPosition, templetInfo.id, templetName, String.valueOf(templetPosition), templetType, subTempletInfo.id, subTempletInfo.title, contentType);
        } else {
            HwLog.columnClick(HwLog.getLogLinkedHashMap().get("store"), channelId, channelTitle, channelPosition, templetInfo.id, templetName, String.valueOf(templetPosition), templetType, "", "", contentType);
        }
    }


    /**
     * 跳转到vip书城
     */
    public void skipToVipStoreActivity() {
        if (!isActivityEmpty()) {
            VipStoreActivity.launch(templetUI.getActivity());
        }
    }

    /**
     * 跳转通用书城拓展页
     *
     * @param title title
     * @param id    id
     * @param type  type
     */
    public void skipToCommonStoreActivity(String id, String type, String title) {
        if (!isActivityEmpty()) {
            ExpendStoreCommonActivity.launch(templetUI.getActivity(), id, type, title);
        }
    }

    /**
     * 跳转到分类activity
     */
    public void skipToTypeActivity() {
        MainTypeActivity.launch(templetUI.getActivity());
    }

    /**
     * 跳转到排行榜activity
     */
    public void skipToRankActivity() {
        RankTopActivity.launch(templetUI.getActivity());
    }

    /**
     * 跳转到限免
     */
    public void skipToLimitFreeActivity() {
        LimitFreeActivity.launch(templetUI.getActivity());
    }

    /**
     * 领取新手礼包
     */
    public void skipToXslb() {
        if (!isActivityEmpty()) {
            ActionEngine.getInstance().toNewGift(templetUI.getActivity());
        }
    }

    /**
     * TempletsInfoObserver
     */
    private class TempletsInfoObserver implements Observer<BeanTempletsInfo> {
        WeakReference<TempletUI> weakTempletUI;
        private final String pageType;
        private final String channelId;

        TempletsInfoObserver(TempletUI templetUI, String pageType, String channelId) {
            this.weakTempletUI = new WeakReference<>(templetUI);
            this.pageType = pageType;
            this.channelId = channelId;
        }

        @Override
        public void onSubscribe(Disposable d) {
            TempletUI templetUI1 = weakTempletUI.get();
            if (null == templetUI1) {
                return;
            }
            templetUI1.setLoadDataState(1);
        }

        @Override
        public void onNext(BeanTempletsInfo value) {
            TempletUI templetUI1 = weakTempletUI.get();
            if (null == templetUI1) {
                return;
            }
            if (value != null && value.isSuccess() && value.isContainTemplet()) {
                templetUI1.setLoadDataState(2);
                templetUI1.setTempletDatas(value.getSection(), true);
                HashMap<String, String> map = new HashMap<>();
                map.put("is_success", "1");
                map.put("has_data", "1");
                if (pageType.equals(LogConstants.MODULE_NSC)) {
                    DzLog.getInstance().logEvent(LogConstants.EVENT_258DTXFJG, map, "");
                } else if (pageType.equals(LogConstants.MODULE_NSCXMZYM) || pageType.equals(LogConstants.MODULE_NSCZYM)) {
                    DzLog.getInstance().logEvent(LogConstants.EVENT_259DTXFJG, map, "");
                }

            } else {
                templetUI1.setLoadDataState(0);
                if (!TextUtils.isEmpty(channelId) && !TextUtils.isEmpty(pageType) && pageType.equals(LogConstants.MODULE_NSC)) {
                    setStatus(value, false);
                    templetUI1.getSingleChannelDataFromCanche(channelId);
                } else {
                    //因为会取本地数据 会导致闪屏
                    setStatus(value, true);
                }
                HashMap<String, String> map = new HashMap<>();
                map.put("is_success", "1");
                map.put("has_data", "2");
                if (pageType.equals(LogConstants.MODULE_NSC)) {
                    DzLog.getInstance().logEvent(LogConstants.EVENT_258DTXFJG, map, "");
                } else if (pageType.equals(LogConstants.MODULE_NSCXMZYM) || pageType.equals(LogConstants.MODULE_NSCZYM)) {
                    DzLog.getInstance().logEvent(LogConstants.EVENT_259DTXFJG, map, "");
                }
            }
        }

        @Override
        public void onError(Throwable e) {
            TempletUI templetUI1 = weakTempletUI.get();
            if (null == templetUI1) {
                return;
            }
            templetUI1.setLoadDataState(0);
            if (!TextUtils.isEmpty(channelId) && !TextUtils.isEmpty(pageType) && pageType.equals(LogConstants.MODULE_NSC)) {
                //因为会取本地数据 会导致闪屏
                setStatus(null, false);
                templetUI1.getSingleChannelDataFromCanche(channelId);
            } else {
                //因为会取本地数据 会导致闪屏
                setStatus(null, true);
            }
            HashMap<String, String> map = new HashMap<>();
            map.put("is_success", "2");
            map.put("has_data", "2");
            if (pageType.equals(LogConstants.MODULE_NSC)) {
                DzLog.getInstance().logEvent(LogConstants.EVENT_258DTXFJG, map, "");
            } else if (pageType.equals(LogConstants.MODULE_NSCXMZYM) || pageType.equals(LogConstants.MODULE_NSCZYM)) {
                DzLog.getInstance().logEvent(LogConstants.EVENT_259DTXFJG, map, "");
            }
        }

        @Override
        public void onComplete() {

        }
    }

    private void setStatus(HwPublicBean hwPublicBean, boolean isShowEmptyView) {
        if (!NetworkUtils.getInstance().checkNet()) {
            //没网
            templetUI.showNoNet(isShowEmptyView);
        } else if (hwPublicBean != null && hwPublicBean.isSuccess()) {
            //缓存成功  但是数据为空
            templetUI.showServerEmpty(isShowEmptyView);
        } else {
            //没有数据
            templetUI.showServerFail(isShowEmptyView);
        }
    }
}
