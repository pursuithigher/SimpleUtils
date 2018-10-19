package com.dzbook.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.dzbook.utils.WpsModel;

/**
 * WpsBroadCastReceiver
 */
public class WpsBroadCastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (!TextUtils.isEmpty(action)) {
            switch (action) {
                //返回键广播
                case WpsModel.RECEIVER_ACTION_BACK:
                    System.out.println(WpsModel.RECEIVER_ACTION_BACK);
                    break;
                //关闭文件时候的广播
                case WpsModel.RECEIVER_ACTION_CLOSE:
                    System.out.println(WpsModel.RECEIVER_ACTION_CLOSE);
                    break;
                //home键广播
                case WpsModel.RECEIVER_ACTION_HOME:
                    System.out.println(WpsModel.RECEIVER_ACTION_HOME);
                    break;
                //保存广播
                case WpsModel.RECEIVER_ACTION_SAVE:
                    System.out.println(WpsModel.RECEIVER_ACTION_SAVE);
                    break;
                default:
                    break;
            }
        }
    }

}
