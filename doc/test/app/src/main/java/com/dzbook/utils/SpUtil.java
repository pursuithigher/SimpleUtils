package com.dzbook.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.text.TextUtils;

import com.dzbook.adapter.shelf.DzShelfDelegateAdapter;

/**
 * sp工具类
 *
 * @author lizhongzhong 2013-11-23
 */
public class SpUtil {

    /**
     * 应用是否安装
     */
    public static final String IS_APP_INITIALIZED = "isAppInitialized";

    /**
     * APP_CODE
     */
    public static final String DZ_APP_CODE = "dz.app.code";

    /**
     * 渠道
     */
    public static final String DZ_APP_CHANNEL = "dz.app.channel";
    /**
     * 书架排序方式：0：时间，1：书名
     */
    public static final String SHELF_BOOK_SORT = "books_sort";

    /**
     * 是否展示新手指引
     */
    public static final String HW_IS_SHOW_GUIDE = "hw_is_show_guide";
    /**
     * 分享链接
     */
    public static final String SP_READER_SHAREURL = "sp_reader_shareurl";
    /**
     * 下载连接
     */
    public static final String SP_READER_DOWNLOADURL = "sp_reader_downloadUrl";
    /**
     * 今日签到
     */
    public static final String SP_USER_SIGN = "user.today.sign";


    /**
     * 推送客户端id
     */
    public static final String PUSH_CLIENTID = "gexin.client.id";

    /**
     * 为了阅读50章收费章节后 提示
     */
    public static final String READ_READMIND_CHECKBOX = "sp.read.readmind.checkbox";
    /**
     * 为了阅读50章收费章节后 提示(一天一次)
     */
    public static final String READ_READMIND_DATE_LIMIT = "sp.read.readmind.date.limit";

    /**
     * 最后一次抽奖的时间yyyyMMdd
     */
    public static final String DZ_TODAY_LUCK_DRAW = "user.today.luck.draw";


    /**
     * 应用安装最新的版本
     */
    public static final String DZ_APP_INSTALL_LAST_VERSION = "dz.app.install.last.version";
    /**
     * 弹出去好评的窗口 true:需要弹窗
     */
    public static final String DZ_APP_APPRAISAL_ALERT = "dz.app.appraisal.alert";


    /**
     * 云书架需要同步的json数据
     */
    public static final String SYNCH_CLOUD_BOOKS_JSON = "sp.synch.cloud.books.json";

    /**
     * 是否已经展示了同步云书架书籍窗口
     */
    public static final String IS_ALREADY_SHOW_CLOUD_DIALOG = "sp.is.already.show.cloud.dialog";
    /**
     * 主界面主tabjson数据
     */
    public static final String IS_OPEN_RECHARGELIST = "is_open_rechargelist";
    /**
     * 是否打开过充值页面
     */
    public static final String DZ_KEY_MAIN_TAB_JSON = "dz.key_main_tab_json";

    /**
     * 是否充值成功过
     */
    public static final String IS_SUCCESS_RECHARGE = "is_success_recharge";

    /**
     * 上次更新时间
     */
    public static final String DZ_LASTUPDATETIME = "dz.lastUpdateTime";

    /**
     * 首次打开 并且需要直接打开书籍
     */
    public static final String FIRST_DIRECTOPEN = "first.DIRECT_OPEN";

    /**
     * h5uri需直接打开
     */
    public static final String FROM_URI_BOOK_OPEN = "from.h5uri.book.open";

    /**
     * 记录阅读器提示向左滑的弹窗 是否弹出过
     */
    public static final String READER_IS_OPEN = "sp.reader.is.open";

    /**
     * 最近阅读
     */
    public static final String RECENT_READER = "recent.reader";

    /**
     * 标记华为的是否上传utdId
     */
    public static final String DZ_HW_IS_ALREAD_UPLOAD_UTDID = "dz.hw.is.already.upload.utdid";

    /**
     * resolve.time
     */
    public static final String HTTPDNS_PRE_RESOLVE_TIME = "dz.sp.httpdns.pre.resolve.time";


