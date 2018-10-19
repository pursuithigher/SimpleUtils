package com.dzbook.activity.search;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.dzbook.BaseTransparencyLoadActivity;
import com.dzbook.adapter.BookStoreSearchHeaderAndFooterAdapter;
import com.dzbook.adapter.BookstoreSearchRecycleViewAdapter;
import com.dzbook.lib.net.DzSchedulers;
import com.dzbook.lib.utils.CompatUtils;
import com.dzbook.log.LogConstants;
import com.dzbook.mvp.UI.SearchUI;
import com.dzbook.mvp.presenter.SearchPresenterImpl;
import com.dzbook.utils.DimensionPixelUtil;
import com.dzbook.utils.ImmersiveUtils;
import com.dzbook.utils.ListUtils;
import com.dzbook.utils.NetworkUtils;
import com.dzbook.utils.ThirdPartyLog;
import com.dzbook.utils.TypefaceUtils;
import com.dzbook.utils.hw.PermissionUtils;
import com.dzbook.view.PullLoadMoreRecycleLayout;
import com.dzbook.view.common.NetErrorTopView;
import com.dzbook.view.common.StatusView;
import com.dzbook.view.search.SearchEmptyView;
import com.dzbook.view.search.SearchHistoryView;
import com.dzbook.view.search.SearchHotView;
import com.dzbook.view.search.SearchTitleView;
import com.dzbook.view.store.Pw1View;
import com.dzbook.view.swipeBack.SwipeBackLayout;
import com.ishugui.R;
import com.iss.app.BaseActivity;
import com.iss.view.common.ToastAlone;

import java.util.concurrent.TimeUnit;

import hw.sdk.net.bean.seach.BeanSearch;
import io.reactivex.android.schedulers.AndroidSchedulers;

/**
 * 搜索activity页面
 *
 * @author dongdianzhou on 2017/3/29.
 */
public class SearchActivity extends BaseTransparencyLoadActivity implements SearchUI, PermissionUtils.OnPermissionListener {

    /**
     * tag
     */
    public static final String TAG = "SearchActivity";
    private static final String OTHER_SEARCH_KEY = "other_search_key";
    private static final String OTHER_SEARCH_TYPE = "other_search_type";
    private static final String IS_NEED_TITLE = "is_need_title";
    private TextView tvAuthor;
    private View relativeEdit;
    private View backgroundView;
    private View inputEditAnimView;
    private View inputEditContentView;
    private boolean isNeedFeedBackView = true;
    private View fraBack;
    private boolean isNeedTitle;
    private Pw1View pw1View;

    private boolean isFromShelf;

    private ImageView mImageViewDelete;
    private EditText mEditViewSearch;
    private RecyclerView mRecyclerViewKeyTips;
    private ScrollView mScrollViewHot;
    private SearchHotView mSearchHotViewHot;
    private SearchHistoryView mSearchHotViewHistory;

    private StatusView statusView;
    private NetErrorTopView netErrorTopView;

    private SearchKeyTipsAdapter mAdapterKeyTips;

    private SearchPresenterImpl searchPresenter;
    /**
     * 点击
     */
    private boolean isGetKeyPrompt = true;

    private SearchTitleView searchTitleView;

    private BookstoreSearchRecycleViewAdapter realSearchAdapter;
    private BookStoreSearchHeaderAndFooterAdapter searchListAdapter;
    private PullLoadMoreRecycleLayout loadMoreBookstoreSearchList;
    private LinearLayout layoutBookstoreSearch, relativeTitle;

    private SearchEmptyView emptyHeader;

    private boolean fromSearch = false;

    private boolean isSearchRecommend = false;

    private boolean isInitKeyboard;
    private Handler myHandler = new Handler();

    /**
     * 页面启动入口
     *
     * @param activity activity
     */
    public static void launch(Activity activity) {
        launch(activity, false);
    }

    /**
     * 页面启动入口
     *
     * @param activity        activity
     * @param isFromBookShelf isFromBookShelf
     */
    public static void launch(Activity activity, boolean isFromBookShelf) {
        Intent intent = new Intent(activity, SearchActivity.class);
        intent.putExtra("isfromshelf", isFromBookShelf);
        activity.startActivity(intent);
        if (isFromBookShelf) {
            activity.overridePendingTransition(0, 0);
        } else {
            BaseActivity.showActivity(activity);
        }
    }

