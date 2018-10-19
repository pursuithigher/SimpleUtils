package com.dzbook.utils.hw;

import android.app.Activity;
import android.content.Context;

import com.dzbook.AppConst;
import com.dzbook.event.EventBusUtils;
import com.dzbook.event.EventConstant;
import com.dzbook.lib.net.DzSchedulers;
import com.dzbook.lib.utils.ALog;
import com.dzbook.lib.utils.StringUtil;
import com.dzbook.utils.NetworkUtils;
import com.dzbook.utils.SpUtil;
import com.huawei.hms.api.ConnectionResult;
import com.huawei.hms.api.HuaweiApiAvailability;
import com.huawei.hms.api.HuaweiApiClient;
import com.huawei.hms.support.api.client.PendingResult;
import com.huawei.hms.support.api.client.ResultCallback;
import com.huawei.hms.support.api.hwid.HuaweiId;
import com.huawei.hms.support.api.hwid.HuaweiIdSignInOptions;
import com.huawei.hms.support.api.hwid.HuaweiIdStatusCodes;
import com.huawei.hms.support.api.hwid.SignInHuaweiId;
import com.huawei.hms.support.api.hwid.SignInResult;

import java.lang.ref.WeakReference;

import hw.sdk.HwSdkAppConstant;

/**
 * 登录检查工具类
 *
 * @author lizz 2018/4/14.
 */

public class LoginCheckUtils implements HuaweiApiClient.ConnectionCallbacks, HuaweiApiClient.OnConnectionFailedListener {

    private static final String TAG = "LoginCheckUtils";

    /**
     * 调用HuaweiApiAvailability.getInstance().resolveError传入的第三个参数
     * 作用同startactivityforresult方法中的requestcode
     */
    private static final int REQUEST_HMS_RESOLVE_ERROR = 1000;
    /**
     * 华为移动服务Client
     */
    private HuaweiApiClient client;

    private boolean mResolvingError = false;

    private WeakReference<Activity> weakReference = null;

    private boolean isInitToken;

    private LoginUtils.LoginCheckListenerSub mAgainObtainListener;

    private LoginUtils.DzAuthListener mAuthListener;

    private LoginCheckUtils() {

    }

    public static LoginCheckUtils getInstance() {
        return new LoginCheckUtils();
    }

    /**
     * 检查登录
     *
     * @param initToken true:需要重新发起华为登录并且请求注册接口
     *                  false:只作为登录检查
     *                  如果存在token,发现重新获取的openId跟内存中不一样后再次调用101，否则不调用
     * @param activity  activity
     */
    public void checkHwLogin(Activity activity, boolean initToken) {
        if (!NetworkUtils.getInstance().checkNet()) {
            return;
        }
        weakReference = new WeakReference<Activity>(activity);
        this.isInitToken = initToken;
        initAuthListener();
        if (client == null) {
            initHwClient(weakReference.get());
        }
        checkHwLoginInner(weakReference.get());
    }

    /**
     * 检查登录
     *
     * @param activity     activity
     * @param authListener authListener
     */
    public void checkHwLogin(Activity activity, LoginUtils.DzAuthListener authListener) {
        weakReference = new WeakReference<Activity>(activity);
        mAuthListener = authListener;
        if (client == null) {
            initHwClient(weakReference.get());
        }
        checkHwLoginInner(weakReference.get());
    }


    /**
     * appToken过期
     * 重新获取token,不做弹出界面的操作
     *
     * @param activity            activity
     * @param againObtainListener againObtainListener
     */
    public void againObtainAppToken(Activity activity, LoginUtils.LoginCheckListenerSub againObtainListener) {
        if (!NetworkUtils.getInstance().checkNet()) {
            return;
        }
        if (mAgainObtainListener != null) {
            mAgainObtainListener = null;
        }
        mAgainObtainListener = againObtainListener;
        checkHwLogin(activity, true);
    }


    /**
     * 停止连接华为移动服务
     */
    public void disHuaWeiConnect() {
        //建议在onDestroy的时候停止连接华为移动服务
        //业务可以根据自己业务的形态来确定client的连接和断开的时机，但是确保connect和disconnect必须成对出现
        try {
            if (client != null) {
                client.disconnect();
            }
        } catch (Exception e) {
            ALog.printStackTrace(e);
        }
    }

