package com.functions.keepalive;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.views.simpleutils.R;

/**
 * Created by qzzhu on 16-9-7.
 */
public class MainActivity extends AppCompatActivity {

    private final ServiceConnection sc =new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            Messenger msg = new Messenger(iBinder);
            Message message = new Message();
            message.what = AliveService.NODIFY;
            try {
                msg.send(message);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
        }
    };

    private BroadcastReceiver receiver = new ScreenLockReceiver();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        registerReceiver(receiver,new IntentFilter(ScreenLockReceiver.ACTION));
        keepAlive();
    }

    @Override
    protected void onDestroy() {
        killService();
        unregisterReceiver(receiver);
        super.onDestroy();
    }

    /**
     * this func is to take a foreground service
     */
    private void keepAlive(){
        bindService(new Intent(this,AliveService.class),sc,BIND_AUTO_CREATE);
    }

    /**
     * this func is to destory foreground service
     */
    private void killService(){
        unbindService(sc);
    }
}
