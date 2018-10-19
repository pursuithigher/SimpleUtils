package com.dzbook.mvp.presenter;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.dzbook.activity.reader.ReaderUtils;
import com.dzbook.database.bean.BookInfo;
import com.dzbook.database.bean.CatalogInfo;
import com.dzbook.lib.utils.ALog;
import com.dzbook.loader.BookLoader;
import com.dzbook.loader.LoadResult;
import com.dzbook.log.DzLog;
import com.dzbook.log.LogConstants;
import com.dzbook.mvp.DialogConstants;
import com.dzbook.mvp.UI.LotOrderUI;
import com.dzbook.pay.Listener;
import com.dzbook.recharge.RechargeObserver;
import com.dzbook.recharge.order.LotOrderPageActivity;
import com.dzbook.recharge.order.RechargeParamBean;
import com.dzbook.service.MarketDao;
import com.dzbook.service.RechargeParams;
import com.dzbook.utils.DBUtils;
import com.dzbook.utils.HwLog;
import com.dzbook.utils.NetworkUtils;
import com.dzbook.utils.NumberUtils;
import com.dzbook.utils.SpUtil;
import com.dzbook.utils.ThirdPartyLog;
import com.dzpay.recharge.api.UtilRecharge;
import com.dzpay.recharge.bean.RechargeAction;
import com.dzpay.recharge.bean.RechargeConstants;
import com.dzpay.recharge.bean.RechargeErrType;
import com.dzpay.recharge.bean.RechargeMsgResult;
import com.dzpay.recharge.bean.RechargeObserverConstants;
import com.dzpay.recharge.netbean.LotOrderPageBean;
import com.dzpay.recharge.netbean.LotOrderPageBeanInfo;
import com.dzpay.recharge.netbean.OrdersCommonBean;
import com.ishugui.R;
import com.iss.app.BaseActivity;
import com.iss.view.common.ToastAlone;

import org.json.JSONObject;

import java.util.HashMap;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

/**
 * 批量订购
 *
 * @author lizz 2017/8/3.
 */
public class LotOrderPresenter extends BaseOrderPresenter {


    private LotOrderUI mUI;


    private RechargeAction actionRefer;

    private boolean isReader;

    private LotOrderPageBeanInfo lotOrderPageBeanInfo;

    /**
     * 构造
     *
     * @param lotOrderUI lotOrderUI
     */
    public LotOrderPresenter(LotOrderUI lotOrderUI) {
        mUI = lotOrderUI;
    }

    /**
     * 获取参数
     */
    @SuppressWarnings("unchecked")
    public void getParamsInfo() {

        Intent intent = mUI.getHostActivity().getIntent();
        if (intent == null) {
            return;
        }

        params = (HashMap<String, String>) intent.getSerializableExtra(RechargeObserver.PARAMS);
        if (params == null) {
            return;
        }

        oprateFrom = params.get(RechargeMsgResult.OPERATE_FROM);
        partFrom = params.get(RechargeMsgResult.PART_FROM);

        String isReader1 = params.get(RechargeMsgResult.IS_READER);
        if (TextUtils.equals(isReader1, "1")) {
            this.isReader = true;
        }

        if (null != observer) {
            this.actionRefer = observer.action;
            this.listener = observer.listener;
        }

    }

    /**
     * 获取订单信息
     */
    public void getLotOrderInfo() {

        try {
            String jsonStr = params.get(RechargeMsgResult.REQUEST_JSON);

            bookId = params.get(RechargeMsgResult.BOOK_ID);
            chapterId = params.get(RechargeMsgResult.CHAPTER_BASE_ID);

            lotOrderPageBeanInfo = new LotOrderPageBeanInfo().parseJSON(new JSONObject(jsonStr));

            setRemainSum(lotOrderPageBeanInfo);

            if (lotOrderPageBeanInfo != null && lotOrderPageBeanInfo.isExistLotData()) {

                if (lotOrderPageBeanInfo.isSingleBook()) {
                    //单本
                    mUI.setSingleLotOrderInfo(lotOrderPageBeanInfo, isReader);
                } else {
                    //连载
                    mUI.setSerialLotOrderInfo(lotOrderPageBeanInfo, isReader);
                }

            } else {
                mUI.showDataError();
            }

        } catch (Exception e) {
            mUI.showDataError();
        }
    }


