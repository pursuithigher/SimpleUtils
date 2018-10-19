package com.dzpay.recharge.netbean;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import hw.sdk.net.bean.HwPublicBean;

/**
 * 订购-单章加载 接口解析bean
 * @author lizz 2018/4/14.
 *
 */
public class SingleOrderBeanInfo extends HwPublicBean<SingleOrderBeanInfo> {

    /**
     * 书籍id
     */
    public String bookId;

    /**
     * 1.扣费成功
     * 2.不需要付费-免费章节 包括限免书籍，包括章节缺失
     * 3.不需要付费-之前已经付费过
     * 4:扣费失败-余额不足去充值
     * 5.需要确认弹窗（余额足的情况）
     */
    public Integer status;

    /**
     * 预加载的数量
     * 默认为 1
     */
    public Integer preloadNum;

    /**
     * 章节数据
     */
    public ArrayList<PayOrderChapterBeanInfo> chapterInfos;

    /**
     * 订购页面数据
     */
    public SingleOrderPageBean orderPage;

    /**
     * json数据
     */
    public String jsonStr;

    @Override
    public SingleOrderBeanInfo parseJSON(JSONObject jsonObj) {
        super.parseJSON(jsonObj);

        if (isSuccess()) {
            jsonStr = jsonObj.toString();


            JSONObject dataJsonObj = jsonObj.optJSONObject("data");
            if (dataJsonObj != null) {
                bookId = dataJsonObj.optString("bookId");
                status = dataJsonObj.optInt("status");
                preloadNum = dataJsonObj.optInt("preloadNum");

                JSONObject jsonOrderPage = dataJsonObj.optJSONObject("orderPage");
                if (jsonOrderPage != null) {
                    this.orderPage = new SingleOrderPageBean();
                    this.orderPage.parseJSON(jsonOrderPage);
                }

                JSONArray array = dataJsonObj.optJSONArray("chapterInfo");
                if (array != null) {
                    this.chapterInfos = new ArrayList<PayOrderChapterBeanInfo>();
                    int length = array.length();
                    for (int i = 0; i < length; i++) {
                        JSONObject obj = array.optJSONObject(i);
                        if (obj != null) {
                            PayOrderChapterBeanInfo bean = new PayOrderChapterBeanInfo();
                            this.chapterInfos.add(bean.parseJSON(obj));
                        }
                    }
                }
            }
        }


        return this;
    }


    /**
     * 批量订购检查是否不需要付费
     *
     * @return boolean
     */
    public boolean lotIsNotNeedPay() {
        return status == 1 || status == 2 || status == 3;
    }

    /**
     * 是否直接加入书架
     *
     * @return boolean
     */
    public boolean isAddBookShelf() {
        return status == 1 || status == 3;
    }

    /**
     * 单章支付检查是否直接返回成功
     *
     * @return boolean
     */
    public boolean singleCheckIsSuccess() {
        return status == 1 || status == 2 || status == 3;
    }

    /**
     * 单章支付检查是否弹出确认订购
     *
     * @return boolean
     */
    public boolean singleCheckIsGoToOrder() {
        return status == 4 || status == 5;
    }

    /**
     * 支付成功
     *
     * @return boolean
     */
    public boolean isPaySuccess() {
        return status == 1;
    }

    /**
     * 是否存在章节数据
     *
     * @return boolean
     */
    public boolean isExistChapterInfos() {
        return chapterInfos != null && chapterInfos.size() > 0;
    }

}
