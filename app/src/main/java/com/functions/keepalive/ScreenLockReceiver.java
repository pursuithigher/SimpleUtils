package com.functions.keepalive;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by qzzhu on 16-9-6.
 * 屏幕锁，在关闭屏幕的时候开启一个像素保活
 */
public class ScreenLockReceiver extends BroadcastReceiver {

    //    Intent.ACTION_SCREEN_OFF ：屏幕关闭
//    Intent.ACTION_USER_PRESENT： 用户解锁
    final static String ACTION = "android.intent.helixnt.keepalive";
    @Override
    public void onReceive(Context context, Intent intent) {
        String TAG = intent.getAction();
        if(TAG.equals(Intent.ACTION_SCREEN_OFF))
        {
            Intent i = new Intent(context,KeepAliveActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(i);
        }else if(TAG.equals(Intent.ACTION_USER_PRESENT) || TAG.equals(Intent.ACTION_SCREEN_ON)){
            context.sendBroadcast(new Intent(KeepAliveActivity.Action));
        }
    }
}
