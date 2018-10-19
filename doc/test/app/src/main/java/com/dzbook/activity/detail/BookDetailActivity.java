package com.dzbook.activity.detail;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.dzbook.AppConst;
import com.dzbook.BaseTransparencyLoadActivity;
import com.dzbook.activity.reader.ChaseRecommendMoreActivity;
import com.dzbook.activity.reader.ReaderUtils;
import com.dzbook.database.bean.BookInfo;
import com.dzbook.database.bean.CatalogInfo;
import com.dzbook.event.EventBusUtils;
import com.dzbook.event.EventConstant;
import com.dzbook.event.EventMessage;
import com.dzbook.fragment.main.MainShelfFragment;
import com.dzbook.log.DzLog;
import com.dzbook.log.DzLogMap;
import com.dzbook.log.LogConstants;
import com.dzbook.model.ModelAction;
import com.dzbook.mvp.UI.BookDetailUI;
import com.dzbook.mvp.presenter.BookDetailPresenter;
import com.dzbook.utils.DimensionPixelUtil;
import com.dzbook.utils.ListUtils;
import com.dzbook.utils.NetworkUtils;
import com.dzbook.utils.ShareUtils;
import com.dzbook.utils.SpUtil;
import com.dzbook.utils.ThirdPartyLog;
import com.dzbook.utils.TypefaceUtils;
import com.dzbook.utils.WhiteListWorker;
import com.dzbook.utils.hw.LoginUtils;
import com.dzbook.utils.hw.PermissionUtils;
import com.dzbook.view.BookLinearLayout;
import com.dzbook.view.DianZhongCommonTitle;
import com.dzbook.view.ElasticScrollView;
import com.dzbook.view.bookdetail.DetailBookIntroView;
import com.dzbook.view.bookdetail.DetailCopyRightView;
import com.dzbook.view.bookdetail.DetailTopView;
import com.dzbook.view.bookdetail.TabAnchorsView;
import com.dzbook.view.comment.CommentBookDetailView;
import com.dzbook.view.common.StatusView;
import com.ishugui.R;
import com.iss.app.BaseActivity;
import com.iss.view.common.ToastAlone;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import hw.sdk.net.bean.BeanBookInfo;
import hw.sdk.net.bean.bookDetail.BeanBookDetail;
import hw.sdk.net.bean.bookDetail.BeanCommentInfo;
import io.reactivex.android.schedulers.AndroidSchedulers;

import static com.dzbook.event.EventConstant.CODE_VIP_OPEN_SUCCESS_REFRESH_STATUS;

/**
 * 书籍详情
 *
 * @author lizz
 */
public class BookDetailActivity extends BaseTransparencyLoadActivity implements OnClickListener, BookDetailUI, PermissionUtils.OnPermissionListener {

    /**
     * tag
     */
    public static final String TAG = "BookDetailActivity";
    private static final int MAX_ALPHA = 229;
    private static final int MIN_ALPHA = 10;
    private static final float HALF_FLOAT_ALPHA = 0.3f;

    //    private ImageView imageView_share;
    private DetailTopView detailTopView;
    private DetailBookIntroView detailBookIntroView;
    private DetailCopyRightView detailCopyRightView;
    private DianZhongCommonTitle mTitleView;
    private BookLinearLayout mAuthorOtherBook;
    private BookLinearLayout mSameBook;
    //是否是全本下载
    private boolean downBookAll;

    private boolean isSetTransTextColor = false;


    /**
     * 顶部显示标题,显示简介更多
     */
//    private TextView txt_text;

    /**
     * 右上角的书架按钮
     */
//    private Button btn_back;

    private TextView textViewFreeReading;

    private View layoutBottomMenu;

    private ElasticScrollView scrollViewBookDetail;

    private StatusView statusView;

    private long shareOnClickTime;