    private void initAuthListener() {
        if (weakReference == null) {
            return;
        }
        Activity activity = weakReference.get();
        if (null == activity) {
            return;
        }
        final Context context = activity.getApplicationContext();

        mAuthListener = new LoginUtils.DzAuthListener() {
            @Override
            public void onComplete(String openId, String hwUid, String accessToken, final String coverWap, final String nickName) {
                if (isInitToken || LoginUtils.isOpenIdNotEqual(openId)) {
                    //1、绑定服务器 获取userId
                    LoginUtils.getInstance().loginAuthCompleteServerBind(context, hwUid, openId, accessToken, coverWap, nickName, true, new Runnable() {
                        @Override
                        public void run() {
                            if (mAgainObtainListener != null) {
                                mAgainObtainListener.loginComplete();
                            }
                        }
                    });
                }

                if (!StringUtil.isEmpty(openId, accessToken)) {
                    LoginUtils.getInstance().hwOpenId = openId;
                    LoginUtils.getInstance().hwAccessToken = accessToken;
                }
            }

            @Override
            public void onError(String errDes) {
                ALog.dZz(TAG + "：onError: " + errDes);
                //同步token失败
                if (isInitToken) {
                    HwSdkAppConstant.setStartAppSynTokenStatus(false);
                }

                if (mAgainObtainListener != null) {
                    mAgainObtainListener.loginFail();
                }
            }
        };
    }


    private void checkHwLoginInner(Activity activity) {
        if (!client.isConnected()) {
            ALog.dZz("登录帐号失败，原因：HuaweiApiClient未连接");
            client.connect(activity);
            return;
        }

        PendingResult<SignInResult> signInResult = HuaweiId.HuaweiIdApi.signInBackend(client);
        signInResult.setResultCallback(new CheckSignInResultCallback());
    }

    @Override
    public void onConnected() {
        //华为移动服务client连接成功，在这边处理业务自己的事件
        ALog.dZz("HuaweiApiClient 连接成功");
        Activity activity = weakReference.get();
        if (activity != null) {
            checkHwLoginInner(activity);
        }
    }

