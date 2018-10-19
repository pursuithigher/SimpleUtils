package com.dzbook.database.bean;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.text.TextUtils;

import com.dzbook.lib.utils.ALog;
import com.dzbook.r.c.AkDocInfo;
import com.dzbook.utils.SpUtil;
import com.iss.bean.BaseBean;
import com.iss.db.BaseContentProvider;
import com.iss.db.IssDbFactory;
import com.iss.db.TableColumn;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;

/**
 * 新书签
 *
 * @author liaowx
 */
public class BookMarkNew extends BaseBean<BookMarkNew> {
    /**
     * 书签
     */
    public static final int TYPE_MARK = 0x01;

    /**
     * 笔记
     */
    public static final int TYPE_NOTE = 0x02;

    /**
     * 已同步
     */
    public static final int OPERATE_NORMAL = 0x00;

    /**
     * 添加或者修改
     */
    public static final int OPERATE_ADD = 0x01;

    /**
     * 删除
     */
    public static final int OPERATE_DEL = 0x02;

    private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("##0.00%");

    private static Uri uriBookmark = null;

    /**
     * 类型
     */
    @TableColumn(type = TableColumn.Types.TEXT, isIndex = true)
    public int type;

    /**
     * operate
     */
    @TableColumn(type = TableColumn.Types.TEXT, isIndex = true)
    public int operate;

    /**
     * userId
     */
    @TableColumn(type = TableColumn.Types.TEXT, isIndex = true)
    public String userId;

    /**
     * bookId
     */
    @TableColumn(type = TableColumn.Types.TEXT, isIndex = true)
    public String bookId;

    /**
     * bookName
     */
    @TableColumn(type = TableColumn.Types.TEXT)
    public String bookName;

    /**
     * chapterId
     */
    @TableColumn(type = TableColumn.Types.TEXT, isIndex = true)
    public String chapterId;

    /**
     * chapterName
     */
    @TableColumn(type = TableColumn.Types.TEXT)
    public String chapterName;

    /**
     * startPos
     */
    @TableColumn(type = TableColumn.Types.TEXT)
    public long startPos = -1;

    /**
     * endPos
     */
    @TableColumn(type = TableColumn.Types.TEXT)
    public long endPos = -1;

    /**
     * percent
     */
    @TableColumn(type = TableColumn.Types.TEXT)
    public String percent;

    /**
     * showText
     */
    @TableColumn(type = TableColumn.Types.TEXT)
    public String showText;

    /**
     * noteText
     */
    @TableColumn(type = TableColumn.Types.TEXT)
    public String noteText;

    /**
     * updateTime
     */
    @TableColumn(type = TableColumn.Types.TEXT)
    public long updateTime;

    @Override
    public BookMarkNew parseJSON(JSONObject jsonObj) {
        if (jsonObj == null) {
            return null;
        }

        type = jsonObj.optInt("type");
        operate = jsonObj.optInt("operate");

        bookId = jsonObj.optString("bookId");
        bookName = jsonObj.optString("bookName");
        chapterId = jsonObj.optString("chapterId");
        chapterName = jsonObj.optString("chapterName");

        startPos = jsonObj.optLong("startPos");
        endPos = jsonObj.optLong("endPos");
        percent = jsonObj.optString("percent");
        showText = jsonObj.optString("showText");
        noteText = jsonObj.optString("noteText");

        updateTime = jsonObj.optLong("updateTime");
        return this;
    }

