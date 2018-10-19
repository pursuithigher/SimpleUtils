package com.dzbook.lib.utils;

import android.text.TextUtils;
import android.util.Log;

import com.dzbook.lib.net.DzSchedulers;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 常规使用方法类似 log 。这里添加了一些功能接口，并且多出来一个开关设置。
 *
 * @author zhenglk
 */
public class ALog {
    /**
     * 本地崩溃log
     */
    public static final String APP_LOG_DIR_PATH = ".ishugui/.log/";

    /**
     * 默认tag
     */
    private static final String TAG = "akcommon";

    /**
     * 李忠忠 日志TAG
     */
    private static final String TAG_LIZZ = "tag_lizz";

    /**
     * 郑良坤 日志TAG
     */
    private static final String TAG_ZHENGLK = "tag_zhenglk";
    /**
     * 胡中顺 日志TAG
     */
    private static final String TAG_HUZS = "tag_huzs";
    /**
     * 王文洲 日志TAG
     */
    private static final String TAG_WZ = "tag_wz";

    /**
     * 董典周 日志TAG
     */
    private static final String TAG_DONGDZ = "tag_dongdz";

    private static final String TAG_LWX = "tag_liaowenxin";


    /**
     * debugMode:true 打开开关debug日志。false 关闭debug日志。
     */
    private static boolean debugMode = false;

    public static boolean getDebugMode() {
        return ALog.debugMode;
    }

    /**
     * 设置debug
     *
     * @param value 是否debug
     */
    public static void setDebugMode(boolean value) {
        ALog.debugMode = value;
        Log.i(TAG_ZHENGLK, "ALog.setDebugMode(" + value + ")");
    }

    /**
     * log级别
     *
     * @param tag tag
     * @param msg 打印类型
     * @return int
     */
    public static int i(String tag, String msg) {
        if (!debugMode) {
            return -1;
        }
        if (null == msg) {
            return printStack("*****i:msg=null*****");
        }
        return Log.i(tag, msg);
    }

    /**
     * log级别
     *
     * @param tag        tag
     * @param msg        打印类型
     * @param stackIndex 几级栈
     * @return int
     */
    public static int i(String tag, String msg, int stackIndex) {
        if (!debugMode) {
            return -1;
        }
        if (null == msg) {
            return printStack("*****i:msg=null*****");
        }
        return Log.i(tag, msg + " @" + getCallStackTraceStr(stackIndex, !debugMode));
    }

    /**
     * log级别
     *
     * @param tag tag
     * @param msg 打印类型
     * @return int
     */
    public static int v(String tag, String msg) {
        if (!debugMode) {
            return -1;
        }
        if (null == msg) {
            return printStack("*****v:msg=null*****");
        }
        return Log.v(tag, msg);
    }

    /**
     * log级别
     *
     * @param tag        tag
     * @param msg        打印类型
     * @param stackIndex 几级栈
     * @return int
     */

    public static int v(String tag, String msg, int stackIndex) {
        if (!debugMode) {
            return -1;
        }
        if (null == msg) {
            return printStack("*****v:msg=null*****");
        }
        return Log.v(tag, msg + " @" + getCallStackTraceStr(stackIndex, !debugMode));
    }


    /**
     * log级别
     *
     * @param tag tag
     * @param msg 打印类型
     * @return int
     */
    public static int d(String tag, String msg) {
        if (!debugMode) {
            return -1;
        }
        if (null == msg) {
            return printStack("*****d:msg=null*****");
        }
        return Log.d(tag, msg);
    }

    /*****************************  李忠忠 debug日志方法开始 ***********************/

    /**
     * log级别
     *
     * @param msg 打印类型
     * @return int
     */
    public static int dZz(String msg) {
        if (!debugMode) {
            return -1;
        }
        if (null == msg) {
            return printStack("*****d:msg=null*****");
        }
        return Log.d(TAG_LIZZ, msg);
    }

    /**
     * log for debug
     *
     * @param format tag
     * @param params 内容
     * @return int
     * @see Log#d(String, String)
     */
    public static int dZz(String format, Object... params) {

        if (!debugMode) {
            return -1;
        }
        if (null == params) {
            return printStack("*****d:msg=null*****");
        }

        return Log.d(TAG_LIZZ, String.format(format, params));
    }

    /**
     * log级别
     *
     * @param msg 打印类型
     * @return int
     */
    public static int eZz(String msg) {
        if (!debugMode) {
            return -1;
        }
        if (null == msg) {
            return printStack("*****e:msg=null*****");
        }
        return Log.e(TAG_LIZZ, msg);
    }

