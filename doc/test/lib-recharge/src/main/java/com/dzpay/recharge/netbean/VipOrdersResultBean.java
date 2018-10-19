package com.dzpay.recharge.netbean;

import org.json.JSONObject;

import hw.sdk.net.bean.HwPublicBean;

/**
 * vip订购bean
 *
 * @author lizz 2018/4/19.
 */

public class VipOrdersResultBean extends HwPublicBean {

    /**
     * vip订购结果
     */
    public static final String VIP_ORDER_RESULT = "vip_order_result";

    /**
     * 开通提示语句：例如：包月VIP开通成功，连续包月VIP开通成功
     */
    public String openMsg;

    /**
     * 开通帐号，显示华为昵称，
     */
    public String nickName;

    /**
     * 开始时间，2018/09/01,例如
     */
    public String startTime;

    /**
     * 过期时间，2018/09/01
     */
    public String deadLine;

    /**
     * 开通天数，例如：31天
     */
    public String openDays;

    /**
     * json数据
     */
    public String json;

    @Override
    public VipOrdersResultBean parseJSON(JSONObject jsonObj) {

        json = jsonObj.toString();

        openMsg = jsonObj.optString("openMsg");
        nickName = jsonObj.optString("nickName");
        startTime = jsonObj.optString("startTime");
        deadLine = jsonObj.optString("deadLine");
        openDays = jsonObj.optString("openDays");

        return this;
    }

    @Override
    public String toString() {
        return "|nickName:" + nickName + ",deadLine:" + deadLine + ",openDays:" + openDays;
    }
}
