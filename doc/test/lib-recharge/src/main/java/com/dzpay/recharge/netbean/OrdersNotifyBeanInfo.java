package com.dzpay.recharge.netbean;

import org.json.JSONObject;

/**
 * 订购
 *
 * @author lizz 2018/4/14.
 */

public class OrdersNotifyBeanInfo extends PublicResBean {


    /**
     * 支付结果，1:充值成功 2:充值失败 3：充值延时
     */
    public int result;

    /**
     * 订单结果
     */
    public OrdersResultBean resultBeanInfo;


    @Override
    public OrdersNotifyBeanInfo parseJSON(JSONObject jsonObj) {
        super.parseJSON(jsonObj);

        if (isSuccess()) {
            JSONObject dataJsonObj = jsonObj.optJSONObject("data");
            if (dataJsonObj == null) {
                return null;
            }
            result = dataJsonObj.optInt("result", 3);
            resultBeanInfo = new OrdersResultBean().parseJSON(dataJsonObj);
        }

        return this;
    }

    /**
     * 是否充值成功
     *
     * @return boolean
     */
    public boolean isRechargeSuccess() {
        return result == 1;
    }

    /**
     * 是否充值延时
     *
     * @return boolean
     */
    public boolean isRechargeDelay() {
        return result == 3;
    }

    @Override
    public String toString() {
        return "result:" + result + (resultBeanInfo != null ? resultBeanInfo.toString() : "");
    }

}
