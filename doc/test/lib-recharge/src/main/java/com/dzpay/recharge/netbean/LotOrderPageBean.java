package com.dzpay.recharge.netbean;

import org.json.JSONObject;

import hw.sdk.net.bean.HwPublicBean;

/**
 * 批量订购
 *
 * @author lizz 2018/4/14.
 */

public class LotOrderPageBean extends HwPublicBean<LotOrderPageBean> {

    /**
     * 批量下载显示内容，例如：后100章必须
     */
    public String tips;

    /**
     * 包含当前章节
     */
    public int afterNum;

    /**
     * 角标 如8折
     */
    public String corner;

    /**
     * 优惠提示 VIP享八折
     */
    public String disTips;

    /**
     * 折扣率
     */
    public String discountRate;

    /**
     * 原价 500看点
     */
    public String oldPrice;

    /**
     * 价格：51
     * 不带单位，客户端展示 51看点
     */
    public String price;

    /**
     * 需支付看点数量，例如：45
     * 不带单位，客户端展示 45看点
     */
    public String needPay;

    /**
     * 抵扣: 5
     * <p>
     * 不带单位，客户端展示 -5代金券
     */
    public String deduction;

    /**
     * 1.直接消费；2：余额不足去充值
     */
    public int action;


    @Override
    public LotOrderPageBean parseJSON(JSONObject jsonObj) {
        if (jsonObj == null) {
            return null;
        }
        tips = jsonObj.optString("tips");
        afterNum = jsonObj.optInt("afterNum");
        corner = jsonObj.optString("corner");
        disTips = jsonObj.optString("disTips");
        discountRate = jsonObj.optString("discountRate");
        oldPrice = jsonObj.optString("oldPrice");
        price = jsonObj.optString("price");
        deduction = jsonObj.optString("deduction");
        needPay = jsonObj.optString("needPay");
        action = jsonObj.optInt("action");

        return this;
    }

    /**
     * 需要充值
     *
     * @return boolean
     */
    public boolean isNeedRecharge() {
        return action == 2;
    }
}
