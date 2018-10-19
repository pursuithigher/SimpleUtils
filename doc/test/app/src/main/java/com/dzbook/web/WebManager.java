package com.dzbook.web;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;

import com.dzbook.AppConst;
import com.dzbook.BaseLoadActivity;
import com.dzbook.activity.ActivityCenterActivity;
import com.dzbook.activity.CenterDetailActivity;
import com.dzbook.activity.GiftCenterActivity;
import com.dzbook.activity.Main2Activity;
import com.dzbook.activity.MainTypeDetailActivity;
import com.dzbook.activity.RankTopActivity;
import com.dzbook.activity.account.ConsumeBookSumActivity;
import com.dzbook.activity.account.RechargeRecordActivity;
import com.dzbook.activity.comment.BookCommentPersonCenterActivity;
import com.dzbook.activity.detail.BookDetailActivity;
import com.dzbook.activity.person.CloudBookShelfActivity;
import com.dzbook.activity.person.PersonAccountActivity;
import com.dzbook.activity.search.SearchActivity;
import com.dzbook.activity.store.CommonTwoLevelActivity;
import com.dzbook.activity.store.ExpendStoreCommonActivity;
import com.dzbook.activity.store.LimitFreeActivity;
import com.dzbook.activity.store.VipStoreActivity;
import com.dzbook.activity.vip.MyVipActivity;
import com.dzbook.activity.vip.VipOpenSuccessActivity;
import com.dzbook.bean.ShareBeanInfo;
import com.dzbook.dialog.common.DialogLoading;
import com.dzbook.event.EventBusUtils;
import com.dzbook.event.EventConstant;
import com.dzbook.fragment.main.MainStoreFragment;
import com.dzbook.lib.net.DzSchedulers;
import com.dzbook.lib.utils.ALog;
import com.dzbook.lib.utils.StringUtil;
import com.dzbook.loader.BookLoader;
import com.dzbook.log.DzLog;
import com.dzbook.log.LogConstants;
import com.dzbook.model.ModelAction;
import com.dzbook.mvp.DialogConstants;
import com.dzbook.mvp.presenter.RechargeListPresenter;
import com.dzbook.net.hw.HwRequestLib;
import com.dzbook.net.hw.RequestCall;
import com.dzbook.pay.Listener;
import com.dzbook.recharge.RechargeObserver;
import com.dzbook.recharge.order.RechargeParamBean;
import com.dzbook.utils.GlideImageLoadUtils;
import com.dzbook.utils.HwLog;
import com.dzbook.utils.ShareUtils;
import com.dzbook.utils.SpUtil;
import com.dzbook.utils.ThirdPartyLog;
import com.dzbook.utils.WhiteListWorker;
import com.dzbook.utils.hw.CheckUpdateUtils;
import com.dzbook.utils.hw.FeedBackHelper;
import com.dzbook.utils.hw.LoginUtils;
import com.dzpay.recharge.api.UtilRecharge;
import com.dzpay.recharge.bean.RechargeAction;
import com.dzpay.recharge.bean.RechargeConstants;
import com.dzpay.recharge.bean.RechargeMsgResult;
import com.dzpay.recharge.netbean.OrdersResultBean;
import com.dzpay.recharge.netbean.VipOrdersResultBean;
import com.dzpay.recharge.utils.RechargeMsgUtils;
import com.dzpay.recharge.utils.RechargeWayUtils;
import com.ishugui.R;
import com.iss.app.BaseActivity;
import com.iss.view.common.ToastAlone;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;

import hw.sdk.utils.HwEncryptParam;
import io.reactivex.android.schedulers.AndroidSchedulers;

/**
 * JS通用定义。
 * 待确认问题
 * 1，登录+VIP订购，忠忠补充(已弄完)
 * 2，打包定价领取图书，满堂补充
 * 3，分享，文洲确认下
 * 4，升级，典周补充
 *
 * @author zhenglk 15/11/9.
 */
public class WebManager extends BaseWebManager {
    private static final String TAG = "WebManager";
    private static HashSet<String> actions = new HashSet<>();
    private static HashMap<String, Integer> tabs = new HashMap<>();
    private String uFrom;
    private String channelId, channelPos, channelTitle;

    static {
        actions.add("01");
        actions.add("02");
        actions.add("03");
        actions.add("04");
        actions.add("05");
        tabs.put("shelf", 0);
        tabs.put("store", 1);
        tabs.put("classify", 2);
        tabs.put("mine", 3);
    }

    /**
     * 构造
     *
     * @param activity activity
     * @param webView  webView
     */
    public WebManager(final BaseActivity activity, WebView webView) {
        super(activity, webView);
    }

    /**
     * 构造
     *
     * @param activity activity
     * @param webView  webView
     * @param uFrom    uFrom
     */
    public WebManager(final BaseActivity activity, WebView webView, String uFrom) {
        super(activity, webView);
        this.uFrom = uFrom;
    }

    public static HashMap<String, Integer> getTabs() {
        return tabs;
    }

