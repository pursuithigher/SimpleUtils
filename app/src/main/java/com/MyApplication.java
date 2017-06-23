package com;

import android.app.Application;
import android.view.View;

import com.squareup.leakcanary.LeakCanary;

/**
 * Created by qzzhu on 17-6-23.
 */

public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        if(LeakCanary.isInAnalyzerProcess(this))
        {
            return ;
        }
        LeakCanary.install(this);
    }
}
