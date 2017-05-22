package com.data.provider;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.Nullable;

/**
 * Created by qzzhu on 17-3-28.
 * 1. manifest set provider tag,
 *    'authorities' = TABLE_AUTHORITY,
 *    'name' = className
 * 2. UriMather.addUri and initial match
 * 3. extends SQLiteOpenHelper and create table
 */
public class MyContentProvider extends ContentProvider {

    private MySqlHelper sqlHelper;
    private static final UriMatcher uriMather = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        uriMather.addURI(AccountBean.TABLE_AUTHORITY,AccountBean.TABLE_NAME,1);
        uriMather.addURI(AccountBean.TABLE_AUTHORITY,AccountBean.TABLE_NAME2,2);//this could be more than one table
    }

    @Override
    public boolean onCreate() {
        sqlHelper = new MySqlHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        int code = uriMather.match(uri);
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        switch (code){
            case 1:
                qb.setTables(AccountBean.TABLE_NAME);
                break;
            case 2:
                qb.setTables(AccountBean.TABLE_NAME2);
                break;
        }
        SQLiteDatabase db = sqlHelper.getReadableDatabase();
        return qb.query(db, projection, selection, selectionArgs, null, null, sortOrder);
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        int code = uriMather.match(uri);
        String tableName ;
        Uri baseUri ;
        switch (code){
            case 1:
                tableName = AccountBean.TABLE_NAME;
                baseUri = AccountBean.URI_BASE;
                break;
            case 2:
                tableName = AccountBean.TABLE_NAME2;
                baseUri = AccountBean.URI_BASE2;
                break;
            default:
                tableName = AccountBean.TABLE_NAME;
                baseUri = AccountBean.URI_BASE;
        }
        SQLiteDatabase db = sqlHelper.getWritableDatabase();
        long rowId = db.insert(tableName, null, values);
        return ContentUris.withAppendedId(baseUri, rowId);
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        int code = uriMather.match(uri);
        String tableName ;
        switch (code){
            case 1:
                tableName = AccountBean.TABLE_NAME;
                break;
            case 2:
                tableName = AccountBean.TABLE_NAME2;
                break;
            default:
                tableName = AccountBean.TABLE_NAME;
        }
        return sqlHelper.getWritableDatabase().delete(tableName,selection,selectionArgs);
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        int code = uriMather.match(uri);
        String tableName ;
        switch (code){
            case 1:
                tableName = AccountBean.TABLE_NAME;
                break;
            case 2:
                tableName = AccountBean.TABLE_NAME2;
                break;
            default:
                tableName = AccountBean.TABLE_NAME;
        }
        return sqlHelper.getWritableDatabase().update(tableName,values,selection,selectionArgs);
    }
}