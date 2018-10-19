package hw.sdk.net.bean;

import android.text.TextUtils;

import org.json.JSONObject;

/**
 * 实名认证 发送短信
 *
 * @author winzows on 2018/4/16
 */

public class BeanLoginVerifyCode extends HwPublicBean<BeanLoginVerifyCode> {
    /**
     * 判断是否验证码短信(关键词) 正则
     */
    public String mtKeyword;

    /**
     * 截取规则，正则表达式re
     */
    public String rule;

    /**
     * 提示信息
     */
    public String message;

    /**
     * 1：短信验证码发送成功 2：短信验证码发送失败 客户端只有result为2时才会提示用户message信息
     */
    public String result;

    private JSONObject jsonObj;


    @Override
    public BeanLoginVerifyCode parseJSON(JSONObject jsonObj1) {
        super.parseJSON(jsonObj1);
        this.jsonObj = jsonObj1;
        JSONObject jsonObject = jsonObj1.optJSONObject("data");
        if (jsonObject != null) {
            message = jsonObject.optString("message");
            mtKeyword = jsonObject.optString("mtKeyword");
            rule = jsonObject.optString("rule");
            result = jsonObject.optString("result");
        }

        return this;
    }

    @Override
    public String toString() {
        if (jsonObj != null) {
            return jsonObj.toString();
        }
        return "BeanLoginVerifyCode{"
                +
                "mtKeyword='"
                + mtKeyword
                + '\''
                +
                ", rule='"
                + rule
                + '\''
                +
                ", message='"
                + message
                + '\''
                +
                ", result='"
                + result + '\''
                +
                '}';
    }

    @Override
    public boolean isSuccess() {
        return super.isSuccess() && TextUtils.equals("1", result);
    }

    @Override
    public String getRetMsg() {
        if (!TextUtils.isEmpty(super.getRetMsg())) {
            return super.getRetMsg();
        }
        return message;
    }
}
