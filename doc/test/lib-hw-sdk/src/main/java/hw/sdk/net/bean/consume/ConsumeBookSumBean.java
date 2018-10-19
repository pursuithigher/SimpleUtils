package hw.sdk.net.bean.consume;

import org.json.JSONObject;

import hw.sdk.net.bean.HwPublicBean;


/**
 * 消费bean
 * @author lizz 2018/4/18.
 */

public class ConsumeBookSumBean extends HwPublicBean<ConsumeBookSumBean> {

    /**
     * 书籍图片地址
     */
    public String coverWap;
    /**
     * 书名
     */
    public String bookName;

    /**
     * 最新消费时间
     */
    public String lastConsumeTime;

    /**
     * 消费金额 例如：100看点+50代金券
     */
    public String consumeSum;

    /**
     * 二级页面请求id，如为空则不可点击
     */
    public String nextId;


    @Override
    public ConsumeBookSumBean parseJSON(JSONObject jsonObj) {
        if (jsonObj == null) {
            return null;
        }
        coverWap = jsonObj.optString("coverWap");
        bookName = jsonObj.optString("bookName");
        lastConsumeTime = jsonObj.optString("lastConsumeTime");
        consumeSum = jsonObj.optString("consumeSum");
        nextId = jsonObj.optString("nextId");

        return this;
    }
}
