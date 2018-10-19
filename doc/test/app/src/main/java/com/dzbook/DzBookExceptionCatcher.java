package com.dzbook;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;

import com.dzbook.lib.utils.ALog;
import com.dzbook.lib.utils.SDCardUtil;
import com.dzbook.lib.utils.UtilTimeOffset;
import com.dzbook.utils.FileUtils;
import com.dzbook.utils.PackageControlUtils;
import com.dzbook.utils.SpUtil;
import com.huawei.common.applog.AppLogApi;
import com.huawei.feedback.bean.MetadataBundle;
import com.huawei.phoneserviceuni.common.d.c;
import com.tencent.bugly.crashreport.CrashReport;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Field;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;


/**
 * 崩溃handler
 *
 * @author wxliao 17/7/6
 */
public class DzBookExceptionCatcher {
    private static final String TAG = "DzBookExceptionCatcher";
    /**
     * 定时器 总的次数
     */
    private static final int COUNT_TIME = Short.MAX_VALUE;
    private static DzBookExceptionCatcher catcher = new DzBookExceptionCatcher();
    private Thread.UncaughtExceptionHandler mExceptionHandler;
    /**
     * 定时器的实现
     */
    private Disposable mDisposable;

    private Map<String, String> infos = new HashMap<>();

    private DzBookExceptionCatcher() {

    }

    /**
     * handler 初始化
     *
     * @param context context
     */
    public static void init(final Context context) {
        CrashReport.UserStrategy strategy = new CrashReport.UserStrategy(context);
        strategy.setCrashHandleCallback(new CrashReport.CrashHandleCallback() {
            @Override
            public Map<String, String> onCrashHandleStart(int crashType, String errorType, String errorMessage, String errorStack) {
                collectDeviceInfo(context, errorMessage, errorStack);
                catcher.handleException(context, new Exception(errorMessage + " \nerrorStack:\n" + errorStack));
                return null;
            }
        });
        CrashReport.initCrashReport(context, strategy);
        String userID = SpUtil.getinstance(context).getUserID();
        if (!TextUtils.isEmpty(userID)) {
            CrashReport.setUserId(userID);
        }

        catcher.startCheck();
    }

