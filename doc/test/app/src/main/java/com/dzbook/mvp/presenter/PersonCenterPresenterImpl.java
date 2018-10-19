package com.dzbook.mvp.presenter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.dzbook.AppConst;
import com.dzbook.activity.CenterDetailActivity;
import com.dzbook.activity.GiftCenterActivity;
import com.dzbook.activity.comment.BookCommentPersonCenterActivity;
import com.dzbook.activity.person.CloudBookShelfActivity;
import com.dzbook.activity.person.PersonAccountActivity;
import com.dzbook.activity.person.PersonSetActivity;
import com.dzbook.activity.vip.MyVipActivity;
import com.dzbook.fragment.main.MainPersonalFragment;
import com.dzbook.lib.net.DzSchedulers;
import com.dzbook.lib.utils.ALog;
import com.dzbook.log.LogConstants;
import com.dzbook.mvp.UI.PersonCenterUI;
import com.dzbook.net.hw.HwRequestLib;
import com.dzbook.net.hw.RequestCall;
import com.dzbook.pay.Listener;
import com.dzbook.recharge.order.RechargeParamBean;
import com.dzbook.utils.NetworkUtils;
import com.dzbook.utils.SpUtil;
import com.dzbook.utils.ThirdPartyLog;
import com.dzbook.utils.UserInfoUtils;
import com.dzbook.utils.hw.FeedBackHelper;
import com.dzbook.utils.hw.LoginUtils;
import com.dzpay.recharge.bean.RechargeAction;
import com.ishugui.R;
import com.iss.app.BaseActivity;

import java.util.HashMap;

import hw.sdk.net.bean.register.UserInfoBeanInfo;
import hw.sdk.net.bean.task.FinishTask;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

import static com.dzbook.model.UserGrow.USER_GROW_READ;

/**
 * PersonCenterPresenterImpl
 *
 * @author dongdianzhou on 2017/4/5.
 */
public class PersonCenterPresenterImpl implements PersonCenterPresenter {

    /**
     * REQUEST_CODE
     */
    public static final int INTENT_TO_HW_ACCOUNT_REQUEST_CODE = 20001;

    private PersonCenterUI mUI;

    /**
     * 构造
     *
     * @param personCenterUI personCenterUI
     */
    public PersonCenterPresenterImpl(PersonCenterUI personCenterUI) {
        mUI = personCenterUI;
    }

    @Override
    public void login() {
        if (!NetworkUtils.getInstance().checkNet()) {
            if (mUI.getActivity() instanceof BaseActivity) {
                ((BaseActivity) mUI.getActivity()).showNotNetDialog();
            }
        } else {
            LoginUtils.getInstance().forceLoginCheck(mUI.getActivity(), new LoginUtils.LoginCheckListener() {
                @Override
                public void loginComplete() {
                    mUI.refreshUserInfo();
                }
            });
        }
    }

    @Override
    public void intentToAccountActivity() {
        if (!NetworkUtils.getInstance().checkNet()) {
            if (mUI.getActivity() instanceof BaseActivity) {
                ((BaseActivity) mUI.getActivity()).showNotNetDialog();
            }
        } else {
            LoginUtils.getInstance().forceLoginCheck(mUI.getActivity(), new LoginUtils.LoginCheckListener() {
                @Override
                public void loginComplete() {
                    PersonAccountActivity.launch((Activity) mUI.getContext());
                }
            });
        }
    }

    @Override
    public void intentToCloudSelfActivity() {
        if (!NetworkUtils.getInstance().checkNet()) {
            if (mUI.getActivity() instanceof BaseActivity) {
                ((BaseActivity) mUI.getActivity()).showNotNetDialog();
            }
        } else {
            LoginUtils.getInstance().forceLoginCheck(mUI.getActivity(), new LoginUtils.LoginCheckListener() {
                @Override
                public void loginComplete() {
                    Intent intent = new Intent(mUI.getContext(), CloudBookShelfActivity.class);
                    mUI.getContext().startActivity(intent);
                    BaseActivity.showActivity(mUI.getContext());
                    ThirdPartyLog.onEvent(mUI.getContext(), ThirdPartyLog.USER_CLOUD_SU);
                }
            });
        }
    }

    @Override
    public void intentToSystemSetActivity() {
        ThirdPartyLog.onEventValue(mUI.getContext(), ThirdPartyLog.USERALLCLICK, ThirdPartyLog.USER_SETTING, 1);
        Intent intent = new Intent(mUI.getContext(), PersonSetActivity.class);
        mUI.getContext().startActivity(intent);
        BaseActivity.showActivity(mUI.getContext());
    }

    @Override
    public void intentToFeedBackActivity() {
        FeedBackHelper.getInstance().gotoFeedBack(mUI.getContext());
    }

    @Override
    public void intentToMyReadTimeActivity() {
        if (!NetworkUtils.getInstance().checkNet()) {
            if (mUI.getActivity() instanceof BaseActivity) {
                ((BaseActivity) mUI.getActivity()).showNotNetDialog();
            }
        } else {
            LoginUtils.getInstance().forceLoginCheck(mUI.getActivity(), new LoginUtils.LoginCheckListener() {
                @Override
                public void loginComplete() {
                    CenterDetailActivity.show(mUI.getActivity(), RequestCall.urlMyReadTime(), mUI.getContext().getResources().getString(R.string.str_read_length));
                }
            });
        }
    }

