package com.dzbook.utils;

import android.app.Activity;
import android.content.Context;
import android.provider.Settings;
import android.view.Window;
import android.view.WindowManager;

/**
 * 屏幕工具类
 *
 * @author zshu on 16/12/16.
 */
public class ScreenUtils {
    private static final float COLOR_MAX_SIZE = 255f;
    private static final float COLOR_100_SIZE = 255f;

    /**
     * 获得<b>系统</b>当前屏幕亮度值 [0, 255]
     *
     * @param context context
     * @return int 当前屏幕亮度值 [0, 255]
     */
    public static int getSystemScreenBrightness(Context context) {
        int screenBrightness = 0;
        try {
            screenBrightness = Settings.System.getInt(context.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS);
        } catch (Exception localException) {
            screenBrightness = 127;
        }
        return screenBrightness;
    }


    /**
     * 改变<b>APP</b>当前Window亮度
     *
     * @param brightness [0, 255]
     * @param activity   activity
     * @param-screenBrightness [0.0, 1.0f]
     */
    public static void setAppScreenBrightnes(Activity activity, int brightness) {
        Window localAppWindow = activity.getWindow();
        WindowManager.LayoutParams localAppLayoutParams = localAppWindow.getAttributes();
        /**
         * screenBrightness是一个0.0~1.0之间的float类型的参数，亮度由0.0~1.0递增。
         * 如果该值小于0，则默认采取最优的屏幕亮度来适配(经过测试就是系统亮度)。
         */
        if (brightness == -1) {
            localAppLayoutParams.screenBrightness = WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_NONE;
        } else {
            localAppLayoutParams.screenBrightness = (brightness <= 0 ? 1 : brightness) / COLOR_MAX_SIZE;
        }
        localAppWindow.setAttributes(localAppLayoutParams);
    }

    /**
     * 亮度设置
     *
     * @param brightness 亮度值
     * @param activity   activity
     * @防止全黑屏，这里只对屏幕亮度的实际设置值做了处理，对ProgressBar的进度显示和TextView的值显示没有影响。
     */
    public static void updateScreenBrightnessMask(Activity activity, int brightness) {
        if (brightness < 10) {
            brightness = 5;
        } else if (brightness > 100) {
            brightness = 100;
        }
        //更新APP的屏幕亮度
        changeAppScreenBrightnes(activity, brightness);
    }

    /**
     * 改变<b>APP</b>当前Window亮度
     *
     * @param activity   activity
     * @param brightness [0, 100]
     * @param-screenBrightness [0.0, 1.0f]
     */
    public static void changeAppScreenBrightnes(Activity activity, int brightness) {
        WindowManager.LayoutParams attrs = activity.getWindow().getAttributes();
        attrs.screenBrightness = brightness / COLOR_100_SIZE;
        activity.getWindow().setAttributes(attrs);
    }
    //************************ Screen Brightness ************************//
}
