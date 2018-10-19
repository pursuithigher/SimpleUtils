package com.dzbook.utils;

import android.annotation.TargetApi;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.content.FileProvider;

import com.dzbook.activity.person.PersonPluginActivity;
import com.dzbook.database.bean.BookInfo;
import com.dzbook.view.common.dialog.CustomHintDialog;
import com.dzbook.view.common.dialog.base.CustomDialogBusiness;
import com.ishugui.BuildConfig;
import com.ishugui.R;

import java.io.File;

import hw.sdk.net.bean.store.TempletContant;

/**
 * WPS插件相关
 *
 * @author kongxp
 */
public class WpsModel {
    /**
     * 文件保存时是否发送广播。
     */
    public static final String SEND_SAVE_BROAD = "SendSaveBroad";
    /**
     * 关闭文件时是否请空临时文件。
     */
    public static final String CLEAR_BUFFER = "ClearBuffer";

    /**
     * 关闭文件时是否删除打开的文件。
     */
    public static final String CLEAR_FILE = "ClearFile";
    /**
     * 文件上次查看的进度。
     */
    public static final String VIEW_PROGRESS = "ViewProgress";
    /**
     * 是否自动跳转到上次查看的进度。
     */
    public static final String AUTO_JUMP = "AutoJump";
    /**
     * 文件保存路径。
     */
    public static final String SAVE_PATH = "SavePath";
    /**
     * 文件上次查看的视图的缩放。
     */
    public static final String VIEW_SCALE = "ViewScale";
    /**
     * 文件上次查看的视图的X坐标。
     */
    public static final String VIEW_SCALE_X = "ViewScrollX";
    /**
     * 文件上次查看的视图的Y坐标。
     */
    public static final String VIEW_SCALE_Y = "ViewScrollY";
    /**
     * 批注的作者。
     */
    public static final String USER_NAME = "UserName";
    /**
     * 监听home键并发广播
     */
    public static final String HOMEKEY_DOWN = "HomeKeyDown";
    /**
     * 监听back键并发广播
     */
    public static final String BACKKEY_DOWN = "BackKeyDown";
    /**
     * 以修订模式打开文档
     */
    public static final String ENTER_REVISE_MODE = "EnterReviseMode";
    /**
     * Wps生成的缓存文件外部是否可见
     */
    public static final String CACHE_FILE_INVISIBLE = "CacheFileInvisible";

    /**
     * 正常模式
     */
    public static final String OPENMODE_READ_ONLY = "ReadOnly";
    /**
     * 打开直接进入阅读器模式
     */
    public static final String OPENMODE_READ_MODE = "ReadMode";

    /**
     * 仅Word、TXT文档支持
     * 保存模式(打开文件,另存,关闭)
     */
    public static final String OPENMODE_SAVE_ONLY = "SaveOnly";
    /**
     * 英文版
     */
    public static final String CLASSNAME_ENGLISH = "cn.wps.moffice.documentmanager.PreStartActivity2";
    /**
     * 企业版
     */
    public static final String CLASSNAME_ENTERPRISE = "cn.wps.moffice.documentmanager.PreStartActivity2";

    /**
     * PackageName
     * 普通版
     */
    public static final String PACKAGENAME_NORMAL = "cn.wps.moffice_eng";
    /**
     * 英文版
     */
    public static final String PACKAGENAME_ENGLISH = "cn.wps.moffice_eng";
    /**
     * Reciver
     * 返回键广播
     */

    public static final String RECEIVER_ACTION_BACK = "com.kingsoft.writer.back.key.down";
    /**
     * Home键广播
     */
    public static final String RECEIVER_ACTION_HOME = "com.kingsoft.writer.home.key.down";
    /**
     * 保存广播
     */
    public static final String RECEIVER_ACTION_SAVE = "cn.wps.moffice.file.save";
    /**
     * 关闭文件广播
     */
    public static final String RECEIVER_ACTION_CLOSE = "cn.wps.moffice.file.close";

    /**
     * OpenMode
     * 只读模式
     */
    private static final String OPENMODE_NORMAL = "Normal";
    /**
     * 点击时间记录
     */
    private static long clickDelayTime = 0;
    /**
     * 打开文件的模式。
     */
    private static final String OPEN_MODE = "OpenMode";

    /**
     * 文件关闭时是否发送广播
     */
    private static final String SEND_CLOSE_BROAD = "SendCloseBroad";
    /**
     * 第三方的包名，关闭的广播会包含该项。
     */
    private static final String THIRD_PACKAGE = "ThirdPackage";

    /**
     * 关闭文件时是否删除使用记录。
     */
    private static final String CLEAR_TRACE = "ClearTrace";

    /**
     * ClassName
     * 普通版
     */
    private static final String CLASS_NAME_NORMAL = "cn.wps.moffice.documentmanager.PreStartActivity2";

    /**
     * 利用wps打开文件
     *
     * @param context   context
     * @param mBookInfo mBookInfo
     */
    @TargetApi(Build.VERSION_CODES.KITKAT)
    public static void openFile4wps(final Context context, BookInfo mBookInfo) {
        if (UtilApkCheck.isInstalledApp(context, WpsModel.PACKAGENAME_NORMAL)) {
            openFile(context, mBookInfo);
        } else {
            long current = System.currentTimeMillis();
            if (current - clickDelayTime > TempletContant.CLICK_DISTANSE) {
                showDlWpsDialog(context);
                clickDelayTime = current;
            }
        }
    }

    /**
     * 打开文件
     */
    private static void openFile(Context context, BookInfo mBookInfo) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        Bundle bundle = new Bundle();
        // 打开模式
        bundle.putString(WpsModel.OPEN_MODE, WpsModel.OPENMODE_NORMAL);
        // 关闭时是否发送广播
        bundle.putBoolean(WpsModel.SEND_CLOSE_BROAD, true);
        // 第三方应用的包名，用于对改应用合法性的验证
        bundle.putString(WpsModel.THIRD_PACKAGE, context.getPackageName());
        // 清除打开记录
        bundle.putBoolean(WpsModel.CLEAR_TRACE, true);
        //关闭后删除打开文件
        // bundle.putBoolean(CLEAR_FILE, true);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setAction(android.content.Intent.ACTION_VIEW);
        intent.setClassName(WpsModel.PACKAGENAME_NORMAL, WpsModel.CLASS_NAME_NORMAL);

        File file = new File(mBookInfo.bookid);
        if (!file.exists()) {
            System.out.println("文件为空或者不存在");
            return;
        }
        //判断是否是AndroidN以及更高的版本
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            Uri contentUri = FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID + ".fileProvider", file);
            intent.setData(contentUri);
        } else {
            intent.setData(Uri.fromFile(file));
        }
        intent.putExtras(bundle);
        try {
            context.startActivity(intent);
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * 安装Office插件dialog
     */
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private static void showDlWpsDialog(final Context context) {
        final CustomHintDialog dialog = new CustomHintDialog(context);
        dialog.setDesc(context.getString(R.string.jump_wps_dialog_title));
        dialog.setCheckListener(new CustomDialogBusiness.OnCheckListener() {
            @Override
            public void clickConfirm(Object object) {
                PersonPluginActivity.launch(context);
                dialog.dismiss();
            }

            @Override
            public void clickCancel() {

            }
        });
        dialog.show();
    }
}
