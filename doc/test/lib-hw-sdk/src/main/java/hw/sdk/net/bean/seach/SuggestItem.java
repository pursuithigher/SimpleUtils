package hw.sdk.net.bean.seach;

import org.json.JSONObject;

import java.io.Serializable;

/**
 * 搜索建议
 * @author caimantang on 2018/4/13.
 */
public class SuggestItem implements Serializable {

    /**
     * title : 万古天帝
     * cover : null1x1/11x0/110x0/11000008404/11000008404.jpg
     * bookId : 11000008404
     * authorName : 第一神
     * type : 1
     */
    /**
     * title
     */
    public String title;
    /**
     * cover
     */
    public String cover;
    /**
     * bookId
     */
    public String bookId;
    /**
     * authorName
     */
    public String authorName;
    /**
     * type
     */
    public String type;

    /**
     * 解析json
     * @param jsonObj json
     * @return SuggestItem
     */
    public SuggestItem parseJSON(JSONObject jsonObj) {
        if (null != jsonObj) {
            this.title = jsonObj.optString("title");
            this.cover = jsonObj.optString("cover");
            this.bookId = jsonObj.optString("bookId");
            this.authorName = jsonObj.optString("authorName");
            this.type = jsonObj.optString("type");
        }
        return this;
    }

    @Override
    public String toString() {
        return "SuggestItem{"
                +
                "title='"
                + title
                + '\''
                +
                ", cover='"
                + cover
                + '\''
                +
                ", bookId='"
                + bookId
                + '\''
                +
                ", authorName='" + authorName
                + '\''
                +
                ", type='"
                + type + '\''
                +
                '}';
    }
}
