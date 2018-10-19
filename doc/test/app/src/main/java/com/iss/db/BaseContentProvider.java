package com.iss.db;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.text.TextUtils;

import com.dzbook.lib.utils.ALog;
import com.iss.bean.BaseBean;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * 自定义ContentProvider
 *
 * @author zhenglk
 */
public abstract class BaseContentProvider extends ContentProvider {
    private static final String TAG = IssDbFactory.TAG;
    private static final String SCHEME = "content";
    protected SQLiteDatabase mDB;
    private String contentType = "vnd.android.cursor.dir/iss.db";

    private boolean isReady = false;
    //++ zhenglk 2015-07-15，sdk合作库使用 jar包时子类表明是包含 '$' 的，为解决此问题，添加模糊匹配流程。
    private ArrayList<String> dbTableNames = null;
    private HashMap<Uri, String> matchUriMap = new HashMap<Uri, String>();
    //-- zhenglk 2015-07-15，sdk合作库使用 jar包时子类表明是包含 '$' 的，为解决此问题，添加模糊匹配流程。

    @Override
    public boolean onCreate() {
        ALog.iLk(TAG + "ContentProvider onCreate -> " + this);
        synchronized (this) {
            isReady = false;
        }
        init();
        IssDbFactory issDbFactory = IssDbFactory.getInstance();
        DbConfig config = IssDbFactory.getInstance().getDBConfig();
        if (config == null) {
            throw new RuntimeException("db factory not register");
        }
        contentType = "vnd.android.cursor.dir/" + config.dbName;
        if (mDB == null) {
            mDB = issDbFactory.open();
        }
        synchronized (this) {
            isReady = true;
        }
        return true;
    }

    /**
     * init
     */
    public abstract void init();


    @Override
    public Uri insert(Uri uri, ContentValues values) {
        String tableName = getTableName(uri);
        long result = mDB.insert(tableName, null, values);
        if (result != -1) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return buildResultUri(tableName, result);
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        try {
            mDB.beginTransaction();
            String tableName = getTableName(uri);
            for (ContentValues value : values) {
                mDB.insert(tableName, null, value);
            }
            mDB.setTransactionSuccessful();
        } finally {
            mDB.endTransaction();
        }
        return values.length;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        String tableName = getTableName(uri);
        return mDB.query(tableName, projection, selection, selectionArgs, null, null, sortOrder);
    }

    @Override
    public String getType(Uri uri) {
        return contentType;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        String tableName = getTableName(uri);
        int result = mDB.delete(tableName, selection, selectionArgs);
        if (result != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return result;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        String tableName = getTableName(uri);
        int result = mDB.update(tableName, values, selection, selectionArgs);
        if (result != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return result;
    }

    private Uri buildResultUri(String tableName, long result) {
        final Uri.Builder builder = new Uri.Builder();
        DbConfig config = IssDbFactory.getInstance().getDBConfig();
        if (config == null) {
            throw new RuntimeException("db factory not register");
        }
        builder.scheme(SCHEME);
        builder.authority(config.authority);
        builder.path(tableName);
        builder.appendPath(String.valueOf(result));
        return builder.build();
    }

    /**
     * 获取数据库中 tableNames，获取失败就使用默认 tableNames
     *
     * @param defaultNames 默认 tableNames
     * @return tableNames
     */
    private ArrayList<String> getDBTableNames(ArrayList<String> defaultNames) {
        if (null == dbTableNames) {
            try {
                // 使用查找数据库方式，查找数据库的所有表的名称
                dbTableNames = new ArrayList<String>();
                Cursor cursor = mDB.rawQuery("select name from sqlite_master where type='table' order by name", null);
                while (cursor.moveToNext()) {
                    String tableName = cursor.getString(0);
                    dbTableNames.add(tableName);
                }
                cursor.close();
                ALog.iLk(TAG + "getDBTableNames:(db)" + dbTableNames);
            } catch (Exception e) {
                // 数据库命令不支持的时候，使用默认的类映射拿到所有表名称。
                ALog.printStackTrace(e);
                dbTableNames = defaultNames;
                ALog.iLk(TAG + "getDBTableNames:(def)" + dbTableNames);
            }
        }
        return dbTableNames;
    }

    /**
     * 根据 Uri 找到 tableName
     *
     * @param uri 查找表的 Uri
     * @return tableName
     */
    private synchronized String getTableName(Uri uri) {
        for (int i = 0; i < 2; i++) {
            DbConfig config = IssDbFactory.getInstance().getDBConfig();
            if (config == null) {
                throw new RuntimeException("db factory not register");
            }

            //++ zhenglk 2015-07-15，sdk合作库使用 jar包时子类表明是包含 '$' 的，为解决此问题，添加模糊匹配流程。
            // matchUriMap 缓存了 Uri 跟 tableName 的映射关系，已初始化的直接获取。
            String path = uri.getLastPathSegment();
            if (matchUriMap.containsKey(uri)) {
                String result = matchUriMap.get(uri);
                if (!isReady) {
                    try {
                        wait(80);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    continue;
                }
                if (TextUtils.isEmpty(result)) {
                    throw new IllegalArgumentException("*Unknown URI " + uri);
                }
            }

            // 完全匹配 path 方式获取 tableName
            ArrayList<String> tables = getDBTableNames(config.tableNameList);
            if (tables.contains(path)) {
                matchUriMap.put(uri, path);
                return path;
            }

            // 去掉 '$' 前的多余内容方式匹配查找 tableName
            int index = path.lastIndexOf('$');
            String shortPath = index > 0 ? path.substring(index + 1) : path;
            for (String tableName : tables) {
                index = tableName.lastIndexOf('$');
                String shortTableName = index > 0 ? tableName.substring(index + 1) : tableName;
                if (shortPath.equals(shortTableName)) {
                    matchUriMap.put(uri, tableName);
                    return tableName;
                }
            }
            //-- zhenglk 2015-07-15，sdk合作库使用 jar包时子类表明是包含 '$' 的，为解决此问题，添加模糊匹配流程。

            if (!isReady) {
                try {
                    wait(80);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        // 还没找到 tableName，那就挂了
        throw new IllegalArgumentException("#Unknown URI " + uri);
    }

    /**
     * Db Uri
     *
     * @param path path
     * @return uri
     */
    public static Uri buildUri(String path) {
        Uri.Builder builder;
        try {
            builder = new Uri.Builder();
            DbConfig config = IssDbFactory.getInstance().getDBConfig();
            if (config == null) {
                throw new RuntimeException("db factory not register");
            }
            builder.scheme(SCHEME);
            builder.authority(config.authority);
            builder.path(path);
        } catch (Exception e) {
            builder = new Uri.Builder();
            DbConfig config = IssDbFactory.getInstance().getDBConfig();
            if (config == null) {
                throw new RuntimeException("db factory not register");
            }
            builder.scheme(SCHEME);
            builder.authority(config.authority);
            builder.path(path);
        }

        return builder.build();
    }

    /**
     * Db Uri
     *
     * @param c BaseBean class
     * @return uri
     */
    public static Uri buildUri(Class<? extends BaseBean<?>> c) {
        final String tableName = TableUtil.getTableName(c);
        return buildUri(tableName);
    }

}
