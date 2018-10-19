package hw.sdk.net.bean.store;

import org.json.JSONObject;

import hw.sdk.net.bean.HwPublicBean;

/**
 * vip bean
 * @author winzows
 */
public class BeanVipInfo extends HwPublicBean<BeanVipInfo> {
    /**
     * vipName
     */
    public String vipName;
    /**
     * vipIcon
     */
    public String vipIcon;
    /**
     * vipTime
     */
    public String vipTime;
    /**
     * isVip
     */
    public int isVip;

    @Override
    public BeanVipInfo parseJSON(JSONObject jsonObj) {
        super.parseJSON(jsonObj);
        vipName = jsonObj.optString("vipName");
        vipIcon = jsonObj.optString("vipIcon");
        vipTime = jsonObj.optString("vipTime");
        isVip = jsonObj.optInt("isVip");
        return this;
    }
}
