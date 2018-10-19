package com.dzbook.push;

import android.content.Context;

import com.dzbook.lib.net.DzSchedulers;
import com.dzbook.lib.utils.ALog;
import com.huawei.hms.api.HuaweiApiClient;
import com.huawei.hms.support.api.client.PendingResult;
import com.huawei.hms.support.api.client.ResultCallback;
import com.huawei.hms.support.api.client.Status;
import com.huawei.hms.support.api.entity.core.CommonCode;
import com.huawei.hms.support.api.push.GetTagResult;
import com.huawei.hms.support.api.push.HuaweiPush;
import com.huawei.hms.support.api.push.TokenResult;

/**
 * 华为push工具类
 *
 * @author winzows 2018/4/11
 */

public class HwPushHelper {
    /**
     * client 无效最大尝试次数
     */
    private static final int MAX_RETRY_TIMES = 5;
    private static volatile HwPushHelper instance;
    /**
     * 当前剩余重试次数
     */
    private int retryTimesGetToken = MAX_RETRY_TIMES;
    private int retryTimesGetTag = MAX_RETRY_TIMES;

    /**
     * 获取单例
     *
     * @return HwPushHelper
     */
    public static HwPushHelper getInstance() {
        if (instance == null) {
            synchronized (HwPushHelper.class) {
                if (instance == null) {
                    instance = new HwPushHelper();
                }
            }
        }
        instance.retryTimesGetToken = MAX_RETRY_TIMES;
        instance.retryTimesGetTag = MAX_RETRY_TIMES;
        return instance;
    }


    /**
     * 获取pushToken
     *
     * @param context context
     */
    public void getPushToken(final Context context) {
        HuaweiApiClient huaweiApiClient = HwPushApiHelper.getInstance().getHwApiClient();
        if (huaweiApiClient != null) {
            PendingResult<TokenResult> tokenResult = HuaweiPush.HuaweiPushApi.getToken(huaweiApiClient);
            tokenResult.setResultCallback(new TokenResultResultCallback(context));
        }
    }

    /**
     * 获取pushState
     */
    public void getPushState() {
        HuaweiApiClient huaweiApiClient = HwPushApiHelper.getInstance().getHwApiClient();
        if (huaweiApiClient != null) {
            HuaweiPush.HuaweiPushApi.getPushState(huaweiApiClient);
        }
    }


    /**
     * 获取pushTag
     */
    public void getPushTag() {
        HuaweiApiClient huaweiApiClient = HwPushApiHelper.getInstance().getHwApiClient();
        if (huaweiApiClient != null) {
            PendingResult<GetTagResult> tagResult = HuaweiPush.HuaweiPushApi.getTags(huaweiApiClient);
            tagResult.setResultCallback(new GetTagResultResultCallback());
        }
    }

    /**
     * 设置是否接收推送消息
     *
     * @param enable enable
     */
    public void setReceiveNotifyMsg(final boolean enable) {
        DzSchedulers.child(new Runnable() {
            @Override
            public void run() {
                HuaweiApiClient huaweiApiClient = HwPushApiHelper.getInstance().getHwApiClient();
                if (huaweiApiClient != null) {
                    HuaweiPush.HuaweiPushApi.enableReceiveNotifyMsg(huaweiApiClient, enable);
                }
            }
        });
    }

    /**
     * 处理返回token的回调 重试
     */
    private class GetTagResultResultCallback implements ResultCallback<GetTagResult> {


        @Override
        public void onResult(GetTagResult result) {
            if (result == null) {
                return;
            }

            Status status = result.getStatus();

            if (status == null) {
                return;
            }
            if (!status.isSuccess()) {
                // 需要重试的错误码，并且可以重试
                int rstCode = status.getStatusCode();
                if (retryTimesGetTag > 0 && (rstCode == CommonCode.ErrorCode.SESSION_INVALID
                        || rstCode == CommonCode.ErrorCode.CLIENT_API_INVALID)) {
                    DzSchedulers.mainDelay(new Runnable() {
                        @Override
                        public void run() {
                            getPushTag();
                        }
                    }, 2000);
                    retryTimesGetTag--;
                } else {
                    retryTimesGetTag = MAX_RETRY_TIMES;
                    ALog.dWz("getPushTag fail status " + result.getStatus());
                }
            } else {
                if (result.getTagsRes() != null) {
                    ALog.dWz("getPushTag success ");
                }
            }
        }
    }

    /**
     * 处理返回token的回调  重试
     */
    private class TokenResultResultCallback implements ResultCallback<TokenResult> {
        private final Context context;

        private TokenResultResultCallback(Context context) {
            this.context = context;
        }

        @Override
        public void onResult(TokenResult result) {
            if (result == null) {
                return;
            }

            Status status = result.getStatus();
            if (status == null) {
                return;
            }

            if (!status.isSuccess()) {
                // 需要重试的错误码，并且可以重试
                int rstCode = status.getStatusCode();
                if (retryTimesGetToken > 0 && (rstCode == CommonCode.ErrorCode.SESSION_INVALID
                        || rstCode == CommonCode.ErrorCode.CLIENT_API_INVALID)) {
                    retryTimesGetToken--;
                    DzSchedulers.mainDelay(new Runnable() {
                        @Override
                        public void run() {
                            getPushToken(context);
                        }
                    }, 2 * 1000);
                } else {
                    retryTimesGetTag = MAX_RETRY_TIMES;
                    ALog.dWz("getPushToken fail status " + result.getStatus());
                }
            } else {
                if (result.getTokenRes() != null) {
                    ALog.dWz("getPushToken success ");
                }
            }
        }
    }


}