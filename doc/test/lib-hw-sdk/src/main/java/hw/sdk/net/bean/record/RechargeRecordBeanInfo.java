package hw.sdk.net.bean.record;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import hw.sdk.net.bean.HwPublicBean;

/**
 * 充值记录
 * @author lizz 2018/4/18.
 */

public class RechargeRecordBeanInfo extends HwPublicBean<RechargeRecordBeanInfo> {

    /**
     * 充值记录bean
     */
    public List<RechargeRecordBean> recordBeans;

    /**
     * 1：是（当前下发了数据）2：无数据下发
     */
    public int isExist;

    @Override
    public RechargeRecordBeanInfo parseJSON(JSONObject jsonObj) {
        super.parseJSON(jsonObj);

        JSONObject dataJsonObj = jsonObj.optJSONObject("data");
        if (dataJsonObj != null) {
            isExist = dataJsonObj.optInt("isExist");

            JSONArray array = dataJsonObj.optJSONArray("records");
            if (array != null) {
                this.recordBeans = new ArrayList<RechargeRecordBean>();
                int length = array.length();
                for (int i = 0; i < length; i++) {
                    JSONObject obj = array.optJSONObject(i);
                    if (obj != null) {
                        RechargeRecordBean bean = new RechargeRecordBean();
                        this.recordBeans.add(bean.parseJSON(obj));
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
        return recordBeans != null && recordBeans.size() > 0;
    }
}

