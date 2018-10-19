package com.dzbook.mvp.UI;

import android.graphics.Bitmap;
import android.view.ViewGroup;

import com.dzbook.activity.reader.ReaderActivity;
import com.dzbook.database.bean.CatalogInfo;
import com.dzbook.mvp.BaseUI;
import com.dzbook.mvp.presenter.ReaderPresenter;
import com.dzbook.r.c.AkDocInfo;
import com.dzbook.r.c.AkReaderView;

import hw.sdk.net.bean.cloudshelf.BeanSingleBookReadProgressInfo;

/**
 * author lizhongzhong 2017/8/17.
 */

public interface ReaderUI extends BaseUI {
    /**
     * 获取Activity实例
     *
     * @return Activity
     */
    ReaderActivity getHostActivity();


    //    void applyCopyrightInfo(boolean visible, String text);
    //
    //    void setReaderModeChecked(boolean checked);
    //
    //    void setReaderEyeCareModeChecked(boolean checked);

    //    void setMoreFunctionEnable(boolean enable);

    //    void refreshBookMarkStatus();

    /**
     * 显示阅读进度弹窗
     *
     * @param beanInfo beanInfo
     */
    void showCloudProgressDialog(BeanSingleBookReadProgressInfo beanInfo);

    //    void setLotDownloadEnable(boolean enable);

    /**
     * 加载文档
     *
     * @param doc doc
     */
    void loadDocument(AkDocInfo doc);

    /**
     * 获取文档
     *
     * @return AkDocInfo
     */
    AkDocInfo getDocument();


    /**
     * 开始自动阅读
     *
     * @param anim          anim
     * @param speed         speed
     * @param hideMenuPanel hideMenuPanel
     */
    void startAutoRead(int anim, int speed, boolean hideMenuPanel);

    /**
     * 结束自动阅读
     */
    void finishAutoRead();

    //    void finishVoice();

    //    void startVoice();

    /**
     * 显示菜单面板
     */
    void showMenuPanel();

    /**
     * 隐藏菜单面板
     *
     * @param resetState resetState
     */
    void hideMenuPanel(boolean resetState);

    /**
     * 获取菜单状态
     *
     * @return int
     */
    int getMenuState();

    /**
     * 跳转章节
     *
     * @param chapter  chapter
     * @param isNext   isNext
     * @param partFrom partFrom
     */
    void turnChapter(CatalogInfo chapter, boolean isNext, String partFrom);

    /**
     * 获取readerView
     *
     * @return readerView
     */
    AkReaderView getReader();

    /**
     * 弹出安装插件弹窗
     */
    void showPluginDialog();

    /**
     * 设置菜单状态
     *
     * @param state state
     */
    void setMenuState(int state);

    /**
     * 获取ReaderPresenter
     *
     * @return ReaderPresenter
     */
    ReaderPresenter getPresenter();

    // =================================基类实现==========================

    /**
     * 根据需要，显示引导图
     *
     * @param viewGroup viewGroup
     */
    void showUserGuideIfNeed(ViewGroup viewGroup);

    /**
     * 全屏设置
     *
     * @param showSys 0，全屏；1，显示状态栏
     */
    void applyFullscreen(int showSys);

    /**
     * 阅读器样式设置
     *
     * @param colorStyle colorStyle
     */
    void applyColorStyle(int colorStyle);

    /**
     * 阅读器样式设置
     *
     * @param layoutStyle layoutStyle
     */
    void applyLayoutStyle(int layoutStyle);

    /**
     * 横竖屏设置
     *
     * @param orientation orientation
     * @return boolean
     */
    boolean applyScreenOrientation(int orientation);

    //    /**
    //     * 黑夜白天模式@只换阅读器的背景样式，不改亮度
    //     *
    //     * @param readerMode
    //     */
    //    void applyReaderMode(int readerMode);

    //    /**
    //     * 护眼模式@只换阅读器的背景样式，不改亮度
    //     *
    //     * @param readerEyeCareMode
    //     */
    //    void applyReaderEyeCareMode(int readerEyeCareMode);

    /**
     * 设置字体
     *
     * @param fontPath 地址对应字体文件存在时，设置字体为自定义字体，否则为系统字体。
     * @return 是否设置成功
     */
    boolean applyFont(String fontPath);

    /**
     * 字号设置
     *
     * @param index index
     */
    void applyFontSize(int index);

    /**
     * 设置阅读进度
     *
     * @param percent percent
     */
    void applyProgress(float percent);

    /**
     * 设置版权图标
     *
     * @param bitmap bitmap
     */
    void applyCopyrightImg(Bitmap bitmap);

    /**
     * 翻页动画更新
     *
     * @param index index
     */
    void applyAnim(int index);

    //    void refreshScreenOffTimeOutControl();
}
