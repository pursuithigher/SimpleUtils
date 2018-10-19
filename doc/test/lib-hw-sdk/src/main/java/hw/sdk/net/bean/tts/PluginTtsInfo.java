package hw.sdk.net.bean.tts;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;

/**
 * 语音tts bean
 *
 * @author wxliao 18/1/16
 */
public class PluginTtsInfo implements Serializable {
    /**
     * version
     */
    public int version;
    /**
     * zipUrl
     */
    public String zipUrl;
    /**
     * baseFileName
     */
    public String baseFileName;
    /**
     * appId
     */
    public String appId;
    /**
     * appKey
     */
    public String appKey;
    /**
     * secretKey
     */
    public String secretKey;
    /**
     * voiceTypeList
     */
    public ArrayList<VoiceType> voiceTypeList;
    /**
     * cachePath
     */

    public String cachePath;

    /**
     * updateTime
     */
    public long updateTime;

    /**
     * 构造器
     */
    public PluginTtsInfo() {

    }

    /**
     * 构造器
     *
     * @param info info
     */
    public PluginTtsInfo(PluginTtsInfo info) {
        if (info != null) {
            version = info.version;
            zipUrl = info.zipUrl;
            baseFileName = info.baseFileName;
            appId = info.appId;
            appKey = info.appKey;
            secretKey = info.secretKey;
            cachePath = info.cachePath;
            updateTime = info.updateTime;
            voiceTypeList = new ArrayList<>(info.voiceTypeList);
        }
    }

    /**
     * 构造器
     *
     * @param jsonObj info
     * @return PluginTtsInfo
     */
    public PluginTtsInfo parseJSON(JSONObject jsonObj) {
        if (jsonObj == null) {
            return null;
        }
        version = jsonObj.optInt("version");
        zipUrl = jsonObj.optString("zipUrl");
        baseFileName = jsonObj.optString("baseFileName");
        appId = jsonObj.optString("appId");
        appKey = jsonObj.optString("appKey");
        secretKey = jsonObj.optString("secretKey");
        cachePath = jsonObj.optString("cachePath");
        updateTime = jsonObj.optLong("updateTime");

        JSONArray array = jsonObj.optJSONArray("voiceTypeList");
        if (array != null) {
            voiceTypeList = new ArrayList<>();
            for (int i = 0; i < array.length(); i++) {
                try {
                    JSONObject typeObject = array.getJSONObject(i);
                    int index = typeObject.optInt("index");
                    String fileName = typeObject.optString("fileName");
                    voiceTypeList.add(new VoiceType(index, fileName));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        return this;
    }

    /**
     * 转成json
     *
     * @return JSONObject
     */
    public JSONObject toJSON() {
        try {
            JSONObject obj = new JSONObject();
            obj.put("version", version);
            obj.put("zipUrl", zipUrl);
            obj.put("baseFileName", baseFileName);
            obj.put("appId", appId);
            obj.put("appKey", appKey);
            obj.put("secretKey", secretKey);
            obj.put("cachePath", cachePath);
            obj.put("updateTime", updateTime);
            if (voiceTypeList != null && voiceTypeList.size() > 0) {
                JSONArray array = new JSONArray();
                for (VoiceType type : voiceTypeList) {
                    JSONObject typeObject = new JSONObject();
                    typeObject.put("index", type.index);
                    typeObject.put("fileName", type.fileName);
                    array.put(typeObject);
                }
                obj.put("voiceTypeList", array);
            }
            return obj;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * getVoiceFilePath
     *
     * @param index index
     * @return path
     */
    public String getVoiceFilePath(int index) {
        if (voiceTypeList == null) {
            return null;
        }
        for (VoiceType voiceType : voiceTypeList) {
            if (voiceType.index == index) {
                return cachePath + File.separator + voiceType.fileName;
            }
        }
        return null;
    }

    public String getBaseFilePath() {
        return cachePath + File.separator + baseFileName;
    }

}
