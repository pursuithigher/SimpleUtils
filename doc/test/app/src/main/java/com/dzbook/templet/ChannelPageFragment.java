package com.dzbook.templet;

import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.alibaba.android.vlayout.VirtualLayoutManager;
import com.dzbook.event.EventBusUtils;
import com.dzbook.event.EventConstant;
import com.dzbook.event.EventMessage;
import com.dzbook.log.DzLog;
import com.dzbook.mvp.presenter.TempletPresenter;
import com.dzbook.templet.adapter.DzDelegateAdapter;
import com.dzbook.templet.adapter.StoreAdapterConstant;
import com.dzbook.utils.NetworkUtils;
import com.dzbook.utils.SpUtil;
import com.dzbook.view.PageView.OnLoadNextListener;
import com.dzbook.view.PageView.OnScrollViewListener;
import com.dzbook.view.common.StatusView;
import com.dzbook.view.common.loading.RefreshLayout;
import com.dzbook.view.store.Bn0View;
import com.ishugui.R;

import java.util.HashMap;
import java.util.List;

import hw.sdk.net.bean.store.BeanSubTempletInfo;
import hw.sdk.net.bean.store.BeanTempletInfo;
import hw.sdk.net.bean.store.BeanTempletsInfo;
import hw.sdk.net.bean.store.TempletContant;

/**
 * 频道页Fragment
 *
 * @author dongdianzhou on 2018/1/8.
 */
public class ChannelPageFragment extends BaseChannelPageFragment {

    int firstVisibleItemPosition;
    private long clickDelayTime = 0;

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

