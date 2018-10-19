package com.dzbook.utils;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Point;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.dzbook.AppInfoUtils;
import com.dzbook.activity.CenterDetailActivity;
import com.dzbook.lib.utils.ALog;
import com.dzbook.lib.utils.CompatUtils;
import com.dzbook.net.hw.RequestCall;
import com.ishugui.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * 测试dialog
 *
 * @author zhenglk on 15/1/21.
 */
public class UtilTest {

    /**
     * 调试IP信息。
     *
     * @param context context
     */
    public void showSetIp(final Activity context) {
        final long limitTime = RequestCall.testUrlGetLimit();
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        final AlertDialog alertDialog = builder.create();

        if (limitTime > 0) {
            DialogInterface.OnClickListener l = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (DialogInterface.BUTTON_POSITIVE == which) {
                        RequestCall.testUrlSet(context, "", false);
                    }

                    dialog.dismiss();
                }
            };
            builder.setTitle("转为现网或取消:");

            String info = "当前网段：测试网\n" + "URL：" + RequestCall.getUrlBasic() + "\n" + "剩余时间：" + limitTime + "秒";

            builder.setMessage(info);
            builder.setPositiveButton("转为现网", l);
            builder.setNegativeButton("返回", l);
            builder.create().show();
        } else {
            View inflate = View.inflate(context, R.layout.show_setip, null);
            TextView dialogTitle = inflate.findViewById(R.id.dialog_title);
            LinearLayout llItem = inflate.findViewById(R.id.ll_item);
            final EditText etIp = inflate.findViewById(R.id.et_ip);
            final EditText etPort = inflate.findViewById(R.id.et_port);
            Button btnCommit = inflate.findViewById(R.id.btn_commit);
            Button dialogCancel = inflate.findViewById(R.id.dialog_cancel);
            //
            for (final String url : RequestCall.getTestUrlPool()) {
                TextView textView = new TextView(context);
                textView.setTextColor(Color.WHITE);
                textView.setPadding(30, 30, 30, 30);
                textView.setText(url);
                textView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        RequestCall.testUrlSet(context, url, true);
                        //清空userId,重新注册
                        SpUtil.getinstance(context).setUserID("");
                        SpUtil.getinstance(context).setAccountLoginStatus(false);
                        alertDialog.dismiss();
                    }
                });
                llItem.addView(textView);
            }
            etIp.setText("http://192.168.0.90");
            etPort.setText("3080");
            dialogTitle.setText(RequestCall.getUrlBasic());
            dialogTitle.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    CenterDetailActivity.show(context, RequestCall.getTestJsUrl());
                    return true;
                }
            });
            dialogCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    alertDialog.dismiss();
                }
            });
            btnCommit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String trim = etIp.getText().toString().trim();
                    if (TextUtils.isEmpty(trim)) {
                        Toast.makeText(context, "不能为空", Toast.LENGTH_SHORT).show();
                    } else {
                        RequestCall.testUrlSet(context, trim + ":" + etPort.getText().toString().trim(), true);
                        //清空userId,重新注册
                        SpUtil.getinstance(context).setUserID("");
                        SpUtil.getinstance(context).setAccountLoginStatus(false);
                        alertDialog.dismiss();
                    }
                }
            });
            alertDialog.setView(inflate);
            alertDialog.show();

            WindowManager.LayoutParams lp = alertDialog.getWindow().getAttributes();
            //设置宽度

            lp.width = ActionBar.LayoutParams.MATCH_PARENT;
            alertDialog.getWindow().setAttributes(lp);
        }
    }

    /**
     * 显示软件信息。
     *
     * @param context context
     */
    public void showSoftInfo(final Context context) {
        SpUtil sp = SpUtil.getinstance(context);
        DisplayMetrics metrics = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(metrics);

        StringBuilder info = new StringBuilder();
        info.append("USER:").append(sp.getUserID());
        info.append("\nSUM:").append(sp.getUserRemainPrice()).append(sp.getUserRemainUnit());
        info.append("\nCHANNEL:").append(DeviceInfoUtils.getInstanse().getChannel());
        info.append("\n┏━━app info━━━");
        info.append("\nPACK:").append(context.getPackageName());
        info.append("\nVER:").append(PackageControlUtils.getAppVersionName()).append('[').append(PackageControlUtils.getAppVersionCode()).append(']');
        info.append("\nBUILD:").append(readBuildTime());
        info.append("\nUID:").append(getSharedUserId(context));
        info.append("\nSINGLE_SCHEME:").append(context.getString(R.string.single_scheme));

        info.append("\n┏━━phone info━━━\n");
        info.append("[").append(DeviceInfoUtils.getInstanse().getBrand()).append("]").append(DeviceInfoUtils.getInstanse().getModel()).append(",").append(DeviceInfoUtils.getInstanse().getOsVersion()).append("\n");

        Point point = CompatUtils.getSize(context);
        if (null != point) {
            info.append(point.x).append("x").append(point.y).append(",").append(metrics.density).append(", ").append(metrics.densityDpi).append(", ").append((int) (point.x / metrics.density)).append("\n");
        }
        info.append("ABI:").append(DeviceInfoUtils.getInstanse().getCpuAbi()).append("\n");
        info.append("APN:").append(NetworkUtils.getInstance().getAPNType()).append("\n");

        info.append("┏━━tinker━━━\n");
        info.append("in ver:").append(AppInfoUtils.getGitTag()).append("_").append(AppInfoUtils.getGitCode()).append("\n");
        info.append("cur ver:").append(PackageControlUtils.gitTag()).append("_").append(PackageControlUtils.gitCode()).append("\n");
        info.append("┏━━Git info━━━\n");
        info.append(PackageControlUtils.gitInfo()).append("\n");

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        String infoStr = info.toString();
        builder.setMessage(infoStr);
        ALog.iLk(infoStr);
        builder.setTitle("INFO:");
        builder.setNegativeButton("返回", new android.content.DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create().show();
    }

    /**
     * 读取编译时间
     *
     * @return
     */
    private static String readBuildTime() {
        try {
            Date d = new Date(PackageControlUtils.appBuildTime());
            SimpleDateFormat s = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);
            return s.format(d);
        } catch (Exception ignore) {
        }
        return "";
    }

    /**
     * 工程暗码
     *
     * @param activity activity
     * @param text     text
     * @return boolean
     */
    public static boolean applyFactoryCode(Activity activity, String text) {
        if (TextUtils.isEmpty(text)) {
            return false;
        }
        String lowStr = text.trim().toLowerCase();
        if (lowStr.startsWith("dz") && lowStr.length() > 2) {
            lowStr = lowStr.substring(2);
            if ("ip".equals(lowStr)) {
                new UtilTest().showSetIp(activity);
                return true;
            } else if ("info".equals(lowStr)) {
                new UtilTest().showSoftInfo(activity);
                return true;
            }

        }
        return false;
    }

    private String getSharedUserId(Context context) {
        try {
            String packName = context.getPackageName();
            PackageManager pm = context.getPackageManager();
            PackageInfo pi = pm.getPackageInfo(packName, 0);
            return pi.sharedUserId;
        } catch (PackageManager.NameNotFoundException e) {
            ALog.printStackTrace(e);
        }
        return "_unknown_";
    }

}
