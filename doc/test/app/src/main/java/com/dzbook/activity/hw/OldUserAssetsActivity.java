package com.dzbook.activity.hw;

import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;

import com.dzbook.event.EventBusUtils;
import com.dzbook.event.EventConstant;
import com.dzbook.event.EventMessage;
import com.dzbook.lib.utils.ALog;
import com.dzbook.lib.utils.MemoryLeakUtils;
import com.dzbook.log.DzLog;
import com.dzbook.net.hw.HwRequestLib;
import com.dzbook.r.c.SettingManager;
import com.dzbook.utils.ImageUtils;
import com.huawei.hwCloudJs.JsClientApi;
import com.huawei.hwCloudJs.core.webview.MainWebviewActivity;
import java.util.ArrayList;
import java.util.List;


/**
 * 简单的加载网页的Activity
 * <p>
 * 默认 不带打点
 *
 * @author winzows
 */
public class OldUserAssetsActivity extends MainWebviewActivity {

    private View mEyeCareView = null;
    private String tag = "OldUserAssetsActivity";

    @Override
    protected void onCreate(Bundle bundle) {
        com.huawei.hwCloudJs.d.e.setPackageName("com.ishugui");
        super.onCreate(bundle);
        EventBusUtils.register(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBusUtils.unregister(this);
        MemoryLeakUtils.fixInputMethodManagerLeak(this);
    }

    /**
     * onEventMainThread
     * @param event event
     */
    public void onEventMainThread(EventMessage event) {
        int requestCode = event.getRequestCode();
        switch (requestCode) {
            case EventConstant.LOGIN_CHECK_RSET_PERSON_LOGIN_STATUS:
            case EventConstant.CODE_JS_CALL_FINISH_PAGE:
                if (!isFinishing()) {
                    finish();
                }
                break;
            case EventConstant.REQUESTCODE_EYE_MODE_CHANGE:
                resetEyeMode();
                break;
            default:
                break;
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        resetEyeMode();
        DzLog.getInstance().onPageStart(this, true);
    }

    @Override
    protected void onResume() {
        super.onResume();
        DzLog.getInstance().logPv(tag,null,"");
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
    protected void onNewIntent(Intent intent) {
        com.huawei.hwCloudJs.d.e.setPackageName("com.ishugui");
        super.onNewIntent(intent);
    }

    /**
     * show
     * @param mActivity mActivity
     * @param h5Url h5Url
     */
    public static void show(Context mActivity, String h5Url) {
        Intent intent = new Intent(mActivity, OldUserAssetsActivity.class);
        intent.putExtra("url", h5Url);
        checkUrlWhiteList();
        mActivity.startActivity(intent);
    }


    @Override
    protected void onPause() {
        super.onPause();
        HwRequestLib.flog(" <--" + tag);
        DzLog.getInstance().onPageEnd(this, false);
    }

    /**
     * 加入白名单
     */
    private static void checkUrlWhiteList() {
        try {
            List<String> whiteList = new ArrayList<>();
            whiteList.add("h5.kuaikandushu.cn");
            whiteList.add("hwbookstore.shuqiread.com");
            whiteList.add("m.zhangyue.com");
            JsClientApi.registerUrlWhiteList(whiteList);
        } catch (Exception e) {
            ALog.printExceptionWz(e);
        }
    }

}
