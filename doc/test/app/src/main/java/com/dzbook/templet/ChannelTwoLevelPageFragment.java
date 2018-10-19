package com.dzbook.templet;

import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.alibaba.android.vlayout.VirtualLayoutManager;
import com.dzbook.mvp.presenter.TempletPresenter;
import com.dzbook.templet.adapter.DzDelegateAdapter;
import com.dzbook.utils.NetworkUtils;
import com.dzbook.utils.SpUtil;
import com.dzbook.view.PageView.OnLoadNextListener;
import com.dzbook.view.PageView.OnScrollViewListener;
import com.dzbook.view.common.StatusView;
import com.dzbook.view.common.loading.RefreshLayout;
import com.ishugui.R;

import java.util.List;

import hw.sdk.net.bean.store.BeanTempletInfo;
import hw.sdk.net.bean.store.BeanTempletsInfo;
import hw.sdk.net.bean.store.TempletContant;

/**
 * ChannelTwoLevelPageFragment
 *
 * @author dongdianzhou on 2018/1/8.
 */

public class ChannelTwoLevelPageFragment extends BaseChannelPageFragment {

    private long clickDelayTime = 0;

    @Override
    public String getTagName() {
        return "ChannelTwoLevelPageFragment";
    }

    @Override
    protected View inflate(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (rootView == null) {
            rootView = inflater.inflate(R.layout.fragment_channeltwolevelpage, container, false);
        }
        return rootView;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void initView(View uiView) {
        templetPresenter = new TempletPresenter(this);
        statusView = uiView.findViewById(R.id.statusView);
        mRecyclerView = uiView.findViewById(R.id.recyclerview);
        swipeLayout = uiView.findViewById(R.id.layout_swipe);
        netErrorTopLayout = uiView.findViewById(R.id.net_error_layout_view);
        VirtualLayoutManager layoutManager = new VirtualLayoutManager(getContext());
        mRecyclerView.setLayoutManager(layoutManager);
        mAdapter = new DzDelegateAdapter(layoutManager, true, getContext(), this, templetPresenter);
        mRecyclerView.setAdapter(mAdapter);
    }


    @Override
    protected void initData(View uiView) {
        Bundle bundle = getArguments();
        if (bundle != null) {
            statusView.showLoading();
            templetID = bundle.getString(TempletContant.KEY_CHANNEL_TEMPLETID);
            subTempletID = bundle.getString(TempletContant.KEY_CHANNEL_ID);
            selectedId = bundle.getString(TempletContant.KEY_CHANNEL_SELECTED_ID);
            Parcelable parcelable = bundle.getParcelable(TempletContant.KEY_CHANNEL_OBJECT);
            if (parcelable != null && parcelable instanceof BeanTempletsInfo) {
                BeanTempletsInfo templetsInfo = (BeanTempletsInfo) parcelable;
                //加载数据，非数据页只是创建页面并不加载数据，待点击的时候加载数据
                if (templetsInfo.isContainTemplet()) {
                    setTempletDatas(templetsInfo.getSection(), true);
                    if (!NetworkUtils.getInstance().checkNet()) {
                        showNoNet(true);
                    }
                } else {
                    if (!TextUtils.isEmpty(selectedId) && selectedId.equals(subTempletID)) {
                        showServerEmpty(true);
                    }
                }
            } else {
                if (!TextUtils.isEmpty(selectedId) && selectedId.equals(subTempletID)) {
                    showServerEmpty(true);
                }
            }
            subTempletTitle = bundle.getString(TempletContant.KEY_CHANNEL_TITLE);
            pageType = bundle.getString(TempletContant.KEY_CHANNEL_PAGETYPE);
            //            ALog.eDongdz("当前页面的数据展示：templetID：" + templetID + " subTempletID:" + subTempletID + " subTempletTitle:" + subTempletTitle + " pageType:" + pageType);
        }
    }


    @Override
    protected void setListener(View uiView) {
        swipeLayout.setRefreshListener(new MyOnRefreshListener());
        mRecyclerView.setLoadNextListener(new MyOnLoadNextListener());
        statusView.setNetErrorClickListener(new StatusView.NetErrorClickListener() {
            @Override
            public void onNetErrorEvent(View v) {
                onDestroyNetView();
                long current = System.currentTimeMillis();
                if (current - clickDelayTime > TempletContant.CLICK_DISTANSE) {
                    showLoading();
                    templetPresenter.getSingleChannelData(templetID, subTempletID, SpUtil.getinstance(getContext()).getPersonReadPref(), pageType, "");
                    clickDelayTime = current;
                }
            }
        });
        mRecyclerView.setScrollViewListener(new MyObservableScrollViewCallbacks());
    }


    @Override
    public void setTempletDatas(final List<BeanTempletInfo> section, final boolean isclear) {
        if (swipeLayout != null && swipeLayout.isRefreshing()) {
            swipeLayout.setRefreshing(false);
        }
        loadDataState = 2;
        initTempletData(section, isclear);
    }


    @Override
    public void getSingleChannelDataFromCanche(String channelId) {

    }

    @Override
    public void onDestroyNetView() {
        if (netErrorTopView != null) {
            netErrorTopLayout.removeView(netErrorTopView);
            netErrorTopView = null;
        }
        mRecyclerView.setPadding(0, 0, 0, 0);
    }


    /**
     * 普通二级列表数据设置
     *
     * @param section      section
     * @param isClear      isClear
     * @param templetID    templetID
     * @param subTempletID subTempletID
     * @param pageType     pageType
     */
    public void setCommonData(List<BeanTempletInfo> section, boolean isClear, String templetID, String subTempletID, String pageType) {
        this.pageType = pageType;
        this.templetID = templetID;
        this.subTempletID = subTempletID;
        setTempletDatas(section, isClear);
    }

    /**
     * 滚动回调
     */
    private static class MyObservableScrollViewCallbacks implements OnScrollViewListener {

        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {

        }

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {

        }
    }

    /**
     * 加载下一个监听
     */
    private class MyOnLoadNextListener implements OnLoadNextListener {
        @Override
        public void onLoadNext() {
            if (!TextUtils.isEmpty(nextPageUrl)) {
                templetPresenter.getNextPageData(nextPageUrl);
            }
        }
    }

    /**
     * 刷新监听
     */
    private class MyOnRefreshListener implements RefreshLayout.OnRefreshListener {
        @Override
        public void onRefresh() {
            onDestroyNetView();
            if (!NetworkUtils.getInstance().checkNet()) {
                swipeLayout.setRefreshing(false);
                showNoNet(true);
                return;
            }
            templetPresenter.referenceChannelData(templetID, subTempletID, SpUtil.getinstance(getContext()).getPersonReadPref(), pageType);
        }
    }


}
