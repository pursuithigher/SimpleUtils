package com.dzpay.recharge.bean;

/**
 * author lizhongzhong 2017/5/4.
 */

public interface RechargeConstants {

    /**
     * show dialog
     */
    public static final int DIALOG_SHOW = 1;
    /**
     * dismiss
     */
    public static final int DIALOG_DISMISS = 2;

    /**
     * 支付开始下订单
     */
    int START_MAKE_ORDER = 1;
    /**
     * 下订单失败
     */
    int MAKE_ORDER_FAIL = START_MAKE_ORDER + 1;
    /**
     * 开始支付，下订单一定成功
     */
    int START_RECHARGE = MAKE_ORDER_FAIL + 1;
    /**
     * 支付完成订单通知开始
     */
    int ORDER_NOTIFY_START = START_RECHARGE + 1;
    /**
     * 支付完成订单通知成功
     */
    int ORDER_NOTIFY_SUCCESS = ORDER_NOTIFY_START + 1;
    /**
     * 支付完成订单通知失败
     */
    int ORDER_NOTIFY_FAIL = ORDER_NOTIFY_SUCCESS + 1;

    /**
     * destory
     */

    int ORDER_CORE_DESTROYED = ORDER_NOTIFY_FAIL + 1;
}
