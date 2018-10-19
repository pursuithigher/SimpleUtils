package com.dzbook.push;

import android.annotation.TargetApi;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.os.Build;
import android.provider.Settings;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;

import com.dzbook.AppConst;
import com.dzbook.activity.CenterDetailActivity;
import com.dzbook.activity.detail.BookDetailActivity;
import com.dzbook.activity.reader.ReaderActivity;
import com.dzbook.activity.reader.ReaderUtils;
import com.dzbook.database.bean.BookInfo;
import com.dzbook.database.bean.CatalogInfo;
import com.dzbook.log.DzLog;
import com.dzbook.log.LogConstants;
import com.dzbook.r.c.AkDocInfo;
import com.dzbook.utils.DBUtils;
import com.dzbook.utils.SpUtil;
import com.dzbook.utils.ThirdPartyLog;
import com.dzbook.web.ActionEngine;
import com.ishugui.R;
import com.iss.view.common.ToastAlone;

import java.util.HashMap;
import java.util.Random;

/**
 * push推送 弹通知栏的
 *
 * @author winzows 2018/4/16
 */
public class HwPushNotificationUtils {
    private static final String NOTIFY_ID_ACTIVITY = "activity";
    private static final String NOTIFY_ID_SUBSCRIBE = "subscribe";
    private static final String CHANNEL_NAME_AC = "活动通知";
    private static final String CHANNEL_NAME_SUBS = "书籍更新";
    private static volatile HwPushNotificationUtils instance;

    /**
     * 获取单例
     *
     * @return HwPushNotificationUtils
     */
    public static HwPushNotificationUtils getInstance() {
        if (instance == null) {
            synchronized (HwPushNotificationUtils.class) {
                if (instance == null) {
                    instance = new HwPushNotificationUtils();
                }
            }
        }
        return instance;
    }

    void handleNotify(Context context, String msg) {
        if (TextUtils.isEmpty(msg)) {
            return;
        }
        String[] msgs = msg.split("\\$");
        if (msgs.length < 3) {
            return;
        }
        BeanCloudyNotify cloudyNotification = new BeanCloudyNotify(msgs);
        addNotification(context, cloudyNotification);
    }

    /**
     * 添加顶部通知
     *
     * @param context            context
     * @param cloudyNotification cloudyNotification
     */
    public void addNotification(final Context context, BeanCloudyNotify cloudyNotification) {
        int notifyType = -1;
        // 实例化Intent
        String notiTitle = "";
        Intent it = null;
        String lowType = cloudyNotification.getType().toLowerCase();
//        ALog.eDongdz("推送bean：" + cloudyNotification.toString());
        //A$452439882$问一声:近来安好?$校花和骚年,折腰共缠绵
        if ("a".equals(lowType)) {
            // 新书推荐消息
            it = new Intent(context, BookDetailActivity.class);
            it.putExtra("bookId", cloudyNotification.getIdentity());
            notiTitle = getNotifyTitle(cloudyNotification, "新书推荐消息");

            notifyType = 2;
        } else if ("b".equals(lowType)) {
            //B$www.chaohuida.com/huodong/170403/index.html$40元呐，手慢就没了$你收到一个支付宝红包
            // 代表营销信息(启动活动中心详细页面)
            it = new Intent(context, CenterDetailActivity.class);
            it.putExtra("url", cloudyNotification.getIdentity());
            it.putExtra("web", ActionEngine.GTTC);
            notiTitle = getNotifyTitle(cloudyNotification, context.getString(R.string.string_active_center));
            it.putExtra("notiTitle", notiTitle);
            notifyType = 1;

        } else if ("f".equals(lowType)) {
            //3.4版本新添功能p=23开始支持，追更书籍直接进入阅读器
            //f$11000031719$本书已更新X章（取更新数量）→点击阅读$《书名》$messageid
            final String bookId = cloudyNotification.getIdentity();
            if (TextUtils.isEmpty(bookId)) {
                return;
            }
            it = prepareBookIntent(context, cloudyNotification, bookId);

            dzLogNewChapter(context, cloudyNotification);

            notiTitle = getNotifyTitle(cloudyNotification, "书籍章节更新消息");
            notifyType = 2;
        } else {
            return;
        }
        it.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        it.putExtra("noti", cloudyNotification);


        tryShowNotify(context, cloudyNotification, notifyType, notiTitle, it);
    }

    private String getNotifyTitle(BeanCloudyNotify cloudyNotication, String def) {
        String notiTitle;
        if (!TextUtils.isEmpty(cloudyNotication.getNotiTitle())) {
            notiTitle = cloudyNotication.getNotiTitle();
        } else {
            notiTitle = def;
        }
        return notiTitle;
    }

    private void dzLogNewChapter(Context context, BeanCloudyNotify cloudyNotication) {
        HashMap<String, String> map = new HashMap<>();
        map.put("messageid", cloudyNotication.getMessageId());
        map.put("bookid", cloudyNotication.getIdentity());
        map.put("issystempush", cloudyNotication.getIsSystemPush());
        map.put("bno", cloudyNotication.getBno());
        map.put("isopenapp", AppConst.isIsMainActivityActive() ? "1" : "2");
        map.put(LogConstants.KEY_GT_CID, SpUtil.getinstance(context).getString(SpUtil.PUSH_CLIENTID, ""));
        DzLog.getInstance().logEvent(LogConstants.EVENT_ZGTSDD, map, "");
    }

