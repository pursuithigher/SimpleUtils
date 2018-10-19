
package com.dzpay.recharge.api;

import android.content.Context;

import com.dzpay.recharge.logic.DZReadManager;

import java.util.HashMap;

/**
 * 计费工具类
 *
 * @author by liz on 15/1/30.
 */
public class UtilRecharge {

    private static UtilRecharge ins = new UtilRecharge();

    public static UtilRecharge getDefault() {
        return ins;
    }

    /**
     * 执行任务
     *
     * @param context  上下文
     * @param param    参数
     * @param ordinal  action
     * @param instance 实例
     */
    public void execute(Context context, HashMap<String, String> param, int ordinal,
                        Object instance) {
        DZReadManager.execute(context, param, ordinal, instance);
    }

}
