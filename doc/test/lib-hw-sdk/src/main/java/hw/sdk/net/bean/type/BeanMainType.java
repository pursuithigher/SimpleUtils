package hw.sdk.net.bean.type;


import android.text.TextUtils;

import com.dzbook.lib.utils.ALog;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import hw.sdk.net.bean.HwPublicBean;

/**
 * 分类一级页面的bean
 *
 * @author winzows on 2018/4/13
 */

public class BeanMainType extends HwPublicBean<BeanMainType> {
    /**
     * 第一行
     */
    public static final String TYPE_FIRST = "first";
    /**
     * 第二行
     */
    public static final String TYPE_SECOND = "second";
    /**
     * 第三行
     */
    public static final String TYPE_THREE = "three";

    /**
     * json 备份
     */
    public JSONObject jsonObj;

    /**
     * 右边的bean
     */
    private LinkedHashMap<BeanMainTypeLeft, ArrayList<BeanMainTypeRight>> beanMap;


    @Override
    public BeanMainType parseJSON(JSONObject object) {
        super.parseJSON(object);

        if (isSuccess()) {
            this.jsonObj = object;
            beanMap = new LinkedHashMap<>();
            JSONObject data = object.optJSONObject("data");
            if (data != null) {
                parseCategoryList(data.optJSONArray("categoryList"));
            }
        } else {
            ALog.dWz("BeanMainType error " + getRetMsg());
        }

        return this;
    }

    private void parseCategoryList(JSONArray array) {
        if (array != null && array.length() > 0) {
            this.beanMap = new LinkedHashMap<>();
            int length = array.length();
            for (int i = 0; i < length; i++) {
                JSONObject obj = array.optJSONObject(i);
                if (obj != null) {
                    JSONArray categoryDetail = obj.optJSONArray("categoryDetail");
                    if (categoryDetail != null && categoryDetail.length() > 0) {
                        BeanMainTypeLeft mainTypeLeft = new BeanMainTypeLeft().parseJSON(obj);

                        int categoryLength = categoryDetail.length();

                        ArrayList<BeanMainTypeRight> arrayList = new ArrayList<>();
                        for (int j = 0; j < categoryLength; j++) {
                            JSONObject categoryObject = categoryDetail.optJSONObject(j);
                            if (categoryObject != null) {
                                BeanMainTypeRight mainTypeRight = new BeanMainTypeRight().parseJSON(categoryObject);
                                arrayList.add(mainTypeRight);
                            }
                        }

                        if (!TextUtils.isEmpty(mainTypeLeft.categoryName) && arrayList.size() > 0) {
                            beanMap.put(mainTypeLeft, arrayList);
                        }
                    }
                }
            }
        }
    }

    /**
     * 获取目录列表
     *
     * @return 右边
     *
     */
    public ArrayList<BeanMainTypeLeft> getCategoryNameList() {
        ArrayList<BeanMainTypeLeft> list = new ArrayList<>();
        if (beanMap != null && beanMap.size() > 0) {
            Iterator iterator = beanMap.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry entry = (Map.Entry) iterator.next();
                BeanMainTypeLeft key = (BeanMainTypeLeft) entry.getKey();
                if (key != null && !TextUtils.isEmpty(key.categoryName)) {
                    list.add(key);
                }
            }
        }
        return list;
    }


    /**
     * 获取目录详情
     * @param categoryIndexBean 分类左边目录的bean
     * @return 获取目录详情
     */
    public ArrayList<BeanMainTypeRight> getCategoryDetailByCategoryName(BeanMainTypeLeft categoryIndexBean) {
        if (beanMap != null && beanMap.size() > 0) {
            return beanMap.get(categoryIndexBean);
        }
        return null;
    }
}
