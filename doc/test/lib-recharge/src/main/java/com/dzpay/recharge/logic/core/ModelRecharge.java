package com.dzpay.recharge.logic.core;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;

import com.dzbook.lib.utils.ALog;
import com.dzpay.recharge.bean.RechargeErrType;
import com.dzpay.recharge.bean.RechargeMsgResult;
import com.dzpay.recharge.bean.RechargeObserverConstants;
import com.dzpay.recharge.logic.Observer;
import com.dzpay.recharge.netbean.OrdersHwBeanInfo;
import com.dzpay.recharge.netbean.OrdersNotifyBeanInfo;
import com.dzpay.recharge.netbean.PublicResBean;
import com.dzpay.recharge.netbean.VipOrdersNotifyBeanInfo;
import com.dzpay.recharge.sdk.AbsSdkPay;
import com.dzpay.recharge.sdk.SdkHuaWeiPay;
import com.dzpay.recharge.sdk.SdkPayListener;
import com.dzpay.recharge.sdk.SdkVipOpenHuaWeiPay;
import com.dzpay.recharge.utils.PayLog;
import com.dzpay.recharge.utils.RechargeWayUtils;

import java.util.HashMap;

/**
 * 处理充值下订单，订单通知的统一实现。
 *
 * @author lizz 2018-04-15
 */
public class ModelRecharge {


    /**
     * 支付完成
     */
    private static final int PAY_SUCCESS = 0x00;

    /**
     * 支付失败
     */
    private static final int PAY_FAIL = PAY_SUCCESS + 1;

    /**
     * 短信支付成功
     */
    private static final int SMS_PAY_RESULT = PAY_FAIL + 1;

    /**
     * 改变dialog样式
     */
    private static final int STATUS_CHANGE = SMS_PAY_RESULT + 1;

    /**
     * 充值状态变化
     */
    private static final int RECHARGE_STATUS = STATUS_CHANGE + 1;

    /**
     * 观察者
     */
    public Observer observer;
    private HashMap<String, String> params;

    private AbsSdkPay sdkPay = null;

    private String rechargeWay;

    private Context context;

    private ContextListener listener;

    /**
     * 监听
     */
    public interface ContextListener {
        /**
         * 控制充值activity销毁
         */
        void onContextFinish();
    }

    /**
     * 构造
     *
     * @param context  上下文
     * @param observer 观察者
     * @param params   参数
     * @param listener 监听
     */
    public ModelRecharge(Context context, Observer observer, HashMap<String, String> params, ContextListener listener) {
        this.context = context;
        this.params = params;
        this.listener = listener;
        this.observer = observer;
    }

    private void handleResult(int what, Object obj) {
        handleResult(what, obj, false);
    }

    private void handleResult(int what, Object obj, boolean isVipOpen) {
        RechargeMsgResult msgResult = new RechargeMsgResult(params);
        switch (what) {
            case PAY_SUCCESS: {
                paySuccessCallBack(msgResult, obj, isVipOpen);

                finishContext();
                break;
            }
            case PAY_FAIL: {
                payFaiCallBack(msgResult, obj, isVipOpen);
                finishContext();
                break;
            }
            case STATUS_CHANGE: {
                if (null != observer) {
                    msgResult.what = RechargeObserverConstants.STATUS_CHANGE;
                    observer.update(msgResult);
                }
                break;
            }
            case RECHARGE_STATUS: {
                rechargeStatusChange(msgResult, obj);
                break;
            }
            default:
                break;
        }
    }

