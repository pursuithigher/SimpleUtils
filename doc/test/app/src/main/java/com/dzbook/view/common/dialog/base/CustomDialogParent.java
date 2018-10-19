package com.dzbook.view.common.dialog.base;

import android.app.Dialog;
import android.content.Context;
import android.view.Display;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;

import com.dzbook.activity.reader.ReaderActivity;
import com.dzbook.utils.DeviceInfoUtils;
import com.dzbook.utils.DimensionPixelUtil;

/**
 * DialogParent： 全局Dialog的唯一入口(可通过它寻找全局所有Dialog)
 *
 * @author wangjianchen
 */
public class CustomDialogParent extends Dialog {

    /**
     * 构造
     *
     * @param context context
     */
    protected CustomDialogParent(Context context) {
        super(context);
    }

    /**
     * 构造
     *
     * @param context    context
     * @param themeResId themeResId
     */
    protected CustomDialogParent(Context context, int themeResId) {
        super(context, themeResId);
    }

    @Override
    public void dismiss() {
        try {
            super.dismiss();
        } catch (Throwable ignored) {
        }
    }

    @Override
    public void show() {
        try {
            //android.view.WindowManager$BadTokenException (原AbsDialog代码，不知当时为什么这样处理，搬过来)
            super.show();
        } catch (Throwable ignored) {
        }
    }

    /**
     * 摘自AbsDialog，子类选择性调用
     */
    @SuppressWarnings("deprecation")
    protected void setProperty() {
        Window window = getWindow();
        WindowManager.LayoutParams p = window.getAttributes();
        //阅读器全屏状态下 去掉状态栏高度
        int statusBarHeight = 0;
        statusBarHeight = DeviceInfoUtils.getInstanse().getStatusBarHeight();
        if (statusBarHeight == 0) {
            statusBarHeight = DimensionPixelUtil.dip2px(getContext(), 18);
        }
        Display d = getWindow().getWindowManager().getDefaultDisplay();
        p.height = (d.getHeight() - statusBarHeight) * 1;
        p.width = d.getWidth() * 1;
        p.gravity = Gravity.BOTTOM | Gravity.CENTER;
        window.setAttributes(p);
    }

    /**
     * 摘自AbsDialog，子类选择性调用
     *
     * @param h 高
     * @param w 宽
     */
    @SuppressWarnings("deprecation")
    protected void setProperty(int w, int h) {
        Window window = getWindow();
        if (window == null) {
            return;
        }
        WindowManager.LayoutParams p = window.getAttributes();
        //阅读器全屏状态下 去掉状态栏高度
        int statusBarHeight = 0;
        if (!(getContext() instanceof ReaderActivity)) {
            statusBarHeight = DeviceInfoUtils.getInstanse().getStatusBarHeight();
            if (statusBarHeight == 0) {
                statusBarHeight = DimensionPixelUtil.dip2px(getContext(), 18);
            }
        } else {
            int flags = ((ReaderActivity) getContext()).getWindow().getAttributes().flags & WindowManager.LayoutParams.FLAG_FULLSCREEN;
            if (flags != WindowManager.LayoutParams.FLAG_FULLSCREEN) {
                statusBarHeight = DeviceInfoUtils.getInstanse().getStatusBarHeight();
                if (statusBarHeight == 0) {
                    statusBarHeight = DimensionPixelUtil.dip2px(getContext(), 18);
                }
            }
        }
        Display d = window.getWindowManager().getDefaultDisplay();
        p.height = (d.getHeight() - statusBarHeight) * 1;
        p.width = d.getWidth() * 1;
        p.gravity = Gravity.BOTTOM | Gravity.CENTER;
        window.setAttributes(p);
    }

}

