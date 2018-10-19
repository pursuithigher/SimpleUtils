package com.dzbook.utils;

import android.content.Context;
import android.os.Looper;
import android.text.TextUtils;

import com.dzbook.AppConst;
import com.dzbook.database.bean.BookInfo;
import com.dzbook.event.EventBusUtils;
import com.dzbook.event.EventConstant;
import com.dzbook.lib.net.DzSchedulers;
import com.dzbook.lib.utils.ALog;
import com.dzbook.log.DzLog;
import com.dzbook.log.LogConstants;
import com.dzbook.net.hw.HwRequestLib;
import com.dzbook.service.SyncBookMarkService;
import com.dzbook.utils.hw.TmsUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import hw.sdk.net.bean.OldUserFlagBean;
import hw.sdk.net.bean.register.RegisterBeanInfo;
import hw.sdk.net.bean.register.UserInfoBean;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * 用户信息工具类
 *
 * @author lizz 2018/4/19.
 */

public class UserInfoUtils {

    /**
     * 设置用户信息
     *
     * @param context  context
     * @param userInfo userInfo
     * @return
     */
    public static void setUserInfo(final Context context, final UserInfoBean userInfo) {
        if (Looper.getMainLooper() == Looper.myLooper()) {
            DzSchedulers.child(new Runnable() {
                @Override
                public void run() {
                    setUserInfoInner(context, userInfo);
                }
            });
        } else {
            setUserInfoInner(context, userInfo);
        }
    }

    /**
     * 必须在子线程中调用
     *
     * @param context  context
     * @param userInfo userInfo
     */
    private static void setUserInfoInner(final Context context, final UserInfoBean userInfo) {
        Context mContext = context.getApplicationContext();
        SpUtil spUtil = SpUtil.getinstance(mContext);
        if (!TextUtils.isEmpty(userInfo.remain) && !TextUtils.isEmpty(userInfo.priceUnit)) {
            //看点金额+单位
            spUtil.setUserRemain(userInfo.remain, userInfo.priceUnit);
        }
        if (!TextUtils.isEmpty(userInfo.vouchers) && !TextUtils.isEmpty(userInfo.vUnit)) {
            //代金券金额+单位
            spUtil.setUserVouchers(userInfo.vouchers, userInfo.vUnit);
        }
        if (userInfo.isVip != -1) {
            //是否vip
            spUtil.setInt(SpUtil.DZ_IS_VIP, userInfo.isVip);
        }
        if (!TextUtils.isEmpty(userInfo.deadLine)) {
            //vip过期时间
            spUtil.setString(SpUtil.DZ_VIP_EXPIRED_TIME, userInfo.deadLine);
        }
    }

    /**
     * 子线程中调用
     *
     * @param context     context
     * @param hwUid       hwUid
     * @param beanInfo    beanInfo
     * @param coverWap    coverWap
     * @param nickName    nickName
     * @param accessToken accessToken
     */
    public static void setLoginSuccessUserInfo(final Context context, final String hwUid, final RegisterBeanInfo beanInfo, final String coverWap, final String nickName, final String accessToken) {
        if (Looper.getMainLooper() == Looper.myLooper()) {
            throw new RuntimeException("请在子线程中调用");
        }
        ALog.dWz("setLoginSuccessUserInfo " + beanInfo);
        Context mContext = context.getApplicationContext();
        final SpUtil spUtil = SpUtil.getinstance(mContext);
        spUtil.setAppToken(beanInfo.token);
        if (!TextUtils.isEmpty(beanInfo.userId)) {
            spUtil.setUserID(beanInfo.userId);
        }

        if (!TextUtils.isEmpty(coverWap)) {
            spUtil.setLoginUserCoverWapByUserId(coverWap);
        } else {
            spUtil.setLoginUserCoverWapByUserId("");
        }
        if (!TextUtils.isEmpty(nickName)) {
            spUtil.setLoginUserNickNameByUserId(nickName);
        }

        if (beanInfo.getUserInfoBean() != null) {
            setUserInfo(context, beanInfo.getUserInfoBean());
        }

        spUtil.setAccountLoginStatus(true);

        //同步书架的签到和书籍更新信息
        EventBusUtils.sendMessage(EventConstant.LOGIN_SUCCESS_UPDATE_SHELF, EventConstant.TYPE_MAINSHELFFRAGMENT, null);

        //同步远端云书架数据（仅作存储）
        cloudShelfLoginSync(context, beanInfo.userId);

        //同步书签笔记
        SyncBookMarkService.launch(context, true);

        //tms查询
        TmsUtils.getInstance().queryAgreement(context.getApplicationContext(), accessToken, beanInfo.userId);

        //是否存在老用户资产的相关处理
        oldUserHasAssets(hwUid);
    }

