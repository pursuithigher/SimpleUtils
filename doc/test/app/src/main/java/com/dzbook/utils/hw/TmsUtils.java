package com.dzbook.utils.hw;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;

import com.dzbook.AppConst;
import com.dzbook.event.EventBusUtils;
import com.dzbook.event.EventConstant;
import com.dzbook.lib.utils.ALog;
import com.dzbook.net.hw.HwRequest;
import com.dzbook.net.hw.HwRequestLib;
import com.dzbook.utils.NetworkUtils;
import com.dzbook.utils.SpUtil;

import org.json.JSONArray;
import org.json.JSONObject;

import hw.sdk.net.bean.tms.BeanTmsRespQuery;
import hw.sdk.net.bean.tms.BeanTmsRespSign;

/**
 * TmsUtils
 *
 * @author winzows  2018/4/13
 */

public class TmsUtils {
    private static final String AGR_TYPE = "agrType";
    private static final String COUNTRY = "country";
    private static final String LANGUAGE = "language";
    private static final int AGREE_NUM_1 = 131;
    private static final int AGREE_NUM_2 = 10019;
    private static final String SP_SIGN_UPLOAD = "isSignUpload";

    private static volatile TmsUtils instance;


    /**
     * 避免频繁请求 一次应用启动 只请求一次
     */
    private boolean isQuery = false;
    private int retryIndex = 0;
    private final int maxRetry = 1;
    private String tempUid;
    private long lastQueryTime = 0;
    private int replyCount = 0;


    /**
     * 获取TmsUtils实例
     *
     * @return 实例
     */
    public static TmsUtils getInstance() {
        if (instance == null) {
            synchronized (TmsUtils.class) {
                if (instance == null) {
                    instance = new TmsUtils();
                }
            }
        }
        return instance;
    }


    /**
     * signAgreement
     *
     * @param context     context
     * @param accessToken accessToken
     * @param uid         uid
     * @return boolean
     */
    public boolean signAgreement(Context context, String accessToken, String uid) {
        if (TextUtils.isEmpty(accessToken) || TextUtils.isEmpty(uid)) {
            ALog.dWz("token is empty1 uid =" + uid);
            return false;
        }
        try {
            BeanTmsRespSign beanTmsRespSign = HwRequestLib.getInstance().signAgreement(getSignAgrInfoJson(context), accessToken);
            /**
             * 记录上传成功
             */
            if (beanTmsRespSign.isSuccess()) {
                SpUtil.getinstance(context).setBoolean(SP_SIGN_UPLOAD, true);
                return true;
            } else {
                if (replyCount > 1) {
                    return false;
                }
                replyCount++;
                signAgreement(context, accessToken, uid);
            }
        } catch (Exception e) {
            e.printStackTrace();
            ALog.printExceptionWz(e);
        }
        return false;
    }

    /**
     * queryAgreement
     *
     * @param context     context
     * @param accessToken accessToken
     * @param thisUid     thisUid
     */
    public void queryAgreement(final Context context, final String accessToken, final String thisUid) {
        long thisQueryTime = System.currentTimeMillis();
        final long thoundsNum = 1000L;
        if (thisQueryTime - lastQueryTime < thoundsNum) {
            return;
        }
        lastQueryTime = thisQueryTime;
        try {

            if (!NetworkUtils.getInstance().checkNet()) {
                return;
            }

            if (TextUtils.isEmpty(accessToken) || TextUtils.isEmpty(thisUid)) {
                ALog.dWz("token is empty uid =" + thisUid);
                return;
            }

            if (TextUtils.isEmpty(tempUid) || TextUtils.equals(tempUid, thisUid)) {
                //判断这个用户 之前有没有签署过 没有的话 需要重新签署。
                boolean isSignUpload = SpUtil.getinstance(context).getBoolean(SP_SIGN_UPLOAD, false);
                if (!isSignUpload) {
                    //签署成功的话 直接返回 失败了的话 走查询流程
                    if (signAgreement(context, accessToken, thisUid)) {
                        tempUid = thisUid;
                        return;
                    }
                }
            } else {
                isQuery = false;
                retryIndex = 0;
                replyCount = 0;
            }

            if (isQuery || retryIndex > maxRetry) {
                ALog.dWz(" is query");
                return;
            }

            tempUid = thisUid;

            BeanTmsRespQuery beanTmsRespQuery = HwRequestLib.getInstance().queryAgreement(getQueryAgrInfoJson(context), accessToken);
            //如果查询到需要重新签署  就弹出来。
            //这里 如果是接口访问失败的 是不会弹出的。
            if (beanTmsRespQuery.needReSign()) {
                ALog.dWz("queryAgreement ---> needReSign ");
                Bundle bundle = new Bundle();
                bundle.putString("thisUid", thisUid);
                bundle.putString("accessToken", accessToken);
                //给Main2Activity发事件  弹出dialog
                EventBusUtils.sendStickyMessage(EventConstant.CODE_SHOW_TMS_DIALOG, EventConstant.TYPE_MAIN2ACTIVITY, bundle);
            } else if (beanTmsRespQuery.isSuccess()) {
                isQuery = true;
            } else {
                retryIndex++;
            }
        } catch (Exception e) {
            ALog.printExceptionWz(e);
        }
    }


    private String getQueryAgrInfoJson(Context context) {
        JSONObject requestObj = new JSONObject();
        try {
            HwRequest hwRequest = HwRequestLib.getInstance().getmRequest();
            JSONArray agrInfoList = new JSONArray();
            JSONObject agrInfoObj = new JSONObject();
            agrInfoObj.put(AGR_TYPE, AGREE_NUM_1);
            agrInfoObj.put(COUNTRY, hwRequest.getCOUNTRY());
            agrInfoList.put(agrInfoObj);

            JSONObject agrInfoObj2 = new JSONObject();
            agrInfoObj2.put(AGR_TYPE, AGREE_NUM_2);
            agrInfoObj2.put(COUNTRY, hwRequest.getCOUNTRY());
            agrInfoList.put(agrInfoObj2);
            requestObj.put("agrInfo", agrInfoList);
            requestObj.put("obtainVersion", false);
        } catch (Exception e) {
            ALog.printExceptionWz(e);
        }
        return requestObj.toString();
    }

    private String getSignAgrInfoJson(Context context) {
        JSONObject requestObj = new JSONObject();
        try {

            HwRequest hwRequest = HwRequestLib.getInstance().getmRequest();

            JSONArray agrInfoList = new JSONArray();
            JSONObject agrInfoObj = new JSONObject();
            agrInfoObj.put(AGR_TYPE, AGREE_NUM_1);
            agrInfoObj.put(COUNTRY, hwRequest.getCOUNTRY());
            agrInfoObj.put(LANGUAGE, hwRequest.getLang());
            agrInfoObj.put("isAgree", true);
            agrInfoList.put(agrInfoObj);

            JSONObject agrInfoObj2 = new JSONObject();
            agrInfoObj2.put(AGR_TYPE, AGREE_NUM_2);
            agrInfoObj2.put(COUNTRY, hwRequest.getCOUNTRY());
            agrInfoObj2.put(LANGUAGE, hwRequest.getLang());
            agrInfoObj2.put("isAgree", true);
            agrInfoList.put(agrInfoObj2);
            requestObj.put("signInfo", agrInfoList);
        } catch (Exception e) {
            ALog.printExceptionWz(e);
        }
        return requestObj.toString();
    }

    public void reset() {
        tempUid = null;
        SpUtil.getinstance(AppConst.getApp()).setBoolean(SP_SIGN_UPLOAD, false);
    }
}
