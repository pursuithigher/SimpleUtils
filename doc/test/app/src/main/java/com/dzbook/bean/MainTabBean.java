package com.dzbook.bean;

import org.json.JSONObject;

import hw.sdk.net.bean.HwPublicBean;

/**
 * MainTabBean
 * @author lizhongzhong 2018/1/16.
 */
public class MainTabBean extends HwPublicBean {

    /**
     * tab名称 shelf
     */
    public String tab;
    /**
     * 显示tab名称
     */
    public String title;

    /**
     * tab文字显示颜色
     */
    public String color;
    /**
     * 按下tab文字颜色
     */
    public String colorPressed;

    /**
     * 正常icon颜色
     */
    public String iconNormal;

    /**
     * icon按下颜色
     */
    public String iconPressed;

    /**
     * logId
     */
    public String logId;
    /**
     * glcass
     */
    public Class glcass;
    /**
     * res
     */
    public int res;
    /**
     * index
     */
    public int index;

    MainTabBean() {
    }

    /**
     * MainTabBean
     * @param index index
     * @param tab tab
     * @param logId logId
     * @param glcass glcass
     * @param res res
     * @param title title
     */
    public MainTabBean(int index, String tab, String logId, Class glcass, int res, String title) {
        this.index = index;
        this.tab = tab;
        this.logId = logId;
        this.glcass = glcass;
        this.res = res;
        this.title = title;
    }

    @Override
    public MainTabBean parseJSON(JSONObject jsonObj) {
        if (jsonObj == null) {
            return null;
        }

        tab = jsonObj.optString("tab");
        title = jsonObj.optString("title");
        color = jsonObj.optString("color");
        colorPressed = jsonObj.optString("color_pressed");
        iconNormal = jsonObj.optString("icon_normal");
        iconPressed = jsonObj.optString("icon_pressed");

        return this;
    }


    @Override
    public String toString() {
        return tab + "_" + title;
    }
}