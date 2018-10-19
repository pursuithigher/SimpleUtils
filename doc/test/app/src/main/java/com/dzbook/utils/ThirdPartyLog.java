package com.dzbook.utils;

import android.content.Context;
import android.text.TextUtils;

import com.dzbook.AppConst;
import com.dzbook.lib.utils.ALog;
import com.dzbook.log.LogConstants;
import com.iss.app.BaseActivity;

import java.util.LinkedHashMap;
import java.util.Map;

import hw.sdk.analysis.HwAnalysis;

/**
 * 统计统一从这里调用。方便调试。
 *
 * @author zhenglk
 */
public class ThirdPartyLog {
    /****************************华为打点***********************************/
    /**
     * app打开
     * <p>
     * userid 用户标识 必须 用户ID
     * netType 当前网络类型 必须 1：Wifi或热点；2：运营商网络2G/3G/4G；-1：无网络。
     * from 打开APP的渠道 必须  "渠道来源类型，包括：1、桌面打开2、Push拉起 3、…各种导流渠道"
     * to 打开APP进入的页面 必须  "可以是拉起的页面名称：例如首页、读书页、会员页等"
     */
    public static final String ENTRY_APP = "V001";
    /**
     * 栏目内容点击
     * <p>
     * tabId 一级导航栏ID M
     * tabName 一级导航栏名 M
     * tabPos 一级导航栏位置顺序 M 从左至右，从1开始编号
     * pageId 频道ID M
     * pageName 频道名称 M
     * pagePos 频道位置 M
     * columeID 栏目ID M
     * columeName 栏目名称 M
     * columePos 栏目位置  M
     * columeTemp 栏目类型 M 区分banner等
     * contentID 内容ID M
     * contentName 内容名称 M
     * contentType 内容类型 M
     * netType 网络类型 M 1：Wifi或热点；2：运营商网络2G/3G/4G；-1：无网络。
     */
    public static final String COLUMN_CLICK = "V002";
    /**
     * 电子书阅读场景
     * name 书名
     * openTime 打开时间
     * closeTime 关闭时间
     * time 阅读时长（秒）
     * chapterAmount 阅读章节数
     * from 打开来源  1：详情；2：书架；3：其它
     */
    // FIXME: cmt 2018/5/2 听书的情况
    public static final String READ_SCENE = "V003";
    /**
     * 电子书购买场景
     * <p>
     * contentID  内容ID
     * contentName  内容名称
     * buyType  购买方式  1：全本；2：批量章节；3：单章
     * buyAmount  购买数量  全本和单章数量为1，批量章节为具体章节数
     * money  付费总金额  单位：元（虚拟币折算成元）
     * coupon  代金券消费金额  使用代金券消费金额，如不涉及，值为0
     * virtual  真实消费金额  使用充值的虚拟币消费金额，如不涉及，值为0
     * cash  现金消费金额  直接使用现金消费金额，如不涉及，值为0
     */
    public static final String READ_BUY_SCENE = "V004";

    /**
     * 用户订购VIP包月
     * <p>
     * name VIP包月名称
     * period  包月周期    1个月、3个月、6个月、1年、连续包月
     * startDate  包月生效日期
     * endDate  包月失效日期
     * money  订购金额
     * isAuto  是否自动续费  1：是；2：否
     */
    public static final String VIP_BUY = "V005";

    /**
     * 用户充值虚拟币
     * <p>
     * money    充值金额       单位：元
     * virtual  充值虚拟币数量
     * coupon   赠送代金券数量
     */
    public static final String USER_RECHARGE = "V006";

    /**
     * 活动&专题页访问数据
     * <p>
     * ID    活动/专题ID
     * name  活动/专题名称
     */
    public static final String ACCESS_ACTIVITY = "V007";

    /**
     * 用户签到
     * type  签到类型    1：当天签到；2：补签
     * award 签到所获奖励
     */
    public static final String SIGN = "V008";

    /**
     * 用户喜好选择
     * gender    用户性别
     * bookType  书籍类型
     */
    public static final String YHPH = "V009";


    /****************************华为打点***********************************/

    /**
     * 后10/50/100章的点击量
     */
    public static final String CASH_ORDER_LOT = "b003";
    /**
     * 订购页面订购成功量
     */
    public static final String CASH_ORDER_SUCCESS = "b004";