    private BookDetailPresenter mPresenter;
    private HashMap<String, String> logMap;
    private String prev;
    private String fromMsg;
    private CommentBookDetailView commentView;
    private String bookId, bookName;
    private TabAnchorsView tabAnchorsViewSuspension, tabAnchorsViewDetail;
    private View reAddShelf, reBuyBook;
    private ImageView ivAddShelf, ivBuyBook;
    private String author;
    private String coverWap;
    private float scrollViewHeight;
    private int scrollViewMinScroll;

    /**
     * 打开
     *
     * @param context       context
     * @param module        module
     * @param zone          zone
     * @param currentBookId currentBookId
     * @param otherBook     otherBook
     * @param bookName      bookName
     */
    public static void launch(Context context, @LogConstants.Module String module, @LogConstants.Zone String zone, String currentBookId, BeanBookInfo otherBook, String bookName) {
        if (!TextUtils.isEmpty(currentBookId)) {
            //打点
            HashMap<String, String> map = new HashMap<>();
            map.put("other_bid", otherBook.bookId);
            DzLog.getInstance().logClick(module, zone, currentBookId, map, null);
        }
        launch(context, otherBook.bookId, bookName);
    }

    /**
     * 打开
     *
     * @param context  context
     * @param bookId   bookId
     * @param bookName bookName
     */
    public static void launch(Context context, String bookId, String bookName) {
        //之后加载转圈搞好了需要打开
        Intent intent = new Intent(context, BookDetailActivity.class);
        intent.putExtra("bookId", bookId);
        intent.putExtra("bookName", bookName);
        context.startActivity(intent);
        BaseActivity.showActivity(context);
    }

    @Override
    public String getTagName() {
        return TAG;
    }

    @Override
    public int getMaxSize() {
        return 2;
    }

    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        checkPermissionUtils = new PermissionUtils();
        overridePendingTransition(R.anim.ac_in_from_right, R.anim.ac_out_keep);
        setContentView(R.layout.ac_book_detail);

