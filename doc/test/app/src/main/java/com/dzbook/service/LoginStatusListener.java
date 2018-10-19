package com.dzbook.service;


import hw.sdk.net.bean.register.RegisterBeanInfo;

/**
 * author lizhongzhong 2018/03/02.
 * <p>
 * 登录接口回调
 */

public interface LoginStatusListener {

    /**
     * 绑定成功
     *
     * @param beanInfo beanInfo
     */
    void onBindSuccess(RegisterBeanInfo beanInfo);

    /**
     * 绑定失败
     *
     * @param msg msg
     */
    void onBindFail(String msg);
}
