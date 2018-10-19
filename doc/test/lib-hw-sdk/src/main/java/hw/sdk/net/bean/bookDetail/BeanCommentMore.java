package hw.sdk.net.bean.bookDetail;

import org.json.JSONObject;

import java.util.ArrayList;

import hw.sdk.net.bean.HwPublicBean;
import hw.sdk.utils.JsonUtils;

/**
 * 更多评论
 * @author caimantang on 2018/4/15.
 */

public class BeanCommentMore extends HwPublicBean<BeanCommentMore> {
    /**
     * 评论列表
     */
    public ArrayList<BeanCommentInfo> commentList;
    /**
     * 书籍id
     */
    public String bookId;
    /**
     * 分
     */
    public double score;

    @Override
    public BeanCommentMore parseJSON(JSONObject jsonObj) {
        super.parseJSON(jsonObj);
        JSONObject data;
        if (isSuccess() && null != (data = jsonObj.optJSONObject("data"))) {
            this.commentList = JsonUtils.getCommentList(data.optJSONArray("commentList"));
            this.bookId = data.optString("bookId");
            this.score = data.optDouble("score");
        }
        return this;
    }
}
