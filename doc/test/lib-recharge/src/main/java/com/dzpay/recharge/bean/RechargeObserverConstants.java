package com.dzpay.recharge.bean;

/**
 * Observer处理结果代码
 *
 * @author lizz 2018/4/16
 */
public class RechargeObserverConstants {

    /**
     * 方法执行成功
     */
    public static final int SUCCESS = 200;

    /**
     * 方法执行失败，所有与客户端的回调都是通过FAIL和SUCESS完成
     */
    public static final int FAIL = 400;

    /**
     * 需要支付确认
     */
    public static final int LOT_GOTO_ORDER = 202;

    /**
     * 改变窗口状态
     */
    public static final int STATUS_CHANGE = 203;

    /**
     * 充值状态改变
     */
    public static final int RECHARGE_STATUS_CHANGE = 204;

    /**
     * 单章需要支付确认
     */
    public static final int SINGLE_GOTO_ORDER = 205;
    /**
     * 打包定价
     */
    public static final int PACKBOOK = 206;

}