    /**
     * log级别
     *
     * @param msg 打印类型
     * @param tr  异常
     * @return int
     */

    public static int eZz(String msg, Exception tr) {
        if (!debugMode) {
            return -1;
        }
        if (null == msg) {
            return printStack("*****e:msg=null*****");
        }
        return Log.e(TAG_LIZZ, msg, tr);
    }

    /**
     * log for error
     *
     * @param format message format, such as "%d ..."
     * @param params message content params
     * @return int
     * @see Log#e(String, String)
     */
    public static int eZz(String format, Object... params) {

        if (!debugMode) {
            return -1;
        }
        if (null == params) {
            return printStack("*****e:msg=null*****");
        }
        return Log.e(TAG_LIZZ, String.format(format, params));
    }

    /**
     * log级别
     *
     * @param msg 打印类型
     * @return int
     */
    public static int iZz(String msg) {
        if (!debugMode) {
            return -1;
        }
        if (null == msg) {
            return printStack("*****i:msg=null*****");
        }
        return Log.i(TAG_LIZZ, msg);
    }


    /*****************************  李忠忠 debug日志方法结束 ***********************/

    /*****************************  蔡满堂 debug日志方法开始 ***********************/
    /**
     * log级别
     *
     * @param msg 打印类型
     * @return int
     */
    public static int cmtDebug(Object msg) {
        if (!debugMode) {
            return -1;
        }
        return longLogD("cmt--" + getCallStackTraceStr(1, !debugMode) + "   ", msg);
    }

    /**
     * log
     *
     * @param msg 打印类型
     * @return int
     */
    public static int thirdLog(Object msg) {
        if (!debugMode) {
            return -1;
        }
        return longLogD("thirdLog-->" + getCallStackTraceStr(1, !debugMode) + "   ", msg);
    }

    /**
     * 打印超长log
     *
     * @param tag 类型
     * @param obj 内容
     * @return int
     */
    public static int longLogD(String tag, Object obj) {
        String log = String.valueOf(obj);
        int maxLength = 1024 * 3;
        if (log.length() > maxLength) {
            String show = log.substring(0, maxLength);
            d(tag, show);
            if ((log.length() - maxLength) > maxLength) {
                String partLog = log.substring(maxLength, log.length());
                longLogD(tag, partLog);
            } else {
                String surplusLog = log.substring(maxLength, log.length());
                d(tag, surplusLog);
            }
        } else {
            d(tag, log);
        }
        return -1;
    }


    /*****************************  蔡满堂 debug日志方法结束 ***********************/

    /*****************************  董典周 debug日志方法开始 ***********************/

    /**
     * log
     *
     * @param msg 内容
     * @return int
     */
    public static int eDongdz(String msg) {
        if (!debugMode) {
            return -1;
        }
        if (TextUtils.isEmpty(msg)) {
            return printStack("*****e:msg=null*****");
        }
        return Log.e(TAG_DONGDZ, msg);
    }

    /**
     * log
     *
     * @param msg 内容
     * @param tr  异常
     * @return int
     */
    public static int eDongdz(String msg, Exception tr) {
        if (!debugMode) {
            return -1;
        }
        if (TextUtils.isEmpty(msg)) {
            return printStack("*****e:msg=null*****");
        }
        return Log.e(TAG_DONGDZ, msg, tr);
    }

    /**
     * log for error
     *
     * @param format message format, such as "%d ..."
     * @param params message content params
     * @return int
     * @see Log#e(String, String)
     */
    public static int eDongdz(String format, Object... params) {

        if (!debugMode) {
            return -1;
        }
        if (null == params) {
            return printStack("*****e:msg=null*****");
        }
        return Log.e(TAG_DONGDZ, String.format(format, params));
    }


    /*****************************  董典周 debug日志方法结束 ***********************/


    /*****************************  廖文新 debug日志方法开始 ***********************/

    /**
     * log
     *
     * @param msg 内容
     * @return int
     */
    public static int dLwx(String msg) {
        if (!debugMode) {
            return -1;
        }
        if (null == msg) {
            return printStack("*****d:msg=null*****");
        }
        return Log.d(TAG_LWX, msg);
    }

    /**
     * log
     *
     * @param msg 内容
     * @return int
     */
    public static int eLwx(String msg) {
        if (!debugMode) {
            return -1;
        }
        if (null == msg) {
            return printStack("*****e:msg=null*****");
        }
        return Log.e(TAG_LWX, msg);
    }

    /*****************************  廖文新 debug日志方法开始 ***********************/

    /*****************************  郑良坤 debug日志方法开始 ***********************/

