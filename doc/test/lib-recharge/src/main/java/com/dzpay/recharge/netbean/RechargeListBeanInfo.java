package com.dzpay.recharge.netbean;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import hw.sdk.net.bean.HwPublicBean;

/**
 * 充值列表bean
 * @author lizz 2018/4/14.
 */

public class RechargeListBeanInfo extends HwPublicBean {

    /**
     * 金额集合
     */
    public List<RechargeProductBean> productBeans;

    /**
     * json数据
     */
    public String json = "";

    @Override
    public RechargeListBeanInfo parseJSON(JSONObject jsonObj) {
        super.parseJSON(jsonObj);

        if (isSuccess()) {
            json = jsonObj.toString();

            JSONObject dataJson = jsonObj.optJSONObject("data");
            JSONArray productsArray = dataJson.optJSONArray("products");
            if (productsArray != null && productsArray.length() > 0) {
                productBeans = new ArrayList<>();
                int length = productsArray.length();
                for (int i = 0; i < length; i++) {
                    JSONObject mJsonObj = productsArray.optJSONObject(i);
                    if (mJsonObj != null) {
                        productBeans.add(new RechargeProductBean().parseJSON(mJsonObj));
                    }
                }
            }

        }

        return this;
    }


    /**
     * 是否存在数据
     *
     * @return boolean
     */
    public boolean isExistProductData() {
        return productBeans != null && productBeans.size() > 0;
    }

    /**
     * 得到选中的位置
     *
     * @return int
     */
    public int getSelectedProductPostion() {
        int position = 0;
        int size = productBeans.size();
        for (int i = 0; i < size; i++) {
            RechargeProductBean bean = productBeans.get(i);
            if (bean != null && bean.isSelected()) {
                position = i;
                break;
            }
        }
        return position;
    }


}