    /**
     * app最后安装时间
     */
    public static final String DZ_APP_BUILD_TIME_LAST = "app.build.time.last";


    /********* 书城中子tab的id，type记录，方便其他子页面获取书城的数据****************/

    public static final String BOOK_STORE_LIMITFREE_ID = "book_store_limitfree_id";
    /**
     * 限免类型
     */
    public static final String BOOK_STORE_LIMITFREE_TYPE = "book_store_limitfree_type";
    /**
     * 书城vip的id
     */
    public static final String BOOK_STORE_VIP_ID = "book_store_vip_id";
    /**
     * 书城vip的类型
     */
    public static final String BOOK_STORE_VIP_TYPE = "book_store_vip_type";


    /********* 书城中子tab的id，type记录，方便其他子页面获取书城的数据****************/

    /**
     * share文件存储搜索历史
     */
    public static final String KEY_SEARCH_HISTORY = "key_search_history";

    /**
     * 缓存书架更新的书籍最大数量
     */
    public static final String KEY_UPDATE_BOOK_NUM = "key_update_book_num";

    /**
     * 启动-模式,首次安装-首次启动
     */
    public static final int LMODE_NEW_INSTALL = 1;
    /**
     * 覆盖安装-首次启动
     */
    public static final int LMODE_UPDATE = 2;

    /**
     * 已安装-二次启动
     */
    public static final int LMODE_AGAIN = 3;

    /**
     * 选中阅读偏好—男
     */
    public static final int SELECT_BOY = 1;

    /**
     * 选中阅读偏好-女
     */
    public static final int SELECT_GIRL = 2;
    /**
     * 存储阅读偏好
     * 2：女生
     * 1：男生
     * 0:跳过
     */
    public static final String KEY_PERSON_READ_PREF = "key_person_read_pref";

    /**
     * 激活时间
     */
    public static final String DEVICE_ACTIVATION_TIME = "device_activation_time";
    /**
     * 注册时间
     */
    public static final String REGIST_TIME = "regist_time";

    /**
     * 是否已经保存过阅读偏好 用户捞取已经保存的但没有上传的用户
     */
    public static final String KEY_PERSON_ALREADY_EXISTS_READ_PREF = "key_person_already_exists_read_pref";


    /******************************************华为使用的sp*********************************/

    /**
     * 老用户掌阅华为阅读资产访问地址
     */
    public static final String DZ_OLD_USER_ASSERT_ZHANG_YUE_H5_URL = "dz.old.user.assert.zhang.yue.h5.url";

    /**
     * 老用户书旗华为阅读资产访问地址
     */
    public static final String DZ_OLD_USER_ASSERT_SHU_QI_H5_URL = "dz.old.user.assert.shu.qi.h5.url";

    /**
     * 是否老用户资产弹窗需要弹出
     */
    public static final String DZ_IS_OLD_USER_ASSERT_NEED_SHOW = "dz.is.old.user.assert.need.show";

    /**
     * 是否VIP 0:否 1:是
     */
    public static final String DZ_IS_VIP = "dz.sp.is.vip";
    /**
     * vip过期时间
     */
    public static final String DZ_VIP_EXPIRED_TIME = "dz.sp.vip.expired.time";

    /**
     * 阅读时长
     */
    public static final String SP_READING_TIME = "sp.reading.time";

    /**
     * 最大阅读时长
     */
    public static final String SP_MAX_READING_TIME = "sp.max.reading.time";

    /**
     * token
     */
    private static final String SP_DZ_APP_TOKEN = "sp.dz.app.token";

    /**
     * 是否老用户资产弹窗已经弹出
     */
    private static final String DZ_IS_OLD_USER_ASSERT_DIALOG_SHOWED = "dz.is.old.user.assert.dialog.showed";

    /**
     * 是否手机号码或者三方帐号登陆成功
     * true:登陆成功
     * false:登陆失败
     */
    private static final String BIND_ACCOUNT_LOGIN_STATUS = "sp.bind.account.login.status";


