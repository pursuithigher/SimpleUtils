package com.dzbook.utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Looper;
import android.support.annotation.MainThread;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.widget.ImageView;

import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.Transition;
import com.dzbook.GlideApp;
import com.dzbook.imageloader.AnimMode;
import com.dzbook.imageloader.DataManager;
import com.dzbook.imageloader.ImageConfig;
import com.dzbook.lib.net.DzSchedulers;
import com.dzbook.lib.utils.ALog;

import java.io.File;

/**
 * ImageLoadUtils
 *
 * @author dongdianzhou on 2017/8/17.
 */

public class GlideImageLoadUtils {
    private static volatile GlideImageLoadUtils instanse;

    private GlideImageLoadUtils() {

    }

    /**
     * 获取GlideImageLoadUtils实例
     *
     * @return 实例
     */
    public static GlideImageLoadUtils getInstanse() {
        if (instanse == null) {
            synchronized (GlideImageLoadUtils.class) {
                if (instanse == null) {
                    instanse = new GlideImageLoadUtils();
                }
            }
        }
        return instanse;
    }

    /**
     * 不加载占位图
     *
     * @param fragment  fragment
     * @param imageView imageView
     * @param url       url
     */
    public void glideImageLoadFromUrl(Fragment fragment, ImageView imageView, String url) {
        glideImageLoadFromUrl(fragment, imageView, url, -10);
    }

    /**
     * 加载默认书籍背景
     *
     * @param activity  activity
     * @param imageView imageView
     * @param url       url
     */
    public void glideImageLoadFromUrlDefaultBookResSkipMemoryCache(Activity activity, ImageView imageView, String url) {
        glideImageLoadFromUrl(activity, imageView, url, -10, true, false);
    }


    /**
     * 加载默认书籍背景
     *
     * @param context   context
     * @param imageView imageView
     * @param url       url
     */
    public void glideImageLoadFromUrlDefaultBookRes(Context context, ImageView imageView, String url) {
        glideImageLoadFromUrl((Activity) context, imageView, url, -10);
    }


    /**
     * 不加载占位图
     *
     * @param activity  activity
     * @param imageView imageView
     * @param url       url
     */
    public void glideImageLoadFromUrl(Activity activity, ImageView imageView, String url) {
        glideImageLoadFromUrl(activity, imageView, url, -10);
    }

    /**
     * 加载url类型图片:绑定fragment
     *
     * @param fragment  fragment
     * @param imageView imageView
     * @param url       url
     * @param res       res
     */
    public void glideImageLoadFromUrl(final Fragment fragment, final ImageView imageView, final String url, final int res) {
        if (isStartLoadImage(fragment.getActivity())) {
            ImageConfig config = ImageConfig.create(imageView).
                    with(fragment).
                    setResource(url).
                    setAnimMode(AnimMode.NULL);
            if (res >= 0) {
                config.setPlaceHolder(res);
            }

            DataManager.getImageHelper().loadImage(config);
        }
    }

    /**
     * 加载url类型图片:绑定fragmentactivity
     *
     * @param activity          activity
     * @param imageView         imageView
     * @param url               url
     * @param res               res
     * @param isSkipDiskCache   isSkipDiskCache
     * @param isSkipMemoryCache isSkipMemoryCache
     */
    public void glideImageLoadFromUrl(final Activity activity, final ImageView imageView, final String url, final int res, boolean isSkipMemoryCache, boolean isSkipDiskCache) {
        if (isStartLoadImage(activity)) {
            ImageConfig config = ImageConfig.create(imageView).
                    with((FragmentActivity) activity).
                    setResource(url).
                    setSkipMemoryCache(isSkipMemoryCache).
                    setSkipDiskCache(isSkipDiskCache).
                    setAnimMode(AnimMode.NULL);
            if (res >= 0) {
                config.setPlaceHolder(res);
            }
            DataManager.getImageHelper().loadImage(config);
        }
    }

    /**
     * 加载url类型图片:绑定fragmentactivity
     *
     * @param activity  activity
     * @param imageView imageView
     * @param url       url
     * @param res       res
     */
    public void glideImageLoadFromUrl(final Activity activity, final ImageView imageView, final String url, final int res) {
        glideImageLoadFromUrl(activity, imageView, url, res, false, false);
    }