    /**
     * 初始化
     */
    public void initData() {
        if (params == null) {
            PublicResBean bean = new PublicResBean();
            bean.error(RechargeErrType.RECHARGE_DATA_ERROR, "充值参数为空");
            handleResult(PAY_FAIL, bean);
            return;
        }
        //充值方式
        rechargeWay = params.get(RechargeMsgResult.RECHARGE_WAY);
        final String rechargeMoneyId = params.get(RechargeMsgResult.RECHARGE_MONEY_ID);
        try {
            switch (RechargeWayUtils.getInt(rechargeWay)) {
                case RechargeWayUtils.VIP_OPEN_HW_PAY:

                    sdkPay = new SdkVipOpenHuaWeiPay((Activity) context, rechargeWay, params, new SdkPayListener() {

                        @Override
                        public boolean onResult(final PublicResBean result) {
                            return onSdkResult(result, true);
                        }

                        @Override
                        public void stautsChange(int status, String message) {
                            params.put(RechargeMsgResult.STATUS_CHANGE, "" + status);
                            params.put(RechargeMsgResult.STATUS_CHANGE_MSG, message);
                            handleResult(STATUS_CHANGE, null, true);
                        }
                    });
                    sdkPay.orderEntry(rechargeMoneyId, observer);

                    break;
                default:
                    sdkPay = new SdkHuaWeiPay((Activity) context, rechargeWay, params, new SdkPayListener() {

                        @Override
                        public boolean onResult(final PublicResBean result) {
                            return onSdkResult(result, false);
                        }

                        @Override
                        public void stautsChange(int status, String message) {
                            params.put(RechargeMsgResult.STATUS_CHANGE, "" + status);
                            params.put(RechargeMsgResult.STATUS_CHANGE_MSG, message);
                            handleResult(STATUS_CHANGE, null);
                        }

                        @Override
                        public void rechargeStatus(int status, PublicResBean result) {
                            params.put(RechargeMsgResult.RECHARGE_STATUS, "" + status);
                            handleResult(RECHARGE_STATUS, result);
                        }

                    });
                    sdkPay.orderEntry(rechargeMoneyId, observer);
                    break;
            }

        } catch (Exception e) {
            PayLog.printStackTrace(e);
            handleResult(PAY_FAIL, new PublicResBean().error(RechargeErrType.RECHARGE_EXCEPTION, "充值过程出现异常"));
        }

    }

    /**
     * 回收调用
     */
    public void orderDestroy() {
        if (null != sdkPay) {
            sdkPay.orderDestroy();
            sdkPay = null;
        }
    }

    /**
     * finish 显式订购的 activity
     */
    private void finishContext() {
        if (null != listener) {
            listener.onContextFinish();
        }
    }

    /**
     * SDK 的 onResult 公共处理。
     *
     * @param result
     * @return
     */
    private boolean onSdkResult(final PublicResBean result, final boolean isVipOpen) {
        if (null != result) {
            if (RechargeErrType.SUCCESS == result.errorType) {
                onSdkResultSuccess(result, isVipOpen);
            } else {
                handleResult(PAY_FAIL, result, isVipOpen);
            }
        } else {
            PublicResBean bean = new PublicResBean();
            bean.error(RechargeErrType.RECHARGE_DATA_ERROR, "充值失败");
            handleResult(PAY_FAIL, bean, isVipOpen);
        }
        return true;
    }

    /**
     * 得到发起支付的抽象类
     *
     * @return AbsSdkPay
     */
    public AbsSdkPay getSdkPay() {
        return sdkPay;
    }

