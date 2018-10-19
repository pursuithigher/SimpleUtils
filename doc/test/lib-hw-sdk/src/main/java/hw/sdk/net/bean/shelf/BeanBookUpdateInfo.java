package hw.sdk.net.bean.shelf;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import hw.sdk.net.bean.BeanBookInfo;
import hw.sdk.net.bean.HwPublicBean;
import hw.sdk.utils.JsonUtils;


/**
 * 书籍更新bean
 *
 * @author winzows
 */
public class BeanBookUpdateInfo extends HwPublicBean<BeanBookUpdateInfo> {
    /**
     * 升级书籍列表
     */
    public ArrayList<BeanBookInfo> updateList;
    /**
     * 活动
     */
    public BeanShelfActivityInfo activity;
    /**
     * 最大数
     */
    public int maxNum;

    /**
     * 用户的阅读时长
     */
    public int userReadTime;

    /**
     * 最大的阅读时长
     */
    public int maxReadTime;
    /**
     * 是否已经签到0：未签1：已签
     */
    public int hasSignIn;

    /**
     * 分享url
     */
    public String rdShareUrl;
    /**
     * 下载url
     */
    public String downloadUrl;
    /**
     * 省份
     */
    public String prov;
    /**
     * 城市
     */
    public String city;

    /**
     * 检查通知权限 校验的app打开次数
     */
    public int checkNotifyAppOpenCount = 5;

    /**
     * 检查通知权限频率 默认是7天检查一次
     */
    public int checkNotifyFrequency = 7;
    /**
     * 提示语
     */
    public String cnMsg;

    public boolean isSignIn() {
        return hasSignIn == 1;
    }


    public boolean isContainBooks() {
        return updateList != null && updateList.size() > 0;
    }

    @Override
    public BeanBookUpdateInfo parseJSON(JSONObject jsonObj) {
        super.parseJSON(jsonObj);
        JSONObject data = jsonObj.optJSONObject("data");
        if (data != null) {
            JSONObject f0Object = data.optJSONObject("f0");
            if (f0Object != null) {
                JSONArray jsonArray = f0Object.optJSONArray("updateList");
                updateList = JsonUtils.getBookList(jsonArray);
                maxNum = f0Object.optInt("maxNum");
                userReadTime = f0Object.optInt("userReadTime");
                maxReadTime = f0Object.optInt("maxReadTime");
                hasSignIn = f0Object.optInt("hasSignIn");
                userReadTime = f0Object.optInt("userReadTime");
                maxReadTime = f0Object.optInt("maxReadTime");
                city = f0Object.optString("city");
                prov = f0Object.optString("prov");
            }
            JSONObject f1Object = data.optJSONObject("f1");
            if (f1Object != null) {
                rdShareUrl = f1Object.optString("rdShareUrl");
                downloadUrl = f1Object.optString("downloadUrl");
                JSONObject activityObject = f1Object.optJSONObject("activity");
                if (activityObject != null) {
                    activity = new BeanShelfActivityInfo();
                    activity.parseJSON(activityObject);
                }
            }

            JSONObject f2Object = data.optJSONObject("f2");
            if (f2Object != null) {
                checkNotifyFrequency = f2Object.optInt("cnf", 7);
                checkNotifyAppOpenCount = f2Object.optInt("cnot", 5);
                cnMsg = f2Object.optString("cnmsg");
            }
        }
        return this;
    }

    public boolean isContainActivity() {
        return activity != null;
    }

    /**
     * 需不需要弹出权限申请
     * @return 是否弹
     */
    public boolean needShowSetNotifyDialogIfNeed() {
        return checkNotifyFrequency > 0;
    }
}
