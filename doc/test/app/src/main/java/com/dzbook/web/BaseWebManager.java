package com.dzbook.web;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.text.TextUtils;
import android.view.ViewGroup;
import android.view.accessibility.AccessibilityManager;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;

import com.dzbook.AppConst;
import com.dzbook.event.EventBusUtils;
import com.dzbook.lib.utils.ALog;
import com.dzbook.log.LogConstants;
import com.iss.app.BaseActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Iterator;

/**
 * BaseWebManager
 *
 * @author zhenglk 18/04/20.
 */
abstract class BaseWebManager {
    private static final String TAG = "BaseWebManager";
    protected double mPercent = -10;
    WebView mWebView;
    BaseActivity mActivity;
    private long lastDetailTime = 0;

    BaseWebManager(final BaseActivity activity, WebView webView) {
        this.mActivity = activity;
        this.mWebView = webView;
        disableAccessibility(activity);
    }

    /**
     * 用于RSA加密
     *
     * @param json
     * @return
     */
    @JavascriptInterface
    abstract String init(String json);

    /**
     * 日志打点，click日志
     *
     * @param module
     * @param zone
     * @param adid
     * @param trackId
     * @param json
     */
    @JavascriptInterface
    abstract void logClick(@LogConstants.Module String module, @LogConstants.Zone String zone, String adid, String trackId, String json);

    /**
     * columeID 栏目ID
     * columeName 栏目名称
     * columePos  栏目位置
     * columeTemp  栏目类型
     * <p>
     * contentID  内容ID
     * contentName 内容名称
     * contentType 内容类型
     */
    abstract void hwLogClick(String columeID, String columeName, String columePos, String columeTemp, String contentID, String contentName, String contentType);

    /**
     * 日志打点，event打点
     *
     * @param event
     * @param trackId
     * @param json
     */
    @JavascriptInterface
    abstract void logEvent(String event, String trackId, String json);

    /**
     * 日志打点，pv打点
     * add  2017-12-12 10:49:01
     *
     * @param name
     * @param trackId
     * @param json
     */
    @JavascriptInterface
    abstract void logPv(String name, String trackId, String json);


    /**
     * 打开图书
     *
     * @param json json数据{bookId, mode}
     *             mode:打开模式<br>
     *             #1，阅读优先<br>
     *             #2，图书详情<br>
     *             #3，阅读<br>
     */
    @JavascriptInterface
    abstract void openBook(String json);

    /**
     * 应用内部打开url
     *
     * @param json json数据 {url, title, type}
     */
    @JavascriptInterface
    abstract void openUrl(String json);

    //    /**
    //     * 应用外部打开url
    //     *
    //     * @param json json数据 {url}
    //     */
    //    @JavascriptInterface
    //    abstract void openExt(String json);

    /**
     * VIP订购
     *
     * @param json json数据 {mode}
     */
    @JavascriptInterface
    abstract void vipOrder(String json);

    /**
     * 去充值
     *
     * @param json json数据 {sourceWhere}
     */
    @JavascriptInterface
    abstract void toRecharge(String json);

    /**
     * 通知更新账户信息
     *
     * @param json json数据 {}
     */
    @JavascriptInterface
    abstract void refreshUserInfo(String json);

    /**
     * 更新一个当天的标识（key 如：更新当天的签到状态）
     *
     * @param json json数据 {key}
     */
    @JavascriptInterface
    abstract void setTodayFlag(String json);

    /**
     * 主动关闭当前页面
     *
     * @param json json数据 {}
     */
    @JavascriptInterface
    abstract void closeCurView(String json);

    /**
     * 分享（弹出分享组合）
     *
     * @param json json数据 {
     *             // 微信朋友圈
     *             "wxPyq": {"url:":"分享url", "title":"分享title", "des":"分享描述", "img":"分享图片url地址" },
     *             //微信好友
     *             "wxHy": {"url:":"分享url", "title":"分享title", "des":"分享描述", "img":"分享图片url地址" },
     *             //QQ空间
     *             "qqKj": {"url:":"分享url", "title":"分享title", "des":"分享描述", "img":"分享图片url地址" },
     *             //QQ好友
     *             "qqHy": {"url:":"分享url", "title":"分享title", "des":"分享描述", "img":"分享图片url地址" }
     *             }
     */
    @JavascriptInterface
    abstract void shareGroup(String json);