    private Intent prepareBookIntent(Context context, BeanCloudyNotify cloudyNotication, String bookId) {
        Intent it;
        BookInfo bookInfo = DBUtils.findShelfBookByBookId(context, bookId);
        //在书架
        if (bookInfo != null) {
            CatalogInfo catalogInfo = DBUtils.getCatalog(context, bookId, bookInfo.currentCatalogId);
            if (catalogInfo != null && catalogInfo.isAvailable()) {
                it = new Intent(context, ReaderActivity.class);

                AkDocInfo docInfo = ReaderUtils.generateDoc(context, bookInfo, catalogInfo);
                docInfo.currentPos = catalogInfo.currentPos;
                it.putExtra("docInfo", docInfo);
                it.putExtra("messageid", cloudyNotication.getMessageId());
                it.putExtra("issystempush", cloudyNotication.getIsSystemPush());
                it.putExtra("pushType", "zhuigeng");
                it.putExtra("bno", cloudyNotication.getBno());
            } else {
                //书籍当前章节不可用
                it = new Intent(context, BookDetailActivity.class);
                it.putExtra("bookId", bookId);
                it.putExtra("pushType", "zhuigeng");
                it.putExtra("messageid", cloudyNotication.getMessageId());
                it.putExtra("issystempush", cloudyNotication.getIsSystemPush());
                it.putExtra("bno", cloudyNotication.getBno());
            }
        } else {
            //不在书架
            it = new Intent(context, BookDetailActivity.class);
            it.putExtra("bookId", bookId);
            it.putExtra("pushType", "zhuigeng");
            it.putExtra("messageid", cloudyNotication.getMessageId());
            it.putExtra("issystempush", cloudyNotication.getIsSystemPush());
            it.putExtra("bno", cloudyNotication.getBno());
        }
        return it;
    }

    /**
     * 真正弹出的实现 适配了安卓8.0
     *
     * @param context          上下文
     * @param cloudyNotication bean
     * @param notifyType       类型
     * @param notiTitle        title
     * @param it               intent
     */
    private void tryShowNotify(Context context, BeanCloudyNotify cloudyNotication, int notifyType, String notiTitle, Intent it) {
        NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        NotificationCompat.Builder builder = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && nm != null) {
            if (notifyType == 1) {
                NotificationChannel channel = nm.getNotificationChannel(NOTIFY_ID_ACTIVITY);
                if (channel != null && (channel.getImportance() == NotificationManager.IMPORTANCE_NONE)) {
                    toSystemNotifySetting(context, channel);
                } else {
                    builder = new NotificationCompat.Builder(context, NOTIFY_ID_ACTIVITY);
                }
            } else if (notifyType == 2) {
                NotificationChannel channel = nm.getNotificationChannel(NOTIFY_ID_SUBSCRIBE);
                if (channel != null && (channel.getImportance() == NotificationManager.IMPORTANCE_NONE)) {
                    toSystemNotifySetting(context, channel);
                } else {
                    builder = new NotificationCompat.Builder(context, NOTIFY_ID_SUBSCRIBE);
                }
            }
        } else {
            builder = new NotificationCompat.Builder(context);
        }

        if (builder != null && nm != null) {
            int requestCode = 0;
            try {
                long currentTimeMillis = System.currentTimeMillis();
                requestCode = (int) currentTimeMillis;
                requestCode += new Random().nextInt(100000);
            } catch (Exception e) {
                e.printStackTrace();
            }
            PendingIntent pi = PendingIntent.getActivity(context, requestCode, it, PendingIntent.FLAG_UPDATE_CURRENT);

            builder.setContentTitle(notiTitle);
            builder.setContentText(cloudyNotication.getContent());
            builder.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));
            builder.setSmallIcon(R.drawable.push);
            builder.setTicker(cloudyNotication.getContent());
            builder.setWhen(System.currentTimeMillis());
            builder.setAutoCancel(true);
            builder.setContentIntent(pi);
            //在友盟设置展示数
            ThirdPartyLog.onEvent(context, ThirdPartyLog.NOTIFICATION_CLICK_NUMB);
            // 发出通知
            nm.notify(new Random().nextInt(100000), builder.build());
        }
    }

    /**
     * 对安卓8.0 在应用刚启动时 创建两个通知渠道
     *
     * @param context context
     */
    @TargetApi(Build.VERSION_CODES.O)
    public void createNotifyChannel(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            if (nm != null) {
                NotificationChannel channel = new NotificationChannel(NOTIFY_ID_ACTIVITY, CHANNEL_NAME_AC, NotificationManager.IMPORTANCE_DEFAULT);
                channel.setShowBadge(true);
                nm.createNotificationChannel(channel);

                channel = new NotificationChannel(NOTIFY_ID_SUBSCRIBE, CHANNEL_NAME_SUBS, NotificationManager.IMPORTANCE_HIGH);
                channel.setShowBadge(true);
                nm.createNotificationChannel(channel);
            }
        }
    }

    /**
     * 打开系统通知设置页面
     *
     * @param context 上下文
     * @param channel 渠道号
     */
    private void toSystemNotifySetting(Context context, NotificationChannel channel) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Intent intent = new Intent(Settings.ACTION_CHANNEL_NOTIFICATION_SETTINGS);
            intent.putExtra(Settings.EXTRA_APP_PACKAGE, context.getPackageName());
            intent.putExtra(Settings.EXTRA_CHANNEL_ID, channel.getId());
            context.startActivity(intent);
            ToastAlone.showLong(R.string.toast_push_tips);
        }
    }
}
