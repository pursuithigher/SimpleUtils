package com.dzpay.recharge.netbean;

import org.json.JSONObject;

import hw.sdk.net.bean.HwPublicBean;

/**
 * 订购
 *
 * @author lizz 2018/4/14.
 */

public class OrdersHwBean extends HwPublicBean<OrdersHwBean> {

    /**
     * 商户对商品的自定义名称。此名称将会在支付时显示给用户确认
     */
    public String productName;

    /**
     * 商品描述， 商户对商品的自定义描述
     */
    public String productDesc;

    /**
     * 应用ID
     */
    public String applicationID;

    /**
     * 开发者支付订单号
     */
    public String requestId;

    /**
     * 商品金额 商品所要支付金额。格式为：元.角分，最小金额为分， 例如：20.00 保留到小数点后两位
     */
    public String amount;

    /**
     * 商户ID
     */
    public String merchantId;

    /**
     * 商户昵称。该名称用于支付客户端的商户名称显示。。
     */
    public String merchantName;

    /**
     * 渠道信息，
     */
    public Integer sdkChannel;

    /**
     * 支付结果回调地址 。
     */
    public String url;

    /**
     * 签名
     */
    public String sign;

    /**
     * 商品类型。
     */
    public String serviceCatalog;

    @Override
    public OrdersHwBean parseJSON(JSONObject jsonObj) {
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

        return this;
    }
}
