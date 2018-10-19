package com.dzbook.utils.hw;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.dzbook.event.EventBusUtils;
import com.dzbook.event.EventConstant;
import com.dzbook.lib.utils.ALog;
import com.dzbook.listener.CheckUpdateListener;
import com.dzbook.utils.NetworkUtils;
import com.dzbook.utils.PackageControlUtils;
import com.huawei.updatesdk.UpdateSdkAPI;
import com.huawei.updatesdk.service.appmgr.bean.ApkUpgradeInfo;
import com.huawei.updatesdk.service.otaupdate.CheckUpdateCallBack;

/**
 * 检查升级
 *
 * @author winzows 2018/4/19
 */

public class CheckUpdateUtils {

    /**
     * 后台更新
     */
    public static final int BACKGROUND_UPDATE = 1;
    /**
     * 主动更新
     */
    public static final int ACTIVE_UPDATE = 0;
    private static final String TAG = "CheckUpdateUtils： ";

    private static CheckUpdateListener checkUpdateListener;
    private static CheckUpdateCallBack checkUpdateCallBack = new CheckUpdateCallBack() {

        @Override
        public void onUpdateInfo(Intent intent) {
            if (intent != null) {
                int status = intent.getIntExtra("status", -99);
                int rtnCode = intent.getIntExtra("failcause", -99);
                //是否强制更新应用
                boolean isExit = intent.getBooleanExtra("compulsoryUpdateCancel", false);
                ALog.dWz(TAG, "onUpdateInfo status: " + status + ",failcause: " + rtnCode + ",isExit: " + isExit);
                ApkUpgradeInfo info = (ApkUpgradeInfo) intent.getSerializableExtra("updatesdk_update_info");
                if (info != null) {
                    int appVersionCode = 10000;
                    try {
                        appVersionCode = Integer.parseInt(PackageControlUtils.getAppVersionCode());
                    } catch (Exception ignore) {
                    }
                    if (info.getVersionCode_() > appVersionCode) {
                        Bundle bundle = new Bundle();
                        bundle.putInt(EventConstant.TYPE_CHECK_UPDATE, info.getVersionCode_());
                        EventBusUtils.sendMessage(EventConstant.CODE_CHECK_UPDATE, EventConstant.TYPE_CHECK_UPDATE, bundle);
                    }
                }
            }
            if (checkUpdateListener != null) {
                checkUpdateListener.end();
            }
        }

        @Override
        public void onMarketInstallInfo(Intent intent) {
            if (intent != null) {
                int downloadCode = intent.getIntExtra("downloadStatus", -99);
                int installState = intent.getIntExtra("installState", -99);
                int installType = intent.getIntExtra("installType", -99);

                ALog.dWz(TAG, "onMarketInstallInfo installState: " + installState + ",installType: " + installType + ",downloadCode: " + downloadCode);
            }
            if (checkUpdateListener != null) {
                checkUpdateListener.end();
            }
        }

        @Override
        public void onMarketStoreError(int responseCode) {
            ALog.eWz(TAG, "onMarketStoreError responseCode: " + responseCode);
            if (checkUpdateListener != null) {
                checkUpdateListener.end();
            }
        }

        @Override
        public void onUpdateStoreError(int responseCode) {
            ALog.eWz(TAG, "onUpdateStoreError responseCode: " + responseCode);
            if (checkUpdateListener != null) {
                checkUpdateListener.end();
            }
        }
    };

    /**
     * 检查更新
     *
     * @param context 上下文
     * @param type    type 1 后台检测更新  0  主动更新
     */
    public static void checkUpdate(final Context context, final int type) {
        checkUpdate(context, type, null);
    }

    /**
     * 检查更新
     *
     * @param context 上下文
     * @param type    type 1 后台检测更新  0  主动更新
     * @param aCheckUpdateListener 更新回调
     */
    public static void checkUpdate(final Context context, final int type, final CheckUpdateListener aCheckUpdateListener) {
        if (!NetworkUtils.getInstance().checkNet()) {
            //            ToastAlone.showLong(R.string.net_work_notuse);
            return;
        }
        checkUpdateListener = aCheckUpdateListener;
        if (checkUpdateListener != null) {
            checkUpdateListener.start();
        }
        if (type == ACTIVE_UPDATE) {
            UpdateSdkAPI.checkAppUpdate(context.getApplicationContext(), checkUpdateCallBack, true, false);
        } else {
            UpdateSdkAPI.checkClientOTAUpdate(context.getApplicationContext(), checkUpdateCallBack, false, 0, false);
        }
    }
}
