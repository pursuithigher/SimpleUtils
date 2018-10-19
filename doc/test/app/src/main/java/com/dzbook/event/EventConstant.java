package com.dzbook.event;

/**
 * EventConstant
 *
 * @author dongdianzhou on 2017/7/19.
 */

public class EventConstant {

    /**
     * type
     */
    public static final String TYPE_SELECTPHOTO = "type_selectphoto";

    /**
     * 书架
     */
    public static final String TYPE_MAINSHELFFRAGMENT = "MainShelfFragment";

    /**
     * 刷新
     */
    public static final String TYPE_RECHARGE_LIST = "RechargeListActivity";

    /**
     * type
     */
    public static final String TYPE_SHELFMANAGER = "type_shelfmanager";

    /**
     * type 书城
     */
    public static final String TYPE_BOOK_STORE = "MainShelfPresenter";
    /**
     * type
     */
    public static final String TYPE_TURNDZRECHARGE = "type_turndzrecharge";
    /**
     * type
     */
    public static final String TYPE_CHANNELPAGEFRAGMENT = "ChannelPageFragment";
    /**
     * type 活动中心
     */
    public static final String TYPE_CENTER_DETAIL_ACTIVITY = "CenterDetailActivity";
    /**
     * type
     */
    public static final String TYPE_LOT_ORDER_PAGE_ACTIVITY = "LotOrderPageActivity";
    /**
     * type
     */
    public static final String TYPE_SINGLE_ORDER_ACTIVITY = "SingleOrderActivity";
    /**
     * type
     */
    public static final String TYPE_WX_ACTIVITY = "BaseWXEnTryActivity";

    /**
     * type 评论
     */
    public static final String TYPE_BOOK_COMMENT = "BookCommentMoreActivity";

    /**
     * type 详情
     */
    public static final String TYPE_BOOK_DETAIL = "BookDetailActivity";

    /**
     * 实名
     */
    public static final String TYPE_REAL_SWITCH_NAME = "real_name_switch_phone";

    //    public static final String TYPE_NIGHT_OR_EYE_CARE = "type_night_or_eye_care";

    /**
     * 检查更新
     */
    public static final String TYPE_CHECK_UPDATE = "type_check_update";

    /**
     * click
     */
    public static final String TYPE_MAIN_TYPE_SUBVIEW_CLICK = "type_main_type_sub_view_click";

    /**
     * 分类顶部的view的toaggle按钮 被点击了
     */
    public static final String TYPE_TYPE_TOGGLE = "type_toggle";

    /**
     * 两者共用
     */
    public static final String CATALOG_INFO = "catalog_info";

    /**
     * 从H5添加书籍
     */
    public static final String TYPE_ADD_BOOK_FROM_H5 = "type_add_book_from_h5";

    /**
     * push Type
     */
    public static final String TYPE_PUSH = "type_push";

    /**
     * main2activity
     */
    public static final String TYPE_MAIN2ACTIVITY = "main2activity";

    /**
     * requestcode
     */
    public static final int REQUESTCODE_OPENBOOK = 110015;
    /**
     * requestcode 关闭书籍
     */
    public static final int REQUESTCODE_CLOSEDBOOK = 110016;
    /**
     * requestcode 护眼模式
     */
    public static final int REQUESTCODE_EYE_MODE_CHANGE = 110017;
    /**
     * requestcode
     */
    public static final int REQUESTCODE_REFERENCESHELFMANAGERVIEW = 110056;

    /**
     * 签到成功
     */
    public static final int REQUESTCODE_SIGNINSUCCESS = 110014;


    /**
     * 实名认证 更改手机号成功
     */
    public static final int REQUESTCODE_SWITCH_PHONE_SUCCESS = 110057;


    /**
     * 分类二级页面 顶部的toggle点击后的事件
     */
    public static final int CODE_TYPE_TOGGLE_SHOW = 110058;
    /**
     * 分类二级页面 顶部的toggle点击后的事件
     */
    public static final int CODE_TYPE_TOGGLE_HIDE = 110059;


    /**
     * cache
     */
    public static final int FIND_HTTP_CACHE_INFO_REQUEST_CODE = 10009;
    /**
     * 查找所有书籍code
     */
    public static final int FIND_ALL_BOOKS_REQUEST_CODE = 10003;
    /**
     * 书籍检查更新
     */
    public static final int UPDATE_SHELF_BOOK_REQUEST_CODE = 10001;
    /**
     * 删除操作
     */
    public static final int BUTTON_SHELF_DELETE_REQUEST_CODE = 10007;
    /**
     * 打开书动画OpenBook
     */
    public static final int OPEN_BOOK_REQUEST_CODE = 10011;
    /**
     * closebook
     */
    public static final int CLOSEBOOK_REQUEST_CODE = 10013;
    /**
     * 书架
     */
    public static final int SHELF_BOOK_TASK_REQUEST_CODE = 10002;
    /**
     * 关闭logoactivity
     */
    public static final int CLOSE_LOGO_REQ_CODE = 10020;


    /**
     * 用于接收eventbus的消息
     */
    public static final int LOGIN_SUCCESS_UPDATE_CLOUDSHELF_SYNC = 30024;

