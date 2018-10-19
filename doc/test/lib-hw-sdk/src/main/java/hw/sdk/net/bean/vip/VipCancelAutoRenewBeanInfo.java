package hw.sdk.net.bean.vip;

import org.json.JSONObject;

import hw.sdk.net.bean.HwPublicBean;

/**
 * Vip取消订购
 *
 * @author lizz 2018/4/18.
 */

public class VipCancelAutoRenewBeanInfo extends HwPublicBean<VipCancelAutoRenewBeanInfo> {

    /**
     * 1.取消成功，否则取消失败
     */
    public int result;

    /**
     * 取消失败或者取消成功的提示
     */
    public String tips;

    @Override
    public VipCancelAutoRenewBeanInfo parseJSON(JSONObject jsonObj) {
        super.parseJSON(jsonObj);

        if (isSuccess()) {
            JSONObject dataJsonObj = jsonObj.optJSONObject("data");
            if (dataJsonObj == null) {
                return null;
            }
            result = dataJsonObj.optInt("result");
            tips = dataJsonObj.optString("tips");
        }

        return this;
    }
}
