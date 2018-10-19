package hw.sdk.net.bean.seach;

import org.json.JSONObject;

import java.util.List;

import hw.sdk.net.bean.BeanBookInfo;
import hw.sdk.net.bean.HwPublicBean;
import hw.sdk.utils.JsonUtils;

/**
 * 搜索bean
 * @author caimantang on 2018/4/13.
 */

public class BeanSearch extends HwPublicBean<BeanSearch> {
    /**
     * 搜索结果列表
     */
    public List<BeanBookInfo> searchList;

    /**
     * 计数
     */
    public long totalCount;

    /**
     * 搜索词类型字典：
     * “0” 全文匹配
     * “1” 书籍匹配
     * “2” 作者匹配
     * “3” 标签匹配
     */
    public String searchType;


    @Override
    public BeanSearch parseJSON(JSONObject jsonObj) {
        super.parseJSON(jsonObj);
        JSONObject data = null;
        if (isSuccess() && null != (data = jsonObj.optJSONObject("data"))) {
            this.searchList = JsonUtils.getBookList(data.optJSONArray("searchList"));
            this.searchType = data.optString("searchType");
            this.totalCount = data.optLong("totalCount");
        }
        return this;
    }

}
