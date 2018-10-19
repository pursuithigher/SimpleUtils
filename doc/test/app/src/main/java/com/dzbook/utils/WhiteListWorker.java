package com.dzbook.utils;

import com.dzbook.activity.CenterDetailActivity;
import com.dzbook.activity.MainTypeDetailActivity;
import com.dzbook.activity.RankTopActivity;
import com.dzbook.activity.detail.BookDetailActivity;
import com.dzbook.activity.person.CloudBookShelfActivity;
import com.dzbook.activity.reader.ChaseRecommendActivity;
import com.dzbook.activity.reader.ChaseRecommendMoreActivity;
import com.dzbook.activity.search.SearchActivity;
import com.dzbook.fragment.main.BaseFragment;
import com.dzbook.fragment.main.MainShelfFragment;
import com.dzbook.fragment.main.MainStoreFragment;
import com.dzbook.lib.utils.ALog;
import com.iss.app.BaseActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import static com.dzbook.log.LogConstants.GH_PI;
import static com.dzbook.log.LogConstants.GH_PN;
import static com.dzbook.log.LogConstants.GH_PS;
import static com.dzbook.log.LogConstants.GH_TYPE;

/**
 * 白名单
 */
public class WhiteListWorker {


    //key
    /**
     * 推送
     */
    public static final String GET_TUI = "get_tui";
    /**
     * 书架推荐
     */
    public static final String BOOK_SHELF_RECOMMEND = "book_shelf_recommend";
    /**
     * 云书架同步（区分手动添加和三方登陆自动添加）
     */
    public static final String CLOUD_SYNC = "cloud_sync";
    /**
     * 书架活动跳转图书详情
     */
    public static final String BOOK_SHELF_ACTIVITY = "book_shelf_activity";

    /**
     * 搜索结果
     */
    public static final String SEARCH_RESULT = "search_result";


    /**
     * 云同步值
     */
    public static final String CLOUD_SYNC_VALUE = "8";

    /**
     * BOOK_LING_QU_VALUE
     */
    public static final String BOOK_LING_QU_VALUE = "11";
    /**
     * BOOK_SHELF_ACTIVITY_VALUE
     */
    public static final String BOOK_SHELF_ACTIVITY_VALUE = "12";
    /**
     * SEARCH_RESULT_VALUE
     */
    public static final String SEARCH_RESULT_VALUE = "13";
    /**
     * CLOUDBOOKSHELF_ACTIVITY_VALUE
     */
    public static final String CLOUDBOOKSHELF_ACTIVITY_VALUE = "14";
    /**
     * 书城限免领书
     */
    public static final String STORE_FREE_GETBOOK = "15";
    /**
     * 分类二级页面
     */
    public static final String FLEJYM_ACTIVITY_VALUE = "16";
    /**
     * 排行榜
     */
    public static final String PHB_ACTIVITY_VALUE = "17";
    /**
     * 追更推荐
     */
    public static final String CHASE_RECOMMEND_ACTIVITY = "18";
    /**
     * 追更推荐更多
     */
    public static final String CHASE_RECOMMEND_MORE_ACTIVITY = "19";
    //白名单
    private static HashMap<String, JSONObject> dzPage;

    private static volatile JSONObject whiteObj = null;
    //value
    private static final String MAINSTOREFRAGMENT = "1";
    private static final String SEARCHACTIVITY = "2";
    private static final String CENTERDETAILACTIVITY = "3";
    private static final String GET_TUI_VALUE = "4";
    private static final String MAINRECOMMENDFRAGMENT = "5";
    private static final String BOOK_DETAIL_VALUE = "6";
    private static final String BOOK_SHELF_RECOMMEND_VALUE = "7";

    public static JSONObject getWhiteObj() {
        return whiteObj;
    }