    private static final String SP_USER_REMAIN = "sp.user.remain";
    private static final String SP_USER_REMAIN_UNIT = "sp.user.remain.unit";
    private static final String SP_USER_VOUCHERS = "sp.user.vouchers";
    private static final String SP_USER_VOUCHERS_UNIT = "sp.user.vouchers.unit";

    private static final String SPLASH_AGREE = "splash_agree";
    /**
     * 分享信息
     */
    private static final String SHARED_PREFERENCES_INFO = "ishugui.shareInfo";

    /**
     * 应用安装小时
     */
    private static final String DZ_APP_INSTALL_HOUR = "dz.app.install.hour";
    /**
     * 应用启动次数
     */
    private static final String APP_COUNTER = "app.counter";

    private static final String MSG_SET = "isReceiveMsg";
    //服务器故障 通知的dialog 弹出时间 做一个标记
    private static final String DZ_SERVER_FAILURE_DIALOG_TIME = "sp.server.failure.dialog.time";
    //服务器故障 通知的dialog 请求弹出的时间 做一个标记
    private static final String DZ_REQUEST_SERVER_FAILURE_DIALOG_TIME = "sp.request.server.failure.dialog.time";

    //登录成功后登录用户的图像地址
    private static final String BIND_ACCOUNT_LOGIN_SUCCESS_USER_PICTRUE = "account.login.success.user.picture";
    //登录成功后登录用户的昵称
    private static final String BIND_ACCOUNT_LOGIN_SUCCESS_USER_NICKNAME = "account.login.success.user.nickname";
    //自有支付预加载数量
    private static final String DZ_PAY_PRELOAD_NUM = "dz.sp.dzpay.preload.num";

    /**
     * 获取位置信息的时间
     */
    private static final String DZ_GET_LOCATION_INFO_TIME = "sp.dz.get.location.info.time";
    /**
     * 客户端所在省份
     */
    private static final String DZ_CLIENT_PROVINCE_INFO = "sp.dz.client.province.info";
    /**
     * 客户端所在城市
     */
    private static final String DZ_CLIENT_CITY_INFO = "sp.dz.client.city.info";
    /**
     * duration_time
     */
    private static final String DURATION_TIME = "duration_time";

    /**
     * grid模式
     * {@link DzShelfDelegateAdapter#MODE_GRID}
     * list模式
     * {@link DzShelfDelegateAdapter#MODE_LIST}
     */
    private static final String DZ_BOOK_SHELF_SHOW_MODE = "dz.sp.book.shelf.show.mode";
    /**
     * 记录系统最后一次崩溃时间。
     */
    private static final String KEY_UNCAUGHT_EXCEPTION_TIME = "key_uncaught_exception_time";
    /**
     * 自有支付的产品线
     */
    private static String[] dzPay = {"f002"};

    private static Context mContext;
    private static SpUtil sharedPreferencesUtil = new SpUtil();
    /**
     * 音量键翻页
     */
    private final String volumeKey = "boolean.volume.key.turn.page";

    private volatile SharedPreferences saveInfo;
    private final String userId = "sp.user.id";
    private final String mKeyShelfBookJson = "key_shelf_book_json";


    private boolean isOpenMarked = false;

    /**
     * 启动-模式
     */
    private int launchMode = LMODE_AGAIN;
    private long mLong = 24;

    private Editor saveEditor;

    private String userRemain, remainUnit;
    private String userVouchers;

    private SpUtil() {
    }

    /**
     * 获取SpUtil实例
     *
     * @param context context
     * @return 实例
     */
    public static SpUtil getinstance(Context context) {
        if (null != context) {
            mContext = context.getApplicationContext();
        }
        sharedPreferencesUtil.init();
        return sharedPreferencesUtil;
    }

    /**
     * 初始化
     */
    @SuppressLint("CommitPrefEdits")
    private void init() {
        if (saveInfo == null) {
            synchronized (SpUtil.class) {
                if (saveInfo == null && mContext != null) {
                    SharedPreferences sp = mContext.getSharedPreferences(SHARED_PREFERENCES_INFO, Context.MODE_PRIVATE);
                    saveEditor = sp.edit();
                    this.saveInfo = sp;
                }
            }
        }
    }