    @Override
    public JSONObject toJSON() {
        try {
            JSONObject obj = new JSONObject();
            obj.put("type", type);
            obj.put("operate", operate);
            //去掉重复的userId，在外层已经传了
//            obj.put("userId", userId);
            obj.put("bookId", bookId);
            obj.put("bookName", bookName);
            obj.put("chapterId", chapterId);
            obj.put("chapterName", chapterName);

            obj.put("startPos", startPos);
            obj.put("endPos", endPos);
            obj.put("percent", percent);
            obj.put("showText", showText);
            obj.put("noteText", noteText);

            obj.put("updateTime", updateTime);
            return obj;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public BookMarkNew cursorToBean(Cursor cursor) {
        try {
            type = cursor.getInt(cursor.getColumnIndex("type"));
            operate = cursor.getInt(cursor.getColumnIndex("operate"));
            userId = cursor.getString(cursor.getColumnIndex("userId"));

            bookId = cursor.getString(cursor.getColumnIndex("bookId"));
            bookName = cursor.getString(cursor.getColumnIndex("bookName"));
            chapterId = cursor.getString(cursor.getColumnIndex("chapterId"));
            chapterName = cursor.getString(cursor.getColumnIndex("chapterName"));

            startPos = cursor.getLong(cursor.getColumnIndex("startPos"));
            endPos = cursor.getLong(cursor.getColumnIndex("endPos"));
            percent = cursor.getString(cursor.getColumnIndex("percent"));
            showText = cursor.getString(cursor.getColumnIndex("showText"));
            noteText = cursor.getString(cursor.getColumnIndex("noteText"));

            updateTime = cursor.getLong(cursor.getColumnIndex("updateTime"));
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

        putContentValue(values, "type", type, -1);
        putContentValue(values, "operate", operate, -1);
        putContentValueNotNull(values, "userId", userId);

        putContentValue(values, "bookId", bookId);
        putContentValue(values, "bookName", bookName);
        putContentValue(values, "chapterId", chapterId);
        putContentValue(values, "chapterName", chapterName);

        putContentValue(values, "startPos", startPos, -1);
        putContentValue(values, "endPos", endPos, -1);
        putContentValue(values, "percent", percent);
        putContentValue(values, "showText", showText);
        putContentValue(values, "noteText", noteText);

        putContentValue(values, "updateTime", updateTime, -1);

        return values;
    }

    /**
     * createBookMark
     * @param context context
     * @param docInfo docInfo
     * @return BookMarkNew
     */
    public static BookMarkNew createBookMark(Context context, AkDocInfo docInfo) {
        if (docInfo == null) {
            return null;
        }
        BookMarkNew beanNew = new BookMarkNew();
        beanNew.type = BookMarkNew.TYPE_MARK;
        beanNew.userId = SpUtil.getinstance(context).getUserID();

        beanNew.bookId = docInfo.bookId;
        beanNew.bookName = docInfo.bookName;
        beanNew.chapterId = docInfo.chapterId;
        beanNew.chapterName = docInfo.chapterName;

        beanNew.startPos = docInfo.currentPos;
        beanNew.endPos = 0;
        beanNew.percent = getPercentStr(docInfo.percent / 100);
        if (null != docInfo.pageText) {
            beanNew.showText = docInfo.pageText.length() > 50 ? docInfo.pageText.substring(0, 50) : docInfo.pageText;
        }
        beanNew.noteText = "";
        beanNew.updateTime = System.currentTimeMillis();
        return beanNew;
    }

    private static String getPercentStr(float percent) {
        return DECIMAL_FORMAT.format(percent);
    }

    /**
     * BookMarkNew
     * @param context context
     * @param docInfo docInfo
     * @param startPos startPos
     * @param endPos endPos
     * @param showText showText
     * @param noteText noteText
     * @return BookMarkNew
     */
    public static BookMarkNew createBookNote(Context context, AkDocInfo docInfo, long startPos, long endPos, String showText, String noteText) {
        if (docInfo == null) {
            return null;
        }
        BookMarkNew bookNote = new BookMarkNew();
        bookNote.type = BookMarkNew.TYPE_NOTE;
        bookNote.userId = SpUtil.getinstance(context).getUserID();

        bookNote.bookId = docInfo.bookId;
        bookNote.bookName = docInfo.bookName;
        bookNote.chapterId = docInfo.chapterId;
        bookNote.chapterName = docInfo.chapterName;

        bookNote.startPos = startPos;
        bookNote.endPos = endPos;
        bookNote.percent = "";
        bookNote.showText = showText;
        bookNote.noteText = noteText;
        bookNote.updateTime = System.currentTimeMillis();
        return bookNote;
    }

    private static ContentResolver getContentResolver(Context context) {
        return context.getApplicationContext().getContentResolver();
    }

    private static void closeCursor(Cursor cursor) {
        if (null != cursor) {
            try {
                cursor.close();
            } catch (Exception e) {
                ALog.printStackTrace(e);
            }
        }
    }

    private static Uri uriBookmark() {
        if (null == uriBookmark) {
            uriBookmark = BaseContentProvider.buildUri(BookMarkNew.class);
        }
        return uriBookmark;
    }

    // -----------------------------------新书签----------------------------------------

    /**
     * isMarked
     * @param context context
     * @param bookMark bookMark
     * @return boolean
     */
    public static boolean isMarked(Context context, BookMarkNew bookMark) {
        if (bookMark == null) {
            return false;
        }
        Cursor cursor = null;
        try {
            ContentResolver mResolver = getContentResolver(context);
            cursor = mResolver.query(uriBookmark(), null, "userId=? and bookId=? and chapterId=? and startPos=? and type=? and operate!=?",
                    new String[]{bookMark.userId, bookMark.bookId, bookMark.chapterId, bookMark.startPos + "", bookMark.type + "", BookMarkNew.OPERATE_DEL + ""},
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                cursor.close();
                return true;
            }
        } catch (Exception e) {
            ALog.printStackTrace(e);
        } finally {
            closeCursor(cursor);
        }
        return false;
    }

    /**
     * addBookMark
     * @param context context
     * @param bookMark bookMark
     */
    public static void addBookMark(Context context, BookMarkNew bookMark) {
        if (bookMark == null) {
            return;
        }
        bookMark.operate = BookMarkNew.OPERATE_ADD;
        addMark(context, bookMark);
    }

    /**
     * deleteBookMark
     * @param context context
     * @param bookMark bookMark
     * @param realDel realDel
     */
    public static void deleteBookMark(Context context, BookMarkNew bookMark, boolean realDel) {
        if (bookMark == null) {
            return;
        }
        bookMark.operate = BookMarkNew.OPERATE_DEL;
        deleteMark(context, bookMark, realDel);
    }

    /**
     * getBookMarkByBook
     * @param context context
     * @param bookId bookId
     * @return ArrayList
     */
    public static ArrayList<BookMarkNew> getBookMarkByBook(Context context, String bookId) {
        return getMarkByBookAndType(context, bookId, BookMarkNew.TYPE_MARK + "");
    }

    /**
     * clearBookMark
     * @param context context
     * @param bookId bookId
     */
    public static void clearBookMark(Context context, String bookId) {
        clearMarkByBookAndType(context, bookId, BookMarkNew.TYPE_MARK + "");
    }


    // -----------------------------------新笔记----------------------------------------

    /**
     * addBookNote
     * @param context context
     * @param bookMark bookMark
     */
    public static void addBookNote(Context context, BookMarkNew bookMark) {
        if (bookMark == null) {
            return;
        }
        bookMark.operate = BookMarkNew.OPERATE_ADD;
        addMark(context, bookMark);
    }

    /**
     * deleteBookNote
     * @param context context
     * @param bookMark bookMark
     * @param realDel realDel
     */
    public static void deleteBookNote(Context context, BookMarkNew bookMark, boolean realDel) {
        if (bookMark == null) {
            return;
        }
        bookMark.operate = BookMarkNew.OPERATE_DEL;
        deleteMark(context, bookMark, realDel);
    }

    /**
     * clearBookNote
     * @param context context
     * @param bookId bookId
     */
    public static void clearBookNote(Context context, String bookId) {
        clearMarkByBookAndType(context, bookId, BookMarkNew.TYPE_NOTE + "");
    }

    /**
     * getBookNoteByChapter
     * @param context context
     * @param bookId bookId
     * @param chapterId chapterId
     * @return ArrayList
     */
    public static ArrayList<BookMarkNew> getBookNoteByChapter(Context context, String bookId, String chapterId) {
        return getMarkByChapterAndType(context, bookId, chapterId, BookMarkNew.TYPE_NOTE + "");
    }

    /**
     * getBookNoteByBook
     * @param context context
     * @param bookId bookId
     * @return ArrayList
     */
    public static ArrayList<BookMarkNew> getBookNoteByBook(Context context, String bookId) {
        return getMarkByBookAndType(context, bookId, BookMarkNew.TYPE_NOTE + "");
    }


    // -----------------------------------书签和笔记公用方法----------------------------------------

    /**
     * addMark
     * @param context context
     * @param bookMark bookMark
     */
    public static void addMark(Context context, BookMarkNew bookMark) {
        if (bookMark == null) {
            return;
        }
        int affectedRow = updateMark(context, bookMark);
        if (affectedRow > 0) {
            return;
        }
        ContentResolver mResolver = getContentResolver(context);
        mResolver.insert(uriBookmark(), bookMark.beanToValues());
    }

    private static int updateMark(Context context, BookMarkNew bookMark) {
        int affectedRow = 0;
        try {
            ContentResolver mResolver = getContentResolver(context);
            affectedRow = mResolver.update(uriBookmark(), bookMark.beanToValues(),
                    "userId=? and bookId=? and chapterId=? and startPos=? and endPos=? and type = ?",
                    new String[]{bookMark.userId, bookMark.bookId, bookMark.chapterId, bookMark.startPos + "", bookMark.endPos + "", bookMark.type + ""});
        } catch (Exception e) {
            ALog.printStackTrace(e);
        }
        return affectedRow;
    }

    /**
     * deleteMark
     * @param context context
     * @param bookMark bookMark
     * @param realDel realDel
     */
    public static void deleteMark(Context context, BookMarkNew bookMark, boolean realDel) {
        if (bookMark == null) {
            return;
        }
        if (realDel) {
            ContentResolver mResolver = getContentResolver(context);
            mResolver.delete(uriBookmark(),
                    "userId=? and bookId=? and chapterId=? and startPos=? and endPos=? and type = ?",
                    new String[]{bookMark.userId, bookMark.bookId, bookMark.chapterId, bookMark.startPos + "", bookMark.endPos + "", bookMark.type + ""});
        } else {
            updateMark(context, bookMark);
        }
    }

    private static void clearMarkByBookAndType(Context context, String bookId, String type) {
        if (TextUtils.isEmpty(bookId)) {
            return;
        }
        String userId = SpUtil.getinstance(context).getUserID();
        ContentValues contentValues = new ContentValues();
        contentValues.put("operate", BookMarkNew.OPERATE_DEL);
        ContentResolver mResolver = getContentResolver(context);
        mResolver.update(uriBookmark(), contentValues,
                "userId=? and bookId=? and type=?",
                new String[]{userId, bookId, type + ""});
    }

    /**
     * upateMarkByUserId
     * @param context context
     * @param userId userId
     */
    public static void upateMarkByUserId(Context context, String userId) {
        if (TextUtils.isEmpty(userId)) {
            return;
        }
        ContentValues contentValues = new ContentValues();
        contentValues.put("userId", userId);
        ContentResolver mResolver = getContentResolver(context);
        mResolver.update(uriBookmark(), contentValues,
                "userId=?", new String[]{""});
    }

    /**
     * getUnSyncMark
     * @param context context
     * @param userId userId
     * @return ArrayList
     */
    public static ArrayList<BookMarkNew> getUnSyncMark(Context context, String userId) {
        ContentResolver mResolver = getContentResolver(context);
        Cursor cursor = null;
        ArrayList<BookMarkNew> list = new ArrayList<>();
        try {
            cursor = mResolver.query(uriBookmark(), null,
                    "userId=? and operate!=?",
                    new String[]{userId, BookMarkNew.OPERATE_NORMAL + ""},
                    "updateTime desc");
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    BookMarkNew bean = new BookMarkNew();
                    bean.cursorToBean(cursor);
                    list.add(bean);
                }
            }

        } catch (Exception e) {
            ALog.printStackTrace(e);
        } finally {
            closeCursor(cursor);
        }
        return list;
    }

    private static ArrayList<BookMarkNew> getMarkByBookAndType(Context context, String bookId, String type) {
        if (TextUtils.isEmpty(bookId)) {
            return null;
        }
        String userId = SpUtil.getinstance(context).getUserID();
        ContentResolver mResolver = getContentResolver(context);
        Cursor cursor = null;
        ArrayList<BookMarkNew> list = new ArrayList<>();
        try {
            cursor = mResolver.query(uriBookmark(), null,
                    "userId=? and bookId=? and type=? and operate!=?",
                    new String[]{userId, bookId, type, BookMarkNew.OPERATE_DEL + ""},
                    "updateTime desc");
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    BookMarkNew bean = new BookMarkNew();
                    bean.cursorToBean(cursor);
                    list.add(bean);
                }
            }

        } catch (Exception e) {
            ALog.printStackTrace(e);
        } finally {
            closeCursor(cursor);
        }
        return list;
    }

    private static ArrayList<BookMarkNew> getMarkByChapterAndType(Context context, String bookId, String chapterId, String type) {
        if (TextUtils.isEmpty(bookId) || TextUtils.isEmpty(chapterId)) {
            return null;
        }
        String userId = SpUtil.getinstance(context).getUserID();
        ContentResolver mResolver = getContentResolver(context);
        Cursor cursor = null;
        ArrayList<BookMarkNew> list = new ArrayList<>();
        try {
            cursor = mResolver.query(uriBookmark(),
                    null,
                    "userId=? and bookId=? and chapterId=? and type=? and operate!=?",
                    new String[]{userId, bookId, chapterId, type, BookMarkNew.OPERATE_DEL + ""},
                    null);
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    BookMarkNew bean = new BookMarkNew();
                    bean.cursorToBean(cursor);
                    list.add(bean);
                }
            }

        } catch (Exception e) {
            ALog.printStackTrace(e);
        } finally {
            closeCursor(cursor);
        }
        return list;
    }
}
