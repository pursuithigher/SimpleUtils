package com.dzbook.mvp.presenter;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.TextUtils;

import com.dzbook.AppConst;
import com.dzbook.activity.CenterDetailActivity;
import com.dzbook.activity.GuideActivity;
import com.dzbook.activity.reader.ReaderUtils;
import com.dzbook.bean.MainTabBean;
import com.dzbook.database.bean.BookInfo;
import com.dzbook.database.bean.CatalogInfo;
import com.dzbook.event.EventConstant;
import com.dzbook.lib.net.DzSchedulers;
import com.dzbook.lib.utils.ALog;
import com.dzbook.lib.utils.SDCardUtil;
import com.dzbook.model.ModelAction;
import com.dzbook.mvp.BasePresenter;
import com.dzbook.mvp.UI.MainUI;
import com.dzbook.net.hw.HwRequestLib;
import com.dzbook.push.HwPushApiHelper;
import com.dzbook.push.HwPushHelper;
import com.dzbook.push.HwPushNotificationUtils;
import com.dzbook.push.HwPushReceiver;
import com.dzbook.utils.DBUtils;
import com.dzbook.utils.FileUtils;
import com.dzbook.utils.MainTabInfoUtils;
import com.dzbook.utils.SpUtil;
import com.dzbook.utils.hw.LoginCheckUtils;
import com.dzbook.utils.hw.TmsUtils;
import com.dzbook.view.common.dialog.CustomScrollViewDialog;

import hw.sdk.net.bean.BeanCidUpload;

/**
 * MainPresenter
 *
 * @author dongdianzhou on 2017/11/28.
 */

public class MainPresenter extends BasePresenter {
    private static final String ACTION_HW_REMOVE_ACCOUNT = "com.huawei.hwid.ACTION_REMOVE_ACCOUNT";
    private static final String ACTION_HW_CHANGE_PIC = "com.huawei.hwid.ACTION_HEAD_PIC_CHANGE";
    private static final String ACTION_HW_CHANGE_REFRESH_USER_INFO = "com.huawei.hwid.ACTION_HEAD_PIC_CHANGE";
    private static final String ACTION_HW_CHANGE_REFRESH_USER_NAME = "com.huawei.hwid.ACTION_ACCOUNTNAME_CHANGE";
    /**
     * 退出帐号失败
     */
    private static final String ACTION_HW_CHANGE_QUIT_FAIL = "com.huawei.hwid.ACTION_LOGOUT_FAIL";
    private static final int MAX_COUNT = 3;

    private CustomScrollViewDialog tmsDialog;
    private int retryCount = 0;
    private MainUI mUI;

    private BroadcastReceiver mAccountInfoChangeReceiver = new BroadcastReceiver() {
        @Override
        public final void onReceive(Context context, Intent mIntent) {
            if (mIntent != null) {
                String action = mIntent.getAction();
                //切换帐号
                if (TextUtils.equals(ACTION_HW_REMOVE_ACCOUNT, action)) {
                    ALog.dWz("hw ---> remove account ");
                    LoginCheckUtils.getInstance().loginAuthFailClearLoginStatus();
                    TmsUtils.getInstance().reset();
                    //刷新头像
                } else if (TextUtils.equals(ACTION_HW_CHANGE_PIC, action)) {
                    ALog.dWz("hw ---> change pic ");
                }
            }
        }
    };

    /**
     * 工作
     *
     * @param mainUI mainUI
     */
    public MainPresenter(MainUI mainUI) {
        mUI = mainUI;
    }

    /**
     * 页面启动前的校验
     *
     * @param intent intent
     */
    public void resumeSatartConfig(Intent intent) {
        //校验sd卡是否还有空余空间
        String form = "";
        if (null != intent.getExtras()) {
            form = (String) intent.getExtras().get("from");
        }
        if (SDCardUtil.getInstance().isSDCardAvailable() && !(GuideActivity.class.getName().equals(form))) {
            DzSchedulers.execute(new Runnable() {
                @Override
                public void run() {
                    FileUtils.getDefault().createAssignSzieFile(AppConst.getApp(), FileUtils.APP_ASSIGN_FILE_SZIE_PATH);
                }
            });

        }
    }

    /**
     * 处理到阅读器/书籍详情等页面的跳转
     *
     * @param bundle bundle
     */
    public void operSkipOtherPage(Bundle bundle) {
        if (null != bundle) {
            int goWhere = bundle.getInt("goWhere", -1);
            String goBookId = bundle.getString("goBookId");
            String openFrom = bundle.getString("openFrom");
            String bookid = bundle.getString("bookid");
            String goChapterId = bundle.getString("goChapterId");
            String url = bundle.getString("goUrl");
            long goChapterPos = bundle.getLong("goChapterPos", -1);
            int goFrom = bundle.getInt("goFrom", AppConst.FROM_DEFAULT);
            if ((AppConst.GO_READER == goWhere || AppConst.GO_SMART == goWhere || AppConst.GO_BOOKDETAIL == goWhere) && !TextUtils.isEmpty(goBookId)) {
                ModelAction.goReaderSmart((Activity) mUI.getContext(), goWhere, -1, goBookId, goChapterId, goChapterPos, true, goFrom);
            } else if ("shortcut".equals(openFrom)) {
                if (!TextUtils.isEmpty(bookid)) {
                    BookInfo bookInfo = DBUtils.findByBookId(mUI.getContext(), bookid);
                    if (null != bookInfo) {
                        CatalogInfo catalog = DBUtils.getCatalog(mUI.getContext(), bookInfo.bookid, bookInfo.currentCatalogId);
                        ReaderUtils.intoReader(mUI.getContext(), catalog, catalog.currentPos);
                    }
                } else {
                    ALog.dLk("cmt---bookid为空");
                }
            } else if (AppConst.GO_WEB == goWhere && !TextUtils.isEmpty(url)) {
                CenterDetailActivity.show(mUI.getContext(), url);
            }
        }
    }

