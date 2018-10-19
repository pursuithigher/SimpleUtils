package com.dzbook.net.hw;

import android.content.Context;
import android.content.res.Resources;
import android.net.Uri;
import android.text.TextUtils;

import com.dzbook.AppConst;
import com.dzbook.utils.SpUtil;
import com.ishugui.R;

/**
 * RequestCall
 *
 * @author lizhongzhong 2018/4/12.
 */

public class RequestCall {

    /**
     * 华为注册接口
     */
    public static final String REGISTER_CALL = "101";


    /**
     * 设备激活接口
     */
    public static final String DEVICE_ACTIVATION_CALL = "102";

    /**
     * 获取用户信息接口
     */
    public static final String GET_USER_INFO_CALL = "106";


    /**
     * push的cid上传接口
     */
    public static final String URL_UPLOAD_CID = "109";

    /**
     * 分类一级页面接口
     */
    public static final String MAIN_TYPE_INDEX = "165";

    /**
     * 分类二级页面接口
     */
    public static final String MAIN_TYPE_DETAIL = "166";

    /**
     * 内置书接口
     */
    public static final String BUILD_IN_BOOK = "176";

    /**
     * 搜索
     */

    public static final String SEARCH_CALL = "320";

    /**
     * 联想词搜索
     */
    public static final String SEARCH_SUGGEST_CALL = "321";

    /**
     * 搜索页热词
     */
    public static final String SEARCH_HOT_CALL = "322";

    /**
     * 分享领看点
     */
    public static final String SHARE_KD_CALL = "500";

    /**
     * 书架更新接口
     */
    public static final String SHELF_UPDATE_URL = "177";

    /**
     * 书城数据url
     */
    public static final String STORE_DATA_URL = "160";


    /**
     * 书城二级页面数据url
     */
    public static final String STORE_TWO_LEVEL_DATA_URL = "161";

    /**
     * 图书详情
     */
    public static final String BOOK_DETAIL_CALL = "110";

    /**
     * 获取章节目录
     */
    public static final String CATALOG_COMMENT_CALL = "111";

    /**
     * 终章推荐
     */
    public static final String BOOK_RECOMMEND_CALL = "112";

    /**
     * 快速打开书籍
     */
    public static final String FAST_OPEN_BOOK_CALL = "113";

    /**
     * 书籍点赞
     */
    public static final String BOOK_PRAISE_CALL = "114";

    /**
     * 推荐书籍查看更多
     */
    public static final String MORE_RECOMMEND_CALL = "115";

    /**
     * 校验用户发表评论状态
     */
    public static final String CHECK_COMMENT_CALL = "116";

    /**
     * 发表评论
     */
    public static final String SEND_COMMENT_CALL = "117";

    /**
     * 查看更多评论
     */
    public static final String MORE_COMMENT_CALL = "118";

    /**
     * 评论点赞 举报 删除
     */
    public static final String ACTION_COMMENT_CALL = "119";
    /**
     * 用户评论 个人中心
     */
    public static final String USER_COMMENT_CALL = "120";


    /**
     * 实名认证 发送短信接口
     */
    public static final String REAL_NAME_AUTH = "105";

    /**
     * 实名认证 绑定接口
     */
    public static final String REAL_NAME_BIND_PHONE = "103";

    /**
     * 实名认证 切换手机号接口
     */
    public static final String REAL_NAME_SWITCH_PHONE = "104";


    /**
     * 订购-后台多章加载
     */
    public static final String URL_LOT_PRELOAD = "153";

    /**
     * 订购-加载已经订购章节
     */
    public static final String URL_LOAD_ALREADY_ORDER_CHAPTER = "154";

    /**
     * 章节内容确实补偿接口
     */
    public static final String URL_MISS_CHAPTER_CONTENT_AWARD = "155";

    /**
     * 领取限免书籍的接口
     */
    public static final String URL_GET_FREE_BOOK_FROM_NET = "164";

    /**
     * 云书架登录同步
     */
    public static final String URL_CLOUD_SHELF_LOGIN_SYNC = "171";

    /**
     * 百度语音tts接口
     */
    public static final String URL_TTS_PLUGIN = "163";
    /**
     * 云书架：上传书籍的阅读进度
     */
    public static final String URL_SYNC_BOOK_PROGRESS = "172";

