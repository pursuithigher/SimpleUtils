package hw.sdk.net.bean.vouchers;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import hw.sdk.net.bean.HwPublicBean;

/**
 * 兑换券bean
 * @author KongXP 2018/4/25.
 */

public class VouchersListBeanInfo extends HwPublicBean<VouchersListBeanInfo> {

    /**
     * 兑换券集合
     */
    public List<VouchersListBean> vouchersListBeans;

    /**
     * 1：是（当前下发了数据）2：无数据下发
     */
    public int isExist;

    @Override
    public VouchersListBeanInfo parseJSON(JSONObject jsonObj) {
        super.parseJSON(jsonObj);

        JSONObject dataJsonObj = jsonObj.optJSONObject("data");
        if (dataJsonObj != null) {
            isExist = dataJsonObj.optInt("isExist");

            JSONArray array = dataJsonObj.optJSONArray("records");
            if (array != null) {
                this.vouchersListBeans = new ArrayList<VouchersListBean>();
                int length = array.length();
                for (int i = 0; i < length; i++) {
                    JSONObject obj = array.optJSONObject(i);
                    if (obj != null) {
                        VouchersListBean bean = new VouchersListBean();
                        this.vouchersListBeans.add(bean.parseJSON(obj));
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
        return vouchersListBeans != null && vouchersListBeans.size() > 0;
    }
}

