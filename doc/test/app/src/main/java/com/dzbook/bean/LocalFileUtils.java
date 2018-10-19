package com.dzbook.bean;

import android.content.Context;
import android.database.Cursor;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;

import com.dzbook.AppConst;
import com.dzbook.database.bean.BookInfo;
import com.dzbook.database.bean.CatalogInfo;
import com.dzbook.lib.utils.ALog;
import com.dzbook.lib.utils.SDCardUtil;
import com.dzbook.r.model.DzChapter;
import com.dzbook.r.model.EpubChapter;
import com.dzbook.r.model.EpubInfo;
import com.dzbook.r.util.EpubUtils;
import com.dzbook.r.util.TxtUtils;
import com.dzbook.utils.DBUtils;
import com.dzbook.utils.FileUtils;
import com.dzbook.utils.ListUtils;
import com.ishugui.R;

import java.io.File;
import java.util.ArrayList;

/**
 * LocalFileUtils
 *
 * @author winzows 2018/5/22
 */

public class LocalFileUtils {
    private static final String STR_ROOT_STORAGE = "/root/";
    private static final String STR_EXTERNAL_FILES = "/external_files";
    private static final String STR_SDCARD = "sdcard1";
    private static final String STR_STORAGE = "/storage/";

    /**
     * 打开本地文件
     */
    private static final String PATH_START_FILE = "/storage";
    private static final String PATH_SPLIT_STR = "file/";


    /**
     * 插入数据库
     *
     * @param context context
     * @param bean    bean
     * @return BookInfo
     */
    public static BookInfo insertLocalDb(Context context, LocalFileBean bean) {
        if (context == null || bean == null) {
            return null;
        }
        if (bean.fileType == LocalFileBean.TYPE_EPUB) {
            EpubInfo epubInfo = EpubUtils.getEpubInfo(bean.filePath, SDCardUtil.getInstance().getSDCardAndroidRootDir() + "/" + FileUtils.APP_ROOT_DIR_PATH);
            if (epubInfo != null && !ListUtils.isEmpty(epubInfo.chapters)) {
                BookInfo bookBean = new BookInfo();
                bookBean.bookid = String.valueOf(bean.filePath.hashCode());
                bookBean.time = System.currentTimeMillis() + "";
                bookBean.bookfrom = 2;
                bookBean.bookname = epubInfo.title;
                bookBean.coverurl = epubInfo.cover;
                bookBean.format = bean.fileType;
                bookBean.isdefautbook = 1;
                bookBean.isAddBook = 2;
                bookBean.currentCatalogId = epubInfo.chapters.get(0).id;

                BookInfo dbBook = DBUtils.findByBookId(context, bookBean.bookid);
                if (dbBook == null) {
                    dbBook = bookBean;
                    DBUtils.insertBook(context, bookBean);

                    ArrayList<CatalogInfo> catalogList = new ArrayList<>();
                    for (EpubChapter chapter : epubInfo.chapters) {
                        CatalogInfo catalogInfo = new CatalogInfo(bookBean.bookid, chapter.id);
                        catalogInfo.currentPos = 0;
                        catalogInfo.path = chapter.path;
                        catalogInfo.catalogname = chapter.name;
                        catalogInfo.ispay = "1";
                        catalogInfo.isdownload = "0";
                        catalogList.add(catalogInfo);
                    }
                    DBUtils.insertLotCatalog(context, catalogList);
                }
                return dbBook;
            }
        } else if (bean.fileType == LocalFileBean.TYPE_TXT) {
            if (TextUtils.isEmpty(bean.filePath)) {
                return null;
            }
            ArrayList<DzChapter> chapters = TxtUtils.getTxtChapters(bean.filePath);
            if (!ListUtils.isEmpty(chapters)) {
                BookInfo bookBean = new BookInfo();
                bookBean.bookid = bean.filePath;
                bookBean.time = System.currentTimeMillis() + "";
                bookBean.bookfrom = 2;
                bookBean.bookname = getFileNameNoEx(bean.fileName);
                bookBean.coverurl = "drawable://" + R.drawable.aa_shelf_icon_default;
                bookBean.format = bean.fileType;
                bookBean.isdefautbook = 1;
                bookBean.isAddBook = 2;
                bookBean.currentCatalogId = chapters.get(0).id;

                BookInfo dbBook = DBUtils.findByBookId(context, bookBean.bookid);
                if (dbBook == null) {
                    dbBook = bookBean;
                    DBUtils.insertBook(context, bookBean);

                    ArrayList<CatalogInfo> catalogList = new ArrayList<>();
                    for (DzChapter chapter : chapters) {
                        CatalogInfo catalogInfo = new CatalogInfo(bookBean.bookid, chapter.id);
                        catalogInfo.currentPos = chapter.start;
                        catalogInfo.path = bean.filePath;
                        catalogInfo.catalogname = chapter.title;
                        catalogInfo.ispay = "1";
                        catalogInfo.isdownload = "0";
                        catalogInfo.startPos = chapter.start;
                        catalogInfo.endPos = chapter.end;
                        catalogList.add(catalogInfo);
                    }
                    DBUtils.insertLotCatalog(context, catalogList);
                }
                return dbBook;
            }
        } else {
            String path = bean.filePath;
            BookInfo bookBean = new BookInfo();
            bookBean.bookid = path;
            bookBean.time = System.currentTimeMillis() + "";
            bookBean.bookfrom = 2;
            bookBean.bookname = getFileNameNoEx(bean.fileName);
            bookBean.coverurl = "drawable://" + R.drawable.aa_shelf_icon_default;
            bookBean.format = bean.fileType;
            bookBean.isdefautbook = 1;
            bookBean.isAddBook = 2;
            //-1表示本地图书还没有被扫描
            bookBean.currentCatalogId = "-1";
            BookInfo dbBook = DBUtils.findByBookId(context, bookBean.bookid);

            if (dbBook == null) {
                dbBook = bookBean;
                DBUtils.insertBook(context, bookBean);
                //-1表示本地图书还没有被扫描
                CatalogInfo catalogInfo = new CatalogInfo(path, "-1");
                catalogInfo.currentPos = 0;
                catalogInfo.path = path;
                catalogInfo.catalogname = bookBean.bookname;
                catalogInfo.ispay = "1";
                catalogInfo.isdownload = "0";
                DBUtils.insertCatalog(context, catalogInfo);
            }
            return dbBook;
        }
        return null;
    }