    /**
     * 支付成功处理
     *
     * @param msgResult 数据
     * @param obj       VipOrdersNotifyBeanInfo
     * @param isVipOpen 是否开vip
     */
    public void paySuccessCallBack(RechargeMsgResult msgResult, Object obj, boolean isVipOpen) {
        String errorDesc = "";

        if (null != observer) {

            if (isVipOpen) {

                if (null != obj && obj instanceof VipOrdersNotifyBeanInfo) {
                    VipOrdersNotifyBeanInfo vipOrdersNotify = (VipOrdersNotifyBeanInfo) obj;
                    if (vipOrdersNotify.resultBean != null) {
                        msgResult.map.put(RechargeMsgResult.VIP_PAY_RESULT_JSON, vipOrdersNotify.resultBean.json);
                    }
                    errorDesc = vipOrdersNotify.errorDesc;
                }

            } else {
                if (null != obj && obj instanceof OrdersNotifyBeanInfo) {
                    OrdersNotifyBeanInfo notifyBeanInfo = (OrdersNotifyBeanInfo) obj;
                    if (notifyBeanInfo.resultBeanInfo != null) {
                        msgResult.map.put(RechargeMsgResult.RECHARGE_RESULT_JSON, notifyBeanInfo.resultBeanInfo.json);
                    }
                    errorDesc = notifyBeanInfo.errorDesc;
                }
            }

            if (!TextUtils.isEmpty(msgResult.map.get(RechargeMsgResult.MORE_DESC))) {
                errorDesc += "_" + msgResult.map.get(RechargeMsgResult.MORE_DESC);
            }

            if (!TextUtils.isEmpty(errorDesc)) {
                msgResult.map.put(RechargeMsgResult.MORE_DESC, errorDesc);
            }

            msgResult.what = RechargeObserverConstants.SUCCESS;
            observer.update(msgResult);
        }
    }

    /**
     * 支付失败处理
     *
     * @param msgResult 数据
     * @param obj       VipOrdersNotifyBeanInfo
     * @param isVipOpen 是否开vip
     */
    public void payFaiCallBack(RechargeMsgResult msgResult, Object obj, boolean isVipOpen) {
        String errorDesc = "";
        String tips = null;
        ALog.fileLog("     ModelRecharge的payFaiCallBack方法：" + (null != observer ? " observer !=null" : " observer=null"));
        if (null != observer) {
            int errorType = RechargeErrType.RECHARGE_DATA_ERROR;
            msgResult.what = RechargeObserverConstants.FAIL;
            msgResult.errType.setErrCode(observer.getAction().actionCode(), errorType, tips);

            if (!TextUtils.isEmpty(msgResult.map.get(RechargeMsgResult.MORE_DESC))) {
                errorDesc += "_" + msgResult.map.get(RechargeMsgResult.MORE_DESC);
            }
            if (!TextUtils.isEmpty(errorDesc)) {
                msgResult.map.put(RechargeMsgResult.MORE_DESC, errorDesc);
            }
            observer.update(msgResult);
        }
    }

    /**
     * 充值状态改变
     *
     * @param msgResult
     * @param obj
     */
    private void rechargeStatusChange(RechargeMsgResult msgResult, Object obj) {
        if (null != observer) {
            if (obj != null && obj instanceof OrdersHwBeanInfo) {
                String orderNum = ((OrdersHwBeanInfo) obj).orderNo;
                if (!TextUtils.isEmpty(orderNum)) {
                    msgResult.map.put(RechargeMsgResult.RECHARGE_ORDER_NUM, orderNum);
                }
            }
            msgResult.what = RechargeObserverConstants.RECHARGE_STATUS_CHANGE;
            observer.update(msgResult);
        }
    }

    /**
     * 处理响应成功结果
     *
     * @param result
     * @param isVipOpen
     */
    private void onSdkResultSuccess(final PublicResBean result, final boolean isVipOpen) {

        if (isVipOpen && (result instanceof VipOrdersNotifyBeanInfo)) {
            VipOrdersNotifyBeanInfo bean = (VipOrdersNotifyBeanInfo) result;
            if (bean.isRechargeSuccess()) {
                handleResult(PAY_SUCCESS, bean, isVipOpen);
                return;
            }

        } else if (result instanceof OrdersNotifyBeanInfo) {
            OrdersNotifyBeanInfo bean = (OrdersNotifyBeanInfo) result;
            if (bean.isRechargeSuccess()) {
                handleResult(PAY_SUCCESS, bean, isVipOpen);
                return;
            }
        }
        handleResult(PAY_FAIL, result, isVipOpen);
    }
}
