package com.dzbook.utils.hw;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.dzbook.AppConst;
import com.huawei.feedback.FeedbackApi;

import hw.sdk.utils.HwDeviceInfoUtils;

/**
 * 反馈
 *
 * @author winzows  2018/4/12
 */

public class FeedBackHelper {

    private static final String APP_ID = "appId";
    private static final String QUESTION_TYPE = "questionType";
    private static final String PACKAGE_NAME = "packageName";
    private static final String PACKAGE_VERSION = "packageVersion";
    private static final String TAG = "FeedBackHelper ";
    private static final String HW_APPID = "37";
    private static final String FEEDBACK = "feedback";
    private static SharedPreferences.Editor edit;

    private static volatile FeedBackHelper instance;

    /**
     * 获取FeedBackHelper实例
     *
     * @return 实例
     */
    @SuppressLint("CommitPrefEdits")
    public static FeedBackHelper getInstance() {
        if (instance == null) {
            synchronized (FeedBackHelper.class) {
                if (instance == null) {
                    instance = new FeedBackHelper();
                    SharedPreferences sp = AppConst.getApp().getSharedPreferences(FEEDBACK, Context.MODE_MULTI_PROCESS);
                    edit = sp.edit();
                }
            }
        }
        return instance;
    }

    /**
     * 去反馈
     *
     * @param context context
     */
    public void gotoFeedBack(Context context) {
        edit.putString("multi_packagename", "com.ishugui").commit();
        Bundle bundle = new Bundle();
        bundle.putString(APP_ID, HW_APPID);
        bundle.putString(QUESTION_TYPE, AppConst.getAppName(context));
        bundle.putString(PACKAGE_NAME, context.getPackageName());
        bundle.putString(PACKAGE_VERSION, HwDeviceInfoUtils.getAppVersion(context));
        FeedbackApi.gotoFeedback(context, bundle);
    }
}
