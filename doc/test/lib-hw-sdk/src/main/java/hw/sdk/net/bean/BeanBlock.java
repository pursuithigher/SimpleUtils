package hw.sdk.net.bean;

import org.json.JSONObject;

import java.io.Serializable;

/**
 * 分章的bean
 * @author caimantang on 2018/4/16.
 */

public class BeanBlock implements Serializable {

    /**
     * 例如:第1-20章
     */
    public String tip;

    /**
     * 最后一个章节的id
     */
    public String endId;

    /**
     * 第一个章节的id
     */
    public String startId;

    /**
     * 解析数据
     * @param jsonObject json
     * @return bean
     */
    public BeanBlock parseJSON(JSONObject jsonObject) {
        if (null != jsonObject) {
            this.tip = jsonObject.optString("tip");
            this.startId = jsonObject.optString("startId");
            this.endId = jsonObject.optString("endId");

        }
        return this;
    }
}
