package com.dzbook.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageView;

import com.dzbook.activity.MainTypeActivity;
import com.dzbook.database.bean.HttpCacheInfo;
import com.dzbook.fragment.main.BaseFragment;
import com.dzbook.mvp.UI.NativeTypeIndexUI;
import com.dzbook.mvp.presenter.MainTypePresenterImpl;
import com.dzbook.net.hw.RequestCall;
import com.dzbook.utils.DBUtils;
import com.dzbook.utils.NetworkUtils;
import com.dzbook.view.common.StatusView;
import com.ishugui.R;

import java.util.ArrayList;

import hw.sdk.net.bean.type.BeanMainTypeLeft;
import hw.sdk.net.bean.type.BeanMainTypeRight;

/**
 * 分类 一级页面
 *
 * @author Winzows 17/3/29
 */

public class MainTypeContentFragment extends BaseFragment implements NativeTypeIndexUI {

    /**
     * tag
     */
    public static final String TAG = "MainTypeContentFragment";
    private RecyclerView recyclerViewRight, recyclerViewLeft;
    private MainTypePresenterImpl mainNewTypePresenter;
    private AlphaAnimation mShowAnimation;
    private StatusView statusView;
    private ImageView hwBack;


    @Override
    public String getTagName() {
        return TAG;
    }

    @Override
    protected View inflate(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_main_typecontent, container, false);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mainNewTypePresenter != null) {
            mainNewTypePresenter.destroy();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    protected void initView(View uiView) {
        recyclerViewLeft = uiView.findViewById(R.id.recyclerViewList);
        recyclerViewRight = uiView.findViewById(R.id.recyclerViewDetail);
        statusView = uiView.findViewById(R.id.statusView);
        hwBack = uiView.findViewById(R.id.imageview_back);
        mainNewTypePresenter = new MainTypePresenterImpl(this);

        if (getContext() instanceof MainTypeActivity) {
            hwBack.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void initData(View uiView) {
        statusView.showLoading();
        mainNewTypePresenter.initRecycleView(recyclerViewLeft, recyclerViewRight);
        requestData();
    }

    /**
     * 左边竖条  分类列表
     */
    @Override
    public void bindLeftCatalogData(ArrayList<BeanMainTypeLeft> list) {
        mainNewTypePresenter.bindLeftCatalogData(recyclerViewLeft, list);
    }

    /**
     * 右边GridView区域  分类列表
     */
    @Override
    public void bindRightCatalogData(ArrayList<BeanMainTypeRight> list, String categoryId, String categoryName, int leftPosition) {
        final long num = 400L;
        mainNewTypePresenter.bindRightCatalogData(recyclerViewRight, list, categoryId, categoryName, leftPosition);
        setShowAnimation(recyclerViewRight, num);

    }


    @Override
    public void onCatalogSelect(BeanMainTypeLeft categoryIndexBean, int leftPosition) {
        mainNewTypePresenter.onLeftCatalogSelect(categoryIndexBean, leftPosition);
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
    }

    @Override
    protected void setListener(View uiView) {
        statusView.setNetErrorClickListener(new StatusView.NetErrorClickListener() {
            @Override
            public void onNetErrorEvent(View v) {
                statusView.showLoading();
                requestData();
            }
        });
        hwBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Activity activity = getActivity();
                if (activity != null) {
                    activity.onBackPressed();
                }
            }
        });
    }

    @Override
    public void onError() {
        recyclerViewRight.setVisibility(View.GONE);
        recyclerViewLeft.setVisibility(View.GONE);
        statusView.showNetError();
    }

    @Override
    public void showView() {
        recyclerViewRight.setVisibility(View.VISIBLE);
        recyclerViewLeft.setVisibility(View.VISIBLE);
        statusView.showSuccess();
    }

    @Override
    public void onRequestData() {
        recyclerViewRight.setVisibility(View.GONE);
        recyclerViewLeft.setVisibility(View.GONE);
        statusView.showLoading();
    }


    @Override
    public void showEmpty() {
        recyclerViewRight.setVisibility(View.GONE);
        recyclerViewLeft.setVisibility(View.GONE);
        statusView.showEmpty(getResources().getString(R.string.string_empty_hint));
    }

    private void requestData() {
        if (NetworkUtils.getInstance().checkNet()) {
            mainNewTypePresenter.requestData(null);
        } else {
            HttpCacheInfo httpCacheInfo = DBUtils.findHttpCacheInfo(getContext(), RequestCall.MAIN_TYPE_INDEX);
            if (httpCacheInfo != null && !TextUtils.isEmpty(httpCacheInfo.response)) {
                mainNewTypePresenter.loadCache(httpCacheInfo.response);
            } else {
                onError();
            }
        }
    }

    private void setShowAnimation(final View view, long duration) {
        if (null == view || duration < 0) {
            return;
        }
        if (null != mShowAnimation) {
            mShowAnimation.cancel();
        }
        if (mShowAnimation == null) {
            final float num = 0.3f;
            mShowAnimation = new AlphaAnimation(num, 1.0f);
            mShowAnimation.setDuration(duration);
            mShowAnimation.setFillAfter(true);
        }

        mShowAnimation.setAnimationListener(new Animation.AnimationListener() {

            @Override
            public void onAnimationStart(Animation arg0) {
                view.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation arg0) {

            }

            @Override
            public void onAnimationEnd(Animation arg0) {

            }
        });
        view.startAnimation(mShowAnimation);
    }
}

