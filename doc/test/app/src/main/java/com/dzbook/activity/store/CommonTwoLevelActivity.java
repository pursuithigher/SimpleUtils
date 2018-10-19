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
import com.dzbook.log.DzLog;
import com.dzbook.log.LogConstants;
import com.dzbook.mvp.UI.CommonTwoLevelUI;
import com.dzbook.mvp.presenter.CommonTwoLevelPresenter;
import com.dzbook.templet.ChannelTwoLevelPageFragment;
import com.dzbook.utils.SpUtil;
import com.dzbook.view.DianZhongCommonTitle;
import com.dzbook.view.common.StatusView;
import com.ishugui.R;

import java.util.HashMap;
import java.util.List;

import hw.sdk.net.bean.store.BeanTempletInfo;

/**
 * 二级页面
 *
 * @author dongdianzhou on 2018/1/15.
 */
public class CommonTwoLevelActivity extends BaseSwipeBackActivity implements CommonTwoLevelUI {

    private static final String TAG = "CommonTwoLevelActivity";
    private DianZhongCommonTitle mTitle;
    private long clickDelayTime = 0;
    private StatusView statusView;
    private String title;
    private FrameLayout mContent;

    private CommonTwoLevelPresenter mPresenter;

    private String id;
    private ChannelTwoLevelPageFragment mFragment;


    /**
     * 打开
     * activity
     *
     * @param activity activity
     * @param title    title
     * @param id       id
     */
    public static void launch(Activity activity, String title, String id) {
        Intent intent = new Intent();
        intent.setClass(activity, CommonTwoLevelActivity.class);
        intent.putExtra("title", title);
        intent.putExtra("id", id);
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
        getWindow().getDecorView().setBackgroundColor(CompatUtils.getColor(getContext(), R.color.color_100_ffffff));
        setContentView(R.layout.activity_commontwolevel);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPresenter.destroy();
    }

    @Override
    protected void initView() {
        super.initView();
        mTitle = findViewById(R.id.commontitle);
        statusView = findViewById(R.id.linearlayout_loading);
        mContent = findViewById(R.id.fragment_content);
    }

    @Override
    protected void initData() {
        super.initData();
        statusView.showLoading();
        setSwipeBackEnable(false);
        mPresenter = new CommonTwoLevelPresenter(this);
        FragmentManager fragmentManager = getSupportFragmentManager();
        if (fragmentManager != null) {
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            if (fragmentTransaction != null) {
                mFragment = new ChannelTwoLevelPageFragment();
                fragmentTransaction.add(R.id.fragment_content, mFragment, "channel");
                fragmentTransaction.commitAllowingStateLoss();
            }
        }
        Intent intent = getIntent();
        if (intent != null) {
            title = intent.getStringExtra("title");
            if (!TextUtils.isEmpty(title)) {
                mTitle.setTitle(title);
            }
            id = intent.getStringExtra("id");
            mPresenter.getDataFromNet(id, SpUtil.getinstance(getContext()).getPersonReadPref());
        }
    }

    @Override
    protected void setListener() {
        super.setListener();
        mTitle.setLeftClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        statusView.setNetErrorClickListener(new StatusView.NetErrorClickListener() {
            @Override
            public void onNetErrorEvent(View v) {
                long current = System.currentTimeMillis();
                if (current - clickDelayTime >= 1000) {
                    mPresenter.getDataFromNet(id, SpUtil.getinstance(getContext()).getPersonReadPref());
                    clickDelayTime = current;
                }
            }
        });

    }

    @Override
    public void setTempletDatas(final List<BeanTempletInfo> section) {
        mFragment.setCommonData(section, true, id, "", LogConstants.MODULE_NSCZYM);
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
    public void showEmptyView() {
        if (mContent != null && mContent.getVisibility() != View.VISIBLE) {
            statusView.showEmpty();
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
    protected void onResume() {
        super.onResume();
        HashMap<String, String> map = new HashMap<>();
        map.put("title", title + "");
        DzLog.getInstance().logPv(getTagName(), map, null);
    }

    @Override
    protected boolean isCustomPv() {
        return true;
    }
}
