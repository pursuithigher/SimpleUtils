package com.dzbook.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.dzbook.activity.account.ConsumeBookSumAdapter;
import com.dzbook.fragment.main.BaseFragment;
import com.dzbook.lib.utils.CompatUtils;
import com.dzbook.mvp.UI.ConsumeBookSumUI;
import com.dzbook.mvp.presenter.ConsumeBookSumPresenter;
import com.dzbook.utils.DimensionPixelUtil;
import com.dzbook.utils.NetworkUtils;
import com.dzbook.view.PullLoadMoreRecycleLayout;
import com.dzbook.view.common.NetErrorTopView;
import com.dzbook.view.common.StatusView;
import com.dzbook.view.store.Pw1View;
import com.ishugui.R;

import java.util.ArrayList;
import java.util.List;

import hw.sdk.net.bean.consume.ConsumeBookSumBean;

/**
 * 消费书籍
 *
 * @author KongXP on 2018/4/20.
 */
public class ConsumeBookFirstFragment extends BaseFragment implements ConsumeBookSumUI {

    private PullLoadMoreRecycleLayout pullLoadMoreRecyclerView;

    private StatusView statusView;

    private ConsumeBookSumPresenter mPresenter;

    private ConsumeBookSumAdapter mAdapter;

    private List<ConsumeBookSumBean> consumeBookSumBeans = new ArrayList<>(16);

    private Pw1View pw1View;
    private NetErrorTopView netErrorTopView;
    private LinearLayout netErrorTopLayout;
    private boolean isShowTips;

    @Override
    public String getTagName() {
        return "GiftExchangeFragment";
    }

    @Override
    protected View inflate(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_consume_first_view, container, false);
    }

    @Override
    protected void initView(View uiView) {
        netErrorTopLayout = uiView.findViewById(R.id.net_error_layout_view);
        pullLoadMoreRecyclerView = uiView.findViewById(R.id.pullLoadMoreRecyclerView);
        statusView = uiView.findViewById(R.id.defaultview_nonet);
        pw1View = new Pw1View(getActivity());
    }

    @Override
    protected void initData(View uiView) {
        pullLoadMoreRecyclerView.setAllReference(false);
        mPresenter = new ConsumeBookSumPresenter(this);
        pullLoadMoreRecyclerView.setLinearLayout();

        mAdapter = new ConsumeBookSumAdapter(getActivity(), mPresenter);
        pullLoadMoreRecyclerView.setAdapter(mAdapter);
        mPresenter.getNetConsumeBookData(true);
    }

    @Override
    protected void setListener(View uiView) {
        pullLoadMoreRecyclerView.setOnPullLoadMoreListener(new PullLoadMoreRecycleLayout.PullLoadMoreListener() {
            @Override
            public void onRefresh() {
                initNetErrorStatus();
            }

            @Override
            public void onLoadMore() {
                initNetErrorStatus();
                if (NetworkUtils.getInstance().checkNet()) {
                    mPresenter.loadMoreNetConsumeBookData();
                } else {
                    pullLoadMoreRecyclerView.setPullLoadMoreCompleted();
                }
            }
        });
        statusView.setNetErrorClickListener(new StatusView.NetErrorClickListener() {
            @Override
            public void onNetErrorEvent(View v) {
                statusView.showSuccess();
                mPresenter.getNetConsumeBookData(true);
            }
        });
    }

    @Override
    public void showNoNetView() {
        if (!NetworkUtils.getInstance().checkNet() && mAdapter != null && mAdapter.getItemCount() > 0) {
            initNetErrorStatus();
        } else {
            statusView.showNetError();
        }
    }

    @Override
    public void setBookConsumeSum(List<ConsumeBookSumBean> list, boolean refresh) {
        if (refresh) {
            consumeBookSumBeans.clear();
        }
        consumeBookSumBeans.addAll(list);
        mAdapter.addItems(list, refresh);
    }

    @Override
    public void stopLoadMore() {
        pullLoadMoreRecyclerView.setPullLoadMoreCompleted();
    }

    @Override
    public void setHasMore(boolean hasMore) {
        pullLoadMoreRecyclerView.setHasMore(hasMore);
    }

    @Override
    public void showLoadProgress() {
        if (statusView.getVisibility() == View.GONE) {
            statusView.setVisibility(View.VISIBLE);
            statusView.showLoading();
        }
    }

    @Override
    public void showNoDataView() {
        statusView.showEmpty(getResources().getString(R.string.str_no_consumption_record), "", CompatUtils.getDrawable(getActivity(), R.drawable.hw_no_money));
    }

    @Override
    public void dismissLoadProgress() {
        if (statusView.getVisibility() == View.VISIBLE) {
            statusView.setVisibility(View.GONE);
        }
    }

    @Override
    public void showAllTips() {
        if (!isShowTips) {
            pullLoadMoreRecyclerView.addFooterView(pw1View);
            isShowTips = true;
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mPresenter != null) {
            mPresenter.destroy();
        }
    }

    private void initNetView() {
        if (netErrorTopView == null) {
            netErrorTopView = new NetErrorTopView(getContext());
            netErrorTopLayout.addView(netErrorTopView, 0, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, DimensionPixelUtil.dip2px(getContext(), 48)));
        }
    }

    private void initNetErrorStatus() {
        if (!NetworkUtils.getInstance().checkNet() && mAdapter != null && mAdapter.getItemCount() > 0) {
            initNetView();
        } else {
            destoryNetView();
        }
    }

    private void destoryNetView() {
        if (netErrorTopView != null) {
            netErrorTopLayout.removeView(netErrorTopView);
            netErrorTopView = null;
        }
    }

}
