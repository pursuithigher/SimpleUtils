package hw.sdk.net.bean.register;

import com.dzbook.lib.utils.ALog;

import org.json.JSONObject;

import hw.sdk.HwSdkAppConstant;
import hw.sdk.net.bean.HwPublicBean;

/**
 * 设备激活bean
 * @author lizz 2018/4/13.
 */

public class DeviceActivationBeanInfo extends HwPublicBean<DeviceActivationBeanInfo> {
    /**
     * 省份
     */
    public String province;
    /**
     * 城市
     */
    public String city;
    /**
     * 时间
     */
    public String ctime;

    @Override
    public DeviceActivationBeanInfo parseJSON(JSONObject jsonObj) {
        super.parseJSON(jsonObj);
        JSONObject data = null;
        if (isSuccess() && null != (data = jsonObj.optJSONObject("data"))) {
            province = data.optString("province");
            city = data.optString("city");
            ctime = data.optString("ctime");

            int abKey = data.optInt("abKey", 0);

            ALog.dZz("switch abKey:" + abKey);
            if (abKey == 1) {
                HwSdkAppConstant.setIsAbKey(true);
            }
        }
        return this;
    }
}
