package com.dzpay.recharge.threadpool;

import com.dzpay.recharge.utils.PayLog;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 单线程池。添加进程时，使用(@link DzAbsRunnable)启动进程，可以设置优先级。
 *
 * @author ZhengLK on 15/1/15.
 */
public class DzSingleExecutor implements Executor {


    static long taskEmptyDelay = 60000;
    static long taskLowDelay = 2000;
    static final int PRIORITY_DEF = DzAbsRunnable.PRIORITY_BASE_LOW + 1;

    private static final String TAG = "_DzSingleExecutor_";

    List<Runnable> taskPool = new ArrayList<Runnable>();
    Thread taskThread = null;
    /**
     * 锁
     */
    private final ReentrantLock mainLock = new ReentrantLock();


    /**
     * 任务排序比较器
     */
    private Comparator comparator = new Comparator() {
        @Override
        public int compare(Object objL, Object objR) {
            int priorityL = getPriority(objL);
            int priorityR = getPriority(objR);
            return priorityL - priorityR;
        }
    };


    public static long getTaskEmptyDelay() {
        return limit(taskEmptyDelay, 1000, 120000);
    }

    public static void setTaskEmptyDelay(long delay) {
        taskEmptyDelay = delay;
    }

    public static long getTaskLowDelay() {
        return limit(taskLowDelay, 1000, 10000);
    }

    public static void setTaskLowDelay(long delay) {
        taskLowDelay = delay;
    }


    private static long limit(long value, long min, long max) {
        if (value < min) {
            return min;
        } else if (value > max) {
            return max;
        } else {
            return value;
        }
    }


    /**
     * 添加启动任务。
     */
    @Override
    public void execute(Runnable command) {
        taskPut(command);
        if (null != taskThread) {
            Thread.State state = taskThread.getState();
            if (Thread.State.TERMINATED.equals(state)) {
                taskThread = null;
            }
        }

        if (null == taskThread) {
            taskThread = new Thread() {
                @Override
                public void run() {
                    taskRun();
                }
            };
        }

        Thread.State state = taskThread.getState();
        if (Thread.State.NEW.equals(state)) {
            taskThread.start();
        }
    }

    /**
     * 执行任务。
     */
    private void taskRun() {
        long markEmpty = -1, markLow = -1;
        while (true) {
            final long thisTime = System.currentTimeMillis();

            threadSleep(80);
            final Runnable task = taskGet();
            final int priority = getPriority(task);
            if (null == task) {
                if (markEmpty < 0) {
                    markEmpty = thisTime;
                }

                // 任务为空， 延时 TASK_EMPTY_DELAY ms 等待。期间添加非空任务继续执行，否则退出任务队列。
                if (thisTime - markEmpty < getTaskEmptyDelay()) {
                    continue;
                } else {
                    taskClear();
                    return;
                }

            } else if (priority == DzAbsRunnable.PRIORITY_BASE_LOW) {
                if (markLow < 0) {
                    markLow = thisTime;
                }

                // 低优先级任务，延时 LOW_PRIORITY_DELAY ms 等待。期间出现更高优先级任务，优先插入执行。
                if (thisTime - markLow < getTaskLowDelay()) {
                    // Time out, Go!
                    continue;
                }
            }

            markEmpty = -1;
            markLow = -1;

            // 移除需要执行的任务
            taskRemove(task);
            // 执行
            task.run();
        }
    }

    /**
     * Thread 休眠
     *
     * @param ms
     */
    private void threadSleep(long ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
        }
    }

    /**
     * 获取任务的优先级。
     *
     * @param obj
     * @return
     */
    private int getPriority(Object obj) {
        if (null == obj) {
            return 0;
        } else if (obj instanceof DzAbsRunnable) {
            return ((DzAbsRunnable) obj).getPriority();
        } else {
            return PRIORITY_DEF;
        }
    }

    /**
     * 获取下一个任务
     */
    private Runnable taskGet() {
        mainLock.lock();
        try {
            if (taskPool.isEmpty()) {
                return null;
            }
            int size = taskPool.size();
            if (size > 1) // 按照优先级排序。
            {
                Collections.sort(taskPool, comparator);
            }

            // 取最高优先级。
            return taskPool.get(size - 1);
        } finally {
            mainLock.unlock();
        }
    }

    /**
     * 移除一个任务。
     *
     * @param task
     */
    private void taskRemove(Runnable task) {
        mainLock.lock();
        try {
            if (taskPool.contains(task)) {
                taskPool.remove(task);
            }
        } finally {
            mainLock.unlock();
        }
    }

    /**
     * 清空任务。
     */
    private void taskClear() {
        mainLock.lock();
        try {
            taskPool.clear();
        } finally {
            mainLock.unlock();
        }
    }

    /**
     * 添加一个任务
     */
    private void taskPut(Runnable task) {
        if (null == task) {
            PayLog.e(TAG + " AbsRunnable is null");
            return;
        }
        //PayLog.d("putTask:" + task.toString());
        mainLock.lock();
        try {
            taskPool.add(task);
        } finally {
            mainLock.unlock();
        }
    }
}
