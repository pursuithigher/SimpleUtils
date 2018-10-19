package com.dzbook.mvp.UI;

import android.app.Activity;

import com.dzbook.mvp.BaseUI;

/**
 * 欢迎页
 */
public interface SplashUI extends BaseUI {
    /**
     * 获取上下文
     *
     * @return activity
     */
    Activity getActivity();

    /**
     * referenceTimeView
     *
     * @param adTime adTime
     */
    void referenceTimeView(int adTime);

    /**
     * 新用户第一次安装 需要弹出用户隐私协议
     * 校验通过后 才走后面的流程
     */
    void splashSecond();

    /**
     * 广告界面
     */
    void loadAd();
}
