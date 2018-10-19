package com.dzbook.mvp.presenter;


import android.view.View;
import android.view.ViewGroup;

import com.dzbook.view.PullLoadMoreRecycleLayout;
import com.dzbook.view.type.MainTypeDetailTopView;

import java.util.ArrayList;

import hw.sdk.net.bean.BeanBookInfo;
import hw.sdk.net.bean.type.BeanMainTypeDetail;

/**
 * MainTypeDetailPresenter
 *
 * @author Winzows on 2018/3/2.
 */

public interface MainTypeDetailPresenter {

    /**
     * 请求数据
     *
     * @param loadType   loadType
     * @param filterBean filterBean
     */
    void requestData(int loadType, BeanMainTypeDetail.TypeFilterBean filterBean);

    /**
     * 绑定顶部试图数据
     *
     * @param loadMoreRecyclerViewLinearLayout loadMoreRecyclerViewLinearLayout
     * @param bean                             bean
     * @param filterBean                       filterBean
     * @param defaultSelect                    defaultSelect
     */
    void bindTopViewData(PullLoadMoreRecycleLayout loadMoreRecyclerViewLinearLayout, BeanMainTypeDetail bean, BeanMainTypeDetail.TypeFilterBean filterBean, String defaultSelect);

    /**
     * 绑定底部数据数据
     *
     * @param loadType                         loadType
     * @param loadMoreRecyclerViewLinearLayout loadMoreRecyclerViewLinearLayout
     * @param bookInfoList                     bookInfoList
     */
    void bindBottomBookInfoData(int loadType, PullLoadMoreRecycleLayout loadMoreRecyclerViewLinearLayout, ArrayList<BeanBookInfo> bookInfoList);

    /**
     * 请求开始
     */
    void onRequestStart();

    /**
     * 结速加载
     */
    void stopLoad();

    /**
     * 获取当前的固化信息
     *
     * @return 打点信息
     */
    String getPI();

    /**
     * 添加悬浮view
     *
     * @param loadMoreLayout loadMoreLayout
     * @param view           view
     * @param tipsView       tipsView
     * @return MainTypeDetailTopView
     */
    MainTypeDetailTopView addSuspensionView(PullLoadMoreRecycleLayout loadMoreLayout, ViewGroup view, View tipsView);

    /**
     * 添加SubTitle
     *
     * @return string
     */
    String getSubTitleStr();

    /**
     * onDestroy
     */
    void onDestroy();

    /**
     * 添加RecycleHeaderView
     *
     * @param loadMoreLayout loadMoreLayout
     * @param type           type
     */
    void addRecycleHeaderView(PullLoadMoreRecycleLayout loadMoreLayout, int type);

}
