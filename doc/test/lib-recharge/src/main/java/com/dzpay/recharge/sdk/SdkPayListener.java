package com.dzpay.recharge.sdk;

import com.dzpay.recharge.bean.RechargeConstants;
import com.dzpay.recharge.netbean.PublicResBean;

/**
 * 支付监听
 *
 * @author zhenglk 15/8/27.
 */
public abstract class SdkPayListener {

    /**
     * 回调结果
     *
     * @param result 回调数据
     * @return boolean
     */
    public abstract boolean onResult(PublicResBean result);

    /**
     * 状态变化
     *
     * @param status  状态值
     * @param message 提示语
     */
    public void stautsChange(int status, String message) {

    }

    /**
     * 充值状态
     * @param status 状态
     *               <p>
     *               <p>
     *               {@link RechargeConstants#START_MAKE_ORDER}
     *               {@link RechargeConstants#MAKE_ORDER_FAIL}
     *               {@link RechargeConstants#START_RECHARGE}
     *               {@link RechargeConstants#ORDER_NOTIFY_START}
     *               {@link RechargeConstants#ORDER_NOTIFY_SUCCESS}
     *               {@link RechargeConstants#ORDER_NOTIFY_FAIL}
     * @param result bean
     */
    public void rechargeStatus(int status, PublicResBean result) {

    }

}
