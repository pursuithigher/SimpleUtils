package hw.sdk.net.bean;

import org.json.JSONObject;

/**
 * push token 上传
 *
 * @author winzows on 2018/4/29
 */

public class BeanCidUpload extends HwPublicBean<BeanCidUpload> {
    private int result;

    @Override
    public BeanCidUpload parseJSON(JSONObject jsonObj) {
        super.parseJSON(jsonObj);
        if (super.isSuccess()) {
            JSONObject jsonObject = jsonObj.optJSONObject("data");
            result = jsonObject.optInt("result");
        }
        return this;
    }

    @Override
    public boolean isSuccess() {
        return super.isSuccess() && result == 1;
    }
}
