package com.dzbook.pay;

import java.io.Serializable;

import hw.sdk.net.bean.BeanBookInfo;

/**
 * LoadBookListener
 * @author caimantang on 2018/1/15.
 */

public interface LoadBookListener extends Serializable {
    /**
     * success
     * @param message      提示信息
     * @param beanBookInfo 图书包信息
     * @param status status
     */
    void success(String status, String message, BeanBookInfo beanBookInfo);

    /**
     * fail
     * @param message 提示信息
     * @param status status
     */
    void fail(String status, String message);

}
