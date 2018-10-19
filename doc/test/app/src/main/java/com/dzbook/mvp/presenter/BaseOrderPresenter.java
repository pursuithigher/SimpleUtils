package com.dzbook.mvp.presenter;

import android.text.TextUtils;

import com.dzbook.log.DzLog;
import com.dzbook.log.DzLogMap;
import com.dzbook.log.LogConstants;
import com.dzbook.log.SourceFrom;
import com.dzbook.mvp.BasePresenter;
import com.dzbook.pay.Listener;
import com.dzbook.recharge.RechargeObserver;
import com.iss.app.BaseActivity;

import java.util.HashMap;

/**
 * BaseOrderPresenter
 *
 * @author winzows 2018/6/26
 */
public abstract class BaseOrderPresenter extends BasePresenter {

    protected static RechargeObserver observer;
    /**
     * 操作来源
     */
    protected String oprateFrom;

    protected String partFrom;

    protected HashMap<String, String> params;

    /**
     * 日志打点需要
     */
    protected String trackId;

    protected String bookId, chapterId;

    protected Listener listener;

    /**
     * 订购页面去订购打点
     *
     * @param num num
     */

    public void dzLogOrder(String num) {
        HashMap<String, String> map = new HashMap<String, String>();
        map.put(LogConstants.KEY_ORDER_BID, bookId);
        map.put(LogConstants.KEY_ORDER_CID, chapterId);
        DzLog.getInstance().logClick(LogConstants.MODULE_DG_DZ, LogConstants.ZONE_DG_1, num, map, trackId);
    }

    /**
     * 打点
     *
     * @param num num
     */
    public void dzLogGoRecahrge(String num) {
        HashMap<String, String> map = new HashMap<String, String>();
        map.put(LogConstants.KEY_ORDER_BID, bookId);
        map.put(LogConstants.KEY_ORDER_CID, chapterId);
        DzLog.getInstance().logClick(LogConstants.MODULE_DG_DZ, LogConstants.ZONE_DG_2, num, map, trackId);
    }

    /**
     * 打点
     */
    public void dzLogCancel() {
        HashMap<String, String> map = new HashMap<String, String>();
        map.put(LogConstants.KEY_ORDER_BID, bookId);
        map.put(LogConstants.KEY_ORDER_CID, chapterId);
        DzLog.getInstance().logClick(LogConstants.MODULE_DG_DZ, LogConstants.ZONE_DG_3, null, map, trackId);
    }

    /**
     * 打点
     */
    public void pvLog() {
        HashMap<String, String> map = new HashMap<String, String>();

        String ext;
        if (TextUtils.equals(oprateFrom, SourceFrom.FROM_BOOK_DETAIL)) {
            if (TextUtils.equals(partFrom, LogConstants.ORDER_SOURCE_FROM_VALUE_1) || TextUtils.equals(partFrom, LogConstants.ORDER_SOURCE_FROM_VALUE_3)) {
                //1,3
                ext = partFrom;
            } else {
                ext = LogConstants.ORDER_SOURCE_FROM_VALUE_10;
            }
        } else if (TextUtils.equals(oprateFrom, SourceFrom.FROM_BOOK_DETAIL_CONTENTS)) {
            if (TextUtils.equals(partFrom, LogConstants.ORDER_SOURCE_FROM_VALUE_2)) {
                //2
                ext = partFrom;
            } else {
                ext = LogConstants.ORDER_SOURCE_FROM_VALUE_10;
            }
        } else if (TextUtils.equals(oprateFrom, SourceFrom.FROM_READER)) {
            if (TextUtils.equals(partFrom, LogConstants.ORDER_SOURCE_FROM_VALUE_4) || TextUtils.equals(partFrom, LogConstants.ORDER_SOURCE_FROM_VALUE_6) || TextUtils.equals(partFrom, LogConstants.ORDER_SOURCE_FROM_VALUE_7)) {
                //4,6,7
                ext = partFrom;
            } else {
                ext = LogConstants.ORDER_SOURCE_FROM_VALUE_10;
            }
        } else if (TextUtils.equals(oprateFrom, SourceFrom.FROM_READER_CONTENTS)) {
            if (TextUtils.equals(partFrom, LogConstants.ORDER_SOURCE_FROM_VALUE_5)) {
                //5
                ext = partFrom;
            } else {
                ext = LogConstants.ORDER_SOURCE_FROM_VALUE_10;
            }
        } else {
            ext = LogConstants.ORDER_SOURCE_FROM_VALUE_10;
        }

        map.put(LogConstants.KEY_EXT, ext);
        map.put(LogConstants.KEY_ORDER_BID, bookId);
        map = DzLogMap.getReaderFrom(getHostActivity(), map, bookId);
        DzLog.getInstance().logPv(getHostActivity(), map, trackId);
    }


    public String getChapterId() {
        return chapterId;
    }

    public String getBookId() {
        return bookId;
    }

    public HashMap<String, String> getParams() {
        return params;
    }

    public Listener getListener() {
        return listener;
    }

    public RechargeObserver getObserver() {
        return observer;
    }

    /**
     * 获取
     * activity实例
     *
     * @return activity
     */
    public abstract BaseActivity getHostActivity();
}
