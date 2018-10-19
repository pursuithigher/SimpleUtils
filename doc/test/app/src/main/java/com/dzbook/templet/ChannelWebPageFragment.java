package com.dzbook.templet;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

import com.dzbook.fragment.main.BaseFragment;
import com.dzbook.fragment.main.MainStoreFragment;
import com.dzbook.lib.utils.StringUtil;
import com.dzbook.log.LogConstants;
import com.dzbook.sonic.DzCacheLayout;
import com.dzbook.sonic.DzWebUtil;
import com.dzbook.utils.NetworkUtils;
import com.dzbook.utils.SpUtil;
import com.dzbook.view.common.loading.RefreshLayout;
import com.dzbook.web.WebManager;
import com.ishugui.R;

import hw.sdk.net.bean.store.BeanSubTempletInfo;
import hw.sdk.net.bean.store.TempletContant;

/**
 * ChannelWebPageFragment
 *
 * @author dongdianzhou on 2018/1/15.
 */

public class ChannelWebPageFragment extends BaseFragment {

    private DzCacheLayout mDzCacheLayout;
    private WebManager mWebManager;
    private String channelId;
    private String mChannelPos;
    private String mChannelTitle;
    private String mLoadUrl;
    private boolean isLoadData = false;

    @Override
    public String getTagName() {
        return "ChannelWebPageFragment";
    }

    @Override
    protected View inflate(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_channelwebpage, container, false);
    }

    @Override
    protected void initView(View uiView) {
        mDzCacheLayout = uiView.findViewById(R.id.dzCacheLayout);
        mWebManager = new WebManager(mActivity, mDzCacheLayout.getWebView());
        mWebManager.initJsBridge();
        mDzCacheLayout.setWebManager(mWebManager);
    }

    @Override
    protected void initData(View uiView) {
        Bundle bundle = getArguments();
        if (bundle != null) {
            mLoadUrl = bundle.getString(TempletContant.KEY_CHANNEL_URL);
            channelId = bundle.getString(TempletContant.KEY_CHANNEL_ID);
            String selectedId = bundle.getString(TempletContant.KEY_CHANNEL_SELECTED_ID);
            mChannelPos = bundle.getString(TempletContant.KEY_CHANNEL_POSITION);
            mChannelTitle = bundle.getString(TempletContant.KEY_CHANNEL_TITLE);
            mWebManager.setChannelInfo(channelId, mChannelPos, mChannelTitle);
            if (!TextUtils.isEmpty(selectedId) && selectedId.equals(channelId)) {
                if (!TextUtils.isEmpty(mLoadUrl) && !isLoadData) {
                    mDzCacheLayout.loadUrl(getParamsUrl(mLoadUrl));
                }
            }
        }
    }

    @Override
    protected void setListener(View uiView) {
        mDzCacheLayout.setOnWebLoadListener(new DzCacheLayout.OnWebLoadListener() {
            @Override
            public boolean overrideUrlLoading(WebView webView, String url) {
                return DzWebUtil.overrideUrlLoading(getActivity(), true, webView, url, MainStoreFragment.TAG, LogConstants.RECHARGE_SOURCE_FROM_VALUE_5);
            }

            @Override
            public void onReceivedTitle(WebView webView, String title) {

            }

        });
        mDzCacheLayout.setRecommendListener(new DzCacheLayout.RecommendListener() {
            @Override
            public void onPageFinished() {
                isLoadData = true;
            }

            @Override
            public void onReceivedError() {

            }

            @Override
            public void onRefresh() {

            }
        });
        mDzCacheLayout.setOnRefreshListener(new RefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                //                ALog.eDongdz("ChannelWebPageFragment onRefresh start mLoadUrl=" + mLoadUrl);
                if (NetworkUtils.getInstance().checkNet()) {
                    if (!TextUtils.isEmpty(mLoadUrl) && !"about:blank".equals(mLoadUrl)) {
                        mDzCacheLayout.loadUrl(getParamsUrl(mLoadUrl));
                    }
                } else {
                    //                    ToastAlone.showShort(R.string.net_work_notcool);
                    mDzCacheLayout.stopOnRefresh();
                }
                final long num = 4000L;
                mDzCacheLayout.stopOnRefreshDelay(num);
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mDzCacheLayout != null) {
            mDzCacheLayout.stopOnRefresh();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (null != mWebManager) {
            mWebManager.destroy();
        }
    }

    /**
     * 暂停回收内存
     */
    public void pauseRecycle() {
        if (null != mWebManager) {
            mWebManager.pause();
        }
    }

    /**
     * resume恢复页面
     */
    public void resumeReference() {
        if (mWebManager != null) {
            mWebManager.resume();
        }
        //        mWebManager.setChannelInfo(channelId, mChannelPos, mChannelTitle);
        //        mDzCacheLayout.loadUrl(getParamsUrl(mLoadUrl));
    }

    /**
     * referenceData
     *
     * @param beanSubTempletInfo beanSubTempletInfo
     * @param position           position
     */
    public void referenceData(final BeanSubTempletInfo beanSubTempletInfo, int position) {
        mChannelPos = String.valueOf(position);
        mWebManager.setChannelInfo(channelId, mChannelPos, mChannelTitle);
        mLoadUrl = beanSubTempletInfo.actionUrl;
        if (!TextUtils.isEmpty(mLoadUrl) && !isLoadData) {
            mDzCacheLayout.loadUrl(getParamsUrl(mLoadUrl));
        }
    }

    /**
     * 拼接url
     *
     * @param url url
     * @return url
     */
    public String getParamsUrl(String url) {
        if (!url.startsWith("http://") && !url.startsWith("file:///") && !url.startsWith("https://") && !url.startsWith("svn://")) {
            url = "http://" + url;
        }
        url = StringUtil.putUrlValue(url, "readPref", SpUtil.getinstance(getContext()).getPersonReadPref() + "");
        return url;
    }

    @Override
    protected boolean isCustomPv() {
        return true;
    }

}