    /**
     * 书架内容点击量
     */
    public static final String SHELF_ALL_CLICK = "c002";
    /**
     * 书架点击免费专区的点击量
     */
    public static final String SHELF_BOOK = "打开图书";
    // 个人中心
    /**
     * 我的的点击量
     */
    public static final String USERALLCLICK = "c401";
    /**
     * 充值记录的点击量
     */
    public static final String USER_RECORD_RCH = "充值记录";
    /**
     * 代金券列表的点击量
     */
    public static final String USER_LIST_VOUCHERS = "代金券列表";
    /**
     * 用户设置
     */
    public static final String USER_SETTING = "设置";

    //图书详情
    /**
     * 图书详情页的总访问量
     */
    public static final String DTL_ENTRY = "d001";
    /**
     * 批量/全本下载的点击量
     */
    public static final String DTL_LOT = "d002";
    /**
     * 免费试读的点击量
     */
    public static final String DTL_FREE = "d003";
    /**
     * 加入书架的点击量
     */
    public static final String DTL_ADD_BOOK = "d004";
    /**
     * 目录的点击量
     */
    public static final String DTL_CATALOG = "d005";
    //阅读
    /**
     * 阅读页进入图书详情页的点击量
     */
    public static final String READ_DETAIL = "d101";

    //云书架和账户合并统计事件

    /**
     * 书架登录取消操作
     */
    public static final String SHELF_LOGIN_CANCEL_SU = "f003";
    /**
     * 个人中心云书架点击
     */
    public static final String USER_CLOUD_SU = "f012";
    /**
     * 阅读进度提示弹出
     */
    public static final String READER_PROGRESS_SHOW_SU = "f032";
    /**
     * 阅读进度提示确认操作
     */
    public static final String READER_PROGRESS_CONFIRM_SU = "f033";
    /**
     * 阅读进度提示取消操作
     */
    public static final String READER_PROGRESS_CANCEL_SU = "f034";

    /**
     * 同步云书架默认启动
     */
    public static final String SHELF_SYSN_CLOUD_START_SU = "f037";

    /**
     * 推送点击数
     */
    public static final String NOTIFICATION_CLICK_NUMB = "gt002";
    /*********************
     * id
     ****************/
    //底部tab五个按钮
    /**
     * 书架
     */
    public static final String BOOK_SHELF_UMENG_ID = "b_shelf";
    /**
     * 书架管理
     */
    public static final String BOOK_SHELF_MENU_UMENG_ID = "b_shelf_manage";
    /**
     * 书架顶部搜索
     */
    public static final String BOOK_SHELF_SEACH_UMENG_ID = "b_shelf_seach";
    /**
     * 书架顶部菜单
     */
    public static final String BOOK_SHELF_TM_UMENG_ID = "b_shelf_top_menu";
    /**
     * 书架签到
     */
    public static final String BOOK_SHELF_SIGN_UMENG_ID = "b_shelf_sign";

    /**
     * 书架被打开
     */
    public static final String BOOK_SHELF_OPEN_UMENG_ID = "b_shelf_opened";

    /**
     * 书城新手礼包
     */
    public static final String BOOK_STORE_NEWGIFT_ID = "b_store_newgift";


    /**
     * 活动中心
     */
    public static final String ACTIVITY_CENTER_UMENG_ID = "a_center";
    /**
     * 搜索页被打开
     */
    public static final String OPEN_SEACH_UMENG_ID = "seach_opened";
    /**
     * 活动如何
     */
    public static final String ACTIVITY_UMENG_ID = "activity_page";

    /**
     * 查看连续包月记录
     */
    public static final String CONTINUOUS_MONTHLY_STATUS_SEE_DATE = "continuous_monthly_status_see_date";
    /**
     * 取消连续包月
     */
    public static final String CONTINUOUS_MONTHLY_STATUS_CANCEL = "continuous_monthly_status_cancel";
    /**
     * 连续包月状态顶部返回btn
     */
    public static final String CONTINUOUS_MONTHLY_STATUS_BACK = "continuous_monthly_status_back";

    /**
     * 书籍详情
     */
    public static final String BOOK_DETAIL_UMENG_ID = "b_detail";
    /**
     * 阅读器
     */
    public static final String READER_UMENG_ID = "reader_page";
    /**
     * 个人中心首页
     */
    public static final String PERSON_CENTER_MENU_UMENG_ID = "p_center_menu";
    /**
     * 个人中心我的帐号
     */
    public static final String PERSON_CENTER_MYACCOUNT_MENU_UMENG_ID = "p_center_myaccount";
    /**
     * 个人中心云书架
     */
    public static final String PERSON_CENTER_CLOUDSELF_MENU_UMENG_ID = "p_center_cloudself";
    /**
     * 个人中心系统设置
     */
    public static final String PERSON_CENTER_SYSTEMSET_MENU_UMENG_ID = "p_center_systemset";
    /**
     * 礼品中心兑换礼品
     */
    public static final String GIFT_CENTER_EXCHANGE_UMENG_ID = "gift_center_exchange";