    /**
     * 通过key获取sp值，默认值为""
     *
     * @param key key
     * @return string
     */
    public String getString(String key) {
        return saveInfo.getString(key, "");
    }

    /**
     * 通过key获取sp值，默认值为defaultValue
     *
     * @param key          key
     * @param defaultValue defaultValue
     * @return string
     */
    public String getString(String key, String defaultValue) {
        return saveInfo.getString(key, defaultValue);
    }

    /**
     * 通过key存储sp值
     *
     * @param key   key
     * @param value value
     */
    public void setString(String key, String value) {
        saveEditor.putString(key, value).apply();
    }

    /**
     * 获取int型sp值，默认值0
     *
     * @param key key
     * @return int
     */
    public int getInt(String key) {
        return saveInfo.getInt(key, 0);
    }

    /**
     * 获取int型sp值，默认值defaultValue
     *
     * @param key          key
     * @param defaultValue defaultValue
     * @return int
     */
    public int getInt(String key, int defaultValue) {
        return saveInfo.getInt(key, defaultValue);
    }

    /**
     * 通过key存储sp值
     *
     * @param key   key
     * @param value value
     */
    public void setInt(String key, int value) {
        saveEditor.putInt(key, value).apply();
    }

    /**
     * 获取long型sp值，默认值defValue
     *
     * @param key      key
     * @param defValue defValue
     * @return long
     */
    public long getLong(String key, long defValue) {
        return saveInfo.getLong(key, defValue);
    }

    /**
     * 通过key存储sp值
     *
     * @param key   key
     * @param value value
     */
    public void setLong(String key, long value) {
        saveEditor.putLong(key, value).apply();
    }

    /**
     * 通过key存储boolean型sp值
     *
     * @param key   key
     * @param value value
     */
    public void setBoolean(String key, boolean value) {
        saveEditor.putBoolean(key, value).apply();
    }

    /**
     * 获取boolean型sp值，默认值false
     *
     * @param key key
     * @return boolean
     */
    public boolean getBoolean(String key) {
        return saveInfo.getBoolean(key, false);
    }

    /**
     * 获取boolean型sp值，defaultValue
     *
     * @param key          key
     * @param defaultValue defaultValue
     * @return boolean
     */
    public boolean getBoolean(String key, boolean defaultValue) {
        return saveInfo.getBoolean(key, defaultValue);
    }

    /**
     * 获取是否接收消息
     *
     * @return boolean
     */
    public boolean getIsReceiveMsg() {
        return getBoolean(MSG_SET, true);
    }

    /**
     * 设置是否接收消息
     *
     * @param value value
     */
    public void setIsReceiveMsg(boolean value) {
        setBoolean(MSG_SET, value);
    }

    /**
     * 记录本地阅读时长
     *
     * @param time time
     */

    public void setLocalReaderDurationTime(long time) {
        setLong(DURATION_TIME, time);
    }

    public long getLocalReaderDurationTime() {
        return getLong(DURATION_TIME, 0);
    }

    public String getSpReaderShareurl() {
        return getString(SP_READER_SHAREURL, "http://fx.jsread.cn/book_content.html");
    }

    /**
     * 用户级别名称（例: "草民"）
     *
     * @param name name
     */
    @Deprecated
    public void setLevelName(String name) {
        setString("levelName", name);
    }


    /**
     * 用户级别名称（例: "草民"）
     *
     * @return string
     */
    @Deprecated
    public String getLevelName() {
        return getString("levelName", "");
    }

    /**
     * 用户级别(例："LV1")
     *
     * @param no no
     */
    @Deprecated
    public void setLevelNo(String no) {
        setString("levelNo", no);
    }

    /**
     * 用户级别(例："LV1")
     *
     * @return string
     */
    @Deprecated
    public String getLevelNo() {
        return getString("levelNo", "");
    }

    /**
     * 设置登录用户昵称
     * 绑定UserId
     *
     * @param nickName nickName
     */
    public void setLoginUserNickNameByUserId(String nickName) {
        setString(BIND_ACCOUNT_LOGIN_SUCCESS_USER_NICKNAME + getUserID(), nickName);
    }

