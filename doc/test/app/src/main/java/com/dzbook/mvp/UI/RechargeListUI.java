package com.dzbook.mvp.UI;

import com.dzbook.mvp.BaseUI;
import com.dzpay.recharge.netbean.OrdersCommonBean;
import com.dzpay.recharge.netbean.RechargeListBeanInfo;
import com.dzpay.recharge.netbean.RechargeProductBean;
import com.iss.app.BaseActivity;

/**
 * RechargeListUI
 *
 * @author lizz 2017/8/28.
 */

public interface RechargeListUI extends BaseUI {

    /**
     * 获取上下文
     *
     * @return activity
     */
    BaseActivity getHostActivity();

    /**
     * 请求数据成功
     */
    void setRequestDataSuccess();

    /**
     * 请求数据失败，展示重试页面
     */
    void setNetErrorShow();

    /**
     * showLoadProgress
     */
    void showLoadProgress();

    /**
     * 设置充值列表数据
     *
     * @param beanInfo                beanInfo
     * @param defaultSelectedPosition defaultSelectedPosition
     * @param product                 product
     */
    void setRechargeListData(RechargeListBeanInfo beanInfo, int defaultSelectedPosition, RechargeProductBean product);

    /**
     * 设置选中的充值金额
     *
     * @param product product
     */
    void setSelectedRechargeProduct(RechargeProductBean product);

    /**
     * 关闭
     * activity
     */
    void finishActivity();

    /**
     * 绑定数据
     *
     * @param bean bean
     */
    void setOrdersInfo(OrdersCommonBean bean);

    /**
     * 是否在显示网络错误弹窗
     */
    void isShowNotNetDialog();

    /**
     * 设置信息
     *
     * @param title     title
     * @param costPrice costPrice
     * @param payPrice  payPrice
     * @param balance   balance
     */
    void setPackBookOrderInfo(String title, String costPrice, String payPrice, String balance);
}
