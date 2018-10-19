package com.dzbook.mvp.UI;

import com.dzbook.database.bean.CatalogInfo;
import com.dzbook.mvp.BaseUI;
import com.iss.app.BaseActivity;

/**
 * BookPageUI
 *
 * @author wxliao on 17/8/16.
 */
public interface BookPageUI extends BaseUI {
    /**
     * 到图书目录
     *
     * @param catalogInfo catalogInfo
     */
    void intoReaderCatalogInfo(CatalogInfo catalogInfo);

    /**
     * 获取Activity
     *
     * @return Activity
     */
    BaseActivity getHostActivity();

}
