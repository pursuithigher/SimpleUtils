package hw.sdk.net.bean.record;

import org.json.JSONObject;

import hw.sdk.net.bean.HwPublicBean;

/**
 * 充值记录
 * @author lizz 2018/4/18.
 */

public class RechargeRecordBean extends HwPublicBean<RechargeRecordBean> {

    /**
     * 描述 <br>
     * 例如：充值看点3000 赠送代金券1000
     */
    public String des;

    /**
     * 充值时间
     */
    public String reTime;

    /**
     * 充值金额
     */
    public String amount;

    @Override
    public RechargeRecordBean parseJSON(JSONObject jsonObj) {
        if (jsonObj == null) {
            return null;
        }
        this.des = jsonObj.optString("des");
        this.reTime = jsonObj.optString("reTime");
        this.amount = jsonObj.optString("amount");

        return this;
    }
}