    /**
     * 用于RSA加密
     *
     * @param json
     * @return
     */
    @JavascriptInterface
    @Override
    public String init(String json) {
        if (!TextUtils.isEmpty(json)) {
            try {
                JSONObject jsonObject = new JSONObject();
                HashMap<String, String> map = jsonToHashMap(json);
                if (map != null && map.size() > 0) {
                    for (Map.Entry<String, String> entry : map.entrySet()) {
                        jsonObject.put(entry.getKey(), HwEncryptParam.hwEncrpt(entry.getValue()));
                    }
                }
                return jsonObject.toString();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }


    @Override
    @JavascriptInterface
    public void logClick(@LogConstants.Module String module, @LogConstants.Zone String zone, String adid, String trackId, String json) {
        if (!TextUtils.isEmpty(module) && !(StringUtil.isAllEmpty(zone, adid, json))) {
            ALog.d(TAG, "(logClick) module:" + module + "|zone:" + zone + "|adid:" + adid + "|trackId:" + trackId + "|json:" + json);
            HashMap<String, String> map = jsonToHashMap(json);
            DzLog.getInstance().logClick(module, zone, adid, map, trackId);
        }
    }

    @Override
    @JavascriptInterface
    public void hwLogClick(String columeID, String columeName, String columePos, String columeTemp, String contentID, String contentName, String contentType) {
        if (mActivity instanceof Main2Activity) {
            HwLog.columnClick(HwLog.getLogLinkedHashMap().get("store"), this.channelId, this.channelTitle, this.channelPos, columeID, columeName, columePos, columeTemp, contentID, contentName, contentType);
        }
    }

    @Override
    @JavascriptInterface
    public void logEvent(String event, String trackId, String json) {
        if (!TextUtils.isEmpty(event)) {
            ALog.d(TAG, "(logEvent) event:" + event + "|trackId:" + trackId + "|json:" + json);

            HashMap<String, String> map = jsonToHashMap(json);
            DzLog.getInstance().logEvent(event, map, trackId);
        }
    }

    @Override
    @JavascriptInterface
    public void logPv(String name, String trackId, String json) {
        if (TextUtils.isEmpty(name)) {
            return;
        }
        ALog.d(TAG, "(logPv) event:" + name + "|trackId:" + trackId + "|json:" + json);

        HashMap<String, String> map = jsonToHashMap(json);
        DzLog.getInstance().logPv(name, map, trackId);
    }

    @Override
    @JavascriptInterface
    public void openBook(String json) {
        ALog.d(TAG, "(openBook) json:" + json);
        if (null == mActivity || tooFast()) {
            return;
        }
        JSONObject obj = optJSONObject(json);
        if (null == obj) {
            return;
        }

        final String bookId = obj.optString("bookId");
        final String mode = obj.optString("mode");
        if (TextUtils.isEmpty(bookId)) {
            return;
        }

        AndroidSchedulers.mainThread().scheduleDirect(new Runnable() {
            @Override
            public void run() {
                // 书籍详情页面
                if ("2".equals(mode)) {
                    BookDetailActivity.launch(mActivity, bookId, "");
                } else {
                    ModelAction.goReaderSmart(mActivity, AppConst.GO_READER, -1, bookId, null, 0, false);
                }
            }
        });
    }

    @Override
    @JavascriptInterface
    public void openUrl(String json) {
        ALog.d(TAG, "(openUrl) json:" + json);
        if (null == mActivity || tooFast()) {
            return;
        }

        JSONObject obj = optJSONObject(json);
        if (null == obj) {
            return;
        }
        final String url = obj.optString("url");
        final String title = obj.optString("title");
        if (TextUtils.isEmpty(url)) {
            return;
        }
        AndroidSchedulers.mainThread().scheduleDirect(new Runnable() {
            @Override
            public void run() {
                // 活动中心。服务端临时配置，可能不会下行url信息，此种情景，点击无效。 兼容新书城type7的情况
                try {
                    Intent intent = new Intent(mActivity, CenterDetailActivity.class);
                    intent.putExtra("url", url);
                    if (!TextUtils.isEmpty(title)) {
                        intent.putExtra("notiTitle", title);
                    }
                    intent.putExtra("paramAppend", true);
                    intent.putExtra("web", ActionEngine.WEB_HDZX);

                    //打点
                    if (mActivity.getIntent() != null) {
                        String operateFrom = mActivity.getIntent().getStringExtra(LogConstants.KEY_OPERATE_FROM);
                        String partFrom = mActivity.getIntent().getStringExtra(LogConstants.KEY_PART_FROM);

                        if (TextUtils.isEmpty(operateFrom) && TextUtils.isEmpty(partFrom) && TextUtils.equals(uFrom, ThirdPartyLog.FROM_MAIN_STORE)) {
                            intent.putExtra(LogConstants.KEY_OPERATE_FROM, MainStoreFragment.TAG);
                            intent.putExtra(LogConstants.KEY_PART_FROM, LogConstants.RECHARGE_SOURCE_FROM_VALUE_5);
                        } else {
                            intent.putExtra(LogConstants.KEY_OPERATE_FROM, operateFrom);
                            intent.putExtra(LogConstants.KEY_PART_FROM, partFrom);
                        }

                    }

                    if (!TextUtils.isEmpty(uFrom)) {
                        ThirdPartyLog.onEventValueOldClick(mActivity, uFrom + ThirdPartyLog.FROM_CENTER, null, 1);
                    }
                    mActivity.startActivity(intent);
                    BaseActivity.showActivity(mActivity);
                } catch (Exception ignore) {
                    ALog.printStackTrace(ignore);
                }
            }
        });
    }

    private VipOrdersResultBean getParams(String orderResultJson) {

        VipOrdersResultBean bean = null;
        try {
            bean = new VipOrdersResultBean();
            bean.parseJSON(new JSONObject(orderResultJson));
        } catch (Exception e) {
            ALog.printStackTrace(e);
        }

        return bean;
    }

    /**
     * vip订单
     *
     * @param json json数据 {"id":"","isRenew":"1是，2:不是"}
     */
    @Override
    @JavascriptInterface
    public void vipOrder(final String json) {
        ALog.d(TAG, "(openOrder) json:" + json);
        if (null == mActivity || tooFast()) {
            return;
        }

        if (TextUtils.isEmpty(json)) {
            return;
        }
        DzSchedulers.main(new Runnable() {
            @Override
            public void run() {
                LoginUtils.getInstance().forceLoginCheck(mActivity, new LoginUtils.LoginCheckListener() {
                    @Override
                    public void loginComplete() {
                        try {
                            JSONObject jsonObject = new JSONObject(json);
                            String id = jsonObject.optString("id");
                            final int isRenew = jsonObject.optInt("isRenew");
                            final String name = jsonObject.optString("name");
                            final String period = jsonObject.optString("period");
                            final String money = jsonObject.optString("money");
                            HashMap<String, String> params = BookLoader.getInstance().getDzLoader().getRechargePayMap(mActivity, null, id, RechargeWayUtils.getString(2));
                            if (isRenew == 1) {
                                params.put(RechargeMsgResult.IS_VIP_OPEN_RENEW, "1");
                            }
                            final DialogLoading mCustomDialog = new DialogLoading(mActivity);
                            mCustomDialog.setCancelable(false);
                            mCustomDialog.setCanceledOnTouchOutside(false);
                            mCustomDialog.setShowMsg(mActivity.getString(R.string.dialog_isLoading));
                            mCustomDialog.show();
                            RechargeObserver rechargeOrder = new RechargeObserver(mActivity, new Listener() {
                                @Override
                                public void onSuccess(int ordinal, final HashMap<String, String> param) {
                                    if (mCustomDialog.isShowing()) {
                                        mCustomDialog.dismiss();
                                    }
                                    rechargeOrderSuccess(ordinal, param, name, period, money, isRenew);
                                }
                                @Override
                                public void onFail(HashMap<String, String> param) {
                                    if (param == null) {
                                        return;
                                    }
                                    ToastAlone.showShort(RechargeMsgUtils.getRechargeMsg(param));
                                    if (mCustomDialog.isShowing()) {
                                        mCustomDialog.dismiss();
                                    }
                                }
                                @Override
                                public void onStatusChange(int status, Map<String, String> parm) {
                                    if (mCustomDialog != null && !mActivity.isFinishing()) {
                                        String statusChangeMsg = parm.get(RechargeMsgResult.STATUS_CHANGE_MSG);
                                        mCustomDialog.setShowMsg(statusChangeMsg);
                                    }
                                }
                            }, RechargeAction.RECHARGE);

                            UtilRecharge manager = UtilRecharge.getDefault();
                            manager.execute(mActivity, params, RechargeAction.RECHARGE.ordinal(), rechargeOrder);
                        } catch (Exception e) {
                            ALog.printStackTrace(e);
                        }
                    }
                });
            }
        });
    }

    private void rechargeOrderSuccess(int ordinal, final HashMap<String, String> param, final String name, final String period, final String money, final int isRenew) {
        final String json = param.get(RechargeMsgResult.VIP_PAY_RESULT_JSON);
        if (!TextUtils.isEmpty(json)) {
            VipOrdersResultBean resultBean = getParams(json);
            if (resultBean != null) {
                SpUtil.getinstance(mActivity).setInt(SpUtil.DZ_IS_VIP, 1);
                SpUtil.getinstance(mActivity).setString(SpUtil.DZ_VIP_EXPIRED_TIME, resultBean.deadLine);
                EventBusUtils.sendMessage(EventConstant.CODE_VIP_OPEN_SUCCESS_REFRESH_STATUS);
                VipOpenSuccessActivity.launch((Activity) mActivity, resultBean);
            }
        }

        DzSchedulers.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    VipOrdersResultBean bean = new VipOrdersResultBean().parseJSON(new JSONObject(json));

                    HwLog.buyVIP(name, period, bean.startTime, bean.deadLine, money, isRenew == 1 ? "1" : "2");
                } catch (Exception e) {
                    ALog.printStackTrace(e);
                }
            }
        });
    }

    @Override
    @JavascriptInterface
    public void toRecharge(String json) {
        ALog.d(TAG, "(toRecharge) json:" + json);
        if (null == mActivity || tooFast()) {
            return;
        }


        if (!TextUtils.isEmpty(uFrom)) {
            ThirdPartyLog.onEventValueOldClick(mActivity, uFrom + ThirdPartyLog.FROM_RECHARGE, null, 1);
        }

        LoginUtils.getInstance().forceLoginCheck(mActivity, new LoginUtils.LoginCheckListener() {
            @Override
            public void loginComplete() {
                String operateFrom = "";
                String partFrom = "";
                if (mActivity.getIntent() != null) {
                    operateFrom = mActivity.getIntent().getStringExtra(LogConstants.KEY_OPERATE_FROM);
                    partFrom = mActivity.getIntent().getStringExtra(LogConstants.KEY_PART_FROM);
                }

                Listener listener = new Listener() {
                    @Override
                    public void onSuccess(int ordinal, HashMap<String, String> parm) {
                        ALog.dLk("WebManager: recharge onSuccess->" + parm);
                        if (mWebView != null) {
                            mWebView.reload();
                        }
                    }

                    @Override
                    public void onFail(HashMap<String, String> parm) {
                        ALog.dLk("WebManager: recharge onFail->" + parm);
                    }
                };

                RechargeParamBean paramBean = new RechargeParamBean(mActivity, listener, RechargeAction.RECHARGE.ordinal(), "活动", null, null, operateFrom, partFrom);
                RechargeListPresenter.launch(paramBean);
            }
        });
    }

    @Override
    @JavascriptInterface
    public void toMainTab(String json) {
        ALog.d(TAG, "(toMainTab) json:" + json);
        if (null == mActivity || tooFast()) {
            return;
        }
        JSONObject jsonObject;
        try {
            if (TextUtils.isEmpty(json)) {
                return;
            }
            jsonObject = new JSONObject(json);
            String tabName = jsonObject.optString("tabName");
            if (null != tabs && tabs.containsKey(tabName)) {
                int tab = tabs.get(tabName);
                Main2Activity.launch(mActivity, tab);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    @JavascriptInterface
    public void signInSuccess(String award, String type) {
        if (StringUtil.isAllEmpty(award, type)) {
            return;
        }
        ALog.d(TAG, "(signInSuccess) award:" + award + "|type:" + type);
        //签到成功
        HwLog.setSign("1", award);
        EventBusUtils.sendMessage(EventConstant.REQUESTCODE_SIGNINSUCCESS, EventConstant.TYPE_MAINSHELFFRAGMENT, null);
    }

    @Override
    @JavascriptInterface
    public void supplemenstSuccess(String award, String type) {
        if (StringUtil.isAllEmpty(award, type)) {
            return;
        }
        //补签成功
        HwLog.setSign("2", award);
    }

    /**
     * 书城通用二级列表页面
     *
     * @param json json数据 {id,title}
     */
    @Override
    @JavascriptInterface
    public void openBookList(String json) {
        ALog.d(TAG, "(openBookList) json:" + json);
        if (null == mActivity || tooFast()) {
            return;
        }
        JSONObject obj = optJSONObject(json);
        if (null == obj) {
            return;
        }
        String id = obj.optString("id");
        String title = obj.optString("title");
        //H5书城限免点击更多
        if (TextUtils.isEmpty(id)) {
            return;
        }
        CommonTwoLevelActivity.launch(mActivity, title, id);
    }


    @Override
    @JavascriptInterface
    public void toShareWxWithBigImage(String json) {
        ALog.d(TAG, "(toShareWxWithBigImage) json:" + json);
        if (null == mActivity || tooFast()) {
            return;
        }
        JSONObject obj = optJSONObject(json);
        if (null == obj) {
            return;
        }

        String bigImg = obj.optString("bigImg");
        if (!TextUtils.isEmpty(bigImg)) {
            try {
                if (TextUtils.isEmpty(bigImg)) {
                    ToastAlone.showShort(R.string.toast_js_error);
                    return;
                }
                if (mActivity instanceof CenterDetailActivity) {
                    mActivity.showDialogByType(DialogConstants.TYPE_GET_DATA, "正在生成分享内容，请稍后...");
                }

                GlideImageLoadUtils.getInstanse().downloadImageBitmapFromUrl(mActivity, bigImg, new GlideImageLoadUtils.DownloadImageListener() {

                    @Override
                    public void downloadSuccess(Bitmap bitmap) {
                        ShareUtils.directWechatShare(mActivity, ShareUtils.SHARE_TO_IMG, null, null, null, bitmap, false);

                        if (mActivity instanceof CenterDetailActivity) {
                            mActivity.dissMissDialog();
                        }
                    }

                    @Override
                    public void downloadSuccess(File resource) {

                    }


                    @Override
                    public void downloadFailed() {
                        ToastAlone.showShort("分享失败，请重试");

                        if (mActivity instanceof CenterDetailActivity) {
                            mActivity.dissMissDialog();
                        }
                    }
                }, false);
            } catch (Exception e) {
                ALog.printStackTrace(e);
            }
        } else {
            ToastAlone.showShort(R.string.toast_js_error);
        }
    }

    /**
     * 刷新用户信息
     *
     * @param json json数据 例子：{"remain":10,"vouchers":100,"unit":"看点","vUnit":"代金券"}
     */
    @Override
    @JavascriptInterface
    public void refreshUserInfo(String json) {
        ALog.d(TAG, "(refreshUserInfo) json:" + json);

        try {

            JSONObject jsonObject = new JSONObject(json);
            int remain = jsonObject.optInt("remain");
            int vouchers = jsonObject.optInt("vouchers");
            String unit = jsonObject.optString("unit");
            String vUnit = jsonObject.optString("vUnit");
            SpUtil sp = SpUtil.getinstance(mActivity);
            if (remain != 0 && !TextUtils.isEmpty(unit)) {
                sp.setUserVouchers(remain + "", unit);
            }
            if (vouchers != 0 && !TextUtils.isEmpty(vUnit)) {
                sp.setUserVouchers(vouchers + "", vUnit);
            }

        } catch (Exception e) {
            ALog.printStackTrace(e);
        }

    }

    @Override
    @JavascriptInterface
    public void setTodayFlag(String json) {
        ALog.d(TAG, "(setTodayFlag) json:" + json);

        if (null == mActivity) {
            return;
        }
        JSONObject obj = optJSONObject(json);
        if (null == obj) {
            return;
        }

        // key = user.today.sign 标识今日签到
        final String key = obj.optString("key");
        if (TextUtils.isEmpty(key)) {
            return;
        }
        SpUtil.getinstance(mActivity).markTodayByKey(key);
    }

    @Override
    @JavascriptInterface
    public void closeCurView(String json) {
        ALog.d(TAG, "(closeCurView) json:" + json);

        if (null == mActivity) {
            return;
        }
        mActivity.finish();
        ModelAction.checkElseGoHome(mActivity);
    }

    @Override
    @JavascriptInterface
    public void shareGroup(String json) {
        ALog.d(TAG, "(shareGroup) json:" + json);
        if (tooFast()) {
            return;
        }
        JSONObject obj = optJSONObject(json);
        if (null == obj) {
            ToastAlone.showShort(R.string.toast_js_error);
            return;
        }
        try {
            ShareBeanInfo shareBeanInfo = new ShareBeanInfo();
            shareBeanInfo.parseJSON(obj);
            ALog.dZz("分享参数：" + shareBeanInfo.toString());

            ShareUtils.goToShare(mActivity, shareBeanInfo, ShareUtils.DIALOG_SHOW_FROM_SIGN_SHARE);
        } catch (Exception e) {
            ALog.printStackTrace(e);
        }
    }

    @Override
    @JavascriptInterface
    public void shareItem(String json) {
        ALog.d(TAG, "(shareItem) json:" + json);
        if (tooFast()) {
            return;
        }
        JSONObject obj = optJSONObject(json);
        if (null == obj) {
            ToastAlone.showShort(R.string.toast_js_error);
            return;
        }

        String shareType = obj.optString("shareType");
        final String url = obj.optString("url");
        final String mTitle = obj.optString("title");
        final String des = obj.optString("des");
        final String img = obj.optString("img");

        switch (shareType) {
            case "wxPyq":
                GlideImageLoadUtils.getInstanse().downloadImageBitmapFromUrl(mActivity, img, new GlideImageLoadUtils.DownloadImageListener() {
                    @Override
                    public void downloadSuccess(Bitmap bitmap) {
                        ShareUtils.directWechatShareByType(mActivity, ShareUtils.SHARE_TO_WEB, url, mTitle, des, bitmap, ShareUtils.SHARE_TO_FRIEND_CIRCLE, false);
                    }

                    @Override
                    public void downloadSuccess(File resource) {

                    }

                    @Override
                    public void downloadFailed() {
                        ToastAlone.showShort("分享失败，请重试");
                    }
                }, false);
                break;
            case "wxHy":
                GlideImageLoadUtils.getInstanse().downloadImageBitmapFromUrl(mActivity, img, new GlideImageLoadUtils.DownloadImageListener() {
                    @Override
                    public void downloadSuccess(Bitmap bitmap) {
                        ShareUtils.directWechatShareByType(mActivity, ShareUtils.SHARE_TO_WEB, url, mTitle, des, bitmap, ShareUtils.SHARE_TO_FRIENDS, false);
                    }

                    @Override
                    public void downloadSuccess(File resource) {

                    }

                    @Override
                    public void downloadFailed() {
                        ToastAlone.showShort("分享失败，请重试");
                    }
                }, false);
                break;
            case "qqHy":
                ShareUtils.directQQShareByType(mActivity, url, mTitle, des, img, ShareUtils.SHARE_TO_FRIENDS);
                break;
            case "qqKJ":
                ShareUtils.directQQShareByType(mActivity, url, mTitle, des, img, ShareUtils.SHARE_TO_FRIEND_CIRCLE);
                break;
            default:
                ToastAlone.showShort(R.string.toast_js_error);
                break;
        }
    }

    /**
     * 登录回调
     *
     * @param json json数据 {"url":""}
     */
    @Override
    @JavascriptInterface
    public void toLoginWitCallback(String json) {
        ALog.d(TAG, "(toLoginWitCallback) json:" + json);
        if (null == mActivity || tooFast()) {
            return;
        }
        JSONObject obj = optJSONObject(json);
        if (null == obj) {
            return;
        }

        final String url = obj.optString("url");
        if (TextUtils.isEmpty(url)) {
            return;
        }
        try {
            DzSchedulers.main(new Runnable() {
                @Override
                public void run() {
                    LoginUtils.getInstance().forceLoginCheck(mActivity, new LoginUtils.LoginCheckListener() {
                        @Override
                        public void loginComplete() {
                            CenterDetailActivity.showWithPriMap(mActivity, url, null);
                        }
                    });
                }
            });
        } catch (Exception e) {
            ALog.printStackTrace(e);
        }
    }

    @Override
    @JavascriptInterface
    public void toast(String json) {
        ALog.d(TAG, "(toast) json:" + json);
        JSONObject obj = optJSONObject(json);
        if (null == obj) {
            return;
        }

        final String msg = obj.optString("msg");
        if (TextUtils.isEmpty(msg)) {
            return;
        }
        ToastAlone.showShort(msg);
    }

    /**
     * H5领取图书（限免，打包定价。。。）
     *
     * @param json json数据 {action,commodityId,bookIds}
     *             "action":字典action
     *             action
     *             01 打包订购一键购
     *             02 打包订购组合购
     *             03 限免领取书籍 //add by caimt 2017-10-23 13:48:55
     *             04 优惠购//add by caimt 2017-10-23 13:48:55
     *             05 立即领取添加到书架//add by caimt 2017-10-23 13:48:55
     *             "commodity_id":"1"//商品id，
     *             "bookIds":""//书籍id组,数组
     */
    @Override
    @JavascriptInterface
    public void giveBook(String json) {
        ALog.d(TAG, "(giveBook) json:" + json);
        if (null == mActivity || tooFast()) {
            return;
        }
        try {
            if (TextUtils.isEmpty(json)) {
                return;
            }
            JSONObject jsonObject = new JSONObject(json);
            final String action = jsonObject.optString("action");
            final String bookIds = jsonObject.optString("bookIds");
            if (null != actions && actions.contains(action)) {
                LoginUtils.getInstance().forceLoginCheck(mActivity, new LoginUtils.LoginCheckListener() {
                    @Override
                    public void loginComplete() {
                        if (mActivity instanceof BaseLoadActivity) {
                            ActionEngine.getInstance().addBookShelf((BaseLoadActivity) mActivity, bookIds, WhiteListWorker.BOOK_LING_QU_VALUE, null);
                        }
                    }
                });
            }
        } catch (Exception e) {
            ALog.cmtDebug("Exception:" + e);
        }
    }

    @Override
    @JavascriptInterface
    public void hwUpdateApp(String json) {
        ALog.d(TAG, "(hwUpdateApp) json:" + json);
        if (null == mActivity || tooFast()) {
            return;
        }
        CheckUpdateUtils.checkUpdate(mActivity, CheckUpdateUtils.ACTIVE_UPDATE);
    }

    @Override
    @JavascriptInterface
    public String addSignHeader(String data, String type) {
        if (!StringUtil.isAllEmpty(data, type)) {

            return HwRequestLib.getInstance().getH5AddSignHeaderData(data, type);
        }
        return "";
    }

    @Override
    @JavascriptInterface
    public void getPercent(String json) {
        JSONObject obj = optJSONObject(json);
        if (null == obj) {
            return;
        }

        // key = user.today.sign 标识今日签到
        mPercent = obj.optDouble("percent");
        //        ALog.eDongdz("**************webview:getPercent:percent:" + mPercent);
    }

    @Override
    @JavascriptInterface
    public void onEventValue(String json) {
        ALog.d(TAG, "(onEventValue) json:" + json);
        try {
            JSONObject jsonObject = new JSONObject(json);
            HashMap<String, String> hashMap = new HashMap<>();
            String eventId = (String) jsonObject.remove("id");
            int duration = jsonObject.isNull("duration") ? 0 : ((Integer) jsonObject.remove("duration")).intValue();
            Iterator keys = jsonObject.keys();
            String value = null;

            while (keys.hasNext()) {
                hashMap.put(value = (String) keys.next(), jsonObject.getString(value));
            }
            ThirdPartyLog.onEventValueOld(mActivity, eventId, hashMap, duration);

        } catch (Exception e) {
            ALog.cmtDebug("onEventValue:" + e.toString());
        }
    }

    /**
     * 打开我的vip
     *
     * @param json
     */
    @Override
    @JavascriptInterface
    public void openVip(String json) {
        ALog.d(TAG, "(openVip) json:" + json);
        MyVipActivity.launch(mActivity);
    }

    @Override
    @JavascriptInterface
    public void openVipStore(String json) {
        ALog.d(TAG, "(openVipStore) json:" + json);
        VipStoreActivity.launch(mActivity);
    }

    /**
     * openSecondType
     *
     * @param json json
     */
    @Override
    @JavascriptInterface
    public void openSecondType(String json) {
        ALog.d(TAG, "(openSecondType) json:" + json);
        JSONObject jsonObject = optJSONObject(json);
        if (null != jsonObject) {
            String title = jsonObject.optString("title");
            String cid = jsonObject.optString("firstCid");
            String categoryId = jsonObject.optString("secondCid");
            if (!TextUtils.isEmpty(title) && !TextUtils.isEmpty(cid) && !TextUtils.isEmpty(categoryId)) {
                MainTypeDetailActivity.launch(mActivity, title, cid, categoryId);
            }
        }
    }

    @Override
    @JavascriptInterface
    public void buyPackBook(String json) {
        ALog.d(TAG, "(buyPackBook) json:" + json);
        JSONObject jsonObject = optJSONObject(json);
        if (null != jsonObject) {
            String action = jsonObject.optString("action");
            String commodityId = jsonObject.optString("commodityId");
            String bookIds = jsonObject.optString("bookIds");
            String needLogin = jsonObject.optString("needLogin");
            boolean isNeedLogin = true;
            if ("0".equals(needLogin)) {
                isNeedLogin = false;
            }
            ActionEngine.addBookByH5(mActivity, action, commodityId, "1", bookIds, "H5", mWebView, DzLog.generateTrackd(), "", isNeedLogin);
        }
    }


    @Override
    @JavascriptInterface
    public void setChannelInfo(String id, String pos, String title) {
        this.channelId = id;
        this.channelPos = pos;
        this.channelTitle = title;
    }


    @Override
    @JavascriptInterface
    public void openMainTypeDetail(String json) {
        ALog.d(TAG, "(openMainTypeDetail) json:" + json);
        JSONObject jsonObject = optJSONObject(json);
        if (null != jsonObject) {
            String title = jsonObject.optString("title");
            String cid = jsonObject.optString("cid");
            String categoryId = jsonObject.optString("categoryId");
            if (!TextUtils.isEmpty(title) && !TextUtils.isEmpty(cid) && !TextUtils.isEmpty(categoryId)) {
                MainTypeDetailActivity.launch(mActivity, title, cid, categoryId);
            }
        }
    }

    @Override
    @JavascriptInterface
    public void openToSign(String json) {
        ALog.d(TAG, "(openToSign) json:" + json);
        LoginUtils.getInstance().forceLoginCheck(mActivity, new LoginUtils.LoginCheckListener() {
            @Override
            public void loginComplete() {
                ActionEngine.getInstance().toSign(mActivity);
            }
        });
    }

    @Override
    @JavascriptInterface
    public void openSearch(String json) {
        ALog.d(TAG, "(openSearch) json:" + json);
        SearchActivity.launch(mActivity);
    }

    @Override
    @JavascriptInterface
    public void openLimitFree(String json) {
        ALog.d(TAG, "(openLimitFree) json:" + json);
        LimitFreeActivity.launch(mActivity);
    }

    @Override
    @JavascriptInterface
    public void openRankTop(String json) {
        ALog.d(TAG, "(openRankTop) json:" + json);
        RankTopActivity.launch(mActivity);
    }

    @Override
    @JavascriptInterface
    public void openVipStoreActivity(String json) {
        ALog.d(TAG, "(openVipStoreActivity) json:" + json);
        VipStoreActivity.launch(mActivity);
    }

    @Override
    @JavascriptInterface
    public void openRechargeList(String json) {
        ALog.d(TAG, "(openRechargeList) json:" + json);
        final Listener listener = new Listener() {
            @Override
            public void onSuccess(int ordinal, HashMap<String, String> parm) {

            }

            @Override
            public void onFail(HashMap<String, String> parm) {

            }
        };
        LoginUtils.getInstance().forceLoginCheck(mActivity, new LoginUtils.LoginCheckListener() {
            @Override
            public void loginComplete() {
                // TODO 一、参数要与服务端确定 （联调时确认）
                RechargeParamBean paramBean = new RechargeParamBean(mActivity, listener, RechargeAction.RECHARGE.ordinal(), "书架活动", null, null, WebManager.TAG, LogConstants.RECHARGE_SOURCE_FROM_VALUE_4);
                RechargeListPresenter.launch(paramBean);
            }
        });
    }

    @Override
    @JavascriptInterface
    public void openGiftCenter(final String json) {
        ALog.d(TAG, "(openGiftCenter) json:" + json);
        LoginUtils.getInstance().forceLoginCheck(mActivity, new LoginUtils.LoginCheckListener() {
            @Override
            public void loginComplete() {
                JSONObject jsonObject = optJSONObject(json);
                if (null != jsonObject) {
                    String index = jsonObject.optString("index");
                    Intent intent = new Intent();
                    if (!TextUtils.isEmpty(index)) {
                        intent.putExtra("index", index);
                    }
                    intent.setClass(mActivity, GiftCenterActivity.class);
                    mActivity.startActivity(intent);
                }
            }
        });
    }

    @Override
    @JavascriptInterface
    public void openActivityCenter(String json) {
        ALog.d(TAG, "(openActivityCenter) json:" + json);
        ActivityCenterActivity.launch(mActivity);
    }

    @Override
    @JavascriptInterface
    public void openPersonAccount(String json) {
        ALog.d(TAG, "(openPersonAccount) json:" + json);
        LoginUtils.getInstance().forceLoginCheck(mActivity, new LoginUtils.LoginCheckListener() {
            @Override
            public void loginComplete() {
                PersonAccountActivity.launch(mActivity);
            }
        });
    }

    @Override
    @JavascriptInterface
    public void openMyReadTime(String json) {
        ALog.d(TAG, "(openMyReadTime) json:" + json);
        LoginUtils.getInstance().forceLoginCheck(mActivity, new LoginUtils.LoginCheckListener() {
            @Override
            public void loginComplete() {
                CenterDetailActivity.show(mActivity, RequestCall.urlMyReadTime(), mActivity.getResources().getString(R.string.str_read_length));
            }
        });
    }

    @Override
    @JavascriptInterface
    public void openBookCommentPersonCenter(String json) {
        ALog.d(TAG, "(openBookCommentPersonCenter) json:" + json);
        LoginUtils.getInstance().forceLoginCheck(mActivity, new LoginUtils.LoginCheckListener() {
            @Override
            public void loginComplete() {
                BookCommentPersonCenterActivity.launch(mActivity);
            }
        });
    }

    @Override
    @JavascriptInterface
    public void openCloudBookShelf(String json) {
        ALog.d(TAG, "(openCloudBookShelf) json:" + json);
        LoginUtils.getInstance().forceLoginCheck(mActivity, new LoginUtils.LoginCheckListener() {
            @Override
            public void loginComplete() {
                Intent intent = new Intent(mActivity, CloudBookShelfActivity.class);
                mActivity.startActivity(intent);
            }
        });
    }

    @Override
    @JavascriptInterface
    public void openFeedBack(String json) {
        ALog.d(TAG, "(openFeedBack) json:" + json);
        FeedBackHelper.getInstance().gotoFeedBack(mActivity.getContext());
    }

    @Override
    @JavascriptInterface
    public void openRechargeRecord(String json) {
        ALog.d(TAG, "(openRechargeRecord) json:" + json);
        LoginUtils.getInstance().forceLoginCheck(mActivity, new LoginUtils.LoginCheckListener() {
            @Override
            public void loginComplete() {
                Intent i = new Intent(mActivity, RechargeRecordActivity.class);
                mActivity.startActivity(i);
            }
        });
    }

    @Override
    @JavascriptInterface
    public void openConsumeBookSum(String json) {
        ALog.d(TAG, "(openConsumeBookSum) json:" + json);
        LoginUtils.getInstance().forceLoginCheck(mActivity, new LoginUtils.LoginCheckListener() {
            @Override
            public void loginComplete() {
                ConsumeBookSumActivity.launchConsumeBookSum(mActivity);
            }
        });
    }

    @Override
    @JavascriptInterface
    public boolean isHasLogin(String json) {
        ALog.d(TAG, "(isHasLogin) json:" + json);
        return LoginUtils.getInstance().checkLoginStatus(AppConst.getApp());
    }

    @Override
    @JavascriptInterface
    public void toLogin(String json) {
        ALog.d(TAG, "(toLogin) json:" + json);
        LoginUtils.getInstance().forceLoginCheck(mActivity, new LoginUtils.LoginCheckListener() {
            @Override
            public void loginComplete() {
                //js调用客户端，去登陆
                ALog.cmtDebug("js调用客户端登陆成功");
                EventBusUtils.sendMessage(EventConstant.LOGIN_SUCCESS_FINISH_REFRESH_SIGN_PAGE_REQUESTCODE, EventConstant.TYPE_CENTER_DETAIL_ACTIVITY, null);
            }
        });
    }

    @Override
    @JavascriptInterface
    public void openChannelPage(String json) {
        ALog.d(TAG, "(openChannelPage) json:" + json);
        JSONObject jsonObject = optJSONObject(json);
        if (null != jsonObject) {
            String channelId1 = jsonObject.optString("channelId");
            String channelType = jsonObject.optString("channelType");
            String title = jsonObject.optString("title");
            ExpendStoreCommonActivity.launch(mActivity, channelId1, channelType, title);
        }
    }

    @Override
    @JavascriptInterface
    public void toAmountRecharge(String json) {
        ALog.d(TAG, "(toAmountRecharge) json:" + json);
        if (null == mActivity || tooFast()) {
            return;
        }
        final JSONObject jsonObject = optJSONObject(json);

        DzSchedulers.main(new Runnable() {
            @Override
            public void run() {
                LoginUtils.getInstance().forceLoginCheck(mActivity, new LoginUtils.LoginCheckListener() {
                    @Override
                    public void loginComplete() {
                        if (null != jsonObject) {
                            String moneyId = jsonObject.optString("id");
                            final String amountNum = jsonObject.optString("amountNum");
                            final String productNum = jsonObject.optString("productNum");
                            final String giveNum = jsonObject.optString("giveNum");
                            final DialogLoading mCustomDialog = new DialogLoading(mActivity);
                            mCustomDialog.setCancelable(false);
                            mCustomDialog.setCanceledOnTouchOutside(false);
                            mCustomDialog.setShowMsg(mActivity.getString(R.string.dialog_isLoading));
                            mCustomDialog.show();
                            HashMap<String, String> params = BookLoader.getInstance().getDzLoader().getRechargePayMap(mActivity, "", null, null);
                            params.put(RechargeMsgResult.RECHARGE_MONEY_ID, moneyId);
                            params.put(RechargeMsgResult.RECHARGE_WAY, RechargeWayUtils.getString(1));
                            params.put(RechargeMsgResult.USER_ID, SpUtil.getinstance(AppConst.getApp()).getUserID());
                            params.put(RechargeMsgResult.APP_TOKEN, SpUtil.getinstance(AppConst.getApp()).getAppToken());
                            RechargeObserver rechargeOrder = new RechargeObserver(mActivity, new Listener() {
                                @Override
                                public void onSuccess(int ordinal, final HashMap<String, String> param) {
                                    forceLoginCheckSuccess(ordinal, param);
                                    dismissDialog(mCustomDialog);
                                    DzSchedulers.execute(new Runnable() {
                                        @Override
                                        public void run() {
                                            HwLog.recharge(amountNum, productNum, giveNum);
                                        }
                                    });
                                }
                                @Override
                                public void onFail(HashMap<String, String> parm) {
                                    if (parm == null) { return; }
                                    ToastAlone.showShort(RechargeMsgUtils.getRechargeMsg(parm));
                                    dismissDialog(mCustomDialog);
                                    forceLoginCheckFail(parm);
                                }
                                @Override
                                public void onStatusChange(int status, Map<String, String> parm) {
                                    HwRequestLib.flog("RechargeListPresenter --->RechargeObserver方法回调 -->onStatusChange  status:" + status);
                                    String statusChangeMsg = parm.get(RechargeMsgResult.STATUS_CHANGE_MSG);
                                    mCustomDialog.setShowMsg(statusChangeMsg);
                                }
                                @Override
                                public void onRechargeStatus(int status, Map<String, String> parm) {
                                    HwRequestLib.flog("RechargeListPresenter --->RechargeObserver方法回调 -->onRechargeStatus  status:" + status);
                                    dealOnRechargeStatus(mCustomDialog, status, parm);
                                }
                            }, RechargeAction.RECHARGE);
                            UtilRecharge manager = UtilRecharge.getDefault();
                            manager.execute(mActivity, params, RechargeAction.RECHARGE.ordinal(), rechargeOrder);
                        }
                    }
                });
            }
        });
    }

    private void forceLoginCheckSuccess(int ordinal, final HashMap<String, String> param) {
        //充值成功
        SpUtil.getinstance(mActivity).setSuccessRechargeTimes();
        try {
            setUserRemain(param);
        } catch (Exception ignored) {
        }

        //打点充值成功
        dzLogRechargeResult(LogConstants.RECHARGE_RESULT_KEY_VALUE_1, param);
    }

    private void forceLoginCheckFail(HashMap<String, String> parm) {
        //如果订单号不为空 则一定是下单成功后 三方充值失败
        String orderId = parm.get(RechargeMsgResult.RECHARGE_ORDER_NUM);
        if (!TextUtils.isEmpty(orderId) && !TextUtils.isEmpty(parm.get(RechargeMsgResult.RECHARGE_STATUS)) && !TextUtils.equals(parm.get(RechargeMsgResult.RECHARGE_STATUS), "6")) {
            dzLogRechargeResult(LogConstants.RECHARGE_RESULT_KEY_VALUE_3, parm);
        }
    }

    /**
     * 打点 充值结果
     *
     * @param result
     * @param params
     */
    private void dzLogRechargeResult(String result, Map<String, String> params) {
        try {
            if (params == null) {
                return;
            }

            String czCode = getEmptyString(params.get(RechargeMsgResult.ERR_CODE));
            String orderId = getEmptyString(params.get(RechargeMsgResult.RECHARGE_ORDER_NUM));
            String desc = getEmptyString(params.get(RechargeMsgResult.ERR_DES) + ":" + params.get(RechargeMsgResult.MORE_DESC));
            String bookId = "";

            HashMap<String, String> map = new HashMap<String, String>();
            map.put(LogConstants.KEY_RECHARGE_RESULT_CZTYPE, "");
            map.put(LogConstants.KEY_RECHARGE_RESULT_RESULT, result);
            map.put(LogConstants.KEY_RECHARGE_RESULT_CZCODE, czCode);
            map.put(LogConstants.KEY_RECHARGE_RESULT_ORDERID, orderId);
            map.put(LogConstants.KEY_RECHARGE_RESULT_DESC, desc);
            map.put(LogConstants.KEY_RECHARGE_BID, bookId);
            map.put(LogConstants.KEY_EXT, LogConstants.RECHARGE_SOURCE_FROM_VALUE_7);
            DzLog.getInstance().logEvent(LogConstants.EVENT_CZJG, map, "");

        } catch (Exception e) {
            ALog.printStackTrace(e);
        }
    }

    /**
     * 获取字符串
     *
     * @param str str
     * @return str
     */
    public String getEmptyString(String str) {
        return !TextUtils.isEmpty(str) ? str : "";
    }

    private void dealOnRechargeStatus(DialogLoading mCustomDialog, int status, Map<String, String> parm) {
        ALog.dZz(TAG + " onRechargeStatus status:" + status);
        if (parm == null) {
            return;
        }
        String msg = "";
        switch (status) {
            //开始下单
            case RechargeConstants.START_MAKE_ORDER:
                msg = "开始下单";
                break;
            //下单失败
            case RechargeConstants.MAKE_ORDER_FAIL:
                dzLogRechargeResult(LogConstants.RECHARGE_RESULT_KEY_VALUE_2, parm);
                dismissDialog(mCustomDialog);
                msg = "下单失败";
                break;
            //发起充值
            case RechargeConstants.START_RECHARGE:
                msg = "发起充值";
                break;
            //订单通知开始
            case RechargeConstants.ORDER_NOTIFY_START:
                msg = "订单通知开始";
                break;
            //订单通知成功
            case RechargeConstants.ORDER_NOTIFY_SUCCESS:
                msg = "订单通知成功";
                dismissDialog(mCustomDialog);
                break;
            //订单通知失败
            case RechargeConstants.ORDER_NOTIFY_FAIL:
                dzLogRechargeResult(LogConstants.RECHARGE_RESULT_KEY_VALUE_4, parm);
                dismissDialog(mCustomDialog);
                msg = "订单通知失败";
                break;
            case RechargeConstants.ORDER_CORE_DESTROYED:
                dismissDialog(mCustomDialog);
                msg = "异常结束";
                break;
            default:
                break;
        }
        ALog.dZz("onRechargeStatus:" + msg);
    }

    private void dismissDialog(DialogLoading mCustomDialog) {
        if (mCustomDialog != null && mCustomDialog.isShowing()) {
            mCustomDialog.dismiss();
        }
    }

    /**
     * 设置用户信息。
     *
     * @param param
     */
    private void setUserRemain(Map<String, String> param) {
        try {
            if (param != null) {
                String resultJson = param.get(RechargeMsgResult.RECHARGE_RESULT_JSON);
                OrdersResultBean bean = new OrdersResultBean();
                bean.parseJSON(new JSONObject(resultJson));
                SpUtil sp = SpUtil.getinstance(mActivity);

                if (bean.remain != 0 && bean.vouchers != 0) {
                    sp.setUserRemain(bean.remain + "", bean.unit);
                    sp.setUserVouchers(bean.vouchers + "", bean.vouchersUnit);
                }
            }
        } catch (Exception e) {
            ALog.printStackTrace(e);
        }
    }
}
