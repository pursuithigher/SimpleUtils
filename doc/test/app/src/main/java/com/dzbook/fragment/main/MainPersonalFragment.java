package com.dzbook.fragment.main;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dzbook.AppConst;
import com.dzbook.activity.AboutActivity;
import com.dzbook.activity.ActivityCenterActivity;
import com.dzbook.activity.Main2Activity;
import com.dzbook.activity.hw.OldUserAssetsActivity;
import com.dzbook.dialog.common.DialogLoading;
import com.dzbook.event.EventBusUtils;
import com.dzbook.event.EventConstant;
import com.dzbook.event.EventMessage;
import com.dzbook.lib.utils.ALog;
import com.dzbook.listener.CheckUpdateListener;
import com.dzbook.log.DzLog;
import com.dzbook.log.LogConstants;
import com.dzbook.mvp.UI.PersonCenterUI;
import com.dzbook.mvp.presenter.PersonCenterPresenter;
import com.dzbook.mvp.presenter.PersonCenterPresenterImpl;
import com.dzbook.utils.NetworkUtils;
import com.dzbook.utils.SpUtil;
import com.dzbook.utils.ThirdPartyLog;
import com.dzbook.utils.hw.CheckUpdateUtils;
import com.dzbook.utils.hw.LoginCheckUtils;
import com.dzbook.utils.hw.LoginUtils;
import com.dzbook.view.common.dialog.CustomHintDialog;
import com.dzbook.view.common.dialog.base.CustomDialogBusiness;
import com.dzbook.view.person.PersonCommonIconTextView;
import com.dzbook.view.person.PersonCommonLoadingView;
import com.dzbook.view.person.PersonCommonView;
import com.dzbook.view.person.PersonTopView;
import com.dzbook.web.ActionEngine;
import com.ishugui.R;

/**
 * MainPersonalFragment
 *
 * @author wxliao on 17/3/29.
 */

public class MainPersonalFragment extends BaseFragment implements View.OnClickListener, PersonCenterUI {
    /**
     * tag
     */
    public static final String TAG = "MainPersonalFragment";
    private static final int MAX_CLICK_INTERVAL_TIME = 1500;

    PersonCommonIconTextView mIconTextViewSignIn;
    PersonCommonIconTextView mIconTextViewTopUp;
    PersonCommonIconTextView mIconTextViewGift;
    PersonCommonIconTextView mIconTextViewAct;
    private PersonTopView mPersontopview;
    private PersonCommonView mCommonViewAccount, mCommonviewVip;
    private PersonCommonView mCommonviewCloudSelf;

    private PersonCommonView mCommonviewReadTime;
    private PersonCommonView mCommonviewBookReview;
    private PersonCommonView mCommonviewAbout;
    private PersonCommonLoadingView mCommonviewCheckUpDate;
    private PersonCommonView mCommonviewSysSet;
    private PersonCommonView mCommonViewFeedback;
    private PersonCommonView commonviewAssertZy, commonviewAssertSj;


    private PersonCenterPresenter mPresenter;

    private DialogLoading dialogLoading;

    private long lastClickTime = 0;

    private LoginCheckUtils loginCheckUtils = null;

    private View rlRoot;

    private boolean isClickFeedBack = false;

    private String zyH5Url, sqH5Url;

    /**
     * 是否需要刷新个人中心数据
     */
    private boolean isNeedRefreshPersonCenterData = true;

    private CustomHintDialog userOldAssertDialog;

    @Override
    public String getTagName() {
        return "MainPersonalFragment";
    }