    /**
     * 加载url类型图片:绑定fragmentactivity
     *
     * @param context   context
     * @param imageView imageView
     * @param url       url
     * @param res       res
     */
    @MainThread
    public void glideImageLoadFromUrl(final Context context, final ImageView imageView, final String url, final int res) {
        ImageConfig config = ImageConfig.create(imageView).
                with(context).
                setResource(url).
                setAnimMode(AnimMode.NULL);
        if (res >= 0) {
            config.setPlaceHolder(res);
        }
        DataManager.getImageHelper().loadImage(config);
    }


    /**
     * 下载图片：和activity绑定：bitmap
     *
     * @param activity              activity
     * @param url                   url
     * @param downloadImageListener downloadImageListener
     * @param isUseCanche           isUseCanche
     */
    public void downloadImageBitmapFromUrl(final Activity activity, final String url, final DownloadImageListener downloadImageListener, final boolean isUseCanche) {
        if (isStartLoadImage(activity)) {
            if (Looper.getMainLooper() == Looper.myLooper()) {
                DzSchedulers.child(new Runnable() {
                    @Override
                    public void run() {
                        downloadImageBitmapFromUrlImpl(activity, null, url, Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL, downloadImageListener, isUseCanche);
                    }
                });
            } else {
                downloadImageBitmapFromUrlImpl(activity, null, url, Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL, downloadImageListener, isUseCanche);
            }
        }
    }

    /**
     * 下载图片：和fragment绑定：bitmap
     *
     * @param fragment              fragment
     * @param url                   url
     * @param height                height
     * @param width                 width
     * @param downloadImageListener downloadImageListener
     * @param isUseCanche           isUseCanche
     */
    public void downloadImageBitmapFromUrl(final Fragment fragment, final String url, final int width, final int height, final DownloadImageListener downloadImageListener, final boolean isUseCanche) {
        if (isStartLoadImage(fragment.getActivity())) {
            if (Looper.getMainLooper() == Looper.myLooper()) {
                DzSchedulers.child(new Runnable() {
                    @Override
                    public void run() {
                        downloadImageBitmapFromUrlImpl(fragment.getActivity(), fragment, url, width, height, downloadImageListener, isUseCanche);
                    }
                });
            } else {
                downloadImageBitmapFromUrlImpl(fragment.getActivity(), fragment, url, width, height, downloadImageListener, isUseCanche);
            }
        }
    }

    /**
     * 下载bitmap图片的具体实现
     *
     * @param activity              activity
     * @param fragment              fragment
     * @param url                   url
     * @param width                 width
     * @param height                height
     * @param downloadImageListener downloadImageListener
     * @param isUseCanche           isUseCanche
     */
    private void downloadImageBitmapFromUrlImpl(Activity activity, Fragment fragment, String url, int width, int height, final DownloadImageListener downloadImageListener, boolean isUseCanche) {

        try {
            handleDownloadImage(activity, fragment, url, width, height, downloadImageListener, isUseCanche);
        } catch (Exception e) {
            ALog.printStackTrace(e);
        }

    }

