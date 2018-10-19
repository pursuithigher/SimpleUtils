package com.dzbook.utils.utdid;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.text.TextUtils;

import com.dzbook.lib.utils.ALog;

import java.net.NetworkInterface;
import java.util.Collections;
import java.util.List;

/**
 * UTD工具类
 */
public class UTDUtils {
    private static String macAddress = "";

    /**
     * MAC地址。根据优先级获取mac地址。优先级如下：<br>
     * &nbsp;1，内存；<br>
     * &nbsp;2，本地存储（sp存储，setting存储）；<br>
     * &nbsp;3，传统 WifiManager 读取；(适用于android 6.0以下)<br>
     * &nbsp;4，使用已连接的wifi网络读取。(适用于android 6.0及以上)<br>
     * 以上任何低优先级获取到数据以后，都会同时同步高优先级缓存，以达到加速目的。
     *
     * @param context context
     * @return string MAC地址
     */
    public static String getMacAddress(Context context) {
        // 内存加速。内存有，返回内存。
        if (!TextUtils.isEmpty(macAddress)) {
            return macAddress;
        }

        // 传统读取。适用于android6.0以下手机。
        String macWifiManager = getMacAddressByWifiManager(context);
        if (!TextUtils.isEmpty(macWifiManager) && !"02:00:00:00:00:00".equals(macWifiManager)) {
            ALog.dLk("getMacAddress from WifiManager = " + macWifiManager);
            return macAddress = macWifiManager;
        }

        // wifi 链接时，android6.0以上读取方式。
        String macWLAN = getMacAddressByWLAN();
        if (!TextUtils.isEmpty(macWLAN) && !"02:00:00:00:00:00".equals(macWLAN)) {
            ALog.dLk("getMacAddress from WLAN = " + macWLAN);
            return macAddress = macWLAN;
        }

        return macWifiManager;
    }

    /**
     * 传统的获取mac地址的方法。android 6.0以后就不再适用。
     *
     * @param context 上下文
     * @return mac地址
     */
    private static String getMacAddressByWifiManager(Context context) {
        try {
            WifiManager wifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
            WifiInfo info = wifi.getConnectionInfo();
            if (null != info) {
                return info.getMacAddress();
            }
        } catch (Exception ignore) {
        }
        return "";
    }

    /**
     * android 6.0 以上手机需要使用此方法才能读取到真实的 mac address
     *
     * @return mac地址
     */
    private static String getMacAddressByWLAN() {
        try {
            List<NetworkInterface> all = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface nif : all) {
                if (!nif.getName().equalsIgnoreCase("wlan0")) {
                    continue;
                }

                byte[] macBytes = nif.getHardwareAddress();
                if (macBytes == null || macBytes.length == 0) {
                    return "";
                }

                StringBuilder res1 = new StringBuilder();
                for (byte b : macBytes) {
                    res1.append(String.format("%02X:", b));
                }

                if (res1.length() > 0) {
                    res1.deleteCharAt(res1.length() - 1);
                }
                return res1.toString();
            }
        } catch (Exception ignore) {
        }
        return "02:00:00:00:00:00";
    }
}
