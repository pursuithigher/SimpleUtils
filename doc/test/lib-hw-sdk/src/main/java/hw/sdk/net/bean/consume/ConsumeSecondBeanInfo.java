package hw.sdk.net.bean.consume;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import hw.sdk.net.bean.HwPublicBean;

/**
 * 消费记录汇总二级bean
 * @author lizz 2018/4/18.
 */

public class ConsumeSecondBeanInfo extends HwPublicBean<ConsumeSecondBeanInfo> {

    /**
     * 消费记录二级bean
     */
    public List<ConsumeSecondBean> consumeSecondBeans;

    /**
     * 1：是（当前下发了数据）2：无数据下发
     */
    public int isExist;

    @Override
    public ConsumeSecondBeanInfo parseJSON(JSONObject jsonObj) {
        super.parseJSON(jsonObj);
        JSONObject dataJsonObj = jsonObj.optJSONObject("data");
        if (dataJsonObj != null) {
            isExist = dataJsonObj.optInt("isExist");

            JSONArray array = dataJsonObj.optJSONArray("details");
            if (array != null) {
                this.consumeSecondBeans = new ArrayList<>();
                int length = array.length();
                for (int i = 0; i < length; i++) {
                    JSONObject obj = array.optJSONObject(i);
                    if (obj != null) {
                        ConsumeSecondBean bean = new ConsumeSecondBean();
                        this.consumeSecondBeans.add(bean.parseJSON(obj));
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
        return consumeSecondBeans != null && consumeSecondBeans.size() > 0;
    }
}