    /**
     * log
     *
     * @param msg 内容
     * @return int
     */
    public static int dLk(String msg) {
        if (!debugMode) {
            return -1;
        }
        if (null == msg) {
            return printStack("*****d:msg=null*****");
        }
        return Log.d(TAG_ZHENGLK, msg);
    }

    /**
     * log for debug
     *
     * @param format message format, such as "%d ..."
     * @param params message content params
     * @return int
     * @see Log#d(String, String)
     */
    public static int dLk(String format, Object... params) {

        if (!debugMode) {
            return -1;
        }
        if (null == params) {
            return printStack("*****d:msg=null*****");
        }

        return Log.d(TAG_ZHENGLK, String.format(format, params));
    }

    /**
     * log
     *
     * @param msg 内容
     * @return int
     */

    public static int eLk(String msg) {
        if (!debugMode) {
            return -1;
        }
        if (null == msg) {
            return printStack("*****e:msg=null*****");
        }
        return Log.e(TAG_ZHENGLK, msg);
    }

    /**
     * log
     *
     * @param msg 内容
     * @param tr  异常
     * @return int
     */
    public static int eLk(String msg, Exception tr) {
        if (!debugMode) {
            return -1;
        }
        if (null == msg) {
            return printStack("*****e:msg=null*****");
        }
        return Log.e(TAG_ZHENGLK, msg, tr);
    }

    /**
     * log for error
     *
     * @param format message format, such as "%d ..."
     * @param params message content params
     * @return int
     * @see Log#e(String, String)
     */
    public static int eLk(String format, Object... params) {

        if (!debugMode) {
            return -1;
        }
        if (null == params) {
            return printStack("*****e:msg=null*****");
        }
        return Log.e(TAG_ZHENGLK, String.format(format, params));
    }

    /**
     * log
     *
     * @param tag     tag
     * @param message 内容
     * @return int
     */
    public static int e(String tag, String message) {

        if (!debugMode) {
            return -1;
        }

        return Log.e(tag, message);
    }

    /**
     * log
     *
     * @param msg 内容
     * @return int
     */

    public static int iLk(String msg) {
        if (!debugMode) {
            return -1;
        }
        if (null == msg) {
            return printStack("*****i:msg=null*****");
        }
        return Log.i(TAG_ZHENGLK, msg);
    }

    /**
     * log for information
     *
     * @param format message format, such as "%d ..."
     * @param params message content params
     * @return int
     * @see Log#i(String, String)
     */
    public static int iLk(String format, Object... params) {

        if (!debugMode) {
            return -1;
        }
        if (null == params) {
            return printStack("*****i:msg=null*****");
        }
        return Log.i(TAG_ZHENGLK, String.format(format, params));
    }

    /**
     * log
     *
     * @param msg 内容
     * @param tr  异常
     * @return int
     */
    public static int iLk(String msg, Exception tr) {
        if (!debugMode) {
            return -1;
        }
        if (null == msg) {
            return printStack("*****i:msg=null*****");
        }
        return Log.i(TAG_ZHENGLK, msg, tr);
    }

    /*****************************  郑良坤 debug日志方法结束 ***********************/

    /*****************************  王文洲 debug日志方法开始 ***********************/

    /**
     * log
     *
     * @param msg 内容
     * @return int
     */
    public static int dWz(Object msg) {
        if (!debugMode) {
            return -1;
        }
        if (null == msg) {
            return printStack("*****d:msg=null*****");
        }
        return Log.d(TAG_WZ, String.valueOf(msg));
    }

    /**
     * log for debug
     *
     * @param subTag message format, such as "%d ..."
     * @param msg    message content params
     * @return int
     * @see Log#d(String, String)
     */
    public static int dWz(String subTag, Object msg) {
        if (!debugMode) {
            return -1;
        }
        if (null == msg) {
            return printStack("*****d:msg=null*****");
        }

        return Log.d(TAG_WZ + "-" + subTag, String.valueOf(msg));
    }

    /**
     * log
     *
     * @param msg 内容
     * @return int
     */
    public static int eWz(String msg) {
        if (!debugMode) {
            return -1;
        }
        if (TextUtils.isEmpty(msg)) {
            return printStack("*****e:msg=null*****");
        }
        return Log.e(TAG_WZ, msg);
    }

    /**
     * log for error
     *
     * @param format message format, such as "%d ..."
     * @param params message content params
     * @return int
     * @see Log#e(String, String)
     */
    public static int eWz(String format, Object... params) {

        if (!debugMode) {
            return -1;
        }
        if (null == params) {
            return printStack("*****e:msg=null*****");
        }
        return Log.e(TAG_WZ, String.format(format, params));
    }

