package com.dzpay.recharge.logic.core;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import com.dzpay.recharge.activity.RechargeCoreActivity;
import com.dzpay.recharge.bean.RechargeAction;
import com.dzpay.recharge.bean.RechargeErrType;
import com.dzpay.recharge.bean.RechargeMsgResult;
import com.dzpay.recharge.bean.RechargeObserverConstants;
import com.dzpay.recharge.logic.DZReadAbstract;
import com.dzpay.recharge.utils.SystemUtils;

import java.util.HashMap;

/**
 * 充值下订单实现
 *
 * @author lizhongzhong 2015/8/21.
 */
public class RechargeImpl extends DZReadAbstract {

    /**
     * 构造
     *
     * @param context 上下文
     * @param param   参数
     * @param action  RechargeAction
     */
    public RechargeImpl(Context context, HashMap<String, String> param, RechargeAction action) {
        super(context, param, action);
    }


    @Override
    public void execute() {
        RechargeMsgResult result = new RechargeMsgResult(param);

        if (!SystemUtils.isNetworkConnected(context)) {
            result.relult = false;
            result.what = RechargeObserverConstants.FAIL;
            result.errType.setErrCode(action.actionCode(), RechargeErrType.NO_NETWORK_CONNECTION);
            nodifyObservers(result);
            return;
        }

        if (context instanceof Activity) {
            Intent intent = new Intent(context, RechargeCoreActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            RechargeCoreActivity.setObserver(observerList.get(0));
            intent.putExtra("params", param);
            context.startActivity(intent);
        } else {
            result.relult = false;
            result.what = RechargeObserverConstants.FAIL;
            result.errType.setErrCode(action.actionCode(), RechargeErrType.CONTEXT_REVERT_ERROR);
            nodifyObservers(result);
        }

    }
}
