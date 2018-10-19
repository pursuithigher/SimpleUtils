package com.dzbook.mvp.UI;

import com.dzbook.mvp.BaseUI;

/**
 * 插件
 *
 * @author wxliao on 18/1/17.
 */

public interface PersonPluginUI extends BaseUI {
    /**
     * 朗读插件状态
     *
     * @param progress progress
     * @param text     text
     */
    void showTtsItem(int progress, String text);

    /**
     * wps插件状态
     *
     * @param progress progress
     * @param text     text
     */
    void showWpsItem(int progress, String text);

    /**
     * 设置Loading状态
     *
     * @param pType 0:隐藏Loading 1:显示Loading 2:显示结果
     */
    void setStatusViewType(int pType);
}
