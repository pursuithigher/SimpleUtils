package com.dzpay.recharge.logic;

import com.dzpay.recharge.bean.RechargeAction;
import com.dzpay.recharge.bean.RechargeMsgResult;

import java.lang.reflect.Method;

/**
 * 观察者
 *
 * @author zhenglk
 */
public class Observer {
    private Object instance;

    /**
     * 构造
     *
     * @param instance 实例
     */
    public Observer(Object instance) {
        this.instance = instance;
    }

    /**
     * 更新接口
     *
     * @param result 传入状态对象，
     */
    public void update(RechargeMsgResult result) {
        try {
            if (result != null && result.getClass().getName().equals(RechargeMsgResult.class.getName())) {
                Class<?> cls = instance.getClass();
                if (!"RechargeObserver".equals(cls.getSimpleName())) {
                    cls = cls.getSuperclass();
                }
                Method method = cls.getDeclaredMethod("update", RechargeMsgResult.class);
                method.invoke(instance, result);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * RechargeAction
     *
     * @return RechargeAction
     */
    public RechargeAction getAction() {
        RechargeAction action = RechargeAction.NONE;
        try {
            Class<?> cls = instance.getClass();
            if (!"RechargeObserver".equals(cls.getSimpleName())) {
                cls = cls.getSuperclass();
            }
            Method method = cls.getDeclaredMethod("getAction");
            int ordinal = Integer.parseInt(method.invoke(instance).toString());
            action = RechargeAction.getByOrdinal(ordinal);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return action;
    }
}
