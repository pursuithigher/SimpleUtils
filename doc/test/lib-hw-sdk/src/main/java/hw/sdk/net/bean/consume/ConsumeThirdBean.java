package hw.sdk.net.bean.consume;

import org.json.JSONObject;

import hw.sdk.net.bean.HwPublicBean;

/**
 * 消费记录 三级bean
 * @author lizz 2018/4/18.
 */

public class ConsumeThirdBean extends HwPublicBean<ConsumeThirdBean> {

    /**
     * 消费描述信息
     */
    public String name;

    /**
     * 消费描述信息
     */
    public String consumeSum;

    /**
     * 消费时间
     */
    public String time;

    @Override
    public ConsumeThirdBean parseJSON(JSONObject jsonObj) {
        if (jsonObj == null) {
            return null;
        }
        this.name = jsonObj.optString("name");
        this.consumeSum = jsonObj.optString("consumeSum");
        this.time = jsonObj.optString("time");

        return this;
    }
}
