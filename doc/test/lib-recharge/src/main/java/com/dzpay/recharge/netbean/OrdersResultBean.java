package com.dzpay.recharge.netbean;

import org.json.JSONObject;

import hw.sdk.net.bean.HwPublicBean;

/**
 * 订购
 * @author lizz 2018/4/19.
 */

public class OrdersResultBean extends HwPublicBean<OrdersResultBean> {

    /**
     * 充值单位
     */
    public String unit;

    /**
     * 用户余额看点数量
     */
    public int remain;

    /**
     * vouchers
     */
    public int vouchers;

    /**
     * 代金券单位
     */
    public String vouchersUnit;

    /**
     * 本次充值金额数量
     */
    public int curRecharge;

    /**
     * 本次充值代金券数量
     */
    public int curVouchers;

    /**
     * json数据
     */
    public String json;

    @Override
    public OrdersResultBean parseJSON(JSONObject jsonObj) {

        json = jsonObj.toString();
        unit = jsonObj.optString("unit");
        remain = jsonObj.optInt("remain");
        vouchers = jsonObj.optInt("vouchers");
        vouchersUnit = jsonObj.optString("vouchersUnit");
        curRecharge = jsonObj.optInt("curRecharge");
        curVouchers = jsonObj.optInt("curVouchers");

        return this;
    }


    @Override
    public String toString() {
        return "|remain:" + remain + ",vouchers:" + vouchers + ",curRecharge:" + curRecharge + ",curVouchers:" + curVouchers;
    }
}
