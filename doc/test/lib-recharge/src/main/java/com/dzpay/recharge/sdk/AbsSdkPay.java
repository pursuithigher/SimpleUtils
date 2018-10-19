package com.dzpay.recharge.sdk;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender;

import com.dzbook.lib.rx.CompositeDisposable;
import com.dzpay.recharge.bean.RechargeErrType;
import com.dzpay.recharge.logic.Observer;
import com.dzpay.recharge.net.RechargeLibUtils;
import com.dzpay.recharge.netbean.OrdersNotifyBeanInfo;
import com.dzpay.recharge.netbean.PublicResBean;
import com.dzpay.recharge.netbean.VipOrdersNotifyBeanInfo;
import com.dzpay.recharge.utils.PayLog;
import com.dzpay.recharge.utils.RechargeWayUtils;
import com.huawei.hms.api.ConnectionResult;
import com.huawei.hms.api.HuaweiApiAvailability;
import com.huawei.hms.api.HuaweiApiClient;
import com.huawei.hms.support.api.client.PendingResult;
import com.huawei.hms.support.api.client.ResultCallback;
import com.huawei.hms.support.api.client.Status;
import com.huawei.hms.support.api.entity.pay.PayStatusCodes;
import com.huawei.hms.support.api.pay.HuaweiPay;
import com.huawei.hms.support.api.pay.PayResult;
import com.huawei.hms.support.api.pay.PayResultInfo;

import org.json.JSONException;

import java.util.HashMap;


/**
 * 支付的基类
 *
 * @author zhenglk 15/8/27.
 */
public abstract class AbsSdkPay implements HuaweiApiClient.OnConnectionFailedListener, HuaweiApiClient.ConnectionCallbacks {


    /**
     * 1:成功
     */
    public static final int PAY_SUCCESS = 1;
    /**
     * 2:失败
     */
    public static final int PAY_FAIL = 2;

    /**
     * 最多重试次数
     */
    static final int MAX_RETRY_COUNT = 3;

    /**
     * 启动参数，区分startactivityforresult的处理结果,调用支付接口
     */
    private static final int REQ_CODE_PAY = 4001;

    /**
     * 调用HuaweiApiAvailability.getInstance().resolveError传入的第三个参数
     * 作用同startactivityforresult方法中的requestcode
     */
    private static final int REQUEST_HMS_RESOLVE_ERROR = 1000;

    /**
     * 如果CP在onConnectionFailed调用了resolveError接口，那么错误结果会通过onActivityResult返回
     * 具体的返回码通过该字段获取
     */
    private static final String EXTRA_RESULT = "intent.extra.RESULT";


    /**
     * 是否需要查询订单状态(只适应于微信支付)
     */
    public boolean isNeedOrderQuery = false;
    /**
     * 华为移动服务Client
     */
    public HuaweiApiClient client;


    HashMap<String, String> params;
    SdkPayListener listener;
    CompositeDisposable composite = new CompositeDisposable();
    private String type;


    private long startTime = System.currentTimeMillis();
    private long lastTime = startTime;
    private StringBuilder moreDesBuilder = new StringBuilder("step:");
    private StringBuilder timeBuilder = new StringBuilder(" time:");

    /**
     * 构造
     *
     * @param type     类型
     * @param params   参数
     * @param listener 监听
     */
    public AbsSdkPay(String type, HashMap<String, String> params, SdkPayListener listener) {
        this.type = type;
        this.listener = listener;
        this.params = params;
    }


    /**
     * addStep
     *
     * @param step 走到哪一步了
     */
    public void addStep(String step) {
        moreDesBuilder.append(step).append(">");
        long curTime = System.currentTimeMillis();
        timeBuilder.append(curTime - lastTime).append(">");
        lastTime = curTime;
    }

    public String getStepStr() {
        return moreDesBuilder.toString() + timeBuilder.toString() + " countTime:" + (System.currentTimeMillis() - startTime);
    }

    /**
     * 查询订单状态(只适应于微信支付),微信支付的需要实现此方法
     * 也适用于超时处理
     */
    public void orderQueryStart() {


    }

    /**
     * 订单处理开始
     *
     * @param rechargeMoneyId id
     * @param observer        回调
     */
    public abstract void orderEntry(String rechargeMoneyId, Observer observer);

