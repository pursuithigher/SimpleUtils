package com.dzbook.mvp.UI;

import com.dzbook.mvp.BaseUI;

import java.util.ArrayList;

import hw.sdk.net.bean.type.BeanMainTypeLeft;
import hw.sdk.net.bean.type.BeanMainTypeRight;

/**
 * 分类 一级页面
 *
 * @author Winzows  2018/2/27
 */

public interface NativeTypeIndexUI extends BaseUI {

    /**
     * 错误页
     */
    void onError();

    /**
     * 加载成功
     */
    void showView();

    /**
     * 空见面
     */
    void showEmpty();

    /**
     * 绑定左边数据
     *
     * @param list list
     */
    void bindLeftCatalogData(ArrayList<BeanMainTypeLeft> list);

    /**
     * 绑定右侧数据
     *
     * @param list         list
     * @param categoryId   categoryId
     * @param categoryName categoryName
     * @param leftPosition leftPosition
     */
    void bindRightCatalogData(ArrayList<BeanMainTypeRight> list, String categoryId, String categoryName, int leftPosition);

    /**
     * 选中操作
     *
     * @param categoryIndexBean categoryIndexBean
     * @param leftPosition      leftPosition
     */
    void onCatalogSelect(BeanMainTypeLeft categoryIndexBean, int leftPosition);

    /**
     * 请求数据
     */
    void onRequestData();
}
