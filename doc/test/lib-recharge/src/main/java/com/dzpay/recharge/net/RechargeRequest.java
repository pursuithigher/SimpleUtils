package com.dzpay.recharge.net;

import android.text.TextUtils;

import com.dzbook.lib.net.OkhttpUtils;
import com.dzbook.lib.utils.JsonUtils;
import com.dzbook.lib.utils.StringUtil;
import com.dzbook.lib.utils.UtilTimeOffset;
import com.dzpay.recharge.utils.PayLog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import hw.sdk.utils.HwEncrpt;


/**
 * 网络请求封装类
 *
 * @author lizhongzhong 2013-11-23
 */
public class RechargeRequest {
    /**
     * key  签名类型
     */
    private static final String KEY_SIGN_TYPE = "signType";


    private static final String KEY_SIGN = "sign";

    /**
     * 构造
     */
    public RechargeRequest() {
    }

    /**
     * 拼接公共请求参数
     *
     * @return
     */
    private String requestAppendParams(String urlBase) {

        String mUrlBase = StringUtil.putUrlValue(urlBase, "appId", NetCommonParamUtils.getAppId());
        mUrlBase = StringUtil.putUrlValue(mUrlBase, "country", NetCommonParamUtils.getCountry());
        mUrlBase = StringUtil.putUrlValue(mUrlBase, "lang", NetCommonParamUtils.getLang());
        mUrlBase = StringUtil.putUrlValue(mUrlBase, "ver", NetCommonParamUtils.getVer());
        mUrlBase = StringUtil.putUrlValue(mUrlBase, "appVer", NetCommonParamUtils.getAppVer());
        mUrlBase = StringUtil.putUrlValue(mUrlBase, "timestamp", UtilTimeOffset.getFormatDateByTimeZone());

        return mUrlBase;
    }

    private Map<String, String> getCommonHeader() {

        Map<String, String> map = new HashMap<String, String>(16);

        map.put("Content-Type", "application/json; charset=utf-8");
        map.put("Accept", "application/json");
        map.put("utdid", NetCommonParamUtils.getUtdid());
        map.put("pname", NetCommonParamUtils.getPname());
        map.put("channelCode", NetCommonParamUtils.getChannelCode());

        String token = NetCommonParamUtils.getAppToken();
        if (!TextUtils.isEmpty(token)) {
            map.put("t", token);
        }
        PayLog.d("appToken:" + token);

        String userId = NetCommonParamUtils.getUserId();
        if (!TextUtils.isEmpty(userId)) {
            map.put("uid", userId);
        }

        return map;
    }

    /**
     * 下订单接口
     * 操作类 使用 SHA256WithRSA签名
     *
     * @param amountId id
     * @return String
     * @throws Exception 异常
     */
    public String makeOrdersRquest(String amountId) throws Exception {

        String urlBase = requestAppendParams(NetCommonParamUtils.getServiceUrl() + RechargeNetCall.MAKE_ORDERS);

        Map<String, Object> params = new HashMap<>(16);
        params.put("id", amountId);

        String queryString = StringUtil.getUrlLastParamsString(urlBase);
        String postBody = JsonUtils.fromHashMap(params);

        Map<String, String> headMap = getCommonHeader();
        headMap.put(KEY_SIGN_TYPE, HwEncrpt.SIGN_TYPE_2 + "");
        headMap.put(KEY_SIGN, HwEncrpt.hwEncrptSign(queryString + postBody, HwEncrpt.SIGN_TYPE_2));

        return okHttpRequest(urlBase, headMap, postBody);
    }


