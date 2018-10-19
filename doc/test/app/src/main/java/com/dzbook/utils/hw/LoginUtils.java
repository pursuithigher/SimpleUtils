package com.dzbook.utils.hw;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.dzbook.lib.net.DzSchedulers;
import com.dzbook.lib.utils.ALog;
import com.dzbook.lib.utils.StringUtil;
import com.dzbook.service.HwLoginRunnable;
import com.dzbook.service.LoginStatusListener;
import com.dzbook.utils.NetworkUtils;
import com.dzbook.utils.SpUtil;
import com.dzbook.utils.UserInfoUtils;
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
import com.iss.view.common.ToastAlone;

import java.lang.ref.WeakReference;

import hw.sdk.HwSdkAppConstant;
import hw.sdk.net.bean.register.RegisterBeanInfo;

/**
 * 登录工具类
 *
 * @author lizhongzhong 2018/4/12.
 */

public class LoginUtils implements HuaweiApiClient.ConnectionCallbacks, HuaweiApiClient.OnConnectionFailedListener {


    /********************************************华为帐号登录*****************************************************/

    /**
     * 如果CP在onConnectionFailed调用了resolveError接口，那么错误结果会通过onActivityResult返回
     * 具体的返回码通过该字段获取
     */
    private static final String EXTRA_RESULT = "intent.extra.RESULT";
    /**
     * 启动参数，用来区分是调用的登录intent还是授权intent
     */
    private static final int REQUEST_SIGN_IN_UNLOGIN = 1002;
    private static final int REQUEST_SIGN_IN_AUTH = 1003;
    private static final int REQUEST_SIGN_IN_CHECK_PASSWORD = 1005;
    /**
     * 调用HuaweiApiAvailability.getInstance().resolveError传入的第三个参数
     * 作用同startactivityforresult方法中的requestcode
     */
    private static final int REQUEST_HMS_RESOLVE_ERROR = 1000;
    private static volatile LoginUtils sInstance;

    /**
     * OpenId
     */
    String hwOpenId;
    /**
     * Token
     */
    String hwAccessToken;

    /**
     * 华为移动服务Client
     */
    private HuaweiApiClient client;

    private boolean mResolvingError = false;

    private LoginCheckListener mCheckListener;

    private WeakReference<Activity> weakReference;

    /**
     * 登录授权 总回调
     */
    private LoginUtils.DzAuthListener dzAuthListener = new LoginUtils.DzAuthListener() {

        @Override
        public void onComplete(String openId, String hwUid, String accessToken, final String coverWap, final String nickName) {

            if (!StringUtil.isEmpty(openId, accessToken)) {
                LoginUtils.getInstance().hwOpenId = openId;
                LoginUtils.getInstance().hwAccessToken = accessToken;
            }

            disHuaWeiConnect();
            ALog.dZz("dzAuthListener onComplete isLoginCheck:" + false);
            loginAuthCompleteServerBind(weakReference.get(), hwUid, openId, accessToken, coverWap, nickName, false, null);
        }

        @Override
        public void onError(String errDes) {
            disHuaWeiConnect();
            loginFailListener();
        }
    };

    private LoginUtils() {

    }

    /**
     * 获取LoginUtils实例
     *
     * @return 实例
     */
    public static LoginUtils getInstance() {
        if (sInstance == null) {
            synchronized (LoginUtils.class) {
                if (sInstance == null) {
                    sInstance = new LoginUtils();
                }
            }
        }

        return sInstance;
    }

    /**
     * 登录
     *
     * @param context context
     */
    public void login(Context context) {
        try {
            if (!NetworkUtils.getInstance().checkNet()) {
                return;
            }
            Activity activity;
            if (context instanceof Activity) {
                activity = (Activity) context;
            } else {
                dzAuthListener.onError("context 不是activity类型");
                return;
            }

            huaWeiLogin(activity);
        } catch (Exception e) {
            ALog.printStackTrace(e);
            loginError("登录失败，请稍后重试");
        }
    }

