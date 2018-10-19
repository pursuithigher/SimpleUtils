package com.dzbook.mvp.UI;

import com.dzbook.mvp.BaseUI;
import com.iss.app.BaseActivity;

import hw.sdk.net.bean.reader.BeanBookRecomment;

/**
 * ChaseRecommendActivity的UI接口
 * author lizhongzhong 2018/3/9.
 */

public interface ChaseRecommendUI extends BaseUI {
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
     * 推荐信息
     *
     * @param bookId   bookId
     * @param beanInfo beanInfo
     */
    void setChaseRecommendInfo(String bookId, BeanBookRecomment beanInfo);

    /**
     * 关闭activity
     */
    void myFinish();

    /**
     * 设置标题
     *
     * @param bookName bookName
     */
    void setTitle(String bookName);

    /**
     * 加载成功界面
     */
    void showSuccess();
}
