package hw.sdk.net.bean.cloudshelf;

import org.json.JSONObject;

import hw.sdk.net.bean.HwPublicBean;

/**
 * 书籍加载进度提示bean
 * @author winzows
 */
public class BeanSingleBookReadProgressTipInfo extends HwPublicBean<BeanSingleBookReadProgressTipInfo> {
    /**
     * 提示语
     */
    public String tip;
    /**
     * 颜色
     */
    public String color;

    @Override
    public BeanSingleBookReadProgressTipInfo parseJSON(JSONObject jsonObj) {
        super.parseJSON(jsonObj);
        tip = jsonObj.optString("tip");
        color = jsonObj.optString("color");
        return this;
    }
}
