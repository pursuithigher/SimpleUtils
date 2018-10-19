package com.dzpay.recharge.logic.core;

import android.content.Context;
import android.text.TextUtils;

import com.dzpay.recharge.bean.RechargeAction;
import com.dzpay.recharge.bean.RechargeErrType;
import com.dzpay.recharge.bean.RechargeMsgResult;
import com.dzpay.recharge.bean.RechargeObserverConstants;
import com.dzpay.recharge.logic.DZReadAbstract;
import com.dzpay.recharge.net.RechargeLibUtils;
import com.dzpay.recharge.netbean.LotPayOrderBeanInfo;
import com.dzpay.recharge.netbean.PayOrderChapterBeanInfo;
import com.dzpay.recharge.netbean.SingleOrderBeanInfo;
import com.dzpay.recharge.utils.PayLog;
import com.dzpay.recharge.utils.SystemUtils;

import java.util.ArrayList;
import java.util.HashMap;

import hw.sdk.net.bean.HwPublicBean;

/**
 * 漫画的支付接口
 *
 * @author lizhongzhong 2017/8/9.
 */

public class PayChapterImpl extends DZReadAbstract {
    private static final String LOT_DOWNLOAD_ACTION = "4";

    String autoPay;

    String confirmPay;


    /**
     * 构造
     *
     * @param context 上下文
     * @param param   参数
     * @param action  操作
     */
    public PayChapterImpl(Context context, HashMap<String, String> param, RechargeAction action) {
        super(context, param, action);
        if (param != null) {

            if (param.containsKey(RechargeMsgResult.AUTO_PAY)) {
                autoPay = param.get(RechargeMsgResult.AUTO_PAY);
            }

            if (param.containsKey(RechargeMsgResult.CONFIRM_PAY)) {
                confirmPay = param.get(RechargeMsgResult.CONFIRM_PAY);
            }
        }
    }

    @Override
    public void execute() {
        RechargeMsgResult result = new RechargeMsgResult(param);

        if (!SystemUtils.isNetworkConnected(context)) {
            requestDataObserver(result, null, RechargeErrType.NO_NETWORK_CONNECTION, "");
            return;
        }

        try {
            PayLog.d("发起扣费请求|bookId:" + bookId + "|chapters:" + baseChapterId + "|readAction:" + readAction);

            if (TextUtils.equals(readAction, LOT_DOWNLOAD_ACTION)) {
                lotPay(result);
            } else {
                singlePay(result);
            }

        } catch (Exception e) {
            result.exception = e;
            requestDataObserver(result, null, RechargeErrType.JSON_CANNOT_RESOLVE, "");
        }
    }


    private Double getSumCost(ArrayList<PayOrderChapterBeanInfo> chapterInfos) {

        double costSum = 0;
        if (chapterInfos != null && chapterInfos.size() > 0) {
            for (PayOrderChapterBeanInfo chapterInfo : chapterInfos) {
                costSum += chapterInfo.cost;
            }
        }
        return costSum;
    }


    private void lotPay(RechargeMsgResult result) throws Exception {
        //批量扣费
        String totalPrice = paramGet(RechargeMsgResult.PAY_TOTAL_PRICE, "");
        String afterNum = paramGet(RechargeMsgResult.PAY_AFTER_NUM, "");
        String discountPrice = paramGet(RechargeMsgResult.PAY_DISCOUNT_PRICE, "");
        String discountRate = paramGet(RechargeMsgResult.PAY_DISCOUNT_RATE, "");

        PayLog.d("发起批量支付请求|bookId:" + bookId + "|baseChapterId:" + baseChapterId + "|afterNum:" + afterNum + "|totalPrice:" + totalPrice + "|afterNum:" + afterNum + "|discountPrice:" + discountPrice + "|discountRate:" + discountRate);

        LotPayOrderBeanInfo beanInfo = RechargeLibUtils.getInstance().lotChapterOrderBeanInfo(bookId, baseChapterId, afterNum, discountRate);

        if (beanInfo.isSuccess()) {

            if (beanInfo.isExistChapterData()) {
                double costSum = getSumCost(beanInfo.chapterInfos);
                PayLog.d(" 总消费：" + costSum);

                result.relult = true;
                result.what = RechargeObserverConstants.SUCCESS;
                result.map.put(RechargeMsgResult.IS_ADD_SHELF, "2");
                result.map.put(RechargeMsgResult.REQUEST_JSON, beanInfo.jsonStr);
                result.errType.setErrCode(action.actionCode(), RechargeErrType.SUCCESS);
                nodifyObservers(result);
            } else {
                int errType = RechargeErrType.ORDER_PAY_FAIL;
                String retMsg = "";
                if (!TextUtils.isEmpty(beanInfo.getRetMsg())) {
                    retMsg = beanInfo.getRetMsg();
                }
                requestDataObserver(result, beanInfo, errType, retMsg);
            }
        } else {
            requestDataObserver(result, beanInfo, RechargeErrType.DATA_ERROR, beanInfo.getRetMsg());
        }
    }

