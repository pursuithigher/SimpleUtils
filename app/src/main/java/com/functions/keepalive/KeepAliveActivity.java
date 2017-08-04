package com.functions.keepalive;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;

import com.job.SchedulerService;

/**
 * Created by qzzhu on 16-9-6.
 *
 * <uses-permission Android:name="android.permission.SYSTEM_ALERT_WINDOW"
 */
public class KeepAliveActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sendViewOnHome();
    }
    private void sendViewOnHome() {
        Window window = getWindow();
        window.setGravity(Gravity.LEFT|Gravity.TOP);
        WindowManager.LayoutParams layoutparams = window.getAttributes();
        layoutparams.x = 0;
        layoutparams.y = 0;
        layoutparams.width =1;
        layoutparams.height = 1;

        //flag not disable back window
        layoutparams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        window.setAttributes(layoutparams);

        registerReceiver(receiver,new IntentFilter(Action));
    }

    private void schedulerJobs(){
        JobScheduler scheduler = (JobScheduler) getSystemService(Context.JOB_SCHEDULER_SERVICE);
        JobInfo.Builder builder = new JobInfo.Builder(2,
                new ComponentName(getPackageName(),SchedulerService.class.getName()))
                .setMinimumLatency(3000)
                ;
        //builder.setPeriodic(3000);//此处7.0手机至少15min才会周期执行一次，该方法小于15min无效

        int code = scheduler.schedule(builder.build());
        Log.i("schedule code",String.valueOf(code)); //判断状态
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(receiver);
        super.onDestroy();
    }

    public final static String Action = "keep_alive";
    final BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getAction().equals(Action))
                finishAliveActivity();
        }
    };

    public void finishAliveActivity(){
        finish();
    }
}

//<style name="keepAliveStyle" parent="@style/Theme.AppCompat.Light.NoActionBar">
//<item name="android:windowBackground">@android:color/transparent</item>
//<item name="android:windowIsTranslucent">true</item>
//<item name="android:windowIsFloating">true</item>
//<item name="android:backgroundDimEnabled">false</item>
//<item name="android:windowFrame">@null</item>
//</style>