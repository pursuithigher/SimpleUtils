package hw.sdk.net.bean.tts;

import org.json.JSONObject;

import java.io.Serializable;

/**
 * wps
 * @author Created by wxliao on 18/4/26.
 */
public class PluginWps implements Serializable {
    /**
     * isEnable
     */
    public int isEnable;
    /**
     * wpsInfo
     */
    public PluginWpsInfo wpsInfo;

    /**
     * 解析json
     * @param jsonObj JSONObject
     * @return PluginWps
     */
    public PluginWps parseJSON(JSONObject jsonObj) {
        if (jsonObj == null) {
            return null;
        }
        isEnable = jsonObj.optInt("isEnable");
        JSONObject obj = jsonObj.optJSONObject("wpsInfo");
        wpsInfo = new PluginWpsInfo().parseJSON(obj);
        return this;
    }

    /**
     * 是否可用
     * @return isEnable
     */
    public boolean isEnable() {
        return isEnable == 1 && wpsInfo != null;
    }
}
