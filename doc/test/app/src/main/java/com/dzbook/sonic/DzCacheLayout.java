package com.dzbook.sonic;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.http.SslError;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.webkit.ConsoleMessage;
import android.webkit.SslErrorHandler;
import android.webkit.URLUtil;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.dzbook.lib.utils.ALog;
import com.dzbook.utils.DeviceInfoUtils;
import com.dzbook.utils.DimensionPixelUtil;
import com.dzbook.utils.NetworkUtils;
import com.dzbook.utils.SpUtil;
import com.dzbook.utils.ThirdPartyLog;
import com.dzbook.view.common.StatusView;
import com.dzbook.view.common.loading.RefreshLayout;
import com.dzbook.web.WebManager;
import com.ishugui.R;
import com.iss.app.BaseActivity;

import io.reactivex.android.schedulers.AndroidSchedulers;


/**
 * 封装了自带缓存的Webview 加载进度条 网络错误页面
 *
 * @author Winzows on 2017/8/02.
 */

public class DzCacheLayout extends RelativeLayout {

    private static final String TAG = "DzCacheLayout: ";
    ProgressBar progressbarLoading;
    StatusView statusView;
    RefreshLayout layoutSwipe;
    DzWebView mWebView;
    long[] mHits = new long[2];
    private WebManager webManager;
    private String loadUrl;
    private JsInjectListener mjsInjectListener;
    private RecommendListener mRecommendListener;
    private RefreshLayout.OnRefreshListener onRefreshListener;
    private int currentProgress;
    private boolean isAnimStart;
    private boolean isAnimStop;
    private double mPercent = -10;
    private OnWebLoadListener mListener;

    /**
     * 构造函数
     *
     * @param context context
     */
    public DzCacheLayout(Context context) {
        this(context, null);
    }

