package com.dzbook.push;

import android.content.Context;
import android.text.TextUtils;

import com.dzbook.lib.utils.ALog;
import com.dzbook.utils.SpUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 华为消息处理 工具类
 *
 * @author winzows 2018/4/16
 */

public class HwPushHandleHelper {
    private static volatile HwPushHandleHelper instance;

    private Pattern p = Pattern.compile("^[0-9]*$");

    /**
     * getInstance
     * @return HwPushHandleHelper
     */
    public static HwPushHandleHelper getInstance() {
        if (instance == null) {
            synchronized (HwPushHandleHelper.class) {
                if (instance == null) {
                    instance = new HwPushHandleHelper();
                }
            }
        }
        return instance;
    }

    /**
     * handleCmd
     * @param context context
     * @param json json
     */
    public void handleCmd(final Context context, Object json) {
        if (null == json) {
            return;
        }
        if (json instanceof String) {
            handleString(context, (String) json);
        } else if (json instanceof JSONArray) {
            handlerJSONArray(context, (JSONArray) json);
        } else if (json instanceof JSONObject) {
            handlerJSONObject(context, (JSONObject) json);
        }
    }

    private void handlerJSONObject(Context context, JSONObject json) {
        // handle cmd
        String action = json.optString("action");
        if (TextUtils.isEmpty(action)) {
            return;
        }


        // UserId匹配
        String userId = json.optString("userId");
        if (!TextUtils.isEmpty(userId)) {
            if (!match(userId, SpUtil.getinstance(context).getUserID())) {
                return;
            }
        }

        action = action.toLowerCase();
        //追更推送
        if ("a".equals(action) || "b".equals(action) || "c".equals(action) || "d".equals(action) || "e".equals(action) || "f".equals(action)) {
            BeanCloudyNotify beanCloudyNotify = new BeanCloudyNotify();
            beanCloudyNotify.parse(json);
            HwPushNotificationUtils.getInstance().addNotification(context, beanCloudyNotify);
        }
    }

    private void handlerJSONArray(Context context, JSONArray json) {
        int length = json.length();
        for (int i = 0; i < length; i++) {
            try {
                handleCmd(context, json.get(i));
            } catch (JSONException e) {
                ALog.printStackTrace(e);
            }
        }
    }

    private void handleString(Context context, String json) {
        if (0 == json.length()) {
            return;
        } else if (json.startsWith("[") && json.endsWith("]")) {
            try {
                handleCmd(context, new JSONArray(json));
                return;
            } catch (JSONException e) {
                ALog.printStackTrace(e);
            }
        } else if (json.startsWith("{") && json.endsWith("}")) {
            try {
                handleCmd(context, new JSONObject(json));
                return;
            } catch (JSONException e) {
                ALog.printStackTrace(e);
            }
        }

        HwPushNotificationUtils.getInstance().handleNotify(context, json);
    }

    /**
     * 匹配数字字符串。
     *
     * @param matchStr
     * @param localStr
     * @return
     */
    private boolean match(String matchStr, String localStr) {
        Matcher m = p.matcher(matchStr);
        // 全数字，进行完全匹配
        if (m.find()) {
            // 匹配失败
            return matchStr.equals(localStr);
        } else {
            // 非全数字，进行正则匹配
            p = Pattern.compile(matchStr);
            m = p.matcher(localStr);
            return m.find();
        }
    }
}
