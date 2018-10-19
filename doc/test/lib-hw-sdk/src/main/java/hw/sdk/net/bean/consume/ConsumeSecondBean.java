package hw.sdk.net.bean.consume;

import org.json.JSONObject;

import hw.sdk.net.bean.HwPublicBean;

/**
 * 消费记录汇总 二级bean
 * @author lizz 2018/4/18.
 */

public class ConsumeSecondBean extends HwPublicBean<ConsumeSecondBean> {

    /**
     * 消费描述信息
     */
    public String name;

    /**
     * 消费金额 例如：100看点+10代金券
     */
    public String consumeSum;

    /**
     * 消费时间
     */
    public String time;

    /**
     * 可能为空，如果为空则不展示下一级
     */
    public String consumeId;

    /**
     * 书籍id
     */
    public String bookId;

    @Override
    public ConsumeSecondBean parseJSON(JSONObject jsonObj) {
        if (jsonObj == null) {
            return null;
        }
        name = jsonObj.optString("name");
        consumeSum = jsonObj.optString("consumeSum");
        time = jsonObj.optString("time");
        consumeId = jsonObj.optString("consumeId");
        bookId = jsonObj.optString("bookId");

        return this;
    }
}
