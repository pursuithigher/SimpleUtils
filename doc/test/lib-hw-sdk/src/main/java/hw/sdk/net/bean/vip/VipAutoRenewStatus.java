package hw.sdk.net.bean.vip;

import org.json.JSONObject;

import hw.sdk.net.bean.HwPublicBean;

/**
 * Vip自动续费状态获取
 *
 * @author lizz 2018/4/18.
 */

public class VipAutoRenewStatus extends HwPublicBean<VipAutoRenewStatus> {


    /**
     * 当前有效期，例如
     */
    public String deadLine;

    /**
     * 原价12元
     */
    public String oldCost;

    /**
     * 自动续订费用，例如：10元/月（原价12元）
     */
    public String autoCost;

    /**
     * 自动续订时间，2018/3/2
     */
    public String autoRenewTime;

    /**
     * 1.已开通连续包月 2:未开通
     */
    public int isOpen;

    /**
     * 开通提示语句
     */
    public String opendTips;

    @Override
    public VipAutoRenewStatus parseJSON(JSONObject jsonObj) {
        super.parseJSON(jsonObj);

        if (isSuccess()) {
            JSONObject dataJsonObj = jsonObj.optJSONObject("data");
            if (dataJsonObj == null) {
                return null;
            }
            deadLine = dataJsonObj.optString("deadLine");
            autoCost = dataJsonObj.optString("autoCost");
            oldCost = dataJsonObj.optString("oldCost");
            autoRenewTime = dataJsonObj.optString("autoRenewTime");
            isOpen = dataJsonObj.optInt("isOpen");
            opendTips = dataJsonObj.optString("opendTips");
        }

        return this;
    }

    /**
     * 是否存在自动续订vip开通成功数据
     *
     * @return 是否存在自动续订vip开通成功数据
     */
    public boolean isAutoOrderVipOpenSucess() {
        return isOpen == 1;
    }
}
