package com.dzpay.recharge.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.dzbook.lib.utils.ALog;
import com.dzbook.lib.utils.MemoryLeakUtils;
import com.dzpay.recharge.logic.Observer;
import com.dzpay.recharge.logic.core.ModelRecharge;
import com.dzpay.recharge.utils.PayLog;

import java.util.HashMap;

/**
 * 处理充值结果
 *
 * @author lizhongzhong 15/8/24
 */
public class RechargeCoreActivity extends Activity {


    private static Observer observer;


    /**
     * 开始支付时间
     */
    private long startTime = System.currentTimeMillis();

    private int maxWaitTime = 30000;

    private ModelRecharge modelRecharge;

    private boolean isActivityStop = false;

    private int onResumeNum = 0;

    public static void setObserver(Observer observer) {
        RechargeCoreActivity.observer = observer;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //设置状态栏透明

        this.isActivityStop = false;
        Intent intent = getIntent();
        if (intent != null) {

            HashMap<String, String> params = (HashMap<String, String>) intent.getSerializableExtra("params");
            modelRecharge = new ModelRecharge(this, observer, params, new ModelRecharge.ContextListener() {
                @Override
                public void onContextFinish() {
                    finish();
                }
            });
            modelRecharge.initData();
        }
        setStateBarTransparentColor();
    }

    private void setStateBarTransparentColor() {
        try {
            Window window = this.getWindow();
            if (window != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY | View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
                /*
                 * 状态栏的适配
                 */
                window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                window.setStatusBarColor(Color.parseColor("#00000000"));
            }
        } catch (Throwable e) {
            ALog.printExceptionWz(e);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        PayLog.d("RechargeCoreActivity:onPause()");
        this.isActivityStop = true;
    }

    @Override
    protected void onStop() {
        super.onStop();
        PayLog.d("RechargeCoreActivity:onStop()");
        this.isActivityStop = true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        PayLog.d("RechargeCoreActivity:onResume()");
        boolean flag = modelRecharge.getSdkPay() != null && (!isActivityStop || modelRecharge.getSdkPay().isNeedOrderQuery);
        if (flag) {
            ++this.onResumeNum;

            PayLog.d("RechargeCoreActivity:onResumeNum:" + onResumeNum + ",isActivityStop:" + isActivityStop + ",isNeedOrderQuery:" + modelRecharge.getSdkPay().isNeedOrderQuery);

            int count = 2;
            if (this.onResumeNum % count == 0) {

                PayLog.d("RechargeCoreActivity:开始回调");
                maxWaitTime = 15000;
            }
        }
    }

    /**
     * 充值结束是，回收sdk必要的内容。
     */
    @Override
    protected void onDestroy() {
        if (null != modelRecharge) {
            modelRecharge.orderDestroy();
            modelRecharge = null;
        }
        setObserver(null);
        super.onDestroy();

        MemoryLeakUtils.fixInputMethodManagerLeak(this);
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK) {
            long endTime = System.currentTimeMillis();

            long divTime = endTime - startTime;
            PayLog.d("RechargeCoreActivity:KeyEvent.KEYCODE_BACK divTime:" + divTime);

            if (divTime > maxWaitTime && modelRecharge.getSdkPay() != null) {
                PayLog.e("RechargeCoreActivity:用户点击返回，查询用户支付订单结果");

                //查询订单是否成功 回调方法 微信支付的需要覆盖此方法 实现查询
                modelRecharge.getSdkPay().orderQueryStart();

                modelRecharge.getSdkPay().isNeedOrderQuery = false;
            }

            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (null != modelRecharge && modelRecharge.getSdkPay() != null) {
            modelRecharge.getSdkPay().onActivityResult(requestCode, resultCode, data);
        }

    }

}

