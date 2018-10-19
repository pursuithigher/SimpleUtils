package com.dzpay.recharge.netbean;

import org.json.JSONObject;

/**
 * Vip订单BeanInfo
 *
 * @author lizz 2018/4/18.
 */

public class VipOrdersBeanInfo extends PublicResBean {

    /**
     * 订单信息
     */
    public VipOrdersBean orderHwBean;
    /**
     * 订单号
     */
    public String orderNo;

    /**
     * 1：现金支付 2：自动续费签约协议
     */
    private int payWay;


    @Override
    public VipOrdersBeanInfo parseJSON(JSONObject jsonObj) {
        super.parseJSON(jsonObj);
        if (isSuccess()) {
            JSONObject dataJsonObj = jsonObj.optJSONObject("data");
            if (dataJsonObj == null) {
                return null;
            }
            orderNo = dataJsonObj.optString("orderNo");
            payWay = dataJsonObj.optInt("payWay", 1);
            JSONObject orderJsonObj = dataJsonObj.optJSONObject("reqParams");
            if (orderJsonObj != null) {
                orderHwBean = new VipOrdersBean().parseJSON(orderJsonObj);
            }
        }

        return this;
    }

    /**
     * 是否自动订购支付方式
     *
     * @return boolean
     */
    public boolean isAutoOrderWay() {
        return payWay == 2;
    }
}
