package hw.sdk.net.bean.tms;

import org.json.JSONObject;

import hw.sdk.net.bean.HwPublicBean;

/**
 * 签署协议 反馈
 * @author winzows on 2018/4/13
 */

public class BeanTmsRespSignInfo extends HwPublicBean<BeanTmsRespSignInfo> {
    /**
     * 协议类型。
     */
    public int agrType;
    /**
     * 国家
     */
    public String country;
    /**
     * 语言
     */
    public String language;
    /**
     * 客户签署的版本号。
     */
    public long version;
    /**
     * 客户签署协议的时间
     */
    public long signTime;
    /**
     * 签署结果。
     */
    public boolean isAgree;
    /**
     * 最新协议版本号。
     */
    public long latestVersion;

    /**
     * 是否需要重新签署
     */
    public boolean needSign;


    @Override
    public BeanTmsRespSignInfo parseJSON(JSONObject jsonObj) {
        super.parseJSON(jsonObj);
        agrType = jsonObj.optInt("agrType");
        country = jsonObj.optString("country");
        language = jsonObj.optString("language");
        version = jsonObj.optLong("version");
        signTime = jsonObj.optLong("signTime");
        isAgree = jsonObj.optBoolean("isAgree", false);
        needSign = jsonObj.optBoolean("needSign", true);
        latestVersion = jsonObj.optLong("latestVersion");
        return this;
    }
}
