package com.dzpay.recharge.utils;

import android.text.TextUtils;

import com.dzpay.recharge.bean.RechargeErrType;
import com.dzpay.recharge.bean.RechargeMsgResult;

import java.util.Map;

/**
 * 根据errCode来获取提示语句
 *
 * @author lizhongzhong 2015/8/30.
 * @see {RechargeErrType}
 */
public class RechargeMsgUtils {

    private static final String RECHARGE_FAIL_DES = "充值失败，请稍后再试";
    private static final String VIP_OPEN_FAIL_DES = "VIP订购失败，请稍后再试";
    private static final String ORDER_FAIL = "订购失败，请稍后再试";
    private static final String LOAD_FAIL = "加载失败，请稍后再试";

    /**
     * 获取订购数据
     *
     * @param param 参数
     * @return String
     */
    public static String getRechargeMsg(Map<String, String> param) {

        try {
            String rechargeWay = param.get(RechargeMsgResult.RECHARGE_WAY);
            String errCode = param.get(RechargeMsgResult.ERR_CODE);

            int endCode = Integer.parseInt(errCode.substring(3));
            switch (endCode) {
                case RechargeErrType.VIEW_BACK:
                case RechargeErrType.SYSTEM_BACK:
                    //不提示用户信息
                    return "";
                case RechargeErrType.RETURN_MY_ERROR:
                    return param.get(RechargeMsgResult.ERR_DES);
                case RechargeErrType.ORDER_FAIL:
                    return ORDER_FAIL;
                default:
                    if (!TextUtils.isEmpty(rechargeWay)) {
                        if (RechargeWayUtils.isVipOpenRechargeWay(rechargeWay)) {
                            return VIP_OPEN_FAIL_DES;
                        }
                        return RECHARGE_FAIL_DES;
                    }
                    return LOAD_FAIL;

            }
        } catch (Exception e) {
            PayLog.printStackTrace(e);
        }

        return "";
    }

    /**
     * 是否返回取消
     *
     * @param parm 参数
     * @return boolean
     */
    public static boolean isBackCancel(Map<String, String> parm) {
        String errCode = parm.get(RechargeMsgResult.ERR_CODE);
        try {
            int endCode = Integer.parseInt(errCode.substring(3));

            switch (endCode) {
                case RechargeErrType.VIEW_BACK:
                case RechargeErrType.SYSTEM_BACK:
                    return true;
                default:
                    return false;
            }
        } catch (Exception e) {
            PayLog.printStackTrace(e);
        }
        return false;
    }

    /**
     * 是否需要登录
     *
     * @param params 参数
     * @return boolean
     */
    public static boolean isNeedHwLogin(Map<String, String> params) {
        return params.containsKey(RechargeMsgResult.APP_NEED_LOGIN_OR_TOKEN_INVALID) && TextUtils.equals(params.get(RechargeMsgResult.APP_NEED_LOGIN_OR_TOKEN_INVALID), "1");
    }
}
