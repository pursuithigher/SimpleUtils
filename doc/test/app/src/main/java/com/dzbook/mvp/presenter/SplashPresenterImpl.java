package com.dzbook.mvp.presenter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import com.dzbook.AppConst;
import com.dzbook.DzBookExceptionCatcher;
import com.dzbook.activity.CenterDetailActivity;
import com.dzbook.activity.GuideActivity;
import com.dzbook.activity.Main2Activity;
import com.dzbook.activity.detail.BookDetailActivity;
import com.dzbook.activity.reader.ReaderActivity;
import com.dzbook.activity.reader.ReaderUtils;
import com.dzbook.bean.LocalFileBean;
import com.dzbook.bean.LocalFileUtils;
import com.dzbook.database.bean.BookInfo;
import com.dzbook.database.bean.CatalogInfo;
import com.dzbook.event.EventBusUtils;
import com.dzbook.event.EventConstant;
import com.dzbook.lib.net.DzSchedulers;
import com.dzbook.lib.utils.ALog;
import com.dzbook.log.DzLog;
import com.dzbook.log.LogConstants;
import com.dzbook.model.ModelAction;
import com.dzbook.mvp.UI.SplashUI;
import com.dzbook.r.c.AkDocInfo;
import com.dzbook.service.HwAdvanceStoreIntentServices;
import com.dzbook.service.HwInitSaleBooksIntentServices;
import com.dzbook.service.HwIntentService;
import com.dzbook.utils.DBUtils;
import com.dzbook.utils.DeviceInfoUtils;
import com.dzbook.utils.FileUtils;
import com.dzbook.utils.SpUtil;
import com.dzbook.utils.ThirdPartyLog;
import com.dzbook.view.common.dialog.CustomScrollViewDialog;
import com.huawei.common.applog.AppLogApi;
import com.ishugui.BuildConfig;
import com.ishugui.R;
import com.iss.app.BaseActivity;

import java.lang.reflect.Method;
import java.net.URLDecoder;
import java.util.HashMap;

import io.reactivex.Single;
import io.reactivex.SingleEmitter;
import io.reactivex.SingleObserver;
import io.reactivex.SingleOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

import static com.dzbook.log.LogConstants.LAUNCH_DIRECT;

/**
 * SplashPresenterImpl
 *
 * @author winzows
 */
public class SplashPresenterImpl implements SplashPresenter {

    private static final String BOOK_DETAIL = "/bookdetail";
    private static final String ACTIVITY = "/activity";
    private static final String BOOK_UPDATE = "/bookupdate";
    private static final long DELAY_TIME = 3000L;
    private CustomScrollViewDialog dialog;
    private boolean insertLocalDbSuccess = false;
    private String insertLocalFileBid = null;
    private boolean isInitData;
    private Uri getIntentUri;
    private int launchMode;
    private long start = 0;
    private SplashUI mUI;
    /**
     * app是否在前台展示
     */
    private boolean isShow;

    /**
     * 构造器
     *
     * @param splashUI splashUI
     */
    public SplashPresenterImpl(SplashUI splashUI) {
        mUI = splashUI;
        isInitData = false;
        start = System.currentTimeMillis();
    }

    @Override
    public void setShow(boolean show) {
        this.isShow = show;
    }

    /**
     * 是否初始化了启动模式
     */
    private void handleMakeFile() {
        FileUtils.getDefault().init();
        FileUtils.handleAppFileRootDirectory();
        FileUtils.getDefault().creatSDDir(FileUtils.APP_ROOT_DIR_PATH);
    }

    /**
     * 预请求书城数据（第一页数据）s
     */
    @Override
    public void initSaleBooks() {
        Intent intent = new Intent(mUI.getContext(), HwInitSaleBooksIntentServices.class);
        intent.putExtra(HwIntentService.SERVICE_TYPE, HwInitSaleBooksIntentServices.SPLASH_INIT_SALE_BOOKS_TYPE);
        mUI.getContext().startService(intent);
    }

