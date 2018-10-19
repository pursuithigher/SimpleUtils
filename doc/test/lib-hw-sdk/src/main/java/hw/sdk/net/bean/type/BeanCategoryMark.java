package hw.sdk.net.bean.type;

import org.json.JSONObject;

import hw.sdk.net.bean.HwPublicBean;

/**
 * 二级cid
 *
 * @author winzows on  2018/4/13
 */

public class BeanCategoryMark extends HwPublicBean<BeanCategoryMark> implements BeanTypeInterface {
    /**
     * 二级cid  title
     */
    public String title;
    /**
     * id
     */
    public String markId;
    /**
     * 是否选中
     */
    public boolean isChecked;

    @Override
    public BeanCategoryMark parseJSON(JSONObject jsonObj) {
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
        return BeanMainType.TYPE_SECOND;
    }
}
