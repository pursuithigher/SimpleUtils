package com.dzbook.pay;


import java.util.HashMap;
import java.util.Map;

/**
 * 结果回调接口
 *
 * @author huangyoubin
 */
public abstract class Listener {
    /**
     * onSuccess
     * @param ordinal ordinal
     * @param parm parm
     */
    public abstract void onSuccess(int ordinal, HashMap<String, String> parm);

    /**
     * onFail
     * @param parm parm
     */
    public abstract void onFail(HashMap<String, String> parm);

    /**
     * 状态切换回调。
     *
     * @param status 显示dialog {@link com.dzpay.recharge.bean.RechargeConstants#DIALOG_SHOW}
     *               隐藏dialog {@link com.dzpay.recharge.bean.RechargeConstants#DIALOG_DISMISS}
     * @param parm   附加参数
     */
    public void onStatusChange(int status, Map<String, String> parm) {
        // FIXME:lizz 2017/9/9 这块需要再处理下
//        EventBus.getDefault().post(new LoaderStatus(status,parm));
    }

    /**
     * 充值状态 主要用于自有充值
     *
     * @param status {@link com.dzpay.recharge.bean.RechargeConstants#START_MAKE_ORDER}
     *               {@link com.dzpay.recharge.bean.RechargeConstants#MAKE_ORDER_FAIL}
     *               {@link com.dzpay.recharge.bean.RechargeConstants#START_RECHARGE}
     *               {@link com.dzpay.recharge.bean.RechargeConstants#ORDER_NOTIFY_START}
     *               {@link com.dzpay.recharge.bean.RechargeConstants#ORDER_NOTIFY_SUCCESS}
     *               {@link com.dzpay.recharge.bean.RechargeConstants#ORDER_NOTIFY_FAIL}
     * @param parm parm
     */
    public void onRechargeStatus(int status, Map<String, String> parm) {
    }
}
