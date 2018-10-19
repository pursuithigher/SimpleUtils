package com.dzbook.log;

import android.support.annotation.IntDef;
import android.support.annotation.StringDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * LogConstants
 *
 * @author wxliao on 17/7/3.
 */

public class LogConstants {
    /************************华为打点**************************/

    public static final String USERID = "userid";
    /**
     * 网络类型
     */
    public static final String NET_TYPE = "netType";
    /**
     * 来源
     */
    public static final String FROM = "from";
    /**
     * to
     */
    public static final String TO = "to";

    /**
     * 阅读
     */
    public static final String TO_READER = "reader";
    /**
     * 搜索
     */
    public static final String TO_SEARCH = "search";
    /**
     * 推荐
     */
    public static final String TO_RECOMMEND = "store";
    /**
     * 签到
     */
    public static final String TO_SIGN = "sign";


    /**
     * 一级导航栏ID
     */
    public static final String TABID = "tabId";
    /**
     * 一级导航栏名称
     */
    public static final String TABNAME = "tabName";
    /**
     * 一级导航栏位置顺序
     */
    public static final String TABPOS = "tabPos";
    /**
     * 频道ID
     */
    public static final String PAGEID = "pageId";
    /**
     * 频道名称
     */
    public static final String PAGENAME = "pageName";
    /**
     * 频道位置
     */
    public static final String PAGEPOS = "pagePos";
    /**
     * 栏目ID
     */
    public static final String COLUMEID = "columeID";
    /**
     * 栏目名称
     */
    public static final String COLUMENAME = "columeName";
    /**
     * 栏目位置
     */
    public static final String COLUMEPOS = "columePos";
    /**
     * 栏目类型
     */
    public static final String COLUMETEMP = "columeTemp";
    /**
     * 内容ID
     */
    public static final String CONTENTID = "contentID";
    /**
     * 内容名称
     */
    public static final String CONTENTNAME = "contentName";
    /**
     * 内容类型
     */
    public static final String CONTENTTYPE = "contentType";

    /**
     * 书名
     */
    public static final String HW_NAME = "name";
    /**
     * id
     */
    public static final String HW_BOOK_ID = "bookid";
    /**
     * HW_ID
     */
    public static final String HW_ID = "ID";

    /**
     * PERIOD
     */
    public static final String PERIOD = "period";
    /**
     * 结束时间
     */
    public static final String END_DATE = "endDate";
    /**
     * 开始日期
     */
    public static final String START_DATE = "startDate";
    /**
     * 打开时间
     */
    public static final String OPEN_TIME = "openTime";
    /**
     * 关闭时间
     */
    public static final String CLOSE_TIME = "closeTime";
    /**
     * 阅读时长（秒）
     */
    public static final String READER_TIME = "time";
    /**
     * 阅读章节数
     */
    public static final String CHAPTER_AMOUNT = "chapterAmount";
    /**
     * 是否自动
     */
    public static final String IS_AUTO = "is_auto";

    /**
     * 购买方式 1：全本；2：批量章节；3：单章
     */
    public static final String BUYT_YPE = "buyType";
    /**
     * 购买数量  全本和单章数量为1，批量章节为具体章节数
     */
    public static final String BUY_AMOUNT = "buyAmount";
    /**
     * 付费总金额  单位：元（虚拟币折算成元）
     */
    public static final String MONEY = "money";
    /**
     * 真实消费金额  使用充值的虚拟币消费金额，如不涉及，值为0
     */
    public static final String VIRTUAL = "virtual";
    /**
     * 现金消费金额  直接使用现金消费金额，如不涉及，值为0
     */
    public static final String CASH = "cash";

    /**
     * 赠送代金券数量
     */
    public static final String COUPON = "coupon";

    /**
     * 签到类型  1：当天签到；2：补签
     */
    public static final String TYPE = "type";
    /**
     * 签到所获奖励
     */
    public static final String AWARD = "award";
    /**
     * 用户性别
     */
    public static final String GENDER = "gender";


    /****************************** 栏目名称*********************/
    /**
     * banner
     */
    public static final String COLUME_NAME_BANNER = "banner";
    /**
     * 分类
     */
    public static final String COLUME_NAME_CLASSIFY = "classify";
    /****************************** 栏目名称*********************/

    /****************************** 栏目类型*********************/
    /**
     * banner
     */
    public static final String COLUME_TEMP_BANNER = "banner";
    /**
     * 分类
     */
    public static final String COLUME_TEMP_CLASSIFY = "classify";
    /**
     * 书籍
     */
    public static final String COLUME_TEMP_BOOK = "book";
    /****************************** 栏目类型*********************/

    /****************************** 内容id/内容名称*********************/
    /**
     * 分类我的vip
     */
    public static final String COLUME_ID_MYVIP = "myvip";

    /**
     * 分类玄幻
     */
    public static final String COLUME_ID_XH = "flxh";


    /**
     * 分类全部
     */
    public static final String COLUME_ID_VIPFL = "flqb";


    /****************************** 内容id/内容名称*********************/


    /****************************** 内容类型*********************/
    /**
     * 书籍
     */
    public static final String CONTENT_TEMP_BOOK = "content_book";
    /**
     * 活动
     */
    public static final String CONTENT_TEMP_ACTIVITY = "content_activity";

    /**
     * 更多
     */
    public static final String CONTENT_TEMP_MORE = "content_more";

    /**
     * 书籍列表
     */
    public static final String CONTENT_BOOK_LIST = "content_booklist";

    /**
     * vipinfo
     */
    public static final String CONTENT_VIP_INFO = "content_vipinfo";

    /****************************** 内容类型*********************/

    /************************华为打点**************************/

    //===========================打点类型：Tag常量==================================

