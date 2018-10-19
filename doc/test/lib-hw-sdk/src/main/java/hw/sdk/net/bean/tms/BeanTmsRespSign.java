package hw.sdk.net.bean.tms;

import org.json.JSONObject;

import hw.sdk.net.bean.HwPublicBean;

/**
 * 签署协议的返回
 *
 * @author winzows on 2018/4/13
 */

public class BeanTmsRespSign extends HwPublicBean<BeanTmsRespSign> {
    /**
     * 网关错误码。调用成功时不带此参数
     */
    public Integer nspStatus;
    /**
     * 错误码
     */
    public Integer errorCode;
    /**
     * 错误信息
     */
    public String errorMessage;

    /**
     * 缓存的json
     */
    private JSONObject jsonObj;

    @Override
    public BeanTmsRespSign parseJSON(JSONObject jsonObject) {
        super.parseJSON(jsonObject);
        this.jsonObj = jsonObject;
        nspStatus = jsonObject.optInt("NSP_STATUS");
        errorCode = jsonObject.optInt("errorCode",-1);
        errorMessage = jsonObject.optString("errorMessage");
        return this;
    }

    @Override
    public String toString() {
        if (jsonObj != null) {
            return jsonObj.toString();
        }
        return super.toString();
    }

    @Override
    public boolean isSuccess() {
        return errorCode == 0;
    }
}
