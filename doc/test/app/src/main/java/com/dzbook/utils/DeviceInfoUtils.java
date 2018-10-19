package com.dzbook.utils;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Point;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import com.dzbook.AppConst;
import com.dzbook.AppInfoUtils;
import com.dzbook.activity.SplashActivity;
import com.dzbook.lib.utils.ALog;
import com.dzbook.lib.utils.CompatUtils;
import com.dzbook.utils.utdid.UTDevice;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.Map;

/**
 * 设备信息
 *
 * @author dllik 2013-11-23
 */
public class DeviceInfoUtils {

    private static volatile DeviceInfoUtils instanse;

    private Context mContext;

    private boolean hasSoftKeys;

    private boolean mIsSupportedBade = true;

    private int statusBarHeight = -1;

    private String utdid = "";


    private DeviceInfoUtils() {

    }

    /**
     * 初始化
     *
     * @param context context
     */
    public void init(Context context) {
        mContext = context;
    }


    /**
     * 获取DeviceInfoUtils实例
     *
     * @return 实例
     */
    public static DeviceInfoUtils getInstanse() {
        if (instanse == null) {
            synchronized (DeviceInfoUtils.class) {
                if (instanse == null) {
                    instanse = new DeviceInfoUtils();
                }
            }
        }
        return instanse;
    }


    /**
     * 反射方法
     *
     * @param ownerClass ownerClass
     * @param fieldName  fieldName
     * @return class
     */
    public Object getClassFiled(Class<?> ownerClass, String fieldName) {
        try {
            Field field = ownerClass.getField(fieldName);
            return field.get(ownerClass);
        } catch (Exception e) {
            ALog.printStackTrace(e);
        }
        return null;
    }

    /**
     * 宽度
     *
     * @return 宽度
     */
    public int getWidthReturnInt() {
        if (mContext == null) {
            mContext = AppConst.getApp();
        }
        Point point = CompatUtils.getSize(mContext);
        if (null != point) {
            return point.x;
        }
        return -1;
    }

    /**
     * 高度
     *
     * @return int
     */
    public int getHeightReturnInt() {
        if (mContext == null) {
            mContext = AppConst.getApp();
        }
        Point point = CompatUtils.getSize(mContext);
        if (null != point) {
            return point.y;
        }
        return -1;
    }

    /**
     * 品牌
     *
     * @return 品牌
     */
    public String getBrand() {
        return Build.BRAND;
    }

    /**
     * 型号
     *
     * @return 型号
     */
    public String getModel() {
        return Build.MODEL;
    }


    /**
     * 日志打点回传用的自定义 ua 参数
     *
     * @return ua
     */
    public String getDzLogUA() {
        return "android" + Build.VERSION.SDK_INT + ";" + Build.BRAND + ";" + Build.MODEL;
    }

    /**
     * OS版本
     *
     * @return 版本号
     */
    public String getOsVersion() {
        try {
            return "android" + Build.VERSION.SDK_INT;
        } catch (Exception e) {
            return "";
        }

    }

    /**
     * OS版本
     *
     * @return 版本数
     */
    public int getOsVersionInt() {
        try {
            return Build.VERSION.SDK_INT;
        } catch (Exception e) {
            return -1;
        }
    }

    /**
     * 杀掉进程
     */
    public void killAllProcess() {
        if (mContext == null) {
            mContext = AppConst.getApp();
        }

        try {
            final ActivityManager am = (ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE);
            if (am == null) {
                return;
            }
            // NOTE: getRunningAppProcess() ONLY GIVE YOU THE PROCESS OF YOUR OWN PACKAGE IN ANDROID M
            // BUT THAT'S ENOUGH HERE
            for (ActivityManager.RunningAppProcessInfo ai : am.getRunningAppProcesses()) {
                //            // KILL OTHER PROCESS OF MINE
                //            if (ai.uid == android.os.Process.myUid() && ai.pid != android.os.Process.myPid()) {
                //
                //            }

                android.os.Process.killProcess(ai.pid);
            }
        } catch (Exception e) {
            ALog.printExceptionWz(e);
        }

    }

