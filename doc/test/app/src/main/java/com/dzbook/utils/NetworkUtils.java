package com.dzbook.utils;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.SystemClock;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import com.dzbook.AppConst;
import com.dzbook.lib.net.DzSchedulers;
import com.dzbook.net.hw.RequestCall;

import java.io.IOException;

import static android.provider.Settings.ACTION_WIRELESS_SETTINGS;

/**
 * NetworkUtils
 *
 * @author lizhongzhong 2013-11-23
 */
public class NetworkUtils {
    /**
     * 没有网络连接
     */
    public static final int NETWORK_NONE = 0;
    /**
     * wifi连接
     */
    public static final int NETWORK_WIFI = 1;
    /**
     * 手机网络数据连接类型-2G
     */
    public static final int NETWORK_2G = 2;
    /**
     * 手机网络数据连接类型-3G
     */
    public static final int NETWORK_3G = 3;
    /**
     * 手机网络数据连接类型-4G
     */
    public static final int NETWORK_4G = 4;
    /**
     * 手机网络数据连接类型
     */
    public static final int NETWORK_MOBILE = 5;

    private static volatile NetworkUtils instanse;

    private long[] mHits = new long[2];

    private Context mContext;

    private NetworkUtils() {
    }

    /**
     * 初始化
     *
     * @param context context
     */
    public void init(Context context) {
        mContext = context;
    }


    /**
     * 获取NetworkUtils实例
     *
     * @return 实例
     */
    public static NetworkUtils getInstance() {
        if (instanse == null) {
            synchronized (NetworkUtils.class) {
                if (instanse == null) {
                    instanse = new NetworkUtils();
                }
            }
        }
        return instanse;
    }


    /**
     * 华为打点使用
     *
     * @return int
     */
    public int getNetType() {
        int networkState = getNetworkState();
        if (NETWORK_NONE == networkState) {
            return -1;

        } else if (NETWORK_WIFI == networkState) {
            return 1;

        } else {
            return 2;
        }
    }

    /**
     * 网络连接
     *
     * @return boolean
     * @author caoTong
     * @date 2012-4-13
     * @tags @param con
     * @tags @return 是否有连接
     */

    public boolean checkNet() {
        if (null == mContext) {
            return false;
        }
        int state = getNetworkState();
        return state != NETWORK_NONE;
    }

    /**
     * 检查WiFI
     *
     * @return boolea
     */
    public boolean checkWiFi() {
        int state = getNetworkState();
        return state == NETWORK_WIFI;
    }

    /**
     * 获取当前apn类型
     *
     * @return boolean
     */
    public String getAPNType() {
        if (checkNet()) {
            if (checkWiFi()) {
                return "wifi";
            } else {
                return getType();
            }
        } else {
            return "none";
        }
    }

    /**
     * 获得网络状态
     *
     * @return "cmwap"、"cmnet"
     */
    public String getType() {
        final ConnectivityManager cm = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        final NetworkInfo info = cm.getActiveNetworkInfo();
        if (info != null && info.isAvailable()) {
            return info.getExtraInfo();
        }
        return "";
    }

    /**
     * 服务器故障 弹窗提示。
     *
     * @param call   调用的接口
     * @param ignore 联网失败报出的error
     */

    public void popServerFailDialog(String call, Exception ignore) {
        if (SpUtil.getinstance(mContext).getServerFailureDialogTime() || ignore == null) {
            return;
        }
        SpUtil.getinstance(AppConst.getApp()).setRequestServerFailureDialogTime(System.currentTimeMillis());
        //
        if (!TextUtils.isEmpty(call)) {
            DzSchedulers.child(new Runnable() {
                @Override
                public void run() {
                    //                    try {
                    //                        String url = !BuildConfig.DEBUG ? "http://fault.haohuida.cn/env_release.json" : "http://fault.haohuida.cn/env_debug.json";
                    //                        String json = OkhttpUtils.getInstance().okHttpGetRequest(url, false, null);
                    //                        if (null != json) {
                    //                            final DzNetStatus dzNetStatus = new DzNetStatus().parseJSON(new JSONObject(json));
                    //                            AndroidSchedulers.mainThread().scheduleDirect(new Runnable() {
                    //                                @Override
                    //                                public void run() {
                    //                                    if (dzNetStatus != null) {
                    //                                        Activity currentActivity = DeviceInfoUtils.getInstanse().getCurrentActivity();
                    //                                        if (null != currentActivity) {
                    //                                            final DialogCommonWithButton dialog = new DialogCommonWithButton(currentActivity, DialogCommonWithButton.ERROR_NET_WORK, dzNetStatus.msg);
                    //                                            dialog.setClickListener(new DialogCommonWithButton.ClickListenerInterface() {
                    //                                                @Override
                    //                                                public void onDone() {
                    //                                                    SpUtil.getinstance(AppConst.app).setServerFailureDialogTime(System.currentTimeMillis());
                    //                                                }
                    //
                    //                                                @Override
                    //                                                public void onCancel() {
                    //
                    //                                                }
                    //                                            });
                    //                                            dialog.show();
                    //                                        }
                    //
                    //                                    }
                    //                                }
                    //                            });
                    //                        }
                    //                    } catch (Exception ignore) {
                    //                    }
                }
            });
        }
    }

