package com.dzbook.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.dzbook.AppConst;
import com.dzbook.activity.ShareActivity;
import com.dzbook.bean.ShareBeanInfo;
import com.dzbook.event.EventBusUtils;
import com.dzbook.event.EventConstant;
import com.dzbook.lib.net.DzSchedulers;
import com.dzbook.lib.utils.ALog;
import com.dzbook.lib.utils.StringUtil;
import com.dzbook.mvp.DialogConstants;
import com.dzbook.net.hw.HwRequestLib;
import com.dzpay.recharge.utils.PayLog;
import com.ishugui.R;
import com.iss.app.BaseActivity;
import com.iss.view.common.ToastAlone;
import com.sina.weibo.sdk.WbSdk;
import com.sina.weibo.sdk.api.ImageObject;
import com.sina.weibo.sdk.api.TextObject;
import com.sina.weibo.sdk.api.WeiboMultiMessage;
import com.sina.weibo.sdk.auth.AuthInfo;
import com.sina.weibo.sdk.share.WbShareCallback;
import com.sina.weibo.sdk.share.WbShareHandler;
import com.tencent.connect.share.QQShare;
import com.tencent.connect.share.QzonePublish;
import com.tencent.connect.share.QzoneShare;
import com.tencent.mm.opensdk.constants.Build;
import com.tencent.mm.opensdk.modelmsg.SendMessageToWX;
import com.tencent.mm.opensdk.modelmsg.WXImageObject;
import com.tencent.mm.opensdk.modelmsg.WXMediaMessage;
import com.tencent.mm.opensdk.modelmsg.WXWebpageObject;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import hw.sdk.net.bean.FastOpenBook;

/**
 * 以防以后加入 其它的分享
 * 分享Utils
 *
 * @author lizhongzhong 2016/11/23.
 */

public class ShareUtils {

    /**
     * 分享类型--QQ
     */
    public static final int QQ_HY = 1;
    /**
     * 分享类型--QQ空间
     */
    public static final int QQ_KJ = 2;
    /**
     * 分享类型--微信
     */
    public static final int WX_HY = 3;
    /**
     * 分享类型--微信朋友圈
     */
    public static final int WX_PYQ = 4;
    /**
     * 分享类型--微博
     */
    public static final int WEI_BO = 5;

    /**
     * 图书详情页面 点击分享按钮
     */
    public static final int DIALOG_SHOW_FROM_BOOK_DETAIL = 1;

    /**
     * js调用java代码调用分享 签到
     */
    public static final int DIALOG_SHOW_FROM_SIGN_SHARE = DIALOG_SHOW_FROM_BOOK_DETAIL + 1;
    /**
     * 阅读分享
     */
    public static final int DIALOG_SHOW_FROM_READER_SHARE = DIALOG_SHOW_FROM_SIGN_SHARE + 1;

    /**
     * 微信分享网页到朋友圈
     */
    public static final int SHARE_TO_WEB = 1;

    /**
     * 微信分享图片到朋友圈
     */
    public static final int SHARE_TO_IMG = SHARE_TO_WEB + 1;

    /**
     * 分享到朋友圈
     */
    public static final int SHARE_TO_FRIEND_CIRCLE = SHARE_TO_IMG + 1;

    /**
     * 分享到好友
     */
    public static final int SHARE_TO_FRIENDS = SHARE_TO_FRIEND_CIRCLE + 1;

    private static final HashMap<String, String> QQ_SHARE_MAP = new HashMap<String, String>();

    private static final String WEI_BO_APP_KEY = "1630884126";
    private static final String WX_APPID = "wxedad010f7004e1ae";
    private static final String QQ_APPID = "101366226";
    /**********************替身模式**************************/

    private static final String MSG_WXSDK_VN = "sdkversion";
    private static final String MSG_WXSHARE_STATUS = "wx_status";
    private static final String MSG_WXSHARE_INTENT = "wx_intent";
    private static final String MSG_WXSHARE_BUNDLE = "wx_bundle";
    private static final String MSG_WXSHARE_STYLE = "wx_style";
    private static final int MSG_SHARE_REPLACE = 0x0014;
    private static final int MSG_SHARE_NULL = 0x0015;
    private static final int MSG_SHARE_SDK = 0x0016;