    /**
     * 得到登录用户昵称
     * 绑定UserId
     *
     * @return string
     */
    public String getLoginUserNickNameByUserId() {
        return getString(BIND_ACCOUNT_LOGIN_SUCCESS_USER_NICKNAME + getUserID());
    }

    /**
     * 设置登录用户图像地址
     * 绑定UserId
     *
     * @param coverWap coverWap
     */
    public void setLoginUserCoverWapByUserId(String coverWap) {
        setString(BIND_ACCOUNT_LOGIN_SUCCESS_USER_PICTRUE + getUserID(), coverWap);
    }

    /**
     * 得到登录用户图像地址
     * 绑定UserId
     *
     * @return string
     */
    public String getLoginUserCoverWapByUserId() {
        return getString(BIND_ACCOUNT_LOGIN_SUCCESS_USER_PICTRUE + getUserID());
    }

    // --------------------------阅读器相关设置------------------------------------------

    /**
     * 移除本地书扫描时间
     *
     * @param key key
     */
    public void removeLocalBookScanTime(String key) {
        saveEditor.remove(key + "time");
        saveEditor.commit();
    }

    /**
     * 移除本地书扫描状态
     *
     * @param key key
     */
    public void removeLocalBookScanState(String key) {
        saveEditor.remove(key + "state");
        saveEditor.commit();
    }


    public int getVolumeKeyCode() {
        return getInt(volumeKey, 1);
    }


    /**
     * 设置应用计数器。进入应用一次，值累加1
     */
    public void setAppConunter() {
        setInt(APP_COUNTER, getAppCounter() + 1);
    }

    public int getAppCounter() {
        return getInt(APP_COUNTER, 0);
    }