    /**
     * 订单通知
     * 操作类 使用 SHA256WithRSA签名
     * 1-充值成功；2-充值失败
     *
     * @param orderNo 订单号
     * @param result  数据
     * @param desc    描述
     * @return String
     * @throws Exception 异常
     */
    public String ordersNotify(String orderNo, String result, String desc) throws Exception {

        String urlBase = requestAppendParams(NetCommonParamUtils.getServiceUrl() + RechargeNetCall.ORDERS_NOTIFY);

        Map<String, Object> params = new HashMap<>(16);
        params.put("orderNo", orderNo);
        params.put("status", result);
        params.put("desc", desc);

        String queryString = StringUtil.getUrlLastParamsString(urlBase);
        String postBody = JsonUtils.fromHashMap(params);

        Map<String, String> headMap = getCommonHeader();
        headMap.put(KEY_SIGN_TYPE, HwEncrpt.SIGN_TYPE_2 + "");
        headMap.put(KEY_SIGN, HwEncrpt.hwEncrptSign(queryString + postBody, HwEncrpt.SIGN_TYPE_2));

        return okHttpRequest(urlBase, headMap, postBody);
    }


    /**
     * 单章订购扣费-单章订购提示页面接口
     *
     * @param bookId     书籍id
     * @param chapterId  章节id
     * @param autoPay    自动支付
     * @param confirmPay 确认支付
     * @return String
     * @throws Exception 异常
     */
    public String singleOrderOrSingleOrderPageRequest(String bookId, String chapterId, String autoPay, String confirmPay) throws Exception {

        String urlBase = requestAppendParams(NetCommonParamUtils.getServiceUrl() + RechargeNetCall.SINGLE_ORDER);

        Map<String, Object> params = new HashMap<>(16);
        params.put("bookId", bookId);
        params.put("chapterId", chapterId);
        params.put("autoPay", autoPay);
        params.put("confirmPay", confirmPay);

        String queryString = StringUtil.getUrlLastParamsString(urlBase);
        String postBody = JsonUtils.fromHashMap(params);

        Map<String, String> headMap = getCommonHeader();
        headMap.put(KEY_SIGN_TYPE, HwEncrpt.SIGN_TYPE_2 + "");
        headMap.put(KEY_SIGN, HwEncrpt.hwEncrptSign(queryString + postBody, HwEncrpt.SIGN_TYPE_2));

        return okHttpRequest(urlBase, headMap, postBody);
    }

    /**
     * 订购-批量订购
     *
     * @param bookId       书籍id
     * @param chapterId    章节id
     * @param num          数量
     * @param discountRate ？？
     * @return String
     * @throws Exception 异常
     */
    public String lotChapterOrderRequest(String bookId, String chapterId, String num, String discountRate) throws Exception {

        String urlBase = requestAppendParams(NetCommonParamUtils.getServiceUrl() + RechargeNetCall.LOT_ORDER_PAY);

        Map<String, Object> params = new HashMap<>(16);
        params.put("bookId", bookId);
        params.put("indexChapterId", chapterId);
        params.put("num", num);
        params.put("discountRate", discountRate);

        String queryString = StringUtil.getUrlLastParamsString(urlBase);
        String postBody = JsonUtils.fromHashMap(params);

        Map<String, String> headMap = getCommonHeader();
        headMap.put(KEY_SIGN_TYPE, HwEncrpt.SIGN_TYPE_2 + "");
        headMap.put(KEY_SIGN, HwEncrpt.hwEncrptSign(queryString + postBody, HwEncrpt.SIGN_TYPE_2));

        return okHttpRequest(urlBase, headMap, postBody);
    }

    /**
     * 批量订购提示页面接口
     *
     * @param bookId         书籍id
     * @param indexChapterId 在读章节之后的第一个未下载章节
     * @return String
     * @throws Exception 异常
     */
    public String getLotOrderPageRequest(String bookId, String indexChapterId) throws Exception {

        String urlBase = requestAppendParams(NetCommonParamUtils.getServiceUrl() + RechargeNetCall.LOT_ORDER_PAGE);

        Map<String, Object> params = new HashMap<>(16);
        params.put("bookId", bookId);
        params.put("indexChapterId", indexChapterId);

        String queryString = StringUtil.getUrlLastParamsString(urlBase);
        String postBody = JsonUtils.fromHashMap(params);

        Map<String, String> headMap = getCommonHeader();
        headMap.put(KEY_SIGN_TYPE, HwEncrpt.SIGN_TYPE_2 + "");
        headMap.put(KEY_SIGN, HwEncrpt.hwEncrptSign(queryString + postBody, HwEncrpt.SIGN_TYPE_2));

        return okHttpRequest(urlBase, headMap, postBody);
    }


