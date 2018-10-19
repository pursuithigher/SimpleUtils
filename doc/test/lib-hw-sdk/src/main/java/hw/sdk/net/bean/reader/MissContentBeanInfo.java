package hw.sdk.net.bean.reader;

import org.json.JSONObject;

import hw.sdk.net.bean.HwPublicBean;

/**
 * MissContentBeanInfo
 * @author lizz 2018/4/19.
 */

public class MissContentBeanInfo extends HwPublicBean<MissContentBeanInfo> {


    /**
     * 提示语，参考以下值。
     */
    public String tips;

    /**
     * 奖励看点数量
     */
    public int amount;


    @Override
    public MissContentBeanInfo parseJSON(JSONObject jsonObj) {
        super.parseJSON(jsonObj);

        if (isSuccess()) {
            JSONObject dataJson = jsonObj.optJSONObject("data");
            if (dataJson != null) {
                tips = dataJson.optString("tips");
                amount = dataJson.optInt("amount");
            }
        }

        return this;

    }
}
