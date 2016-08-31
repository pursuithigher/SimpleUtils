package com.utils.ui;

import android.app.Dialog;
import android.content.Context;
import android.view.ContextThemeWrapper;
import android.view.View;

import com.qzzhu.simpleutils.R;

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
}