    public static final int TAG_LAUNCH = 100;

    /**
     * PV
     */
    public static final int TAG_PV = 101;

    /**
     * CLICK
     */
    public static final int TAG_CLICK = 102;

    /**
     * EVENT
     */
    public static final int TAG_EVENT = 103;


    //===========================启动日志：启动方式常量==================================


    /**
     * 打开方式，直接打开
     */
    public static final int LAUNCH_DIRECT = 1;

    /**
     * push 拉起
     */
    public static final int LAUNCH_PUSH = 2;

    /**
     * 打开方式，三方唤起
     */
    public static final int LAUNCH_THIRD = 3;

    /**
     * 打开方式，覆盖安装
     */
    public static final int LAUNCH_COVER = 4;

    /**
     * 打开方式，初始安装
     */
    public static final int LAUNCH_FIRST = 5;

    /**
     * 打开方式，后台重新唤起
     */
    public static final int LAUNCH_BACKGROUND = 6;

    /**
     * 本地文件打开
     */
    public static final int LAUNCH_LOCAL_FILE = 7;

    /**
     * 全局搜索打开
     */
    public static final int LAUNCH_GLOBAL_SEARCH = 8;

    /**
     * IntDef
     */
    @IntDef({LAUNCH_DIRECT, LAUNCH_THIRD, LAUNCH_COVER, LAUNCH_FIRST, LAUNCH_BACKGROUND, LAUNCH_GLOBAL_SEARCH})
    @Retention(RetentionPolicy.SOURCE)
    public @interface LaunchSm {

    }

    //=========================点击日志：module常量====================================


    /**
     * 书架
     */
    public static final String MODULE_SJ = "sj";

    /**
     * 书城
     */
    public static final String MODULE_SC = "sc";

    /**
     * 本地书城
     */
    public static final String MODULE_NSC = "nsc";

    /**
     * 本地书城限免子页面
     */
    public static final String MODULE_NSCXMZYM = "nscxmzym";

    /**
     * 本地书城子页面
     */
    public static final String MODULE_NSCZYM = "nsczym";

    /**
     * 分类
     */
    public static final String MODULE_FL = "fl";

    /**
     * 发现
     */
    public static final String MODULE_FX = "fx";

    /**
     * 书籍详情
     */
    public static final String MODULE_SJXQ = "sjxq";

    /**
     * 阅读器
     */
    public static final String MODULE_YDQ = "ydq";

    /**
     * 阅读器目录
     */
    public static final String MODULE_YDQML = "ydqml";

    /**
     * 阅读器分享
     */
    public static final String MODULE_YDQFX = "ydqfx";

    /**
     * 搜索页面
     */
    public static final String MODULE_SSYM = "ssym";

    /**
     * 搜索结果页面
     */
    public static final String MODULE_SSJGYM = "ssjgym";

    /**
     * Main2Activity页
     */
    public static final String MODULE_MAIN = "main";

    /**
     * 订购页面
     */
    public static final String MODULE_DG_DZ = "dgdz";

    /**
     * 订购页面
     */
    public static final String MODULE_DG_SELL = "dgsell";

    /**
     * 充值页面
     */
    public static final String MODULE_CZ = "cz";

    /**
     * 我的页面
     */
    public static final String MODULE_WD = "wd";

    /**
     * 引导页面
     */
    public static final String MODULE_YDYM = "ydym";
    /**
     * 用户偏好设置
     */
    public static final String MODULE_PHSZ = "phsz";

    /**
     * 客户端缺内容页面
     */
    public static final String MODULE_QNR = "qnr";

    /**
     * 自有登录
     */
    public static final String MODULE_ZYDL = "zydl";

    /**
     * 手机号登录
     */
    public static final String MODULE_SJHDL = "sjhdl";

    /**
     * 书籍全部评论
     */
    public static final String MODULE_QBPL = "qbpl";


    /**
     * 书籍评论详情
     */
    public static final String MODULE_PLXQ = "plxq";

    /**
     * 我的点评
     */
    public static final String MODULE_WDDP = "wddp";

    /**
     * 发送评论
     */
    public static final String MODULE_FSPL = "fspl";

    /**
     * 系统设置
     */
    public static final String MODULE_XTSZ = "xtsz";

    /**
     * 系统设置
     */
    public static final String MODULE_VIPZFYM = "vipzfym";

    /**
     * 客户端升级
     */
    public static final String MODULE_KFDSJ = "kfdsj";

    /**
     * 分类一级页面
     */
    public static final String MODULE_FLYJ = "flyj";

    /**
     * 分类二级页面顶部（top）
     */
    public static final String MODULE_FLEJT = "flejt";

    /**
     * 分类二级页面底部（bottom）书籍列表部分
     */
    public static final String MODULE_FLEJB = "flejb";

    /**
     * 排行榜top部分
     */
    public static final String MODULE_PHBT = "phbt";

    /**
     * 排行榜bottom部分
     */
    public static final String MODULE_PHBB = "phbb";

    /**
     * 阅读器追更推荐
     */
    public static final String MODULE_YDQZGTJ = "ydqzgtj";

    /**
     * 阅读器追更推荐更多
     */
    public static final String MODULE_YDQZGTJGD = "ydqzgtjgd";

    /**
     * 设置通知
     */
    public static final String MODULE_SZTZ = "module_sztz";

    /**
     * 礼品中心
     */
    public static final String MODULE_LPZX = "lpzx";

    /**
     * 智能导书
     */
    public static final String PV_ZNDS = "znds";

    /**
     * 本地目录
     */
    public static final String PV_BDML = "bdml";

    /**
     * 礼品兑换
     */
    public static final String PV_LPDH = "lpdh";

    /**
     * 我的礼物
     */
    public static final String PV_WDLW = "wdlw";

