package hw.sdk.net.bean.register;

import org.json.JSONObject;

import hw.sdk.net.bean.HwPublicBean;

/**
 * 注册bean
 * @author lizz 2018/4/13.
 */

public class RegisterBeanInfo extends HwPublicBean<RegisterBeanInfo> {

    /**
     * 用户id
     */
    public String userId;

    /**
     * 访问token
     */
    public String token;

    /**
     * 用户信息
     */
    private UserInfoBean userInfoBean;

    @Override
    public RegisterBeanInfo parseJSON(JSONObject jsonObj) {
        super.parseJSON(jsonObj);

        if (isSuccess()) {
            JSONObject dataJson = jsonObj.optJSONObject("data");
            userId = dataJson.optString("userId");
            token = dataJson.optString("t");

            userInfoBean = new UserInfoBean().parseJSON(dataJson);
        }
        return this;
    }

    @Override
    public String toString() {
        return "RegisterBeanInfo{"
                +
                "userId='"
                + userId
                + '\''
                +
                ", token='"
                + token + '\''
                +
                '}' + (getUserInfoBean() != null ? getUserInfoBean().toString() : "");
    }


    public UserInfoBean getUserInfoBean() {
        return userInfoBean;
    }
}
