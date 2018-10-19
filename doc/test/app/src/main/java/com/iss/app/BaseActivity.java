package com.iss.app;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.StringRes;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;

import com.dzbook.activity.GuideActivity;
import com.dzbook.activity.Main2Activity;
import com.dzbook.activity.SplashActivity;
import com.dzbook.dialog.common.DialogLoading;
import com.dzbook.event.EventBusUtils;
import com.dzbook.event.EventConstant;
import com.dzbook.event.EventMessage;
import com.dzbook.lib.net.DzSchedulers;
import com.dzbook.lib.rx.CompositeDisposable;
import com.dzbook.lib.utils.ALog;
import com.dzbook.lib.utils.MemoryLeakUtils;
import com.dzbook.log.DzLog;
import com.dzbook.model.UserGrow;
import com.dzbook.mvp.BaseUI;
import com.dzbook.mvp.DialogConstants;
import com.dzbook.net.hw.HwRequestLib;
import com.dzbook.r.c.SettingManager;
import com.dzbook.utils.DeviceUtils;
import com.dzbook.utils.ImageUtils;
import com.dzbook.utils.ImmersiveUtils;
import com.dzbook.utils.ListUtils;
import com.dzbook.utils.NetworkUtils;
import com.dzbook.utils.ThirdPartyLog;
import com.dzbook.utils.WhiteListWorker;
import com.dzbook.utils.hw.LoginCheckUtils;
import com.dzbook.utils.hw.LoginUtils;
import com.dzbook.utils.hw.PermissionUtils;
import com.dzbook.view.common.dialog.CustomHintDialog;
import com.dzbook.view.common.dialog.base.CustomDialogBusiness;
import com.ishugui.R;
import com.iss.view.common.ToastAlone;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import hw.sdk.HwSdkAppConstant;


/**
 * Base Activity
 *
 * @author lizz
 */
public abstract class BaseActivity extends FragmentActivity implements BaseUI {
    private static final long MAX_LEAVE_TIME = 15 * 1000;
    /**
     * 保存在栈里的所有Activity
     */
    private static HashMap<String, LinkedList<Activity>> mActivitiesMap = new HashMap<>();

    /**
     * CompositeDisposable
     */
    public CompositeDisposable composite = new CompositeDisposable();
    protected PermissionUtils checkPermissionUtils;
    /**
     * 自定义加载进度条
     */
    private DialogLoading dialogLoading;
    private View mEyeCareView = null;

    private long leaveTime;
    private boolean isLeave;


    private LoginCheckUtils loginCheckUtils = null;


    /**
     * 设置图书来源
     */
    public void setBookSourceFrom() {
        WhiteListWorker.setBookSourceFrom(this.getName(), null, this);
    }


    //日志打点 详情见wiki pn pi ps
    public String getPI() {
        return null;
    }

    public String getPS() {
        return null;
    }

    /**
     * class Simple Name
     *
     * @return name
     */
    public final String getName() {
        String tagName = getTagName();
        if (!TextUtils.isEmpty(tagName)) {
            return tagName;
        }
        return getClass().getSimpleName();
    }

    public int getMaxSize() {
        return 1;
    }

    /**
     * 当Activity执行onCreate时调用 - 保存启动的Activity
     */
    public void activityStackAdd() {
        if (getMaxSize() >= 1) {
            String tag = getName();
            LinkedList<Activity> list = mActivitiesMap.get(tag);
            if (null == list) {
                list = new LinkedList<>();
                mActivitiesMap.put(tag, list);
            }

            if (list.size() >= getMaxSize()) {
                ALog.dZz("ActivityStackManager onCreate " + tag + " sizeBeyond:" + list.size());

                Activity mOldActivity = list.getFirst();
                if (!mOldActivity.equals(this)) {
                    mOldActivity.finish();
                    list.remove(this);
                }

                ALog.dZz("ActivityStackManager onCreate " + tag + " remove after size:" + list.size());
            }
            list.add(this);
            ALog.dZz("ActivityManager onCreate mActivitiesMap last size:" + list.size());
        }
    }

