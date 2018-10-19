package com.dzpay.recharge.net;

import android.text.TextUtils;

import com.dzpay.recharge.bean.RechargeMsgResult;

import java.util.Map;

/**
 * 网络解析
 * @author lizz 2018/4/14.
 */
public class NetCommonParamUtils {

    /**
     * 服务器url
     */
    private static String serviceUrl = "";

    private static String appId = "";

    private static String country = "";

    private static String lang = "";

    private static String ver = "";

    private static String appVer = "";

    private static String appToken = "";

    /**
     * 用户id
     */
    private static String userId = "";

    private static String utdid = "";

    private static String pname = "";

    private static String channelCode = "";

    public static String getServiceUrl() {
        return serviceUrl;
    }

    public static String getAppId() {
        return appId;
    }

    public static String getCountry() {
        return country;
    }

    public static String getLang() {
        return lang;
    }

    public static String getVer() {
        return ver;
    }

    public static String getAppVer() {
        return appVer;
    }

    public static String getAppToken() {
        return appToken;
    }

    public static String getUserId() {
        return userId;
    }

    public static String getUtdid() {
        return utdid;
    }

    public static String getPname() {
        return pname;
    }

    public static String getChannelCode() {
        return channelCode;
    }

    /**
     * 初始化
     * @param param 参数
     */
    public static void initNetParam(Map<String, String> param) {
        clearHisParams();
        if (null == param) {
            return;
        }
        NetCommonParamUtils.serviceUrl = get(param, RechargeMsgResult.SERVICE_URL, NetCommonParamUtils.serviceUrl);
        NetCommonParamUtils.appId = get(param, RechargeMsgResult.APP_ID, NetCommonParamUtils.appId);
        NetCommonParamUtils.country = get(param, RechargeMsgResult.COUNTRY, NetCommonParamUtils.country);
        NetCommonParamUtils.lang = get(param, RechargeMsgResult.LANG, NetCommonParamUtils.lang);
        NetCommonParamUtils.ver = get(param, RechargeMsgResult.VER, NetCommonParamUtils.ver);
        NetCommonParamUtils.appVer = get(param, RechargeMsgResult.APP_VER, NetCommonParamUtils.appVer);
        NetCommonParamUtils.appToken = get(param, RechargeMsgResult.APP_TOKEN, NetCommonParamUtils.appToken);
        NetCommonParamUtils.userId = get(param, RechargeMsgResult.USER_ID, NetCommonParamUtils.userId);
        NetCommonParamUtils.utdid = get(param, RechargeMsgResult.UTD_ID, NetCommonParamUtils.utdid);
        NetCommonParamUtils.pname = get(param, RechargeMsgResult.P_NAME, NetCommonParamUtils.pname);
        NetCommonParamUtils.channelCode = get(param, RechargeMsgResult.CHANNEL_CODE, NetCommonParamUtils.channelCode);
    }

    private static String get(Map<String, String> map, String key, String def) {
        String value = map.get(key);
        return TextUtils.isEmpty(value) ? def : value;
    }

    private static void clearHisParams() {
        serviceUrl = "";
        appId = "";
        country = "";
        lang = "";
        ver = "";
        appVer = "";
        appToken = "";
        userId = "";
        utdid = "";
        pname = "";
        channelCode = "";
    }

}
