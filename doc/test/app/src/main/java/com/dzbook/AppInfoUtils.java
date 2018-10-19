package com.dzbook;

import android.text.TextUtils;

import com.dzbook.utils.DeviceInfoUtils;

/**
 * 图书信息utils
 *
 * @author wxliao on 17/4/18.
 */
public class AppInfoUtils {
    private static String getMetaData(String metaName) {
        String metaData = DeviceInfoUtils.getInstanse().getMetaDataValue(metaName);
        if (TextUtils.isEmpty(metaData)) {
            return "";
        }
        return metaData;
    }

    public static String getGitCode() {
        return getMetaData("GIT_CODE");
    }

    public static String getGitTag() {
        return getMetaData("GIT_TAG");
    }

    public static String getChannel() {
        return getMetaData("UMENG_CHANNEL");
    }

    public static String getHwAppId() {
        return DeviceInfoUtils.getInstanse().getMetaDataValueInt("com.huawei.hms.client.appid");
    }
}
