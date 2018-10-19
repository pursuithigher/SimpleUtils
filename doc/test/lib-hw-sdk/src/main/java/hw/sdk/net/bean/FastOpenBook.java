package hw.sdk.net.bean;

import org.json.JSONObject;

import java.util.ArrayList;

import hw.sdk.utils.JsonUtils;

/**
 * 快速打开书籍
 * @author caimantang on 2018/4/17.
 */

public class FastOpenBook extends HwPublicBean<FastOpenBook> {
    /**
     * 书籍bean
     */
    public BeanBookInfo book;
    /**
     * 章节列表
     */
    public ArrayList<BeanChapterInfo> chapterList;
    /**
     * 内容集合
     */
    public ArrayList<BeanChapterInfo> contentList;

    @Override
    public FastOpenBook parseJSON(JSONObject jsonObj) {
        super.parseJSON(jsonObj);
        JSONObject data;
        if (isSuccess() && null != (data = jsonObj.optJSONObject("data"))) {
            this.book = new BeanBookInfo().parseJSON(data.optJSONObject("book"));
            this.chapterList = JsonUtils.getChapterList(data.optJSONArray("chapterList"));
            this.contentList = JsonUtils.getChapterList(data.optJSONArray("contentList"));

        }
        return this;
    }
    /**
     * 数据是否有效
     * @return 有效数据
     */
    public boolean isAValid() {
        return !isEmpty(chapterList) && !isEmpty(chapterList);
    }
}
