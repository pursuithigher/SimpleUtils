package hw.sdk.utils;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.Resources;
import android.os.Build;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;

/**
 * UiHelper工具类
 * @author winzows
 */
public class UiHelper {

    /**
     * tag
     */
    public static final String TAG = "UiHelper";

    /**
     * get the screen size.
     *
     * @param context the context.
     * @return the screen size with Inch unit.
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    public static double getScreenInch(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics dm = new DisplayMetrics();
        Display display = wm.getDefaultDisplay();
        display.getMetrics(dm);

        if (isUpperApiLevel(Build.VERSION_CODES.JELLY_BEAN_MR1)) {
            wm.getDefaultDisplay().getRealMetrics(dm);
        }
        double x = Math.pow(dm.widthPixels / dm.xdpi, 2);
        double y = Math.pow(dm.heightPixels / dm.ydpi, 2);
        double screenInch = Math.sqrt(x + y);
        Log.d(TAG, "getScreenInch: " + screenInch);
        return screenInch;
    }

    /**
     * 获取屏幕尺寸
     *
     * @param context 上下文
     * @return 屏幕尺寸
     */
    public static DisplayMetrics getDisplayMetrics(Context context) {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        if (null != context) {
            WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
            if (null != windowManager) {
                windowManager.getDefaultDisplay().getMetrics(displayMetrics);
            }
        }
        return displayMetrics;
    }

    /**
     * 是否符合sdk定义的最低api版本
     *
     * @return true：支持，false：不支持
     */
    private static boolean isUpperApiLevel(int apiLevel) {
        int version = Build.VERSION.SDK_INT;
        return version >= apiLevel;
    }

    /**
     * 获取当前上下文的屏幕宽度
     *
     * @param context 上下文
     * @return 屏幕宽度
     */
    public static int getScreenWidth(Context context) {
        DisplayMetrics displayMetrics = getDisplayMetrics(context);
        return displayMetrics.widthPixels;
    }

    /**
     * 获取当前上下文的屏幕高度
     *
     * @param context 上下文
     * @return 屏幕高度
     */
    public static int getScreenHeight(Context context) {
        DisplayMetrics displayMetrics = getDisplayMetrics(context);
        return displayMetrics.heightPixels;
    }

    /**
     * <一句话功能简述>
     * dp转换为px
     *
     * @param context 上下文
     * @param dp dp
     * @return dp转换为px
     * @see [类、类#方法、类#成员]
     */
    public static float dp2px(Context context, int dp) {
        return dp * getDisplayMetrics(context).density;
    }

    /**
     * 获取资源id
     * @param name name
     * @param context 上下文
     * @return 资源id
     */
    public static int getResId(String name, Context context) {
        Resources r = context.getResources();
        int id = r.getIdentifier(name, "id", context.getPackageName());
        return id;
    }

    /**
     * findViewById
     * @param view view
     * @param id id
     * @param <T> 返回类型
     * @return view
     */
    public static <T extends View> T findViewById(View view, int id) {
        if (null != view) {
            try {
                return (T) view.findViewById(id);
            } catch (ClassCastException ex) {
                Log.e(TAG, "findViewById ClassCastException: " + ex.getMessage());
                throw ex;
            }
        }
        return null;
    }

}
