package com.dzpay.recharge.net;

import com.dzpay.recharge.netbean.LotOrderPageBeanInfo;
import com.dzpay.recharge.netbean.LotPayOrderBeanInfo;
import com.dzpay.recharge.netbean.OrdersHwBeanInfo;
import com.dzpay.recharge.netbean.OrdersNotifyBeanInfo;
import com.dzpay.recharge.netbean.SingleOrderBeanInfo;
import com.dzpay.recharge.netbean.VipOrdersBeanInfo;
import com.dzpay.recharge.netbean.VipOrdersNotifyBeanInfo;

import org.json.JSONObject;

import java.util.ArrayList;

/**
 * 得到网络请求数据
 *
 * @author lizhongzhong 2013-11-23
 */
public class RechargeLibUtils {
    private static volatile RechargeLibUtils mLib;

    private RechargeRequest mRequest;

    private RechargeLibUtils() {
        mRequest = new RechargeRequest();
    }

    /**
     * 获取实例
     *
     * @return RechargeLibUtils
     */
    public static RechargeLibUtils getInstance() {
        if (mLib == null) {
            synchronized (RechargeLibUtils.class) {
                if (mLib == null) {
                    mLib = new RechargeLibUtils();
                }
            }
        }
        return mLib;
    }


    /**
     * OrdersNotifyBeanInfo
     * @param orderNo 订单号
     * @param result  数据
     * @param desc    描述
     * @return OrdersNotifyBeanInfo
     * @throws Exception 异常
     */
    public OrdersNotifyBeanInfo getOrderNotifyRequestInfo(String orderNo, String result, String desc) throws Exception {
        String json = mRequest.ordersNotify(orderNo, result, desc);
        OrdersNotifyBeanInfo beanInfo = new OrdersNotifyBeanInfo();
        return beanInfo.parseJSON(new JSONObject(json));
    }

    /**
     * 下订单接口
     *
     * @param amountId 充值方式的金额id
     * @return OrdersHwBeanInfo
     * @throws Exception 异常
     */
    public OrdersHwBeanInfo getRequestOrderBeanInfo(String amountId) throws Exception {
        String json = mRequest.makeOrdersRquest(amountId);
        OrdersHwBeanInfo beanInfo = new OrdersHwBeanInfo();
        return beanInfo.parseJSON(new JSONObject(json));
    }

    /**
     * 批量订购提示页面接口
     *
     * @param bookId         书籍id
     * @param indexChapterId 在读章节之后的第一个未下载章节
     * @return LotOrderPageBeanInfo
     * @throws Exception 异常
     */
    public LotOrderPageBeanInfo getLotOrderPageBeanInfo(String bookId, String indexChapterId) throws Exception {
        String json = mRequest.getLotOrderPageRequest(bookId, indexChapterId);
        return new LotOrderPageBeanInfo().parseJSON(new JSONObject(json));
    }

    /**
     * 单章订购扣费-单章订购提示页面接口
     *
     * @param bookId     书籍id
     * @param chapterId  章节id
     * @param autoPay    自动支付
     * @param confirmPay 确认支付
     * @return SingleOrderBeanInfo
     * @throws Exception 异常
     */
    public SingleOrderBeanInfo singleOrderOrSingleOrderPageBeanInfo(String bookId, String chapterId, String autoPay, String confirmPay) throws Exception {
        String json = mRequest.singleOrderOrSingleOrderPageRequest(bookId, chapterId, autoPay, confirmPay);
        return new SingleOrderBeanInfo().parseJSON(new JSONObject(json));
    }

    /**
     * 订购-批量订购
     *
     * @param bookId       书籍id
     * @param chapterId    章节id
     * @param num          数量
     * @param discountRate 折扣比例
     * @return LotPayOrderBeanInfo
     * @throws Exception 异常
     */
    public LotPayOrderBeanInfo lotChapterOrderBeanInfo(String bookId, String chapterId, String num, String discountRate) throws Exception {
        String json = mRequest.lotChapterOrderRequest(bookId, chapterId, num, discountRate);
        return new LotPayOrderBeanInfo().parseJSON(new JSONObject(json));
    }

    /**
     * Vip订单通知
     *
     * @param orderNo      订单号
     * @param result       数据
     * @param desc         描述
     * @param isAutoOpened 是否自动
     * @return VipOrdersNotifyBeanInfo
     * @throws Exception 异常
     */
    public VipOrdersNotifyBeanInfo getVipOrdersNotifyRequestInfo(String orderNo, String result, String desc, int isAutoOpened) throws Exception {
        String json = mRequest.vipOrdersNotify(orderNo, result, desc, isAutoOpened);
        VipOrdersNotifyBeanInfo beanInfo = new VipOrdersNotifyBeanInfo();
        return beanInfo.parseJSON(new JSONObject(json));
    }

    /**
     * Vip下订单接口
     *
     * @param amountId <br>
     *                 充值方式的金额id
     * @return VipOrdersBeanInfo
     * @throws Exception 异常
     */
    public VipOrdersBeanInfo getRequestVipOrderBeanInfo(String amountId) throws Exception {
        String json = mRequest.vipMakeOrdersRequest(amountId);
        VipOrdersBeanInfo beanInfo = new VipOrdersBeanInfo();
        return beanInfo.parseJSON(new JSONObject(json));
    }

    /**
     * 打包订购 一键购 组合购
     *
     * @param buyType     购买方式
     * @param commodityId 商品id
     * @param originate   来源
     * @param bookIds     书籍ids
     * @return String
     * @throws Exception 异常
     */
    public String getPackOrder(String buyType, int commodityId, int originate, ArrayList<String> bookIds) throws Exception {
        String json = mRequest.getPackOrder(buyType, commodityId, originate, bookIds);
        return json;
    }
}