    @Override
    public void intentToBookCommentPerson() {
        if (!NetworkUtils.getInstance().checkNet()) {
            if (mUI.getActivity() instanceof BaseActivity) {
                ((BaseActivity) mUI.getActivity()).showNotNetDialog();
            }
        } else {
            LoginUtils.getInstance().forceLoginCheck(mUI.getActivity(), new LoginUtils.LoginCheckListener() {
                @Override
                public void loginComplete() {
                    BookCommentPersonCenterActivity.launch(mUI.getActivity());
                }
            });
        }
    }

    @Override
    public void intentToGiftActivity() {
        if (!NetworkUtils.getInstance().checkNet()) {
            if (mUI.getActivity() instanceof BaseActivity) {
                ((BaseActivity) mUI.getActivity()).showNotNetDialog();
            }
        } else {
            LoginUtils.getInstance().forceLoginCheck(mUI.getActivity(), new LoginUtils.LoginCheckListener() {
                @Override
                public void loginComplete() {
                    Intent intent = new Intent();
                    intent.setClass(mUI.getActivity(), GiftCenterActivity.class);
                    mUI.getActivity().startActivity(intent);
                    BaseActivity.showActivity(mUI.getActivity());
                }
            });
        }
    }

    @Override
    public void dzRechargePay() {
        if (!NetworkUtils.getInstance().checkNet()) {
            if (mUI.getActivity() instanceof BaseActivity) {
                ((BaseActivity) mUI.getActivity()).showNotNetDialog();
            }
        } else {
            LoginUtils.getInstance().forceLoginCheck(mUI.getActivity(), new LoginUtils.LoginCheckListener() {
                @Override
                public void loginComplete() {
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
            });
        }
    }

    @Override
    public void intentToMyVipActivity() {
        //        CenterDetailActivity.show(mUI.getActivity(), RequestCall.urlMyVip(), mUI.getContext().getResources().getString(R.string.str_myvip));
        //        CenterDetailActivity.show(mUI.getActivity(), RequestCall.urlMyVip(), mUI.getContext().getResources().getString(R.string.str_myvip));
        MyVipActivity.launch(mUI.getActivity());
    }

    @Override
    public void getReaderTimeAndUserInfoFromNet() {
        final SpUtil spUtil = SpUtil.getinstance(AppConst.getApp());
        if (LoginUtils.getInstance().checkLoginStatus(AppConst.getApp())) {
            final long localReaderDurationTime = spUtil.getLocalReaderDurationTime();
            ALog.cmtDebug("需要同步服务器阅读时间:" + localReaderDurationTime);
            DzSchedulers.child(new Runnable() {
                @Override
                public void run() {
                    try {
                        if (NetworkUtils.getInstance().checkNet()) {
                            FinishTask task = HwRequestLib.getInstance().finishTask(USER_GROW_READ, (int) localReaderDurationTime);
                            if (null != task && task.isFinish) {
                                //上传成功
                                spUtil.setLocalReaderDurationTime(0);
                                spUtil.setShowReaderTime(task.totalReadDuration);
                            }
                        }
                    } catch (Exception e) {
                        ALog.printStackTrace(e);
                    } finally {
                        getUserInfoFromNet();
                    }
                }
            });
        }
    }

    @Override
    public void getUserInfoFromNet() {
        Observable.create(new ObservableOnSubscribe<UserInfoBeanInfo>() {

            @Override
            public void subscribe(ObservableEmitter<UserInfoBeanInfo> e) {
                UserInfoBeanInfo userInfoBean = null;
                try {
                    userInfoBean = HwRequestLib.getInstance().getUserInfo();
                } catch (Exception e1) {
                    ALog.printStackTrace(e1);
                }
                e.onNext(userInfoBean);
                e.onComplete();
            }
        }).subscribeOn(Schedulers.io()).
                observeOn(AndroidSchedulers.mainThread()).
                subscribeWith(new DisposableObserver<UserInfoBeanInfo>() {

                    @Override
                    public void onNext(UserInfoBeanInfo result) {

                        if (result != null) {
                            if (result.isSuccess()) {
                                UserInfoUtils.setUserInfo(mUI.getActivity(), result.getUserInfoBean());
                                mUI.refreshUserInfo();
                            } else {
                                if (result.isTokenExpireOrNeedLogin()) {
                                    mUI.appTokenInvalid();
                                }
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

    }

    @Override
    public void intentToHwAccountCenter() {
        Intent intent = new Intent();
        intent.setAction("com.huawei.hwid.ACTION_MAIN_SETTINGS");
        intent.setPackage("com.huawei.hwid");
        intent.putExtra("showLogout", true);
        try {
            mUI.getActivity().startActivityForResult(intent, INTENT_TO_HW_ACCOUNT_REQUEST_CODE);

        } catch (Exception e) {
            ALog.printStackTrace(e);
        }
    }

    @Override
    public void showNotNetDialog() {
        if (mUI.getActivity() instanceof BaseActivity) {
            ((BaseActivity) mUI.getActivity()).showNotNetDialog();
        }
    }

    @Override
    public void hideSoft(View view) {
        try {
            Activity activity = mUI.getActivity();
            if (activity != null) {
                InputMethodManager systemService = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
                if (systemService != null) {
                    systemService.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }
            }
        } catch (Throwable e) {
            ALog.printExceptionWz(e);
        }
    }

}