    /**
     * 构造函数
     *
     * @param context context
     * @param attrs   attrs
     */
    public DzCacheLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
        initListener(context);
    }

    public void setWebManager(WebManager webManager) {
        this.webManager = webManager;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        //        ALog.eDongdz("**************onInterceptTouchEvent:");
        float y = ev.getRawY();
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (webManager != null) {
                    mPercent = webManager.getCurPercent();
                    if (mPercent != -10) {
                        int webviewHeight = mWebView.getMeasuredHeight();
                        int bannerHeight = (int) (webviewHeight * mPercent + 5);
                        int stateHeight = DeviceInfoUtils.getInstanse().getStatusBarHeight();
                        if (stateHeight <= 0) {
                            stateHeight = DimensionPixelUtil.dip2px(getContext(), 25);
                        }
                        //搜索标题栏48dp+频道高度48dp
                        int titleHeight = DimensionPixelUtil.dip2px(getContext(), 96);
                        int top = stateHeight + titleHeight;
                        int scrollY = mWebView.getScrollY();
                        int bottom = top + bannerHeight - scrollY;
                        if (y > top && y < bottom) {
                            requestDisallowInterceptTouchEvent(true);
                        } else {
                            requestDisallowInterceptTouchEvent(false);
                        }
                        //                        ALog.eDongdz("**************webview:onTouchEvent:webviewHeight:" + webviewHeight
                        //                                + " bannerHeight:" + bannerHeight + " mPercent:" + mPercent +
                        //                                " y:" + y + " top:" + top + "bottom:" + bottom);
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
                break;
            case MotionEvent.ACTION_MOVE:
                break;
            default:
                break;
        }
        return super.onInterceptTouchEvent(ev);
    }


    //    @SuppressLint("SetJavaScriptEnabled")
    private void initView(Context mContext) {
        View uiView = LayoutInflater.from(mContext).inflate(R.layout.lauout_common_webview, this);
        layoutSwipe = uiView.findViewById(R.id.layout_swipe);
        progressbarLoading = uiView.findViewById(R.id.progressbar_loading);
        statusView = uiView.findViewById(R.id.defaultview_nonet);
        mWebView = new DzWebView(getContext().getApplicationContext());
        mWebView.setHorizontalScrollBarEnabled(false);//水平不显示
        mWebView.setVerticalScrollBarEnabled(false); //垂直不显示
        mWebView.setOverScrollMode(OVER_SCROLL_NEVER);
        mPercent = -10;
        layoutSwipe.addView(mWebView, new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        mWebView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onReceivedTitle(WebView view, String title) {
                super.onReceivedTitle(view, title);
                DzCacheLayout.this.onReceivedTitle(view, title);
            }

            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                //                if (SpUtil.getinstance(getContext()).getIsInitTingYUN()) {
                //                    NBSWebChromeClient.initJSMonitor(view, newProgress);
                //                }
                super.onProgressChanged(view, newProgress);
                DzCacheLayout.this.onProgressChanged(view, newProgress);

            }

            @Override
            public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
                if (!NetworkUtils.getInstance().checkNet() && consoleMessage.message().contains("not defined")) {
                    if (getContext() instanceof BaseActivity) {
                        ((BaseActivity) getContext()).showNotNetDialog();
                    }
                }
                ALog.dWz("consoleMessage :" + consoleMessage.message());
                return super.onConsoleMessage(consoleMessage);
            }

        });

        mWebView.setOnLongClickListener(new OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                return true;
            }
        });

        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);

                DzCacheLayout.this.onPageFinished(view, url);
                if (mRecommendListener != null) {
                    mRecommendListener.onPageFinished();
                }
            }


            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {

                return DzCacheLayout.this.shouldOverrideUrlLoading(view, url);
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                DzCacheLayout.this.onPageStarted(view, url, favicon);
            }

            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                super.onReceivedError(view, request, error);
                DzCacheLayout.this.onReceivedError();
                if (mRecommendListener != null) {
                    mRecommendListener.onReceivedError();
                }
            }

            @Override
            public void doUpdateVisitedHistory(WebView view, String url, boolean isReload) {
                super.doUpdateVisitedHistory(view, url, isReload);
                if (mjsInjectListener != null) {
                    mjsInjectListener.onUpdateVisitedHistory();
                }
            }

            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                super.onReceivedSslError(view, handler, error);
                ALog.dWz("onReceivedSslError loadUrl=" + loadUrl + " error= " + error);
                ThirdPartyLog.onEventValueSSLError(getContext(), loadUrl);
            }
        });

    }

    private void initListener(final Context context) {
        //TODO  用户主动刷新 并且wifi环境下 是否选择去更新缓存？
        onRefreshListener = new RefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                final long num = 4000L;
                ALog.dWz(TAG, "onRefresh start loadUrl=" + loadUrl);
                if (NetworkUtils.getInstance().checkNet()) {
                    //TODO  用户主动刷新 并且wifi环境下 是否选择去更新缓存？
                    if (mRecommendListener != null) {
                        mRecommendListener.onRefresh();
                    } else if (!TextUtils.isEmpty(loadUrl) && !"about:blank".equals(loadUrl)) {
                        ALog.dWz(TAG, "onRefresh reLoad: " + loadUrl);
                        loadUrl(loadUrl);
                    }
                } else {
                    if (context instanceof BaseActivity) {
                        ((BaseActivity) context).showNotNetDialog();
                    }
                    layoutSwipe.setRefreshing(false);
                }
                layoutSwipe.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        layoutSwipe.setRefreshing(false);
                    }
                }, num);
            }
        };

        layoutSwipe.setRefreshListener(onRefreshListener);
        statusView.setNetErrorClickListener(new StatusView.NetErrorClickListener() {
            @Override
            public void onNetErrorEvent(View v) {
                if (NetworkUtils.getInstance().checkNet() && !TextUtils.isEmpty(loadUrl)) {
                    mWebView.setVisibility(View.VISIBLE);
                    statusView.setVisibility(View.GONE);
                    mWebView.loadUrl(loadUrl);
                }
            }
        });
    }

    public DzWebView getWebView() {
        return mWebView;
    }

    /**
     * 滚动到顶部
     */
    public void scrollTop() {
        mWebView.startScroll(mWebView.getScrollY());
    }

    @SuppressLint("MissingPermission")
    private boolean shouldOverrideUrlLoading(WebView webView, String url) {
        if (TextUtils.isEmpty(url)) {
            return false;
        }
        ALog.dWz(TAG + " shouldOverrideUrlLoading split after:", url);
        if (URLUtil.isNetworkUrl(url)) {
            if (url.contains("asg/portal/watchaward/list.do?")) {
                SpUtil.getinstance(getContext()).markTodayByKey(SpUtil.DZ_TODAY_LUCK_DRAW);
            }
        }
        return mListener != null && mListener.overrideUrlLoading(webView, url);
    }

    private void onPageStarted(WebView view, String url, Bitmap favicon) {
        layoutSwipe.setRefreshing(true);
    }

    private void onPageFinished(WebView view, String url) {
        layoutSwipe.setRefreshing(false);
    }

    private void onReceivedError() {
        statusView.showNetError();
    }

    private void onReceivedTitle(WebView view, String title) {
        if (mListener != null) {
            mListener.onReceivedTitle(view, title);
        }
    }

    private void onProgressChanged(WebView view, int newProgress) {
        if (progressbarLoading == null) {
            return;
        }
        currentProgress = progressbarLoading.getProgress();
        if (newProgress >= 100 && !isAnimStart) {
            // 防止调用多次动画
            isAnimStart = true;
            progressbarLoading.setProgress(newProgress);
            // 开启属性动画让进度条平滑消失
            startDismissAnimation(progressbarLoading.getProgress());
        } else {
            // 开启属性动画让进度条平滑递增
            if (!isAnimStop) {
                isAnimStop = true;
                startProgressAnimation(newProgress);
            }
        }
    }

    private void startProgressAnimation(int newProgress) {
        if (progressbarLoading.getVisibility() == GONE) {
            progressbarLoading.setVisibility(View.VISIBLE);
        }
        ObjectAnimator animator = ObjectAnimator.ofInt(progressbarLoading, "progress", currentProgress, newProgress);
        animator.setDuration(300);
        animator.setInterpolator(new DecelerateInterpolator());
        animator.start();
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                isAnimStop = false;
            }
        });
    }


    /**
     * 执行消失动画
     *
     * @param progress progress
     */
    protected void startDismissAnimation(final int progress) {
        ObjectAnimator anim = ObjectAnimator.ofFloat(progressbarLoading, "alpha", 1.0f, 0.0f);
        anim.setDuration(800);  // 动画时长
        anim.setInterpolator(new DecelerateInterpolator());     // 减速
        // 关键, 添加动画进度监听器
        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                float fraction = valueAnimator.getAnimatedFraction();      // 0.0f ~ 1.0f
                int offset = 100 - progress;
                progressbarLoading.setProgress((int) (progress + offset * fraction));
            }
        });

        anim.addListener(new AnimatorListenerAdapter() {

            @Override
            public void onAnimationEnd(Animator animation) {
                // 动画结束
                progressbarLoading.setProgress(0);
                progressbarLoading.setVisibility(View.GONE);
                isAnimStart = false;
            }
        });
        anim.start();
    }

    /**
     * 点击事件
     */
    public void onClick() {
        if (NetworkUtils.getInstance().checkNet()) {
            ALog.dWz(TAG, "onReload: " + loadUrl);
            if (!TextUtils.isEmpty(loadUrl) && !"about:blank".equals(loadUrl)) {
                statusView.setVisibility(View.GONE);
                mWebView.loadUrl(loadUrl);
            } else {
                statusView.showNetError();
                if (getContext() instanceof BaseActivity) {
                    ((BaseActivity) getContext()).showNotNetDialog();
                }
            }
        } else {
            if (getContext() instanceof BaseActivity) {
                ((BaseActivity) getContext()).showNotNetDialog();
            }
        }
    }

    /**
     * 加载监听
     */
    public interface OnWebLoadListener {
        /**
         * overrideUrlLoading
         *
         * @param url     url
         * @param webView webView
         * @return 跟httpChromeClient.overrideUrlLoading 返回值解释一致。true：已处理。
         */
        boolean overrideUrlLoading(WebView webView, String url);

        /**
         * 接收到title回调
         *
         * @param webView webView
         * @param title   title
         */
        void onReceivedTitle(WebView webView, String title);
    }


    public void setOnWebLoadListener(OnWebLoadListener listener) {
        mListener = listener;
    }

    /**
     * js注入接口
     */
    public interface JsInjectListener {
        /**
         * 更新访问记录
         */
        void onUpdateVisitedHistory();
    }

    public void setJsInjectListener(JsInjectListener mjsInjectListener1) {
        this.mjsInjectListener = mjsInjectListener1;
    }

    /**
     * 监听
     */
    public interface RecommendListener {
        /**
         * 加载完成接口
         */
        void onPageFinished();

        /**
         * 加载错误
         */
        void onReceivedError();

        /**
         * 刷新
         */
        void onRefresh();
    }

    /**
     * 加载url
     *
     * @param baseUrl baseUrl
     */
    @SuppressLint("SetJavaScriptEnabled")
    public void loadUrl(final String baseUrl) {
        if (TextUtils.isEmpty(baseUrl)) {
            return;
        }

        if (baseUrl.startsWith("http")) {
            loadUrl = baseUrl;
        }
        AndroidSchedulers.mainThread().scheduleDirect(new Runnable() {
            @Override
            public void run() {
                mWebView.loadUrl(baseUrl);
            }
        });
    }


    public void setRecommendListener(RecommendListener mRecommendListener1) {
        this.mRecommendListener = mRecommendListener1;
    }

    /**
     * 停止刷新
     */
    public void stopOnRefresh() {
        if (layoutSwipe != null) {
            layoutSwipe.setRefreshing(false);

        }
    }

    /**
     * 延时结束刷新
     *
     * @param delayTime delayTime
     */
    public void stopOnRefreshDelay(long delayTime) {
        if (layoutSwipe != null) {
            layoutSwipe.postDelayed(new Runnable() {
                @Override
                public void run() {
                    layoutSwipe.setRefreshing(false);
                }
            }, delayTime);
        }
    }

    /**
     * 外面设置下拉刷新监听
     *
     * @param onRefreshListener onRefreshListener
     */
    public void setOnRefreshListener(RefreshLayout.OnRefreshListener onRefreshListener) {
        if (layoutSwipe != null) {
            layoutSwipe.setRefreshListener(onRefreshListener);
        }
    }

    /**
     * 开始刷新
     */
    public void startOnRefresh() {
        if (layoutSwipe != null) {
            layoutSwipe.setRefreshing(true);
        }
    }

    /**
     * 双击刷新
     */
    public void onReClickRefresh() {
        if (NetworkUtils.getInstance().checkNet()) {
            AndroidSchedulers.mainThread().scheduleDirect(new Runnable() {
                @Override
                public void run() {
                    System.arraycopy(mHits, 1, mHits, 0, mHits.length - 1);
                    mHits[mHits.length - 1] = SystemClock.uptimeMillis();
                    if (mHits[1] >= (mHits[0] + 500)) {
                        stopOnRefresh();
                        startOnRefresh();
                        if (null != mWebView) {
                            mWebView.pageUp(true);
                        }
                        if (!TextUtils.isEmpty(loadUrl)) {
                            loadUrl(loadUrl);
                        }
                    }
                }
            });
        } else {
            if (getContext() instanceof BaseActivity) {
                ((BaseActivity) getContext()).showNotNetDialog();
            }
        }
    }
}
