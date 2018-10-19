package hw.sdk.net.bean.store;

import org.json.JSONObject;

import hw.sdk.net.bean.HwPublicBean;

/**
 * banner bean
 * @author winzows
 */
public class BeanBannerInfo extends HwPublicBean<BeanBannerInfo> {
    /**
     * 背景图url
     */
    public String backImgUrl;
    /**
     * 主图url
     */
    public String mainImgUrl;
    /**
     * icon url
     */
    public String iconImgUrl;
    /**
     * text1
     */
    public String text1;
    /**
     * text2
     */
    public String text2;
    /**
     * text3
     */
    public String text3;

    @Override
    public BeanBannerInfo parseJSON(JSONObject jsonObj) {
        super.parseJSON(jsonObj);
        backImgUrl = jsonObj.optString("backImgUrl");
        mainImgUrl = jsonObj.optString("mainImgUrl");
        iconImgUrl = jsonObj.optString("iconImgUrl");
        text1 = jsonObj.optString("text1");
        text2 = jsonObj.optString("text2");
        text3 = jsonObj.optString("text3");
        return this;
    }
}
