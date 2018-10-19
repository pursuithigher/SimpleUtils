package com.dzbook.mvp.presenter;

import android.animation.ValueAnimator;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;

import com.dzbook.adapter.MainTypeDetailAdapter;
import com.dzbook.lib.rx.CompositeDisposable;
import com.dzbook.lib.utils.ALog;
import com.dzbook.mvp.UI.NativeTypeDetailUI;
import com.dzbook.net.hw.HwRequestLib;
import com.dzbook.view.PullLoadMoreRecycleLayout;
import com.dzbook.view.type.MainTypeDetailTopView;

import java.util.ArrayList;

import hw.sdk.net.bean.BeanBookInfo;
import hw.sdk.net.bean.type.BeanMainTypeDetail;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

/**
 * MainTypeDetailPresenterImpl
 *
 * @author Winzows 2018/3/2
 */

public class MainTypeDetailPresenterImpl implements MainTypeDetailPresenter {
    /**
     * 加载类型 默认
     */
    public static final int LOAD_TYPE_DEFAULT = 0x0011;
    /**
     * 加载类型 加载更多
     */
    public static final int LOAD_TYPE_LOADMORE = 0x0012;
    /**
     * 加载更多 切换
     */
    public static final int LOAD_TYPE_LOAD_SWITCH = 0x0013;

    private CompositeDisposable composite = new CompositeDisposable();
    private NativeTypeDetailUI mUI;
    private MainTypeDetailAdapter detailAdapter;
    private int pageIndex = 1;
    private String flag = "1";
    private MainTypeDetailTopView topView;
    private View tempView;

    /**
     * 构造
     *
     * @param mUI mUI
     */
    public MainTypeDetailPresenterImpl(NativeTypeDetailUI mUI) {
        this.mUI = mUI;
    }

    @Override
    public void requestData(final int loadType, final BeanMainTypeDetail.TypeFilterBean filterBean) {
        mUI.clickHead();
        Observable<BeanMainTypeDetail> observable = Observable.create(new ObservableOnSubscribe<BeanMainTypeDetail>() {
            @Override
            public void subscribe(ObservableEmitter<BeanMainTypeDetail> e) {
                try {
                    handleFlag(loadType);

                    BeanMainTypeDetail bookInfo = HwRequestLib.getInstance().getMainTypeDetailData(filterBean.getSort(), filterBean.getTid(), filterBean.getStatus(), filterBean.getCid(), flag, pageIndex + "", "15");
                    e.onNext(bookInfo);
                    e.onComplete();
                } catch (Exception ex) {
                    ALog.printStackTrace(ex);
                    e.onError(ex);
                }
            }
        });
        Disposable disposable = observable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribeWith(new DisposableObserver<BeanMainTypeDetail>() {
            @Override
            public void onNext(BeanMainTypeDetail value) {
                if (LOAD_TYPE_DEFAULT == loadType) {
                    mUI.dismissLoadProgress();
                }
                handleValue(value, loadType);
            }

            @Override
            public void onError(Throwable e) {
                mUI.dissMissDialog();
                if (LOAD_TYPE_DEFAULT == loadType) {
                    mUI.onError();
                }
                //mUI.showMessage(R.string.net_work_notcool);
                mUI.stopLoad();
            }

            @Override
            public void onComplete() {
                mUI.dissMissDialog();
                mUI.stopLoad();
            }

            @Override
            protected void onStart() {
                super.onStart();
                mUI.removeFootView();
                if (LOAD_TYPE_DEFAULT == loadType) {
                    mUI.showLoadProgress();
                }
            }
        });
        composite.addAndDisposeOldByKey("requestTypeDetail", disposable);
    }

    /**
     * 处理 加载
     *
     * @param value    bean
     * @param loadType type
     */
    private void handleValue(BeanMainTypeDetail value, int loadType) {
        if (value != null) {
            if (value.isSuccess()) {
                if (loadType == LOAD_TYPE_LOADMORE) {
                    if (!value.checkBookInfoList() || value.bookInfoList.size() == 0) {
                        mUI.noMore();
                    } else {
                        mUI.bindBottomBookInfoData(loadType, value.bookInfoList);
                    }
                } else {
                    if (!value.checkBookInfoList() || value.bookInfoList.size() == 0) {
                        if (value.checkTopViewData()) {
                            mUI.bindTopViewData(value);
                        }
                        mUI.showEmpty();
                    } else {
                        mUI.bindTopViewData(value);
                    }
                    mUI.bindBottomBookInfoData(loadType, value.bookInfoList);
                }
                mUI.showView();
            } else {
                if (LOAD_TYPE_DEFAULT == loadType) {
                    mUI.onError();
                }
                //ToastAlone.showShort(R.string.net_work_notcool);
            }
        } else {
            if (LOAD_TYPE_DEFAULT == loadType) {
                mUI.onError();
            }
            //ToastAlone.showShort(R.string.net_work_notcool);
        }
        mUI.stopLoad();
    }

