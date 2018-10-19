package com.dzbook.bean;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import hw.sdk.net.bean.HwPublicBean;

/**
 * MainTabBeanInfo
 * @author lizhongzhong 2017/4/11.
 */
public class MainTabBeanInfo extends HwPublicBean {

    /**
     * mainTabBeans
     */
    public List<MainTabBean> mainTabBeans;

    /**
     * defaultEnter
     */
    public String defaultEnter;

    /**
     * defaultOut
     */
    public String defaultOut;

    /**
     * tabJson
     */
    public String tabJson;

    @Override
    public MainTabBeanInfo parseJSON(JSONObject jsonObj) {
        if (jsonObj == null) {
            return null;
        }
        tabJson = jsonObj.toString();

        JSONArray infoArray = jsonObj.optJSONArray("data");
        if (null != infoArray && infoArray.length() > 0) {
            mainTabBeans = new ArrayList<MainTabBean>();
            for (int i = 0; i < infoArray.length(); i++) {
                JSONObject jsonObj1 = infoArray.optJSONObject(i);
                if (null != jsonObj1) {
                    MainTabBean bean = new MainTabBean();
                    this.mainTabBeans.add(bean.parseJSON(jsonObj1));
                }
            }
        }

        defaultEnter = jsonObj.optString("default_enter");
        defaultOut = jsonObj.optString("default_out");

        return this;
    }

}
