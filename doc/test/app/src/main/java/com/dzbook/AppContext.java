package com.dzbook;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.StrictMode;
import android.support.multidex.MultiDex;

import com.dzbook.activity.detail.BookDetailActivity;
import com.dzbook.activity.hw.JsActionbarClass;
import com.dzbook.activity.hw.JsFinishPageClass;
import com.dzbook.activity.reader.ReaderCatalogActivity;
import com.dzbook.activity.store.CommonTwoLevelActivity;
import com.dzbook.activity.store.LimitFreeTwoLevelActivity;
import com.dzbook.lib.utils.SDCardUtil;
import com.dzbook.utils.DeviceInfoUtils;
import com.dzbook.utils.NetworkUtils;
import com.dzbook.web.ActionEngine;
import com.huawei.common.applog.AppLogApi;
import com.huawei.hwCloudJs.JsClientApi;
import com.huawei.hwCloudJs.service.hms.HmsCoreApi;
import com.ishugui.BuildConfig;

import java.util.ArrayList;
import java.util.HashMap;

import hw.sdk.net.bean.store.BeanTempletsInfo;
import io.reactivex.internal.functions.Functions;
import io.reactivex.plugins.RxJavaPlugins;

//import com.squareup.leakcanary.LeakCanary;

/**
 * 全局的Application
 * <p>###  界面  ###<br>
 * 【欢迎页】{@link com.dzbook.activity.SplashActivity}<br><br>
 * 【引导页】{@link com.dzbook.activity.GuideActivity}<br><br>
 * <p>
 * 【主页面】{@link com.dzbook.activity.Main2Activity}<br><br>
 * 【书架】{@link com.dzbook.fragment.main.MainShelfFragment}<br>
 * 【书城】{@link com.dzbook.fragment.main.MainStoreFragment}<br>
 * 【分类】{@link com.dzbook.fragment.MainTypeContentFragment}<br>
 * 【我的】{@link com.dzbook.fragment.main.MainPersonalFragment}<br>
 * <p>
 * 【导入本地书籍】{@link com.dzbook.activity.UpLoadActivity}<br>
 * 【阅读器】{@link com.dzbook.activity.reader.ReaderActivity}<br>
 * 【阅读器目录】{@link ReaderCatalogActivity}<br>
 * 【写笔记】{@link com.dzbook.activity.reader.ReaderNoteActivity}<br>
 * 【阅读器异常章节】{@link com.dzbook.activity.reader.MissingContentActivity}<br>
 * 【追更推荐】{@link com.dzbook.activity.reader.ChaseRecommendActivity}<br>
 * 【追更推荐更多】{@link com.dzbook.activity.reader.ChaseRecommendMoreActivity}<br>
 * <p>
 * 【书城-二级页面】{@link CommonTwoLevelActivity}<br>
 * 【一级分类页面】{@link com.dzbook.activity.MainTypeActivity}
 * 【二级分类页面】{@link com.dzbook.activity.MainTypeDetailActivity}
 * <p>
 * 【VIP商城】{@link com.dzbook.activity.store.VipStoreActivity}<br>
 * 【限免】{@link com.dzbook.activity.store.LimitFreeActivity}<br>
 * 【跳到限免的具体模块】{@link LimitFreeTwoLevelActivity}
 * 【排行榜】{@link com.dzbook.activity.RankTopActivity}
 * <p>
 * 【书籍详情】{@link BookDetailActivity}<br>
 * 【书籍详情目录】{@link com.dzbook.activity.detail.BookDetailChapterActivity}<br>
 * 【搜索】{@link com.dzbook.activity.search.SearchActivity}<br>
 * 【写书评】{@link com.dzbook.activity.comment.BookCommentSendActivity}<br>
 * 【我的书评详情】{@link com.dzbook.activity.comment.BookCommentItemDetailActivity}<br>
 * <p>
 * 【评论列表】{@link com.dzbook.activity.comment.BookCommentMoreActivity}<br>
 * 【批量订购】{@link com.dzbook.recharge.order.LotOrderPageActivity}<br>
 * 【单章订购】{@link com.dzbook.recharge.order.SingleOrderActivity}<br>
 * <p>
 * 【分享】{@link com.dzbook.activity.ShareActivity}<br>
 * <p>
 * 【礼品】{@link com.dzbook.activity.GiftCenterActivity}<br>
 * 【礼品兑换】{@link com.dzbook.fragment.GiftExchangeFragment}<br>
 * 【礼品列表】{@link com.dzbook.fragment.GiftReceiveFragment}<br>
 * <p>
 * 【活动列表】{@link com.dzbook.activity.ActivityCenterActivity}<br> *
 * <p>
 * 【我的账户】{@link com.dzbook.activity.person.PersonAccountActivity}<br>
 * 【充值】{@link com.dzbook.recharge.ui.RechargeListActivity}<br>
 * 【充值记录】{@link com.dzbook.activity.account.RechargeRecordActivity}<br>
 * 【代金券列表】{@link com.dzbook.activity.account.VouchersListActivity}<br>
 * 【消费记录一级】{@link com.dzbook.activity.account.ConsumeBookSumActivity}<br>
 * 【消费记录二级】{@link com.dzbook.activity.account.ConsumeSecondActivity}<br>
 * 【消费记录三级】{@link com.dzbook.activity.account.ConsumeThirdActivity}<br>
 * <p>
 * 【我的VIP】{@link com.dzbook.activity.vip.MyVipActivity}<br>
 * <p>
 * 【云书架】{@link com.dzbook.activity.person.CloudBookShelfActivity}<br>
 * <p>
 * 【我的书评】{@link com.dzbook.activity.comment.BookCommentPersonCenterActivity}<br>
 * <p>
 * 【设置】{@link com.dzbook.activity.person.PersonSetActivity}<br>
 * 【连续包月状态】{@link com.dzbook.activity.continuous.AutoOrderVipListActivity}<br>
 * 【连续包月】{@link com.dzbook.activity.vip.AutoOrderVipActivity}<br>
 * 【开通包月成功】{@link com.dzbook.activity.vip.VipOpenSuccessActivity}<br>
 * 【自动购买章节】{@link com.dzbook.activity.CancelAutoOrderActivity}<br>
 * 【阅读偏好】{@link com.dzbook.activity.person.PersonReadPrefActivity}<br>
 * 【插件】{@link com.dzbook.activity.person.PersonPluginActivity}<br>
 * 【实名认证】{@link com.dzbook.activity.hw.RealNameAuthActivity}<br>
 * <p>
 * 【关于我们】{@link com.dzbook.activity.AboutActivity}
 * <p>
 * 【H5活动页】{@link com.dzbook.activity.CenterDetailActivity}<br>
 * 【简单网页加载】{@link com.dzbook.activity.hw.PrivacyActivity}<br>
 * <p>
 * </p>
 * <p>
 * <p>###  方法入口  ###<br>
 * 【签到入口】{@link ActionEngine#toSign(Activity)}
 *
 * @author dllik 2013-11-23
 */
