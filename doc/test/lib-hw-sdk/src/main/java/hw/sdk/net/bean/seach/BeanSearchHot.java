package hw.sdk.net.bean.seach;

import org.json.JSONObject;

import java.util.ArrayList;

import hw.sdk.net.bean.HwPublicBean;
import hw.sdk.utils.JsonUtils;

/**
 * 搜索热点词
 * @author caimantang on 2018/4/15.
 */

public class BeanSearchHot extends HwPublicBean<BeanSearchHot> {
    /**
     * 搜索热词
     */
    public ArrayList<BeanKeywordHotVo> keywordHot;
    /**
     * 默认关键词
     */
    public ArrayList<String> keywordDefault;

    @Override
    public BeanSearchHot parseJSON(JSONObject jsonObj) {
        super.parseJSON(jsonObj);
        JSONObject data = null;
        if (isSuccess() && null != (data = jsonObj.optJSONObject("data"))) {
            this.keywordHot = JsonUtils.getHotKeyList(data.optJSONArray("keywordHot"));
            this.keywordDefault = JsonUtils.getStringList(data.optJSONArray("keywordDefault"));

        }
        return this;
    }

    /**
     * 是否存在编辑框热词或者存在热门搜索
     *
     * @return 是否存在编辑框热词或者存在热门搜索
     */
    public boolean isExistData() {
        return isExistSearchEditKey() || isExistSearchHotKeys();
    }


    @Override
    public String toString() {
        return "BeanSearchHot{"
                +
                "keywordHot="
                + keywordHot
                +
                ", keywordDefault="
                + keywordDefault
                +
                '}';
    }

    public boolean isExistSearchEditKey() {
        return null != keywordDefault && keywordDefault.size() > 0;
    }

    public boolean isExistSearchHotKeys() {
        return null != keywordHot && keywordHot.size() > 0;
    }
}
