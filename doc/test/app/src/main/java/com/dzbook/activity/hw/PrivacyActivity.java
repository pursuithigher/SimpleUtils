package com.dzbook.activity.hw;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.webkit.ConsoleMessage;
import android.webkit.DownloadListener;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.dzbook.BaseSwipeBackActivity;
import com.dzbook.lib.utils.ALog;
import com.dzbook.sonic.DzWebView;
import com.dzbook.utils.DeviceUtils;
import com.dzbook.utils.NetworkUtils;
import com.dzbook.view.DianZhongCommonTitle;
import com.dzbook.view.common.StatusView;
import com.ishugui.R;
import com.iss.app.BaseActivity;
import com.iss.view.common.ToastAlone;


/**
 * 简单的加载网页的Activity
 * <p>
 * 默认 不带打点
 *
 * @author winzows
 */
public class PrivacyActivity extends BaseSwipeBackActivity {


    private static final String TAG = "PrivacyActivity";
    private static final String DEFAULT_HTTP = "http";
    private DzWebView mWebView;
    private String title;
    private String loadUrl;
    private StatusView statusView;

    private DianZhongCommonTitle mCommonTitle;

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
        setContentView(R.layout.ac_dzweb_activity);
    }

    @Override
    protected void initView() {
        mCommonTitle = findViewById(R.id.include_top_title_item);
        mWebView = findViewById(R.id.mWebView);
        statusView = findViewById(R.id.statusView);
    }

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void initData() {
        Intent getIntent = getIntent();
        if (getIntent != null) {
            title = getIntent.getStringExtra("title");
            loadUrl = getIntent.getStringExtra("url");
        }

        if (TextUtils.isEmpty(loadUrl)) {
            onError();
            return;
        }

        if (!NetworkUtils.getInstance().checkNet()) {
            onError();
            return;
        }

        DeviceUtils.disableAccessibility(this);
        mCommonTitle.setTitle(title + "");

        mWebView.setOnLongClickListener(new View.OnLongClickListener() {

            @Override
            public boolean onLongClick(View view) {
                return true;
            }

        });

//        mWebView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);

        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                super.onReceivedError(view, request, error);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && request != null && error != null) {
                    ALog.eWz(TAG + request.getUrl() + " error= " + error.getDescription());
                    onError();
                }
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (!TextUtils.isEmpty(url)) {
                    try {
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setData(Uri.parse(url));
                        startActivity(intent);
                        return true;
                    } catch (Throwable e) {
                        ALog.printExceptionWz(e);
                    }
                }
                return super.shouldOverrideUrlLoading(view, url);
            }
        });

        mWebView.setWebChromeClient(new WebChromeClient() {
            @Override
            public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
                ALog.eWz(TAG, "consoleMessage " + consoleMessage.message());
                return super.onConsoleMessage(consoleMessage);
            }
        });

        if (checkUrlWhite(loadUrl)) {
            mWebView.loadUrl(loadUrl);
        } else {
            ToastAlone.showLong("url is not on the white list!\n finish this activity!! ");
            finish();
        }


    }

    /**
     * 校验域名 是否在白名单里
     *
     * @param mUrl url
     * @return 是不是？
     */
    private boolean checkUrlWhite(String mUrl) {
        try {
            Uri uri = Uri.parse(mUrl);
            String host = uri.getHost();
            ALog.dWz("checkUrlWhite host is " + host + " url = " + mUrl);
            if (!TextUtils.isEmpty(host)) {
                if (!TextUtils.equals(host, "consumer.huawei.com") && !mUrl.startsWith("file:///android_asset/")) {
                    return false;
                }
            }
        } catch (Throwable e) {
            ALog.printExceptionWz(e);
        }
        return true;
    }


    @Override
    protected void setListener() {
        mCommonTitle.setLeftClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        statusView.setNetErrorClickListener(new StatusView.NetErrorClickListener() {
            @Override
            public void onNetErrorEvent(View v) {
                if (NetworkUtils.getInstance().checkNet() && !TextUtils.isEmpty(loadUrl)) {
                    statusView.setVisibility(View.GONE);
                    initData();
                } else {
                    onError();
                }
            }
        });

        mWebView.setDownloadListener(new DownloadListener() {
            @Override
            public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype, long contentLength) {
                try {
                    Uri uri = Uri.parse(url);
                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    startActivity(intent);
                } catch (Exception e) {
                    ALog.printExceptionWz(e);
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (mWebView != null && mWebView.canGoBack()) {
            mWebView.goBack();
        } else {
            finish();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            mWebView.stopLoading();
            mWebView.clearHistory();
            mWebView.removeAllViews();
            mWebView.destroy();
        } catch (Throwable ignore) {
        }
    }

    /**
     * startActivity
     *
     * @param context context
     * @param url     url
     * @param title   title
     */
    public static void show(Context context, String url, String title) {
        ALog.dWz(TAG, "url=" + url + " title=" + title);
        Intent intent = new Intent();
        intent.putExtra("url", url);
        intent.putExtra("title", title);
        intent.setClass(context, PrivacyActivity.class);
        context.startActivity(intent);
        BaseActivity.showActivity(context);
    }

    @Override
    protected boolean isCustomPv() {
        return true;
    }

    @Override
    public boolean needCheckPermission() {
        return false;
    }

    @Override
    protected boolean isNeedThirdLog() {
        return false;
    }

    /**
     * 异常
     */
    public void onError() {
        statusView.setVisibility(View.VISIBLE);
        statusView.showNetError();
    }
}
