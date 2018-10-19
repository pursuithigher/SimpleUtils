package com.dzbook.mvp.UI;

import com.dzbook.mvp.BaseUI;

/**
 * 网络请求状态统一封装（封装网络不同异常状态、show、hide等各种状态回调）
 */

public interface NetRequestBaseUI extends BaseUI {
    /**
     * 没有链接网络
     *
     * @param isShowEmptyView isShowEmptyView
     */
    void showNoNet(boolean isShowEmptyView);
}
