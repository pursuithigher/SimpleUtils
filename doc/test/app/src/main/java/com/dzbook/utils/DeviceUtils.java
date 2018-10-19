package com.dzbook.utils;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.WindowManager;
import android.view.accessibility.AccessibilityManager;

import com.dzbook.lib.utils.ALog;
import com.dzbook.lib.utils.CompatUtils;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 设备相关的工具类 ((物理屏幕可变)时，需即时计算)
 *
 * @author zshu on 16/8/24.
 * <p>
 * <b/>1 是否为平板
 * </p>
 */
public class DeviceUtils {

    private static double mScreenInches = -1;
    private static String emuiStringVersion = "";
    private static int emuiVersion = -1;
    private static final double PAD_INCHES = 7.0;

    /**
     * 校验是否是pad
     *
     * @param context context
     * @return true是Pad，false不是Pad
     */
    public static boolean isPad(Context context) {
        if (context == null) {
            return false;
        }
        if (mScreenInches < 0) {
            WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
            Display display = wm.getDefaultDisplay();
            DisplayMetrics dm = new DisplayMetrics();
            display.getMetrics(dm);
            double x = Math.pow(dm.widthPixels / dm.xdpi, 2);
            double y = Math.pow(dm.heightPixels / dm.ydpi, 2);
            // 屏幕尺寸
            mScreenInches = Math.sqrt(x + y);
        }

        // 大于6尺寸则为Pad
        return mScreenInches >= PAD_INCHES;
    }

