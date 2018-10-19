package com.dzbook.imageloader;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.annotation.DrawableRes;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.widget.ImageView;

import com.bumptech.glide.request.RequestListener;

import java.io.File;

/**
 * 图片配置信息
 *
 * @author wxliao on 17/5/5.
 */
public class ImageConfig {
    /**
     * Activity对象
     */
    public FragmentActivity mAttachActivity;
    /**
     * Fragment
     */
    public Fragment mAttachFragment;
    /**
     * Context
     */
    public Context mAttachContext;


    /**
     * ResourceString
     */
    public String mResourceString;
    /**
     * uri
     */
    public Uri mResourceUri;
    /**
     * file
     */
    public File mResourceFile;
    /**
     * res
     */
    public int mResourceRes;

    /**
     * image
     */
    public ImageView mTargetImageView;
    /**
     * Holder
     */
    public Drawable placeHolder;
    /**
     * res
     */
    public int placeHolderRes;
    /**
     * 请求监听
     */
    public RequestListener<Drawable> requestListener;
    /**
     * mTransformMode
     */
    public TransformMode mTransformMode;

    private int mAttachMode;
    private int mResourceMode;
    private int mAnimMode;
    private boolean isSkipMemoryCache = false;
    private boolean isSkipDiskCache = false;

    private ImageConfig() {

    }

    /**
     * 生成ImageConfig实例
     *
     * @param imageView imageView
     * @return config
     */
    public static ImageConfig create(ImageView imageView) {
        ImageConfig config = new ImageConfig();
        config.mTargetImageView = imageView;
        config.mAnimMode = AnimMode.CROSS_FADE;
        return config;
    }

    /**
     * with
     * @param activity activity
     * @return ImageConfig
     */
    public ImageConfig with(FragmentActivity activity) {
        mAttachMode = AttachMode.ACTIVITY;
        mAttachActivity = activity;
        return this;
    }

    /**
     * with
     * @param fragment fragment
     * @return ImageConfig
     */
    public ImageConfig with(Fragment fragment) {
        mAttachMode = AttachMode.FRAGMENT;
        mAttachFragment = fragment;
        return this;
    }

    /**
     * with
     * @param context context
     * @return ImageConfig
     */
    public ImageConfig with(Context context) {
        mAttachMode = AttachMode.CONTEXT;
        mAttachContext = context;
        return this;
    }

    /**
     * setResource
     * @param url url
     * @return ImageConfig
     */
    public ImageConfig setResource(String url) {
        mResourceMode = ResourceMode.STRING;
        mResourceString = url;
        return this;
    }

    /**
     * setResource
     * @param uri uri
     * @return ImageConfig
     */
    public ImageConfig setResource(Uri uri) {
        mResourceMode = ResourceMode.URI;
        mResourceUri = uri;
        return this;
    }

    /**
     * setRequestListener
     * @param aRequestListener requestListener
     * @return ImageConfig
     */
    public ImageConfig setRequestListener(RequestListener<Drawable> aRequestListener) {
        this.requestListener = aRequestListener;
        return this;
    }

    /**
     * setResource
     * @param file file
     * @return ImageConfig
     */
    public ImageConfig setResource(File file) {
        mResourceMode = ResourceMode.FILE;
        mResourceFile = file;
        return this;
    }

    /**
     * setResource
     * @param res res
     * @return ImageConfig
     */
    public ImageConfig setResource(@DrawableRes int res) {
        mResourceMode = ResourceMode.RES;
        mResourceRes = res;
        return this;
    }

    /**
     * setAnimMode
     * @param animMode animMode
     * @return ImageConfig
     */
    public ImageConfig setAnimMode(@AnimMode.AnimModes int animMode) {
        mAnimMode = animMode;
        return this;
    }

    /**
     * setSkipMemoryCache
     * @param isSkipCache isSkipCache
     * @return ImageConfig
     */
    public ImageConfig setSkipMemoryCache(boolean isSkipCache) {
        this.isSkipMemoryCache = isSkipCache;
        return this;
    }

    /**
     * setPlaceHolder
     * @param drawable drawable
     * @return ImageConfig
     */
    public ImageConfig setPlaceHolder(Drawable drawable) {
        this.placeHolder = drawable;
        return this;
    }

    /**
     * setPlaceHolder
     * @param res res
     * @return ImageConfig
     */
    public ImageConfig setPlaceHolder(int res) {
        placeHolderRes = res;
        return this;
    }

    public boolean getSkipMemoryCache() {
        return isSkipMemoryCache;
    }

    /**
     * setTransformMode
     * @param mode mode
     * @return ImageConfig
     */
    public ImageConfig setTransformMode(TransformMode mode) {
        mTransformMode = mode;
        return this;
    }

    //    public ImageConfig target(ImageView imageView){
    //        mTargetImageView = imageView;
    //        return this;
    //    }

    @AttachMode.AttachModes
    public int getAttachMode() {
        return mAttachMode;
    }

    @ResourceMode.ResourceModes
    public int getResourceMode() {
        return mResourceMode;
    }

    @AnimMode.AnimModes
    public int getAnimMode() {
        return mAnimMode;
    }

    //    public Drawable getPlaceHolder() {
    //        return placeHolder;
    //    }

    /**
     * setSkipDiskCache
     * @param skipDiskCache skipDiskCache
     * @return ImageConfig
     */
    public ImageConfig setSkipDiskCache(boolean skipDiskCache) {
        this.isSkipDiskCache = skipDiskCache;
        return this;
    }

    public boolean getSkipDiskCache() {
        return isSkipDiskCache;
    }
}
