package hw.sdk.net.bean.tts;

import org.json.JSONObject;

import hw.sdk.net.bean.HwPublicBean;

/**
 * Plugins
 * @author wxliao on 18/4/26.
 */

public class Plugins extends HwPublicBean<Plugins> {
    /**
     * tts插件
     */
    public PluginTts ttsPlugin;
    /**
     * wps插件
     */
    public PluginWps wpsPlugin;

    @Override
    public Plugins parseJSON(JSONObject jsonObj) {
        super.parseJSON(jsonObj);
        if (isSuccess()) {
            JSONObject data = jsonObj.optJSONObject("data");
            JSONObject tts = data.optJSONObject("ttsPlugin");
            ttsPlugin = new PluginTts().parseJSON(tts);
            JSONObject wps = data.optJSONObject("wpsPlugin");
            wpsPlugin = new PluginWps().parseJSON(wps);
        }
        return this;
    }
}
