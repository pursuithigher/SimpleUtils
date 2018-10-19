package com.dzbook.activity.account;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.dzbook.BaseSwipeBackActivity;
import com.dzbook.lib.utils.CompatUtils;
import com.dzbook.mvp.UI.ConsumeSecondUI;
import com.dzbook.mvp.presenter.ConsumeSecondPresenter;
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

import hw.sdk.net.bean.consume.ConsumeSecondBean;

/**
 * 消费记录二级页面
 *
 * @author lizz 2018/4/20.
 */
public class ConsumeSecondActivity extends BaseSwipeBackActivity implements ConsumeSecondUI, PullLoadMoreRecycleLayout.PullLoadMoreListener {

    private static final String TAG = "ConsumeSecondActivity";
    private DianZhongCommonTitle mCommonTitle;

    private PullLoadMoreRecycleLayout pullLoadMoreRecyclerView;

    private StatusView statusView;

    private ConsumeSecondPresenter mPresenter;

    private ConsumeSecondAdapter mAdapter;

    private Pw1View pw1View;
    private boolean isShowTips;

    private NetErrorTopView netErrorTopView;
    private LinearLayout netErrorTopLayout;

    /**
     * 打开消费记录二级
     *
     * @param activity activity
     * @param nextId   nextId
     * @param type     type
     */
    public static void launch(Activity activity, String nextId, String type) {
        Intent intent = new Intent(activity, ConsumeSecondActivity.class);
        intent.putExtra(ConsumeSecondPresenter.TYPE, type);
        intent.putExtra(ConsumeSecondPresenter.NEXT_ID, nextId);
        activity.startActivity(intent);
        BaseActivity.showActivity(activity);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ac_consume_second_summary);
    }

    @Override
    public String getTagName() {
        return TAG;
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
    protected void initData() {
        pullLoadMoreRecyclerView.setAllReference(false);
        mPresenter = new ConsumeSecondPresenter(this);
        mPresenter.getParams();
        pullLoadMoreRecyclerView.setLinearLayout();

        mAdapter = new ConsumeSecondAdapter(this, mPresenter);
        pullLoadMoreRecyclerView.setAdapter(mAdapter);
        mPresenter.getNetConsumeData(true);

    }

    @Override
    public void showNoDataView() {
        statusView.showEmpty(getResources().getString(TextUtils.equals(mPresenter.getType(), "3") ? R.string.str_no_consumption_record_vip : R.string.str_no_consumption_record_activity),
                CompatUtils.getDrawable(this, R.drawable.hw_no_money));
    }

    @Override
    public void showNoNetView() {
        statusView.showNetError();
    }


    @Override
    public void setBookConsumeSum(List<ConsumeSecondBean> list, boolean refresh) {
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
    public void showAllTips() {
        if (!isShowTips) {
            pullLoadMoreRecyclerView.addFooterView(pw1View);
            isShowTips = true;
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
    protected void setListener() {
        mCommonTitle.setLeftClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        statusView.setNetErrorClickListener(new StatusView.NetErrorClickListener() {
            @Override
            public void onNetErrorEvent(View v) {
                statusView.showSuccess();
                mPresenter.getNetConsumeData(true);
            }
        });
        pullLoadMoreRecyclerView.setOnPullLoadMoreListener(this);

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
}