    private void singlePay(RechargeMsgResult result) throws Exception {
        //单章扣费
        SingleOrderBeanInfo beanInfo = RechargeLibUtils.getInstance().singleOrderOrSingleOrderPageBeanInfo(bookId, baseChapterId, autoPay, confirmPay);

        if (beanInfo.isSuccess()) {

            PayLog.d("单章加载支付检查请求返回 状态status: "
                    + beanInfo.status + "|1.扣费成功 2.不需要付费-免费章节 包括限免书籍，包括章节缺失  3.不需要付费-之前已经付费过  4:扣费失败-余额不足去充值 5.需要确认弹窗（余额足的情况） ，" + "retMsg:" + beanInfo.getRetMsg() + ",preload_num:" + beanInfo.preloadNum);

            /** status
             * 1.扣费成功 2.不需要付费-免费章节 包括限免书籍，包括章节缺失  3.不需要付费-之前已经付费过 4:扣费失败-余额不足去充值 5.需要确认弹窗（余额足的情况）
             */
            if (beanInfo.singleCheckIsSuccess()) {
                //直接成功
                result.relult = true;
                result.what = RechargeObserverConstants.SUCCESS;
                result.map.put(RechargeMsgResult.REQUEST_JSON, beanInfo.jsonStr);

                if (beanInfo.isAddBookShelf()) {
                    result.map.put(RechargeMsgResult.IS_ADD_SHELF, "2");
                }
                result.errType.setErrCode(action.actionCode(), RechargeErrType.SUCCESS);
                nodifyObservers(result);
            } else if (beanInfo.singleCheckIsGoToOrder()) {
                //需要弹出确认订购
                result.relult = true;
                result.what = RechargeObserverConstants.SINGLE_GOTO_ORDER;
                result.errType.setErrCode(action.actionCode(), RechargeErrType.SUCCESS);
                result.map.put(RechargeMsgResult.REQUEST_JSON, beanInfo.jsonStr);
                nodifyObservers(result);
            } else {
                requestDataObserver(result, beanInfo, RechargeErrType.DATA_ERROR, beanInfo.getRetMsg());
            }

        } else {
            requestDataObserver(result, beanInfo, RechargeErrType.DATA_ERROR, beanInfo.getRetMsg());
        }

    }

    private void requestDataObserver(RechargeMsgResult result, HwPublicBean beanInfo, int errType, String retMsg) {
        StringBuffer buffer = new StringBuffer();
        buffer.append("errType:" + errType + "|");
        if (null != beanInfo) {
            buffer.append("订购过程， 支付异常 retCode=" + beanInfo.getRetCode());
            if (beanInfo.isTokenExpireOrNeedLogin()) {
                result.map.put(RechargeMsgResult.APP_NEED_LOGIN_OR_TOKEN_INVALID, "1");
            }
        } else {
            buffer.append("订购过程， 支付异常 payOrderCheck=null");
        }
        result.what = RechargeObserverConstants.FAIL;
        result.relult = true;
        if (TextUtils.isEmpty(retMsg)) {
            result.errType.setErrCode(action.actionCode(), RechargeErrType.ORDER_FAIL);
        } else {
            result.errType.setErrCode(action.actionCode(), RechargeErrType.RETURN_MY_ERROR, retMsg);
        }
        result.map.put(RechargeMsgResult.MORE_DESC, buffer.toString());

        nodifyObservers(result);
    }
}
