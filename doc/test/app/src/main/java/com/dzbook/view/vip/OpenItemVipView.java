package com.dzbook.view.vip;

import android.app.Activity;
import android.content.Context;
import android.graphics.Paint;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dzbook.activity.vip.VipOpenSuccessActivity;
import com.dzbook.dialog.VipTipsDialog;
import com.dzbook.dialog.common.DialogLoading;
import com.dzbook.event.EventBusUtils;
import com.dzbook.event.EventConstant;
import com.dzbook.lib.net.DzSchedulers;
import com.dzbook.lib.utils.ALog;
import com.dzbook.lib.utils.CompatUtils;
import com.dzbook.loader.BookLoader;
import com.dzbook.mvp.presenter.MyVipPresenter;
import com.dzbook.pay.Listener;
import com.dzbook.recharge.RechargeObserver;
import com.dzbook.utils.HwLog;
import com.dzbook.utils.NetworkUtils;
import com.dzbook.utils.SpUtil;
import com.dzbook.utils.hw.LoginUtils;
import com.dzpay.recharge.api.UtilRecharge;
import com.dzpay.recharge.bean.RechargeAction;
import com.dzpay.recharge.bean.RechargeMsgResult;
import com.dzpay.recharge.netbean.VipOrdersResultBean;
import com.dzpay.recharge.utils.RechargeMsgUtils;
import com.dzpay.recharge.utils.RechargeWayUtils;
import com.ishugui.R;
import com.iss.app.BaseActivity;
import com.iss.view.common.ToastAlone;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import hw.sdk.net.bean.vip.VipUserInfoBean;
import hw.sdk.net.bean.vip.VipUserPayBean;


/**
 * vip开通列表
 *
 * @author gavin
 */
public class OpenItemVipView extends RelativeLayout {
    /**
     * 点击时间间隔最大等待1秒
     */
    private static final int MAX_CLICK_INTERVAL_TIME = 1500;
    private Context mContext;
    private TextView mTvtitle;
    private TextView mTvOpen;
    private TextView mTvMoney;
    private TextView mTvOriginMoney;
    private TextView mTvPrice;
    private TextView mTvGiveLogo;
    private TextView mTvGive;
    private MyVipPresenter myVipPresenter;
    private ImageView tips;
    private LinearLayout mVipRecommend;
    private View endLine;
    private long lastClickTime = 0;
    private VipUserPayBean info;
    private int position;

    /**
     * 构造
     *
     * @param context        context
     * @param myVipPresenter myVipPresenter
     */
    public OpenItemVipView(Context context, MyVipPresenter myVipPresenter) {
        this(context, null, myVipPresenter);
    }

    /**
     * 构造
     *
     * @param context        context
     * @param attrs          attrs
     * @param myVipPresenter myVipPresenter
     */
    public OpenItemVipView(Context context, AttributeSet attrs, MyVipPresenter myVipPresenter) {
        super(context, attrs);
        mContext = context;
        this.myVipPresenter = myVipPresenter;
        initView();
        initData();
        setListener();
    }

    private void setListener() {
        mTvOpen.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                vipOrder();
            }
        });
        tips.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                new VipTipsDialog(mContext).show();
            }
        });
