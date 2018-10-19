package hw.sdk.net.bean.gift;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import hw.sdk.net.bean.HwPublicBean;

/**
 * 礼物列表bean
 * @author KongXP 2018/4/25.
 */

public class GiftListBeanInfo extends HwPublicBean<GiftListBeanInfo> {

    /**
     * 礼物列表bean
     */
    public List<GiftListBean> giftListBeans;

    /**
     * 1：是（当前下发了数据）2：无数据下发
     */
    //public int isExist;
    @Override
    public GiftListBeanInfo parseJSON(JSONObject jsonObj) {
        super.parseJSON(jsonObj);

        JSONObject dataJsonObj = jsonObj.optJSONObject("data");
        if (dataJsonObj != null) {
            //isExist = dataJsonObj.optInt("isExist");

            JSONArray array = dataJsonObj.optJSONArray("gifts");
            if (array != null) {
                this.giftListBeans = new ArrayList<GiftListBean>();
                int length = array.length();
                for (int i = 0; i < length; i++) {
                    JSONObject obj = array.optJSONObject(i);
                    if (obj != null) {
                        GiftListBean bean = new GiftListBean();
                        this.giftListBeans.add(bean.parseJSON(obj));
                    }
                }
            }
        }

        return this;
    }


    /**
     * 是否存在记录
     *
     * @return 是否存在记录
     */
    public boolean isExistData() {
        return giftListBeans != null && giftListBeans.size() > 0;
    }
}

