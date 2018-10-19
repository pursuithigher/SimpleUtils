package com.dzbook.lib.net;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.alibaba.sdk.android.httpdns.HttpDns;
import com.alibaba.sdk.android.httpdns.HttpDnsService;
import com.dzbook.lib.utils.ALog;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import okhttp3.Dns;

/**
 * Created by wxliao on 17/6/27.
 *
 * @author liaowx
 */
public class OkHttpDns implements Dns {


    private static final String ACCOUNT_ID = "144522";
    private static boolean isInit;
    private static OkHttpDns instance = null;
    private static HttpListener mListener;
    private static Context mContext;

    private HttpDnsService mHttpDns;//httpdns 解析服务

    /**
     * 构造
     */
    private OkHttpDns() {
        mHttpDns = HttpDns.getService(mContext, ACCOUNT_ID);
        mHttpDns.setCachedIPEnabled(true);
    }

    /**
     * 初始化
     *
     * @param context  context
     * @param listener listener
     */

    public static void init(Context context, HttpListener listener) {
        mContext = context;
        mListener = listener;
        isInit = true;
    }

    /**
     * 单例
     *
     * @return 实例
     */
    public static OkHttpDns getInstance() {
        if (instance == null) {
            instance = new OkHttpDns();
        }
        return instance;
    }

    /**
     * 是否已经初始化了
     *
     * @return boolean
     */
    public static boolean isIsInit() {
        return isInit;
    }

    @Override
    public List<InetAddress> lookup(String hostname) throws UnknownHostException {
        Log.e("OkHttpDns", "lookup :" + hostname);
        //通过异步解析接口获取ip
        String ip = mHttpDns.getIpByHostAsync(hostname);
        ALog.dWz("通过异步解析获取->" + " hostname->" + hostname + " ip->" + ip);
        if (!TextUtils.isEmpty(ip)) {
            //如果ip不为null，直接使用该ip进行网络请求
            List<InetAddress> inetAddresses = Arrays.asList(InetAddress.getAllByName(ip));
            for (InetAddress address : inetAddresses) {
                ALog.dWz("lookup ip=" + address.getHostAddress());
            }
            if (mListener != null) {
                mListener.onDnsPrepare(hostname, ip);
            }
            return inetAddresses;
        }
        //如果返回null，走系统DNS服务解析域名
        return Dns.SYSTEM.lookup(hostname);
    }

    /**
     * 设置预解析域名列表 预解析操作为异步行为，不会阻塞启动流程
     */
    public void setPreResolve() {
        if (mHttpDns != null) {
            mHttpDns.setPreResolveHosts(new ArrayList<>(Arrays.asList("m.kuaikandushu.cn", "log.kuaikandushu.cn", "qng.kuaikandushu.cn")));
        }
    }
}
