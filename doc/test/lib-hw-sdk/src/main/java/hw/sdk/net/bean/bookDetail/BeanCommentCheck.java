package hw.sdk.net.bean.bookDetail;

import org.json.JSONObject;

import hw.sdk.net.bean.HwPublicBean;

/**
 * 书籍评论 bean
 * @author caimantang on 2018/4/16.
 */

public class BeanCommentCheck extends HwPublicBean<BeanCommentCheck> {

    /**
     * 必须不为空 1：校验通过 其他 校验不通过
     * 2:需要登录
     * 13：需要实名认证
     */
    public int status;
    /**
     * 提示语
     */
    public String tip;
    /**
     * 评论内容
     */
    public String content;
    /**
     * 分
     */
    public double score;

    @Override
    public BeanCommentCheck parseJSON(JSONObject jsonObj) {
        super.parseJSON(jsonObj);
        JSONObject data;
        if (isSuccess() && null != (data = jsonObj.optJSONObject("data"))) {
            this.tip = data.optString("tip");
            this.content = data.optString("content");
            this.score = data.optDouble("score", 0.0D);
            this.status = data.optInt("status");
        }
        return this;
    }


    /**
     * 需要登录
     *
     * @return 需要登录？
     */
    public boolean isCommentCheckNeedLogin() {
        return status == 2;
    }

    /**
     * 校验通过
     *
     * @return  校验通过？
     */
    public boolean isCommentCheckPass() {
        return status == 1;
    }

    /**
     * 需要实名认证
     *
     * @return 需要实名认证？
     */
    public boolean isCommentNeedRealNameVerified() {
        return status == 13;
    }
}
