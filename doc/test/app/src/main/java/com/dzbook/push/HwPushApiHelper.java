package com.dzbook.push;

import android.app.Activity;
import android.content.Context;

import com.dzbook.lib.net.DzSchedulers;
import com.dzbook.lib.utils.ALog;
import com.huawei.hms.api.ConnectionResult;
import com.huawei.hms.api.HuaweiApiClient;
import com.huawei.hms.support.api.push.HuaweiPush;

/**
 * 华为push工具类
 *
 * @author winzows 2018/4/11
 */
public class HwPushApiHelper implements HuaweiApiClient.ConnectionCallbacks, HuaweiApiClient.OnConnectionFailedListener {

    /**
     * client操作锁，避免连接使用紊乱
     */
    private static final Object API_LOCK = new Object();
    private static final String TAG = "HwPushApiHelper: ";
    private static volatile HwPushApiHelper instance;
    /**
     * HuaweiApiClient 实例
     */
    private HuaweiApiClient apiClient;

    /**
     * 获取单例
     *
     * @return HwPushApiHelper
     */
    public static HwPushApiHelper getInstance() {
        if (instance == null) {
            synchronized (HwPushApiHelper.class) {
                if (instance == null) {
                    instance = new HwPushApiHelper();
                }
            }
        }
        return instance;
    }

    /**
     * 初始化华为push
     *
     * @param context context
     */
    public void initHwApiClient(Context context) {
        synchronized (API_LOCK) {
            if (apiClient != null) {
                // 对于老的apiClient，1分钟后才丢弃，防止外面正在使用过程中这边disConnect了
                disConnectClientDelay(apiClient, 60000);
            }
            // 这种重置client，极端情况可能会出现2个client都回调结果的情况。此时可能出现rstCode=0，但是client无效。
            // 因为业务调用封装中都进行了一次重试。所以不会有问题
            apiClient = new HuaweiApiClient.Builder(context)
                    .addApi(HuaweiPush.PUSH_API)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .build();
        }
    }

    /**
     * 华为移动服务服务 需要一个Activity。。。。。
     *
     * @param activity activity
     */
    public void bindActivity(Activity activity) {
        if (apiClient != null) {
            apiClient.connect(activity);
        }
    }


    private static void disConnectClientDelay(final HuaweiApiClient clientTmp, int delay) {
        DzSchedulers.childDelay(new Runnable() {
            @Override
            public void run() {
                clientTmp.disconnect();
                ALog.dWz(TAG + " clientTmp  disconnect()");
            }
        }, delay);
    }

    /**
     * 获取hw api实例
     *
     * @return HuaweiApiClient
     */
    public HuaweiApiClient getHwApiClient() {
        return apiClient;
    }

    @Override
    public void onConnected() {
        ALog.dWz(TAG + "onConnected " + "onConnected: ");
    }


    @Override
    public void onConnectionSuspended(int cause) {
        ALog.dWz(TAG + "onConnectionSuspended " + "cause: " + cause);
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        ALog.dWz(TAG + "onConnectionFailed " + "result: " + result.getErrorCode());
    }
}
