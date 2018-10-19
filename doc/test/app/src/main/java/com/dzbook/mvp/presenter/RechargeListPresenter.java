package com.dzbook.mvp.presenter;

import android.app.Activity;
import android.content.Intent;
import android.text.TextUtils;

import com.dzbook.AppConst;
import com.dzbook.dialog.common.DialogLoading;
import com.dzbook.lib.net.DzSchedulers;
import com.dzbook.lib.utils.ALog;
import com.dzbook.loader.BookLoader;
import com.dzbook.log.DzLog;
import com.dzbook.log.DzLogMap;
import com.dzbook.log.LogConstants;
import com.dzbook.log.SourceFrom;
import com.dzbook.mvp.BasePresenter;
import com.dzbook.mvp.UI.RechargeListUI;
import com.dzbook.net.hw.HwRequestLib;
import com.dzbook.pay.Listener;
import com.dzbook.recharge.RechargeObserver;
import com.dzbook.recharge.order.RechargeParamBean;
import com.dzbook.recharge.ui.RechargeListActivity;
import com.dzbook.utils.HwLog;
import com.dzbook.utils.NetworkUtils;
import com.dzbook.utils.SpUtil;
import com.dzbook.utils.hw.LoginCheckUtils;
import com.dzbook.utils.hw.LoginUtils;
import com.dzpay.recharge.api.UtilRecharge;
import com.dzpay.recharge.bean.RechargeAction;
import com.dzpay.recharge.bean.RechargeConstants;
import com.dzpay.recharge.bean.RechargeErrType;
import com.dzpay.recharge.bean.RechargeMsgResult;
import com.dzpay.recharge.bean.RechargeObserverConstants;
import com.dzpay.recharge.netbean.OrdersCommonBean;
import com.dzpay.recharge.netbean.OrdersResultBean;
import com.dzpay.recharge.netbean.RechargeListBeanInfo;
import com.dzpay.recharge.netbean.RechargeProductBean;
import com.dzpay.recharge.utils.RechargeMsgUtils;
import com.dzpay.recharge.utils.RechargeWayUtils;
import com.ishugui.R;
import com.iss.app.BaseActivity;
import com.iss.view.common.ToastAlone;

import org.json.JSONObject;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

/**
 * RechargeListPresenter
 *
 * @author lizz 2017/8/28.
 */

public class RechargeListPresenter extends BasePresenter {

    /**
     * 打包书籍
     */
    public static final String PACK_BOOK = "recharge_list_presenter_pack_book";

    private static final String TAG = "RechargeListPresenter";

    private static final String RECHARGE_LIST_ACTION = "action";

    private static final String SOURCE_WHERE = "sourceWhere";

    private static final String ORDER_SELECT = "orderSelect";

    private static final String PARAMS = "params";

    private static Listener listener;

    /**
     * 最大点击时间间隔
     */
    private static final long MAX_CLICK_TIME_INTERVAL = 500;

    private RechargeListUI mUI;

    /**
     * 日志打点需要
     */
    private String trackId;

    private Intent intent;

    private HashMap<String, String> params;

    private RechargeAction action;


    private RechargeListBeanInfo rechargeListBeanInfo;

    private DialogLoading mCustomDialog;

    private long memClickTimer = 0;

    private HashMap<String, String> mParam;

    /**
     * 充值列表入口
     * 1.个人中心 2.余额不足 3.主动进入
     */
    private String sourceWhere;

    private LoginCheckUtils loginCheckUtils = null;
    private boolean packbook;

    /**
     * 构造
     *
     * @param ui ui
     */
    public RechargeListPresenter(RechargeListUI ui) {
        this.mUI = ui;
    }

    /**
     * 生成trackId
     */
    public void generateTrackd() {
        intent = mUI.getHostActivity().getIntent();
        //pv打点
        if (intent == null) {
            return;
        }
        trackId = intent.getStringExtra(LogConstants.KEY_TRACK_ID);
        if (TextUtils.isEmpty(trackId)) {
            trackId = DzLog.generateTrackd();
        }
    }