    /**
     * 分享（指定分享类型）
     *
     * @param json json数据 {}
     */
    @JavascriptInterface
    abstract void shareItem(String json);

    /**
     * 大图分享（2018-04-21支持朋友圈）
     *
     * @param json json数据 {}
     */
    @JavascriptInterface
    abstract void toShareWxWithBigImage(String json);

    /**
     * 去登录，登录成功以后，跳转到一个H5页面
     *
     * @param json json数据 {url}
     */
    @JavascriptInterface
    abstract void toLoginWitCallback(String json);

    /**
     * Toast
     *
     * @param json json数据 {msg}
     */
    @JavascriptInterface
    abstract void toast(String json);

    /**
     * H5领取图书（限免，打包定价。。。）
     *
     * @param json json数据 {action,commodity_id,bookIds}
     *             "action":字典action
     *             action
     *             01   打包订购一键购
     *             02   打包订购组合购
     *             03   限免领取书籍 //add by caimt 2017-10-23 13:48:55
     *             04   优惠购//add by caimt 2017-10-23 13:48:55
     *             05   立即领取添加到书架//add by caimt 2017-10-23 13:48:55
     *             "commodity_id":"1"//商品id，
     *             "bookIds":""//书籍id组,数组
     */
    @JavascriptInterface
    abstract void giveBook(String json);

    /**
     * 检查更新（调用华为的升级sdk）
     *
     * @param json json数据 {}
     */
    @JavascriptInterface
    abstract void hwUpdateApp(String json);

    /**
     * 书城通用二级列表页面
     *
     * @param json json数据 {id,title}
     */
    @JavascriptInterface
    abstract void openBookList(String json);

    /**
     * 进入客户端的主tab页面
     *
     * @param json {tabName}
     */
    @JavascriptInterface
    abstract void toMainTab(String json);

    /**
     * 签到成功回调修改状态
     */
    @JavascriptInterface
    abstract void signInSuccess(String award, String type);

    /**
     * 补签成功
     */
    abstract void supplemenstSuccess(String award, String type);

    /**
     * js请求签名交互
     *
     * @param data postBody参数
     * @param type 签名类型1：查询类 2：操作类
     */
    @JavascriptInterface
    abstract String addSignHeader(String data, String type);

    @JavascriptInterface
    public abstract void getPercent(String json);

    @JavascriptInterface
    public abstract void onEventValue(String json);

    @JavascriptInterface
    public abstract void openVip(String json);

    /**
     * vip 书城
     *
     * @param json
     */
    @JavascriptInterface
    public abstract void openVipStore(String json);

    @JavascriptInterface
    public abstract void openSecondType(String json);

    /**
     * 打包定价
     *
     * @param json
     */
    public abstract void buyPackBook(String json);


    /**
     * 关闭辅助功能，针对4.2.1和4.2.2 崩溃问题
     * java.lang.NullPointerException
     * at android.webkit.AccessibilityInjector$TextToSpeechWrapper$1.onInit(AccessibilityInjector.java:753)
     * ... ...
     * at android.webkit.CallbackProxy.handleMessage(CallbackProxy.java:321)
     */
    void disableAccessibility(Context context) {
        if (Build.VERSION.SDK_INT == 17) {
            if (context != null) {
                try {
                    AccessibilityManager am = (AccessibilityManager) context.getSystemService(Context.ACCESSIBILITY_SERVICE);
                    if (!am.isEnabled()) {
                        return;
                    }
                    Method set = am.getClass().getDeclaredMethod("setState", int.class);
                    set.setAccessible(true);
                    set.invoke(am, 0);
                } catch (Exception e) {
                    ALog.d(TAG, ALog.getStackTraceString(e));
                }
            }
        }
    }

