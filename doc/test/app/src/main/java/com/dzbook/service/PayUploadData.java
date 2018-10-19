package com.dzbook.service;

import android.content.Context;

/**
 * PayUploadData
 */
public class PayUploadData {
    private static String pub(String str, Context context) {
        return str;
    }

    /**
     * 自有支付系统批量下载
     *
     * @param context context
     * @return string
     */
    public static String loadRecharge(Context context) {
        return pub("LOAD_RECHARGE_", context);
    }

    /**
     * 自有支付系统下载单本
     *
     * @param context context
     * @return string
     */
    public static String loadRechargeSingle(Context context) {
        return pub("LOAD_RECHARGE_SINGLE", context);
    }
}
