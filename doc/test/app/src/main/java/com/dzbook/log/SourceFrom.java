package com.dzbook.log;

import com.dzbook.activity.CenterDetailActivity;
import com.dzbook.activity.SplashActivity;
import com.dzbook.activity.detail.BookDetailActivity;
import com.dzbook.activity.detail.BookDetailChapterActivity;
import com.dzbook.activity.reader.ReaderActivity;
import com.dzbook.activity.reader.ReaderCatalogActivity;
import com.dzbook.fragment.main.MainPersonalFragment;
import com.dzbook.fragment.main.MainShelfFragment;
import com.dzbook.fragment.main.MainStoreFragment;
import com.dzbook.recharge.order.LotOrderPageActivity;
import com.dzbook.recharge.order.SingleOrderActivity;

/**
 * author lizhongzhong 2017/7/10.
 */

public class SourceFrom {

    /**
     * 书籍详情页面
     */
    public static final String FROM_BOOK_DETAIL = BookDetailActivity.TAG;

    /**
     * 书籍详情目录页面
     */
    public static final String FROM_BOOK_DETAIL_CONTENTS = BookDetailChapterActivity.TAG;

    /**
     * 阅读器页面
     */
    public static final String FROM_READER = ReaderActivity.TAG;

    /**
     * 阅读器章节页面
     */
    public static final String FROM_READER_CONTENTS = ReaderCatalogActivity.TAG;

    /**
     * 单章订购页面
     */
    public static final String FROM_SIMPLE_PAY_ORDER = SingleOrderActivity.TAG;

    /**
     * 批量订购页面
     */
    public static final String FROM_LOT_PAY_ORDER = LotOrderPageActivity.TAG;

    /**
     * 个人中心
     */
    public static final String FROM_PERSNAL_CENTER = MainPersonalFragment.TAG;

    /**
     * 活动详情页面
     */
    public static final String FROM_CENTER_DETAIL = CenterDetailActivity.TAG;

    /**
     * 启动页
     */
    public static final String FROM_LOGIN = SplashActivity.TAG;

    /**
     * 书架
     */
    public static final String FROM_BOOK_SHELF = MainShelfFragment.TAG;

    /**
     * 书城
     */
    public static final String FROM_BOOK_STORE = MainStoreFragment.TAG;

    //发现
    //    public static String FROM_DISCOVER= MainDiscoverFragment.class.getSimpleName();
    /**
     * 打包定价
     */
    public static final String FROM_PACK_ORDER = "com.ishugui.pack.order";
}
