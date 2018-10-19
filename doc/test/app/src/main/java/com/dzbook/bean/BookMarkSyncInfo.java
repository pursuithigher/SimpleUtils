package com.dzbook.bean;

import com.dzbook.database.bean.BookMarkNew;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import hw.sdk.net.bean.HwPublicBean;

/**
 * BookMarkSyncInfo
 * @author admin
 */
public class BookMarkSyncInfo extends HwPublicBean<BookMarkSyncInfo> {
    /**
     * userId
     */
    public String userId;
    /**
     * time
     */
    public String time;
    /**
     * markList
     */
    public ArrayList<BookMarkNew> markList;

    @Override
    public BookMarkSyncInfo parseJSON(JSONObject jsonObj) {
        super.parseJSON(jsonObj);
        if (isSuccess()) {
            JSONObject data = jsonObj.optJSONObject("data");
            if (data != null) {
                time = data.optString("time");
                JSONArray array = data.optJSONArray("markList");
                if (array != null && array.length() > 0) {
                    markList = new ArrayList<>();
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject obj = array.optJSONObject(i);
                        BookMarkNew bean = new BookMarkNew();
                        bean = bean.parseJSON(obj);
                        if (bean != null) {
                            markList.add(bean);
                        }
                    }
                }
            }
        }
        return this;
    }
}
