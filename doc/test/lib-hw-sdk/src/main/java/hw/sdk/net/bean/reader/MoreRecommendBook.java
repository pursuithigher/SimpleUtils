package hw.sdk.net.bean.reader;

import org.json.JSONObject;

import java.util.ArrayList;

import hw.sdk.net.bean.BeanBookInfo;
import hw.sdk.net.bean.HwPublicBean;
import hw.sdk.utils.JsonUtils;

/**
 * 推荐更多书 bean
 * @author caimantang on 2018/4/22.
 */

public class MoreRecommendBook extends HwPublicBean<MoreRecommendBook> {
    /**
     * 是否有更多
     */
    public boolean hasMore;
    /**
     * books
     */
    public ArrayList<BeanBookInfo> books;

    @Override
    public MoreRecommendBook parseJSON(JSONObject jsonObj) {
        super.parseJSON(jsonObj);
        JSONObject data;
        if (isSuccess() && null != (data = jsonObj.optJSONObject("data"))) {
            books = JsonUtils.getBookList(data.optJSONArray("books"));
            hasMore = data.optBoolean("hasMore");
        }
        return this;
    }
}
