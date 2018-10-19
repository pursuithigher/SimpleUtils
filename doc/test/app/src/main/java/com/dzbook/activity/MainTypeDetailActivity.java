package com.dzbook.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dzbook.BaseTransparencyLoadActivity;
import com.dzbook.event.EventConstant;
import com.dzbook.event.EventMessage;
import com.dzbook.log.DzLog;
import com.dzbook.mvp.UI.NativeTypeDetailUI;
import com.dzbook.mvp.presenter.MainTypeDetailPresenter;
import com.dzbook.mvp.presenter.MainTypeDetailPresenterImpl;
import com.dzbook.utils.DimensionPixelUtil;
import com.dzbook.utils.NetworkUtils;
import com.dzbook.view.DianZhongCommonTitle;
import com.dzbook.view.PullLoadMoreRecycleLayout;
import com.dzbook.view.common.NetErrorTopView;
import com.dzbook.view.common.StatusView;
import com.dzbook.view.store.Pw1View;
import com.dzbook.view.type.MainTypeDetailTopView;
import com.ishugui.R;

import java.util.ArrayList;
import java.util.HashMap;

import hw.sdk.net.bean.BeanBookInfo;
import hw.sdk.net.bean.type.BeanMainTypeDetail;

/**
 * 分类页面 二级菜单
 *
 * @author Winzows 2018/3/2
 */

public class MainTypeDetailActivity extends BaseTransparencyLoadActivity implements NativeTypeDetailUI {
    /**
     * tag
     */
    public static final String TAG = "MainTypeDetailActivity";
    private MainTypeDetailPresenter mPresenter;
    private PullLoadMoreRecycleLayout loadMoreLayout;
    private String cid, title;
    private DianZhongCommonTitle includeTopTitleItem;
    private BeanMainTypeDetail.TypeFilterBean filterBean;
    private LinearLayout llBaseView;
    private RelativeLayout rlBaseview, rlDefaultTipsView;
    private StatusView statusView;
    private TextView tvDefaultTips;
    private NetErrorTopView netErrorTopView;
    private LinearLayout netErrorTopLayout;
    private String categoryId;
    private Pw1View pw1View;
    private boolean isShowTips;
    private String defaultSelect;
    /**
     * 避免不断地重绘页面
     */
    private boolean isResetTopView = false;

    /**
     * 弹出来的suspendView
     */
    private MainTypeDetailTopView suspensionView;
    private boolean isHideSuspendionView = false;
    private boolean showSubView;

