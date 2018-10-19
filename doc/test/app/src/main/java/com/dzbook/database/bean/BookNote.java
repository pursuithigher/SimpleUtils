package com.dzbook.database.bean;

import android.content.ContentValues;
import android.database.Cursor;

import com.iss.bean.BaseBean;
import com.iss.db.IssDbFactory;
import com.iss.db.TableColumn;

import org.json.JSONObject;

/**
 * BookNote
 * @author wxliao on 17/12/5.
 */
public class BookNote extends BaseBean<BookNote> {

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


    @Override
    public BookNote parseJSON(JSONObject jsonObj) {
        return null;
    }

    @Override
    public JSONObject toJSON() {
        return null;
    }

    @Override
    public BookNote cursorToBean(Cursor cursor) {
        try {
            bookId = cursor.getString(cursor.getColumnIndex("bookId"));
            bookName = cursor.getString(cursor.getColumnIndex("bookName"));
            chapterId = cursor.getString(cursor.getColumnIndex("chapterId"));
            chapterName = cursor.getString(cursor.getColumnIndex("chapterName"));

            startPos = cursor.getLong(cursor.getColumnIndex("startPos"));
            endPos = cursor.getLong(cursor.getColumnIndex("endPos"));
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

        putContentValue(values, "bookId", bookId);
        putContentValue(values, "bookName", bookName);
        putContentValue(values, "chapterId", chapterId);
        putContentValue(values, "chapterName", chapterName);
        putContentValue(values, "startPos", startPos, -1);
        putContentValue(values, "endPos", endPos, -1);
        putContentValue(values, "showText", showText);
        putContentValue(values, "noteText", noteText);
        putContentValue(values, "updateTime", updateTime, -1);

        return values;
    }
}
