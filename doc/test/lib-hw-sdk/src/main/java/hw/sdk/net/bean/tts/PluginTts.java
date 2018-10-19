package hw.sdk.net.bean.tts;

import org.json.JSONObject;

import java.io.Serializable;

/**
 * 语音插件
 *
 * @author wxliao on 18/1/16
 */
public class PluginTts implements Serializable {
    /**
     * 是否可用
     */
    public int isEnable;
    /**
     * 语音插件的bean
     */
    public PluginTtsInfo ttsInfo;

    /**
     * 解析bean
     * @param jsonObj json
     * @return bean
     */
    public PluginTts parseJSON(JSONObject jsonObj) {
        if (jsonObj == null) {
            return null;
        }
        isEnable = jsonObj.optInt("isEnable");
        JSONObject obj = jsonObj.optJSONObject("ttsInfo");
        ttsInfo = new PluginTtsInfo().parseJSON(obj);
        return this;
    }

    /**
     * 是否可用
     *
     * @return isEnable
     */
    public boolean isEnable() {
        return isEnable == 1 && ttsInfo != null;
    }
}
