package com.dzpay.recharge.sdk;

import android.app.Activity;
import android.text.TextUtils;

import com.dzbook.lib.net.DzSchedulers;
import com.dzbook.lib.utils.ALog;
import com.dzpay.recharge.bean.RechargeConstants;
import com.dzpay.recharge.bean.RechargeErrType;
import com.dzpay.recharge.bean.RechargeMsgResult;
import com.dzpay.recharge.logic.Observer;
import com.dzpay.recharge.netbean.PublicResBean;
import com.dzpay.recharge.netbean.VipOrdersBean;
import com.dzpay.recharge.netbean.VipOrdersBeanInfo;
import com.dzpay.recharge.netbean.VipOrdersNotifyBeanInfo;
import com.dzpay.recharge.utils.PayLog;
import com.huawei.hms.support.api.client.PendingResult;
import com.huawei.hms.support.api.entity.pay.PayReq;
import com.huawei.hms.support.api.entity.pay.WithholdRequest;
import com.huawei.hms.support.api.pay.HuaweiPay;
import com.huawei.hms.support.api.pay.PayResult;

import java.util.HashMap;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

/**
 * 华为支付
 * @author lizz 2018/4/19.
 */

public class SdkVipOpenHuaWeiPay extends AbsSdkPay {

    private Activity mActivity;

    private String orderNum;

    private VipOrdersBean ordersHwBean;

    private boolean isVipRenew;

    /**
     * 构造
     *
     * @param activity 上下文
     * @param type     类型
     * @param params   参数
     * @param listener 监听
     */
    public SdkVipOpenHuaWeiPay(Activity activity, String type, HashMap<String, String> params, SdkPayListener listener) {
        super(type, params, listener);
        mActivity = activity;
    }

