package com.dzbook.utils;

import android.content.res.AssetManager;
import android.text.TextUtils;

import com.dzbook.AppConst;
import com.dzbook.lib.utils.ALog;
import com.ishugui.BuildConfig;

import java.io.IOException;
import java.util.HashMap;

/**
 * 打包控制工具类
 *
 * @author dongdianzhou on 2017/8/30.
 * 打包器控制的字段
 */

public class PackageControlUtils {
    private static final String TAG = "PackageControlUtils";
    private static HashMap<String, String> mapCfg = new HashMap<>();

    /**
     * 是否打开alog：0：不打开 1：打开
     */
    private static final String OPEN_ALOG = BuildConfig.openAlog;

    public static boolean isDriectOpenAlog() {
        return !TextUtils.isEmpty(OPEN_ALOG) && OPEN_ALOG.equals("1");
    }

    /**
     * 初始化assets相关的配置信息
     */
    private static void tryInit() {
        synchronized (PackageControlUtils.class) {
            try {
                AssetManager assetManager = AppConst.getApp().getAssets();
                String[] list = assetManager.list("dz_config");
                if (null != list && list.length > 0) {
                    ALog.dLk(TAG + ", 配置信息 (" + list.length + ")");
                    HashMap<String, String> map = new HashMap<>();
                    for (String name : list) {
                        if (!TextUtils.isEmpty(name)) {
                            String body = FileUtils.getStringByAssetManager(assetManager, "dz_config/" + name);
                            map.put(name, body);
                            ALog.dLk(TAG + ", " + name + " = " + body);
                        }
                    }
                    mapCfg = map;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static String getCfg(String key) {
        if (mapCfg.isEmpty() && null != AppConst.getApp()) {
            tryInit();
        }
        String v = mapCfg.get(key);
        return null == v ? "" : v;
    }

    static long appBuildTime() {
        String value = getCfg("app_build_time");
        if (!TextUtils.isEmpty(value)) {
            try {
                return Long.parseLong(value, 10);
            } catch (NumberFormatException e) {
                ALog.printStackTrace(e);
            }
        }
        return 0;
    }

    public static String getAppVersionName() {
        return getCfg("version_name");
    }

    public static String getAppVersionCode() {
        return getCfg("version_code");
    }

    public static String getChannel() {
        return getCfg("channel");
    }

    /**
     * appCode 代表产品线。已有产品线：
     *
     * @return ishugui：主客户端
     * ishuguiSub：单本书
     * i001：ios客户端
     * f001：包月客户端
     * f002：快看阅读
     */
    public static String appCode() {
        return getCfg("app_code");
    }

    /**
     * git版本
     *
     * @return string
     */
    public static String gitCode() {
        return getCfg("git_code");
    }

    static String gitInfo() {
        return getCfg("git_info");
    }

    /**
     * gitTag
     *
     * @return string
     */
    public static String gitTag() {
        return getCfg("git_tag");
    }

    /**
     * 书架模式
     * 1.九宫格
     * 2.列表
     *
     * @return "1"，"2"
     */
    public static String shelfMode() {
        return getCfg("shelf_mode");
    }


    /**
     * 获取 AppCode
     *
     * @return string
     */
    public static String getAppCode() {
        SpUtil sp = SpUtil.getinstance(AppConst.getApp());
        String v = sp.getString(SpUtil.DZ_APP_CODE);
        String appCode = appCode();
        if (!TextUtils.isEmpty(v)) {
            if ((!v.equals(appCode)) && sp.isDzAppCode(appCode)) {
                sp.setString(SpUtil.DZ_APP_CODE, appCode);
                return appCode;
            } else {
                return v;
            }
        } else {
            sp.setString(SpUtil.DZ_APP_CODE, appCode);
            return appCode;
        }
    }

    /**
     * 客户端产品
     *
     * @return "ishugui"等
     */
    public static String getApp() {
        String app = "";
        String appCode = getAppCode();
        if ("ishugui".equals(appCode)) {
            //ishugui：主客户端
            app = "1";
        } else if ("ishuguiSub".equals(appCode)) {
            //ishuguiSub：单本书
            app = "2";
        } else if ("f001".equals(appCode)) {
            //f001：包月客户端
            app = "3";
        } else if ("f002".equals(appCode)) {
            //f002：快看阅读
            app = "4";
        }
        return app;
    }
}
