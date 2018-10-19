package com.dzbook.service;

import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.Nullable;

import com.dzbook.AppConst;
import com.dzbook.AppContext;
import com.dzbook.lib.net.HttpListener;
import com.dzbook.lib.net.OkHttpDns;
import com.dzbook.lib.utils.ALog;
import com.dzbook.net.hw.HwRequestLib;
import com.dzbook.utils.ListUtils;
import com.dzbook.utils.NetworkUtils;
import com.dzbook.utils.SpUtil;

import hw.sdk.net.bean.store.BeanTempletsInfo;

/**
 * HwAdvanceStoreIntentServices
 */
public class HwAdvanceStoreIntentServices extends IntentService {

    /**
     * 启动页预访问书城数据
     */
    public static final int SPLASH_BOOK_STORE_DATA_AND_DEVICE_ACTIVITY_TYPE = 2;

    /**
     * SERVICE_TYPE
     */
    public static final String SERVICE_TYPE = "service_type";

    /**
     * HwAdvanceStoreIntentServices
     */
    public HwAdvanceStoreIntentServices() {
        super("HwAdvanceStoreIntentServices");
    }

    /**
     * 设置书城数据
     */
    private void advanceStoreData() {
        if (!NetworkUtils.getInstance().checkNet()) {
            return;
        }
        try {
            int readPref = SpUtil.getinstance(this).getPersonReadPref();
            BeanTempletsInfo beanTempletsInfo = HwRequestLib.getInstance().getStorePageDataFromNet(this, "", readPref + "", "");
            if (beanTempletsInfo != null) {
                if (beanTempletsInfo.isSuccess()) {
                    if (beanTempletsInfo.isContainChannel()) {
                        AppContext.setBeanTempletsInfo(beanTempletsInfo);
                    }
                    //保存域名 白名单
                    if (!ListUtils.isEmpty(beanTempletsInfo.whiteUrlList)) {
                        AppContext.setWhiteUrlList(beanTempletsInfo.whiteUrlList);
                    }
                }

                if (beanTempletsInfo.isTokenExpire()) {
                    SpUtil.getinstance(AppConst.getApp()).setAppToken("");
                }
            }
        } catch (Exception e) {
            ALog.printStackTrace(e);
        }
    }

    /**
     * HttpDnsListener
     */
    private static class HttpDnsListener implements HttpListener {
        @Override
        public void onDnsPrepare(String hostname, String ip) {
            ALog.eLk("HttpDnsListener onDnsPrepare hostname=" + hostname + ", ip=" + ip);
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

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        if (intent != null) {
            int type = intent.getIntExtra(SERVICE_TYPE, -1);
            if (type == SPLASH_BOOK_STORE_DATA_AND_DEVICE_ACTIVITY_TYPE) {
                advanceStoreData();
                //处理httpDns
                dealOkHttpDns();
                HwIntentService.deviceActivation(this);
                // 明确需要删除的图书，和未读的下架图书，可以在这里删除掉了。
                MarketDao.deleteSomeBook(AppConst.getApp(), null);

            }
        }
    }
}
