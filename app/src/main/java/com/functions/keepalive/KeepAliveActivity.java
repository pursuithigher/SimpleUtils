package com.functions.keepalive;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;

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