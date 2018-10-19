package com.dzbook.mvp;

import android.content.Context;
import android.support.annotation.StringRes;

/**
 * BaseUI
 *
 * @author dongdianzhou on 2017/3/29.
 */

public interface BaseUI {

    /**
     * 获取上下文对象
     *
     * @return context
     */
    Context getContext();

    /**
     * 展示dialog
     *
     * @param loadingType {@link DialogConstants.DialogType}
     */
    void showDialogByType(@DialogConstants.DialogType int loadingType);

    /**
     * 展示dialog
     *
     * @param loadingType {@link DialogConstants.DialogType}
     * @param text        展示文本
     */
    void showDialogByType(@DialogConstants.DialogType int loadingType, CharSequence text);

    /**
     * 隐藏dialog
     */
    void dissMissDialog();

    /**
     * 显示提示语
     *
     * @param message message
     */
    void showMessage(String message);

    /**
     * 显示提示语
     *
     * @param resId resId
     */
    void showMessage(@StringRes int resId);

    /**
     * 网络是否连接
     *
     * @return boolean
     */
    boolean isNetworkConnected();

    /**
     * Tag Name
     *
     * @return 自定义的tag
     */
    String getTagName();

}
