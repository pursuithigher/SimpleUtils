package com.dzpay.recharge.bean;

import android.text.TextUtils;

import java.io.Serializable;
import java.text.DecimalFormat;

/**
 * 定义所有的错误类型
 *
 * @author huangyoubin
 */
public class RechargeErrType implements Serializable {


    /**
     * 公共错误代码
     */
    /**
     * 成功
     */
    public static final int SUCCESS = 0;
    /**
     * 界面返回键取消
     */
    public static final int VIEW_BACK = 1;
    /**
     * 系统返回键取消
     */
    public static final int SYSTEM_BACK = 2;
    /**
     * 失败
     */
    public static final int FAIL = 10;

    /**
     * json无法解析
     */
    public static final int JSON_CANNOT_RESOLVE = 12;
    /**
     * 订购，服务器处理异常
     */
    public static final int DATA_ERROR = 13;
    /**
     * 联网失败
     */
    public static final int NETWORK_FAIL = 14;

    /**
     * 没有网络连接
     */
    public static final int NO_NETWORK_CONNECTION = 16;
    /**
     * 订单支付失败
     */
    public static final int ORDER_PAY_FAIL = 18;
    /**
     * context不可转换成activity,进入不了充值流程
     */
    public static final int CONTEXT_REVERT_ERROR = 19;
    /**
     * 订单请求失败
     */
    public static final int MAKE_ORDER_ERROR = 20;

    /**
     * 用于返回自己组织的提示错误代码
     */
    public static final int RETURN_MY_ERROR = 22;
    /**
     * 充值，服务器处理异常
     */
    public static final int RECHARGE_DATA_ERROR = 23;

    /**
     * 充值出现异常
     */
    public static final int RECHARGE_EXCEPTION = 24;

    /**
     * 订购失败
     */
    public static final int ORDER_FAIL = 25;

    /**
     * VIP自动订购已开通
     */
    public static final int VIP_AUTO_ORDER_OPENED = 26;

    /**
     * 线程错误
     */
    public static final int THREAD_ERROR = 27;
    /**
     * 参数为空
     */
    public static final int PARAM_ERROR = 28;
    /**
     * 解析请求结果异常
     */
    public static final int FAIL_REQUEST = 29;
    private static final long serialVersionUID = 6984825252237313450L;
    /**
     * 错误码
     */
    private String errorCode;
    /**
     * 错误描述
     */
    private String errorDes;

    /**
     * 构造
     */
    public RechargeErrType() {
        errorCode = "";
        errorDes = "未知错误";
    }

    /**
     * 货物错误码
     *
     * @return String
     */
    public String getErrCode() {
        return errorCode;
    }

    /**
     * 设置错误码
     *
     * @param action  操作码
     * @param errCode 错误码
     */
    public void setErrCode(RechargeAction action, int errCode) {
        setErrDes(errCode);
        if (null != action) {
            this.errorCode = "a" + action.actionCode() + errorCodeFormat(errCode);
        } else {
            this.errorCode = "a00" + errorCodeFormat(errCode);
        }
    }

    /**
     * 设置错误码
     *
     * @param actionCode 操作码
     * @param errCode    错误码
     */
    public void setErrCode(int actionCode, int errCode) {
        setErrDes(errCode);
        this.errorCode = "a" + actionCode + errorCodeFormat(errCode);
    }

    public String getErrDes() {
        return errorDes;
    }

    /**
     * 如果需要设置服务器返回的errDes,
     * 必须返回错误码为:RETURN_MY_ERROR
     *
     * @param actionCode 操作码
     * @param errCode    错误码
     * @param errDes     错误描述
     */
    public void setErrCode(int actionCode, int errCode, String errDes) {
        if (!TextUtils.isEmpty(errDes)) {
            this.errorDes = errDes;
            this.errorCode = "a" + actionCode + errorCodeFormat(errCode);
        } else {
            setErrCode(actionCode, errCode);
        }

    }

    /**
     * 根据错误码设置
     *
     * @param errCode 错误码
     */
    private void setErrDes(int errCode) {
        String des;
        switch (errCode) {
            case SUCCESS:
                des = "成功";
                break;
            case VIEW_BACK:
                des = "界面返回键取消";
                break;
            case SYSTEM_BACK:
                des = "系统返回键取消";
                break;
            case NO_NETWORK_CONNECTION:
                des = "网络未连接，请稍后重试";
                break;
            case NETWORK_FAIL:
                des = "联网失败,请稍后再试";
                break;
            case MAKE_ORDER_ERROR:
                des = "支付订单信息请求失败";
                break;
            default:
                des = "充值失败，请稍微再试";
        }
        this.errorDes = des;
    }

    /**
     * 格式化错误码
     *
     * @param code 错误码
     * @return String
     */

    public static String errorCodeFormat(int code) {
        DecimalFormat df = new DecimalFormat("00");
        return df.format(code);
    }


}
