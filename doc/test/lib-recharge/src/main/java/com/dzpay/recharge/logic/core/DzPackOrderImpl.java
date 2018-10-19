package com.dzpay.recharge.logic.core;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.dzbook.lib.utils.JsonUtils;
import com.dzpay.recharge.bean.RechargeAction;
import com.dzpay.recharge.bean.RechargeErrType;
import com.dzpay.recharge.bean.RechargeMsgResult;
import com.dzpay.recharge.bean.RechargeObserverConstants;
import com.dzpay.recharge.logic.DZReadAbstract;
import com.dzpay.recharge.net.RechargeLibUtils;
import com.dzpay.recharge.utils.SystemUtils;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * 打包定价
 *
 * @author caimantang
 */
public class DzPackOrderImpl extends DZReadAbstract {
    String buyType;
    String commodityId;
    String originate;
    String bookIds;

    /**
     * 构造
     *
     * @param context 上下文
     * @param param   参数
     * @param action  操作
     */

    public DzPackOrderImpl(Context context, HashMap<String, String> param, RechargeAction action) {
        super(context, param, action);
        if (param != null) {
            if (param.containsKey(RechargeMsgResult.COMMODITY_ID)) {
                commodityId = param.get(RechargeMsgResult.COMMODITY_ID);
            }
            if (param.containsKey(RechargeMsgResult.ORIGINATE)) {
                originate = param.get(RechargeMsgResult.ORIGINATE);
            }
            if (param.containsKey(RechargeMsgResult.BOOKIDS)) {
                bookIds = param.get(RechargeMsgResult.BOOKIDS);
            }
            if (param.containsKey(RechargeMsgResult.BUY_TYPE)) {
                buyType = param.get(RechargeMsgResult.BUY_TYPE);
            }
        }
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

        try {
            if (TextUtils.isEmpty(buyType) || TextUtils.isEmpty(commodityId) || TextUtils.isEmpty(originate) || TextUtils.isEmpty(bookIds)) {
                result.relult = false;
                result.what = RechargeObserverConstants.FAIL;
                result.errType.setErrCode(action.actionCode(), RechargeErrType.PARAM_ERROR);
                nodifyObservers(result);
                return;
            }

            final ArrayList<String> bookIds1 = JsonUtils.jsonToArrayListByStr(bookIds);
            if (null == bookIds1 || bookIds1.size() <= 0) {
                result.relult = false;
                result.what = RechargeObserverConstants.FAIL;
                result.errType.setErrCode(action.actionCode(), RechargeErrType.PARAM_ERROR);
                nodifyObservers(result);
                return;
            }
            String moreDesc;
            String json = RechargeLibUtils.getInstance().getPackOrder(buyType, Integer.parseInt(commodityId), Integer.parseInt(originate), bookIds1);
            Log.d("cmt--PackOrder", json);
            if (!TextUtils.isEmpty(json)) {
                result.map.put(RechargeMsgResult.BOOKS_JSON, json);
                result.map.put(RechargeMsgResult.COMMODITY_ID, commodityId);
                result.map.put(RechargeMsgResult.BOOKIDS, bookIds);
                result.map.put(RechargeMsgResult.BUY_TYPE, buyType);
                result.map.put(RechargeMsgResult.ORIGINATE, originate);
                result.relult = true;
                result.what = RechargeObserverConstants.PACKBOOK;
                result.errType.setErrCode(action.actionCode(), RechargeErrType.SUCCESS);
                nodifyObservers(result);
                return;
            } else {
                moreDesc = "订购过程，检查支付异常 packOrder=null";
            }
            result.relult = true;
            result.what = RechargeObserverConstants.FAIL;
            result.errType.setErrCode(action.actionCode(), RechargeErrType.DATA_ERROR);
            if (!TextUtils.isEmpty(moreDesc)) {
                result.map.put(RechargeMsgResult.MORE_DESC, moreDesc);
            }
            nodifyObservers(result);
        } catch (JSONException e) {
            result.relult = false;
            result.what = RechargeObserverConstants.FAIL;
            result.errType.setErrCode(action.actionCode(), RechargeErrType.FAIL_REQUEST);
            result.exception = e;
            nodifyObservers(result);
        } catch (Exception e) {
            result.relult = false;
            result.what = RechargeObserverConstants.FAIL;
            result.errType.setErrCode(action.actionCode(), RechargeErrType.FAIL_REQUEST);
            result.exception = e;
            nodifyObservers(result);
        }
    }
}