    //搜索

    /**
     * 搜索页 搜索按钮
     */
    public static final String SEACH_PAGE_SEACH_ID = "seach_page_seach";
    /**
     * 搜索页 热门搜索
     */
    public static final String SEACH_PAGE_HOT_ID = "seach_page_hot";
    /**
     * 搜索页 搜索历史
     */
    public static final String SEACH_PAGE_HISTORY_ID = "seach_page_history";
    /**
     * 搜索页 搜索结果点击
     */
    public static final String SEACH_PAGE_RESULT_ID = "seach_page_result";

    /**
     * 连续包月顶部title
     */
    public static final String AUTO_ORDER_VIP_TITLE_ID = "auto_order_vip_title_id";
    /*********************id****************/


    /*********************
     * value
     ****************/
    //活动入口 evaluate

    /**
     * 更新弹窗
     */
    public static final String UPDATA_DIALOG_VALUE = "updata_dialog_value";
    /**
     * 退出弹窗
     */
    public static final String EXIT_DIALOG_VALUE = "exit_dialog_value";
    /**
     * 推荐书籍弹窗
     */
    public static final String RECOMMEND_BOOK_DIALOG_VALUE = "recommend_book_dialog_value";

    /**
     * 搜索页
     */
    public static final String BOOK_SHELF_READERINTO_VALUE = "book_shelf_readerinto_value";


    /**
     * 云书架
     */
    public static final String CLOUD_BOOKSHELF_VALUE = "cloud_bookshelf_value";
    /**
     * 本地导入
     */
    public static final String LOCAL_IMPORT_VALUE = "local_import_value";
    /**
     * 书架管理
     */
    public static final String BOOKSHELF_MANAGEMENT_VALUE = "bookshelf_management_value";
    /**
     * 列表模式
     */
    public static final String LIST_MODE_LIST_VALUE = "list_mode_list_value";
    /**
     * grid模式
     */
    public static final String GRID_MODE_LIST_VALUE = "grid_mode_list_value";


    //书籍详情
    /**
     * 返回
     */
    public static final String BOOK_DETAIL_BACK_VALUE = "book_detail_back_value";
    /**
     * 分享
     */
    public static final String BOOK_DETAIL_SHARE_VALUE = "book_detail_share_value";
    /**
     * 简介
     */
    public static final String BOOK_DETAIL_BRIEF_VALUE = "book_detail_brief_value";
    /**
     * 更新
     */
    public static final String BOOK_DETAIL_UPDATA_VALUE = "book_detail_updata_value";
    /**
     * 目录
     */
    public static final String BOOK_DETAIL_CATALOG_VALUE = "book_detail_catalog_value";
    /**
     * 批量下载
     */
    public static final String BOOK_DETAIL_DOWNLOAD_VALUE = "book_detail_download_value";
    /**
     * 开始阅读
     */
    public static final String BOOK_DETAIL_START_READER_VALUE = "book_detail_start_reader_value";
    /**
     * 加入书架
     */
    public static final String BOOK_DETAIL_JOIN_BOOKSHELF_VALUE = "book_detail_join_bookshelf_value";
    /*********************
     * 礼品中心
     *******************/
    /**
     * 兑换礼品按钮
     */
    public static final String GIFT_CENTER_EXCHANGE_VALUE = "gift_center_exchange_value";

    /*********************
     * 个人中心
     *******************/
    /**
     * 我的账户
     */
    public static final String PERSON_CENTER_MYACCOUNT_VALUE = "person_center_myaccount_value";
    /**
     * 云书架
     */
    public static final String PERSON_CENTER_CLOUDSELF_VALUE = "person_center_cloudself_value";
    /**
     * 关闭夜间模式
     */
    public static final String PERSON_CENTER_READ_MODE_EYE_CARE_CLOSED_VALUE = "person_center_read_mode_eye_care_closed_value";
    /**
     * 打开护眼模式
     */
    public static final String PERSON_CENTER_READ_MODE_EYE_CARE_OPEN_VALUE = "person_center_read_mode_eye_care_open_value";
    /**
     * 系统设置
     */
    public static final String PERSON_CENTER_SYSTEMSET_VALUE = "person_center_systemset_value";
    /**
     * 阅读偏好
     */
    public static final String PERSON_CENTER_READPREF_VALUE = "person_center_readpref_value";

