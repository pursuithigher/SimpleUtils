package hw.sdk.net.bean.consume;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import hw.sdk.net.bean.HwPublicBean;

/**
 * 书籍消费记录汇总
 *
 * @author lizz 2018/4/18.
 */

public class ConsumeBookSumBeanInfo extends HwPublicBean<ConsumeBookSumBeanInfo> {

    /**
     * 消费记录
     */
    public List<ConsumeBookSumBean> consumeSumBeans;

    /**
     * 1：是（当前下发了数据）2：无数据下发
     */
    public int isExist;

    @Override
    public ConsumeBookSumBeanInfo parseJSON(JSONObject jsonObj) {
        super.parseJSON(jsonObj);

        JSONObject dataJsonObj = jsonObj.optJSONObject("data");
        if (dataJsonObj != null) {
            isExist = dataJsonObj.optInt("isExist");

            JSONArray array = dataJsonObj.optJSONArray("books");
            if (array != null) {
                this.consumeSumBeans = new ArrayList<ConsumeBookSumBean>();
                int length = array.length();
                for (int i = 0; i < length; i++) {
                    JSONObject obj = array.optJSONObject(i);
                    if (obj != null) {
                        ConsumeBookSumBean bean = new ConsumeBookSumBean();
                        this.consumeSumBeans.add(bean.parseJSON(obj));
                    }
                }
            }
        }

        return this;
    }

    /**
     * 是否存在数据
     *
     * @return 是否存在数据
     */
    public boolean isExistData() {
        return consumeSumBeans != null && consumeSumBeans.size() > 0;
    }


}
