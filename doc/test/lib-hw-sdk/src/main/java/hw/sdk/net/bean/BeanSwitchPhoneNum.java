package hw.sdk.net.bean;

import android.text.TextUtils;

import org.json.JSONObject;

/**
 * 切换手机绑定
 * @author winzows on 2018/4/17
 */

public class BeanSwitchPhoneNum extends HwPublicBean<BeanSwitchPhoneNum> {
    /**
     * 手机号码
     */
    public String phoneNum;

    /**
     * 头像url
     */
    public String imgUrl;
    /**
     * type == 1 手机号变更
     * type 其他是没有手机号 需要绑定
     */
    public int type;

    @Override
    public BeanSwitchPhoneNum parseJSON(JSONObject jsonObj) {
        super.parseJSON(jsonObj);
        if (isSuccess()) {
            JSONObject dataObj = jsonObj.optJSONObject("data");
            phoneNum = dataObj.optString("phoneNum");
            imgUrl = dataObj.optString("imgUrl");
            type = dataObj.optInt("type", 2);
        }
        return this;
    }

    /**
     * 是否是切换手机号
     *
     * @return 是否是切换手机号
     */
    public boolean isSwitch() {
        return type == 1;
    }

    /**
     * 校验数据
     * @return  校验数据
     */
    public boolean checkBindData() {
        return super.isSuccess() && !TextUtils.isEmpty(phoneNum);
    }
}
