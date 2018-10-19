package com.dzbook.mvp.presenter;


import android.app.Activity;

import com.dzbook.activity.detail.BookCommentAdapter;
import com.dzbook.lib.utils.ALog;
import com.dzbook.mvp.BasePresenter;
import com.dzbook.mvp.UI.BookCommentDetailUI;
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
 * BookCommentPresenter
 *
 * @author Winzows on 2017/11/27.
 */

public class BookCommentPresenter extends BasePresenter {
    private BookCommentDetailUI mUI;
    private int pageIndex = 1;

    private LoginCheckUtils loginCheckUtils = null;

    /**
     * 构造
     *
     * @param mUI ui
     */
    public BookCommentPresenter(BookCommentDetailUI mUI) {
        this.mUI = mUI;
    }

    /**
     * 请求数据
     *
     * @param bookID              bookID
     * @param loadType            loadType
     * @param isTokenInvalidRetry isTokenInvalidRetry
     */
    public void requestData(final String bookID, final int loadType, final boolean isTokenInvalidRetry) {
        Disposable disposable = Observable.create(new ObservableOnSubscribe<BeanCommentMore>() {
            @Override
            public void subscribe(ObservableEmitter<BeanCommentMore> e) {
                try {
                    if (loadType == BookCommentAdapter.LOAD_TYPE_LOADMORE) {
                        pageIndex++;
                    } else {
                        pageIndex = 1;
                    }
                    BeanCommentMore beanCommentMore = HwRequestLib.getInstance().moreCommentRequest(bookID, pageIndex, 15);
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
                handleOnNext(value, loadType, isTokenInvalidRetry, bookID);
            }

            @Override
            public void onError(Throwable e) {
                mUI.dissMissDialog();
                mUI.onError();
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

    private void handleOnNext(BeanCommentMore value, int loadType, boolean isTokenInvalidRetry, String bookID) {
        if (value != null) {
            if (value.isSuccess()) {
                handleIsSuccess(value, loadType);
            } else {
                if (handleIsFail(value, loadType, isTokenInvalidRetry, bookID)) {
                    return;
                }
            }
        } else {
            mUI.showEmpty();
            ToastAlone.showShort(R.string.comment_error);
        }
        mUI.stopLoad();
    }

    private boolean handleIsFail(BeanCommentMore value, int loadType, boolean isTokenInvalidRetry, String bookID) {
        if (!isTokenInvalidRetry && value.isTokenExpireOrNeedLogin()) {
            tokenInvalidRetry(bookID, loadType);
            return true;
        }
        mUI.showEmpty();
        ToastAlone.showShort(R.string.comment_error);
        return false;
    }

    private void handleIsSuccess(BeanCommentMore value, int loadType) {
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
    private void tokenInvalidRetry(final String bookID, final int loadType) {
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
                requestData(bookID, loadType, true);
            }
        });
    }

}