    /**
     * log
     *
     * @param msg 内容
     * @return int
     */

    public static int iWz(Object msg) {
        if (!debugMode) {
            return -1;
        }
        if (null == msg) {
            return printStack("*****i:msg=null*****");
        }
        return Log.i(TAG_WZ, String.valueOf(msg));
    }

    /**
     * log
     *
     * @param msg 内容
     * @return int
     */

    public static int printStackWz(Object msg) {
        if (!debugMode) {
            return -1;
        }
        return Log.e(TAG_WZ + "-stack", toMsg(msg) + new LogThrowable().getStackTraceStr(1, 12));
    }

    /**
     * log
     *
     * @param e 异常
     * @return int
     */
    public static int printExceptionWz(Throwable e) {
        if (!debugMode) {
            if (null == e) {
                return printStack("*****i:exception=null*****");
            }
        }
        return eWz(getStackTraceString(e));
    }

    /*****************************  王文洲 debug日志方法结束 ***********************/

    /**
     * log
     *
     * @param e 异常
     */
    public static void printStackTrace(Exception e) {
        if (debugMode) {
            e.printStackTrace();
        }
    }


    /**
     * log
     *
     * @param msg 内容
     */
    public static void fileLog(String msg) {
        if (!debugMode) {
            return;
        }
        ALog.f(SDCardUtil.getInstance().getSDCardAndroidRootDir() + "/" + APP_LOG_DIR_PATH + "_log.txt", msg);
    }

    /**
     * 保存文件log
     *
     * @param sfile 文件
     * @param msg   内容
     */
    public static void f(final String sfile, final String msg) {
        if (!debugMode) {
            return;
        }
        Runnable r = new Runnable() {
            @Override
            public void run() {
                if (null == sfile || null == msg || msg.length() == 0) {
                    return;
                }
                FileOutputStream fos = null;
                try {
                    String spath = sfile.substring(0, sfile.lastIndexOf("/"));

                    File parentdir = new File(spath);
                    if (!parentdir.exists()) {
                        boolean isSuccess = parentdir.mkdirs();
                        if (!isSuccess) {
                            return;
                        }
                    }

                    File file = new File(sfile);
                    // 每满512k备份一下，重新记录。防止文件过大。
                    if (file.exists() && file.length() > 1024 * 1024) {
                        File filebak = new File(sfile + ".bak");
                        if (filebak.exists() && !filebak.delete()) {
                            ALog.eLk("f delete fail");
                        }
                        if (!file.renameTo(filebak)) {
                            ALog.eLk("f renameTo fail");
                        }
                        file = new File(sfile);
                    }
                    fos = new FileOutputStream(file, true);
                    SimpleDateFormat formatter = new SimpleDateFormat("[MM/dd_HH:mm:ss_SSS] ");
                    String dateTime = formatter.format(new Date(System.currentTimeMillis()));
                    String thisMsg = dateTime + msg + "\n";
                    ALog.iLk("flog: " + thisMsg);
                    byte[] buffer = thisMsg.getBytes("UTF-8");
                    fos.write(buffer);
                    fos.flush();
                } catch (Exception e) {
                    ALog.printStackTrace(e);
                } finally {
                    if (fos != null) {
                        try {
                            fos.close();
                        } catch (IOException e) {
                        }
                    }
                }

            }
        };
        DzSchedulers.execute(r);
    }

    /**
     * log
     *
     * @param msg 内容
     * @return int
     */
    public static int printStack(Object msg) {
        if (!debugMode) {
            return -1;
        }
        return Log.e(TAG + "-stack", toMsg(msg) + new LogThrowable().getStackTraceStr(1, 10));
    }

    /**
     * 打印栈使用。
     */
    public static class LogThrowable extends Throwable {

        /**
         * log
         *
         * @param start 开始
         * @param limit 结束
         * @return String
         */

        public String getStackTraceStr(final int start, final int limit) {
            StringBuffer sBuf = new StringBuffer();
            StackTraceElement[] stacks = getStackTrace();
            for (int i = start; i < stacks.length && i < limit + start; i++) {
                StackTraceElement stack = stacks[i];
                sBuf.append("\n").append(i).append(",").append(getStackStr(stack));
            }
            if (stacks.length > limit + start) {
                sBuf.append("\n### stack at (").append(start).append("-").append(limit + start - 1)
                        .append(") of (0-").append(stacks.length - 1).append(") ###");
            } else {
                sBuf.append("\n### stack of (0-").append(stacks.length - 1).append(") ###");
            }
            return sBuf.toString();
        }