    /**
     * 预请求书城数据（第一页数据）
     */
    @Override
    public void advanceStoreData() {
        Intent intent = new Intent(mUI.getContext(), HwAdvanceStoreIntentServices.class);
        intent.putExtra(HwIntentService.SERVICE_TYPE, HwAdvanceStoreIntentServices.SPLASH_BOOK_STORE_DATA_AND_DEVICE_ACTIVITY_TYPE);
        mUI.getContext().startService(intent);
    }

    /**
     * 内置书接口处理
     */
    @Override
    public void buildInBook() {
        Intent intent = new Intent(mUI.getContext(), HwIntentService.class);
        intent.putExtra(HwIntentService.SERVICE_TYPE, HwIntentService.SPLASH_GET_INIT_BOOK_DATA_TYPE);
        mUI.getContext().startService(intent);
    }

    private void dealCrashCatcher() {
        /**
         * 初始化appLog
         */
        if (!BuildConfig.DEBUG) {
            AppLogApi.Param param = new AppLogApi.Param().setLogLevel(AppLogApi.LogLevel.VERBOSE);
            AppLogApi.init(AppConst.getApp(), param);
        }

        //Bugly初始化
        DzBookExceptionCatcher.init(AppConst.getApp());
    }

    @Override
    public void jump() {
        if (!isActivityEmpty()) {
            long time = DELAY_TIME - (System.currentTimeMillis() - start);
            if (time < 0) {
                time = 0;
            }
            DzSchedulers.mainDelay(new Runnable() {
                @Override
                public void run() {
                    if (isShow) {
                        jumpMain();
                    }
                }
            }, time);
        }
    }

    private void jumpMain() {
        Activity activity = mUI.getActivity();
        Intent intent = new Intent(activity, Main2Activity.class);
        intent.putExtra("turnPage", getLaunchOpen());
        activity.startActivity(intent);
        BaseActivity.showActivity(activity);
        activity.finish();
    }

    private int getLaunchOpen() {
        Intent intent1 = mUI.getActivity().getIntent();
        if (null != intent1) {
            return intent1.getIntExtra("turnPage", -1);
        }
        return -1;
    }

    @Override
    public void intentToGuideActivity() {
        if (!isActivityEmpty()) {
            GuideActivity.launch(mUI.getActivity());
            mUI.getActivity().finish();
        }
    }

    @Override
    public boolean isActivityEmpty() {
        Activity activity = mUI.getActivity();
        return activity == null;
    }

    /**
     * 新用户 第一次启动时 需要弹用户隐私协议的弹窗
     */
    @Override
    public void showAgreementDialog() {
        if (dialog == null) {
            dialog = new CustomScrollViewDialog(mUI.getActivity(), mUI.getActivity().getWindow());
        }
        if (dialog.isShowing()) {
            return;
        }
        dialog.setOnClickCallback(new CustomScrollViewDialog.OnDialogClickCallBack() {
            @Override
            public void onClickConfirm() {
                mUI.splashSecond();
            }
        });
        dialog.show();
    }

    /**
     * 处理唤起等
     */
    @Override
    public void handleIntent() {
        handleTypeByIntent();
    }