    /**
     * 获取 Channel
     *
     * @return Channel
     */
    public String getChannel() {
        try {
            SpUtil sp = SpUtil.getinstance(mContext);
            String v = sp.getString(SpUtil.DZ_APP_CHANNEL);
            if (!TextUtils.isEmpty(v)) {
                return v;
            } else {
                String channel = PackageControlUtils.getChannel();
                if (TextUtils.isEmpty(channel)) {
                    channel = AppInfoUtils.getChannel();
                    if (TextUtils.isEmpty(channel)) {
                        // 默认未知渠道
                        channel = "HW1000001";
                    }
                }
                sp.setString(SpUtil.DZ_APP_CHANNEL, channel);
                return channel;
            }
        } catch (Exception e) {
            ALog.printStackTrace(e);
        }
        return "HW1000001";
    }

    /**
     * 获取manifest中meta-data的值
     *
     * @param name 对应meta-data的android:name属性
     * @return meta-data的值
     */

    public String getMetaDataValue(String name) {
        String value = null;
        try {
            if (mContext == null) {
                mContext = AppConst.getApp();
            }

            ApplicationInfo appInfo = mContext.getPackageManager().getApplicationInfo(mContext.getPackageName(), PackageManager.GET_META_DATA);
            value = appInfo.metaData.getString(name);
        } catch (Exception e) {
            ALog.printStackTrace(e);
        }
        return value;
    }

    /**
     * 获取manifest中meta-data的值
     * getInt方式
     *
     * @param name 对应meta-data的android:name属性
     * @return meta-data的值
     */

    public String getMetaDataValueInt(String name) {
        String value = "";
        try {
            if (mContext == null) {
                mContext = AppConst.getApp();
            }

            ApplicationInfo appInfo = mContext.getPackageManager().getApplicationInfo(mContext.getPackageName(), PackageManager.GET_META_DATA);
            value = appInfo.metaData.getInt(name) + "";
        } catch (PackageManager.NameNotFoundException e) {
            ALog.printStackTrace(e);
        }
        return value;
    }

    /**
     * 获取 PackName
     *
     * @return getPackName
     */
    public String getPackName() {
        if (mContext == null) {
            mContext = AppConst.getApp();
        }
        try {
            return mContext.getPackageName();
        } catch (Exception e) {
            ALog.printStackTrace(e);
        }
        return null;
    }

    /**
     * 获取当前的process进程。
     *
     * @return 当前进程名
     */
    private String getCurProcessName() {
        try {
            int pid = android.os.Process.myPid();
            ActivityManager mActivityManager = (ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE);
            for (ActivityManager.RunningAppProcessInfo appProcess : mActivityManager.getRunningAppProcesses()) {
                if (appProcess.pid == pid) {
                    return appProcess.processName;
                }
            }
        } catch (Exception e) {
            ALog.printExceptionWz(e);
        }
        return null;
    }

    /**
     * 当前是否在主进程。
     *
     * @return true是主进程，否则不是
     */
    public boolean isMainProcess() {
        try {
            return TextUtils.equals(getPackName(), getCurProcessName());
        } catch (Exception ignore) {
        }
        return false;
    }

