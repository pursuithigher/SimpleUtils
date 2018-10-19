package com.dzbook.utils;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;

/**
 * 剪切板
 *
 * @author Blankj
 * blog  : http://blankj.com
 * time  : 2016/09/25
 * desc  : 剪贴板相关工具类
 */
public final class ClipboardUtils {


    private static volatile ClipboardUtils instanse;
    private Context mContext;

    private ClipboardUtils() {

    }

    /**
     * 初始化
     *
     * @param context context
     */
    public void init(Context context) {
        mContext = context;
    }

    /**
     * 单例获取
     *
     * @return 实例
     */
    public static ClipboardUtils getInstanse() {
        if (instanse == null) {
            synchronized (ClipboardUtils.class) {
                if (instanse == null) {
                    instanse = new ClipboardUtils();
                }
            }
        }
        return instanse;
    }

    /**
     * 复制文本到剪贴板
     *
     * @param text 文本
     */
    public void copyText(final CharSequence text) {
        try {
            ClipboardManager clipboard = (ClipboardManager) mContext.getSystemService(Context.CLIPBOARD_SERVICE);
            if (null != clipboard) {
                clipboard.setPrimaryClip(ClipData.newPlainText("text", text));
            }
        } catch (Throwable ignored) {
        }
    }
}
