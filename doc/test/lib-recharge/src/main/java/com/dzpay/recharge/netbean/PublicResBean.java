package com.dzpay.recharge.netbean;

import com.dzpay.recharge.bean.RechargeErrType;

import org.json.JSONObject;

import java.io.Serializable;

import hw.sdk.net.bean.HwPublicBean;

/**
 * 公共类
 * @author lizhongzhong 2015-08-18
 */
public class PublicResBean extends HwPublicBean<PublicResBean> implements Serializable {
    /**
     * 错误类型
     */
    public int errorType = RechargeErrType.SUCCESS;
    /**
     * 错误描述
     */
    public String errorDesc = null;
    /**
     * 错误
     */
    public Throwable e;


    /**
     * 服务器返回的响应信息包含错误提示
     */
    public String repMsg;

    @Override
    public PublicResBean parseJSON(JSONObject jsonObj) {
        super.parseJSON(jsonObj);
        return this;
    }

    /**
     * error
     *
     * @param errType (#RechargeErrType) 错误类型
     * @param error   错误
     * @return PublicResBean
     */
    public PublicResBean error(int errType, Throwable error) {
        this.errorType = errType;
        this.e = error;
        return this;
    }

    /**
     * error
     *
     * @param errType (#RechargeErrType) 错误类型
     * @param errDesc 错误描述
     * @return PublicResBean
     */
    public PublicResBean error(int errType, String errDesc) {
        this.errorType = errType;
        this.errorDesc = errDesc;
        return this;
    }

    /**
     * error
     *
     * @param errType (#RechargeErrType) 错误类型
     * @param errDesc 错误描述
     * @param mRepMsg 错误
     * @return PublicResBean
     */
    public PublicResBean error(int errType, String errDesc, String mRepMsg) {
        this.errorType = errType;
        this.errorDesc = errDesc;
        this.repMsg = mRepMsg;
        return this;
    }

}
