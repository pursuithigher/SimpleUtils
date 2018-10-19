package hw.sdk.net.bean.bookDetail;

import org.json.JSONObject;

import hw.sdk.net.bean.HwPublicBean;

/**
 * 评论点赞 举报 删除
 * @author caimantang on 2018/4/16.
 */

public class BeanCommentAction extends HwPublicBean<BeanCommentAction> {
    /**
     * 状态
     */
    public int status;
    /**
     * 提示语
     */
    public String tip;

    @Override
    public BeanCommentAction parseJSON(JSONObject jsonObj) {
        super.parseJSON(jsonObj);
        JSONObject data;
        if (isSuccess() && null != (data = jsonObj.optJSONObject("data"))) {
            this.status = data.optInt("status");
            this.tip = data.optString("tip");
        }
        return this;
    }
}
