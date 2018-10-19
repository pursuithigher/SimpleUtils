package hw.sdk.net.bean.type;

import android.text.TextUtils;

import org.json.JSONObject;

import hw.sdk.net.bean.HwPublicBean;


/**
 * 分类一级页面 左边目录
 *
 * @author winzows on 2018/4/13
 */

public class BeanMainTypeLeft extends HwPublicBean<BeanMainTypeLeft> {

    /**
     * 分类左边目录名字
     */
    public String categoryName;
    /**
     * 分类左边 目录id
     */
    public String categoryId;

    @Override
    public BeanMainTypeLeft parseJSON(JSONObject jsonObj) {
        super.parseJSON(jsonObj);
        categoryName = jsonObj.optString("categoryName");
        categoryId = jsonObj.optString("categoryId");
        return this;
    }

    @Override
    public int hashCode() {
        if (!TextUtils.isEmpty(categoryName)) {
            return categoryName.hashCode();
        }
        return super.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof BeanMainTypeLeft) {
            if (!TextUtils.isEmpty(categoryName) && TextUtils.equals(((BeanMainTypeLeft) obj).categoryName, this.categoryName)) {
                return true;
            }
        }
        return super.equals(obj);
    }
}