    /**
     * 设置通知。
     */
    public static final String PV_SZTZ = "sztz";

    /**
     * StringDef
     */
    @StringDef({MODULE_SJ, MODULE_SC, MODULE_NSC, MODULE_NSCXMZYM, MODULE_NSCZYM, MODULE_FL, MODULE_FX, MODULE_SJXQ, MODULE_YDQ, MODULE_YDQML, MODULE_YDQFX, MODULE_SSYM, MODULE_SSJGYM, MODULE_DG_DZ, MODULE_CZ, MODULE_WD, MODULE_YDYM, MODULE_PHSZ, MODULE_MAIN, MODULE_QNR, MODULE_ZYDL, MODULE_SJHDL, MODULE_DG_SELL,

            //评论
            MODULE_QBPL, MODULE_PLXQ, MODULE_WDDP, MODULE_FSPL, MODULE_XTSZ, MODULE_KFDSJ,

            MODULE_VIPZFYM, MODULE_FLYJ, MODULE_FLEJT, MODULE_FLEJB, MODULE_PHBT, MODULE_PHBB, MODULE_YDQZGTJ, MODULE_YDQZGTJGD,
            //
            //            CLASS_PLXZ,
            //            CLASS_TSDD
            MODULE_SZTZ

    })
    @Retention(RetentionPolicy.SOURCE)
    public @interface Module {

    }

    //=========================点击日志：zone常量====================================

    /**
     * zone常量：mfzq
     */
    public static final String ZONE_SJ_MFZQ = "mfzq";
    /**
     * zone常量：pmd
     */
    public static final String ZONE_SJ_PMD = "pmd";
    /**
     * zone常量：qd
     */
    public static final String ZONE_SJ_QD = "qd";
    /**
     * zone常量：book
     */
    public static final String ZONE_SJ_BOOK = "book";
    /**
     * zone常量：tjlq
     */
    public static final String ZONE_SJ_TJLQ = "tjlq";
    /**
     * zone常量：xszx
     */
    public static final String ZONE_SJ_XSZX = "xszx";
    /**
     * zone常量：sjtchd
     */
    public static final String ZONE_SJ_SJTCHD = "sjtchd";
    /**
     * zone常量：banner
     */
    public static final String ZONE_SC_BANNER = "banner";
    /**
     * zone常量：pmd
     */
    public static final String ZONE_SC_PMD = "pmd";
    /**
     * zone常量：menu
     */
    public static final String ZONE_SC_MENU = "menu";
    /**
     * zone常量：pdsj
     */
    public static final String ZONE_SC_PDSJ = "pdsj";
    /**
     * zone常量：pdgd
     */
    public static final String ZONE_SC_PDGD = "pdgd";

    /**
     * 新书城：nscss
     */
    public static final String ZONE_NSC_NSCSS = "nscss";
    /**
     * 新书城：nscfl
     */
    public static final String ZONE_NSC_NSCFL = "nscfl";
    /**
     * 新书城：pd0
     */
    public static final String ZONE_NSC_PD0 = "pd0";
    /**
     * 新书城：pd1
     */
    public static final String ZONE_NSC_PD1 = "pd1";
    /**
     * 新书城：bn0
     */
    public static final String ZONE_NSC_BN0 = "bn0";
    /**
     * 新书城：bn1
     */
    public static final String ZONE_NSC_BN1 = "bn1";
    /**
     * 新书城：fl0
     */
    public static final String ZONE_NSC_FL0 = "fl0";
    /**
     * 新书城：zt0
     */
    public static final String ZONE_NSC_ZT0 = "zt0";
    /**
     * 新书城：xm0
     */
    public static final String ZONE_NSC_XM0 = "xm0";
    /**
     * 新书城：sj0
     */
    public static final String ZONE_NSC_SJ0 = "sj0";
    /**
     * 新书城：sj1
     */
    public static final String ZONE_NSC_SJ1 = "sj1";
    /**
     * 新书城：sj2
     */
    public static final String ZONE_NSC_SJ2 = "sj2";
    /**
     * 新书城：sj3
     */
    public static final String ZONE_NSC_SJ3 = "sj3";
    /**
     * 新书城：db0
     */
    public static final String ZONE_NSC_DB0 = "db0";
    /**
     * 新书城：db1
     */
    public static final String ZONE_NSC_DB1 = "db1";
    /**
     * 新书城：vpt0
     */
    public static final String ZONE_NSC_VPT0 = "vpt0";
    /**
     * 新书城：xslb
     */
    public static final String ZONE_NSC_XSLB = "xslb";
    /**
     * 分类：rmfl
     */
    public static final String ZONE_FL_RMFL = "rmfl";
    /**
     * 分类：boy
     */
    public static final String ZONE_FL_BOY = "boy";
    /**
     * 分类：girl
     */
    public static final String ZONE_FL_GIRL = "girl";
    /**
     * 分类：phb
     */
    public static final String ZONE_FL_PHB = "phb";

    /**
     * banner
     */
    public static final String ZONE_FX_BANNER = "banner";
    /**
     * menu
     */
    public static final String ZONE_FX_MENU = "menu";

    /**
     * dz
     */
    public static final String ZONE_SJXQ_DZ = "dz";
    /**
     * qxdz
     */
    public static final String ZONE_SJXQ_QXDZ = "qxdz";
    /**
     * fx
     */
    public static final String ZONE_SJXQ_FX = "fx";
    /**
     * qtsj
     */
    public static final String ZONE_SJXQ_QTSJ = "qtsj";
    /**
     * hzk
     */
    public static final String ZONE_SJXQ_HZK = "hzk";
    /**
     * plxz
     */
    public static final String ZONE_SJXQ_PLXZ = "plxz";
    /**
     * mfsd
     */
    public static final String ZONE_SJXQ_MFSD = "mfsd";
    /**
     * jrsj
     */
    public static final String ZONE_SJXQ_JRSJ = "jrsj";
    /**
     * vipsell
     */
    public static final String ZONE_SJXQ_VIPSELL = "vipsell";


