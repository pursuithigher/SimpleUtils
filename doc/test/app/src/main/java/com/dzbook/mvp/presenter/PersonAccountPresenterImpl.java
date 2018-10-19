package com.dzbook.mvp.presenter;

import android.content.Intent;

import com.dzbook.activity.account.ConsumeBookSumActivity;
import com.dzbook.activity.account.RechargeRecordActivity;
import com.dzbook.activity.account.VouchersListActivity;
import com.dzbook.fragment.main.MainPersonalFragment;
import com.dzbook.log.LogConstants;
import com.dzbook.mvp.UI.PersonAccountUI;
import com.dzbook.pay.Listener;
import com.dzbook.recharge.order.RechargeParamBean;
import com.dzbook.utils.NetworkUtils;
import com.dzbook.utils.ThirdPartyLog;
import com.dzbook.utils.hw.LoginUtils;
import com.dzpay.recharge.bean.RechargeAction;
import com.iss.app.BaseActivity;

import java.util.HashMap;

/**
 * PersonAccountPresenterImpl
 *
 * @author dongdianzhou on 2017/4/6.
 */

public class PersonAccountPresenterImpl implements PersonAccountPresenter {

    private PersonAccountUI mUI;

    /**
     * 构造
     *
     * @param personAccountUI personAccountUI
     */
    public PersonAccountPresenterImpl(PersonAccountUI personAccountUI) {
        mUI = personAccountUI;
    }


    @Override
    public void dzRechargePay() {
        ThirdPartyLog.onEventValue(mUI.getContext(), ThirdPartyLog.USERALLCLICK, ThirdPartyLog.USER_RECHARGE, 1);

        Listener listener = new Listener() {
            @Override
            public void onSuccess(int ordinal, HashMap<String, String> parm) {
                if (parm != null) {
                    mUI.referencePriceView();
                }
            }

            @Override
            public void onFail(HashMap<String, String> parm) {
            }
        };

        RechargeParamBean paramBean = new RechargeParamBean(mUI.getActivity(), listener, RechargeAction.RECHARGE.ordinal(), "个人中心", null, null, MainPersonalFragment.TAG, LogConstants.RECHARGE_SOURCE_FROM_VALUE_2);
        RechargeListPresenter.launch(paramBean);
    }

    @Override
    public void intentToRechargeRecord() {
        if (!NetworkUtils.getInstance().checkNet()) {
            if (mUI.getActivity() instanceof BaseActivity) {
                ((BaseActivity) mUI.getActivity()).showNotNetDialog();
            }
        } else {
            LoginUtils.getInstance().forceLoginCheck(mUI.getActivity(), new LoginUtils.LoginCheckListener() {
                @Override
                public void loginComplete() {
                    ThirdPartyLog.onEventValue(mUI.getActivity(), ThirdPartyLog.USERALLCLICK, ThirdPartyLog.USER_RECORD_RCH, 1);
                    Intent i = new Intent(mUI.getActivity(), RechargeRecordActivity.class);
                    mUI.getActivity().startActivity(i);
                    BaseActivity.showActivity(mUI.getActivity());
                }
            });
        }
    }

    @Override
    public void intentToVouchersList() {
        if (!NetworkUtils.getInstance().checkNet()) {
            if (mUI.getActivity() instanceof BaseActivity) {
                ((BaseActivity) mUI.getActivity()).showNotNetDialog();
            }
        } else {
            LoginUtils.getInstance().forceLoginCheck(mUI.getActivity(), new LoginUtils.LoginCheckListener() {
                @Override
                public void loginComplete() {
                    ThirdPartyLog.onEventValue(mUI.getActivity(), ThirdPartyLog.USERALLCLICK, ThirdPartyLog.USER_LIST_VOUCHERS, 1);
                    Intent i = new Intent(mUI.getActivity(), VouchersListActivity.class);
                    mUI.getActivity().startActivity(i);
                    BaseActivity.showActivity(mUI.getActivity());
                }
            });
        }

    }

    @Override
    public void intentToConsumeRecordActivity() {
        if (!NetworkUtils.getInstance().checkNet()) {
            if (mUI.getActivity() instanceof BaseActivity) {
                ((BaseActivity) mUI.getActivity()).showNotNetDialog();
            }
        } else {
            LoginUtils.getInstance().forceLoginCheck(mUI.getActivity(), new LoginUtils.LoginCheckListener() {
                @Override
                public void loginComplete() {
                    ConsumeBookSumActivity.launchConsumeBookSum(mUI.getActivity());
                }
            });
        }
    }
}