    /**
     * 获取不带扩展名的文件名
     */
    private static String getFileNameNoEx(String filename) {
        if ((filename != null) && (filename.length() > 0)) {
            int dot = filename.lastIndexOf('.');
            if ((dot > -1) && (dot < (filename.length()))) {
                return filename.substring(0, dot);
            }
        }
        return filename;
    }
    //    }
    //        void onFail(String errDes);
    //
    //        void onSuccess(int fileType, String bookId);
    //    public interface InsertCallBack {


    /**
     * 获取本地文件
     *
     * @param uriPath uriPath
     * @return LocalFileBean
     */
    public static LocalFileBean getLocalFile(String uriPath) {
        File file = null;
        if (TextUtils.isEmpty(uriPath)) {
            return null;
        }
        ALog.dWz("getLocalFile " + "before URI=" + uriPath);
        if (uriPath.startsWith(STR_ROOT_STORAGE)) {
            uriPath = uriPath.replace(STR_ROOT_STORAGE, "/");
        } else if (uriPath.startsWith(STR_EXTERNAL_FILES)) {
            uriPath = uriPath.replace(STR_EXTERNAL_FILES, Environment.getExternalStorageDirectory().getAbsolutePath());
        }

        ALog.dWz("getLocalFile " + "after URI=" + uriPath);
        if (uriPath.contains(STR_SDCARD) || uriPath.startsWith(PATH_START_FILE) || uriPath.startsWith(STR_STORAGE)) {
            file = new File(uriPath);
        } else {
            Cursor cursor = null;
            try {
                String fileId = uriPath.split(LocalFileUtils.PATH_SPLIT_STR)[1];
                cursor = AppConst.getApp().getContentResolver().query(
                        //数据源
                        MediaStore.Files.getContentUri("external"),
                        //查询路径
                        new String[]{MediaStore.Images.Media.DATA},
                        //条件为id
                        MediaStore.Files.FileColumns._ID + "= ?",
                        //id = fileId
                        new String[]{fileId},
                        //默认排序
                        null);

                if (cursor != null) {
                    int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                    cursor.moveToFirst();
                    String readPath = cursor.getString(columnIndex);
                    file = new File(readPath);
                }
            } catch (Exception e) {
                ALog.printExceptionWz(e);
            } finally {
                if (cursor != null) {
                    cursor.close();
                }
            }
        }
        return LocalFileBean.fileToLocalBean(AppConst.getApp(), file, "");
    }


}
