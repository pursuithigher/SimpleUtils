package hw.sdk.utils;

import android.text.TextUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import hw.sdk.net.bean.BeanBlock;
import hw.sdk.net.bean.BeanBookInfo;
import hw.sdk.net.bean.BeanChapterInfo;
import hw.sdk.net.bean.BeanSingleBookInfo;
import hw.sdk.net.bean.bookDetail.BeanCommentInfo;
import hw.sdk.net.bean.reader.BeanRecommentBookInfo;
import hw.sdk.net.bean.seach.BeanKeywordHotVo;
import hw.sdk.net.bean.seach.SuggestItem;

/**
 * JsonUtils
 *
 * @author caimantang on 2018/4/13.
 */
public class JsonUtils {
    /**
     * jsonArrayIsAvailable
     * @param jsonArray json
     * @return 是否可用
     */
    public static boolean jsonArrayIsAvailable(JSONArray jsonArray) {
        return null != jsonArray && jsonArray.length() > 0;
    }

    /**
     * getStringList
     * @param jsonArray json
     * @return arrayList
     */
    public static ArrayList<String> getStringList(JSONArray jsonArray) {
        ArrayList<String> list = null;
        try {
            if (jsonArrayIsAvailable(jsonArray)) {
                list = new ArrayList<>();
                for (int i = 0; i < jsonArray.length(); i++) {
                    String string = jsonArray.getString(i);
                    if (!TextUtils.isEmpty(string)) {
                        list.add(string);
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return list;
    }
    /**
     * getHotKeyList
     * @param jsonArray json
     * @return arrayList<BeanKeywordHotVo>
     */
    public static ArrayList<BeanKeywordHotVo> getHotKeyList(JSONArray jsonArray) {
        ArrayList<BeanKeywordHotVo> list = null;
        try {
            if (jsonArrayIsAvailable(jsonArray)) {
                list = new ArrayList<>();
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    if (null != jsonObject) {
                        BeanKeywordHotVo bean = new BeanKeywordHotVo();
                        bean.parseJSON(jsonObject);
                        list.add(bean);
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return list;
    }
    /**
     * getSuggestItemList
     * @param jsonArray json
     * @return arrayList<SuggestItem>
     */
    public static ArrayList<SuggestItem> getSuggestItemList(JSONArray jsonArray) {
        ArrayList<SuggestItem> list = null;
        try {
            if (jsonArrayIsAvailable(jsonArray)) {
                list = new ArrayList<>();
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    if (null != jsonObject) {
                        SuggestItem bean = new SuggestItem();
                        bean.parseJSON(jsonObject);
                        list.add(bean);
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * getBeanRecommentBookInfoList
     * @param jsonArray json
     * @return arrayList<BeanRecommentBookInfo>
     */
    public static ArrayList<BeanRecommentBookInfo> getBeanRecommentBookInfoList(JSONArray jsonArray) {
        ArrayList<BeanRecommentBookInfo> list = null;
        try {
            if (jsonArrayIsAvailable(jsonArray)) {
                list = new ArrayList<>();
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    if (null != jsonObject) {
                        BeanRecommentBookInfo bean = new BeanRecommentBookInfo();
                        bean.parseJSON(jsonObject);
                        list.add(bean);
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * getSingleBookList
     * @param jsonArray json
     * @return arrayList<BeanSingleBookInfo>
     */
    public static ArrayList<BeanSingleBookInfo> getSingleBookList(JSONArray jsonArray) {
        ArrayList<BeanSingleBookInfo> list = null;
        try {
            if (jsonArrayIsAvailable(jsonArray)) {
                list = new ArrayList<>();
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    if (null != jsonObject) {
                        BeanSingleBookInfo bean = new BeanSingleBookInfo();
                        bean.parseJSON(jsonObject);
                        list.add(bean);
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return list;
    }
    /**
     * getBookList
     * @param jsonArray json
     * @return arrayList<BeanBookInfo>
     */
    public static ArrayList<BeanBookInfo> getBookList(JSONArray jsonArray) {
        ArrayList<BeanBookInfo> list = null;
        try {
            if (jsonArrayIsAvailable(jsonArray)) {
                list = new ArrayList<>();
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    if (null != jsonObject) {
                        BeanBookInfo bean = new BeanBookInfo();
                        bean.parseJSON(jsonObject);
                        list.add(bean);
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return list;
    }
    /**
     * getCommentList
     * @param jsonArray json
     * @return arrayList<BeanCommentInfo>
     */
    public static ArrayList<BeanCommentInfo> getCommentList(JSONArray jsonArray) {
        ArrayList<BeanCommentInfo> list = null;
        try {
            if (jsonArrayIsAvailable(jsonArray)) {
                list = new ArrayList<>();
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    if (null != jsonObject) {
                        BeanCommentInfo bean = new BeanCommentInfo();
                        bean.parseJSON(jsonObject);
                        list.add(bean);
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return list;
    }
    /**
     * getBlockList
     * @param jsonArray json
     * @return arrayList<BeanBlock>
     */
    public static ArrayList<BeanBlock> getBlockList(JSONArray jsonArray) {
        ArrayList<BeanBlock> list = null;
        try {
            if (jsonArrayIsAvailable(jsonArray)) {
                list = new ArrayList<>();
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    if (null != jsonObject) {
                        BeanBlock bean = new BeanBlock();
                        bean.parseJSON(jsonObject);
                        list.add(bean);
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return list;
    }
    /**
     * getChapterList
     * @param jsonArray json
     * @return arrayList<BeanChapterInfo>
     */
    public static ArrayList<BeanChapterInfo> getChapterList(JSONArray jsonArray) {
        ArrayList<BeanChapterInfo> list = null;
        try {
            if (jsonArrayIsAvailable(jsonArray)) {
                list = new ArrayList<>();
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    if (null != jsonObject) {
                        BeanChapterInfo bean = new BeanChapterInfo();
                        bean.parseJSON(jsonObject);
                        list.add(bean);
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return list;
    }
}
