package com.dzbook.view.navigation;

/**
 * NavigationAble
 *
 * @author wxliao on 17/3/28.
 */

public interface NavigationAble {
    /**
     * 选中
     */
    void select();

    /**
     * 未选中
     */
    void unSelect();

    /**
     * 显示信息
     */
    void showNewMessage();

    /**
     * 隐藏信息
     */
    void hideNewMessage();

}
