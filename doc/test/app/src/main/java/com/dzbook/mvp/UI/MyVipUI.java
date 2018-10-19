package com.dzbook.mvp.UI;

import com.dzbook.mvp.BaseUI;

import java.util.List;

import hw.sdk.net.bean.vip.VipBookInfo;
import hw.sdk.net.bean.vip.VipUserInfoBean;
import hw.sdk.net.bean.vip.VipUserPayBean;


/**
 * MyVipActivity的UI接口
 *
 * @author gavin
 */
public interface MyVipUI extends BaseUI {

    /**
     * 刷新页面
     *
     * @param userInfoBean    用户信息
     * @param vipUserPayBeans vip购买信息
     * @param vipBookInfoList vip书籍推荐信息
     */
    void updateUI(VipUserInfoBean userInfoBean, List<VipUserPayBean> vipUserPayBeans, List<VipBookInfo> vipBookInfoList);

    /**
     * 设置无数据空页面
     */
    void showEmptyView();

    /**
     * 刷新结束
     */
    void refreshFinish();

    /**
     * 设置无网络界面
     */
    void showNoNetView();

    /**
     * setSelectItem
     * @param position position
     */
    void setSelectItem(int position);
}