    /**
     * 判断启动模式
     */
    @Override
    public void handleLaunchMode() {
        SpUtil spUtil = SpUtil.getinstance(mUI.getActivity());
        PackageManager packageManager = mUI.getActivity().getPackageManager();
        launchMode = LAUNCH_DIRECT;
        try {
            long dzLastUpdateTime = spUtil.getLong(SpUtil.DZ_LASTUPDATETIME, 0);
            PackageInfo packageInfo = packageManager.getPackageInfo(DeviceInfoUtils.getInstanse().getPackName(), 0);
            long lastUpdateTime = packageInfo.lastUpdateTime;
            if (dzLastUpdateTime != lastUpdateTime) {
                if (0 == dzLastUpdateTime) {
                    launchMode = LogConstants.LAUNCH_FIRST;
                } else {
                    launchMode = LogConstants.LAUNCH_COVER;
                }
            } else {
                launchMode = LAUNCH_DIRECT;
            }

            Intent getIntent = mUI.getActivity().getIntent();
            getIntentUri = getIntent.getData();

            //FIXME  第一次安装 从没打开过的 不支持唤起??
            if (dzLastUpdateTime != 0) {
                if (getIntentUri != null) {
                    String host = getIntentUri.getHost();
                    if (TextUtils.equals(host, mUI.getActivity().getPackageName())) {
                        //三方唤醒
                        launchMode = LogConstants.LAUNCH_THIRD;
                    }
                }
            }
            if (null != getIntentUri) {
                String scheme = getIntentUri.getScheme();
                Resources resources = mUI.getContext().getResources();
                if (TextUtils.equals(scheme, resources.getString(R.string.hw_push_scheme))) {
                    launchMode = LogConstants.LAUNCH_PUSH;
                } else if (TextUtils.equals(scheme, resources.getString(R.string.hw_search_scheme))) {
                    launchMode = LogConstants.LAUNCH_GLOBAL_SEARCH;
                } else if (TextUtils.equals(scheme, resources.getString(R.string.hw_push_scheme))) {
                    launchMode = LogConstants.LAUNCH_PUSH;
                } else if (TextUtils.equals(scheme, resources.getString(R.string.hw_local_content)) || TextUtils.equals(scheme, resources.getString(R.string.hw_local_file))) {
                    launchMode = LogConstants.LAUNCH_LOCAL_FILE;
                }
            }

            Log.d("launchMode-", "launchMode = " + launchMode + " getIntentUri =" + getIntentUri);
            spUtil.setLong(SpUtil.DZ_LASTUPDATETIME, lastUpdateTime);
        } catch (Exception e) {
            ALog.printStackWz(e);
        }

        DzLog.getInstance().logLaunch(launchMode);
        AppConst.setLaunchMode(launchMode);

        /**
         * 如果 应用还没有初始化过。这里需要直接跳转
         */
        if (!SpUtil.getinstance(mUI.getActivity()).getBoolean(SpUtil.HW_IS_SHOW_GUIDE) && launchMode == LogConstants.LAUNCH_GLOBAL_SEARCH) {
            handleIntent();
        }
    }

    @Override
    @LogConstants.LaunchSm
    public int launchMode() {
        return this.launchMode;
    }

    /**
     * 华为打点使用
     *
     * @return
     */
    @Override
    public int getLaunchFrom() {
        if (this.launchMode <= 3) {
            return this.launchMode;
        } else {
            return LAUNCH_DIRECT;
        }
    }

    @Override
    public String getLaunchTo() {
        int launchOpen = getLaunchOpen();
        if (launchOpen > 0) {
            switch (launchOpen) {
                case ModelAction.TO_SEARCH:
                    return LogConstants.TO_SEARCH;
                case ModelAction.TO_SIGN:
                    return LogConstants.TO_SIGN;
                case ModelAction.TO_BOOKSTORE:
                    return LogConstants.TO_RECOMMEND;
                case ModelAction.TO_READER:
                    return LogConstants.TO_READER;
                default:
                    break;
            }

        }
        return LogConstants.TO_RECOMMEND;
    }

    @Override
    public void destroy() {
        if (dialog != null) {
            dialog.dismiss();
            dialog = null;
        }
        if (insertLocalDbSuccess && !TextUtils.isEmpty(insertLocalFileBid)) {
            insertLocalDbSuccess = false;
            Bundle bundle = new Bundle();
            bundle.putString("goBookId", insertLocalFileBid);
            bundle.putInt("goWhere", AppConst.GO_READER);
            EventBusUtils.sendStickyMessage(EventConstant.START_OPEN_BOOK, EventConstant.TYPE_MAIN2ACTIVITY, bundle);
        }
    }

