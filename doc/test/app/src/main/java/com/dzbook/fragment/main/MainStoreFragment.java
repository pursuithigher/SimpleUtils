package com.dzbook.fragment.main;

import android.app.Activity;
import android.content.ComponentCallbacks2;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.dzbook.activity.Main2Activity;
import com.dzbook.dialog.DialogBookShelfActivity;
import com.dzbook.log.LogConstants;
import com.dzbook.model.UserGrow;
import com.dzbook.mvp.UI.MainStoreUI;
import com.dzbook.mvp.presenter.MainStorePresenter;
import com.dzbook.utils.SpUtil;
import com.dzbook.view.common.StatusView;
import com.dzbook.view.store.Pd0View;
import com.ishugui.R;

import hw.sdk.net.bean.shelf.BeanShelfActivityInfo;
import hw.sdk.net.bean.store.BeanTempletsInfo;
import hw.sdk.net.bean.store.TempletContant;

/**
 * MainStoreFragment
 *
 * @author wxliao on 17/3/29.
 */

public class MainStoreFragment extends BaseFragment implements MainStoreUI, View.OnClickListener, ComponentCallbacks2 {
    /**
     * tag
     */
    public static final String TAG = "MainStoreFragment";
    private StatusView mStatusView;
    private MainStorePresenter presenter;
    private boolean firstNoResumeData = true;

    private Pd0View mPd0View;
    private long lastClickTime = 0;
    private DialogBookShelfActivity bookShelfActivity;
    private BeanShelfActivityInfo shelfActivity;

    @Override
    public String getTagName() {
        return "MainStoreFragment";
    }

    @Override
    protected View inflate(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_main_store, container, false);
    }

    @Override
    protected void initView(View uiView) {
        mStatusView = uiView.findViewById(R.id.statusView);
        presenter = new MainStorePresenter(this);
        mPd0View = uiView.findViewById(R.id.pd0view);
        mPd0View.setPresenter(presenter);
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mPd0View != null) {
            mPd0View.pause();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        UserGrow.userGrowOnceToday(getContext(), UserGrow.USER_GROW_MAIN_STORE);
        if (mPd0View != null && !firstNoResumeData) {
            mPd0View.resume();
        } else {
            firstNoResumeData = false;
        }
        if (shelfActivity != null) {
            setShowBookShelfActivity();
        }
    }

    @Override
    public void initData(View uiView) {
        mStatusView.showLoading();
        presenter.getDataFromNet("", SpUtil.getinstance(getContext()).getPersonReadPref());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
    }

    @Override
    protected void setListener(View uiView) {
        mStatusView.setNetErrorClickListener(new StatusView.NetErrorClickListener() {
            @Override
            public void onNetErrorEvent(View v) {
                onClick(v);
            }
        });
    }

    /**
     * 低内存回调
     */
    @Override
    public void onLowMemory() {
        super.onLowMemory();
        Glide.get(getContext()).onLowMemory();
    }

    /**
     * 进程到后台的时候回调：降低内存避免被杀死
     *
     * @param level
     */
    @Override
    public void onTrimMemory(int level) {
        //apk进程退到后台
        Context context = getContext();
        if (context != null) {
            if (level == TRIM_MEMORY_UI_HIDDEN) {
                Glide.get(context).onLowMemory();
            }
            Glide.get(context).onTrimMemory(level);
        }
    }


    @Override
    public void onRefreshFragment() {
        super.onRefreshFragment();
        mPd0View.returnViewTop();
    }


    @Override
    public void showLoading() {
        mStatusView.showLoading();
    }

    @Override
    public void hideLoading() {
        mStatusView.showSuccess();
    }

    @Override
    public void setChannelDatas(final BeanTempletsInfo beanTempletsInfo) {
        hideLoading();
        if (mPd0View != null) {
            mPd0View.bindData(getChildFragmentManager(), beanTempletsInfo, "", LogConstants.MODULE_NSC, "");
        }
        mStatusView.showSuccess();
        if (mPd0View != null && mPd0View.getVisibility() != View.VISIBLE) {
            mPd0View.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void showNoNetView() {
        if (mPd0View != null && mPd0View.getVisibility() != View.VISIBLE) {
            mStatusView.showNetError();

        }
    }

    @Override
    public void showEmptyView() {
        if (mPd0View != null && mPd0View.getVisibility() != View.VISIBLE) {
            mStatusView.showEmpty();
        }
    }

    @Override
    public void setNotiAndActivityData(BeanShelfActivityInfo activity) {
        shelfActivity = activity;
        setShowBookShelfActivity();
    }

    @Override
    public void onDestroy() {
        if (bookShelfActivity != null && bookShelfActivity.isShowing()) {
            bookShelfActivity.dismiss();
            bookShelfActivity = null;
        }
        super.onDestroy();
    }

    @Override
    public void onMultiWindowModeChanged(boolean isInMultiWindowMode) {
        if (bookShelfActivity != null && bookShelfActivity.isShowing()) {
            bookShelfActivity.dismiss();
            bookShelfActivity = null;
        }
        super.onMultiWindowModeChanged(isInMultiWindowMode);
    }

    /**
     * 弹出活动弹窗
     */
    private void setShowBookShelfActivity() {
        Activity activity = getActivity();
        if (activity != null && activity instanceof Main2Activity) {
            if (((Main2Activity) activity).getCurrentTab() == 1) {
                if (shelfActivity != null && !TextUtils.isEmpty(shelfActivity.type)) {
                    //覆盖升级下第一次启动不弹出 否则弹出  为了用户引导正常弹出
                    if (!SpUtil.getinstance(getActivity()).isUpdateInstall()) {
                        // 活动详情
                        if (bookShelfActivity == null && getActivity() != null && !getActivity().isFinishing()) {
                            bookShelfActivity = new DialogBookShelfActivity(getActivity());
                        }
                        if (bookShelfActivity != null && !bookShelfActivity.isShowing()) {
                            bookShelfActivity.show(shelfActivity);
                        }
                    }
                }
            }
        }
    }

    @Override
    public void onClick(View v) {
        long currentClickTime = System.currentTimeMillis();
        if (currentClickTime - lastClickTime > TempletContant.CLICK_DISTANSE) {
            int id = v.getId();
            if (id == R.id.status_setting) {
                showLoading();
                presenter.getDataFromNet("", SpUtil.getinstance(getContext()).getPersonReadPref());
            }
        }
        lastClickTime = currentClickTime;
    }
}