    @Override
    public void onConnectionSuspended(int cause) {
        //HuaweiApiClient断开连接的时候，业务可以处理自己的事件
        ALog.dZz("HuaweiApiClient 连接断开");
        Activity activity = weakReference.get();
        //HuaweiApiClient异常断开连接, if 括号里的条件可以根据需要修改
        if (activity != null && !activity.isFinishing()) {
            client.connect(activity);
        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        ALog.dZz("HuaweiApiClient连接失败，错误码：" + connectionResult.getErrorCode());

        HwSdkAppConstant.setStartAppSynTokenStatus(false);

        if (mResolvingError) {
            return;
        }

        if (HuaweiApiAvailability.getInstance().isUserResolvableError(connectionResult.getErrorCode())) {
            mResolvingError = true;
            final int errorCode = connectionResult.getErrorCode();
            ALog.eZz("onConnectionFailed errorCode:" + errorCode);
            DzSchedulers.main(new Runnable() {
                @Override
                public void run() {
                    // 此方法必须在主线程调用
                    Activity activity = weakReference.get();
                    if (activity != null) {
                        HuaweiApiAvailability.getInstance().resolveError(activity, errorCode, REQUEST_HMS_RESOLVE_ERROR);
                    }
                }
            });
        }
    }


    /**
     * 登录结果回调
     * 请CP处理登录结果
     * 登录成功result.isSuccess()  CP处理自己的业务
     * 需要授权  请调用返回结果中的intent字段来处理需要授权的场景，拉起授权页面
     * 未登录 请调用返回结果中的intent字段来处理需要授权的场景，拉起登录页面
     */
    private class CheckSignInResultCallback implements ResultCallback<SignInResult> {

        @Override
        public void onResult(SignInResult result) {
            if (result.isSuccess()) {

                checkLoginComplete(result);
            } else {
                int statusCode = result.getStatus().getStatusCode();
                if (isInitToken) {
                    HwSdkAppConstant.setStartAppSynTokenStatus(false);
                }
                ALog.dZz("华为登录检查授权失败结果 statusCode:" + statusCode + "|2001:华为帐号未登录|2002:华为帐号已登录，需要用户授权|2004:华为帐号需要检验密码");

                checkLoginError("华为登录状态不可用");

                if (statusCode == HuaweiIdStatusCodes.SIGN_IN_UNLOGIN || statusCode == HuaweiIdStatusCodes.SIGN_IN_AUTH || statusCode == HuaweiIdStatusCodes.SIGN_IN_CHECK_PASSWORD) {
                    loginAuthFailClearLoginStatus();
                }
            }
        }
    }


    /**
     * 登录成功处理
     *
     * @param result
     */
    private void checkLoginComplete(SignInResult result) {
        //可以获取帐号的 openid，昵称，头像 at信息
        SignInHuaweiId account = result.getSignInHuaweiId();
        String coverWap = account.getPhotoUrl();
        String nickName = account.getDisplayName();
        String accessToken = account.getAccessToken();
        String openId = account.getOpenId();
        String hwUid = account.getUid();
        ALog.dZz("华为帐号检查登录成功，昵称：" + nickName + "，openid:" + openId + "，hwUid:" + hwUid + "，accessToken:" + accessToken + "，头像url:" + coverWap);

        if (mAuthListener != null) {
            disHuaWeiConnect();
            mAuthListener.onComplete(openId, hwUid, accessToken, coverWap, nickName);
        }
    }

    /**
     * 登录失败
     *
     * @param message
     */
    private void checkLoginError(String message) {
        ALog.dZz(message);
        if (mAuthListener != null) {
            disHuaWeiConnect();
            mAuthListener.onError(message);
        }
    }


    private void initHwClient(final Activity activity) {
        //创建基础权限的登录参数options
        //requestUid可选，如果设置了该参数，则登录成功后会返回用户的userid
        HuaweiIdSignInOptions signInOptions = new HuaweiIdSignInOptions.Builder(HuaweiIdSignInOptions.DEFAULT_SIGN_IN).requestUid().build();

        //创建华为移动服务client实例用以登录华为帐号
        //需要指定api为HuaweiId.SIGN_IN_API
        //scope为HuaweiId.HUAEWEIID_BASE_SCOPE,可以不指定，HuaweiIdSignInOptions.DEFAULT_SIGN_IN默认使用该scope
        //连接回调以及连接失败监听
        client = new HuaweiApiClient.Builder(activity).addApi(HuaweiId.SIGN_IN_API, signInOptions).addScope(HuaweiId.HUAEWEIID_BASE_SCOPE).addConnectionCallbacks(this).addOnConnectionFailedListener(this).build();
    }

    /**
     * 明确登录失败，直接清除相关用户信息
     */
    public void loginAuthFailClearLoginStatus() {
        DzSchedulers.child(new Runnable() {
            @Override
            public void run() {
                SpUtil spUtil = SpUtil.getinstance(AppConst.getApp());
                spUtil.setAccountLoginStatus(false);
                boolean isSuccess = spUtil.setAppToken("");
                if (!isSuccess) {
                    ALog.dZz("spUtil.setAppToken 失败，重试");
                    spUtil.setAppToken("");
                }
                spUtil.setUserID("");
                spUtil.setInt(SpUtil.DZ_IS_VIP, 0);
                spUtil.setString(SpUtil.DZ_VIP_EXPIRED_TIME, "");
                spUtil.setShowReaderTime(0);
                spUtil.setLoginUserCoverWapByUserId("");
                spUtil.setLoginUserNickNameByUserId("");
                //清除用户资产的一些设置
                spUtil.setString(SpUtil.DZ_OLD_USER_ASSERT_SHU_QI_H5_URL, "");
                spUtil.setString(SpUtil.DZ_OLD_USER_ASSERT_ZHANG_YUE_H5_URL, "");

                ALog.dZz("loginAuthFailClearLoginStatus 清除用户信息完成");
                EventBusUtils.sendMessage(EventConstant.LOGIN_CHECK_RSET_PERSON_LOGIN_STATUS);
            }
        });
    }


    /**
     * 重置监听
     */
    public void resetAgainObtainListener() {
        mAgainObtainListener = null;
    }
}
