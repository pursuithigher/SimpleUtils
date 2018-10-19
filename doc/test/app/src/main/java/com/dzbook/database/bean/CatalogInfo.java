package com.dzbook.database.bean;

import android.content.ContentValues;
import android.database.Cursor;
import android.text.TextUtils;

import com.iss.bean.BaseBean;
import com.iss.db.IssDbFactory;
import com.iss.db.TableColumn;

import org.json.JSONObject;

import java.io.File;

/**
 * 章节表 存放数据库bean
 *
 * @author lizhongzhong 2014-1-24
 */
public class CatalogInfo extends BaseBean<CatalogInfo> {

    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = 1L;


    /**
     * 章节从哪里打开的
     */
    public String openFrom;


    /**
     * 数据库中的_id
     */
    public String id;

    /**
     * 目录id
     */
    @TableColumn(type = TableColumn.Types.TEXT, isIndex = true)
    public String catalogid = "";

    /**
     * 所属book的id
     */
    @TableColumn(type = TableColumn.Types.TEXT)
    public String bookid = "";

    /**
     * 是否收费<br>
     * 0(收费) 1(不收费)
     */
    @TableColumn(type = TableColumn.Types.TEXT)
    public String ispay = "";

    /**
     * 位置<br>
     * 0(服务器) 1(基地)
     */
    @TableColumn(type = TableColumn.Types.TEXT)
    public String catalogfrom = "";

    /**
     * 是否已读<br>
     * 0(已读) 1(未读)
     */
    @TableColumn(type = TableColumn.Types.TEXT)
    public String isread = "";

    /**
     * 是否已下载<br>
     * 0(已下载) 1(未下载) -1(下载中) 2(缺章,未领取) 3(缺章，已领取) //4(删章)
     */
    @TableColumn(type = TableColumn.Types.TEXT)
    public String isdownload = "";

    /**
     * 章节名称
     */
    @TableColumn(type = TableColumn.Types.TEXT)
    public String catalogname = "";

    /**
     * 章节原始文件存放路径：
     */
    @TableColumn(type = TableColumn.Types.TEXT)
    public String path = "";

    /**
     * 当前阅读位置
     */
    @TableColumn(type = TableColumn.Types.TEXT)
    public long currentPos = -1;

    /**
     * 支付时间
     */
    @TableColumn(type = TableColumn.Types.TEXT)
    public String payTime;
    /**
     * 第一次下载时间
     */
    @TableColumn(type = TableColumn.Types.TEXT)
    public String dlTime;

    /**
     * 扩展信息。
     * <p>
     * add by lizhongzhong 2017-08-21
     * 1.为了批量下载 检查支付章节时候 传递检查支付描述不同
     */
    @TableColumn(type = TableColumn.Types.TEXT)
    public String extInfo = "";


    /**
     * 本地txt智能断章使用
     */
    @TableColumn(type = TableColumn.Types.TEXT)
    public long startPos;

    /**
     * 本地txt智能断章使用
     */
    @TableColumn(type = TableColumn.Types.TEXT)
    public long endPos;

    /**
     * 带有必要参数的构造，防止遗漏必要参数的设置。
     *
     * @param bookId    bookId
     * @param catalogId catalogId
     */
    public CatalogInfo(String bookId, String catalogId) {
        if (!TextUtils.isEmpty(bookId)) {
            this.bookid = bookId;
        }
        if (!TextUtils.isEmpty(catalogId)) {
            this.catalogid = catalogId;
        }
    }

    @Override
    public CatalogInfo parseJSON(JSONObject jsonObj) {
        return null;
    }

    /**
     * 是否收费<br>
     * 0(收费) 1(不收费)
     *
     * @param ispay 是否是付费章节
     */
    public void setIspay(String ispay) {
        this.ispay = ispay;
    }


    @Override
    public JSONObject toJSON() {
        return null;
    }

