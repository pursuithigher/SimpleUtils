package com.dzbook.activity;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.dzbook.AppConst;
import com.dzbook.event.EventConstant;
import com.dzbook.event.EventMessage;
import com.dzbook.lib.net.DzSchedulers;
import com.dzbook.lib.utils.ALog;
import com.dzbook.log.LogConstants;
import com.dzbook.mvp.UI.SplashUI;
import com.dzbook.mvp.presenter.SplashPresenter;
import com.dzbook.mvp.presenter.SplashPresenterImpl;
import com.dzbook.utils.DeviceUtils;
import com.dzbook.utils.HwLog;
import com.dzbook.utils.SpUtil;
import com.dzbook.utils.TypefaceUtils;
import com.dzbook.utils.hw.PermissionUtils;
import com.dzbook.utils.hw.ShortcutUtils;
import com.huawei.openalliance.ad.beans.parameter.AdSlotParam;
import com.huawei.openalliance.ad.inter.HiAd;
import com.huawei.openalliance.ad.inter.HiAdSplash;
import com.huawei.openalliance.ad.inter.IHiAd;
import com.huawei.openalliance.ad.inter.IHiAdSplash;
import com.huawei.openalliance.ad.inter.listeners.AdListener;
import com.huawei.openalliance.ad.views.SplashAdView;
import com.ishugui.R;
import com.iss.app.BaseActivity;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import hw.sdk.utils.Constants;
import io.reactivex.android.schedulers.AndroidSchedulers;


/**
 * 启动页
 *
 * @author dongdz
 */
public class SplashActivity extends BaseActivity implements SplashUI, PermissionUtils.OnPermissionListener {
    /**
     * tag
     */
    public static final String TAG = "SplashActivity";
    /**
     * 广告展示超时时间：单位毫秒
     */
    private static final int AD_TIMEOUT = 6000;

    /**
     * 广告超时消息标记
     */
    private static final int MSG_AD_TIMEOUT = 1001;

    /**
     * 超时消息回调handler
     */
    private final TimeOutHandler timeoutHandler = new TimeOutHandler(this);

    private SplashPresenter mPresenter;


    private TextView mTextViewAdTime;

    /**
     * 设备类型 4：手机 5：平板
     */
    private int deviceType = 4;
    private int phoneType = 4;
    private int padType = 5;

    private PermissionUtils checkPermission;
    private TextView mTvAppNameSplash;

