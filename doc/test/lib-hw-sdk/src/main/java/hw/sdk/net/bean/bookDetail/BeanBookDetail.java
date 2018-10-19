package hw.sdk.net.bean.bookDetail;

import org.json.JSONObject;

import java.util.ArrayList;

import hw.sdk.net.bean.BeanBookInfo;
import hw.sdk.net.bean.BeanChapterInfo;
import hw.sdk.net.bean.HwPublicBean;
import hw.sdk.utils.JsonUtils;

/**
 * 书籍详情bean
 *
 * @author caimantang on 2018/4/13.
 */

public class BeanBookDetail extends HwPublicBean {
    /**
     * 作者的其他书
     */
    public ArrayList<BeanBookInfo> authorOtherBooks;
    /**
     * 推荐
     */
    public ArrayList<BeanBookInfo> recommendBooks;
    /**
     * 章节信息
     */
    public ArrayList<BeanChapterInfo> chapters;
    /**
     * 评论
     */
    public ArrayList<BeanCommentInfo> comments;
    /**
     * 书
     */
    public BeanBookInfo book;
    /**
     * 是否被删
     */
    public boolean isDelect = false;
    //0没有跟多作者书籍 1.有
    /**
     * 有没有更多书籍
     */
    public int moreAuthor;
    /**
     * 有没有推荐
     */
    public int moreRecommend;
    /**
     * 最后一章的信息
     */
    public BeanChapterInfo lastChapter;

    @Override
    public BeanBookDetail parseJSON(JSONObject jsonObj) {
        super.parseJSON(jsonObj);
        JSONObject data = null;
        if (isSuccess() && null != (data = jsonObj.optJSONObject("data"))) {
            this.authorOtherBooks = JsonUtils.getBookList(data.optJSONArray("authorOtherBooks"));
            this.recommendBooks = JsonUtils.getBookList(data.optJSONArray("recommendBooks"));
            this.chapters = JsonUtils.getChapterList(data.optJSONArray("chapters"));
            this.comments = JsonUtils.getCommentList(data.optJSONArray("comments"));
            this.book = new BeanBookInfo().parseJSON(data.optJSONObject("book"));
            this.lastChapter = new BeanChapterInfo().parseJSON(data.optJSONObject("lastChapter"));
            this.moreAuthor = data.optInt("moreAuthor");
            this.moreRecommend = data.optInt("moreRecommend");
        } else {
            isDelect = getRetCode() == 3000;
        }
        return this;
    }

    @Override
    public String toString() {
        return "BookDetailBean{"
                +
                "authorOtherBooks="
                + authorOtherBooks
                +
                ", recommendBooks="
                + recommendBooks
                +
                ", chapters="
                + chapters
                +
                ", book="
                + book
                +
                ", lastChapter="
                + lastChapter
                + '}';
    }
}
