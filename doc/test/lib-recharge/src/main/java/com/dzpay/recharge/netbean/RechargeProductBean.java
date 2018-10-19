package com.dzpay.recharge.netbean;

import org.json.JSONObject;

import hw.sdk.net.bean.HwPublicBean;

/**
 * 支付bean
 *
 * @author lizz 2018/4/14.
 */

public class RechargeProductBean extends HwPublicBean<RechargeProductBean> {

    /**
     * 金额ID
     */
    public String id;

    /**
     * 金额 入：1元 | 花币
     */
    public String amount;

    /**
     * 金额角标 如：送1元 或者 送33%
     */
    public String corner;

    /**
     * 显示看点 如：100看点
     */
    public String product;

    /**
     * 赠送代金券 如 10代金券
     */
    public String give;

    /**
     * 是否选中 1:选中 2:不选中
     */
    public int checked;

    /**
     * 金额数量 如：1
     */
    public String amountNum;

    /**
     * 看点数量 如: 100
     */
    public String productNum;

    /**
     * 赠送代金券数量 如: 10
     */
    public String giveNum;

    @Override
    public RechargeProductBean parseJSON(JSONObject jsonObj) {
        if (jsonObj == null) {
            return null;
        }
        id = jsonObj.optString("id");
        amount = jsonObj.optString("amount");
        corner = jsonObj.optString("corner");
        product = jsonObj.optString("product");
        give = jsonObj.optString("give");
        checked = jsonObj.optInt("checked", 2);

        amountNum = jsonObj.optString("amountNum");
        productNum = jsonObj.optString("productNum");
        giveNum = jsonObj.optString("giveNum");

        return this;
    }

    /**
     * 是否选中
     *
     * @return boolean
     */
    public boolean isSelected() {
        return checked == 1;
    }
}
