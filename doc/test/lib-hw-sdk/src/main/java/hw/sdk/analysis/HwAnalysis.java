package hw.sdk.analysis;

import android.content.Context;

import com.huawei.hianalytics.util.HiAnalyticTools;
import com.huawei.hianalytics.v2.HiAnalytics;
import com.huawei.hianalytics.v2.HiAnalyticsConf;

import java.util.LinkedHashMap;

/**
 * 华为分析 用于打点分析数据
 * @author caimantang on 2018/4/11.
 */

public class HwAnalysis {
    private static final  String URL = "https://metrics1.data.hicloud.com:6447";

    //test
//    private final static String url="https://cloudbackup.hwcloudtest.cn:6447";

    /**
     * 初始化
     * @param context 上下文
     * @param channel 渠道号
     */
    public static void init(Context context, String channel) {
        HiAnalyticsConf.Builder builder = new HiAnalyticsConf.Builder(context);
        builder.setCollectURL(1, URL)
                .setCollectURL(0, URL)
                .setEnableImei(true)
                .setEnableAndroidID(true)
                .setChannel(channel)
                .create();
        //test
//        enableLog(context);
    }

    /**
     * 特别注明：Map中不允许key为”constants”或”_constants”
     * 此接口仅仅适用于运营大数据采集
     * 采集数据的可变字段由调用控制方确保符合安
     * 全规范，不能包含个人数据与敏感数据，如必
     * 须要上报，需要经安全组确认，并使用符合安
     * 全规范的加密方式进行加密
     *
     * @param eventId  自定义事件的标识符。eventId不能为null，长度大于0不超过256
     * @param mapValue 事件携带的信息,键值对，针对专属业务上报，mapValue键值对的个数大于0小于2048个，同时大小不超过200KB
     */
    public static void onEvent(String eventId, LinkedHashMap<String, String> mapValue) {
        HiAnalytics.onEvent(eventId, mapValue);
    }

    /**
     * 特别注明：Map中不允许key为”constants”或”_constants”
     * 采集数据的可变字段由调用控制方确保符合安
     * 全规范，不能包含个人数据与敏感数据，如必
     * 须要上报，需要经安全组确认，并使用符合安
     * 全规范的加密方式进行加密
     *
     * @param type     上报的数据类型，默认运营，对应数字如下。0：运营 1：运维 2：预装
     * @param eventId  自定义事件的标识符。eventId不能为null，长度大于0不超过256
     * @param mapValue 事件携带的信息,键值对，针对专属业务上报，mapValue键值对的个数大于0小于2048个，同时大小不超过200KB
     */
    public static void onEvent(int type, String eventId, LinkedHashMap<String, String> mapValue) {
        HiAnalytics.onEvent(type, eventId, mapValue);
    }


    /**
     * 特别注明：onResume采集数据的可变字段由调用控制方确保符合安全规范，
     * 不能包含个人数据与敏感数据，如必须要上报，需要经安全组确认，并使用
     * 符合安全规范的加密方式进行加密
     *
     * @param context 上下文
     */
    public static void onResume(Context context) {
        HiAnalytics.onResume(context);
    }

    /**
     * 特别注明：不允许含有key为"_constants"或"constants"
     * onResume采集数据的可变字段由调用控制方确保符合安全规范，
     * 不能包含个人数据与敏感数据，如必须要上报，需要经安全组确认，并使用
     * 符合安全规范的加密方式进行加密
     *
     * @param context 上下文
     * @param mapValue 当Map的size为1时，mapValue键值对的个数大于0小于2048个，同时大小不超过200KB
     */
    public static void onResume(Context context, LinkedHashMap<String, String> mapValue) {
        HiAnalytics.onResume(context, mapValue);
    }

    /**
     * 特别注明：不允许含有key为"_constants"或"constants"
     * onResume采集数据的可变字段由调用控制方确保符合安全规范，
     * 不能包含个人数据与敏感数据，如必须要上报，需要经安全组确认，并使用
     * 符合安全规范的加密方式进行加密
     *
     * @param viewName 采集的页面名，页面名称校验规则
     *                 •不允许为空
     *                 •长度不超过256
     *                 •必须以字母或下划线开头,其余内容为[a-zA-Z0-9. _-]
     *                 最长255
     * @param mapValue 当Map的size为1时，mapValue键值对的个数大于0小于2048个，同时大小不超过200KB
     */
    public static void onResume(String viewName, LinkedHashMap<String, String> mapValue) {
        HiAnalytics.onResume(viewName, mapValue);
    }


    /**
     * 特别注明：onResume采集数据的可变字段由调用控制方确保符合安全规范，
     * 不能包含个人数据与敏感数据，如必须要上报，需要经安全组确认，并使用
     * 符合安全规范的加密方式进行加密
     *
     * @param context 上下文
     */
    public static void onPause(Context context) {
        HiAnalytics.onPause(context);

    }

    /**
     * 特别注明：不允许含有key为"_constants"或"constants"
     * onResume采集数据的可变字段由调用控制方确保符合安全规范，
     * 不能包含个人数据与敏感数据，如必须要上报，需要经安全组确认，并使用
     * 符合安全规范的加密方式进行加密
     *
     * @param context 上下文
     * @param mapValue 当Map的size为1时，mapValue键值对的个数大于0小于2048个，同时大小不超过200KB
     */
    public static void onPause(Context context, LinkedHashMap<String, String> mapValue) {
        HiAnalytics.onPause(context, mapValue);
    }

