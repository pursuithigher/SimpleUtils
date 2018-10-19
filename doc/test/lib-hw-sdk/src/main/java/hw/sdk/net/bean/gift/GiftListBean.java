package hw.sdk.net.bean.gift;

import org.json.JSONObject;

import hw.sdk.net.bean.HwPublicBean;

/**
 * 礼物兑换bean
 * @author KongXP 2018/4/25.
 */

public class GiftListBean extends HwPublicBean<GiftListBean> {
    /**
     * CONSTANT_ZERO
     */
    public static final int CONSTANT_ZERO = 0;
    /**
     * CONSTANT_ONE
     */
    public static final int CONSTANT_ONE = 1;
    /**
     * CONSTANT_TWO
     */
    public static final int CONSTANT_TWO = 2;

    /**
     * 图片名称
     */
    public String imgName;


    /**
     * 图片色值 十六机制 #888
     */
    public String imgColor;

    /**
     * 图片url
     */
    public String imgUrl;

    /**
     * 奖品标题
     */
    public String title;

    /**
     * 奖品描述
     */
    public String desc;

    /**
     * 时间
     */
    public String time;

    /**
     * 类型1 代金券 2 实物 3 书籍
     */
    public int type;

    /**
     * 状态0 未领取 1已领取
     */
    public int status;

    /**
     * 奖品id
     */
    public Long gid;

    @Override
    public GiftListBean parseJSON(JSONObject jsonObj) {
        if (jsonObj == null) {
            return null;
        }
        this.imgName = jsonObj.optString("imgName");
        this.imgColor = jsonObj.optString("imgColor");
        this.imgUrl = jsonObj.optString("imgUrl");
        this.title = jsonObj.optString("title");

        this.desc = jsonObj.optString("desc");
        this.time = jsonObj.optString("time");
        this.type = jsonObj.optInt("type",-1);
        this.status = jsonObj.optInt("status",-1);
        this.gid = jsonObj.optLong("gid");

        return this;
    }
}
