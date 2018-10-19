package com.dzbook.net.hw;

import android.content.Context;
import android.os.Build;
import android.text.TextUtils;

import com.dzbook.AppConst;
import com.dzbook.AppInfoUtils;
import com.dzbook.lib.net.OkhttpUtils;
import com.dzbook.lib.utils.ALog;
import com.dzbook.lib.utils.CompatUtils;
import com.dzbook.lib.utils.JsonUtils;
import com.dzbook.lib.utils.UtilTimeOffset;
import com.dzbook.utils.DeviceInfoUtils;
import com.dzbook.utils.NetworkUtils;
import com.dzbook.utils.PackageControlUtils;
import com.dzbook.utils.SpUtil;
import com.iss.httpclient.core.HttpRequestException;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import hw.sdk.net.bean.shelf.BeanShelfBookItem;
import hw.sdk.utils.HwEncrpt;
import hw.sdk.utils.HwEncryptParam;

/**
 * HwRequest
 *
 * @author lizhongzhong 2018/4/12.
 */

public class HwRequest {

    /**
     * tag
     */
    public static final String TAG = "HwRequest";
    /**
     * key  签名类型
     */
    private static final String KEY_SIGN_TYPE = "signType";

    private static final String KEY_SIGN = "sign";

    /**
     * 调用方身份标识，使用在开发者联盟上注册的appId
     */
    private String appId = "";
    /**
     * 提供服务的国家
     * 二位字母代码（ISO 3166-1 alpha-2）
     * 例如: CN, HK;
     */
    private String count = "CN";
    /**
     * 面向用户的语言， 格式为en_US;
     */
    private String lang = "zh_CN";
    /**
     * 接口版本号
     * 8位数字，[1-9]\\d{7}
     */
    private String ver = "";
    /**
     * 客户端版本号
     */
    private String appVer = "";

    //    private Context mContext;
    private String nativeQueryString = null;


    HwRequest() {
    }

    public String getCOUNTRY() {
        return count;
    }

    /**
     * 获取语言
     *
     * @return string
     */
    public String getLang() {
        if (TextUtils.isEmpty(lang)) {
            Context context = AppConst.getApp();
            if (null != context) {
                Locale locale = CompatUtils.getLocale(context.getResources().getConfiguration());
                lang = locale.getLanguage() + "_" + locale.getCountry();
            }
        }
        return lang;
    }

    /**
     * 获取appId
     *
     * @return String
     */
    public String getAppId() {
        if (TextUtils.isEmpty(appId)) {
            appId = AppInfoUtils.getHwAppId();
        }
        return appId;
    }

    /**
     * getAppVer
     *
     * @return string
     */
    public String getAppVer() {
        if (TextUtils.isEmpty(appVer)) {
            appVer = PackageControlUtils.getAppVersionName();
        }
        return appVer;
    }

    /**
     * getVer
     *
     * @return String
     */
    public String getVer() {
        if (TextUtils.isEmpty(ver)) {
            ver = PackageControlUtils.getAppVersionCode();
        }
        return ver;
    }

    /**
     * 拼接公共请求参数
     *
     * @return String
     */
    private String getQueryString() {
        if (TextUtils.isEmpty(nativeQueryString)) {
            String partQueryString;
            partQueryString = putUrlValue(null, "appId", getAppId());
            partQueryString = putUrlValue(partQueryString, "country", getCOUNTRY());
            partQueryString = putUrlValue(partQueryString, "lang", getLang());
            partQueryString = putUrlValue(partQueryString, "ver", getVer());
            partQueryString = putUrlValue(partQueryString, "appVer", getAppVer());
            nativeQueryString = partQueryString;
        }
        return putUrlValue(nativeQueryString, "timestamp", UtilTimeOffset.getFormatDateByTimeZone());
    }

    private String putUrlValue(String src, String key, String value) {
        if (!TextUtils.isEmpty(key) && !TextUtils.isEmpty(value)) {
            if (!TextUtils.isEmpty(src)) {
                return src + "&" + key + "=" + value;
            } else {
                return key + "=" + value;
            }
        }
        return src;
    }

    private HashMap<String, Object> newHashMap() {
        return new HashMap<>(16);
    }

    private Map<String, String> getCommonHeader() {

        Map<String, String> map = new HashMap<String, String>(16);

        String utdId = DeviceInfoUtils.getInstanse().getHwUtdId();

        map.put("Content-Type", "application/json; charset=utf-8");
        map.put("Accept", "application/json");
        map.put("utdid", utdId);
        map.put("channelCode", PackageControlUtils.getChannel());
        map.put("domain", AppConst.DOMAIN + "");
        map.put("p", AppConst.VERSION_P + "");

        Context context = AppConst.getApp();
        if (null != context) {
            map.put("pname", context.getPackageName());
            String token = SpUtil.getinstance(context).getAppToken();
            if (!TextUtils.isEmpty(token)) {
                map.put("t", token);
            }

            String userId = SpUtil.getinstance(context).getUserID();
            if (!TextUtils.isEmpty(userId)) {
                map.put("uid", userId);
            }
            ALog.d(TAG, "uid:" + userId + "|utdId:" + utdId + "|t:" + token);
        }

        return map;
    }

    /**
     * 网络请求统一处理 需要处理如果服务器网络不通的情况下，给用户弹出提示(处理书城的下拉页面（地址是服务器下发无需拼接）)
     *
     * @param queryString
     * @param headMap
     * @param json
     * @return
     * @throws Exception 异常
     */
    private String toRequestByUrl(String url, String queryString, Map<String, String> headMap, String json) throws Exception {
        Exception exception = null;
        try {
            return OkhttpUtils.getInstance().okHttpRequest(RequestCall.getCallByUrl(url, queryString), headMap, json);
        } catch (Exception e) {
            exception = e;
            throw new HttpRequestException(e, 0);
        } finally {
            if (null != exception) {
                try {
                    NetworkUtils.getInstance().popServerFailDialog(url, exception);
                } catch (Exception e) {
                    ALog.printStackTrace(e);
                }
            }
        }
    }

