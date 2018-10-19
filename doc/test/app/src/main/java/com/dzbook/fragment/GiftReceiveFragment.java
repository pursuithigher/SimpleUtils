package com.dzbook.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.dzbook.fragment.main.BaseFragment;
import com.dzbook.lib.utils.CompatUtils;
import com.dzbook.mvp.UI.GiftUI;
import com.dzbook.mvp.presenter.GiftPresenter;
import com.dzbook.utils.DimensionPixelUtil;
import com.dzbook.utils.NetworkUtils;
import com.dzbook.utils.hw.LoginUtils;
import com.dzbook.view.PullLoadMoreRecycleLayout;
import com.dzbook.view.common.NetErrorTopView;
import com.dzbook.view.common.StatusView;
import com.dzbook.view.store.Pw1View;
import com.ishugui.R;
import com.iss.view.common.ToastAlone;

import java.util.List;

import hw.sdk.net.bean.gift.GiftListBean;

/**
 * 礼物fragment
 *
 * @author KongXP on 2018/4/20.
 */
public class GiftReceiveFragment extends BaseFragment implements View.OnClickListener, GiftUI {
    private PullLoadMoreRecycleLayout mRecyclerView;
    private StatusView statusView;

    private GiftListAdapter mAdapter;
    private GiftPresenter mPresenter;
    private boolean isShowTips;
    private Pw1View pw1View;
    private NetErrorTopView netErrorTopView;
    private LinearLayout netErrorTopLayout;

    @Override
    public String getTagName() {
        return "GiftReceiveFragment";
    }

    @Override
    protected View inflate(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_gift_receive, container, false);
    }

    /**
     * 刷新列表
     */
    public void refreshListView() {
        if (null == mPresenter) {
            return;
        }
        isNotNetGetNetInfo();
    }

    @Override
    public void onResume() {
        super.onResume();
        isNotNetGetNetInfo();
    }

    private void isNotNetGetNetInfo() {
        if (NetworkUtils.getInstance().checkNet()) {
            mAdapter.clearData();
            mPresenter.resetIndex(false);
            getNetInfo(true, true);
        } else {
            setNoNetView();
            destroyNetView();
        }
    }

    @Override
    protected void initView(View uiView) {
        netErrorTopLayout = uiView.findViewById(R.id.net_error_layout_view);
        mRecyclerView = uiView.findViewById(R.id.pullLoadMoreRecyclerView);
        statusView = uiView.findViewById(R.id.defaultview_recharge_empty);
        pw1View = new Pw1View(getContext());
    }

    @Override
    protected void initData(View uiView) {
        mRecyclerView.setLinearLayout();
        mAdapter = new GiftListAdapter();
        mRecyclerView.setAdapter(mAdapter);
        mPresenter = new GiftPresenter(this);
    }

    @Override
    protected void setListener(View uiView) {
        statusView.setNetErrorClickListener(new StatusView.NetErrorClickListener() {
            @Override
            public void onNetErrorEvent(View v) {
                mPresenter.resetIndex(false);
                getNetInfo(true, true);
            }
        });
        mRecyclerView.setOnPullLoadMoreListener(new PullLoadMoreRecycleLayout.PullLoadMoreListener() {
            @Override
            public void onRefresh() {
                initNetErrorStatus();
                if (NetworkUtils.getInstance().checkNet()) {
                    mPresenter.resetIndex(false);
                    getNetInfo(true, false);
                    if (isShowTips) {
                        mRecyclerView.removeFooterView(pw1View);
                        isShowTips = false;
                    }
                } else {
                    mRecyclerView.setPullLoadMoreCompleted();
                }
            }

            @Override
            public void onLoadMore() {
                initNetErrorStatus();
                if (NetworkUtils.getInstance().checkNet()) {
                    mPresenter.resetIndex(true);
                    getNetInfo(false, false);
                } else {
                    mRecyclerView.setPullLoadMoreCompleted();
                }
            }
        });
    }

    @Override
    public void showNoNetView() {
        if (mAdapter.getItemCount() <= 0) {
            mRecyclerView.setVisibility(View.GONE);
            setNoNetView();
        }
    }

    @Override
    public void setRecordList(final List<GiftListBean> list, final boolean refresh) {
        mAdapter.append(list, refresh);
        if (statusView.getVisibility() == View.VISIBLE) {
            statusView.setVisibility(View.GONE);
        }

        if (mRecyclerView.getVisibility() == View.GONE) {
            mRecyclerView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void showEmptyView() {
        if (mAdapter.getItemCount() <= 0) {
            mRecyclerView.setVisibility(View.GONE);
            setNoDataEmptyView();
        } else {
            initNetErrorStatus();
        }
    }

    @Override
    public void stopLoadMore() {
        mRecyclerView.setPullLoadMoreCompleted();
    }

    @Override
    public void setHasMore(final boolean hasMore) {
        mRecyclerView.setHasMore(hasMore);
    }

    @Override
    public void showMessage(final String message) {
        ToastAlone.showShort(message);
    }

    /**
     * 设置无网络情况下的展示
     */
    private void setNoNetView() {
        statusView.setVisibility(View.VISIBLE);
        statusView.showNetError();
    }

    /**
     * 设置无数据情况下的显示
     */
    private void setNoDataEmptyView() {
        statusView.setVisibility(View.VISIBLE);
        if (getContext() != null) {
            statusView.showEmpty(getResources().getString(R.string.string_empty_gift), "", CompatUtils.getDrawable(getContext(), R.drawable.hw_no_gift));
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
    public void dismissLoadProgress() {
        if (statusView.getVisibility() == View.VISIBLE) {
            statusView.setVisibility(View.GONE);
        }
    }

    @Override
    public void showAllTips() {
        if (!isShowTips) {
            mRecyclerView.addFooterView(pw1View);
            isShowTips = true;
        }
    }

    @Override
    public void setResultMsg(String pResult) {

    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mPresenter != null) {
            mPresenter.destroy();
        }
    }

    private void getNetInfo(boolean refresh, boolean isFirstLoad) {
        if (LoginUtils.getInstance().checkLoginStatus(getContext())) {
            mPresenter.getGiftReceiveDataFromNet(refresh, isFirstLoad);
        }
    }

    private void initNetErrorStatus() {
        if (!NetworkUtils.getInstance().checkNet() && mAdapter != null && mAdapter.getItemCount() > 0) {
            initNetView();
        } else {
            destroyNetView();
        }
    }

    private void initNetView() {
        if (netErrorTopView == null) {
            netErrorTopView = new NetErrorTopView(getContext());
            netErrorTopLayout.addView(netErrorTopView, 0, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, DimensionPixelUtil.dip2px(getContext(), 48)));
        }
    }

    private void destroyNetView() {
        if (netErrorTopView != null) {
            netErrorTopLayout.removeView(netErrorTopView);
            netErrorTopView = null;
        }
    }
}