    private void setRemainSum(LotOrderPageBeanInfo beanInfo) {
        String remainSum = beanInfo.remain + "";
        String priceUnit = beanInfo.priceUnit;
        if (!TextUtils.isEmpty(remainSum) && !TextUtils.isEmpty(priceUnit)) {
            SpUtil.getinstance(mUI.getContext()).setUserRemain(remainSum, priceUnit);
        }
    }

    /**
     * 充值
     *
     * @param item        item
     * @param sourceWhere sourceWhere
     */
    public void toRecharge(final LotOrderPageBean item, String sourceWhere) {

        int ordinal = RechargeAction.NONE.ordinal();
        if (observer != null && observer.action != null) {
            ordinal = observer.action.ordinal();
        }

        OrdersCommonBean ordersInfo = getCommonOrdersInfo(getLotOrderPageBeanInfo(), item);

        Listener mListener = new Listener() {
            @Override
            public void onSuccess(int ordinal, HashMap<String, String> parm) {
                toPay(item, true);
            }

            @Override
            public void onFail(HashMap<String, String> parm) {
            }
        };

        RechargeParamBean paramBean = new RechargeParamBean(mUI.getHostActivity(), mListener, ordinal, sourceWhere, params, trackId, mUI.getTagName(), LogConstants.RECHARGE_SOURCE_FROM_VALUE_1, ordersInfo);
        RechargeListPresenter.launch(paramBean);

        setUmengEventGoRecharge(item.tips);
        dzLogGoRecahrge(item.afterNum + "");

    }

    /**
     * 支付
     *
     * @param bean                   bean
     * @param isRechargeSuccessToPay isRechargeSuccessToPay
     */
    public void toPay(final LotOrderPageBean bean, final boolean isRechargeSuccessToPay) {

        RechargeAction rechargeAction = RechargeAction.PAY;
        if (observer != null && observer.action != null) {
            rechargeAction = observer.action;
        }

        setUmengEventOrder(bean.tips);

        //打点
        dzLogOrder(bean.afterNum + "");

        mUI.showDialogByType(DialogConstants.TYPE_GET_DATA);

        //一定要重新传递，否则充值完成之后 会提示登录，登录完成之后userId可能换了
        params.put(RechargeMsgResult.USER_ID, SpUtil.getinstance(mUI.getContext()).getUserID());

        params.put(RechargeMsgResult.PAY_AFTER_NUM, bean.afterNum + "");
        params.put(RechargeMsgResult.PAY_DISCOUNT_RATE, bean.discountRate);

        RechargeObserver obs = new RechargeObserver(mUI.getContext(), new Listener() {
            @Override
            public void onSuccess(int ordinal, HashMap<String, String> parm) {
                listener.onStatusChange(RechargeConstants.DIALOG_SHOW, parm);
                listener.onSuccess(ordinal, parm);

                if (getLotOrderPageBeanInfo() != null && getLotOrderPageBeanInfo().isSingleBook()) {
                    ToastAlone.showLong(R.string.order_single_buy_success_tips);
                } else {
                    ToastAlone.showLong(R.string.order_lot_buy_success_tips);
                }

                mUI.dissMissDialog();

                lotOrderSuccessHwLog(bean);

                mUI.finish();

                ThirdPartyLog.onEvent(mUI.getContext(), ThirdPartyLog.CASH_ORDER_SUCCESS);
            }

            @Override
            public void onFail(HashMap<String, String> parm) {
                if (parm.containsKey(RechargeMsgResult.ERR_DES) && !TextUtils.isEmpty(parm.get(RechargeMsgResult.ERR_DES))) {
                    ToastAlone.showLong(parm.get(RechargeMsgResult.ERR_DES));
                } else {
                    if (isRechargeSuccessToPay) {
                        ToastAlone.showLong(R.string.order_recharge_not_enough_buy_chapter);
                    }
                }

                listener.onFail(parm);
                mUI.dissMissDialog();
                mUI.finish();

            }
        }, rechargeAction);
        UtilRecharge manager = UtilRecharge.getDefault();
        manager.execute(mUI.getContext(), params, RechargeAction.PAY.ordinal(), obs);

        // 发起付费意向
        MarketDao.markConfirmWilling(mUI.getContext(), bookId);
    }

    /**
     * 生成TrackId
     */
    public void generterTrackId() {
        trackId = DzLog.generateTrackd();
    }


