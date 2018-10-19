package com.dzbook.activity.hw;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;

import com.dzbook.lib.utils.CompatUtils;
import com.huawei.hwCloudJs.api.ActionbarClickListenner;
import com.huawei.hwCloudJs.api.IJsActionbar;
import com.ishugui.R;

import java.io.Serializable;

/**
 * JsActionbarClass
 * @author lizz 2018/7/27.
 */

public class JsActionbarClass implements IJsActionbar, Serializable {

    private Context mContext;

    @Override
    public void setContext(Context context) {
        mContext = context;
    }

    @Override
    public String textcolorID() {
        return "color_100_1a1a1a";
    }

    @Override
    public boolean isShow() {
        return true;
    }

    @Override
    public Drawable getStartIcon() {
        return CompatUtils.getDrawable(mContext, R.drawable.hw_back);
    }

    @Override
    public String getActionbarTitle() {
        return mContext.getResources().getString(R.string.app_name);
    }

    @Override
    public ActionbarClickListenner getStartClickListenner() {
        return new ActionbarClickListenner() {
            @Override
            public void onActionbarClickListener(String s, String s1, WebView webView, String s2) {
                if (null != webView) {
                    if (webView.canGoBack()) {
                        webView.goBack();
                    } else {
                        Context context = webView.getContext();
                        if (context instanceof Activity) {
                            ((Activity) context).finish();
                        }
                    }
                }
            }
        };
    }

    @Override
    public void setControlIcon(Context context, WebView webView, String s, View view, Menu menu) {

    }

    @Override
    public void handleOptionsItemSelected(MenuItem menuItem) {

    }
}
