package com.dzbook.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.dzbook.BaseSwipeBackActivity;
import com.dzbook.activity.detail.BookDetailActivity;
import com.dzbook.adapter.BookStoreSearchHeaderAndFooterAdapter;
import com.dzbook.adapter.CommonBookListRecycleViewAdapter;
import com.dzbook.log.DzLog;
import com.dzbook.log.LogConstants;
import com.dzbook.mvp.UI.RankTopUI;
import com.dzbook.mvp.presenter.RankTopPresenter;
import com.dzbook.utils.DimensionPixelUtil;
import com.dzbook.utils.NetworkUtils;
import com.dzbook.utils.WhiteListWorker;
import com.dzbook.view.DianZhongCommonTitle;
import com.dzbook.view.PullLoadMoreRecycleLayout;
import com.dzbook.view.RankTopTopView;
import com.dzbook.view.common.NetErrorTopView;
import com.dzbook.view.common.StatusView;
import com.dzbook.view.store.Pw1View;
import com.ishugui.R;
import com.iss.app.BaseActivity;

import java.util.HashMap;
import java.util.List;

import hw.sdk.net.bean.BeanBookInfo;
import hw.sdk.net.bean.BeanRankTopResBeanInfo;

/**
 * 排行榜
 *
 * @author lizz 2018-03-08
 */
public class RankTopActivity extends BaseSwipeBackActivity implements RankTopUI {
    /**
     * tag
     */
    public static final String TAG = "RankTopActivity";

    private DianZhongCommonTitle commontitle;

    private RankTopPresenter rankTopPresenter;

    private CommonBookListRecycleViewAdapter realAdapter;
    private BookStoreSearchHeaderAndFooterAdapter rankAdapter;

    private PullLoadMoreRecycleLayout pullLoadMoreRecyclerView;

    private StatusView statusView;

    private RankTopTopView rankTopView;

    private Pw1View pw1View;

    private boolean isShowTips;

    private NetErrorTopView netErrorTopView;
    private LinearLayout netErrorTopLayout;

