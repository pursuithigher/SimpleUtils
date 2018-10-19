package com.dzbook.lib.utils;

import android.text.TextUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

/**
 * Json-HashMap转换工具
 *
 * @author dllik
 * <p>
 * 2013-11-23
 */
public class JsonUtils {

    /**
     * map转string
     *
     * @param map map
     * @return String
     */
    public static String fromHashMap(Map<String, ?> map) {
        try {
            return getJSONObject(map).toString();
        } catch (Throwable t) {
            t.printStackTrace();
        }
        return "";
    }

    @SuppressWarnings("unchecked")
    private static JSONObject getJSONObject(Map<String, ?> map) throws JSONException {
        JSONObject json = new JSONObject();
        for (Entry<String, ?> entry : map.entrySet()) {
            Object value = entry.getValue();
            if (value instanceof HashMap<?, ?>) {
                value = getJSONObject((HashMap<String, Object>) value);
            } else if (value instanceof ArrayList<?>) {
                value = getJSONArray((ArrayList<Object>) value);
            }
            json.put(entry.getKey(), value);
        }
        return json;
    }

    @SuppressWarnings("unchecked")
    private static JSONArray getJSONArray(ArrayList<? extends Object> list) throws JSONException {
        JSONArray array = new JSONArray();
        for (Object value : list) {
            if (value instanceof HashMap<?, ?>) {
                value = getJSONObject((HashMap<String, Object>) value);
            } else if (value instanceof ArrayList<?>) {
                value = getJSONArray((ArrayList<Object>) value);
            }
            array.put(value);
        }
        return array;
    }

    /**
     * 数组json字符串转换
     *
     * @param json 数据
     * @return ArrayList
     * @throws JSONException
     */
    public static ArrayList<String> jsonToArrayListByStr(String json) {
        if (TextUtils.isEmpty(json)) {
            return null;
        }
        ArrayList<String> list = new ArrayList<String>();
        try {
            JSONArray jsonArray = new JSONArray(json);
            if (jsonArray.length() > 0) {
                for (int i = 0; i < jsonArray.length(); i++) {
                    Object o = jsonArray.get(i);
                    if (o instanceof String) {
                        list.add((String) o);
                    }
                }
            }
        } catch (Exception ignored) {
        }
        return list;
    }

}
