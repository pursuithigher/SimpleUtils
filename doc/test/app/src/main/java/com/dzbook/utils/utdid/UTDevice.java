package com.dzbook.utils.utdid;

import android.os.Build;
import android.text.TextUtils;

import com.dzbook.AppConst;
import com.dzbook.utils.SpUtil;

import java.util.UUID;


/**
 * UTDevice
 *
 * @author caimantang on 2018/5/18.
 */
public class UTDevice {
    private static final String UTDID = "utdid";

    /**
     * 获取utdid
     *
     * @return utdid
     */
    public static String getUtdid() {
        String utdid = SpUtil.getinstance(AppConst.getApp()).getString(UTDID);
        if (!TextUtils.isEmpty(utdid)) {
            return utdid;
        }
        String macAddress = UTDUtils.getMacAddress(AppConst.getApp());

        if (!TextUtils.isEmpty(macAddress)) {
            String deviceShort = getEmptyString(Build.BOARD) + getEmptyString(Build.BRAND) + getEmptyString(Build.CPU_ABI) + getEmptyString(Build.DEVICE) + getEmptyString(Build.MANUFACTURER) + getEmptyString(Build.MODEL) + getEmptyString(Build.PRODUCT) + macAddress;
            String uuid = new UUID(deviceShort.hashCode(), macAddress.hashCode()).toString();
            SpUtil.getinstance(AppConst.getApp()).setString(UTDID, uuid);
            return uuid;
        }
        return null;
    }


    private static String getEmptyString(String str) {
        return !TextUtils.isEmpty(str) ? str : "";
    }
}
