package hw.sdk.net.bean.vip;

import org.json.JSONObject;

import hw.sdk.net.bean.HwPublicBean;

/**
 * 用户vip支付信息
 * @author gavin
 */
public class VipUserPayBean extends HwPublicBean<VipUserPayBean> {


    /**
     * 支付id
     */
    public String id;
    /**
     * 支付标题
     */
    public String title;
    /**
     * 现在价格
     */
    public String money;
    /**
     * 原价格
     */
    public int originPrice;


    /**
     * 渠道号
     */
    public String channelNo;
    /**
     * 每月价格
     */
    public String recomIcon;

    /**
     * 档位时间
     */
    public String costMonth;

    /**
     * 赠送代金券数量，award>0时有“送”角标
     */
    public int award;
    /**
     * 是否自动续订(0-不支持，1-支持)
     */
    public int isAuto = -1;


    @Override
    public VipUserPayBean parseJSON(JSONObject dataJsonObj) {
        id = dataJsonObj.optString("id");
        title = dataJsonObj.optString("title");
        money = dataJsonObj.optString("money");
        originPrice = dataJsonObj.optInt("originPrice");
        channelNo = dataJsonObj.optString("channelNo");
        recomIcon = dataJsonObj.optString("recomIcon");
        costMonth = dataJsonObj.optString("costMonth");
        award = dataJsonObj.optInt("award");
        isAuto = dataJsonObj.optInt("isAuto");
        return this;
    }

}