    /**
     * 判断是否是AppCode
     *
     * @param code code
     * @return boolean
     */
    public boolean isDzAppCode(String code) {
        if (null != dzPay && dzPay.length > 0) {
            for (int i = 0; i < dzPay.length; i++) {
                if (dzPay[i].equals(code)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 初始化安装时间（小时）
     */
    public void initInstallHour() {
        long curHour = System.currentTimeMillis() / 3600000;
        long memHour = getLong(DZ_APP_INSTALL_HOUR, -1);
        if (memHour < 0 || memHour > curHour) {
            setLong(DZ_APP_INSTALL_HOUR, curHour);
        }
    }

    /**
     * 获取安装时间（小时）
     *
     * @return long
     */
    public long getInstallHour() {
        long curHour = System.currentTimeMillis() / 3600000;
        long memHour = getLong(DZ_APP_INSTALL_HOUR, -1);
        if (memHour < 0 || memHour > curHour) {
            setLong(DZ_APP_INSTALL_HOUR, curHour);
        }
        return curHour - getLong(DZ_APP_INSTALL_HOUR, -1);
    }

    /**
     * 是否安装超过一个星期了
     *
     * @param frequency frequency
     * @return boolean
     */
    public boolean isInstallOneMonth(int frequency) {
        return getInstallHour() >= mLong * frequency;
    }

    /**
     * 今天有没有标记
     *
     * @param key 关键字
     * @return boolean
     */
    public boolean hasMarkTodayByKey(String key) {
        return (TimeUtils.getFormatDate("yyyyMMdd") + getUserID()).equals(getString(key, ""));
    }

    /**
     * 设置今天的标记。
     *
     * @param key 关键字
     */
    public void markTodayByKey(String key) {
        setString(key, TimeUtils.getFormatDate("yyyyMMdd") + getUserID());
    }

    /**
     * 设置客户端省份
     *
     * @param province province
     */
    public void setClientProvince(String province) {
        setString(DZ_CLIENT_PROVINCE_INFO, province);
    }

    /**
     * 设置客户端所在城市
     *
     * @param city city
     */
    public void setClientCity(String city) {
        setString(DZ_CLIENT_CITY_INFO, city);
    }

    /**
     * 得到客户端省份信息
     *
     * @return string
     */
    public String getClientProvince() {
        return getString(DZ_CLIENT_PROVINCE_INFO);
    }

    /**
     * 得到客户端城市信息
     *
     * @return string
     */
    public String getClientCity() {
        return getString(DZ_CLIENT_CITY_INFO);
    }


    /**
     * 获取启动-模式
     */
    public void markOpenApp() {
        // 防止-重复调用
        if (isOpenMarked) {
            return;
        }
        isOpenMarked = true;

        String lastVersion = getString(SpUtil.DZ_APP_INSTALL_LAST_VERSION, "");
        String thisVersion = PackageControlUtils.getAppVersionName();

        // 首次启动
        if (TextUtils.isEmpty(lastVersion)) {
            launchMode = LMODE_NEW_INSTALL;
            setString(SpUtil.DZ_APP_INSTALL_LAST_VERSION, thisVersion);
        } else if (!TextUtils.equals(thisVersion, lastVersion)) {
            launchMode = LMODE_UPDATE;
            setString(SpUtil.DZ_APP_INSTALL_LAST_VERSION, thisVersion);
        } else {
            launchMode = LMODE_AGAIN;
        }
    }

    /**
     * 是否是首次安装
     *
     * @return boolean
     */
    public boolean isUpdateInstall() {
        return launchMode == LMODE_UPDATE;
    }

    /**
     * 获取是否需要弹故障通知 有些时候用户点了不在提醒 那就不需要请求网络走下一步什么的了
     *
     * @return boolean
     */
    public boolean getServerFailureDialogTime() {
        if (getRequestServerFailureDialogTime()) {
            return true;
        }
        long thisTime = System.currentTimeMillis();
        long delayTime = thisTime - getLong(DZ_SERVER_FAILURE_DIALOG_TIME, 0);
        return delayTime != thisTime && delayTime <= 30 * 60 * 1000;
    }

    /**
     * 避免服务器失败 去频繁的弹窗。5分钟内的请求不做处理。
     *
     * @param time time
     */
    public void setRequestServerFailureDialogTime(long time) {
        setLong(DZ_REQUEST_SERVER_FAILURE_DIALOG_TIME, time);
    }


    private boolean getRequestServerFailureDialogTime() {
        long thisTime = System.currentTimeMillis();
        long delayTime = thisTime - getLong(DZ_REQUEST_SERVER_FAILURE_DIALOG_TIME, 0);
        return delayTime != thisTime && delayTime < 5 * 60 * 1000;
    }


    /**
     * 将搜索记录存储到share文件
     *
     * @param keySearchHistory keySearchHistory
     */
    public void setKeySearchHistory(String keySearchHistory) {
        setString(KEY_SEARCH_HISTORY, keySearchHistory);
    }

    public String getKeySearchHistory() {
        return getString(KEY_SEARCH_HISTORY);
    }


    /**
     * 设备激活时间
     *
     * @param value value
     */
    public void setDeviceActivationTime(String value) {
        setString(DEVICE_ACTIVATION_TIME, value);
    }

    public String getDeviceActivationTime() {
        return getString(DEVICE_ACTIVATION_TIME);
    }

    /**
     * 注册时间
     *
     * @param value value
     */
    public void setRegistTime(String value) {
        setString(REGIST_TIME, value);
    }

    public String getRegistTime() {
        return getString(REGIST_TIME);
    }

    /**
     * 否已经保存过阅读偏好 用户捞取已经保存的但没有上传的用户
     *
     * @param exist exist
     */
    public void setPersonExistsReadPref(boolean exist) {
        setBoolean(KEY_PERSON_ALREADY_EXISTS_READ_PREF, exist);
    }

    public boolean getPersonExistsReadPref() {
        return getBoolean(KEY_PERSON_ALREADY_EXISTS_READ_PREF, false);
    }

    /**
     * 设置阅读票好
     *
     * @param sex sex
     */
    public void setPersonReadPref(int sex) {
        setInt(KEY_PERSON_READ_PREF, sex);
    }

    public int getPersonReadPref() {
        return getInt(KEY_PERSON_READ_PREF, -1);
    }

    /**
     * 获取书架显示模式
     *
     * @return int
     */
    public int getBookShelfMode() {
        return getInt(DZ_BOOK_SHELF_SHOW_MODE, "1".equals(PackageControlUtils.shelfMode()) ? DzShelfDelegateAdapter.MODE_GRID : DzShelfDelegateAdapter.MODE_LIST);
    }

    /**
     * 设置书架显示模式
     * grid模式
     * {@link DzShelfDelegateAdapter#MODE_GRID}
     * list模式
     * {@link DzShelfDelegateAdapter#MODE_LIST}
     *
     * @param bookShelfMode bookShelfMode
     */
    public void setBookShelfMode(int bookShelfMode) {
        setInt(DZ_BOOK_SHELF_SHOW_MODE, bookShelfMode);
    }


    /**
     * 服务端在启动过程，缓存下来的图书列表信息。
     * 0:初始状态。
     * -1:失败。
     * 1:已内置。
     * (body):请求成功以后配置的值。exp:{"girls":{...},"default":{...}}
     *
     * @return string
     */
    public String getShelfBookList() {
        return getString(mKeyShelfBookJson, "0");
    }

    /**
     * 服务端在启动过程，缓存下来的图书列表信息。
     * 0:初始状态。
     * -1:失败。
     * 1:已内置。
     * (body):请求成功以后配置的值。exp:{"girls":{...},"default":{...}}
     *
     * @param v v
     */
    public void setShelfBookList(String v) {
        if (!TextUtils.equals(v, "1")) {
            setString(mKeyShelfBookJson, v);
        }
    }

    /**
     * 设置代开主界面主tabjson数据次数
     */
    public void setOpenRechargelistTimes() {
        int anInt = getInt(IS_OPEN_RECHARGELIST, 0);
        setInt(IS_OPEN_RECHARGELIST, anInt + 1);
    }

    /**
     * 成功充值次数
     */
    public void setSuccessRechargeTimes() {
        int anInt = getInt(IS_SUCCESS_RECHARGE, 0);
        setInt(IS_SUCCESS_RECHARGE, anInt + 1);
    }


    /**
     * 设置更新书籍数
     *
     * @param bookNum bookNum
     */
    public void setUpdateBookNum(int bookNum) {
        setInt(KEY_UPDATE_BOOK_NUM, bookNum);
    }

    public int getUpdateBookNum() {
        return getInt(KEY_UPDATE_BOOK_NUM, 0);
    }


    /**
     * 记录系统最后一次的崩溃时间
     *
     * @param l l
     */
    public void setUncaughtExceptionTime(long l) {
        setLong(KEY_UNCAUGHT_EXCEPTION_TIME, l);
    }

    public long getUncaughtExceptionTime() {
        return getLong(KEY_UNCAUGHT_EXCEPTION_TIME, -1);
    }

    /**
     * 设置预加载数量
     *
     * @param num num
     */
    public void setDzPayPreloadNum(int num) {
        if (num > 0) {
            setInt(DZ_PAY_PRELOAD_NUM, num);
        }
    }

    /**
     * 默认预加载数量为1章
     *
     * @return int
     */
    public int getDzPayPreloadNum() {
        return getInt(DZ_PAY_PRELOAD_NUM, 3);
    }

    /**
     * 设置splash页面 签署协议
     *
     * @param sign 是否签署
     */
    public void setSignAgreement(boolean sign) {
        setBoolean(SPLASH_AGREE, sign);
    }

    /**
     * 获取splash页面 签署协议
     *
     * @return boolean
     */
    public boolean getSignAgreement() {
        return getBoolean(SPLASH_AGREE, false);
    }


    /**
     * 用于展示的阅读时长
     *
     * @param time time
     */
    public void setShowReaderTime(long time) {
        setLong(SP_READING_TIME, time);
    }

    public long getShowReaderTime() {
        return getLong(SP_READING_TIME, 0);
    }

    /**
     * 设置用户余额
     *
     * @param userRemainP 用户余额 数值
     * @param remainUnitP 用户余额 单位
     */
    public void setUserRemain(String userRemainP, String remainUnitP) {
        if (!TextUtils.isEmpty(userRemainP) && !TextUtils.isEmpty(remainUnitP)) {
            this.userRemain = userRemainP;
            this.remainUnit = remainUnitP;
            setString(SP_USER_REMAIN, userRemainP);
            setString(SP_USER_REMAIN_UNIT, remainUnitP);
        }
    }

    /**
     * 得到用户余额，数值
     *
     * @return string
     */
    public String getUserRemainPrice() {
        if (TextUtils.isEmpty(userRemain)) {
            userRemain = getString(SP_USER_REMAIN);
        }
        return userRemain;
    }

    /**
     * 得到用户余额，单位
     *
     * @return string
     */
    public String getUserRemainUnit() {
        if (TextUtils.isEmpty(remainUnit)) {
            remainUnit = getString(SP_USER_REMAIN_UNIT);
        }
        return remainUnit;
    }


    /**
     * 设置用户代金券
     *
     * @param userVouchersP    用户余额 数值
     * @param userVouchersUnit 用户余额 单位
     */
    public void setUserVouchers(String userVouchersP, String userVouchersUnit) {
        if (!TextUtils.isEmpty(userVouchersP) && !TextUtils.isEmpty(userVouchersUnit)) {
            this.userVouchers = userVouchersP;
            setString(SP_USER_VOUCHERS, userVouchersP);
            setString(SP_USER_VOUCHERS_UNIT, userVouchersUnit);
        }
    }

    /**
     * 得到用户代金券，数值
     *
     * @return string
     */
    public String getUserVouchers() {
        if (TextUtils.isEmpty(userVouchers)) {
            userVouchers = getString(SP_USER_VOUCHERS);
        }
        return userVouchers;
    }

    /**
     * 用户id
     *
     * @return string
     */
    public String getUserID() {
        return saveInfo.getString(userId, "");
    }

    /**
     * 用户id
     *
     * @param value value
     * @return boolean
     */
    public boolean setUserID(String value) {
        saveEditor.putString(userId, value);
        return saveEditor.commit();
    }

    /**
     * 获取书签同步时间
     *
     * @param userIdP userIdP
     * @return string
     */
    public String getBookMarkSyncTime(String userIdP) {
        return saveInfo.getString(userIdP + "-mark-sync", "");
    }

    /**
     * 设置书签同步时间
     *
     * @param userIdP  userIdP
     * @param syncTime syncTime
     */
    public void setBookMarkSyncTime(String userIdP, String syncTime) {
        saveEditor.putString(userIdP + "-mark-sync", syncTime);
        saveEditor.apply();
    }

    /**
     * 获取appToken
     *
     * @return string
     */
    public String getAppToken() {
        return getString(SP_DZ_APP_TOKEN, "");
    }

    /**
     * 设置appToken
     *
     * @param value value
     * @return boolean
     */
    public boolean setAppToken(String value) {
        saveEditor.putString(SP_DZ_APP_TOKEN, value);
        return saveEditor.commit();
    }

    /**
     * 得到帐号登陆状态
     * true:登陆成功
     * false:登陆失败
     *
     * @return boolean
     */
    public Boolean getAccountLoginStatus() {
        return getBoolean(BIND_ACCOUNT_LOGIN_STATUS, false);
    }

    /**
     * 设置用户帐号登陆状态
     *
     * @param status status
     */
    public void setAccountLoginStatus(boolean status) {
        setBoolean(BIND_ACCOUNT_LOGIN_STATUS, status);
    }

    /**
     * 是否老用户资产弹窗已经弹出 跟用户绑定
     *
     * @return boolean
     */
    public boolean isOldUserAssertDialogShowed() {
        return getBoolean(SpUtil.DZ_IS_OLD_USER_ASSERT_DIALOG_SHOWED + getUserID(), false);
    }

    /**
     * 设置用户资产弹窗 已经弹出
     * 跟用户绑定
     */
    public void setOldUserAssertDialogAlreadyshow() {
        setBoolean(SpUtil.DZ_IS_OLD_USER_ASSERT_DIALOG_SHOWED + getUserID(), true);
    }

    /*************************************************************************************/


}
