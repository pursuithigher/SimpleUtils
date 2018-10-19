package com.dzbook.mvp.presenter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.dzbook.activity.reader.ReaderUtils;
import com.dzbook.database.bean.BookInfo;
import com.dzbook.database.bean.CatalogInfo;
import com.dzbook.lib.net.DzSchedulers;
import com.dzbook.lib.utils.ALog;
import com.dzbook.loader.BookLoader;
import com.dzbook.loader.LoadResult;
import com.dzbook.log.DzLog;
import com.dzbook.log.LogConstants;
import com.dzbook.mvp.DialogConstants;
import com.dzbook.mvp.UI.SingleOrderUI;
import com.dzbook.pay.Listener;
import com.dzbook.recharge.RechargeObserver;
import com.dzbook.recharge.order.RechargeParamBean;
import com.dzbook.recharge.order.SingleOrderActivity;
import com.dzbook.service.MarketDao;
import com.dzbook.service.RechargeParams;
import com.dzbook.utils.DBUtils;
import com.dzbook.utils.NetworkUtils;
import com.dzbook.utils.SpUtil;
import com.dzbook.utils.ThirdPartyLog;
import com.dzpay.recharge.api.UtilRecharge;
import com.dzpay.recharge.bean.RechargeAction;
import com.dzpay.recharge.bean.RechargeConstants;
import com.dzpay.recharge.bean.RechargeErrType;
import com.dzpay.recharge.bean.RechargeMsgResult;
import com.dzpay.recharge.bean.RechargeObserverConstants;
import com.dzpay.recharge.netbean.OrdersCommonBean;
import com.dzpay.recharge.netbean.SingleOrderBeanInfo;
import com.dzpay.recharge.netbean.SingleOrderPageBean;
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
 * SingleOrderPresenter
 *
 * @author lizz 2017/8/6.
 */

public class SingleOrderPresenter extends BaseOrderPresenter {

    private SingleOrderUI mUI;

    private Intent intent;

    private RechargeAction actionRefer;


    private SingleOrderBeanInfo paySingleOrderBeanInfo;

    /**
     * 初始化
     *
     * @param singleOrderUI singleOrderUI
     */
    public SingleOrderPresenter(SingleOrderUI singleOrderUI) {
        mUI = singleOrderUI;
        intent = ((Activity) (mUI.getContext())).getIntent();
    }

    /**
     * 获取参数信息
     */
    @SuppressWarnings("unchecked")
    public void getParamsInfo() {
        if (intent == null) {
            mUI.finishThisActivity(false);
            return;
        }

        params = (HashMap<String, String>) intent.getSerializableExtra(RechargeObserver.PARAMS);
        if (null == params) {
            return;
        }
        oprateFrom = params.get(RechargeMsgResult.OPERATE_FROM);
        partFrom = params.get(RechargeMsgResult.PART_FROM);

        if (null != observer) {
            this.actionRefer = observer.action;
            this.listener = observer.listener;
        }

    }

    /**
     * 获取订单信息
     */
    public void getOrderInfo() {

        try {
            String jsonStr = params.get(RechargeMsgResult.REQUEST_JSON);

            bookId = params.get(RechargeMsgResult.BOOK_ID);
            chapterId = params.get(RechargeMsgResult.CHAPTER_BASE_ID);

            paySingleOrderBeanInfo = new SingleOrderBeanInfo().parseJSON(new JSONObject(jsonStr));

            if (paySingleOrderBeanInfo != null && paySingleOrderBeanInfo.orderPage != null) {
                mUI.setViewOrderInfo(paySingleOrderBeanInfo);
                setRemainSum(paySingleOrderBeanInfo.orderPage);
            } else {
                mUI.showDataError();
            }

            setPreloadNum(paySingleOrderBeanInfo);
        } catch (Exception e) {
            ALog.printStack(e);
            mUI.showDataError();
        }
    }


    private void setRemainSum(SingleOrderPageBean beanInfo) {
        String remainSum = beanInfo.remain + "";
        String priceUnit = beanInfo.priceUnit;
        if (!TextUtils.isEmpty(remainSum) && !TextUtils.isEmpty(priceUnit)) {
            SpUtil.getinstance(mUI.getContext()).setUserRemain(remainSum, priceUnit);
        }
    }

    /**
     * 设置预加载数量
     *
     * @param beanInfo
     */
    private void setPreloadNum(SingleOrderBeanInfo beanInfo) {
        if (beanInfo != null) {
            SpUtil.getinstance(mUI.getContext()).setDzPayPreloadNum(beanInfo.preloadNum);
        }
    }