    /**
     * jrsj_qd
     */
    public static final String ZONE_YDQ_JRSJ_QD = "jrsj_qd";
    /**
     * jrsj_qx
     */
    public static final String ZONE_YDQ_JRSJ_QX = "jrsj_qx";
    /**
     * jrsj_ff
     */
    public static final String ZONE_YDQ_JRSJ_FF = "jrsj_ff";
    /**
     * ydcz
     */
    public static final String ZONE_YDQ_YDCZ = "ydcz";

    /**
     * zj
     */
    public static final String ZONE_YDQML_ZJ = "zj";
    /**
     * sq
     */
    public static final String ZONE_YDQML_SQ = "sq";
    /**
     * bj
     */
    public static final String ZONE_YDQML_BJ = "bj";

    /**
     * gb
     */
    public static final String ZONE_YDQFX_GB = "gb";
    /**
     * wxpyq
     */
    public static final String ZONE_YDQFX_WXPYQ = "wxpyq";
    /**
     * wxhy
     */
    public static final String ZONE_YDQFX_WXHY = "wxhy";
    /**
     * qqhy
     */
    public static final String ZONE_YDQFX_QQHY = "qqhy";

    /**
     * rmss
     */
    public static final String ZONE_SSYM_RMSS = "rmss";
    /**
     * lxss
     */
    public static final String ZONE_SSYM_LXSS = "lxss";
    /**
     * mrcss
     */
    public static final String ZONE_SSYM_MRCSS = "mrcss";
    /**
     * cgss
     */
    public static final String ZONE_SSYM_CGSS = "cgss";

    /**
     * bqss
     */
    public static final String ZONE_SSYM_BQSS = "bqss";
    /**
     * zzss
     */
    public static final String ZONE_SSYM_ZZSS = "zzss";
    /**
     * sjss
     */
    public static final String ZONE_SSYM_SJSS = "sjss";
    /**
     * qyd
     */
    public static final String ZONE_SSYM_QYD = "qyd";


    /**
     * bqpp
     */
    public static final String ZONE_SSJGYM_BQPP = "bqpp";
    /**
     * bqpp_qb
     */
    public static final String ZONE_SSJGYM_BQPP_QB = "bqpp_qb";
    /**
     * mzpp
     */
    public static final String ZONE_SSJGYM_MZPP = "mzpp";
    /**
     * zt
     */
    public static final String ZONE_SSJGYM_ZT = "zt";
    /**
     * tjsj
     */
    public static final String ZONE_SSJGYM_TJSJ = "tjsj";

    /**
     * sj
     */
    public static final String ZONE_MAIN_SJ = "sj";
    /**
     * tj
     */
    public static final String ZONE_MAIN_TJ = "tj";
    /**
     * fl
     */
    public static final String ZONE_MAIN_FL = "fl";
    /**
     * sc
     */
    public static final String ZONE_MAIN_SC = "sc";
    /**
     * wd
     */
    public static final String ZONE_MAIN_WD = "wd";
    /**
     * mf
     */
    public static final String ZONE_MAIN_MF = "mf";


    /**
     * 1
     */
    public static final String ZONE_DG_1 = "1";
    /**
     * 2
     */
    public static final String ZONE_DG_2 = "2";
    /**
     * 3
     */
    public static final String ZONE_DG_3 = "3";

    /**
     * subtype
     */
    public static final String ZONE_CZ_SUBTYPE = "subtype";
    /**
     * gb
     */
    public static final String ZONE_CZ_GB = "gb";

    /**
     * tx
     */
    public static final String ZONE_WD_TX = "tx";
    /**
     * dj
     */
    public static final String ZONE_WD_DJ = "dj";
    /**
     * dl
     */
    public static final String ZONE_WD_DL = "dl";
    /**
     * wdzh
     */
    public static final String ZONE_WD_WDZH = "wdzh";
    /**
     * wdcz
     */
    public static final String ZONE_WD_WDCZ = "wdcz";
    /**
     * ysj
     */
    public static final String ZONE_WD_YSJ = "ysj";
    /**
     * yjms
     */
    public static final String ZONE_WD_YJMS = "yjms";
    /**
     * hyms
     */
    public static final String ZONE_WD_HYMS = "hyms";
    /**
     * xtsz
     */
    public static final String ZONE_WD_XTSZ = "xtsz";
    /**
     * zhaq
     */
    public static final String ZONE_WD_ZHAQ = "zhaq";
    /**
     * xsbz
     */
    public static final String ZONE_WD_XSBZ = "xsbz";
    /**
     * lxkf
     */
    public static final String ZONE_WD_LXKF = "lxkf";
    /**
     * sx
     */
    public static final String ZONE_WD_SX = "sx";
    /**
     * dp
     */
    public static final String ZONE_WD_DP = "dp";
    /**
     * wdvip
     */
    public static final String ZONE_WD_WDVIP = "wdvip";
    /**
     * vipkt
     */
    public static final String ZONE_WD_VIPKT = "vipkt";
    /**
     * vipxf
     */
    public static final String ZONE_WD_VIPXF = "vipxf";
    /**
     * ZONE_WD_SQZC
     */
    public static final String ZONE_WD_SQZC = "sjzc";
    /**
     * ZONE_WD_ZYZC
     */
    public static final String ZONE_WD_ZYZC = "zyzc";

    /**
     * yhph
     */
    public static final String ZONE_YDYM_YHPH = "yhph";

