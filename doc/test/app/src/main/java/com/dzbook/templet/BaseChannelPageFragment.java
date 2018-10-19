package com.dzbook.templet;

import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.dzbook.fragment.main.BaseFragment;
import com.dzbook.lib.utils.ALog;
import com.dzbook.log.LogConstants;
import com.dzbook.mvp.UI.TempletUI;
import com.dzbook.mvp.presenter.TempletPresenter;
import com.dzbook.net.hw.RequestCall;
import com.dzbook.templet.adapter.DzDelegateAdapter;
import com.dzbook.utils.DimensionPixelUtil;
import com.dzbook.view.PageView.PageRecyclerView;
import com.dzbook.view.PageView.PageState;
import com.dzbook.view.common.NetErrorTopView;
import com.dzbook.view.common.StatusView;
import com.dzbook.view.common.loading.RefreshLayout;
import com.ishugui.R;
import com.iss.view.common.ToastAlone;

import java.util.List;

import hw.sdk.net.bean.store.BeanSubTempletInfo;
import hw.sdk.net.bean.store.BeanTempletInfo;

/**
 * ChannelPageFragment
 *
 * @author winzows 2018/6/26
 */

public abstract class BaseChannelPageFragment extends BaseFragment implements TempletUI {
    protected TempletPresenter templetPresenter;

    protected StatusView statusView;
    protected PageRecyclerView mRecyclerView;
    protected RefreshLayout swipeLayout;
    protected NetErrorTopView netErrorTopView;
    protected LinearLayout netErrorTopLayout;
    /**
     * 0：未加载 1：加载中 2：已加载
     */
    protected int loadDataState = 0;

    protected View rootView;

    protected DzDelegateAdapter mAdapter;

    //针对二级限免页面：二级限免数据需要两个数据：限免所属的栏目id，限免所属的单个tabid
    protected String templetID;
    //频道id
    protected String subTempletID;
    //被选中的id
    protected String selectedId;
    //频道title
    protected String subTempletTitle;
    //频道potion
    protected String channelPosition = "0";

    //页面类型：由于书城和限免都复用这个fragment，用于打点和接口请求
    protected String pageType = LogConstants.MODULE_NSC;

    //书城vipnative，用来区别
    protected String indexPageType = "";

    protected boolean isFromLoadMore;

    protected String nextPageUrl = "";

    /**
     * 初始化数据
     *
     * @param section section
     * @param isclear isclear
     */
    protected void initTempletData(List<BeanTempletInfo> section, boolean isclear) {
        BeanTempletInfo templetInfo = templetPresenter.getLoadingItem(section);
        if (templetInfo != null && templetInfo.action != null && !TextUtils.isEmpty(templetInfo.action.url)) {
            nextPageUrl = RequestCall.getNextPageUrl(templetInfo.action.url);
            mRecyclerView.setState(PageState.Loadable);
        } else {
            nextPageUrl = "";
            mRecyclerView.setState(PageState.End);
        }
        if (isclear) {
            mAdapter.setItems(section);
        } else {
            mAdapter.addItems(section);
        }
        statusView.showSuccess();
        if (swipeLayout != null && swipeLayout.getVisibility() != View.VISIBLE) {
            swipeLayout.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void showToastMsg(final String msg) {
        ToastAlone.showShort(msg);
    }

    @Override
    public void setLoadDataState(int state) {
        loadDataState = state;
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

    @Override
    public void limitfreeCompelete(final String state) {
        if (mAdapter != null) {
            mAdapter.limitFreeComplete(state);
        }
    }

    @Override
    public void setPageState(final boolean isFailed) {
        if (!isFailed) {
            mRecyclerView.setState(PageState.Loadable);
        } else {
            mRecyclerView.setState(PageState.Failed);
        }
        mAdapter.notifyDataSetChanged();
    }


    @Override
    public void getBookSuccess(final BeanSubTempletInfo subTempletInfo) {
        if (mAdapter != null) {
            mAdapter.getBookSuccess(subTempletInfo);
        }
    }


    @Override
    public String getLogModule() {
        return pageType;
    }

    @Override
    public String getLogAdid() {
        return subTempletTitle;
    }

    @Override
    public String getChannelID() {
        return subTempletID;
    }

    @Override
    public String getChannelPosition() {
        return channelPosition;
    }

    @Override
    public void showNoNet(boolean isShowEmptyView) {
        //没有网络
        ALog.cmtDebug("showNoNet");
        showResult(NetErrorTopView.TYPE_NO_NET, isShowEmptyView);

    }

    @Override
    public void showServerFail(boolean isShowEmptyView) {
        //服务器连接失败
        ALog.cmtDebug("showServerFail");
        showResult(NetErrorTopView.TYPE_SERVER_ERROR, isShowEmptyView);

    }

    @Override
    public void showServerEmpty(boolean isShowEmptyView) {
        //服务器返回数据为为空
        ALog.cmtDebug("showServerEmpty");
        showResult(NetErrorTopView.TYPE_SERVER_EMPTY, isShowEmptyView);

    }

    private void showResult(int type, boolean isShowEmptyView) {
        if (mAdapter.getAdaptersCount() > 0) {
            initNetErrorStatus(type);
            swipeLayout.setVisibility(View.VISIBLE);
        } else {
            swipeLayout.setVisibility(View.GONE);
            if (type == NetErrorTopView.TYPE_SERVER_EMPTY) {
                statusView.showEmpty();
            } else {
                statusView.showNetError(type);
            }
        }
    }

    private void initNetErrorStatus(int type) {
        initNetView(type);
        mRecyclerView.setPadding(0, getResources().getDimensionPixelSize(R.dimen.hw_dp_8), 0, 0);
    }

    private void initNetView(final int type) {
        onDestroyNetView();
        if (netErrorTopView == null) {
            netErrorTopView = new NetErrorTopView(getContext());
            netErrorTopView.setType(type);
            netErrorTopView.setOnFreshClickListener(new NetErrorTopView.FreshClickListener() {
                @Override
                public void onFresh(View v) {
                    swipeLayout.autoRefresh(0);
                }
            });
            netErrorTopLayout.addView(netErrorTopView, 0, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, DimensionPixelUtil.dip2px(getContext(), 48)));
        }
    }

}
