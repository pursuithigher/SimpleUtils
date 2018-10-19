package com.dzbook.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.support.v4.content.FileProvider;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.inputmethod.InputMethodManager;
import android.webkit.DownloadListener;
import android.webkit.SslErrorHandler;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.dzbook.AppConst;
import com.dzbook.AppContext;
import com.dzbook.BaseTransparencyLoadActivity;
import com.dzbook.activity.reader.ReaderUtils;
import com.dzbook.database.bean.CatalogInfo;
import com.dzbook.event.EventBusUtils;
import com.dzbook.event.EventConstant;
import com.dzbook.event.EventMessage;
import com.dzbook.lib.net.DzSchedulers;
import com.dzbook.lib.utils.ALog;
import com.dzbook.lib.utils.SDCardUtil;
import com.dzbook.log.DzLog;
import com.dzbook.log.DzLogMap;
import com.dzbook.log.LogConstants;
import com.dzbook.model.ModelAction;
import com.dzbook.push.BeanCloudyNotify;
import com.dzbook.sonic.DzWebView;
import com.dzbook.utils.FileUtils;
import com.dzbook.utils.HwLog;
import com.dzbook.utils.ListUtils;
import com.dzbook.utils.NetworkUtils;
import com.dzbook.utils.NewDownloadManagerUtils;
import com.dzbook.utils.SpUtil;
import com.dzbook.utils.ThirdPartyLog;
import com.dzbook.view.DianZhongCommonTitle;
import com.dzbook.view.common.StatusView;
import com.dzbook.view.common.loading.RefreshLayout;
import com.dzbook.web.WebManager;
import com.ishugui.R;
import com.iss.app.BaseActivity;
import com.iss.view.common.ToastAlone;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import io.reactivex.disposables.Disposable;

/**
 * 活动中心- 打开item- 活动详情
 *
 * @author dllik 2013-11-28
 */
@SuppressLint("SetJavaScriptEnabled")
public class CenterDetailActivity extends BaseTransparencyLoadActivity implements RefreshLayout.OnRefreshListener {
    /**
     * tag
     */
    public static final String TAG = "CenterDetailActivity: ";

    private static final String TITLE_ERROR_1 = "/php";
    private static final String TITLE_ERROR_2 = "找不到网页";
    private static final String TITLE_ERROR_3 = "网页无法打开";
    private static final String TITLE_ERROR_4 = "about:blank";

    private static final int TITLE_MIN_LENGTH = 50;


    private DianZhongCommonTitle mCommonTitle;
    private StatusView statusView;

    /**
     * 活动的webview
     */
    private DzWebView webViewCenter;

    private BeanCloudyNotify cloudyNotication = null;

    private String notiTitle;

    private ProgressBar progressBarLoading;
    private WebManager webManager;

    private HashMap<String, String> priMap;
    private boolean isLoadeError = false;
    private String mUrl;
    private RefreshLayout swipeLayout;

    private String webFrom;

    /**
     * 为了解决签到页面登录成功之后刷新url的问题
     */
    private boolean needClearHistory;
    private boolean isReloadWeb;
    private boolean isPause;
    private boolean isUpLoadLog;

    private AlphaAnimation anim;

    @Override
    public String getTagName() {
        return TAG;
    }

