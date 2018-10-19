package com.dzbook.activity.continuous;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.dzbook.BaseSwipeBackActivity;
import com.dzbook.lib.utils.CompatUtils;
import com.dzbook.mvp.UI.AutoOrderVipListUI;
import com.dzbook.mvp.presenter.AutoOrderVipListPresenter;
import com.dzbook.utils.DimensionPixelUtil;
import com.dzbook.utils.NetworkUtils;
import com.dzbook.view.DianZhongCommonTitle;
import com.dzbook.view.PullLoadMoreRecycleLayout;
import com.dzbook.view.common.NetErrorTopView;
import com.dzbook.view.common.StatusView;
import com.ishugui.R;
import com.iss.view.common.ToastAlone;

import java.util.List;

import hw.sdk.net.bean.vip.VipContinueOpenHisBean;

/**
 * 连续包月状态历史列表
 *
 * @author Kongxp 2018/4/23
 */

public class AutoOrderVipListActivity extends BaseSwipeBackActivity implements AutoOrderVipListUI {

    private static final String TAG = "AutoOrderVipListActivity";
    private PullLoadMoreRecycleLayout mRecyclerView;
    private StatusView mStatusView;
    private DianZhongCommonTitle mCommonTitle;

    private AutoOrderVipListAdapter mAdapter;
    private AutoOrderVipListPresenter mPresenter;

    private NetErrorTopView netErrorTopView;
    private LinearLayout netErrorTopLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ac_auto_order_vip_list);
    }

    @Override
    public String getTagName() {
        return TAG;
    }

    @Override
    protected void initData() {
        mRecyclerView.setLinearLayout();
        mAdapter = new AutoOrderVipListAdapter();
        mRecyclerView.setAdapter(mAdapter);
        mStatusView.showLoading();
        mPresenter = new AutoOrderVipListPresenter(this);
        mPresenter.getVipStateDataFromNet(true, true);
    }

    @Override
    protected void initView() {
        netErrorTopLayout = findViewById(R.id.net_error_layout_view);
        mCommonTitle = findViewById(R.id.include_top_title_item);
        mRecyclerView = findViewById(R.id.pullLoadMoreRecyclerView);
        mStatusView = findViewById(R.id.defaultview_recharge_empty);
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
        mStatusView.setNetErrorClickListener(new StatusView.NetErrorClickListener() {
            @Override
            public void onNetErrorEvent(View v) {
                mPresenter.resetIndex(false);
                mPresenter.getVipStateDataFromNet(true, true);
            }
        });
        mRecyclerView.setOnPullLoadMoreListener(new PullLoadMoreRecycleLayout.PullLoadMoreListener() {
            @Override
            public void onRefresh() {
                initNetErrorStatus();
                if (NetworkUtils.getInstance().checkNet()) {
                    mPresenter.resetIndex(false);
                    mPresenter.getVipStateDataFromNet(true, false);
                } else {
                    mRecyclerView.setPullLoadMoreCompleted();
                }
            }

            @Override
            public void onLoadMore() {
                initNetErrorStatus();
                if (NetworkUtils.getInstance().checkNet()) {
                    mPresenter.resetIndex(true);
                    mPresenter.getVipStateDataFromNet(false, false);
                } else {
                    mRecyclerView.setPullLoadMoreCompleted();
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
    public void setHasMore(final boolean hasMore) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mRecyclerView.setHasMore(hasMore);
            }
        });
    }

    @Override
    public void showMessage(final String message) {
        ToastAlone.showShort(message);
    }

    /**
     * 设置无网络情况下的展示
     */
    private void setNoNetView() {
        mStatusView.showNetError();
    }

    /**
     * 设置无数据情况下的显示
     */
    private void setNoDataEmptyView() {
        mStatusView.showEmpty(getResources().getString(R.string.string_empty_vip_record), CompatUtils.getDrawable(this, R.drawable.hw_empty_default));
    }


    @Override
    public void setVipList(final List<VipContinueOpenHisBean> list, final boolean refresh) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mAdapter.append(list, refresh);
                mStatusView.showSuccess();
                if (mRecyclerView.getVisibility() == View.GONE) {
                    mRecyclerView.setVisibility(View.VISIBLE);
                }
            }
        });
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
    public void dismissLoadProgress() {
        if (mStatusView.getVisibility() == View.VISIBLE) {
            mStatusView.setVisibility(View.GONE);
        }
    }

    @Override
    public void showLoadProgress() {
        if (mStatusView.getVisibility() == View.GONE) {
            mStatusView.setVisibility(View.VISIBLE);
            mStatusView.showLoading();
        }
    }

    private void initNetErrorStatus() {
        if (!NetworkUtils.getInstance().checkNet() && mAdapter != null && mAdapter.getItemCount() > 0) {
            initNetView();
        } else {
            destoryNetView();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
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

    private void destoryNetView() {
        if (netErrorTopView != null) {
            netErrorTopLayout.removeView(netErrorTopView);
            netErrorTopView = null;
        }
    }
}
