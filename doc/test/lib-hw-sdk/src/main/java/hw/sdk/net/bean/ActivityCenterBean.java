package hw.sdk.net.bean;

import android.text.TextUtils;

import com.dzbook.lib.utils.ALog;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * 活动中心bean
 * @author gavin 2018/4/25
 */

public class ActivityCenterBean extends HwPublicBean<ActivityCenterBean> {

    /**
     * 活动中心集合
     */
    public ArrayList<CenterInfoBean> activityCenterBeans;
    /**
     * 返回活动状态
     */
    public int status = -1;

    @Override
    public ActivityCenterBean parseJSON(JSONObject jsonObj) {
        super.parseJSON(jsonObj);

        if (isSuccess()) {
            JSONObject dataJsonObj = jsonObj.optJSONObject("data");
            if (dataJsonObj != null) {
                if (dataJsonObj.has("status")) {
                    status = dataJsonObj.optInt("status");
                }
                if (status == 1) {
                    JSONArray array = dataJsonObj.optJSONArray("activityList");
                    if (array != null) {
                        this.activityCenterBeans = new ArrayList<>();
                        int length = array.length();
                        for (int i = 0; i < length; i++) {
                            JSONObject obj = array.optJSONObject(i);
                            if (obj != null) {
                                CenterInfoBean centerInfoBean = new CenterInfoBean();
                                this.activityCenterBeans.add(centerInfoBean.parseJSON(obj));
                            }
                        }
                    }
                }
            }
        }

        return this;
    }

    /**
     *  校验
     * @return 数据是否正确
     */
    public boolean isHasData() {
        return activityCenterBeans != null && activityCenterBeans.size() > 0;
    }

    /**
     * 活动详情
     */
    public static class CenterInfoBean implements Serializable {
        /**
         * 图片链接
         */
        private String img;
        /**
         * 活动名称
         */
        private String title;
        /**
         * 活动时间
         */
        private String temp;

        /**
         * 详情页链接
         */
        private String url;

        /**
         * channelCode
         */
        private int channelCode;


        public String getImg() {
            return img;
        }

        public String getTitle() {
            return title;
        }

        public String getTemp() {
            return temp;
        }

        public String getUrl() {
            return url;
        }

        public int getChannelCode() {
            return channelCode;
        }

        /**
         * 解析bean
         * @param jsonObj json
         * @return bean
         */
        public CenterInfoBean parseJSON(JSONObject jsonObj) {
            if (jsonObj != null) {
                img = jsonObj.optString("imgUrl");
                url = jsonObj.optString("url");
                title = jsonObj.optString("title");
                channelCode = jsonObj.optInt("channelCode");
                if (!TextUtils.isEmpty(jsonObj.optString("startTime"))) {
                    String start = getFormatTime(jsonObj.optLong("startTime"), "yyyy/MM/dd");
                    String end = "";
                    if (!TextUtils.isEmpty(jsonObj.optString("endTime"))) {
                        end = getFormatTime(jsonObj.optLong("endTime"), "yyyy/MM/dd");
                    }
                    temp = start + "-" + end;
                } else {
                    temp = "";
                }
            }
            return this;
        }

        /**
         * 根据指定的时间戳，返回指定格式的日期时间
         *
         * @param time   时间戳
         * @param format 指定的日期格式<br>
         *               eg:<br>
         *               "yyyy-MM-dd HH:mm:ss"<br>
         *               "yyyy-MM-dd"<br>
         *               "yyyyMMddHHmmss"<br>
         *               "HH:mm:ss"<br>
         * @return
         */
        static String getFormatTime(long time, String format) {
            Date date = new Date(time);
            String strs = "";
            try {
                SimpleDateFormat sdf = new SimpleDateFormat(format);
                strs = sdf.format(date);
            } catch (Exception e) {
                ALog.printStackTrace(e);
            }
            return strs;
        }
    }
}