    /**
     * 打开 排行
     *
     * @param activity activity
     */
    public static void launch(Activity activity) {
        Intent intent = new Intent(activity, RankTopActivity.class);
        activity.startActivity(intent);
        BaseActivity.showActivity(activity);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ac_rank_top);
    }

    @Override
    protected void initView() {
        commontitle = findViewById(R.id.commontitle);
        netErrorTopLayout = findViewById(R.id.net_error_layout_view);
        pullLoadMoreRecyclerView = findViewById(R.id.pullLoadMoreRecyclerView);
        RecyclerView.RecycledViewPool recycledViewPool = new RecyclerView.RecycledViewPool();
        recycledViewPool.setMaxRecycledViews(0, 8);
        pullLoadMoreRecyclerView.getRecyclerView().setRecycledViewPool(recycledViewPool);
        statusView = findViewById(R.id.defaultview_nonet);

        rankTopView = new RankTopTopView(this);
        pw1View = new Pw1View(this);

    }


    @Override
    public String getTagName() {
        return TAG;
    }

    @Override
    protected void initData() {
        rankTopPresenter = new RankTopPresenter(this);
        realAdapter = new CommonBookListRecycleViewAdapter(this, true);
        rankAdapter = new BookStoreSearchHeaderAndFooterAdapter<>(realAdapter);

        rankTopView.setRankTopPresenter(rankTopPresenter);
        rankTopView.setRankTopUI(this);
        pullLoadMoreRecyclerView.setLinearLayout();
        pullLoadMoreRecyclerView.setAdapter(rankAdapter);
        pullLoadMoreRecyclerView.addHeaderView(rankTopView);

        if (NetworkUtils.getInstance().checkNet()) {
            rankTopPresenter.getFirstPageRankTopInfo(false);
        } else {
            setLoadFail(true);
        }
    }

    @Override
    public String getPI() {
        if (null != rankTopView) {
            return rankTopView.getCurrentInfo();
        } else {
            return super.getPI();
        }
    }

    @Override
    protected void setListener() {
        statusView.setNetErrorClickListener(new StatusView.NetErrorClickListener() {
            @Override
            public void onNetErrorEvent(View v) {
                initNetErrorStatus();
                statusView.showSuccess();
                Boolean isFirstLoad = (Boolean) statusView.getTag();
                if (isFirstLoad) {
                    rankTopPresenter.getFirstPageRankTopInfo(false);
                } else {
                    rankTopPresenter.getClickRankTopInfo(rankTopPresenter.getLoadFailParentId(), rankTopPresenter.getLoadFailSubId());
                }
            }
        });
        pullLoadMoreRecyclerView.setOnPullLoadMoreListener(new MyPullLoadMoreListener());
        realAdapter.setOnItemClickListener(new MyOnItemClickListener());
        commontitle.setLeftClickListener(new MyLeftClickListener());
    }

    @Override
    public void setLoadFail(Boolean isFirstLoad) {
        dismissProgress();
        realAdapter.addNetBeanItem(null, true);
        statusView.setTag(isFirstLoad);
        statusView.showNetError();

        if (!isFirstLoad) {
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) statusView.getLayoutParams();
            params.topMargin = DimensionPixelUtil.dip2px(this, 98);
            statusView.setLayoutParams(params);
        } else {
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) statusView.getLayoutParams();
            params.topMargin = DimensionPixelUtil.dip2px(this, 0);
            statusView.setLayoutParams(params);
        }
    }

    @Override
    public void showLoadProgresss() {
        if (statusView.getVisibility() == View.GONE) {
            statusView.setVisibility(View.VISIBLE);
            statusView.showLoading();
        }
    }

    @Override
    public void dismissProgress() {
        statusView.showSuccess();
    }

    @Override
    public void setFirstLoadRankTopInfo(BeanRankTopResBeanInfo beanInfo) {
        statusView.showSuccess();
        pullLoadMoreRecyclerView.setHasMore(true);
        rankTopView.bindData(beanInfo);
        if (beanInfo != null && beanInfo.rankBooks != null && beanInfo.rankBooks.size() > 0) {
            realAdapter.addNetBeanItem(beanInfo.rankBooks, true);
            rankAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void setLoadMoreRankTopInfo(BeanRankTopResBeanInfo beanInfo) {
        if (beanInfo.rankBooks != null && beanInfo.rankBooks.size() > 0) {
            realAdapter.addNetBeanItem(beanInfo.rankBooks, false);
            rankAdapter.notifyDataSetChanged();
            pullLoadMoreRecyclerView.setHasMore(true);
        } else if (!beanInfo.isMoreData()) {
            pullLoadMoreRecyclerView.setHasMore(false);
//            showMessage(R.string.no_more_data);
            if (!isShowTips) {
                pullLoadMoreRecyclerView.addFooterView(pw1View);
                isShowTips = true;
            }
        }
    }

    @Override
    public void setClickRankTopInfo(List<BeanBookInfo> books) {
        pullLoadMoreRecyclerView.setHasMore(true);
        if (books == null || books.size() <= 0) {
            realAdapter.addNetBeanItem(null, true);
            rankAdapter.notifyDataSetChanged();
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) statusView.getLayoutParams();
            params.topMargin = DimensionPixelUtil.dip2px(this, 98);
            statusView.setLayoutParams(params);
            statusView.showEmpty(getString(R.string.string_empty_combination));
        } else {
            statusView.showSuccess();
            realAdapter.addNetBeanItem(books, true);
            rankAdapter.notifyDataSetChanged();
        }

    }

    @Override
    public void setLoadProgressMarginTop(boolean isExistSub) {
        if (isExistSub) {

            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) statusView.getLayoutParams();
            params.topMargin = DimensionPixelUtil.dip2px(this, 98);
            statusView.setLayoutParams(params);
        } else {
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) statusView.getLayoutParams();
            params.topMargin = DimensionPixelUtil.dip2px(this, 48);
            statusView.setLayoutParams(params);
        }
    }

    @Override
    public void setPullRefreshComplete() {
        pullLoadMoreRecyclerView.setPullLoadMoreCompleted();
    }

    @Override
    public void removeRecycleViewHeader() {
        pullLoadMoreRecyclerView.removeAllHeaderView();
    }

    @Override
    public void showNoNetView() {
        initNetErrorStatus();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (rankTopPresenter != null) {
            rankTopPresenter.destroy();
        }
        if (rankTopView != null) {
            rankTopView.destory();
        }
    }

    /**
     * 加载更多监听
     */
    private class MyPullLoadMoreListener implements PullLoadMoreRecycleLayout.PullLoadMoreListener {
        @Override
        public void onRefresh() {
            initNetErrorStatus();
            pullLoadMoreRecyclerView.postDelayed(new Runnable() {
                @Override
                public void run() {
                    pullLoadMoreRecyclerView.setRefreshing(false);
                }
            }, 3000);

            rankTopPresenter.getFirstPageRankTopInfo(true);
            if (isShowTips) {
                pullLoadMoreRecyclerView.removeFooterView(pw1View);
                isShowTips = false;
            }
        }

        @Override
        public void onLoadMore() {
            initNetErrorStatus();
            if (!NetworkUtils.getInstance().checkNet()) {
                //showMessage(R.string.net_work_notuse);
                pullLoadMoreRecyclerView.setPullLoadMoreCompleted();
                return;
            }
            //获取当前的parentId,subId
            rankTopPresenter.getClickRankTopLoadMoreInfo(rankTopPresenter.getLoadFailParentId(), rankTopPresenter.getLoadFailSubId());
        }
    }

    /**
     * item 监听
     */
    private class MyOnItemClickListener implements CommonBookListRecycleViewAdapter.OnItemClickListener {
        @Override
        public void onItemClick(View view, BeanBookInfo bean, int position) {
            if (bean != null) {
                //click 打点
                HashMap<String, String> hashMap = new HashMap<>();
                hashMap.put("index", position + "");
                DzLog.getInstance().logClick(LogConstants.MODULE_PHBB, getPI(), bean.bookId, hashMap, "");
                //固化信息
                WhiteListWorker.resetBookSourceFrom(RankTopActivity.this);
                BookDetailActivity.launch(getActivity(), bean.bookId, bean.bookName);
            }
        }
    }

    /**
     * 左边item监听
     */
    private class MyLeftClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            finish();
        }
    }

    private void initNetErrorStatus() {
        if (!NetworkUtils.getInstance().checkNet() && rankAdapter != null && rankAdapter.getItemCount() > 0) {
            initNetView();
        } else {
            destoryNetView();
        }
    }

    private void initNetView() {
        if (netErrorTopView == null) {
            netErrorTopView = new NetErrorTopView(getContext());
            netErrorTopLayout.addView(netErrorTopView, 0, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, DimensionPixelUtil.dip2px(getContext(), 48)));
        }
    }

    private void destoryNetView() {
        if (netErrorTopView != null) {
            netErrorTopLayout.removeView(netErrorTopView);
            netErrorTopView = null;
        }
    }
}
