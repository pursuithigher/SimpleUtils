package com.dzbook.mvp.UI;

import com.dzbook.database.bean.CatalogInfo;
import com.dzbook.mvp.BaseUI;
import com.iss.app.BaseActivity;

/**
 * MissingContentActivity的UI接口
 *
 * @author lizhongzhong 2017/8/16.
 */

public interface MissingContentUI extends BaseUI {

    /**
     * 设置领取按钮状态
     */
    void setAlreadyReceveAward();

    /**
     * 获取
     * activity实例
     *
     * @return activity
     */
    BaseActivity getHostActivity();

    /**
     * 进入阅读器
     *
     * @param catalogInfo 章节信息
     */
    void intoReaderCatalogInfo(CatalogInfo catalogInfo);

    /**
     * 设置缺章信息
     */
    void setDeleteChapterReceiveAwardShow();

    /**
     * setNormalReceiveAwardShow
     */
    void setNormalReceiveAwardShow();

    /**
     * 关闭
     * activity
     */
    void finish();

    /**
     * 设置标题
     *
     * @param pName pName
     */
    void setTitle(String pName);
}
