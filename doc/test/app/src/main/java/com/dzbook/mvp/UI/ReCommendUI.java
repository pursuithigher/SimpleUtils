package com.dzbook.mvp.UI;

import android.app.Activity;

import com.dzbook.mvp.BaseUI;

/**
 * ReCommendUI
 *
 * @author Winzows on 2017/9/5.
 */

public interface ReCommendUI extends BaseUI {
    /**
     * 获取上下文
     *
     * @return activity
     */
    Activity getActivity();

    /**
     * 加载url
     *
     * @param url url
     */
    void onLoadUrl(String url);

}