    /**
     * 订单处理结束
     */
    public abstract void orderDestroy();

    /**
     * 获取activity上下文
     *
     * @return Activity
     */
    public abstract Activity getActivity();

    /**
     * 是否VIP自动订购，外部需要的地方需要复写
     *
     * @return false
     */
    public boolean isVipAutoOrder() {
        return false;
    }

    /**
     * 华为服务连接成功开始支付
     */
    public abstract void connectSuccessStartHwPay();

    /**
     * 充值完成后,发送服务器
     * 错误则主动提示用户信息,而不是统一的支付失败,请稍后再试
     *
     * @param result     数据
     * @param desc       描述
     * @param isCallBack 是否listener回调，为了展示计费内部的提示信息
     */
    public abstract void outSideNotifyServer(final String result, final String desc, final boolean isCallBack);


    /**
     * 开始下单
     *
     * @param rechargeMoneyId 订单id
     * @return PublicResBean
     */
    public PublicResBean makeOrder(String rechargeMoneyId) {
        addStep("下订单开始");
        PublicResBean order;
        try {
            int typeValue = RechargeWayUtils.getInt(type);
            switch (typeValue) {
                case RechargeWayUtils.VIP_OPEN_HW_PAY:
                    order = RechargeLibUtils.getInstance().getRequestVipOrderBeanInfo(rechargeMoneyId);
                    break;
                default:
                    order = RechargeLibUtils.getInstance().getRequestOrderBeanInfo(rechargeMoneyId);
                    break;
            }

        } catch (JSONException e) {
            order = new PublicResBean().error(RechargeErrType.JSON_CANNOT_RESOLVE, e);
        } catch (Exception e) {
            order = new PublicResBean().error(RechargeErrType.NETWORK_FAIL, e);
        }
        if (null == order || RechargeErrType.SUCCESS != order.errorType) {
            addStep("下订单失败,服务器返回RetCode:" + (order != null ? order.getRetCode() : ""));
        } else {
            addStep("下订单成功");
        }
        return order;
    }

    /**
     * 订单通知
     *
     * @param orderNo 订单号
     * @param result  结果
     * @param desc    描述
     * @return PublicResBean
     */
    public PublicResBean notifyServer(String orderNo, String result, String desc) {
        addStep("订单通知开始");
        try {

            int typeValue = RechargeWayUtils.getInt(type);
            switch (typeValue) {
                case RechargeWayUtils.VIP_OPEN_HW_PAY: {
                    return vipOpenNotifyServer(orderNo, result, desc, isVipAutoOrder() ? 1 : 2);
                }
                default: {
                    return rechargeNotifyServer(orderNo, result, desc);
                }
            }

        } catch (JSONException e) {
            addStep("订单通知失败@JSON");
            return new PublicResBean().error(RechargeErrType.JSON_CANNOT_RESOLVE, e);
        } catch (Exception e) {
            addStep("订单通知失败");
            return new PublicResBean().error(RechargeErrType.NETWORK_FAIL, e);
        }
    }

    /**
     * 充值订单通知
     *
     * @param orderNo
     * @param result
     * @param desc
     * @return
     * @throws Exception
     */
    private PublicResBean rechargeNotifyServer(String orderNo, String result, String desc) throws Exception {

        OrdersNotifyBeanInfo notifyBeanInfo = RechargeLibUtils.getInstance().getOrderNotifyRequestInfo(orderNo, result, desc);
        if (notifyBeanInfo.isRechargeSuccess()) {
            addStep("订单通知完成-成功");
        } else {
            addStep("订单通知完成-失败@result:" + notifyBeanInfo.result);
        }
        PayLog.d(notifyBeanInfo.toString() + "|tips:" + notifyBeanInfo.repMsg);

        return notifyBeanInfo;
    }

    /**
     * vip支付订单通知
     *
     * @param orderNo
     * @param result
     * @param desc
     * @return
     * @throws Exception
     */
    private PublicResBean vipOpenNotifyServer(String orderNo, String result, String desc, int isAutoOpend) throws Exception {

        VipOrdersNotifyBeanInfo notifyBeanInfo = RechargeLibUtils.getInstance().getVipOrdersNotifyRequestInfo(orderNo, result, desc, isAutoOpend);
        if (notifyBeanInfo.isRechargeSuccess()) {
            addStep("订单通知完成-成功");
        } else {
            addStep("订单通知完成-失败@result:" + notifyBeanInfo.result);
        }

        PayLog.d(notifyBeanInfo.toString() + "|tips:" + notifyBeanInfo.repMsg);

        return notifyBeanInfo;
    }


