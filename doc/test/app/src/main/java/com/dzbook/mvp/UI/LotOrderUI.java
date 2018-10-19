package com.dzbook.mvp.UI;

import com.dzbook.mvp.BaseUI;
import com.dzpay.recharge.netbean.LotOrderPageBean;
import com.dzpay.recharge.netbean.LotOrderPageBeanInfo;
import com.iss.app.BaseActivity;

/**
 * LotOrderPageActivity的UI接口
 * author lizhongzhong 2017/8/3.
 */

public interface LotOrderUI extends BaseUI {

    /**
     * 设置连载书籍展示信息
     *
     * @param beanInfo beanInfo
     * @param isReader isReader
     */
    void setSerialLotOrderInfo(LotOrderPageBeanInfo beanInfo, boolean isReader);

    /**
     * 设置单本书籍展示信息
     *
     * @param beanInfo beanInfo
     * @param isReader isReader
     */
    void setSingleLotOrderInfo(LotOrderPageBeanInfo beanInfo, boolean isReader);

    /**
     * 设置数据错误界面
     */
    void showDataError();


    /**
     * 选中后购买按钮状态
     *
     * @param position position
     * @param bean     bean
     */
    void onSelected(int position, LotOrderPageBean bean);

    /**
     * 选中位置
     *
     * @param position position
     */
    void setSelection(int position);

    /**
     * 关闭
     * activity
     */
    void finish();

    /**
     * 获取activity实例
     *
     * @return activity
     */
    BaseActivity getHostActivity();
}
