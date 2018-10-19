package hw.sdk.net.bean.vouchers;

import org.json.JSONObject;

import hw.sdk.net.bean.HwPublicBean;

/**
 * 兑换券列表bean
 * @author KongXP 2018/4/25.
 */

public class VouchersListBean extends HwPublicBean<VouchersListBean> {

    /**
     * 获得描述
     */
    public String des;

    /**
     * 是否过期
     * 0-正常，1-快过期，2-已使用，3-已过期
     */
    public int status;

    /**
     * 有效期截止时间描述
     */
    public String endTime;

    /**
     * 总额
     */
    public String amount;

    /**
     * 剩余数量
     */
    public String remain;

    @Override
    public VouchersListBean parseJSON(JSONObject jsonObj) {
        if (jsonObj == null) {
            return null;
        }
        this.des = jsonObj.optString("des");
        this.status = jsonObj.optInt("status");
        this.endTime = jsonObj.optString("endTime");
        this.amount = jsonObj.optString("amount");
        this.remain = jsonObj.optString("remain");

        return this;
    }
}
