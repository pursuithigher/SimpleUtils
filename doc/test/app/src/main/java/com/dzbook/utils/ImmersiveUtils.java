package com.dzbook.utils;

import android.app.Activity;
import android.os.Build;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.dzbook.lib.utils.ALog;
import com.dzbook.lib.utils.CompatUtils;

/**
 * ImmersiveUtils
 *
 * @author by lizz
 */
public class ImmersiveUtils {

    /**
     * 导航栏
     *
     * @param activity        activity
     * @param statusColor     状态栏色值
     * @param navigationColor 导航栏色值
     */
    public static void init(Activity activity, int statusColor, int navigationColor) {
        try {
            Window window = activity.getWindow();

            if (window != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

                window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY | View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

                /*
                 * 状态栏的适配
                 */
                window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                window.setStatusBarColor(CompatUtils.getColor(activity, statusColor));

                /*
                 * 导航栏沉浸
                 */
                if (DeviceInfoUtils.getInstanse().checkDeviceHasNavigationBar(activity)) {
                    window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
                    int emuiVersion = DeviceUtils.getEMUIVersion();
                    if (emuiVersion < 0 || emuiVersion > 4) {
                        window.setNavigationBarColor(CompatUtils.getColor(activity, navigationColor));
                    }
                }
            }
        } catch (Throwable e) {
            ALog.printExceptionWz(e);
        }
    }

}
