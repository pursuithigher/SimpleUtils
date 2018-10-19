package com.dzbook.utils;

import android.content.Context;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.WindowManager;

/**
 * 代码中的单位转换 px dip sp
 *
 * @author perry
 */
public class DimensionPixelUtil {
    private static final int PX = TypedValue.COMPLEX_UNIT_PX;
    private static final int DIP = TypedValue.COMPLEX_UNIT_DIP;
    private static final int SP = TypedValue.COMPLEX_UNIT_SP;
    private static final float HALF = 0.5f;

    /**
     * 转换尺寸
     *
     * @param unit    单位 </br>0 px</br>1 dip</br>2 sp
     * @param value   size 大小
     * @param context context
     * @return 转换结果 float
     */
    public static float getDimensionPixelSize(int unit, float value, Context context) {
        DisplayMetrics metrics = new DisplayMetrics();
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        wm.getDefaultDisplay().getMetrics(metrics);
        switch (unit) {
            case PX:
                return value;
            case DIP:
            case SP:
                return TypedValue.applyDimension(unit, value, metrics);
            default:
                throw new IllegalArgumentException("unknow unix");
        }
    }

    /**
     * 根据手机的屏幕属性从 dip 的单位 转成为 px(像素)
     *
     * @param context context
     * @param value   value
     * @return float
     */
    public static float dip2px(Context context, float value) {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        return value * metrics.density;
    }

    /**
     * 根据手机的屏幕属性从 dip 的单位 转成为 px(像素)
     *
     * @param context   context
     * @param dipLength int 类型
     * @return int
     */
    public static int dip2px(Context context, int dipLength) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dipLength, context.getResources().getDisplayMetrics());
    }

    /**
     * 根据手机的屏幕属性从 px(像素) 的单位 转成为 dip
     *
     * @param context context
     * @param value   value
     * @return float
     */
    public static float px2dip(Context context, float value) {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        return value / metrics.density;
    }

    /**
     * 将sp值转换为px值，保证文字大小不变
     *
     * @param context context
     * @param spValue spValue
     * @return int
     */
    public static int sp2px(Context context, float spValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + HALF);
    }

}
