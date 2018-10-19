package com.dzbook.mvp.presenter;

import com.dzbook.dialog.common.DialogLoading;
import com.dzbook.lib.utils.ALog;
import com.dzbook.mvp.BasePresenter;
import com.dzbook.mvp.UI.MyVipUI;
import com.dzbook.net.hw.HwRequestLib;
import com.dzbook.utils.NetworkUtils;
import com.ishugui.R;
import com.iss.app.BaseActivity;
import com.iss.view.common.ToastAlone;

import hw.sdk.net.bean.vip.VipBeanInfo;
import hw.sdk.net.bean.vip.VipWellInfo;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

/**
 * 我的VIP
 *
 * @author gavin
 */
public class MyVipPresenter extends BasePresenter {

    private MyVipUI mUI;

    /**
     * 构造
     *
     * @param vipUI ui
     */
    public MyVipPresenter(MyVipUI vipUI) {
        mUI = vipUI;
    }

    /**
     * 获取VIP信息
     */
    public void getVipInfo() {
        if (!NetworkUtils.getInstance().checkNet()) {
            mUI.showNoNetView();
            if (mUI.getContext() instanceof BaseActivity) {
                ((BaseActivity) mUI.getContext()).showNotNetDialog();
            }
            return;
        }
        Disposable disposable = Observable.create(new ObservableOnSubscribe<VipBeanInfo>() {

            @Override
            public void subscribe(ObservableEmitter<VipBeanInfo> e) {
                try {
                    VipBeanInfo info = HwRequestLib.getInstance().getVipInfoBean();
                    e.onNext(info);
                    e.onComplete();
                } catch (Exception ex) {
                    ALog.printStackTrace(ex);
                    e.onError(ex);
                }
            }

        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribeWith(new DisposableObserver<VipBeanInfo>() {

            @Override
            public void onNext(VipBeanInfo value) {
                if (value != null && value.isSuccess()) {
                    mUI.updateUI(value.vipUserInfoBeans, value.vipUserPayBeans, value.vipBookInfoList);
                } else {
                    mUI.showNoNetView();
                }
            }

            @Override
            public void onError(Throwable throwable) {
                mUI.showNoNetView();
            }

            @Override
            public void onComplete() {
                mUI.refreshFinish();
            }
        });

        composite.addAndDisposeOldByKey("getVipInfo", disposable);
    }

    /**
     * 领取vip福利
     */
    public void getVipWell() {
        if (!NetworkUtils.getInstance().checkNet()) {
            mUI.showNoNetView();
            if (mUI.getContext() instanceof BaseActivity) {
                ((BaseActivity) mUI.getContext()).showNotNetDialog();
            }
            return;
        }
        final DialogLoading mCustomDialog = new DialogLoading(mUI.getContext());
        mCustomDialog.setCancelable(false);
        mCustomDialog.setCanceledOnTouchOutside(false);
        mCustomDialog.setShowMsg(mUI.getContext().getString(R.string.dialog_isLoading));
        mCustomDialog.show();

        Observable.create(new ObservableOnSubscribe<VipWellInfo>() {

            @Override
            public void subscribe(ObservableEmitter<VipWellInfo> e) {
                try {
                    VipWellInfo info = HwRequestLib.getInstance().getVipWellInfo();
                    e.onNext(info);
                    e.onComplete();
                } catch (Exception ex) {
                    ALog.printStackTrace(ex);
                    e.onError(ex);
                }
            }

        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribeWith(new DisposableObserver<VipWellInfo>() {
            @Override
            public void onNext(VipWellInfo value) {
                if (value != null && value.isSuccess()) {
                    String getVoucher = String.format(mUI.getContext().getString(R.string.a_voucher_has_been_collected), String.valueOf(value.award));
                    ToastAlone.showShort(getVoucher);
                    getVipInfo();
                } else {
                    ToastAlone.showShort(mUI.getContext().getString(R.string.fail_received));
                }
            }

            @Override
            public void onError(Throwable throwable) {
                ToastAlone.showShort(mUI.getContext().getString(R.string.fail_received));
                if (mCustomDialog.isShowing()) {
                    mCustomDialog.dismiss();
                }
            }

            @Override
            public void onComplete() {
                if (mCustomDialog.isShowing()) {
                    mCustomDialog.dismiss();
                }

            }
        });
    }

    /**
     * destroy
     */
    public void destroy() {
        composite.disposeAll();
    }

    /**
     * setSelectedItem
     * @param position position
     */
    public void setSelectedItem(int position) {
        mUI.setSelectItem(position);
    }

}
