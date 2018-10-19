package com.dzbook;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.bumptech.glide.Glide;
import com.bumptech.glide.GlideBuilder;
import com.bumptech.glide.Registry;
import com.bumptech.glide.annotation.GlideModule;
import com.bumptech.glide.load.engine.cache.DiskCache;
import com.bumptech.glide.load.engine.cache.DiskLruCacheWrapper;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.module.AppGlideModule;
import com.dzbook.imageloader.OkHttpUrlLoader;
import com.dzbook.lib.utils.ALog;

import java.io.InputStream;

import hw.sdk.HwSdkAppConstant;


/**
 * DzGlideModule
 *
 * @author dongdianzhou 2018/2/7
 */
@GlideModule
public final class DzGlideModule extends AppGlideModule {

    @Override
    public void applyOptions(@NonNull Context context, @NonNull GlideBuilder builder) {

//        MemorySizeCalculator calculator = new MemorySizeCalculator.Builder(context)
//                .setMemoryCacheScreens(1)
//                .setBitmapPoolScreens(2)
//                .build();
        if (builder != null) {
//            builder.setMemoryCache(new LruResourceCache(calculator.getMemoryCacheSize()));
            builder.setLogLevel(Log.ERROR);

            builder.setDiskCache(new DiskCache.Factory() {
                @Nullable
                @Override
                public DiskCache build() {
                    return new DzDiskCache().build();
                }
            });
        }

//        ALog.eZz("Glide"
//                + ", Calculated memory cache size: "
//                + Formatter.formatFileSize(context, calculator.getMemoryCacheSize())
//                + ", pool size: "
//                + Formatter.formatFileSize(context, calculator.getBitmapPoolSize())
//                + ", byte array size: "
//                + Formatter.formatFileSize(context, calculator.getArrayPoolSizeInBytes()));
    }

    @Override
    public void registerComponents(@NonNull Context context, @NonNull Glide glide, @NonNull Registry registry) {
        if (registry != null) {
            ALog.dZz("switch DzGlideModule registerComponents");
            if (HwSdkAppConstant.isAbKey()) {
                ALog.dZz("switch DzGlideModule registerComponents registry.replace OkHttpUrlLoader");
                registry.replace(GlideUrl.class, InputStream.class, new OkHttpUrlLoader.Factory());
            }
            registry.prepend(String.class, InputStream.class, new DzPicModelLoader.Factory());
        }
    }

    @Override
    public boolean isManifestParsingEnabled() {
        return false;
    }

    /**
     * 清缓存工厂类
     */
    public static class DzDiskCache implements DiskCache.Factory {
        private int diskCacheSizeBytes = 1024 * 1024 * 100;

        private DzDiskCache() {
        }

        @Nullable
        @Override
        public DiskCache build() {
            return DiskLruCacheWrapper.create(AppConst.getGlideCacheFile(), diskCacheSizeBytes);
        }
    }

}

