package com.qzzhu.keepalive;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Messenger;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;

import com.qzzhu.simpleutils.R;

/**
 * Created by qzzhu on 16-9-6.
 */

public class AliveService extends Service {
    public final static int NODIFY = 1;
    public final static int CANCEL = 2;
    private final static int NODIFY_ID = 0x10000000;

    private Handler mhandler = new Handler(Looper.getMainLooper()){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            //show notify
            if(msg.what == NODIFY)
            {
                showNodify();
            }else if(msg.what == CANCEL)
            {
                cancelNodify();
            }

        }
    };

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return new Messenger(mhandler).getBinder();
    }

    private void showNodify(){
        NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        final Intent i = new Intent(this,MainActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        startForeground(NODIFY_ID,new NotificationCompat.Builder(getApplicationContext())
                .setContentTitle("title")
                .setContentText("content")
                .setSmallIcon(R.drawable.notify)
                .setContentIntent(PendingIntent.getActivity(this,0,i,0))
                .setAutoCancel(false)
                .setOngoing(true)
                .build());
    }

    @Override
    public void onDestroy() {
        cancelNodify();
        mhandler = null;
        super.onDestroy();
    }

    private void cancelNodify(){
        NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        nm.cancel(NODIFY_ID);
    }
}
