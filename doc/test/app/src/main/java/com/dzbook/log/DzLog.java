package com.dzbook.log;

import android.app.Activity;
import android.text.TextUtils;
import android.util.Log;

import com.dzbook.AppConst;
import com.dzbook.fragment.main.BaseFragment;
import com.dzbook.lib.net.DzSchedulers;
import com.dzbook.lib.net.OkhttpUtils;
import com.dzbook.lib.utils.ALog;
import com.dzbook.lib.utils.JsonUtils;
import com.dzbook.r.c.DzThread;
import com.dzbook.utils.DeviceInfoUtils;
import com.dzbook.utils.PackageControlUtils;
import com.dzbook.utils.QueueWorker;
import com.dzbook.utils.SpUtil;
import com.iss.app.BaseActivity;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.UUID;
import java.util.concurrent.PriorityBlockingQueue;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


/**
 * DzLog
 *
 * @author wxliao on 17/7/3.
 */

public class DzLog {
    private static final String LOG_URL = "https://log.kuaikandushu.cn/hwlog.php";
    private static volatile DzLog instance;
    private final String mTag = "DzLog";
    private Worker worker;
    private OkHttpClient mOkHttpClient;
    private DzThread mThread;
    private String lastPvName;
    private long leaveTime;
    private boolean isLeave;
    private String sessionId;
    private final long maxLeaveTime = 6 * 60 * 1000;


    private DzLog() {
        worker = new Worker();

        mOkHttpClient = OkhttpUtils.generateClient();

        mThread = DzThread.getByTag(mTag);
    }


    /**
     * 获取DzLog实例
     *
     * @return 实例
     */
    public static DzLog getInstance() {
        if (instance == null) {
            synchronized (DzLog.class) {
                if (instance == null) {
                    instance = new DzLog();
                }
            }
        }
        return instance;
    }

    /**
     * 生成Session
     *
     * @return String
     */
    public static String generateSession() {
        return UUID.randomUUID().toString();
    }

    /**
     * 生成Trackid
     *
     * @return string
     */
    public static String generateTrackd() {
        return UUID.randomUUID().toString();
    }


    /**
     * 页面显示在前台
     *
     * @param activity   activity
     * @param isCustomPv isCustomPv
     */
    public void onPageStart(Activity activity, boolean isCustomPv) {

        if (isLeave && (System.currentTimeMillis() > (leaveTime + maxLeaveTime))) {
            ALog.e(mTag, "应用在后台时间超过时限，重记启动");
            logLaunch(LogConstants.LAUNCH_BACKGROUND);
        }

        isLeave = false;

        if (!isCustomPv && activity instanceof BaseActivity) {
            logPv(((BaseActivity) activity).getName());
        }
        QueueWorker.getInstance().add(activity);
    }

    /**
     * 页面显示在前台
     *
     * @param fragment   fragment
     * @param isCustomPv isCustomPv
     */
    public void onPageStart(BaseFragment fragment, boolean isCustomPv) {
        if (!isCustomPv) {
            logPv(fragment.getName());
        }
        QueueWorker.getInstance().add(fragment);
    }


    /**
     * 离开页面
     *
     * @param activity   activity
     * @param isCustomPv isCustomPv
     */
    public void onPageEnd(Activity activity, boolean isCustomPv) {
        //这里其实不需要判断是否在后台，离开页面之后必然要新开页面，
        // 否则如果中间的页面间隔时间超过设定的阈值，就可以认为是新启动
        isLeave = true;
        leaveTime = System.currentTimeMillis();

    }

    /**
     * 离开页面
     *
     * @param fragment   fragment
     * @param isCustomPv isCustomPv
     */
    public void onPageEnd(BaseFragment fragment, boolean isCustomPv) {

    }