    /**
     * 缺内容页面
     */
    public static final String ZONE_QNR_LQ = "lq";
    /**
     * xyz
     */
    public static final String ZONE_QNR_XYZ = "xyz";

    //自有登录
    /**
     * qq登录
     */
    public static final String ZONE_QQDL = "qqdl";
    /**
     * 微信登录
     */
    public static final String ZONE_WXDL = "wxdl";
    /**
     * 微博登录
     */
    public static final String ZONE_WBDL = "wbdl";
    /**
     * 手机登录
     */
    public static final String ZONE_SJDL = "sjdl";
    /**
     * 华为帐号登录
     */
    public static final String ZONE_HWDL = "hwdl";
    /**
     * 中国移动帐号登录
     */
    public static final String ZONE_CMCCDL = "cmccdl";

    /**
     * 获取验证码
     */
    public static final String ZONE_HQYZM = "hqyzm";

    /**
     * 发送评论
     */
    public static final String ZONE_FSPL = "fspl";
    /**
     * 全部评论
     */
    public static final String ZONE_QBPL = "qbpl";
    /**
     * 评论详情
     */
    public static final String ZONE_PLXQ = "plxq";
    /**
     * 编辑评论
     */
    public static final String ZONE_PLBJ = "bjpl";
    /**
     * 删除评论
     */
    public static final String ZONE_PLSC = "scpl";
    /**
     * 提交评论
     */
    public static final String ZONE_TJPL = "tjpl";

    /**
     * 取消自动购买VIP
     */
    public static final String ZONE_XTSZ_QXZDGMVIP = "qxzdgmvip";

    /**
     * 1
     */
    public static final String ZONE_KFDSJ_1 = "1";
    /**
     * 2
     */
    public static final String ZONE_KFDSJ_2 = "2";
    /**
     * 3
     */
    public static final String ZONE_KFDSJ_3 = "3";

    /**
     * 排行榜一级分类
     */
    public static final String ZONE_PHBYJFL = "phbyjfl";
    /**
     * 排行榜二级分类
     */
    public static final String ZONE_PHBEJFL = "phbejfl";

    /**
     * 阅读器追更推荐
     */
    public static final String ZONE_TLXSJ = "tlxsj";
    /**
     * zzqtsj
     */
    public static final String ZONE_ZZQTSJ = "zzqtsj";
    /**
     * viprmsj
     */
    public static final String ZONE_VIPRMSJ = "viprmsj";
    /**
     * gd_tlxsj
     */
    public static final String ZONE_GD_TLXSJ = "gd_tlxsj";
    /**
     * gd_zzqtsj
     */
    public static final String ZONE_GD_ZZQTSJ = "gd_zzqtsj";
    /**
     * gd_viprmsj
     */
    public static final String ZONE_GD_VIPRMSJ = "gd_viprmsj";

    /**
     * 设置通知权限
     */
    public static final String ZONE_SZTZ_QSZ = "sztz_qsz";

    /**
     * sztz_qx
     */
    public static final String ZONE_SZTZ_QX = "sztz_qx";

    /**
     * 礼品中心
     */
    public static final String ZONE_LPZX_DH = "lpzxdh";

