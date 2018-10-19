package com.dzbook.lib.utils;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import java.lang.reflect.Field;

/**
 * 内存泄露处理工具
 *
 * @author by wxliao on 18/5/3.
 */

public class MemoryLeakUtils {
    /**
     * 处理键盘泄露
     *
     * @param destContext 上下文
     */
    public static void fixInputMethodManagerLeak(Context destContext) {
        if (destContext == null) {
            return;
        }

        InputMethodManager imm = (InputMethodManager) destContext.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm == null) {
            return;
        }

        String[] arr = new String[]{"mLastSrvView", "mCurRootView", "mServedView", "mNextServedView"};
        Field field = null;
        Object viewObject = null;
        for (int i = 0; i < arr.length; i++) {
            String param = arr[i];
            try {
                field = imm.getClass().getDeclaredField(param);
                if (!field.isAccessible()) {
                    field.setAccessible(true);
                }
                viewObject = field.get(imm);
                if (viewObject != null && viewObject instanceof View) {
                    View view = (View) viewObject;
                    if (getActivity(view) == destContext) {
                        // 被InputMethodManager持有引用的context是想要目标销毁的
                        // 置空，破坏掉path to gc节点
                        field.set(imm, null);
                    } else {
                        // 不是想要目标销毁的，即为又进了另一层界面了，不要处理，避免影响原逻辑,也就不用继续for循环了
                        break;
                    }
                }
            } catch (Throwable t) {
//                t.printStackTrace();
            }
        }
    }

    /**
     * 通过view暴力获取getContext()
     *
     * @param view 要获取context的view
     * @return 返回一个activity
     */
    public static Activity getActivity(View view) {
        Activity activity = null;
        if (view.getContext().getClass().getName().contains("com.android.internal.policy.DecorContext")) {
            try {
                Field field = view.getContext().getClass().getDeclaredField("mPhoneWindow");
                field.setAccessible(true);
                Object obj = field.get(view.getContext());
                java.lang.reflect.Method m1 = obj.getClass().getMethod("getContext");
                activity = (Activity) (m1.invoke(obj));
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            activity = (Activity) view.getContext();
        }
        return activity;
    }
}
