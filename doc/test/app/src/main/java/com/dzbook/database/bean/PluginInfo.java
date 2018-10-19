package com.dzbook.database.bean;

import android.content.ContentValues;
import android.database.Cursor;

import com.iss.bean.BaseBean;
import com.iss.db.IssDbFactory;
import com.iss.db.TableColumn;

import org.json.JSONException;
import org.json.JSONObject;

import hw.sdk.net.bean.tts.PluginTtsInfo;

/**
 * PluginInfo
 * @author wxliao
\ */

public class PluginInfo extends BaseBean<PluginInfo> {
    /**
     * TTS_NAME
     */
    public static final String TTS_NAME = "ttsPlugin";
    /**
     * WPS_NAME
     */
    public static final String WPS_NAME = "wpsPlugin";

    /**
     * name
     */
    @TableColumn(type = TableColumn.Types.TEXT)
    public String name;

    /**
     * info
     */
    @TableColumn(type = TableColumn.Types.TEXT)
    public String info;

    @Override
    public PluginInfo parseJSON(JSONObject jsonObj) {
        return null;
    }

    @Override
    public JSONObject toJSON() {
        return null;
    }

    @Override
    public PluginInfo cursorToBean(Cursor cursor) {
        try {
            name = cursor.getString(cursor.getColumnIndex("name"));
            info = cursor.getString(cursor.getColumnIndex("info"));
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

        putContentValue(values, "name", name);
        putContentValue(values, "info", info);

        return values;
    }

    /**
     * getTtsInfo
     * @return PluginTtsInfo
     */
    public PluginTtsInfo getTtsInfo() {
        if (info != null) {
            try {
                JSONObject object = new JSONObject(info);
                PluginTtsInfo tts = new PluginTtsInfo();
                tts.parseJSON(object);
                return tts;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}