    /**
     * 云书架：获取多本书籍的详情信息
     */
    public static final String URL_CLOUD_SHELF_BOOK_DETAIL = "175";

    /**
     * 同步远程书籍进度
     */
    public static final String URL_CLOUD_SHELF_SYNC_BOOK_PROGRESS = "173";

    /**
     * 云书架页面数据获取
     */
    public static final String URL_CLOUD_SHELF_GET_LIST = "170";

    /**
     * 云书架页面数据删除
     */
    public static final String URL_CLOUD_SHELF_DELETE_BOOK = "174";


    /**
     * 充值记录
     */
    public static final String URL_RECHARGE_RECORD = "134";

    /**
     * 代金券列表
     */
    public static final String URL_VOUCHERS_LIST = "135";

    /**
     * 礼品列表
     */
    public static final String URL_GIFT_LIST = "107";

    /**
     * 书籍消费记录汇总
     */
    public static final String URL_BOOKS_CONSUME_SUMMARY = "183";

    /**
     * 消费记录二级（活动，书籍，vip）
     */
    public static final String URL_CONSUME_SECOND_SUMMARY = "184";

    /**
     * 书籍消费记录三级接口
     */
    public static final String URL_CONSUME_THIRD_SUMMARY = "185";

    /**
     * VIP自动续费状态获取
     */
    public static final String URL_VIP_AUTO_RENEW_STATUS = "212";

    /**
     * 取消VIP自动续费
     */
    public static final String URL_VIP_CANCEL_AUTO_RENEW = "213";

    /**
     * 获取VIP连续开通历史
     */
    public static final String URL_CONTINUE_OPEN_HIS = "214";

    /**
     * 获取兑换礼品结果
     */
    public static final String GET_GIFT_EXCHANGE_CALL = "181";


    /**
     * 挂关闭
     */
    public static final String FINISH_TAKS = "141";

    /**
     * 排行榜
     */
    public static final String URL_RANK_TOP = "167";

    /**
     * 活动列表
     */
    public static final String URL_ACTIVITY_CENTER = "220";

    /**
     * h5领书
     * 打包定价
     */
    public static final String URL_H5_ADD_SHELF = "406";

    /**
     * 充值列表
     */
    public static final String RECHARGE_LIST = "131";

    /**
     * 我的VIP
     */
    public static final String URL_VIP_INFO = "192";
    /**
     * 我的VIP福利
     */
    public static final String URL_VIP_WELL = "200";


    /**
     * 笔记书签同步数据
     */
    public static final String URL_SYNC_BOOK_MARK = "121";

    /**
     * 查询老用户资产
     */
    public static final String URL_OLD_USER_ASSETS = "300";


    /**
     * 国内AGW服务域名
     */
    private static final String TMS_URL_CHINA = "https://terms1.hicloud.com/agreementservice/";
    /**
     * 新加坡AGW服务域名
     */
    private static final String TMS_URL_XINJIAPO = "https://terms3.hicloud.com/agreementservice/";
    /**
     * 欧洲AGW服务域名
     */
    private static final String TMS_URL_OUZHOU = "https://terms7.hicloud.com/agreementservice/";

    /**
     * 签署
     */
    private static final String TMS_URL_SIGN = "user";

    /**
     * 查询用户签署记录
     */
    private static final String TMS_URL_QUERY_RECORD = "user/api";

    /**
     * QA地址
     */
    private static final String COMMON_REQUEST_BASE_URL = "https://m.kuaikandushu.cn";

    private static String hostTest = "http://192.168.0.60:3080";
    /**
     * 测试切换地址
     */
    private static boolean urlTestSwitch = true;
    private static long urlTestOutTime = 0;

    /**
     * js回调的测试地址
     *
     * @return String
     */
    public static String getTestJsUrl() {
        return "http://111.202.124.250:4999/php/vip/callbacktest";
    }

    public static String[] getTestUrlPool() {
        return new String[]{"http://192.168.0.90:3080", "http://192.168.0.60:3080", "http://111.202.124.250:30809", "https://m.kuaikandushu.cn"};
    }

    /**
     * 签到url
     *
     * @return String
     */
    public static String urlSignIn() {
        return getHostBasic() + "/php/sign/index?_typeid_=sign0";
    }

    /**
     * 我的VIP页面
     *
     * @return String
     */
    public static String urlMyVip() {
        return getHostBasic() + "/php/vip/viphome?_typeid_=vip0";
    }

