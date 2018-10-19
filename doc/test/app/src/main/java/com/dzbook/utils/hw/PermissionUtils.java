package com.dzbook.utils.hw;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AppOpsManager;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;

import com.dzbook.AppConst;
import com.dzbook.lib.utils.ALog;
import com.dzbook.log.DzLog;
import com.dzbook.log.LogConstants;
import com.dzbook.model.ModelAction;
import com.dzbook.utils.SpUtil;
import com.dzbook.view.common.dialog.CustomHintDialog;
import com.dzbook.view.common.dialog.base.CustomDialogBusiness;
import com.ishugui.R;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * 应用权限工具类
 *
 * @author Winzows 2018/4/3
 */
public class PermissionUtils {

    /**
     * CODE_LOGO_REQUEST
     */
    public static final int CODE_LOGO_REQUEST = 1;
    private static final String CHECK_OP_NO_THROW = "checkOpNoThrow";
    private static final String OP_POST_NOTIFICATION = "OP_POST_NOTIFICATION";
    private static final String SP_CHECK_DIALOG = "checkNotifyDialog";
    private static final long THOUND_NUM = 1000L;
    private OnPermissionListener callback;
    private int mRequestCode;
    private CustomHintDialog dialog;

    /**
     * 加载请求权限数组
     *
     * @return String[]
     */
    public static String[] loadingPnList() {
        return new String[]{
                //写sd卡权限
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                //读取imei权限
                Manifest.permission.READ_PHONE_STATE};
    }

    /**
     * 请求权限处理
     *
     * @param activity    activity
     * @param requestCode 请求码
     * @param permissions 需要请求的权限
     * @param callbackC   结果回调
     */
    @TargetApi(Build.VERSION_CODES.M)
    public void requestPermissions(Activity activity, int requestCode, String[] permissions, OnPermissionListener callbackC) {
        if (isDialogShow()) {
            return;
        }
        this.callback = callbackC;
        this.mRequestCode = requestCode;

        List<String> deniedPermissions = getDeniedPermissions(activity, permissions);
        if (deniedPermissions.size() > 0) {
            activity.requestPermissions(deniedPermissions.toArray(new String[deniedPermissions.size()]), requestCode);
        }
    }