    @Override
    public void orderEntry(final String rechargeMoneyId, Observer observer) {
        hwConnect();

//        isVipRenew = TextUtils.equals(params.get(RechargeMsgResult.IS_VIP_OPEN_RENEW), "1");

        listener.rechargeStatus(RechargeConstants.START_MAKE_ORDER, null);

        Disposable disposable = Observable.create(new ObservableOnSubscribe<PublicResBean>() {

            @Override
            public void subscribe(ObservableEmitter<PublicResBean> e) {

                PublicResBean bean = makeOrder(rechargeMoneyId);
                e.onNext(bean);
                e.onComplete();
            }
        }).observeOn(AndroidSchedulers.mainThread()).subscribeOn(Schedulers.io()).
                subscribeWith(new DisposableObserver<PublicResBean>() {
                    @Override
                    public void onNext(PublicResBean order) {

                        if (order != null && order.isSuccess() && order instanceof VipOrdersBeanInfo) {
                            VipOrdersBeanInfo huaWeiPayBean = (VipOrdersBeanInfo) order;
                            orderNum = huaWeiPayBean.orderNo;

                            isVipRenew = huaWeiPayBean.isAutoOrderWay();

                            startPay(huaWeiPayBean);
                        } else {
                            params.put(RechargeMsgResult.MORE_DESC, getStepStr());

                            listener.onResult(new PublicResBean().error(RechargeErrType.MAKE_ORDER_ERROR, "下订单失败"));

                            listener.rechargeStatus(RechargeConstants.MAKE_ORDER_FAIL, order);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });

        composite.addAndDisposeOldByKey("VIP_orderEntry", disposable);
    }

    @Override
    public void orderQueryStart() {
        outSideNotifyServer(PAY_SUCCESS + "", "", true);
    }

    @Override
    public boolean isVipAutoOrder() {
        if (isVipRenew) {
            return true;
        }
        return false;
    }

    @Override
    public void orderDestroy() {
        if (null != listener) {
            listener.rechargeStatus(RechargeConstants.ORDER_CORE_DESTROYED, null);
        }
        disConnect();
        composite.disposeAll();
    }

    private void startPay(final VipOrdersBeanInfo huaWeiPayBean) {

        try {

            listener.rechargeStatus(RechargeConstants.START_RECHARGE, huaWeiPayBean);

            if (huaWeiPayBean != null && huaWeiPayBean.orderHwBean != null) {

                ordersHwBean = huaWeiPayBean.orderHwBean;

                if (!TextUtils.isEmpty(ordersHwBean.applicationID) && !TextUtils.isEmpty(ordersHwBean.merchantId)
                        && !TextUtils.isEmpty(ordersHwBean.sign)) {

                    hwStartPay();
                } else {
                    addStep("_支付失败，有个参数为空");
                    params.put(RechargeMsgResult.MORE_DESC, getStepStr());
                    listener.onResult(new PublicResBean().error(RechargeErrType.MAKE_ORDER_ERROR, "下订单失败"));
                }
            } else {

                addStep("_支付失败，orderHwBean空");
                params.put(RechargeMsgResult.MORE_DESC, getStepStr());
                listener.onResult(new PublicResBean().error(RechargeErrType.MAKE_ORDER_ERROR, "下订单失败"));
            }

        } catch (Exception e) {
            PayLog.printStackTrace(e);
            listener.onResult(new PublicResBean().error(RechargeErrType.RECHARGE_DATA_ERROR, "支付出现异常，请重试"));
        }
    }

    @Override
    public void outSideNotifyServer(final String result, final String desc, final boolean isCallBack) {
        DzSchedulers.child(new Runnable() {
            @Override
            public void run() {
                PublicResBean pubBean = null;

                if (isCallBack) {
                    listener.rechargeStatus(RechargeConstants.ORDER_NOTIFY_START, null);
                    if (isVipRenew) {
                        listener.stautsChange(RechargeConstants.DIALOG_SHOW, "正在查询中，请稍后");
                    }

                    for (int i = 0; i < MAX_RETRY_COUNT; i++) {
                        try {
                            pubBean = notifyServer(orderNum, result, desc);

                            PayLog.d("通知次数第" + (i + 1) + "次");

                            if (pubBean != null && pubBean instanceof VipOrdersNotifyBeanInfo) {
                                VipOrdersNotifyBeanInfo result = (VipOrdersNotifyBeanInfo) pubBean;
                                if (result.isRechargeDelay()) {
                                    if (isVipRenew) {
                                        Thread.sleep(2000);
                                    } else {
                                        Thread.sleep(1200);
                                    }
                                    continue;
                                }
                                PayLog.i("充值通知结果: " + result.toString());
                            }
                        } catch (Exception e) {
                            ALog.printExceptionWz(e);
                        }
                        break;
                    }
                } else {
                    pubBean = notifyServer(orderNum, result, desc);
                }

                if (isCallBack) {

                    //订单通知
                    if (pubBean != null && pubBean.isSuccess()) {
                        listener.rechargeStatus(RechargeConstants.ORDER_NOTIFY_SUCCESS, pubBean);
                    } else {
                        listener.rechargeStatus(RechargeConstants.ORDER_NOTIFY_FAIL, pubBean);
                    }
                    params.put(RechargeMsgResult.MORE_DESC, getStepStr());
                    listener.onResult(pubBean);
                }

                if (pubBean == null || !(pubBean instanceof VipOrdersNotifyBeanInfo)) {
                    PayLog.e("通知失败！");
                    return;
                }
                PayLog.i("响应数据：" + pubBean);
            }
        });
    }


    @Override
    public void connectSuccessStartHwPay() {
        if (ordersHwBean == null) {
            PayLog.d("connectSuccessStartHwPay ordersHwBean为空");
            return;
        }

        if (client == null) {
            initHuaweiApiClient();
        }

        isNeedOrderQuery = true;
        PendingResult<PayResult> payResult;
        if (isVipRenew) {
            payResult = HuaweiPay.HuaweiPayApi.addWithholdingPlan(client, createRenewPayReq(ordersHwBean));

        } else {
            payResult = HuaweiPay.HuaweiPayApi.pay(client, createPayReq(ordersHwBean));
        }

        setResultCallback(payResult);
    }

    /**
     * 生成PayReq对象，用来在进行支付请求的时候携带支付相关信息
     * payReq订单参数需要商户使用在华为开发者联盟申请的RSA私钥进行签名，强烈建议将签名操作在商户服务端处理，避免私钥泄露
     */
    private PayReq createPayReq(VipOrdersBean bean) {

        PayReq payReq = new PayReq();
        payReq.productName = bean.productName;
        payReq.productDesc = bean.productDesc;
        payReq.applicationID = bean.applicationID;
        payReq.requestId = bean.requestId;
        payReq.amount = bean.amount;
        payReq.merchantId = bean.merchantId;
        payReq.merchantName = bean.merchantName;
        payReq.sdkChannel = bean.sdkChannel;
        payReq.url = bean.url;
        payReq.sign = bean.sign;
        payReq.serviceCatalog = bean.serviceCatalog;

        return payReq;
    }

    /**
     * 创建自动续订vip
     * 生成PayReq对象，用来在进行支付请求的时候携带支付相关信息
     * payReq订单参数需要商户使用在华为开发者联盟申请的RSA私钥进行签名，强烈建议将签名操作在商户服务端处理，避免私钥泄露
     */
    private WithholdRequest createRenewPayReq(VipOrdersBean bean) {

        WithholdRequest payReq = new WithholdRequest();
        payReq.productName = bean.productName;
        payReq.productDesc = bean.productDesc;
        payReq.applicationID = bean.applicationID;
        payReq.requestId = bean.requestId;
        payReq.amount = bean.amount;
        payReq.merchantId = bean.merchantId;
        payReq.merchantName = bean.merchantName;
        payReq.sdkChannel = bean.sdkChannel;
        payReq.url = bean.url;
        payReq.sign = bean.sign;
        payReq.serviceCatalog = bean.serviceCatalog;
        payReq.country = bean.country;
        payReq.tradeType = bean.tradeType;
        payReq.currency = bean.currency;

        return payReq;
    }


    @Override
    public Activity getActivity() {
        return mActivity;
    }
}
