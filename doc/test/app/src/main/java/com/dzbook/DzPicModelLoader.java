package com.dzbook;

import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.bumptech.glide.load.Options;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.model.ModelLoader;
import com.bumptech.glide.load.model.ModelLoaderFactory;
import com.bumptech.glide.load.model.MultiModelLoaderFactory;
import com.bumptech.glide.load.model.stream.BaseGlideUrlLoader;
import com.dzbook.lib.utils.ALog;

import java.io.InputStream;
import java.util.Locale;

/**
 * 图片加载
 *
 * @author wxliao
 */
public class DzPicModelLoader extends BaseGlideUrlLoader<String> {

    private static final String START_WITH_SCHEME1 = "https://qn";
    private static final String START_WITH_SCHEME2 = "http://qn";

    DzPicModelLoader(ModelLoader<GlideUrl, InputStream> concreteLoader) {
        super(concreteLoader);
    }


    @Override
    protected String getUrl(String url, int width, int height, Options options) {
        if (ALog.getDebugMode()) {
            ALog.dZz("Glide DzPicModelLoader origin url:" + url);
        }
        if (isNeedAppendUrlClipping(url)) {
            int iKeyL = url.indexOf('?');

            float scaleRate = 1;
//            if (HwSdkAppConstant.isAbKey()) {
//                scaleRate = 0.8f;
//            }

            int scaleWidth = (int) (scaleRate * width);
            int scaleHeight = (int) (scaleRate * height);
            if (iKeyL == -1) {
                url = String.format(Locale.getDefault(), "%s?imageView2/1/w/%d/h/%d/ignore-error/1", url, scaleWidth, scaleHeight);
            } else {
                url = String.format(Locale.getDefault(), "%s&imageView2/1/w/%d/h/%d/ignore-error/1", url, scaleWidth, scaleHeight);
            }
            if (ALog.getDebugMode()) {
                ALog.dZz("Glide DzPicModelLoader real url:" + url);
            }
            return url;
        }
        return url;
    }

    @Override
    public boolean handles(@NonNull String s) {
        return true;
    }

    /**
     * 是否需要拼接Cdn裁剪功能
     *
     * @return
     */
    private boolean isNeedAppendUrlClipping(String url) {
        return !TextUtils.isEmpty(url) && (url.startsWith(START_WITH_SCHEME1) || url.startsWith(START_WITH_SCHEME2));
    }

    /**
     * 工厂类
     */
    public static final class Factory implements ModelLoaderFactory<String, InputStream> {
        @Override
        public ModelLoader<String, InputStream> build(MultiModelLoaderFactory multiFactory) {
            return new DzPicModelLoader(multiFactory.build(GlideUrl.class, InputStream.class));
        }

        @Override
        public void teardown() {
            // Do nothing.
        }
    }
}