    /**
     * 注册推送
     */
    public void registerPushMsgListener() {
        //push的hwApiClient需要提前绑定个Activity
        HwPushApiHelper.getInstance().bindActivity(mUI.getActivity());

        HwPushHelper.getInstance().getPushToken(mUI.getContext().getApplicationContext());

        //设置是否接收消息
        HwPushHelper.getInstance().setReceiveNotifyMsg(SpUtil.getinstance(AppConst.getApp()).getIsReceiveMsg());

        //注册通知渠道
        HwPushNotificationUtils.getInstance().createNotifyChannel(mUI.getContext());
    }


    private void upLoadCid(final String cid) {
        if (TextUtils.isEmpty(cid)) {
            return;
        }
        DzSchedulers.child(new Runnable() {
            @Override
            public void run() {
                boolean isSuccess = true;
                retryCount++;
                if (retryCount > MAX_COUNT) {
                    return;
                }
                try {
                    BeanCidUpload beanCidUpload = HwRequestLib.getInstance().upLoadCid(cid);
                    if (beanCidUpload != null) {
                        if (beanCidUpload.isTokenExpire()) {
                            //如果token失效 不再重试
                            retryCount = 4;
                            isSuccess = true;
                        } else if (!beanCidUpload.isSuccess()) {
                            isSuccess = false;
                        }
                    }
                } catch (Exception e) {
                    ALog.printExceptionWz(e);
                    isSuccess = false;
                }

                if (!isSuccess) {
                    upLoadCid(cid);
                }
            }
        });
    }

    /**
     * 操作书城
     *
     * @param mBundle mBundle
     */
    public void onEventSetBookStore(Bundle mBundle) {
        if (mBundle != null) {
            try {
                String tabType = mBundle.getString(EventConstant.EVENT_BOOKSTORE_TYPE);
                MainTabBean sti = MainTabInfoUtils.getInstance().getInfoByType(tabType);
                if (sti != null) {
                    try {
                        if (sti.index < MainTabInfoUtils.getInstance().getList().size()) {
                            mUI.setBookStoreTableHost(sti.index);
                            return;
                        }
                    } catch (Throwable ignore) {
                    }
                }

                mUI.setBookStoreTableHost(0);
            } catch (Throwable ignore) {
            }
        }
    }

    /**
     * 推送
     *
     * @param mBundle mBundle
     */
    public void onEventPush(Bundle mBundle) {
        if (mBundle != null) {
            String action = mBundle.getString(HwPushReceiver.ACTION_STR);
            if (!TextUtils.isEmpty(action)) {
                switch (action) {
                    case HwPushReceiver.ACTION_MSG:
                        String msg = mBundle.getString(HwPushReceiver.ACTION_MSG);
                        ALog.dWz("msg = " + msg);
                        break;
                    case HwPushReceiver.ACTION_TOKEN:
                        String token = mBundle.getString(HwPushReceiver.ACTION_TOKEN);
                        ALog.dWz("token = " + token);
                        upLoadCid(token);
                        break;
                    case HwPushReceiver.ACTION_EVENT:
                        String event = mBundle.getString(HwPushReceiver.ACTION_EVENT);
                        ALog.dWz("event = " + event);
                        break;
                    case HwPushReceiver.ACTION_STATE:
                        boolean state = mBundle.getBoolean(HwPushReceiver.ACTION_STATE);
                        ALog.dWz("state = " + state);
                        break;
                    default:
                        break;
                }
            }
        }
    }

    /**
     * show Tms Dialog
     *
     * @param thisUid     thisUid
     * @param accessToken accessToken
     */
    public void showTmsDialog(final String thisUid, final String accessToken) {
        if (tmsDialog == null) {
            tmsDialog = new CustomScrollViewDialog(mUI.getActivity(), mUI.getActivity().getWindow());
        }
        if (tmsDialog.isShowing()) {
            return;
        }
        ALog.dWz("showTmsDialog -->");
        SpUtil.getinstance(mUI.getContext()).setSignAgreement(false);
        tmsDialog.setOnClickCallback(new CustomScrollViewDialog.OnDialogClickCallBack() {
            @Override
            public void onClickConfirm() {
                DzSchedulers.child(new Runnable() {
                    @Override
                    public void run() {
                        TmsUtils.getInstance().signAgreement(mUI.getContext(), accessToken, thisUid);
                    }
                });
            }
        });
        tmsDialog.show();
    }

    /**
     * 注册华为帐号变化广播 包括头像 帐号退出等
     */
    public void registerHwAccountChangeReceiver() {
        IntentFilter localIntentFilter = new IntentFilter();
        localIntentFilter.addAction(ACTION_HW_REMOVE_ACCOUNT);
        localIntentFilter.addAction(ACTION_HW_CHANGE_PIC);
        localIntentFilter.addAction(ACTION_HW_CHANGE_REFRESH_USER_INFO);
        localIntentFilter.addAction(ACTION_HW_CHANGE_REFRESH_USER_NAME);
        localIntentFilter.addAction(ACTION_HW_CHANGE_QUIT_FAIL);
        mUI.getContext().getApplicationContext().registerReceiver(mAccountInfoChangeReceiver, localIntentFilter);
    }


    /**
     * destroy
     */
    public void destroy() {
        try {
            mUI.getContext().getApplicationContext().unregisterReceiver(mAccountInfoChangeReceiver);
        } catch (Throwable e) {
            ALog.printExceptionWz(e);
        }
    }
}
