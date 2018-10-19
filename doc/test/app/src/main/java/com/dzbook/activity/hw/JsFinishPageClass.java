package com.dzbook.activity.hw;

import android.webkit.JavascriptInterface;
import android.webkit.WebView;

import com.dzbook.event.EventBusUtils;
import com.dzbook.event.EventConstant;
import com.huawei.hwCloudJs.api.WebviewObject;

import java.io.Serializable;

/**
 * JsFinishPageClass
 * @author lizz 2018/7/30.
 */
public class JsFinishPageClass implements WebviewObject, Serializable {

    private WebView mWebview;

    /**
     * finish
     */
    @JavascriptInterface
    public void finish() {
        //关闭H5页面
        EventBusUtils.sendMessage(EventConstant.CODE_JS_CALL_FINISH_PAGE);
    }

    @Override public void setWebView(WebView webView) {
        mWebview = webView;
    }
}