        ThirdPartyLog.onEvent(getActivity(), ThirdPartyLog.DTL_ENTRY);
        prev = DzLog.getInstance().getPrev();
        EventBusUtils.sendMessage(EventConstant.FINISH_SPLASH);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            if (window != null) {
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                window.setStatusBarColor(Color.TRANSPARENT);
            }
        }
    }

    @Override
    public void setBookSourceFrom() {

    }

    /**
     * 设置book from
     */
    public void resetBookSourceFrom() {
        if (this.getName().equals(prev)) {
            HashMap<String, String> hashMap = new HashMap<>();
            if (null != logMap) {
                try {
                    if (logMap.containsKey(LogConstants.MAP_PN)) {
                        hashMap.put(LogConstants.GH_PN, logMap.get(LogConstants.MAP_PN));
                    }
                    if (logMap.containsKey(LogConstants.MAP_PI)) {
                        hashMap.put(LogConstants.GH_PI, logMap.get(LogConstants.MAP_PI));
                    }
                    if (logMap.containsKey(LogConstants.MAP_PS)) {
                        hashMap.put(LogConstants.GH_PS, logMap.get(LogConstants.MAP_PS));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            WhiteListWorker.setBookSourceFrom(this.getName(), hashMap, this);
        } else if (MainShelfFragment.TAG.equals(prev)) {
            WhiteListWorker.setBookSourceFrom(WhiteListWorker.BOOK_SHELF_ACTIVITY, null, null);
        }
    }

    @Override
    protected void initView() {
        /*
         * 判断Main2Activity是否处于运行状态
         * 非运行状态下，关闭滑动退出activity功能
         */
        setSwipeBackEnable(AppConst.isIsMainActivityActive());

        detailTopView = findViewById(R.id.detailTopView);
        mAuthorOtherBook = findViewById(R.id.author_other);
        mSameBook = findViewById(R.id.same_book);
        reBuyBook = findViewById(R.id.re_buy_book);
        reAddShelf = findViewById(R.id.re_add_shelf);
        ivAddShelf = findViewById(R.id.iv_add_shelf);
        ivBuyBook = findViewById(R.id.iv_buy_book);
        textViewFreeReading = findViewById(R.id.textView_freeReading);
        detailBookIntroView = findViewById(R.id.detailBookIntroView);
        detailCopyRightView = findViewById(R.id.detailcopyrightview);
        commentView = findViewById(R.id.commentView);
        mTitleView = findViewById(R.id.commontitle);
        scrollViewBookDetail = findViewById(R.id.scrollView_bookDetail);
        layoutBottomMenu = findViewById(R.id.layout_bottomMenu);
        statusView = findViewById(R.id.statusView);
        //悬浮的View
        tabAnchorsViewSuspension = findViewById(R.id.float_view_suspension);
        //布局上的view
        tabAnchorsViewDetail = findViewById(R.id.float_view);
        scrollViewMinScroll = DimensionPixelUtil.dip2px(getContext(), 55);

        TypefaceUtils.setHwChineseMediumFonts(textViewFreeReading);
    }

    @Override
    protected void initData() {

        Intent intent = getIntent();
        if (null == intent) {
            finish();
            return;
        }
        statusView.showLoading();
        BeanBookDetail bookDetailBean = (BeanBookDetail) intent.getSerializableExtra("bookInfoBean");
        bookId = intent.getStringExtra("bookId");
        bookName = intent.getStringExtra("bookName");
        if (null != bookDetailBean && null != bookDetailBean.book) {
            mPresenter = new BookDetailPresenter(this, bookDetailBean);
            setPageData(bookDetailBean);
        } else if (!TextUtils.isEmpty(bookId)) {
            mPresenter = new BookDetailPresenter(this, bookId);
            // 此为点击通知推送消息过来的时候 执行此语句来查询数据
            if (NetworkUtils.getInstance().checkNet()) {
                mPresenter.getBookDetail(bookId);
            } else {
                setErrPage();
            }
        } else {
            finish();
        }
    }

    public BookDetailPresenter getPresenter() {
        return mPresenter;
    }

    @Override
    public void setPageData(BeanBookDetail bookDetailBean) {
        if (null == bookDetailBean) {
            scrollViewBookDetail.setVisibility(View.INVISIBLE);
            layoutBottomMenu.setVisibility(View.INVISIBLE);
            return;
        }

        final BeanBookInfo bookInfo = bookDetailBean.book;
        if (null != bookInfo) {
            if (bookInfo.isDeleteOrUndercarriage()) {
                ToastAlone.showShort(getString(R.string.book_down_shelf));
                finish();
                return;
            }

            scrollViewBookDetail.setVisibility(View.VISIBLE);
            layoutBottomMenu.setVisibility(View.VISIBLE);

            ThirdPartyLog.onEventValueOldClick(this, "dz_" + ThirdPartyLog.BOOK_DETAIL_UMENG_ID + ThirdPartyLog.TOTAL, null, 1);

            bookName = bookInfo.bookName;

            detailTopView.bindData(bookInfo);

            detailBookIntroView.bindData(bookInfo, bookDetailBean.lastChapter, this);

            tabAnchorsViewDetail.bindData(bookInfo);
            tabAnchorsViewSuspension.bindData(bookInfo);

            bookId = bookInfo.bookId;

            //评论详情
            author = bookInfo.author;
            coverWap = bookInfo.coverWap;
            commentView.bindData(bookDetailBean.comments, bookId, bookName, author, coverWap);

            //作者的其它书
            List<BeanBookInfo> authorOtherBooks = bookDetailBean.authorOtherBooks;
            if (!ListUtils.isEmpty(authorOtherBooks)) {
                mAuthorOtherBook.setVisibility(View.VISIBLE);
                mAuthorOtherBook.bindData(1 == bookDetailBean.moreAuthor, bookInfo.bookId, authorOtherBooks);
            } else {
                mAuthorOtherBook.setVisibility(View.GONE);
            }

            //大家都在看
            List<BeanBookInfo> recommendBooks = bookDetailBean.recommendBooks;
            if (!ListUtils.isEmpty(recommendBooks)) {
                mSameBook.setVisibility(View.VISIBLE);
                mSameBook.bindData(1 == bookDetailBean.moreRecommend, bookInfo.bookId, recommendBooks);
            } else {
                mSameBook.setVisibility(View.GONE);
            }

            mAuthorOtherBook.setOnMoreClickListener(new AuthorOtherMoreClickListener(bookInfo, this));
            mSameBook.setOnMoreClickListener(new SameBookMoreClickListener(bookInfo, this));

            if (!TextUtils.isEmpty(bookInfo.bookCopyright) || !TextUtils.isEmpty(bookInfo.bookDisclaimer)) {
                detailCopyRightView.bindData(bookInfo);
                detailCopyRightView.setVisibility(View.VISIBLE);
            } else {
                detailCopyRightView.setVisibility(View.GONE);
            }

            if (mPresenter.isVipFreeReadBook()) {
                //是vip书籍，并且用户是vip用户
                ivBuyBook.setAlpha(HALF_FLOAT_ALPHA);
                textViewFreeReading.setText(R.string.vip_free_read);
            } else {
                ivBuyBook.setAlpha(1.0f);
                textViewFreeReading.setText(R.string.free_test_read);
            }
        }

        if (null != bookInfo && !TextUtils.isEmpty(bookInfo.shareUrl) && ShareUtils.isSupportWeChateShare()) {
            mTitleView.setRightOperVisible(DianZhongCommonTitle.COMMON_RIGHT_SHOW_MODE_DES);
        } else {
            mTitleView.setRightOperVisible(DianZhongCommonTitle.COMMON_RIGHT_SHOW_MODE_NONE);
        }

        mTitleView.setTitle(bookName);
        mTitleView.getTitleText().setTextSize(18);
        mTitleView.getTitleText().setTextColor(Color.argb(0, 0, 0, 0));

    }

    @Override
    public void refreshMenu(BeanBookInfo beanBookInfo, int marketStatus, BookInfo bookInfo, boolean isShowFreeStatus) {
        //按本计费-----------------
        //左按钮：全本下载|| 继续阅读
        //右按钮：加入书架|| 已加入
        //按章计费-----------------
        //左按钮：批量下载
        //右按钮：加入书架|| 已加入

        if (TextUtils.equals(beanBookInfo.unit, "1")) {
            refreshSingleBookMenu(marketStatus, bookInfo);
        } else {
            refreshSerialBookMenu(marketStatus, bookInfo, isShowFreeStatus);
        }
    }

    private void refreshSerialBookMenu(int marketStatus, BookInfo bookInfo, boolean isShowFreeStatus) {
        if (bookInfo != null && bookInfo.isAddBook == 2) {
            setBookShelfMenu(false);
            if (bookInfo.confirmStatus == 2) {
                setBookDownloadMenu(true);
            } else {
                if (isShowFreeStatus) {
                    setBookDownloadMenu(false);
                } else {
                    setBookDownloadMenu(true);
                }
            }
        } else {
            setBookShelfMenu(true);

            if (bookInfo != null && bookInfo.isFreeStatus(getContext())) {
                setBookDownloadMenu(false);
            } else {
                setBookDownloadMenu(true);
                reBuyBook.setClickable(true);
            }
        }
    }

    private void refreshSingleBookMenu(int marketStatus, BookInfo bookInfo) {
        if (bookInfo != null && bookInfo.isAddBook == 2) {
            setBookShelfMenu(false);
            setBookDownloadMenu(true);
        } else {
            setBookShelfMenu(true);

            boolean isVipUser = SpUtil.getinstance(this).getInt(SpUtil.DZ_IS_VIP, 0) == 1;
            if (marketStatus == 12 && isVipUser) {
                downBookAll = true;
                setBookDownloadMenu(false);
            } else {
                switch (marketStatus) {
                    case 3:
                    case 5:
                    case 6:
                    case 103:
                    case 105:
                    case 106:
                        downBookAll = true;
                        setBookDownloadMenu(false);
                        return;
                    default:
                        downBookAll = true;
                        setBookDownloadMenu(true);
                        reBuyBook.setClickable(true);
                        break;
                }
            }
        }
    }

    @Override
    protected void setListener() {
        statusView.setNetErrorClickListener(new StatusView.NetErrorClickListener() {
            @Override
            public void onNetErrorEvent(View v) {
                if (!NetworkUtils.getInstance().checkNet()) {
                    setErrPage();
                    return;
                }
                if (!TextUtils.isEmpty(bookId)) {
                    mPresenter.getBookDetail(bookId);
                }
            }
        });

        tabAnchorsViewDetail.setScrollView(scrollViewBookDetail, detailTopView, tabAnchorsViewDetail, detailBookIntroView, commentView);
        tabAnchorsViewSuspension.setScrollView(scrollViewBookDetail, detailTopView, tabAnchorsViewDetail, detailBookIntroView, commentView);
        scrollViewBookDetail.setScrollViewListener(new ElasticScrollView.ScrollViewListener() {
            @Override
            public void onScrollChanged(ElasticScrollView scrollView, int x, int y, int oldx, int oldy) {
                int offset = 0;
                if (tabAnchorsViewDetail.getVisibility() == View.VISIBLE) {
                    offset = tabAnchorsViewDetail.getMeasuredHeight();
                } else if (tabAnchorsViewSuspension.getVisibility() == View.VISIBLE) {
                    offset = tabAnchorsViewSuspension.getMeasuredHeight();
                }
                tabAnchorsViewDetail.onScrollChanged(x, y, offset);
                tabAnchorsViewSuspension.onScrollChanged(x, y, offset);
                setAlphaTitleBack(y);
                setAlphaTitle();
            }
        });
        reBuyBook.setOnClickListener(this);
        textViewFreeReading.setOnClickListener(this);

        reAddShelf.setOnClickListener(this);
        mTitleView.setLeftClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                ThirdPartyLog.onEventValueOldClick(getActivity(), ThirdPartyLog.BOOK_DETAIL_UMENG_ID, ThirdPartyLog.BOOK_DETAIL_BACK_VALUE, 1);
                // 点击返回 返回到书城activity
                ModelAction.checkElseGoHome(BookDetailActivity.this);
                finish();
            }
        });
        mTitleView.setRightIconVisibility(ShareUtils.isSupportShare() ? View.VISIBLE : View.GONE);
        mTitleView.setRightClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (shareOnClickTime != 0 && System.currentTimeMillis() - shareOnClickTime < 500) {
                    return;
                }
                shareOnClickTime = System.currentTimeMillis();
                mPresenter.share();
            }
        });
    }

    private void setAlphaTitleBack(int y) {
        if (y > detailTopView.getHeight()) {
            int argb = Color.argb(255, 242, 242, 242);
            mTitleView.setBackgroundColor(argb);
        } else if (y < 0) {
            int argb = Color.argb(255, 255, 255, 255);
            mTitleView.setBackgroundColor(argb);
        } else {

            float alpha = y * 255.0F / detailTopView.getHeight();
            int argb = Color.argb((int) alpha, 242, 242, 242);
            mTitleView.setBackgroundColor(argb);
        }
    }

    /**
     * 设置渐变标题
     */
    private void setAlphaTitle() {
        if (scrollViewHeight == 0) {
            scrollViewHeight = scrollViewBookDetail.getMeasuredHeight();
        }

        if (scrollViewHeight == 0) {
            return;
        }


        float thisScrollY = scrollViewBookDetail.getScrollY();

        if (thisScrollY < scrollViewMinScroll) {
            //避免频繁设置 浪费性能
            if (!isSetTransTextColor) {
                mTitleView.getTitleText().setTextColor(Color.argb(0, 0, 0, 0));
            }
            isSetTransTextColor = true;
            return;
        }

        isSetTransTextColor = false;

        float scale = thisScrollY / scrollViewHeight;
        float alpha = MAX_ALPHA * scale;

        if (alpha > MAX_ALPHA) {
            alpha = MAX_ALPHA;
        }
        if (alpha < MIN_ALPHA || thisScrollY <= 0) {
            alpha = 0;
        }
        mTitleView.getTitleText().setTextColor(Color.argb((int) alpha, 0, 0, 0));
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.re_buy_book:
                if (!NetworkUtils.getInstance().checkNet()) {
                    showNotNetDialog();
                } else {
                    LoginUtils.getInstance().forceLoginCheck(this, new LoginUtils.LoginCheckListener() {
                        @Override
                        public void loginComplete() {
                            mPresenter.download(downBookAll);
                        }
                    });
                }

                break;
            case R.id.textView_freeReading:
                mPresenter.freeReading();
                break;
            case R.id.re_add_shelf:
                mPresenter.addToShelf(logMap);
                break;
            default:
                break;
        }
    }


    @Override
    public void onBackPressed() {
        ModelAction.checkElseGoHome(this);
        super.onBackPressed();
    }

    @Override
    public void setBookShelfMenu(boolean enable) {
        reAddShelf.setEnabled(enable);
        if (enable) {
            ivAddShelf.setAlpha(1.0f);
        } else {
            ivAddShelf.setAlpha(HALF_FLOAT_ALPHA);
        }
    }

    @Override
    public void dismissLoadDataDialog() {
        AndroidSchedulers.mainThread().scheduleDirect(new Runnable() {
            @Override
            public void run() {
                statusView.showSuccess();
            }
        });
    }

    @Override
    public void setDeletePage() {
        statusView.showEmpty(getResources().getString(R.string.string_empty_lower));
    }

    @Override
    public void setErrPage() {
        statusView.showNetError();
        if (!TextUtils.isEmpty(bookName)) {
            mTitleView.setTitle(bookName);
        }
    }

    @Override
    public void showLoadDataDialog() {
        AndroidSchedulers.mainThread().scheduleDirect(new Runnable() {
            @Override
            public void run() {
                statusView.showLoading();
            }
        });
    }

    private void setBookDownloadMenu(boolean enable) {
        if (mPresenter.isVipFreeReadBook()) {
            enable = false;
        }
        reBuyBook.setEnabled(enable);
        if (enable) {
            ivBuyBook.setAlpha(1.0f);
        } else {
            ivBuyBook.setAlpha(HALF_FLOAT_ALPHA);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mPresenter != null) {
            mPresenter.destroy();
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        if (null == logMap) {
            logMap = DzLogMap.getPreLastMap();
        }

        resetBookSourceFrom();

        if (mPresenter != null) {
            mPresenter.pvLog();
            mPresenter.refreshMenu();
        }

        hideSoftKeyboard();//以防点击微信分享 到微信页面后去登陆 弹出软键盘 返回后没隐藏
    }

    @Override
    public String getPI() {
        return mPresenter.getBookId();
    }

    @Override
    public String getPS() {
        return super.getPS();
    }

    @Override
    protected void onPause() {
        super.onPause();
        // 友盟统计
        ThirdPartyLog.onPauseActivity(this);
    }

    @Override
    protected boolean isCustomPv() {
        return true;
    }

    @Override
    public void finish() {
        super.finish();
    }

    @Override
    public void intoReaderCatalogInfo(CatalogInfo catalogInfo) {
        ReaderUtils.intoReader(this, catalogInfo, catalogInfo.currentPos);
    }

    @Override
    public BaseActivity getHostActivity() {
        return this;
    }


    @Override
    public Context getContext() {
        return getActivity();
    }

    @Override
    public void onEventMainThread(EventMessage event) {
        super.onEventMainThread(event);
        int requestCode = event.getRequestCode();
        Bundle bundle = event.getBundle();
        switch (requestCode) {
            case CODE_VIP_OPEN_SUCCESS_REFRESH_STATUS:
                afterPayVip();
                break;
            case EventConstant.CODE_COMMENT_BOOKDETAIL_SEND_SUCCESS:
                afterCommentSuccess(bundle);
                break;
            case EventConstant.CODE_DELETE_BOOK_IS_EMPTY:
                if (commentView != null && !TextUtils.isEmpty(bookId)) {
                    commentView.bindData(null, bookId, bookName, author, coverWap);
                }
                break;
            default:
                break;

        }
    }

    private void afterPayVip() {
        //                VIP开通成功，更新页面关于vip的状态
        if (!NetworkUtils.getInstance().checkNet()) {
            setErrPage();
            return;
        }
        if (!TextUtils.isEmpty(bookId)) {
            mPresenter.getBookDetail(bookId);
        }
    }

    private void afterCommentSuccess(Bundle bundle) {
        if (commentView != null && bundle != null && !TextUtils.isEmpty(bookId)) {
            ArrayList<BeanCommentInfo> infoList = (ArrayList<BeanCommentInfo>) bundle.getSerializable("commentList");
            String bid = bundle.getString("bookId");
            if (infoList != null && infoList.size() > 0 && TextUtils.equals(bid, this.bookId)) {
                commentView.bindData(infoList, this.bookId, bookName, author, coverWap);
            }
        }
    }

    /**
     * 作者其他图书
     */
    private static class AuthorOtherMoreClickListener implements BookLinearLayout.OnMoreClickListener {
        private final BeanBookInfo bookInfo;

        private WeakReference<Activity> weakReference;

        AuthorOtherMoreClickListener(BeanBookInfo bookInfo, Activity activity) {
            weakReference = new WeakReference<Activity>(activity);
            this.bookInfo = bookInfo;
        }

        @Override
        public void onClick() {
            Activity activity = weakReference.get();
            if (null != activity) {
                ChaseRecommendMoreActivity.lauchMore(activity, bookInfo.author, bookInfo.bookId, "1");
            }
        }
    }

    /**
     * 同类图书
     */
    private static class SameBookMoreClickListener implements BookLinearLayout.OnMoreClickListener {
        private final BeanBookInfo bookInfo;

        private WeakReference<Activity> weakReference;

        SameBookMoreClickListener(BeanBookInfo bookInfo, Activity activity) {
            weakReference = new WeakReference<Activity>(activity);
            this.bookInfo = bookInfo;
        }

        @Override
        public void onClick() {
            Activity activity = weakReference.get();
            if (null != activity) {
                ChaseRecommendMoreActivity.lauchMore(activity, activity.getResources().getString(R.string.same_book), bookInfo.bookId, "2");
            }
        }
    }

    @Override
    public int getStatusColor() {
        return R.color.color_100_ffffff;
    }


    @Override
    public void onPermissionGranted() {
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        checkPermissionUtils.onRequestPermissionsResult(requestCode, permissions, grantResults);
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void checkPermission() {
        String[] pnList = PermissionUtils.loadingPnList();
        boolean isGrant = checkPermissionUtils.checkPermissions(pnList);
        if (!isGrant) {
            checkPermissionUtils.requestPermissions(this, PermissionUtils.CODE_LOGO_REQUEST, pnList, this);
        }
    }

    @Override
    public void onPermissionDenied() {
        checkPermissionUtils.showTipsDialog(this);
    }
}