    /**
     * 定时器的实现 1分钟就检查一下 crasher
     */
    private void startCheck() {
        Observable.interval(0, 1, TimeUnit.MINUTES)
                //设置总共发送的次数
                .take(COUNT_TIME + 1)
                .map(new Function<Long, Long>() {
                    @Override
                    public Long apply(Long aLong) {
                        //aLong从0开始
                        return COUNT_TIME - aLong;
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Long>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        mDisposable = d;
                    }

                    @Override
                    public void onNext(Long value) {
                        Thread.UncaughtExceptionHandler handler = Thread.getDefaultUncaughtExceptionHandler();
                        boolean isBugly = isBugly(handler);
                        if (isBugly) {
                            if (mExceptionHandler == null) {
                                mExceptionHandler = handler;
                            }
                            ALog.dWz(TAG, "get bugly exception handler!");
                        } else {
                            if (mExceptionHandler != null) {
                                Thread.setDefaultUncaughtExceptionHandler(mExceptionHandler);
                            }
                            ALog.dWz(TAG, "reset bugly exception handler!");
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        ALog.printExceptionWz(e);
                        if (mDisposable != null) {
                            mDisposable.dispose();
                        }
                    }

                    @Override
                    public void onComplete() {
                        if (mDisposable != null) {
                            mDisposable.dispose();
                        }
                    }
                });
    }


    /**
     * 收集设备参数信息
     *
     * @param ctx
     */
    private static void collectDeviceInfo(Context ctx, String errorMessage, String errorStack) {
        // 记录系统最后一次的崩溃时间。
        SpUtil.getinstance(ctx).setUncaughtExceptionTime(System.currentTimeMillis());

        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        long mstime = UtilTimeOffset.currentTimeMillisSev();
        String timeNow = dateFormat.format(mstime);

        Field[] fields = Build.class.getDeclaredFields();
        for (Field field : fields) {
            try {
                field.setAccessible(true);
                ALog.dLk(TAG + field.getName() + " : " + field.get(null));
            } catch (Exception e) {
                ALog.eLk(TAG + "an error occured when collect crash info", e);
            }
        }
        ALog.dLk(TAG + "VersionName=" + PackageControlUtils.getAppVersionName());
        ALog.dLk(TAG + "VersionCode=" + PackageControlUtils.getAppVersionCode());
        ALog.dLk(TAG + "timeNow=" + timeNow);
        ALog.dLk(TAG + "ExceptionMsg=" + errorMessage);
        ALog.eLk(TAG + "Stack=" + errorStack);

        try {
            AppLogApi.e(TAG, errorMessage + errorStack);
            SimpleDateFormat formatter = new SimpleDateFormat("yy-MM-dd_HH-mm");
            String dateTime = formatter.format(new Date(mstime));
            String path = SDCardUtil.getInstance().getSDCardAndroidRootDir() + "/" + FileUtils.APP_LOG_DIR_PATH + "Exception"
                    + dateTime + ".txt";
            logFile(path, errorMessage + errorStack);
        } catch (Exception e) {
            ALog.printStackTrace(e);
        }

    }

    /**
     * 保存文件log
     *
     * @param sfile
     * @param msg
     * @return
     */
    private static void logFile(String sfile, String msg) {
        FileOutputStream fos = null;
        try {
            String spath = sfile.substring(0, sfile.lastIndexOf("/"));

            File parentDir = new File(spath);
            if (!parentDir.exists() && !parentDir.mkdirs()) {
                ALog.eWz("logFile mkdirs error");
            }

            File file = new File(sfile);
            // 每满512k备份一下，重新记录。防止文件过大。
            if (file.exists()) {
                boolean delete = file.delete();
                file = new File(sfile);
                ALog.eWz("delete is " + delete);
            }
            fos = new FileOutputStream(file, true);
            @SuppressLint("SimpleDateFormat") SimpleDateFormat formatter = new SimpleDateFormat("[yy/MM/dd_HH:mm:ss] ");
            String dateTime = formatter.format(new Date(System.currentTimeMillis()));
            msg = dateTime + msg + "\n";
            byte[] buffer = msg.getBytes(FileUtils.DEFAULT_CHARSET);

            fos.write(buffer);
            fos.flush();
        } catch (Exception e) {
            ALog.printStackTrace(e);
        } finally {
            FileUtils.closeStream(fos);
        }
    }


    private boolean isBugly(Thread.UncaughtExceptionHandler handler) {
        return handler instanceof com.tencent.bugly.crashreport.crash.e;
    }

    private void handleException(Context context, Throwable var1) {
        if (var1 != null && context != null) {
            this.collectDeviceInfo(context);
            this.collectStackInfo(var1);
            String var2 = infos.get("packageName");
            String var3 = infos.get("versionName");
            String var4 = infos.get("errStack");
            MetadataBundle var5 = new MetadataBundle(907121999, var2, var3);
            var5.putData("errStack", var4);
            var5.putData("osVersion", Build.VERSION.RELEASE);
            AppLogApi.e("AppLogApi/CrashHandler", var5.toString());
            Bundle var6 = new Bundle();
            var6.putString("MetaData", var5.toString());
            var6.putString("LogVersion", "1.0");
            var6.putString("LogSubversion", "1.0");
            var6.putString("ProductName", Build.MODEL);
            var6.putString("ProductVersion", Build.DISPLAY);
            String var7 = com.huawei.feedback.c.a(context);

            var6.putString("SN", var7);
            var6.putString("Eventid", String.valueOf(907121999));
            long var9 = System.currentTimeMillis();
            var6.putString("HappenTime", "" + var9);
            Intent var11 = new Intent("com.huawei.phoneservice.AUTOUPLOAD_REQUEST");
            c.d("AppLogApi/CrashHandler", "CrashHandler no hasPhoneServiceAutoUpload!");
            var11.setClassName(context, "com.huawei.feedback.component.AutoUploadService");
            var11.putExtra("uploadFile", false);
            var11.putExtra("metaData", var6);

            try {
                context.startService(var11);
            } catch (Exception var15) {
                var11.setClassName(context, "com.huawei.feedback.component.AutoUploadService");

                try {
                    context.startService(var11);
                } catch (Exception var14) {
                    c.d("AppLogApi/CrashHandler", "CrashHandler start AutoUploadService intent error");
                }
            }
        }
    }


    private void collectDeviceInfo(Context var1) {
        try {
            PackageManager var2 = var1.getPackageManager();
            PackageInfo var3 = var2.getPackageInfo(var1.getPackageName(), PackageManager.GET_ACTIVITIES);
            infos.put("packageName", var1.getPackageName());
            if (var3 != null) {
                String var4 = var3.versionName == null ? "null" : var3.versionName;
                infos.put("versionName", var4);
            }
        } catch (Exception var5) {
            c.d("AppLogApi/CrashHandler", "an error occured when collect package info" + var5.getMessage());
        }
    }

    private void collectStackInfo(Throwable var1) {
        StringWriter var2 = new StringWriter();
        PrintWriter var3 = new PrintWriter(var2);
        var1.printStackTrace(var3);

        for (Throwable var4 = var1.getCause(); var4 != null; var4 = var4.getCause()) {
            var4.printStackTrace(var3);
        }

        var3.close();
        this.infos.put("errStack", var2.toString());
    }

}
