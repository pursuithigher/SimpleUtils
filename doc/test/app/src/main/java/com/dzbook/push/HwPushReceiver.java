package com.dzbook.push;

import android.app.NotificationManager;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;

import com.dzbook.event.EventBusUtils;
import com.dzbook.event.EventConstant;
import com.dzbook.event.EventMessage;
import com.dzbook.lib.utils.ALog;
import com.dzbook.utils.SpUtil;
import com.huawei.hms.support.api.push.PushReceiver;

import java.io.UnsupportedEncodingException;

/**
 * 华为Push 接受消息的Receiver
 *
 * @author winzows
 */

public class HwPushReceiver extends PushReceiver {

    /**
     * TAG
     */
    public static final String TAG = "HwPushReceiver： ";
    /**
     * ACTION_TOKEN
     */
    public static final String ACTION_TOKEN = "action.token";
    /**
     * ACTION_MSG
     */
    public static final String ACTION_MSG = "action.msg";
    /**
     * ACTION_EVENT
     */
    public static final String ACTION_EVENT = "action.event";
    /**
     * ACTION_STATE
     */
    public static final String ACTION_STATE = "action.state";
    /**
     * ACTION_STR
     */
    public static final String ACTION_STR = "action";


    /**
     * 调用getToken方法后，获取服务端返回的token结果，返回token以及belongId
     *
     * @param context
     * @param tokenIn
     * @param extras
     */
    @Override
    public void onToken(Context context, String tokenIn, Bundle extras) {
        String belongId = extras.getString("belongId");
        Bundle mBundle = new Bundle();
        mBundle.putString(ACTION_STR, ACTION_TOKEN);
        mBundle.putString(ACTION_TOKEN, tokenIn);
        callBack(mBundle);
        ALog.dWz(TAG + " onToken is " + tokenIn + " belongId is:" + belongId);
        if (!TextUtils.isEmpty(tokenIn)) {
            SpUtil.getinstance(context).setString(SpUtil.PUSH_CLIENTID, "");
        }
    }

    /**
     * 推送消息下来时会自动回调onPushMsg方法实现应用透传消息处理
     *
     * @param context
     * @param msg
     * @param bundle
     * @return
     */
    @Override
    public boolean onPushMsg(Context context, byte[] msg, Bundle bundle) {
        Bundle mBundle = new Bundle();
        mBundle.putString(ACTION_STR, ACTION_MSG);
        String content = "";
        try {
            content = new String(msg, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        if (!TextUtils.isEmpty(content)) {
            HwPushHandleHelper.getInstance().handleCmd(context, content);
        }
        mBundle.putString(ACTION_MSG, content);
        callBack(mBundle);
        ALog.dWz(TAG + "onPushMsg content " + content + " bundle=" + bundle);
        return false;
    }

    /**
     * 该方法会在设置标签、点击打开通知栏消息、点击通知栏上的按钮之后被调用。
     *
     * @param context
     * @param event
     * @param extras
     */
    @Override
    public void onEvent(Context context, Event event, Bundle extras) {
        Bundle mBundle = new Bundle();
        mBundle.putString(ACTION_STR, ACTION_EVENT);
        int notifyId = 0;
        if (Event.NOTIFICATION_OPENED.equals(event) || Event.NOTIFICATION_CLICK_BTN.equals(event)) {
            notifyId = extras.getInt(BOUND_KEY.pushNotifyId, 0);
            if (0 != notifyId) {
                NotificationManager manager = (NotificationManager) context
                        .getSystemService(Context.NOTIFICATION_SERVICE);
                if (manager != null) {
                    manager.cancel(notifyId);
                }
            }
        }
        String message = extras.getString(BOUND_KEY.pushMsgKey);
        mBundle.putString(ACTION_EVENT, message);
        callBack(mBundle);
        ALog.dWz(TAG + "Received event,notifyId:" + notifyId + " msg:" + message);
        super.onEvent(context, event, extras);
    }

    /**
     * 调用getPushState方法后，获取push连接状态的查询结果
     *
     * @param context
     * @param pushState
     */
    @Override
    public void onPushState(Context context, boolean pushState) {
        Bundle mBundle = new Bundle();
        mBundle.putString(ACTION_STR, ACTION_STATE);
        mBundle.putBoolean(ACTION_STATE, pushState);
        callBack(mBundle);
        ALog.dWz(TAG + "onPushState= " + pushState);
    }

    /**
     * 获取到消息 回调 发消息
     *
     * @param mBundle mBundle
     */
    private void callBack(Bundle mBundle) {
        EventBusUtils.sendStickyMessage(new EventMessage(EventConstant.CODE_PUSH, EventConstant.TYPE_PUSH, mBundle));
    }

}
