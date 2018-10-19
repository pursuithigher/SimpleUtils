package com.dzbook.mvp.UI;

import com.dzbook.mvp.BaseUI;
import com.dzpay.recharge.netbean.SingleOrderBeanInfo;
import com.iss.app.BaseActivity;

/**
 * author lizhongzhong 2017/8/6.
 */

public interface SingleOrderUI extends BaseUI {

    /**
     * 绑定数据
     *
     * @param bean bean
     */
    void setViewOrderInfo(SingleOrderBeanInfo bean);

    /**
     * 数据加载错误页面
     */
    void showDataError();

    /**
     * 关闭activity
     *
     * @param isNeedAnim 是否要关闭动画
     */
    void finishThisActivity(boolean isNeedAnim);

    /**
     * 获取上下文
     *
     * @return activity
     */
    BaseActivity getHostActivity();
}