    /**
     * StringDef
     */
    @StringDef({
            //书架页面相关的zone
            ZONE_SJ_MFZQ, ZONE_SJ_PMD, ZONE_SJ_QD, ZONE_SJ_BOOK, ZONE_SJ_TJLQ, ZONE_SJ_XSZX, ZONE_SJ_SJTCHD,

            //书城页面相关的zone
            ZONE_SC_BANNER, ZONE_SC_PMD, ZONE_SC_MENU, ZONE_SC_PDSJ, ZONE_SC_PDGD, ZONE_NSC_VPT0,

            //新书城页面相关的zone
            ZONE_NSC_NSCSS, ZONE_NSC_NSCFL, ZONE_NSC_PD0, ZONE_NSC_PD1, ZONE_NSC_BN0, ZONE_NSC_BN1, ZONE_NSC_FL0, ZONE_NSC_ZT0, ZONE_NSC_XM0, ZONE_NSC_SJ0, ZONE_NSC_SJ1, ZONE_NSC_SJ2, ZONE_NSC_SJ3, ZONE_NSC_DB0, ZONE_NSC_DB1, ZONE_NSC_XSLB,

            //分类页面相关的zone
            ZONE_FL_RMFL, ZONE_FL_BOY, ZONE_FL_GIRL, ZONE_FL_PHB,

            //发现页面相关的zone
            ZONE_FX_BANNER, ZONE_FX_MENU,

            //书籍详情页面相关的zone
            ZONE_SJXQ_DZ, ZONE_SJXQ_QXDZ, ZONE_SJXQ_FX, ZONE_SJXQ_QTSJ, ZONE_SJXQ_HZK, ZONE_SJXQ_PLXZ, ZONE_SJXQ_MFSD, ZONE_SJXQ_JRSJ, ZONE_SJXQ_VIPSELL,

            //阅读器页面相关的zone
            ZONE_YDQ_JRSJ_QD, ZONE_YDQ_JRSJ_QX, ZONE_YDQ_YDCZ,

            //阅读器目录页面
            ZONE_YDQML_BJ, ZONE_YDQML_SQ, ZONE_YDQML_ZJ,

            //阅读器分享
            ZONE_YDQFX_GB, ZONE_YDQFX_WXPYQ, ZONE_YDQFX_WXHY, ZONE_YDQFX_QQHY,

            //搜索页面相关的zone
            ZONE_SSYM_RMSS, ZONE_SSYM_LXSS, ZONE_SSYM_MRCSS, ZONE_SSYM_CGSS, ZONE_SSYM_BQSS, ZONE_SSYM_ZZSS, ZONE_SSYM_SJSS, ZONE_SSYM_QYD,

            //搜索结果页面相关的zone
            ZONE_SSJGYM_BQPP, ZONE_SSJGYM_BQPP_QB, ZONE_SSJGYM_MZPP, ZONE_SSJGYM_ZT, ZONE_SSJGYM_TJSJ,

            //Main2Activity相关
            ZONE_MAIN_SJ, ZONE_MAIN_TJ, ZONE_MAIN_FL, ZONE_MAIN_SC, ZONE_MAIN_WD,
            //订购页面相关的zone
            ZONE_DG_1, ZONE_DG_2, ZONE_DG_3,
            //充值页面相关zone
            ZONE_CZ_SUBTYPE, ZONE_CZ_GB,

            //我的页面相关的zone
            ZONE_WD_TX, ZONE_WD_DJ, ZONE_WD_DL, ZONE_WD_WDZH, ZONE_WD_WDCZ, ZONE_WD_YSJ, ZONE_WD_YJMS, ZONE_WD_XTSZ, ZONE_WD_ZHAQ, ZONE_WD_XSBZ, ZONE_WD_LXKF, ZONE_WD_SX, ZONE_WD_DP, ZONE_WD_WDVIP, ZONE_WD_VIPKT, ZONE_WD_VIPXF, ZONE_WD_ZYZC, ZONE_WD_SQZC,

            //引导页面相关的zone
            ZONE_YDYM_YHPH,
            //章节付费后加入书架
            ZONE_YDQ_JRSJ_FF,
            //缺内容页面
            ZONE_QNR_LQ, ZONE_QNR_XYZ,

            //自有登录
            ZONE_QQDL, ZONE_WXDL, ZONE_WBDL, ZONE_SJDL, ZONE_HWDL, ZONE_HQYZM, ZONE_CMCCDL,

            //评论
            ZONE_FSPL, ZONE_QBPL, ZONE_PLXQ, ZONE_PLBJ, ZONE_PLSC, ZONE_TJPL,

            ZONE_XTSZ_QXZDGMVIP, ZONE_KFDSJ_1, ZONE_KFDSJ_2, ZONE_KFDSJ_3, ZONE_PHBYJFL, ZONE_TLXSJ, ZONE_ZZQTSJ, ZONE_VIPRMSJ, ZONE_GD_TLXSJ, ZONE_GD_ZZQTSJ, ZONE_GD_VIPRMSJ, ZONE_PHBEJFL,

            ZONE_SZTZ_QSZ, ZONE_SZTZ_QX


    })
    @Retention(RetentionPolicy.SOURCE)
    public @interface Zone {

    }

    //==========================自定义事件===================================

    /**
     * czjg
     */
    public static final String EVENT_CZJG = "czjg";
    /**
     * gxzc
     */
    public static final String EVENT_GXZC = "gxzc";
    /**
     * phszhc
     */
    public static final String EVENT_PHSZHC = "phszhc";
    /**
     * dljg
     */
    public static final String EVENT_DLJG = "dljg";
    /**
     * qnr
     */
    public static final String EVENT_QNR = "qnr";
    /**
     * htsz
     */
    public static final String EVENT_HTSZ = "htsz";
    /**
     * 追更推送到达
     */
    public static final String EVENT_ZGTSDD = "zgtsdd";
    /**
     * 追更推送点击
     */
    public static final String EVENT_ZGTSDJ = "zgtsdj";
    /**
     * 追更推送记录
     */
    public static final String EVENT_ZGTSJL = "zgtsjl";
    /**
     * 追更推送记录
     */
    public static final String EVENT_WEBAPKXZ = "webapkxz";
    /**
     * 新手专享展示
     */
    public static final String EVENT_XSZXZS = "xszxzs";
    /**
     * 删除书籍
     */
    public static final String EVENT_SCSJ = "scsj";
    /**
     * 书架书籍
     */
    public static final String EVENT_SJSJ = "sjsj";
    /**
     * 发起升级
     */
    public static final String EVENT_FQSJ = "fqsj";
    /**
     * 下载内容失败
     */
    public static final String EVENT_XZNRSB = "xznrsb";
    /**
     * 重试下载内容失败
     */
    public static final String EVENT_CSXZNRSB = "csxznrsb";
    /**
     * 广告打点
     */
    public static final String EVENT_ADINFO = "adinfo";
    /**
     * logo action打点
     */
    public static final String EVENT_LOGOACTION = "logoaction";
    /**
     * 引导页阅读分类
     */
    public static final String EVENT_YDYYDFL = "ydyydfl";
    /**
     * 258接口数据下发是否成功
     */
    public static final String EVENT_258DTXFJG = "258_dtxfjg";
    /**
     * 259接口数据下发是否成功
     */
    public static final String EVENT_259DTXFJG = "259_dtxfjg";
    /**
     * 主页面tab
     */
    public static final String EVENT_MAIN_TAB = "maintab";


    /**
     * ydq_tts_a
     */
    public static final String EVENT_YDQ_TTS_A = "ydq_tts_a";
    /**
     * ydq_tts_b
     */
    public static final String EVENT_YDQ_TTS_B = "ydq_tts_b";
    /**
     * cjgl_tts_a
     */
    public static final String EVENT_CJGL_TTS_A = "cjgl_tts_a";
    /**
     * cjgl_tts_b
     */
    public static final String EVENT_CJGL_TTS_B = "cjgl_tts_b";
    /**
     * vip 兑换弹窗
     */
    public static final String EVENT_VIP_DHTC = "vip_dhtc";

    /**
     * 剪切板 或者是h5唤起 打开书
     */
    public static final String EVENT_CLIP_OPEN = "clip_open_book";


