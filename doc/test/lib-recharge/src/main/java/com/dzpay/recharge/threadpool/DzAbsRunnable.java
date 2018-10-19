package com.dzpay.recharge.threadpool;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * 线程池依赖的任务
 *
 * @author ZhengLK on 15/1/15.
 */
public abstract class DzAbsRunnable implements Runnable {
    /**
     * 最低优先级
     */
    public static final int PRIORITY_BASE_LOW = 1;
    private static volatile SimpleDateFormat format;
    private String createTime;
    private int priority;
    private String tag;


    /**
     * 构造
     *
     * @param tag      标识
     * @param priority 优先级
     */
    protected DzAbsRunnable(String tag, int priority) {
        this.createTime = getFormat().format(new Date());
        this.tag = tag;
        this.priority = priority;
    }

    private static SimpleDateFormat getFormat() {
        if (null == format) {
            synchronized (SimpleDateFormat.class) {
                format = new SimpleDateFormat("MM-dd HH:mm:ss.SSS", Locale.CHINA);
            }
        }
        return format;
    }

    public int getPriority() {
        return priority;
    }

    public String getTag() {
        return tag;
    }


    @Override
    public String toString() {
        return "[ " + tag + ", " + priority + " ] create at " + createTime + " @" + Integer.toHexString(hashCode());
    }
}
