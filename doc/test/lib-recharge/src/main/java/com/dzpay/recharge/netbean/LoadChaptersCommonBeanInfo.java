package com.dzpay.recharge.netbean;

import android.text.TextUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import hw.sdk.net.bean.HwPublicBean;

/**
 * 加载章节
 *
 * @author lizz 2018/4/17.
 */

public class LoadChaptersCommonBeanInfo extends HwPublicBean {

    /**
     * 书籍id
     */
    public String bookId;

    /**
     * 章节数据
     */
    public ArrayList<PayOrderChapterBeanInfo> chapterInfos;

    /**
     * 预加载的数量,客户端默认为1
     */
    public Integer preloadNum;


    @Override
    public LoadChaptersCommonBeanInfo parseJSON(JSONObject jsonObj) {
        super.parseJSON(jsonObj);

        if (isSuccess()) {
            JSONObject dataJsonObj = jsonObj.optJSONObject("data");
            if (dataJsonObj != null) {
                bookId = dataJsonObj.optString("bookId");
                preloadNum = dataJsonObj.optInt("preloadNum", 1);

                JSONArray array = dataJsonObj.optJSONArray("chapterInfo");
                if (array != null) {
                    this.chapterInfos = new ArrayList<PayOrderChapterBeanInfo>();
                    int length = array.length();
                    for (int i = 0; i < length; i++) {
                        JSONObject obj = array.optJSONObject(i);
                        if (obj != null) {
                            PayOrderChapterBeanInfo bean = new PayOrderChapterBeanInfo();
                            this.chapterInfos.add(bean.parseJSON(obj));
                        }
                    }
                }
            }
        }

        return this;
    }

    /**
     * 是否存在章节数据
     *
     * @return boolean
     */
    public boolean isExistChapterData() {
        return chapterInfos != null && chapterInfos.size() > 0;
    }

    /**
     * 获取需要下载的章节id
     *
     * @return List
     */
    public List<String> getCatalogIds() {
        List<String> catalogList = null;
        if (isExistChapterData()) {
            catalogList = new ArrayList<>();
            for (PayOrderChapterBeanInfo bean : chapterInfos) {
                if (bean != null && !TextUtils.isEmpty(bean.chapterId)) {
                    catalogList.add(bean.chapterId);
                }
            }
        }
        return catalogList;
    }
}
