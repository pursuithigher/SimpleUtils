package com.dzbook.recharge.order;

import android.content.Context;

import com.dzbook.pay.Listener;
import com.dzpay.recharge.netbean.OrdersCommonBean;

import java.util.HashMap;

/**
 * RechargeParamBean
 * @author lizz 2018/4/24.
 * 充值列表信息bean
 */
public class RechargeParamBean {

    /**
     * context
     */
    public Context context;

    /**
     * listener
     */
    public Listener listener;

    /**
     * action
     */
    public int mOrdinal;

    /**
     * 充值来源
     */
    public String sourceWhere;

    /**
     * map参数
     */
    public HashMap<String, String> methodParams;

    /**
     * 打点trackId
     */
    public String trackId;

    /**
     * 打点来源
     */
    public String operateFrom;

    /**
     * 打点来源
     */
    public String partFrom;

    /**
     * 订单信息bean
     */
    public OrdersCommonBean bean;

    private RechargeParamBean() {

    }

    /**
     * RechargeParamBean
     * @param context context
     * @param listener listener
     * @param mOrdinal mOrdinal
     * @param sourceWhere sourceWhere
     * @param methodParams methodParams
     * @param trackId trackId
     * @param operateFrom operateFrom
     * @param partFrom partFrom
     */
    public RechargeParamBean(Context context, Listener listener, int mOrdinal, String sourceWhere, HashMap<String, String> methodParams, String trackId, String operateFrom, String partFrom) {
        this.context = context;
        this.listener = listener;
        this.mOrdinal = mOrdinal;
        this.sourceWhere = sourceWhere;
        this.methodParams = methodParams;
        this.trackId = trackId;
        this.operateFrom = operateFrom;
        this.partFrom = partFrom;
    }

    /**
     * RechargeParamBean
     * @param context context
     * @param listener listener
     * @param mOrdinal mOrdinal
     * @param sourceWhere sourceWhere
     * @param methodParams methodParams
     * @param trackId  trackId
     * @param operateFrom operateFrom
     * @param partFrom partFrom
     * @param bean bean
     */
    public RechargeParamBean(Context context, Listener listener, int mOrdinal, String sourceWhere, HashMap<String, String> methodParams, String trackId, String operateFrom, String partFrom, OrdersCommonBean bean) {
        this.context = context;
        this.listener = listener;
        this.mOrdinal = mOrdinal;
        this.sourceWhere = sourceWhere;
        this.methodParams = methodParams;
        this.trackId = trackId;
        this.operateFrom = operateFrom;
        this.partFrom = partFrom;
        this.bean = bean;
    }
}