    /**
     * 请求权限结果，对应onRequestPermissionsResult()方法。
     *
     * @param requestCode  requestCode
     * @param permissions  permissions
     * @param grantResults grantResults
     */
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == mRequestCode && callback != null) {
            if (verifyPermissions(grantResults)) {
                callback.onPermissionGranted();
            } else {
                callback.onPermissionDenied();
            }
        }
    }

    /**
     * 显示提示对话框
     *
     * @param context context
     */
    public void showTipsDialog(final Activity context) {
        Resources resources = context.getResources();
        if (dialog == null) {
            dialog = new CustomHintDialog(context);
        }
        if (dialog.isShow()) {
            return;
        }
        dialog.setDesc(resources.getString(R.string.app_inner_name) + context.getString(R.string.str_permission_tips));
        dialog.setTitle(resources.getString(R.string.str_permission_title));
        dialog.setCancelTxt(resources.getString(R.string.cancel));
        dialog.setConfirmTxt(resources.getString(R.string.str_permission_goto_grant));
        dialog.setCheckListener(new CustomDialogBusiness.OnCheckListener() {
            @Override
            public void clickConfirm(Object object) {
                startAppSettings(context);
            }

            @Override
            public void clickCancel() {
                ModelAction.exitApp(context, false);
            }
        });

        dialog.show();
    }

    private boolean isDialogShow() {
        if (dialog != null) {
            return dialog.isShow();
        }
        return false;
    }

    /**
     * 启动当前应用设置页面
     */
    private static void startAppSettings(Context context) {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.setData(Uri.parse("package:" + context.getPackageName()));
        context.startActivity(intent);
    }

    /**
     * 验证权限是否都已经授权
     */
    private static boolean verifyPermissions(int[] grantResults) {
        // 如果请求被取消，则结果数组为空
        if (grantResults.length <= 0) {
            return false;
        }
        // 循环判断每个权限是否被拒绝
        for (int grantResult : grantResults) {
            if (grantResult != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    /**
     * 获取权限列表中所有需要授权的权限
     *
     * @param context     上下文
     * @param permissions 权限列表
     * @return
     */
    private List<String> getDeniedPermissions(Context context, String... permissions) {
        List<String> deniedPermissions = new ArrayList<>();
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_DENIED) {
                deniedPermissions.add(permission);
            }
        }
        return deniedPermissions;
    }

    /**
     * 检查所有的权限是否已经被授权
     *
     * @param permissions 权限列表
     * @return 是否获取权限
     */
    public boolean checkPermissions(String... permissions) {

        if (isOverMarshmallow()) {
            for (String permission : permissions) {
                if (ContextCompat.checkSelfPermission(AppConst.getApp(), permission) == PackageManager.PERMISSION_DENIED) {
                    ALog.dWz("checkPermissions denied " + permission);
                    return false;
                }
                ALog.dWz("checkPermissions grant " + permission);
            }
        }
        return true;
    }

    /**
     * 判断当前手机API版本是否 >= 6.0
     */
    private boolean isOverMarshmallow() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M;
    }

    /**
     * 权限授权状态回调接口
     */
    public interface OnPermissionListener {
        /**
         * 当授予权限时
         */
        void onPermissionGranted();

        /**
         * 当拒绝权限时
         */
        void onPermissionDenied();
    }


    /**
     * 检查有没有通知权限
     *
     * @param activity     activity
     * @param cnMsg        cnMsg
     * @param appOpenCount app打开次数
     * @param frequency    频率 间隔多少天
     */
    public void showNotifySettingIfNeed(final Activity activity, int appOpenCount, int frequency, String cnMsg) {
        if (activity == null || activity.isFinishing()) {
            return;
        }

        SpUtil spUtil = SpUtil.getinstance(activity);
        if (!spUtil.isInstallOneMonth(frequency) || spUtil.getAppCounter() <= appOpenCount) {
            return;
        }

        long thisTime = System.currentTimeMillis();

        if (thisTime - spUtil.getLong(SP_CHECK_DIALOG, 0) <= THOUND_NUM * 3600 * 24 * frequency) {
            return;
        }

        boolean allowed = checkNotifyPermission(activity);

        if (allowed) {
            return;
        }

        CustomHintDialog customHintDialog = new CustomHintDialog(activity, 1);
        customHintDialog.setTitle(TextUtils.isEmpty(cnMsg) ? activity.getString(R.string.dialog_notify_title) : cnMsg);
        customHintDialog.setDesc(activity.getString(R.string.dialog_notify_desc));
        customHintDialog.setCheckListener(new CustomDialogBusiness.OnCheckListener() {
            @Override
            public void clickConfirm(Object object) {
                startDefaultPage(activity);
                DzLog.getInstance().logClick(LogConstants.MODULE_SZTZ, LogConstants.ZONE_SZTZ_QSZ, "", null, "");
            }

            @Override
            public void clickCancel() {
                DzLog.getInstance().logClick(LogConstants.MODULE_SZTZ, LogConstants.ZONE_SZTZ_QX, "", null, "");
            }
        });
        customHintDialog.setConfirmTxt(activity.getResources().getString(R.string.str_setting));
        customHintDialog.show();

        spUtil.setLong(SP_CHECK_DIALOG, thisTime);

        DzLog.getInstance().logPv(LogConstants.PV_SZTZ, null, null);
    }


    /**
     * 检查 是否具有通知权限
     *
     * @param context context
     * @return boolean
     */
    public boolean checkNotifyPermission(Context context) {
        context = context.getApplicationContext();
        boolean allowed = false;
        NotificationManager notifyManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        try {
            if (notifyManager != null) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    allowed = notifyManager.areNotificationsEnabled();
                } else if (Build.VERSION.SDK_INT >= 16) {
                    AppOpsManager mAppOps = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
                    ApplicationInfo appInfo = context.getApplicationInfo();
                    String pkg = context.getPackageName();
                    int uid = appInfo.uid;
                    Class appOpsClass = Class.forName("android.app.AppOpsManager");
                    Method checkOpNoThrowMethod = appOpsClass.getMethod(CHECK_OP_NO_THROW, Integer.TYPE, Integer.TYPE, String.class);
                    Field opPostNotificationValue = appOpsClass.getDeclaredField(OP_POST_NOTIFICATION);

                    int value = (Integer) opPostNotificationValue.get(Integer.class);
                    allowed = (Integer) checkOpNoThrowMethod.invoke(mAppOps, value, uid, pkg) == AppOpsManager.MODE_ALLOWED;
                }
            }
        } catch (Throwable e) {
            allowed = true;
        }
        return allowed;
    }


    private void startDefaultPage(Activity activity) {
        try {
            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            Uri uri = Uri.fromParts("package", activity.getPackageName(), null);
            intent.setData(uri);
            activity.startActivity(intent);
        } catch (Exception e) {
            ALog.printExceptionWz(e);
        }
    }
}