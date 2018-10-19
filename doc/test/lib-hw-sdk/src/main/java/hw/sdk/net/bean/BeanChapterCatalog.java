package hw.sdk.net.bean;

import android.text.TextUtils;

import org.json.JSONObject;

import java.util.ArrayList;

import hw.sdk.utils.JsonUtils;

/**
 * 章节目录信息 bean
 *
 * @author caimantang on 2018/4/16.
 */

public class BeanChapterCatalog extends HwPublicBean<BeanChapterCatalog> {
    /**
     * 书籍id
     */
    public String bookId;
    /**
     * 章节id列表
     */
    public ArrayList<String> chapterIdList;
    /**
     * 章节名字列表
     */
    public ArrayList<String> chapterNameList;
    /**
     * 支付列表
     */
    public String isChargeList;
    /**
     * 分页列表
     */
    public ArrayList<BeanBlock> blockList;
    /**
     * 章节信息
     */
    public ArrayList<BeanChapterInfo> chapterInfoList;

    @Override
    public BeanChapterCatalog parseJSON(JSONObject jsonObj) {
        super.parseJSON(jsonObj);
        JSONObject data;
        if (isSuccess() && null != (data = jsonObj.optJSONObject("data"))) {
            this.blockList = JsonUtils.getBlockList(data.optJSONArray("blockList"));
            this.chapterNameList = JsonUtils.getStringList(data.optJSONArray("chapterNameList"));
            this.chapterIdList = JsonUtils.getStringList(data.optJSONArray("chapterIdList"));
            this.bookId = data.optString("bookId");
            this.isChargeList = data.optString("isChargeList");

            if (!TextUtils.isEmpty(isChargeList)
                    && !isEmpty(chapterIdList)
                    && !isEmpty(chapterNameList)
                    && (chapterIdList.size() == chapterNameList.size())
                    && (chapterNameList.size() == isChargeList.length())) {
                chapterInfoList = new ArrayList<>();
                for (int i = 0; i < chapterIdList.size(); i++) {
                    String chapterId = chapterIdList.get(i);
                    String chapterName = chapterNameList.get(i);
                    BeanChapterInfo chapterInfo = new BeanChapterInfo();
                    chapterInfo.chapterName = chapterName;
                    chapterInfo.chapterId = chapterId;
                    chapterInfo.bookId = bookId;
                    String isCharge = i < isChargeList.length() ? String.valueOf(isChargeList.charAt(i)) : "";
                    chapterInfo.isCharge = isCharge;
                    chapterInfoList.add(chapterInfo);
                }
            }

        }
        return this;
    }
}
