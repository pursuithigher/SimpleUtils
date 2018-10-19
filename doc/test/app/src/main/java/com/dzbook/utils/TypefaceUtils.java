package com.dzbook.utils;

import android.content.Context;
import android.graphics.Typeface;
import android.text.TextUtils;
import android.widget.TextView;

import com.dzbook.lib.utils.CompatUtils;

/**
 * 描述：字体工具类 (摘自网上开源项目)
 * <p>
 * 扩展，针对文件名前面是特殊符号这种，有需求请
 * </p>
 */
public class TypefaceUtils {

    private static final int GB_SP_DIFF = 160;


    // 存放国标一级汉字不同读音的起始区位码
    private static final int[] SEC_POS_VALUE_LIST = {1601, 1637, 1833, 2078, 2274, 2302, 2433, 2594, 2787, 3106, 3212, 3472, 3635, 3722, 3730, 3858, 4027, 4086, 4390, 4558, 4684, 4925, 5249, 5600};

    // 存放国标一级汉字不同读音的起始区位码对应读音
    private static final char[] FIRST_LETTER = {'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'w', 'x', 'y', 'z'};
    private static Typeface mHwChineseMedium;

    /**
     * 外部调用，传过来字符串，返回首字母的英文..
     *
     * @param characters 传入的字符串
     * @return string
     */
    public static String getFirstLetter(String characters) {
        if (TextUtils.isEmpty(characters)) {
            return "";
        }
        String data = characters.replaceAll("\\s", "").trim();
        if (TextUtils.isEmpty(data)) {
            return "";
        }
        char star = data.charAt(0);
        if ((star >> 7) == 0) {
            // 判断是否为汉字，如果左移7为为0就不是汉字，否则是汉字
            return getLimitFirstLitter(String.valueOf(data.charAt(0)));
        } else {
            char spell = getFirstLetter(star);
            return getLimitFirstLitter(String.valueOf(spell));
        }
    }

    /**
     * 我们自己自动以的规则
     *
     * @param firstLetter firstLetter
     * @return string
     */
    public static String getLimitFirstLitter(String firstLetter) {
        if (TextUtils.isEmpty(firstLetter) || TextUtils.isEmpty(firstLetter.trim())) {
            return String.valueOf('#');
        }
        Character finalStr = firstLetter.toLowerCase().charAt(0);
        if (finalStr < 'a' || finalStr > 'z') {
            finalStr = '#';
        }
        return String.valueOf(finalStr);
    }

    /**
     * 外部调用，传过来字符，返回首字母的英文
     *
     * @param ch ch
     * @return character
     */
    public static Character getFirstLetter(char ch) {
        char result = ' ';
        byte[] uniCode = null;
        try {
            uniCode = String.valueOf(ch).getBytes("GBK");
        } catch (Throwable e) {
            e.printStackTrace();
            return result;
        }
        // 非汉字
        if (uniCode[0] > 0) {
            return result;
        } else {
            return convert(uniCode);
        }
    }

    /**
     * 获取一个汉字的拼音首字母。 GB码两个字节分别减去160，转换成10进制码组合就可以得到区位码
     * 例如汉字“你”的GB码是0xC4/0xE3，分别减去0xA0（160）就是0x24/0x43
     * 0x24转成10进制就是36，0x43是67，那么它的区位码就是3667，在对照表中读音为‘n'
     *
     * @param bytes bytes
     * @return char
     */
    private static char convert(byte[] bytes) {
        char result = '-';
        int secPosValue = 0;
        int i;
        for (i = 0; i < bytes.length; i++) {
            bytes[i] -= GB_SP_DIFF;
        }
        secPosValue = bytes[0] * 100 + bytes[1];
        for (i = 0; i < 23; i++) {
            if (secPosValue >= SEC_POS_VALUE_LIST[i] && secPosValue < SEC_POS_VALUE_LIST[i + 1]) {
                result = FIRST_LETTER[i];
                break;
            }
        }
        return result;
    }


    /**
     * 华为设置中等字重方法
     *
     * @param textView textView
     */
    public static void setHwChineseMediumFonts(TextView textView) {
        if (textView == null) {
            return;
        }

        if (mHwChineseMedium == null) {
            mHwChineseMedium = Typeface.create("HwChinese-medium", Typeface.NORMAL);
        }

        if (mHwChineseMedium == null) {
            return;
        }
        textView.setTypeface(mHwChineseMedium);

    }

    /**
     * 获取华为medium字体
     *
     * @return Typeface
     */
    public static Typeface getHwChineseMedium() {
        if (mHwChineseMedium == null) {
            mHwChineseMedium = Typeface.create("HwChinese-medium", Typeface.NORMAL);
        }

        return mHwChineseMedium;
    }

    /**
     * 设置正常字体自重
     *
     * @param textView textView
     */
    public static void setRegularFonts(TextView textView) {
        if (textView == null) {
            return;
        }
        textView.setTypeface(Typeface.DEFAULT, Typeface.NORMAL);
    }

    /**
     * 设置行间距
     *
     * @param context     context
     * @param textView    字符串
     * @param lineSpacing 普通语言下的行间距
     * @param lineSpaceZw 藏文下的行间距
     */
    public static void setLineSpacingBylocale(Context context, TextView textView, float lineSpacing, float lineSpaceZw) {
        try {
            String locale = CompatUtils.getLocale(context.getResources().getConfiguration()).toString();
            if (!TextUtils.isEmpty(locale) && locale.equalsIgnoreCase("bo_CN")) {
                textView.setLineSpacing(0, lineSpaceZw);
            } else {
                textView.setLineSpacing(0, lineSpacing);
            }
        } catch (Exception ex) {
            textView.setLineSpacing(0, lineSpacing);
        }
    }
}
