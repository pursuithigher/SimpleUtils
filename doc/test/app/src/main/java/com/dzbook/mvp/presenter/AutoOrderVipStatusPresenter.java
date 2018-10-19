package com.dzbook.mvp.presenter;

import android.app.Activity;

import com.dzbook.lib.rx.CompositeDisposable;
import com.dzbook.lib.utils.ALog;
import com.dzbook.mvp.BasePresenter;
import com.dzbook.mvp.UI.AutoOrderVipStatusUI;
import com.dzbook.net.hw.HwRequestLib;
import com.dzbook.view.common.dialog.CustomHintDialog;
import com.dzbook.view.common.dialog.base.CustomDialogBusiness;
import com.ishugui.R;

import hw.sdk.net.bean.vip.VipAutoRenewStatus;
import hw.sdk.net.bean.vip.VipCancelAutoRenewBeanInfo;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

/**
 * 自动续订状态 Presenter
 *
 * @author KongXP 2018/4/21.
 */
public class AutoOrderVipStatusPresenter extends BasePresenter {

    private AutoOrderVipStatusUI mUI;

    private CompositeDisposable composite = new CompositeDisposable();

    private CustomHintDialog cancelDialog, enterDialog;

    /**
     * 构造
     *
     * @param activity activity
     * @param ui       ui
     */
    public AutoOrderVipStatusPresenter(Activity activity, AutoOrderVipStatusUI ui) {
        mUI = ui;
    }


    /**
     * 获取自动续订状态
     */
    public void getAutoOrderVipStatus() {
        Observable<VipAutoRenewStatus> observable = Observable.create(new ObservableOnSubscribe<VipAutoRenewStatus>() {
            @Override
            public void subscribe(ObservableEmitter<VipAutoRenewStatus> e) {
                VipAutoRenewStatus vipAutoRenewStatus = null;
                try {
                    vipAutoRenewStatus = HwRequestLib.getInstance().getVipAutoRenewStatusInfo();
                } catch (Exception e1) {
                    ALog.printStackTrace(e1);
                }
                e.onNext(vipAutoRenewStatus);
                e.onComplete();
            }
        });
        Disposable disposable = observable.subscribeOn(Schedulers.io()).
                observeOn(AndroidSchedulers.mainThread()).
                subscribeWith(new DisposableObserver<VipAutoRenewStatus>() {

                    @Override
                    public void onNext(VipAutoRenewStatus result) {
                        mUI.dismissLoadProgress();

                        if (result != null && result.isSuccess()) {
                            if (result.isAutoOrderVipOpenSucess()) {
                                mUI.setVipOrderStatusInfo(result);
                            } else {
                                mUI.showNoDataView();
                            }
                        } else {
                            mUI.showNoDataView();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        ALog.eZz("getUserInfoFromNet " + e.getMessage());
                    }

                    @Override
                    public void onComplete() {
                        ALog.eZz("getUserInfoFromNet onComplete");
                    }

                    @Override
                    protected void onStart() {
                        mUI.showLoadProgress();
                    }
                });
        composite.addAndDisposeOldByKey("getautoordervipstatus", disposable);
    }

    /**
     * 取消自动续订
     */
    public void cancelContinueMonthInfo() {
        showCancelDialog();
    }

    /**
     * 析构
     */
    public void destroy() {
        composite.disposeAll();
        if (cancelDialog != null) {
            cancelDialog.dismiss();
            cancelDialog = null;
        }
        if (enterDialog != null) {
            enterDialog.dismiss();
            enterDialog = null;
        }
    }

    /**
     * 取消包月的dialog
     */
    public void showCancelDialog() {
        if (cancelDialog == null) {
            cancelDialog = new CustomHintDialog(mUI.getActivity(), CustomDialogBusiness.STYLE_DIALOG_NORMAL);
            cancelDialog.setTitle(mUI.getActivity().getResources().getString(R.string.cancel_monthly_dialog_title));
            cancelDialog.setDesc(mUI.getActivity().getResources().getString(R.string.cancel_monthly_dialog_content));
            cancelDialog.setCheckListener(new CustomDialogBusiness.OnCheckListener() {
                @Override
                public void clickConfirm(Object object) {
                    getCancelNet();
                }

                @Override
                public void clickCancel() {

                }
            });
            cancelDialog.setCancelTxt(mUI.getActivity().getResources().getString(R.string.cancel_monthly_dialog_think_again));
            cancelDialog.setConfirmTxt(mUI.getActivity().getResources().getString(R.string.cancel_monthly_dialog_enter_cancel));
        }
        cancelDialog.show();
    }


    private void getCancelNet() {
        Disposable disposable = Observable.create(new ObservableOnSubscribe<VipCancelAutoRenewBeanInfo>() {
            @Override
            public void subscribe(ObservableEmitter<VipCancelAutoRenewBeanInfo> e) {
                VipCancelAutoRenewBeanInfo vipContinueOpenHisBeanInfo = null;
                try {
                    vipContinueOpenHisBeanInfo = HwRequestLib.getInstance().cancelVipAutoRenewInfo();
                } catch (Exception e1) {
                    ALog.printStackTrace(e1);
                }
                e.onNext(vipContinueOpenHisBeanInfo);
                e.onComplete();
            }
        }).subscribeOn(Schedulers.io()).
                observeOn(AndroidSchedulers.mainThread()).
                subscribeWith(new DisposableObserver<VipCancelAutoRenewBeanInfo>() {

                    @Override
                    public void onNext(VipCancelAutoRenewBeanInfo result) {

                        if (result != null) {
                            if (result.isSuccess()) {
                                String resultInfo = result.tips;
                                showEnterDialog(resultInfo);
                            } else {
                                mUI.showMessage(result.getRetMsg());
                            }
                        } else {
                            mUI.isShowNotNetDialog();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        ALog.eZz("getUserInfoFromNet " + e.getMessage());
                    }

                    @Override
                    public void onComplete() {
                        ALog.eZz("getUserInfoFromNet onComplete");
                    }
                });

        composite.addAndDisposeOldByKey("getCancelVipNet", disposable);
    }

    private void showEnterDialog(String pCon) {
        if (enterDialog == null) {
            enterDialog = new CustomHintDialog(mUI.getActivity(), CustomDialogBusiness.STYLE_DIALOG_CANCEL);
            enterDialog.setTitle(mUI.getActivity().getResources().getString(R.string.cancel_monthly_dialog_title));
            enterDialog.setCheckListener(new CustomDialogBusiness.OnCheckListener() {
                @Override
                public void clickConfirm(Object object) {

                }

                @Override
                public void clickCancel() {
                    getAutoOrderVipStatus();
                }
            });
            enterDialog.setCancelTxt(mUI.getActivity().getResources().getString(R.string.gift_exchange));
        }
        enterDialog.setDesc(pCon);
        enterDialog.show();
    }


}