    /**
     * 去充值
     *
     * @param item        item
     * @param sourceWhere sourceWhere
     * @param isSelected  isSelected
     */
    public void toRecharge(final SingleOrderBeanInfo item, String sourceWhere, final boolean isSelected) {
        int ordinal = RechargeAction.NONE.ordinal();
        if (observer != null && observer.action != null) {
            ordinal = observer.action.ordinal();
        }

        Listener mListener = new Listener() {
            @Override
            public void onSuccess(int ordinal, HashMap<String, String> parm) {
                toPay(item, isSelected);
            }

            @Override
            public void onFail(HashMap<String, String> parm) {
            }
        };
        OrdersCommonBean commonBean = getCommonOrdersInfo(item);

        RechargeParamBean paramBean = new RechargeParamBean(mUI.getHostActivity(), mListener, ordinal, sourceWhere, params, trackId, mUI.getContext().getClass().getSimpleName(), LogConstants.RECHARGE_SOURCE_FROM_VALUE_1, commonBean);
        RechargeListPresenter.launch(paramBean);

        setUmengEventGoRecharge();
        dzLogGoRecahrge("1");

    }

    /**
     * 支付
     *
     * @param bean       bean
     * @param isSelected isSelected
     */
    public void toPay(final SingleOrderBeanInfo bean, boolean isSelected) {

        RechargeAction rechargeAction = RechargeAction.PAY;
        if (observer != null && observer.action != null) {
            rechargeAction = observer.action;
        }

        updatePayAutoOrder(isSelected);

        setUmengEventOrder();

        //打点
        dzLogOrder("1");

        mUI.showDialogByType(DialogConstants.TYPE_GET_DATA);

        //一定要重新传递，否则充值完成之后 会提示登录，登录完成之后userId可能换了
        params.put(RechargeMsgResult.USER_ID, SpUtil.getinstance(mUI.getContext()).getUserID());

        params.put(RechargeMsgResult.CONFIRM_PAY, "2");
        params.put(RechargeMsgResult.AUTO_PAY, isSelected ? "2" : "1");

        RechargeObserver obs = new RechargeObserver(mUI.getContext(), new Listener() {
            @Override
            public void onSuccess(int ordinal, HashMap<String, String> param) {
                listener.onStatusChange(RechargeConstants.DIALOG_SHOW, param);
                listener.onSuccess(ordinal, param);

                mUI.dissMissDialog();

                mUI.finishThisActivity(false);
                ThirdPartyLog.onEvent(mUI.getContext(), ThirdPartyLog.CASH_ORDER_SUCCESS);
            }

            @Override
            public void onFail(HashMap<String, String> param) {
                listener.onFail(param);
                mUI.dissMissDialog();
                mUI.finishThisActivity(false);
            }
        }, rechargeAction);
        UtilRecharge manager = UtilRecharge.getDefault();
        manager.execute(mUI.getContext(), params, RechargeAction.PAY.ordinal(), obs);

        // 发起付费意向
        MarketDao.markConfirmWilling(mUI.getContext(), bookId);
    }

    /**
     * 生成trackId
     */
    public void generateTrackd() {
        trackId = DzLog.generateTrackd();
    }

    /**
     * 取消弹窗操作
     *
     * @param cancelType       cancelType
     * @param moreDesc         moreDesc is
     * @param isNeedFinishAnim NeedFinishAnim
     */
    public void dialogCancel(int cancelType, String moreDesc, boolean isNeedFinishAnim) {

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
        mUI.finishThisActivity(isNeedFinishAnim);

        setCancelUmengEvent();

        dzLogCancel();
    }


    /**
     * 统计事件：自有单章订购页面取消
     */
    public void setCancelUmengEvent() {
        ThirdPartyLog.onEvent(mUI.getContext(), ThirdPartyLog.OWN_SINGLE_ORDER_PAGE_CANCLE);
    }

    /**
     * 统计事件：自有单章订购页面  展示总量
     */
    public void setUmengEventSum() {
        ThirdPartyLog.onEvent(mUI.getContext(), ThirdPartyLog.OWN_SINGLE_ORDER_PAGE);
    }

    /**
     * 统计事件：自有批量订购页面 确定
     */
    public void setUmengEventOrder() {
        ThirdPartyLog.onEventValueOldClick(mUI.getContext(), ThirdPartyLog.OWN_SINGLE_ORDER_PAGE_ORDER, "单章-确定", 1);
    }

    /**
     * 统计事件：自有批量订购页面 余额不足去充值
     */
    public void setUmengEventGoRecharge() {
        ThirdPartyLog.onEventValueOldClick(mUI.getContext(), ThirdPartyLog.OWN_SINGLE_ORDER_GO_RECHARGE, "单章-余额不足，去充值", 1);
    }

    private void dzLogSellClick() {
        HashMap<String, String> map = new HashMap<String, String>();
        map.put(LogConstants.KEY_ORDER_BID, bookId);
        map.put(LogConstants.KEY_ORDER_CID, chapterId);
        DzLog.getInstance().logClick(LogConstants.MODULE_DG_SELL, null, null, map, trackId);
    }


    public String getOprateFrom() {
        return oprateFrom;
    }