    /**
     * 充值记录
     */
    public static final String PERSON_CENTER_MYACCOUNT_RECHARGERECORD_VALUE = "person_center_myaccount_rechargerecord_value";
    /**
     * 充值
     */
    public static final String PERSON_CENTER_MYACCOUNT_RECHARGE_VALUE = "person_center_myaccount_recharge_value";

    /**
     * 添加到书架
     */
    public static final String PERSON_CENTER_CLOUDSELF_ADDSELF_VALUE = "person_center_cloudself_addself_value";
    /**
     * 继续阅读
     */
    public static final String PERSON_CENTER_CLOUDSELF_CONTINUEREAD_VALUE = "person_center_cloudself_continueread_value";

    /**
     * 添加标签
     */
    public static final String ADD_BOOKMARK_VALUE = "add_bookmark_value";
    /**
     * 删除标签
     */
    public static final String DELETE_BOOKMARK_VALUE = "delete_bookmark_value";
    /**
     * 下载后续已购章节
     */
    public static final String DOWNLOAD_FOLLOWING_CHAPTERS_VALUE = "download_following_chapters_value";
    /**
     * 关闭
     */
    public static final String CLOSE_VALUE = "close_value";
    /**
     * 图书详情
     */
    public static final String BOOKDETAIL_VALUE = "bookdetail_value";

    /**
     * 系统设置打开接收消息
     */
    public static final String PERSON_CENTER_SYSTEMSET_RECEIVEMESSAGE_OPEN_VALUE = "person_center_systemset_receivemessage_open_value";
    /**
     * 系统设置关闭接收消息
     */
    public static final String PERSON_CENTER_SYSTEMSET_RECEIVEMESSAGE_CLOSED_VALUE = "person_center_systemset_receivemessage_closed_value";
    /**
     * 取消自动购买
     */
    public static final String PERSON_CENTER_SYSTEMSET_AUTOCANCELBUYNEXT_VALUE = "person_center_systemset_autocancelbuynext_value";
    /**
     * 意见反馈
     */
    public static final String PERSON_CENTER_SYSTEMSET_FEEDBACK_VALUE = "person_center_systemset_feedback_value";
    /**
     * 关于我们
     */
    public static final String PERSON_CENTER_SYSTEMSET_ABOUTUS_VALUE = "person_center_systemset_aboutus_value";

    /**
     * 清理缓存
     */
    public static final String PERSON_CENTER_SYSTEMSET_CLEARCANCEL_VALUE = "person_center_systemset_clearcancel_value";

    /*********************个人中心*******************/


    /**********************签到分享埋点*******************/

    /*******************
     * 订购页面
     *********************/
    /**
     * 自有单章订购页面  展示总量
     */
    public static final String OWN_SINGLE_ORDER_PAGE = "own_single_order_page";
    /**
     * 自有批量订购页面 确定
     */
    public static final String OWN_SINGLE_ORDER_PAGE_ORDER = "own_sigle_order_page_order";
    /**
     * 自有批量订购页面 余额不足去充值
     */
    public static final String OWN_SINGLE_ORDER_GO_RECHARGE = "own_single_order_go_recharge";
    /**
     * 自有单章订购页面取消
     */
    public static final String OWN_SINGLE_ORDER_PAGE_CANCLE = "own_single_order_page_cancle";

    /**
     * 自有批量订购页面 展示总量
     */
    public static final String OWN_LOT_ORDER_PAGE = "own_lot_order_page";
    /**
     * 自有批量订购页面 确定
     */
    public static final String OWN_LOT_ORDER_PAGE_ORDER = "own_lot_order_page_order";
    /**
     * 自有批量订购页面 余额不足去充值
     */
    public static final String OWN_LOT_ORDER_GO_RECHARGE = "own_lot_order_go_recharge";
    /**
     * 自有批量订购页面取消
     */
    public static final String OWN_LOT_ORDER_PAGE_CANCLE = "own_lot_order_page_cancle";

    /*******************订购页面*******************/


    /*******************
     * 漏斗
     *******************/
    public static final String TOTAL = "_total";


    /**
     * 书城
     */
    public static final String FROM_MAIN_STORE = "from_main_store";
    /**
     * 活动中心
     */
    public static final String FROM_CENTER = "from_center";
    /**
     * 充值
     */
    public static final String FROM_RECHARGE = "from_recharge";


    /*******************漏斗*******************/

