package com.dzbook.web;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.webkit.WebView;

import com.dzbook.AppConst;
import com.dzbook.BaseLoadActivity;
import com.dzbook.lib.net.DzSchedulers;
import com.dzbook.lib.utils.ALog;
import com.dzbook.lib.utils.StringUtil;
import com.dzbook.loader.BookLoader;
import com.dzbook.log.LogConstants;
import com.dzbook.log.SourceFrom;
import com.dzbook.mvp.DialogConstants;
import com.dzbook.mvp.presenter.RechargeListPresenter;
import com.dzbook.pay.Listener;
import com.dzbook.recharge.RechargeObserver;
import com.dzbook.recharge.order.RechargeParamBean;
import com.dzbook.service.InsertBookInfoDataUtil;
import com.dzbook.utils.WhiteListWorker;
import com.dzpay.recharge.api.UtilRecharge;
import com.dzpay.recharge.bean.RechargeAction;
import com.dzpay.recharge.bean.RechargeMsgResult;
import com.dzpay.recharge.bean.RechargeObserverConstants;
import com.iss.view.common.ToastAlone;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import hw.sdk.net.bean.store.BeanGetBookInfo;

/**
 * WebView js 工具类
 *
 * @author caimt
 */
public class WebViewJsUtils {

    private static void callWebViewByJs(Activity activity, final WebView webView, final String js) {

        try {
            if (activity == null || TextUtils.isEmpty(js)) {
                return;
            }
            DzSchedulers.main(new Runnable() {
                @Override
                public void run() {
                    webView.loadUrl(js);
                }
            });

        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    private static void showDialog(Context context) {
        if (null != context && context instanceof BaseLoadActivity) {
            ((BaseLoadActivity) context).showDialogByType(DialogConstants.TYPE_GET_DATA);
        }
    }

    private static void dismissDialog(Context context) {
        if (null != context && context instanceof BaseLoadActivity) {
            ((BaseLoadActivity) context).dissMissDialog();
        }
    }


    /**
     * 打包定价  一键购 组合购 406接口用于H5领书
     * 1.打包订购一键购打包订购组合购
     * 2.限免领取书籍
     * 3.优惠购
     * 4.点击添加到书架
     *
     * @param context     context
     * @param action      action
     * @param commodityId 订单id
     * @param originate   来源
     * @param bookIds     书籍id数组字符串
     * @param descFrom    descFrom
     * @param mWebView    mWebView
     * @param trackId     trackId
     * @param webFrom     webFrom
     */
    public static void packPrice(final Context context, final String action, final String commodityId, final String originate,
                                 final String bookIds, final String descFrom, final WebView mWebView, final String trackId, final String webFrom) {
        try {
            if (null == context || StringUtil.isEmpty(commodityId, action, originate, bookIds)) {
                return;
            }

            showDialog(context);

            HashMap<String, String> payData = BookLoader.getInstance().getDzLoader().getRechargePayMap(context, descFrom, null, null);
            payData.put(RechargeMsgResult.COMMODITY_ID, commodityId);
            payData.put(RechargeMsgResult.ORIGINATE, originate);
            payData.put(RechargeMsgResult.BOOKIDS, bookIds);
            payData.put(RechargeMsgResult.BUY_TYPE, action);
            payData.put(LogConstants.KEY_TRACK_ID, trackId);
            payData.put(RechargeListPresenter.PACK_BOOK, "1");
            UtilRecharge manager = UtilRecharge.getDefault();
            RechargeObserver observer = new RechargeObserver(context, new Listener() {
                Listener listener = this;

                @Override
                public void onStatusChange(int status, final Map<String, String> params) {

                    if (status != RechargeObserverConstants.PACKBOOK) {
                        return;
                    }
                    dismissDialog(context);
                    if (null == params || params.size() <= 0 || null == mWebView) {
                        return;
                    }

                    DzSchedulers.main(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                String json = params.get(RechargeMsgResult.BOOKS_JSON);
                                final BeanGetBookInfo packBook = new BeanGetBookInfo();
                                packBook.parseJSON(new JSONObject(json));
                                String packStatus = params.get(RechargeMsgResult.PACK_STATUS);
                                if (TextUtils.isEmpty(packStatus)) {
                                    packStatus = packBook.status;
                                }
                                ALog.cmtDebug("packStatus:" + packStatus);
                                dealPackStatus(packStatus, context, mWebView, packBook, webFrom, listener, params, action, trackId);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                        }
                    });

                }

                @Override
                public void onSuccess(int ordinal, HashMap<String, String> parm) {
                    dismissDialog(context);
                }

                @Override
                public void onFail(HashMap<String, String> parm) {
                    dismissDialog(context);
                    if (null == parm) {
                        return;
                    }
                    String errDes = parm.get(RechargeMsgResult.ERR_DES);
                    String errCode = parm.get(RechargeMsgResult.ERR_CODE);
                    if (!StringUtil.isEmpty(errDes, errCode) && !errCode.endsWith("02") && !errCode.endsWith("01")) {
                        ToastAlone.showShort(errDes);
                    }
                }
            }, RechargeAction.PACKBOOK_ORDER);
            manager.execute(context, payData, RechargeAction.PACKBOOK_ORDER.ordinal(), observer);
        } catch (Exception e) {
            ALog.cmtDebug("Exception:" + e.toString());
        }
    }

