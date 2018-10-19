package hw.sdk.net.bean.vip;

import org.json.JSONObject;

import hw.sdk.net.bean.HwPublicBean;

/**
 * VipWellInfo
 * @author gavin
 */
public class VipWellInfo extends HwPublicBean<VipWellInfo> {
    /**
     *奖励
     */
    public int award;

    @Override
    public VipWellInfo parseJSON(JSONObject jsonObj) {
        super.parseJSON(jsonObj);
        if (isSuccess()) {
            JSONObject dataJsonObj = jsonObj.optJSONObject("data");
            if (dataJsonObj == null) {
                return null;
            }
            award = dataJsonObj.optInt("award");
        }
        return this;
    }
}
