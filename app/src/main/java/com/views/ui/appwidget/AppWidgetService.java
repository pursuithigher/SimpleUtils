package com.views.ui.appwidget;

import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.RemoteViews;

import com.views.simpleutils.R;

import java.util.Date;

/**
 * Created by qzzhu on 17-6-27.
 */

public class AppWidgetService extends Service {

    private final static long PERIOD = 30000;

    private HandlerThread mThread ;
    private Handler mHandler ;
    private int flag = -1;

    @Override
    public void onCreate() {
        super.onCreate();
        mThread = new HandlerThread("mAppWidgetService");
        mThread.start();
        mHandler = new Handler(mThread.getLooper());

        mHandler.postDelayed(runnable,PERIOD);
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
        quit();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        quit();

    }

    private void quit(){
        if(mThread != null) {
            mHandler = null;
            mThread.quit();
            mThread = null;
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            Log.i("broadCast","times = "+flag);
            if(flag >= 10)
                return ;

            AppWidgetManager manager = AppWidgetManager.getInstance(AppWidgetService.this);

            RemoteViews views = new RemoteViews(getPackageName(), R.layout.appwidget_home);
            views.setTextViewText(R.id.appwidget_txt,new Date(System.currentTimeMillis()).toLocaleString());

            manager.updateAppWidget(new ComponentName(AppWidgetService.this,MyAppWidgetReceiver.class),views);
            flag ++ ;
            mHandler.postDelayed(this,PERIOD);
        }
    };
}
