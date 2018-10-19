package com.dzbook.mvp.UI;

import com.dzbook.mvp.BaseUI;
import com.iss.app.BaseActivity;

import hw.sdk.net.bean.reader.MoreRecommendBook;

/**
 * ChaseRecommendMoreActivity的UI接口
 * author lizhongzhong 2018/3/9.
 */

public interface ChaseRecommendMoreUI extends BaseUI {
    /**
     * 获取activty实例
     *
     * @return BaseActivity
     */
    BaseActivity getHostActivity();

    /**
     * 显示加载进度
     */
    void showLoadProgresss();

    /**
     * 隐藏加载进度
     */
    void dismissProgress();

    /**
     * 加载失败
     */
    void setLoadFail();

    /**
     * 加载更多
     *
     * @param beanInfo   beanInfo
     * @param isLoadMore isLoadMore
     */
    void setChaseRecommendMoreInfo(MoreRecommendBook beanInfo, boolean isLoadMore);

    /**
     * 关闭activity
     */
    void myFinish();

    /**
     * 设置标题
     *
     * @param bookName bookName
     */
    void setMyTitle(String bookName);

    /**
     * 下啦刷新完成
     */
    void setPullRefreshComplete();

    /**
     * 无网界面
     */
    void showNoNetView();
}