    public String getPartFrom() {
        return partFrom;
    }

    public RechargeAction getActionRefer() {
        return actionRefer;
    }

    /**
     * 销毁
     */
    public void onDestroy() {
        if (observer != null) {
            observer = null;
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

    private void updatePayAutoOrder(final boolean selected) {
        DzSchedulers.child(new Runnable() {
            @Override
            public void run() {
                //默认勾选下次订购不再提醒
                BookInfo newInfo = new BookInfo();
                newInfo.bookid = bookId;
                newInfo.payRemind = selected ? 2 : 1;
                DBUtils.updateBook(mUI.getContext(), newInfo);
            }
        });
    }

    /**
     * 批量订单
     */
    public void lotOrder() {

        if (observer != null && observer.context != null && observer.context instanceof BaseActivity) {

            //这里一定不能用当前的activity  应该需要关闭当前页面
            final BaseActivity activity = (BaseActivity) observer.context;

            dialogCancel(RechargeErrType.VIEW_BACK, "单章订购中选择批量订购，将单章流程结束，发起批量订购流程", false);

            BookLoader.getInstance().singleOrderToLotOrder(activity, bookId, chapterId);
        }


    }

    /**
     * 获取余额不足带去充值页面的订单信息
     *
     * @param bean bean
     * @return 余额不足带去充值页面的订单信息
     */
    public OrdersCommonBean getCommonOrdersInfo(SingleOrderBeanInfo bean) {
        SingleOrderPageBean pageBean = bean.orderPage;
        String orderName;
        if (pageBean.isSingleBook()) {
            orderName = pageBean.bookName;
        } else {
            orderName = pageBean.chapterName;
        }
        OrdersCommonBean bean1 = new OrdersCommonBean(pageBean.unit, pageBean.remain, pageBean.vouchers, pageBean.priceUnit, pageBean.vUnit, pageBean.needPay, pageBean.deduction, orderName, pageBean.author, pageBean.price, pageBean.disTips, pageBean.oldPrice, pageBean.vipTips);
        bean1.setChapterId(pageBean.chapterId);
        bean1.setBookId(bean.bookId);
        bean1.trackId = trackId;
        return bean1;
    }

    /**
     * 刷新页面
     */
    public void refreshUIPage() {

        if (observer != null && observer.context != null && observer.context instanceof BaseActivity) {

            //这里一定不能用当前的activity  应该需要关闭当前页面
            final BaseActivity activity = (BaseActivity) observer.context;

            dialogCancel(RechargeErrType.VIEW_BACK, "VIP开通成功，刷新当前页面", false);

            Observable.create(new ObservableOnSubscribe<LoadResult>() {

                @Override
                public void subscribe(ObservableEmitter<LoadResult> e) throws Exception {

                    BookInfo mBookInfo = DBUtils.findByBookId(activity, bookId);
                    CatalogInfo mCatalogInfo = DBUtils.getCatalog(activity, bookId, chapterId);

                    RechargeParams rechargeParams = new RechargeParams("1", mBookInfo);
                    rechargeParams.setOperateFrom(oprateFrom);
                    rechargeParams.setPartFrom(partFrom);

                    LoadResult result = BookLoader.getInstance().loadOneChapter(activity, mBookInfo, mCatalogInfo, rechargeParams);

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

                    if (value == null) {
                        ALog.dZz("LoadResult null");
                        activity.showNotNetDialog();
                        return;
                    }
                    if (value.isSuccess()) {
                        CatalogInfo info = DBUtils.getCatalog(activity, value.mChapter.bookid, value.mChapter.catalogid);
                        ReaderUtils.intoReader(activity, info, info.currentPos);
                    } else {
                        ALog.dZz("LoadResult:" + value.status);


                        if (value.status == LoadResult.STATUS_NET_WORK_NOT_USE || value.status == LoadResult.STATUS_NET_WORK_NOT_COOL || value.status == LoadResult.STATUS_ERROR && !NetworkUtils.getInstance().checkNet()) {
                            if (!TextUtils.isEmpty(value.getMessage(mUI.getContext())) && mUI.getHostActivity() != null) {
                                mUI.getHostActivity().showNotNetDialog();
                            }
                        } else {
                            ToastAlone.showShort(value.getMessage(activity));
                        }
                    }
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
    }


    /**
     * 前往 SingleOrderActivity
     *
     * @param context  context
     * @param observer observer
     * @param params   params
     */
    public static void launchSingleOrderPage(Context context, RechargeObserver observer, HashMap<String, String> params) {
        Intent intent = new Intent(context, SingleOrderActivity.class);
        SingleOrderPresenter.observer = observer;
        intent.putExtra(RechargeObserver.PARAMS, params);
        context.startActivity(intent);
        if (context instanceof Activity) {
            BaseActivity.showActivity(context);
        }
    }

    @Override
    public BaseActivity getHostActivity() {
        return mUI.getHostActivity();
    }
}