    /**
     * 获取参数信息
     */
    @SuppressWarnings("unchecked")
    public void getParamInfo() {
        int ordinal = intent.getIntExtra(RECHARGE_LIST_ACTION, 0);
        sourceWhere = intent.getStringExtra(SOURCE_WHERE);
        ALog.dZz(TAG + ":sourceWhere" + sourceWhere);

        action = RechargeAction.getByOrdinal(ordinal);
        Serializable item = intent.getSerializableExtra(ORDER_SELECT);
        Serializable serializable = intent.getSerializableExtra(PARAMS);

        if (null != serializable && serializable instanceof HashMap) {
            params = (HashMap<String, String>) serializable;
            packbook = "1".equals(params.get(PACK_BOOK));
        }


        if (params != null) {
            params.put(RechargeMsgResult.USER_ID, SpUtil.getinstance(mUI.getContext()).getUserID());
        } else {
            params = BookLoader.getInstance().getDzLoader().getRechargePayMap(mUI.getContext(), "", null, null);
        }
        if (packbook) {
            String title = params.get(RechargeMsgResult.PACK_TITLE);
            String costPrice = params.get(RechargeMsgResult.PACK_COST_PRICE);
            String payPrice = params.get(RechargeMsgResult.PACK_PAY_PRICE);
            String balance = params.get(RechargeMsgResult.PACK_BALANCE);
            if (!TextUtils.isEmpty(title) && !TextUtils.isEmpty(costPrice) && !TextUtils.isEmpty(payPrice) && !TextUtils.isEmpty(balance)) {
                mUI.setPackBookOrderInfo(title, costPrice, payPrice, balance);
            }
        } else {
            if (null != item && item instanceof OrdersCommonBean) {
                OrdersCommonBean ordersInfo = (OrdersCommonBean) item;
                mUI.setOrdersInfo(ordersInfo);
            }
            params.remove(RechargeMsgResult.REQUEST_JSON);
            params.remove(RechargeMsgResult.ERR_DES);
            params.remove(RechargeMsgResult.ERR_CODE);
        }
    }

    /**
     * 获取充值信息
     */
    public void getRechargeInfo() {
        getRechargeInfo(false);
    }

