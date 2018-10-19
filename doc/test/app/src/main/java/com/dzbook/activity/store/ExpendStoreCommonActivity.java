package com.dzbook.activity.store;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.View;
import android.widget.FrameLayout;

import com.dzbook.BaseSwipeBackActivity;
import com.dzbook.mvp.UI.ExpendStoreUI;
import com.dzbook.mvp.presenter.ExpendStorePresenter;
import com.dzbook.templet.ChannelPageFragment;
import com.dzbook.view.DianZhongCommonTitle;
import com.dzbook.view.common.StatusView;
import com.ishugui.R;

import java.util.List;

import hw.sdk.net.bean.store.BeanTempletInfo;

/**
 * 通用拓展二级页面，type=7
 *
 * @author gavin
 */
public class ExpendStoreCommonActivity extends BaseSwipeBackActivity implements ExpendStoreUI {

    private static final String TAG = "ExpendStoreCommonActivity";
    private DianZhongCommonTitle mTitle;
    private StatusView statusView;
    private String id;
    private String type;
    private FrameLayout mContent;
    private ExpendStorePresenter mPresenter;
    private ChannelPageFragment mFragment;
    private long clickDelayTime = 0;

    /**
     * 跳转页面
     *
     * @param activity activity
     * @param id       id
     * @param type     type
     * @param title    title
     */
    public static void launch(Activity activity, String id, String type, String title) {
        Intent intent = new Intent();
        intent.setClass(activity, ExpendStoreCommonActivity.class);
        intent.putExtra("type", type);
        intent.putExtra("id", id);
        intent.putExtra("title", title);
        activity.startActivity(intent);
        showActivity(activity);
    }


    @Override
    public String getTagName() {
        return TAG;
    }

    @Override
    public int getMaxSize() {
        return 3;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_common);
    }

    @Override
    public Activity getActivity() {
        return this;
    }

    @Override
    protected void initView() {
        super.initView();
        mTitle = findViewById(R.id.commontitle);
        statusView = findViewById(R.id.linearlayout_loading);
        mContent = findViewById(R.id.fragment_content);
        mTitle.setTitle(getResources().getString(R.string.recommend));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPresenter.destroy();
    }


    @Override
    protected void initData() {
        super.initData();
        mPresenter = new ExpendStorePresenter(this);
        FragmentManager fragmentManager = getSupportFragmentManager();
        if (fragmentManager != null) {
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            if (fragmentTransaction != null) {
                mFragment = new ChannelPageFragment();
                fragmentTransaction.add(R.id.fragment_content, mFragment, "channel");
                fragmentTransaction.commit();
            }
        }
        Intent intent = getIntent();
        if (intent != null) {
            String title = intent.getStringExtra("title");
            if (!TextUtils.isEmpty(title)) {
                mTitle.setTitle(title);
            }
            id = intent.getStringExtra("id");
            type = intent.getStringExtra("type");
            mPresenter.getDataFromNet(type, id);
        }
    }


    @Override
    protected void setListener() {
        super.setListener();
        mTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        statusView.setClickSetListener(new StatusView.SetClickListener() {
            @Override
            public void onSetEvent(View v) {
                long current = System.currentTimeMillis();
                if (current - clickDelayTime >= 1000) {
                    mPresenter.getDataFromNet(type, id);
                    clickDelayTime = current;
                }
            }
        });
    }

    @Override
    public void setTempletDatas(final List<BeanTempletInfo> section) {
        if (TextUtils.isEmpty(id)) {
            id = "0";
        }
        mFragment.setExpendData(id, type, section, true);
        hideLoading();
        statusView.showSuccess();
        if (mContent != null && mContent.getVisibility() != View.VISIBLE) {
            mContent.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void showNoNetView() {
        if (mContent != null && mContent.getVisibility() != View.VISIBLE) {
            statusView.showNetError();
        }
    }

    @Override
    public void hideLoading() {
        statusView.showSuccess();
    }

    @Override
    public int getStatusColor() {
        return R.color.color_100_ffffff;
    }

    @Override
    public void showEmptyView() {
        if (mContent != null && mContent.getVisibility() != View.VISIBLE) {
            statusView.showEmpty();
        }
    }

    @Override
    public void showLoadding() {
        statusView.showLoading();
    }
}
