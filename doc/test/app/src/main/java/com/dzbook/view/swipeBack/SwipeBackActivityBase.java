package com.dzbook.view.swipeBack;


/**
 * SwipeBackActivityBase
 *
 * @author Yrom
 */
public interface SwipeBackActivityBase {
    /**
     * getSwipeBackLayout
     *
     * @return the SwipeBackLayout associated with this activity.
     */
    SwipeBackLayout getSwipeBackLayout();

    /**
     * setSwipeBackEnable
     *
     * @param enable enable
     */
    void setSwipeBackEnable(boolean enable);

    /**
     * Scroll out contentView and finish the activity
     */
    void scrollToFinishActivity();

}
