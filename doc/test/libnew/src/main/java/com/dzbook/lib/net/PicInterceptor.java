package com.dzbook.lib.net;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * 图片拦截器
 *
 * @author liaowx
 */
public class PicInterceptor implements Interceptor {

    private static final int NORMAL = 0x00;
    private static final int TOP_IMAGE = 0x01;
    private static final int TOP_ICON = 0x02;

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();

        try {
            String url = request.url().toString();
            int type = getImageType(url);
            int sleepTime = 0;
            switch (type) {
                case TOP_IMAGE:
//                    Log.e(TAG, "大图：" + url);
                    sleepTime = 0;
                    break;
                case TOP_ICON:
//                    Log.e(TAG, "小图：" + url);
                    sleepTime = 0;
                    break;
                case NORMAL:
//                    Log.e(TAG, "普通：" + url);
                    sleepTime = 90;
                    break;
                default:
                    break;
            }

            Thread.sleep(sleepTime);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return chain.proceed(request);
    }


    private int getImageType(String url) {
        if (url == null) {
            return NORMAL;
        }
        if (url.contains("bookStoreAdvert")) {
            return TOP_IMAGE;
        } else if (url.contains("bookStoreType")) {
            return TOP_ICON;
        }
        return NORMAL;
    }
}