    /**
     * 获取当前网络连接类型
     *
     * @return int
     */
    public int getNetworkState() {
        //获取系统的网络服务
        ConnectivityManager connManager = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);

        //如果当前没有网络
        if (null == connManager) {
            return NETWORK_NONE;
        }

        //获取当前网络类型，如果为空，返回无网络
        NetworkInfo activeNetInfo = connManager.getActiveNetworkInfo();
        if (activeNetInfo == null || !activeNetInfo.isAvailable()) {
            return NETWORK_NONE;
        }

        // 判断是不是连接的是不是wifi
        NetworkInfo wifiInfo = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if (null != wifiInfo) {
            NetworkInfo.State state = wifiInfo.getState();
            if (null != state) {
                if (state == NetworkInfo.State.CONNECTED || state == NetworkInfo.State.CONNECTING) {
                    return NETWORK_WIFI;
                }
            }
        }

        // 如果不是wifi，则判断当前连接的是运营商的哪种网络2g、3g、4g等
        NetworkInfo networkInfo = connManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

        if (null != networkInfo) {
            NetworkInfo.State state = networkInfo.getState();
            String strSubTypeName = networkInfo.getSubtypeName();
            if (null != state) {
                if (state == NetworkInfo.State.CONNECTED || state == NetworkInfo.State.CONNECTING) {
                    return getNetWorkType(activeNetInfo, strSubTypeName);
                }
            }
        }
        return NETWORK_NONE;
    }

    /**
     * 获取网络类型
     *
     * @param activeNetInfo  activeNetInfo
     * @param strSubTypeName strSubTypeName
     * @return int
     */
    private static int getNetWorkType(NetworkInfo activeNetInfo, String strSubTypeName) {
        switch (activeNetInfo.getSubtype()) {
            //如果是2g类型
            // 联通2g
            case TelephonyManager.NETWORK_TYPE_GPRS:
                // 电信2g
            case TelephonyManager.NETWORK_TYPE_CDMA:
                // 移动2g
            case TelephonyManager.NETWORK_TYPE_EDGE:
            case TelephonyManager.NETWORK_TYPE_1xRTT:
            case TelephonyManager.NETWORK_TYPE_IDEN:
                return NETWORK_2G;
            //如果是3g类型
            // 电信3g
            case TelephonyManager.NETWORK_TYPE_EVDO_A:
            case TelephonyManager.NETWORK_TYPE_UMTS:
            case TelephonyManager.NETWORK_TYPE_EVDO_0:
            case TelephonyManager.NETWORK_TYPE_HSDPA:
            case TelephonyManager.NETWORK_TYPE_HSUPA:
            case TelephonyManager.NETWORK_TYPE_HSPA:
            case TelephonyManager.NETWORK_TYPE_EVDO_B:
            case TelephonyManager.NETWORK_TYPE_EHRPD:
            case TelephonyManager.NETWORK_TYPE_HSPAP:
                return NETWORK_3G;
            //如果是4g类型
            case TelephonyManager.NETWORK_TYPE_LTE:
                return NETWORK_4G;
            default:
                //中国移动 联通 电信 三种3G制式
                if ("TD-SCDMA".equalsIgnoreCase(strSubTypeName) || "WCDMA".equalsIgnoreCase(strSubTypeName) || "CDMA2000".equalsIgnoreCase(strSubTypeName)) {
                    return NETWORK_3G;
                } else {
                    return NETWORK_MOBILE;
                }
        }
    }

    /**
     * 判断网络
     *
     * @return boolean
     */
    public boolean isNetWorkOnline() {
        Runtime runtime = Runtime.getRuntime();
        try {
            Process ipProcess = runtime.exec("ping -c 1 " + RequestCall.getDzHost());
            int exitValue = ipProcess.waitFor();
            return exitValue == 0;
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        return false;
    }

    private void setNetWork(Context context) {
        Intent intent = new Intent(Settings.ACTION_SETTINGS);
        context.startActivity(intent);
    }


    /**
     * 设置网络
     *
     * @param context context
     */
    public void setNetSetting(Context context) {
        System.arraycopy(mHits, 1, mHits, 0, mHits.length - 1);
        mHits[mHits.length - 1] = SystemClock.uptimeMillis();
        if (mHits[1] >= (mHits[0] + 500)) {
            // 打开网络设置界面
            if (Build.VERSION.SDK_INT >= 26 && DeviceUtils.getEMUIVersion() > 0) {
                try {
                    Intent intent = new Intent();
                    intent.setAction(ACTION_WIRELESS_SETTINGS);
                    intent.putExtra("use_emui_ui", true);
                    context.startActivity(intent);
                } catch (Exception e) {
                    setNetWork(context);
                }
            } else {
                setNetWork(context);
            }
        }
    }
}