    @Override
    public String getTagName() {
        return TAG;
    }


    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        mPresenter.handleLaunchMode();
        int launchMode = mPresenter.launchMode();
        Log.d("launchMode-", "onNewIntent --->launchMode" + launchMode);
        if (launchMode == LogConstants.LAUNCH_PUSH || launchMode == LogConstants.LAUNCH_THIRD || launchMode == LogConstants.LAUNCH_GLOBAL_SEARCH) {
            mPresenter.handleIntent();
        }
    }

    @Override
    public int getStatusColor() {
        return R.color.common_backgroud_day_color;
    }

    /**
     * TimeOutHandler
     */
    private static class TimeOutHandler extends Handler {

        private WeakReference<SplashActivity> weakReference;

        TimeOutHandler(SplashActivity activity) {
            super();
            weakReference = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            if (weakReference == null || weakReference.get() == null) {
                return;
            }

            SplashActivity activity = weakReference.get();
            if (null != activity && !activity.isFinishing()) {
                if (activity.mPresenter != null) {
                    activity.mPresenter.jump();
                } else {
                    //异常情况，退出页面
                    activity.finish();
                }
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        checkPermission = new PermissionUtils();
        setContentView(R.layout.activity_splash);
        ShortcutUtils.addShortcut(SplashActivity.this);
        mPresenter = new SplashPresenterImpl(this);
        mPresenter.handleLaunchMode();
        if (!checkLauncher()) {
            return;
        }
    }

    private boolean checkLauncher() {
        Intent intent = getIntent();
        String[] pnList = PermissionUtils.loadingPnList();
        int launchMode = mPresenter.launchMode();
        Log.d("launchMode-", "checkLauncher --->launchMode" + launchMode);
        boolean isGrant = checkPermission.checkPermissions(pnList);
        if (null != intent) {
            boolean category = intent.hasCategory("android.shortcut.conversation");
            if ((intent.getFlags() & Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT) != 0
                    && isGrant
                    && launchMode != LogConstants.LAUNCH_LOCAL_FILE
                    && !category) {
                finish();
                return false;
            }
        }

        return true;
    }

    @Override
    public void loadAd() {
        if (isMultiWin()) {
            ALog.cmtDebug("isMultiWin");
            if (null != mPresenter) {
                mPresenter.jump();
                return;
            }
        }

        IHiAd hiad = HiAd.getInstance(AppConst.getApp());
        hiad.initLog(false, Log.DEBUG);
        hiad.enableUserInfo(true);
        AdSlotParam.Builder slotParamBuilder = new AdSlotParam.Builder();
        List<String> adIds = new ArrayList<String>(1);
        adIds.add(Constants.SPLASH_ADID);
        boolean pad = DeviceUtils.isPad(AppConst.getApp());
        deviceType = pad ? padType : phoneType;
        slotParamBuilder.setAdIds(adIds).setDeviceType(deviceType).setOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT).setTest(
                Constants.SPLASH_TEST_FLAG);

        ALog.cmtDebug("SplashAdView");

        // 设置第一次启动时的slogan展示时间，如果应用必须展示广告界面则调用
        IHiAdSplash hiAdSplash = HiAdSplash.getInstance(AppConst.getApp());
        hiAdSplash.setSloganDefTime(3000);
//
//        if (!hiAdSplash.isAvailable(slotParamBuilder.build())) {
//            if (null != mPresenter) {
//                ALog.cmtDebug("isAvailable");
//                mPresenter.jump();
//                return;
//            }
//        }
        final SplashAdView splashAdView = findViewById(R.id.splash_ad_view);
        splashAdView.setAdSlotParam(slotParamBuilder.build());
        splashAdView.setSloganResId(R.drawable.img_launch_bg);
        splashAdView.setLogo(findViewById(R.id.relative_copyright));
        splashAdView.setAdListener(new AdListener() {
            @Override
            public void onAdFailedToLoad(int errorCode) {
                ALog.cmtDebug("onAdFailedToLoad");
                splashAdView.setAdListener(null);
                if (!isFinishing()) {
                    mPresenter.jump();
                }
            }

            @Override
            public void onAdLoaded() {
                ALog.cmtDebug("onAdLoaded");
//                finish();
            }

            @Override
            public void onAdDismissed() {
                splashAdView.setAdListener(null);
                ALog.cmtDebug("onAdDismissed");
                if (!isFinishing()) {
                    mPresenter.jump();
                }
            }
        });
        splashAdView.loadAd();
        /**
         * 发送延迟消息,用来处理广告超时时能够返回
         */
        timeoutHandler.removeMessages(MSG_AD_TIMEOUT);
        timeoutHandler.sendEmptyMessageDelayed(MSG_AD_TIMEOUT, AD_TIMEOUT);
    }

    @Override
    protected void onStop() {
        // 移除超时消息
        timeoutHandler.removeMessages(MSG_AD_TIMEOUT);
        super.onStop();
        mPresenter.setShow(false);
    }

    @TargetApi(24)
    private boolean isMultiWin() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.N && isInMultiWindowMode();
    }

    @Override
    protected void initData() {
    }

    @Override
    protected void setListener() {

    }

    @Override
    protected void initView() {
        mTextViewAdTime = findViewById(R.id.textview_time);
        mTvAppNameSplash = findViewById(R.id.tv_app_name_splash);
        TypefaceUtils.setHwChineseMediumFonts(mTvAppNameSplash);
    }

    @Override
    protected boolean isCustomPv() {
        return true;
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        mPresenter.setShow(true);
        boolean isShowGuide = SpUtil.getinstance(AppConst.getApp()).getBoolean(SpUtil.HW_IS_SHOW_GUIDE);
        String[] pnList = PermissionUtils.loadingPnList();
        boolean isGrant = checkPermission.checkPermissions(pnList);
        if (isShowGuide && isGrant) {
            mPresenter.jump();
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (newConfig != null && mPresenter != null) {
            mPresenter.onConfigurationChanged(newConfig.screenHeightDp);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mPresenter.setShow(true);
        HwLog.entryApp(String.valueOf(mPresenter.getLaunchFrom()), mPresenter.getLaunchTo());
        if (!SpUtil.getinstance(getContext()).getSignAgreement()) {
            DzSchedulers.mainDelay(new Runnable() {
                @Override
                public void run() {
                    mPresenter.showAgreementDialog();
                }
            }, 300);
        } else {
            splashSecond();
        }
    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        checkPermission.onRequestPermissionsResult(requestCode, permissions, grantResults);
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void referenceTimeView(int adTime) {
        if (mTextViewAdTime != null) {
            mTextViewAdTime.setText(adTime + "s");
            if (mTextViewAdTime.getVisibility() != View.VISIBLE) {
                mTextViewAdTime.setVisibility(View.VISIBLE);
            }
        }
    }

    /**
     * 新用户第一次安装 需要弹出用户隐私协议
     * 校验通过后 才走后面的流程
     */
    @Override
    public void splashSecond() {
        AndroidSchedulers.mainThread().scheduleDirect(new Runnable() {
            @Override
            public void run() {
                checkPermission();
            }
        }, 50, TimeUnit.MILLISECONDS);
    }

    @Override
    public Activity getActivity() {
        return SplashActivity.this;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        timeoutHandler.removeCallbacksAndMessages(null);
        Log.d("launchMode-", "onDestroy --->");
        getWindow().setBackgroundDrawable(null);
        if (mPresenter != null) {
            mPresenter.fixLeaked();
            mPresenter.destroy();
        }

    }

    @Override
    public void checkPermission() {
        String[] pnList = PermissionUtils.loadingPnList();
        boolean isGrant = checkPermission.checkPermissions(pnList);
        if (isGrant) {
            mPresenter.onPermissionGrant();
        } else {
            checkPermission.requestPermissions(this, PermissionUtils.CODE_LOGO_REQUEST, pnList, this);
        }

    }

    @Override
    public void onPermissionGranted() {
        mPresenter.onPermissionGrant();
    }

    @Override
    public void onPermissionDenied() {
        checkPermission.showTipsDialog(this);
    }

    @Override
    public boolean needCheckPermission() {
        return false;
    }


    @Override
    public void onEventMainThread(EventMessage event) {
        super.onEventMainThread(event);
        int requestCode = event.getRequestCode();
        if (requestCode == EventConstant.FINISH_SPLASH && !isFinishing()) {
            mPresenter.onEventFinish();
        }
    }

    @Override
    protected boolean isNeedThirdLog() {
        return false;
    }
}