    @Override
    public String getTagName() {
        return TAG;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ac_native_type_detail);
    }


    @Override
    protected void initView() {
        netErrorTopLayout = findViewById(R.id.net_error_layout_view);
        includeTopTitleItem = findViewById(R.id.include_top_title_item);
        loadMoreLayout = findViewById(R.id.pullLoadMoreRecyclerViewLinearLayout);
        loadMoreLayout.setAllReference(false);
        loadMoreLayout.setLinearLayout();
        RecyclerView.RecycledViewPool recycledViewPool = new RecyclerView.RecycledViewPool();
        recycledViewPool.setMaxRecycledViews(0, 8);
        loadMoreLayout.getRecyclerView().setRecycledViewPool(recycledViewPool);
        rlBaseview = findViewById(R.id.rl_baseView);
        tvDefaultTips = findViewById(R.id.tv_sub);
        rlDefaultTipsView = findViewById(R.id.rlSub);
        llBaseView = findViewById(R.id.llSub);
        statusView = findViewById(R.id.defaultview_nonet);
        mPresenter = new MainTypeDetailPresenterImpl(this);
        pw1View = new Pw1View(this);

    }

    @Override
    protected void initData() {
        rlBaseview.setVisibility(View.GONE);
        Intent getIntent = getIntent();
        if (getIntent != null) {
            cid = getIntent.getStringExtra("cid");
            title = getIntent.getStringExtra("title");
            categoryId = getIntent.getStringExtra("category_id");
            defaultSelect = getIntent.getStringExtra("defaultSelect");
        }
        filterBean = new BeanMainTypeDetail.TypeFilterBean();
        filterBean.setCid(cid);
        includeTopTitleItem.setTitle(TextUtils.isEmpty(title) ? "分类" : title);

        requestData();
    }


    @Override
    public String getPI() {
        return mPresenter.getPI();
    }

    @Override
    public String getPS() {
        if (TextUtils.isEmpty(categoryId)) {
            categoryId = "-1";
        }
        if (TextUtils.isEmpty(cid)) {
            cid = "-1";
        }
        return categoryId + "_" + cid;
    }

    @Override
    protected void onResume() {
        super.onResume();
        HashMap<String, String> map = new HashMap<>();
        map.put("title", title + "");
        DzLog.getInstance().logPv(getTagName(), map, "");
    }

    /**
     * 顶部条件
     */
    @Override
    public void bindTopViewData(BeanMainTypeDetail bean) {
        if (bean != null && bean.checkTopViewData()) {
            mPresenter.bindTopViewData(loadMoreLayout, bean, filterBean, defaultSelect);
        }
    }

    @Override
    protected void setListener() {
        includeTopTitleItem.setLeftClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        loadMoreLayout.setOnPullLoadMoreListener(new PullLoadMoreRecycleLayout.PullLoadMoreListener() {
            @Override
            public void onRefresh() {

            }

            @Override
            public void onLoadMore() {
                initNetErrorStatus();
                mPresenter.requestData(MainTypeDetailPresenterImpl.LOAD_TYPE_LOADMORE, filterBean);
            }
        });
        statusView.setNetErrorClickListener(new StatusView.NetErrorClickListener() {
            @Override
            public void onNetErrorEvent(View v) {
                statusView.showSuccess();
                requestData();
            }
        });
        loadMoreLayout.addOnScrollListener(new RecycleViewOnScrollListener());

        rlDefaultTipsView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                suspensionView = mPresenter.addSuspensionView(loadMoreLayout, llBaseView, rlDefaultTipsView);
                showSubView = true;
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mPresenter != null) {
            mPresenter.onDestroy();
        }
    }

    /**
     * 底部书籍信息
     */
    @Override
    public void bindBottomBookInfoData(int loadType, ArrayList<BeanBookInfo> bookInfoList) {
        if (bookInfoList != null && bookInfoList.size() > 0) {
            loadMoreLayout.setHasMore(true);
        }
        mPresenter.bindBottomBookInfoData(loadType, loadMoreLayout, bookInfoList);
    }

    @Override
    public void onError() {
        rlBaseview.setVisibility(View.GONE);
        if (loadMoreLayout != null && loadMoreLayout.getAdapter() != null && loadMoreLayout.getAdapter().getItemCount() > 0 && !NetworkUtils.getInstance().checkNet()) {
            netErrorTopLayout.setVisibility(View.VISIBLE);
            statusView.showSuccess();
        } else {
            netErrorTopLayout.setVisibility(View.GONE);
            statusView.showNetError();
        }
    }


    @Override
    public void showView() {
        statusView.showSuccess();
        rlBaseview.setVisibility(View.VISIBLE);
    }

    @Override
    public void showEmpty() {
        loadMoreLayout.setPullLoadMoreCompleted();
        loadMoreLayout.setHasMore(false);
    }


    @Override
    public void stopLoad() {
        loadMoreLayout.post(new Runnable() {
            @Override
            public void run() {
                loadMoreLayout.setPullLoadMoreCompleted();
                if (mPresenter != null) {
                    mPresenter.stopLoad();
                }
                if (!NetworkUtils.getInstance().checkNet()) {
                    initNetErrorStatus();
                }
            }
        });
    }


    @Override
    public void noMore() {
        loadMoreLayout.post(new Runnable() {
            @Override
            public void run() {
                loadMoreLayout.setPullLoadMoreCompleted();
                loadMoreLayout.setHasMore(false);
                if (!isShowTips) {
                    loadMoreLayout.addFooterView(pw1View);
                    isShowTips = true;
                }
            }
        });
    }

    @Override
    public void clickHead() {
        initNetErrorStatus();
    }

    /**
     * 打开分类二级
     *
     * @param context    context
     * @param title      title
     * @param cid        cid
     * @param categoryId categoryId
     */
    public static void launch(Context context, String title, String cid, String categoryId) {
        Intent intent = new Intent(context, MainTypeDetailActivity.class);
        intent.putExtra("cid", cid);
        intent.putExtra("title", title);
        intent.putExtra("category_id", categoryId);
        context.startActivity(intent);
        showActivity(context);
    }

    /**
     * 打开vip
     *
     * @param context       上下文
     * @param title         vip分类
     * @param defaultSelect 默认选中的那个 可以传递 玄幻 可以为空
     */
    public static void launchVip(Context context, String title, String defaultSelect) {
        Intent intent = new Intent(context, MainTypeDetailActivity.class);
        //默认是vip的一级分类
        intent.putExtra("cid", "vip");
        intent.putExtra("title", title);
        //打点使用的 在这里 只是vip
        intent.putExtra("category_id", "vip");
        intent.putExtra("defaultSelect", defaultSelect);
        context.startActivity(intent);
        showActivity(context);
    }

    /**
     * 刷新数据
     */
    public void requestData() {
        initNetErrorStatus();
        if (NetworkUtils.getInstance().checkNet()) {
            if (mPresenter != null && filterBean != null) {
                mPresenter.requestData(MainTypeDetailPresenterImpl.LOAD_TYPE_DEFAULT, filterBean);
                if (isShowTips) {
                    loadMoreLayout.removeFooterView(pw1View);
                    isShowTips = false;
                }
            }
        } else {
            onError();
        }
    }

    /**
     * RecycleViewOnScrollListener
     */
    private class RecycleViewOnScrollListener extends RecyclerView.OnScrollListener {

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
            LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
            int firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();
            if (firstVisibleItemPosition == 0) {
                resetTopView();
            } else if (showSubView) {
                showSubView = false;
                isHideSuspendionView = true;
            } else {
                if (!isHideSuspendionView && suspensionView != null && suspensionView.getParent() != null) {
                    return;
                }
                isHideSuspendionView = false;
                isResetTopView = false;
                llBaseView.removeAllViews();
                tvDefaultTips.setText(mPresenter.getSubTitleStr());
                llBaseView.addView(rlDefaultTipsView);
                llBaseView.setVisibility(View.VISIBLE);
            }
        }

        /**
         * Cannot call this method in a scroll callback. Scroll callbacks mightbe run during a measure & layout pass where you cannot change theRecyclerView data.
         * Any method call that might change the structureof the RecyclerView or the adapter contents should be postponed tothe next frame.
         */
        private void resetTopView() {
            llBaseView.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (isResetTopView) {
                        return;
                    }
                    llBaseView.removeAllViews();
                    llBaseView.setVisibility(View.GONE);
                    mPresenter.addRecycleHeaderView(loadMoreLayout, MainTypeDetailTopView.TYPE_TOP_VIEW);
                    isResetTopView = true;
                }
            }, 25);
        }
    }

    @Override
    public void onEventMainThread(EventMessage event) {
        super.onEventMainThread(event);
        int requestCode = event.getRequestCode();
        String type = event.getType();
        if (TextUtils.equals(type, EventConstant.TYPE_MAIN_TYPE_SUBVIEW_CLICK) && requestCode == EventConstant.CODE_TYPE_SUBVIEW_CLICK) {
            llBaseView.removeAllViews();
            llBaseView.setVisibility(View.GONE);
            mPresenter.addRecycleHeaderView(loadMoreLayout, MainTypeDetailTopView.TYPE_SUP_VIEW);
        }
    }

    private void initNetErrorStatus() {
        if (!NetworkUtils.getInstance().checkNet() && loadMoreLayout.getAdapter() != null && loadMoreLayout.getAdapter().getItemCount() > 0) {
            initNetView();
        } else {
            destoryNetView();
        }
    }

    private void initNetView() {
        if (netErrorTopView == null) {
            netErrorTopView = new NetErrorTopView(getContext());
            netErrorTopLayout.addView(netErrorTopView, 0, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, DimensionPixelUtil.dip2px(getContext(), 48)));
            netErrorTopLayout.setVisibility(View.VISIBLE);
        }
    }

    private void destoryNetView() {
        if (netErrorTopView != null) {
            netErrorTopLayout.removeView(netErrorTopView);
            netErrorTopView = null;
            netErrorTopLayout.setVisibility(View.GONE);
        }
    }

    @Override
    public void dismissLoadProgress() {
        if (statusView.getVisibility() == View.VISIBLE) {
            statusView.setVisibility(View.GONE);
        }
    }

    @Override
    public void showLoadProgress() {
        if (statusView.getVisibility() == View.GONE) {
            statusView.setVisibility(View.VISIBLE);
            statusView.showLoading();
        }
    }

    @Override
    public void removeFootView() {
        if (isShowTips) {
            loadMoreLayout.removeFooterView(pw1View);
            isShowTips = false;
        }
    }

    @Override
    protected boolean isCustomPv() {
        return true;
    }
}
