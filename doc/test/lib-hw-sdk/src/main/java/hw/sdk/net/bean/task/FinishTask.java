package hw.sdk.net.bean.task;

import org.json.JSONObject;

import hw.sdk.net.bean.HwPublicBean;

/**
 * FinishTask
 * @author caimantang on 2018/4/20.
 */

public class FinishTask extends HwPublicBean<FinishTask> {
    /**
     * 结束了吗
     */
    public boolean isFinish;
    /**
     * 总共阅读时间
     */
    public int totalReadDuration;

    @Override
    public FinishTask parseJSON(JSONObject jsonObj) {
        super.parseJSON(jsonObj);
        JSONObject data = null;
        if (isSuccess()) {
            isFinish = true;
        }
        try {
            data = jsonObj.optJSONObject("data");
            if (null != data) {
                this.totalReadDuration = data.optInt("totalReadDuration", 0);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return this;
    }
}
