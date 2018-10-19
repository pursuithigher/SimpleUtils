package hw.sdk.net.bean.tms;

import com.dzbook.lib.utils.ALog;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import hw.sdk.net.bean.HwPublicBean;

/**
 * 查询协议的返回
 *
 * @author winzows on  2018/4/13
 */

public class BeanTmsRespQuery extends HwPublicBean<BeanTmsRespQuery> {
    /**
     * 网关错误码。调用成功时不带此参数
     */
    public Integer nspStatus;
    /**
     * 错误码
     */
    public Integer errorCode = -1;
    /**
     * 错误信息
     */
    public String errorMessage;
    /**
     * 签署过的协议
     */
    public ArrayList<BeanTmsRespSignInfo> signInfos;
    /**
     * temp jsonObj
     */
    private JSONObject jsonObj;


    @Override
    public BeanTmsRespQuery parseJSON(JSONObject object) {
        super.parseJSON(object);
        this.jsonObj = object;
        nspStatus = object.optInt("NSP_STATUS", -1);
        errorCode = object.optInt("errorCode", -1);
        errorMessage = object.optString("errorMessage");
        if (errorCode == 0) {
            parseSignInfo(object.optJSONArray("signInfo"));
        } else {
            ALog.dWz("BeanTmsRespQuery fail " + errorMessage);
        }
        return this;
    }

    /**
     * 解析签名signInfo
     * @param signInfo signInfo
     */
    private void parseSignInfo(JSONArray signInfo) {
        if (signInfo != null) {
            int signs = signInfo.length();
            if (signs > 0) {
                signInfos = new ArrayList<>();
                for (int i = 0; i < signs; i++) {
                    JSONObject jsonObject = signInfo.optJSONObject(i);
                    if (jsonObject != null) {
                        BeanTmsRespSignInfo sign = new BeanTmsRespSignInfo().parseJSON(jsonObject);
                        signInfos.add(sign);
                    }
                }
            }
        }
    }

    @Override
    public boolean isSuccess() {
        return errorCode == 0;
    }

    /**
     * 检验用户 之前有没有签署过协议。。。
     *
     * @return 之前有没有签署过协议
     */
    public boolean checkSignList() {
        return signInfos != null && signInfos.size() > 0;
    }

    /**
     * 当获取到的列表 不为空时 判断 当前用户 之前是否签署过这份协议
     *
     * @return 是否需要重新签署
     */
    public boolean needReSign() {
        if (!isSuccess()) {
            return false;
        }
        if (!checkSignList()) {
            return true;
        }

        for (BeanTmsRespSignInfo infos : signInfos) {
            if (infos != null) {
                if (infos.needSign || !infos.isAgree) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public String toString() {
        if (jsonObj != null) {
            return jsonObj.toString();
        }
        return super.toString();
    }
}
