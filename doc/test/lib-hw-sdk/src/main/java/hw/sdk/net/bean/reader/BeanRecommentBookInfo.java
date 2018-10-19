package hw.sdk.net.bean.reader;


import org.json.JSONObject;

import java.util.List;

import hw.sdk.net.bean.BeanBookInfo;
import hw.sdk.net.bean.HwPublicBean;
import hw.sdk.utils.JsonUtils;

/**
 * 推荐bean
 *
 * @author caimantang on 2018/4/16.
 */

public class BeanRecommentBookInfo extends HwPublicBean {
    /**
     * logName
     */
    public String logName;
    /**
     * name
     */
    public String name;
    /**
     * url
     */
    public String url;
    /**
     * moreName
     */
    public String moreName;
    /**
     * moreType
     */
    public int moreType;
    /**
     * size
     */
    public int size;
    /**
     * type
     */
    public int type;
    /**
     * hasMore
     */
    public int hasMore;
    /**
     * books
     */
    public List<BeanBookInfo> books;

    public boolean isMore() {
        return hasMore == 1;
    }

    public boolean isBsJump() {
        return type == 2;
    }

    @Override
    public BeanRecommentBookInfo parseJSON(JSONObject jsonObj) {
        super.parseJSON(jsonObj);
        if (null != jsonObj) {
            this.logName = jsonObj.optString("logName");
            this.name = jsonObj.optString("name");
            this.url = jsonObj.optString("url");
            this.moreName = jsonObj.optString("moreName");
            this.moreType = jsonObj.optInt("moreType");
            this.size = jsonObj.optInt("size");
            this.type = jsonObj.optInt("type");
            this.hasMore = jsonObj.optInt("hasMore");
            this.books = JsonUtils.getBookList(jsonObj.optJSONArray("books"));

        }
        return this;
    }
}
