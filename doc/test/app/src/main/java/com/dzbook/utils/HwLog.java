package com.dzbook.utils;

import android.text.TextUtils;

import com.dzbook.database.bean.BookInfo;
import com.dzbook.log.LogConstants;

import java.util.LinkedHashMap;

import hw.sdk.net.bean.BeanTabLog;

/**
 * Log
 *
 * @author caimantang on 2018/5/3.
 */

public class HwLog {
    private static LinkedHashMap<String, BeanTabLog> logLinkedHashMap;

    /**
     * app打开
     * <p>
     * userid 用户标识 必须 用户ID
     * netType 当前网络类型 必须 1：Wifi或热点；2：运营商网络2G/3G/4G；-1：无网络。
     *
     * @param from from 打开APP的渠道 必须  "渠道来源类型，包括：1、桌面打开2、Push拉起 3、…各种导流渠道"
     * @param to   to 打开APP进入的页面 必须  "可以是拉起的页面名称：例如首页、读书页、会员页等"
     */
    public static void entryApp(String from, String to) {
        LinkedHashMap<String, String> hashMap = new LinkedHashMap<>();
        hashMap = addMap(hashMap, LogConstants.NET_TYPE, String.valueOf(NetworkUtils.getInstance().getNetType()));
        hashMap = addMap(hashMap, LogConstants.FROM, from);
        hashMap = addMap(hashMap, LogConstants.TO, to);
        ThirdPartyLog.onHwEvent(ThirdPartyLog.ENTRY_APP, hashMap);
    }

    /**
     * 活动&专题页访问数据
     * <p>
     *
     * @param id   活动/专题ID
     * @param name 活动/专题名称
     */
    public static void setActivity(String id, String name) {
        LinkedHashMap<String, String> hashMap = new LinkedHashMap<>();
        hashMap.put(LogConstants.HW_ID, id);
        hashMap.put(LogConstants.HW_NAME, name);
        ThirdPartyLog.onHwEvent(ThirdPartyLog.ACCESS_ACTIVITY, hashMap);
    }


    /**
     * 用户喜好选择
     *
     * @param gender 用户性别
     */
    public static void setPh(String gender) {
        LinkedHashMap<String, String> hashMap = new LinkedHashMap<>();
        hashMap.put(LogConstants.GENDER, gender);
        ThirdPartyLog.onHwEvent(ThirdPartyLog.YHPH, hashMap);
    }

    /**
     * 用户签到
     *
     * @param type  签到类 1：当天签到；2：补签
     * @param award 签到所获奖励
     */
    public static void setSign(String type, String award) {
        LinkedHashMap<String, String> hashMap = new LinkedHashMap<>();
        hashMap.put(LogConstants.TYPE, type);
        hashMap.put(LogConstants.AWARD, award);
        ThirdPartyLog.onHwEvent(ThirdPartyLog.SIGN, hashMap);
    }

    public static LinkedHashMap<String, BeanTabLog> getLogLinkedHashMap() {
        return logLinkedHashMap;
    }


    static {
        logLinkedHashMap = new LinkedHashMap<>();
        logLinkedHashMap.put("store", new BeanTabLog("store", "书城", "1"));
        logLinkedHashMap.put("type", new BeanTabLog("type", "分类", "2"));
    }

    /**
     * <p>
     *
     * @param tabLog      tabLog
     * @param pageId      频道ID
     * @param pageName    频道名称
     * @param pagePos     频道位置
     *                    <p>
     * @param columeID    栏目ID
     * @param columeName  栏目名称
     * @param columePos   栏目位置
     * @param columeTemp  栏目类型
     *                    <p>
     * @param contentID   内容ID
     * @param contentName 内容名称
     * @param contentType 内容类型
     */
    public static void columnClick(BeanTabLog tabLog, String pageId, String pageName, String pagePos, String columeID, String columeName, String columePos, String columeTemp, String contentID, String contentName, String contentType) {
        LinkedHashMap<String, String> hashMap = new LinkedHashMap<>();
        hashMap = addMap(hashMap, LogConstants.TABID, tabLog.getTabId());
        hashMap = addMap(hashMap, LogConstants.TABNAME, tabLog.getTabName());
        hashMap = addMap(hashMap, LogConstants.TABPOS, tabLog.getTabPos());
        hashMap = addMap(hashMap, LogConstants.PAGEID, pageId);
        hashMap = addMap(hashMap, LogConstants.PAGENAME, pageName);
        hashMap = addMap(hashMap, LogConstants.PAGEPOS, pagePos);
        hashMap = addMap(hashMap, LogConstants.COLUMEID, columeID);
        hashMap = addMap(hashMap, LogConstants.COLUMENAME, columeName);
        hashMap = addMap(hashMap, LogConstants.COLUMEPOS, columePos);
        hashMap = addMap(hashMap, LogConstants.COLUMETEMP, columeTemp);
        hashMap = addMap(hashMap, LogConstants.CONTENTID, contentID);
        hashMap = addMap(hashMap, LogConstants.CONTENTNAME, contentName);
        hashMap = addMap(hashMap, LogConstants.CONTENTTYPE, contentType);
        hashMap = addMap(hashMap, LogConstants.NET_TYPE, NetworkUtils.getInstance().getNetType() + "");

        ThirdPartyLog.onHwEvent(ThirdPartyLog.COLUMN_CLICK, hashMap);
    }

