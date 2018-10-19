package com.dzbook.view;

/**
 * ComScrollViewListener
 *
 * @author wz on 2016/5/26 0026.
 */
public interface ComScrollViewListener {
    /**
     * 监听
     *
     * @param scrollView scrollView
     * @param x          x
     * @param y          y
     * @param oldx       oldx
     * @param oldy       oldy
     */
    void onScrollChanged(ComScrollView scrollView, int x, int y, int oldx, int oldy);
}
