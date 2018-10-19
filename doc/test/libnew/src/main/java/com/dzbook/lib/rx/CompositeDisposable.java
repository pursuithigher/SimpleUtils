package com.dzbook.lib.rx;

import java.util.concurrent.ConcurrentHashMap;

import io.reactivex.disposables.Disposable;

/**
 * Disposable工具类
 *
 * @author Created by wxliao on 17/4/7.
 */

public class CompositeDisposable {
    ConcurrentHashMap<String, Disposable> resources;

    /**
     * 构造
     */
    public CompositeDisposable() {
        resources = new ConcurrentHashMap<>();
    }

    /**
     * 添加
     *
     * @param key        键
     * @param disposable 值
     */
    public void addAndDisposeOldByKey(String key, Disposable disposable) {
        disposeByKey(key);
        resources.put(key, disposable);
    }

    /**
     * destory
     */
    public void disposeAll() {
        for (Disposable disposable : resources.values()) {
            try {
                disposable.dispose();
            } catch (Throwable ex) {
                ex.printStackTrace();
            }
        }
        resources.clear();
    }

    /**
     * destory
     *
     * @param key 键
     */
    public void disposeByKey(String key) {
        if (resources.containsKey(key)) {
            try {
                Disposable old = resources.get(key);
                old.dispose();
            } catch (Throwable ex) {
                ex.printStackTrace();
            }

            resources.remove(key);
        }
    }
}
