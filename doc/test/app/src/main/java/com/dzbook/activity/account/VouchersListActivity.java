package com.dzbook.activity.account;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.dzbook.BaseSwipeBackActivity;
import com.dzbook.lib.utils.CompatUtils;
import com.dzbook.mvp.UI.PersonVouchersListUI;
import com.dzbook.mvp.presenter.PersonVouchersListPresenter;
import com.dzbook.utils.DimensionPixelUtil;
import com.dzbook.utils.NetworkUtils;
import com.dzbook.view.DianZhongCommonTitle;
import com.dzbook.view.PullLoadMoreRecycleLayout;
import com.dzbook.view.common.NetErrorTopView;
import com.dzbook.view.common.StatusView;
import com.dzbook.view.store.Pw1View;
import com.ishugui.R;
import com.iss.view.common.ToastAlone;

import java.util.List;

import hw.sdk.net.bean.vouchers.VouchersListBean;

/**
 * 代金券
 *
 * @author KongXP on 2018/4/25.
 */
public class VouchersListActivity extends BaseSwipeBackActivity implements PersonVouchersListUI {

    private static final String TAG = "VouchersListActivity";
    private PullLoadMoreRecycleLayout mRecyclerView;
    private StatusView statusView;
    private DianZhongCommonTitle mCommonTitle;

    private VouchersListAdapter mAdapter;
    private PersonVouchersListPresenter mPresenter;
    private Pw1View pw1View;
    private boolean isShowTips;
    private NetErrorTopView netErrorTopView;
    private LinearLayout netErrorTopLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vouchers_list);
    }

    @Override
    protected void initView() {
        netErrorTopLayout = findViewById(R.id.net_error_layout_view);
        mCommonTitle = findViewById(R.id.include_top_title_item);
        mRecyclerView = findViewById(R.id.pullLoadMoreRecyclerView);
        statusView = findViewById(R.id.defaultview_recharge_empty);
        pw1View = new Pw1View(this);
    }


    @Override
    public String getTagName() {
        return TAG;
    }

    @Override
    protected void setListener() {
        mCommonTitle.setLeftClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        finish();
                    }
                });
        statusView.setNetErrorClickListener(new StatusView.NetErrorClickListener() {
            @Override
            public void onNetErrorEvent(View v) {
                mPresenter.resetIndex(false);
                mPresenter.getVouchersListDataFromNet(true, true);
            }
        });
        mRecyclerView.setOnPullLoadMoreListener(new PullLoadMoreRecycleLayout.PullLoadMoreListener() {
            @Override
            public void onRefresh() {
                initNetErrorStatus();
                if (NetworkUtils.getInstance().checkNet()) {
                    mPresenter.resetIndex(false);
                    mPresenter.getVouchersListDataFromNet(true, false);
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
                    mPresenter.getVouchersListDataFromNet(false, false);
                } else {
                    mRecyclerView.setPullLoadMoreCompleted();
                }
            }
        });

    }

    @Override
    protected void initData() {
        mRecyclerView.setLinearLayout();
        mAdapter = new VouchersListAdapter();
        mRecyclerView.setAdapter(mAdapter);
        mPresenter = new PersonVouchersListPresenter(this);
        mPresenter.getVouchersListDataFromNet(true, true);
    }


    @Override
    public void showNoNetView() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mAdapter.getItemCount() <= 0) {
                    mRecyclerView.setVisibility(View.GONE);
                    setNoNetView();
                } else {
                    initNetErrorStatus();
                }
            }
        });
    }

    @Override
    public void stopLoadMore() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mRecyclerView.setPullLoadMoreCompleted();
            }
        });
    }

    @Override
    public void setHasMore(final boolean hasMore) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mRecyclerView.setHasMore(hasMore);
            }
        });
    }

    @Override
    public void setRecordList(final List<VouchersListBean> list, final boolean refresh) {
        runOnUI(list, refresh);
    }

    private void runOnUI(final List<VouchersListBean> list, final boolean refresh) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mAdapter.append(list, refresh);
                if (statusView.getVisibility() == View.VISIBLE) {
                    statusView.setVisibility(View.GONE);
                }

                if (mRecyclerView.getVisibility() == View.GONE) {
                    mRecyclerView.setVisibility(View.VISIBLE);
                }
            }
        });
    }


    /**
     * 设置无网络情况下的展示
     */
    private void setNoNetView() {
        statusView.setVisibility(View.VISIBLE);
        statusView.showNetError();
    }

    @Override
    public void showEmptyView() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mAdapter.getItemCount() <= 0) {
                    mRecyclerView.setVisibility(View.GONE);
                    setNoDataEmptyView();
                }
            }
        });
    }


    @Override
    public void showMessage(final String message) {
        ToastAlone.showShort(message);
    }


    /**
     * 设置无数据情况下的显示
     */
    private void setNoDataEmptyView() {
        statusView.setVisibility(View.VISIBLE);
        statusView.showEmpty(getResources().getString(R.string.string_empty_cash_coupon),
                CompatUtils.getDrawable(this, R.drawable.hw_no_money));
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
    public void showLoadProgress() {
        if (statusView.getVisibility() == View.GONE) {
            statusView.setVisibility(View.VISIBLE);
            statusView.showLoading();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mPresenter != null) {
            mPresenter.destroy();
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