    /**
     * 网络请求统一处理 需要处理如果服务器网络不通的情况下，给用户弹出提示
     *
     * @param queryString
     * @param headMap
     * @param json
     * @return
     * @throws Exception 异常
     */
    private String toRequest(String call, String queryString, Map<String, String> headMap, String json) throws Exception {
        Exception exception = null;
        try {
            return OkhttpUtils.getInstance().okHttpRequest(RequestCall.getCallUrl(call, queryString), headMap, json);
        } catch (Exception e) {
            exception = e;
            throw new HttpRequestException(e, 0);
        } finally {
            if (null != exception) {
                try {
                    NetworkUtils.getInstance().popServerFailDialog(call, exception);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 华为登录注册接口
     * 操作类 使用 SHA256WithRSA签名
     *
     * @param hwAccessToken hwAccessToken
     * @param hwOpenId      hwOpenId
     * @param avatar        avatar
     * @param nickName      nickName
     * @param hwUid         hwUid
     * @param utdid         utdid
     * @return String
     * @throws Exception 异常
     */
    public String launchRegisterRequest(String hwUid, String hwAccessToken, String hwOpenId, String avatar, String nickName, String utdid) throws Exception {
        Map<String, Object> params = newHashMap();
        params.put("at", hwAccessToken);
        params.put("hwUid", hwUid);
        params.put("hwOpenId", hwOpenId);
        params.put("avatar", avatar);
        params.put("nickName", nickName);
        params.put("utdid", utdid);
        params.put("brand", getBrand());
        params.put("model", getModel());
        params.put("domain", AppConst.DOMAIN);
        params.put("channelCode", DeviceInfoUtils.getInstanse().getChannel());
        params.put("ei", HwEncryptParam.hwEncrpt(DeviceInfoUtils.getInstanse().getImei()));

        String queryString = getQueryString();
        String postBody = JsonUtils.fromHashMap(params);

        Map<String, String> headMap = getCommonHeader();
        headMap.put(KEY_SIGN_TYPE, HwEncrpt.SIGN_TYPE_2 + "");
        headMap.put(KEY_SIGN, HwEncrpt.hwEncrptSign(queryString + postBody, HwEncrpt.SIGN_TYPE_2));
        return toRequest(RequestCall.REGISTER_CALL, queryString, headMap, postBody);
    }


    /**
     * 设备激活
     * 操作类 使用 SHA256WithRSA签名
     *
     * @param utdId utdId
     * @return String
     * @throws Exception 异常
     */
    public String launchDeviceActivationRequest(String utdId) throws Exception {
        Map<String, Object> params = newHashMap();
        params.put("utdid", utdId);
        params.put("brand", getBrand());
        params.put("model", getModel());
        params.put("channelCode", DeviceInfoUtils.getInstanse().getChannel());
        params.put("domain", AppConst.DOMAIN);
        params.put("ei", HwEncryptParam.hwEncrpt(DeviceInfoUtils.getInstanse().getImei()));

        String queryString = getQueryString();
        String postBody = JsonUtils.fromHashMap(params);

        Map<String, String> headMap = getCommonHeader();
        headMap.put(KEY_SIGN_TYPE, HwEncrpt.SIGN_TYPE_2 + "");
        headMap.put(KEY_SIGN, HwEncrpt.hwEncrptSign(queryString + postBody, HwEncrpt.SIGN_TYPE_2));
        return toRequest(RequestCall.DEVICE_ACTIVATION_CALL, queryString, headMap, postBody);
    }

    /**
     * 获取用户信息
     *
     * @return String
     * @throws Exception 异常
     */
    public String getUserInfoRequest() throws Exception {
        String queryString = getQueryString();

        Map<String, String> headMap = getCommonHeader();
        headMap.put(KEY_SIGN_TYPE, HwEncrpt.SIGN_TYPE_1 + "");
        headMap.put(KEY_SIGN, HwEncrpt.hwEncrptSign(queryString, HwEncrpt.SIGN_TYPE_1));

        return toRequest(RequestCall.GET_USER_INFO_CALL, queryString, headMap, null);
    }

    /**
     * 获取兑换礼品结果
     *
     * @param code code
     * @return String
     * @throws Exception 异常
     */
    public String getGiftExchangeRequest(String code) throws Exception {
        String queryString = getQueryString();
        Map<String, Object> params = newHashMap();
        params.put("code", code);
        String postBody = JsonUtils.fromHashMap(params);
        Map<String, String> headMap = getCommonHeader();
        headMap.put(KEY_SIGN_TYPE, HwEncrpt.SIGN_TYPE_2 + "");
        headMap.put(KEY_SIGN, HwEncrpt.hwEncrptSign(queryString + postBody, HwEncrpt.SIGN_TYPE_2));
        return toRequest(RequestCall.GET_GIFT_EXCHANGE_CALL, queryString, headMap, postBody);
    }

    /**
     * 搜索
     *
     * @param keyWord    keyWord
     * @param index      index
     * @param size       size
     * @param searchType searchType
     * @return String
     * @throws Exception 异常
     */
    public String searchRequest(String keyWord, int index, int size, String searchType) throws Exception {
        Map<String, Object> params = newHashMap();
        params.put("keyword", keyWord);
        params.put("index", index);
        params.put("size", size);
        params.put("searchType", searchType);
        String queryString = getQueryString();
        String postBody = JsonUtils.fromHashMap(params);
        Map<String, String> headMap = getCommonHeader();
        headMap.put(KEY_SIGN_TYPE, HwEncrpt.SIGN_TYPE_1 + "");
        headMap.put(KEY_SIGN, HwEncrpt.hwEncrptSign(queryString + postBody, HwEncrpt.SIGN_TYPE_1));

        return toRequest(RequestCall.SEARCH_CALL, queryString, headMap, postBody);
    }

    /**
     * 联想词搜索
     *
     * @param keyWord keyWord
     * @return string
     * @throws Exception 异常
     */
    public String searchSuggestRequest(String keyWord) throws Exception {
        Map<String, Object> params = newHashMap();
        params.put("keyword", keyWord);
        String queryString = getQueryString();
        String postBody = JsonUtils.fromHashMap(params);
        Map<String, String> headMap = getCommonHeader();
        headMap.put(KEY_SIGN_TYPE, HwEncrpt.SIGN_TYPE_1 + "");
        headMap.put(KEY_SIGN, HwEncrpt.hwEncrptSign(queryString + postBody, HwEncrpt.SIGN_TYPE_1));

        return toRequest(RequestCall.SEARCH_SUGGEST_CALL, queryString, headMap, postBody);
    }

    /**
     * 搜索首页热词
     *
     * @param sex sex
     * @return string
     * @throws Exception 异常
     */
    public String searchHotRequest(int sex) throws Exception {
        Map<String, Object> params = newHashMap();
        if (sex == 1 || sex == 2) {
            params.put("sex", sex);
        }
        String queryString = getQueryString();
        String postBody = JsonUtils.fromHashMap(params);
        Map<String, String> headMap = getCommonHeader();
        headMap.put(KEY_SIGN_TYPE, HwEncrpt.SIGN_TYPE_1 + "");
        headMap.put(KEY_SIGN, HwEncrpt.hwEncrptSign(queryString + postBody, HwEncrpt.SIGN_TYPE_1));

        return toRequest(RequestCall.SEARCH_HOT_CALL, queryString, headMap, postBody);
    }

    /**
     * 分享领看点
     *
     * @param type type
     * @return string
     * @throws Exception 异常
     */
    public String shareKd(int type) throws Exception {
        Map<String, Object> params = newHashMap();
        params.put("type", type);
        String queryString = getQueryString();
        String postBody = JsonUtils.fromHashMap(params);
        Map<String, String> headMap = getCommonHeader();
        headMap.put(KEY_SIGN_TYPE, HwEncrpt.SIGN_TYPE_1 + "");
        headMap.put(KEY_SIGN, HwEncrpt.hwEncrptSign(queryString + postBody, HwEncrpt.SIGN_TYPE_1));

        return toRequest(RequestCall.SHARE_KD_CALL, queryString, headMap, postBody);
    }

    /**
     * 更多推荐书籍
     *
     * @param bookId   bookId
     * @param page     page
     * @param pageSize pageSize
     * @param type     1.作者2.同类型
     * @return string
     * @throws Exception 异常
     */
    public String moreRecommendBooks(String bookId, int page, int pageSize, int type) throws Exception {
        Map<String, Object> params = newHashMap();
        params.put("bookId", bookId);
        params.put("page", page);
        params.put("pageSize", pageSize);
        params.put("type", type);
        String queryString = getQueryString();
        String postBody = JsonUtils.fromHashMap(params);
        Map<String, String> headMap = getCommonHeader();
        headMap.put(KEY_SIGN_TYPE, HwEncrpt.SIGN_TYPE_1 + "");
        headMap.put(KEY_SIGN, HwEncrpt.hwEncrptSign(queryString + postBody, HwEncrpt.SIGN_TYPE_1));

        return toRequest(RequestCall.MORE_RECOMMEND_CALL, queryString, headMap, postBody);
    }

    /**
     * 关闭
     *
     * @param action       action
     * @param readDuration readDuration
     * @return string
     * @throws Exception 异常
     */
    public String finishTask(String action, int readDuration) throws Exception {
        Map<String, Object> params = newHashMap();
        params.put("action", action);
        params.put("readDuration", readDuration);
        String queryString = getQueryString();
        String postBody = JsonUtils.fromHashMap(params);
        Map<String, String> headMap = getCommonHeader();
        headMap.put(KEY_SIGN_TYPE, HwEncrpt.SIGN_TYPE_2 + "");
        headMap.put(KEY_SIGN, HwEncrpt.hwEncrptSign(queryString + postBody, HwEncrpt.SIGN_TYPE_2));
        return toRequest(RequestCall.FINISH_TAKS, queryString, headMap, postBody);
    }

    /**
     * 快速打开
     *
     * @param bookId    bookId
     * @param chapterId chapterId
     * @return string
     * @throws Exception 异常
     */
    public String fastOpenBookRequest(String bookId, String chapterId) throws Exception {
        Map<String, Object> params = newHashMap();
        params.put("bookId", bookId);
        params.put("chapterId", chapterId);
        String queryString = getQueryString();
        String postBody = JsonUtils.fromHashMap(params);
        Map<String, String> headMap = getCommonHeader();
        headMap.put(KEY_SIGN_TYPE, HwEncrpt.SIGN_TYPE_1 + "");
        headMap.put(KEY_SIGN, HwEncrpt.hwEncrptSign(queryString + postBody, HwEncrpt.SIGN_TYPE_1));
        return toRequest(RequestCall.FAST_OPEN_BOOK_CALL, queryString, headMap, postBody);
    }

    /**
     * 发表评论
     *
     * @param bookId    bookId
     * @param content   content
     * @param score     score
     * @param bookName  bookName
     * @param type      type
     * @param commentId commentId
     * @return string
     * @throws Exception 异常
     */
    public String sendCommentRequest(String bookId, String content, int score, String bookName, int type, String commentId) throws Exception {
        Map<String, Object> params = newHashMap();
        params.put("bookId", bookId);
        params.put("content", content);
        params.put("score", score);
        params.put("bookName", bookName);
        params.put("type", type);
        params.put("commentId", commentId);
        String queryString = getQueryString();
        String postBody = JsonUtils.fromHashMap(params);
        Map<String, String> headMap = getCommonHeader();
        headMap.put(KEY_SIGN_TYPE, HwEncrpt.SIGN_TYPE_2 + "");
        headMap.put(KEY_SIGN, HwEncrpt.hwEncrptSign(queryString + postBody, HwEncrpt.SIGN_TYPE_2));

        return toRequest(RequestCall.SEND_COMMENT_CALL, queryString, headMap, postBody);
    }

    /**
     * 查看更多评论
     *
     * @param bookId    bookId
     * @param pageIndex pageIndex
     * @param pageSize  pageSize
     * @return String
     * @throws Exception 异常
     */
    public String moreCommentRequest(String bookId, int pageIndex, int pageSize) throws Exception {
        Map<String, Object> params = newHashMap();
        params.put("bookId", bookId);
        params.put("pageIndex", pageIndex);
        params.put("pageSize", pageSize);
        String queryString = getQueryString();
        String postBody = JsonUtils.fromHashMap(params);
        Map<String, String> headMap = getCommonHeader();
        headMap.put(KEY_SIGN_TYPE, HwEncrpt.SIGN_TYPE_1 + "");
        headMap.put(KEY_SIGN, HwEncrpt.hwEncrptSign(queryString + postBody, HwEncrpt.SIGN_TYPE_1));
        return toRequest(RequestCall.MORE_COMMENT_CALL, queryString, headMap, postBody);
    }

    /**
     * 用户评论
     *
     * @param pageIndex pageIndex
     * @param pageSize  pageSize
     * @return string
     * @throws Exception 异常
     */
    public String userCommentRequest(int pageIndex, int pageSize) throws Exception {
        Map<String, Object> params = newHashMap();
        params.put("pageIndex", pageIndex);
        params.put("pageSize", pageSize);
        String queryString = getQueryString();
        String postBody = JsonUtils.fromHashMap(params);
        Map<String, String> headMap = getCommonHeader();
        headMap.put(KEY_SIGN_TYPE, HwEncrpt.SIGN_TYPE_1 + "");
        headMap.put(KEY_SIGN, HwEncrpt.hwEncrptSign(queryString + postBody, HwEncrpt.SIGN_TYPE_1));
        return toRequest(RequestCall.USER_COMMENT_CALL, queryString, headMap, postBody);
    }

    /**
     * 校验评论
     *
     * @param bookId bookId
     * @return String
     * @throws Exception 异常
     */
    public String checkCommentRequest(String bookId) throws Exception {
        Map<String, Object> params = newHashMap();
        params.put("bookId", bookId);
        String queryString = getQueryString();
        String postBody = JsonUtils.fromHashMap(params);
        Map<String, String> headMap = getCommonHeader();
        headMap.put(KEY_SIGN_TYPE, HwEncrpt.SIGN_TYPE_1 + "");
        headMap.put(KEY_SIGN, HwEncrpt.hwEncrptSign(queryString + postBody, HwEncrpt.SIGN_TYPE_1));
        return toRequest(RequestCall.CHECK_COMMENT_CALL, queryString, headMap, postBody);
    }

    /**
     * 评论action
     *
     * @param type      type
     * @param bookId    bookId
     * @param commentId commentId
     * @return String
     * @throws Exception 异常
     */
    public String commentActionRequest(int type, String bookId, String commentId) throws Exception {
        Map<String, Object> params = newHashMap();
        params.put("bookId", bookId);
        params.put("type", type);
        params.put("commentId", commentId);
        String queryString = getQueryString();
        String postBody = JsonUtils.fromHashMap(params);
        Map<String, String> headMap = getCommonHeader();
        headMap.put(KEY_SIGN_TYPE, HwEncrpt.SIGN_TYPE_2 + "");
        headMap.put(KEY_SIGN, HwEncrpt.hwEncrptSign(queryString + postBody, HwEncrpt.SIGN_TYPE_2));
        return toRequest(RequestCall.ACTION_COMMENT_CALL, queryString, headMap, postBody);
    }

    /**
     * 章节
     *
     * @param bookId        bookId
     * @param chapterId     chapterId
     * @param chapterOffset chapterOffset
     * @param chapterEndId  chapterEndId
     * @param needBlockList needBlockList
     * @return String
     * @throws Exception 异常
     */
    public String chapterCatalog(String bookId, String chapterId, String chapterOffset, String chapterEndId, String needBlockList) throws Exception {
        Map<String, Object> params = newHashMap();
        params.put("bookId", bookId);
        params.put("chapterId", chapterId);
        params.put("chapterOffset", chapterOffset);
        params.put("chapterEndId", chapterEndId);
        params.put("needBlockList", needBlockList);
        String queryString = getQueryString();
        String postBody = JsonUtils.fromHashMap(params);
        Map<String, String> headMap = getCommonHeader();
        headMap.put(KEY_SIGN_TYPE, HwEncrpt.SIGN_TYPE_1 + "");
        headMap.put(KEY_SIGN, HwEncrpt.hwEncrptSign(queryString + postBody, HwEncrpt.SIGN_TYPE_1));
        return toRequest(RequestCall.CATALOG_COMMENT_CALL, queryString, headMap, postBody);
    }

    /**
     * 终章推荐
     *
     * @param bookId bookId
     * @return String
     * @throws Exception 异常
     */
    public String bookRecommendRequest(String bookId) throws Exception {
        Map<String, Object> params = newHashMap();
        params.put("bookId", bookId);
        String queryString = getQueryString();
        String postBody = JsonUtils.fromHashMap(params);
        Map<String, String> headMap = getCommonHeader();
        headMap.put(KEY_SIGN_TYPE, HwEncrpt.SIGN_TYPE_1 + "");
        headMap.put(KEY_SIGN, HwEncrpt.hwEncrptSign(queryString + postBody, HwEncrpt.SIGN_TYPE_1));
        return toRequest(RequestCall.BOOK_RECOMMEND_CALL, queryString, headMap, postBody);
    }

    /**
     * 获取分类页面 一级页面数据
     * 查询类 用的算法 1
     *
     * @return String
     * @throws Exception 异常
     */
    public String getMainTypeIndex() throws Exception {
        String queryString = getQueryString();
        Map<String, String> headMap = getCommonHeader();
        headMap.put(KEY_SIGN_TYPE, HwEncrpt.SIGN_TYPE_1 + "");
        headMap.put(KEY_SIGN, HwEncrpt.hwEncrptSign(queryString, HwEncrpt.SIGN_TYPE_1));
        return toRequest(RequestCall.MAIN_TYPE_INDEX, queryString, headMap, null);
    }

    /**
     * 分类 二级页面
     *
     * @param sort      排序
     * @param tid       tid
     * @param status    状态
     * @param cid       分类三级id
     * @param flag      要不要title
     * @param pageIndex 页码
     * @param pageSize  每页多少
     * @return String
     * @throws Exception 异常
     */
    public String getMainTypeDetailData(String sort, String tid, String status, String cid, String flag, String pageIndex, String pageSize) throws Exception {
        Map<String, Object> params = newHashMap();
        params.put("sort", sort);
        params.put("tid", tid);
        params.put("status", status);
        params.put("sort", sort);
        params.put("cid", cid);
        params.put("flag", flag);
        params.put("index", pageIndex);
        params.put("size", pageSize);

        String queryString = getQueryString();
        String postBody = JsonUtils.fromHashMap(params);

        Map<String, String> headMap = getCommonHeader();
        headMap.put(KEY_SIGN_TYPE, HwEncrpt.SIGN_TYPE_1 + "");
        headMap.put(KEY_SIGN, HwEncrpt.hwEncrptSign(queryString + postBody, HwEncrpt.SIGN_TYPE_1));

        return toRequest(RequestCall.MAIN_TYPE_DETAIL, queryString, headMap, postBody);
    }

    /**
     * 内置书接口
     *
     * @return ：接口返回数据
     * @throws Exception ：异常
     */
    public String buildInBooK() throws Exception {
        String queryString = getQueryString();
        Map<String, String> headMap = getCommonHeader();
        headMap.put(KEY_SIGN_TYPE, HwEncrpt.SIGN_TYPE_1 + "");
        headMap.put(KEY_SIGN, HwEncrpt.hwEncrptSign(queryString, HwEncrpt.SIGN_TYPE_1));
        return toRequest(RequestCall.BUILD_IN_BOOK, queryString, headMap, null);
    }

    /**
     * 图书详情
     *
     * @param bookId bookId
     * @return String
     * @throws Exception 异常
     */
    public String bookDetailRequest(String bookId) throws Exception {
        Map<String, Object> params = newHashMap();
        params.put("bookId", bookId);
        String queryString = getQueryString();
        String postBody = JsonUtils.fromHashMap(params);
        Map<String, String> headMap = getCommonHeader();
        headMap.put(KEY_SIGN_TYPE, HwEncrpt.SIGN_TYPE_1 + "");
        headMap.put(KEY_SIGN, HwEncrpt.hwEncrptSign(queryString + postBody, HwEncrpt.SIGN_TYPE_1));
        return toRequest(RequestCall.BOOK_DETAIL_CALL, queryString, headMap, postBody);
    }

    /**
     * 点赞
     *
     * @param bookId bookId
     * @return String
     * @throws Exception 异常
     */
    public String bookPraiseRequest(String bookId) throws Exception {
        Map<String, Object> params = newHashMap();
        params.put("bookId", bookId);
        String queryString = getQueryString();
        String postBody = JsonUtils.fromHashMap(params);
        Map<String, String> headMap = getCommonHeader();
        headMap.put(KEY_SIGN_TYPE, HwEncrpt.SIGN_TYPE_1 + "");
        headMap.put(KEY_SIGN, HwEncrpt.hwEncrptSign(queryString + postBody, HwEncrpt.SIGN_TYPE_1));
        return toRequest(RequestCall.BOOK_PRAISE_CALL, queryString, headMap, postBody);
    }


    /**
     * 品牌
     *
     * @return String
     */
    public static String getBrand() {
        return Build.BRAND;
    }

    /**
     * 型号
     *
     * @return String
     */
    public static String getModel() {
        return Build.MODEL;
    }


    /**
     * 书架书籍更新接口
     *
     * @param sex            :偏好
     * @param makeUpFunction ：请求功能
     * @param books          ：书籍列表          json（[{"bookId":"11000007217","chapterId": ""},{"bookId":"11000008404","chapterId": ""},{"bookId":"11000079808","chapterId": ""}]）
     * @return String
     * @throws Exception 异常
     */
    public String shelfBookUpdate(String sex, String makeUpFunction, ArrayList<BeanShelfBookItem> books) throws Exception {
        String queryString = getQueryString();
        JSONObject jsonObject = new JSONObject();
        if ("1".equals(sex) || "2".equals(sex)) {
            jsonObject.put("sex", sex);
        }
        jsonObject.put("f", makeUpFunction);
        if (books != null && books.size() > 0) {
            JSONArray array = new JSONArray();
            for (BeanShelfBookItem item : books) {
                if (item != null) {
                    JSONObject itemObject = new JSONObject();
                    itemObject.put("bookId", item.getBookId());
                    itemObject.put("chapterId", item.getChapterId());
                    array.put(itemObject);
                }
            }
            jsonObject.put("bidCidList", array);
        }
        String postBody = jsonObject.toString();
        Map<String, String> headMap = getCommonHeader();
        headMap.put(KEY_SIGN_TYPE, HwEncrpt.SIGN_TYPE_1 + "");
        headMap.put(KEY_SIGN, HwEncrpt.hwEncrptSign(queryString + postBody, HwEncrpt.SIGN_TYPE_1));

        return toRequest(RequestCall.SHELF_UPDATE_URL, queryString, headMap, postBody);
    }

    /**
     * 网络请求书城数据
     *
     * @param readPref    :偏好
     * @param channelId   ：频道id
     * @param channelType channelType
     * @return String
     * @throws Exception 异常
     */
    public String getStoreDataFromNet(String readPref, String channelId, String channelType) throws Exception {
        Map<String, Object> params = newHashMap();
        params.put("readPref", readPref);
        params.put("channelId", channelId);
        params.put("type", channelType);
        String queryString = getQueryString();
        String postBody = JsonUtils.fromHashMap(params);

        Map<String, String> headMap = getCommonHeader();
        headMap.put(KEY_SIGN_TYPE, HwEncrpt.SIGN_TYPE_1 + "");
        headMap.put(KEY_SIGN, HwEncrpt.hwEncrptSign(queryString + postBody, HwEncrpt.SIGN_TYPE_1));

        return toRequest(RequestCall.STORE_DATA_URL, queryString, headMap, postBody);
    }

    /**
     * 网络请求书城数据
     *
     * @param id       :栏目id
     * @param tabId    ：频道id
     * @param readPref ：偏好
     * @return String
     * @throws Exception 异常
     */
    public String getStoreTwoPageDataFromNet(String readPref, String id, String tabId) throws Exception {
        Map<String, Object> params = newHashMap();
        params.put("readPref", readPref);
        params.put("id", id);
        params.put("tabId", tabId);

        String queryString = getQueryString();
        String postBody = JsonUtils.fromHashMap(params);

        Map<String, String> headMap = getCommonHeader();
        headMap.put(KEY_SIGN_TYPE, HwEncrpt.SIGN_TYPE_1 + "");
        headMap.put(KEY_SIGN, HwEncrpt.hwEncrptSign(queryString + postBody, HwEncrpt.SIGN_TYPE_1));

        return toRequest(RequestCall.STORE_TWO_LEVEL_DATA_URL, queryString, headMap, postBody);
    }

    /**
     * 书城更多接口数据
     *
     * @param url :接口请求url
     * @return String
     * @throws Exception 异常
     */
    public String getStoreMoreDataFromNet(String url) throws Exception {
        String paramStr = null;
        if (url.contains("?")) {
            String[] urls = url.split("[?]");
            if (urls != null && urls.length > 0) {
                url = urls[0];
                paramStr = urls[1];
            }
        }

        String queryString = getQueryString();
        Map<String, String> headMap = getCommonHeader();
        headMap.put(KEY_SIGN_TYPE, HwEncrpt.SIGN_TYPE_1 + "");
        headMap.put(KEY_SIGN, HwEncrpt.hwEncrptSign(queryString + paramStr, HwEncrpt.SIGN_TYPE_1));

        return toRequestByUrl(url, queryString, headMap, paramStr);
    }


    /**
     * 签署或者查询用户协议
     *
     * @param signInfoJson 必须urlncode
     * @param token        用户级别token
     * @param type         type=1 签署 type=2 查询签署状况
     * @return String
     * @throws Exception 异常
     */
    public String signOrQueryAgreement(String signInfoJson, String token, int type) throws Exception {
        HashMap<String, String> map = new HashMap<>();
        map.put("nsp_svc", type == 1 ? "as.user.sign" : "as.user.query");
        map.put("access_token", token);
        map.put("request", signInfoJson);
        return OkhttpUtils.getInstance().okHttpRequestGet(type == 1 ? RequestCall.getTmsUrlReqSign() : RequestCall.getTmsUrlReqQuery(type), map, true);
    }

    /**
     * 查询老用户资产
     *
     * @param uid userId
     * @return String
     * @throws Exception Exception
     */
    public String getOldUserAssertRequest(String uid) throws Exception {
        Map<String, Object> params = newHashMap();
        params.put("uid", uid);
        String queryString = getQueryString();
        String postBody = JsonUtils.fromHashMap(params);
        Map<String, String> headMap = getCommonHeader();
        headMap.put(KEY_SIGN_TYPE, HwEncrpt.SIGN_TYPE_1 + "");
        headMap.put(KEY_SIGN, HwEncrpt.hwEncrptSign(queryString + postBody, HwEncrpt.SIGN_TYPE_1));
        return toRequest(RequestCall.URL_OLD_USER_ASSETS, queryString, headMap, postBody);
    }

    /**
     * 订购-后台多章加载
     *
     * @param bookId     书籍id
     * @param chapterIds 预加载的章节列表
     * @param autoPay    是否直接购买：1，否；2，是
     * @return String
     * @throws Exception 异常
     */
    public String preloadLotChapterRequest(String bookId, ArrayList<String> chapterIds, String autoPay) throws Exception {
        Map<String, Object> params = newHashMap();
        params.put("bookId", bookId);
        params.put("chapterIds", chapterIds);
        params.put("autoPay", autoPay);
        String queryString = getQueryString();
        String postBody = JsonUtils.fromHashMap(params);
        Map<String, String> headMap = getCommonHeader();
        headMap.put(KEY_SIGN_TYPE, HwEncrpt.SIGN_TYPE_2 + "");
        headMap.put(KEY_SIGN, HwEncrpt.hwEncrptSign(queryString + postBody, HwEncrpt.SIGN_TYPE_2));

        return toRequest(RequestCall.URL_LOT_PRELOAD, queryString, headMap, postBody);
    }


    /**
     * 订购-加载已经订购章节
     *
     * @param bookId    书籍id
     * @param chapterId 章节id，当前在读章节之后第一个无内容章节id
     * @return String
     * @throws Exception 异常
     */
    public String loadAlreadyOrderChapterRequest(String bookId, String chapterId) throws Exception {
        Map<String, Object> params = newHashMap();
        params.put("bookId", bookId);
        params.put("chapterId", chapterId);
        String queryString = getQueryString();
        String postBody = JsonUtils.fromHashMap(params);
        Map<String, String> headMap = getCommonHeader();
        headMap.put(KEY_SIGN_TYPE, HwEncrpt.SIGN_TYPE_2 + "");
        headMap.put(KEY_SIGN, HwEncrpt.hwEncrptSign(queryString + postBody, HwEncrpt.SIGN_TYPE_2));

        return toRequest(RequestCall.URL_LOAD_ALREADY_ORDER_CHAPTER, queryString, headMap, postBody);
    }

    /**
     * 领书接口（限免/限价）
     *
     * @param productId ：商品id
     * @param bookId    ：书籍id
     * @param type      ：领书类型
     * @return String
     * @throws Exception 异常
     */
    public String getBookInfoFromNet(String productId, String bookId, String type) throws Exception {
        Map<String, Object> params = newHashMap();
        params.put("productId", productId);
        params.put("bookId", bookId);
        params.put("type", type);
        String queryString = getQueryString();
        String postBody = JsonUtils.fromHashMap(params);
        Map<String, String> headMap = getCommonHeader();
        headMap.put(KEY_SIGN_TYPE, HwEncrpt.SIGN_TYPE_2 + "");
        headMap.put(KEY_SIGN, HwEncrpt.hwEncrptSign(queryString + postBody, HwEncrpt.SIGN_TYPE_2));

        return toRequest(RequestCall.URL_GET_FREE_BOOK_FROM_NET, queryString, headMap, postBody);
    }

    /**
     * 实名认证 发送手机短信
     *
     * @param phoneNum phoneNum
     * @return String
     * @throws Exception 异常
     */
    public String getVerifyByPhoneRequest(String phoneNum) throws Exception {
        Map<String, Object> params = newHashMap();
        if (!TextUtils.isEmpty(phoneNum)) {
            params.put("phoneNum", HwEncryptParam.hwEncrpt(phoneNum));
        }
        String queryString = getQueryString();
        String postBody = JsonUtils.fromHashMap(params);
        Map<String, String> headMap = getCommonHeader();
        headMap.put(KEY_SIGN_TYPE, HwEncrpt.SIGN_TYPE_1 + "");
        headMap.put(KEY_SIGN, HwEncrpt.hwEncrptSign(queryString + postBody, HwEncrpt.SIGN_TYPE_1));
        return toRequest(RequestCall.REAL_NAME_AUTH, queryString, headMap, postBody);
    }

    /**
     * 发送绑定手机号
     *
     * @param type       1 绑定 2是解除绑定时  当type=2时  phoneNum不需要传。
     * @param phoneNum   手机号
     * @param verifyCode 验证码
     * @return String
     * @throws Exception 异常
     */
    public String sendVerifyByPhoneRequest(int type, String phoneNum, String verifyCode) throws Exception {
        Map<String, Object> params = newHashMap();
        if (!TextUtils.isEmpty(phoneNum)) {
            params.put("phoneNum", HwEncryptParam.hwEncrpt(phoneNum));
        }
        params.put("type", type);
        params.put("verifyCode", verifyCode);
        String queryString = getQueryString();
        String postBody = JsonUtils.fromHashMap(params);
        Map<String, String> headMap = getCommonHeader();
        headMap.put(KEY_SIGN_TYPE, HwEncrpt.SIGN_TYPE_2 + "");
        headMap.put(KEY_SIGN, HwEncrpt.hwEncrptSign(queryString + postBody, HwEncrpt.SIGN_TYPE_2));
        return toRequest(RequestCall.REAL_NAME_BIND_PHONE, queryString, headMap, postBody);
    }

    /**
     * 实名认证  获取 之前绑定的手机号信息。
     *
     * @return String
     * @throws Exception 异常
     */
    public String getSwitchPhoneNumInfo() throws Exception {
        String queryString = getQueryString();
        Map<String, String> headMap = getCommonHeader();
        headMap.put(KEY_SIGN_TYPE, HwEncrpt.SIGN_TYPE_1 + "");
        headMap.put(KEY_SIGN, HwEncrpt.hwEncrptSign(queryString, HwEncrpt.SIGN_TYPE_1));
        return toRequest(RequestCall.REAL_NAME_SWITCH_PHONE, queryString, headMap, null);
    }

    /**
     * 插件列表
     *
     * @return String
     * @throws Exception 异常
     */
    public String getPluginInfo() throws Exception {
        String queryString = getQueryString();
        Map<String, String> headMap = getCommonHeader();
        headMap.put(KEY_SIGN_TYPE, HwEncrpt.SIGN_TYPE_1 + "");
        headMap.put(KEY_SIGN, HwEncrpt.hwEncrptSign(queryString, HwEncrpt.SIGN_TYPE_1));
        return toRequest(RequestCall.URL_TTS_PLUGIN, queryString, headMap, null);
    }

    /**
     * 登录后同步云书架
     *
     * @param bookIds bookIds
     * @return String
     * @throws Exception 异常
     */
    public String cloudShelfLoginSync(String bookIds) throws Exception {
        String queryString = getQueryString();
        Map<String, String> headMap = getCommonHeader();
        headMap.put(KEY_SIGN_TYPE, HwEncrpt.SIGN_TYPE_1 + "");
        headMap.put(KEY_SIGN, HwEncrpt.hwEncrptSign(queryString + bookIds, HwEncrpt.SIGN_TYPE_1));
        return toRequest(RequestCall.URL_CLOUD_SHELF_LOGIN_SYNC, queryString, headMap, bookIds);
    }

    /**
     * 获取云书架同步后的数据详情列表
     *
     * @param bookIds bookIds
     * @return String
     * @throws Exception 异常
     */
    public String getCloudShelfBookDetail(ArrayList<String> bookIds) throws Exception {
        String queryString = getQueryString();
        JSONArray array = new JSONArray();
        for (int i = 0; i < bookIds.size(); i++) {
            array.put(bookIds.get(i));
        }
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("bookIds", array);
        String postBody = jsonObject.toString();
        Map<String, String> headMap = getCommonHeader();
        headMap.put(KEY_SIGN_TYPE, HwEncrpt.SIGN_TYPE_1 + "");
        headMap.put(KEY_SIGN, HwEncrpt.hwEncrptSign(queryString + postBody, HwEncrpt.SIGN_TYPE_1));
        return toRequest(RequestCall.URL_CLOUD_SHELF_BOOK_DETAIL, queryString, headMap, postBody);
    }

    /**
     * 同步书籍的阅读进度
     *
     * @param bookId     ：书籍id
     * @param chapterId  ：章节id
     * @param operateDur ：时间
     * @throws Exception 异常
     */
    public void syncBookReadProgress(String bookId, String chapterId, String operateDur) throws Exception {
        Map<String, Object> params = newHashMap();
        params.put("bookId", bookId);
        params.put("chapterId", chapterId);
        params.put("operateDur", operateDur);
        String queryString = getQueryString();
        String postBody = JsonUtils.fromHashMap(params);
        Map<String, String> headMap = getCommonHeader();
        headMap.put(KEY_SIGN_TYPE, HwEncrpt.SIGN_TYPE_2 + "");
        headMap.put(KEY_SIGN, HwEncrpt.hwEncrptSign(queryString + postBody, HwEncrpt.SIGN_TYPE_2));
        toRequest(RequestCall.URL_SYNC_BOOK_PROGRESS, queryString, headMap, postBody);
    }

    /**
     * 云书架：同步远程书籍的阅读进度
     *
     * @param bookId    ：书籍id
     * @param chapterId ：章节id
     * @return String
     * @throws Exception 异常
     */
    public String syncBookReadProgressFromNet(String bookId, String chapterId) throws Exception {
        Map<String, Object> params = newHashMap();
        params.put("bookId", bookId);
        params.put("chapterId", chapterId);
        String queryString = getQueryString();
        String postBody = JsonUtils.fromHashMap(params);
        Map<String, String> headMap = getCommonHeader();
        headMap.put(KEY_SIGN_TYPE, HwEncrpt.SIGN_TYPE_1 + "");
        headMap.put(KEY_SIGN, HwEncrpt.hwEncrptSign(queryString + postBody, HwEncrpt.SIGN_TYPE_1));
        return toRequest(RequestCall.URL_CLOUD_SHELF_SYNC_BOOK_PROGRESS, queryString, headMap, postBody);
    }

    /**
     * 云书架：页面数据获取
     *
     * @param page         ：页码
     * @param size         ：数量
     * @param lastItemTime lastItemTime
     * @return String
     * @throws Exception 异常
     */
    public String getBeanCloudShelfPageList(String page, String size, String lastItemTime) throws Exception {
        Map<String, Object> params = newHashMap();
        params.put("page", page);
        params.put("size", size);
        params.put("utime", lastItemTime);
        String queryString = getQueryString();
        String postBody = JsonUtils.fromHashMap(params);
        Map<String, String> headMap = getCommonHeader();
        headMap.put(KEY_SIGN_TYPE, HwEncrpt.SIGN_TYPE_1 + "");
        headMap.put(KEY_SIGN, HwEncrpt.hwEncrptSign(queryString + postBody, HwEncrpt.SIGN_TYPE_1));
        return toRequest(RequestCall.URL_CLOUD_SHELF_GET_LIST, queryString, headMap, postBody);
    }

    /**
     * 云书架：删除书籍
     *
     * @param bookIds ：删除书籍id
     * @param size    ：数量
     * @return String
     * @throws Exception 异常
     */
    public String deleteCloudShelfData(String bookIds, String size) throws Exception {
        Map<String, Object> params = newHashMap();
        params.put("bookId", bookIds);
        params.put("size", size);
        String queryString = getQueryString();
        String postBody = JsonUtils.fromHashMap(params);
        Map<String, String> headMap = getCommonHeader();
        headMap.put(KEY_SIGN_TYPE, HwEncrpt.SIGN_TYPE_2 + "");
        headMap.put(KEY_SIGN, HwEncrpt.hwEncrptSign(queryString + postBody, HwEncrpt.SIGN_TYPE_2));
        return toRequest(RequestCall.URL_CLOUD_SHELF_DELETE_BOOK, queryString, headMap, postBody);
    }

    /**
     * 领取缺失内容奖励接口
     *
     * @param bookId    bookId
     * @param chapterId chapterId
     * @return String
     * @throws Exception 异常
     */
    public String receiveMissContentAwardRequest(String bookId, String chapterId) throws Exception {
        Map<String, Object> params = newHashMap();
        params.put("bookId", bookId);
        params.put("chapterId", chapterId);
        String queryString = getQueryString();
        String postBody = JsonUtils.fromHashMap(params);
        Map<String, String> headMap = getCommonHeader();
        headMap.put(KEY_SIGN_TYPE, HwEncrpt.SIGN_TYPE_2 + "");
        headMap.put(KEY_SIGN, HwEncrpt.hwEncrptSign(queryString + postBody, HwEncrpt.SIGN_TYPE_2));
        return toRequest(RequestCall.URL_MISS_CHAPTER_CONTENT_AWARD, queryString, headMap, postBody);
    }


    /**
     * 获取充值记录
     *
     * @param index    默认index为1，第几页
     * @param totalNum 每页返回条数
     * @return String
     * @throws Exception 异常
     */
    public String getRechargeRecordRequest(String index, String totalNum) throws Exception {
        Map<String, Object> params = newHashMap();
        params.put("index", index);
        params.put("totalNum", totalNum);
        String queryString = getQueryString();
        String postBody = JsonUtils.fromHashMap(params);
        Map<String, String> headMap = getCommonHeader();
        headMap.put(KEY_SIGN_TYPE, HwEncrpt.SIGN_TYPE_1 + "");
        headMap.put(KEY_SIGN, HwEncrpt.hwEncrptSign(queryString + postBody, HwEncrpt.SIGN_TYPE_1));
        return toRequest(RequestCall.URL_RECHARGE_RECORD, queryString, headMap, postBody);
    }

    /**
     * 获取代金券
     *
     * @param index 默认index为1，第几页
     * @return String
     * @throws Exception 异常
     */
    public String getVouchersListRequest(String index) throws Exception {
        Map<String, Object> params = newHashMap();
        params.put("index", index);
        String queryString = getQueryString();
        String postBody = JsonUtils.fromHashMap(params);
        Map<String, String> headMap = getCommonHeader();
        headMap.put(KEY_SIGN_TYPE, HwEncrpt.SIGN_TYPE_1 + "");
        headMap.put(KEY_SIGN, HwEncrpt.hwEncrptSign(queryString + postBody, HwEncrpt.SIGN_TYPE_1));
        return toRequest(RequestCall.URL_VOUCHERS_LIST, queryString, headMap, postBody);
    }

    /**
     * 获取礼品列表
     *
     * @param index index
     * @return String
     * @throws Exception 异常
     */
    public String getGiftListRequest(String index) throws Exception {
        Map<String, Object> params = newHashMap();
        params.put("pageIndex", index);
        String queryString = getQueryString();
        String postBody = JsonUtils.fromHashMap(params);
        Map<String, String> headMap = getCommonHeader();
        headMap.put(KEY_SIGN_TYPE, HwEncrpt.SIGN_TYPE_1 + "");
        headMap.put(KEY_SIGN, HwEncrpt.hwEncrptSign(queryString + postBody, HwEncrpt.SIGN_TYPE_1));
        return toRequest(RequestCall.URL_GIFT_LIST, queryString, headMap, postBody);
    }

    /**
     * 获取书籍消费记录汇总
     *
     * @param index    默认index为1，第几页
     * @param totalNum 每页返回条数
     * @return String
     * @throws Exception 异常
     */
    public String getBookConsumeSummaryRequest(String index, String totalNum) throws Exception {
        Map<String, Object> params = newHashMap();
        params.put("index", index);
        params.put("totalNum", totalNum);
        String queryString = getQueryString();
        String postBody = JsonUtils.fromHashMap(params);
        Map<String, String> headMap = getCommonHeader();
        headMap.put(KEY_SIGN_TYPE, HwEncrpt.SIGN_TYPE_1 + "");
        headMap.put(KEY_SIGN, HwEncrpt.hwEncrptSign(queryString + postBody, HwEncrpt.SIGN_TYPE_1));
        return toRequest(RequestCall.URL_BOOKS_CONSUME_SUMMARY, queryString, headMap, postBody);
    }

    /**
     * 获取消费记录二级（活动，书籍，vip）
     *
     * @param index    默认index为1，第几页
     * @param totalNum 每页返回条数
     * @param type     type
     * @param nextId   nextId
     * @return String
     * @throws Exception 异常
     */
    public String getConsumeSecondRequest(String type, String nextId, String index, String totalNum) throws Exception {
        Map<String, Object> params = newHashMap();
        params.put("type", type);
        params.put("nextId", nextId);
        params.put("index", index);
        params.put("totalNum", totalNum);
        String queryString = getQueryString();
        String postBody = JsonUtils.fromHashMap(params);
        Map<String, String> headMap = getCommonHeader();
        headMap.put(KEY_SIGN_TYPE, HwEncrpt.SIGN_TYPE_1 + "");
        headMap.put(KEY_SIGN, HwEncrpt.hwEncrptSign(queryString + postBody, HwEncrpt.SIGN_TYPE_1));
        return toRequest(RequestCall.URL_CONSUME_SECOND_SUMMARY, queryString, headMap, postBody);
    }

    /**
     * 获取消费记录二级（活动，书籍，vip）
     *
     * @param index     默认index为1，第几页
     * @param totalNum  每页返回条数
     * @param consumeId consumeId
     * @param bookId    bookId
     * @return String
     * @throws Exception 异常
     */
    public String getConsumeThirdRequest(String consumeId, String bookId, String index, String totalNum) throws Exception {
        Map<String, Object> params = newHashMap();
        params.put("consumeId", consumeId);
        params.put("bookId", bookId);
        params.put("index", index);
        params.put("totalNum", totalNum);
        String queryString = getQueryString();
        String postBody = JsonUtils.fromHashMap(params);
        Map<String, String> headMap = getCommonHeader();
        headMap.put(KEY_SIGN_TYPE, HwEncrpt.SIGN_TYPE_1 + "");
        headMap.put(KEY_SIGN, HwEncrpt.hwEncrptSign(queryString + postBody, HwEncrpt.SIGN_TYPE_1));
        return toRequest(RequestCall.URL_CONSUME_THIRD_SUMMARY, queryString, headMap, postBody);
    }


    /**
     * VIP自动续费状态获取
     *
     * @return String
     * @throws Exception 异常
     */
    public String getVipAutoRenewStatusRequest() throws Exception {
        String queryString = getQueryString();
        Map<String, String> headMap = getCommonHeader();
        headMap.put(KEY_SIGN_TYPE, HwEncrpt.SIGN_TYPE_1 + "");
        headMap.put(KEY_SIGN, HwEncrpt.hwEncrptSign(queryString, HwEncrpt.SIGN_TYPE_1));
        return toRequest(RequestCall.URL_VIP_AUTO_RENEW_STATUS, queryString, headMap, null);
    }

    /**
     * 取消VIP自动续费
     *
     * @return String
     * @throws Exception 异常
     */
    public String cancelVipAutoRenewRequest() throws Exception {
        String queryString = getQueryString();
        Map<String, String> headMap = getCommonHeader();
        headMap.put(KEY_SIGN_TYPE, HwEncrpt.SIGN_TYPE_2 + "");
        headMap.put(KEY_SIGN, HwEncrpt.hwEncrptSign(queryString, HwEncrpt.SIGN_TYPE_2));
        return toRequest(RequestCall.URL_VIP_CANCEL_AUTO_RENEW, queryString, headMap, null);
    }

    /**
     * 获取活动列表信息
     *
     * @return String
     * @throws Exception 异常
     */
    public String getActicityCenterListRequest() throws Exception {
        String queryString = getQueryString();
        Map<String, String> headMap = getCommonHeader();
        headMap.put(KEY_SIGN_TYPE, HwEncrpt.SIGN_TYPE_2 + "");
        headMap.put(KEY_SIGN, HwEncrpt.hwEncrptSign(queryString, HwEncrpt.SIGN_TYPE_2));
        return toRequest(RequestCall.URL_ACTIVITY_CENTER, queryString, headMap, null);
    }

    /**
     * 获取VIP连续开通历史
     *
     * @param index index
     * @return String
     * @throws Exception 异常
     */
    public String getVipContinueOpenHisRequest(String index) throws Exception {
        Map<String, Object> params = newHashMap();
        params.put("index", index);
        String queryString = getQueryString();
        String postBody = JsonUtils.fromHashMap(params);
        Map<String, String> headMap = getCommonHeader();
        headMap.put(KEY_SIGN_TYPE, HwEncrpt.SIGN_TYPE_2 + "");
        headMap.put(KEY_SIGN, HwEncrpt.hwEncrptSign(queryString + postBody, HwEncrpt.SIGN_TYPE_2));
        return toRequest(RequestCall.URL_CONTINUE_OPEN_HIS, queryString, headMap, postBody);
    }

    /**
     * 暴露给h5方法返回签名及其公共数据
     *
     * @param type type
     * @param data data
     * @return String
     */
    public String getH5AddSignHeaderData(String data, String type) {
        String queryString = getQueryString();
        String sign;
        if (TextUtils.equals(type, HwEncrpt.SIGN_TYPE_1 + "")) {
            sign = HwEncrpt.hwEncrptSign(queryString + data, HwEncrpt.SIGN_TYPE_1);
        } else {
            sign = HwEncrpt.hwEncrptSign(queryString + data, HwEncrpt.SIGN_TYPE_2);
        }
        Map<String, Object> map = newHashMap();
        map.put("queryString", queryString);
        Map<String, String> headMap = getCommonHeader();

        headMap.put(KEY_SIGN_TYPE, type);
        headMap.put(KEY_SIGN, sign);

        map.put("headers", headMap);

        return JsonUtils.fromHashMap(map);
    }

    /**
     * 获取排行榜数据
     *
     * @param parentId parentId
     * @param subId    subId
     * @param page     page
     * @param pageSize pageSize
     * @return String
     * @throws Exception 异常
     */
    public String getBookStoreRankTopData(String parentId, String subId, int page, int pageSize) throws Exception {
        Map<String, Object> params = newHashMap();
        params.put("parentId", parentId);
        params.put("subId", subId);
        params.put("page", page);
        params.put("pageSize", pageSize);
        String queryString = getQueryString();
        String postBody = JsonUtils.fromHashMap(params);
        Map<String, String> headMap = getCommonHeader();
        headMap.put(KEY_SIGN_TYPE, HwEncrpt.SIGN_TYPE_1 + "");
        headMap.put(KEY_SIGN, HwEncrpt.hwEncrptSign(queryString + postBody, HwEncrpt.SIGN_TYPE_1));
        return toRequest(RequestCall.URL_RANK_TOP, queryString, headMap, postBody);
    }

    /**
     * 添加书籍
     *
     * @param commodityId commodityId
     * @param bookIds     bookIds
     * @param originate   originate
     * @return String
     * @throws Exception 异常
     */
    public String addBookByH5(int commodityId, String bookIds, int originate) throws Exception {
        Map<String, Object> params = newHashMap();
        params.put("commodityId", commodityId);
        params.put("bookIds", bookIds);
        params.put("originate", originate);
        String queryString = getQueryString();
        String postBody = JsonUtils.fromHashMap(params);
        Map<String, String> headMap = getCommonHeader();
        headMap.put(KEY_SIGN_TYPE, HwEncrpt.SIGN_TYPE_1 + "");
        headMap.put(KEY_SIGN, HwEncrpt.hwEncrptSign(queryString + postBody, HwEncrpt.SIGN_TYPE_1));
        return toRequest(RequestCall.URL_H5_ADD_SHELF, queryString, headMap, postBody);
    }

    /**
     * push 的Token 上传
     *
     * @param token token
     * @return string
     * @throws Exception 异常
     */
    public String upLoadCid(String token) throws Exception {
        Map<String, Object> params = newHashMap();
        params.put("cid", token);
        String queryString = getQueryString();
        String postBody = JsonUtils.fromHashMap(params);
        Map<String, String> headMap = getCommonHeader();
        headMap.put(KEY_SIGN_TYPE, HwEncrpt.SIGN_TYPE_2 + "");
        headMap.put(KEY_SIGN, HwEncrpt.hwEncrptSign(queryString + postBody, HwEncrpt.SIGN_TYPE_2));
        return toRequest(RequestCall.URL_UPLOAD_CID, queryString, headMap, postBody);
    }

    /**
     * 获取充值列表
     *
     * @return String
     * @throws Exception 异常
     */
    public String getRechargeListRequest() throws Exception {
        String queryString = getQueryString();
        Map<String, String> headMap = getCommonHeader();
        headMap.put(KEY_SIGN_TYPE, HwEncrpt.SIGN_TYPE_1 + "");
        headMap.put(KEY_SIGN, HwEncrpt.hwEncrptSign(queryString, HwEncrpt.SIGN_TYPE_1));
        return toRequest(RequestCall.RECHARGE_LIST, queryString, headMap, null);
    }

    /**
     * 获取我的Vip信息
     *
     * @return String
     * @throws Exception 异常
     */
    public String getVipListRequest() throws Exception {
        String queryString = getQueryString();
        Map<String, String> headMap = getCommonHeader();
        headMap.put(KEY_SIGN_TYPE, HwEncrpt.SIGN_TYPE_1 + "");
        headMap.put(KEY_SIGN, HwEncrpt.hwEncrptSign(queryString, HwEncrpt.SIGN_TYPE_1));
        return toRequest(RequestCall.URL_VIP_INFO, queryString, headMap, null);
    }

    /**
     * 获取我的Vip福利
     *
     * @return String
     * @throws Exception 异常
     */
    public String getVipWellRequest() throws Exception {
        String queryString = getQueryString();
        Map<String, String> headMap = getCommonHeader();
        headMap.put(KEY_SIGN_TYPE, HwEncrpt.SIGN_TYPE_2 + "");
        headMap.put(KEY_SIGN, HwEncrpt.hwEncrptSign(queryString, HwEncrpt.SIGN_TYPE_2));
        return toRequest(RequestCall.URL_VIP_WELL, queryString, headMap, null);
    }


    /**
     * 同步书签
     *
     * @param userId   userId
     * @param time     time
     * @param markList markList
     * @return String
     * @throws Exception 异常
     */
    public String syncMarkRequest(String userId, String time, String markList) throws Exception {
        Map<String, Object> params = newHashMap();
        params.put("userId", userId);
        params.put("time", time);
        params.put("markList", markList);
        String queryString = getQueryString();
        String postBody = JsonUtils.fromHashMap(params);
        Map<String, String> headMap = getCommonHeader();
        headMap.put(KEY_SIGN_TYPE, HwEncrpt.SIGN_TYPE_2 + "");
        headMap.put(KEY_SIGN, HwEncrpt.hwEncrptSign(queryString + postBody, HwEncrpt.SIGN_TYPE_2));
        return toRequest(RequestCall.URL_SYNC_BOOK_MARK, queryString, headMap, postBody);
    }
}