    @Override
    public CatalogInfo cursorToBean(Cursor cursor) {
        try {
            id = cursor.getString(cursor.getColumnIndex("_ID"));
            catalogid = cursor.getString(cursor.getColumnIndex("catalogid"));
            bookid = cursor.getString(cursor.getColumnIndex("bookid"));
            ispay = cursor.getString(cursor.getColumnIndex("ispay"));
            isread = cursor.getString(cursor.getColumnIndex("isread"));
            isdownload = cursor.getString(cursor.getColumnIndex("isdownload"));
            catalogname = cursor.getString(cursor.getColumnIndex("catalogname"));
            path = cursor.getString(cursor.getColumnIndex("path"));
            catalogfrom = cursor.getString(cursor.getColumnIndex("catalogfrom"));
            currentPos = cursor.getLong(cursor.getColumnIndex("currentPos"));
            payTime = cursor.getString(cursor.getColumnIndex("payTime"));
            dlTime = cursor.getString(cursor.getColumnIndex("dlTime"));
            extInfo = cursor.getString(cursor.getColumnIndex("extInfo"));

            startPos = cursor.getLong(cursor.getColumnIndex("startPos"));
            endPos = cursor.getLong(cursor.getColumnIndex("endPos"));
        } catch (IllegalStateException e) {
            try {
                IssDbFactory.getInstance().updateTable(this.getClass());
            } catch (Exception ignore) {
            }
        }

        return this;
    }

    @Override
    public ContentValues beanToValues() {
        ContentValues values = new ContentValues();

        putContentValue(values, "catalogid", catalogid);
        putContentValue(values, "bookid", bookid);
        putContentValue(values, "ispay", ispay);
        putContentValue(values, "isread", isread);
        putContentValue(values, "isdownload", isdownload);
        putContentValue(values, "catalogname", catalogname);
        putContentValue(values, "path", path);
        putContentValue(values, "catalogfrom", catalogfrom);
        putContentValue(values, "payTime", payTime);
        putContentValue(values, "dlTime", dlTime);
        putContentValue(values, "currentPos", currentPos, -1);
        putContentValue(values, "extInfo", extInfo);
        putContentValue(values, "startPos", startPos, 0);
        putContentValue(values, "endPos", endPos, 0);

        return values;
    }

    public boolean isAvailable() {
        return "0".equals(isdownload) && !TextUtils.isEmpty(path) && new File(path).exists();
    }

    public boolean isContentEmptyDeleted() {
        return TextUtils.equals(isdownload, "2") || TextUtils.equals(isdownload, "3") || TextUtils.equals(isdownload, "4");
    }

    /**
     * 章节是否可用
     *
     * @return boolean
     */
    public boolean isFileCanUse() {
        if (!"0".equals(isdownload) || TextUtils.isEmpty(path)) {
            return false;
        }
        File file = new File(path);
        return file.exists() && file.length() > 10;
    }

    public String getKey() {
        return bookid + "_" + catalogid;
    }

    @Override
    public String toString() {
        return super.toString() + "-" + getKey()
                + "[" + catalogname
                + "], isread=" + isread
                + ", ispay=" + ispay
                + ", from=" + catalogfrom
                + ", path=" + path
                + (TextUtils.isEmpty(extInfo) ? "" : (", extInfo" + extInfo));
    }


    /**
     * 缺内容未领取
     *
     * @return boolean
     */
    public boolean isContentEmptyAndReceiveAward() {
        return TextUtils.equals(isdownload, "2");
    }

    /**
     * 是否缺内容已经领取
     *
     * @return boolean
     */
    public boolean isContentEmptyAndAlreadyReceveAward() {
        return TextUtils.equals(isdownload, "3");
    }

    /**
     * 是否缺内容当前章节被删除
     *
     * @return boolean
     */
    public boolean isContentEmptyChapterDeleted() {
        return TextUtils.equals(isdownload, "4");
    }
}