    /**
     * 特别注明：不允许含有key为"_constants"或"constants"
     * onResume采集数据的可变字段由调用控制方确保符合安全规范，
     * 不能包含个人数据与敏感数据，如必须要上报，需要经安全组确认，并使用
     * 符合安全规范的加密方式进行加密
     *
     * @param viewName 采集的页面名，页面名称校验规则
     *                 •不允许为空
     *                 •长度不超过256
     *                 •必须以字母或下划线开头,其余内容为[a-zA-Z0-9. _-]
     *                 最长255
     * @param mapValue 当Map的size为1时，mapValue键值对的个数大于0小于2048个，同时大小不超过200KB
     */
    public static void onPause(String viewName, LinkedHashMap<String, String> mapValue) {
        HiAnalytics.onPause(viewName, mapValue);
    }

    /**
     * 立即上报本地缓存中的数据。数据上报成功后控制台会有相应信息提示
     * （前提是开启调试日志，提示信息：“resultCode: 200”）
     * 注意：如果业务不调用onReport接口，SDK会将业务的打点数据保存到
     * 本地，当本地数据量超过一定大小(10k)后，SDK会自动上报。
     * 为了避免SDK 采集的数据不能及时上报带来的消息不及时或者消息时间过
     * 久可能带来的数据丢弃、报表不全的情况，建议在业务启动后或在关键路径
     * 上或在进入后台前主动调用onReport接口，进行数据主动上报
     */
    public static void onReport() {
        HiAnalytics.onReport();
    }

    /**
     * 广告ID设置，由业务安全隐私专家评估是否设置
     *
     * @param oaid 广告ID。长度不超过4096
     */
    public static void setOAID(String oaid) {
        HiAnalytics.setOAID(oaid);
    }

    /**
     * 设置oaid是否允许跟踪开关
     *
     * @param isOaidTracking 应用是否允许跟踪OAID(默认不设置为空)：
     *                       True:允许跟踪
     *                       False:不允许跟踪
     */
    public static void setIsOaidTracking(boolean isOaidTracking) {
        HiAnalytics.setIsOaidTracking(isOaidTracking);
    }

    /**
     * 设置华为帐号内部虚拟ID，由业务安全隐私专家评估是否设置
     *
     * @param upid 华为帐号ID。长度不超过4096
     */
    public static void setUPID(String upid) {
        HiAnalytics.setUPID(upid);
    }

    /**
     * 返回SDK是否已经初始化。返回true标识已经初始化，false标识没有初始化
     *
     * @return 返回SDK是否已经初始化。返回true标识已经初始化，false标识没有初始化
     */
    public boolean getInitFlag() {
        return HiAnalytics.getInitFlag();
    }

    /**
     * 清除HASDK本地缓存的所有采集数据，其中包括发送失败的缓存数据
     */
    public static void clearCachedData() {
        HiAnalytics.clearCachedData();
    }

    /**
     * 特别注意：
     * a. 此接口仅用来处理APK升级时，BISDK 留在本地的缓存数据的场景；
     * b. 调用此接口时，需要确保当前apk进程的所有组件都没有调用BISDK
     * (hianalytics_v2.9.1.jar或hianalytics_v2.8.1.jar等)；若当前apk进程仍然有组件在使用BISDK，则业务不必调用此接口。
     * c. 此接口仅在apk升级时调用1次，后续不能再调用
     */
    public static void handleV1Cache() {
        HiAnalytics.handleV1Cache();
    }

    /**
     * HASDK初始化后，业务调用用此接口，HASDK对设置相应类别的URL进行判断，
     * 在没有调用refresh接口前，如果设置的URL与已设置好的URL不同，则停止此类别数据的采集与上报。
     * 特别注意：
     * a. 此接口用于处理单APK进程中多组件的情况下，各组件的上报策略不同的场景；
     * b. 为了最大程度避免上报策略不同带来的安全和隐私风险，若单个apk进程中不
     * 同的组件上报的地址不同，此时SDK不再进行采集和上报；
     * c. 调用此接口后，若再次调用了refresh 接口，那么SDK会将所有的设置清空，
     * 此设置会无效，各业务组件需要重新调用此接口以避免安全和隐私风险
     *
     * @param type       0 ：运营   、1：运维
     * @param tag        apk进程中各组件类别，如HMS-Core APK进程中的push, pay， game等
     * @param collectURL 采集服务器地址
     */
    public static void checkCollectURL(int type, String tag, String collectURL) {
        HiAnalytics.checkCollectURL(type, tag, collectURL);
    }

    /**
     * 开启调试日志，正式发布请关闭。默认开启debug级别日志
     *
     * @param context 上下文
     */
    public static void enableLog(Context context) {
        HiAnalyticTools.enableLog(context);
    }

    /**
     * 开启指定级别的调试日志，正式发布请关闭。
     * level:打印的日志最低级别
     * Log.DEBUG
     * Log.INFO
     * Log.WARN
     * Log.ERROR
     *
     * @param context 上下文
     * @param level log level
     */
    public static void enableLog(Context context, int level) {
        HiAnalyticTools.enableLog(context, level);
    }
}