    /**
     * 直接搜索 关键字
     *
     * @param context     context
     * @param searchKey   searchKey
     * @param searchType  searchType=4 从全局搜索过来的
     * @param isNeedTitle isNeedTitle
     */
    public static void toSearch(Context context, String searchKey, String searchType, boolean isNeedTitle) {
        Intent intent = new Intent(context, SearchActivity.class);
        intent.putExtra(OTHER_SEARCH_KEY, searchKey);
        intent.putExtra(OTHER_SEARCH_TYPE, searchType);
        intent.putExtra(IS_NEED_TITLE, isNeedTitle);
        context.startActivity(intent);
        BaseActivity.showActivity(context);
    }

    @Override
    public String getTagName() {
        return TAG;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        if (intent != null) {
            isFromShelf = intent.getBooleanExtra("isfromshelf", false);
        }
        checkPermissionUtils = new PermissionUtils();
        setContentView(R.layout.activity_search);
        ThirdPartyLog.onEventValueOldClick(SearchActivity.this, ThirdPartyLog.OPEN_SEACH_UMENG_ID, null, 1);
    }

    @Override
    public void onBackPressed() {
        if (fromSearch) {
            super.onBackPressed();
        } else {
            Editable editable = mEditViewSearch.getText();
            if (isNeedFeedBackView && null != editable && !TextUtils.isEmpty(editable.toString())) {
                mEditViewSearch.setText("");
            } else {
                super.onBackPressed();
            }
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (searchPresenter != null) {
            searchPresenter.destroyCallBack();
        }
    }

    @Override
    public String getPI() {
        return searchPresenter.getSearchKey();
    }

    @Override
    public String getPS() {
        return super.getPS();
    }

    @Override
    protected void initData() {
//        super.initData();
        if (searchPresenter == null) {
            searchPresenter = new SearchPresenterImpl(this);
        }
        String format = String.format("%1$1s" + getResources().getString(R.string.str_search_hint), "");
        mEditViewSearch.setHint(format);
        mSearchHotViewHot.setSearchPresenter(searchPresenter);
        mSearchHotViewHistory.setSearchPresenter(searchPresenter);
        mAdapterKeyTips.setSearchPresenter(searchPresenter);
        realSearchAdapter = new BookstoreSearchRecycleViewAdapter(getActivity());
        realSearchAdapter.setSearchPresenter(searchPresenter);
        searchListAdapter = new BookStoreSearchHeaderAndFooterAdapter<>(realSearchAdapter);
        loadMoreBookstoreSearchList.setAdapter(searchListAdapter);
        dealIntent();
    }

    private void dealIntent() {
        Intent intent = getIntent();
        if (intent != null) {
            if (TextUtils.equals(getString(R.string.hw_search_action), intent.getAction())) {
                String queryKey = intent.getStringExtra("suggest_intent_query");
                if (!TextUtils.isEmpty(queryKey)) {
                    SearchActivity.toSearch(getContext(), queryKey, "4", false);
                    finish();
                    return;
                }
            }
        }


        //外部跳转到搜索的 搜索词
        String otherJumpSearchKey = null;
        //外部跳转到搜索的 搜索词的类型
        String otherJumpSearchType = "";
        if (null != intent) {
            otherJumpSearchKey = intent.getStringExtra(OTHER_SEARCH_KEY);
            otherJumpSearchType = intent.getStringExtra(OTHER_SEARCH_TYPE);
            isNeedTitle = intent.getBooleanExtra(IS_NEED_TITLE, false);
        }
        //从书籍详情跳转 需要等待所有信息初始化完成 才能调用
        if (isNeedTitle) {
            isNeedFeedBackView = false;
            tvAuthor.setVisibility(View.VISIBLE);
            if ("3".equals(otherJumpSearchType)) {
                tvAuthor.setText(otherJumpSearchKey);
                relativeTitle.setBackgroundResource(R.color.color_100_f2f2f2);
                ImmersiveUtils.init(getActivity(), R.color.color_100_f2f2f2, getNavigationBarColor());
            }
            relativeEdit.setVisibility(View.GONE);
            mEditViewSearch.setText(otherJumpSearchKey);

            checkSearch(otherJumpSearchType, true);
        } else {
            tvAuthor.setVisibility(View.GONE);
            relativeEdit.setVisibility(View.VISIBLE);
            if (TextUtils.equals("4", otherJumpSearchType)) {
                fromSearch = true;
                mEditViewSearch.setText(otherJumpSearchKey);
                mEditViewSearch.setSelection(otherJumpSearchKey.length());
                checkSearch("4", true);
            } else {
                if (!isInitKeyboard) {
                    isInitKeyboard = true;
                    AndroidSchedulers.mainThread().scheduleDirect(new Runnable() {
                        @Override
                        public void run() {
//                            if (!isFromShelf) {
                            showKeyboard();
//                            }
                        }
                    }, 200, TimeUnit.MILLISECONDS);
                }
                //165接口 如果是从书籍详情页直接点击作者或者标签搜索的，165接口请求成功后不需要设置hot visiable
                searchPresenter.getHotSearchDataFromNet();
            }
        }

    }

    @Override
    protected void initView() {
//        super.initView();
        inputEditContentView = findViewById(R.id.linearlayout_search);
        inputEditAnimView = findViewById(R.id.relative_title2);
        relativeTitle = findViewById(R.id.relative_title);
        backgroundView = findViewById(R.id.background_view);
        mImageViewDelete = findViewById(R.id.imageview_delete);
        emptyHeader = new SearchEmptyView(getContext());
        loadMoreBookstoreSearchList = findViewById(
                R.id.pullLoadMoreRecyclerView);
        loadMoreBookstoreSearchList.setAllReference(false);
        layoutBookstoreSearch = findViewById(R.id.layout_bookstore_search);
        loadMoreBookstoreSearchList.setLinearLayout();
        RecyclerView.RecycledViewPool recycledViewPool = new RecyclerView.RecycledViewPool();
        recycledViewPool.setMaxRecycledViews(0, 8);
        loadMoreBookstoreSearchList.getRecyclerView().setRecycledViewPool(recycledViewPool);
        loadMoreBookstoreSearchList.setRefreshDisable();
        searchTitleView = new SearchTitleView(getContext());
        statusView = findViewById(R.id.statusView);
        fraBack = findViewById(R.id.fra_back);
        mEditViewSearch = findViewById(R.id.edit_search);
        mScrollViewHot = findViewById(R.id.scrollview_hot);
        mSearchHotViewHot = findViewById(R.id.searchhotview_hot);
        mSearchHotViewHistory = findViewById(R.id.searchhotview_history);
        mRecyclerViewKeyTips = findViewById(R.id.recyclerview_keytips);
        mRecyclerViewKeyTips.setLayoutManager(new LinearLayoutManager(this));
        mAdapterKeyTips = new SearchKeyTipsAdapter(this);
        mRecyclerViewKeyTips.setAdapter(mAdapterKeyTips);
        tvAuthor = findViewById(R.id.tv_author);
        relativeEdit = findViewById(R.id.relative_edit);
        pw1View = new Pw1View(this);
        TypefaceUtils.setHwChineseMediumFonts(tvAuthor);
        openAlphaAnimation();
    }

    @Override
    protected void setListener() {
        statusView.setNetErrorClickListener(new StatusView.NetErrorClickListener() {
            @Override
            public void onNetErrorEvent(View v) {
                searchPresenter.retryNetRequest(0);
            }
        });


//        super.setListener();
        mSwipeBackLayout.addSwipeListener(new SwipeBackLayout.SwipeListener() {
            @Override
            public void onScrollStateChange(int state, float scrollPercent) {
            }

            @Override
            public void onEdgeTouch(int edgeFlag) {
            }

            @Override
            public void onScrollOverThreshold() {
                hideKeyboard();
            }
        });
        mImageViewDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mEditViewSearch.setText("");
            }
        });
        loadMoreBookstoreSearchList.setOnPullLoadMoreListener(new PullLoadMoreRecycleLayout.PullLoadMoreListener() {
            @Override
            public void onRefresh() {

            }

            @Override
            public void onLoadMore() {
                initNetErrorStatus();
                searchPresenter.getLoadMorePase();
            }
        });
        fraBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideKeyboard();
                Editable editable = mEditViewSearch.getText();
                if (isNeedFeedBackView && null != editable && !TextUtils.isEmpty(editable.toString())) {
                    mEditViewSearch.setText("");
                } else {
                    searchPresenter.saveSearchHistoryToShareFile();
                    myHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            finish();
                        }
                    }, 100);
                }
            }
        });
        mEditViewSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                switch (actionId) {
                    case EditorInfo.IME_ACTION_SEARCH:
                        checkSearch("", false);
                        return true;
                    default:
                        return false;
                }
            }
        });
        mEditViewSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (isGetKeyPrompt) {
                    String key = mEditViewSearch.getText().toString().trim();
                    if (!TextUtils.isEmpty(key)) {
                        mImageViewDelete.setVisibility(View.VISIBLE);
                    } else {
                        mImageViewDelete.setVisibility(View.GONE);
                    }
                    if (!TextUtils.isEmpty(key) && searchPresenter != null) {
                        searchPresenter.getPromptKeys(key);
                    } else if (TextUtils.isEmpty(key)) {
                        showSearchHotView();
                        if (loadMoreBookstoreSearchList != null) {
                            loadMoreBookstoreSearchList.scrollToTop();
                        }
                    }
                } else {
                    isGetKeyPrompt = true;
                }
            }
        });
    }

    private void checkSearch(String searchType, boolean isOtherJump) {
        String key = mEditViewSearch.getText().toString().trim();
        String hint = mEditViewSearch.getHint().toString().trim();
        if (hint.equals(getString(R.string.str_search_hint))) {
            hint = "";
        }
        if (TextUtils.isEmpty(key) && TextUtils.isEmpty(hint)) {
            //明确指出去除吐司,UX检视报告page35
            //ToastAlone.showShort(R.string.toast_search_key_not_empty);
            searchPresenter.checkNet();
        } else {
            if (!TextUtils.isEmpty(key)) {
                searchPresenter.searchkey(key, LogConstants.ZONE_SSYM_CGSS, searchType, isOtherJump);
                return;
            }
            searchPresenter.searchkey(hint, LogConstants.ZONE_SSYM_MRCSS, searchType, isOtherJump);
        }
    }

    @Override
    public Context getContext() {
        return this;
    }

    @Override
    public void showNoNetConnectView(final int requestmode) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                requestShowNetError();
            }
        });
    }

    @Override
    public void showLoadDataDialog() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (layoutBookstoreSearch != null && layoutBookstoreSearch.getVisibility() == View.VISIBLE) {
                    layoutBookstoreSearch.setVisibility(View.GONE);
                }
                if (mScrollViewHot != null && mScrollViewHot.getVisibility() == View.VISIBLE) {
                    mScrollViewHot.setVisibility(View.GONE);
                }
                if (mRecyclerViewKeyTips != null && mRecyclerViewKeyTips.getVisibility() == View.VISIBLE) {
                    mRecyclerViewKeyTips.setVisibility(View.GONE);
                }
                statusView.showLoading();
            }
        });
    }

    @Override
    public void dismissLoadDataDialog() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                statusView.showSuccess();
            }
        });
    }

    @Override
    public void setEditTextData(final String bookstoreSearchKeyBean) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mEditViewSearch.setHintTextColor(CompatUtils.getColor(getContext(), R.color.color_30_000000));
                mEditViewSearch.setHint(bookstoreSearchKeyBean);
            }
        });
    }

    @Override
    public void setEditTextData(final String tags, final boolean isGetKeyPropt) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                isGetKeyPrompt = isGetKeyPropt;
                mEditViewSearch.setText(tags);
                mEditViewSearch.setSelection(tags.length());
                if (!TextUtils.isEmpty(tags)) {
                    mImageViewDelete.setVisibility(View.VISIBLE);
                } else {
                    mImageViewDelete.setVisibility(View.GONE);
                }
            }
        });
    }

    @Override
    public void clearKeyPromptDatas() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mAdapterKeyTips.clearData();
                if (!TextUtils.isEmpty(mEditViewSearch.getText().toString())) {
                    showKeyPromptViews();
                }
            }
        });
    }

    @Override
    public void netErrorPage() {
        requestShowNetError();
    }

    @Override
    public void setKeyPromptDatas(final SearchKeysBeanInfo searchKeysBeanInfo) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mAdapterKeyTips.setData(searchKeysBeanInfo, mEditViewSearch.getText().toString().trim(), true);
                if (!TextUtils.isEmpty(mEditViewSearch.getText().toString())) {
                    showKeyPromptViews();
                }
            }
        });
    }

    @Override
    public void referenceHistory(final SearchHotAndHistoryBeanInfo searchHotAndHistoryBeanInfo) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (searchHotAndHistoryBeanInfo.isExistHistoryList()) {
                    mSearchHotViewHistory.bindData(searchHotAndHistoryBeanInfo);
                    mSearchHotViewHistory.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    @Override
    public void disableHistoryView(final SearchHotAndHistoryBeanInfo searchHotAndHistory) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mSearchHotViewHistory != null && mSearchHotViewHistory.getVisibility() == View.VISIBLE) {
                    mSearchHotViewHistory.setVisibility(View.GONE);
                }
            }
        });
    }

    @Override
    public void setHotAndHistoryData(final SearchHotAndHistoryBeanInfo searchHotAndHistory) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (searchHotAndHistory.isExistSearchHotKeys()) {
                    mSearchHotViewHot.bindData(searchHotAndHistory.getSearchHotKeys(), true);
                    mSearchHotViewHot.setVisibility(View.VISIBLE);
                }
                if (searchHotAndHistory.isExistHistoryList()) {
                    mSearchHotViewHistory.bindData(searchHotAndHistory);
                    mSearchHotViewHistory.setVisibility(View.VISIBLE);
                }
                showSearchHotView();
            }
        });
    }

    @Override
    public boolean getSearchResultType() {
        return isSearchRecommend;
    }


    /**
     * 在输入过程中有key提示的时候显示keyview
     */
    private void showKeyPromptViews() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                statusView.showSuccess();
                if (layoutBookstoreSearch != null && layoutBookstoreSearch.getVisibility() == View.VISIBLE) {
                    layoutBookstoreSearch.setVisibility(View.GONE);
                }
                if (mScrollViewHot != null && mScrollViewHot.getVisibility() == View.VISIBLE) {
                    mScrollViewHot.setVisibility(View.GONE);
                }
                if (mRecyclerViewKeyTips != null && mRecyclerViewKeyTips.getVisibility() != View.VISIBLE) {
                    mRecyclerViewKeyTips.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    /**
     * 热门搜索有数据的时候显示热门搜索
     */
    private void showSearchHotView() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (!NetworkUtils.getInstance().checkNet()) {
                    requestShowNetError();
                } else {
                    statusView.showSuccess();
                    if (layoutBookstoreSearch != null && layoutBookstoreSearch.getVisibility() == View.VISIBLE) {
                        layoutBookstoreSearch.setVisibility(View.GONE);
                    }
                    if (mRecyclerViewKeyTips != null && mRecyclerViewKeyTips.getVisibility() == View.VISIBLE) {
                        mRecyclerViewKeyTips.setVisibility(View.GONE);
                    }
                    if (mScrollViewHot != null && mScrollViewHot.getVisibility() != View.VISIBLE) {
                        mScrollViewHot.setVisibility(View.VISIBLE);
                    }
                }

            }
        });
    }


    /**
     * 显示搜索结果
     */
    private void showSearchResult() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                statusView.showSuccess();
                if (mRecyclerViewKeyTips != null && mRecyclerViewKeyTips.getVisibility() == View.VISIBLE) {
                    mRecyclerViewKeyTips.setVisibility(View.GONE);
                }
                if (mScrollViewHot != null && mScrollViewHot.getVisibility() == View.VISIBLE) {
                    mScrollViewHot.setVisibility(View.GONE);
                }
                if (layoutBookstoreSearch != null && layoutBookstoreSearch.getVisibility() != View.VISIBLE) {
                    layoutBookstoreSearch.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    // 隐藏软键盘
    @Override
    public void hideKeyboard() {
        try {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) {
                imm.hideSoftInputFromWindow(mEditViewSearch.getWindowToken(), 0);
            }
        } catch (Throwable ignore) {
            ignore.printStackTrace();
        }
    }

    @Override
    public void showKeyboard() {
        try {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null && null != mEditViewSearch) {
                imm.showSoftInput(mEditViewSearch, InputMethodManager.SHOW_IMPLICIT);
            }
        } catch (Throwable ignore) {
        }
    }

    @Override
    public void setPullLoadMoreCompleted() {
        loadMoreBookstoreSearchList.setPullLoadMoreCompleted();
    }

    @Override
    public void clearEmptySearchData(boolean refresh) {
        if (refresh && realSearchAdapter != null) {
            realSearchAdapter.addItem(null, true);
            searchListAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void setSearchResultData(BeanSearch result, boolean refresh, String strPage) {
        initNetErrorStatus();
        if (result != null && result.isSuccess()) {
            //刷新数据
            if (refresh) {
                loadMoreBookstoreSearchList.removeHeaderView(emptyHeader);
                loadMoreBookstoreSearchList.removeHeaderView(searchTitleView);
                loadMoreBookstoreSearchList.removeFooterView(pw1View);
                searchTitleView.removeTitle();

                if (!ListUtils.isEmpty(result.searchList)) {
                    setSearchResultResultView(result);
                } else {
                    ToastAlone.showShort(R.string.no_search_data);
                }
            } else {
                //分页数据
                if (!ListUtils.isEmpty(result.searchList)) {
                    loadMoreBookstoreSearchList.setHasMore(true);
                    if (realSearchAdapter == null) {
                        realSearchAdapter = new BookstoreSearchRecycleViewAdapter(getActivity());
                        realSearchAdapter.addItem(result.searchList, false);
                        loadMoreBookstoreSearchList.setAdapter(searchListAdapter);
                    } else {
                        realSearchAdapter.addItem(result.searchList, false);
                        searchListAdapter.notifyDataSetChanged();
                    }
                } else {
                    loadMoreBookstoreSearchList.setHasMore(false);
                    loadMoreBookstoreSearchList.addFooterView(pw1View);
                }
            }
            showSearchResult();
        } else {
            if ("1".equals(strPage)) {
                ToastAlone.showShort(R.string.request_data_failed);
            } else {
                loadMoreBookstoreSearchList.setHasMore(false);
                loadMoreBookstoreSearchList.addFooterView(pw1View);
                ToastAlone.showShort(R.string.no_more_data);
            }
        }
        loadMoreBookstoreSearchList.setPullLoadMoreCompleted();
    }

    @SuppressLint("StringFormatInvalid")
    private void setSearchResultResultView(BeanSearch result) {
        initNetErrorStatus();
        realSearchAdapter.addItem(result.searchList, true);
        searchListAdapter.notifyDataSetChanged();
        loadMoreBookstoreSearchList.setHasMore(true);

        switch (result.searchType) {
            case "0":
                //2018/3/7 全文匹配 有返回结果
                searchTitleView.setBookTitle(mEditViewSearch.getText().toString());
                loadMoreBookstoreSearchList.addHeaderView(searchTitleView);
                break;
            case "1":
                //2018/3/7 书籍匹配 有返回结果

                break;
            case "2":
                //2018/3/7 作者匹配 有返回结果
                searchTitleView.setAuthorTitle(mEditViewSearch.getText().toString());
                loadMoreBookstoreSearchList.addHeaderView(searchTitleView);
                break;
            case "3":
                //2018/3/7 标签匹配 有返回结果
                searchTitleView.setTagTitle(mEditViewSearch.getText().toString());
                loadMoreBookstoreSearchList.addHeaderView(searchTitleView);
                break;
            case "5":
                searchTitleView.setRecommendTitle();
                //2018/3/7 没有搜索到结果 返回默认推荐的书籍
                if (isNeedFeedBackView) {
                    loadMoreBookstoreSearchList.addHeaderView(emptyHeader);
                    loadMoreBookstoreSearchList.addHeaderView(searchTitleView);
                }
                loadMoreBookstoreSearchList.setHasMore(false);
                loadMoreBookstoreSearchList.addFooterView(pw1View);
                break;
            default:
                searchTitleView.setTagTitle(mEditViewSearch.getText().toString());
                loadMoreBookstoreSearchList.addHeaderView(searchTitleView);
                break;

        }
        searchTitleView.setTvRight("");
        if (result.totalCount > 0 && !"5".equals(result.searchType)) {
            String authorTitle = getResources().getString(R.string.search_max_numb);
            searchTitleView.setTitle(String.format(authorTitle, result.totalCount + ""));
        }
    }

    @Override
    public int getStatusColor() {
        return R.color.color_100_ffffff;
    }

    /**
     * 网络错误view
     */
    public void requestShowNetError() {
        DzSchedulers.main(new Runnable() {
            @Override
            public void run() {
                if (!NetworkUtils.getInstance().checkNet() && loadMoreBookstoreSearchList.getAdapter() != null && loadMoreBookstoreSearchList.getAdapter().getItemCount() > 0) {
                    initNetView();
                } else {
                    statusView.showNetError();
                    if (mRecyclerViewKeyTips != null && mRecyclerViewKeyTips.getVisibility() == View.VISIBLE) {
                        mRecyclerViewKeyTips.setVisibility(View.GONE);
                    }
                    if (layoutBookstoreSearch != null && layoutBookstoreSearch.getVisibility() == View.VISIBLE) {
                        layoutBookstoreSearch.setVisibility(View.GONE);
                    }
                    if (mScrollViewHot != null && mScrollViewHot.getVisibility() == View.VISIBLE) {
                        mScrollViewHot.setVisibility(View.GONE);
                    }
                }
            }
        });
    }

    private void initNetErrorStatus() {
        if (!NetworkUtils.getInstance().checkNet() && loadMoreBookstoreSearchList.getAdapter() != null && loadMoreBookstoreSearchList.getAdapter().getItemCount() > 0) {
            initNetView();
        } else {
            destoryNetView();
        }
    }

    private void initNetView() {
        if (netErrorTopView == null) {
            netErrorTopView = new NetErrorTopView(getContext());
            layoutBookstoreSearch.addView(netErrorTopView, 0, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, DimensionPixelUtil.dip2px(getContext(), 48)));
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        checkPermissionUtils.onRequestPermissionsResult(requestCode, permissions, grantResults);
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void destoryNetView() {
        if (netErrorTopView != null) {
            layoutBookstoreSearch.removeView(netErrorTopView);
            netErrorTopView = null;
        }
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

    @Override
    public void onPermissionGranted() {
    }

    @Override
    public void finish() {
        if (isFromShelf) {
            finishAlphaAnimation();
        } else {
            super.finish();
        }
    }

    private void openAlphaAnimation() {
        if (!isFromShelf) {
            mScrollViewHot.setAlpha(1);
            mRecyclerViewKeyTips.setAlpha(1);
            statusView.setAlpha(1);
            backgroundView.setAlpha(1);
            return;
        }
        doAlphaAnimation(new Runnable() {
            @Override
            public void run() {
                inputEditAnimView.setVisibility(View.GONE);
            }
        }, 0, 1, 0, DimensionPixelUtil.dip2px(getContext(), 32));
    }

    private void finishAlphaAnimation() {
        doAlphaAnimation(new Runnable() {
            @Override
            public void run() {
                finishNoAnimation();
            }
        }, 1, 0, DimensionPixelUtil.dip2px(getContext(), 32), 0);
    }

    private void doAlphaAnimation(final Runnable onEnd, float v1, float v2, float... values) {
        ValueAnimator scaleYAnimator = ValueAnimator.ofFloat(v1, v2);
        scaleYAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                mScrollViewHot.setAlpha((Float) valueAnimator.getAnimatedValue());
                mRecyclerViewKeyTips.setAlpha((Float) valueAnimator.getAnimatedValue());
                statusView.setAlpha((Float) valueAnimator.getAnimatedValue());
                backgroundView.setAlpha((Float) valueAnimator.getAnimatedValue());
            }
        });
        scaleYAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationEnd(Animator animation) {
                onEnd.run();
            }

            @Override
            public void onAnimationStart(Animator animation) {
                inputEditAnimView.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationCancel(Animator animation) {
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
            }
        });
        scaleYAnimator.setDuration(500);
        scaleYAnimator.start();

        // 平移
        ObjectAnimator animator = ObjectAnimator.ofFloat(inputEditContentView, "translationX", values);
        animator.setDuration(500);
        animator.start();
    }

}
