package com.dzbook.fragment.main;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.StringRes;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dzbook.event.EventMessage;
import com.dzbook.log.DzLog;
import com.dzbook.mvp.BaseUI;
import com.dzbook.mvp.DialogConstants;
import com.dzbook.net.hw.HwRequestLib;
import com.dzbook.utils.ThirdPartyLog;
import com.dzbook.utils.WhiteListWorker;
import com.iss.app.BaseActivity;

/**
 * base fragment
 *
 * @author wxliao on 17/3/28.
 */
public abstract class BaseFragment extends Fragment implements BaseUI {

    /**
     * BaseFragment 使用的额 BaseActivity
     */
    public BaseActivity mActivity;
    private View mViewContent;


    /**
     * inflate
     *
     * @param inflater           inflater
     * @param container          container
     * @param savedInstanceState savedInstanceState
     * @return view
     */
    protected abstract View inflate(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState);

    /**
     * init View
     *
     * @param uiView uiView
     */
    protected abstract void initView(View uiView);

    /**
     * init Data
     *
     * @param uiView uiView
     */
    protected abstract void initData(View uiView);

    /**
     * set Listener
     *
     * @param uiView uiView
     */
    protected abstract void setListener(View uiView);

    /**
     * class Simple Name
     *
     * @return name
     */
    public final String getName() {
        String tagName = getTagName();
        if (!TextUtils.isEmpty(tagName)) {
            return tagName;
        }
        return getClass().getSimpleName();
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof BaseActivity) {
            this.mActivity = (BaseActivity) context;
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (mViewContent == null) {
            mViewContent = inflate(inflater, container, savedInstanceState);
            initView(mViewContent);
            initData(mViewContent);
            setListener(mViewContent);
        }

        ViewGroup parent = (ViewGroup) mViewContent.getParent();
        if (parent != null) {
            parent.removeView(mViewContent);
        }
        return mViewContent;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mViewContent = null;
    }

    /**
     * 日志打点 详情见wiki pn pi ps
     *
     * @return str
     */
    public String getPI() {
        return null;
    }

    /**
     * 日志打点 详情见wiki pn pi ps
     *
     * @return str
     */
    public String getPS() {
        return null;
    }

    /**
     * 设置图书来源
     */
    public void setBookSourceFrom() {
        WhiteListWorker.setBookSourceFrom(this.getName(), null, this);
    }


    @Override
    public void onResume() {
        super.onResume();
        setBookSourceFrom();
        HwRequestLib.flog("==> " + this.getName());
        DzLog.getInstance().onPageStart(this, isCustomPv());
        ThirdPartyLog.onResumeFragment(getActivity(), this.getName());
    }

    @Override
    public void onPause() {
        super.onPause();
        HwRequestLib.flog(" <--" + this.getName());
        DzLog.getInstance().onPageEnd(this, isCustomPv());
        ThirdPartyLog.onPauseFragment(getActivity(), this.getName());
    }

    public boolean isActivityFinish() {
        return mActivity == null || mActivity.isFinishing();
    }

    /**
     * 主线程运行
     *
     * @param run runnable
     */
    public void runOnUiThread(Runnable run) {
        if (!isActivityFinish() && getActivity() != null) {
            getActivity().runOnUiThread(run);
        }
    }

    /**
     * 刷新
     */
    public void onRefreshFragment() {
    }

    /**
     * 用于接收eventbus的消息
     *
     * @param event event
     */
    public void onEventMainThread(EventMessage event) {

    }

    protected boolean isCustomPv() {
        return false;
    }

    @Override
    public void dissMissDialog() {
        if (mActivity != null) {
            mActivity.dissMissDialog();
        }
    }

    @Override
    public boolean isNetworkConnected() {
        if (mActivity != null) {
            return mActivity.isNetworkConnected();
        }

        return false;
    }

    @Override
    public void showMessage(String message) {
        if (mActivity != null) {
            mActivity.showMessage(message);
        }
    }

    @Override
    public void showMessage(@StringRes int resId) {

        if (mActivity != null) {
            mActivity.showMessage(resId);
        }
    }


    @Override
    public void showDialogByType(@DialogConstants.DialogType int loadingType) {
        if (mActivity != null) {
            mActivity.showDialogByType(loadingType);
        }
    }

    @Override
    public void showDialogByType(@DialogConstants.DialogType int loadingType, CharSequence text) {
        if (mActivity != null) {
            mActivity.showDialogByType(loadingType, text);
        }
    }

    @Override
    public Context getContext() {
        return getActivity();
    }


}
