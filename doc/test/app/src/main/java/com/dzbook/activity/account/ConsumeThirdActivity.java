package com.dzbook.activity.account;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.dzbook.BaseSwipeBackActivity;
import com.dzbook.lib.utils.CompatUtils;
import com.dzbook.mvp.UI.ConsumeThirdUI;
import com.dzbook.mvp.presenter.ConsumeThirdPresenter;
import com.dzbook.utils.DimensionPixelUtil;
import com.dzbook.utils.NetworkUtils;
import com.dzbook.view.DianZhongCommonTitle;
import com.dzbook.view.PullLoadMoreRecycleLayout;
import com.dzbook.view.common.NetErrorTopView;
import com.dzbook.view.common.StatusView;
import com.dzbook.view.store.Pw1View;
import com.ishugui.R;
import com.iss.app.BaseActivity;

import java.util.List;

import hw.sdk.net.bean.consume.ConsumeThirdBean;

/**
 * 消费记录三级
 *
 * @author lizz 2018/4/20.
 */
public class ConsumeThirdActivity extends BaseSwipeBackActivity implements ConsumeThirdUI {

    private static final String TAG = "ConsumeThirdActivity";
    private DianZhongCommonTitle mCommonTitle;

    private PullLoadMoreRecycleLayout pullLoadMoreRecyclerView;

    private StatusView statusView;

    private ConsumeThirdPresenter mPresenter;

    private ConsumeThirdAdapter mAdapter;
    private boolean isShowTips;
    private Pw1View pw1View;

    private NetErrorTopView netErrorTopView;
    private LinearLayout netErrorTopLayout;

    /**
     * 打开消费记录三级
     *
     * @param activity  activity
     * @param consumeID consumeID
     * @param bookId    bookId
     */
    public static void launch(Activity activity, String consumeID, String bookId) {
        Intent intent = new Intent(activity, ConsumeThirdActivity.class);
        intent.putExtra(ConsumeThirdPresenter.CONSUME_ID, consumeID);
        intent.putExtra(ConsumeThirdPresenter.BOOK_ID, bookId);
        activity.startActivity(intent);
        BaseActivity.showActivity(activity);
    }


    @Override
    protected void initView() {
        netErrorTopLayout = findViewById(R.id.net_error_layout_view);
        mCommonTitle = findViewById(R.id.commontitle);
        pullLoadMoreRecyclerView = findViewById(R.id.pullLoadMoreRecyclerView);
        statusView = findViewById(R.id.defaultview_nonet);
        pw1View = new Pw1View(this);
    }


    @Override
    public String getTagName() {
        return TAG;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ac_consume_third_summary);
    }


    @Override
    protected void setListener() {
        mCommonTitle.setLeftClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        pullLoadMoreRecyclerView.setOnPullLoadMoreListener(new PullLoadMoreRecycleLayout.PullLoadMoreListener() {
            @Override
            public void onRefresh() {

            }

            @Override
            public void onLoadMore() {
                initNetErrorStatus();
                if (NetworkUtils.getInstance().checkNet()) {
                    mPresenter.loadMoreNetConsumeData();
                } else {
                    pullLoadMoreRecyclerView.setPullLoadMoreCompleted();
                }
            }
        });
        statusView.setNetErrorClickListener(new StatusView.NetErrorClickListener() {
            @Override
            public void onNetErrorEvent(View v) {
                statusView.showSuccess();
                mPresenter.getNetConsumeData(true);
            }
        });
    }

    @Override
    protected void initData() {
        pullLoadMoreRecyclerView.setAllReference(false);
        mPresenter = new ConsumeThirdPresenter(this);
        mPresenter.getParams();
        pullLoadMoreRecyclerView.setLinearLayout();

        mAdapter = new ConsumeThirdAdapter(this);
        pullLoadMoreRecyclerView.setAdapter(mAdapter);
        mPresenter.getNetConsumeData(true);

    }

    @Override
    public void dismissLoadProgress() {
        if (statusView.getVisibility() == View.VISIBLE) {
            statusView.setVisibility(View.GONE);
        }
    }


    @Override
    public void showNoDataView() {
        statusView.setVisibility(View.VISIBLE);
        statusView.showEmpty(getResources().getString(R.string.str_no_consumption_record),
                "", CompatUtils.getDrawable(this, R.drawable.hw_no_money));
    }

    @Override
    public void showLoadProgress() {
        if (statusView.getVisibility() == View.GONE) {
            statusView.setVisibility(View.VISIBLE);
            statusView.showLoading();
        }
    }

    @Override
    public void showNoNetView() {
        statusView.setVisibility(View.VISIBLE);
        statusView.showNetError();
    }

    @Override
    public void setBookConsumeSum(List<ConsumeThirdBean> list, boolean refresh) {
        mAdapter.addItems(list, refresh);
    }

    @Override
    public void setHasMore(boolean hasMore) {
        pullLoadMoreRecyclerView.setHasMore(hasMore);
    }

    @Override
    public void stopLoadMore() {
        pullLoadMoreRecyclerView.setPullLoadMoreCompleted();
    }

    @Override
    public void showAllTips() {
        if (!isShowTips) {
            pullLoadMoreRecyclerView.addFooterView(pw1View);
            isShowTips = true;
        }
    }

    private void initNetView() {
        if (netErrorTopView == null) {
            netErrorTopView = new NetErrorTopView(getContext());
            netErrorTopLayout.addView(netErrorTopView, 0, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, DimensionPixelUtil.dip2px(getContext(), 48)));
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
