package com.dzbook.lib.utils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * 时间工具类
 *
 * @author zhenglk
 */
public class UtilTimeOffset {
    private static long mOffset = 0;

    public static void setOffset(long l) {
        mOffset = l;
    }

    /**
     * 获得当前ms时间
     *
     * @return long时间
     */
    public static long currentTimeMillisSev() {
        return System.currentTimeMillis() + mOffset;
    }

    /**
     * 获得当前日期时间格式
     *
     * @return yyyy-MM-dd HH:mm:ss
     */
    public static String getDateFormatSev() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        long time = System.currentTimeMillis() + mOffset;
        String formatTime = dateFormat.format(time);
        ALog.iLk("UtilTimeOffset time = (" + mOffset + ")" + formatTime);
        return formatTime;
    }

    /**
     * 获得当前日期时间
     *
     * @return yyyyMMddHHmmss
     */
    public static String getFormatDateByTimeZone() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        sdf.setTimeZone(TimeZone.getTimeZone("GMT+0"));
        return sdf.format(new Date());
    }
}
