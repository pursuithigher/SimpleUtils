package com.dzpay.recharge.logic;

import android.content.Context;
import android.os.Looper;
import android.text.TextUtils;

import com.dzpay.recharge.bean.RechargeAction;
import com.dzpay.recharge.bean.RechargeErrType;
import com.dzpay.recharge.bean.RechargeMsgResult;
import com.dzpay.recharge.bean.RechargeObserverConstants;
import com.dzpay.recharge.logic.core.DzPackOrderImpl;
import com.dzpay.recharge.logic.core.PayChapterImpl;
import com.dzpay.recharge.logic.core.PayCheckImpl;
import com.dzpay.recharge.logic.core.RechargeImpl;
import com.dzpay.recharge.net.NetCommonParamUtils;
import com.dzpay.recharge.threadpool.DzAbsRunnable;
import com.dzpay.recharge.threadpool.DzSingleExecutor;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * 操作管理类
 *
 * @author huangyoubin
 */
public class DZReadManager {


    /**
     * 普通线程池
     */
    public static final Executor NORMAL_THREAD_POOL = Executors.newSingleThreadExecutor();

    /**
     * 支付线程池
     */
    public static final Executor PAY_THREAD_POOL = new DzSingleExecutor();

    private static final ConcurrentMap<String, String> EXECUTE_CACHE = new ConcurrentHashMap<String, String>();
    /**
     * entry_thread_pool
     */
    private static final Executor ENTRY_THREAD_POOL = Executors.newSingleThreadExecutor();

    /**
     * 执行
     *
     * @param context  上下文
     * @param param    参数
     * @param ordinal  操作
     * @param instance 观察者
     */
    public static synchronized void execute(final Context context, final HashMap<String, String> param, final int ordinal, final Object instance) {

        Runnable runnable = new MyRunnable(ordinal, instance, param, context);

        // 发起队列任务以前的判断过程，可能会较长。如果在主线程，就再下放任务。
        if (Looper.myLooper() == Looper.getMainLooper()) {
            ENTRY_THREAD_POOL.execute(runnable);
        } else {
            runnable.run();
        }
    }

    private static RechargeMsgResult failRechargeMsgResult(HashMap<String, String> param, RechargeAction action) {
        RechargeMsgResult result = new RechargeMsgResult(param);
        result.relult = false;
        result.what = RechargeObserverConstants.FAIL;
        result.errType.setErrCode(action.actionCode(), RechargeErrType.THREAD_ERROR);
        return result;
    }

    private static String getKey(Map<String, String> param, RechargeAction action) {
        StringBuffer key = new StringBuffer();
        if (param != null) {
            key.append(param.toString());
        }
        if (action != null) {
            key.append(action.name());
        }
        return key.toString();

    }

    /**
     * execute run
     */
    private static class MyRunnable implements Runnable {
        private final int ordinal;
        private final Object instance;
        private final HashMap<String, String> param;
        private final Context context;

        public MyRunnable(int ordinal, Object instance, HashMap<String, String> param, Context context) {
            this.ordinal = ordinal;
            this.instance = instance;
            this.param = param;
            this.context = context;
        }

        @Override
        public void run() {
            final RechargeAction action = RechargeAction.getByOrdinal(ordinal);
            final Observer observer = new Observer(instance);
            NetCommonParamUtils.initNetParam(param);
            DZReadAbstract dzRead = null;
            Executor executor = null;
            int priority = -1;
            switch (action) {
                case RECHARGE:
                    dzRead = new RechargeImpl(context, param, action);
                    String state = param.get(RechargeMsgResult.ORDER_STATE);
                    if ("2".equals(state) || "3".equals(state)) {
                        priority = DzAbsRunnable.PRIORITY_BASE_LOW + 10;
                        executor = NORMAL_THREAD_POOL;
                    }
                    break;
                case PAY:
                    /**param key值定义*/
                    dzRead = new PayChapterImpl(context, param, action);
                    executor = PAY_THREAD_POOL;
                    break;
                case PAY_CHECK:
                    /**param key值定义*/
                    dzRead = new PayCheckImpl(context, param, action);
                    executor = PAY_THREAD_POOL;
                    break;
                case PACKBOOK_ORDER:
                    /**打包订购 一键购 组合购*/
                    dzRead = new DzPackOrderImpl(context, param, action);
                    executor = NORMAL_THREAD_POOL;
                    break;
                default:
                    RechargeMsgResult result = new RechargeMsgResult(param);
                    result.map.put(RechargeMsgResult.ERR_DES, "Action 未知");
                    observer.update(result);
                    executor = NORMAL_THREAD_POOL;
                    break;
            }
            final DZReadAbstract fDzRead = dzRead;
            if (fDzRead != null) {
                String descFrom = (null == param) ? null : param.get(RechargeMsgResult.DESC_FROM);
                descFrom = TextUtils.isEmpty(descFrom) ? "" : "-" + descFrom;
                final String key = getKey(param, action);
                if (!EXECUTE_CACHE.containsKey(key)) {
                    //充值才需要判断key，其余不需要判断key
                    if (action == RechargeAction.RECHARGE) {
                        EXECUTE_CACHE.put(key, action + descFrom);
                    }
                    DzAbsRunnable dzAbsRunnable = new DzAbsRunnable(action + descFrom, priority) {
                        @Override
                        public void run() {
                            fDzRead.attach(observer);
                            fDzRead.execute();
                            fDzRead.detach(observer);
                            EXECUTE_CACHE.remove(key);
                        }
                    };
                    // 有线程池，就运行在线程池，没有线程池，就运行在原来创建的县城。
                    if (null != executor) {
                        executor.execute(dzAbsRunnable);
                    } else {
                        dzAbsRunnable.run();
                    }
                }
            }
        }
    }
}
