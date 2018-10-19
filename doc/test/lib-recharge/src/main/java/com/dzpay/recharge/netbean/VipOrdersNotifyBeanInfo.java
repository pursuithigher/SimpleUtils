package com.dzpay.recharge.netbean;

import org.json.JSONObject;

/**
 * vip bean
 *
 * @author lizz 2018/4/18.
 */

public class VipOrdersNotifyBeanInfo extends PublicResBean {

    /**
     * 支付结果，1:充值成功 2:充值失败 0：充值延时
     */
    public int result;

    /**
     * VipOrdersResultBean
     */
    public VipOrdersResultBean resultBean;


    @Override
    public VipOrdersNotifyBeanInfo parseJSON(JSONObject jsonObj) {
        super.parseJSON(jsonObj);
        if (isSuccess()) {
            JSONObject dataJsonObj = jsonObj.optJSONObject("data");
            if (dataJsonObj == null) {
                return null;
            }
            result = dataJsonObj.optInt("result");

            resultBean = new VipOrdersResultBean().parseJSON(dataJsonObj);
        }

        return this;
    }

    @Override
    public String toString() {
        return "result:" + result + (resultBean != null ? resultBean.toString() : "");
    }

    /**
     * 是否充值成功
     *
     * @return result == 1
     */
    public boolean isRechargeSuccess() {
        return result == 1;
    }

    /**
     * 是否充值延时
     *
     * @return result == 0
     */
    public boolean isRechargeDelay() {
        return result == 0;
    }
}