    /************************** 客户端优化事件*************************/
    public static final String EVENT_APP_STARTTIME = "astti";

    /************************** tinker事件  **************/
    public static final String EVENT_THINKER_MSG = "tinker_msg";

    /************************** crash回传打点  **************/
    public static final String EVENT_CRASH = "crash";

    /**
     * StringDef
     */
    @StringDef({EVENT_CZJG, EVENT_PHSZHC, EVENT_GXZC, EVENT_DLJG, EVENT_QNR, EVENT_ZGTSDD, EVENT_ZGTSDJ, EVENT_ZGTSJL, EVENT_HTSZ, EVENT_WEBAPKXZ, EVENT_XSZXZS, EVENT_SCSJ, EVENT_SJSJ, EVENT_FQSJ, EVENT_XZNRSB, EVENT_ADINFO, EVENT_YDYYDFL, EVENT_YDQ_TTS_A, EVENT_YDQ_TTS_B, EVENT_CJGL_TTS_A, EVENT_CJGL_TTS_B, EVENT_YDYYDFL, EVENT_258DTXFJG, EVENT_259DTXFJG, EVENT_LOGOACTION, EVENT_CSXZNRSB, EVENT_MAIN_TAB, EVENT_VIP_DHTC, EVENT_CLIP_OPEN, EVENT_APP_STARTTIME, EVENT_THINKER_MSG, EVENT_CRASH})
    @Retention(RetentionPolicy.SOURCE)
    public @interface Event {

    }


    //==========================搜索==========================

    /**
     * key
     */
    public static final String KEY_SEARCHRESULT_KEYWORD = "kw";
    /**
     * type
     */
    public static final String KEY_SEARCHRESULT_KEYTYPE = "type";
    /**
     * result
     */
    public static final String KEY_SEARCHRESULT_RESULTTYPE = "result";
    /**
     * SearchResult
     */
    public static final String KEY_SEARCHRESULT_PYTYPE = "SearchResult";
    /**
     * bid
     */
    public static final String KEY_SEARCHRESULT_BID = "bid";
    /**
     * index
     */
    public static final String KEY_SEARCHRESULT_INDEX = "index";
    /**
     * id
     */
    public static final String KEY_SEARCHRESULT_ID = "id";
    /**
     * title
     */
    public static final String KEY_SEARCHRESULT_TITLE = "title";


    //==========================搜索==========================

    /**
     * sjms
     */
    public static final String KEY_BOOK_SHELF_SJMS = "sjms";

    //==========================充值方式==========================

    /**
     * money
     */
    public static final String KEY_RECHARGE_MONEY = "money";
    /**
     * bid
     */
    public static final String KEY_RECHARGE_BID = "bid";

    //==========================充值方式==========================


    //==========================订购，充值==========================
    /**
     * 操作来源详情值
     * 1，详情批量
     */
    public static final String ORDER_SOURCE_FROM_VALUE_1 = "1";
    /**
     * 操作来源详情值
     * 2，详情目录
     */
    public static final String ORDER_SOURCE_FROM_VALUE_2 = "2";
    /**
     * 操作来源详情值
     * 3，详情最新章节
     */
    public static final String ORDER_SOURCE_FROM_VALUE_3 = "3";
    /**
     * 操作来源详情值
     * 4，阅读菜单批量
     */
    public static final String ORDER_SOURCE_FROM_VALUE_4 = "4";
    /**
     * 操作来源详情值
     * 5，阅读目录
     */
    public static final String ORDER_SOURCE_FROM_VALUE_5 = "5";
    /**
     * 操作来源详情值
     * 6，阅读翻章
     */
    public static final String ORDER_SOURCE_FROM_VALUE_6 = "6";
    /**
     * 操作来源详情值
     * 7，阅读菜单点击下一章
     */
    public static final String ORDER_SOURCE_FROM_VALUE_7 = "7";
    /**
     * 操作来源详情值
     * 10.其它
     */
    public static final String ORDER_SOURCE_FROM_VALUE_10 = "10";


    /**
     * 操作来源详情值
     * 1，订购
     */
    public static final String RECHARGE_SOURCE_FROM_VALUE_1 = "1";
    /**
     * 操作来源详情值
     * 2，个人中心充值
     */
    public static final String RECHARGE_SOURCE_FROM_VALUE_2 = "2";
    /**
     * 操作来源详情值
     * 3，启动-活动
     */
    public static final String RECHARGE_SOURCE_FROM_VALUE_3 = "3";
    /**
     * 操作来源详情值
     * 4，书架推荐位-活动
     */
    public static final String RECHARGE_SOURCE_FROM_VALUE_4 = "4";
    /**
     * 操作来源详情值
     * 5，书城-活动
     */
    public static final String RECHARGE_SOURCE_FROM_VALUE_5 = "5";
    /**
     * 操作来源详情值
     * 6，发现-活动
     */
    public static final String RECHARGE_SOURCE_FROM_VALUE_6 = "6";
    /**
     * 操作来源详情值
     * 7，活动
     */
    public static final String RECHARGE_SOURCE_FROM_VALUE_7 = "7";
    /**
     * 操作来源详情值
     * 8,打包定价
     */
    public static final String RECHARGE_SOURCE_FROM_VALUE_8 = "8";
    /**
     * 操作来源详情值
     * 9,新手专享
     */
    public static final String RECHARGE_SOURCE_FROM_VALUE_9 = "9";
    /**
     * 操作来源详情值
     * 10:vip充值开通
     */
    public static final String RECHARGE_SOURCE_FROM_VALUE_10 = "10";


    /**
     * pv map参数值
     */
    public static final String MAP_PN = "pn";
    /**
     * pi
     */
    public static final String MAP_PI = "pi";
    /**
     * ps
     */
    public static final String MAP_PS = "ps";

