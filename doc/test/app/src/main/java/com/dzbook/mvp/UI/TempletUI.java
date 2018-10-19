package com.dzbook.mvp.UI;

import android.app.Activity;

import java.util.List;

import hw.sdk.net.bean.store.BeanSubTempletInfo;
import hw.sdk.net.bean.store.BeanTempletInfo;

/**
 * BaseChannelPageFragment UI
 *
 * @author dongdianzhou on 2018/1/11.
 */

public interface TempletUI extends NetRequestBaseUI {

    /**
     * 绑定数据
     *
     * @param section section
     * @param isclear isclear
     */
    void setTempletDatas(List<BeanTempletInfo> section, boolean isclear);

    /**
     * 获取Activity实例
     *
     * @return Activity
     */
    Activity getActivity();

    /**
     * 吐司
     *
     * @param msg 内容
     */
    void showToastMsg(String msg);

    /**
     * 加载状态
     *
     * @param state 0：未加载 1：加载中 2：已加载
     */
    void setLoadDataState(int state);

    /**
     * 隐藏加载动画
     */
    void hideLoading();

    /**
     * 限免加载完成
     *
     * @param state state
     */
    void limitfreeCompelete(String state);

    /**
     * 设置页面状态
     *
     * @param isFailed isFailed
     */
    void setPageState(boolean isFailed);

    /**
     * 领取成功：修改对应的状态
     *
     * @param subTempletInfo subTempletInfo
     */
    void getBookSuccess(BeanSubTempletInfo subTempletInfo);

    /**
     * 返回页面类型
     *
     * @return 页面类型：由于书城和限免都复用这个fragment，用于打点和接口请求
     */
    String getLogModule();

    /**
     * 获取title
     *
     * @return 频道title
     */
    String getLogAdid();

    /**
     * 获取频道id
     *
     * @return 频道id
     */
    String getChannelID();

    /**
     * 获取频道potion
     *
     * @return 频道potion
     */
    String getChannelPosition();

    /**
     * 从缓存中获取频道数据
     *
     * @param channelId channelId
     */
    void getSingleChannelDataFromCanche(String channelId);

    /**
     * 服务器连接失败，显示状态
     *
     * @param isShowEmptyView 是否展示空界面
     */
    void showServerFail(boolean isShowEmptyView);

    /**
     * 服务器返回数据为为空，显示状态
     *
     * @param isShowEmptyView isShowEmptyView
     */
    void showServerEmpty(boolean isShowEmptyView);

    /**
     * 移除网络错误提示
     */
    void onDestroyNetView();
}