    /**
     * 检查登录状态
     * true:登录成功
     * false:登录失败
     *
     * @param context context
     * @return boolean
     */
    public boolean checkLoginStatus(Context context) {
        return SpUtil.getinstance(context).getAccountLoginStatus();
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

    @Override
    public void onConnected() {
        //华为移动服务client连接成功，在这边处理业务自己的事件
        ALog.dZz("HuaweiApiClient 连接成功");
        if (weakReference != null && weakReference.get() != null) {
            signIn(weakReference.get());
        }
    }

    @Override
    public void onConnectionSuspended(int cause) {
        //HuaweiApiClient断开连接的时候，业务可以处理自己的事件
        ALog.dZz("HuaweiApiClient 连接断开");
        //HuaweiApiClient异常断开连接, if 括号里的条件可以根据需要修改
        Activity activity = null != weakReference ? weakReference.get() : null;
        if (activity != null && activity.isFinishing()) {
            client.connect(activity);
        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        ALog.dZz("HuaweiApiClient连接失败，错误码：" + connectionResult.getErrorCode());

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
                    if (weakReference != null && weakReference.get() != null) {
                        HuaweiApiAvailability.getInstance().resolveError(weakReference.get(), errorCode, REQUEST_HMS_RESOLVE_ERROR);
                    }
                }
            });
        } else {
            //其他错误码请参见开发指南或者API文档
            loginError("华为帐号服务初始化失败，请稍后再试");
        }
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

    /**
     * 登录华为移动服务
     *
     * @param activity activity
     */
    private void huaWeiLogin(final Activity activity) {
        weakReference = new WeakReference<Activity>(activity);

        if (client == null) {
            initHwClient(weakReference.get());
        }
        //建议在oncreate的时候连接华为移动服务
        //业务可以根据自己业务的形态来确定client的连接和断开的时机，但是确保connect和disconnect必须成对出现
        client.connect(weakReference.get());

        signIn(weakReference.get());
    }


    private void signIn(Activity activity) {
        if (client == null) {
            if (weakReference.get() == null) {
                weakReference = new WeakReference<Activity>(activity);
            }
            initHwClient(weakReference.get());
        }
        if (!client.isConnected()) {
            ALog.dZz("登录帐号失败，原因：HuaweiApiClient未连接");
            client.connect(activity);
            return;
        }

        PendingResult<SignInResult> signInResult = HuaweiId.HuaweiIdApi.signInBackend(client);
        signInResult.setResultCallback(new SignInResultCallback(activity));
    }


    /**
     * 登录授权接口
     */
    public interface DzAuthListener {
        /**
         * cmcc登录,华为登录单独定制
         *
         * @param accessToken accessToken
         * @param openId      openId
         * @param coverWap    coverWap
         * @param nickName    nickName
         * @param hwUid       hwUid
         */
        void onComplete(String openId, String hwUid, String accessToken, String coverWap, String nickName);

        /**
         * 登录失败
         *
         * @param errDes errDes
         */
        void onError(String errDes);
    }


    /**
     * 登录结果回调
     * 请CP处理登录结果
     * 登录成功result.isSuccess()  CP处理自己的业务
     * 需要授权  请调用返回结果中的intent字段来处理需要授权的场景，拉起授权页面
     * 未登录 请调用返回结果中的intent字段来处理需要授权的场景，拉起登录页面
     */
    private class SignInResultCallback implements ResultCallback<SignInResult> {

        private Activity activity;

        public SignInResultCallback(Activity activity) {
            this.activity = activity;
        }

        @Override
        public void onResult(SignInResult result) {
            if (result.isSuccess()) {

                loginComplete(result);
            } else {
                //当未登录或者未授权，回调的result中包含处理该种异常的intent，CP只需要通过getData将对应异常的intent获取出来
                //并通过startActivityForResult启动对应的异常处理界面。再相应的页面处理完毕后返回结果后，CP需要做相应的处理
                //等
                if (result.getStatus().getStatusCode() == HuaweiIdStatusCodes.SIGN_IN_UNLOGIN) {
                    ALog.dZz("华为帐号未登录");
                    Intent intent = result.getData();
                    if (intent != null) {
                        activity.startActivityForResult(intent, REQUEST_SIGN_IN_UNLOGIN);
                    } else {
                        //异常场景，未知原因导致的登录失败，CP可以在这走容错处理
                        loginError(result);
                    }
                } else if (result.getStatus().getStatusCode() == HuaweiIdStatusCodes.SIGN_IN_AUTH) {
                    ALog.dZz("华为帐号已登录，需要用户授权");
                    Intent intent = result.getData();
                    if (intent != null) {
                        activity.startActivityForResult(intent, REQUEST_SIGN_IN_AUTH);
                    } else {
                        //异常场景，未知原因导致的登录失败，CP可以在这走容错处理
                        loginError(result);
                    }
                } else if (result.getStatus().getStatusCode() == HuaweiIdStatusCodes.SIGN_IN_CHECK_PASSWORD) {
                    //华为帐号需要检验密码
                    Intent intent = result.getData();
                    if (intent != null) {
                        activity.startActivityForResult(intent, REQUEST_SIGN_IN_CHECK_PASSWORD);
                    } else {
                        //异常场景，未知原因导致的登录失败，CP可以在这走容错处理
                        loginError(result);
                    }
                } else {
                    if (result.getStatus().getStatusCode() == HuaweiIdStatusCodes.SIGN_IN_NETWORK_ERROR) {
                        ALog.dZz("网络错误");
                    }
                    //网络异常，请CP自行处理
                    loginError(result);
                }
            }
        }
    }


    /**
     * 华为登录授权onActivityResult中回调
     *
     * @param activity    activity
     * @param requestCode requestCode
     * @param resultCode  resultCode
     * @param data        data
     */
    public void doHuaweiOnActivityResult(Activity activity, int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_SIGN_IN_UNLOGIN) {
            onResultSignInUnLogin(activity, resultCode);
        } else if (requestCode == REQUEST_SIGN_IN_AUTH) {
            onResultSignInAuth(resultCode, data);

        } else if (requestCode == REQUEST_SIGN_IN_CHECK_PASSWORD) {
            onResultSignInCheckPassword(activity, resultCode);
        } else if (requestCode == REQUEST_HMS_RESOLVE_ERROR) {
            onResultHmsResolveError(activity, resultCode, data);
        }
    }

    private void onResultSignInUnLogin(Activity activity, int resultCode) {
        //当返回值是-1的时候表明用户登录成功，需要CP再次调用signIn
        if (resultCode == Activity.RESULT_OK) {
            ALog.dZz("用户登录 成功");
            signIn(activity);
        } else {
            //当resultCode 为0的时候表明用户未登录，则CP可以处理用户不登录事件
            loginError("用户登录失败或者未登录");
            ALog.dZz("用户登录失败或者未登录");
        }
    }

    private void onResultSignInAuth(int resultCode, Intent data) {
        //当返回值是-1的时候表明用户确认授权，
        if (resultCode == Activity.RESULT_OK) {
            ALog.dZz("用户已经授权");
            SignInResult result = HuaweiId.HuaweiIdApi.getSignInResultFromIntent(data);

            if (result.isSuccess()) {
                loginComplete(result);

            } else {
                // 授权失败，result.getStatus()获取错误原因
                loginError(result);
            }
        } else {
            //当resultCode 为0的时候表明用户未授权，则CP可以处理用户未授权事件
            loginError("取消授权");
        }
    }

    private void onResultSignInCheckPassword(Activity activity, int resultCode) {
        if (resultCode == Activity.RESULT_OK) {
            //登录成功
            signIn(activity);
        } else {
            //登录失败
            loginError("登录失败，请重试");
        }
    }

    private void onResultHmsResolveError(Activity activity, int resultCode, Intent data) {
        mResolvingError = false;
        if (resultCode == Activity.RESULT_OK) {
            int result = data.getIntExtra(EXTRA_RESULT, 0);
            if (result == ConnectionResult.SUCCESS) {
                ALog.dZz("错误成功解决");
                if (client != null && !client.isConnecting() && !client.isConnected()) {
                    client.connect(activity);
                }
            } else {
                if (result == ConnectionResult.CANCELED) {
                    ALog.dZz("解决错误过程被用户取消");
                } else if (result == ConnectionResult.INTERNAL_ERROR) {
                    ALog.dZz("发生内部错误，重试可以解决");
                    //CP可以在此处重试连接华为移动服务等操作，导致失败的原因可能是网络原因等
                } else {
                    ALog.dZz("未知返回码");
                }
                loginError("支付服务连接失败，请稍后再试");
            }
        } else {
            ALog.dZz("调用解决方案发生错误");
            loginError("支付服务连接失败，请稍后再试");
        }
    }

    /**
     * 登录成功处理
     *
     * @param result
     */
    private void loginComplete(SignInResult result) {
        //可以获取帐号的 openid，昵称，头像 at信息
        SignInHuaweiId account = result.getSignInHuaweiId();
        String coverWap = account.getPhotoUrl();
        String nickName = account.getDisplayName();
        String accessToken = account.getAccessToken();
        String openId = account.getOpenId();
        String hwUid = account.getUid();
        ALog.dZz("华为帐号登录成功，昵称：" + nickName + "，openid:" + openId + "，hwUid:" + hwUid + "，accessToken:" + accessToken + "，头像url:" + coverWap);

        if (dzAuthListener != null) {
            dzAuthListener.onComplete(openId, hwUid, accessToken, coverWap, nickName);
        }
    }

    /**
     * 登录失败
     *
     * @param result
     */
    private void loginError(SignInResult result) {
        ALog.dZz("授权失败 失败原因:" + result.getStatus().toString());

        if (dzAuthListener != null) {
            String message = TextUtils.isEmpty(result.getStatus().getStatusMessage()) ? "登录失败，请重试" : result.getStatus().getStatusMessage();
            dzAuthListener.onError(message);
        }
    }

    /**
     * 登录失败
     *
     * @param message
     */
    private void loginError(String message) {
        ALog.dZz(message);
        if (dzAuthListener != null) {
            dzAuthListener.onError(message);
        }
    }

    /**
     * 是否OpenId跟之前存储的不一致
     *
     * @param openId openId
     * @return boolean
     */
    public static boolean isOpenIdNotEqual(String openId) {
        if (TextUtils.isEmpty(openId)) {
            return true;
        }
        String lastOpenId = LoginUtils.getInstance().hwOpenId;

        return !TextUtils.equals(lastOpenId, openId);
    }

    /**
     * 登录完成服务器绑定获取token
     *
     * @param context      context
     * @param hwUid        hwUid
     * @param openId       openId
     * @param accessToken  accessToken
     * @param coverWap     coverWap
     * @param nickName     nickName
     * @param isLoginCheck isLoginCheck
     * @param runnable     runnable
     */
    public void loginAuthCompleteServerBind(final Context context, final String hwUid, final String openId, final String accessToken, final String coverWap, final String nickName, final boolean isLoginCheck, final Runnable runnable) {

        if (context != null) {

            if (ALog.getDebugMode()) {
                ALog.dZz("loginAuthCompleteServerBind isLoginCheck:" + isLoginCheck + "|activityName:" + context.getClass().getSimpleName());
            }

            DzSchedulers.child(new HwLoginRunnable(openId, hwUid, accessToken, coverWap, nickName, new LoginStatusListener() {

                @Override
                public void onBindSuccess(final RegisterBeanInfo beanInfo) {

                    ALog.dZz("loginAuthCompleteServerBind  onBindSuccess");

                    //同步成功
                    HwSdkAppConstant.setStartAppSynTokenStatus(true);
                    HwSdkAppConstant.setIsAppTokenInvalidNeedRetrySys(false);

                    DzSchedulers.child(new Runnable() {
                        @Override
                        public void run() {
                            UserInfoUtils.setLoginSuccessUserInfo(context, hwUid, beanInfo, coverWap, nickName, accessToken);
                            DzSchedulers.main(new Runnable() {
                                @Override
                                public void run() {
                                    if (mCheckListener != null && !isLoginCheck) {
                                        mCheckListener.loginComplete();
                                        mCheckListener = null;
                                    }

                                    if (runnable != null) {
                                        runnable.run();
                                    }
                                }
                            });
                        }
                    });

                }

                @Override
                public void onBindFail(String msg) {
                    ALog.eZz("loginAuthCompleteServerBind onBindFail：" + msg);
                    if (!isLoginCheck) {
                        ToastAlone.showShort(msg);
                        loginFailListener();
                    } else {
                        HwSdkAppConstant.setStartAppSynTokenStatus(false);
                    }

                }
            }));
        }
    }


    /**
     * 登录失败，为了单章加载碰到需要登录的
     * 登录成功或者登录失败后，线程等待结束，不然
     * 会让用户卡主
     */
    private void loginFailListener() {
        if (mCheckListener != null && mCheckListener instanceof LoginCheckListenerSub) {
            ((LoginCheckListenerSub) mCheckListener).loginFail();
            mCheckListener = null;
        }
    }


    /**
     * 设置强制登录检查回调
     *
     * @param context       context
     * @param checkListener checkListener
     */
    public void forceLoginCheck(final Context context, final LoginCheckListener checkListener) {
        DzSchedulers.main(new Runnable() {
            @Override
            public void run() {
                if (checkLoginStatus(context)) {
                    checkListener.loginComplete();
                } else {
                    if (mCheckListener != null) {
                        mCheckListener = null;
                    }
                    mCheckListener = checkListener;
                    login(context);
                }
            }
        });

    }

    /**
     * 登录失败接口
     */
    public interface LoginCheckListenerSub extends LoginCheckListener {

        /**
         * 登录失败
         */
        void loginFail();
    }

    /**
     * 登录完成接口
     */
    public interface LoginCheckListener {
        /**
         * 登录完成
         */
        void loginComplete();
    }

    /**
     * mainActivity销毁重置
     */
    public void resetCheckListener() {
        mCheckListener = null;
    }
}
