package com.dzbook.web;

/**
 * JSCode 工具类
 */
public class JSCode {
    /**
     * 拼接javascript
     *
     * @param methodName methodName
     * @param params     params
     * @return js
     */
    public static String loadJs(String methodName, String... params) {
        String param = "";
        if (null != params && params.length > 0) {
            for (int i = 0; i < params.length; i++) {
                if (0 == i) {
                    param = "'" + params[i] + "'";
                } else {
                    param += "," + "'" + params[i] + "'";
                }
            }
        }
        return "javascript:" + methodName + "(" + param + ")";
    }
}
