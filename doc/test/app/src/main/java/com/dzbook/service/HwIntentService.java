package com.dzbook.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.dzbook.AppConst;
import com.dzbook.lib.net.DzSchedulers;
import com.dzbook.lib.net.HttpListener;
import com.dzbook.lib.net.OkHttpDns;
import com.dzbook.lib.utils.ALog;
import com.dzbook.net.hw.HwRequestLib;
import com.dzbook.utils.DeviceInfoUtils;
import com.dzbook.utils.NetworkUtils;
import com.dzbook.utils.SpUtil;

import hw.sdk.net.bean.register.DeviceActivationBeanInfo;
import hw.sdk.net.bean.shelf.BeanBuiltInBookListInfo;

/**
 * HwIntentService
 * @author lizz 2018/5/7.
 */
public class HwIntentService extends Service {

    /**
     * 启动页内置书籍数据
     */
    public static final int SPLASH_GET_INIT_BOOK_DATA_TYPE = 1;

    /**
     * 内置书籍
     */
    public static final int BUID_IN_BOOK_DATA = 3;

    /**
     * SERVICE_TYPE
     */
    public static final String SERVICE_TYPE = "service_type";


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            int type = intent.getIntExtra(SERVICE_TYPE, -1);
            ALog.dZz("HwIntentService type: " + type);
            if (type == SPLASH_GET_INIT_BOOK_DATA_TYPE) {
                DzSchedulers.child(new Runnable() {
                    @Override
                    public void run() {
                        initBookData();
                        //处理httpDns
                        dealOkHttpDns();
                        deviceActivation(getApplicationContext());
                    }
                });
            } else if (type == BUID_IN_BOOK_DATA) {
                DzSchedulers.child(new InitBookRunnable(this, SpUtil.getinstance(this).getPersonReadPref(), 1500));
            }
        }
        return START_STICKY;
    }


    /**
     * 设备激活
     *
     * @param context 上下文
     */
    public static void deviceActivation(Context context) {
        boolean isUploadUtdId = SpUtil.getinstance(context).getBoolean(SpUtil.DZ_HW_IS_ALREAD_UPLOAD_UTDID);
        if (!isUploadUtdId) {
            try {
                String utdId = DeviceInfoUtils.getInstanse().getHwUtdId();
                if (!TextUtils.isEmpty(utdId)) {
                    DeviceActivationBeanInfo beanInfo = HwRequestLib.getInstance().launchDeviceActivationRequest(utdId);
                    if (beanInfo != null && beanInfo.isSuccess()) {
                        if (!TextUtils.isEmpty(beanInfo.city)) {
                            SpUtil.getinstance(AppConst.getApp()).setClientCity(beanInfo.city);
                        }
                        if (!TextUtils.isEmpty(beanInfo.ctime)) {
                            SpUtil.getinstance(AppConst.getApp()).setDeviceActivationTime(beanInfo.ctime);
                        }
                        if (!TextUtils.isEmpty(beanInfo.province)) {
                            SpUtil.getinstance(AppConst.getApp()).setClientProvince(beanInfo.province);
                        }
                        SpUtil.getinstance(context).setBoolean(SpUtil.DZ_HW_IS_ALREAD_UPLOAD_UTDID, true);
                    }
                }
            } catch (Exception e) {
                ALog.printStackTrace(e);
            }
        }
    }

    /**
     * 初始化内置书籍
     */
    private void initBookData() {
        boolean hasSetBooks = false;

        if (NetworkUtils.getInstance().checkNet()) {
            try {
                BeanBuiltInBookListInfo bookListInfo = HwRequestLib.getInstance().buildInBooK();

                if (bookListInfo.isSuccess() && bookListInfo.isContainData()) {
                    String history = SpUtil.getinstance(AppConst.getApp()).getShelfBookList();

                    if (BeanBuiltInBookListInfo.isHistoryValue(history)) {
                        ALog.dZz("loading页面设置内置书籍数据");
                        SpUtil.getinstance(AppConst.getApp()).setShelfBookList(bookListInfo.getBuildInBookData());
                        hasSetBooks = true;
                    }
                }

            } catch (Exception e) {
                ALog.printStackTrace(e);
            }

            if (!hasSetBooks && !BeanBuiltInBookListInfo.isAlreadyInitBook(SpUtil.getinstance(this).getShelfBookList())) {
                SpUtil.getinstance(this).setShelfBookList("-1");
            }
        } else {
            SpUtil.getinstance(this).setShelfBookList("-1");
        }
    }


    private void dealOkHttpDns() {
        OkHttpDns.init(AppConst.getApp(), new HttpDnsListener());

        SpUtil spUtil1 = SpUtil.getinstance(this);
        long resolveTime = spUtil1.getLong(SpUtil.HTTPDNS_PRE_RESOLVE_TIME, 0);
        if (System.currentTimeMillis() - resolveTime > 2 * 60 * 60 * 1000) {
            OkHttpDns.getInstance().setPreResolve();//预制域名预解析
            spUtil1.setLong(SpUtil.HTTPDNS_PRE_RESOLVE_TIME, System.currentTimeMillis());
        }
    }

    /**
     * dns预解析监听
     */
    private static class HttpDnsListener implements HttpListener {
        @Override
        public void onDnsPrepare(String hostname, String ip) {
            ALog.eLk("HttpDnsListener onDnsPrepare hostname=" + hostname + ", ip=" + ip);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        startService(new Intent(getApplicationContext(), HwIntentService.class));
    }
}