    /**
     * 获取StatusBar高度
     *
     * @return StatusBar高度
     */
    public int getStatusBarHeight2() {
        int result = 0;
        int resourceId = mContext.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = mContext.getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    /**
     * 获取StatusBar高度
     *
     * @return StatusBar高度
     */
    public int getStatusBarHeight() {
        try {
            if (statusBarHeight != -1) {
                return statusBarHeight;
            }
            Class<?> c = Class.forName("com.android.internal.R$dimen");
            Object obj = c.newInstance();
            Field field = c.getField("status_bar_height");
            int x = Integer.parseInt(field.get(obj).toString());
            int height = mContext.getResources().getDimensionPixelSize(x);
            ALog.dWz("getStatusBarHeight:" + height);
            if (height <= 0) {
                height = getStatusBarHeight2();
            }
            statusBarHeight = height;
            return height;
        } catch (Exception e) {
            ALog.printStackTrace(e);
        }
        return 0;
    }

    /**
     * 获取手机的（CPU type + ABI convention）信息
     *
     * @return CPU信息
     */
    public String getCpuAbi() {
        if (Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            try {
                String[] list = Build.SUPPORTED_ABIS;
                if (list.length > 0) {
                    StringBuffer result = new StringBuffer().append("{");
                    for (String abi : list) {
                        result = result.append(abi).append(", ");
                    }
                    return result.substring(0, result.length() - 2) + "}";
                }
            } catch (Exception ignore) {
            }
        }
        return "{" + Build.CPU_ABI + ", " + Build.CPU_ABI2 + "}";
    }

    /**
     * 判断栈顶的activity
     *
     * @return 栈顶的activity
     * @throws Exception
     */
    @SuppressWarnings("unchecked")
    public Activity getCurrentActivity() {
        if (mContext == null) {
            mContext = AppConst.getApp();
        }
        Activity activity = null;
        try {
            Class<?> clz;
            try {
                clz = Class.forName("android.globalContext.ActivityThread");
            } catch (ClassNotFoundException e) {
                clz = Class.forName("android.app.ActivityThread");
            }
            Object currentActivityThread = clz.getMethod("currentActivityThread").invoke(null);
            Field f = clz.getDeclaredField("mActivities");
            f.setAccessible(true);
            Map map = (Map) f.get(currentActivityThread);
            Iterator it = map.entrySet().iterator();
            while (it.hasNext()) {
                Object objMap = it.next();
                if (null != objMap && objMap instanceof Map.Entry) {
                    Map.Entry<Object, Object> entry = (Map.Entry<Object, Object>) objMap;
                    if (null == activity) {
                        Object activityRecord = entry.getValue();
                        Field actField = activityRecord.getClass().getDeclaredField("activity");
                        actField.setAccessible(true);
                        Object obj = actField.get(activityRecord);
                        ActivityManager manager = (ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE);
                        String name = manager.getRunningTasks(1).get(0).topActivity.getClassName();
                        if (obj.toString().contains(name)) {
                            activity = (Activity) obj;
                        }
                    }
                }
            }
        } catch (Exception e) {
            ALog.printStackTrace(e);
        }
        return activity;
    }

    /**
     * 判断是不是魅族的机器 魅族以前的老机器 smartbar有问题
     *
     * @return true是魅族机器，false不是
     */
    public boolean isMeizuSmartBar() {
        String brand = getBrand();
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP && !TextUtils.isEmpty(brand) && "meizu".equals(brand.toLowerCase())) {
            return true;
        }
        return findActionBarTabsShowAtBottom();
    }

    /**
     * 是否显示ActionBar
     *
     * @return true显示，false不显示
     */
    public boolean findActionBarTabsShowAtBottom() {
        String brand = getBrand();
        if (!TextUtils.isEmpty(brand) && "meizu".equals(brand.toLowerCase()) && hasSmartBar()) {
            try {
                Class.forName("android.app.ActionBar").getMethod("setTabsShowAtBottom", boolean.class);
            } catch (Throwable e) {
                return false;
            }
            return true;
        } else {
            return false;
        }
    }

    /**
     * 是否有SmartBar
     *
     * @return true有，false没有
     */
    private boolean hasSmartBar() {
        try {
            // 新型号可用反射调用Build.hasSmartBar()
            Method method = Class.forName("android.os.Build").getMethod("hasSmartBar");
            return ((Boolean) method.invoke(null)).booleanValue();
        } catch (Throwable e) {
        }
        // 反射不到Build.hasSmartBar()，则用Build.DEVICE判断
        if ("mx2".equals(Build.DEVICE)) {
            return true;
        } else if ("mx".equals(Build.DEVICE) || "m9".equals(Build.DEVICE)) {
            return false;
        }

        return false;
    }

