package hw.sdk.net.bean.task;

import org.json.JSONObject;

import hw.sdk.net.bean.HwPublicBean;

/**
 * 分享领看点
 * @author caimantang on 2018/4/22.
 */

public class ShareKd extends HwPublicBean<ShareKd> {
    /**
     * status
     */
    public int status;
    /**
     * 提示语
     */
    public String msg;

    @Override
    public ShareKd parseJSON(JSONObject jsonObj) {
        super.parseJSON(jsonObj);
        JSONObject data;
        if (isSuccess() && null != (data = jsonObj.optJSONObject("data"))) {
            status = data.optInt("status");
            msg = data.optString("msg");
        }
        return this;
    }
}
