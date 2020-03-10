package com.example.myapplication;

import android.app.Activity;
import android.app.Instrumentation;
import android.os.IBinder;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class HookUtils {
    public static void hookInstrumentation() {
        try {
            Class<?> activityThread = null;
            activityThread = Class.forName("android.app.ActivityThread");
            Method sCurrentActivityThread = activityThread.getDeclaredMethod("currentActivityThread");
            sCurrentActivityThread.setAccessible(true);
            //获取ActivityThread 对象
            Object activityThreadObject = sCurrentActivityThread.invoke(activityThread);

            //获取 Instrumentation 对象
            Field mInstrumentation = activityThread.getDeclaredField("mInstrumentation");
            mInstrumentation.setAccessible(true);
            Instrumentation instrumentation = (Instrumentation) mInstrumentation.get(activityThreadObject);
            CustomInstrumentation customInstrumentation = new CustomInstrumentation(instrumentation);
            //将我们的 customInstrumentation 设置进去
            mInstrumentation.set(activityThreadObject, customInstrumentation);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }

    }

    public static void getActivities(){
        try {
            Class<?> activityThread = null;
            activityThread = Class.forName("android.app.ActivityThread");
            Method sCurrentActivityThread = activityThread.getDeclaredMethod("currentActivityThread");
            sCurrentActivityThread.setAccessible(true);
            //获取ActivityThread 对象
            Object activityThreadObject = sCurrentActivityThread.invoke(activityThread);

            //获取 Instrumentation 对象
            Field mActivities = activityThread.getDeclaredField("mActivities");
            mActivities.setAccessible(true);

            Map<IBinder,?> records = (Map<IBinder,?>) mActivities.get(activityThreadObject);
            List<Activity> activities = getActivities(records.values());
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
    }

    public static List<Activity> getActivities(Collection<?> values){
        try {
            Class<?> aClass = Class.forName("android.app.ActivityThread$ActivityClientRecord");
            Field activity = aClass.getDeclaredField("activity");
            activity.setAccessible(true);
            List<Activity> activities = new ArrayList<>(values.size());
            for(Object object:values) {
                Activity o = (Activity) activity.get(object);
                activities.add(o);
            }
            return activities;

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }
}