    /**
     * 如果用户存在资产，则返回阅读H5页面
     *
     * @param uid 用户id
     */
    private static void oldUserHasAssets(final String uid) {
        if (TextUtils.isEmpty(uid)) {
            return;
        }
        DzSchedulers.child(new Runnable() {
            @Override
            public void run() {

                try {
                    OldUserFlagBean bean = HwRequestLib.getInstance().getOldUserFlagBean(uid);
                    if (bean != null && bean.isSuccess()) {

                        boolean isNeedSendEvent = false;
                        HashMap<String, String> map = new HashMap<String, String>();

                        if (bean.isSqHasAssets() && !TextUtils.isEmpty(bean.sQH5Url)) {
                            SpUtil.getinstance(AppConst.getApp()).setString(SpUtil.DZ_OLD_USER_ASSERT_SHU_QI_H5_URL, bean.sQH5Url);
                            isNeedSendEvent = true;
                            map.put(LogConstants.KEY_TYPE, "1");
                        } else {
                            SpUtil.getinstance(AppConst.getApp()).setString(SpUtil.DZ_OLD_USER_ASSERT_SHU_QI_H5_URL, "");
                        }

                        if (bean.isZyHasAssets() && !TextUtils.isEmpty(bean.zyH5Url)) {
                            SpUtil.getinstance(AppConst.getApp()).setString(SpUtil.DZ_OLD_USER_ASSERT_ZHANG_YUE_H5_URL, bean.zyH5Url);
                            isNeedSendEvent = true;
                            map.put(LogConstants.KEY_TYPE, "2");
                        } else {
                            SpUtil.getinstance(AppConst.getApp()).setString(SpUtil.DZ_OLD_USER_ASSERT_ZHANG_YUE_H5_URL, "");
                        }

                        if (isNeedSendEvent) {
                            SpUtil.getinstance(AppConst.getApp()).setBoolean(SpUtil.DZ_IS_OLD_USER_ASSERT_NEED_SHOW, true);
                            EventBusUtils.sendStickyMessage(EventConstant.CODE_PERSON_CENTER_SHOW_USER_ASSERT_DIALOG, "", null);
                            DzLog.getInstance().logEvent(LogConstants.EVENT_QNR, map, null);
                        }
                    }

                } catch (Exception e) {
                    ALog.printExceptionWz(e);
                }
                EventBusUtils.sendMessage(EventConstant.LOGIN_SUCCESS_UPDATE_USER_VIEW, EventConstant.TYPE_MAINSHELFFRAGMENT, null);
            }
        });

    }

    /**
     * 登录后同步云书架
     *
     * @param context context
     * @param userId  userId
     */
    private static void cloudShelfLoginSync(final Context context, final String userId) {
        String syncJson = SpUtil.getinstance(context).getString(SpUtil.SYNCH_CLOUD_BOOKS_JSON + userId);
        if (TextUtils.isEmpty(syncJson)) {
            Observable.create(new ObservableOnSubscribe<String>() {
                @Override
                public void subscribe(ObservableEmitter<String> e) throws Exception {
                    ArrayList<BookInfo> list = DBUtils.findAllBooks(context);
                    JSONObject jsonObject = null;
                    if (list != null && list.size() > 0) {
                        JSONArray array = new JSONArray();
                        for (BookInfo bookInfo : list) {
                            array.put(bookInfo.bookid);
                        }
                        jsonObject = new JSONObject();
                        jsonObject.put("bookIds", array);
                    }
                    String cloudShelfLoginSyncInfo = HwRequestLib.getInstance().cloudShelfLoginSync(jsonObject == null ? "" : jsonObject.toString());
                    if (!TextUtils.isEmpty(cloudShelfLoginSyncInfo)) {
                        SpUtil.getinstance(context).setString(SpUtil.SYNCH_CLOUD_BOOKS_JSON + userId, cloudShelfLoginSyncInfo);
                        e.onNext(cloudShelfLoginSyncInfo);
                    }
                }
            }).subscribeOn(Schedulers.io()).
                    observeOn(AndroidSchedulers.mainThread()).
                    subscribe(new Consumer<String>() {
                        @Override
                        public void accept(String beanCloudShelfLoginSyncInfo) {
                            if (!TextUtils.isEmpty(beanCloudShelfLoginSyncInfo)) {
                                EventBusUtils.sendMessage(EventConstant.LOGIN_SUCCESS_UPDATE_CLOUDSHELF_SYNC, EventConstant.TYPE_MAINSHELFFRAGMENT, null);
                            }
                        }
                    });
        }
    }
}
