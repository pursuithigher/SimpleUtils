package com.dzpay.recharge.netbean;

import org.json.JSONObject;

import hw.sdk.net.bean.HwPublicBean;

/**
 * bean
 * @author lizz 2018/4/14.
 */

public class RechargeActivityBean extends HwPublicBean<RechargeActivityBean> {

    /**
     * 地址
     */
    public String url;

    /**
     * 图片地址
     */
    public String img;

    @Override
    public RechargeActivityBean parseJSON(JSONObject jsonObj) {
        if (jsonObj == null) {
            return null;
        }
        url = jsonObj.optString("url");
        img = jsonObj.optString("img");

        return this;
    }
}
