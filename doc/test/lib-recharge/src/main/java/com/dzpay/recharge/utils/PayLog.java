package com.dzpay.recharge.utils;

import android.util.Log;

/**
 * 打印log
 *
 * @author lizz
 */
public class PayLog {
    private static final String TAG = "akRecharge";

    /**
     * 是否是debug
     */
    private static boolean debugMode = false;

    /**
     * 设置debug
     *
     * @param debugMode 模式
     */
    public static void setDebugMode(boolean debugMode) {
        PayLog.debugMode = debugMode;
        i("PayLog.setDebugMode(" + debugMode + ")");
    }

    /**
     * log级别
     *
     * @param msg 打印的信息
     * @return int
     */


    public static int d(String msg) {
        if (!debugMode) {
            return -1;
        }
        return Log.d(TAG, msg);
    }


    /**
     * log级别
     *
     * @param msg 打印的信息
     * @return int
     */


    public static int e(String msg) {
        if (!debugMode) {
            return -1;
        }
        return Log.e(TAG, msg);
    }

    /**
     * log级别
     *
     * @param msg 打印的信息
     * @return int
     */


    public static int i(String msg) {
        if (!debugMode) {
            return -1;
        }
        return Log.i(TAG, msg);
    }


    /**
     * log级别
     *
     * @param msg 打印的信息
     * @return int
     */

    public static int w(String msg) {
        if (!debugMode) {
            return -1;
        }
        return Log.w(TAG, msg);
    }

    /**
     * log级别
     *
     * @param msg 打印的信息
     * @return int
     */

    public static int v(String msg) {
        if (!debugMode) {
            return -1;
        }
        return Log.v(TAG, msg);
    }


    /**
     * 打印错误堆栈日志
     *
     * @param e 异常
     */
    public static void printStackTrace(Exception e) {
        if (debugMode) {
            e.printStackTrace();
        }
    }

}