        private String getStackStr(StackTraceElement stack) {
            StringBuilder buf = new StringBuilder(80);

            String className = stack.getClassName();
            int index = className.lastIndexOf(".");
            if (index > 0 && index + 1 < className.length()) {
                className = className.substring(index + 1);
            }
            buf.append(className);
            buf.append('.');
            buf.append(stack.getMethodName());

            if (stack.isNativeMethod()) {
                buf.append("(Native Method)");
            } else {
                String fName = stack.getFileName();

                if (fName == null) {
                    buf.append("(Unknown Source)");
                } else {
                    int lineNum = stack.getLineNumber();

                    buf.append('(');
                    buf.append(fName);
                    if (lineNum >= 0) {
                        buf.append(':');
                        buf.append(lineNum);
                    }
                    buf.append(')');
                }
            }
            return buf.toString();
        }
    }

    /**
     * 转string
     *
     * @param object 内容
     * @return String
     */
    public static String toMsg(Object object) {
        return toMsg("", object);
    }

    /**
     * 转string
     *
     * @param header 头
     * @param object 内容
     * @return String
     */
    private static String toMsg(String header, Object object) {
        if (null == object) {
            return "<--log msg is null-->";
        } else if (object instanceof Map) {
            StringBuilder buf = new StringBuilder(header);
            Map map = (Map) object;
            if (map.isEmpty()) {
                buf.append("<--map is empty-->");
            } else {
                buf.append("\n/^^(map size:").append(map.size()).append(")^^\\\n");
                for (Map.Entry entry : (Iterable<Map.Entry>) map.entrySet()) {
                    buf.append(header).append(entry.getKey()).append(" = ")
                            .append(toMsg(header + " ", entry.getValue())).append("\n");
                }
                buf.append("\\__(map size:").append(map.size()).append(")__/\n");
            }
            return buf.toString();
        } else if (object instanceof List) {
            StringBuilder buf = new StringBuilder(header);
            List list = (List) object;
            if (list.isEmpty()) {
                buf.append("<--list is empty-->");
            } else {
                buf.append("\n/^^(list size:").append(list.size()).append(")^^\\\n");
                for (Object obj : list) {
                    buf.append(header).append(toMsg(header + " ", obj)).append("\n");
                }
                buf.append("\\__(list size:").append(list.size()).append(")__/\n");
            }
            return buf.toString();
        } else if (object instanceof Object[]) {
            StringBuilder buf = new StringBuilder(header);
            Object[] list = (Object[]) object;
            if (list.length == 0) {
                buf.append("<--array is empty-->");
            } else {
                buf.append("\n/^^(array length:").append(list.length).append(")^^\\\n");
                for (Object obj : list) {
                    buf.append(header).append(toMsg(header + " ", obj)).append("\n");
                }
                buf.append("\\__(array length:").append(list.length).append(")__/\n");
            }
            return buf.toString();
        } else {
            return header + object;
        }
    }

    /**
     * 将异常Exception对象转换成字符串
     *
     * @param tr 异常
     * @return String
     */
    public static String getStackTraceString(Throwable tr) {
        if (tr == null) {
            return "";
        }
        Writer writer = new StringWriter();
        PrintWriter printWriter = new PrintWriter(writer);
        tr.printStackTrace(printWriter);
        String errDes = writer.toString();
        try {
            printWriter.close();
            writer.close();
        } catch (Exception ignore) {
        }
        return errDes;
    }

    /**
     * 打印栈使用。
     */
    private static String getCallStackTraceStr(int index, boolean simple) {
        StackTraceElement[] stacks = Thread.currentThread().getStackTrace();
        if (stacks.length > index + 3) {
            StringBuilder buf = new StringBuilder(256);
            appendStack(buf, stacks[index + 3], simple);
            return buf.toString();
        }
        return "";
    }

    private static void appendStack(StringBuilder buf, StackTraceElement stack, boolean simple) {
        String className = stack.getClassName();
        int index = className.lastIndexOf(".");
        if (index > 0 && index + 1 < className.length()) {
            className = className.substring(index + 1);
        }
        buf.append(className);
        buf.append('.');
        buf.append(stack.getMethodName());
        if (simple) {
            return;
        }
        if (stack.isNativeMethod()) {
            buf.append("(Native Method)");
        } else {
            String fName = stack.getFileName();
            if (fName == null) {
                buf.append("(Unknown Source)");
            } else {
                int lineNum = stack.getLineNumber();

                buf.append('(');
                buf.append(fName);
                if (lineNum >= 0) {
                    buf.append(':');
                    buf.append(lineNum);
                }
                buf.append(')');
            }
        }
    }

}
