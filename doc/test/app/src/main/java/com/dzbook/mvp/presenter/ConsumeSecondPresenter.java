package com.dzbook.mvp.presenter;

import android.content.Intent;
import android.text.TextUtils;

import com.dzbook.activity.account.ConsumeThirdActivity;
import com.dzbook.lib.utils.ALog;
import com.dzbook.mvp.BasePresenter;
import com.dzbook.mvp.UI.ConsumeSecondUI;
import com.dzbook.net.hw.HwRequestLib;
import com.ishugui.R;

import hw.sdk.net.bean.consume.ConsumeSecondBeanInfo;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

/**
 * ConsumeSecondPresenter
 *
 * @author lizz 2018/4/20.
 **/
public class ConsumeSecondPresenter extends BasePresenter {

    /**
     * 类型
     */
    public static final String TYPE = "type";
    /**
     * 下一个
     */
    public static final String NEXT_ID = "nextId";

    private ConsumeSecondUI mUI;
    private int index = 1;
    private String totalNum = "20";

    private String type;
    private String nextId;

    /**
     * 构造
     *
     * @param ui ui
     */
    public ConsumeSecondPresenter(ConsumeSecondUI ui) {
        this.mUI = ui;
    }

    /**
     * 获取参数
     */
    public void getParams() {
        Intent intent = mUI.getActivity().getIntent();
        if (intent != null) {
            type = intent.getStringExtra(TYPE);
            nextId = intent.getStringExtra(NEXT_ID);
        }

        if (nextId == null) {
            nextId = "";
        }

        if (TextUtils.isEmpty(type)) {
            mUI.finish();
        }
    }

    public String getType() {
        return type;
    }

    /**
     * 加载更多
     */
    public void loadMoreNetConsumeData() {
        index = index + 1;
        getNetConsumeData(false);
    }

    /**
     * 加载数据
     *
     * @param isFirstLoad isFirstLoad
     */
    public void getNetConsumeData(final boolean isFirstLoad) {
        if (isFirstLoad) {
            index = 1;
        }

        Disposable disposable = Observable.create(new ObservableOnSubscribe<ConsumeSecondBeanInfo>() {

            @Override
            public void subscribe(ObservableEmitter<ConsumeSecondBeanInfo> e) throws Exception {
                ConsumeSecondBeanInfo sumBeanInfo = null;
                try {
                    sumBeanInfo = HwRequestLib.getInstance().getConsumeSecondInfo(type, nextId, index + "", totalNum);
                } catch (Exception e1) {
                    ALog.printStackTrace(e1);
                }
                e.onNext(sumBeanInfo);
                e.onComplete();

            }
        }).observeOn(AndroidSchedulers.mainThread()).subscribeOn(Schedulers.io()).subscribeWith(new DisposableObserver<ConsumeSecondBeanInfo>() {

            @Override
            public void onNext(ConsumeSecondBeanInfo beanInfo) {
                handleOnNext(beanInfo, isFirstLoad);
            }

            @Override
            public void onError(Throwable e) {
                if (isFirstLoad) {
                    mUI.dismissLoadProgress();
                    mUI.showNoNetView();
                }
            }

            @Override
            public void onComplete() {
                if (!isFirstLoad) {
                    mUI.stopLoadMore();
                }
            }

            @Override
            protected void onStart() {
                if (isFirstLoad) {
                    mUI.showLoadProgress();
                }
            }
        });

        composite.addAndDisposeOldByKey("getNetConsumeData", disposable);
    }

    private void handleOnNext(ConsumeSecondBeanInfo beanInfo, boolean isFirstLoad) {
        if (isFirstLoad) {
            mUI.dismissLoadProgress();
        }
        if (beanInfo != null && beanInfo.isSuccess()) {
            if (beanInfo.isExistData()) {
                mUI.setBookConsumeSum(beanInfo.consumeSecondBeans, isFirstLoad);
            } else {
                if (isFirstLoad) {
                    mUI.showNoDataView();
                } else {
                    mUI.setHasMore(false);
                    //                            mUI.showMessage(R.string.no_more_data);
                    mUI.showAllTips();
                }
            }
        } else {
            if (isFirstLoad) {
                mUI.showNoNetView();
            } else {
                mUI.setHasMore(true);
                mUI.showMessage(R.string.request_data_failed);
            }
        }
    }

    /**
     * destroy
     */
    public void destroy() {
        composite.disposeAll();
    }

    /**
     * 跳转ConsumeThirdActivity
     *
     * @param consumeID consumeID
     * @param bookId    bookId
     */
    public void launchThirdConsume(String consumeID, String bookId) {
        ConsumeThirdActivity.launch(mUI.getActivity(), consumeID, bookId);
    }
}