    /**
     * 处理flag
     *
     * @param loadType 加载类型
     */
    private void handleFlag(int loadType) {
        if (loadType == LOAD_TYPE_LOADMORE) {
            pageIndex++;
            flag = "0";
        } else if (loadType == LOAD_TYPE_LOAD_SWITCH) {
            pageIndex = 1;
            flag = "0";
        } else {
            pageIndex = 1;
            flag = "1";
        }
    }


    @Override
    public void bindBottomBookInfoData(int loadType, PullLoadMoreRecycleLayout loadMoreRecyclerViewLinearLayout, ArrayList<BeanBookInfo> bookInfoList) {
        if (loadMoreRecyclerViewLinearLayout.getAdapter() == null) {
            if (detailAdapter == null) {
                detailAdapter = new MainTypeDetailAdapter(mUI.getActivity());
            }
            loadMoreRecyclerViewLinearLayout.setAdapter(detailAdapter);
        }
        if (detailAdapter != null) {
            detailAdapter.putData(bookInfoList, loadType == LOAD_TYPE_LOADMORE);
        }
    }

    @Override
    public void bindTopViewData(PullLoadMoreRecycleLayout loadMoreLayout, BeanMainTypeDetail bean, BeanMainTypeDetail.TypeFilterBean filterBean, String defaultSelect) {
        topView = new MainTypeDetailTopView(mUI.getContext());
        if (bean == null || filterBean == null) {
            mUI.onError();
            return;
        }

        if (!TextUtils.isEmpty(defaultSelect)) {
            topView.setDefaultSelectTag(defaultSelect);
        }

        topView.setFilterBean(filterBean);
        topView.setTypeDetailPresenter(this);

        topView.bindFirstMarkData(bean.sortMarkList);
        topView.bindCategoryData(bean.categoryMarkList);
        topView.bindBookStatusData(bean.statusMarkList);

        if (loadMoreLayout.getAdapter() == null) {
            if (detailAdapter == null) {
                detailAdapter = new MainTypeDetailAdapter(mUI.getActivity(), filterBean);
            }
            loadMoreLayout.setAdapter(detailAdapter);
        }
        loadMoreLayout.removeAllHeaderView();
        topView.setViewType(MainTypeDetailTopView.TYPE_TOP_VIEW);
        loadMoreLayout.addHeaderView(topView);
    }

    @Override
    public void onRequestStart() {
        if (detailAdapter != null) {
            detailAdapter.setLoading(true);
            detailAdapter.putData(null, false);
        }
    }

    @Override
    public void stopLoad() {
        if (detailAdapter != null) {
            detailAdapter.setLoading(false);
            detailAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public String getPI() {
        if (null != topView) {
            return topView.getCurrentGHInfo();
        }
        return null;
    }


    @Override
    public MainTypeDetailTopView addSuspensionView(PullLoadMoreRecycleLayout loadMoreLayout, final ViewGroup view, View tipsView) {
        loadMoreLayout.removeAllHeaderView();
        addTempView(loadMoreLayout);
        view.removeView(tipsView);
        topView.setViewType(MainTypeDetailTopView.TYPE_SUP_VIEW);
        view.addView(topView);
        //        scaleTopView(topView.getHeight(), view.getHeight());
        return topView;
    }

    private void addTempView(PullLoadMoreRecycleLayout loadMoreLayout) {
        if (tempView == null) {
            tempView = new View(mUI.getContext());
        }
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, topView.getMeasuredHeight());
        tempView.setLayoutParams(params);
        loadMoreLayout.addHeaderView(tempView);
    }

    private void scaleTopView(int finalHeight, int currentHeight) {
        ValueAnimator valueAnimator = ValueAnimator.ofFloat(currentHeight, finalHeight);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                float scaleHeight = (Float) valueAnimator.getAnimatedValue();
                ViewGroup.LayoutParams layoutParams = topView.getLayoutParams();
                layoutParams.height = (int) scaleHeight;
                topView.setLayoutParams(layoutParams);
            }
        });
        valueAnimator.setDuration(300);
        valueAnimator.start();
    }

    @Override
    public String getSubTitleStr() {
        if (topView != null) {
            return topView.getSubTitleStr();
        }
        return "热门/全部/全部";
    }

    @Override
    public void onDestroy() {
        topView = null;
        detailAdapter = null;
        composite.disposeAll();
    }

    @Override
    public void addRecycleHeaderView(PullLoadMoreRecycleLayout loadMoreLayout, int type) {
        try {
            if (tempView != null) {
                loadMoreLayout.removeHeaderView(tempView);
            }

            if (loadMoreLayout.hasHeader()) {
                return;
            }
            topView.setViewType(type);
            loadMoreLayout.addHeaderView(topView);
        } catch (Throwable e) {
            ALog.printExceptionWz(e);
        }
    }

}
