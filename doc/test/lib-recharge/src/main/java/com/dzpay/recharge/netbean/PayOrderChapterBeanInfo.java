package com.dzpay.recharge.netbean;

import android.text.TextUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import hw.sdk.net.bean.HwPublicBean;

/**
 * 下发章节公共解析bean
 *
 * @author lizz 2018/4/14.
 * <p>
 */
public class PayOrderChapterBeanInfo extends HwPublicBean<PayOrderChapterBeanInfo> {

    /**
     * 章节id
     */
    public String chapterId;

    /**
     * 章节状态
     * 1.正常 2.被删除 3.缺内容，未领取 4.缺内容，已领取
     */
    public Integer chapterStatus;

    /**
     * 章节内容文件对应的cdn地址
     */
    public List<String> backupUrls;

    /**
     * cdn地址
     */
    public String cdnUrl;

    /**
     * 此章节扣费数量
     */
    public Double cost;

    /**
     * 订购-后台多章加载需要
     * 代金券花费数量
     */
    public int vCost;

    /**
     * 订购-后台多章加载需要
     * 看点花费数量
     */
    public int rCost;


    @Override
    public PayOrderChapterBeanInfo parseJSON(JSONObject jsonObj) {

        if (jsonObj != null) {
            this.chapterId = jsonObj.optString("chapterId");
            this.chapterStatus = jsonObj.optInt("chapterStatus");
            this.cost = jsonObj.optDouble("cost");

            this.vCost = jsonObj.optInt("vCost");
            this.rCost = jsonObj.optInt("rCost");

            JSONArray array = jsonObj.optJSONArray("cdnUrls");
            if (array != null) {
                this.backupUrls = new ArrayList<String>();
                int length = array.length();
                for (int i = 0; i < length; i++) {
                    String url = array.optString(i);
                    if (!TextUtils.isEmpty(url)) {
                        if (i == 0) {
                            this.cdnUrl = url;
                        } else {
                            this.backupUrls.add(url);
                        }
                    }
                }
            }
        }

        return this;
    }

    @Override
    public String toString() {
        return "chapterId：" + chapterId + "，chapterStatus：" + chapterStatus + "，cost：" + cost;
    }
}
