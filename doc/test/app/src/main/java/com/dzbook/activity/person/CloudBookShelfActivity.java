package com.dzbook.activity.person;

import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.dzbook.BaseTransparencyLoadActivity;
import com.dzbook.event.EventBusUtils;
import com.dzbook.event.EventConstant;
import com.dzbook.mvp.UI.PersonCloudShelfUI;
import com.dzbook.mvp.presenter.PersonCloudShelfPresenter;
import com.dzbook.utils.DimensionPixelUtil;
import com.dzbook.utils.NetworkUtils;
import com.dzbook.view.DianZhongCommonTitle;
import com.dzbook.view.PullLoadMoreRecycleLayout;
import com.dzbook.view.common.NetErrorTopView;
import com.dzbook.view.common.StatusView;
import com.dzbook.view.common.dialog.CustomHintDialog;
import com.dzbook.view.common.dialog.base.CustomDialogBusiness;
import com.dzbook.view.store.Pw1View;
import com.ishugui.R;
import com.iss.view.common.ToastAlone;

import java.util.ArrayList;

import hw.sdk.net.bean.BeanBookInfo;

/**
 * 云书架
 *
 * @author dongdianzhou on 2017/11/20.
 */
public class CloudBookShelfActivity extends BaseTransparencyLoadActivity implements PersonCloudShelfUI {

    /**
     * tag
     */
    public static final String TAG = "CloudBookShelfActivity";
    private PullLoadMoreRecycleLayout mRecyclerView;

    private StatusView statusView;

    private PersonCloudShelfPresenter mPresenter;
    private CloudShelfAdapter mAdapter;

    private Pw1View pw1View;
    private NetErrorTopView netErrorTopView;
    private LinearLayout netErrorTopLayout;
    private boolean isShowTips;
    private CustomHintDialog dialog = null;

