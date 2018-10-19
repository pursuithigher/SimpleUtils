package hw.sdk.net.bean.type;

import org.json.JSONObject;

import hw.sdk.net.bean.HwPublicBean;

/**
 * 分类排序 热门书籍啊  最热书籍啥的
 *
 * @author winzows on 2018/4/13.
 */

public class BeanSortMark extends HwPublicBean<BeanSortMark> implements BeanTypeInterface {
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
    public BeanSortMark parseJSON(JSONObject jsonObj) {
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
        return BeanMainType.TYPE_FIRST;
    }
}
