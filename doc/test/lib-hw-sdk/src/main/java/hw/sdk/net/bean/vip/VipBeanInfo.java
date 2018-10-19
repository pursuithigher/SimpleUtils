package hw.sdk.net.bean.vip;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import hw.sdk.net.bean.HwPublicBean;

/**
 * vip的bean
 * @author gavin
 */
public class VipBeanInfo extends HwPublicBean<VipBeanInfo> {

    /**
     * vip用户bean
     */
    public VipUserInfoBean vipUserInfoBeans;

    /**
     * vip 用户支付bean
     */
    public List<VipUserPayBean> vipUserPayBeans;

    /**
     * vip书籍bean
     */
    public List<VipBookInfo> vipBookInfoList;

    @Override
    public VipBeanInfo parseJSON(JSONObject jsonObj) {
        super.parseJSON(jsonObj);
        if (jsonObj == null) {
            return null;
        }

        if (isSuccess()) {
            try {
                JSONObject jsonPri = jsonObj.optJSONObject("data");

                JSONArray array = jsonPri.optJSONArray("section");
                if (array != null && array.length() > 1) {
                    vipBookInfoList = new ArrayList<>();
                    JSONObject jsonObject = array.optJSONObject(0);

                    JSONArray items = jsonObject.getJSONArray("items");
                    if (items != null && items.length() > 0) {
                        vipUserInfoBeans = new VipUserInfoBean().parseJSON(items.getJSONObject(0));
                    }

                    for (int i = 1; i < array.length(); i++) {
                        VipBookInfo bookInfo = new VipBookInfo().parseJSON(array.optJSONObject(i));
                        vipBookInfoList.add(bookInfo);
                    }
                }
                JSONArray jsonArray1 = jsonPri.optJSONArray("payList");
                if (jsonArray1 != null) {

                    vipUserPayBeans = new ArrayList<>();
                    for (int i = 0; i < jsonArray1.length(); i++) {
                        JSONObject item = jsonArray1.optJSONObject(i);
                        if (item != null) {
                            vipUserPayBeans.add(new VipUserPayBean().parseJSON(item));
                        }
                    }
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return this;
    }
}
