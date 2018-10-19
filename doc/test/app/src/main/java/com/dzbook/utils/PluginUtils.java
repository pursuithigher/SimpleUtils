package com.dzbook.utils;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.support.v4.content.FileProvider;

import com.ishugui.BuildConfig;

import java.io.File;

/**
 * 插件
 *
 * @author wxliao on 18/4/26.
 */

public class PluginUtils {

    /**
     * 获取已安装wps信息
     *
     * @param context context
     * @return PackageInfo
     */
    public static PackageInfo getInstalledWpsInfo(Context context) {
        String[] checkList = {"cn.wps.moffice_eng"};
        for (String packageName : checkList) {
            PackageInfo packageInfo = getInstalledInfo(context, packageName);
            if (packageInfo != null) {
                return packageInfo;
            }
        }
        return null;
    }

    private static PackageInfo getInstalledInfo(Context context, String pkgName) {
        PackageInfo packageInfo = null;
        try {
            packageInfo = context.getPackageManager().getPackageInfo(pkgName, 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return packageInfo;
    }


    /**
     * 安装apk
     *
     * @param context context
     * @param file    file
     */
    public static void installFile(Context context, File file) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        //判断是否是AndroidN以及更高的版本
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            Uri contentUri = FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID + ".fileProvider", file);
            intent.setDataAndType(contentUri, "application/vnd.android.package-archive");
        } else {
            intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
        }
        try {
            context.startActivity(intent);
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
        }
    }
}
