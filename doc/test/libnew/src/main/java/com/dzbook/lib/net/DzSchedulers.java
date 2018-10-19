/**
 * Copyright (c) 2013 DuoKu Inc.
 *
 * @author
 * @date 2013-2-22
 */

package com.dzbook.lib.net;

import android.support.annotation.NonNull;

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * 线程调度
 */
public class DzSchedulers {
    private static volatile DzSchedulers instanse;
    ConcurrentHashMap<String, ConcurrentHashMap<String, Disposable>> resources;

    /**
     * 构造
     */
    private DzSchedulers() {
        resources = new ConcurrentHashMap<>();
    }


    /**
     * 获取实例
     *
     * @return DzSchedulers
     */
    public static DzSchedulers getInstance() {
        if (instanse == null) {
            synchronized (DzSchedulers.class) {
                if (instanse == null) {
                    instanse = new DzSchedulers();
                }
            }
        }
        return instanse;
    }

    /**
     * 只想
     *
     * @param task 任务
     * @return disposable
     */
    public static Disposable execute(Runnable task) {
        return child(task);
    }

    /**
     * 执行
     *
     * @param pageKey      页面key
     * @param taskKey      任务key
     * @param task         任务
     * @param frontDisable ？
     */

    public void execute(@NonNull String pageKey, @NonNull String taskKey, Runnable task, boolean frontDisable) {
        child(pageKey, taskKey, frontDisable, task);
    }

    /**
     * 主线程
     *
     * @param task 任务
     * @return disposable
     */
    public static Disposable main(Runnable task) {
        return AndroidSchedulers.mainThread().scheduleDirect(task);
    }

    /**
     * 主线程 延迟
     *
     * @param task  任务
     * @param delay 延迟
     * @return disposable
     */
    public static Disposable mainDelay(Runnable task, long delay) {
        return AndroidSchedulers.mainThread().scheduleDirect(task, delay, TimeUnit.MILLISECONDS);
    }

    /**
     * 子线程
     *
     * @param task 任务
     * @return disposable
     */
    public static Disposable child(Runnable task) {
        return Schedulers.io().scheduleDirect(task);
    }

    /**
     * 子线程
     *
     * @param pageKey      页面key
     * @param taskKey      任务key
     * @param task         任务
     * @param frontDisable ？
     * @return disposable
     */
    public Disposable child(@NonNull String pageKey, @NonNull String taskKey, boolean frontDisable, Runnable task) {
        Disposable disposable = Schedulers.io().scheduleDirect(task);
        if (disposable != null) {
            addAndDisposeOldByKey(pageKey, taskKey, disposable, frontDisable);
        }

        return disposable;
    }

    /**
     * 子线程
     *
     * @param task  任务
     * @param delay 延迟
     * @return disposable
     */

    public static Disposable childDelay(Runnable task, long delay) {
        return Schedulers.io().scheduleDirect(task, delay, TimeUnit.MILLISECONDS);
    }

    /**
     * 执行
     *
     * @param pageKey      页面key
     * @param taskKey      任务key
     * @param task         任务
     * @param delay        延迟
     * @param frontDisable ？
     * @return disposable
     */
    public Disposable childDelay(@NonNull String pageKey, @NonNull String taskKey, Runnable task, long delay, boolean frontDisable) {
        Disposable disposable = Schedulers.io().scheduleDirect(task, delay, TimeUnit.MILLISECONDS);
        if (disposable != null) {
            addAndDisposeOldByKey(pageKey, taskKey, disposable, frontDisable);
        }

        return disposable;
    }

    /**
     * 添加到订阅管理器
     *
     * @param pageKey      页面key
     * @param taskKey      任务key
     * @param disposable   disposable
     * @param frontDisable ？
     */
    private void addAndDisposeOldByKey(String pageKey, String taskKey, Disposable disposable, boolean frontDisable) {
        if (resources.containsKey(pageKey)) {
            ConcurrentHashMap<String, Disposable> map = resources.get(pageKey);
            resources.remove(pageKey);
            Disposable disposable1 = map.get(taskKey);
            if (disposable1 != null && frontDisable) {
                disposable1.dispose();
            }
            map.put(taskKey, disposable);
            resources.put(pageKey, map);
        } else {
            ConcurrentHashMap<String, Disposable> map = new ConcurrentHashMap<>();
            map.put(taskKey, disposable);
            resources.put(pageKey, map);
        }

    }

    /**
     * 解除单个页面所有的订阅
     *
     * @param pageKey 页面key
     */
    public void disposeAll(String pageKey) {
        if (resources.containsKey(pageKey)) {
            ConcurrentHashMap<String, Disposable> map = resources.get(pageKey);
            if (map != null && map.size() > 0) {
                for (Map.Entry<String, Disposable> entry : map.entrySet()) {
                    Disposable disposable = entry.getValue();
                    if (disposable != null) {
                        disposable.dispose();
                    }
                }
            }
        }
    }

    /**
     * 解除单个页面单个task的订阅
     *
     * @param pageKey 页面key
     * @param taskKey 任务key
     */
    public void disposeByKey(String pageKey, String taskKey) {
        if (resources.containsKey(pageKey)) {
            ConcurrentHashMap<String, Disposable> map = resources.get(pageKey);
            if (map != null && map.size() > 0) {
                Iterator<Map.Entry<String, Disposable>> entryKeyIterator = map.entrySet().iterator();
                while (entryKeyIterator.hasNext()) {
                    Map.Entry<String, Disposable> e = entryKeyIterator.next();
                    Disposable disposable = e.getValue();
                    if (disposable != null) {
                        disposable.dispose();
                    }
                }
            }
        }
    }


}
