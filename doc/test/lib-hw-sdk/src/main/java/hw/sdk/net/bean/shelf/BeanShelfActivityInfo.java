package hw.sdk.net.bean.shelf;

import org.json.JSONObject;

import hw.sdk.net.bean.HwPublicBean;

/**
 * 书架活动
 * @author winzows
 */
public class BeanShelfActivityInfo extends HwPublicBean<BeanShelfActivityInfo> {
    /**
     * 活动id
     */
    public String id;
    /**
     * 活动标题
     */
    public String title;
    /**
     * 类型
     */
    public String type;
    /**
     * 描述
     */
    public String description;
    /**
     * 展示时间
     */
    public String displayTime;
    /**
     * 资源id
     */
    public String resourceId;
    /**
     * 活动图片地址
     */
    public String imageUrl;
    /**
     * titlw
     */
    public String resourceTitle;
    /**
     * 活动地址 跳转的url
     */
    public String url;

    @Override
    public BeanShelfActivityInfo parseJSON(JSONObject jsonObj) {
        super.parseJSON(jsonObj);
        id = jsonObj.optString("id");
        title = jsonObj.optString("title");
        type = jsonObj.optString("type");
        description = jsonObj.optString("description");
        displayTime = jsonObj.optString("displayTime");
        resourceId = jsonObj.optString("resourceId");
        imageUrl = jsonObj.optString("imageUrl");
        resourceTitle = jsonObj.optString("resourceTitle");
        url = jsonObj.optString("url");
        return this;
    }
}