    /**
     * 当Activity执行onDestroy时调用 - 移除销毁的Activity
     */
    public void activityStackRemove() {
        if (getMaxSize() >= 1) {
            String tag = getName();
            LinkedList<Activity> list = mActivitiesMap.get(tag);
            if (null != list) {
                ALog.dZz("ActivityStackManager onDestroy " + tag + " size：" + list.size());
                list.remove(this);
                ALog.dZz("ActivityStackManager onDestroy " + tag + " remove after size：" + list.size());
            }
        }
    }

    /**
     * Main2Activity onResume 的时候，把所有的记录都清楚。避免内存泄漏
     *
     * @param except 除指定Activity，其余的清空
     */
    public void activityStackClear(String except) {
        if (!TextUtils.isEmpty(except)) {
            for (Map.Entry<String, LinkedList<Activity>> entry : mActivitiesMap.entrySet()) {
                String key = entry.getKey();
                if (!except.equals(key)) {
                    LinkedList<Activity> list = entry.getValue();
                    if (!ListUtils.isEmpty(list)) {
                        list.clear();
                    }
                }
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityStackAdd();

        loginStatusSyn();

        if (needImmersionBar()) {
            ImmersiveUtils.init(getActivity(), getStatusColor(), getNavigationBarColor());
        }
    }

    /**
     * 底部导航栏 沉浸色值
     *
     * @return color
     */
    public int getNavigationBarColor() {
        return R.color.color_100_ffffff;
    }


    /**
     * 二级页面都是灰色 一级页面都是白色
     * 状态栏 沉浸色值
     *
     * @return color
     */
    public int getStatusColor() {
        return R.color.color_100_f2f2f2;
    }

    protected boolean isNeedRegisterEventBus() {
        return true;
    }

    @Override
    protected void onStart() {
        super.onStart();
        resetEyeMode();
        if (isNeedRegisterEventBus()) {
            EventBusUtils.register(this);
        }
    }

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
        initView();
        initData();
        setListener();
    }

    @Override
    public void setContentView(View view) {
        super.setContentView(view);
        initView();
        initData();
        setListener();
    }

    @Override
    public void setContentView(View view, LayoutParams params) {
        super.setContentView(view, params);
        initView();
        initData();
        setListener();
    }


    protected boolean isCustomPv() {
        return false;
    }

    /**
     * 是否自动同步appToken
     *
     * @return result
     */
    protected boolean isAutoSysAppToken() {
        return false;
    }

    @Override
    protected void onDestroy() {
        if (dialogLoading != null && dialogLoading.isShowing()) {
            dialogLoading.dismiss();
        }

        activityStackRemove();
        super.onDestroy();
        EventBusUtils.unregister(this);

        if (loginCheckUtils != null) {
            loginCheckUtils.disHuaWeiConnect();
        }

        MemoryLeakUtils.fixInputMethodManagerLeak(this);

        if (composite != null) {
            composite.disposeAll();
        }

        dialogLoading = null;
    }

    @Override
    protected void onPause() {
        super.onPause();
        HwRequestLib.flog(" <--" + this.getName());
        DzLog.getInstance().onPageEnd(this, isCustomPv());
        if(isNeedThirdLog()) {
            ThirdPartyLog.onPauseActivity(this);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        setBookSourceFrom();
        HwRequestLib.flog("==> " + this.getName());
        DzLog.getInstance().onPageStart(this, isCustomPv());
        if(isNeedThirdLog()) {
            ThirdPartyLog.onResumeActivity(this);
        }
        ALog.dZz("onResume：" + this.getName());

        if (isLeave && (System.currentTimeMillis() > (leaveTime + MAX_LEAVE_TIME))) {
            loginCheckUtils = LoginCheckUtils.getInstance();
            loginCheckUtils.checkHwLogin(getActivity(), false);
        }

        isLeave = false;

        if (needCheckPermission()) {
            checkPermission();
        }
    }

    /**
     * 动态检查权限
     */
    public void checkPermission() {
        if (checkPermissionUtils == null) {
            checkPermissionUtils = new PermissionUtils();
        }
        boolean isGrant = checkPermissionUtils.checkPermissions(PermissionUtils.loadingPnList());
        if (!isGrant && !(this instanceof Main2Activity)) {
            Main2Activity.launch(getContext(), 1);
            finish();
        }
    }

    /**
     * 是否需要自定义动态权限申请
     *
     * @return boolean
     */
    public boolean needCheckPermission() {
        return true;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (isNoFragmentCache()) {
            if (outState.containsKey("android:support:fragments")) {
                outState.remove("android:support:fragments");
            }
        }
    }

    protected boolean isNoFragmentCache() {
        return false;
    }

    protected boolean isNeedThirdLog(){
        return true;
    }

    @Override
    protected void onStop() {
        super.onStop();

        isLeave = true;
        leaveTime = System.currentTimeMillis();

        ALog.dZz("onStop：" + this.getName());
    }

    /**
     * init view
     */
    protected abstract void initView();

    /**
     * init data
     */
    protected abstract void initData();

    /**
     * set listener
     */
    protected abstract void setListener();

    /**
     * intent activity
     *
     * @param context content
     */
    public static void showActivity(Context context) {
        if (skipAnim(context)) {
            ((Activity) context).overridePendingTransition(-1, -1);
            return;
        }
        if (context instanceof Activity) {
            ((Activity) context).overridePendingTransition(R.anim.ac_in_from_right, R.anim.ac_out_keep);
        }
    }

    /**
     * finish activity
     *
     * @param context content
     */
    public static void finishActivity(Context context) {
        if (skipAnim(context)) {
            ((Activity) context).overridePendingTransition(-1, -1);
            return;
        }
        if (context instanceof Activity) {
            ((Activity) context).overridePendingTransition(R.anim.ac_out_keep, R.anim.ac_out_from_right);
        }
    }

    private static boolean skipAnim(Context context) {
        return (context instanceof SplashActivity) || (context instanceof GuideActivity);
    }

    @Override
    public void finish() {
        super.finish();
        finishActivity(this);
    }

    /**
     * 无动画finish
     */
    public void finishNoAnimation() {
        super.finish();
        overridePendingTransition(-1, -1);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finishActivity(this);
    }

    /**
     * event bus 回调
     *
     * @param event event
     */
    public void onEventMainThread(EventMessage event) {
        if (EventConstant.REQUESTCODE_EYE_MODE_CHANGE == event.getRequestCode()) {
            resetEyeMode();
        }
    }

    /**
     * 设置护眼模式
     */
    protected void resetEyeMode() {
        try {
            boolean openEyeMode = SettingManager.getInstance(this).getReaderEyeMode();
            if (openEyeMode) {
                if (mEyeCareView == null) {
                    mEyeCareView = new View(this);
                    mEyeCareView.setBackgroundColor(ImageUtils.getBlueFilterColor());
                    getWindowManager().addView(mEyeCareView, getEyeCareViewParams());
                }
            } else {
                if (mEyeCareView != null) {
                    getWindowManager().removeViewImmediate(mEyeCareView);
                    mEyeCareView = null;
                }
            }

        } catch (Exception e) {
            ALog.printStackTrace(e);
        }
    }

    private WindowManager.LayoutParams getEyeCareViewParams() {
        int flags = WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        try {
            if (Build.VERSION.SDK_INT >= 19) {
                flags = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
            }
        } catch (Exception e) {
            ALog.printStackTrace(e);
        }

        DisplayMetrics dm = getResources().getDisplayMetrics();
        int size = Math.max(dm.widthPixels, dm.heightPixels) + 300;
        WindowManager.LayoutParams params = new WindowManager.LayoutParams(size, size, WindowManager.LayoutParams.TYPE_APPLICATION, flags, PixelFormat.TRANSLUCENT);

        params.gravity = Gravity.TOP;
        return params;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        LoginUtils.getInstance().doHuaweiOnActivityResult(this, requestCode, resultCode, data);
    }

    public Activity getActivity() {
        return this;
    }


    @Override
    public void dissMissDialog() {
        runOnUiThread(new Runnable() {

            @Override
            public void run() {
                if (dialogLoading != null && !isFinishing() && dialogLoading.isShowing()) {
                    dialogLoading.dismiss();
                }

            }
        });
    }

    /**
     * 显示Light Dialog
     */
    public void showDialogLight() {
        if (isFinishing()) {
            return;
        }
        runOnUiThread(new Runnable() {

            @Override
            public void run() {
                initDialog();

                if (dialogLoading != null && !isFinishing() && !dialogLoading.isShowing()) {
                    dialogLoading.showLight();
                }
            }
        });
    }

    @Override
    public boolean isNetworkConnected() {
        return NetworkUtils.getInstance().checkNet();
    }

    @Override
    public void showMessage(String message) {
        if (!TextUtils.isEmpty(message)) {
            ToastAlone.showShort(message);
        }
    }

    @Override
    public void showMessage(@StringRes int resId) {
        ToastAlone.showShort(resId);
    }

    /**
     * reset Visibility
     */
    public void resetUiVisibility() {
        if (getWindow() == null || getWindow().getDecorView() == null) {
            return;
        }
        try {
            View decorView = getWindow().getDecorView();
            int uiVisibility = decorView.getSystemUiVisibility();
            ALog.eLwx("reset uiVisibility:" + uiVisibility);
            decorView.setSystemUiVisibility(uiVisibility);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public void showDialogByType(@DialogConstants.DialogType final int loadingType) {
        if (isFinishing()) {
            return;
        }
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                initDialog();

                switch (loadingType) {
                    case DialogConstants.TYPE_INIT_PAGE:
                        break;
                    case DialogConstants.TYPE_GET_DATA:
                        dialogLoading.setShowMsg(getString(R.string.loadContent));
                        if (dialogLoading != null && !isFinishing() && !dialogLoading.isShowing()) {
                            dialogLoading.show();
                        }
                        break;
                    case DialogConstants.TYPE_GET_DATA_TRANSPARENT:
                        if (dialogLoading != null && !isFinishing() && !dialogLoading.isShowing()) {
                            dialogLoading.showTransparent();
                        }
                        break;
                    case DialogConstants.TYPE_NO_DIALOG:
                        break;
                    default:
                        break;
                }
            }
        });
    }

    @Override
    public void showDialogByType(@DialogConstants.DialogType final int loadingType, final CharSequence text) {
        if (isFinishing()) {
            return;
        }
        runOnUiThread(new Runnable() {

            @Override
            public void run() {
                initDialog();

                switch (loadingType) {
                    case DialogConstants.TYPE_INIT_PAGE:
                        break;
                    case DialogConstants.TYPE_GET_DATA:
                        if (dialogLoading != null) {
                            dialogLoading.setShowMsg(text);
                            if (!isFinishing() && !dialogLoading.isShowing()) {
                                dialogLoading.show();
                            }
                        }
                        break;
                    case DialogConstants.TYPE_NO_DIALOG:
                        break;
                    default:
                        break;
                }
            }
        });
    }

    @Override
    public Context getContext() {
        return this;
    }

    /**
     * 是否需要沉浸式状态栏
     *
     * @return boolean
     */
    protected boolean needImmersionBar() {
        int cutEMUIVersionStringLength = 11;
        int versionStandard = 1;
        String stringTitle = "EmotionUI_";
        int versionCutStart = 10;
        int versionCutEnd = 11;
        if (DeviceUtils.getEMUI().contains(stringTitle) && DeviceUtils.getEMUI().length() >= cutEMUIVersionStringLength) {
            try {
                if (Integer.parseInt(DeviceUtils.getEMUI().substring(versionCutStart, versionCutEnd)) < versionStandard) {
                    return false;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return true;
    }

    /**
     * 隐藏键盘
     */
    public void hideSoftKeyboard() {
        try {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE | WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        } catch (Exception e) {
            ALog.printStackTrace(e);
        }
    }

    /************************callback*************************************/

    /**
     * 取消分享
     *
     * @param type     type
     * @param isFinish 取消后，是否finish
     */
    public void shareCancel(int type, boolean isFinish) {
        //分享取消
        ToastAlone.showShort(getResources().getString(R.string.share_cancel));
        if (isFinish) {
            finish();
        }
        overridePendingTransition(R.anim.anim_activityin, R.anim.anim_activity_out);
    }

    /**
     * 分享成功
     *
     * @param type     type
     * @param isFinish 是否finish
     */
    public void shareSuccess(int type, boolean isFinish) {
        DzSchedulers.child(new Runnable() {
            @Override
            public void run() {
                try {
                    if (NetworkUtils.getInstance().checkNet()) {
                        HwRequestLib.getInstance().finishTask(UserGrow.USER_GROW_SHARE, 0);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        //分享成功
        ToastAlone.showShort(getResources().getString(R.string.share_success));
        if (isFinish) {
            finish();
        }
        overridePendingTransition(R.anim.anim_activityin, R.anim.anim_activity_out);
    }

    /**
     * 分享失败
     *
     * @param type     type
     * @param isFinish isFinish
     */
    public void shareFail(int type, boolean isFinish) {
        //分享失败
        ToastAlone.showShort(getResources().getString(R.string.share_fail));
        if (isFinish) {
            finish();
        }
        overridePendingTransition(R.anim.anim_activityin, R.anim.anim_activity_out);
    }

    private void initDialog() {
        if (dialogLoading == null) {
            dialogLoading = new DialogLoading(getContext());
            dialogLoading.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    resetUiVisibility();
                }
            });
        }
    }

    /**
     * 设置网络Dialog
     */
    public void showNotNetDialog() {
        CustomHintDialog customHintDialog = new CustomHintDialog(this, 1);
        customHintDialog.setDesc(getResources().getString(R.string.str_check_network_connection));
        customHintDialog.setCheckListener(new CustomDialogBusiness.OnCheckListener() {
            @Override
            public void clickConfirm(Object object) {
                // 打开系统设置界面
                NetworkUtils.getInstance().setNetSetting(BaseActivity.this);
            }

            @Override
            public void clickCancel() {

            }
        });
        customHintDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                resetUiVisibility();
            }
        });
        customHintDialog.setConfirmTxt(getResources().getString(R.string.str_set_up_the_network));
        customHintDialog.show();
    }

    private void loginStatusSyn() {
        if (NetworkUtils.getInstance().checkNet()) {
            if (isAutoSysAppToken()) {
                HwSdkAppConstant.setStartAppSynTokenStatus(true);
                ALog.dZz(BaseActivity.this.getName() + "  onCreate方法中isAutoSysAppToken");
                LoginCheckUtils.getInstance().checkHwLogin(getActivity(), true);
            } else {
                //是否需要同步token 1.启动应用时候MainActivity没同步成功 2:服务器端接口返回token失效，需要再次同步
                if (!HwSdkAppConstant.isStartAppSynTokenStatus() || HwSdkAppConstant.isIsAppTokenInvalidNeedRetrySys()) {
                    HwSdkAppConstant.setStartAppSynTokenStatus(true);
                    HwSdkAppConstant.setIsAppTokenInvalidNeedRetrySys(false);
                    ALog.dZz(BaseActivity.this.getName() + "  onCreate方法中执行同步，需要同步的条件（1.启动应用时候MainActivity没同步成功 2:服务器端接口返回token失效，需要再次同步）");
                    LoginCheckUtils.getInstance().checkHwLogin(getActivity(), true);
                }
            }
        } else if (TextUtils.equals(Main2Activity.TAG, getTagName())) {
            //保证没有网络进入住页面的时候，打开新页面需要同步
            HwSdkAppConstant.setStartAppSynTokenStatus(false);
        }
    }

    //分屏模式生命周期
    /*@Override
    public void onMultiWindowModeChanged(boolean isInMultiWindowMode) {
        super.onMultiWindowModeChanged(isInMultiWindowMode);
        ImmersiveUtils.init(this, getStatusColor(), getNavigationBarColor());
    }*/
}
