package com.dzbook.sonic;

import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;
import android.webkit.URLUtil;
import android.webkit.WebView;

import com.dzbook.activity.CenterDetailActivity;
import com.dzbook.fragment.main.MainStoreFragment;
import com.dzbook.lib.utils.StringUtil;
import com.dzbook.web.ActionEngine;

/**
 * webview工具类
 *
 * @author Winzows on 2017/8/10.
 */

public class DzWebUtil {
    /**
     * 重写overrideUrlLoading方法
     *
     * @param context     context
     * @param newFace     newFace
     * @param webView     webView
     * @param url         url
     * @param operateFrom operateFrom
     * @param partFrom    partFrom
     * @return boolean
     */
    public static boolean overrideUrlLoading(Context context, boolean newFace, WebView webView, String url, String operateFrom, String partFrom) {
        if (newFace) {
            if ((url.startsWith("http://") || url.startsWith("https://") || null == context) && null != webView) {
                //加载网页
                String webFrom = getWebFrom(url, operateFrom);
                String tUrl = StringUtil.putUrlValue(url, "t", System.currentTimeMillis() + "");
                CenterDetailActivity.show(context, tUrl, "", "", false, operateFrom, partFrom, webFrom);
                return true;
            }
        }
        if (URLUtil.isNetworkUrl(url)) {
            Uri u = Uri.parse(url);
            String vJp = u.getQueryParameter("j_p");
            if (!TextUtils.isEmpty(vJp) && "0".equals(vJp)) {
                CenterDetailActivity.show(context, url);
                return true;
            }
        }
        return false;
    }

    private static String getWebFrom(String url, String operateFrom) {
        String webFrom = "";
        Uri parse = Uri.parse(url);
        if (null != parse) {
            String lastPathSegment = parse.getLastPathSegment();
            if (!TextUtils.isEmpty(lastPathSegment)) {
                if ("index.html".equals(lastPathSegment)) {
                    //caimt 精选
                    webFrom = getFrom(operateFrom, webFrom, ActionEngine.TJ_JX, ActionEngine.SC_JX);
                } else if ("boy.html".equals(lastPathSegment)) {
                    //caimt 男频
                    webFrom = getFrom(operateFrom, webFrom, ActionEngine.TJ_NP, ActionEngine.SC_NP);
                } else if ("classify.html".equals(lastPathSegment)) {
                    //caimt 分类
                    webFrom = getFrom(operateFrom, webFrom, ActionEngine.TJ_FL, ActionEngine.SC_FL);
                } else if ("bargain.html".equals(lastPathSegment)) {
                    //caimt 活动
                    webFrom = getFrom(operateFrom, webFrom, ActionEngine.TJ_HD, ActionEngine.SC_HD);
                } else if ("girl.html".equals(lastPathSegment)) {
                    //caimt 女频
                    webFrom = getFrom(operateFrom, webFrom, ActionEngine.TJ_NV_P, ActionEngine.SC_NV_P);
                } else if ("publish.html".equals(lastPathSegment)) {
                    //caimt 优惠尝鲜
                    webFrom = getFrom(operateFrom, webFrom, ActionEngine.TJ_YHCX, ActionEngine.SC_YHCX);
                } else if ("free.html".equals(lastPathSegment)) {
                    //caimt 免费
                    webFrom = getFrom(operateFrom, webFrom, ActionEngine.TJ_MF, ActionEngine.SC_MF);
                } else if ("subject.html".equals(lastPathSegment)) {
                    //caimt 测试专用
                    webFrom = ActionEngine.SC_CSZY;
                }
            }
        }
        return webFrom;
    }

    private static String getFrom(String operateFrom, String webFrom, String tjJx, String scJx) {
        if (MainStoreFragment.TAG.equals(operateFrom)) {
            webFrom = scJx;
        }
        return webFrom;
    }
}
