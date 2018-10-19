package com.dzbook.utils;

import android.app.Activity;

import com.dzbook.activity.detail.BookDetailActivity;
import com.dzbook.lib.utils.ALog;

import java.util.LinkedList;

/**
 * Activity的管理
 *
 * @author hao.xiong
 * @version 1.0.0
 */
public final class ActivityManager {

    private static ActivityManager sInstance;

    private static final int MAX_PAGE_SIZE = 2;
    /******************************************书籍详情页面5层activity管理开始******************************/
    /**
     * 保存在栈里的所有Activity
     */
    private LinkedList<Activity> mDetailActivities = new LinkedList<Activity>();


    private ActivityManager() {
    }

    /**
     * 获取ActivityManager实例
     *
     * @return ActivityManager实例
     */
    public static ActivityManager instance() {
        if (sInstance == null) {
            sInstance = new ActivityManager();
        }
        return sInstance;
    }


    /**
     * 当Activity执行onCreate时调用 - 保存启动的Activity
     *
     * @param activity 执行onCreate的Activity
     */
    public void onCreate(BookDetailActivity activity) {
        if (mDetailActivities.size() == MAX_PAGE_SIZE) {
            ALog.dZz("ActivityManager onCreate mDetailActivities sizeBeyond:" + mDetailActivities.size());
            Activity mDetailActivity = mDetailActivities.getFirst();
            mDetailActivity.finish();
            mDetailActivities.removeFirst();
            ALog.dZz("ActivityManager onCreate mDetailActivities remove after size:" + mDetailActivities.size());
        }
        mDetailActivities.add(activity);
        ALog.dZz("ActivityManager onCreate mDetailActivities last size:" + mDetailActivities.size());
    }


    /**
     * 当Activity执行onDestroy时调用 - 移除销毁的Activity
     *
     * @param activity 执行onDestroy时的Activity
     */
    public void onDestroy(BookDetailActivity activity) {
        ALog.dZz("ActivityManager onDestroy size：" + mDetailActivities.size());
        mDetailActivities.remove(activity);
        ALog.dZz("ActivityManager onDestroy remove after size：" + mDetailActivities.size());
    }


    /******************************************书籍详情页面5层activity管理结束******************************/
}