    private static final String[] STRINGS = {"com.tencent.mobileqq", "com.tencent.mtt", "com.ss.android.article.news", "com.sina.weibo", "com.baidu.searchbox", "com.UCMobile", "com.UCMobile.ac", "com.UCMobile.dev", "com.UCMobile.x86", "com.UCMobile.love", "com.qiyi.video", "com.tencent.android.qqdownloader", "com.snda.wifilocating", "com.xunmeng.pinduoduo"};

    private static final String[] B = {"wxf0a80d0ac2e82aa7", "wx64f9cf5b17af074d", "wx50d801314d9eb858", "wx299208e619de7026", "wx27a43222a6bf2931", "wx020a535dccd46c11", "wx9e7e2766ee2d0eee", "wx2ace6041e8919680", "wx8781aa7b0facd259", "wxd6415d454a022e1e", "wx2fab8a9063c8c6d0", "wx3909f6add1206543", "wx13f22259f9bbd047", "wx77d53b84434b9d9a"};

    private static byte[] sumByte = null;

    private static WbShareCallback wbShareCallback = new WbShareCallback() {

        @Override
        public void onWbShareSuccess() {
            Bundle bundle = new Bundle();
            bundle.putInt("type", ShareUtils.WEI_BO);
            EventBusUtils.sendMessage(EventConstant.SHARE_SUCCESS, "", bundle);

        }

        @Override
        public void onWbShareCancel() {
            Bundle bundle = new Bundle();
            bundle.putInt("type", ShareUtils.WEI_BO);
            EventBusUtils.sendMessage(EventConstant.SHARE_CANCEL, "", bundle);

        }

        @Override
        public void onWbShareFail() {
            Bundle bundle = new Bundle();
            bundle.putInt("type", ShareUtils.WEI_BO);
            EventBusUtils.sendMessage(EventConstant.SHARE_FAIL, "", bundle);

        }
    };
    private static IUiListener qqShareListener = new IUiListener() {
        @Override
        public void onCancel() {
            Bundle bundle = new Bundle();
            bundle.putInt("type", ShareUtils.QQ_HY);
            EventBusUtils.sendMessage(EventConstant.SHARE_CANCEL, "", bundle);
        }

        @Override
        public void onComplete(Object response) {
            Bundle bundle = new Bundle();
            bundle.putInt("type", ShareUtils.QQ_HY);
            EventBusUtils.sendMessage(EventConstant.SHARE_SUCCESS, "", bundle);
        }

        @Override
        public void onError(UiError uiError) {
            Bundle bundle = new Bundle();
            bundle.putInt("type", ShareUtils.QQ_HY);
            EventBusUtils.sendMessage(EventConstant.SHARE_FAIL, "", bundle);
        }
    };

    public static WbShareCallback getWbShareCallback() {
        return wbShareCallback;
    }

    /**
     * 通过包名，得到微信类型 appId
     *
     * @param pakageName 包名
     * @return 类型 String
     */
    private static String getWechateAppId(String pakageName) {
        return WX_APPID;
    }

    private static IWXAPI createWXAPI() {
        IWXAPI api = null;
        String wechateShareAppId = getWechateShareAppId();
        if (!TextUtils.isEmpty(wechateShareAppId)) {
            api = WXAPIFactory.createWXAPI(AppConst.getApp(), wechateShareAppId);
            // 将该app注册到微信
            api.registerApp(wechateShareAppId);
        }
        return api;
    }

    /**
     * 通过包名，得到QQ类型 appId
     *
     * @param pakageName 包名
     * @return 类型 String
     */
    private static String getQQAppId(String pakageName) {
        return QQ_APPID;
    }

    /**
     * 是否支持分享
     *
     * @return boolean
     */
    public static boolean isSupportShare() {
        //        String packageName = DeviceInfoUtils.getInstanse().getPackName();
        //        String wxAppId = getWechateAppId(packageName);
        //        String qqAppId = getWechateAppId(packageName);
        //        return !TextUtils.isEmpty(wxAppId) && !TextUtils.isEmpty(qqAppId);
        return false;
    }

