package com.dzbook.utils;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Looper;
import android.text.TextUtils;

import com.dzbook.database.bean.BookInfo;
import com.dzbook.database.bean.CatalogInfo;
import com.dzbook.database.bean.HttpCacheInfo;
import com.dzbook.database.bean.PluginInfo;
import com.dzbook.lib.utils.ALog;
import com.iss.db.BaseContentProvider;
import com.iss.db.IssDbFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

/**
 * 操作数据库的utils
 *
 * @author dllik 2013-11-23
 */
public class DBUtils {
    private static Uri uriBookBean = null;
    private static Uri uriCategory = null;

    private static Uri uriHttpCache = null;

    private static Uri uriPlugin = null;
    private static int defultCatalogInfoNumb = 5;
    private static long time = 0;

    private static Uri uriBookBean() {
        if (null == uriBookBean) {
            uriBookBean = BaseContentProvider.buildUri(BookInfo.class);
        }
        return uriBookBean;
    }

    private static Uri uriUcategory() {
        if (null == uriCategory) {
            uriCategory = BaseContentProvider.buildUri(CatalogInfo.class);
        }
        return uriCategory;
    }


    private static Uri uriHttpCache() {
        if (null == uriHttpCache) {
            uriHttpCache = BaseContentProvider.buildUri(HttpCacheInfo.class);
        }
        return uriHttpCache;
    }


