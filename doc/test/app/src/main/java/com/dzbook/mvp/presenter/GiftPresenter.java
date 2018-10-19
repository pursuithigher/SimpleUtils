package com.dzbook.mvp.presenter;

import com.dzbook.lib.rx.CompositeDisposable;
import com.dzbook.lib.utils.ALog;
import com.dzbook.mvp.UI.GiftUI;
import com.dzbook.net.hw.HwRequestLib;
import com.dzbook.service.InsertBookInfoDataUtil;
import com.dzbook.view.common.dialog.CustomHintDialog;
import com.dzbook.view.common.dialog.base.CustomDialogBusiness;
import com.ishugui.R;

import hw.sdk.net.bean.gift.GiftListBeanInfo;
import hw.sdk.net.bean.store.BeanGetBookInfo;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

/**
 * GiftPresenter
 *
 * @author KongXP on 2018/4/25.
 */

public class GiftPresenter {

    CompositeDisposable composite = new CompositeDisposable();
    private GiftUI mUI;

    /**
     * 页码
     */
    private int page = 1;


    /**
     * 构造
     *
     * @param personRechargeRecordUI ui
     */
    public GiftPresenter(GiftUI personRechargeRecordUI) {
        mUI = personRechargeRecordUI;
    }

    /**
     * 获取记录数据
     *
     * @param isFirstLoad isFirstLoad
     * @param refresh     refresh
     */
    public void getGiftReceiveDataFromNet(final boolean refresh, final boolean isFirstLoad) {

        Disposable disposable = Observable.create(new ObservableOnSubscribe<GiftListBeanInfo>() {

            @Override
            public void subscribe(ObservableEmitter<GiftListBeanInfo> e) throws Exception {

                GiftListBeanInfo bean = null;
                try {
                    bean = HwRequestLib.getInstance().getGiftListInfo(page + "");
                } catch (Exception e1) {
                    e1.printStackTrace();
                }

                e.onNext(bean);
                e.onComplete();
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribeWith(new DisposableObserver<GiftListBeanInfo>() {

            @Override
            public void onNext(GiftListBeanInfo result) {

                handleOnNext(result, refresh);
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {

            }

            @Override
            protected void onStart() {
                if (isFirstLoad) {
                    mUI.showLoadProgress();
                }
            }
        });

        composite.addAndDisposeOldByKey("getGiftReceiveDataFromNet", disposable);
    }

    /**
     * 重置index
     *
     * @param isAdd isAdd
     */
    public void resetIndex(boolean isAdd) {
        if (isAdd) {
            page += 1;
            return;
        }
        page = 1;
    }


    private void handleOnNext(GiftListBeanInfo result, boolean refresh) {
        mUI.dismissLoadProgress();
        if (result != null && result.isSuccess()) {
            handleSuccess(result, refresh);
        } else {
            if (refresh) {
                mUI.showNoNetView();
            } else {
                mUI.setHasMore(true);
                mUI.showMessage(R.string.request_data_failed);
            }
        }
        mUI.stopLoadMore();
    }

    private void handleSuccess(GiftListBeanInfo result, boolean refresh) {
        if (result.isExistData()) {
            mUI.setHasMore(true);
            mUI.setRecordList(result.giftListBeans, refresh);
        } else {
            mUI.setHasMore(false);
            if (refresh) {
                mUI.showEmptyView();
            } else {
                //                  mUI.showMessage(R.string.no_more_data);
                mUI.showAllTips();
            }
        }
    }


    /**
     * 获取兑换礼品结果
     *
     * @param pCode code
     */
    public void getGiftExchangeFromNet(final String pCode) {
        Disposable disposable = Observable.create(new ObservableOnSubscribe<BeanGetBookInfo>() {
            @Override
            public void subscribe(ObservableEmitter<BeanGetBookInfo> e) {
                BeanGetBookInfo giftExchangeBean = null;
                try {
                    giftExchangeBean = HwRequestLib.getInstance().getGiftExchange(pCode);
                } catch (Exception e1) {
                    ALog.printStackTrace(e1);
                }
                e.onNext(giftExchangeBean);
                e.onComplete();
            }
        }).subscribeOn(Schedulers.io()).
                observeOn(AndroidSchedulers.mainThread()).
                subscribeWith(new DisposableObserver<BeanGetBookInfo>() {

                    @Override
                    public void onNext(BeanGetBookInfo result) {
                        if (result != null && result.isSuccess()) {
                            if (null != result.books && !result.books.isEmpty()) {
                                InsertBookInfoDataUtil.insertNativeBook(mUI.getContext(), result, "");
                            }
                            //兑换的结果 1成功 2 失败
                            String successfulStatus = "1";
                            String failureStatus = "2";
                            if (successfulStatus.equals(result.status)) {
                                showSuccessfulDialog(result.message);
                            } else if (failureStatus.equals(result.status)) {
                                mUI.setResultMsg(result.message);
                            }
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

        composite.addAndDisposeOldByKey("getGiftExchangeFromNet" + pCode, disposable);
    }

    /**
     * destroy
     */
    public void destroy() {
        composite.disposeAll();
    }

    /**
     * 显示成功Dialog
     */
    private void showSuccessfulDialog(String pResult) {
        CustomHintDialog customHintDialog = new CustomHintDialog(mUI.getContext(), CustomDialogBusiness.STYLE_DIALOG_CANCEL);
        customHintDialog.setDesc(pResult);
        customHintDialog.setCheckListener(new CustomDialogBusiness.OnCheckListener() {
            @Override
            public void clickConfirm(Object object) {
            }

            @Override
            public void clickCancel() {
            }
        });
        customHintDialog.setCancelTxt(mUI.getContext().getString(R.string.sure));
        customHintDialog.show();
    }
}