    /**
     * 打包订购 一键购 组合购
     *
     * @param buyType     购买类型
     * @param commodityId 商品id
     * @param originate    来源
     * @param bookIds      书籍ids
     * @return String
     * @throws Exception 异常
     */
    public String getPackOrder(String buyType, int commodityId, int originate, ArrayList<String> bookIds) throws Exception {
        String urlBase = requestAppendParams(NetCommonParamUtils.getServiceUrl() + RechargeNetCall.BUY_PACK_BOOK);
        Map<String, Object> params = new HashMap<>(16);
        params.put("commodityId", commodityId);
        params.put("originate", originate);
        params.put("bookIds", bookIds);
        params.put("buyType", buyType);
        String queryString = StringUtil.getUrlLastParamsString(urlBase);
        String postBody = JsonUtils.fromHashMap(params);

        Map<String, String> headMap = getCommonHeader();
        headMap.put(KEY_SIGN_TYPE, HwEncrpt.SIGN_TYPE_2 + "");
        headMap.put(KEY_SIGN, HwEncrpt.hwEncrptSign(queryString + postBody, HwEncrpt.SIGN_TYPE_2));
        return okHttpRequest(urlBase, headMap, postBody);
    }


    /**
     * Vip下订单接口
     * 操作类 使用 SHA256WithRSA签名
     *
     * @param amountId id
     * @return String
     * @throws Exception 异常
     */
    public String vipMakeOrdersRequest(String amountId) throws Exception {

        String urlBase = requestAppendParams(NetCommonParamUtils.getServiceUrl() + RechargeNetCall.VIP_MAKE_ORDERS);

        Map<String, Object> params = new HashMap<>(16);
        params.put("id", amountId);

        String queryString = StringUtil.getUrlLastParamsString(urlBase);
        String postBody = JsonUtils.fromHashMap(params);

        Map<String, String> headMap = getCommonHeader();
        headMap.put(KEY_SIGN_TYPE, HwEncrpt.SIGN_TYPE_2 + "");
        headMap.put(KEY_SIGN, HwEncrpt.hwEncrptSign(queryString + postBody, HwEncrpt.SIGN_TYPE_2));

        return okHttpRequest(urlBase, headMap, postBody);
    }

    /**
     * Vip订单通知
     * 操作类 使用 SHA256WithRSA签名
     * 1-充值成功；2-充值失败
     *
     * @param orderNo     订单号
     * @param result      数据
     * @param desc        描述
     * @param isAutoOpend 描是否开启自动
     * @return String
     * @throws Exception 异常
     */
    public String vipOrdersNotify(String orderNo, String result, String desc, int isAutoOpend) throws Exception {

        String urlBase = requestAppendParams(NetCommonParamUtils.getServiceUrl() + RechargeNetCall.VIP_ORDERS_NOTIFY);

        Map<String, Object> params = new HashMap<>(16);
        params.put("orderNo", orderNo);
        params.put("status", result);
        params.put("desc", desc);
        params.put("isAutoOpend", isAutoOpend);

        String queryString = StringUtil.getUrlLastParamsString(urlBase);
        String postBody = JsonUtils.fromHashMap(params);

        Map<String, String> headMap = getCommonHeader();
        headMap.put(KEY_SIGN_TYPE, HwEncrpt.SIGN_TYPE_2 + "");
        headMap.put(KEY_SIGN, HwEncrpt.hwEncrptSign(queryString + postBody, HwEncrpt.SIGN_TYPE_2));

        return okHttpRequest(urlBase, headMap, postBody);
    }

    private String okHttpRequest(String urlBase, Map<String, String> headMap, String postBody) throws Exception {

        PayLog.d("okHttpRequest urlBase:" + urlBase);
        String response = OkhttpUtils.getInstance().okHttpRequest(urlBase, headMap, postBody);
        PayLog.d("response：" + response);

        return response;
    }
}
