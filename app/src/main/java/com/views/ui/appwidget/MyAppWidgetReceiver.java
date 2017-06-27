package com.views.ui.appwidget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.RemoteViews;

import com.animtions.ObjectAnimActivity;
import com.views.simpleutils.R;

import java.util.Date;

/**
 * Created by qzzhu on 17-6-27.
 */

public class MyAppWidgetReceiver extends AppWidgetProvider {
    private final static String ACTION = "android.appwidget.action.APPWIDGET_UPDATE";

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        super.onUpdate(context, appWidgetManager, appWidgetIds);
        final int N = appWidgetIds.length;
        Log.i("update","appwidget,N = "+N);

        context.startService(new Intent(context,AppWidgetService.class));
//        for(int i = 0;i < N;i++){
//            int appWidgetId = appWidgetIds[i];
//
//            Intent intent = new Intent(context,ObjectAnimActivity.class);
//            PendingIntent pendingIntent = PendingIntent.getActivity(context,0,intent,0);
//
//            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.appwidget_home);
//            views.setOnClickPendingIntent(R.id.appwidget_launcher,pendingIntent);
//            views.setTextViewText(R.id.appwidget_txt,new Date(System.currentTimeMillis()).toLocaleString());
//
//            appWidgetManager.updateAppWidget(appWidgetId,views);
//        }
    }

    @Override
    public void onAppWidgetOptionsChanged(Context context, AppWidgetManager appWidgetManager, int appWidgetId, Bundle newOptions) {
        super.onAppWidgetOptionsChanged(context, appWidgetManager, appWidgetId, newOptions);
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        super.onDeleted(context, appWidgetIds);
    }

    @Override
    public void onDisabled(Context context) {
        super.onDisabled(context);
    }

}
