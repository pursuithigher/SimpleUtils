package com.dzbook.mvp.UI;

import android.app.Activity;

import com.dzbook.mvp.BaseUI;

import java.util.ArrayList;

import hw.sdk.net.bean.BeanBookInfo;
import hw.sdk.net.bean.type.BeanMainTypeDetail;

/**
 * 分类页面 二级菜单
 *
 * @author Winzows 2018/3/2
 */

public interface NativeTypeDetailUI extends BaseUI {

    /**
     * 获取
     * activity实例
     *
     * @return activity
     */
    Activity getActivity();

    /**
     * 底部书籍信息
     *
     * @param loadType     loadType
     * @param bookInfoList bookInfoList
     */
    void bindBottomBookInfoData(int loadType, ArrayList<BeanBookInfo> bookInfoList);

    /**
     * 错误页
     */
    void onError();

    /**
     * 加载成功
     */
    void showView();

    /**
     * 空页面
     */
    void showEmpty();

    /**
     * 没有更多
     */
    void noMore();

    /**
     * 停止加载
     */
    void stopLoad();

    /**
     * 绑定头部信息
     *
     * @param bean bean
     */
    void bindTopViewData(BeanMainTypeDetail bean);

    /**
     * 点击头部
     */
    void clickHead();

    /**
     * 隐藏加载动画
     */
    void dismissLoadProgress();

    /**
     * 显示加载动画
     */
    void showLoadProgress();

    /**
     * 移除脚布局
     */
    void removeFootView();

}
