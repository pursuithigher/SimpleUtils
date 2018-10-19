package hw.sdk.net.bean.seach;

import org.json.JSONObject;

import java.io.Serializable;

/**
 * 搜索关键词
 * @author caimantang on 2018/4/15.
 */

public class BeanKeywordHotVo implements Serializable {
    /**
     * 热词名称
     */
    public String name;

    /**
     * 解析bean
     * @param jsonObj json
     * @return bean
     */
    public BeanKeywordHotVo parseJSON(JSONObject jsonObj) {
        if (null != jsonObj) {
            this.name = jsonObj.optString("name");
        }
        return this;
    }
}
