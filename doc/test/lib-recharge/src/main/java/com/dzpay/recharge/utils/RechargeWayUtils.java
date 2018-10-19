package com.dzpay.recharge.utils;

import android.text.TextUtils;

import java.util.HashMap;


/**
 * 支付方式
 *
 * @author lizhongzhong 2015/7/17.
 * 工具类
 */
public final class RechargeWayUtils {

    /**
     * 华为充值
     */
    public static final int RECHARGE_HW_PAY = 1;

    /**
     * 华为vip开通
     */
    public static final int VIP_OPEN_HW_PAY = 2;


    /**
     * 汇总表，每添加一种新的类型都需要在这里添加映射项
     */
    private static final String[][] ARRAY = {
            {"RECHARGE_HW_PAY", String.valueOf(RECHARGE_HW_PAY)},
            {"VIP_OPEN_HW_PAY", String.valueOf(VIP_OPEN_HW_PAY)},
    };

    private static final HashMap<String, Integer> STR_TO_INT = new HashMap<>();

    private static final HashMap<Integer, String> INT_TO_STR = new HashMap<>();

    /**
     * 通过类型 String，得到类型值
     *
     * @param type 类型 String
     * @return 类型值
     */
    public static int getInt(String type) {

        try {
            if (null == type) {
                return 0;
            }
            type = type.trim();
            if (TextUtils.isEmpty(type)) {
                return 0;
            }
            if (STR_TO_INT.isEmpty()) {
                init();
            }
            return STR_TO_INT.get(type);

        } catch (Exception e) {
            PayLog.printStackTrace(e);
        }

        return 0;
    }

    /**
     * 通过类型值，得到类型 String
     *
     * @param value 类型值
     * @return 类型 String
     */
    public static String getString(int value) {

        try {
            if (INT_TO_STR.isEmpty()) {
                init();
            }
            return INT_TO_STR.get(value);

        } catch (Exception e) {
            PayLog.printStackTrace(e);
        }

        return null;
    }

    /**
     * 初始化映射map缓存
     */
    private static synchronized void init() {
        for (String[] nameValue : ARRAY) {
            if (null != nameValue && 2 == nameValue.length && !TextUtils.isEmpty(nameValue[0]) && !TextUtils.isEmpty(nameValue[1])) {
                int value = Integer.parseInt(nameValue[1]);
                String name = nameValue[0];
                INT_TO_STR.put(value, name);
                STR_TO_INT.put(name, value);
            }
        }
    }

    /**
     * 是否vip开通支付
     *
     * @param rechargeWay 支付方式
     * @return boolean
     */
    public static boolean isVipOpenRechargeWay(String rechargeWay) {
        return getInt(rechargeWay) == VIP_OPEN_HW_PAY;
    }

}
