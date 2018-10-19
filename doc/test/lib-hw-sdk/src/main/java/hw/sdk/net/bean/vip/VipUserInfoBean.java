package hw.sdk.net.bean.vip;

import org.json.JSONObject;

import hw.sdk.net.bean.HwPublicBean;

/**
 * vip用户信息bean
 * @author gavin
 */
public class VipUserInfoBean extends HwPublicBean {


    /**
     * 当前有效期
     */
    public String deadLine;

    /**
     * 用户名
     */
    public String name;

    /**
     * 用户头像
     */
    public String userIcon;

    /**
     * 是否领取过福利，1代表已领取
     */
    public int isReceived;
    /**
     * 是否已经是VIP
     */
    public int isVip;


    @Override
    public VipUserInfoBean parseJSON(JSONObject jsonObj) {
        JSONObject dataJsonObj = jsonObj.optJSONObject("vipInfo");
        if (dataJsonObj == null) {
            return null;
        }
        deadLine = dataJsonObj.optString("vipTime");
        name = dataJsonObj.optString("vipName");
        userIcon = dataJsonObj.optString("vipIcon");
        isReceived = dataJsonObj.optInt("isAward");
        isVip = dataJsonObj.optInt("isVip");
        return this;
    }

}