    /**
     * activity 回调
     *
     * @param requestCode 请求码
     * @param resultCode  结果吗
     * @param data        数据
     */
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == REQUEST_HMS_RESOLVE_ERROR) {
            if (resultCode == Activity.RESULT_OK) {

                int result = data.getIntExtra(EXTRA_RESULT, 0);

                if (result == ConnectionResult.SUCCESS) {
                    PayLog.d("错误成功解决");
                    if (!client.isConnecting() && !client.isConnected()) {
                        client.connect(getActivity());
                    }
                } else {
                    if (result == ConnectionResult.CANCELED) {
                        PayLog.d("解决错误过程被用户取消");
                    } else if (result == ConnectionResult.INTERNAL_ERROR) {
                        PayLog.d("发生内部错误，重试可以解决");
                        //CP可以在此处重试连接华为移动服务等操作，导致失败的原因可能是网络原因等
                    } else {
                        PayLog.d("未知返回码");
                    }
                    //其他错误码请参见开发指南或者API文档
                    listener.onResult(new PublicResBean().error(RechargeErrType.FAIL, "code:result,华为华为移动服务连接失败"));
                }
            } else {
                PayLog.d("调用解决方案发生错误");
                listener.onResult(new PublicResBean().error(RechargeErrType.FAIL, "code:result,华为华为移动服务连接失败"));
            }
        } else {
            handlePayPResult(requestCode, resultCode, data);
        }

    }

    /**
     * 初始化
     */
    public void initHuaweiApiClient() {
        //创建华为移动服务client实例用以实现支付功能
        //需要指定api为HuaweiPay.PAY_API
        //连接回调以及连接失败监听
        client = new HuaweiApiClient.Builder(getActivity())
                .addApi(HuaweiPay.PAY_API)
                .addOnConnectionFailedListener(this)
                .addConnectionCallbacks(this)
                .build();

        //建议在oncreate的时候连接华为移动服务
        //业务可以根据自己业务的形态来确定client的连接和断开的时机，但是确保connect和disconnect必须成对出现
        client.connect(getActivity());
    }


    @Override
    public void onConnected() {
        //华为移动服务client连接成功，在这边处理业务自己的事件
        PayLog.d("HuaweiApiClient 连接成功");
        connectSuccessStartHwPay();
    }

    @Override
    public void onConnectionSuspended(int cause) {
        //HuaweiApiClient断开连接的时候，业务可以处理自己的事件
        PayLog.e("HuaweiApiClient 连接断开" + cause);
        //HuaweiApiClient异常断开连接, if 括号里的条件可以根据需要修改

        listener.onResult(new PublicResBean().error(RechargeErrType.FAIL, "HuaweiApiClient 连接断开" + cause));
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        PayLog.e("HuaweiApiClient连接失败，错误码：" + result.getErrorCode());
        if (HuaweiApiAvailability.getInstance().isUserResolvableError(result.getErrorCode())) {
            final int errorCode = result.getErrorCode();

            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    // 此方法必须在主线程调用
                    HuaweiApiAvailability.getInstance().resolveError(getActivity(), errorCode, REQUEST_HMS_RESOLVE_ERROR);
                }
            });
        } else {
            //其他错误码请参见开发指南或者API文档
            payFailListener(RechargeErrType.FAIL, "code:connectionResult.getErrorCode(),华为华为移动服务连接失败");
        }
    }

    /**
     * 连接华为服务
     */
    public void hwConnect() {
        if (client == null) {
            initHuaweiApiClient();
        }
        if (!client.isConnecting()) {
            client.connect(getActivity());
        }
    }

    /**
     * 支付接口，CP可以直接参照该方法写法
     */
    public void hwStartPay() {
        if (!client.isConnected()) {
            client.connect(getActivity());
            return;
        }
        connectSuccessStartHwPay();
    }


    /**
     * 处理支付结果
     *
     * @param requestCode 请求码
     * @param resultCode  结果码
     * @param data        数据
     */
    public void handlePayPResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == REQ_CODE_PAY) {
            //当返回值是-1的时候表明用户支付调用调用成功
            if (resultCode == Activity.RESULT_OK) {
                //获取支付完成信息
                PayResultInfo payResultInfo = HuaweiPay.HuaweiPayApi.getPayResultInfoFromIntent(data);
                if (payResultInfo != null) {
                    if (PayStatusCodes.PAY_STATE_SUCCESS == payResultInfo.getReturnCode()) {

                        PayLog.d("支付/订阅成功，开始服务器订单通知");

                        outSideNotifyServer(PAY_SUCCESS + "", "", true);

                    } else if (PayStatusCodes.PAY_STATE_CANCEL == payResultInfo.getReturnCode()) {
                        //支付失败，原因是用户取消了支付，可能是用户取消登录，或者取消支付
                        String des = "用户中途取消";
                        payFailListenerReturnResMsg(RechargeErrType.SYSTEM_BACK, des, "取消支付");
                    } else {
                        //支付失败，其他一些原因
                        String des = "三方支付失败信息：" + payResultInfo.getErrMsg();
                        payFailListener(RechargeErrType.SYSTEM_BACK, des);
                    }
                } else {
                    //支付失败
                    String des = "三方支付失败信息：" + resultCode;
                    payFailListener(RechargeErrType.ORDER_PAY_FAIL, des);
                }
            } else {
                //当resultCode 为0的时候表明用户未登录，则CP可以处理用户不登录事件
                String des = "resultCode为0, 用户未登录 CP可以处理用户不登录事件";
                payFailListener(RechargeErrType.ORDER_PAY_FAIL, des);
            }
        }
    }


    /**
     * 停止连接华为移动服务
     */
    public void disConnect() {
        if (client != null) {
            //建议在onDestroy的时候停止连接华为移动服务
            //业务可以根据自己业务的形态来确定client的连接和断开的时机，但是确保connect和disconnect必须成对出现
            client.disconnect();
        }
    }

    /**
     * 支付失败回调
     *
     * @param errorType 错误类型
     * @param des       描述
     */
    public void payFailListener(int errorType, String des) {
        PayLog.d("payFailListener errorType=" + errorType + "des=" + des);
        addStep(des);
        outSideNotifyServer(PAY_FAIL + "", des, false);
        listener.onResult(new PublicResBean().error(errorType, des));
    }

    /**
     * 支付失败回调
     *
     * @param errorType 错误类型
     * @param des       描述
     * @param resMsg    数据
     */
    public void payFailListenerReturnResMsg(int errorType, String des, String resMsg) {
        PayLog.d(des);
        addStep(des);
        outSideNotifyServer(PAY_FAIL + "", des, false);
        listener.onResult(new PublicResBean().error(errorType, des, resMsg));
    }


    /**
     * 设置回调
     *
     * @param payResult 参数
     */
    public void setResultCallback(PendingResult<PayResult> payResult) {
        payResult.setResultCallback(new ResultCallback<PayResult>() {

            @Override
            public void onResult(PayResult payResult) {
                //支付鉴权结果，处理result.getStatus()
                Status status = payResult.getStatus();
                if (PayStatusCodes.PAY_STATE_SUCCESS == status.getStatusCode()) {
                    //当支付回调 返回码为0的时候，表明支付流程正确，CP需要调用startResolutionForResult接口来进来后续处理
                    //支付会先判断华为帐号是否登录，如果未登录，会先提示用户登录帐号。之后才会进行支付流程
                    try {
                        status.startResolutionForResult(getActivity(), REQ_CODE_PAY);
                    } catch (IntentSender.SendIntentException e) {
                        PayLog.e("启动支付失败" + e.getMessage());
                        payFailListener(RechargeErrType.ORDER_PAY_FAIL, "启动支付失败" + e.getMessage());
                    }
                } else {
                    PayLog.e("支付失败，原因 :" + status.getStatusCode());
                    payFailListener(RechargeErrType.ORDER_PAY_FAIL, "支付鉴权结果失败，原因 :" + status.getStatusCode());
                }
            }
        });
    }
}
