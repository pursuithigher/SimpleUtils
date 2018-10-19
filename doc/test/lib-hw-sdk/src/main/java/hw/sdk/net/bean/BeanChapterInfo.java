package hw.sdk.net.bean;

import org.json.JSONObject;

import java.io.Serializable;


/**
 * 书籍章节bean
 *
 * @author winzows
 */
public class BeanChapterInfo implements Serializable {
    /**
     * 书籍id
     */
    public String bookId;
    /**
     * 章节id
     */
    public String chapterId;
    /**
     * 章节名字
     */
    public String chapterName;
    /**
     * 书籍地址
     */
    public String chapterUrl;
    /**
     * 书籍内容
     */
    public String content;
    /**
     * 章节是否收费：0：免费 1：收费
     */
    public String isCharge;

    /**
     * 解析bean
     *
     * @param jsonObj json
     * @return bean
     */
    public BeanChapterInfo parseJSON(JSONObject jsonObj) {
        if (null != jsonObj) {
            this.bookId = jsonObj.optString("bookId");
            this.chapterId = jsonObj.optString("chapterId");
            this.chapterName = jsonObj.optString("chapterName");
            this.chapterUrl = jsonObj.optString("chapterUrl");
            this.isCharge = jsonObj.optString("isCharge");
            this.content = jsonObj.optString("content");
        }
        return this;
    }

    @Override
    public String toString() {
        return "BeanChapterInfo{"
                +
                "bookId='"
                + bookId
                + '\''
                +
                ", chapterId='"
                + chapterId
                + '\''
                +
                ", chapterName='"
                + chapterName
                + '\''
                +
                ", chapterUrl='"
                + chapterUrl
                + '\''
                +
                ", isCharge='"
                + isCharge
                + '\'' + '}';
    }
}
