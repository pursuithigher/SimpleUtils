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
import com.dzbook.lib.utils.CompatUtils;
import com.dzbook.mvp.UI.LimitFreeUI;
import com.dzbook.mvp.presenter.LimitFreePresenter;
import com.dzbook.templet.ChannelPageFragment;
import com.dzbook.utils.SpUtil;
import com.dzbook.view.DianZhongCommonTitle;
import com.dzbook.view.common.StatusView;
import com.ishugui.R;

import java.util.List;

import hw.sdk.net.bean.store.BeanTempletInfo;

/**
 * 限免
 *
 * @author dongdianzhou on 2018/1/15.
 */
public class LimitFreeActivity extends BaseSwipeBackActivity implements LimitFreeUI {

    private static final String TAG = "LimitFreeActivity";
    private DianZhongCommonTitle mTitle;
    private StatusView statusView;

    private FrameLayout mContent;
    private String id;
    private ChannelPageFragment mFragment;

    private LimitFreePresenter mPresenter;

    private long clickDelayTime = 0;

    /**
     * 跳转
     *
     * @param activity activity
     */
    public static void launch(Activity activity) {
        Intent intent = new Intent();
        intent.setClass(activity, LimitFreeActivity.class);
        activity.startActivity(intent);
        showActivity(activity);
    }

    @Override
    public Activity getActivity() {
        return super.getActivity();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().getDecorView().setBackgroundColor(CompatUtils.getColor(getContext(), R.color.color_100_ffffff));
        setContentView(R.layout.activity_commontwolevel);
    }


    @Override
    public String getTagName() {
        return TAG;
    }

    @Override
    protected void initView() {
        super.initView();
        mTitle = findViewById(R.id.commontitle);
        statusView = findViewById(R.id.linearlayout_loading);
        mContent = findViewById(R.id.fragment_content);
        mTitle.setTitle(getResources().getString(R.string.shelf_free_zone));
    }

    @Override
    protected void initData() {
        super.initData();
        setSwipeBackEnable(false);
        mPresenter = new LimitFreePresenter(this);
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
            mPresenter.getLimitFreeDataFromNet();
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
                    mPresenter.getLimitFreeDataFromNet();
                    clickDelayTime = current;
                }
            }
        });
    }

    @Override
    public void setTempletDatas(final List<BeanTempletInfo> section) {
        SpUtil spUtil = SpUtil.getinstance(getActivity());
        String vipId = spUtil.getString(SpUtil.BOOK_STORE_LIMITFREE_ID);
        String vipType = spUtil.getString(SpUtil.BOOK_STORE_LIMITFREE_TYPE);
        if (TextUtils.isEmpty(vipType)) {
            vipType = "6";
        }
        mFragment.setVipOrLimitFreeData(vipId, vipType, section, true);
        hideLoading();
        statusView.showSuccess();
        if (mContent != null && mContent.getVisibility() != View.VISIBLE) {
            mContent.setVisibility(View.VISIBLE);
        }
    }


    @Override
    public void showEmptyView() {
        if (mContent != null && mContent.getVisibility() != View.VISIBLE) {
            statusView.showEmpty();
        }
    }

    @Override
    public void showNoNetView() {
        if (mContent != null && mContent.getVisibility() != View.VISIBLE) {
            statusView.showNetError();
        }
    }

    @Override
    public int getStatusColor() {
        return R.color.color_100_ffffff;
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPresenter.destroy();
    }

    @Override
    public void showLoadding() {
        statusView.showLoading();
    }

    @Override
    public void hideLoading() {
        statusView.showSuccess();
    }
}
