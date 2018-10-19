package com.dzbook.mvp.UI;

import com.dzbook.mvp.BaseUI;

import java.util.List;

import hw.sdk.net.bean.BeanBookInfo;
import hw.sdk.net.bean.BeanRankTopResBeanInfo;

/**
 * author lizhongzhong 2017/8/28.
 */

public interface RankTopUI extends BaseUI {

    /**
     * 加载失败
     *
     * @param isFirstLoad isFirstLoad
     */
    void setLoadFail(Boolean isFirstLoad);

    /**
     * 设置首次加载排行榜数据
     *
     * @param beanInfo beanInfo
     */
    void setFirstLoadRankTopInfo(BeanRankTopResBeanInfo beanInfo);

    /**
     * 设置数据
     *
     * @param books books
     */
    void setClickRankTopInfo(List<BeanBookInfo> books);

    /**
     * 加载更多完毕
     *
     * @param beanInfo beanInfo
     */
    void setLoadMoreRankTopInfo(BeanRankTopResBeanInfo beanInfo);

    /**
     * 显示加载动画
     */
    void showLoadProgresss();

    /**
     * 隐藏加载动画
     */
    void dismissProgress();

    /**
     * 设置顶部艰巨
     *
     * @param isExistSub isExistSub
     */
    void setLoadProgressMarginTop(boolean isExistSub);

    /**
     * 刷新完毕
     */
    void setPullRefreshComplete();

    /**
     * 移除头布局
     */
    void removeRecycleViewHeader();

    /**
     * 无网界面
     */
    void showNoNetView();
}
