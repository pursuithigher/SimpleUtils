package com.dzbook.activity.vip;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.alibaba.android.vlayout.VirtualLayoutManager;
import com.dzbook.BaseSwipeBackActivity;
import com.dzbook.event.EventConstant;
import com.dzbook.event.EventMessage;
import com.dzbook.mvp.UI.MyVipUI;
import com.dzbook.mvp.presenter.MyVipPresenter;
import com.dzbook.utils.NetworkUtils;
import com.dzbook.view.DianZhongCommonTitle;
import com.dzbook.view.common.StatusView;
import com.dzbook.view.common.loading.RefreshLayout;
import com.dzbook.vip.adapter.VipDelegateAdapter;
import com.ishugui.R;
import com.iss.app.BaseActivity;

import java.util.List;

import hw.sdk.net.bean.vip.VipBookInfo;
import hw.sdk.net.bean.vip.VipUserInfoBean;
import hw.sdk.net.bean.vip.VipUserPayBean;

/**
 * 我的vip页面
 *
 * @author gavin
 */
public class MyVipActivity extends BaseSwipeBackActivity implements MyVipUI, RefreshLayout.OnRefreshListener {
    /**
     * tag
     */
    public static final String TAG = "MyVipActivity";
    private MyVipPresenter myVipPresenter;
    private VipDelegateAdapter vipDelegateAdapter;
    private RecyclerView recyclerView;
    private StatusView statusView;
    private DianZhongCommonTitle mCommonTitle;
    private RefreshLayout swipeLayout;

    /**
     * 处理跳转
     *
     * @param activity activity
     */
    public static void launch(Context activity) {
        Intent intent = new Intent(activity, MyVipActivity.class);
        activity.startActivity(intent);
        BaseActivity.showActivity(activity);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_vip);
    }

    @Override
    public String getTagName() {
        return TAG;
    }

    //    @Override
    //    protected boolean needImmersionBar() {
    //        return true;
    //    }

    @Override
    protected void initData() {
        super.initData();
        myVipPresenter = new MyVipPresenter(this);
        VirtualLayoutManager layoutManager = new VirtualLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        vipDelegateAdapter = new VipDelegateAdapter(layoutManager, this, myVipPresenter);
        recyclerView.setAdapter(vipDelegateAdapter);
        myVipPresenter.getVipInfo();
    }

    @Override
    protected void initView() {
        super.initView();
        recyclerView = findViewById(R.id.rv_vip);
        swipeLayout = findViewById(R.id.rf_vip);
        statusView = findViewById(R.id.defaultview);
        statusView.showLoading();
        mCommonTitle = findViewById(R.id.commontitle);
    }

    @Override
    protected void setListener() {
        super.setListener();
        swipeLayout.setRefreshListener(this);
        mCommonTitle.setLeftClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    public void showNoNetView() {
        if (vipDelegateAdapter.getAdaptersCount() > 0) {
            swipeLayout.setVisibility(View.VISIBLE);
        } else {
            swipeLayout.setVisibility(View.GONE);
            statusView.showNetError();
        }
    }

    @Override
    public void setSelectItem(int position) {
        vipDelegateAdapter.refreshItemBg(position);
    }

    @Override
    public void showEmptyView() {
        if (swipeLayout != null && swipeLayout.getVisibility() == View.VISIBLE) {
            swipeLayout.setVisibility(View.GONE);
        }
        statusView.showEmpty(getResources().getString(R.string.string_empty_hint));
    }

    @Override
    public void refreshFinish() {
        swipeLayout.refreshComplete();
    }

    @Override
    public void updateUI(VipUserInfoBean userInfoBean, List<VipUserPayBean> vipUserPayBeans, List<VipBookInfo> vipBookInfoList) {
        vipDelegateAdapter.addItems(userInfoBean, vipUserPayBeans, vipBookInfoList);
        statusView.showSuccess();
    }

    @Override
    public void onEventMainThread(EventMessage event) {
        int requestCode = event.getRequestCode();
        switch (requestCode) {
            case EventConstant.LOGIN_SUCCESS_UPDATE_SHELF:
            case EventConstant.LOGIN_CHECK_RSET_PERSON_LOGIN_STATUS:
                myVipPresenter.getVipInfo();
                break;
            default:
                break;
        }
    }

    @Override
    public void onRefresh() {
        if (!NetworkUtils.getInstance().checkNet()) {
            swipeLayout.setRefreshing(false);
            return;
        }
        myVipPresenter.getVipInfo();
        swipeLayout.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (swipeLayout.isRefreshing()) {
                    swipeLayout.setRefreshing(false);
                }
            }
        }, 4000L);

    }
}
