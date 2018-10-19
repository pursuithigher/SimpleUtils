package com.dzbook.activity.reader;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.dzbook.BaseSwipeBackActivity;
import com.dzbook.activity.detail.BookDetailActivity;
import com.dzbook.adapter.CommonBookListRecycleViewAdapter;
import com.dzbook.mvp.UI.ChaseRecommendMoreUI;
import com.dzbook.mvp.presenter.ChaseRecommendMorePresenter;
import com.dzbook.utils.DimensionPixelUtil;
import com.dzbook.utils.ListUtils;
import com.dzbook.utils.NetworkUtils;
import com.dzbook.view.DianZhongCommonTitle;
import com.dzbook.view.PullLoadMoreRecycleLayout;
import com.dzbook.view.common.NetErrorTopView;
import com.dzbook.view.common.StatusView;
import com.dzbook.view.store.Pw1View;
import com.ishugui.R;
import com.iss.app.BaseActivity;

import hw.sdk.net.bean.BeanBookInfo;
import hw.sdk.net.bean.reader.MoreRecommendBook;


/**
 * 追跟-更多
 *
 * @author lizhongzhong 2018/3/9.
 */
public class ChaseRecommendMoreActivity extends BaseSwipeBackActivity implements ChaseRecommendMoreUI {
    /**
     * tag
     */
    public static final String TAG = "ChaseRecommendMoreActivity";
    private ChaseRecommendMorePresenter mPresenter;

    private DianZhongCommonTitle commonTitle;

    private CommonBookListRecycleViewAdapter mBookListAdapter;

    private PullLoadMoreRecycleLayout pullLoadRecycler;

    private StatusView statusView;

    private NetErrorTopView netErrorTopView;
    private LinearLayout netErrorTopLayout;

    private Pw1View pw1View;
    private boolean isShowTips;

    /**
     * load
     *
     * @param activity activity
     * @param name     name
     * @param bookId   bookId
     * @param type     type
     */
    public static void lauchMore(Context activity, String name, String bookId, String type) {
        Intent intent = new Intent(activity, ChaseRecommendMoreActivity.class);
        intent.putExtra(ChaseRecommendMorePresenter.CHASE_RECOMMEND_MORE_NAME, name);
        intent.putExtra(ChaseRecommendMorePresenter.CHASE_RECOMMEND_MORE_BOOKID, bookId);
        intent.putExtra(ChaseRecommendMorePresenter.CHASE_RECOMMEND_MORE_TYPE, type);
        activity.startActivity(intent);
        BaseActivity.showActivity(activity);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ac_chase_recommond_more);
    }

    @Override
    protected void initView() {
        netErrorTopLayout = findViewById(R.id.net_error_layout_view);
        pullLoadRecycler = findViewById(R.id.pullLoadMoreRecyclerView);
        statusView = findViewById(R.id.defaultview_nonet);
        commonTitle = findViewById(R.id.commontitle);
        pw1View = new Pw1View(this);
    }

    @Override
    protected void initData() {
        mPresenter = new ChaseRecommendMorePresenter(this);
        mBookListAdapter = new CommonBookListRecycleViewAdapter(this, false);

        pullLoadRecycler.setLinearLayout();
        pullLoadRecycler.setAdapter(mBookListAdapter);

        mPresenter.getParams();
        mPresenter.getFristRequstChaseRecommendMoreInfo(true);
    }

    @Override
    protected void setListener() {
        statusView.setNetErrorClickListener(new StatusView.NetErrorClickListener() {
            @Override
            public void onNetErrorEvent(View v) {
                if (statusView.getVisibility() == View.VISIBLE) {
                    statusView.setVisibility(View.GONE);
                }
                mPresenter.getFristRequstChaseRecommendMoreInfo(true);
            }
        });

        pullLoadRecycler.setOnPullLoadMoreListener(new PullLoadMoreRecycleLayout.PullLoadMoreListener() {
            @Override
            public void onRefresh() {
                initNetErrorStatus();
                if (!NetworkUtils.getInstance().checkNet()) {
                    pullLoadRecycler.setPullLoadMoreCompleted();
                    return;
                }

                pullLoadRecycler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        pullLoadRecycler.setPullLoadMoreCompleted();
                    }
                }, 3000);
                mPresenter.getFristRequstChaseRecommendMoreInfo(false);
                if (isShowTips) {
                    pullLoadRecycler.removeFooterView(pw1View);
                    isShowTips = false;
                }
            }

            @Override
            public void onLoadMore() {
                initNetErrorStatus();
                if (NetworkUtils.getInstance().checkNet()) {
                    mPresenter.getMoreBooksInfo();
                } else {
                    pullLoadRecycler.setPullLoadMoreCompleted();
                }
            }
        });

        commonTitle.setLeftClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mBookListAdapter.setOnItemClickListener(new CommonBookListRecycleViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, BeanBookInfo bean, int position) {
                if (bean != null) {
                    mPresenter.logClick(bean.bookId, (position + 1) + "");

                    BookDetailActivity.launch(getActivity(), bean.bookId, bean.bookName);
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mPresenter != null) {
            mPresenter.logPv();
        }
    }


    @Override
    public String getTagName() {
        return TAG;
    }


    @Override
    public void showLoadProgresss() {
        statusView.showLoading();
    }

    @Override
    public void dismissProgress() {
        statusView.showSuccess();

    }

    @Override
    public void setChaseRecommendMoreInfo(MoreRecommendBook beanInfo, boolean isLoadMore) {
        pullLoadRecycler.setHasMore(true);
        pullLoadRecycler.setPullLoadMoreCompleted();
        if (isLoadMore) {
            if (null != beanInfo && !ListUtils.isEmpty(beanInfo.books)) {
                mBookListAdapter.addNetBeanItem(beanInfo.books, false);
            } else {
                pullLoadRecycler.setHasMore(false);
                if (!isShowTips) {
                    pullLoadRecycler.addFooterView(pw1View);
                    isShowTips = true;
                }
                //                showMessage(R.string.no_more_data);
            }

        } else {
            if (null != beanInfo && !ListUtils.isEmpty(beanInfo.books)) {
                mBookListAdapter.addNetBeanItem(beanInfo.books, true);
            } else {
                setLoadFail();
            }
        }
    }


    @Override
    public void setLoadFail() {
        dismissProgress();
        if (!NetworkUtils.getInstance().checkNet() && mBookListAdapter != null && mBookListAdapter.getItemCount() > 0) {
            initNetErrorStatus();
        } else {
            statusView.showNetError();
        }
    }

    @Override
    public void myFinish() {
        super.finish();
    }

    @Override
    public void setMyTitle(String bookName) {
        commonTitle.setTitle(bookName);
    }

    @Override
    public void setPullRefreshComplete() {
        pullLoadRecycler.setPullLoadMoreCompleted();
    }

    @Override
    public void showNoNetView() {
        initNetErrorStatus();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mPresenter != null) {
            mPresenter.destroy();
        }
    }

    private void initNetErrorStatus() {
        if (!NetworkUtils.getInstance().checkNet() && mBookListAdapter != null && mBookListAdapter.getItemCount() > 0) {
            initNetView();
        } else {
            destoryNetView();
        }
    }


    @Override
    public BaseActivity getHostActivity() {
        return this;
    }

    @Override
    protected boolean isCustomPv() {
        return true;
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