    /********************
     * 内置书
     ********************/
    /**
     * 网络失败 直接内置本地assert目录
     */
    public static final String NET_FAIL_BUILD_ASSERT = "NET_FAIL_BUILD_ASSERT";

    /**
     * 10s超时 直接内置本地assert目录
     */
    public static final String NET_TIME_OUT_BUILD_ASSERT = "NET_TIME_OUT_BUILD_ASSERT";

    /**
     * 内置网络数据失败，直接内置assert目录
     */
    public static final String NET_SUC_BUILD_FAIL_BUILD_ASSERT = "NET_SUC_BUILD_FAIL_BUILD_ASSERT";

    /**
     * 内置网络数据成功
     */
    public static final String JS_BUILD_NET_SUCCESS = "js_build_net_success";

    /**
     * 内置总数
     */
    public static final String JS_BUILD_IN = "js_build_in";
    /**
     * 内置app总数
     */
    public static final String JS_BUILD_IN_APP = "js_build_in_app";

    /**
     * 内置总量
     */
    public static final String JS_BUILD_SUM = "js_build_sum";

    /**
     * 在Activity onPause 调用。
     *
     * @param activity activity
     */
    public static void onPauseActivity(BaseActivity activity) {
        if (null != activity) {
            ALog.thirdLog("<===" + activity.getName());
        }
        HwAnalysis.onPause(activity);
    }

    /**
     * 在Activity onResume 调用
     *
     * @param activity activity
     */
    public static void onResumeActivity(BaseActivity activity) {
        if (null != activity) {
            ALog.thirdLog("--->" + activity.getName());
        }

        HwAnalysis.onResume(activity);
    }

    /**
     * 在Fragment onPause 调用
     *
     * @param context context
     * @param name    name
     */
    public static void onPauseFragment(Context context, String name) {
        if (!TextUtils.isEmpty(name)) {
            ALog.thirdLog("<===" + name);
        }
        HwAnalysis.onPause(name, null);
    }


    /**
     * 在Fragment onResume 调用
     *
     * @param context context
     * @param name    name
     */
    public static void onResumeFragment(Context context, String name) {
        if (!TextUtils.isEmpty(name)) {
            ALog.thirdLog("---->" + name);
        }
        HwAnalysis.onResume(name, null);
    }

    /**
     * 统计点击事件和页面访问数目
     *
     * @param context context
     * @param eventId eventId
     */
    public static void onEvent(Context context, String eventId) {
    }

    /**
     * 统计点击事件和页面访问数目
     *
     * @param eventId eventId
     * @param map     map
     */
    public static void onEvent(String eventId, LinkedHashMap<String, String> map) {
    }

    /**
     * 华为打点
     *
     * @param eventId eventId
     * @param map     map
     */
    public static void onHwEvent(String eventId, LinkedHashMap<String, String> map) {
        String userID = SpUtil.getinstance(AppConst.getApp()).getUserID();
        if (null != map && !TextUtils.isEmpty(userID)) {
            map.put(LogConstants.USERID, userID);
        }
        if (null != map) {
            ALog.thirdLog("eventId:" + eventId + "  ;" + "map:" + map.toString());
        }

        HwAnalysis.onEvent(eventId, map);
    }

    /**
     * 统计
     *
     * @param context  context
     * @param eventId  eventId
     * @param value    value
     * @param duration duration
     */
    public static void onEventValueOldClick(Context context, String eventId, String value, long duration) {
    }

    /**
     * 统计数值型变量的值的分布
     *
     * @param context  context
     * @param eventId  eventId
     * @param mapValue map_value
     * @param duration duration
     */
    public static void onEventValueOld(Context context, String eventId, Map<String, String> mapValue, int duration) {
    }

    /**
     * 统计数值型变量的值的分布<br>
     * (用于网络错误)
     *
     * @param context  context
     * @param eventId  eventId
     * @param code     code
     * @param duration duration
     */
    public static void onEventValue(Context context, String eventId, String code, int duration) {
    }

    /**
     * webView ssl报错 回传
     *
     * @param context context
     * @param url     url
     */
    public static void onEventValueSSLError(final Context context, final String url) {
    }

    /**
     * 友盟错误日志主动回传。
     *
     * @param throwable throwable
     */
    public static void reportError(Throwable throwable) {
        //        CrashReport.postCatchedException(throwable);
    }

    /**
     * 初始化。 不使用sp，加速启动速度
     *
     * @param context context
     */
    public static void initLog(Context context) {

        String channel = DeviceInfoUtils.getInstanse().getChannel();
        ALog.thirdLog("channel:" + channel);
        HwAnalysis.init(context, channel);
    }

}