    /**
     * 我的阅读时长
     *
     * @return String
     */
    public static String urlMyReadTime() {
        return getHostBasic() + "/php/my/ydsc";
    }

    /**
     * 批量下载折扣
     *
     * @return String
     */
    public static String urlLotDownloadDiscount() {
        return getHostBasic() + "/php/vip/vipdiscount";
    }


    private static String getHostBasic() {
        return (testUrlGetLimit() > 0) ? hostTest : COMMON_REQUEST_BASE_URL;
    }

    /**
     * 关于我们用户协议
     *
     * @return String
     */
    public static String getUrlAgreement() {
        Resources resources = AppConst.getApp().getResources();
        return "https://consumer.huawei.com/minisite/cloudservice/huaweireader-dz/terms.htm?country=" + resources.getString(R.string.tms_country) + "&language=" + resources.getString(R.string.tms_language);
    }

    /**
     * 关于我们隐私声明
     *
     * @return String
     */
    public static String getUrlPrivacyPolicy() {
        Resources resources = AppConst.getApp().getResources();
        return "https://consumer.huawei.com/minisite/cloudservice/huaweireader-dz/privacy-statement.htm?country=" + resources.getString(R.string.tms_country) + "&language=" + resources.getString(R.string.tms_language);
    }

    /**
     * 代金券实物领取
     *
     * @return String
     */
    public static String urlObjectToReceive() {
        return getHostBasic() + "/php/vip/prizeform?gid=";
    }

    /**
     * 根据地区 判断用户的TMS协议服务器
     *
     * @return String
     */
    public static String getTmsUrlReqSign() {
        return TMS_URL_CHINA + TMS_URL_SIGN;
    }

    /**
     * 根据地区 返回用户查询签署的url
     *
     * @param type 2 查询用户签署 type=3 查询用户签署记录
     * @return str
     */
    public static String getTmsUrlReqQuery(int type) {
        if (type == 2) {
            return TMS_URL_CHINA + TMS_URL_SIGN;
        } else {
            return TMS_URL_CHINA + TMS_URL_QUERY_RECORD;
        }
    }

    /**
     * 获取下一页url
     *
     * @param url url
     * @return String
     */
    public static String getNextPageUrl(String url) {
        return getHostBasic() + url;
    }

    public static String getUrlBasic() {
        return getHostBasic() + "/glory/portal/";
    }

    static String getCallUrl(String call, String queryString) {
        return getHostBasic() + "/glory/portal/" + call + "?" + queryString;
    }

    static String getCallByUrl(String url, String queryString) {
        return url + "?" + queryString;
    }

    public static String getDzHost() {
        return Uri.parse(getHostBasic()).getHost();
    }

    /**
     * 设置测试URL，打开时设置过期时间为一小时以后。
     *
     * @param context 上下文
     * @param host    host
     * @param enable  enable
     */
    public static void testUrlSet(Context context, String host, boolean enable) {
        urlTestSwitch = enable;
        SpUtil.getinstance(context).setBoolean("url.test.sw", urlTestSwitch);
        if (enable) {
            urlTestOutTime = System.currentTimeMillis() + 5 * 3600000L;
            SpUtil.getinstance(context).setLong("url.test.outtime", urlTestOutTime);
            if (!TextUtils.isEmpty(host)) {
                SpUtil.getinstance(context).setString("url.test.action", host);
                hostTest = host;
            }
        }
    }

    /**
     * 获取过期时间，为负值时，代表使用现网。正值代表正在使用测试URL。
     *
     * @return String
     */
    public static Long testUrlGetLimit() {
        if (urlTestSwitch) {
            return (urlTestOutTime - System.currentTimeMillis()) / 1000;
        } else {
            return -1L;
        }
    }

    /**
     * 应用启动时，初始化测试URL开关。
     *
     * @param context 上下文
     */
    public static void testUrlInit(Context context) {
        urlTestSwitch = SpUtil.getinstance(context).getBoolean("url.test.sw", false);
        urlTestOutTime = SpUtil.getinstance(context).getLong("url.test.outtime", 0L);
        hostTest = SpUtil.getinstance(context).getString("url.test.action", "101.200.193.169:3080");
    }

    /**
     * 跳转到新手礼包
     *
     * @return String
     */
    public static String urlNewGift() {
        return getHostBasic() + "/php/gift/newgift";
    }
}
