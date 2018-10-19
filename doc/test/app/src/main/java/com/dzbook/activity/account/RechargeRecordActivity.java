package com.dzbook.activity.account;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.dzbook.BaseSwipeBackActivity;
import com.dzbook.lib.utils.CompatUtils;
import com.dzbook.mvp.UI.PersonRechargeRecordUI;
import com.dzbook.mvp.presenter.PersonRechargeRecordPresenter;
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

import hw.sdk.net.bean.record.RechargeRecordBean;

/**
 * 充值记录
 *
 * @author by lizz on 2018/04/20.
 */
public class RechargeRecordActivity extends BaseSwipeBackActivity implements PersonRechargeRecordUI {
    /**
     * tag
     */
    public static final String TAG = "RechargeRecordActivity";
    private PullLoadMoreRecycleLayout mRecyclerView;
    private StatusView mStatusView;
    private DianZhongCommonTitle mCommonTitle;

    private RechargeRecordAdapter mAdapter;
    private PersonRechargeRecordPresenter mPresenter;
    private Pw1View pw1View;
    private NetErrorTopView netErrorTopView;
    private LinearLayout netErrorTopLayout;
    private boolean isShowTips;


    @Override
    public String getTagName() {
        return TAG;
    }


    @Override
    protected void initView() {
        netErrorTopLayout = findViewById(R.id.net_error_layout_view);
        mCommonTitle = findViewById(R.id.include_top_title_item);
        mRecyclerView = findViewById(R.id.pullLoadMoreRecyclerView);
        mStatusView = findViewById(R.id.defaultview_recharge_empty);
        pw1View = new Pw1View(this);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rechargerecord);
    }


    @Override
    protected void initData() {
        mRecyclerView.setLinearLayout();
        mAdapter = new RechargeRecordAdapter();
        mRecyclerView.setAdapter(mAdapter);
        mPresenter = new PersonRechargeRecordPresenter(this);
        mPresenter.getRechargeRecordDataFromNet(true, true);
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
                mPresenter.getRechargeRecordDataFromNet(true, true);
            }
        });
        mRecyclerView.setOnPullLoadMoreListener(new PullLoadMoreRecycleLayout.PullLoadMoreListener() {
            @Override
            public void onRefresh() {
                initNetErrorStatus();
                if (NetworkUtils.getInstance().checkNet()) {
                    mPresenter.resetIndex(false);
                    mPresenter.getRechargeRecordDataFromNet(true, false);
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
                    mPresenter.getRechargeRecordDataFromNet(false, false);
                } else {
                    mRecyclerView.setPullLoadMoreCompleted();
                }
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
                }
            }
        });
    }

    @Override
    public void setRecordList(final List<RechargeRecordBean> list, final boolean refresh) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mAdapter.append(list, refresh);
                if (mStatusView.getVisibility() == View.VISIBLE) {
                    mStatusView.setVisibility(View.GONE);
                }

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
    public void setHasMore(final boolean hasMore) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mRecyclerView.setHasMore(hasMore);
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

    /**
     * 设置无网络情况下的展示
     */
    private void setNoNetView() {
        if (!NetworkUtils.getInstance().checkNet() && mAdapter != null && mAdapter.getItemCount() > 0) {
            initNetErrorStatus();
        } else {
            mStatusView.showNetError();
        }
    }

    @Override
    public void showMessage(final String message) {
        ToastAlone.showShort(message);
    }


    @Override
    public void dismissLoadProgress() {
        if (mStatusView.getVisibility() == View.VISIBLE) {
            mStatusView.setVisibility(View.GONE);
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
        if (mStatusView.getVisibility() == View.GONE) {
            mStatusView.setVisibility(View.VISIBLE);
            mStatusView.showLoading();
        }
    }

    /**
     * 数据空页面
     */
    public void setNoDataEmptyView() {
        mStatusView.showEmpty(getResources().getString(R.string.str_no_recharge_record),
                CompatUtils.getDrawable(this, R.drawable.hw_no_money));
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
