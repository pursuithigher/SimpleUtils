package com.dzbook.utils;

import android.annotation.SuppressLint;
import android.content.res.Resources;

import com.dzbook.AppConst;
import com.dzbook.lib.utils.ALog;
import com.ishugui.R;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 时间工具类
 */
@SuppressLint("SimpleDateFormat")
public class TimeUtils {

    /**
     * 一天以内(当天和大于当天都是一天以内)
     */
    public static final char TYPE_TIME_UNKNOW = 0x00;
    /**
     * 一天以内(当天和大于当天都是一天以内)
     */
    public static final char TYPE_TIME_TODAY = 0x01;
    /**
     * 7天以内
     */
    public static final char TYPE_TIME_WEEK = 0x02;
    /**
     * 30天以内
     */
    public static final char TYPE_TIME_MONTH = 0x03;
    /**
     * 30天以前
     */
    public static final char TYPE_TIME_OTHER = 0x04;

//    public static void main(String[] args) {
//        System.out.println(getFormatDate("HH:mm:ss"));
//    }

    /**
     * 获取指定格式的日期字符串
     *
     * @param format 指定的日期格式<br>
     *               eg:<br>
     *               "yyyy-MM-dd HH:mm:ss"<br>
     *               "yyyy-MM-dd"<br>
     *               "yyyyMMddHHmmss"<br>
     *               "HH:mm:ss"<br>
     * @return string
     */
    public static String getFormatDate(String format) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
        Date date = new Date();
        return simpleDateFormat.format(date);
    }

    /**
     * 根据指定的时间戳，返回指定格式的日期时间
     *
     * @param str    时间串
     * @param format 指定的日期格式<br>
     *               eg:<br>
     *               "yyyy-MM-dd HH:mm:ss"<br>
     *               "yyyy-MM-dd"<br>
     *               "yyyyMMddHHmmss"<br>
     *               "HH:mm:ss"<br>
     * @return string
     */
    public static String getFormatTime1(String str, String format) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(format);
            Date date = sdf.parse(str);

            return sdf.format(date);
        } catch (Exception e) {
            ALog.printStackTrace(e);
        }
        return "";
    }

    /**
     * 显示时间规则：
     * 1.小于1小时显示为：刚刚
     * 2.大于1小时，小于24小时显示为：具体小时
     * 3.大于24小时，小于1个月显示为：几天前
     * 4.大于1个月，显示为很久以前
     * 一个月：添加书籍到书架的时间点往后1个非自然月算是一个月
     *
     * @param time time
     * @return string
     */
    public static String getShowTimeByReadTime(String time) {
        Resources resources = AppConst.getApp().getResources();
        try {
            long readTime = Long.parseLong(time);
            long curentTime = System.currentTimeMillis();

            Long day = (curentTime - readTime) / (1000 * 60 * 60 * 24);
            Long hour = ((curentTime - readTime) % (1000 * 60 * 60 * 24)) / (1000 * 60 * 60);

            if (day >= 30) {
                return resources.getString(R.string.time_long_ago);
            } else if (day >= 1) {
                return day + resources.getString(R.string.time_day_ago);
            } else if (hour >= 1) {
                return hour + resources.getString(R.string.time_hours_ago);
            } else {
                return resources.getString(R.string.time_just_ago);
            }

        } catch (Exception e) {
            ALog.printStackTrace(e);
        }

        return resources.getString(R.string.time_unknow_ago);
    }

    /**
     * 显示时间规则：
     * 一天内  TYPE_TIME_TODAY
     * 一周内  TYPE_TIME_WEEK
     * 一月内  TYPE_TIME_MONTH
     * 一月前  TYPE_TIME_OTHER
     *
     * @param readTime readTime
     * @return int
     */
    public static int getShowTimeByFile(long readTime) {
        try {
            long curentTime = System.currentTimeMillis();

            Long day = (curentTime - readTime) / (1000 * 60 * 60 * 24);

            if (day <= 1) {
                return TYPE_TIME_TODAY;
            } else if (day <= 7) {
                return TYPE_TIME_WEEK;
            } else if (day <= 30) {
                return TYPE_TIME_MONTH;
            } else {
                return TYPE_TIME_OTHER;
            }

        } catch (Exception e) {
            ALog.printStackTrace(e);
        }
        return TYPE_TIME_UNKNOW;
    }

}
