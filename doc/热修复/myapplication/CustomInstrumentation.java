package com.example.myapplication;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.Intent;

public class CustomInstrumentation extends Instrumentation{
    private Instrumentation base;

    public CustomInstrumentation(Instrumentation base) {
        this.base = base;
    }

    @Override
    public Activity newActivity(ClassLoader cl, String className, Intent intent) throws InstantiationException, IllegalAccessException, ClassNotFoundException {
//        if("1".equals(intent.getStringExtra("from"))){
//            intent.putExtra("from","3");
//            className = ActivityC.class.getCanonicalName();
//        }
//        else if(MainActivity.class.getCanonicalName().equals(className)){
//            className = ActivityC.class.getCanonicalName();
//        }
        return base.newActivity(cl, className, intent);
    }


}
