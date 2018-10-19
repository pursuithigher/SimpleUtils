package com.dzbook.mvp.UI;

import com.dzbook.bean.LocalFileBean;
import com.dzbook.mvp.BaseUI;

import java.util.ArrayList;

/**
 * UpLoadActivity的UI接口
 *
 * @author wxliao on 17/10/19.
 */
public interface LocalFileUI extends BaseUI {
    /**
     * 查找失败状态刷新
     */
    void refreshIndexError();

    /**
     * 刷新查找状态
     *
     * @param list        list
     * @param currentPath currentPath
     */
    void refreshIndexInfo(ArrayList<LocalFileBean> list, String currentPath);

    /**
     * 查找失败状态刷新
     */
    void refreshLocalError();

    /**
     * 刷新本地书籍信息
     *
     * @param list        list
     * @param currentPath currentPath
     */
    void refreshLocalInfo(ArrayList<LocalFileBean> list, String currentPath);

    /**
     * 删除书籍
     *
     * @param list list
     */
    void deleteBean(ArrayList<LocalFileBean> list);

    /**
     * 添加书籍
     *
     * @param addedBean addedBean
     */
    void bookAdded(LocalFileBean addedBean);

    /**
     * 刷新选中状态
     */
    void refreshSelectState();

    /**
     * 添加书籍完成
     *
     * @param list list
     */
    void bookAddComplete(ArrayList<LocalFileBean> list);
}