    /**
     * 是否支持微信分享
     *
     * @return true:支持分享 false:不支持分享
     */
    public static boolean isSupportWeChateShare() {
        String pakageName = DeviceInfoUtils.getInstanse().getPackName();
        String wxAppId = getWechateAppId(pakageName);

        return !TextUtils.isEmpty(wxAppId);
    }

    /**
     * 得到微信分享的appId
     *
     * @return appId
     */
    public static String getWechateShareAppId() {
        return getWechateAppId(DeviceInfoUtils.getInstanse().getPackName());
    }

    /**
     * 得到QQ分享的appId
     *
     * @return appId
     */
    public static String getQQShareAppId() {
        return getQQAppId(DeviceInfoUtils.getInstanse().getPackName());
    }

    /**
     * 调用去分享
     *
     * @param activity activity
     * @param bookId   bookId
     * @param from     from
     */
    public static void gotoShare(final Activity activity, final String bookId, final int from) {
        if (TextUtils.isEmpty(bookId)) {
            return;
        }
        DzSchedulers.child(new Runnable() {
            @Override
            public void run() {

                try {
                    FastOpenBook fastOpenBook = HwRequestLib.getInstance().fastOpenBookRequest(bookId, "");
                    if (null != fastOpenBook && null != fastOpenBook.book) {
                        String shareurl = SpUtil.getinstance(AppConst.getApp()).getSpReaderShareurl();
                        String sharedUrl = shareurl + "?bookId=" + fastOpenBook.book.bookId;
                        String bookName = fastOpenBook.book.bookName;
                        String coverUrl = fastOpenBook.book.coverWap;
                        String des = fastOpenBook.book.introduction;
                        final ShareBeanInfo shareBeanInfo = new ShareBeanInfo();
                        shareBeanInfo.setShareParam(bookName, des, sharedUrl, coverUrl);
                        ALog.dZz("分享参数：" + shareBeanInfo.toString());
                        DzSchedulers.main(new Runnable() {
                            @Override
                            public void run() {
                                ShareUtils.goToShare(activity, shareBeanInfo, from);
                            }
                        });

                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * 跳转分享
     *
     * @param activity      activity
     * @param shareBeanInfo 分享封装bean
     * @param from          from
     */
    public static void goToShare(Activity activity, ShareBeanInfo shareBeanInfo, int from) {
        String wxAppID = getWechateShareAppId();
        if (!TextUtils.isEmpty(wxAppID)) {
            ShareActivity.lanuch(activity, shareBeanInfo, from, false);
        } else {
            ToastAlone.showShort(activity.getResources().getString(R.string.share_tip));
        }
    }

    /**
     * 直接指定朋友圈微信分享
     *
     * @param acitivity   acitivity
     * @param type        type
     * @param shareUrl    shareUrl
     * @param title       title
     * @param description description
     * @param mBitmap     mBitmap
     * @param isRecycler  isRecycler
     */
    public static void directWechatShare(BaseActivity acitivity, int type, String shareUrl, String title, String description, Bitmap mBitmap, boolean isRecycler) {
        directWechatShareByType(acitivity, type, shareUrl, title, description, mBitmap, SHARE_TO_FRIEND_CIRCLE, isRecycler);
    }

    /**
     * 直接微信分享
     *
     * @param acitivity   acitivity
     * @param type        type
     *                    {@link #SHARE_TO_WEB}
     *                    {@link #SHARE_TO_IMG}
     * @param shareUrl    shareUrl
     * @param title       title
     * @param description description
     * @param mBitmap     mBitmap
     * @param shareType   {@link #SHARE_TO_FRIEND_CIRCLE}
     *                    {@link #SHARE_TO_FRIENDS}
     * @param isRecycler  isRecycler
     */
    public static void directWechatShareByType(BaseActivity acitivity, int type, String shareUrl, String title, String description, Bitmap mBitmap, int shareType, boolean isRecycler) {
        directWechatShareByType(acitivity, type, shareUrl, title, description, mBitmap, shareType, isRecycler, -1);
    }

    /**
     * 直接微信分享
     *
     * @param acitivity   acitivity
     * @param type        type
     *                    {@link #SHARE_TO_WEB}
     *                    {@link #SHARE_TO_IMG}
     * @param shareUrl    shareUrl
     * @param title       title
     * @param description description
     * @param mBitmap     mBitmap
     * @param shareType   {@link #SHARE_TO_FRIEND_CIRCLE}
     *                    {@link #SHARE_TO_FRIENDS}
     * @param isRecycler  isRecycler
     * @param style       style
     */
    public static void directWechatShareByType(BaseActivity acitivity, int type, String shareUrl, String title, String description, Bitmap mBitmap, int shareType, boolean isRecycler, int style) {
        String wxAppId = getWechateShareAppId();
        ALog.dZz("directWechatShare,wxAppId:" + wxAppId);
        if (!TextUtils.isEmpty(wxAppId)) {
            if (shareType == SHARE_TO_FRIEND_CIRCLE) {
                if (type == SHARE_TO_WEB) {
                    doWechatShareForWebpage(acitivity, 1, shareUrl, title, description, ImageUtils.compressBitmap(acitivity, mBitmap, 30, isRecycler), true, style);
                } else if (type == SHARE_TO_IMG) {
                    doWechatShareForImg(acitivity, 1, mBitmap, true, isRecycler, style);
                }
            } else if (shareType == SHARE_TO_FRIENDS) {
                if (type == SHARE_TO_WEB) {
                    doWechatShareForWebpage(acitivity, 0, shareUrl, title, description, ImageUtils.compressBitmap(acitivity, mBitmap, 30, isRecycler), true, style);
                } else if (type == SHARE_TO_IMG) {
                    doWechatShareForImg(acitivity, 0, mBitmap, true, isRecycler, style);
                }
            }

        } else {
            ToastAlone.showShort(acitivity.getResources().getString(R.string.share_tip));
        }
    }

    /**
     * 直接微博分享
     *
     * @param mContext mContext
     * @param bytes    bytes
     */
    public static void directWeiBoShareByImg(ShareActivity mContext, byte[] bytes) {
        WeiboMultiMessage weiboMessage = new WeiboMultiMessage();
        WbSdk.install(mContext, new AuthInfo(mContext, WEI_BO_APP_KEY, "https://api.weibo.com/oauth2/default.html", ""));
        final WbShareHandler wbShareHandler = mContext.getWbShareHandler();
        wbShareHandler.registerApp();
        wbShareHandler.setProgressId(R.layout.relative_loadding);
        if (null != bytes) {
            weiboMessage.imageObject = getImageObject(bytes);
        }
        wbShareHandler.shareMessage(weiboMessage, false);
    }

    /**
     * 微博分享
     *
     * @param mContext       mContext
     * @param url            url
     * @param title          title
     * @param des            des
     * @param img            img
     * @param shareToFriends shareToFriends
     */
    @SuppressLint("CheckResult")
    public static void directWeiBoShareByType(final ShareActivity mContext, final String url, final String title, final String des, String img, int shareToFriends) {

        WbSdk.install(mContext, new AuthInfo(mContext, WEI_BO_APP_KEY, "https://api.weibo.com/oauth2/default.html", ""));
        final WbShareHandler wbShareHandler = mContext.getWbShareHandler();
        wbShareHandler.registerApp();
        wbShareHandler.setProgressId(R.layout.relative_loadding);
        mContext.showDialogByType(DialogConstants.TYPE_GET_DATA);
        Glide.with(mContext).asBitmap().load(img).into(new SimpleTarget<Bitmap>() {
            @Override
            public void onLoadStarted(@Nullable Drawable placeholder) {
                super.onLoadStarted(placeholder);
                ALog.cmtDebug("onLoadStarted");
            }

            @Override
            public void onLoadFailed(@Nullable Drawable errorDrawable) {
                super.onLoadFailed(errorDrawable);
                mContext.dissMissDialog();
                ALog.cmtDebug("onLoadFailed");
                WeiboMultiMessage weiboMessage = new WeiboMultiMessage();
                weiboMessage.textObject = getTextObject(des, title, url);
            }

            @Override
            public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                mContext.dissMissDialog();
                WeiboMultiMessage weiboMessage = new WeiboMultiMessage();
                weiboMessage.textObject = getTextObject(des, title, url);

                byte[] bytes = null;
                try {
                    bytes = ImageUtils.compressBitmap(mContext, resource, 30, false);
                    if (null != bytes) {
                        weiboMessage.imageObject = getImageObject(bytes);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                wbShareHandler.shareMessage(weiboMessage, false);
            }
        });
    }

    private static TextObject getTextObject(String des, String title, String url) {
        TextObject textObject = new TextObject();
        textObject.text = des + url + "#place";
        textObject.title = title;
        textObject.actionUrl = url;
        return textObject;
    }

    private static ImageObject getImageObject(byte[] data) {
        ImageObject imageObject = new ImageObject();
        imageObject.imageData = data;
        return imageObject;
    }

    /**
     * 直接QQ分享
     *
     * @param activity    activity
     * @param shareUrl    shareUrl
     * @param title       title
     * @param description description
     * @param imageUrl    imageUrl
     * @param shareType   {@link #SHARE_TO_FRIEND_CIRCLE}
     *                    {@link #SHARE_TO_FRIENDS}
     */
    public static void directQQShareByType(BaseActivity activity, String shareUrl, String title, String description, String imageUrl, int shareType) {
        String qqAppId = getQQShareAppId();
        ALog.dZz("directQQShare,qqAppId:" + qqAppId);
        if (!TextUtils.isEmpty(qqAppId)) {
            Tencent mTencent = Tencent.createInstance(qqAppId, activity);

            if (shareType == SHARE_TO_FRIEND_CIRCLE) {
                doQQShareForWebpage(activity, mTencent, 1, shareUrl, title, description, imageUrl, true);
            } else if (shareType == SHARE_TO_FRIENDS) {
                doQQShareForWebpage(activity, mTencent, 0, shareUrl, title, description, imageUrl, true);
            }

        } else {
            ToastAlone.showShort(activity.getResources().getString(R.string.share_tip));
        }
    }

    /**
     * 直接QQ分享图片(当前QQ空间sdk版本不支持大图分享)
     *
     * @param acitivity acitivity
     * @param content   content
     * @param imageUrl  imageUrl
     * @param flag      flag
     */
    public static void directQQShareByImage(final ShareActivity acitivity, final String imageUrl, final String content, final int flag) {
        final String qqAppId = getQQShareAppId();
        ALog.dZz("directQQShare,qqAppId:" + qqAppId);
        // QZone分享要在主线程做
        DzSchedulers.main(new Runnable() {
            @Override
            public void run() {
                if (!TextUtils.isEmpty(qqAppId)) {
                    final Tencent mTencent = Tencent.createInstance(qqAppId, acitivity);
                    Bundle bundle = new Bundle();
                    if (null != mTencent) {
                        IUiListener shareListener = null;
                        shareListener = getQqShareListener();
                        if (flag == 0) {
                            bundle.putString(QQShare.SHARE_TO_QQ_IMAGE_LOCAL_URL, imageUrl);
                            bundle = getQqBundle(bundle, QQShare.SHARE_TO_QQ_KEY_TYPE, QQShare.SHARE_TO_QQ_TYPE_IMAGE);
                            mTencent.shareToQQ(acitivity, bundle, shareListener);
                        } else {
                            ArrayList<String> imgUrlList = new ArrayList<>();
                            imgUrlList.add(imageUrl);
                            bundle.putInt(QzonePublish.PUBLISH_TO_QZONE_KEY_TYPE, QzonePublish.PUBLISH_TO_QZONE_TYPE_PUBLISHMOOD);
                            bundle.putString(QzonePublish.PUBLISH_TO_QZONE_SUMMARY, acitivity.getResources().getString(R.string.thing_share));
                            bundle.putStringArrayList(QzonePublish.PUBLISH_TO_QZONE_IMAGE_URL, imgUrlList);// 图片地址ArrayList
                            mTencent.publishToQzone(acitivity, bundle, shareListener);
                        }

                    }
                } else {
                    ToastAlone.showShort(acitivity.getResources().getString(R.string.share_tip));
                }

            }
        });

    }

    private static boolean checkInitWxSuccess(BaseActivity context) {
        IWXAPI api = createWXAPI();
        if (null == api) {
            context.shareFail(-1, true);
            return false;
        }
        if (!api.isWXAppInstalled()) {
            showToast(context, context.getResources().getString(R.string.un_install_wx), true);
            return false;
        }

        if (!api.isWXAppSupportAPI()) {
            showToast(context, context.getResources().getString(R.string.need_updata_wx), true);
            return false;
        }
        return true;
    }

    /**
     * 微信分享 网页分享
     *
     * @param context     context
     * @param flag        1:微信朋友圈 0:微信好友
     * @param shareUrl    shareUrl
     * @param title       title
     * @param style       1是启动替身模式 其他是sdk分享
     * @param description description
     * @param image       image
     * @param isToast     isToast
     */
    public static void doWechatShareForWebpage(BaseActivity context, int flag, String shareUrl, String title, String description, byte[] image, boolean isToast, int style) {

        if (!checkInitWxSuccess(context)) {
            return;
        }

        if (StringUtil.isEmpty(shareUrl, title, description)) {
            context.shareFail(-1, true);
            return;
        }

        WXWebpageObject webPage = new WXWebpageObject();
        webPage.webpageUrl = shareUrl;
        WXMediaMessage msg = new WXMediaMessage(webPage);
        msg.title = title;
        msg.description = description;
        if (image == null) {
            context.shareFail(-1, true);
            return;
        }
        msg.thumbData = image;
        ALog.iWz("bmpToByteArray_byte[] Size:" + image.length);
        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.transaction = buildTransaction("webpage");
        req.message = msg;
        req.scene = flag == 0 ? SendMessageToWX.Req.WXSceneSession : SendMessageToWX.Req.WXSceneTimeline;

        ALog.iWz("title：" + title + "--description:" + description);
        libWxShare(context, isToast, style, req, flag);
    }

    /**
     * 微信分享 图片
     *
     * @param flag    1:微信朋友圈 0:微信好友
     * @param context context
     * @param bytes   bytes
     */
    public static void doWechatShareForImg(BaseActivity context, int flag, byte[] bytes) {
        if (!checkInitWxSuccess(context)) {
            return;
        }
        WXImageObject imgObj = new WXImageObject();
        imgObj.imageData = bytes;
        WXMediaMessage msg = new WXMediaMessage();
        msg.mediaObject = imgObj;
        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.transaction = buildTransaction("img");
        req.message = msg;
        req.scene = flag == 0 ? SendMessageToWX.Req.WXSceneSession : SendMessageToWX.Req.WXSceneTimeline;

        libWxShare(context, false, -1, req, flag);
    }

    /**
     * 微信分享 图片
     *
     * @param context    context
     * @param flag       1:微信朋友圈 0:微信好友
     * @param bitmap     bitmap
     * @param isToast    isToast
     * @param isRecycler isRecycler
     * @param style      style
     */
    public static void doWechatShareForImg(BaseActivity context, int flag, Bitmap bitmap, boolean isToast, boolean isRecycler, int style) {
        if (!checkInitWxSuccess(context)) {
            return;
        }
        WXImageObject imgObj = new WXImageObject(bitmap);
        WXMediaMessage msg = new WXMediaMessage();
        msg.mediaObject = imgObj;

        Bitmap thumbBmp = Bitmap.createScaledBitmap(bitmap, bitmap.getWidth() / 4, bitmap.getHeight() / 4, true);
        if (isRecycler) {
            bitmap.recycle();
        }
        msg.thumbData = ImageUtils.compressBitmap(context, thumbBmp, 30, true);
        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.transaction = buildTransaction("img");
        req.message = msg;
        req.scene = flag == 0 ? SendMessageToWX.Req.WXSceneSession : SendMessageToWX.Req.WXSceneTimeline;

        libWxShare(context, isToast, style, req, flag);
    }


    /**
     * buildTransaction
     *
     * @param type type
     * @return string
     */
    public static String buildTransaction(final String type) {
        return (type == null) ? String.valueOf(System.currentTimeMillis()) : type + System.currentTimeMillis();
    }


    /**
     * 包含替身模式 正式模式的分享 封装
     *
     * @param context
     * @param isToast
     * @param style   style =1 替身模式 其他为sdk模式 默认style = -1
     * @param req
     * @param flag
     */
    private static void libWxShare(BaseActivity context, boolean isToast, int style, SendMessageToWX.Req req, int flag) {
        IWXAPI api = createWXAPI();
        int type = -1;
        try {
            if (flag == 1) {
                type = ShareUtils.WX_PYQ;
            } else {
                type = ShareUtils.WX_HY;
            }
            boolean boo;
            Bundle bundle = new Bundle();
            req.toBundle(bundle);

            HashMap<String, Object> map = new HashMap<>();
            map.put(MSG_WXSHARE_STYLE, style);
            map.put(MSG_WXSHARE_BUNDLE, bundle);
            map.put(MSG_WXSDK_VN, Build.SDK_INT);
            Map<String, Object> libMap = ShareUtils.handleShare(context, map);
            if (libMap != null && libMap.size() > 0) {
                int status = 0;
                if (libMap.containsKey(MSG_WXSHARE_STATUS)) {
                    status = (int) libMap.get(MSG_WXSHARE_STATUS);
                }

                switch (status) {
                    case MSG_SHARE_REPLACE:
                        Intent intent = null;
                        if (libMap.containsKey(MSG_WXSHARE_INTENT)) {
                            intent = (Intent) libMap.get(MSG_WXSHARE_INTENT);
                        }
                        if (intent != null) {
                            try {
                                context.startActivity(intent);
                            } catch (Exception e) {
                                context.shareFail(type, true);
                            }
                        }
                        break;
                    case MSG_SHARE_SDK:
                        boo = api.sendReq(req);
                        if (!boo) {
                            context.shareFail(type, true);
                        }
                        break;
                    case MSG_SHARE_NULL://一个替身也没有
                        break;
                    default:
                        boo = api.sendReq(req);
                        if (!boo) {
                            context.shareFail(type, true);
                        }
                        break;
                }

            } else {
                context.shareFail(type, true);
            }
        } catch (Throwable e) {
            ALog.printExceptionWz(e);
            context.shareFail(type, true);
        } finally {
            context.dissMissDialog();
        }
    }

    /***********************************************微信分享结束******************************************************************/

    private static Bundle getQqBundle(Bundle params, String key, int value) {
        if (null == params) {
            params = new Bundle();
        }
        params.putInt(key, value);
        return params;
    }

    /***********************************************QQ分享开始******************************************************************/

    /**
     * QQ分享 分享图文消息
     *
     * @param mActivity   mActivity
     * @param mTencent    mTencent
     * @param flag        1:QQ空间 0:QQ好友
     * @param shareUrl    shareUrl
     * @param title       title
     * @param description description
     * @param imageUrl    imageUrl
     * @param isToast     isToast
     */
    public static void doQQShareForWebpage(final BaseActivity mActivity, final Tencent mTencent, int flag, String shareUrl, String title, String description, String imageUrl, final boolean isToast) {

        final Bundle params = new Bundle();
        if (flag == 0) {
            //分享类型
            params.putInt(QQShare.SHARE_TO_QQ_KEY_TYPE, QQShare.SHARE_TO_QQ_TYPE_DEFAULT);
            //必填
            params.putString(QQShare.SHARE_TO_QQ_TITLE, title);
            //选填
            params.putString(QQShare.SHARE_TO_QQ_SUMMARY, description);
            //必填
            params.putString(QQShare.SHARE_TO_QQ_TARGET_URL, shareUrl);
            params.putString(QQShare.SHARE_TO_QQ_IMAGE_URL, imageUrl);
            // QZone分享要在主线程做
            DzSchedulers.main(new Runnable() {
                @Override
                public void run() {
                    if (null != mTencent) {
                        mTencent.shareToQQ(mActivity, params, getQqShareListener());
                    }
                }
            });
        } else if (flag == 1) {
            params.putInt(QzoneShare.SHARE_TO_QZONE_KEY_TYPE, QzoneShare.SHARE_TO_QZONE_TYPE_IMAGE_TEXT);
            params.putString(QzoneShare.SHARE_TO_QQ_TITLE, title);
            params.putString(QzoneShare.SHARE_TO_QQ_SUMMARY, description);
            params.putString(QzoneShare.SHARE_TO_QQ_TARGET_URL, shareUrl);
            ArrayList<String> imageUrls = new ArrayList<String>();
            imageUrls.add(imageUrl);
            params.putStringArrayList(QzoneShare.SHARE_TO_QQ_IMAGE_URL, imageUrls);
            // QZone分享要在主线程做
            DzSchedulers.main(new Runnable() {
                @Override
                public void run() {
                    if (null != mTencent) {
                        mTencent.shareToQzone(mActivity, params, getQqShareListener());
                    }
                }
            });
        }
    }


    /***********************************************QQ分享结束******************************************************************/


    private static void showToast(Context context, String content, boolean isToast) {
        if (TextUtils.isEmpty(content) || !isToast) {
            return;
        }
        ToastAlone.showShort(content);
    }


    private static HashMap<String, Object> handleShare(Context context, Map<String, Object> map) {
        try {
            int style = 0;
            int sdkVersion = 0;
            Bundle mBundle = null;
            HashMap<String, Object> returnMap;

            if (map != null && map.size() > 0) {
                if (map.containsKey(MSG_WXSHARE_STYLE)) {
                    style = (int) map.get(MSG_WXSHARE_STYLE);
                }
                if (map.containsKey(MSG_WXSDK_VN)) {
                    sdkVersion = (int) map.get(MSG_WXSDK_VN);
                }
                if (map.containsKey(MSG_WXSHARE_BUNDLE)) {
                    mBundle = (Bundle) map.get(MSG_WXSHARE_BUNDLE);
                }
                if (style * sdkVersion != 0 && mBundle != null) {
                    returnMap = new HashMap<>();

                    if (style == 0 || style == -1) {
                        returnMap.put(MSG_WXSHARE_STATUS, MSG_SHARE_SDK);
                        return returnMap;
                    }

                    String[] shareData = getShareData(context);
                    if (shareData == null) {
                        returnMap.put(MSG_WXSHARE_STATUS, MSG_SHARE_SDK);
                        //                        returnMap.put(DzpayConstants.MSG_WXSHARE_STATUS, DzpayConstants.MSG_SHARE_NULL);//返回null时  主客户端 什么也没有执行
                        return returnMap;
                    }

                    Intent localIntent = new Intent();
                    localIntent.setClassName("com.tencent.mm", "com.tencent.mm.plugin.base.stub.WXEntryActivity");
                    localIntent.putExtras(mBundle);
                    localIntent.putExtra("_mmessage_sdkVersion", sdkVersion);
                    localIntent.putExtra("_mmessage_appPackage", shareData[0]);
                    localIntent.putExtra("_mmessage_content", "weixin://sendreq?appid=" + shareData[1]);
                    localIntent.putExtra("_mmessage_checksum", checkSum("weixin://sendreq?appid=wxd930ea5d5a258f4f", sdkVersion, context.getPackageName()));
                    localIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
                        localIntent.addFlags(Intent.FLAG_RECEIVER_NO_ABORT);
                    }
                    returnMap.put(MSG_WXSHARE_STATUS, MSG_SHARE_REPLACE);
                    returnMap.put(MSG_WXSHARE_INTENT, localIntent);
                    return returnMap;
                }
            }
        } catch (Exception e) {
            PayLog.printStackTrace(e);
        }
        return null;
    }


    private static String[] getShareData(Context context) {

        for (int i = 0; i < STRINGS.length; i++) {
            if (UtilApkCheck.isInstalledApp(context, STRINGS[i])) {
                return new String[]{STRINGS[i], B[i]};
            }
        }
        return null;
    }


    private static byte[] checkSum(String var0, int var1, String var2) {
        return sumByte;
    }


    public static IUiListener getQqShareListener() {
        return qqShareListener;
    }

}
