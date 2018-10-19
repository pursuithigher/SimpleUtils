package com.dzbook.database.bean;

import android.content.ContentValues;
import android.database.Cursor;

import com.iss.bean.BaseBean;
import com.iss.db.IssDbFactory;
import com.iss.db.TableColumn;

import org.json.JSONObject;

/**
 * 书签
 *
 * @author liaowx
 */
public class BookMark extends BaseBean<BookMark> {

    /**
     * id
     */
    private static final long serialVersionUID = 1L;

    /**
     * 书签类型：1：目录；2：书签；3：笔记；4：阅读尾页标记。
     */
    @TableColumn(type = TableColumn.Types.TEXT, isIndex = true)
    public int type = -1;

    /**
     * 图书id
     */
    @TableColumn(type = TableColumn.Types.TEXT, isIndex = true)
    public String bookId;

    /**
     * 图书名称
     */
    @TableColumn(type = TableColumn.Types.TEXT)
    public String bookName;

    /**
     * 章节id
     */
    @TableColumn(type = TableColumn.Types.TEXT, isIndex = true)
    public String chapterId;

    /**
     * 章节名称
     */
    @TableColumn(type = TableColumn.Types.TEXT)
    public String chapterName;

    /**
     * 路径
     */
    @TableColumn(type = TableColumn.Types.TEXT, isIndex = true)
    public String path;

    /**
     * 起点byte坐标
     */
    @TableColumn(type = TableColumn.Types.TEXT)
    public long startPos = -1;

    /**
     * 终点byte坐标
     */
    @TableColumn(type = TableColumn.Types.TEXT)
    public long endPos = -1;

    /**
     * 起点百分比
     */
    @TableColumn(type = TableColumn.Types.TEXT)
    public String percent;

    /**
     * 书签简介，展示串
     */
    @TableColumn(type = TableColumn.Types.TEXT)
    public String showText;

    /**
     * 书签时间
     */
    @TableColumn(type = TableColumn.Types.TEXT)
    public String markTime;

    @Override
    public BookMark parseJSON(JSONObject jsonObj) {
        return null;
    }

    @Override
    public JSONObject toJSON() {
        return null;
    }

    @Override
    public BookMark cursorToBean(Cursor cursor) {
        try {
            type = cursor.getInt(cursor.getColumnIndex("type"));
            bookId = cursor.getString(cursor.getColumnIndex("bookId"));
            bookName = cursor.getString(cursor.getColumnIndex("bookName"));
            chapterId = cursor.getString(cursor.getColumnIndex("chapterId"));
            chapterName = cursor.getString(cursor.getColumnIndex("chapterName"));
            path = cursor.getString(cursor.getColumnIndex("path"));
            startPos = cursor.getLong(cursor.getColumnIndex("startPos"));
            endPos = cursor.getLong(cursor.getColumnIndex("endPos"));
            percent = cursor.getString(cursor.getColumnIndex("percent"));
            showText = cursor.getString(cursor.getColumnIndex("showText"));
            markTime = cursor.getString(cursor.getColumnIndex("markTime"));
        } catch (IllegalStateException e) {
            try {
                IssDbFactory.getInstance().updateTable(this.getClass());
            } catch (Exception ignored) {
            }
        }
        return this;
    }

    @Override
    public ContentValues beanToValues() {
        ContentValues values = new ContentValues();

        putContentValue(values, "type", type, -1);
        putContentValue(values, "bookId", bookId);
        putContentValue(values, "bookName", bookName);
        putContentValue(values, "chapterId", chapterId);
        putContentValue(values, "chapterName", chapterName);
        putContentValue(values, "path", path);
        putContentValue(values, "startPos", startPos, -1);
        putContentValue(values, "endPos", endPos, -1);
        putContentValue(values, "percent", percent);
        putContentValue(values, "showText", showText);
        putContentValue(values, "markTime", markTime);

        return values;
    }

}
