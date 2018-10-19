package com.dzpay.recharge.bean;

import java.util.HashMap;

/**
 * Recharge 结果
 *
 * @author huangyoubin
 */
public class RechargeMsgResult {

    /**
     * 错误描述
     */
    public static final String ERR_DES = "errdes";

    /**
     * 更多提示信息
     */
    public static final String MORE_DESC = "more_desc";

    /**
     * 错误码, 在RDO充值过程广播通知错误类型。
     */
    public static final String ERR_CODE = "err_code";


    /**
     * 商品id
     */
    public static final String COMMODITY_ID = "commodity_id";
    /**
     * 来源
     */
    public static final String ORIGINATE = "originate";
    /**
     * 书籍id
     */
    public static final String BOOKIDS = "bookIds";
    /**
     * 购买方式
     */
    public static final String BUY_TYPE = "buy_type";
    /**
     * json数据
     */
    public static final String BOOKS_JSON = "booksjson";
    /**
     * 打包订购状态
     */
    public static final String PACK_STATUS = "pack_status";
    /**
     * 打包订购标题
     */
    public static final String PACK_TITLE = "pack_title";
    /**
     * 余额
     */
    public static final String PACK_BALANCE = "pack_balance";
    /**
     * 订购价格
     */
    public static final String PACK_PAY_PRICE = "pack_payPrice";
    /**
     * 还需钱数
     */
    public static final String PACK_COST_PRICE = "pack_costPrice";


    /**
     * serviceUrl
     */
    public static final String SERVICE_URL = "serviceUrl";

    /**
     * 订购状态
     */
    public static final String ORDER_STATE = "order_state";

    /**
     * 1：非确认订购扣费 2：确认订购-扣费
     */
    public static final String CONFIRM_PAY = "confirm_pay";

    /**
     * 是否加入书架 1：否 2：是否
     */
    public static final String IS_ADD_SHELF = "is_add_shelf";

    /**
     * 触发操作 1：点击下一章，2：点击上一章，3：点击目录直接读取某一章。4:批量下载
     */
    public static final String READ_ACTION = "read_action";

    /**
     * 是否阅读器发起 1：是 2：否
     */
    public static final String IS_READER = "is_reader";

    /**
     * 网络地址
     */
    public static final String URL = "url";

    /**
     * 页面来源
     */
    public static final String OPERATE_FROM = "operate_from";

    /**
     * 操作来源 1，详情批量，2，详情目录，3，详情最新章节。4，阅读菜单批量,5，阅读目录,6，阅读翻章 7，其他
     */
    public static final String PART_FROM = "part_from";

    /**
     * 充值金额id
     */
    public static final String RECHARGE_MONEY_ID = "rechargeMoneyId";

    /**
     * 充值方式
     */
    public static final String RECHARGE_WAY = "recharge_way";

    /**
     * 打点用
     */
    public static final String DESC_FROM = "desc_from";

    /**
     * 充值列表json数据
     */
    public static final String REQUEST_JSON = "recharge_list_json";

    /**
     * 错误日志tag
     */
    public static final String ERR_RECORD_TAG = "err_record_tag";

    /**
     * 图书id
     */
    public static final String BOOK_ID = "bookId";

    /**
     * 章节id
     */
    public static final String CHAPTER_BASE_ID = "chapterId";

    /**
     * 章节集合以
     * json数组方式存储
     * ["","",""]
     */
    public static final String CHAPTER_IDS_JSON = "chapter_ids_json";

    /**
     * 总价
     */
    public static final String PAY_TOTAL_PRICE = "totalPrice";
    /**
     * 数量
     */
    public static final String PAY_AFTER_NUM = "afterNum";
    /**
     * 折扣价格
     */
    public static final String PAY_DISCOUNT_PRICE = "discountPrice";
    /**
     * 折扣比例
     */
    public static final String PAY_DISCOUNT_RATE = "discountRate";

    /**
     * dialog弹窗内容
     */
    public static final String STATUS_CHANGE_MSG = "status_change_msg";

    /**
     * statusChange方法中的status标记,用于以后扩展
     * 用于改变dialog样式
     */
    public static final String STATUS_CHANGE = "status_change";

    /**
     * 充值状态
     * {@link RechargeConstants#MAKE_ORDER_FAIL}
     * {@link RechargeConstants#START_RECHARGE}
     */
    public static final String RECHARGE_STATUS = "recharge_status";

    /**
     * 充值订单号
     */
    public static final String RECHARGE_ORDER_NUM = "recharge_order_num";

    /********************************************************************/

    /**
     * appId
     */
    public static final String APP_ID = "appId";

    /**
     * 国家
     */
    public static final String COUNTRY = "country";

    /**
     * 国家
     */
    public static final String LANG = "lang";

    /**
     * 版本
     */
    public static final String VER = "ver";
    /**
     * app版本
     */

    public static final String APP_VER = "app_ver";
    /**
     * token
     */

    public static final String APP_TOKEN = "app_token";

    /**
     * 用户id
     */
    public static final String USER_ID = "userId";

    /**
     * 设备id
     */
    public static final String UTD_ID = "utdid";

    /**
     * 包名
     */
    public static final String P_NAME = "pname";

    /**
     * 渠道号
     */
    public static final String CHANNEL_CODE = "channelCode";

    /**
     * 充值结果json
     */
    public static final String RECHARGE_RESULT_JSON = "result_result_json";

    /**
     * vip开通结果json
     */
    public static final String VIP_PAY_RESULT_JSON = "vip_pay_result_json";

    /**
     * APP需要登录或者token失效 1:是
     **/
    public static final String APP_NEED_LOGIN_OR_TOKEN_INVALID = "app_need_login_or_token_invalid";

    /**
     * 是否直接购买：1，否；2，是
     */
    public static final String AUTO_PAY = "auto_pay";

    /**
     * 是否VIP开通自动续订 1，是；2，否
     */
    public static final String IS_VIP_OPEN_RENEW = "is_vip_open_renew";

    /********************************************************************/

    /**
     * 方法处理是否成功
     */
    public boolean relult = false;

    /**
     * 状态码
     */
    public int what = 400;

    /**
     * 方法中捕获到的异常
     */
    public Exception exception = null;

    /**
     * 错误类型
     */
    public RechargeErrType errType = new RechargeErrType();


    /**
     * 方法中获得的参数
     */
    public HashMap<String, String> map;

    /**
     * 构造
     *
     * @param map 参数
     */
    public RechargeMsgResult(HashMap<String, String> map) {
        if (null != map) {
            this.map = map;
        } else {
            this.map = new HashMap<String, String>();
        }
    }

}