    HashMap<String, String> jsonToHashMap(String json) {
        if (TextUtils.isEmpty(json)) {
            return null;
        }
        try {
            return jsonToHashMap(new JSONObject(json));
        } catch (JSONException e) {
            ALog.cmtDebug("JSONException:" + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    private HashMap<String, String> jsonToHashMap(JSONObject jsonObject) {
        if (null == jsonObject) {
            return null;
        }
        Iterator<String> iterator = jsonObject.keys();
        if (iterator != null) {
            HashMap<String, String> map = new HashMap<>();
            while (iterator.hasNext()) {
                String key = iterator.next();
                map.put(key, jsonObject.optString(key));
            }
            return map;
        }
        return null;
    }


    JSONObject optJSONObject(String json) {
        if (!TextUtils.isEmpty(json) && json.contains("{")) {
            try {
                return new JSONObject(json);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    boolean tooFast() {
        long thisTime = System.currentTimeMillis();
        if (thisTime - lastDetailTime > AppConst.MAX_CLICK_INTERVAL_TIME) {
            lastDetailTime = thisTime;
            return false;
        } else {
            return true;
        }
    }

    /**
     * 初始化 webSetting + Javascript
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @SuppressLint({"JavascriptInterface"})
    public void init() {
        initJsBridge();
    }


    public double getCurPercent() {
        return mPercent;
    }

    @SuppressLint("AddJavascriptInterface")
    public void initJsBridge() {
        mWebView.addJavascriptInterface(this, "bookStore");
    }

    /**
     * 相关的内容销毁
     */
    public void destroy() {
        EventBusUtils.unregister(this);
        if (mWebView != null) {
            try {
                mWebView.loadUrl("about:blank");
                ViewGroup parent = (ViewGroup) mWebView.getParent();
                if (parent != null) {
                    parent.removeView(mWebView);
                }
                mWebView.clearCache(true);
                mWebView.removeAllViews();
                mWebView.destroy();
                mWebView = null;
            } catch (Exception e) {
                ALog.printStackTrace(e);
            }


        }
    }

    public void pause() {
        if (mWebView != null) {
            mWebView.onPause();
        }
    }

    public void resume() {
        if (mWebView != null) {
            mWebView.onResume();
        }
    }

    public abstract void setChannelInfo(String channelId, String channelPos, String channelTitle);

    /**
     * 分类(二级：玄幻。Vip。言情)
     */
    @JavascriptInterface
    public abstract void openMainTypeDetail(String json);

    /**
     * 签到页
     */
    @JavascriptInterface
    public abstract void openToSign(String json);

    /**
     * 搜索页
     */
    @JavascriptInterface
    public abstract void openSearch(String json);

    /**
     * 免费专区
     */
    @JavascriptInterface
    public abstract void openLimitFree(String json);

    /**
     * 排行榜
     */
    @JavascriptInterface
    public abstract void openRankTop(String json);

    /**
     * vip书城
     */
    @JavascriptInterface
    public abstract void openVipStoreActivity(String json);

    /**
     * 我的--充值页
     */
    @JavascriptInterface
    public abstract void openRechargeList(String json);

    /**
     * 我的--礼品中心（区分兑换页和我的礼物页）
     */
    @JavascriptInterface
    public abstract void openGiftCenter(String json);

    /**
     * 我的--活动
     */
    @JavascriptInterface
    public abstract void openActivityCenter(String json);

    /**
     * 我的--我的账户
     */
    @JavascriptInterface
    public abstract void openPersonAccount(String json);

    /**
     * 我的--我的阅读时长
     */
    @JavascriptInterface
    public abstract void openMyReadTime(String json);

    /**
     * 我的--我的书评
     */
    @JavascriptInterface
    public abstract void openBookCommentPersonCenter(String json);

    /**
     * 我的--我的云书架
     */
    @JavascriptInterface
    public abstract void openCloudBookShelf(String json);

    /**
     * 我的--问题与反馈
     */
    @JavascriptInterface
    public abstract void openFeedBack(String json);

    /**
     * 我的--充值记录
     */
    @JavascriptInterface
    public abstract void openRechargeRecord(String json);

    /**
     * 我的--消费记录
     */
    @JavascriptInterface
    public abstract void openConsumeBookSum(String json);

    /**
     * 是否已经登录
     */
    @JavascriptInterface
    public abstract boolean isHasLogin(String json);

    /**
     * 去登陆
     *
     * @param json
     * @return
     */
    @JavascriptInterface
    public abstract void toLogin(String json);

    /**
     * 打开书城单独一个频道页
     *
     * @param json
     */
    @JavascriptInterface
    public abstract void openChannelPage(String json);

    /**
     * 指定金额充值
     *
     * @param json
     */
    @JavascriptInterface
    public abstract void toAmountRecharge(String json);
}
