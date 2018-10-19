package hw.sdk.net.bean.shelf;

import android.text.TextUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import hw.sdk.net.bean.BeanSingleBookInfo;
import hw.sdk.net.bean.HwPublicBean;

/**
 * 内置书的bean
 * @author winzows
 */
public class BeanBuiltInBookListInfo extends HwPublicBean<BeanBuiltInBookListInfo> {
    /**
     * 男孩
     */
    public ArrayList<BeanSingleBookInfo> boys;
    /**
     * 女孩
     */
    public ArrayList<BeanSingleBookInfo> girls;

    /**
     * String
     */
    public String buildInBookStr;

    public boolean isContainData() {
        return (boys != null && boys.size() > 0) || (girls != null && girls.size() > 0);
    }

    public String getBuildInBookData() {
        return buildInBookStr;
    }

    @Override
    public BeanBuiltInBookListInfo parseJSON(JSONObject jsonObj) {
        super.parseJSON(jsonObj);
        JSONObject data = jsonObj.optJSONObject("data");
        buildInBookStr = jsonObj.optString("data");
        if (data != null) {
            JSONArray jsonBoys = data.optJSONArray("boys");
            if (jsonBoys != null) {
                this.boys = new ArrayList<>();
                for (int i = 0; i < jsonBoys.length(); i++) {
                    JSONObject jsonObject = jsonBoys.optJSONObject(i);
                    if (jsonObject != null) {
                        BeanSingleBookInfo sigleInfo = new BeanSingleBookInfo();
                        sigleInfo.parseJSON(jsonObj);
                        this.boys.add(sigleInfo);
                    }
                }
            }

            JSONArray jsonGirls = data.optJSONArray("girls");
            if (jsonGirls != null) {
                this.girls = new ArrayList<>();
                for (int i = 0; i < jsonGirls.length(); i++) {
                    JSONObject jsonObject = jsonGirls.optJSONObject(i);
                    if (jsonObject != null) {
                        BeanSingleBookInfo sigleInfo = new BeanSingleBookInfo();
                        sigleInfo.parseJSON(jsonObj);
                        this.girls.add(sigleInfo);
                    }
                }
            }
        }
        return this;
    }

    /**
     * 是否默认值
     *
     * @param value 值
     * @return 是否默认值
     */
    public static boolean isHistoryValue(String value) {
        return TextUtils.equals(value, "0");
    }


    /**
     * 是否已经内置
     *
     * @param value 值
     * @return 是否已经内置
     */
    public static boolean isAlreadyInitBook(String value) {
        return TextUtils.equals(value, "1");
    }
}
