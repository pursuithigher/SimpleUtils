package com.views.ui.customview.dialog;

import android.content.Context;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.view.Window;

import com.views.simpleutils.R;


/**
 * Created by qzzhu on 16-8-30.
 * if you want set Dialog size then set Dialog.getWindow().setWidth/setHeight
 */
public class BaseDialog extends android.support.v7.app.AppCompatDialog{

    private BaseDialog(Context context, int themeResId) {
        super(context, themeResId);
    }

    /*<style name="dialog" parent="Base.Theme.AppCompat.Light.Dialog.Alert">
    <item name="android:windowFrame">@null</item>
    <item name="android:windowNoTitle">true</item>
    <item name="android:windowBackground">@android:color/transparent</item>
    <item name="android:windowIsFloating">true</item>
    <item name="android:windowContentOverlay">@null</item>
    <item name="android:backgroundDimEnabled">true</item>
    <item name="android:windowFullscreen">true</item>
    </style>

    <style name="dialog_deepblack" parent="@android:style/Theme.Dialog">
    <item name="android:windowFrame">@null</item><!--边框-->
    <item name="android:windowIsFloating">true</item><!--是否浮现在activity之上-->
    <item name="android:windowIsTranslucent">false</item><!--半透明-->
    <item name="android:windowNoTitle">true</item><!--无标题-->
    <item name="android:windowBackground">@android:color/transparent</item><!--背景透明-->
    <item name="android:backgroundDimEnabled">false</item><!--真正控制是否有背景的属性，模糊-->
    </style>*/
    /**
     * get a custom view dialog
     * @param context ContextThemeWrapper
     * @param contentView  dialog content View
     * @param background_halftranslate whether has half alpha background
     * @param outsidecancel outSide Cancelable
     * @return the new Dialog instance
     */
    public static BaseDialog getInstance(ContextThemeWrapper context,View contentView, boolean background_halftranslate,boolean outsidecancel){
        BaseDialog dialog;
        if(!background_halftranslate) {
            dialog = new BaseDialog(context, R.style.dialog_deepblack);
        }else{
            dialog = new BaseDialog(context, R.style.dialog);
        }
        dialog.setContentView(contentView);
        dialog.setCanceledOnTouchOutside(outsidecancel);
        return dialog;
    }

    public static void showFromLoc(BaseDialog dialog,int gravity){
        Window window = dialog.getWindow();
        if(window!=null) {
            window.setGravity(gravity);  //此处可以设置dialog显示的位置
            window.setWindowAnimations(R.style.dialog_anim);  //添加动画
        }
        dialog.show();
    }
}