    private void getRechargeInfo(final boolean isTokenInvalidRetry) {
        Disposable disposable = Observable.create(new ObservableOnSubscribe<RechargeListBeanInfo>() {

            @Override
            public void subscribe(ObservableEmitter<RechargeListBeanInfo> e) throws Exception {

                RechargeListBeanInfo beanInfo;
                try {
                    beanInfo = HwRequestLib.getInstance().getRechargeListInfo();
                    e.onNext(beanInfo);
                    e.onComplete();
                } catch (Exception e1) {
                    e.onError(e1);
                    ALog.printStackTrace(e1);
                }
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribeWith(new DisposableObserver<RechargeListBeanInfo>() {
            @Override
            public void onNext(RechargeListBeanInfo result) {
                mUI.setRequestDataSuccess();
                if (result != null) {

                    if (result.isSuccess() && result.isExistProductData()) {
                        rechargeListBeanInfo = result;
                        int defaultSelectedPosition = rechargeListBeanInfo.getSelectedProductPostion();
                        RechargeProductBean defaultSelectedBean = rechargeListBeanInfo.productBeans.get(defaultSelectedPosition);
                        mUI.setRechargeListData(rechargeListBeanInfo, defaultSelectedPosition, defaultSelectedBean);

                    } else {
                        if (!isTokenInvalidRetry && result.isTokenExpireOrNeedLogin()) {
                            tokenInvalidRetry();
                            return;
                        }
                        mUI.setNetErrorShow();
                    }

                } else {
                    mUI.setNetErrorShow();
                }
            }

            @Override
            public void onError(Throwable e) {
                mUI.setNetErrorShow();
            }

            @Override
            public void onComplete() {

            }

            @Override
            protected void onStart() {
                mUI.showLoadProgress();
            }
        });

        composite.addAndDisposeOldByKey("getRechargeInfoList", disposable);
    }

    /**
     * 销毁
     */
    public void destroy() {
        if (listener != null) {
            listener = null;
        }

        if (loginCheckUtils != null) {
            loginCheckUtils.disHuaWeiConnect();
            loginCheckUtils.resetAgainObtainListener();
        }

        composite.disposeAll();
    }

    /**
     * 充值入口。
     *
     * @param moneyBean 充值方式下面的额金额选项
     */
    public void buttonRecharge(final RechargeProductBean moneyBean) {
        if (!NetworkUtils.getInstance().checkNet()) {
            mUI.isShowNotNetDialog();
            return;
        }

        if (moneyBean == null) {
            return;
        }

        long curTime = System.currentTimeMillis();
        if (curTime - memClickTimer < MAX_CLICK_TIME_INTERVAL) {
            return;
        }
        memClickTimer = curTime;

        //打点
        dzLogRechargeClick(moneyBean);

        if (null == mCustomDialog) {
            mCustomDialog = new DialogLoading(mUI.getContext());
            mCustomDialog.setCancelable(false);
            mCustomDialog.setCanceledOnTouchOutside(false);
        }
        mCustomDialog.setShowMsg(mUI.getContext().getString(R.string.dialog_isLoading));
        mCustomDialog.show();

        params.put(RechargeMsgResult.RECHARGE_MONEY_ID, moneyBean.id);
        params.put(RechargeMsgResult.RECHARGE_WAY, RechargeWayUtils.getString(1));
        params.put(RechargeMsgResult.USER_ID, SpUtil.getinstance(mUI.getContext()).getUserID());
        params.put(RechargeMsgResult.APP_TOKEN, SpUtil.getinstance(mUI.getContext()).getAppToken());

        RechargeObserver rechargeOrder = new RechargeObserver(mUI.getContext(), new Listener() {
            @Override
            public void onSuccess(int ordinal, final HashMap<String, String> param) {
                //充值成功
                SpUtil.getinstance(mUI.getContext()).setSuccessRechargeTimes();
                try {
                    setUserRemain(param);
                } catch (Exception ignored) {
                }

                //打点充值成功
                dzLogRechargeResult(LogConstants.RECHARGE_RESULT_KEY_VALUE_1, param);

                dismissDialog();

                rechargeSuccessHwLog(moneyBean.amountNum, moneyBean.productNum, moneyBean.giveNum);

                if (packbook) {
                    //打包订购
                    packbookRechargeSuccessObserver(param);
                } else {
                    rechargeSuccessObserver(param);
                }
            }

            @Override
            public void onFail(HashMap<String, String> parm) {
                if (parm == null) {
                    return;
                }

                ToastAlone.showShort(RechargeMsgUtils.getRechargeMsg(parm));

                dismissDialog();

                //如果订单号不为空 则一定是下单成功后 三方充值失败
                String orderId = parm.get(RechargeMsgResult.RECHARGE_ORDER_NUM);
                if (!TextUtils.isEmpty(orderId) && !TextUtils.isEmpty(parm.get(RechargeMsgResult.RECHARGE_STATUS)) && !TextUtils.equals(parm.get(RechargeMsgResult.RECHARGE_STATUS), "6")) {
                    dzLogRechargeResult(LogConstants.RECHARGE_RESULT_KEY_VALUE_3, parm);
                }
            }

            @Override
            public void onStatusChange(int status, Map<String, String> parm) {
                HwRequestLib.flog("RechargeListPresenter --->RechargeObserver方法回调 -->onStatusChange  status:" + status);
                String statusChangeMsg = parm.get(RechargeMsgResult.STATUS_CHANGE_MSG);
                mCustomDialog.setShowMsg(statusChangeMsg);
            }

            @Override
            public void onRechargeStatus(int status, Map<String, String> parm) {
                HwRequestLib.flog("RechargeListPresenter --->RechargeObserver方法回调 -->onRechargeStatus  status:" + status);
                dealOnRechargeStatus(status, parm);
            }
        }, action);

        UtilRecharge manager = UtilRecharge.getDefault();
        manager.execute(mUI.getContext(), params, RechargeAction.RECHARGE.ordinal(), rechargeOrder);
    }

    private void dismissDialog() {
        if (mCustomDialog != null && mCustomDialog.isShowing()) {
            mCustomDialog.dismiss();
        }
    }

    private void dealOnRechargeStatus(int status, Map<String, String> parm) {
        ALog.dZz(TAG + " onRechargeStatus status:" + status);
        if (parm == null) {
            return;
        }
        String msg = "";
        switch (status) {
            //开始下单
            case RechargeConstants.START_MAKE_ORDER:
                msg = "开始下单";
                break;
            //下单失败
            case RechargeConstants.MAKE_ORDER_FAIL:
                dzLogRechargeResult(LogConstants.RECHARGE_RESULT_KEY_VALUE_2, parm);
                dismissDialog();
                msg = "下单失败";
                break;
            //发起充值
            case RechargeConstants.START_RECHARGE:
                msg = "发起充值";
                break;
            //订单通知开始
            case RechargeConstants.ORDER_NOTIFY_START:
                msg = "订单通知开始";
                break;
            //订单通知成功
            case RechargeConstants.ORDER_NOTIFY_SUCCESS:
                msg = "订单通知成功";
                dismissDialog();
                break;
            //订单通知失败
            case RechargeConstants.ORDER_NOTIFY_FAIL:
                dzLogRechargeResult(LogConstants.RECHARGE_RESULT_KEY_VALUE_4, parm);
                dismissDialog();
                msg = "订单通知失败";
                break;
            case RechargeConstants.ORDER_CORE_DESTROYED:
                dismissDialog();
                msg = "异常结束";
                break;
            default:
                break;
        }
        ALog.dZz("onRechargeStatus:" + msg);
    }

    /**
     * 打包定价处理
     *
     * @param parm
     */

    private void packbookRechargeSuccessObserver(HashMap<String, String> parm) {
        RechargeObserver observer = new RechargeObserver(mUI.getContext(), listener, action);
        if (parm.containsKey(RechargeMsgResult.PACK_STATUS)) {
            parm.remove(RechargeMsgResult.PACK_STATUS);
        }
        parm.put(RechargeMsgResult.PACK_STATUS, AppConst.PACK_STATUS_RECHARGE_SUCCESS);
        RechargeMsgResult msgResult = new RechargeMsgResult(parm);
        msgResult.errType.setErrCode(action, RechargeErrType.SUCCESS);
        msgResult.what = RechargeObserverConstants.PACKBOOK;
        observer.updateDir(msgResult);
        if (mCustomDialog != null && mCustomDialog.isShowing()) {
            mCustomDialog.dismiss();
        }
        mUI.finishActivity();
    }

    /**
     * 充值成功回调
     *
     * @param parm
     */
    private void rechargeSuccessObserver(HashMap<String, String> parm) {
        RechargeObserver observer = new RechargeObserver(mUI.getContext(), listener, action);
        RechargeMsgResult msgResult = new RechargeMsgResult(parm);
        msgResult.errType.setErrCode(action, RechargeErrType.SUCCESS);
        msgResult.what = RechargeObserverConstants.SUCCESS;
        //解决执行onDestroy的时候 将listener给清空了，导致回调不成功
        observer.onSuccess(msgResult);
        //        observer.update(msgResult);
        if (mCustomDialog != null && mCustomDialog.isShowing()) {
            mCustomDialog.dismiss();
        }
        mUI.finishActivity();
    }

    /**
     * 设置用户信息。
     *
     * @param param
     */
    private void setUserRemain(Map<String, String> param) {
        try {
            if (param != null) {
                String resultJson = param.get(RechargeMsgResult.RECHARGE_RESULT_JSON);
                OrdersResultBean bean = new OrdersResultBean();
                bean.parseJSON(new JSONObject(resultJson));
                SpUtil sp = SpUtil.getinstance(mUI.getContext());

                if (bean.remain != 0 && bean.vouchers != 0) {
                    sp.setUserRemain(bean.remain + "", bean.unit);
                    sp.setUserVouchers(bean.vouchers + "", bean.vouchersUnit);
                }
            }
        } catch (Exception e) {
            ALog.printStackTrace(e);
        }
    }


    /**
     * 打点
     *
     * @param moneyBean
     */
    private void dzLogRechargeClick(RechargeProductBean moneyBean) {
        HashMap<String, String> map = new HashMap<>();
        map.put(LogConstants.KEY_RECHARGE_MONEY, moneyBean.amount);

        String bookId = "";
        if (params != null) {
            bookId = params.get(RechargeMsgResult.BOOK_ID);
        }
        map.put(LogConstants.KEY_RECHARGE_BID, bookId);
        map.put(LogConstants.KEY_EXT, getExt());
        DzLog.getInstance().logClick(LogConstants.MODULE_CZ, LogConstants.ZONE_CZ_SUBTYPE, "", map, trackId);
    }


    /**
     * 打点 充值结果
     *
     * @param result    result
     * @param paramsMap paramsMap
     */
    private void dzLogRechargeResult(String result, Map<String, String> paramsMap) {
        try {
            if (paramsMap == null) {
                return;
            }

            String czCode = getEmptyString(paramsMap.get(RechargeMsgResult.ERR_CODE));
            String orderId = getEmptyString(paramsMap.get(RechargeMsgResult.RECHARGE_ORDER_NUM));
            String desc = getEmptyString(paramsMap.get(RechargeMsgResult.ERR_DES) + ":" + paramsMap.get(RechargeMsgResult.MORE_DESC));
            String bookId = "";
            //打包定价
            if ("1".equals(this.params.get(PACK_BOOK)) && !TextUtils.isEmpty(this.params.get(RechargeMsgResult.COMMODITY_ID))) {
                bookId = this.params.get(RechargeMsgResult.COMMODITY_ID);
            } else if (!TextUtils.isEmpty(this.params.get(RechargeMsgResult.BOOK_ID))) {
                bookId = this.params.get(RechargeMsgResult.BOOK_ID);
            }

            HashMap<String, String> map = new HashMap<String, String>();
            map.put(LogConstants.KEY_RECHARGE_RESULT_CZTYPE, "");
            map.put(LogConstants.KEY_RECHARGE_RESULT_RESULT, result);
            map.put(LogConstants.KEY_RECHARGE_RESULT_CZCODE, czCode);
            map.put(LogConstants.KEY_RECHARGE_RESULT_ORDERID, orderId);
            map.put(LogConstants.KEY_RECHARGE_RESULT_DESC, desc);
            map.put(LogConstants.KEY_RECHARGE_BID, bookId);
            map.put(LogConstants.KEY_EXT, getExt());
            DzLog.getInstance().logEvent(LogConstants.EVENT_CZJG, map, trackId);

        } catch (Exception e) {
            ALog.printStackTrace(e);
        }
    }

    /**
     * 获取字符串
     *
     * @param str str
     * @return str
     */
    public String getEmptyString(String str) {
        return !TextUtils.isEmpty(str) ? str : "";
    }

    /**
     * 打点
     */
    public void dzLogCancel() {
        HashMap<String, String> map = new HashMap<String, String>();
        if (params != null) {
            String bookId = params.get(RechargeMsgResult.BOOK_ID);
            map.put(LogConstants.KEY_RECHARGE_BID, bookId);
        }

        DzLog.getInstance().logClick(LogConstants.MODULE_CZ, LogConstants.ZONE_CZ_GB, null, map, trackId);
    }

    /**
     * 取消
     *
     * @param cancelDes cancelDes
     */
    public void dzObserverCancel(String cancelDes) {
        RechargeObserver observer = new RechargeObserver(mUI.getContext(), listener, action);
        RechargeMsgResult msgResult = new RechargeMsgResult(params);
        msgResult.what = RechargeObserverConstants.FAIL;
        msgResult.errType.setErrCode(action.actionCode(), RechargeErrType.SYSTEM_BACK);
        msgResult.map.put(RechargeMsgResult.MORE_DESC, cancelDes);
        observer.update(msgResult);
        mUI.finishActivity();
    }


    /**
     * 打点
     */
    public void dzPvLog() {
        String bookId = "";
        HashMap<String, String> map = new HashMap<String, String>();
        if (params != null) {
            bookId = params.get(RechargeMsgResult.BOOK_ID);
            map = DzLogMap.getReaderFrom(mUI.getHostActivity(), map, bookId);
        }
        map.put(LogConstants.KEY_EXT, getExt());
        map.put(LogConstants.KEY_ORDER_BID, bookId);
        DzLog.getInstance().logPv(mUI.getHostActivity(), map, trackId);
    }

    private String getExt() {
        String ext;
        String operateFrom = intent.getStringExtra(LogConstants.KEY_OPERATE_FROM);

        if (TextUtils.equals(operateFrom, SourceFrom.FROM_SIMPLE_PAY_ORDER) || TextUtils.equals(operateFrom, SourceFrom.FROM_LOT_PAY_ORDER)) {
            ext = LogConstants.RECHARGE_SOURCE_FROM_VALUE_1;
        } else if (TextUtils.equals(operateFrom, SourceFrom.FROM_PERSNAL_CENTER)) {
            ext = LogConstants.RECHARGE_SOURCE_FROM_VALUE_2;
        } else if (TextUtils.equals(operateFrom, SourceFrom.FROM_LOGIN)) {
            ext = LogConstants.RECHARGE_SOURCE_FROM_VALUE_3;
        } else if (TextUtils.equals(operateFrom, SourceFrom.FROM_BOOK_SHELF)) {
            ext = LogConstants.RECHARGE_SOURCE_FROM_VALUE_4;
        } else if (TextUtils.equals(operateFrom, SourceFrom.FROM_BOOK_STORE)) {
            ext = LogConstants.RECHARGE_SOURCE_FROM_VALUE_5;
        } else if (TextUtils.equals(operateFrom, SourceFrom.FROM_CENTER_DETAIL)) {
            ext = LogConstants.RECHARGE_SOURCE_FROM_VALUE_7;
        } else if (TextUtils.equals(operateFrom, SourceFrom.FROM_PACK_ORDER)) {
            ext = LogConstants.RECHARGE_SOURCE_FROM_VALUE_8;
        } else {
            ext = LogConstants.RECHARGE_SOURCE_FROM_VALUE_7;
        }
        return ext;
    }


    public void setParam(HashMap<String, String> param) {
        this.mParam = param;
    }

    /**
     * 充值成功回调
     */
    public void rechargeSuccessObserver() {
        if (mParam != null) {
            rechargeSuccessObserver(mParam);
        }
    }

    /**
     * 跳转
     *
     * @param bean bean
     */
    public static void launch(RechargeParamBean bean) {
        if (bean == null || bean.context == null) {
            return;
        }

        Intent intent = new Intent(bean.context, RechargeListActivity.class);
        RechargeListPresenter.listener = bean.listener;
        intent.putExtra(RechargeListPresenter.RECHARGE_LIST_ACTION, bean.mOrdinal);
        intent.putExtra(RechargeListPresenter.SOURCE_WHERE, bean.sourceWhere);

        if (bean.methodParams != null) {
            intent.putExtra(RechargeListPresenter.PARAMS, bean.methodParams);
        }
        if (!TextUtils.isEmpty(bean.trackId)) {
            //打点追踪
            intent.putExtra(LogConstants.KEY_TRACK_ID, bean.trackId);
        }
        intent.putExtra(ORDER_SELECT, bean.bean);
        intent.putExtra(LogConstants.KEY_OPERATE_FROM, bean.operateFrom);
        intent.putExtra(LogConstants.KEY_PART_FROM, bean.partFrom);
        bean.context.startActivity(intent);
        BaseActivity.showActivity(bean.context);
    }

    /**
     * 充值成功华为打点
     *
     * @param money   money
     * @param virtual virtual
     * @param coupon  coupon
     */
    public void rechargeSuccessHwLog(final String money, final String virtual, final String coupon) {
        DzSchedulers.execute(new Runnable() {
            @Override
            public void run() {
                HwLog.recharge(money, virtual, coupon);
            }
        });
    }

    /**
     * 只重试一次
     */
    private void tokenInvalidRetry() {
        mUI.showLoadProgress();
        if (loginCheckUtils == null) {
            loginCheckUtils = LoginCheckUtils.getInstance();
        }
        loginCheckUtils.againObtainAppToken((Activity) mUI.getContext(), new LoginUtils.LoginCheckListenerSub() {
            @Override
            public void loginFail() {
                mUI.setNetErrorShow();
            }

            @Override
            public void loginComplete() {
                getRechargeInfo(true);
            }
        });
    }

    /**
     * onResume
     */
    public void onResume() {
        Disposable disposable = DzSchedulers.mainDelay(new Runnable() {
            @Override
            public void run() {
                if (mCustomDialog != null && mCustomDialog.isShowing() && mUI.getHostActivity() != null && !mUI.getHostActivity().isFinishing()) {
                    mCustomDialog.setCancelable(true);
                    mCustomDialog.setCanceledOnTouchOutside(true);
                    mCustomDialog.dismiss();
                }
            }
        }, 10000);

        composite.addAndDisposeOldByKey("rechargeOnResume", disposable);
    }


    public Intent getIntent() {
        return intent;
    }
}