    /**
     * 下载Banner位图片
     *
     * @param context               context
     * @param url                   url
     * @param width                 width
     * @param height                height
     * @param downloadImageListener downloadImageListener
     * @param isUseCanche           isUseCanche
     */
    public void loadBannerPic(Context context, String url, int width, int height, final DownloadImageListener downloadImageListener, boolean isUseCanche) {
        try {
            RequestManager requestManager = GlideApp.with(context);
            DiskCacheStrategy diskCacheStrategy = null;
            if (!isUseCanche) {
                diskCacheStrategy = DiskCacheStrategy.NONE;
            } else {
                diskCacheStrategy = DiskCacheStrategy.RESOURCE;
            }
            RequestOptions options = new RequestOptions().skipMemoryCache(!isUseCanche).diskCacheStrategy(diskCacheStrategy);
            //            if (AppConst.isHasPicFormat525()) {
            //                options=options.format(DecodeFormat.PREFER_RGB_565);
            //            }
            RequestBuilder<Bitmap> builder = requestManager.asBitmap().load(url).apply(options);
            builder.into(new SimpleTarget<Bitmap>(width, height) {
                @Override
                public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                    if (resource != null) {
                        downloadImageListener.downloadSuccess(resource);
                    } else {
                        downloadImageListener.downloadFailed();
                    }
                }
            });
        } catch (Exception e) {
            ALog.printStackTrace(e);
        }
    }

    /**
     * 下载图片：和activity绑定:指定宽高
     *
     * @param activity              activity
     * @param url                   url
     * @param height                height
     * @param width                 width
     * @param downloadImageListener downloadImageListener
     * @param isUseCanche           isUseCanche
     */
    public void downloadImageBitmapFromUrlByWidthAndHeight(final Activity activity, final String url, final int width, final int height, final DownloadImageListener downloadImageListener, final boolean isUseCanche) {
        if (isStartLoadImage(activity)) {
            if (Looper.getMainLooper() == Looper.myLooper()) {
                DzSchedulers.child(new Runnable() {
                    @Override
                    public void run() {
                        downloadImageBitmapFromUrlByWidthAndHeightImpl(activity, null, url, width, height, downloadImageListener, isUseCanche);
                    }
                });
            } else {
                downloadImageBitmapFromUrlByWidthAndHeightImpl(activity, null, url, width, height, downloadImageListener, isUseCanche);
            }
        }
    }

    /**
     * 指定宽高下载的具体实现
     *
     * @param activity              activity
     * @param fragment              fragment
     * @param url                   url
     * @param width                 width
     * @param height                height
     * @param downloadImageListener downloadImageListener
     * @param isUseCanche           isUseCanche
     */
    private void downloadImageBitmapFromUrlByWidthAndHeightImpl(Activity activity, Fragment fragment, String url, int width, int height, final DownloadImageListener downloadImageListener, boolean isUseCanche) {

        try {

            handleDownloadImage(activity, fragment, url, width, height, downloadImageListener, isUseCanche);
        } catch (Exception e) {
            ALog.printStackTrace(e);
        }


    }

    private void handleDownloadImage(Activity activity, Fragment fragment, String url, int width, int height, final DownloadImageListener downloadImageListener, boolean isUseCanche) throws InterruptedException, java.util.concurrent.ExecutionException {
        RequestManager requestManager = null;
        if (activity != null) {
            requestManager = GlideApp.with(activity);
        } else {
            requestManager = GlideApp.with(fragment);
        }
        DiskCacheStrategy diskCacheStrategy = null;
        if (!isUseCanche) {
            diskCacheStrategy = DiskCacheStrategy.NONE;
        } else {
            diskCacheStrategy = DiskCacheStrategy.RESOURCE;
        }
        RequestOptions options = new RequestOptions().skipMemoryCache(!isUseCanche).diskCacheStrategy(diskCacheStrategy);
//            if (AppConst.isHasPicFormat525()) {
//                options=options.format(DecodeFormat.PREFER_RGB_565);
//            }
        RequestBuilder<Bitmap> builder = requestManager.asBitmap().load(url).apply(options);
        final Bitmap bitmap = builder.submit(width, height).get();
        if (Looper.getMainLooper() != Looper.myLooper() && activity != null && !activity.isFinishing()) {
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (bitmap != null) {
                        downloadImageListener.downloadSuccess(bitmap);
                    } else {
                        downloadImageListener.downloadFailed();
                    }
                }
            });
        }
    }

    /**
     * 校验：当前activity不为空并没有finsh掉才会加载，不然可能出bug
     *
     * @param activity activity
     * @return boolean
     */
    private boolean isStartLoadImage(Activity activity) {
        return activity != null && !activity.isFinishing();
    }

    /**
     * 图片下载监听
     */
    public interface DownloadImageListener {
        /**
         * 下载成功
         *
         * @param resource resource
         */
        void downloadSuccess(Bitmap resource);

        /**
         * 下载成功
         *
         * @param resource resource
         */
        void downloadSuccess(File resource);

        /**
         * 下载失败
         */
        void downloadFailed();
    }
}
