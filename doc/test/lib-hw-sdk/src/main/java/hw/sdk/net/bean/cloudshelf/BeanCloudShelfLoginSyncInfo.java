package hw.sdk.net.bean.cloudshelf;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import hw.sdk.net.bean.HwPublicBean;

/**
 * 云书架 bean
 * @author winzows
 */
public class BeanCloudShelfLoginSyncInfo extends HwPublicBean<BeanCloudShelfLoginSyncInfo> {

    /**
     * 书籍id
     */
    public ArrayList<String> bookIds;
    /**
     * 云书架书籍是否超过20本：0:少于 1：大于
     */
    public int hasMore;

    /**
     * 是否还有更多
     * @return has more?
     */
    public boolean hasMore() {
        return hasMore != 0;
    }

    /**
     * 书籍列表 不能为空
     * @return 是否为空
     */
    public boolean hasBookIds() {
        return bookIds != null && bookIds.size() > 0;
    }

    @Override
    public BeanCloudShelfLoginSyncInfo parseJSON(JSONObject jsonObj) {
        super.parseJSON(jsonObj);
        JSONObject data = jsonObj.optJSONObject("data");
        if (data != null) {
            hasMore = data.optInt("hasMore");
            JSONArray array = data.optJSONArray("bookIds");
            if (array != null && array.length() > 0) {
                bookIds = new ArrayList<>();
                for (int i = 0; i < array.length(); i++) {
                    String bookid = array.optString(i);
                    bookIds.add(bookid);
                }
            }
        }
        return this;
    }
}
