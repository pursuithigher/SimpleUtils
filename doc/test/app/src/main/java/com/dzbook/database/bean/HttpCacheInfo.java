package com.dzbook.database.bean;

import android.content.ContentValues;
import android.database.Cursor;
import android.text.TextUtils;

import com.dzbook.lib.utils.ALog;
import com.iss.bean.BaseBean;
import com.iss.db.IssDbFactory;
import com.iss.db.TableColumn;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * HttpCacheInfo
 */
public class HttpCacheInfo extends BaseBean<HttpCacheInfo> {

    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = -1108683065240592304L;

    /**
     * 调用的接口call
     */
    @TableColumn(type = TableColumn.Types.TEXT, isIndex = true)
    public String url;

    /**
     * 根据调用的接口call对应的缓存json内容
     */
    @TableColumn(type = TableColumn.Types.TEXT)
    public String response;

    /**
     * 缓存过期时间
     */
    @TableColumn(type = TableColumn.Types.TEXT)
    public String expires;

    /**
     * 缓存创建时间
     */
    @TableColumn(type = TableColumn.Types.TEXT)
    public String gmt_create;

    /**
     * 接口存放类型(1:短期类型,2:长期类型)\n 暂时不需要
     */
    @TableColumn(type = TableColumn.Types.TEXT)
    public String type;

    @Override
    public HttpCacheInfo parseJSON(JSONObject jsonObj) {
        return null;
    }

    @Override
    public JSONObject toJSON() {
        JSONObject object = null;
        try {
            object = new JSONObject();
            if (!TextUtils.isEmpty(url)) {
                object.put("url", url);
            }

            if (!TextUtils.isEmpty(response)) {
                object.put("response", response);
            }

            if (!TextUtils.isEmpty(expires)) {
                object.put("expires", expires);
            }

            if (!TextUtils.isEmpty(gmt_create)) {
                object.put("gmt_create", gmt_create);
            }
            if (!TextUtils.isEmpty(type)) {
                object.put("type", type);
            }

        } catch (JSONException e) {
            ALog.printStackTrace(e);
        }
        return object;
    }

    @Override
    public HttpCacheInfo cursorToBean(Cursor cursor) {
        try {
            url = cursor.getString(cursor.getColumnIndex("url"));
            response = cursor.getString(cursor.getColumnIndex("response"));
            gmt_create = cursor.getString(cursor.getColumnIndex("gmt_create"));
            expires = cursor.getString(cursor.getColumnIndex("expires"));
            type = cursor.getString(cursor.getColumnIndex("type"));
        } catch (IllegalStateException e) {
            try {
                IssDbFactory.getInstance().updateTable(this.getClass());
            } catch (Exception ee) {
            }
        }
        return this;
    }

    @Override
    public ContentValues beanToValues() {
        ContentValues values = new ContentValues();

        putContentValue(values, "url", url);
        putContentValue(values, "response", response);
        putContentValue(values, "gmt_create", gmt_create);
        putContentValue(values, "expires", expires);
        putContentValue(values, "type", type);

        return values;
    }

}
