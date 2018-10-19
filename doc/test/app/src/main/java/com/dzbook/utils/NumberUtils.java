package com.dzbook.utils;

import java.text.DecimalFormat;

/**
 * NumberUtils
 *
 * @author lizz 2018/5/4.
 */

public class NumberUtils {

    /**
     * 保留两位小数
     *
     * @param number    number
     * @param exceptNum exceptNum
     * @return string
     */
    public static String numberConversion(int number, int exceptNum) {
        float num = (float) number / exceptNum;
        DecimalFormat df = new DecimalFormat("0.00");
        return df.format(num);
    }
}
