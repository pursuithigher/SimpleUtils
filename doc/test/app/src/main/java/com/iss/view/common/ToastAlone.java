package com.iss.view.common;

import android.support.annotation.StringRes;
import android.text.TextUtils;
import android.widget.TextView;
import android.widget.Toast;

import com.dzbook.AppConst;
import com.dzbook.lib.utils.ALog;
import com.dzbook.lib.utils.CompatUtils;
import com.dzbook.r.c.SettingManager;
import com.ishugui.R;

import io.reactivex.android.schedulers.AndroidSchedulers;


/**
 * 吐司
 *
 * @author wangwz  2017/08/14
 */
public final class ToastAlone {

    private static Toast sToast;

    private ToastAlone() {
    }


    /**
     * 显示短时吐司
     *
     * @param text 文本
     */
    public static void showShort(CharSequence text) {
        show(text, Toast.LENGTH_SHORT);
    }

    /**
     * 显示短时吐司
     *
     * @param resId 资源Id
     */
    public static void showShort(@StringRes int resId) {
        show(resId, Toast.LENGTH_SHORT);
    }

    /**
     * 显示长时吐司
     *
     * @param text 文本
     */
    public static void showLong(CharSequence text) {
        show(text, Toast.LENGTH_LONG);
    }

    /**
     * 显示长时吐司
     *
     * @param resId 资源Id
     */
    public static void showLong(@StringRes int resId) {
        show(resId, Toast.LENGTH_LONG);
    }


    /**
     * 显示吐司
     *
     * @param resId    资源Id
     * @param duration 显示时长
     */
    private static void show(@StringRes int resId, int duration) {
        if (AppConst.getApp() == null) {
            return;
        }
        show(AppConst.getApp().getResources().getText(resId).toString(), duration);
    }

    /**
     * 显示吐司
     *
     * @param text     文本
     * @param duration 显示时长
     */
    private static void show(final CharSequence text, final int duration) {
        ALog.printStack("ToastAlone str=" + text);
        if (TextUtils.isEmpty(text)) {
            return;
        }

        AndroidSchedulers.mainThread().scheduleDirect(new Runnable() {
            @Override
            public void run() {
                if (AppConst.getApp() != null) {
                    // 普通状态：D9 90% 文字：000
                    // 夜间状态：666 90% 文字：fff
                    // 护眼模式：背景666 夜间、日间文字：F1E8D8
                    sToast = Toast.makeText(AppConst.getApp(), text, duration);
                    TextView textView = sToast.getView().findViewById(android.R.id.message);
                    int color;
                    int backDrawable;
                    // 护眼模式
                    if (SettingManager.getInstance(AppConst.getApp()).getReaderEyeMode()) {
                        color = 0XFFF1E8D8;
                        backDrawable = R.drawable.shape_hw_dialog_nignt;
                    } else {
                        // 夜间模式
                        if (SettingManager.getInstance(AppConst.getApp()).getReaderNightMode()) {
                            backDrawable = R.drawable.shape_hw_dialog_nignt;
                            color = 0XFFFFFFFF;
                        } else {
                            backDrawable = R.drawable.shape_hw_dialog;
                            color = 0XFF000000;
                        }
                    }
                    textView.setTextColor(color);
                    textView.setShadowLayer(0, 0, 0, 0);
                    sToast.getView().setBackground(CompatUtils.getDrawable(AppConst.getApp(), backDrawable));
                    sToast.show();
                }
            }
        });
    }

    /**
     * 取消吐司显示
     */
    public static void cancel() {
        if (sToast != null) {
            sToast.cancel();
            sToast = null;
        }
    }
}
