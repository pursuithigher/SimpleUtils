package com.views.ui.customview.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.views.simpleutils.R;

/**
 * 创建一个Dialog位于中间位置
 * Created by qzzhu on 16-8-30.
 */
public class BaseDialog extends Dialog{

    public BaseDialog(Context context) {
        this(context, R.style.dialog);
    }

    public BaseDialog(Context context, int themeResId) {
        super(context, themeResId);
    }

    /**
     * get a custom view dialog
     * @param context ContextThemeWrapper
     * @param contentView  dialog content View
     * @param background_halftranslate whether has half alpha background
     * @param outsidecancel outSide Cancelable
     * @return the new Dialog instance
     */
    public static BaseDialog getInstance(ContextThemeWrapper context,View contentView, boolean background_halftranslate,boolean outsidecancel){
        BaseDialog dialog = null;
        if(!background_halftranslate) {
            dialog = new BaseDialog(context, R.style.dialog_deepblack);
        }else{
            dialog = new BaseDialog(context, R.style.dialog);
        }
        dialog.setContentView(contentView);
        dialog.setCanceledOnTouchOutside(outsidecancel);


        return dialog;
    }


    /**
     * 设置Dialog对话框的位置和大小
     * @param dialog
     */
    private static void setPosition(BaseDialog dialog){
        Window dialogWindow = dialog.getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        dialogWindow.setGravity(Gravity.LEFT | Gravity.TOP);
        lp.x = 100; // 新位置X坐标
        lp.y = 100; // 新位置Y坐标
        lp.width = 300; // 宽度
        lp.height = 300; // 高度
        lp.alpha = 0.7f; // 透明度
    }

    /**
     * 设置Dialog对话框进出动画
     * @param dialog
     * @return
     */
    public static BaseDialog setAnimation(BaseDialog dialog){
        dialog.getWindow().setWindowAnimations(R.style.dialog_anim);
        return dialog;
    }
}
