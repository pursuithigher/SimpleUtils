package hw.sdk.net.bean.tts;

import org.json.JSONObject;

import java.io.Serializable;

/**
 * PluginWpsInfo
 * @author Created by wxliao on 18/4/26.
 */

public class PluginWpsInfo implements Serializable {
    /**
     * versionCode
     */
    public int versionCode;
    /**
     * packageName
     */
    public String packageName;
    /**
     * downloadUrl
     */
    public String downloadUrl;

    /**
     * parseJSON
     * @param jsonObj jsonObj
     * @return PluginWpsInfo
     */
    public PluginWpsInfo parseJSON(JSONObject jsonObj) {
        if (jsonObj == null) {
            return null;
        }
        versionCode = jsonObj.optInt("versionCode");
        packageName = jsonObj.optString("packageName");
        downloadUrl = jsonObj.optString("downloadUrl");
        return this;
    }
}
