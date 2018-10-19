package hw.sdk.net.bean.bookDetail;

import org.json.JSONObject;

import java.util.ArrayList;

import hw.sdk.net.bean.HwPublicBean;
import hw.sdk.utils.JsonUtils;

/**
 * 评论结果 bean
 * @author caimantang on 2018/4/16.
 */

public class BeanCommentResult extends HwPublicBean<BeanCommentResult> {
    /**
     * 评论状态
     */
    public int status;
    /**
     * tips
     */
    public String tip;
    /**
     * book id
     */
    public String bookId;
    /**
     * 评论列表
     */
    public ArrayList<BeanCommentInfo> commentList;

    @Override
    public BeanCommentResult parseJSON(JSONObject jsonObj) {
        super.parseJSON(jsonObj);
        JSONObject data = null;
        if (isSuccess() && null != (data = jsonObj.optJSONObject("data"))) {
            this.status = data.optInt("status");
            this.tip = data.optString("tip");
            this.bookId = data.optString("bookId");
            this.commentList = JsonUtils.getCommentList(data.optJSONArray("commentList"));
        }
        return this;
    }
}