    private static void dealPackStatus(String packStatus, Context context, WebView mWebView, BeanGetBookInfo packBook, String webFrom, Listener listener, Map<String, String> params, String action, String trackId) {
        switch (packStatus) {
            case AppConst.PACK_STATUS_BUY_SUCCESS:
            case AppConst.PACK_STATUS_PAY_SUCCESS:
                WebViewJsUtils.callWebViewByJs((Activity) context, mWebView, JSCode.loadJs("successDialog", packStatus, packBook.getBookNames()));
                InsertBookInfoDataUtil.insertWebBook(context, packBook, WhiteListWorker.BOOK_LING_QU_VALUE, webFrom);
                break;
            case AppConst.PACK_STATUS_LING_SUCCESS:
                WebViewJsUtils.callWebViewByJs((Activity) context, mWebView, JSCode.loadJs("successDialog", packStatus, packBook.getBookNames()));
                InsertBookInfoDataUtil.insertWebBook(context, packBook, WhiteListWorker.BOOK_LING_QU_VALUE, webFrom);
                break;
            case AppConst.PACK_STATUS_E:
                WebViewJsUtils.callWebViewByJs((Activity) context, mWebView, JSCode.loadJs("failDialog", packBook.message));
                break;
            case AppConst.PACK_STATUS_LING_E:
                WebViewJsUtils.callWebViewByJs((Activity) context, mWebView, JSCode.loadJs("failDialog", packBook.message));
                break;
            case AppConst.PACK_STATUS_OVER_E:
                //活动过期 刷新页面
                ToastAlone.showShort(packBook.message);
                mWebView.reload();
                break;
            case AppConst.PACK_STATUS_TO_PAY:
                //去充值
                params.put(RechargeMsgResult.PACK_TITLE, packBook.title);
                params.put(RechargeMsgResult.PACK_PAY_PRICE, packBook.remainSum);
                params.put(RechargeMsgResult.PACK_COST_PRICE, packBook.price);
                params.put(RechargeMsgResult.PACK_BALANCE, packBook.balance);
                RechargeParamBean paramBean = new RechargeParamBean(context, listener, RechargeAction.PACKBOOK_ORDER.ordinal(), "打包订购", (HashMap<String, String>) params, null, SourceFrom.FROM_PACK_ORDER, LogConstants.RECHARGE_SOURCE_FROM_VALUE_1);
                RechargeListPresenter.launch(paramBean);
                break;
            case AppConst.PACK_STATUS_RECHARGE_SUCCESS:
                ALog.cmtDebug("packbook:充值成功");
                //充值成功再去打包订购
                String commodityId = params.get(RechargeMsgResult.COMMODITY_ID);
                String bookIds = params.get(RechargeMsgResult.BOOKIDS);
                String originate = params.get(RechargeMsgResult.ORIGINATE);
                packPrice(context, action, commodityId, originate, bookIds, "2", mWebView, trackId, webFrom);
                break;
            default:
                break;
        }
    }

}
