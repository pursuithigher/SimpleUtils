package com.dzpay.recharge.netbean;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import hw.sdk.net.bean.HwPublicBean;

/**
 * 订购-批量订购接
 *
 * @author lizz 2018/4/14.
 */
public class LotPayOrderBeanInfo extends HwPublicBean<LotPayOrderBeanInfo> {

    /**
     * 书籍id
     */
    public String bookId;

    /**
     * 章节数据
     */
    public ArrayList<PayOrderChapterBeanInfo> chapterInfos;

    /**
     * json
     */
    public String jsonStr = "";


    @Override
    public LotPayOrderBeanInfo parseJSON(JSONObject jsonObj) {
        super.parseJSON(jsonObj);

        if (isSuccess()) {
            jsonStr = jsonObj.toString();

            JSONObject dataJsonObj = jsonObj.optJSONObject("data");
            if (dataJsonObj != null) {
                bookId = dataJsonObj.optString("bookId");

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
     * 是否存在章节数据
     *
     * @return boolean
     */
    public boolean isExistChapterData() {
        return chapterInfos != null && chapterInfos.size() > 0;
    }
}
