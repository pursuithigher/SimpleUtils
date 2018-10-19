package com.dzbook.mvp.presenter;

import android.app.Activity;

import com.dzbook.activity.detail.BookCommentAdapter;
import com.dzbook.lib.utils.ALog;
import com.dzbook.mvp.BasePresenter;
import com.dzbook.mvp.UI.BookCommentPersonCenterUI;
import com.dzbook.net.hw.HwRequestLib;
import com.dzbook.utils.ListUtils;
import com.dzbook.utils.hw.LoginCheckUtils;
import com.dzbook.utils.hw.LoginUtils;
import com.ishugui.R;
import com.iss.view.common.ToastAlone;

import hw.sdk.net.bean.bookDetail.BeanCommentMore;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

/**
 * BookCommentPersonCenterPresenter
 *
 * @author Winzows on 2017/12/8.
 */

public class BookCommentPersonCenterPresenter extends BasePresenter {

    private BookCommentPersonCenterUI mUI;
    private int pageIndex = 1;

    private LoginCheckUtils loginCheckUtils = null;

    /**
     * 构造
     *
     * @param mUI mUi
     */
    public BookCommentPersonCenterPresenter(BookCommentPersonCenterUI mUI) {
        this.mUI = mUI;
    }

    /**
     * 请求数据
     *
     * @param loadType loadType
     */
    public void requestData(final int loadType) {
        requestData(loadType, false);
    }

    /**
     * 请求数据
     *
     * @param isTokenInvalidRetry isTokenInvalidRetry
     * @param loadType            loadType
     */
    public void requestData(final int loadType, final boolean isTokenInvalidRetry) {
        Disposable disposable = Observable.create(new ObservableOnSubscribe<BeanCommentMore>() {
            @Override
            public void subscribe(ObservableEmitter<BeanCommentMore> e) {
                try {
                    if (loadType == BookCommentAdapter.LOAD_TYPE_LOADMORE) {
                        pageIndex++;
                    } else {
                        pageIndex = 1;
                    }
                    BeanCommentMore beanCommentMore = HwRequestLib.getInstance().userCommentRequest(pageIndex, 15);
                    e.onNext(beanCommentMore);
                    e.onComplete();
                } catch (Exception ex) {
                    ALog.printStackTrace(ex);
                    e.onError(ex);
                }
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribeWith(new DisposableObserver<BeanCommentMore>() {
            @Override
            public void onNext(BeanCommentMore value) {
                if (value != null) {
                    if (value.isSuccess()) {
                        if (loadType == BookCommentAdapter.LOAD_TYPE_LOADMORE) {
                            if (ListUtils.isEmpty(value.commentList)) {
                                mUI.noMore();
                            } else {
                                mUI.fillData(value, loadType);
                            }
                        } else {
                            if (ListUtils.isEmpty(value.commentList)) {
                                mUI.showEmpty();
                            } else {
                                mUI.fillData(value, loadType);
                                mUI.showView();
                            }
                        }
                    } else {

                        if (!isTokenInvalidRetry && value.isTokenExpireOrNeedLogin()) {
                            tokenInvalidRetry(loadType);
                            return;
                        }

                        mUI.showEmpty();
                        ToastAlone.showShort(R.string.comment_error);
                    }
                } else {
                    mUI.showEmpty();
                    ToastAlone.showShort(R.string.comment_error);
                }
                mUI.stopLoad();
            }

            @Override
            public void onError(Throwable e) {
                mUI.dissMissDialog();
                mUI.onError();
                mUI.initNetErrorStatus();
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
            }
        });

        composite.addAndDisposeOldByKey("requestData" + isTokenInvalidRetry, disposable);
    }


    /**
     * destroy
     */
    public void destroy() {
        composite.disposeAll();
        if (loginCheckUtils != null) {
            loginCheckUtils.resetAgainObtainListener();
            loginCheckUtils.disHuaWeiConnect();
        }
    }

    /**
     * 只重试一次
     */
    private void tokenInvalidRetry(final int loadType) {
        if (loginCheckUtils == null) {
            loginCheckUtils = LoginCheckUtils.getInstance();
        }
        loginCheckUtils.againObtainAppToken((Activity) mUI.getContext(), new LoginUtils.LoginCheckListenerSub() {
            @Override
            public void loginFail() {
                mUI.showEmpty();
                ToastAlone.showShort(R.string.comment_error);
            }

            @Override
            public void loginComplete() {
                requestData(loadType, true);
            }
        });
    }

}
