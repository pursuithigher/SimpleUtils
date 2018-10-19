package com.dzbook;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import com.dzbook.event.EventBusUtils;
import com.dzbook.event.EventConstant;
import com.dzbook.lib.utils.ALog;
import com.dzbook.utils.ShareUtils;
import com.iss.app.BaseActivity;
import com.iss.view.common.ToastAlone;
import com.tencent.mm.opensdk.constants.ConstantsAPI;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

/**
 * 微信分享回调基础类
 *
 * @author lizhongzhong 2016/11/23.
 */
public abstract class BaseWXEnTryActivity extends BaseActivity implements IWXAPIEventHandler {

    /**
     * 分享成功
     */
    public static final int ERR_SUCCESS = 0;

    /**
     * 分享取消
     */
    public static final int ERR_CANCEL = ERR_SUCCESS + 1;

    /**
     * 分享失败
     */
    public static final int ERR_FAIL = ERR_CANCEL + 1;
    private IWXAPI api;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        api = WXAPIFactory.createWXAPI(AppConst.getApp(), getAppId(), true);
        api.registerApp(getAppId());

        //注意：
        //第三方开发者如果使用透明界面来实现WXEntryActivity，需要判断handleIntent的返回值，如果返回值为false，则说明入参不合法未被SDK处理，应finish当前透明界面，避免外部通过传递非法参数的Intent导致停留在透明界面，引起用户的疑惑
        try {
            boolean flag = api.handleIntent(getIntent(), this);
            if (!flag) {
                finish();
            }
        } catch (Exception e) {
            ALog.printStackTrace(e);
            finish();
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);

        //注意：
        //第三方开发者如果使用透明界面来实现WXEntryActivity，需要判断handleIntent的返回值，如果返回值为false，则说明入参不合法未被SDK处理，应finish当前透明界面，避免外部通过传递非法参数的Intent导致停留在透明界面，引起用户的疑惑
        try {
            boolean flag = api.handleIntent(getIntent(), this);
            if (!flag) {
                finish();
            }
        } catch (Exception e) {
            ALog.printStackTrace(e);
            finish();
        }
    }

    public String getAppId() {
        return ShareUtils.getWechateShareAppId();
    }


    @Override
    public void onReq(BaseReq baseReq) {

    }

    @Override
    public void onResp(BaseResp baseResp) {
        ALog.iWz("Wx_errorCode:" + baseResp.errCode + "___errorStr:" + baseResp.errStr);

        Intent intent = new Intent(AppConst.WECHATE_CALL_BACK_STATE_ACTION, null);
        switch (baseResp.errCode) {
            case BaseResp.ErrCode.ERR_OK:
                //分享成功
                EventBusUtils.sendMessage(EventConstant.FINISH_SHARE, "", null);
                Bundle bundle = new Bundle();
                bundle.putBoolean("success", true);
                if (baseResp.getType() == ConstantsAPI.COMMAND_SENDMESSAGE_TO_WX) {
                    shareSuccess(ShareUtils.WX_HY, false);
                }

                if (baseResp.getType() == ConstantsAPI.COMMAND_SENDAUTH) {
                    String code = ((SendAuth.Resp) baseResp).code;
                    ALog.iWz("wechat WXEnTryActivity SendAuth code:" + code);
                    intent.putExtra("code", code);
                }

                intent.putExtra("err_code", ERR_SUCCESS);
                sendBroadcast(intent);

                break;
            case BaseResp.ErrCode.ERR_USER_CANCEL:
                EventBusUtils.sendMessage(EventConstant.FINISH_SHARE, "", null);
                intent.putExtra("err_code", ERR_CANCEL);
                sendBroadcast(intent);

                //分享取消
                if (baseResp.getType() == ConstantsAPI.COMMAND_SENDMESSAGE_TO_WX) {
                    shareCancel(ShareUtils.WX_HY, false);
                }
                break;
            default:
                EventBusUtils.sendMessage(EventConstant.FINISH_SHARE, "", null);
                intent.putExtra("err_code", ERR_FAIL);
                sendBroadcast(intent);

                if (baseResp.getType() == ConstantsAPI.COMMAND_SENDMESSAGE_TO_WX) {
                    shareFail(ShareUtils.WX_HY, false);
                }
                ALog.iWz("微信分享：" + "errorCode:" + baseResp.errCode + "--errorDes:" + baseResp.errStr);
                break;
        }
        finish();
    }

    private void showToast(String content) {
        if (TextUtils.isEmpty(content)) {
            return;
        }
        ToastAlone.showShort(content);
    }

    @Override
    protected void initView() {

    }

    @Override
    protected void initData() {

    }

    @Override
    protected void setListener() {

    }

    @Override
    protected boolean isCustomPv() {
        return true;
    }
}