    /**
     * 取消弹窗
     *
     * @param cancelType cancelType
     * @param moreDesc   moreDesc
     */
    public void dialogCancel(int cancelType, String moreDesc) {

        RechargeMsgResult msgResult = new RechargeMsgResult(getParams());
        msgResult.what = RechargeObserverConstants.FAIL;
        int actionCode = RechargeAction.PAY_CHECK.actionCode();
        if (getActionRefer() != null) {
            actionCode = getActionRefer().actionCode();
        }
        msgResult.errType.setErrCode(actionCode, cancelType);
        if (!TextUtils.isEmpty(moreDesc)) {
            msgResult.map.put(RechargeMsgResult.MORE_DESC, moreDesc);
        }
        if (observer != null) {
            observer.onErr(msgResult, getListener());
        } else if (getListener() != null) {
            getListener().onFail(msgResult.map);
        }
        mUI.finish();

        setCancelUmengEvent();

        dzLogCancel();
    }

    /**
     * 统计事件：自有单章订购页面取消
     */
    public void setCancelUmengEvent() {
        ThirdPartyLog.onEvent(mUI.getContext(), ThirdPartyLog.OWN_LOT_ORDER_PAGE_CANCLE);
    }

    /**
     * 统计事件：自有单章订购页面  展示总量
     */
    public void setUmengEventSum() {
        ThirdPartyLog.onEvent(mUI.getContext(), ThirdPartyLog.OWN_LOT_ORDER_PAGE);
    }

    /**
     * 统计事件：自有批量订购页面 确定
     *
     * @param lotsTips lotsTips
     */
    public void setUmengEventOrder(String lotsTips) {
        ThirdPartyLog.onEventValueOldClick(mUI.getContext(), ThirdPartyLog.OWN_LOT_ORDER_PAGE_ORDER, lotsTips + "-确定", 1);
    }

    /**
     * 统计事件：自有批量订购页面 余额不足去充值
     *
     * @param lotsTips lotsTips
     */
    public void setUmengEventGoRecharge(String lotsTips) {
        ThirdPartyLog.onEventValueOldClick(mUI.getContext(), ThirdPartyLog.OWN_LOT_ORDER_GO_RECHARGE, lotsTips + "-余额不足，去充值", 1);
    }

    @Override
    public BaseActivity getHostActivity() {
        return mUI.getHostActivity();
    }

    public String getPartFrom() {
        return partFrom;
    }

    public RechargeAction getActionRefer() {
        return actionRefer;
    }

    /**
     * onDestroy
     */
    public static void onDestroy() {
        observer = null;
    }

    /**
     * 刷新界面
     */
    public void refreshUIPage() {

        if (observer != null && observer.context != null && observer.context instanceof BaseActivity) {

            //这里一定不能用当前的activity  应该需要关闭当前页面
            final BaseActivity activity = (BaseActivity) observer.context;

            dialogCancel(RechargeErrType.VIEW_BACK, "VIP开通成功，刷新当前页面");

            lotOrderRefresh(activity);
        }
    }

