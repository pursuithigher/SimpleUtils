package com.data.provider;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by qzzhu on 17-3-28.
 */

public class MySqlHelper extends SQLiteOpenHelper {
    public MySqlHelper(Context context) {
        super(context, "userinfo.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(AccountBean.FIELD_BUDDY_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
