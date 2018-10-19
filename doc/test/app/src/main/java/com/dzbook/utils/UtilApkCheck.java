package com.dzbook.utils;

import android.content.Context;
import android.content.pm.PackageManager;

/**
 * 判断是否安装指定包名的APP
 *
 * @author zhenglk
 */
public class UtilApkCheck {

    /**
     * 判断是否安装指定包名的APP
     *
     * @param mContext    mContext
     * @param packageName packageName
     * @return boolean
     */
    public static boolean isInstalledApp(Context mContext, String packageName) {
        if (packageName == null || "".equals(packageName)) {
            return false;
        }
        try {
            mContext.getPackageManager().getApplicationInfo(packageName, PackageManager.GET_UNINSTALLED_PACKAGES);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }
}
