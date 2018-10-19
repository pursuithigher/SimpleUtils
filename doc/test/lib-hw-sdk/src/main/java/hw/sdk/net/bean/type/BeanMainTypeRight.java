package hw.sdk.net.bean.type;

import org.json.JSONObject;

import hw.sdk.net.bean.HwPublicBean;

/**
 * 分类页面 一级页面  右边的bean
 *
 * @author winzows on 018/4/13
 */

public class BeanMainTypeRight extends HwPublicBean<BeanMainTypeRight> {
    /**
     * 图片url
     */
    public String imgUrl = "";
    /**
     * title
     */
    public String title = "";
    /**
     * cid
     */
    public String cid = "";
    /**
     * 红圈内部提示语
     */
    public String markMsg = "";
    /**
     * 颜色
     */
    public String markColor = "";

    @Override
    public BeanMainTypeRight parseJSON(JSONObject jsonObj) {
        super.parseJSON(jsonObj);

        imgUrl = jsonObj.optString("imgUrl");
        title = jsonObj.optString("title");
        cid = jsonObj.optString("cid");
        markMsg = jsonObj.optString("markMsg");
        markColor = jsonObj.optString("markColor");
        return this;
    }
}
