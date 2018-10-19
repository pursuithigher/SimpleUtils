package com.dzbook.mvp.presenter;

import com.dzbook.log.LogConstants;

/**
 * SplashPresenter
 *
 * @author winzows 2018/4/26
 */

public interface SplashPresenter {

    /**
     * 预请求书城数据
     */
    void initSaleBooks();

    /**
     * 预请求书城数据（第一页数据）
     */
    void advanceStoreData();

    /**
     * 内置书接口处理
     */
    void buildInBook();

    /**
     * 跳转
     */
    void jump();

    /**
     * 去引导页
     */
    void intentToGuideActivity();

    /**
     * 判空
     *
     * @return boolean
     */
    boolean isActivityEmpty();

    /**
     * 展示协议
     */
    void showAgreementDialog();

    /**
     * 处理跳转
     */
    void handleIntent();

    /**
     * 启动模式
     */
    void handleLaunchMode();

    /**
     * 启动模式
     *
     * @return 启动模式
     */
    @LogConstants.LaunchSm
    int launchMode();

    /**
     * 华为打点使用
     *
     * @return int
     */
    int getLaunchFrom();

    /**
     * 打点使用，搜索，签到，阅读，书城
     *
     * @return string
     */
    String getLaunchTo();


    /**
     * 销毁
     */
    void destroy();

    /**
     * 权限
     */
    void onPermissionGrant();

    /**
     * 内存泄漏检查插件
     */
    void fixLeaked();

    /**
     * 设置app是否在前台展示
     *
     * @param show show
     */
    void setShow(boolean show);

    /**
     * 处理finish事件
     */
    void onEventFinish();

    /**
     * 处理配置切换
     *
     * @param screenHeightDp 高度
     */
    void onConfigurationChanged(int screenHeightDp);
}