    /**
     * 返回actionbar的高度  如果反射获取不到的话 就返回status
     *
     * @return actionbar的高度
     */
    public int geActionBarHeight() {
        try {
            if (mContext == null) {
                mContext = AppConst.getApp();
            }
            TypedValue tv = new TypedValue();
            if (mContext.getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true)) {
                return TypedValue.complexToDimensionPixelSize(tv.data, mContext.getResources().getDisplayMetrics());
            }
        } catch (Exception e) {
        }
        return getStatusBarHeight();
    }

    /**
     * 对魅族的smartbar适配。。。至于为啥RelativeLayout要设置FrameLayout的参数
     * 我也不知道 反正不这样写报错
     *
     * @param layoutRoot layoutRoot
     */
    public void setDialogMargin(RelativeLayout layoutRoot) {
        if (isMeizuSmartBar() && layoutRoot.getLayoutParams() instanceof FrameLayout.LayoutParams) {
            FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) layoutRoot.getLayoutParams();
            layoutParams.setMargins(0, 0, 0, geActionBarHeight());
            layoutRoot.setLayoutParams(layoutParams);
        }
    }

    /**
     * 获取屏幕像素
     *
     * @return 像素信息
     */
    public String getPixels() {
        if (mContext == null) {
            mContext = AppConst.getApp();
        }
        try {
            DisplayMetrics displayMetrics = new DisplayMetrics();
            WindowManager mWindowManager = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
            mWindowManager.getDefaultDisplay().getMetrics(displayMetrics);
            int screenWidth = displayMetrics.widthPixels;
            int screenHeight = displayMetrics.heightPixels;
            return screenWidth + "_" + screenHeight;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * 获取UTDID
     *
     * @return UTDID
     */
    public String getUtdId() {
        if (!TextUtils.isEmpty(utdid)) {
            return utdid;
        }
        utdid = UTDevice.getUtdid();
        return utdid == null ? "" : utdid;
    }

    /**
     * 获取华为的utdId
     *
     * @return UTDID
     */
    public String getHwUtdId() {
        return getUtdId();
    }

    /**
     * 获取是否存在NavigationBar
     *
     * @param context context
     * @return true存在NavigationBar，反之不存在
     */
    public boolean checkDeviceHasNavigationBar(Context context) {
        if (hasSoftKeys) {
            return true;
        }

        boolean hasNavigationBar = false;
        try {
            Resources rs = context.getResources();
            int id = rs.getIdentifier("config_showNavigationBar", "bool", "android");
            if (id > 0) {
                hasNavigationBar = rs.getBoolean(id);
            }
            Class<?> systemPropertiesClass = Class.forName("android.os.SystemProperties");
            Method m = systemPropertiesClass.getMethod("get", String.class);
            String navBarOverride = (String) m.invoke(systemPropertiesClass, "qemu.hw.mainkeys");
            if ("1".equals(navBarOverride)) {
                hasNavigationBar = false;
            } else if ("0".equals(navBarOverride)) {
                hasNavigationBar = true;
            }
        } catch (Throwable e) {
            ALog.printExceptionWz(e);
        }
        hasSoftKeys = hasNavigationBar;
        return hasNavigationBar;
    }

    /**
     * 设置 桌面角标
     *
     * @param num 数字
     */
    public void setBadgeNum(int num) {
        if (!mIsSupportedBade) {
            ALog.dWz("setBadgeNum error not support badgeNum");
            return;
        }
        try {
            if (num % 2 == 0) {
                num = 0;
            }
            Bundle bundle = new Bundle();
            bundle.putString("package", AppConst.getApp().getPackageName());
            bundle.putString("class", SplashActivity.class.getCanonicalName());
            bundle.putInt("badgenumber", num);
            AppConst.getApp().getContentResolver().call(Uri.parse("content://com.huawei.android.launcher.settings/badge/"), "change_badge", null, bundle);
        } catch (Exception e) {
            mIsSupportedBade = false;
            ALog.printExceptionWz(e);
        }
    }

    /**
     * IMEI
     *
     * @return IMEI
     **/
    public String getImei() {
        try {
            TelephonyManager tm = (TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE);
            return tm.getDeviceId();

        } catch (Exception e) {
            return "";
        }
    }

}
