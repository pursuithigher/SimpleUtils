package com.dzbook.lib.utils;

import android.text.TextUtils;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.util.Collection;
import java.util.regex.Pattern;

/**
 * String util
 *
 * @author zhenglk
 */
public class StringUtil {

    private static final int CACHE_SIZE = 4096;
    private static final String REGEX_HTML = "[\\S\\s]*<((?i)html)>[\\S\\s]*</((?i)html)>[\\S\\s]*";

    /**
     * 为url添加参数。如果已经存在，则替换。
     *
     * @param url   url
     * @param key   key
     * @param value value
     * @return put后的url
     */
    public static String putUrlValue(String url, String key, String value) {
        if (!TextUtils.isEmpty(url) && !TextUtils.isEmpty(key) && !TextUtils.isEmpty(value)) {
            String lowUrl = url.toLowerCase();
            String lowKey = key.toLowerCase();

            int iKeyL = lowUrl.indexOf("?" + lowKey + "=");
            if (-1 == iKeyL) {
                iKeyL = lowUrl.indexOf("&" + lowKey + "=");
            }
            if (-1 == iKeyL) {
                url += (-1 == lowUrl.indexOf('?') ? '?' : '&') + key + "=" + value;
            } else {
                int iValueL = iKeyL + lowKey.length() + 2;
                int iValueR = lowUrl.indexOf("&", iValueL);
                if (-1 == iValueR) {
                    iValueR = lowUrl.length();
                }
                url = url.substring(0, iValueL) + value + url.substring(iValueR);
            }
        }
        return url;
    }

    /**
     * 移除空格，包括移除全角空格。
     * 维护：增加了回车换行
     *
     * @param str str
     * @return 删除space后的str
     */
    public static String delSpaceAndLn(String str) {
        if (TextUtils.isEmpty(str)) {
            return "";
        }

        try {
            char startChar = str.charAt(0);
            if (startChar == '　' || startChar == ' ') {
                str = str.replaceAll("^[　 ]*", "");
            }
            char endChar = str.charAt(str.length() - 1);
            if (endChar == '　' || endChar == ' ') {
                str = str.replaceAll("[　 ]*$", "");
            }
            if (str.contains("\n")) {
                str = str.replace("\n", "");
            }
            if (str.contains("\r")) {
                str = str.replace("\r", "");
            }
        } catch (Exception e) {
            ALog.printStackTrace(e);
        }

        return str;
    }


    private static void appendArrayObjectToStringBuilder(StringBuilder stringBuilder, String delimiter, Object array) {
        int length = Array.getLength(array);
        for (int i = 0; i < length; i++) {
            appendObjectToStringBuilder(stringBuilder, delimiter, Array.get(array, i));
        }
    }

    private static void appendCollectionObjectToStringBuilder(StringBuilder stringBuilder, String delimiter, Collection<?> collection) {
        for (Object aCollection : collection) {
            appendObjectToStringBuilder(stringBuilder, delimiter, aCollection);
        }
    }

    private static void appendObjectToStringBuilder(StringBuilder stringBuilder, String delimiter, Object object) {
        if (object == null) {
            return;
        }
        if (object.getClass().isArray()) {
            appendArrayObjectToStringBuilder(stringBuilder, delimiter, object);
        } else if (object instanceof Collection) {
            appendCollectionObjectToStringBuilder(stringBuilder, delimiter, (Collection) object);
        } else {
            String objectString = object.toString();
            stringBuilder.append(objectString);
            if (!isEmpty(objectString) && !objectString.endsWith(delimiter)) {
                stringBuilder.append(delimiter);
            }
        }
    }

    /**
     * 测试传入的字符串是否为空
     *
     * @param string 需要测试的字符串
     * @return 如果字符串为空（包括不为空但其中为空白字符串的情况）返回true，否则返回false
     */
    public static boolean isEmpty(String string) {
        return string == null || string.trim().length() == 0;
    }


    /**
     * InputSteam 转换到 String，会把输入流关闭
     *
     * @param inputStream 输入流
     * @return String 如果有异常则返回null
     */
    public static String stringFromInputStream(InputStream inputStream) {
        try {
            byte[] readBuffer = new byte[CACHE_SIZE];
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            while (true) {
                int readLen = inputStream.read(readBuffer, 0, CACHE_SIZE);
                if (readLen <= 0) {
                    break;
                }

                byteArrayOutputStream.write(readBuffer, 0, readLen);
            }

            return byteArrayOutputStream.toString("UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                inputStream.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    /**
     * 封装判断传入的参数是否为空或者空字符串
     *
     * @param text str
     * @return boolean
     */
    public static boolean isEmpty(CharSequence... text) {
        for (CharSequence str : text) {
            if (TextUtils.isEmpty(str)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 封装判断传入的参数是否为空或者空字符串
     *
     * @param text str
     * @return boolean
     */
    public static boolean isAllEmpty(CharSequence... text) {
        if (null == text || text.length == 0) {
            return true;
        }
        for (CharSequence str : text) {
            if (!TextUtils.isEmpty(str)) {
                return false;
            }
        }
        return true;
    }


    /**
     * 检测是否有emoji表情
     *
     * @param source source
     * @return 包含emoji
     */
    public static boolean containsEmoji(String source) {
        if (TextUtils.isEmpty(source)) {
            return false;
        }
        int len = source.length();
        for (int i = 0; i < len; i++) {
            char codePoint = source.charAt(i);
            if (codePoint >= 0x20 && codePoint <= 0xD7FF) {
                continue;
            } else if (codePoint >= 0xE000 && codePoint <= 0xFFFD) {
                continue;
            } else if (codePoint == 0x9 || codePoint == 0xA || codePoint == 0xD) {
                continue;
            }
            return true;
        }
        return false;
    }

    /**
     * 判断文本是否是html
     *
     * @param text 文本
     * @return 判断结果
     */
    public static boolean isHtml(String text) {
        return Pattern.matches(REGEX_HTML, text);
    }

    /**
     * 获取url的参数串
     *
     * @param url url
     * @return 参数
     */
    public static String getUrlLastParamsString(String url) {
        int index = url.indexOf("?");
        if (index != -1) {
            return url.substring(index + 1, url.length());
        }
        return "";
    }
}
