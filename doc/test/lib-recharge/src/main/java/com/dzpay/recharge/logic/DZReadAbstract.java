package com.dzpay.recharge.logic;

import android.content.Context;

import com.dzpay.recharge.bean.RechargeAction;
import com.dzpay.recharge.bean.RechargeMsgResult;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 抽象类：实现登录、支付中的公共方法和接口
 *
 * @author huangyoubin
 */
public abstract class DZReadAbstract {
    protected Context context;
    protected HashMap<String, String> param;
    protected RechargeAction action = RechargeAction.NONE;
    protected String logTag;

    protected String bookId;
    protected String baseChapterId;
    protected String readAction;
    /**
     * 用来保存注册的观察者对象
     */
    protected List<Observer> observerList = new ArrayList<Observer>();

    /**
     * 构造
     *
     * @param context 上下文
     * @param param   参数
     * @param action  RechargeAction
     */
    public DZReadAbstract(Context context, HashMap<String, String> param, RechargeAction action) {
        this.context = context;
        this.param = param;
        this.action = action;
        logTag = action.name();
        if (null != param) {
            bookId = param.get(RechargeMsgResult.BOOK_ID);
            baseChapterId = param.get(RechargeMsgResult.CHAPTER_BASE_ID);
            readAction = param.get(RechargeMsgResult.READ_ACTION);
        }

    }

    /**
     * 执行
     */
    public abstract void execute();

    /**
     * 注册观察者对象
     *
     * @param observer 观察者对象
     */
    public void attach(Observer observer) {
        observerList.add(observer);
    }

    /**
     * 删除观察者对象
     *
     * @param observer 观察者对象
     */
    public void detach(Observer observer) {
        observerList.remove(observer);
    }

    /**
     * 通知所有注册的观察者对象
     *
     * @param result 数据
     */
    public void nodifyObservers(RechargeMsgResult result) {
        if (null != result && null != result.map) {
            result.map.put(RechargeMsgResult.ERR_RECORD_TAG, logTag);
        }
        for (Observer observer : observerList) {
            observer.update(result);
        }
    }

    /**
     * 获取参数
     *
     * @param key key
     * @param def 值
     * @return String
     */
    public String paramGet(String key, String def) {
        if (null == param || null == key) {
            return def;
        }
        if (param.containsKey(key)) {
            return param.get(key);
        }
        return def;
    }

}
