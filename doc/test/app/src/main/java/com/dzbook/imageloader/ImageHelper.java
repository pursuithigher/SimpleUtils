package com.dzbook.imageloader;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;

import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;
import com.dzbook.GlideApp;
import com.dzbook.lib.utils.ALog;

/**
 * ImageHelper
 *
 * @author wxliao 17/5/5
 */

public class ImageHelper {
    private Context mContext;

    /**
     * 构造
     *
     * @param context context
     */
    public ImageHelper(Context context) {
        mContext = context;
    }


    /**
     * 加载图片
     *
     * @param config config
     */
    public void loadImage(final ImageConfig config) {
        try {

            RequestManager requestManager = getRequestManager(config);
            if (requestManager == null) {
                throw new RuntimeException("need call ImageConfig with() first");
            }

            RequestBuilder<Drawable> request = getDrawableRequestBuilder(config, requestManager);

            if (request == null) {
                throw new RuntimeException("need call ImageConfig setResource() first");
            }

            RequestOptions options = null;
            //            if (AppConst.isHasPicFormat525()) {
            //                options.format(DecodeFormat.PREFER_RGB_565);
            //            }

            if (config.mTransformMode != null) {
                //                options = RequestOptions.bitmapTransform(new CenterCrop());
                options = RequestOptions.bitmapTransform(config.mTransformMode.getTransform(mContext));
            }
            // 解决option复用导致的全局所有书架封面(ImageView) 的默认占位图被迫统一的问题
            if (options == null) {
                options = new RequestOptions();
            }
            if (config.getAnimMode() == AnimMode.NULL) {
                options = options.dontAnimate();
            } else {
                request = request.transition(DrawableTransitionOptions.withCrossFade());
            }
            if (config.getSkipMemoryCache()) {
                options = options.skipMemoryCache(true);
            } else {
                options = options.skipMemoryCache(false);
            }

            if (config.getSkipDiskCache()) {
                options = options.diskCacheStrategy(DiskCacheStrategy.NONE);
            } else {
                options = options.diskCacheStrategy(DiskCacheStrategy.RESOURCE);
            }

            if (config.placeHolderRes != 0) {
                options = options.placeholder(config.placeHolderRes);
            } else if (config.placeHolder != null) {
                options = options.placeholder(config.placeHolder);
            }

            if (config.mTargetImageView == null) {
                throw new RuntimeException("need call ImageConfig target() first");
            }
            if (config.requestListener != null) {
                request = request.listener(config.requestListener);
            }
            request = request.apply(options);
            request.into(config.mTargetImageView);
        } catch (Exception e) {
            ALog.printStackTrace(e);
        }

    }

    @Nullable
    private RequestBuilder<Drawable> getDrawableRequestBuilder(ImageConfig config, RequestManager requestManager) {
        RequestBuilder<Drawable> request = null;

        int resourceMode = config.getResourceMode();
        switch (resourceMode) {
            case ResourceMode.STRING:
                request = requestManager.load(config.mResourceString);
                break;
            case ResourceMode.URI:
                request = requestManager.load(config.mResourceUri);
                break;
            case ResourceMode.FILE:
                request = requestManager.load(config.mResourceFile);
                break;
            case ResourceMode.RES:
                request = requestManager.load(config.mResourceRes);
                break;
            default:
                break;
        }
        return request;
    }

    @Nullable
    private RequestManager getRequestManager(ImageConfig config) {
        RequestManager requestManager = null;
        int attachMode = config.getAttachMode();
        switch (attachMode) {
            case AttachMode.ACTIVITY:
                requestManager = GlideApp.with(config.mAttachActivity);
                break;
            case AttachMode.FRAGMENT:
                requestManager = GlideApp.with(config.mAttachFragment);
                break;
            case AttachMode.CONTEXT:
                requestManager = GlideApp.with(config.mAttachContext);
                break;
            default:
                break;
        }
        return requestManager;
    }

}
