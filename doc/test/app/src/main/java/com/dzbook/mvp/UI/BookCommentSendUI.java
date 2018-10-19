package com.dzbook.mvp.UI;

import android.app.Activity;

import com.dzbook.mvp.BaseUI;

import java.util.ArrayList;

import hw.sdk.net.bean.bookDetail.BeanCommentInfo;

/**
 * BookCommentSendActivity的UI接口
 *
 * @author Winzows on 2017/11/30.
 */

public interface BookCommentSendUI extends BaseUI {
    /**
     * 获取activity实例
     *
     * @return activity
     */
    Activity getActivity();

    /**
     * 通知图书详情页刷新
     *
     * @param infoList infoList
     * @param bookId   bookId
     */
    void notifyBookDetailRefresh(ArrayList<BeanCommentInfo> infoList, String bookId);

    /**
     * 是否显示无网络弹窗
     */
    void isShowNotNetDialog();
}