    /**
     * 登录成功更新书架
     */
    public static final int LOGIN_SUCCESS_UPDATE_SHELF = 35001;
    /**
     * 登录成功更新书架
     */
    public static final int LOGIN_SUCCESS_UPDATE_USER_VIEW = 35002;

    /**
     * 更新
     */
    public static final int UPDATA_FEATURED_URL_REQUESTCODE = 30025;

    /**
     * 充值成功引导登录，登录成功后需要将订购等流程再次拉起来
     */
    public static final int LOGIN_SUCCESS_FINISH_RECHARGE_PREGRESS_REQUESTCODE = 30026;

    /**
     * 充值成功引导登录，取消后需要将订购等流程再次拉起来
     */
    public static final int LOGIN_CANCEL_FINISH_RECHARGE_PREGRESS_REQUESTCODE = 30027;

    /**
     * 签到引导登录 登录成功之后需要刷新签到页面
     */
    public static final int LOGIN_SUCCESS_FINISH_REFRESH_SIGN_PAGE_REQUESTCODE = 30028;

    /**
     * 登录成功userId变化，用新userId更新订购页面数据
     */
    public static final int LOGIN_SUCCESS_USERID_CHANGE_UPDATE_ORDER_PAGE_REQUESTCODE = 30029;

    /**
     * 用于remove 启动页的handler的message
     */
    public static final int LOGO_REMOVE_MESSAGE = 30030;

    /**
     * 分享微信的处理结果
     */
    public static final int WX_RESULT = 30031;


    /**
     * 删除书籍评论
     */
    public static final int CODE_DELETE_BOOK_COMMENT = 30032;


    /**
     * 删除书籍评论后 适配器 数据Wie空
     */
    public static final int CODE_DELETE_BOOK_IS_EMPTY = 30033;

    /**
     * 删除书籍评论
     */
    public static final int CODE_COMMENT_BOOKDETAIL_SEND_SUCCESS = 30034;

    /**
     * 给评论点赞
     */
    public static final int CODE_PARISE_BOOK_COMMENT = 30035;

    /**
     * 取消点赞
     */
    public static final int CODE_CANCEL_PARISE_BOOK_COMMENT = 30036;

    /**
     * 书城事件类型：1：转自有，2：阅读偏好3：云书架跳转 4:跳我的 5：跳书架
     */
    public static final String EVENT_BOOKSTORE_TYPE = "event_bookstore_type";

    /**
     * 当前tab的实现是通过其对应的id实现，当前去除掉tab到index数值的映射
     */
    public static final String SKIP_TAB_SHELF = "shelf";
    /**
     * 书城tab
     */
    public static final String SKIP_TAB_STORE = "store";


    /**
     * h5 唤醒时  如果再阅读器内了 就自动帮用户把这本书 加入书架
     */
    public static final int CODE_ADD_BOOK_FROM_H5 = 400003;

    /**
     * 有新版本需要更新
     */
    public static final int CODE_CHECK_UPDATE = 400004;

    /**
     * 分类顶部的 浮窗view  被点击后
     */
    public static final int CODE_TYPE_SUBVIEW_CLICK = 400005;

    /**
     * 登录检查发现需要弹窗登录，重新刷新个人中心顶部页面信息
     */
    public static final int LOGIN_CHECK_RSET_PERSON_LOGIN_STATUS = 400006;


    /**
     * 关闭阅读器
     */
    public static final int CODE_FINISH_READER = 500001;
    /**
     * VIP开通成功，更新页面关于vip的状态
     */
    public static final int CODE_VIP_OPEN_SUCCESS_REFRESH_STATUS = 500002;
    /**
     * 关闭启动页
     */
    public static final int FINISH_SPLASH = 500003;

    /**
     * 推荐页request code
     */
    public static final int RECOMMEND_REQUEST_CODE = 500004;

    /**
     * 关闭
     * activity request code
     */
    public static final int FINISH_ACTIVITY_REQUEST_CODE = 500005;
    /**
     * 关闭分享
     */
    public static final int FINISH_SHARE = 500006;

    /**
     * push的code
     */
    public static final int CODE_PUSH = 500007;

    /**
     * 分享成功
     */
    public static final int SHARE_SUCCESS = 500008;
    /**
     * 分享失败
     */
    public static final int SHARE_FAIL = 500009;
    /**
     * 取消分享
     */
    public static final int SHARE_CANCEL = 500010;
    /**
     * 从数据库刷新书架图书
     */
    public static final int SHELF_LOCAL_REFRESH = 500011;

    /**
     * 发送消息给书架，打开图书
     */
    public static final int START_OPEN_BOOK = 500012;

    /**
     * show Tms 对话框
     */
    public static final int CODE_SHOW_TMS_DIALOG = 500013;

    /**
     * 显示用户个人中心其他家资产弹窗
     */
    public static final int CODE_PERSON_CENTER_SHOW_USER_ASSERT_DIALOG = 500014;

    /**
     * 华为H5页面 js调用关闭当前页面H5页面
     */
    public static final int CODE_JS_CALL_FINISH_PAGE = 500015;
}
