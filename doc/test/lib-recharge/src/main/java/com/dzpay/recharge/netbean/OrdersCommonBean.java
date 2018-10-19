package com.dzpay.recharge.netbean;


import android.text.TextUtils;

import com.dzbook.lib.utils.ALog;

import java.io.Serializable;

/**
 * 余额不足带去充值页面的订单信息
 *
 * @author lizz 2018/4/16.
 */

public class OrdersCommonBean implements Serializable {

    /**
     * 1.按本购买；2.按章购买
     */
    public int unit;

    /**
     * 100
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
     * 订购章节名称或者订购书籍名称
     */
    public String orderName;

    /**
     * 价格：51
     * 不带单位，客户端展示 51看点
     */
    public String price;

    /**
     * 优惠提示 VIP享八折
     */
    public String disTips;

    /**
     * 优惠原价 500看点
     */
    public String oldPrice;

    /**
     * VIP 提示
     */
    public String vipTips;

    /**
     * 作者
     */
    public String author;
    /**
     * 跟踪日志
     */
    public String trackId;
    private String bookId;

    private String chapterId;


    /**
     * 构造
     *
     * @param unit      1.按本购买；2.按章购买
     * @param remain    100
     * @param vouchers  剩余代金券，例如：270
     * @param priceUnit 看点单位 例如：看点
     * @param vUnit     代金券单位 例如：代金券
     * @param needPay   需支付看点数量，例如：45
     *                  不带单位，客户端展示 45看点
     * @param deduction 抵扣: 5
     *                  不带单位，客户端展示 -5代金券
     * @param orderName 订购章节名称或者订购书籍名称
     * @param author    作者
     * @param price     价格
     * @param disTips   优惠提示 VIP享八折
     * @param oldPrice  优惠原价 500看点
     * @param vipTips   VIP 提示
     */
    public OrdersCommonBean(int unit, int remain, int vouchers, String priceUnit, String vUnit, String needPay, String deduction, String orderName, String author, String price, String disTips, String oldPrice, String vipTips) {
        this.unit = unit;
        this.remain = remain;
        this.vouchers = vouchers;
        this.priceUnit = priceUnit;
        this.vUnit = vUnit;
        this.needPay = needPay;
        this.deduction = deduction;
        this.orderName = orderName;
        this.author = author;
        this.price = price;
        this.disTips = disTips;
        this.oldPrice = oldPrice;
        this.vipTips = vipTips;
    }


    public String getBookId() {
        return bookId;
    }

    public void setBookId(String bookId) {
        this.bookId = bookId;
    }

    public String getChapterId() {
        return chapterId;
    }

    public void setChapterId(String chapterId) {
        this.chapterId = chapterId;
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
     * 书籍id和章节id不为空
     *
     * @return boolean
     */
    public boolean bookIdAndChapterIdNoEmpty() {
        return !TextUtils.isEmpty(bookId) && !TextUtils.isEmpty(chapterId);
    }

    /**
     * 是否展示代金券
     *
     * @return boolean
     */
    public boolean isShowDeductionView() {
        try {
            if (!TextUtils.isEmpty(deduction)) {
                int mDeduction = Integer.parseInt(deduction);
                if (mDeduction > 0) {
                    return true;
                }
            }
        } catch (Exception e) {
            ALog.printStackTrace(e);
        }
        return false;
    }
}
