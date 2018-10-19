package com.dzbook.model;

import android.content.Context;

import com.dzbook.AppConst;
import com.dzbook.lib.net.DzSchedulers;
import com.dzbook.lib.utils.ALog;
import com.dzbook.net.hw.HwRequestLib;
import com.dzbook.utils.HwLog;
import com.dzbook.utils.NetworkUtils;
import com.dzbook.utils.SpUtil;
import com.dzbook.utils.hw.LoginUtils;

import java.util.HashSet;
import java.util.concurrent.TimeUnit;

import hw.sdk.net.bean.task.FinishTask;

/**
 * 用户成长值上传公共类。
 *
 * @author zhenglk 15/11/12.
 */
public class UserGrow {
    /**
     * 阅读时常，添加成长值
     */
    public static final String USER_GROW_READ = "12";
    /**
     * 分享
     */
    public static final String USER_GROW_SHARE = "1";
    /**
     * 进入书城
     */
    public static final String USER_GROW_MAIN_STORE = "5";
    /**
     * 安装任务
     */
    public static final String USER_GROW_INSTALL = "16";
    /**
     * 添加图书到书架，添加成长值
     */
    public static final String USER_GROW_ADD_BOOK = "4";

    /**
     * 夜间模式
     */
    public static final String USER_GROW_NIGHT_READER = "15";

    /**
     * 用户成长值Action
     */
    public enum EnumUserGrowAction {
        /**
         * 新启动
         */
        RESUME, /**
         * 暂停
         */
        PAUSE, /**
         * 上传
         */
        MARK_UP
    }

    static long startReaderTime;
    static long endReaderTime;
    private static HashSet<String> addBookDayThread = new HashSet<String>();

    /**
     * 任务系统  阅读时长
     *
     * @param action     action
     * @param bookName   bookName
     * @param bookId     bookId
     * @param readerFrom readerFrom
     */
    public static synchronized void userGrowByReadNew(EnumUserGrowAction action, String bookId, String bookName, String readerFrom) {
        switch (action) {
            case RESUME:
                //开始
                startReaderTime = System.currentTimeMillis();
                break;
            case PAUSE:
                //暂停
                endReaderTime = System.currentTimeMillis();
                //上传
                if (endReaderTime > startReaderTime && startReaderTime > 0) {
                    long durationTime = endReaderTime - startReaderTime;
                    TimeUnit timeUnit = TimeUnit.DAYS; //单位为天
                    long millis = timeUnit.toMillis(7);
                    ALog.cmtDebug("millis:" + millis);
                    if (durationTime > millis) {
                        durationTime = millis;
                    }
                    endReaderTime = 0;
                    startReaderTime = 0;
                    HwLog.reader(bookId, bookName, "", "", (durationTime / 1000) + "", "", readerFrom);
                    if (LoginUtils.getInstance().checkLoginStatus(AppConst.getApp())) {
                        //登陆成功之后 才去上传
                        DzSchedulers.child(new UserGrowRunnable(durationTime));
                    }

                }
                break;
            default:
                break;
        }
    }

    /**
     * 同步阅读时长
     *
     * @param isFirst isFirst
     */
    public static void synchrodataServerReaderTime(boolean isFirst) {
        ALog.cmtDebug("synchrodataServerReaderTime");
        if (LoginUtils.getInstance().checkLoginStatus(AppConst.getApp())) {
            long localReaderDurationTime = SpUtil.getinstance(AppConst.getApp()).getLocalReaderDurationTime();
            ALog.cmtDebug("需要同步服务器阅读时间:" + localReaderDurationTime);
            if (isFirst || localReaderDurationTime > 0) {
                DzSchedulers.child(new SynchrodataServerReaderTime(localReaderDurationTime));
            }
        }
    }

    /**
     * 同步阅读时长
     */
    public static class SynchrodataServerReaderTime implements Runnable {
        private long mDurationTime;

        /**
         * 同步阅读时长
         *
         * @param localReaderDurationTime localReaderDurationTime
         */
        public SynchrodataServerReaderTime(long localReaderDurationTime) {
            mDurationTime = localReaderDurationTime;
        }

        @Override
        public void run() {
            SpUtil spUtil = SpUtil.getinstance(AppConst.getApp());

            if (NetworkUtils.getInstance().checkNet()) {
                try {
                    FinishTask task = HwRequestLib.getInstance().finishTask(USER_GROW_READ, (int) mDurationTime);
                    if (null != task && task.isFinish) {
                        //上传成功
                        spUtil.setLocalReaderDurationTime(0);
                        spUtil.setShowReaderTime(task.totalReadDuration);
                    }
                } catch (Exception e) {
                    ALog.printStackTrace(e);
                }
            }
        }
    }

    /**
     * UserGrowRunnable
     */
    public static class UserGrowRunnable implements Runnable {
        private long mDurationTime;

        /**
         * 构造
         *
         * @param durationTime durationTime
         */
        public UserGrowRunnable(long durationTime) {
            this.mDurationTime = durationTime;
        }

        private void upDataReaderTimeFail(long time) {
            SpUtil spUtil = SpUtil.getinstance(AppConst.getApp());
            spUtil.setLocalReaderDurationTime(time + spUtil.getLocalReaderDurationTime());
            spUtil.setShowReaderTime(spUtil.getShowReaderTime() + time);
        }

        @Override
        public void run() {
            SpUtil spUtil = SpUtil.getinstance(AppConst.getApp());

            if (NetworkUtils.getInstance().checkNet()) {
                try {
                    long time = spUtil.getLocalReaderDurationTime();
                    FinishTask task = HwRequestLib.getInstance().finishTask(USER_GROW_READ, (int) (mDurationTime + time));
                    if (null != task && task.isFinish) {
                        //上传成功
                        spUtil.setLocalReaderDurationTime(0);
                        spUtil.setShowReaderTime(task.totalReadDuration);
                    } else {
                        //上传失败
                        upDataReaderTimeFail(mDurationTime);
                    }
                } catch (Exception e) {
                    ALog.printStackTrace(e);
                    //上传失败
                    upDataReaderTimeFail(mDurationTime);
                }

            } else {
                //上传失败
                upDataReaderTimeFail(mDurationTime);
            }
        }
    }


    /**
     * 单次动作，同步成长值
     *
     * @param context 上下文
     * @param action  action
     */
    public static synchronized void userGrowOnceToday(final Context context, final String action) {
        if (!LoginUtils.getInstance().checkLoginStatus(AppConst.getApp())) {
            return;
        }
        final Context appContext = context.getApplicationContext();
        final SpUtil sp = SpUtil.getinstance(appContext);
        String userID = sp.getUserID();
        if (!addBookDayThread.contains(userID + action) && !sp.hasMarkTodayByKey(userID + "user.grow.add.book." + action)) {
            Runnable addBookDayRunnable = new Runnable() {
                @Override
                public void run() {
                    try {
                        FinishTask finishTask = HwRequestLib.getInstance().finishTask(action, 0);
                        if (null != finishTask && finishTask.isFinish) {
                            sp.markTodayByKey(sp.getUserID() + "user.grow.add.book." + action);
                            HwRequestLib.flog("userGrowOnceToday 回传成功 type" + action);
                            return;
                        }
                    } catch (Exception e) {
                        ALog.eLk(e.getMessage());
                    } finally {
                        addBookDayThread.remove(sp.getUserID() + action);
                    }
                    HwRequestLib.flog("userGrowOnceToday 回传失败 type" + action);
                }
            };

            DzSchedulers.child(addBookDayRunnable);
            addBookDayThread.add(sp.getUserID() + action);
        }
    }
}