        if (getUserVisibleHint()) {
            onVisible();
        } else {
            onInvisible();
        }
    }

    /**
     * 可见时的回调方法
     */
    protected void onVisible() {
        HashMap<String, String> map = new HashMap<>();
        map.put("pageType", pageType);
        map.put("pageTitle", subTempletTitle);
        map.put("templetId", subTempletID);
        DzLog.getInstance().logPv(getTagName(), map, "");
    }

    /**
     * 不可见时的回调方法
     */
    protected void onInvisible() {

    }

    @Override
    public String getTagName() {
        return "ChannelPageFragment";
    }

    @Override
    protected View inflate(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (rootView == null) {
            rootView = inflater.inflate(R.layout.fragment_channelpage, container, false);
        }
        return rootView;
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
        RecyclerView.RecycledViewPool recycledViewPool = new RecyclerView.RecycledViewPool();
        recycledViewPool.setMaxRecycledViews(StoreAdapterConstant.VIEW_TYPE_SBVV, 10);
        recycledViewPool.setMaxRecycledViews(StoreAdapterConstant.VIEW_TYPE_BN0, 2);
        recycledViewPool.setMaxRecycledViews(StoreAdapterConstant.VIEW_TYPE_SBVH, 8);
        recycledViewPool.setMaxRecycledViews(StoreAdapterConstant.VIEW_TYPE_PW1, 1);
        recycledViewPool.setMaxRecycledViews(StoreAdapterConstant.VIEW_TYPE_DB0, 8);
        recycledViewPool.setMaxRecycledViews(StoreAdapterConstant.VIEW_TYPE_DB1, 8);
        mRecyclerView.setRecycledViewPool(recycledViewPool);
        mAdapter = new DzDelegateAdapter(layoutManager, true, getContext(), this, templetPresenter);
        mRecyclerView.setAdapter(mAdapter);
    }


    @Override
    protected void initData(View uiView) {
        Bundle bundle = getArguments();
        if (bundle != null) {
            templetID = bundle.getString(TempletContant.KEY_CHANNEL_TEMPLETID);
            subTempletID = bundle.getString(TempletContant.KEY_CHANNEL_ID);
            selectedId = bundle.getString(TempletContant.KEY_CHANNEL_SELECTED_ID);
            handleBundleShowView(bundle);
            indexPageType = bundle.getString(TempletContant.KEY_CHANNEL_TYPE, "");
            if ("5".equals(indexPageType)) {
                EventBusUtils.register(this);
            }
            subTempletTitle = bundle.getString(TempletContant.KEY_CHANNEL_TITLE);
            pageType = bundle.getString(TempletContant.KEY_CHANNEL_PAGETYPE);
            //            ALog.eDongdz("当前页面的数据展示：templetID：" + templetID + " subTempletID:" + subTempletID + " subTempletTitle:" + subTempletTitle + " pageType:" + pageType);
        }
    }

    private void handleBundleShowView(Bundle bundle) {
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
    }


    @Override
    protected void setListener(View uiView) {
        mRecyclerView.setLoadNextListener(new MyOnLoadNextListener());
        swipeLayout.setRefreshListener(new MyOnRefreshListener());
        statusView.setNetErrorClickListener(new StatusView.NetErrorClickListener() {
            @Override
            public void onNetErrorEvent(View v) {
                long current = System.currentTimeMillis();
                if (current - clickDelayTime > TempletContant.CLICK_DISTANSE) {
                    showLoading();
                    isFromLoadMore = false;
                    templetPresenter.getSingleChannelData(templetID, subTempletID, SpUtil.getinstance(getContext()).getPersonReadPref(), pageType, indexPageType);
                    clickDelayTime = current;
                }
            }
        });
        mRecyclerView.setScrollViewListener(new MyObservableScrollViewCallbacks());
    }


    /**
     * 跳出书城的时候仅处理bn0
     */
    public void pauseRecycleOnlyStopBn0() {
        if (null == mRecyclerView) {
            return;
        }
        VirtualLayoutManager layoutManager = (VirtualLayoutManager) mRecyclerView.getLayoutManager();
        int count = layoutManager.getItemCount();
        if (count > 0) {
            for (int i = 0; i < count; i++) {
                View view = layoutManager.findViewByPosition(i);
                if (view != null) {
                    if (view instanceof Bn0View) {
                        Bn0View bn0View = (Bn0View) view;
                        bn0View.stopAutoPlay();
                    }
                }
            }
        }
    }


    /**
     * 回到书城刷新仅处理bn0
     */
    public void resumeReferenceOnlyStopBn0() {
        if (null == mRecyclerView) {
            return;
        }
        VirtualLayoutManager layoutManager = (VirtualLayoutManager) mRecyclerView.getLayoutManager();
        int count = layoutManager.getItemCount();
        if (count > 0) {
            for (int i = 0; i < count; i++) {
                View view = layoutManager.findViewByPosition(i);
                if (view != null) {
                    if (view instanceof Bn0View) {
                        Bn0View bn0View = (Bn0View) view;
                        bn0View.startAutoPlay();
                    }
                }
            }
        }
    }

    /**
     * 加载下一个监听
     */
    private class MyOnLoadNextListener implements OnLoadNextListener {
        @Override
        public void onLoadNext() {
            onDestroyNetView();
            if (!TextUtils.isEmpty(nextPageUrl)) {
                isFromLoadMore = true;
                templetPresenter.getNextPageData(nextPageUrl);
            }
        }
    }

    @Override
    public void setTempletDatas(final List<BeanTempletInfo> section, final boolean isclear) {
        if (swipeLayout != null && swipeLayout.isRefreshing()) {
            swipeLayout.setRefreshing(false);
        }
        initTempletData(section, isclear);
    }


    @Override
    public void getSingleChannelDataFromCanche(String channelId) {
        if (templetPresenter != null) {
            isFromLoadMore = false;
            templetPresenter.getSingleChannelDataFromCanche(channelId);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    /**
     * 设置vip或者限免页面的数据
     *
     * @param indexPageType indexPageType
     * @param section       section
     * @param isclear       isclear
     * @param channelId     channelId
     */
    public void setVipOrLimitFreeData(String channelId, String indexPageType, List<BeanTempletInfo> section, boolean isclear) {
        this.subTempletID = channelId;
        this.indexPageType = indexPageType;
        if ("5".equals(indexPageType)) {
            EventBusUtils.register(this);
        }
        setTempletDatas(section, isclear);
    }

    /**
     * 设置拓展页面的数据
     *
     * @param channelId     channelId
     * @param indexPageType indexPageType
     * @param section       section
     * @param isclear       isclear
     */
    public void setExpendData(String channelId, String indexPageType, List<BeanTempletInfo> section, boolean isclear) {
        this.subTempletID = channelId;
        this.indexPageType = indexPageType;
        setTempletDatas(section, isclear);
    }

    /**
     * 频道点击或者滑动到的时候刷新数据
     *
     * @param templetId          :               针对259接口（限免二级页面）需要栏目id
     * @param beanSubTempletInfo ：栏目中对应的子栏目
     * @param pageType           ：请求接口类型
     * @param channelPosition    ：位置
     */
    public void referenceData(String templetId, BeanSubTempletInfo beanSubTempletInfo, String pageType, int channelPosition) {
        this.channelPosition = String.valueOf(channelPosition);
        templetID = templetId;
        if (beanSubTempletInfo != null) {
            indexPageType = beanSubTempletInfo.type;
            subTempletID = beanSubTempletInfo.id;
            subTempletTitle = beanSubTempletInfo.title;
        }
        this.pageType = pageType;
        isFromLoadMore = false;
        if (templetPresenter != null && loadDataState == 0) {
            templetPresenter.getSingleChannelData(templetID, subTempletID, SpUtil.getinstance(getContext()).getPersonReadPref(), pageType, indexPageType);
        }
    }


    /**
     * 滚动回调
     */
    private class MyObservableScrollViewCallbacks implements OnScrollViewListener {

        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
            firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();
        }

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {

        }
    }

    /**
     * 内容刷新监听
     */
    private class MyOnRefreshListener implements RefreshLayout.OnRefreshListener {
        @Override
        public void onRefresh() {
            isFromLoadMore = false;
            onDestroyNetView();
            if (!NetworkUtils.getInstance().checkNet()) {
                swipeLayout.setRefreshing(false);
                showNoNet(true);
                return;
            }
            templetPresenter.getSingleChannelData(templetID, subTempletID, SpUtil.getinstance(getContext()).getPersonReadPref(), pageType, indexPageType);
        }
    }


    @Override
    public void onDestroyNetView() {
        if (NetworkUtils.getInstance().checkNet()) {
            if (netErrorTopView != null) {
                netErrorTopLayout.removeView(netErrorTopView);
                netErrorTopView = null;
            }
            mRecyclerView.setPadding(0, 0, 0, 0);
        }
    }

    @Override
    public void onEventMainThread(EventMessage event) {
        int requestCode = event.getRequestCode();
        switch (requestCode) {
            case EventConstant.LOGIN_SUCCESS_UPDATE_SHELF:
            case EventConstant.CODE_VIP_OPEN_SUCCESS_REFRESH_STATUS:
            case EventConstant.LOGIN_CHECK_RSET_PERSON_LOGIN_STATUS:
                mAdapter.referenceVptView();
                break;
            default:
                break;
        }
    }

    /**
     * 返回顶部功能
     */
    public void returnTop() {
        if (mRecyclerView != null) {
            if (firstVisibleItemPosition > 10) {
                mRecyclerView.scrollToPosition(10);
            }
            mRecyclerView.smoothScrollToPosition(0);
            //            if (swipeLayout != null && !swipeLayout.isRefreshing()) {
            //                swipeLayout.setRefreshing(true);
            //            }
        }
    }

    @Override
    protected boolean isCustomPv() {
        return true;
    }
}