    /**
     * 重置书籍源
     *
     * @param object object
     */
    public static void resetBookSourceFrom(Object object) {
        if (null != whiteObj && object.getClass().getName().equals(whiteObj.optString(GH_PN))) {
            if (!whiteObj.has(GH_PI)) {
                whiteObj.remove(GH_PI);
            }
            if (!whiteObj.has(GH_PS)) {
                whiteObj.remove(GH_PS);
            }
        }
        try {
            if (object instanceof BaseActivity) {
                whiteObj.put(GH_PI, ((BaseActivity) object).getPI());
                whiteObj.put(GH_PS, ((BaseActivity) object).getPS());
            } else if (object instanceof BaseFragment) {
                whiteObj.put(GH_PI, ((BaseFragment) object).getPI());
                whiteObj.put(GH_PS, ((BaseFragment) object).getPS());
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * setBookSourceFrom
     *
     * @param key    key
     * @param map    map
     * @param object object
     */
    public static void setBookSourceFrom(String key, HashMap<String, String> map, Object object) {
        if (isWhiteList(key)) {
            try {
                //白名单
                whiteObj = dzPage.get(key);
                if (null != whiteObj) {
                    JSONObject jsonObject = new JSONObject();
                    Object type = whiteObj.get(GH_TYPE);
                    jsonObject.put(GH_TYPE, type);
                    if (null != map) {
                        Iterator<Map.Entry<String, String>> it = map.entrySet().iterator();
                        while (it.hasNext()) {
                            Map.Entry<String, String> entry = it.next();
                            if (!jsonObject.has(entry.getKey())) {
                                jsonObject.put(entry.getKey(), entry.getValue());
                            }
                        }
                    }
                    String pi = "";
                    String ps = "";
                    setPnPs(object, jsonObject, pi, ps);
                    whiteObj = jsonObject;
                }
            } catch (Exception e) {
                ALog.printStackTrace(e);
            }

        }
    }

    private static void setPnPs(Object object, JSONObject jsonObject, String pi, String ps) throws JSONException {
        if (null != object) {
            if (object instanceof BaseActivity) {
                pi = ((BaseActivity) object).getPI();
                ps = ((BaseActivity) object).getPS();

            } else if (object instanceof BaseFragment) {
                pi = ((BaseFragment) object).getPI();
                ps = ((BaseFragment) object).getPS();
            }
            if (!jsonObject.has(GH_PI)) {
                jsonObject.put(GH_PI, pi);
            }
            if (!jsonObject.has(GH_PS)) {
                jsonObject.put(GH_PS, ps);
            }
            if (!jsonObject.has(GH_PN)) {
                jsonObject.put(GH_PN, object.getClass().getSimpleName());
            }
        }
    }

    /**
     * setPnPi
     *
     * @param object     object
     * @param jsonObject jsonObject
     * @return jsonObject
     */
    public static JSONObject setPnPi(Object object, JSONObject jsonObject) {
        if (null == jsonObject) {
            return null;
        }
        String pi = "";
        String ps = "";
        try {
            setPnPs(object, jsonObject, pi, ps);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return jsonObject;
    }

    /**
     * 判断是否在白名单里面
     *
     * @param key key
     * @return boolean
     */
    public static boolean isWhiteList(String key) {
        if (MainShelfFragment.class.getSimpleName().equals(key)) {
            whiteObj = null;
        }
        if (null != dzPage) {
            return dzPage.containsKey(key);
        }
        return false;
    }


    static {
        //设置默认的白名单
        dzPage = new HashMap<String, JSONObject>();
        /**
         1.书城
         2.搜索页
         3.活动（活动来源(loading,跑马灯，书架弹窗，信息流活动，推送活动)）
         4.推送
         5.信息流
         6.两级图书详情
         7.书架推荐书籍  //非页面 领取书籍成功后直接固化
         8.云书架同步（区分手动添加和三方登陆自动添加） //非页面 领取书籍成功后直接固化
         9.免费列表 特惠页面
         10.阅读器退出的弹框
         11.H5领取
         12.书架活动
         13.搜索结果
         14.云书架最近阅读
         15.新书城(native)
         16.分类(native)
         17.新排行榜(native)
         */
        dzPage.put(MainStoreFragment.TAG, initJSONObject(MAINSTOREFRAGMENT));
        dzPage.put(SearchActivity.TAG, initJSONObject(SEARCHACTIVITY));
        dzPage.put(CenterDetailActivity.TAG, initJSONObject(CENTERDETAILACTIVITY));
        dzPage.put(GET_TUI, initJSONObject(GET_TUI_VALUE));
        dzPage.put(BookDetailActivity.TAG, initJSONObject(BOOK_DETAIL_VALUE));
        dzPage.put(BOOK_SHELF_RECOMMEND, initJSONObject(BOOK_SHELF_RECOMMEND_VALUE));
        dzPage.put(CLOUD_SYNC, initJSONObject(CLOUD_SYNC_VALUE));
        dzPage.put(BOOK_SHELF_ACTIVITY, initJSONObject(BOOK_SHELF_ACTIVITY_VALUE));
        dzPage.put(SEARCH_RESULT, initJSONObject(SEARCH_RESULT_VALUE));
        dzPage.put(CloudBookShelfActivity.TAG, initJSONObject(CLOUDBOOKSHELF_ACTIVITY_VALUE));
        dzPage.put(MainTypeDetailActivity.TAG, initJSONObject(FLEJYM_ACTIVITY_VALUE));
        dzPage.put(RankTopActivity.TAG, initJSONObject(PHB_ACTIVITY_VALUE));
        dzPage.put(ChaseRecommendActivity.TAG, initJSONObject(CHASE_RECOMMEND_ACTIVITY));
        dzPage.put(ChaseRecommendMoreActivity.TAG, initJSONObject(CHASE_RECOMMEND_MORE_ACTIVITY));
    }

    /**
     * 初始化jsonObject
     *
     * @param value value
     * @return jsonObject
     */
    public static JSONObject initJSONObject(String value) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(GH_TYPE, value);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }
}
