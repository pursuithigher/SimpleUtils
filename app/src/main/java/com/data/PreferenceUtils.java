package com.data;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

/**
 * Created by qzzhu on 17-5-9.
 */

public class PreferenceUtils {

    public static void putString(Context context,String fileName,String key,String value){
        if(TextUtils.isEmpty(fileName))
            fileName = context.getPackageName();
        SharedPreferences.Editor editor = context.getSharedPreferences(fileName, Context.MODE_PRIVATE).edit();
        editor.putString(key,value);
        editor.apply();
    }

    public static void putInt(Context context,String fileName,String key,int value){
        if(TextUtils.isEmpty(fileName))
            fileName = context.getPackageName();
        SharedPreferences.Editor editor = context.getSharedPreferences(fileName, Context.MODE_PRIVATE).edit();
        editor.putInt(key,value);
        editor.apply();
    }
}