//        setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                //                setBackgroundColor(CompatUtils.getColor(getContext(), R.color.color_20_FF742E));
//                myVipPresenter.setSelectedItem(position);
//            }
//        });
    }

    private void initData() {

    }

    private void initView() {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.view_vip_open_item, this);
        mTvtitle = view.findViewById(R.id.vip_open_title);
        mTvOpen = view.findViewById(R.id.tv_vip_open);
        mTvMoney = view.findViewById(R.id.tv_vip_money);
        mTvOriginMoney = view.findViewById(R.id.tv_vip_origin_money);
        mTvPrice = view.findViewById(R.id.vip_open_price_month);
        mTvGiveLogo = view.findViewById(R.id.tv_vip_give_logo);
        mTvGive = view.findViewById(R.id.tv_vip_give);
        tips = view.findViewById(R.id.open_vip_tips);
        mVipRecommend = view.findViewById(R.id.ll_vip_recommend);
        endLine = view.findViewById(R.id.line_end);
    }

    /**
     * 绑定数据
     *
     * @param payBean      info
     * @param userInfoBean userInfoBean
     * @param isDrawLine   isDrawLine
     * @param pos          pos
     * @param selectPos    selectPos
     */
    public void bindData(VipUserPayBean payBean, VipUserInfoBean userInfoBean, boolean isDrawLine, int pos, int selectPos) {
        this.info = payBean;
        this.position = pos;
        if (payBean != null) {
            mTvtitle.setText(payBean.title);
            if (userInfoBean != null && userInfoBean.isVip == 1) {
                mTvOpen.setText(getResources().getString(R.string.hw_vip_renew));
            } else {
                mTvOpen.setText(getResources().getString(R.string.hw_vip_open));
            }
            if (payBean.isAuto == 1) {
                tips.setVisibility(VISIBLE);
                mVipRecommend.setVisibility(VISIBLE);
            } else {
                if (tips.getVisibility() != GONE) {
                    tips.setVisibility(GONE);
                    mVipRecommend.setVisibility(GONE);
                }
            }
            if (selectPos != -1) {
                if (pos == selectPos) {
                    setBackgroundColor(CompatUtils.getColor(getContext(), R.color.color_20_FF742E));
                } else {
                    setBackgroundColor(CompatUtils.getColor(getContext(), R.color.white));
                }
            } else {
                if (pos == 0) {
                    setBackgroundColor(CompatUtils.getColor(getContext(), R.color.color_20_FF742E));
                } else {
                    setBackgroundColor(CompatUtils.getColor(getContext(), R.color.white));
                }
            }
            mTvMoney.setText("¥" + payBean.money);
            mTvOriginMoney.setText("¥" + payBean.originPrice + "");
            mTvOriginMoney.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
            mTvPrice.setText(payBean.recomIcon);

            if (payBean.award > 0) {
                mTvGiveLogo.setVisibility(VISIBLE);
                mTvGive.setVisibility(VISIBLE);
                mTvGive.setText(payBean.award + getResources().getString(R.string.hw_vip_money));
            } else {
                mTvGiveLogo.setVisibility(GONE);
                mTvGive.setVisibility(GONE);
            }
            int visibility = isDrawLine ? GONE : VISIBLE;
            if (endLine.getVisibility() != visibility) {
                endLine.setVisibility(visibility);
            }
        }
    }

    private VipOrdersResultBean getParams(String orderResultJson) {

        VipOrdersResultBean bean = null;
        try {
            bean = new VipOrdersResultBean();
            bean.parseJSON(new JSONObject(orderResultJson));
        } catch (Exception e) {
            ALog.printStackTrace(e);
        }

        return bean;
    }

    /**
     * 订购vip
     */
    public void vipOrder() {
        long thisClickTime = System.currentTimeMillis();
        if (thisClickTime - lastClickTime < MAX_CLICK_INTERVAL_TIME) {
            return;
        }
        lastClickTime = thisClickTime;

        if (info == null) {
            return;
        }
        if (!NetworkUtils.getInstance().checkNet()) {
            if (getContext() instanceof BaseActivity) {
                ((BaseActivity) getContext()).showNotNetDialog();
            }
        } else {
            LoginUtils.getInstance().forceLoginCheck(mContext, new LoginUtils.LoginCheckListener() {
                @Override
                public void loginComplete() {
                    try {
                        String id = info.id;
                        final int isRenew = info.isAuto;
                        final String name = info.title;
                        final String period = info.costMonth;
                        final String money = info.money;

                        HashMap<String, String> params = BookLoader.getInstance().getDzLoader().getRechargePayMap(mContext, null, id, RechargeWayUtils.getString(2));
                        if (isRenew == 1) {
                            params.put(RechargeMsgResult.IS_VIP_OPEN_RENEW, "1");
                        }

                        final DialogLoading mCustomDialog = new DialogLoading(mContext);
                        mCustomDialog.setShowMsg(mContext.getString(R.string.dialog_isLoading));
                        mCustomDialog.show();
                        RechargeObserver rechargeOrder = new RechargeObserver(mContext, new Listener() {
                            @Override
                            public void onSuccess(int ordinal, final HashMap<String, String> param) {
                                if (mCustomDialog.isShowing()) {
                                    mCustomDialog.dismiss();
                                }

                                final String json = param.get(RechargeMsgResult.VIP_PAY_RESULT_JSON);
                                isNotEmpty(json);

                                myVipPresenter.getVipInfo();

                                handleHwLog(json, name, period, money, isRenew);
                            }

                            @Override
                            public void onFail(HashMap<String, String> param) {
                                if (param == null) {
                                    return;
                                }
                                handleFail(param, mCustomDialog);
                            }

                            @Override
                            public void onStatusChange(int status, Map<String, String> parm) {
                                if (mCustomDialog != null && !((Activity) mContext).isFinishing()) {
                                    String statusChangeMsg = parm.get(RechargeMsgResult.STATUS_CHANGE_MSG);
                                    mCustomDialog.setShowMsg(statusChangeMsg);
                                }
                            }

                        }, RechargeAction.RECHARGE);

                        UtilRecharge manager = UtilRecharge.getDefault();
                        manager.execute(mContext, params, RechargeAction.RECHARGE.ordinal(), rechargeOrder);
                    } catch (Exception e) {
                        ALog.printStackTrace(e);
                    }
                }
            });
        }
    }

    private void handleFail(HashMap<String, String> param, DialogLoading mCustomDialog) {
        ToastAlone.showShort(RechargeMsgUtils.getRechargeMsg(param));
        if (mCustomDialog.isShowing()) {
            mCustomDialog.dismiss();
        }
    }

    private void handleHwLog(final String json, final String name, final String period, final String money, final int isRenew) {
        DzSchedulers.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    VipOrdersResultBean bean = new VipOrdersResultBean().parseJSON(new JSONObject(json));
                    HwLog.buyVIP(name, period, bean.startTime, bean.deadLine, money, isRenew == 1 ? "1" : "2");
                } catch (Exception e) {
                    ALog.printStackTrace(e);
                }
            }
        });
    }

    private void isNotEmpty(String json) {
        if (!TextUtils.isEmpty(json)) {
            VipOrdersResultBean resultBean = getParams(json);
            if (resultBean != null) {
                SpUtil.getinstance(mContext).setInt(SpUtil.DZ_IS_VIP, 1);
                SpUtil.getinstance(mContext).setString(SpUtil.DZ_VIP_EXPIRED_TIME, resultBean.deadLine);
                EventBusUtils.sendMessage(EventConstant.CODE_VIP_OPEN_SUCCESS_REFRESH_STATUS);
                VipOpenSuccessActivity.launch((Activity) mContext, resultBean);
            }
        }
    }

}
