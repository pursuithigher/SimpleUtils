package com.dzbook.mvp.presenter;

import android.app.Activity;

import com.dzbook.mvp.BasePresenter;
import com.dzbook.mvp.UI.ActivityCenterUI;
import com.dzbook.net.hw.HwRequestLib;
import com.ishugui.R;

import hw.sdk.net.bean.ActivityCenterBean;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * 活动中心 Presenter
 *
 * @author gavin 2018/4/26
 */
public class ActivityCenterPresenter extends BasePresenter {
    private ActivityCenterUI mUI;

    /**
     * 构造
     *
     * @param activityCenterUI activityCenterUI
     */
    public ActivityCenterPresenter(ActivityCenterUI activityCenterUI) {
        mUI = activityCenterUI;
    }

    private boolean isActivityEmpty() {
        Activity activity = mUI.getActivity();
        return activity == null;
    }

    /**
     * 获取活动数据
     */
    public void getData() {
        Observable.create(new ObservableOnSubscribe<ActivityCenterBean>() {
            @Override
            public void subscribe(ObservableEmitter<ActivityCenterBean> observableEmitter) {
                try {
                    if (!isActivityEmpty()) {
                        ActivityCenterBean activityCenterBean = HwRequestLib.getInstance().getActivityCenterBean();
                        observableEmitter.onNext(activityCenterBean);
                        observableEmitter.onComplete();
                    }
                } catch (Exception e) {
                    observableEmitter.onError(e);

                }
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<ActivityCenterBean>() {
            @Override
            public void onSubscribe(Disposable disposable) {

            }

            @Override
            public void onNext(ActivityCenterBean activityCenterBeanList) {
                if (activityCenterBeanList.isSuccess()) {
                    if (activityCenterBeanList.isHasData()) {
                        mUI.setData(activityCenterBeanList.activityCenterBeans);
                    } else if (activityCenterBeanList.status == 0) {
                        mUI.showEmptyView(R.string.string_empty_expect);
                    } else if (activityCenterBeanList.status == 2) {
                        mUI.showEmptyView(R.string.string_empty_expect_end);
                    }
                } else {
                    mUI.showNoNetView();
                }
            }

            @Override
            public void onError(Throwable throwable) {
                mUI.dismissLoadingView();
                mUI.showNoNetView();
            }

            @Override
            public void onComplete() {

            }

        });

    }

    /**
     * destroy
     */
    public void destroy() {
        composite.disposeAll();
    }

}
