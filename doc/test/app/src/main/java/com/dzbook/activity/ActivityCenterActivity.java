package com.dzbook.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.dzbook.BaseSwipeBackActivity;
import com.dzbook.adapter.ActivityCenterAdapter;
import com.dzbook.lib.utils.CompatUtils;
import com.dzbook.mvp.DialogConstants;
import com.dzbook.mvp.UI.ActivityCenterUI;
import com.dzbook.mvp.presenter.ActivityCenterPresenter;
import com.dzbook.view.DianZhongCommonTitle;
import com.dzbook.view.PullLoadMoreRecycleLayout;
import com.dzbook.view.common.StatusView;
import com.ishugui.R;
import com.iss.app.BaseActivity;

import java.util.ArrayList;

import hw.sdk.net.bean.ActivityCenterBean;

/**
 * 活动中心
 *
 * @author gavin 2018/4/25
 */

public class ActivityCenterActivity extends BaseSwipeBackActivity implements ActivityCenterUI {
    /**
     * tag
     */
    public static final String TAG = "ActivityCenterActivity";
    private DianZhongCommonTitle mCommonTitle;
    private PullLoadMoreRecycleLayout mRecycleLayout;
    private ActivityCenterAdapter mAdapter;
    private ActivityCenterPresenter mPresenter;
    private StatusView statusView;

    /**
     * 启动
     *
     * @param activity activity
     */
    public static void launch(Activity activity) {
        Intent intent = new Intent();
        intent.setClass(activity, ActivityCenterActivity.class);
        activity.startActivity(intent);
        BaseActivity.showActivity(activity);
    }

    @Override
    public String getTagName() {
        return TAG;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ac_activity_center);
    }

    @Override
    protected void initView() {
        super.initView();
        mCommonTitle = findViewById(R.id.commontitle);
        mRecycleLayout = findViewById(R.id.pullLoadMoreRecyclerView);
        mRecycleLayout.setLinearLayout();
        mRecycleLayout.setAllReference(false);
        statusView = findViewById(R.id.statusView);
    }

    @Override
    protected void initData() {
        super.initData();
        statusView.showLoading();
        mPresenter = new ActivityCenterPresenter(this);
        mAdapter = new ActivityCenterAdapter();
        mRecycleLayout.setAdapter(mAdapter);
        mPresenter.getData();
        showDialogByType(DialogConstants.TYPE_GET_DATA);
        dissMissDialog();
    }

    @Override
    protected void setListener() {
        super.setListener();
        mCommonTitle.setLeftClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        statusView.setNetErrorClickListener(new StatusView.NetErrorClickListener() {
            @Override
            public void onNetErrorEvent(View v) {
                mPresenter.getData();
            }
        });
    }

    @Override
    public void setData(ArrayList<ActivityCenterBean.CenterInfoBean> list) {
        mAdapter.addItems(list);
        if (mRecycleLayout.getVisibility() == View.GONE) {
            mRecycleLayout.setVisibility(View.VISIBLE);
        }
        statusView.showSuccess();
    }

    @Override
    public void showEmptyView(int resID) {
        mRecycleLayout.setVisibility(View.GONE);
        statusView.showEmpty(getResources().getString(resID), CompatUtils.getDrawable(this, R.drawable.hw_empty_activity));
    }

    @Override
    public void showExpiredView() {

    }


    @Override
    public void showNoNetView() {
        if (mAdapter.getItemCount() <= 0) {
            mRecycleLayout.setVisibility(View.GONE);
            statusView.showNetError();
        }
    }

    @Override
    public void dismissLoadingView() {
        statusView.showSuccess();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mPresenter != null) {
            mPresenter.destroy();
        }
    }
}