    /**
     * map传递value为Object类型
     *
     * @param map
     * @return
     */
    private HashMap readyMap(HashMap<String, String> map) {
        if (null == map) {
            map = new HashMap<>();
        }
        try {
            SpUtil sp = SpUtil.getinstance(AppConst.getApp());
            // 安装小时数，卸载清零
            if (!map.containsKey("ih")) {
                map.put("ih", String.valueOf(sp.getInstallHour()));
            }
            // 版本
            if (!map.containsKey("p")) {
                map.put("p", PackageControlUtils.getAppVersionCode());
            }
            // 补充 UncaughtExceptionHandler
            Thread.UncaughtExceptionHandler expHandler = Thread.getDefaultUncaughtExceptionHandler();
            if (null != expHandler) {
                String handlerName = expHandler.getClass().getName();
                map.put("hdl", handlerName);
            }
        } catch (Exception e) {
            ALog.printStackTrace(e);
        }
        return map;
    }

    /**
     * map传递value为Object类型
     *
     * @param map map
     * @return map
     */
    private HashMap readyMapObj(HashMap<String, Object> map) {
        if (null == map) {
            map = new HashMap<>();
        }
        try {
            SpUtil sp = SpUtil.getinstance(AppConst.getApp());
            // 安装小时数，卸载清零
            if (!map.containsKey("ih")) {
                map.put("ih", String.valueOf(sp.getInstallHour()));
            }
            // 省份
            if (!map.containsKey("prov")) {
                map.put("prov", sp.getClientProvince());
            }
            // 城市
            if (!map.containsKey("city")) {
                map.put("city", sp.getClientCity());
            }
            // 用户偏好
            if (!map.containsKey("ph")) {
                map.put("ph", sp.getPersonReadPref() + "");
            }
            // 版本
            if (!map.containsKey("p")) {
                map.put("p", PackageControlUtils.getAppVersionCode());
            }
            // 补充 UncaughtExceptionHandler
            Thread.UncaughtExceptionHandler expHandler = Thread.getDefaultUncaughtExceptionHandler();
            if (null != expHandler) {
                String handlerName = expHandler.getClass().getName();
                map.put("hdl", handlerName);
            }
        } catch (Exception e) {
            ALog.printStackTrace(e);
        }
        return map;
    }

    /**
     * 预先准备一个带有特殊标志备用的sessionId，在启动打点的位置会覆盖这个sessionId。
     *
     * @param sessionIdHead 特殊标识，会添加在常规的sessionId前面。
     */
    public void readySession(String sessionIdHead) {
        sessionId = sessionIdHead + generateSession();
    }

    /**
     * 启动日志
     *
     * @param sm sm
     */
    public void logLaunch(@LogConstants.LaunchSm final int sm) {
        Log.d(mTag, "sm=" + sm);
        mThread.postOnWorker(new Runnable() {
            @Override
            public void run() {
                sessionId = generateSession();
                HashMap<String, Object> params = getCommonParams();
                params.put("tag", LogConstants.TAG_LAUNCH);
                params.put("sm", sm); // 打开方式

                params.put("map", readyMap(null)); // 扩展字段

                enqueue(params, LogConstants.TAG_LAUNCH);
            }
        });
    }

    /**
     * PV日志，普通页面的PV打点，通过Base类自动触发
     *
     * @param name
     */
    private void logPv(String name) {
        logPv(name, null, null);
    }


    /**
     * PV日志，有附加参数需求的PV打点，调用此方法
     *
     * @param activity activity
     * @param map      包含附加参数的map
     * @param trackId  如果页面需要跟踪，调用@see #generateTrackId()方法生成一个trackId,用于日志Track。
     */
    public void logPv(BaseActivity activity, HashMap<String, String> map, String trackId) {
        logPv(activity.getName(), map, trackId);
    }

    /**
     * PV日志，有附加参数需求的PV打点，调用此方法
     *
     * @param fragment fragment
     * @param map      包含附加参数的map
     * @param trackId  如果页面需要跟踪，调用@see #generateTrackId()方法生成一个trackId,用于日志Track。
     */
    public void logPv(BaseFragment fragment, HashMap<String, String> map, String trackId) {
        logPv(fragment.getName(), map, trackId);
    }

