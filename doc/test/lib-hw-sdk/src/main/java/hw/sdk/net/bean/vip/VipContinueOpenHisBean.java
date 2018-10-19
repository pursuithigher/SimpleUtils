package hw.sdk.net.bean.vip;

import org.json.JSONObject;

import hw.sdk.net.bean.HwPublicBean;

/**
 * vip开通信息
 * @author lizz 2018/4/18.
 */

public class VipContinueOpenHisBean extends HwPublicBean<VipContinueOpenHisBean> {

    /**
     * 订单号
     */
    public String orderNum;

    /**
     * 会员开始时间
     */
    public String memberStartTime;

    /**
     * 会员结束时间
     */
    public String memberEndTime;

    /**
     * VIP开通描述，例如：VIP连续开通1个月
     */
    public String vipOpenDes;

    /**
     * 花费金额，例如：10元
     */
    public String cost;

    @Override
    public VipContinueOpenHisBean parseJSON(JSONObject jsonObj) {
        if (jsonObj == null) {
            return null;
        }
        orderNum = jsonObj.optString("orderNum");
        memberStartTime = jsonObj.optString("memberStartTime");
        memberEndTime = jsonObj.optString("memberEndTime");
        vipOpenDes = jsonObj.optString("vipOpenDes");
        cost = jsonObj.optString("cost");

        return this;
    }
}