    @Override
    public void onPermissionGrant() {
        if (isInitData) {
            return;
        }
        isInitData = true;
        FileUtils.logSwitch();
        //华为打点
        ThirdPartyLog.initLog(AppConst.getApp());
        //处理崩溃日志收集
        dealCrashCatcher();
        AppConst.initHttpDns();
        initSaleBooks();
        boolean isShowGuide = SpUtil.getinstance(mUI.getActivity()).getBoolean(SpUtil.HW_IS_SHOW_GUIDE);
        if (!isShowGuide) {
            handleMakeFile();
            buildInBook();
            if (launchMode == LogConstants.LAUNCH_LOCAL_FILE && getIntentUri != null) {
                openLocalFile(getIntentUri.getPath(), GuideActivity.class.getSimpleName());
            } else {
                intentToGuideActivity();
            }
        } else {
            advanceStoreData();
            if (launchMode != LogConstants.LAUNCH_THIRD && launchMode != LogConstants.LAUNCH_PUSH && launchMode != LogConstants.LAUNCH_LOCAL_FILE && launchMode != LogConstants.LAUNCH_GLOBAL_SEARCH) {
                mUI.loadAd();
            } else if (launchMode == LogConstants.LAUNCH_LOCAL_FILE && getIntentUri != null) {
                openLocalFile(getIntentUri.getPath(), Main2Activity.class.getSimpleName());
            } else {
                handleIntent();
            }
        }
        //打一个pv的点。
        ThirdPartyLog.onResumeActivity((BaseActivity) mUI.getActivity());
        ALog.d("tag_wz", "onPermissionGranted");
    }


    @SuppressLint("PrivateApi")
    @Override
    public void fixLeaked() {
        try {
            Class<?> aClass = Class.forName("android.view.HwNsdImpl");

            Method method = aClass.getDeclaredMethod("getDefault");
            Object object = method.invoke(null);

            Method methodSetContext = aClass.getDeclaredMethod("setContext", Context.class);
            methodSetContext.invoke(object, new Object[]{null});
        } catch (Throwable e) {
            ALog.printExceptionWz(e);
        }
    }

    /**
     * 根据path类型来 决定跳转哪些页面
     */
    private void handleTypeByIntent() {

        if (getIntentUri != null) {
            String uriPath = getIntentUri.getPath();
            if (!TextUtils.isEmpty(uriPath)) {
                switch (uriPath) {
                    case BOOK_UPDATE:
                        bookUpdate();
                        break;
                    case BOOK_DETAIL:
                        bookDetail();
                        break;
                    case ACTIVITY:
                        act();
                        break;
                    default:
                        jump();
                        break;
                }
            }
        }
    }

    private void jumpWithName(String name) {
        if (Main2Activity.class.getSimpleName().equals(name)) {
            jumpMain();
        } else if (GuideActivity.class.getSimpleName().equals(name)) {
            intentToGuideActivity();
        } else {
            jumpMain();
        }
    }

    private void openLocalFile(final String uriPath, final String name) {
        Single.create(new SingleOnSubscribe<BookInfo>() {

            @Override
            public void subscribe(SingleEmitter<BookInfo> e) {
                if (TextUtils.isEmpty(uriPath)) {
                    e.onError(new RuntimeException());
                    return;
                }
                LocalFileBean localFileBean = LocalFileUtils.getLocalFile(uriPath);
                if (localFileBean == null) {
                    e.onError(new RuntimeException());
                    return;
                }
                BookInfo bookInfo = LocalFileUtils.insertLocalDb(AppConst.getApp(), localFileBean);
                if (bookInfo != null) {
                    e.onSuccess(bookInfo);
                } else {
                    e.onError(new RuntimeException());
                }
            }

        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new SingleObserver<BookInfo>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onSuccess(BookInfo value) {
                insertLocalDbSuccess = true;
                insertLocalFileBid = value.bookid;
                jumpWithName(name);
            }

            @Override
            public void onError(Throwable e) {
                insertLocalDbSuccess = false;
                insertLocalFileBid = null;
                jumpWithName(name);
            }
        });
    }


