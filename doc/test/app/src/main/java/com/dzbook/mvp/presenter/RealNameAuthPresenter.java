package com.dzbook.mvp.presenter;

import android.app.Activity;
import android.text.TextUtils;

import com.dzbook.lib.utils.ALog;
import com.dzbook.mvp.BasePresenter;
import com.dzbook.mvp.UI.RealNameAuthUI;
import com.dzbook.net.hw.HwRequestLib;
import com.dzbook.utils.hw.LoginCheckUtils;
import com.dzbook.utils.hw.LoginUtils;
import com.iss.app.BaseActivity;
import com.iss.view.common.ToastAlone;

import hw.sdk.net.bean.BeanSwitchPhoneNum;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

/**
 * RealNameAuthPresenter
 *
 * @author winzows  2018/4/17
 */

public class RealNameAuthPresenter extends BasePresenter {
    private RealNameAuthUI mUI;

    private LoginCheckUtils loginCheckUtils = null;

    /**
     * 构造
     *
     * @param mUI mUI
     */
    public RealNameAuthPresenter(RealNameAuthUI mUI) {
        this.mUI = mUI;
    }

    /**
     * 请求数据
     *
     * @param isTokenInvalidRetry isTokenInvalidRetry
     */
    public void requestData(final boolean isTokenInvalidRetry) {
        Disposable disposable = Observable.create(new ObservableOnSubscribe<BeanSwitchPhoneNum>() {
            @Override
            public void subscribe(ObservableEmitter<BeanSwitchPhoneNum> e) {
                try {
                    BeanSwitchPhoneNum beanSwitchPhoneNum = HwRequestLib.getInstance().getSwitchPhoneNumInfo();
                    e.onNext(beanSwitchPhoneNum);
                    e.onComplete();
                } catch (Exception ex) {
                    ALog.printStackTrace(ex);
                    e.onError(ex);
                }
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribeWith(new DisposableObserver<BeanSwitchPhoneNum>() {
            @Override
            public void onNext(BeanSwitchPhoneNum value) {
                if (value != null) {
                    if (value.isSuccess()) {
                        if (value.isSwitch() && value.checkBindData()) {
                            mUI.bindSwitchPhoneData(value);
                            mUI.showSwitchPhoneView();
                        } else {
                            mUI.showBindPhoneView();
                        }
                    } else {

                        if (!isTokenInvalidRetry && value.isTokenExpireOrNeedLogin()) {
                            tokenInvalidRetry(value);
                            return;
                        }

                        if (!TextUtils.isEmpty(value.getRetMsg())) {
                            ToastAlone.showShort(value.getRetMsg());
                        } else {
                            if (mUI.getContext() instanceof BaseActivity) {
                                ((BaseActivity) mUI.getContext()).showNotNetDialog();
                            }
                        }
                        mUI.showErrorView();
                    }
                }
            }

            @Override
            public void onError(Throwable e) {
                mUI.showErrorView();
            }

            @Override
            public void onComplete() {
                mUI.dissMissDialog();
            }

            @Override
            protected void onStart() {
                super.onStart();
                mUI.onRequestStart();
            }
        });

        composite.addAndDisposeOldByKey("RealNameAuthPresenter" + isTokenInvalidRetry, disposable);
    }

    /**
     * 请求数据
     */
    public void requestData() {
        requestData(false);
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
     * 延迟个0.5秒
     */
    public void finish() {
        mUI.finishActivity();
    }

    public String getPageTag() {
        return mUI.getPageTag();
    }

    /**
     * 只重试一次
     */
    private void tokenInvalidRetry(final BeanSwitchPhoneNum value) {
        mUI.onRequestStart();
        if (loginCheckUtils == null) {
            loginCheckUtils = LoginCheckUtils.getInstance();
        }
        loginCheckUtils.againObtainAppToken((Activity) mUI.getContext(), new LoginUtils.LoginCheckListenerSub() {
            @Override
            public void loginFail() {
                mUI.dissMissDialog();
                if (!TextUtils.isEmpty(value.getRetMsg())) {
                    ToastAlone.showShort(value.getRetMsg());
                } else {
                    if (mUI.getContext() instanceof BaseActivity) {
                        ((BaseActivity) mUI.getContext()).showNotNetDialog();
                    }
                }
                mUI.showErrorView();
            }

            @Override
            public void loginComplete() {
                requestData(true);
            }
        });
    }
}