    /**
     * PV日志，有附加参数需求的PV打点，调用此方法（如果非特殊需求，请调用logPv(Activity ..) 或者 logPv(Fragment ..)）
     *
     * @param name    name
     * @param map     map
     * @param trackId trackId
     */
    public void logPv(final String name, final HashMap<String, String> map, final String trackId) {
        mThread.postOnWorker(new Runnable() {
            @Override
            public void run() {
                String prev = lastPvName;
                lastPvName = name;

                ALog.e(mTag, "s-logPv ptype:" + name + " map:" + map + " trackId:" + trackId);

                HashMap<String, Object> params = getCommonParams();
                params.put("tag", LogConstants.TAG_PV);

                //上一个页面的关联
                params.put("prev", getEmptyString(prev));

                params.put("ptype", name);

                params.put("map", readyMap(map));

                params.put("trackid", getEmptyString(trackId));

                enqueue(params, LogConstants.TAG_PV);
            }
        });

    }


    /**
     * Click日志
     *
     * @param module  module
     * @param zone    zone
     * @param adid    adid
     * @param map     map
     * @param trackId trackId
     */
    public void logClick(@LogConstants.Module final String module, @LogConstants.Zone final String zone, final String adid, final HashMap<String, String> map, final String trackId) {
        mThread.postOnWorker(new Runnable() {
            @Override
            public void run() {
                ALog.e(mTag, "s-logClick module:" + module + " zone:" + zone + " adid:" + adid + " map:" + map + " trackid:" + trackId);
                HashMap<String, Object> params = getCommonParams();
                params.put("tag", LogConstants.TAG_CLICK);
                params.put("module", module);
                params.put("zone", zone);
                params.put("adid", getEmptyString(adid));

                params.put("map", readyMap(map));

                params.put("trackid", getEmptyString(trackId));

                enqueue(params, LogConstants.TAG_CLICK);
            }
        });
    }

    /**
     * 自定义事件
     *
     * @param event   event
     * @param map     map
     * @param trackId trackId
     */
    public void logEvent(@LogConstants.Event final String event, final HashMap<String, String> map, final String trackId) {
        mThread.postOnWorker(new Runnable() {
            @Override
            public void run() {
                ALog.e(mTag, "s-logEvent event:" + event + " map:" + map + " trackId:" + trackId);
                HashMap<String, Object> params = getCommonParams();
                params.put("tag", LogConstants.TAG_EVENT);
                params.put("event", event);

                params.put("map", readyMap(map));
                params.put("trackid", getEmptyString(trackId));

                enqueue(params, LogConstants.TAG_EVENT);
            }
        });
    }

    /**
     * 自定义事件
     * <p>
     * map传递value为Object类型
     *
     * @param event   event
     * @param map     map
     * @param trackId trackId
     */
    public void logEventMapObj(@LogConstants.Event final String event, final HashMap<String, Object> map, final String trackId) {
        mThread.postOnWorker(new Runnable() {
            @Override
            public void run() {
                ALog.e(mTag, "s-logEvent event:" + event + " map:" + map + " trackId:" + trackId);
                HashMap<String, Object> params = getCommonParams();
                params.put("tag", LogConstants.TAG_EVENT);
                params.put("event", event);

                params.put("map", readyMapObj(map));
                params.put("trackid", getEmptyString(trackId));

                enqueue(params, LogConstants.TAG_EVENT);
            }
        });
    }


