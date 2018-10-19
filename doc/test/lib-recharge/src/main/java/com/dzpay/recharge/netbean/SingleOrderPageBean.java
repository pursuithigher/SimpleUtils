package com.dzpay.recharge.netbean;

import org.json.JSONObject;

import hw.sdk.net.bean.HwPublicBean;

/**
 * 单张订购bean
 *
 * @author lizz 2018/4/14.
 */

public class SingleOrderPageBean extends HwPublicBean<SingleOrderPageBean> {

    /**
     * 章节id
     */
    public String chapterId;

    /**
     * 书名
     */
    public String bookName;

    /**
     * 作者
     */
    public String author;

    /**
     * 章节名
     */
    public String chapterName;

    /**
     * 优惠提示 VIP享八折
     */
    public String disTips;

    /**
     * 优惠原价 500看点
     */
    public String oldPrice;

    /**
     * 剩余看点，例如：50
     */
    public int remain;

    /**
     * 剩余代金券，例如：270
     */
    public int vouchers;

    /**
     * 看点单位 例如：看点
     */
    public String priceUnit;

    /**
     * 代金券单位 例如：代金券
     */
    public String vUnit;

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
     * 1.按本购买；2.按章购买
     */
    public int unit;

    /**
     * 1.直接消费确认；2：余额不足去充值
     */
    public int action;

    /**
     * VIP 提示
     */
    public String vipTips;

    /**
     * 批量订购提示
     */
    public String lotTips;

    @Override
    public SingleOrderPageBean parseJSON(JSONObject jsonObj) {
        if (jsonObj == null) {
            return null;
        }
        this.chapterId = jsonObj.optString("chapterId");
        this.bookName = jsonObj.optString("bookName");
        this.chapterName = jsonObj.optString("chapterName");
        this.author = jsonObj.optString("author");
        this.disTips = jsonObj.optString("disTips");
        this.oldPrice = jsonObj.optString("oldPrice");
        this.remain = jsonObj.optInt("remain");
        this.vouchers = jsonObj.optInt("vouchers");
        this.vUnit = jsonObj.optString("vUnit");
        this.price = jsonObj.optString("price");
        this.needPay = jsonObj.optString("needPay");
        this.deduction = jsonObj.optString("deduction");
        this.unit = jsonObj.optInt("unit");
        this.action = jsonObj.optInt("action");
        this.vipTips = jsonObj.optString("vipTips");
        this.lotTips = jsonObj.optString("lotTips");
        this.priceUnit = jsonObj.optString("priceUnit");


        return this;
    }

    /**
     * 是否单本书籍
     *
     * @return boolean
     */
    public boolean isSingleBook() {
        return unit == 1;
    }

    /**
     * 是否需要充值
     *
     * @return boolean
     */
    public boolean isNeedRecharge() {
        return action == 2;
    }
}
