package com.dzpay.recharge.netbean;

import org.json.JSONObject;

import hw.sdk.net.bean.HwPublicBean;

/**
 * vip订购bean
 *
 * @author lizz 2018/4/18.
 */

public class VipOrdersBean extends HwPublicBean<VipOrdersBean> {

    /**
     * 商户对商品的自定义名称。此名称将会在支付时显示给用户确认
     */
    public String productName;

    /**
     * 商品描述， 商户对商品的自定义描述 。
     */
    public String productDesc;

    /**
     * 应用ID。
     */
    public String applicationID;

    /**
     * 开发者支付订单号
     */
    public String requestId;

    /**
     * 商品金额 商品所要支付金额
     */
    public String amount;

    /**
     * 商户ID。
     */
    public String merchantId;

    /**
     * 商品类型。X9
     */
    public String serviceCatalog;

    /**
     * 商户昵称。该名称用于支付客户端的商户名称显示。。
     */
    public String merchantName;

    /**
     * 渠道信息，取值如下
     */
    public int sdkChannel;

    /**
     * 支付结果回调地址
     */
    public String url;

    /**
     * 币种，
     */
    public String currency;

    /**
     * 商品金额 商品所要支付金额
     */
    public String country;

    /**
     * 交易类型，取值: 固定为toSign
     */
    public String tradeType;

    /**
     * 签名
     */
    public String sign;


    @Override
    public VipOrdersBean parseJSON(JSONObject jsonObj) {
        if (jsonObj == null) {
            return null;
        }

        productName = jsonObj.optString("productName");
        productDesc = jsonObj.optString("productDesc");
        applicationID = jsonObj.optString("applicationID");
        requestId = jsonObj.optString("requestId");
        amount = jsonObj.optString("amount");
        merchantId = jsonObj.optString("merchantId");
        merchantName = jsonObj.optString("merchantName");
        sdkChannel = jsonObj.optInt("sdkChannel");
        url = jsonObj.optString("url");
        sign = jsonObj.optString("sign");
        serviceCatalog = jsonObj.optString("serviceCatalog");
        tradeType = jsonObj.optString("tradeType");
        currency = jsonObj.optString("currency");
        country = jsonObj.optString("country");

        return this;
    }
}
