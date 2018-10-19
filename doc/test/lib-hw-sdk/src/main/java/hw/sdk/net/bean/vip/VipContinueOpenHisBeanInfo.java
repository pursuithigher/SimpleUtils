package hw.sdk.net.bean.vip;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import hw.sdk.net.bean.HwPublicBean;

/**
 * VipContinueOpenHisBeanInfo
 * @author lizz 2018/4/18.
 */

public class VipContinueOpenHisBeanInfo extends HwPublicBean<VipContinueOpenHisBeanInfo> {

    /**
     * vipContinueOpenHisBeans
     */
    public ArrayList<VipContinueOpenHisBean> vipContinueOpenHisBeans;

    @Override
    public VipContinueOpenHisBeanInfo parseJSON(JSONObject jsonObj) {
        super.parseJSON(jsonObj);

        if (isSuccess()) {
            JSONObject dataJsonObj = jsonObj.optJSONObject("data");
            if (dataJsonObj != null) {

                JSONArray array = dataJsonObj.optJSONArray("autoOrderHis");
                if (array != null) {
                    this.vipContinueOpenHisBeans = new ArrayList<VipContinueOpenHisBean>();
                    int length = array.length();
                    for (int i = 0; i < length; i++) {
                        JSONObject obj = array.optJSONObject(i);
                        if (obj != null) {
                            VipContinueOpenHisBean bean = new VipContinueOpenHisBean();
                            this.vipContinueOpenHisBeans.add(bean.parseJSON(obj));
                        }
                    }
                }
            }
        }

        return this;
    }


    /**
     * 是否存在数据
     *
     * @return 是否存在数据
     */
    public boolean isExsitData() {
        return vipContinueOpenHisBeans != null && vipContinueOpenHisBeans.size() > 0;
    }
}
