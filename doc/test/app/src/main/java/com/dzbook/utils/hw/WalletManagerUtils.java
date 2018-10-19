package com.dzbook.utils.hw;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;

import com.dzbook.lib.net.DzSchedulers;
import com.dzbook.lib.utils.ALog;
import com.dzbook.lib.utils.StringUtil;
import com.dzbook.utils.NetworkUtils;
import com.huawei.walletapi.logic.IQueryCallback;
import com.huawei.walletapi.logic.QueryParams;
import com.huawei.walletapi.logic.ResponseResult;
import com.huawei.walletapi.logic.WalletManager;

/**
 * 支付管理工具类
 *
 * @author lizz 2018/4/23.
 */

public class WalletManagerUtils {

    /**
     * 商户id
     */
    private static final String MERCHANTS_ID = "900086000021971033";

    /**
     * 查询花币余额接口
     *
     * @param activity      activity
     * @param queryCallBack queryCallBack
     */
    public static void queryWalletInfo(final Activity activity, final HcoinQueryCallBack queryCallBack) {
        if (!NetworkUtils.getInstance().checkNet()) {
            return;
        }

        if (!StringUtil.isEmpty(LoginUtils.getInstance().hwOpenId, LoginUtils.getInstance().hwAccessToken)) {

            queryWalletInfo(activity, MERCHANTS_ID, LoginUtils.getInstance().hwOpenId, LoginUtils.getInstance().hwAccessToken, new HcoinQueryCallBackSub() {
                @Override
                public void queryFail(String returnCode) {
                    if (TextUtils.equals(returnCode, ResponseResult.QUERY_ERROR_TOKEN)) {
                        queryWalletInfoByNewToken(activity, queryCallBack);
                    }
                }

                @Override
                public void querySuccess(float hCoin) {
                    queryCallBack.querySuccess(hCoin);
                }
            });

        } else {
            queryWalletInfoByNewToken(activity, queryCallBack);
        }
    }

    /**
     * 获取新的token后再查询华为花余额
     *
     * @param activity      activity
     * @param queryCallBack queryCallBack
     */
    private static void queryWalletInfoByNewToken(final Activity activity, final HcoinQueryCallBack queryCallBack) {
        LoginCheckUtils.getInstance().checkHwLogin(activity, new LoginUtils.DzAuthListener() {
            @Override
            public void onComplete(String openId, String hwUid, String accessToken, final String coverWap, final String nickName) {
                queryWalletInfo(activity, MERCHANTS_ID, openId, accessToken, queryCallBack);
            }

            @Override
            public void onError(String errDes) {
            }
        });
    }

    /**
     * 查询花币余额接口
     *
     * @param context
     */
    private static void queryWalletInfo(Context context, String merchantsId, String accountId, String accessToken, final HcoinQueryCallBack queryCallBack) {
        QueryParams queryParams = new QueryParams();
        queryParams.setUserId(merchantsId);
        queryParams.setAccountId(accountId);
        queryParams.setAccessToken(accessToken);
        queryParams.setContext(context.getApplicationContext());
        queryParams.setQueryFlag(QueryParams.FLAG_HCOIN);
        WalletManager.getInstance().queryWalletInfo(queryParams, new IQueryCallback() {

            @Override
            public void onQueryResult(ResponseResult result) {
                String returnCode = result.getReturnCode();
                final float hCoin = result.gethCoin();
                ALog.dZz("returnCode:" + returnCode + "|0:查询成功|-1:查询失败|-2:查询超时|-3:参数错误|-4:失败，json解析错误|-5:失败,ios异常，需要检查网络是否ok|-6：失败，token异常，需要检查帐号的token是否还有效");

                if (ResponseResult.QUERY_SUCCESS.equals(returnCode)) {

                    DzSchedulers.main(new Runnable() {
                        @Override
                        public void run() {
                            queryCallBack.querySuccess(hCoin);
                        }
                    });
                }
            }
        });
    }


    /**
     * 第三方打开花币页面
     *
     * @param activity activity
     */
    public static void startHcoinActivity(Activity activity) {
        WalletManager.getInstance().startHcoinActivity(activity);
    }

    /**
     * 花币查询回调接口
     */
    public interface HcoinQueryCallBackSub extends HcoinQueryCallBack {

        /**
         * 查询失败
         *
         * @param returnCode returnCode
         */
        void queryFail(String returnCode);
    }

    /**
     * 花币查询回调接口
     */
    public interface HcoinQueryCallBack {
        /**
         * 查询成功
         *
         * @param hCoin hCoin
         */
        void querySuccess(float hCoin);
    }
}
