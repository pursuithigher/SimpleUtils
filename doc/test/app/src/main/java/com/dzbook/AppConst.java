package com.dzbook;

import android.app.Application;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;

import com.dzbook.imageloader.DataManager;
import com.dzbook.lib.net.HttpListener;
import com.dzbook.lib.net.OkHttpDns;
import com.dzbook.lib.utils.ALog;
import com.dzbook.lib.utils.CompatUtils;
import com.dzbook.lib.utils.SDCardUtil;
import com.dzbook.loader.BookLoader;
import com.dzbook.log.DzLog;
import com.dzbook.log.LogConstants;
import com.dzbook.net.hw.RequestCall;
import com.dzbook.push.HwPushApiHelper;
import com.dzbook.utils.ClipboardUtils;
import com.dzbook.utils.DeviceInfoUtils;
import com.dzbook.utils.FileUtils;
import com.dzbook.utils.NewDownloadManagerUtils;
import com.dzbook.utils.SpUtil;
import com.dzbook.utils.ThirdPartyLog;
import com.ishugui.R;

import java.io.File;

/**
 * 应用常量
 *
 * @author ZhengLK 2015-05-21
 */
public class AppConst {

    /**
     * p版本参数
     * 起始值：10
     */
    public static final int VERSION_P = 10;

    /***************打包定价********************/

    /**
     * 已经购买了
     */
    public static final String PACK_STATUS_BUY_SUCCESS = "1";
    /**
     * 支付成功
     */
    public static final String PACK_STATUS_PAY_SUCCESS = "2";
    /**
     * 去支付
     */
    public static final String PACK_STATUS_TO_PAY = "3";
    /**
     * 购买失败
     */
    public static final String PACK_STATUS_E = "4";
    /**
     * 领取成功
     */
    public static final String PACK_STATUS_LING_SUCCESS = "5";
    /**
     * 领取失败
     */
    public static final String PACK_STATUS_LING_E = "6";
    /**
     * 活动过期
     */
    public static final String PACK_STATUS_OVER_E = "7";
    /**
     * 充值
     */
    public static final String PACK_STATUS_RECHARGE_SUCCESS = "200";
    /***************打包定价********************/


    /**
     * 域标识
     * 1：HUAWEI
     */
    public static final int DOMAIN = 1;

    /**
     * 去阅读
     */
    public static final int GO_READER = 1;
    /**
     * 智能判断模式
     */
    public static final int GO_SMART = 2;
    /**
     * 去图书详情
     */
    public static final int GO_BOOKDETAIL = 3;
    /**
     * 去web
     */
    public static final int GO_WEB = 4;
    /**
     * 从默认位置来
     */
    public static final int FROM_DEFAULT = 7;
    /**
     * 微信分享回调监听广播action
     */
    public static final String WECHATE_CALL_BACK_STATE_ACTION = "wechate.call.back.state.broadcast";

    /**
     * 点击时间间隔最大等待1秒
     */
    public static final int MAX_CLICK_INTERVAL_TIME = 1300;

    private static volatile Context app;
    private static volatile Application application;
    private static volatile int launchMode = LogConstants.LAUNCH_DIRECT;

    private static volatile boolean isMainActivityActive = false;

    /**
     * 监听语言的变化
     */
    private static volatile String locale = "";


    public static Context getApp() {
        return app;
    }


    public static void setApplication(Application application) {
        AppConst.application = application;
    }

    public static void setApp(Context app) {
        AppConst.app = app;
    }

    public static boolean isIsMainActivityActive() {
        return isMainActivityActive;
    }

    public static void setIsMainActivityActive(boolean isMainActivityActive) {
        AppConst.isMainActivityActive = isMainActivityActive;
    }

    public static String getLocale() {
        return locale;
    }

    public static void setLocale(String locale) {
        AppConst.locale = locale;
    }

    /**
     * 初始化app
     */
    public static void initApp() {
        NewDownloadManagerUtils.getInstanse().initDownConnect(application, getApp());
        DataManager.init(getApp());
        BookLoader.init(getApp());
        ClipboardUtils.getInstanse().init(getApp());
        HwPushApiHelper.getInstance().initHwApiClient(getApp());
        //这里先初始化，为了后面启动加速
        SDCardUtil.getInstance().isSDCardAvailable();
        long dur = System.currentTimeMillis() - SpUtil.getinstance(getApp()).getUncaughtExceptionTime();
        if (dur < 5000) {
            ALog.dLk("AppContext exception dur time = " + dur + " ms");
            DzLog.getInstance().readySession("[dz-e]");
        } else {
            DzLog.getInstance().readySession("[dz]");
        }

        if (DeviceInfoUtils.getInstanse().isMainProcess()) {
//            setMainAppContext(this);

            SpUtil spUtil = SpUtil.getinstance(getApp());
            spUtil.setAppConunter();// 应用启动次数计数器
            spUtil.initInstallHour();// 初始化安装时间
            //获取启动-模式
            spUtil.markOpenApp();
            // 打点初始化。


            // 测试网值初始化。
            RequestCall.testUrlInit(getApp());
        }
        Resources resources = app.getResources();
        if (resources != null) {
            Configuration configuration = resources.getConfiguration();
            if (configuration != null) {
                AppConst.setLocale(CompatUtils.getLocale(configuration).toString());
            }
        }
    }

    /**
     * 初始化 http dns
     */
    public static void initHttpDns() {
        if (!OkHttpDns.isIsInit()) {
            // 参考：https://doc.open.alipay.com/docs/doc.htm?spm=a219a.7386797.0.0.deaZAE&source=search&treeId=193&articleId=104509&docType=1
            OkHttpDns.init(getApp(), new HttpListener() {
                @Override
                public void onDnsPrepare(String hostname, String ip) {
                    ALog.cmtDebug("hostname:" + hostname + ";  ip:" + ip);
                }
            });
            SpUtil spUtil1 = SpUtil.getinstance(getApp());
            long resolveTime = spUtil1.getLong(SpUtil.HTTPDNS_PRE_RESOLVE_TIME, 0);
            if (System.currentTimeMillis() - resolveTime > 2 * 60 * 60 * 1000) {
                OkHttpDns.getInstance().setPreResolve();//预制域名预解析
                spUtil1.setLong(SpUtil.HTTPDNS_PRE_RESOLVE_TIME, System.currentTimeMillis());
            }
        }
    }


    /**
     * 获取应用名称
     *
     * @param context context
     * @return name
     */
    public static String getAppName(Context context) {
        return context.getResources().getString(R.string.app_name);
    }

    /**
     * 获取Glide缓存目录
     *
     * @return file
     */
    public static File getGlideCacheFile() {
        return new File(SDCardUtil.getInstance().getSDCardAndroidRootDir() + File.separator + FileUtils.APP_BOOK_IMAGE_GLIDE_CACHE);
    }

    public static int getLaunchMode() {
        return launchMode;
    }

    public static void setLaunchMode(int launchMode) {
        AppConst.launchMode = launchMode;
    }
}