    //固化阅读来源
    /**
     * gh_pn
     */
    public static final String GH_PN = "gh_pn";
    /**
     * gh_pi
     */
    public static final String GH_PI = "gh_pi";
    /**
     * gh_ps
     */
    public static final String GH_PS = "gh_ps";
    /**
     * gh_type
     */
    public static final String GH_TYPE = "gh_type";
    /**
     * gh_web
     */
    public static final String GH_WEB = "gh_web";


    /**
     * map参数key
     */
    public static final String KEY_ORDER_BID = "bid";
    /**
     * cid
     */
    public static final String KEY_ORDER_CID = "cid";

    /**
     * ext
     */
    public static final String KEY_EXT = "ext";

    /**
     * id
     */
    public static final String KEY_ID = "id";
    /**
     * url
     */
    public static final String KEY_URL = "url";


    /**
     * operatefrom
     */
    public static final String KEY_OPERATE_FROM = "operatefrom";
    /**
     * partfrom
     */
    public static final String KEY_PART_FROM = "partfrom";
    /**
     * trackId
     */
    public static final String KEY_TRACK_ID = "trackId";

    /**
     * 个信推送cid
     */
    public static final String KEY_GT_CID = "gtcid";


    //==========================订购，充值==========================


    //=========================充值结果===========================

    /**
     * 自定义事件key
     */
    public static final String KEY_RECHARGE_RESULT_CZTYPE = "cztype";
    /**
     * result
     */
    public static final String KEY_RECHARGE_RESULT_RESULT = "result";
    /**
     * czcode
     */
    public static final String KEY_RECHARGE_RESULT_CZCODE = "czcode";
    /**
     * orderid
     */
    public static final String KEY_RECHARGE_RESULT_ORDERID = "orderid";
    /**
     * desc
     */
    public static final String KEY_RECHARGE_RESULT_DESC = "desc";

    /**
     * dlfs
     */
    public static final String KEY_LOGIN_RESULT_DLFS = "dlfs";
    /**
     * result
     */
    public static final String KEY_LOGIN_RESULT_RESULT = "result";
    /**
     * des
     */
    public static final String KEY_LOGIN_RESULT_DES = "des";

    /**
     * 1
     */
    public static final String KEY_LOGIN_RESULT_VALUE1 = "1";
    /**
     * 2
     */
    public static final String KEY_LOGIN_RESULT_VALUE2 = "2";

    /**
     * ext
     */
    public static final String KEY_LOGIN_EXT = "ext";
    /**
     * 1
     */
    public static final String KEY_LOGIN_EXT_VALUE1 = "1";
    /**
     * 2
     */
    public static final String KEY_LOGIN_EXT_VALUE2 = "2";
    /**
     * 3
     */
    public static final String KEY_LOGIN_EXT_VALUE3 = "3";

    /**
     * status
     */
    public static final String KEY_QNR_STATUS = "status";
    /**
     * bid
     */
    public static final String KEY_QNR_BID = "bid";
    /**
     * cid
     */
    public static final String KEY_QNR_CID = "cid";
    /**
     * 奖励未领取
     */
    public static final String QNR_STATUS_VALULE_1 = "1";
    /**
     * 奖励已领取
     */
    public static final String QNR_STATUS_VALULE_2 = "2";
    /**
     * 当前章节被删除
     */
    public static final String QNR_STATUS_VALULE_3 = "3";

    /**
     * 充值结果result值
     * 1:成功、2：订单失败、3：三方失败、4：查订单失败
     */
    public static final String RECHARGE_RESULT_KEY_VALUE_1 = "1";
    /**
     * 订单失败
     */
    public static final String RECHARGE_RESULT_KEY_VALUE_2 = "2";
    /**
     * 三方失败
     */
    public static final String RECHARGE_RESULT_KEY_VALUE_3 = "3";
    /**
     * 查订单失败
     */
    public static final String RECHARGE_RESULT_KEY_VALUE_4 = "4";

    //==========================充值结果==========================

    //=========================发起升级===========================
    /**
     * mustupdate
     */
    public static final String KEY_CLIENT_UPDATE_MUSTUPDATE = "mustupdate";
    /**
     * sfzdgx
     */
    public static final String KEY_CLIENT_UPDATE_SFZDGX = "sfzdgx";
    /**
     * apkurl
     */
    public static final String KEY_CLIENT_APK_URL = "apkurl";

    //=========================发起升级===========================

    /**
     * KEY_BID
     */
    public static final String KEY_BID = "bid";
    /**
     * key
     */
    public static final String KEY_CID = "cid";
    /**
     * key
     */
    public static final String KEY_XZNRSB_URL = "url";
    /**
     * key
     */
    public static final String KEY_EXCEPTION = "exception";
    /**
     * key
     */
    public static final String KEY_RESPONSE_CODE = "response_code";
    /**
     * key
     */
    public static final String KEY_NET_SIZE = "net_size";
    /**
     * key
     */
    public static final String KEY_DLDSIZE = "dldsize";
    /**
     * key des
     */
    public static final String KEY_DES = "des";

    /**
     * 备用cdn地址
     */
    public static final String KEY_XZNRSB_BACKUP_URL = "back_urls";

    /**
     * default
     */
    public static final String KEY_TAB_IS_DEFAULT = "isdefault";
    /**
     * tabs
     */
    public static final String KEY_TABS = "tabs";


    /**
     * 分类数据key
     */
    public static final String KEY_FLSJ = "flsj";

    /**
     * 索引
     */
    public static final String KEY_INDEX = "index";


    /**
     * 个像giuid
     */
    public static final String GXZC_GI_GUID = "giuid";

    /**
     * KEY_TYPE
     */
    public static final String KEY_TYPE = "type";
}
