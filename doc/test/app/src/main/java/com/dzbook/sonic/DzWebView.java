package com.dzbook.sonic;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.accessibility.AccessibilityManager;
import android.view.animation.Interpolator;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Scroller;

import com.dzbook.lib.utils.ALog;
import com.dzbook.utils.SpUtil;

import java.lang.reflect.Method;
import java.util.HashMap;

/**
 * 可以缓慢滑动到顶部的webview
 *
 * @author Winzows on 2017/5/12.
 */

public class DzWebView extends WebView {

    private static final Interpolator S_INTERPOLATOR = new Interpolator() {
        @Override
        public float getInterpolation(float t) {
            t -= 1.0f;
            return t * t * t * t * t + 1.0f;
        }
    };
    private Scroller mScroller;
    private boolean mPreventParentTouch = true;

    /**
     * 构造函数
     *
     * @param context  context
     * @param attrs    attrs
     * @param defStyle defStyle
     */
    public DzWebView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    /**
     * 构造函数
     *
     * @param context context
     * @param attrs   attrs
     */
    public DzWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    /**
     * 构造函数
     *
     * @param context context
     */
    public DzWebView(Context context) {
        super(context);
        init();
    }

    @Override
    public void loadUrl(String url) {
        String token = SpUtil.getinstance(getContext()).getAppToken();
        if (!TextUtils.isEmpty(token)) {
            HashMap<String, String> map = new HashMap<>(10);
            map.put("t", token);
            super.loadUrl(url, map);
        } else {
            super.loadUrl(url);
        }
    }


    private void init() {
        mScroller = new Scroller(getContext(), S_INTERPOLATOR);
        disableAccessibility();
        initWebViewSetting();
        setOverScrollMode(OVER_SCROLL_NEVER);

    }

    @SuppressLint("SetJavaScriptEnabled")
    private void initWebViewSetting() {
        WebSettings webSettings = getSettings();
        if (webSettings != null) {
            webSettings.setAllowFileAccess(false);
            webSettings.setJavaScriptEnabled(true);
            webSettings.setSupportZoom(false);
            webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);
            webSettings.setSavePassword(false);
            webSettings.setUseWideViewPort(true);
            webSettings.setDomStorageEnabled(true);
            webSettings.setLoadWithOverviewMode(true);
            webSettings.setSupportMultipleWindows(false);
            webSettings.setLoadsImagesAutomatically(true);

            //            setScrollBarStyle(WebView.SCROLLBARS_INSIDE_OVERLAY);
            setFocusable(true);
            setBackgroundColor(0);
            //            setVerticalScrollBarEnabled(false);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                removeJavascriptInterface("searchBoxJavaBridge_");
                webSettings.setAllowContentAccess(true);
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                webSettings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
            }
        }

    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {

        if (!mPreventParentTouch) {
            int i = ev.getAction();
            if (i == MotionEvent.ACTION_MOVE || i == MotionEvent.ACTION_DOWN) {
                requestDisallowInterceptTouchEvent(true);
                return true;
            } else if (i == MotionEvent.ACTION_UP || i == MotionEvent.ACTION_CANCEL) {
                requestDisallowInterceptTouchEvent(false);
                mPreventParentTouch = true;
            }
        }

        return super.onTouchEvent(ev);
    }

    /**
     * 开始滚动
     *
     * @param startY 从哪里开始 滑动多少的距离
     */
    public void startScroll(int startY) {
        mScroller.startScroll(0, startY, 0, -startY, 1200);
        invalidate();
    }

    @Override
    public void computeScroll() {
        if (mScroller.computeScrollOffset()) {
            scrollTo(mScroller.getCurrX(), mScroller.getCurrY());
            postInvalidate();
        }
        super.computeScroll();
    }

    private void disableAccessibility() {
        if (Build.VERSION.SDK_INT == 17/*4.2 (Build.VERSION_CODES.JELLY_BEAN_MR1)*/) {
            Context context;
            if ((context = getContext()) != null) {
                try {
                    AccessibilityManager am = (AccessibilityManager) context.getSystemService(Context.ACCESSIBILITY_SERVICE);
                    if (!am.isEnabled()) {
                        return;
                    }
                    Method set = am.getClass().getDeclaredMethod("setState", int.class);
                    set.setAccessible(true);
                    set.invoke(am, 0);/**{@link AccessibilityManager#STATE_FLAG_ACCESSIBILITY_ENABLED}*/
                } catch (RuntimeException e) {
                    ALog.printStackTrace(e);
                } catch (Exception e) {
                    ALog.printExceptionWz(e);
                }
            }
        }
    }
}

