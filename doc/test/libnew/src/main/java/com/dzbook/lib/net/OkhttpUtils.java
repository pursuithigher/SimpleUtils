package com.dzbook.lib.net;


import android.os.Looper;
import android.text.TextUtils;

import com.dzbook.lib.utils.ALog;
import com.dzbook.lib.utils.StringUtil;
import com.dzbook.lib.utils.UtilTimeOffset;

import java.net.URLEncoder;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.CertificatePinner;
import okhttp3.Dispatcher;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * OkHttp util
 *
 * @author zhenglk
 */
public class OkhttpUtils {

    private static final String TAG = "IshuguiRequest.ok";

    private static final int CONNECT_TIMEOUT = 15;
    private static final int READ_TIMEOUT = 30;
    private static final int WRITE_TIMEOUT = 10;
    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    private static volatile OkhttpUtils instance;
    private OkHttpClient mOkHttpClient = null;

    /**
     * 单例
     *
     * @return 实例
     */
    public static OkhttpUtils getInstance() {
        if (instance == null) {
            synchronized (OkhttpUtils.class) {
                if (instance == null) {
                    OkhttpUtils ins = new OkhttpUtils();
                    ins.mOkHttpClient = generateClient();
                    OkhttpUtils.instance = ins;
                }
            }
        }
        return instance;
    }

    /**
     * 准备一份OkHttpClient
     *
     * @return client
     */
    public static OkHttpClient generateClient() {
        return generateClient(false);
    }

    /**
     * 准备一份OkHttpClient
     *
     * @param interceptPic 减速模式？
     * @return client
     */
    public static OkHttpClient generateClient(boolean interceptPic) {
        ALog.dZz("switch OkhttpUtils generateClient interceptPic:" + interceptPic);

        OkHttpClient.Builder builder = new OkHttpClient.Builder()
                //设置读取超时时间
                .readTimeout(READ_TIMEOUT, TimeUnit.SECONDS)
                //设置写的超时时间
                .writeTimeout(WRITE_TIMEOUT, TimeUnit.SECONDS)
                //设置连接超时时间
                .connectTimeout(CONNECT_TIMEOUT, TimeUnit.SECONDS);
        if (OkHttpDns.isIsInit()) {
            builder.dns(OkHttpDns.getInstance());
        }
        builder.certificatePinner(getFixPinner());


        if (interceptPic) {
            Dispatcher dispatcher = new Dispatcher();
            dispatcher.setMaxRequests(1);
            dispatcher.setMaxRequestsPerHost(1);
            builder.dispatcher(dispatcher);
            builder.addInterceptor(new PicInterceptor());
        }

        return builder.build();
    }

    public static CertificatePinner getFixPinner() {
        return new CertificatePinner.Builder()
//                .add("*.kuaikandushu.cn", "sha256/AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA=")
                .add("*.kuaikandushu.cn", "sha256/nC7/KGBFBYOQeYO29s9nAMFUzoj5xqQ4G+aHkFPDacw=")
                .add("*.kuaikandushu.cn", "sha256/nKWcsYrc+y5I8vLf1VGByjbt+Hnasjl+9h8lNKJytoE=")
                .add("*.kuaikandushu.cn", "sha256/r/mIkG3eEpVdm+u/ko/cwxzOMo1bk4TyHIlByibiA5E=")
                .build();
    }


    /**
     * 是否是urlEncode
     *
     * @param urlBasic    urlBasic
     * @param paramsMap   参数
     * @param isUrlEncode 是否是urlEncode
     * @return str
     * @throws Exception e
     */
    public String okHttpRequestGet(String urlBasic, HashMap<String, String> paramsMap, boolean isUrlEncode) throws Exception {
        StringBuilder tempParams = new StringBuilder();
        //处理参数
        int pos = 0;
        for (Map.Entry<String, String> entry : paramsMap.entrySet()) {
            if (pos > 0) {
                tempParams.append("&");
            }
            //对参数进行URLEncoder
            tempParams.append(String.format("%s=%s", entry.getKey(), URLEncoder.encode(entry.getValue(), "utf-8")));
            pos++;
        }
        //补全请求地址
        String requestUrl = String.format("%s?%s", urlBasic, tempParams.toString());
        //创建一个请求
        Request.Builder b = new Request.Builder();
        b.url(urlBasic);
        if (isUrlEncode) {
            b.headers(getHeaders(getCommonHeader()));
        }
        b.get().url(requestUrl);
        Request request = b.build();
        if (ALog.getDebugMode()) {
            ALog.i(TAG, "requestUrl = " + requestUrl);
        }
        Response response = mOkHttpClient.newCall(request).execute();

        ResponseBody responseBody = response.body();
        if (responseBody != null) {
            return responseBody.string();
        }

        return "";
    }


    /**
     * okhttp req
     *
     * @param urlBasic urlBasic
     * @param headMap  headMap
     * @param json     json
     * @return str
     * @throws Exception e
     */
    public String okHttpRequest(String urlBasic, Map<String, String> headMap, String json) throws Exception {
        if (ALog.getDebugMode()) {
            ALog.i(TAG, "urlBasic = " + urlBasic + " json=" + json + " headMap=" + headMap.toString());
        }

        if (Looper.getMainLooper() == Looper.myLooper()) {
            ALog.e(TAG, "error!! NetWorkOnMainThread!!! \n" + new ALog.LogThrowable().getStackTraceStr(1, 10));
        }

        Request.Builder b = new Request.Builder();
        b.headers(getHeaders(headMap));
        b.url(urlBasic);

        RequestBody body;
        if (TextUtils.isEmpty(json)) {
            json = "";
        }
        body = RequestBody.create(JSON, json);
        b.post(body);

        Request request = b.build();
        Response response = mOkHttpClient.newCall(request).execute();
        String date = response.header("date");
        if (!TextUtils.isEmpty(date)) {
            long timeLocal = System.currentTimeMillis();
            long timeServer = Date.parse(date);
            UtilTimeOffset.setOffset(timeServer - timeLocal);
        }

        ResponseBody responseBody = response.body();
        if (responseBody != null) {
            return responseBody.string();
        }

        return "";
    }


    /**
     * 设置请求头
     *
     * @param key
     * @param value
     * @return
     */
    private Headers setHeaders(String key, String value) {
        Headers.Builder headerBuilder = new Headers.Builder();
        if (!StringUtil.isEmpty(key, value)) {
            headerBuilder.add(key, value);
            return headerBuilder.build();
        }
        return null;
    }

    private Headers getHeaders(Map<String, String> headMap) {
        Headers.Builder headerBuilder = new Headers.Builder();
        for (Map.Entry<String, String> entry : headMap.entrySet()) {
            if (entry != null) {
                headerBuilder.add(entry.getKey(), entry.getValue());
            }
        }
        return headerBuilder.build();
    }

    private Map<String, String> getCommonHeader() {
        Map<String, String> map = new HashMap<String, String>(16);
        map.put("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
        map.put("Accept", "application/json");
        return map;
    }
}
