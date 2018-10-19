package com.dzbook.mvp.presenter;

import android.content.Intent;
import android.text.TextUtils;

import com.dzbook.lib.utils.ALog;
import com.dzbook.mvp.BasePresenter;
import com.dzbook.mvp.UI.ConsumeThirdUI;
import com.dzbook.net.hw.HwRequestLib;
import com.ishugui.R;

import hw.sdk.net.bean.consume.ConsumeThirdBeanInfo;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

/**
 * ConsumeThirdPresenter
 *
 * @author lizz 2018/4/20.
 **/
public class ConsumeThirdPresenter extends BasePresenter {
    /**
     * 消费id
     */
    public static final String CONSUME_ID = "consume_id";
    /**
     * 书籍id
     */
    public static final String BOOK_ID = "book_id";

    private ConsumeThirdUI mUI;
    private int index = 1;
    private String totalNum = "20";
    private String consumeId;
    private String bookId;

    /**
     * 构造
     *
     * @param ui ui
     */
    public ConsumeThirdPresenter(ConsumeThirdUI ui) {
        this.mUI = ui;
    }

    /**
     * 获取参数
     */
    public void getParams() {
        Intent intent = mUI.getActivity().getIntent();
        if (intent != null) {
            consumeId = intent.getStringExtra(CONSUME_ID);
            bookId = intent.getStringExtra(BOOK_ID);
        }

        if (TextUtils.isEmpty(consumeId)) {
            mUI.finish();
        }
    }

    /**
     * 获取更多数据
     */
    public void loadMoreNetConsumeData() {
        index = index + 1;
        getNetConsumeData(false);
    }

    /**
     * 获取数据
     *
     * @param isFirstLoad isFirstLoad
     */
    public void getNetConsumeData(final boolean isFirstLoad) {
        if (isFirstLoad) {
            index = 1;
        }

        Disposable disposable = Observable.create(new ObservableOnSubscribe<ConsumeThirdBeanInfo>() {

            @Override
            public void subscribe(ObservableEmitter<ConsumeThirdBeanInfo> e) throws Exception {
                ConsumeThirdBeanInfo sumBeanInfo = null;
                try {
                    sumBeanInfo = HwRequestLib.getInstance().getConsumeThirdInfo(consumeId, bookId, index + "", totalNum);
                } catch (Exception e1) {
                    ALog.printStackTrace(e1);
                }
                e.onNext(sumBeanInfo);
                e.onComplete();

            }
        }).observeOn(AndroidSchedulers.mainThread()).subscribeOn(Schedulers.io()).subscribeWith(new DisposableObserver<ConsumeThirdBeanInfo>() {

            @Override
            public void onNext(ConsumeThirdBeanInfo beanInfo) {
                if (isFirstLoad) {
                    mUI.dismissLoadProgress();
                }
                handleOnNext(beanInfo, isFirstLoad);
            }

            @Override
            public void onError(Throwable e) {
                if (isFirstLoad) {
                    mUI.dismissLoadProgress();
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

        composite.addAndDisposeOldByKey("getConsumeThirdData", disposable);
    }

    private void handleOnNext(ConsumeThirdBeanInfo beanInfo, boolean isFirstLoad) {
        if (beanInfo != null && beanInfo.isSuccess()) {
            handleOnSuccess(beanInfo, isFirstLoad);
        } else {
            if (isFirstLoad) {
                mUI.showNoNetView();
            } else {
                mUI.setHasMore(true);
                mUI.showMessage(R.string.request_data_failed);
            }
        }
    }

    private void handleOnSuccess(ConsumeThirdBeanInfo beanInfo, boolean isFirstLoad) {
        if (beanInfo.isExistData()) {
            mUI.setBookConsumeSum(beanInfo.consumeThirdBeans, isFirstLoad);
        } else {
            if (isFirstLoad) {
                mUI.showNoDataView();
            } else {
                mUI.setHasMore(false);
                mUI.showAllTips();
                //                            mUI.showMessage(R.string.no_more_data);
            }
        }
    }

    /**
     * destroy
     */
    public void destroy() {
        composite.disposeAll();
    }

}
