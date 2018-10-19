package com.dzbook.recharge;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;

import com.dzbook.lib.utils.ALog;
import com.dzbook.mvp.presenter.LotOrderPresenter;
import com.dzbook.mvp.presenter.SingleOrderPresenter;
import com.dzbook.pay.AutoTest;
import com.dzbook.pay.Listener;
import com.dzpay.recharge.bean.RechargeAction;
import com.dzpay.recharge.bean.RechargeMsgResult;
import com.dzpay.recharge.bean.RechargeObserverConstants;

import java.util.HashMap;
import java.util.Map;

/**
 * 观察者模式，监听处理结果，流程控制
 *
 * @author huangyoubin
 */
public class RechargeObserver {
    /**
     * PARAMS
     */
    public static final String PARAMS = "params";

    private static final String TAG = "RechargeObserver: ";

    /**
     * listener
     */
    public Listener listener;
    /**
     * context
     */
    public Context context;
    /**
     * action
     */
    public RechargeAction action;
    private Handler handler;

    /**
     * RechargeObserver
     * @param context context
     * @param listener listener
     * @param actionFrom actionFrom
     */
    public RechargeObserver(Context context, Listener listener, RechargeAction actionFrom) {
        this.context = context;
        this.listener = listener;
        this.action = actionFrom;
        handler = new AbsHandler(context.getMainLooper());
    }

    public RechargeAction getAction() {
        return action;
    }

    /**
     * 更新接口
     *
     * @param msgResult 传入状态对象，
     */
    public void update(RechargeMsgResult msgResult) {
        if (handler == null) {
            return;
        }
        if (msgResult == null) {
            return;
        }
        Message message = handler.obtainMessage();
        message.obj = msgResult;
        message.sendToTarget();

    }

    /**
     * 更新接口
     *
     * @param msgResult 传入状态对象，
     */
    public void updateDir(RechargeMsgResult msgResult) {
        if (null != msgResult) {
            handleMsg(msgResult);
        }
    }

    /**
     * AbsHandler
     */
    public class AbsHandler extends Handler {

        /**
         * AbsHandler
         * @param looper looper
         */
        public AbsHandler(Looper looper) {
            super(looper);
        }

        @Override
        @AutoTest
        public void handleMessage(Message msg) {
            RechargeMsgResult msgResult = null;
            if (msg.obj.getClass().getName().equals(RechargeMsgResult.class.getName())) {
                msgResult = (RechargeMsgResult) msg.obj;
                if (msgResult == null) {
                    return;
                }
                handleMsg(msgResult);
            }
        }
    }

    /**
     * 处理返回结果
     *
     * @param msgResult msgResult
     */
    public void handleMsg(RechargeMsgResult msgResult) {
        ALog.dLk("RechargeObserver handleMsg what=" + msgResult.what);
        HashMap<String, String> params = new HashMap<>(msgResult.map);
        switch (msgResult.what) {
            case RechargeObserverConstants.FAIL:
                onErr(msgResult, listener);
                break;
            case RechargeObserverConstants.SUCCESS:
                onSuccess(msgResult);
                break;
            case RechargeObserverConstants.STATUS_CHANGE:
                onStatusChange(msgResult);
                break;
            case RechargeObserverConstants.LOT_GOTO_ORDER: {
                LotOrderPresenter.launchLotOrderPage(context, this, params);
                break;
            }

            case RechargeObserverConstants.SINGLE_GOTO_ORDER: {
                SingleOrderPresenter.launchSingleOrderPage(context, this, params);
                break;
            }
            case RechargeObserverConstants.RECHARGE_STATUS_CHANGE: {
                rechargeStatus(msgResult);
                break;
            }
            case RechargeObserverConstants.PACKBOOK:
                if (null != listener) {
                    listener.onStatusChange(RechargeObserverConstants.PACKBOOK, params);
                }
                break;
            default:
                break;
        }
    }

    /**
     * onSuccess
     * @param msgResult msgResult
     */
    public void onSuccess(RechargeMsgResult msgResult) {
        synchronized (RechargeObserver.class) {
            try {
                HashMap<String, String> params = msgResult.map;

                params.put(RechargeMsgResult.ERR_CODE, msgResult.errType.getErrCode());
                params.put(RechargeMsgResult.ERR_DES, "成功");
                if (listener != null) {
                    listener.onSuccess(action.ordinal(), params);
                    listener = null;
                }
                context = null;
            } catch (Exception e) {
                ALog.printStackWz(e);
            }
        }
    }

    /**
     * onErr
     * @param msgResult msgResult
     * @param aListener listener
     */
    public void onErr(RechargeMsgResult msgResult, Listener aListener) {
        synchronized (RechargeObserver.class) {
            try {
                HashMap<String, String> params = msgResult.map;
                if (params == null) {
                    return;
                }

                if (msgResult.exception != null) {
                    String moreDesc = params.get(RechargeMsgResult.MORE_DESC) + ", exception=" + ALog.getStackTraceString(msgResult.exception);
                    params.put(RechargeMsgResult.MORE_DESC, moreDesc);
                }
                params.put(RechargeMsgResult.ERR_CODE, msgResult.errType.getErrCode());
                // ERR_DES 为空的时候，补充 ERR_DES
                if (TextUtils.isEmpty(params.get(RechargeMsgResult.ERR_DES))) {
                    params.put(RechargeMsgResult.ERR_DES, msgResult.errType.getErrDes());
                }

                ALog.dZz(TAG + "Fail:" + msgResult.errType.getErrCode() + ":" + msgResult.errType.getErrDes());

                if (aListener != null) {
                    aListener.onFail(params);
                    this.listener = null;
                }
                context = null;
            } catch (Exception ignore) {
            }
        }
    }

    /**
     * onStatusChange
     * @param msgResult msgResult
     */
    public void onStatusChange(RechargeMsgResult msgResult) {
        synchronized (RechargeObserver.class) {
            try {
                Map<String, String> params = msgResult.map;

                params.put(RechargeMsgResult.ERR_CODE, msgResult.errType.getErrCode());
                String statusChangeStr = params.get(RechargeMsgResult.STATUS_CHANGE);
                int statusChange = -1;
                if (!TextUtils.isEmpty(statusChangeStr)) {
                    try {
                        statusChange = Integer.parseInt(statusChangeStr);
                    } catch (Exception ignore) {
                    }
                }
                if (listener != null && !TextUtils.isEmpty(params.get(RechargeMsgResult.STATUS_CHANGE_MSG))
                        && statusChange != -1) {
                    listener.onStatusChange(statusChange, params);
                }
            } catch (Exception ignore) {
            }
        }
    }

    /**
     * rechargeStatus
     * @param msgResult msgResult
     */
    public void rechargeStatus(RechargeMsgResult msgResult) {
        synchronized (RechargeObserver.class) {
            try {
                Map<String, String> params = msgResult.map;

                String statusChangeStr = params.get(RechargeMsgResult.RECHARGE_STATUS);
                int statusChange = -1;
                if (!TextUtils.isEmpty(statusChangeStr)) {
                    try {
                        statusChange = Integer.parseInt(statusChangeStr);
                    } catch (Exception ignore) {
                    }
                }

                if (listener != null && statusChange != -1) {
                    listener.onRechargeStatus(statusChange, params);
                }
            } catch (Exception ignore) {
            }
        }
    }
}
