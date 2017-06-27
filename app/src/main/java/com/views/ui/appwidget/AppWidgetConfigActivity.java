package com.views.ui.appwidget;

import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.RemoteViews;

import com.views.simpleutils.R;

/**
 * Created by qzzhu on 17-6-27.
 */

public class AppWidgetConfigActivity extends AppCompatActivity {

    private int appWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.appwidget_config);
        Bundle bundle = getIntent().getExtras();
        if(bundle != null)
        {
            appWidgetId = bundle.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID,AppWidgetManager.INVALID_APPWIDGET_ID);
        }
    }

    public void SetUp(View view){
        AppWidgetManager widgetManager = AppWidgetManager.getInstance(this);

        RemoteViews views = new RemoteViews(getPackageName(),R.layout.appwidget_home);
        views.setTextColor(R.id.appwidget_txt, Color.BLUE);

        widgetManager.updateAppWidget(appWidgetId,views);

        Intent result = new Intent();
        result.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,appWidgetId);
        setResult(RESULT_OK,result);
        finish();
    }
}