    /**
     * 跳转活动页面
     */
    private void act() {
        String url = getIntentUri.getQueryParameter("url");
        if (TextUtils.isEmpty(url)) {
            jump();
            return;
        }
        String actUrl = URLDecoder.decode(url);
        ALog.dWz("handleTypeByIntent = actUrl " + actUrl);
        if (!TextUtils.isEmpty(actUrl)) {
            CenterDetailActivity.show(mUI.getActivity(), actUrl);
            mUI.getActivity().finish();
        } else {
            jump();
        }
    }

    /**
     * 跳转书籍详情
     */
    private void bookDetail() {
        String goBookId = getIntentUri.getQueryParameter("bookId");
        ALog.dWz("handleTypeByIntent = bookDetail " + goBookId);
        if (!TextUtils.isEmpty(goBookId)) {
            BookDetailActivity.launch(mUI.getContext(), goBookId, "");
            //mUI.getActivity().finish();
        } else {
            jump();
        }
    }

    /**
     * 书籍更新
     */
    private void bookUpdate() {
        String updateBookId = getIntentUri.getQueryParameter("bookId");
        ALog.dWz("handleTypeByIntent = bookUpdate " + updateBookId);
        if (!TextUtils.isEmpty(updateBookId)) {
            tryJumpReaderActivity(updateBookId);
            // mUI.getActivity().finish();
        } else {
            jump();
        }
    }

    /**
     * 追更图书 到阅读器 或者到图书详情页面
     *
     * @param bookId 书籍Id
     */
    private void tryJumpReaderActivity(String bookId) {
        ALog.dWz("handleTypeByIntent = tryJumpReaderActivity " + bookId);
        Intent it;
        BookInfo bookInfo = DBUtils.findShelfBookByBookId(mUI.getActivity(), bookId);
        //在书架
        if (bookInfo != null) {
            CatalogInfo catalogInfo = DBUtils.getCatalog(mUI.getActivity(), bookId, bookInfo.currentCatalogId);
            if (catalogInfo != null && catalogInfo.isAvailable()) {
                it = new Intent(mUI.getActivity(), ReaderActivity.class);

                AkDocInfo docInfo = ReaderUtils.generateDoc(mUI.getActivity(), bookInfo, catalogInfo);
                docInfo.currentPos = catalogInfo.currentPos;
                it.putExtra("docInfo", docInfo);
                mUI.getActivity().startActivity(it);
            } else {
                //书籍当前章节不可用
                BookDetailActivity.launch(mUI.getActivity(), bookId, "");
            }
        } else {
            //不在书架
            BookDetailActivity.launch(mUI.getActivity(), bookId, "");
        }
        HashMap<String, String> map = new HashMap<>(5);
        map.put("bookid", bookId);

        String gtCid = SpUtil.getinstance(mUI.getContext()).getString(SpUtil.PUSH_CLIENTID, "");
        map.put(LogConstants.KEY_GT_CID, gtCid);
        DzLog.getInstance().logEvent(LogConstants.EVENT_ZGTSDJ, map, "");
    }

    @Override
    public void onEventFinish() {
        DzSchedulers.mainDelay(new Runnable() {
            @Override
            public void run() {
                mUI.getActivity().finish();
                Log.d("launchMode-", "onEventFinish --->");
            }
        }, 1000L);
    }

    @Override
    public void onConfigurationChanged(int screenHeightDp) {
        if (dialog != null && dialog.isShowing()) {
            dialog.onConfigurationChanged(screenHeightDp);
        }
    }

}
