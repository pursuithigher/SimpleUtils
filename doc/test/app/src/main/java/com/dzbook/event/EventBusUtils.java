package com.dzbook.event;

import android.os.Bundle;

/**
 * EventBusUtils
 *
 * @author Created by caimantang on 16/8/8.
 */
public class EventBusUtils {
    //    private static EventBusUtils instance;
    //
    //    public static EventBusUtils getInstance() {
    //        synchronized (EventBusUtils.class) {
    //            if (null == instance) {
    //                instance = new EventBusUtils();
    //            }
    //        }
    //        return instance;
    //    }

    /**
     * 普通事件注册
     *
     * @param subscriber 事件监听者
     */
    public static void register(Object subscriber) {
        if (!EventBus.getDefault().isRegistered(subscriber)) {
            EventBus.getDefault().register(subscriber);
        }
    }

    /**
     * 取消普通事件注册
     *
     * @param subscriber 事件监听者
     */
    public static void unregister(Object subscriber) {
        if (EventBus.getDefault().isRegistered(subscriber)) {
            EventBus.getDefault().unregister(subscriber);
        }
    }


    /**
     * 粘性事件注册
     *
     * @param subscriber 事件监听者
     */
    public static void registerSticky(Object subscriber) {
        if (!EventBus.getDefault().isRegistered(subscriber)) {
            EventBus.getDefault().registerSticky(subscriber);
        }
    }


    /**
     * 取消粘性事件注册
     *
     * @param subscriber 事件监听者
     */
    public static void unRegisterSticky(Object subscriber) {
        if (EventBus.getDefault().isRegistered(subscriber)) {
            EventBus.getDefault().unregister(subscriber);
            EventBus.getDefault().removeStickyEvent(EventMessage.class);
        }
    }

    /**
     * 发送事件
     *
     * @param object 事件
     */
    public static void sendMessage(Object object) {
        EventBus.getDefault().post(object);
    }

    /**
     * 发送事件
     *
     * @param requestCode requestCode
     * @param type        type
     * @param bundle      bundle
     */
    public static void sendMessage(int requestCode, String type, Bundle bundle) {
        EventBus.getDefault().post(new EventMessage(requestCode, type, bundle));
    }

    /**
     * 发送事件
     *
     * @param requestCode requestCode
     */
    public static void sendMessage(int requestCode) {
        EventBus.getDefault().post(new EventMessage(requestCode));
    }

    /**
     * 发送粘性事件
     *
     * @param requestCode requestCode
     * @param type        type
     * @param bundle      bundle
     */
    public static void sendStickyMessage(int requestCode, String type, Bundle bundle) {
        EventBus.getDefault().postSticky(new EventMessage(requestCode, type, bundle));
    }

    /**
     * 发送粘性事件
     *
     * @param object 事件
     */
    public static void sendStickyMessage(Object object) {
        EventBus.getDefault().postSticky(object);
    }


}
