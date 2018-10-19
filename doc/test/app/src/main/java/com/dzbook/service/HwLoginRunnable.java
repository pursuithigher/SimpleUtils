package com.dzbook.service;

import android.text.TextUtils;

import com.dzbook.AppConst;
import com.dzbook.lib.net.DzSchedulers;
import com.dzbook.lib.utils.ALog;
import com.dzbook.net.hw.HwRequestLib;
import com.dzbook.utils.DeviceInfoUtils;
import com.dzbook.utils.SpUtil;

import hw.sdk.net.bean.register.RegisterBeanInfo;
import hw.sdk.net.bean.register.UserInfoBean;

/**
 * 华为登录 Runnable
 *
 * @author lizz 2018/04/13.
 */
public class HwLoginRunnable implements Runnable {

    private static final int RETRY_COUNT = 2;

    private String openId;

    private String hwUid;

    private String accessToken;

    private String coverWap;

    private String nickName;

    private LoginStatusListener loginStatusListener;

    /**
     * HwLoginRunnable
     * @param openId openId
     * @param hwUid hwUid
     * @param accessToken accessToken
     * @param coverWap coverWap
     * @param nickName nickName
     * @param loginStatusListener loginStatusListener
     */
    public HwLoginRunnable(String openId, String hwUid, String accessToken, String coverWap, String nickName, LoginStatusListener loginStatusListener) {
        this.openId = openId;
        this.hwUid = hwUid;
        this.accessToken = accessToken;
        this.coverWap = coverWap;
        this.nickName = nickName;
        this.loginStatusListener = loginStatusListener;
    }

    @Override
    public void run() {
        if (!SpUtil.getinstance(AppConst.getApp()).getSignAgreement()) {
            ALog.eLk("HwLoginRunnable 未签署协议，不允许登录");
            loginFail("");
            return;
        }
        try {
            RegisterBeanInfo beanInfo = null;

            for (int i = 0; i < RETRY_COUNT; i++) {
                String utdId = DeviceInfoUtils.getInstanse().getHwUtdId();
                beanInfo = HwRequestLib.getInstance().launchRegisterRequest(hwUid, accessToken, openId, coverWap, nickName, utdId);
                if (beanInfo != null && beanInfo.isSuccess()) {
                    break;
                }
            }
            if (beanInfo != null && beanInfo.isSuccess()) {
                UserInfoBean userInfoBean = beanInfo.getUserInfoBean();
                //阅读时长
                if (null != userInfoBean) {
                    SpUtil.getinstance(AppConst.getApp()).setShowReaderTime(userInfoBean.readingTime);
                }
                if (null != userInfoBean && !TextUtils.isEmpty(userInfoBean.ctime)) {
                    SpUtil.getinstance(AppConst.getApp()).setRegistTime(userInfoBean.ctime);
                }
                loginSuccess(beanInfo);
            } else {
                loginFail("登录失败，请稍后重试");
            }
        } catch (Exception e) {
            ALog.printStackTrace(e);
            loginFail("");
        }
    }


    private void loginFail(final String message) {
        DzSchedulers.main(new Runnable() {
            @Override
            public void run() {
                if (loginStatusListener != null) {
                    loginStatusListener.onBindFail(message);
                }
            }
        });
    }

    private void loginSuccess(final RegisterBeanInfo beanInfo) {
        DzSchedulers.main(new Runnable() {
            @Override
            public void run() {
                if (loginStatusListener != null) {
                    loginStatusListener.onBindSuccess(beanInfo);
                }
            }
        });
    }
}
