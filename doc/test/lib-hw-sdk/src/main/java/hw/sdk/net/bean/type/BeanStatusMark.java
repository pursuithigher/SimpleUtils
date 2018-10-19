package hw.sdk.net.bean.type;

import org.json.JSONObject;

import hw.sdk.net.bean.HwPublicBean;

/**
 * 书籍状态 完本 连载等
 *
 * @author winzows on 2018/4/13
 */

public class BeanStatusMark extends HwPublicBean<BeanStatusMark> implements BeanTypeInterface {
    /**
     * title
     */
    public String title;
    /**
     * markId
     */
    public String markId;
    /**
     * isChecked
     */
    public boolean isChecked;

    @Override
    public BeanStatusMark parseJSON(JSONObject jsonObj) {
        super.parseJSON(jsonObj);
        title = jsonObj.optString("title");
        markId = jsonObj.optString("markId");
        return this;
    }

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public String getMarkId() {
        return markId;
    }

    @Override
    public String getType() {
        return BeanMainType.TYPE_THREE;
    }
}
