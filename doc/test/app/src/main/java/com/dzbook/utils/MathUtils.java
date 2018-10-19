package com.dzbook.utils;

/**
 * MathUtils
 *
 * @author caimantang on 2018/4/26.
 */

public class MathUtils {
    private static final double TEN_SIZE = 10.0;

    /**
     * 处理数据
     *
     * @param i i
     * @return float
     */
    public static float meg(float i) {
        String parseFloast = i + "";
        if (parseFloast.endsWith(".5")) {
            return i;
        }
        //小数点后两位前移，并四舍五入
        int b = Math.round(i * 10);
        //还原小数点后两位
        double c = (double) b / TEN_SIZE;
        if ((c * 10) % 5 != 0) {
            //小数点前移，并四舍五入
            int d = (int) Math.round(c);
            //还原小数点
            c = (double) d;
        }
        return (float) c;
    }
}
