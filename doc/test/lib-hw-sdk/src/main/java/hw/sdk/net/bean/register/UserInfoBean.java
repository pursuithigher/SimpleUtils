package hw.sdk.net.bean.register;

import org.json.JSONObject;

import hw.sdk.net.bean.HwPublicBean;

/**
 * 用户信息封装bean
 *
 * @author lizz 2018/4/19.
 */

public class UserInfoBean extends HwPublicBean<UserInfoBean> {

    /**
     * 剩余看点，例如：50
     */
    public String remain;

    /**
     * 剩余代金券，例如：270
     */
    public String vouchers;

    /**
     * 看点单位 例如：看点
     */
    public String priceUnit;
    /**
     * 代金券单位 例如：代金券
     */
    public String vUnit;
    /**
     * 1：是，2：否
     */
    public int isVip;

    /**
     * VIP截止时间，如果是isVip为1，则不能为空，
     */
    public String deadLine;

    /**
     * 阅读时长 例如：例如：90000ms
     */
    public long readingTime;
    /**
     * 注册时间
     */
    public String ctime;

    /**
     * 访问是否存在余额的接口url
     */
    public UserInfoBean() {

    }

    @Override
    public UserInfoBean parseJSON(JSONObject jsonObj) {
        if (jsonObj == null) {
            return null;
        }
        remain = jsonObj.optString("remain");
        vouchers = jsonObj.optString("vouchers");
        priceUnit = jsonObj.optString("priceUnit");
        ctime = jsonObj.optString("ctime");
        vUnit = jsonObj.optString("vUnit");
        isVip = jsonObj.optInt("isVip");
        deadLine = jsonObj.optString("deadLine");
        readingTime = jsonObj.optLong("readingTime", -1);

        return this;
    }


    @Override
    public String toString() {
        return "UserInfo{"
                +
                ", remain='"
                + remain
                + '\''
                +
                ", vouchers='"
                + vouchers
                + '\''
                +
                ", vip='"
                + isVip
                + '\''
                +
                ", deadLine='"
                + deadLine
                + '\''
                +
                ", priceUnit='"
                + priceUnit
                + '\''
                +
                ", deadLine='"
                + deadLine + '\''
                + ", oldFlagUrl='"
                +
                '}';
    }

    public boolean isVip() {
        return isVip == 1;
    }
}