public class AppContext extends Application {


    /**
     * 搜索页面的编辑框中的key是每次进页面刷新一次然后就固定不再刷新
     */
    private static int searchShowIndex = 0;

    /**
     * 公共的：用于书城预加载
     */
    private static BeanTempletsInfo beanTempletsInfo;
    private static long shelfBookUpdateRequestTime = 0;

    /**
     * whiteUrlList
     */
    private static ArrayList<String> whiteUrlList;


    public static BeanTempletsInfo getBeanTempletsInfo() {
        return beanTempletsInfo;
    }

    public static void setBeanTempletsInfo(BeanTempletsInfo bean) {
        beanTempletsInfo = bean;
    }

    public static long getShelfBookUpdateRequestTime() {
        return shelfBookUpdateRequestTime;
    }

    public static void setShelfBookUpdateRequestTime(long shelfBookUpdateRequestTime) {
        AppContext.shelfBookUpdateRequestTime = shelfBookUpdateRequestTime;
    }

    public static int getSearchShowIndex() {
        return searchShowIndex;
    }

    public static void setSearchShowIndex(int searchShowIndex) {
        AppContext.searchShowIndex = searchShowIndex;
    }

    public static ArrayList<String> getWhiteUrlList() {
        return whiteUrlList;
    }

    public static void setWhiteUrlList(ArrayList<String> whiteUrlList) {
        AppContext.whiteUrlList = whiteUrlList;
    }


    @Override
    public void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        //you must install multiDex whatever tinker is installed!
        MultiDex.install(base);
    }


    @Override
    public void onCreate() {
        super.onCreate();
        AppConst.setApp(this.getApplicationContext());
        AppConst.setApplication(this);
        SDCardUtil.getInstance().init(getApplicationContext());
        //添加内存泄漏检测LeackCanary（AS 插件）
//        LeakCanary.install(this);
        //2.x一个重要的设计需求就是不能吞下任何的Throwable错误。这里的错误是指那些由于下游流的生命周期走到了尽头或下游流取消了即将发射错误的序列。
        RxJavaPlugins.setErrorHandler(Functions.<Throwable>emptyConsumer());
        initApkUtils();

        /**
         * 启动严格模式 可以检测代码不规范的地方  在logcat里 过滤StrictMode 可以看到。
         */

        if (BuildConfig.DEBUG) {
            StrictMode.ThreadPolicy oldThreadPolicy = StrictMode.getThreadPolicy();
            StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder(oldThreadPolicy)
                    .permitDiskWrites()  // 在原有策略的规则基础上，不监测读写磁盘
                    .build());
            StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder().detectAll().penaltyLog().build());
        }

        AppLogApi.Param param = new AppLogApi.Param().setLogLevel(AppLogApi.LogLevel.VERBOSE);
        AppLogApi.init(AppConst.getApp(), param);
        initAssetsWebJs();

        AppConst.initApp();
    }

    /**
     * 初始化当前的utils
     */
    private void initApkUtils() {
        NetworkUtils.getInstance().init(this);
        DeviceInfoUtils.getInstanse().init(this);
    }

    /**
     * 初始化 JsClient
     */
    private void initAssetsWebJs() {
        JsClientApi.SdkOpt opt = new JsClientApi.SdkOpt.Builder().setShowAuthDlg(false).setBiType(JsClientApi.SdkOpt.BiType.PERMISSION_USEREXP).build();
        JsClientApi.setJSOption(opt);
        JsClientApi.registerJsApi(HmsCoreApi.class);
        JsClientApi.registerActionbarClass(JsActionbarClass.class);

        HashMap<String, Class> jsInterface = new HashMap<>();
        jsInterface.put("HuaweiReader", JsFinishPageClass.class);
        JsClientApi.registerjsInterface(jsInterface);
    }


}
