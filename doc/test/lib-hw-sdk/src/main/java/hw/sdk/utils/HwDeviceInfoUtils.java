package hw.sdk.utils;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;

import com.dzbook.lib.utils.ALog;

/**
 * 获取设备信息实例
 *
 * @author winzows 2018/4/12
 */
public class HwDeviceInfoUtils {

    /**
     * 华为appId
     */
    private static final String META_HW_APPID = "com.huawei.hms.client.appid";

    /**
     * 获取华为appID
     *
     * @param context context
     * @return appID
     */
    public static String getHwAppId(Context context) {
        return getMetaDataValue(context, META_HW_APPID);
    }

    /**
     * 获取manifest中meta-data的值
     *
     * @param context
     * @param name    对应meta-data的android:name属性
     * @return
     */
    private static String getMetaDataValue(Context context, String name) {
        Object value = null;
        try {
            ApplicationInfo appInfo = context.getPackageManager().getApplicationInfo(context.getPackageName(),
                    PackageManager.GET_META_DATA);
            value = appInfo.metaData.get(name);
        } catch (Exception e) {
            ALog.printStackTrace(e);
        }
        return String.valueOf(value);
    }


    /**
     * 获取app版本号
     *
     * @param context context
     * @return appVersion
     */
    public static String getAppVersion(Context context) {
        String appName = "";
        try {
            appName = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;
        } catch (Exception e) {
            ALog.printExceptionWz(e);
        }
        return appName;
    }
}
