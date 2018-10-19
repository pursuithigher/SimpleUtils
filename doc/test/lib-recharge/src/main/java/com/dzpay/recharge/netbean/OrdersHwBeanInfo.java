package com.dzpay.recharge.netbean;

import org.json.JSONObject;

/**
 * 订购
 *
 * @author lizz 2018/4/14.
 */

public class OrdersHwBeanInfo extends PublicResBean {

    /**
     * 订单号
     */
    public String orderNo;

    /**
     * 订单信息
     */
    public OrdersHwBean orderHwBean;

    @Override
    public OrdersHwBeanInfo parseJSON(JSONObject jsonObj) {
        super.parseJSON(jsonObj);
        if (isSuccess()) {
            JSONObject dataJsonObj = jsonObj.optJSONObject("data");
            if (dataJsonObj == null) {
                return null;
            }
            orderNo = dataJsonObj.optString("orderNo");
            JSONObject orderJsonObj = dataJsonObj.optJSONObject("reqParams");
            if (orderJsonObj != null) {
                orderHwBean = new OrdersHwBean().parseJSON(orderJsonObj);
            }
        }

        return this;
    }
}
