package hw.sdk.net.bean.cloudshelf;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import hw.sdk.net.bean.BeanBookInfo;
import hw.sdk.net.bean.HwPublicBean;
import hw.sdk.utils.JsonUtils;

/**
 * 云书架 列表页 bean
 * @author winzows
 */
public class BeanCloudShelfPageListInfo extends HwPublicBean<BeanCloudShelfPageListInfo> {
    /**
     * 书籍列表
     */
    public ArrayList<BeanBookInfo> list;
    /**
     * 是否有更多
     */
    public int hasMore;

    @Override
    public BeanCloudShelfPageListInfo parseJSON(JSONObject jsonObj) {
        super.parseJSON(jsonObj);
        JSONObject data = jsonObj.optJSONObject("data");
        if (data != null) {
            hasMore = data.optInt("hasMore");
            JSONArray array = data.optJSONArray("readList");
            if (array != null && array.length() > 0) {
                list = JsonUtils.getBookList(array);
            }
        }
        return this;
    }

    public boolean isContainData() {
        return list != null && list.size() > 0;
    }
}