    private static Uri uriPlugin() {
        if (null == uriPlugin) {
            uriPlugin = BaseContentProvider.buildUri(PluginInfo.class);
        }
        return uriPlugin;
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

    // -------------------------------------书集表-------------------------------------------------

    /**
     * insert书籍
     *
     * @param context context
     * @param bean    bean
     */
    public static void insertBook(Context context, BookInfo bean) {
        ContentResolver mResolver = getContentResolver(context);
        mResolver.insert(uriBookBean(), bean.beanToValues());
    }

    /**
     * 插入书籍
     *
     * @param context   context
     * @param bookInfos bookInfos
     */
    public static void insertBooks(Context context, List<BookInfo> bookInfos) {
        ContentResolver mResolver = getContentResolver(context);
        try {
            //插入之前先排重
            List<BookInfo> list = new ArrayList<>();
            for (int i = 0; i < bookInfos.size(); i++) {
                BookInfo bookInfo = bookInfos.get(i);
                if (null != bookInfo) {
                    BookInfo byBookId = findByBookId(context, bookInfo.bookid);
                    if (null == byBookId) {
                        //查不到 说明没有插入过
                        list.add(bookInfo);
                    }
                }
            }
            mResolver.bulkInsert(uriBookBean(), beansToValues(list));
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * 集合转换成ContentValues
     *
     * @param bookInfos bookInfos
     * @return contentsValues
     */
    public static ContentValues[] beansToValues(List<BookInfo> bookInfos) {
        ContentValues[] contentsValues = new ContentValues[bookInfos.size()];
        for (int i = 0; i < bookInfos.size(); i++) {
            contentsValues[i] = bookInfos.get(i).beanToValues();
        }
        return contentsValues;


    }

    /**
     * 删除书籍
     *
     * @param context context
     * @param bean    bean
     */
    public static void deleteBook(Context context, BookInfo bean) {
        deleteBookByBookId(context, bean.bookid);
    }

    /**
     * 根据bookId来删除书籍
     *
     * @param context context
     * @param bookId  bookId
     */
    public static void deleteBookByBookId(Context context, String bookId) {
        ContentResolver mResolver = getContentResolver(context);
        mResolver.delete(uriBookBean(), "bookid=?", new String[]{bookId});
    }


    /**
     * 删除多本书籍
     *
     * @param context   context
     * @param bookInfos bookInfos
     */
    public static void deleteMoreBook(Context context, List<BookInfo> bookInfos) {

        try {

            ContentResolver mResolver = getContentResolver(context);
            if (bookInfos != null && bookInfos.size() > 0) {
                String parameters = "";
                String[] strParam = new String[bookInfos.size()];
                int length = bookInfos.size();
                for (int i = 0; i < length; i++) {
                    BookInfo bean = bookInfos.get(i);

                    if (i == bookInfos.size() - 1) {
                        parameters += " bookid=? ";
                    } else {
                        parameters += " bookid=? or ";
                    }

                    strParam[i] = bean.bookid + "";
                }

                mResolver.delete(uriBookBean(), parameters, strParam);
            }

        } catch (Exception e) {
            ALog.printStackTrace(e);
        }

    }

    /**
     * 批量更新书籍
     *
     * @param context context
     * @param beans   beans   可能是多个线程同时去更新书籍 所以导致最后回滚时报错
     */
    public static void updateBooks(Context context, List<BookInfo> beans) {
        SQLiteDatabase db = IssDbFactory.getInstance().open();
        db.beginTransaction();//批量操作使用一个事务
        try {
            for (int i = 0; i < beans.size(); i++) {
                BookInfo bean = beans.get(i);
                ContentResolver mResolver = getContentResolver(context);
                int id = mResolver.update(uriBookBean(), bean.beanToValues(), "bookid=?", new String[]{bean.bookid});
            }
            db.setTransactionSuccessful();
        } catch (Exception e) {
            ALog.printStackTrace(e);
        } finally {
            try {
                db.endTransaction();
            } catch (Exception ignore) {
            }
        }
    }

    /**
     * 更新书籍
     *
     * @param context context
     * @param bean    bean
     */
    public static void updateBook(Context context, BookInfo bean) {

        try {
            ContentResolver mResolver = getContentResolver(context);
            int id = mResolver.update(uriBookBean(), bean.beanToValues(), "bookid=?", new String[]{bean.bookid});
        } catch (Exception e) {
            ALog.printStackTrace(e);
        }

    }

    /**
     * 查找所有选中了 自动订购下一章 选项的连载书籍
     *
     * @param context 上下文
     * @return 查找结果
     */
    public static ArrayList<BookInfo> findAllNetBooksByPayRemind(Context context) {
        ArrayList<BookInfo> list = new ArrayList<BookInfo>();
        ContentResolver mResolver = getContentResolver(context);
        Cursor cursor = null;
        try {
            cursor = mResolver.query(uriBookBean(), null, "isAddBook=2 and bookfrom=1 and bookstatus=2 and payRemind=2", null, null);
            if (null != cursor) {
                while (cursor.moveToNext()) {
                    BookInfo bean = new BookInfo();
                    bean.cursorToBean(cursor);
                    list.add(bean);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeCursor(cursor);
        }
        return list;
    }

    /**
     * 查找书架中的需要更新的书籍i
     *
     * @param context context
     * @return 查找结果
     */
    public static ArrayList<BookInfo> findBookShelfUpdateBooks(Context context) {
        ArrayList<BookInfo> list = new ArrayList<BookInfo>();

        ContentResolver mResolver = getContentResolver(context);
        Cursor cursor = null;
        try {
            int updateNum = SpUtil.getinstance(context).getUpdateBookNum();
            if (updateNum < 50) {
                updateNum = 50;
            }
            String booksSort = SpUtil.getinstance(context).getString(SpUtil.SHELF_BOOK_SORT, "0");
            if (TextUtils.equals(booksSort, "0")) {
                cursor = mResolver.query(uriBookBean(), null, "isAddBook=2 and bookfrom=1 ", null, " time DESC limit " + updateNum);
            } else {
                cursor = mResolver.query(uriBookBean(), null, "isAddBook=2 and bookfrom=1 ", null, "bookname COLLATE LOCALIZED");
            }
            BookInfo bean = null;
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    bean = new BookInfo();
                    bean.cursorToBean(cursor);
                    list.add(bean);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeCursor(cursor);
        }
        return list;
    }

    /**
     * 查找所有书籍
     *
     * @param context context
     * @return 查找结果
     */
    public static ArrayList<BookInfo> findAllBooks(Context context) {
        ArrayList<BookInfo> list = new ArrayList<BookInfo>();

        ContentResolver mResolver = getContentResolver(context);
        Cursor cursor = null;
        try {
            cursor = mResolver.query(uriBookBean(), null, "isAddBook=2", null, " time DESC");
            BookInfo bean = null;
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    bean = new BookInfo();
                    bean.cursorToBean(cursor);
                    list.add(bean);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeCursor(cursor);
        }
        return list;
    }

    /**
     * 模糊查询获取与key有关的所有图书表
     * bookname||bookauthor||书籍简介（book表中并没有存储简介，所以前面两个）
     * 默认排序
     *
     * @param context context
     * @param key     key
     * @return 查找结果
     */
    public static List<BookInfo> findSearchBooksByKey(Context context, String key) {
        //        ALog.eDongdz("findSearchBooksByKey:" + key);
        List<BookInfo> list = new ArrayList<BookInfo>();
        ContentResolver mResolver = getContentResolver(context);
        Cursor cursor = null;
        try {
            cursor = mResolver.query(uriBookBean(), null, "isAddBook=2 And bookname like ?", new String[]{"%" + key.replace(" ", "%") + "%"}, " time DESC");// OR author like ? , key
            BookInfo bean = null;
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    bean = new BookInfo();
                    bean.cursorToBean(cursor);
                    list.add(bean);
                }
            }
        } catch (Exception e) {
            ALog.printStackTrace(e);
        } finally {
            closeCursor(cursor);
        }
        //        ALog.eDongdz("findSearchBooksBy:list.size():" + list.size());
        return list;
    }

    /**
     * 查找所有书籍,根据名称排序
     *
     * @param context context
     * @return 查找结果
     */
    public static List<BookInfo> findAllBooksSortByName(Context context) {
        List<BookInfo> list = new ArrayList<BookInfo>();
        Cursor cursor = null;
        try {
            ContentResolver mResolver = getContentResolver(context);
            cursor = mResolver.query(uriBookBean(), null, "isAddBook=2", null, "bookname COLLATE LOCALIZED");
            BookInfo bean = null;
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    bean = new BookInfo();
                    bean.cursorToBean(cursor);
                    list.add(bean);
                }
            }
        } catch (Exception e) {
            ALog.printStack(e);
        } finally {
            closeCursor(cursor);
        }
        return list;
    }

    /**
     * 根据书籍id查找
     *
     * @param context context
     * @param bookId  bookId
     * @return 查找结果
     */
    public static BookInfo findByBookId(Context context, String bookId) {
        ContentResolver mResolver = getContentResolver(context);
        BookInfo bean = null;
        Cursor cursor = null;
        try {
            cursor = mResolver.query(uriBookBean(), null, "bookid=?", new String[]{bookId + ""}, null);
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    bean = new BookInfo();
                    bean.cursorToBean(cursor);
                }
            }
        } catch (Exception e) {
            ALog.printStackTrace(e);
        } finally {
            closeCursor(cursor);
        }
        return bean;
    }

    /**
     * 查找所有不在书架的书籍
     *
     * @param context context
     * @return 查找结果
     */
    public static ArrayList<BookInfo> findAllBooksNoAdd(Context context) {
        ArrayList<BookInfo> list = new ArrayList<BookInfo>();
        ContentResolver mResolver = getContentResolver(context);
        Cursor cursor = null;
        try {
            cursor = mResolver.query(uriBookBean(), null, "isAddBook!=2", null, " time DESC");
            BookInfo bean;
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    bean = new BookInfo();
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

    // -------------------------------------章节表-------------------------------------------------


    /**
     * 插入章节时需要先进行排重处理
     *
     * @param context
     * @param beans
     * @return
     */

    private static List<CatalogInfo> resetCatalogs(Context context, List<CatalogInfo> beans) {
        ALog.iLk(context.toString() + "--1--" + beans.size());
        List<CatalogInfo> list = new ArrayList<CatalogInfo>();
        if (!ListUtils.isEmpty(beans)) {
            //先批量查出
            if (beans.size() > defultCatalogInfoNumb) {
                ALog.iLk(context.toString() + "--2--" + beans.size());
                CatalogInfo catalogInfo = beans.get(0);
                HashSet<String> catalogSet = getCatalogSetByBookId(context, catalogInfo.bookid);
                if (null != catalogSet && catalogSet.size() > 0) {
                    for (int i = 0; i < beans.size(); i++) {
                        if (!catalogSet.contains(beans.get(i).catalogid)) {
                            list.add(beans.get(i));
                        }
                    }

                } else {
                    ALog.iLk(context.toString() + "--3--" + beans.size());
                    list.addAll(beans);
                }
            } else {
                ALog.iLk(context.toString() + "--4--" + beans.size());
                //一条一条查
                for (int i = 0; i < beans.size(); i++) {
                    CatalogInfo catalogInfo = beans.get(i);
                    CatalogInfo catalog = getCatalog(context, catalogInfo.bookid, catalogInfo.catalogid);
                    if (null == catalog) {
                        //为null说明没有查到 添加数据
                        list.add(catalogInfo);
                    }

                }
            }
        }
        ALog.iLk(context.toString() + "--5--" + list.size());
        return list;
    }

    /**
     * insert章节信息
     *
     * @param context context
     * @param bean    bean
     */
    public static void insertCatalog(Context context, CatalogInfo bean) {
        CatalogInfo catalog = getCatalog(context, bean.bookid, bean.catalogid);
        if (null == catalog) {
            //为null说明没有查到 添加数据
            ContentResolver mResolver = getContentResolver(context);
            mResolver.insert(uriUcategory(), bean.beanToValues());
        }
    }

    /**
     * 返回值true表示有章节更新 否则没有新的章节更新
     *
     * @param context context
     * @param beans   beans
     * @return true更新，false没更新
     */

    public static boolean insertLotCatalog(Context context, List<CatalogInfo> beans) {
        List<CatalogInfo> list = resetCatalogs(context, beans);
        if (!ListUtils.isEmpty(list)) {
            ContentValues[] contentValues = new ContentValues[list.size()];
            for (int i = 0; i < list.size(); i++) {
                contentValues[i] = list.get(i).beanToValues();
            }
            ContentResolver mResolver = getContentResolver(context);
            mResolver.bulkInsert(uriUcategory(), contentValues);
            return true;
        }
        return false;
    }

    /**
     * 更新章节信息
     *
     * @param context     context
     * @param catalogInfo catalogInfo
     */
    public static void updateCatalog(Context context, CatalogInfo catalogInfo) {
        try {
            ContentResolver mResolver = getContentResolver(context);
            mResolver.update(uriUcategory(), catalogInfo.beanToValues(), "bookid=? and catalogid=?", new String[]{catalogInfo.bookid, catalogInfo.catalogid});
            ALog.dWz("updateCatalog ");
        } catch (Exception e) {
            ALog.printStackTrace(e);
            ALog.printExceptionWz(e);
        }

    }

    /**
     * 获取书籍的最后一章
     *
     * @param context context
     * @param bookId  bookId
     * @return 最后一章信息
     */
    public static CatalogInfo getLastCatalog(Context context, String bookId) {
        ContentResolver mResolver = getContentResolver(context);
        Cursor cursor = null;
        try {
            cursor = mResolver.query(uriUcategory(), null, "bookid=? order by _ID desc limit 1", new String[]{bookId + ""}, null);
            if (null != cursor) {
                if (cursor.moveToNext()) {
                    CatalogInfo bean = new CatalogInfo(null, null);
                    bean.cursorToBean(cursor);
                    return bean;
                }
            }
        } catch (Exception e) {
            ALog.printStackTrace(e);
        } finally {
            closeCursor(cursor);
        }
        return null;
    }

    /**
     * 获取书籍的最后一章
     *
     * @param context context
     * @param bookId  bookId
     * @return 最后一章信息
     */
    public static CatalogInfo getFirstCatalog(Context context, String bookId) {
        ContentResolver mResolver = getContentResolver(context);
        CatalogInfo bean = null;
        Cursor cursor = null;
        try {
            cursor = mResolver.query(uriUcategory(), null, "bookid=? limit 1", new String[]{bookId + ""}, null);
            bean = null;
            if (cursor != null && cursor.moveToNext()) {
                bean = new CatalogInfo(null, null);
                bean.cursorToBean(cursor);
            }
        } catch (Exception e) {
            ALog.printStackTrace(e);
        } finally {
            closeCursor(cursor);
        }
        return bean;
    }

    /**
     * 根据bookid检查category
     *
     * @param context 上下文
     * @param bookId  图书id
     * @return 章节信息
     */
    public static ArrayList<CatalogInfo> getCatalogByBookId(Context context, String bookId) {
        ContentResolver mResolver = getContentResolver(context);
        ArrayList<CatalogInfo> list = new ArrayList<CatalogInfo>();
        Cursor cursor = null;
        try {
            cursor = mResolver.query(uriUcategory(), null, "bookid=?", new String[]{bookId + ""}, null);
            if (null != cursor) {
                while (cursor.moveToNext()) {
                    CatalogInfo bean = new CatalogInfo(null, null);
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

    /**
     * 根据bookid检查category
     *
     * @param context 上下文
     * @param bookId  图书id
     * @param cInfo   cInfo
     * @return 章节信息
     */
    public static ArrayList<CatalogInfo> getCatalogByBookIdByRange(Context context, String bookId, CatalogInfo cInfo) {
        ContentResolver mResolver = getContentResolver(context);
        ArrayList<CatalogInfo> list = new ArrayList<CatalogInfo>();

        // 前面的 50章 以及 当前章
        Cursor cursor1 = null;
        Cursor cursor2 = null;
        try {
            cursor1 = mResolver.query(uriUcategory(), null, "bookid=? and _ID<=?", new String[]{bookId + "", cInfo.id}, "_ID desc limit 51");
            if (null != cursor1) {
                if (cursor1.moveToLast()) {
                    do {
                        CatalogInfo bean = new CatalogInfo(null, null);
                        bean.cursorToBean(cursor1);
                        list.add(bean);
                    } while (cursor1.moveToPrevious());
                }
            }

            // 后面的50章
            cursor2 = mResolver.query(uriUcategory(), null, "bookid=? and _ID>?", new String[]{bookId + "", cInfo.id}, "_ID limit 50");
            if (null != cursor2) {
                while (cursor2.moveToNext()) {
                    CatalogInfo bean = new CatalogInfo(null, null);
                    bean.cursorToBean(cursor2);
                    list.add(bean);
                }
            }
        } catch (Exception e) {
            ALog.printStackTrace(e);
        } finally {
            closeCursor(cursor1);
            closeCursor(cursor2);
        }
        return list;
    }

    /**
     * 根据bookid检查category
     *
     * @param context 上下文
     * @param bookId  图书id
     * @return 查找到的章节信息
     */
    public static HashSet<String> getCatalogSetByBookId(Context context, String bookId) {
        ContentResolver mResolver = getContentResolver(context);
        HashSet<String> map = null;
        Cursor cursor = null;
        try {
            cursor = mResolver.query(uriUcategory(), null, "bookid=?", new String[]{bookId + ""}, null);
            if (null == cursor) {
                return null;
            }
            map = new HashSet<String>();
            while (cursor.moveToNext()) {
                CatalogInfo bean = new CatalogInfo(null, null);
                bean.cursorToBean(cursor);
                map.add(bean.catalogid);
            }
        } catch (Exception e) {
            ALog.printStackTrace(e);
        } finally {
            closeCursor(cursor);
        }
        return map;
    }

    /**
     * 根据bookid检查category isdownload为-1(正在下载的章节集合)
     *
     * @param context context
     * @param bookId  bookId
     * @return 查找到的章节信息
     */
    public static ArrayList<CatalogInfo> getCatalogLoadingByBookId(Context context, String bookId) {
        ContentResolver mResolver = getContentResolver(context);
        Cursor cursor = null;
        ArrayList<CatalogInfo> list = null;
        try {
            cursor = mResolver.query(uriUcategory(), null, "bookid=? and isdownload=?", new String[]{bookId + "", "-1"}, null);
            CatalogInfo bean = null;
            list = new ArrayList<CatalogInfo>();
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    bean = new CatalogInfo(null, null);
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

    /**
     * 根据bookid检查category isdownload为0(本地已标记为下载的章节)
     *
     * @param context context
     * @param bookId  bookId
     * @return 根据bookid检查category的列表
     */
    public static ArrayList<CatalogInfo> getCategLocalByBookId(Context context, String bookId) {
        ContentResolver mResolver = getContentResolver(context);
        ArrayList<CatalogInfo> list = null;
        Cursor cursor = null;
        try {
            cursor = mResolver.query(uriUcategory(), null, "bookid=? and isdownload=?", new String[]{bookId + "", "0"}, null);
            CatalogInfo bean = null;
            list = new ArrayList<CatalogInfo>();
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    bean = new CatalogInfo(null, null);
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

    /**
     * 根据bookid、catalogid检查category
     *
     * @param context   context
     * @param bookId    bookId
     * @param catalogId catalogId
     * @return 查找的章节信息
     */
    public static CatalogInfo getCatalog(Context context, String bookId, String catalogId) {
        ContentResolver mResolver = getContentResolver(context);
        CatalogInfo bean = null;
        Cursor cursor = null;
        try {
            cursor = mResolver.query(uriUcategory(), null, "bookid=? and catalogid=?", new String[]{bookId + "", catalogId + ""}, null);
            bean = null;
            if (cursor != null && cursor.moveToNext()) {
                bean = new CatalogInfo(null, null);
                bean.cursorToBean(cursor);
            }
        } catch (Exception e) {
            ALog.printStackTrace(e);
        } finally {
            closeCursor(cursor);
        }
        return bean;
    }

    /**
     * 获取上一章CatalogInfo
     *
     * @param context   context
     * @param bookid    bookid
     * @param catalogid catalogid
     * @return 上一章信息
     */
    public static CatalogInfo getPreCatalog(Context context, String bookid, String catalogid) {
        CatalogInfo catalog = getCatalog(context, bookid, catalogid);
        if (catalog != null) {
            return getPreCatalog(context, catalog);
        } else {
            return null;
        }
    }

    /**
     * 获取章节在本书是第几章
     *
     * @param context context
     * @param bid     bid
     * @param id      id
     * @return 章节数
     */
    public static int getCatalogNumb(Context context, String bid, String id) {
        int numb = 0;
        if (TextUtils.isEmpty(bid) || null == context) {
            return 0;
        }
        ContentResolver mResolver = getContentResolver(context);
        Cursor cursor = null;
        try {
            cursor = mResolver.query(uriUcategory(), new String[]{"COUNT(DISTINCT catalogid) as cid_numb"}, "bookid=? and _ID<=?", new String[]{bid + "", id + ""}, null);
            if (cursor != null && cursor.moveToNext()) {
                numb = cursor.getInt(cursor.getColumnIndex("cid_numb"));
            }
        } catch (Exception e) {
            ALog.printStackTrace(e);
        } finally {
            closeCursor(cursor);
        }
        return numb;
    }

    /**
     * 获取上一章CatalogInfo
     *
     * @param context     context
     * @param cataloginfo cataloginfo
     * @return 上一章信息
     */
    public static CatalogInfo getPreCatalog(Context context, CatalogInfo cataloginfo) {
        ContentResolver mResolver = getContentResolver(context);
        Cursor cursor = null;
        CatalogInfo bean = null;
        try {
            cursor = mResolver.query(uriUcategory(), null, "bookid=? and _ID<? order by _ID desc limit 1", new String[]{cataloginfo.bookid + "", cataloginfo.id + ""}, null);
            if (cursor != null && cursor.moveToNext()) {
                bean = new CatalogInfo(null, null);
                bean.cursorToBean(cursor);
            }
        } catch (Exception e) {
            ALog.printStackTrace(e);
        } finally {
            closeCursor(cursor);
        }
        return bean;
    }

    /**
     * 获取下一章
     *
     * @param context   context
     * @param bookid    bookid
     * @param catalogId catalogId
     * @return 下一章信息
     */
    public static CatalogInfo getNextCatalog(Context context, String bookid, String catalogId) {
        CatalogInfo catalog = getCatalog(context, bookid, catalogId);
        if (catalog != null) {
            return getNextCatalog(context, catalog);
        } else {
            return null;
        }
    }

    /**
     * 获取下一章
     *
     * @param context     context
     * @param catalogInfo catalogInfo
     * @return 下一章信息
     */
    public static CatalogInfo getNextCatalog(Context context, CatalogInfo catalogInfo) {
        ContentResolver mResolver = getContentResolver(context);
        Cursor cursor = null;
        CatalogInfo bean = null;
        try {
            cursor = mResolver.query(uriUcategory(), null, "bookid=? and _ID>? limit 1", new String[]{catalogInfo.bookid + "", catalogInfo.id + ""}, null);
            if (cursor != null && cursor.moveToNext()) {
                bean = new CatalogInfo(null, null);
                bean.cursorToBean(cursor);
            }
        } catch (Exception e) {
            ALog.printStackTrace(e);
        } finally {
            closeCursor(cursor);
        }
        return bean;
    }

    /**
     * 获取第一章CatalogInfo
     *
     * @param context context
     * @param bookId  bookId
     * @return 第一章信息
     */
    public static CatalogInfo getCatalogFirst(Context context, String bookId) {
        ContentResolver mResolver = getContentResolver(context);
        Cursor cursor = null;
        CatalogInfo bean = null;
        try {
            cursor = mResolver.query(uriUcategory(), null, "bookid=? order by catalogid asc limit 1", new String[]{bookId}, null);
            bean = null;
            if (cursor != null) {
                if (cursor.moveToNext()) {
                    bean = new CatalogInfo(null, null);
                    bean.cursorToBean(cursor);
                }
            }
        } catch (Exception e) {
            ALog.printStackTrace(e);
        } finally {
            closeCursor(cursor);
        }
        return bean;
    }

    /**
     * 获取当天阅读付费章节的数目
     *
     * @param context context
     * @param bookid  bookid
     * @return 当天阅读付费章节的数目
     */
    public static int getReadPayCatalogSizeByDate(Context context, String bookid) {

        String date = TimeUtils.getFormatDate("yyyy-MM-dd");
        String firstTime = date + " 00:00:00";
        String lastTime = date + " 23:59:59";
        ContentResolver mResolver = getContentResolver(context);
        int size = 0;
        Cursor cursor = null;
        try {
            cursor = mResolver.query(uriUcategory(), null, "payTime >=? and payTime <=? and ispay=? and isalreadypay=? and bookid=?", new String[]{firstTime, lastTime, "0", "0", bookid + ""}, null);
            if (cursor != null) {
                size = cursor.getCount();
            }
        } catch (Exception e) {
            ALog.printStackTrace(e);
        } finally {
            closeCursor(cursor);
        }
        return size;

    }

    // -------------------------------------自有专用查询开始-------------------------------------------------

    /**
     * 获取当前章节之后的第一个未下载的章节
     *
     * @param context context
     * @param current current
     * @return 第一个未下载的章节
     */
    public static CatalogInfo getFirstNoDownloadCatalog(Context context, CatalogInfo current) {
        ContentResolver mResolver = getContentResolver(context);
        Cursor cursor = null;
        CatalogInfo bean = null;
        try {
            cursor = mResolver.query(uriUcategory(), null, "bookid=? and isdownload=? and _ID>=? limit 1", new String[]{current.bookid + "", "1", current.id + ""}, null);
            if (cursor != null && cursor.moveToFirst()) {
                bean = new CatalogInfo(null, null);
                bean.cursorToBean(cursor);
            }
        } catch (Exception e) {
            ALog.printStackTrace(e);
        } finally {
            closeCursor(cursor);
        }
        return bean;
    }


    /**
     * 获取需要付费章节数
     *
     * @param context     context
     * @param catalogInfo catalogInfo
     * @param limit       limit
     * @return 结果
     */
    public static ArrayList<CatalogInfo> getDzPayNeedDownChapters(Context context, CatalogInfo catalogInfo, int limit) {
        ContentResolver mResolver = getContentResolver(context);
        ArrayList<CatalogInfo> list = null;
        Cursor cursor = null;
        try {
            cursor = mResolver.query(uriUcategory(), null, "bookid=? and _ID>=? order by _ID asc limit " + limit, new String[]{catalogInfo.bookid + "", catalogInfo.id + ""}, null);
            list = new ArrayList<>();
            if (null != cursor) {
                while (cursor.moveToNext()) {
                    CatalogInfo bean = new CatalogInfo(null, null);
                    bean.cursorToBean(cursor);
                    if (TextUtils.isEmpty(bean.isdownload) || TextUtils.equals(bean.isdownload, "1") || TextUtils.equals(bean.isdownload, "-1")) {
                        list.add(bean);
                    }
                }
            }
        } catch (Exception e) {
            ALog.printStackTrace(e);
        } finally {
            closeCursor(cursor);
        }
        return list;
    }


    /**
     * 根据指定的书籍id,章节id查询出指定章节id之后的所有章节存储到map中
     *
     * @param context     context
     * @param catalogInfo catalogInfo
     * @return 查询结果
     */
    public static Map<String, CatalogInfo> getMapCatalogByBookIdLimitCatalog(Context context, CatalogInfo catalogInfo) {
        Map<String, CatalogInfo> map = new HashMap<>(16);

        ContentResolver mResolver = getContentResolver(context);
        Cursor cursor = null;
        try {
            cursor = mResolver.query(uriUcategory(), null, "bookid=? and _ID>=? order by _ID asc", new String[]{catalogInfo.bookid + "", catalogInfo.id + ""}, null);
            if (null != cursor) {
                while (cursor.moveToNext()) {
                    CatalogInfo bean = new CatalogInfo(null, null);
                    bean.cursorToBean(cursor);
                    map.put(bean.catalogid, bean);
                }
            }
        } catch (Exception e) {
            ALog.printStackTrace(e);
        } finally {
            closeCursor(cursor);
        }
        return map;
    }


    // -------------------------------------自有专用查询结束-------------------------------------------------


    // -------------------------------------其他-------------------------------------------------

    /**
     * 删除目录
     *
     * @param context context
     * @param bookid  bookid
     */
    public static void deleteCatalogByBoodId(Context context, String bookid) {
        ContentResolver mResolver = getContentResolver(context);
        mResolver.delete(uriUcategory(), "bookid=?", new String[]{bookid + ""});
    }

    /**
     * 删除章节
     *
     * @param context   context
     * @param bookId    bookId
     * @param catalogId catalogId
     */
    public static void deleteCatalog(Context context, String bookId, String catalogId) {
        ContentResolver mResolver = getContentResolver(context);
        mResolver.delete(uriUcategory(), "bookid=? and catalogid=?", new String[]{bookId + "", catalogId + ""});
    }


    /**
     * 删除多本书籍的目录
     *
     * @param context   context
     * @param bookInfos bookInfos
     */
    public static void deleteMoreBookCatalogByBoodIds(Context context, List<BookInfo> bookInfos) {

        try {

            ContentResolver mResolver = getContentResolver(context);

            if (bookInfos != null && bookInfos.size() > 0) {
                String parameters = "";
                String[] strParam = new String[bookInfos.size()];

                for (int i = 0; i < bookInfos.size(); i++) {
                    BookInfo bean = bookInfos.get(i);

                    if (i == bookInfos.size() - 1) {
                        parameters += " bookid=? ";
                    } else {
                        parameters += " bookid=? or ";
                    }

                    strParam[i] = bean.bookid + "";
                }

                mResolver.delete(uriUcategory(), parameters, strParam);
            }

        } catch (Exception e) {
            ALog.printStackTrace(e);
        }


    }

    // -----------------------------------新书签----------------------------------------


    //    // -------------------------------------书签-------------------------------------------------
    //    public static ArrayList<BookMark> getBookMarkByBook(Context context, String bookId, int type) {
    //        ContentResolver mResolver = getContentResolver(context);
    //        ArrayList<BookMark> beanList = null;
    //        Cursor cursor = null;
    //        try {
    //            cursor = mResolver.query(URI_BOOKMARK(), null, "bookId = ? and type = ?",
    //                    new String[]{bookId + "", type + ""}, "_id desc");
    //            beanList = new ArrayList<BookMark>();
    //            if (cursor != null) {
    //                while (cursor.moveToNext()) {
    //                    BookMark bean = new BookMark();
    //                    bean.cursorToBean(cursor);
    //                    beanList.add(bean);
    //                }
    //            }
    //        } catch (Exception e) {
    //            ALog.printStackTrace(e);
    //        } finally {
    //            closeCursor(cursor);
    //        }
    //        return beanList;
    //    }
    //
    //    public static BookMark getEndBookMark(Context context, BookMark bean) {
    //        if (null == bean) {
    //            return null;
    //        }
    //        ContentResolver mResolver = getContentResolver(context);
    //        Cursor cursor = null;
    //        try {
    //            // 尾页偏移（endPos：存储的是字号dp值）
    //            if (4 == bean.type) {
    //                cursor = mResolver.query(URI_BOOKMARK(), null, "path=? and endPos=? and type=?", new String[]{
    //                        bean.path + "", bean.endPos + "", bean.type + ""}, null);
    //                if (cursor != null && cursor.moveToFirst()) {
    //                    bean.cursorToBean(cursor);
    //                    return bean;
    //                }
    //            }
    //        } catch (Exception e) {
    //            ALog.printStackTrace(e);
    //        } finally {
    //            closeCursor(cursor);
    //        }
    //        return null;
    //    }
    //
    //
    //    public static void setEndBookMark(Context context, BookMark bean) {
    //        if (null == bean) {
    //            return;
    //        }
    //        ContentResolver mResolver = getContentResolver(context);
    //        Cursor cursor = null;
    //        try {
    //            // 尾页偏移（endPos：存储的是字号dp值）
    //            if (4 == bean.type) {
    //                cursor = mResolver.query(URI_BOOKMARK(), null, "path=? and endPos=? and type=?", new String[]{
    //                        bean.path + "", bean.endPos + "", bean.type + ""}, null);
    //                if (cursor != null && cursor.moveToFirst()) {
    //                    mResolver.delete(URI_BOOKMARK(), "path=? and endPos=? and type=?", new String[]{bean.path + "",
    //                            bean.endPos + "", bean.type + ""});
    //                }
    //            }
    //        } catch (Exception e) {
    //            ALog.printStackTrace(e);
    //        } finally {
    //            closeCursor(cursor);
    //        }
    //        mResolver.insert(URI_BOOKMARK(), bean.beanToValues());
    //    }
    //
    //    public static void addBookMark(Context context, BookMark bean) {
    //        ContentResolver mResolver = getContentResolver(context);
    //        mResolver.insert(URI_BOOKMARK(), bean.beanToValues());
    //    }
    //
    //    public static void deleteBookMark(Context context, BookMark bean) {
    //        ContentResolver mResolver = getContentResolver(context);
    //        mResolver.delete(URI_BOOKMARK(), "path=? and startPos=? and type=?", new String[]{bean.path + "",
    //                bean.startPos + "", bean.type + ""});
    //    }
    //
    //    public static void clearBookMark(Context context, String bookId) {
    //        ContentResolver mResolver = getContentResolver(context);
    //        mResolver.delete(URI_BOOKMARK(), "bookId=?", new String[]{bookId});
    //    }
    //
    //    public static boolean isMarked(Context context, BookMark bean) {
    //        Cursor cursor = null;
    //        try {
    //            ContentResolver mResolver = getContentResolver(context);
    //            cursor = mResolver.query(URI_BOOKMARK(), null, "path=? and startPos=? and type=?", new String[]{
    //                    bean.path + "", bean.startPos + "", "2"}, null);
    //            if (cursor != null && cursor.moveToFirst()) {
    //                cursor.close();
    //                return true;
    //            }
    //        } catch (Exception e) {
    //            ALog.printStackTrace(e);
    //        } finally {
    //            closeCursor(cursor);
    //        }
    //        return false;
    //    }

    // -------------------------------------HTTP请求数据缓存-------------------------------------------------

    /**
     * 更新bs页面缓存(如果更新不成功表示数据库不存在这条数据则插入数据库) (先删除后插入 效率比更新效率高)
     *
     * @param context       context
     * @param httpCacheInfo httpCacheInfo
     */
    public static void updateOrInsertHttpCacheInfo(Context context, HttpCacheInfo httpCacheInfo) {
        try {
            if (!TextUtils.isEmpty(httpCacheInfo.response)) {
                ContentResolver mResolver = getContentResolver(context);
                mResolver.delete(uriHttpCache(), "url=?", new String[]{httpCacheInfo.url});
                mResolver.insert(uriHttpCache(), httpCacheInfo.beanToValues());
            }
        } catch (Exception e) {
            ALog.printStackTrace(e);
        }
    }

    /**
     * 查询bs页面缓存
     *
     * @param context context
     * @param url     url
     * @return 查询结果
     */
    public static HttpCacheInfo findHttpCacheInfo(Context context, String url) {
        ContentResolver mResolver = getContentResolver(context);
        Cursor cursor = null;
        HttpCacheInfo bean = null;
        try {
            cursor = mResolver.query(uriHttpCache(), null, "url=? ", new String[]{url + ""}, null);
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    bean = new HttpCacheInfo();
                    bean.cursorToBean(cursor);
                    return bean;
                }
            }
        } catch (Exception e) {
            ALog.printStackTrace(e);
        } finally {
            closeCursor(cursor);
        }
        return bean;
    }

    /************************************** 章节报错 *********************************************************/


    /***
     * 开始时间
     * @return 开始时间
     */
    public static long start() {
        return System.currentTimeMillis();

    }


    /**
     * 结束时间
     *
     * @param l       l
     * @param context context
     */
    public static void end(long l, Object context) {
        String longtime = "";
        String methodName;
        StackTraceElement[] elementArray = new Throwable().getStackTrace();
        if (null != elementArray && elementArray.length > 1) {
            StackTraceElement element = elementArray[1];
            methodName = element.getMethodName();
            long end = System.currentTimeMillis() - l;
            if (end >= 500) {
                longtime = "严重耗时";
            }
            if (Looper.myLooper() == Looper.getMainLooper()) {
                time += end;
            }
            ALog.d("计时-" + (Looper.myLooper() == Looper.getMainLooper() ? "主线程" : "子线程") + longtime + context.getClass().getSimpleName() + ":" + methodName, String.valueOf(end) + "ms" + " " + " 已经持续耗时" + String.valueOf(time) + "ms   ");
        }
    }

    /**
     * 打印日志
     *
     * @param context context
     */
    public static void log(Object context) {
        String methodName = null;
        StackTraceElement[] elementArray = new Throwable().getStackTrace();
        if (null != elementArray && elementArray.length > 1) {
            StackTraceElement element = elementArray[1];
            methodName = element.getMethodName();
            ALog.d("打印-" + (Looper.myLooper() == Looper.getMainLooper() ? "主线程" : "子线程") + context.getClass().getSimpleName() + ":" + methodName, "===");
        }
    }

    /**
     * 获取到给定书籍列表的最新章节列表
     *
     * @param context context
     * @return 查询结果
     */
    public static Map<String, CatalogInfo> getAllLaterCatalogInfos(Context context) {
        ContentResolver mResolver = getContentResolver(context);
        Cursor cursor = null;
        Map<String, CatalogInfo> map = new HashMap<>();
        try {
            cursor = mResolver.query(uriUcategory(), null, "_ID IN (SELECT MAX(_ID) AS _ID FROM CatalogInfo WHERE bookid IN (SELECT bookid FROM BookInfo WHERE isAddBook==2) GROUP BY bookid)", null, null);
            if (null != cursor) {
                while (cursor.moveToNext()) {
                    CatalogInfo bean = new CatalogInfo(null, null);
                    bean.cursorToBean(cursor);
                    if (!TextUtils.isEmpty(bean.bookid)) {
                        map.put(bean.bookid, bean);
                    }
                }
            }
        } catch (Exception e) {
            ALog.printStackTrace(e);
        } finally {
            closeCursor(cursor);
        }
        return map;
    }


    /**
     * 添加插件
     *
     * @param context    context
     * @param pluginInfo pluginInfo
     */
    public static void addPlugin(Context context, PluginInfo pluginInfo) {
        int affectedRow = updatePlugin(context, pluginInfo);
        if (affectedRow > 0) {
            return;
        }
        ContentResolver mResolver = getContentResolver(context);
        mResolver.insert(uriPlugin(), pluginInfo.beanToValues());
    }

    /**
     * 更新插件
     *
     * @param context    context
     * @param pluginInfo pluginInfo
     */
    private static int updatePlugin(Context context, PluginInfo pluginInfo) {
        int affectedRow = 0;
        try {
            ContentResolver mResolver = getContentResolver(context);
            affectedRow = mResolver.update(uriPlugin(), pluginInfo.beanToValues(), "name=? ", new String[]{pluginInfo.name});
        } catch (Exception e) {
            ALog.printStackTrace(e);
        }
        return affectedRow;
    }

    /**
     * 获取插件
     *
     * @param context context
     * @param name    name
     * @return 插件
     */
    public static PluginInfo getPlugin(Context context, String name) {
        ContentResolver mResolver = getContentResolver(context);
        Cursor cursor = null;
        PluginInfo bean = null;
        try {
            cursor = mResolver.query(uriPlugin(), null, "name=? ", new String[]{name}, null);
            if (cursor != null && cursor.moveToFirst()) {
                bean = new PluginInfo();
                bean.cursorToBean(cursor);
            }

        } catch (Exception e) {
            ALog.printStackTrace(e);
        } finally {
            closeCursor(cursor);
        }
        return bean;
    }


    /**
     * 根据书籍id查找存在书架上的书
     *
     * @param context context
     * @param bookId  bookId
     * @return 查找结果
     */
    public static BookInfo findShelfBookByBookId(Context context, String bookId) {
        ContentResolver mResolver = getContentResolver(context);
        BookInfo bean = null;
        Cursor cursor = null;
        try {
            cursor = mResolver.query(uriBookBean(), null, "bookid=? and isAddBook=2", new String[]{bookId + ""}, null);
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    bean = new BookInfo();
                    bean.cursorToBean(cursor);
                }
            }
        } catch (Exception e) {
            ALog.printStackTrace(e);
        } finally {
            closeCursor(cursor);
        }
        return bean;
    }

    /**
     * 获取ContentResolver实例
     *
     * @param context context
     * @return 对象
     */
    public static ContentResolver getContentResolver(Context context) {
        return context.getApplicationContext().getContentResolver();
    }


}
