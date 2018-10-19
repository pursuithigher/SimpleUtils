package hw.sdk.net.bean;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import hw.sdk.utils.JsonUtils;

/**
 * 单本书的book bean
 *
 * @author winzows
 */
public class BeanSingleBookInfo extends HwPublicBean<BeanSingleBookInfo> {
    /**
     * 书籍bean
     */
    public BeanBookInfo bookInfo;
    /**
     * 章节bean
     */
    public ArrayList<BeanChapterInfo> chapterList;

    @Override
    public BeanSingleBookInfo parseJSON(JSONObject jsonObj) {
        super.parseJSON(jsonObj);
        JSONObject bookObject = jsonObj.optJSONObject("book");
        if (bookObject != null) {
            bookInfo = new BeanBookInfo();
            bookInfo.parseJSON(bookObject);
        }
        JSONArray chapterListObject = jsonObj.optJSONArray("chapterList");
        chapterList = JsonUtils.getChapterList(chapterListObject);
        return this;
    }
}
