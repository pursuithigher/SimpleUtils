package hw.sdk.net.bean.cloudshelf;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import hw.sdk.net.bean.HwPublicBean;

/**
 * 阅读进度bean
 * @author winzows
 */
public class BeanSingleBookReadProgressInfo extends HwPublicBean<BeanSingleBookReadProgressInfo> {
    /**
     * 书籍id
     */
    public String bookId;
    /**
     * 章节id
     */
    public String chapterId;
    /**
     * 类型
     */
    public int resFormat;
    /**
     * 进度提示bean
     */
    public ArrayList<BeanSingleBookReadProgressTipInfo> tips;

    @Override
    public BeanSingleBookReadProgressInfo parseJSON(JSONObject jsonObj) {
        super.parseJSON(jsonObj);
        JSONObject data = jsonObj.optJSONObject("data");
        if (data != null) {
            bookId = data.optString("bookId");
            chapterId = data.optString("chapterId");
            resFormat = data.optInt("resFormat");
            JSONArray array = data.optJSONArray("tips");
            if (array != null && array.length() > 0) {
                tips = new ArrayList<>();
                for (int i = 0; i < array.length(); i++) {
                    BeanSingleBookReadProgressTipInfo tipInfo = new BeanSingleBookReadProgressTipInfo();
                    tipInfo.parseJSON(array.optJSONObject(i));
                    tips.add(tipInfo);
                }
            }
        }
        return this;
    }
}