    /**
     * 打开书籍
     *
     * @param bookInfo      bookInfo
     * @param openTime      打开时间
     * @param closeTime     关闭时间
     * @param time          阅读时长（秒）
     * @param chapterAmount 阅读章节数
     * @param from          打开来源
     */
    public static void reader(BookInfo bookInfo, String openTime, String closeTime, String time, String chapterAmount, String from) {
        if (null != bookInfo) {
            reader(bookInfo.bookid, bookInfo.bookname, openTime, closeTime, time, chapterAmount, from);
        }
    }

    /**
     * 打开书籍
     *
     * @param bookId        bookId
     * @param bookName      书名
     * @param openTime      打开时间
     * @param closeTime     关闭时间
     * @param time          阅读时长（秒）
     * @param chapterAmount 阅读章节数
     * @param from          打开来源
     */
    public static void reader(String bookId, String bookName, String openTime, String closeTime, String time, String chapterAmount, String from) {
        LinkedHashMap<String, String> hashMap = new LinkedHashMap<>();
        hashMap = addMap(hashMap, LogConstants.CONTENTID, bookId);
        hashMap = addMap(hashMap, LogConstants.CONTENTNAME, bookName);
        hashMap = addMap(hashMap, LogConstants.OPEN_TIME, openTime);
        hashMap = addMap(hashMap, LogConstants.CLOSE_TIME, closeTime);
        hashMap = addMap(hashMap, LogConstants.READER_TIME, time);
        hashMap = addMap(hashMap, LogConstants.CHAPTER_AMOUNT, chapterAmount);
        hashMap = addMap(hashMap, LogConstants.FROM, from);
        ThirdPartyLog.onHwEvent(ThirdPartyLog.READ_SCENE, hashMap);
    }

    /**
     * 加入集合
     *
     * @param hashMap hashMap
     * @param key     key
     * @param value   value
     * @return LinkedHashMap
     */
    public static LinkedHashMap<String, String> addMap(LinkedHashMap<String, String> hashMap, String key, String value) {
        if (null == hashMap) {
            hashMap = new LinkedHashMap<>();
        }
        if (!TextUtils.isEmpty(key) && !TextUtils.isEmpty(value)) {
            hashMap.put(key, value);
        }
        return hashMap;
    }

    /**
     * 用户订购VIP包月
     * 字段非必须 没有传空
     *
     * @param name      VIP包月名称
     * @param period    包月周期 1个月、3个月、6个月、1年、连续包月
     * @param startDate 包月生效日期
     * @param endDate   包月失效日期
     * @param money     订购金额
     * @param isAuto    是否自动续费 1：是；2：否
     */
    public static void buyVIP(String name, String period, String startDate, String endDate, String money, String isAuto) {
        LinkedHashMap<String, String> hashMap = new LinkedHashMap<>();
        hashMap = addMap(hashMap, LogConstants.HW_NAME, name);
        hashMap = addMap(hashMap, LogConstants.PERIOD, period);
        hashMap = addMap(hashMap, LogConstants.START_DATE, startDate);
        hashMap = addMap(hashMap, LogConstants.END_DATE, endDate);
        hashMap = addMap(hashMap, LogConstants.MONEY, money);
        hashMap = addMap(hashMap, LogConstants.IS_AUTO, isAuto);
        ThirdPartyLog.onHwEvent(ThirdPartyLog.VIP_BUY, hashMap);
    }

    /**
     * 虚拟币充值
     * 字段非必须 没有传空
     *
     * @param money   充值金额 元
     * @param virtual 充值虚拟币数量
     * @param coupon  赠送代金券数量
     */
    public static void recharge(String money, String virtual, String coupon) {
        LinkedHashMap<String, String> hashMap = new LinkedHashMap<>();
        hashMap = addMap(hashMap, LogConstants.MONEY, money);
        hashMap = addMap(hashMap, LogConstants.VIRTUAL, virtual);
        hashMap = addMap(hashMap, LogConstants.COUPON, coupon);
        ThirdPartyLog.onHwEvent(ThirdPartyLog.USER_RECHARGE, hashMap);
    }

    /**
     * 电子书购买场景
     *
     * @param contentID   内容ID
     * @param contentName 内容名称
     * @param buyType     购买方式 1：全本；2：批量章节；3：单章
     * @param buyAmount   购买数量 全本和单章数量为1，批量章节为具体章节数
     * @param money       付费总金额 单位：元（虚拟币折算成元）
     * @param coupon      代金券消费金额 使用代金券消费金额，如不涉及，值为0
     * @param virtual     真实消费金额 使用充值的虚拟币消费金额，如不涉及，值为0
     * @param cash        现金消费金额 直接使用现金消费金额，如不涉及，值为0
     */
    public static void buyBook(String contentID, String contentName, String buyType, String buyAmount, String money, String coupon, String virtual, String cash) {
        LinkedHashMap<String, String> hashMap = new LinkedHashMap<>();
        hashMap = addMap(hashMap, LogConstants.CONTENTID, contentID);
        hashMap = addMap(hashMap, LogConstants.CONTENTNAME, contentName);
        hashMap = addMap(hashMap, LogConstants.BUYT_YPE, buyType);
        hashMap = addMap(hashMap, LogConstants.BUY_AMOUNT, buyAmount);
        hashMap = addMap(hashMap, LogConstants.MONEY, money);
        hashMap = addMap(hashMap, LogConstants.COUPON, coupon);
        hashMap = addMap(hashMap, LogConstants.VIRTUAL, virtual);
        hashMap = addMap(hashMap, LogConstants.CASH, cash);
        ThirdPartyLog.onHwEvent(ThirdPartyLog.READ_BUY_SCENE, hashMap);
    }


}