    @Override
    protected View inflate(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_main_personal, container, false);
    }

    @Override
    public void onResume() {
        super.onResume();
        long localReaderDurationTime = SpUtil.getinstance(AppConst.getApp()).getLocalReaderDurationTime();
        if (localReaderDurationTime > 0) {
            //需要先同步
            mPresenter.getReaderTimeAndUserInfoFromNet();

        } else {
            if (LoginUtils.getInstance().checkLoginStatus(getContext()) && isNeedRefreshPersonCenterData) {
                mPresenter.getUserInfoFromNet();
            }
        }
        refreshUserInfo();

        if (isClickFeedBack) {
            mPresenter.hideSoft(rlRoot);
        }
        isClickFeedBack = false;
        isNeedRefreshPersonCenterData = true;

        boolean isNeedShow = SpUtil.getinstance(mActivity).getBoolean(SpUtil.DZ_IS_OLD_USER_ASSERT_NEED_SHOW);
        if (isNeedShow) {
            showUserOldAssertDialog();
        }
    }

    /**
     * 设置自有对我的view的控制
     * 由于随时可能转自有，所以放到onresume中
     */
    public void setUserViewStatus() {
        SpUtil sp = SpUtil.getinstance(getContext());
        if (LoginUtils.getInstance().checkLoginStatus(getContext())) {
            if (sp.getInt(SpUtil.DZ_IS_VIP) == 1) {
                mCommonviewVip.setTextViewContentShowStatus(View.VISIBLE);
                String showVipEndTime = String.format(getString(R.string.the_end_of_time), sp.getString(SpUtil.DZ_VIP_EXPIRED_TIME));
                mCommonviewVip.setTextViewContent(showVipEndTime);
            } else {
                mCommonviewVip.setTextViewContentShowStatus(View.GONE);
            }
            long readTime = sp.getShowReaderTime();
            String showReadTime = String.format(getString(R.string.str_read_time), String.valueOf(readTime / 60000));
            mCommonviewReadTime.setTextViewContentShowStatus(View.VISIBLE);
            mCommonviewReadTime.setTextViewContent(showReadTime);
        } else {
            mCommonviewVip.setTextViewContentShowStatus(View.GONE);
            mCommonviewReadTime.setTextViewContentShowStatus(View.GONE);
        }

        zyH5Url = sp.getString(SpUtil.DZ_OLD_USER_ASSERT_ZHANG_YUE_H5_URL);
        sqH5Url = sp.getString(SpUtil.DZ_OLD_USER_ASSERT_SHU_QI_H5_URL);

        if (!TextUtils.isEmpty(zyH5Url)) {
            commonviewAssertZy.setVisibility(View.VISIBLE);
        } else {
            commonviewAssertZy.setVisibility(View.GONE);
        }
        if (!TextUtils.isEmpty(sqH5Url)) {
            commonviewAssertSj.setVisibility(View.VISIBLE);
        } else {
            commonviewAssertSj.setVisibility(View.GONE);
        }
    }

    @Override
    protected void initView(View uiView) {

        mCommonviewCloudSelf = uiView.findViewById(R.id.commonview_cloudself);
        mCommonviewReadTime = uiView.findViewById(R.id.commonview_read_time);
        mCommonviewBookReview = uiView.findViewById(R.id.commonview_book_review);
        mCommonviewAbout = uiView.findViewById(R.id.commonview_about);
        mCommonviewCheckUpDate = uiView.findViewById(R.id.commonview_check_update);
        mCommonviewSysSet = uiView.findViewById(R.id.commonview_systemset);
        mCommonViewFeedback = uiView.findViewById(R.id.commonview_feedback);
        commonviewAssertZy = uiView.findViewById(R.id.commonview_assert_zy);
        commonviewAssertSj = uiView.findViewById(R.id.commonview_assert_sj);
        mPersontopview = uiView.findViewById(R.id.persontopview);
        mCommonViewAccount = uiView.findViewById(R.id.commonview_account);
        mCommonviewVip = uiView.findViewById(R.id.commonview_myvip);

        mIconTextViewSignIn = uiView.findViewById(R.id.icon_text_view_sign_in);
        mIconTextViewTopUp = uiView.findViewById(R.id.icon_text_view_top_up);
        mIconTextViewGift = uiView.findViewById(R.id.icon_text_view_gift);
        mIconTextViewAct = uiView.findViewById(R.id.icon_text_view_activity);
        rlRoot = uiView.findViewById(R.id.rlRoot);

        dialogLoading = new DialogLoading(getContext());
    }

    @Override
    protected void initData(View uiView) {
        EventBusUtils.registerSticky(this);
        if (mPresenter == null) {
            mPresenter = new PersonCenterPresenterImpl(this);
            initChildrenViewPresenter();
        }
        SpUtil spUtil = SpUtil.getinstance(getActivity());
        zyH5Url = spUtil.getString(SpUtil.DZ_OLD_USER_ASSERT_ZHANG_YUE_H5_URL);
        sqH5Url = spUtil.getString(SpUtil.DZ_OLD_USER_ASSERT_SHU_QI_H5_URL);

        if (!TextUtils.isEmpty(zyH5Url)) {
            commonviewAssertZy.setVisibility(View.VISIBLE);
        }

        if (!TextUtils.isEmpty(sqH5Url)) {
            commonviewAssertSj.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBusUtils.unRegisterSticky(this);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (loginCheckUtils != null) {
            loginCheckUtils.resetAgainObtainListener();
            loginCheckUtils.disHuaWeiConnect();
        }
        if (userOldAssertDialog != null) {
            if (userOldAssertDialog.isShow()) {
                userOldAssertDialog.dismiss();
            }
            userOldAssertDialog = null;
        }
    }

    private void initChildrenViewPresenter() {
        if (mPersontopview != null) {
            mPersontopview.setPresenter(mPresenter);
        }
        mCommonViewAccount.setPresenter(mPresenter);
        mCommonviewVip.setPresenter(mPresenter);
        mCommonviewCloudSelf.setPresenter(mPresenter);
        mCommonviewReadTime.setPresenter(mPresenter);
        mCommonviewBookReview.setPresenter(mPresenter);
        mCommonviewAbout.setPresenter(mPresenter);
        mCommonviewCheckUpDate.setPresenter(mPresenter);
        mCommonviewSysSet.setPresenter(mPresenter);
        mCommonViewFeedback.setPresenter(mPresenter);
    }

    @Override
    protected void setListener(View uiView) {
        mCommonViewAccount.setOnClickListener(this);
        mCommonviewVip.setOnClickListener(this);
        mCommonviewCloudSelf.setOnClickListener(this);
        mCommonviewReadTime.setOnClickListener(this);
        mCommonviewBookReview.setOnClickListener(this);
        mCommonviewAbout.setOnClickListener(this);
        mCommonviewCheckUpDate.setOnClickListener(this);
        mCommonviewSysSet.setOnClickListener(this);
        mCommonViewFeedback.setOnClickListener(this);

        mIconTextViewSignIn.setOnClickListener(this);
        mIconTextViewTopUp.setOnClickListener(this);
        mIconTextViewGift.setOnClickListener(this);
        mIconTextViewAct.setOnClickListener(this);
        commonviewAssertZy.setOnClickListener(this);
        commonviewAssertSj.setOnClickListener(this);

    }


    @Override
    public void onClick(View v) {
        long thisClickTime = System.currentTimeMillis();
        if (Math.abs(thisClickTime - lastClickTime) < MAX_CLICK_INTERVAL_TIME) {
            return;
        }
        lastClickTime = thisClickTime;

        if (!NetworkUtils.getInstance().checkNet()) {
            switch (v.getId()) {
                //系统设置
                case R.id.commonview_systemset:
                    isNeedRefreshPersonCenterData = false;
                    ThirdPartyLog.onEventValueOldClick(getContext(), ThirdPartyLog.PERSON_CENTER_MENU_UMENG_ID, ThirdPartyLog.PERSON_CENTER_SYSTEMSET_VALUE, 1);
                    DzLog.getInstance().logClick(LogConstants.MODULE_WD, LogConstants.ZONE_WD_XTSZ, "", null, null);
                    mPresenter.intentToSystemSetActivity();
                    break;
                //帮助反馈
                case R.id.commonview_feedback:
                    isNeedRefreshPersonCenterData = false;
                    isClickFeedBack = true;
                    ThirdPartyLog.onEventValueOldClick(getActivity(), ThirdPartyLog.PERSON_CENTER_SYSTEMSET_MENU_UMENG_ID, ThirdPartyLog.PERSON_CENTER_SYSTEMSET_FEEDBACK_VALUE, 1);
                    mPresenter.intentToFeedBackActivity();
                    break;
                //关于我们
                case R.id.commonview_about:
                    isNeedRefreshPersonCenterData = false;
                    ThirdPartyLog.onEventValueOldClick(getContext(), ThirdPartyLog.PERSON_CENTER_MENU_UMENG_ID, ThirdPartyLog.PERSON_CENTER_SYSTEMSET_ABOUTUS_VALUE, 1);
                    AboutActivity.launch(getActivity());
                    break;

                default:
                    mPresenter.showNotNetDialog();
                    break;
            }
        } else {
            int id = v.getId();
            if (dealIcons(id)) {
                ALog.dLk("MainPersonalFragment click icons");
            } else if (dealList(id)) {
                ALog.dLk("MainPersonalFragment click list");
            } else {
                ALog.dLk("MainPersonalFragment click what?");
            }
        }
    }

    private boolean dealIcons(int id) {
        switch (id) {
            //签到
            case R.id.icon_text_view_sign_in:
                ActionEngine.getInstance().toSign(mActivity);
                break;
            // 充值
            case R.id.icon_text_view_top_up:
                isNeedRefreshPersonCenterData = false;
                mPresenter.dzRechargePay();
                DzLog.getInstance().logClick(LogConstants.MODULE_WD, LogConstants.ZONE_WD_WDCZ, "", null, null);
                break;
            //礼品
            case R.id.icon_text_view_gift:
                if (LoginUtils.getInstance().checkLoginStatus(getContext())) {
                    mPresenter.intentToGiftActivity();
                } else {
                    mPresenter.login();
                }

                break;
            //活动
            case R.id.icon_text_view_activity:
                ActivityCenterActivity.launch(getActivity());
                break;
            default:
                return false;
        }
        return true;
    }

    private boolean dealList(int id) {
        switch (id) {
            //我的账户
            case R.id.commonview_account:
                isNeedRefreshPersonCenterData = false;
                ThirdPartyLog.onEventValueOldClick(getContext(), ThirdPartyLog.PERSON_CENTER_MENU_UMENG_ID, ThirdPartyLog.PERSON_CENTER_MYACCOUNT_VALUE, 1);
                DzLog.getInstance().logClick(LogConstants.MODULE_WD, LogConstants.ZONE_WD_WDZH, "", null, null);
                mPresenter.intentToAccountActivity();
                break;
            case R.id.commonview_assert_zy:
                if (!TextUtils.isEmpty(zyH5Url)) {
                    OldUserAssetsActivity.show(getActivity(), zyH5Url);
                    DzLog.getInstance().logClick(LogConstants.MODULE_WD, LogConstants.ZONE_WD_ZYZC, "", null, null);
                }
                break;
            case R.id.commonview_assert_sj:
                if (!TextUtils.isEmpty(sqH5Url)) {
                    OldUserAssetsActivity.show(getActivity(), sqH5Url);
                    DzLog.getInstance().logClick(LogConstants.MODULE_WD, LogConstants.ZONE_WD_SQZC, "", null, null);
                }
                break;
            //我的包月vip页面
            case R.id.commonview_myvip:
                DzLog.getInstance().logClick(LogConstants.MODULE_WD, LogConstants.ZONE_WD_WDVIP, "", null, null);
                mPresenter.intentToMyVipActivity();
                break;
            //我的阅读时长
            case R.id.commonview_read_time:
                mPresenter.intentToMyReadTimeActivity();
                break;
            //我的云书架
            case R.id.commonview_cloudself:
                isNeedRefreshPersonCenterData = false;
                ThirdPartyLog.onEventValueOldClick(getContext(), ThirdPartyLog.PERSON_CENTER_MENU_UMENG_ID, ThirdPartyLog.PERSON_CENTER_CLOUDSELF_VALUE, 1);
                DzLog.getInstance().logClick(LogConstants.MODULE_WD, LogConstants.ZONE_WD_YSJ, "", null, null);
                mPresenter.intentToCloudSelfActivity();
                break;
            //我的书评
            case R.id.commonview_book_review:
                isNeedRefreshPersonCenterData = false;
                mPresenter.intentToBookCommentPerson();
                break;
            //设置
            case R.id.commonview_systemset:
                isNeedRefreshPersonCenterData = false;
                ThirdPartyLog.onEventValueOldClick(getContext(), ThirdPartyLog.PERSON_CENTER_MENU_UMENG_ID, ThirdPartyLog.PERSON_CENTER_SYSTEMSET_VALUE, 1);
                DzLog.getInstance().logClick(LogConstants.MODULE_WD, LogConstants.ZONE_WD_XTSZ, "", null, null);
                mPresenter.intentToSystemSetActivity();
                break;
            //帮助反馈
            case R.id.commonview_feedback:
                isNeedRefreshPersonCenterData = false;
                isClickFeedBack = true;
                ThirdPartyLog.onEventValueOldClick(getActivity(), ThirdPartyLog.PERSON_CENTER_SYSTEMSET_MENU_UMENG_ID, ThirdPartyLog.PERSON_CENTER_SYSTEMSET_FEEDBACK_VALUE, 1);
                mPresenter.intentToFeedBackActivity();
                break;
            //检查更新
            case R.id.commonview_check_update:
                CheckUpdateUtils.checkUpdate(getActivity(), CheckUpdateUtils.ACTIVE_UPDATE, new CheckUpdateListener() {
                    @Override
                    public void start() {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mCommonviewCheckUpDate.showRightLoading();
                            }
                        });
                    }

                    @Override
                    public void end() {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mCommonviewCheckUpDate.showRightIcon();
                            }
                        });
                    }
                });
                break;
            //关于我们
            case R.id.commonview_about:
                isNeedRefreshPersonCenterData = false;
                ThirdPartyLog.onEventValueOldClick(getContext(), ThirdPartyLog.PERSON_CENTER_MENU_UMENG_ID, ThirdPartyLog.PERSON_CENTER_SYSTEMSET_ABOUTUS_VALUE, 1);
                AboutActivity.launch(getActivity());
                break;
            default:
                return false;
        }
        return true;
    }

    @Override
    public void referenceTopView(final boolean isReferenceUserInfo) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mPersontopview != null) {
                    mPersontopview.referenceView(isReferenceUserInfo);
                }
            }
        });
    }

    @Override
    public Fragment getFragment() {
        return getParentFragment();
    }

    @Override
    public void showLoadingDialog(final String message) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (dialogLoading != null && !dialogLoading.isShowing() && getActivity() != null && !getActivity().isFinishing()) {
                    dialogLoading.setShowMsg(message);
                    dialogLoading.show();
                }
            }
        });
    }

    @Override
    public void dismissLoadingDialog() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (dialogLoading != null && dialogLoading.isShowing() && getActivity() != null && !getActivity().isFinishing()) {
                    dialogLoading.dismiss();
                }
            }
        });
    }

    @Override
    public Context getContext() {
        return getActivity();
    }

    @Override
    public void onEventMainThread(EventMessage event) {
        int requestCode = event.getRequestCode();
        switch (requestCode) {
            case EventConstant.LOGIN_CHECK_RSET_PERSON_LOGIN_STATUS:
                if (userOldAssertDialog != null && userOldAssertDialog.isShow()) {
                    userOldAssertDialog.dismiss();
                }
                refreshUserInfo();
                break;
            case EventConstant.LOGIN_SUCCESS_UPDATE_USER_VIEW:
                refreshUserInfo();
                break;
            case EventConstant.CODE_PERSON_CENTER_SHOW_USER_ASSERT_DIALOG:
                if (mActivity instanceof Main2Activity) {
                    Main2Activity ac = (Main2Activity) mActivity;
                    if (ac.isPersonalCenter()) {
                        showUserOldAssertDialog();
                    }
                }
                break;
            default:
                break;
        }

    }

    @Override
    public void referencePriceView() {

    }

    @Override
    public void refreshUserInfo() {
        referenceTopView(true);
        referencePriceView();
        setUserViewStatus();
    }

    /**
     * 显示用户老资产弹窗
     */
    private void showUserOldAssertDialog() {
        final SpUtil spUtil = SpUtil.getinstance(mActivity);
        boolean isShowed = spUtil.isOldUserAssertDialogShowed();
        if (!isShowed) {
            if (userOldAssertDialog == null) {
                userOldAssertDialog = new CustomHintDialog(mActivity, false);
            }
            userOldAssertDialog.setDesc(getResources().getString(R.string.str_user_old_assert_look_des));
            userOldAssertDialog.setCheckListener(new CustomDialogBusiness.OnCheckListener() {
                @Override
                public void clickConfirm(Object object) {
                    String zyH5Url = spUtil.getString(SpUtil.DZ_OLD_USER_ASSERT_ZHANG_YUE_H5_URL);
                    String sqH5Url = spUtil.getString(SpUtil.DZ_OLD_USER_ASSERT_SHU_QI_H5_URL);
                    if (!TextUtils.isEmpty(zyH5Url)) {
                        OldUserAssetsActivity.show(mActivity, zyH5Url);
                    } else if (!TextUtils.isEmpty(sqH5Url)) {
                        OldUserAssetsActivity.show(mActivity, sqH5Url);
                    }
                }

                @Override
                public void clickCancel() {

                }
            });
            userOldAssertDialog.show();
            SpUtil.getinstance(mActivity).setOldUserAssertDialogAlreadyshow();
            SpUtil.getinstance(mActivity).setBoolean(SpUtil.DZ_IS_OLD_USER_ASSERT_NEED_SHOW, false);
        }
    }


    @Override
    public void needLogin() {

    }

    @Override
    public void appTokenInvalid() {
        if (loginCheckUtils == null) {
            loginCheckUtils = LoginCheckUtils.getInstance();
        }
        loginCheckUtils.againObtainAppToken(getActivity(), new LoginUtils.LoginCheckListenerSub() {
            @Override
            public void loginFail() {

            }

            @Override
            public void loginComplete() {
                refreshUserInfo();
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PersonCenterPresenterImpl.INTENT_TO_HW_ACCOUNT_REQUEST_CODE) {
            if (loginCheckUtils == null) {
                loginCheckUtils = LoginCheckUtils.getInstance();
            }
            loginCheckUtils.checkHwLogin(getActivity(), true);
        }
    }
}