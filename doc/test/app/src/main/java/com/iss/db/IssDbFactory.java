package com.iss.db;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

import com.dzbook.lib.utils.ALog;
import com.iss.bean.BaseBean;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * IssDbFactory
 */
@SuppressLint("NewApi")
public class IssDbFactory {
    protected static final String TAG = "dz_database: ";
    private static volatile IssDbFactory instance;
    private DbConfig mConfig;
    private SQLiteDatabase mSQLiteDB;

    private IssDbOpenHelper mDBOpenHelper;

    private final Context mContext;

    private IssDbFactory(Context context) {
        mContext = context;
    }

    /**
     * 初始化
     *
     * @param context  context
     * @param dbConfig dbConfig
     */
    public static void init(Context context, DbConfig dbConfig) {
        ALog.iLk(TAG + "IssDbFactory.register(" + context + ", " + dbConfig);
        if (instance == null) {
            synchronized (IssDbFactory.class) {
                if (instance == null) {
                    IssDbFactory ins = new IssDbFactory(context.getApplicationContext());
                    ins.setDBConfig(dbConfig);
                    instance = ins;
                }
            }
        }
    }

    public static IssDbFactory getInstance() {
        return instance;
    }

    public void setDBConfig(DbConfig dbConfig) {
        mConfig = dbConfig;
    }

    public DbConfig getDBConfig() {
        return mConfig;
    }

    /**
     * open db
     *
     * @return SQLiteDatabase
     */
    public SQLiteDatabase open() {
        if (mSQLiteDB == null) {
            if (mContext != null) {
                mDBOpenHelper = new IssDbOpenHelper(mContext, mConfig.dbName, null, mConfig.dbVersion);
                mSQLiteDB = mDBOpenHelper.getWritableDatabase();
            }

        }
        return mSQLiteDB;
    }

    /**
     * close db
     */
    public void close() {
        if (mDBOpenHelper != null) {
            mDBOpenHelper.close();
        }
    }

    /**
     * SQLiteOpenHelper
     */
    private final class IssDbOpenHelper extends SQLiteOpenHelper {

        IssDbOpenHelper(Context context, String name, CursorFactory factory, int version) {
            super(context, name, factory, version);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            for (Class<? extends BaseBean<?>> table : mConfig.tableList) {
                try {
                    for (String statment : TableUtil.getCreateStatments(table)) {
                        ALog.dLk(TAG + statment);
                        db.execSQL(statment);
                    }
                } catch (Throwable e) {
                    ALog.eLk(TAG + "Can't create table " + table.getSimpleName());
                }
            }
        }

        @Override
        public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            ALog.dLk(TAG + "onUpgrade: " + oldVersion + " >> " + newVersion);
            for (Class<? extends BaseBean<?>> table : mConfig.tableList) {
                if (newVersion > oldVersion) {
                    try {
                        updateDB(db, table);
                    } catch (Throwable e) {
                        db.execSQL("DROP TABLE IF EXISTS " + TableUtil.getTableName(table));
                        for (String statment : TableUtil.getCreateStatments(table)) {
                            ALog.dLk(TAG + statment);
                            db.execSQL(statment);
                        }
                    }
                } else {
                    db.execSQL("DROP TABLE IF EXISTS " + TableUtil.getTableName(table));
                }
            }
        }
    }

    private boolean tableIsExist(SQLiteDatabase db, String tableName) {
        Cursor cursor = null;
        try {
            cursor = db.rawQuery("SELECT COUNT(*) AS c FROM sqlite_master WHERE type ='table' AND name =?", new String[]{tableName});
            if (cursor != null && cursor.moveToNext()) {
                int count = cursor.getInt(0);
                if (count > 0) {
                    return true;
                }
            }

        } catch (Exception e) {
            ALog.printStackTrace(e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return false;
    }

    private void updateDB(SQLiteDatabase db, Class<? extends BaseBean<?>> table) {
        if (!tableIsExist(db, TableUtil.getTableName(table))) {

            for (String statment : TableUtil.getCreateStatments(table)) {
                ALog.dLk(TAG + statment);
                db.execSQL(statment);
            }
            return;
        }
        Map<String, Field> fieldMap = new HashMap<String, Field>();
        Map<String, Field> addMap = new HashMap<String, Field>();
        for (final Field f : table.getFields()) {
            f.setAccessible(true);
            final TableColumn tableColumnAnnotation = f.getAnnotation(TableColumn.class);
            if (tableColumnAnnotation != null) {
                fieldMap.put(f.getName(), f);
            }
        }
        Cursor cursor = db.rawQuery("select * from " + TableUtil.getTableName(table), null);
        // 如果数据库数据非空
        if (cursor != null && cursor.moveToFirst()) {
            for (int i = 0; i < cursor.getColumnCount(); i++) {
                String columnName = cursor.getColumnName(i);
                Field field = fieldMap.get(columnName);
                if (field != null) {
                    field.setAccessible(true);
                    fieldMap.remove(columnName);
                }
            }
            addMap.putAll(fieldMap);
            cursor.close();
        } else {
            db.execSQL("DROP TABLE IF EXISTS " + TableUtil.getTableName(table));
            for (String statment : TableUtil.getCreateStatments(table)) {
                ALog.dZz(TAG + statment);
                db.execSQL(statment);
            }
        }
        if (addMap.size() > 0) {
            List<Field> lists = mapTransitionList(addMap);
            for (Field f : lists) {
                f.setAccessible(true);
                final TableColumn tableColumnAnnotation = f.getAnnotation(TableColumn.class);
                String type = columnTypeToString(tableColumnAnnotation.type());
                String sql = "alter table " + TableUtil.getTableName(table) + " ADD " + f.getName() + type;
                ALog.dZz(TAG + sql);
                db.execSQL(sql);
            }

        }

    }

    /**
     * map convert
     *
     * @param map map
     * @param <T> map value type
     * @return list
     */
    private static <T> List<T> mapTransitionList(Map<String, T> map) {
        List<T> list = new ArrayList<T>();
        // 获得map的Iterator
        Iterator<Entry<String, T>> iter = map.entrySet().iterator();
        while (iter.hasNext()) {
            Entry<String, T> entry = iter.next();
            list.add(entry.getValue());
        }
        return list;
    }

    /**
     * type convert
     *
     * @param type tpe
     * @return str
     */
    private String columnTypeToString(TableColumn.Types type) {
        String str;
        if (type == TableColumn.Types.INTEGER) {
            str = " INTEGER";
        } else if (type == TableColumn.Types.BLOB) {
            str = " BLOB";
        } else if (type == TableColumn.Types.TEXT) {
            str = " TEXT";
        } else {
            str = " DATETIME";
        }
        return str;
    }

    /**
     * update
     *
     * @param table table
     */
    public void updateTable(Class<? extends BaseBean<?>> table) {
        try {
            updateDB(open(), table);
        } catch (Exception ee) {
        }
    }

}