    @Override
    public String getTagName() {
        return TAG;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personcloudshelf);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        mPresenter.resetIndex(false);
        mPresenter.getCloudShelfData();
    }

    @Override
    protected void initView() {
        netErrorTopLayout = findViewById(R.id.net_error_layout_view);
        mRecyclerView = findViewById(R.id.pullLoadMoreRecyclerView);
        RecyclerView.RecycledViewPool recycledViewPool = new RecyclerView.RecycledViewPool();
        recycledViewPool.setMaxRecycledViews(0, 8);
        mRecyclerView.getRecyclerView().setRecycledViewPool(recycledViewPool);
        statusView = findViewById(R.id.statusView);
        mRecyclerView.setLinearLayout();
        mRecyclerView.setAllReference(true);
        pw1View = new Pw1View(this);
    }

    @Override
    protected void initData() {
        statusView.showLoading();
        mAdapter = new CloudShelfAdapter();
        mRecyclerView.setAdapter(mAdapter);
        mPresenter = new PersonCloudShelfPresenter(this);
        mAdapter.setPersonCloudShelfPresenter(mPresenter);
        mPresenter.getCloudShelfData();
    }

    @Override
    protected void setListener() {
        DianZhongCommonTitle title = findViewById(R.id.include_top_title_item);
        title.setLeftClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        statusView.setClickSetListener(new StatusView.SetClickListener() {
            @Override
            public void onSetEvent(View v) {
                Bundle bundle = new Bundle();
                bundle.putString(EventConstant.EVENT_BOOKSTORE_TYPE, EventConstant.SKIP_TAB_STORE);
                EventBusUtils.sendStickyMessage(EventConstant.UPDATA_FEATURED_URL_REQUESTCODE, EventConstant.TYPE_BOOK_STORE, bundle);
                finish();
            }
        });
        statusView.setNetErrorClickListener(new StatusView.NetErrorClickListener() {
            @Override
            public void onNetErrorEvent(View v) {
                mPresenter.resetIndex(false);
                mPresenter.getCloudShelfData();
            }
        });
        mRecyclerView.setOnPullLoadMoreListener(new PullLoadMoreRecycleLayout.PullLoadMoreListener() {
            @Override
            public void onRefresh() {
                mPresenter.resetIndex(false);
                initNetErrorStatus();
                if (NetworkUtils.getInstance().checkNet()) {
                    mPresenter.getCloudShelfDataFromNet(true, false);
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
                    mPresenter.getCloudShelfDataFromNet(false, false);
                } else {
                    mRecyclerView.setPullLoadMoreCompleted();
                }
            }
        });
    }

    @Override
    public void setShelfData(final ArrayList<BeanBookInfo> list, final boolean isBackSource) {
        mAdapter.addItems(list, isBackSource);
        if (isBackSource) {
            mPresenter.resetIndex(false);
            mRecyclerView.post(new Runnable() {
                @Override
                public void run() {
                    mRecyclerView.setSelectionFromTop(0);
                }
            });
        }
        statusView.showSuccess();
        if (mRecyclerView.getVisibility() == View.GONE) {
            mRecyclerView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void setLoadMore(final boolean isLoadMore) {
        mRecyclerView.setHasMore(isLoadMore);
    }

    @Override
    public void showEmptyView() {
        mRecyclerView.setVisibility(View.GONE);
        statusView.showEmpty(getContext().getResources().getString(R.string.string_self_empty), getContext().getResources().getString(R.string.string_gobookstore));
    }

    @Override
    public void showNoNetView() {
        if (mAdapter.getItemCount() <= 0) {
            mRecyclerView.setVisibility(View.GONE);
            statusView.showNetError();
        } else {
            initNetErrorStatus();
        }
    }

    @Override
    public void referenceAdapter() {
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void compeletePullLoadMore() {
        mRecyclerView.setPullLoadMoreCompleted();
    }

    @Override
    public void hideLoadding() {
        if (!isFinishing()) {
            statusView.showSuccess();
        }
    }

    @Override
    public void showLoadding() {
        if (!isFinishing()) {
            statusView.showLoading();
        }
    }

    @Override
    public void popDeleteDialog(final BeanBookInfo beanBookInfo) {
        if (dialog == null) {
            dialog = new CustomHintDialog(getContext());
        }
        dialog.setDesc(getString(R.string.str_shelf_delete_this_books));
        dialog.setConfirmTxt(getString(R.string.delete));
        dialog.setCheckListener(new CustomDialogBusiness.OnCheckListener() {
            @Override
            public void clickConfirm(Object object) {
                if (!NetworkUtils.getInstance().checkNet()) {
                    ToastAlone.showShort(getResources().getString(R.string.net_work_notuse));
                } else {
                    mPresenter.deleteItemsSyncNet(beanBookInfo);
                }
            }

            @Override
            public void clickCancel() {
            }
        });
        dialog.show();
    }

    @Override
    public void showAllTips() {
        if (!isShowTips) {
            mRecyclerView.addFooterView(pw1View);
            isShowTips = true;
        }
    }

    @Override
    public String getLastItemTime() {
        return mAdapter.getLastItemTime();
    }

    @Override
    public void deleteDataFromAdapter(BeanBookInfo beanBookInfo) {
        int count = mAdapter.deleteDataFromAdapter(beanBookInfo);
        if (count == 0) {
            //showEmptyView();
            mPresenter.setPage(1);
            mPresenter.getCloudShelfDataFromNet(false, false);
        }
        if (isShowTips) {
            mRecyclerView.removeFooterView(pw1View);
            isShowTips = false;
        }
    }

    @Override
    public int getCount() {
        return mAdapter.getItemCount();
    }

    @Override
    public void showMessage(final String msg) {
        ToastAlone.showShort(msg);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mPresenter != null) {
            mPresenter.destroy();
        }
        if (dialog != null && dialog.isShow()) {
            dialog.dismiss();
            dialog = null;
        }
    }


    private void initNetView() {
        if (netErrorTopView == null) {
            netErrorTopView = new NetErrorTopView(getContext());
            netErrorTopLayout.addView(netErrorTopView, 0, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, DimensionPixelUtil.dip2px(getContext(), 48)));
        }
    }

    @Override
    public void initNetErrorStatus() {
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
