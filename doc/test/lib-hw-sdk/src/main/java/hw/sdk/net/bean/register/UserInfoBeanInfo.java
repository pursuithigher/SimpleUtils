package hw.sdk.net.bean.register;

import org.json.JSONObject;

import hw.sdk.net.bean.HwPublicBean;

/**
 * 用户注册的bean
 * @author lizz 2018/4/19.
 */

public class UserInfoBeanInfo extends HwPublicBean<UserInfoBeanInfo> {


    /**
     * 我的vip,url，客户端需要预埋一个默认地址
     */
    public String myVipUrl;
    /**
     * 用户信息
     */
    private UserInfoBean userInfoBean;

    @Override
    public UserInfoBeanInfo parseJSON(JSONObject jsonObj) {
        super.parseJSON(jsonObj);

        if (isSuccess()) {
            JSONObject dataJson = jsonObj.optJSONObject("data");
            userInfoBean = new UserInfoBean().parseJSON(dataJson);

            myVipUrl = dataJson.optString("dataJson");

        }
        return this;
    }


    public UserInfoBean getUserInfoBean() {
        return userInfoBean;
    }
}