    /**
     * 读取总运存大小 单位：M
     *
     * @return long
     */
    public static long getMemoryTotalSize() {
        long mTotal;
        String path = "/proc/meminfo";
        String content = null;
        BufferedReader br = null;
        try {
            br = new BufferedReader(new InputStreamReader(new FileInputStream(path), FileUtils.DEFAULT_CHARSET), 8);
            String line;
            if ((line = br.readLine()) != null) {
                content = line;
            }
            if (!TextUtils.isEmpty(content)) {
                ALog.dWz("getMemoryTotalSize:" + content);
                int begin = content.indexOf(':');
                // endIndex
                int end = content.indexOf('k');
                // 截取字符串信息
                content = content.substring(begin + 1, end).trim();
                mTotal = Integer.parseInt(content);
                return mTotal / 1024;
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return 0;
    }

    /**
     * 关闭辅助功能，针对4.2.1和4.2.2 崩溃问题
     * java.lang.NullPointerException
     * atcom.tencent.smtt.sdk..AccessibilityInjector$TextToSpeechWrapper$1.onInit(AccessibilityInjector.java:753)
     * ... ...
     * atcom.tencent.smtt.sdk..CallbackProxy.handleMessage(CallbackProxy.java:321)
     *
     * @param context context
     */
    public static void disableAccessibility(Context context) {
        if (Build.VERSION.SDK_INT == 17/*4.2 (Build.VERSION_CODES.JELLY_BEAN_MR1)*/) {
            try {
                AccessibilityManager am = (AccessibilityManager) context.getSystemService(Context.ACCESSIBILITY_SERVICE);
                if (!am.isEnabled()) {
                    return;
                }
                Method set = am.getClass().getDeclaredMethod("setState", int.class);
                set.setAccessible(true);
                set.invoke(am, 0);/**{@link AccessibilityManager#STATE_FLAG_ACCESSIBILITY_ENABLED}*/
            } catch (RuntimeException e) {
                ALog.printStackTrace(e);
            } catch (Exception e) {
                ALog.printStackTrace(e);
            }

        }
    }

    /**
     * 在Android shell模式下输入 getprop 就能获取系统属性值
     * 如果Rom是miUI那么就会有以下字段.
     * [ro.miui.ui.version.code]: [3]
     * [ro.miui.ui.version.name]: [V5]
     * 基于安卓6.0开发的miui8  系统大改，权限要变
     *
     * @return boolean
     */
    public static boolean isMiui8() {
        String line;
        BufferedReader input = null;
        Runtime runtime = Runtime.getRuntime();
        try {
            Process p = runtime.exec("getprop " + "ro.miui.ui.version.name");
            input = new BufferedReader(new InputStreamReader(p.getInputStream(), FileUtils.DEFAULT_CHARSET), 1024);
            line = input.readLine();
            if (!TextUtils.isEmpty(line) && (line.contains("V8") || line.contains("V9") || line.contains("V10"))) {
                return true;
            }
        } catch (RuntimeException e) {
            ALog.printStackTrace(e);
        } catch (Exception ex) {
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (Exception e) {
                }
            }
        }
        return false;
    }

    /**
     * 手机号码11位数，匹配格式：前三位固定格式+后8位任意数
     * 此方法中前三位格式有：
     * 13+任意数
     * 15+除4的任意数
     * 18+除1和4的任意数
     * 17+除9的任意数
     * 147
     *
     * @param str str
     * @return boolean
     */
    public static boolean isPhoneNum(String str) {
        try {
            String regExp = "^((13[0-9])|(15[0-9])|(18[0-9])|(17[0-9])|(147)||(145))\\d{8}$";
            Pattern p = Pattern.compile(regExp);
            Matcher m = p.matcher(str);
            return m.matches();
        } catch (Exception e) {
            return true;//这里在出异常时  返回true吧 。。
        }
    }

    /**
     * 根据机型动态跳转到权限管理页面
     * 在这里适配的是魅族和小米 华为的机器 经过测试
     * 发现华为自己的权限管理做的跟系统的一样
     *
     * @param context context
     */
    public static void gotoSystemPermission(Context context) {
        String brand = android.os.Build.BRAND;
        try {
            if ("meizu".equalsIgnoreCase(brand)) {
                Intent intent = new Intent("com.meizu.safe.security.SHOW_APPSEC");
                intent.addCategory(Intent.CATEGORY_DEFAULT);
                intent.putExtra("packageName", context.getPackageName());
                context.startActivity(intent);
                return;
            } else if ("xiaomi".equalsIgnoreCase(brand)) {
                Intent i = new Intent("miui.intent.action.APP_PERM_EDITOR");
                ComponentName componentName = new ComponentName("com.miui.securitycenter", "com.miui.permcenter.permissions.AppPermissionsEditorActivity");
                i.setComponent(componentName);
                i.putExtra("extra_pkgname", context.getPackageName());
                context.startActivity(i);
                return;
            }
        } catch (Exception e) {
            ALog.printExceptionWz(e);
        }
        Intent localIntent = new Intent();
        localIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        localIntent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
        localIntent.setData(Uri.fromParts("package", context.getPackageName(), null));
        context.startActivity(localIntent);
    }

    /**
     * 设置背景色
     *
     * @param activity activity
     */
    public static void showBackGround(Activity activity) {
        if (null == activity) {
            return;
        }
        WindowManager.LayoutParams lp = activity.getWindow().getAttributes();
        lp.alpha = 1f;
        activity.getWindow().setAttributes(lp);
    }

    /**
     * 获取华为EMUI版本号
     *
     * @return int EMUI版本号
     */
    public static int getEMUIVersion() {
        if (emuiVersion > 0) {
            return emuiVersion;
        }
        int cutEMUIVersionStringLength = 11;
        String stringTitle = "EmotionUI_";
        int versionCutStart = 10;
        int versionCutEnd = 11;
        String eMui = DeviceUtils.getEMUI();
        if (eMui.contains(stringTitle) && eMui.length() >= cutEMUIVersionStringLength) {
            try {
                int anInt = Integer.parseInt(eMui.substring(versionCutStart, versionCutEnd));
                if (anInt > 0) {
                    emuiVersion = anInt;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return emuiVersion;
    }


    /**
     * 获取华为EMUI版本
     *
     * @return EMUI版本
     */
    public static String getEMUI() {
        if (!TextUtils.isEmpty(emuiStringVersion)) {
            return emuiStringVersion;
        }
        Class<?> classType;
        try {
            classType = Class.forName("android.os.SystemProperties");
            Method getMethod = classType.getDeclaredMethod("get", new Class<?>[]{String.class});
            emuiStringVersion = (String) getMethod.invoke(classType, new Object[]{"ro.build.version.emui"});
        } catch (Exception e) {
            e.printStackTrace();
        }
        return emuiStringVersion;
    }

    /**
     * 判断是否为中文环境
     *
     * @param context context
     * @return boolean
     */
    public static boolean isZh(Context context) {
        Locale locale = CompatUtils.getLocale(context.getResources().getConfiguration());
        String language = locale.getLanguage();
        return language.endsWith("zh");
    }
}