    private void lotOrderRefresh(final BaseActivity activity) {

        Observable.create(new ObservableOnSubscribe<LoadResult>() {

            @Override
            public void subscribe(ObservableEmitter<LoadResult> e) {

                BookInfo mBookInfo = DBUtils.findByBookId(activity, bookId);
                CatalogInfo mCatalogInfo = DBUtils.getCatalog(activity, bookId, chapterId);

                RechargeParams rechargeParams = new RechargeParams("4", mBookInfo);
                rechargeParams.setOperateFrom(oprateFrom);
                rechargeParams.setPartFrom(partFrom);
                rechargeParams.isReader = isReader;

                LoadResult result = BookLoader.getInstance().loadBulkChapters(activity, mBookInfo, mCatalogInfo, rechargeParams);
                if (result != null) {
                    result.mChapter = mCatalogInfo;
                }

                e.onNext(result);
                e.onComplete();
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribeWith(new DisposableObserver<LoadResult>() {
            @Override
            public void onNext(LoadResult value) {
                activity.dissMissDialog();

                if (isReader) {
                    if (value == null) {
                        ALog.dLwx("LoadResult null");
                        activity.showNotNetDialog();
                        return;
                    }
                    if (value.status == LoadResult.STATUS_NET_WORK_NOT_USE || value.status == LoadResult.STATUS_NET_WORK_NOT_COOL || value.status == LoadResult.STATUS_ERROR && !NetworkUtils.getInstance().checkNet()) {
                        if (!TextUtils.isEmpty(value.getMessage(mUI.getContext())) && mUI.getHostActivity() != null) {
                            mUI.getHostActivity().showNotNetDialog();
                        }
                    } else {
                        ReaderUtils.dialogOrToast(activity, value.getMessage(mUI.getContext()), false, bookId);
                    }
                } else {
                    isSuccess(value, activity);
                }
                ALog.dZz("LoadResult:" + value.status);
            }

            @Override
            public void onError(Throwable e) {
                activity.dissMissDialog();
            }

            @Override
            public void onComplete() {

            }

            @Override
            protected void onStart() {
                activity.showDialogByType(DialogConstants.TYPE_GET_DATA);
            }
        });
    }

    private void isSuccess(LoadResult value, BaseActivity activity) {
        if (value.isSuccess()) {
            CatalogInfo info = DBUtils.getCatalog(activity, value.mChapter.bookid, value.mChapter.catalogid);
            ReaderUtils.intoReader(activity, info, info.currentPos);
        } else {
            if (value.status == LoadResult.STATUS_NET_WORK_NOT_USE || value.status == LoadResult.STATUS_NET_WORK_NOT_COOL || value.status == LoadResult.STATUS_ERROR && !NetworkUtils.getInstance().checkNet()) {
                if (!TextUtils.isEmpty(value.getMessage(mUI.getContext())) && mUI.getHostActivity() != null) {
                    mUI.getHostActivity().showNotNetDialog();
                }
            } else {
                ToastAlone.showShort(value.getMessage(activity));
            }
        }
    }

    /**
     * 获取订单信息
     *
     * @param beanInfo beanInfo
     * @param bean     bean
     * @return OrdersCommonBean
     */
    public OrdersCommonBean getCommonOrdersInfo(LotOrderPageBeanInfo beanInfo, LotOrderPageBean bean) {
        String orderName;
        if (beanInfo.isSingleBook()) {
            orderName = beanInfo.bookName;
        } else {
            orderName = beanInfo.startChapter;
        }
        OrdersCommonBean commonBean = new OrdersCommonBean(beanInfo.unit, beanInfo.remain, beanInfo.vouchers, beanInfo.priceUnit, beanInfo.vUnit, bean.needPay, bean.deduction, orderName, beanInfo.author, bean.price, bean.disTips, bean.oldPrice, beanInfo.vipTips);
        commonBean.setBookId(beanInfo.bookId);
        return commonBean;
    }

    public LotOrderPageBeanInfo getLotOrderPageBeanInfo() {
        return lotOrderPageBeanInfo;
    }


    private void lotOrderSuccessHwLog(LotOrderPageBean beanInfo) {
        try {
            if (beanInfo != null) {
                //1：全本；2：批量章节；3：单章
                String buyType = lotOrderPageBeanInfo.isSingleBook() ? "1" : "2";
                String deduction = "0";
                String needPay = "0";
                String price = NumberUtils.numberConversion(Integer.parseInt(beanInfo.price), 100);
                if (!TextUtils.isEmpty(beanInfo.deduction)) {
                    deduction = NumberUtils.numberConversion(Integer.parseInt(beanInfo.deduction), 100);
                }
                if (!TextUtils.isEmpty(beanInfo.needPay)) {
                    needPay = NumberUtils.numberConversion(Integer.parseInt(beanInfo.needPay), 100);
                }

                HwLog.buyBook(bookId, lotOrderPageBeanInfo.bookName, buyType, beanInfo.afterNum + "", price, deduction, needPay, "0");
            }

        } catch (Exception e) {
            ALog.printStackTrace(e);
        }
    }


    /**
     * 跳转LotOrderPageActivity
     *
     * @param context  context
     * @param observer observer
     * @param params   params
     */
    public static void launchLotOrderPage(Context context, RechargeObserver observer, HashMap<String, String> params) {
        Intent intent = new Intent(context, LotOrderPageActivity.class);
        LotOrderPresenter.observer = observer;
        intent.putExtra(RechargeObserver.PARAMS, params);
        context.startActivity(intent);
        if (context instanceof Activity) {
            BaseActivity.showActivity(context);
        }
    }

    /**
     * onResume
     */
    public void onResume() {
        if (observer != null && observer.context != null && observer.context instanceof BaseActivity) {
            final BaseActivity activity = (BaseActivity) observer.context;
            activity.dissMissDialog();

        }
    }
}