    private HashMap<String, Object> getCommonParams() {
        HashMap<String, Object> params = new HashMap<>();
        SpUtil spUtil = SpUtil.getinstance(AppConst.getApp());
        // uid 获取
        params.put("uid", getEmptyString(SpUtil.getinstance(AppConst.getApp()).getUserID()));
        //平台
        params.put("ptx", "2");
        //包名
        params.put("pkna", getEmptyString(DeviceInfoUtils.getInstanse().getPackName()));
        //渠道号
        params.put("chid", getEmptyString(DeviceInfoUtils.getInstanse().getChannel()));
        // 版本号
        params.put("vn", getEmptyString(PackageControlUtils.getAppVersionName()));
        // ua
        params.put("ua", getEmptyString(DeviceInfoUtils.getInstanse().getDzLogUA()));
        // 手机分辨率(格式要求, ：800_600 )
        params.put("swl", getEmptyString(DeviceInfoUtils.getInstanse().getPixels()));

        if (SpUtil.getinstance(AppConst.getApp()).getSignAgreement()) {
            // 阿里的utdid
            params.put("utdid", getEmptyString(DeviceInfoUtils.getInstanse().getUtdId()));
        } else {
            // 阿里的utdid
            params.put("utdid", "");
        }
        params.put("sessionid", getEmptyString(sessionId));
        params.put("prov", spUtil.getClientProvince());
        params.put("city", spUtil.getClientCity());
        params.put("ph", spUtil.getPersonReadPref() + "");
        //设备激活时间
        params.put("atime", spUtil.getDeviceActivationTime() + "");
        //用户注册时间，用户进行华为登录以后，下放的时间
        params.put("ctime", spUtil.getRegistTime() + "");
        return params;
    }

    /**
     * 获取字符串
     *
     * @param str str
     * @return str
     */
    public String getEmptyString(String str) {
        return !TextUtils.isEmpty(str) ? str : "";
    }


    private void enqueue(HashMap<String, Object> params, int type) {
        if (params != null) {
            String json = JsonUtils.fromHashMap(params);
            ALog.e(mTag, "logType:" + type + " ,wash json:" + json);

            if (!worker.isStarted()) {
                worker.start();
            }
            worker.enqueue(new PriorityLog(PriorityLog.PRIORITY_NORMAL, json));
        }
    }

    public String getPrev() {
        return this.lastPvName;
    }


    /**
     * PriorityLog
     */
    static class PriorityLog implements Comparable<PriorityLog> {
        static final int PRIORITY_EMPTY = -1;
        static final int PRIORITY_NORMAL = 0;
        public int priority;
        public String log;

        public PriorityLog(int priority, String log) {
            this.priority = priority;
            this.log = log;
        }

        @Override
        public int compareTo(PriorityLog another) {
            if (null == another) {
                return priority;
            }
            return priority - another.priority;
        }

        @Override
        public int hashCode() {
            return super.hashCode();
        }

        @Override
        public boolean equals(Object obj) {
            return super.equals(obj);
        }
    }


    /**
     * Worker
     */
    class Worker implements Runnable {
        private PriorityBlockingQueue<PriorityLog> queue = new PriorityBlockingQueue<>();

        private volatile boolean started;

        void enqueue(PriorityLog log) {
            queue.put(log);
        }

        boolean isStarted() {
            synchronized (this) {
                return started;
            }
        }

        void start() {
            synchronized (this) {
                DzSchedulers.child(this);
                started = true;
            }
        }


        @Override
        public void run() {
            PriorityLog log;
            try {
                while ((log = take()) != null) {
                    try {
                        if (log.priority == PriorityLog.PRIORITY_EMPTY) {
                            Thread.sleep(1000);
                        } else {
                            RequestBody formBody = new FormBody.Builder().add("json", log.log).build();
                            Request request = new Request.Builder().url(LOG_URL).post(formBody).build();

                            Response response = mOkHttpClient.newCall(request).execute();

                            if (response.isSuccessful()) {
                                ALog.e(mTag, "post log success:" + log.log);
                            } else {
                                ALog.e(mTag, "post log failure:" + log.log);
                            }
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
                synchronized (this) {
                    started = false;
                }
            }
        }

        public PriorityLog take() throws InterruptedException {
            if (!SpUtil.getinstance(AppConst.getApp()).getSignAgreement()) {
                return new PriorityLog(PriorityLog.PRIORITY_EMPTY, "");
            } else {
                return queue.take();
            }
        }
    }
}