    @Override
    public int getMaxSize() {
        return 4;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ac_centerdetail);
        EventBusUtils.sendMessage(EventConstant.FINISH_SPLASH);
    }

    @Override
    @SuppressWarnings("unchecked")
    protected void initView() {

        mCommonTitle = findViewById(R.id.include_top_title_item);
        statusView = findViewById(R.id.defaultview_nonet);
        progressBarLoading = findViewById(R.id.progressbar_loading);
        swipeLayout = findViewById(R.id.swipe_container);
        webViewCenter = new DzWebView(getApplicationContext());

        webViewCenter.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        swipeLayout.addView(webViewCenter);

        webViewCenter.setHorizontalScrollbarOverlay(false);
        webViewCenter.setHorizontalScrollBarEnabled(false);
        webViewCenter.setVerticalScrollBarEnabled(false);
        webViewCenter.setDownloadListener(new MyWebViewDownLoadListener());
        swipeLayout.setRefreshListener(this);

        webManager = new WebManager(this, webViewCenter, ThirdPartyLog.FROM_CENTER);
        webManager.init();
        isLoadeError = false;

        Intent intent = getIntent();
        if (null == intent) {
            finish();
            return;
        }
        final Bundle bundle = getIntent().getExtras();
        if (null == bundle) {
            finish();
            return;
        }
        cloudyNotication = (BeanCloudyNotify) bundle.getSerializable("noti");
        notiTitle = bundle.getString("notiTitle");
        webFrom = bundle.getString("web");
        isReloadWeb = bundle.getBoolean("isReload", false);
        Object tmp = bundle.getSerializable("priMap");
        if (null != tmp && tmp instanceof HashMap) {
            priMap = (HashMap) tmp;
        }

        mUrl = bundle.getString("url");
        ThirdPartyLog.onEventValueOldClick(this, ThirdPartyLog.ACTIVITY_CENTER_UMENG_ID, null, 1);
        //抽取公共方法，签到页面取登录登录完成之后可能userId变了，所以需要刷新
        initData2();
    }


    private void setLog() {
        if (isUpLoadLog) {
            return;
        }
        try {
            isUpLoadLog = true;
            if (!TextUtils.isEmpty(mUrl)) {
                Uri parse = Uri.parse(mUrl);
                parse.getQueryParameterNames();
                String id = parse.getQueryParameter("_typeid_");
                if (!TextUtils.isEmpty(id)) {
                    HwLog.setActivity(id, notiTitle);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected boolean isCustomPv() {
        return true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        isPause = true;
        try {
            if (webViewCenter != null) {
                webViewCenter.onPause();
            }
        } catch (Throwable ignore) {
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        pvLog("");
        if (isReloadWeb && isPause && !needClearHistory && null != webViewCenter) {
            initData2();
        }
        try {
            if (webViewCenter != null) {
                webViewCenter.onResume();
            }
        } catch (Throwable ignore) {
        }
    }

    @Override
    public String getPI() {
        return notiTitle;
    }

    @Override
    public String getPS() {
        return DzLogMap.parseUrl(this.mUrl);
    }

    /**
     * pv日志
     *
     * @param url url
     */
    public void pvLog(String url) {
        HashMap<String, String> map = new HashMap<>();
        if (!TextUtils.isEmpty(webFrom)) {
            map.put("web", webFrom);
        }
        DzLog.getInstance().logPv(this.getName(), map, null);
    }

    private void initData2() {
        String url = mUrl;
        if (!url.startsWith("http://") && !url.startsWith("file:///") && !url.startsWith("https://") && !url.startsWith("svn://")) {
            url = "http://" + url;
        }

        ALog.iLk("url:  " + url);

        boolean isWhiteUrl = checkUrlWhite(url);
        if (isWhiteUrl) {
            webViewCenter.loadUrl(url);
        } else {
            ToastAlone.showLong("url is not on the white list!\n finish this activity!! ");
            finish();
        }
    }

    /**
     * 校验域名 是否在白名单里
     *
     * @param aUrl url
     * @return 是不是？
     */
    private boolean checkUrlWhite(String aUrl) {
        ArrayList<String> whiteUrlList = AppContext.getWhiteUrlList();
        if (!ListUtils.isEmpty(whiteUrlList)) {
            try {
                Uri uri = Uri.parse(aUrl);
                String host = uri.getHost();
                ALog.dWz("checkUrlWhite host is " + host + " url = " + aUrl);
                if (!TextUtils.isEmpty(host)) {
                    if (!whiteUrlList.contains(host)) {
                        return false;
                    }
                }
            } catch (Throwable e) {
                ALog.printExceptionWz(e);
            }
        }
        return true;
    }

    /**
     * 初始化数据
     */
    @Override
    protected void initData() {

        /**
         * 判断Main2Activity是否处于运行状态
         * 非运行状态下，关闭滑动退出activity功能
         */
        setSwipeBackEnable(AppConst.isIsMainActivityActive());
        if (!TextUtils.isEmpty(notiTitle)) {
            mCommonTitle.setTitle(notiTitle);
        }

        webViewCenter.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                return true;
            }
        });

        webViewCenter.setWebChromeClient(new WebChromeClient() {


            @Override
            public void onProgressChanged(WebView view, int newProgress) {

                if (progressBarLoading == null) {
                    return;
                }
                int tempProgress = newProgress > 50 ? 100 : newProgress;
                if (progressBarLoading.getVisibility() == View.GONE) {
                    showProgressView();
                }
                progressBarLoading.setProgress(tempProgress);
                if (newProgress == 100) {
                    dismissProgressView();
                }
                super.onProgressChanged(view, newProgress);
            }

            @Override
            public void onReceivedTitle(WebView view, String title) {
                super.onReceivedTitle(view, title);
                ALog.iLk("onReceivedTitle=" + title);
                if (!NetworkUtils.getInstance().checkNet() && TextUtils.isEmpty(notiTitle)) {
                    notiTitle = getString(R.string.net_work_notuse);
                } else if (!TextUtils.isEmpty(title)) {
                    //优化的方式：isLoadeError
                    if (title.length() > TITLE_MIN_LENGTH || title.contains(TITLE_ERROR_1) || TITLE_ERROR_2.equals(title) || TITLE_ERROR_3.equals(title)) {
                        return;
                    }
                    if (!TextUtils.equals(notiTitle, "签到")) {
                        notiTitle = title;
                    }
                }
                mCommonTitle.setTitle(notiTitle);
                setLog();
            }
        });

        //        webViewCenter.setDownloadListener(new MyDownloadListener());

        webViewCenter.setWebViewClient(new MyWebViewClient());
    }

    /**
     * 跳转连接是apk直接下载安装
     *
     * @param url
     */
    private void downApk(String url, String fileName, boolean isApk, long contentLength) {
        if (!TextUtils.isEmpty(url) && getActivity() != null && !getActivity().isFinishing()) {
            if (TextUtils.isEmpty(fileName)) {
                fileName = FileUtils.getFileName(url);
            }
            String savePath = SDCardUtil.getInstance().getSDCardAndroidRootDir() + "/" + FileUtils.APP_ROOT_DIR_PATH + fileName;
            final File appFile = new File(savePath);
            if (FileUtils.fileExists(savePath) && appFile.length() > 0) {
                openFile(getActivity(), appFile);
                return;
            }
            NewDownloadManagerUtils.getInstanse().down(getActivity(), url, savePath, isApk, contentLength);

        }
    }

    private void openFile(Activity activity, File file) {
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            Uri data;
            // 判断版本大于等于7.0
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                data = FileProvider.getUriForFile(activity.getApplicationContext(), activity.getPackageName() + ".fileProvider", file);
                // 给目标应用一个临时授权
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            } else {
                data = Uri.fromFile(file);
            }
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setDataAndType(data, "application/vnd.android.package-archive");
            activity.startActivity(intent);
        } catch (Exception e) {
            ALog.printStackTrace(e);
        }
    }

    /**
     * show process
     */
    protected void showProgressView() {
        AlphaAnimation alphaAnimation = new AlphaAnimation(0, 1.0f);
        alphaAnimation.setDuration(200);
        progressBarLoading.startAnimation(alphaAnimation);
        progressBarLoading.setVisibility(View.VISIBLE);

    }

    @Override
    public void onRefresh() {
        if (!TITLE_ERROR_4.equals(webViewCenter.getUrl())) {
            webViewCenter.reload();
        } else if (isLoadeError) {
            webViewCenter.loadUrl(mUrl);
        }

        Disposable disposable = DzSchedulers.mainDelay(new Runnable() {
            @Override
            public void run() {
                swipeLayout.setRefreshing(false);
            }
        }, 4000);

        composite.addAndDisposeOldByKey("onRefresh", disposable);
    }

    /**
     * 下载监听。
     */
    private class MyWebViewDownLoadListener implements DownloadListener {
        @Override
        public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype, long contentLength) {
            Uri uri = Uri.parse(url);
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(intent);
        }
    }

    /**
     * dismiss progress
     */
    protected void dismissProgressView() {
        anim = new AlphaAnimation(1.0f, 0);
        anim.setDuration(200);
        anim.setAnimationListener(new Animation.AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                progressBarLoading.setVisibility(View.GONE);
            }
        });
        progressBarLoading.startAnimation(anim);
        progressBarLoading.setVisibility(View.VISIBLE);

    }

    /**
     * 设置监听
     */
    @Override
    protected void setListener() {
        statusView.setNetErrorClickListener(new StatusView.NetErrorClickListener() {
            @Override
            public void onNetErrorEvent(View v) {
                if (!TextUtils.isEmpty(mUrl)) {
                    isLoadeError = false;
                    webViewCenter.setVisibility(View.VISIBLE);
                    if (!TextUtils.isEmpty(webViewCenter.getUrl()) && !TITLE_ERROR_4.equals(webViewCenter.getUrl())) {
                        webViewCenter.reload();
                    } else {
                        webViewCenter.loadUrl(mUrl);
                    }
                }
            }
        });
        mCommonTitle.setLeftClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        mCommonTitle.setRightClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                ModelAction.checkElseGoHome(CenterDetailActivity.this);
                hideInput(webViewCenter);
                finish();
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    public void onBackPressed() {
        if (webViewCenter != null && webViewCenter.canGoBack()) {
            String url = webViewCenter.getUrl();
            ALog.cmtDebug("url---->" + url);

            webViewCenter.goBack();

            Disposable disposable = DzSchedulers.mainDelay(new Runnable() {
                @Override
                public void run() {
                    String title = webViewCenter.getTitle();
                    if (!TextUtils.isEmpty(title)) {
                        mCommonTitle.setTitle(title);
                    }
                }
            }, 500);


            composite.addAndDisposeOldByKey("onBackPressedDisposable", disposable);

        } else {
            ModelAction.checkElseGoHome(this);
            hideInput(webViewCenter);
            finish();
        }
    }

    @Override
    protected void onDestroy() {
        if (null != webManager) {
            webManager.destroy();
        }
        if (anim != null) {
            anim.setAnimationListener(null);
        }
        super.onDestroy();
    }

    /**
     * show activity
     *
     * @param context     context
     * @param path        path
     * @param title       title
     * @param positionId  positionId
     * @param isSec       isSec
     * @param operateFrom operateFrom
     * @param partFrom    partFrom
     * @param webFrom     webFrom
     */
    public static void show(Context context, String path, String title, String positionId, boolean isSec, String operateFrom, String partFrom, String webFrom) {
        Intent intent = new Intent();
        intent.putExtra("url", path);
        intent.putExtra("notiTitle", title);
        intent.putExtra("positionId", positionId);
        intent.putExtra("isSecond", isSec);
        intent.putExtra("web", webFrom);
        //打点
        intent.putExtra(LogConstants.KEY_OPERATE_FROM, operateFrom);
        intent.putExtra(LogConstants.KEY_PART_FROM, partFrom);

        intent.setClass(context, CenterDetailActivity.class);
        context.startActivity(intent);
        BaseActivity.showActivity(context);
    }

    /**
     * 注意 此方法 会清掉链接上的私有参数
     *
     * @param context context
     * @param url     url
     * @param title   title
     */
    public static void show(Context context, String url, String title) {
        Intent intent = new Intent();
        intent.putExtra("url", url);
        intent.putExtra("notiTitle", title);
        intent.setClass(context, CenterDetailActivity.class);
        context.startActivity(intent);
        BaseActivity.showActivity(context);
    }

    /**
     * 该方法会  会清掉连接上带的所有参数
     *
     * @param context context
     * @param url     url
     */
    public static void show(Context context, String url) {
        Intent intent = new Intent();
        intent.putExtra("url", url);
        intent.setClass(context, CenterDetailActivity.class);
        context.startActivity(intent);
        BaseActivity.showActivity(context);
    }

    /**
     * 带参数打开url
     *
     * @param context context
     * @param url     url
     * @param priMap  需要带的私参
     */
    public static void showWithPriMap(Context context, String url, HashMap<String, Object> priMap) {
        Intent intent = new Intent();
        intent.putExtra("url", url);
        intent.putExtra("priMap", priMap);
        intent.setClass(context, CenterDetailActivity.class);
        context.startActivity(intent);
        BaseActivity.showActivity(context);
    }

    @Override
    public void onEventMainThread(EventMessage event) {
        String type = event.getType();
        int requestCode = event.getRequestCode();
        Bundle bundle = event.getBundle();
        switch (requestCode) {
            case EventConstant.RECOMMEND_REQUEST_CODE:
                if (this.getClass().getName().equals(type)) {
                    if (null == bundle) {
                        ToastAlone.showShort(R.string.chapter_list_error);
                        return;
                    }
                    CatalogInfo info = (CatalogInfo) bundle.getSerializable(EventConstant.CATALOG_INFO);
                    if (null == info) {
                        ToastAlone.showShort(R.string.chapter_list_error);
                        return;
                    }
                    ReaderUtils.intoReader(getActivity(), info, info.currentPos);
                }
                break;
            case EventConstant.FINISH_ACTIVITY_REQUEST_CODE:
                if (this.getClass().getName().equals(type)) {
                    finish();
                }
                break;
            case EventConstant.LOGIN_SUCCESS_FINISH_REFRESH_SIGN_PAGE_REQUESTCODE:
                if (EventConstant.TYPE_CENTER_DETAIL_ACTIVITY.equals(type)) {
                    needClearHistory = true;
                    initData2();
                }
                break;
            case EventConstant.CODE_VIP_OPEN_SUCCESS_REFRESH_STATUS:
                needClearHistory = true;
                initData2();
                break;
            default:
                break;
        }
    }

    /**
     * 给H5放入头像和昵称，为了保证登录用户我的Vip图像昵称保持一致
     */
    private void loadLocalHeadAndNickName() {
        try {
            if (webViewCenter != null) {
                String coverWap = SpUtil.getinstance(this).getLoginUserCoverWapByUserId();
                String nickName = SpUtil.getinstance(this).getLoginUserNickNameByUserId();
                if (coverWap == null) {
                    coverWap = "";
                }
                if (nickName == null) {
                    nickName = "";
                }
                if (!TextUtils.isEmpty(coverWap) || !TextUtils.isEmpty(nickName)) {
                    callWebViewByJs(getActivity(), webViewCenter, "javascript:headCallBack('" + coverWap + "','" + nickName + "')");
                }
            }
        } catch (Exception e) {
            ALog.printStackTrace(e);
        }

    }

    /**
     * 隐藏软键盘
     *
     * @param view view
     */
    public void hideInput(View view) {
        try {
            if (view != null) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null) {
                    boolean isOpen = imm.isActive();
                    if (isOpen) {
                        ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(view.getWindowToken(), 0);
                    }
                }
            }
        } catch (Exception ignore) {
        }
    }

    /**
     * 自定义的 WebViewClient
     */
    private class MyWebViewClient extends WebViewClient {


        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            try {
                if (!TextUtils.isEmpty(url) && url.contains("/asg/portal/packPrice/list.do")) {
                    Uri parse = Uri.parse(url);
                    String json = parse.getQueryParameter("json");
                    if (TextUtils.isEmpty(json)) {
                        CenterDetailActivity.show(getActivity(), url);
                        return true;
                    }
                }
                if (!url.startsWith("http:") && !url.startsWith("https:")) {
                    try {
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                        startActivity(intent);
                    } catch (Exception e) {
                        ThirdPartyLog.reportError(new Exception("dz:CenterDetailActivity", e));
                    }
                } else {
                    isLoadeError = false;
                    //                        if (url.endsWith("apk")) {//去下载
                    //                            downApk(url, "", true);
                    //                        } else {
                    view.loadUrl(url);
                    //                        }
                }
                if (url.contains("asg/portal/watchaward/list.do?")) {
                    SpUtil.getinstance(getActivity()).markTodayByKey(SpUtil.DZ_TODAY_LUCK_DRAW);
                }
                return true;
            } catch (Exception e) {
                ALog.cmtDebug("Exception:" + e.toString());
            }
            return false;
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            progressBarLoading.setVisibility(View.VISIBLE);
            super.onPageStarted(view, url, favicon);
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            progressBarLoading.setVisibility(View.GONE);
            //                if (webViewCenter.canGoBack() || webViewCenter.canGoForward()) {
            //                    pvLog(url);
            //                }

            // 当web可以回退的时候，不允许又滑退出。
            setSwipeBackEnable(!(webViewCenter != null && webViewCenter.canGoBack()) && AppConst.isIsMainActivityActive());

            //页面加载完成时结束刷新动作
            swipeLayout.setRefreshing(false);

            if (isLoadeError) {
                statusView.showNetError();
            } else {
                statusView.showSuccess();
            }
            super.onPageFinished(view, url);
            //给H5页面放入头像和昵称
            loadLocalHeadAndNickName();

            //网络错误时，添加标题
            if (!NetworkUtils.getInstance().checkNet() && TextUtils.isEmpty(notiTitle)) {
                notiTitle = getString(R.string.net_work_notuse);
            }
            mCommonTitle.setTitle(notiTitle);
        }

        @Override
        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
            ALog.iWz(TAG + "--onReceivedError " + "description:" + description + " failingUrl:" + failingUrl);
            view.stopLoading();
            isLoadeError = true;
            callWebViewByJs(getActivity(), webViewCenter, "javascript:document.body.innerHTML=\"\"");
            //界面加载出错结束刷新动作
            swipeLayout.setRefreshing(false);
        }

        @Override
        public void doUpdateVisitedHistory(WebView view, String url, boolean isReload) {
            super.doUpdateVisitedHistory(view, url, isReload);
            if (needClearHistory) {
                needClearHistory = false;
                webViewCenter.clearHistory();// 清除，为了解决签到页面登录成功之后,返回回回退上一级页面的问题
            }
        }

        @Override
        public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
            super.onReceivedSslError(view, handler, error);
            ALog.dWz("onReceivedSslError  error = " + error);
            ThirdPartyLog.onEventValueSSLError(getContext(), mUrl);
        }
    }

    /**
     * 下载回调
     *
     * @author zhenglk
     */
    private class MyDownloadListener implements DownloadListener {
        @Override
        public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype, long contentLength) {
            HashMap<String, String> map = new HashMap<String, String>();
            map.put("sourceurl", webViewCenter.getUrl());
            map.put("downloadurl", url);
            map.put("downloadfiletype", mimetype);
            map.put("downloaddisposition", contentDisposition);
            DzLog.getInstance().logEvent(LogConstants.EVENT_WEBAPKXZ, map, "");
            if (!TextUtils.isEmpty(url)) {
                String fileName = getFileName(url);

                //下载时  还是使用原来的url。
                if (!TextUtils.isEmpty(fileName)) {
                    downApk(url, fileName, true, contentLength);
                    return;
                }
                if (!TextUtils.isEmpty(mimetype)) {
                    //下载url是apk
                    if ("application/vnd.android.package-archive".equals(mimetype)) {
                        fileName = FileUtils.getFileName(url);
                        if (fileName.contains(".")) {
                            String[] strs = fileName.split("[.]");
                            if (strs.length > 0) {
                                fileName = strs[0] + ".apk";
                            }
                        } else {
                            fileName = fileName + ".apk";
                        }
                        downApk(url, fileName, true, contentLength);
                    }
                    downApk(url, fileName, false, contentLength);
                    return;
                }
                if (!TextUtils.isEmpty(contentDisposition) && contentDisposition.contains("filename")) {
                    String[] strs = contentDisposition.split("=");
                    if (strs.length > 0) {
                        fileName = strs[1];
                    }
                    if (!TextUtils.isEmpty(fileName) && fileName.endsWith("apk")) {
                        downApk(url, fileName, true, contentLength);
                    }
                }
            }

        }

        private String getFileName(String url) {
            String fileName = "";
            //不带参数的url链接。
            String baseUrlNoParams = "";
            try {
                if (url.contains("?")) {
                    //去掉参数部分。
                    baseUrlNoParams = url.split("[?]")[0];
                }
            } catch (Exception ignore) {
            }

            if ((!TextUtils.isEmpty(baseUrlNoParams) && baseUrlNoParams.endsWith("apk")) || url.endsWith("apk")) {
                fileName = FileUtils.getFileName(url);
            }
            return fileName;
        }
    }

    private static void callWebViewByJs(Activity activity, final WebView webView, final String js) {
        try {
            if (activity == null || TextUtils.isEmpty(js)) {
                return;
            }
            //非主线程，将其放到主线程中
            if (Looper.myLooper() != activity.getMainLooper()) {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        int sdkVersion = Build.VERSION.SDK_INT;
                        if (sdkVersion >= Build.VERSION_CODES.N) {
                            webView.evaluateJavascript(js, new ValueCallback<String>() {
                                @Override
                                public void onReceiveValue(String value) {
                                    //暂时没有处理js结果的需求
                                }
                            });
                        } else {
                            webView.loadUrl(js);
                        }
                    }
                });
            } else {
                int sdkVersion = Build.VERSION.SDK_INT;
                if (sdkVersion >= Build.VERSION_CODES.N) {
                    webView.evaluateJavascript(js, new ValueCallback<String>() {
                        @Override
                        public void onReceiveValue(String value) {
                            //暂时没有处理js结果的需求
                        }
                    });
                } else {
                    webView.loadUrl(js);
                }
            }

        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

}
