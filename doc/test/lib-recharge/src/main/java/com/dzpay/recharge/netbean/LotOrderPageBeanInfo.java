package com.dzpay.recharge.netbean;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import hw.sdk.net.bean.HwPublicBean;

/**
 * 批量订购提示页面 解析bean
 *
 * @author lizz 2018/4/14.
 */
public class LotOrderPageBeanInfo extends HwPublicBean<LotOrderPageBeanInfo> {


    /**
     * 开始章节
     */
    public String startChapter;

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
     * 批量下载章节选项,如果是单本订购，第一条信息则是单本订购的信息
     */
    public ArrayList<LotOrderPageBean> lotOrderPageBeans;

    /**
     * VIP 提示
     */
    public String vipTips;

    /**
     * 书名
     */
    public String bookName;

    /**
     * 书籍id
     */
    public String bookId;

    /**
     * 作者
     */
    public String author;

    /**
     * json
     */
    public String jsonStr;


    @Override
    public LotOrderPageBeanInfo parseJSON(JSONObject jsonObj) {
        super.parseJSON(jsonObj);

        if (isSuccess()) {
            jsonStr = jsonObj.toString();


            JSONObject dataJson = jsonObj.optJSONObject("data");
            if (dataJson != null) {

                startChapter = dataJson.optString("startChapter");
                unit = dataJson.optInt("unit");
                remain = dataJson.optInt("remain");
                vouchers = dataJson.optInt("vouchers");
                priceUnit = dataJson.optString("priceUnit");
                vUnit = dataJson.optString("vUnit");
                vipTips = dataJson.optString("vipTips");

                bookId =  dataJson.optString("bookId");
                bookName = dataJson.optString("bookName");
                author = dataJson.optString("author");

                JSONArray array = dataJson.optJSONArray("lotsTips");
                if (array != null) {
                    this.lotOrderPageBeans = new ArrayList<>();
                    int length = array.length();
                    for (int i = 0; i < length; i++) {
                        JSONObject obj = array.optJSONObject(i);
                        if (obj != null) {
                            LotOrderPageBean bean = new LotOrderPageBean();
                            this.lotOrderPageBeans.add(bean.parseJSON(obj));
                        }
                    }
                }
            }
        }

        return this;
    }

    /**
     * 是否存在批量数据
     *
     * @return boolean
     */
    public boolean isExistLotData() {
        return lotOrderPageBeans != null && lotOrderPageBeans.size() > 0;
    }

    /**
     * 是否单本书
     *
     * @return boolean
     */
    public boolean isSingleBook() {
        return unit == 1;
    }
}
