package com.network;

import android.util.Pair;

import com.utils.Base64Util;
import com.views.simpleutils.BuildConfig;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.security.cert.CertificateFactory;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManagerFactory;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by QZhu on 7/19/16.
 */
public class HttpClient {

    private HttpClient(){}

    private final static OkHttpClient okHttpClient = new OkHttpClient.Builder()
            .connectTimeout(5_000, TimeUnit.MILLISECONDS)
//            .addInterceptor(new Interceptor() {
//                @Override
//                public Response intercept(Chain chain) throws IOException {
//                    Request request = chain.request();
//                    Log.i("request info:",request.headers().toString()+"\t"+request.toString()+"\t"+request.body().toString());
//
//                    Response response = chain.proceed(request);
//                    Log.i("response info:",response.headers().toString()+"\t"+response.toString()+"\t"+response.body().contentLength());
//                    return response;
//                }
//            })
            //.sslSocketFactory(setCertificates(getAssets().open("srca.cer")))      //set ssl certificate
            .build();


    private static SSLSocketFactory setCertificates(InputStream... certificates){
//        InputStream is = new Buffer().writeUtf8(certificates).inputStream();
        try {
            CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");
            KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
            keyStore.load(null);
            int index = 0;

            for (InputStream certificate : certificates) {
                String certificateAlias = Integer.toString(index);
                keyStore.setCertificateEntry(certificateAlias, certificateFactory.generateCertificate(certificate));

                try {

                } catch (Exception e) {
                    certificate.close();
                }
            }

            SSLContext sslContext = SSLContext.getInstance("TLS");
            TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            trustManagerFactory.init(keyStore);
            sslContext.init(null, trustManagerFactory.getTrustManagers(), new SecureRandom());
            return sslContext.getSocketFactory();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static HttpsResponse Post_Sync(String url, String args) throws IOException {
        //use base64
        args = BuildConfig.HTTP_B64 ? Base64Util.encode(args) : args;
        RequestBody argues = RequestBody.create(MediaType.parse("application/json"), args);
        Request request = new Request.Builder().url(url).header("charset", "UTF-8").post(argues).build();
        HttpsResponse httpsResponse = null;
        Response response = okHttpClient.newCall(request).execute();
        //use base64
        httpsResponse = BuildConfig.HTTP_B64 ? new HttpsResponse(response.code(), Base64Util.decode(response.body().string()))
                : new HttpsResponse(response.code(), response.body().string());
        return httpsResponse;
    }

    public static HttpsResponse Post_FileSync(String url , File args) throws IOException {
        RequestBody argues = RequestBody.create(MediaType.parse("image/*"),args);
        Request request = new Request.Builder().url(url).header("charset", "UTF-8").post(argues).build();
        HttpsResponse httpsResponse;
        Response response = okHttpClient.newCall(request).execute();
        //use base64
        httpsResponse = //BuildConfig.HTTP_B64 ? new HttpsResponse(response.code(),Base64Util.decode(response.body().string())) :
                new HttpsResponse(response.code(),response.body().string());
        return httpsResponse;
    }

    public static HttpsResponse Post_FileSync(String url , Map<String,Object> paramsMap) throws IOException {
        MultipartBody.Builder builder = new MultipartBody.Builder();
        //设置类型
        builder.setType(MultipartBody.FORM);
        //追加参数
        for (String key : paramsMap.keySet()) {
            Object object = paramsMap.get(key);
            if (!(object instanceof File)) {
                builder.addFormDataPart(key, object.toString());
            } else {
                File file = (File) object;
                builder.addFormDataPart(key, file.getName(), RequestBody.create(null, file));
            }
        }
        //创建RequestBody
        RequestBody body = builder.build();
        //创建Request
        final Request request = new Request.Builder().url(url).post(body).build();
        Response response = okHttpClient.newCall(request).execute();
        //use base64
        HttpsResponse httpsResponse = //BuildConfig.HTTP_B64 ? new HttpsResponse(response.code(),Base64Util.decode(response.body().string())) :
                new HttpsResponse(response.code(),response.body().string());
        return httpsResponse;
    }


    /**
     * http Get method execute immediately
     * @param url url
     * @return result [String]
     * @throws IOException
     */
    public static Response Get_Sync(String url, Pair<String,String>... heads) throws IOException{
        Request.Builder request = new Request.Builder()
                .url(url);
        for(Pair<String,String> pair:heads)
        {
            request.header(pair.first,pair.second);
        }
        return okHttpClient.newCall(request.build()).execute();
    }

    /**
     * http Get method execute immediately
     * @param url url
     * @return result [String]
     * @throws IOException
     */
    public static Response Get_Sync(String url, String param) throws IOException{
        param = BuildConfig.HTTP_B64 ? Base64Util.encode(param) : param;
        RequestBody argues = RequestBody.create(MediaType.parse("image/*"),param);
        Request.Builder request = new Request.Builder()
                .url(url).post(argues);
        return okHttpClient.newCall(request.build()).execute();
    }

//    public static InputStream Get_Sync(String url) throws IOException{
//        Request request = new Request.Builder().url(url).header("charset", "UTF-8").build();
//        Response response = okHttpClient.newCall(request).execute();
//        return response.body().byteStream();
//    }
}
