package hw.sdk.net.bean.reader;

import org.json.JSONObject;

import java.util.ArrayList;

import hw.sdk.net.bean.HwPublicBean;
import hw.sdk.utils.JsonUtils;

/**
 * 书籍推荐 bean
 *
 * @author caimantang on 2018/4/16.
 */

public class BeanBookRecomment extends HwPublicBean<BeanBookRecomment> {
    /**
     * is end？
     */
    public int isEnd;
    /**
     * 提示语
     */
    public String tip;
    /**
     * 书籍列表
     */
    public ArrayList<BeanRecommentBookInfo> data;

    public boolean isEndBook() {
        return isEnd == 1;
    }

    public boolean isSerialBook() {
        return isEnd == 0;
    }

    @Override
    public BeanBookRecomment parseJSON(JSONObject jsonObj) {
        super.parseJSON(jsonObj);
        JSONObject jsonObject = null;
        if (isSuccess() && null != (jsonObject = jsonObj.optJSONObject("data"))) {
            this.tip = jsonObject.optString("tip");
            this.isEnd = jsonObject.optInt("isEnd");
            this.data = JsonUtils.getBeanRecommentBookInfoList(jsonObject.optJSONArray("recommentList"));
        }
        return this;
    }

}
