package com.dzbook.activity.store;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import com.dzbook.BaseSwipeBackActivity;
import com.dzbook.lib.utils.CompatUtils;
import com.dzbook.log.LogConstants;
import com.dzbook.mvp.UI.LimitFreeTwoLevelUI;
import com.dzbook.mvp.presenter.LimitFreeTwoLevelPresenter;
import com.dzbook.utils.SpUtil;
import com.dzbook.view.DianZhongCommonTitle;
import com.dzbook.view.common.StatusView;
import com.dzbook.view.store.Pd1View;
import com.ishugui.R;

import hw.sdk.net.bean.store.BeanTempletsInfo;

/**
 * 限免二级
 *
 * @author dongdianzhou on 2018/1/15.
 */

public class LimitFreeTwoLevelActivity extends BaseSwipeBackActivity implements LimitFreeTwoLevelUI {

    private static final String TAG = "LimitFreeTwoLevelActivity";
    private DianZhongCommonTitle mTitle;
    private StatusView statusView;
    private String id;
    private String tabId;
    private long clickDelayTime = 0;
    private Pd1View mPd1View;

    private LimitFreeTwoLevelPresenter mPresenter;

    /**
     * 跳转
     *
     * @param activity activity
     * @param title    title
     * @param id       id
     * @param tabId    tabId
     */
    public static void launch(Activity activity, String title, String id, String tabId) {
        Intent intent = new Intent();
        intent.setClass(activity, LimitFreeTwoLevelActivity.class);
        intent.putExtra("title", title);
        intent.putExtra("id", id);
        intent.putExtra("tabId", tabId);
        activity.startActivity(intent);
        showActivity(activity);
    }

    @Override
    public String getTagName() {
        return TAG;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().getDecorView().setBackgroundColor(CompatUtils.getColor(this, R.color.color_100_ffffff));
        setContentView(R.layout.activity_limitfree_twolevel);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPresenter.destroy();
    }


    @Override
    protected void initData() {
        super.initData();
        setSwipeBackEnable(false);
        mPresenter = new LimitFreeTwoLevelPresenter(this);
        mPd1View.setPresenter(mPresenter);
        Intent intent = getIntent();
        if (intent != null) {
            String title = intent.getStringExtra("title");
            if (!TextUtils.isEmpty(title)) {
                mTitle.setTitle(title);
            }
            id = intent.getStringExtra("id");
            tabId = intent.getStringExtra("tabId");
            mPresenter.getDataFromNet(id, tabId, SpUtil.getinstance(getContext()).getPersonReadPref());
        }
    }

    @Override
    protected void initView() {
        super.initView();
        mTitle = findViewById(R.id.commontitle);
        statusView = findViewById(R.id.statusView);
        mPd1View = findViewById(R.id.pd1view);
    }


    @Override
    protected void setListener() {
        super.setListener();
        statusView.setNetErrorClickListener(new StatusView.NetErrorClickListener() {
            @Override
            public void onNetErrorEvent(View v) {
                long current = System.currentTimeMillis();
                if (current - clickDelayTime >= 1000) {
                    showLoading();
                    mPresenter.getDataFromNet(id, tabId, SpUtil.getinstance(getContext()).getPersonReadPref());
                    clickDelayTime = current;
                }
            }
        });
        mTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }


    @Override
    public void setChannelDatas(final BeanTempletsInfo templetsInfo) {
        statusView.showSuccess();
        if (mPd1View != null) {
            mPd1View.bindData(templetsInfo, tabId, LogConstants.MODULE_NSCXMZYM, id);
        }
        if (mPd1View != null && mPd1View.getVisibility() != View.VISIBLE) {
            mPd1View.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void showNoNetView() {
        if (mPd1View != null && mPd1View.getVisibility() != View.VISIBLE) {
            statusView.showNetError();
        }
    }

    @Override
    public void showEmptyView() {
        if (mPd1View != null && mPd1View.getVisibility() != View.VISIBLE) {
            statusView.showEmpty();
        }
    }

    /**
     * 显示加载动画
     */
    public void showLoading() {
        statusView.showLoading();
    }

    @Override
    public void hideLoading() {
        statusView.showSuccess();
    }
}
