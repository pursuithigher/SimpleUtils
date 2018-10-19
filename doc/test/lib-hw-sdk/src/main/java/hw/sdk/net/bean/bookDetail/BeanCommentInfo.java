package hw.sdk.net.bean.bookDetail;

import org.json.JSONObject;

import hw.sdk.net.bean.HwPublicBean;

/**
 * 评论 bean
 * @author caimantang on 2018/4/15.
 */

public class BeanCommentInfo extends HwPublicBean {
    /**
     * 发评论者是否是vip
     */
    public boolean vip;

    /**
     * 评论状态(置顶-精华)
     */
    public boolean isTop;
    /**
     * 读者对这条评论是否点过赞
     */
    public boolean praise;

    /**
     * 评论内容
     */
    public String content;

    /**
     * 评论者等级
     */
    public String commenterLevel;
    /**
     * 评论者的user_id
     */
    public String uId;

    /**
     * 这条评论的id
     */
    public String commentId;
    /**
     * 评论的日期
     */
    public String date;

    /**
     * 评论者的头像
     */
    public String url;
    /**
     * 图书名称
     */
    public String bookName;
    /**
     * 书籍作者
     */
    public String author;

    /**
     * 书籍封面
     */
    public String coverWap;

    /**
     * 图书ID
     */
    public String bookId;

    /**
     * 评论打的星级分值
     */
    public float score;
    /**
     * 点赞个数
     */
    public int praiseNum;

    /**
     * 1 未审核 2 未通过 3 通过
     */
    public int commentStatus;

    /**
     * 评论者名称
     */
    public String uName;

    /**
     * 发表评论时间
     */
    public String ctime;
    /**
     * 更新时间
     */
    public String utime;

    @Override
    public HwPublicBean parseJSON(JSONObject jsonObj) {
        super.parseJSON(jsonObj);
        if (null != jsonObj) {
            this.vip = jsonObj.optBoolean("vip");
            this.uName = jsonObj.optString("uName");
            this.isTop = jsonObj.optBoolean("isTop");
            this.praise = jsonObj.optBoolean("praise");
            this.url = jsonObj.optString("url");
            this.ctime = jsonObj.optString("ctime");
            this.utime = jsonObj.optString("utime");
            this.date = jsonObj.optString("date");
            this.commentId = jsonObj.optString("commentId");
            this.uId = jsonObj.optString("uId");
            this.bookId = jsonObj.optString("bookId");
            this.bookName = jsonObj.optString("bookName");
            this.commenterLevel = jsonObj.optString("commenterLevel");
            this.content = jsonObj.optString("content");
            this.author = jsonObj.optString("author");
            this.coverWap = jsonObj.optString("coverWap");
            this.praiseNum = jsonObj.optInt("praiseNum");
            this.commentStatus = jsonObj.optInt("status");
            this.score = (float) jsonObj.optDouble("score");
        }
        return this;
    }
}
