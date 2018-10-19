package com.dzbook.bean;

import android.content.Context;
import android.text.TextUtils;

import com.dzbook.AppConst;
import com.dzbook.database.bean.BookInfo;
import com.dzbook.utils.DBUtils;
import com.dzbook.utils.TimeUtils;
import com.dzbook.utils.TypefaceUtils;
import com.ishugui.R;

import java.io.File;
import java.util.Comparator;

/**
 * 书架-文件对应的bean
 *
 * @author dllik 2013-11-23
 */
public class LocalFileBean {
    /**
     * type dir
     */
    public static final int TYPE_DIR = 0x00;
    /**
     * epub
     */
    public static final int TYPE_EPUB = 0x01;
    /**
     * txt
     */
    public static final int TYPE_TXT = 0x02;


    /**
     * name
     */
    public static final int TYPE_SORT_NAME = 0x01;
    /**
     * time
     */
    public static final int TYPE_SORT_TIEM = 0x02;

    private static final int TYPE_UN_SUPPORT = 0x10;
    /**
     * 0x20--0x29为WPS打开文件类型
     */
    private static final int TYPE_DOC = 0x20;
    private static final int TYPE_DOCX = 0x21;
    private static final int TYPE_PDF = 0x22;
    private static final int TYPE_PPT = 0x23;
    private static final int TYPE_PPTX = 0x24;
    private static final int TYPE_PPS = 0x25;
    private static final int TYPE_PPSX = 0x26;
    private static final int TYPE_XLS = 0x27;
    private static final int TYPE_XLSX = 0x28;

    /**
     * 文件名
     */
    public String fileName;
    /**
     * filePath
     */
    public String filePath;
    /**
     * 文件类型
     */
    public int fileType;
    /**
     * sortType
     */
    public int sortType = TYPE_SORT_NAME;
    /**
     * size
     */
    public long size;
    /**
     * firstLetter
     */
    public String firstLetter = "";
    /**
     * lastModified
     */
    public long lastModified;
    /**
     * lastModifiedDesc
     */
    public String lastModifiedDesc;
    /**
     * lastModifiedType
     */
    public int lastModifiedType;
    /**
     * 文件数量
     */
    public String childNum;
    /**
     * isTitle
     */
    public boolean isTitle;
    /**
     * isAdded
     */
    public boolean isAdded;
    /**
     * isChecked
     */
    public boolean isChecked;

    /**
     * isImportSuccess
     */
    public boolean isImportSuccess;


    private static int getFileType(File file, String ignoreStr) {
        if (file == null || file.isHidden()) {
            return TYPE_UN_SUPPORT;
        }
        return isReturnType(file, ignoreStr);
    }

    private static int isReturnType(File file, String ignoreStr) {
        String path = file.getAbsolutePath().toLowerCase();
        if (!TextUtils.isEmpty(ignoreStr) && path.contains(ignoreStr)) {
            return TYPE_UN_SUPPORT;
        } else if (file.isDirectory()) {
            return TYPE_DIR;
        } else if (path.endsWith(".txt")) {
            return TYPE_TXT;
        } else if (path.endsWith(".epub")) {
            return TYPE_EPUB;
        } else if (path.endsWith(".doc")) {
            return TYPE_DOC;
        } else if (path.endsWith(".docx")) {
            return TYPE_DOCX;
        } else if (path.endsWith(".pdf")) {
            return TYPE_PDF;
        } else if (path.endsWith(".ppt")) {
            return TYPE_PPT;
        } else if (path.endsWith(".pptx")) {
            return TYPE_PPTX;
        } else if (path.endsWith(".pps")) {
            return TYPE_PPS;
        } else if (path.endsWith(".ppsx")) {
            return TYPE_PPSX;
        } else if (path.endsWith(".xls")) {
            return TYPE_XLS;
        } else if (path.endsWith(".xlsx")) {
            return TYPE_XLSX;
        } else {
            return TYPE_UN_SUPPORT;
        }
    }

    /**
     * getFileTypeName
     * @param aFileType aFileType
     * @return String
     */
    public static String getFileTypeName(int aFileType) {
        if (aFileType == TYPE_UN_SUPPORT) {
            return AppConst.getApp().getResources().getString(R.string.time_unknow_ago);
        } else if (aFileType == TYPE_DIR) {
            return AppConst.getApp().getResources().getString(R.string.reader_catalog);
        } else if (aFileType == TYPE_TXT) {
            return "TXT";
        } else if (aFileType == TYPE_EPUB) {
            return "EPUB";
        } else if (aFileType == TYPE_DOC) {
            return "DOC";
        } else if (aFileType == TYPE_DOCX) {
            return "DOCX";
        } else if (aFileType == TYPE_PDF) {
            return "PDF";
        } else if (aFileType == TYPE_PPT) {
            return "PPT";
        } else if (aFileType == TYPE_PPTX) {
            return "PPTX";
        } else if (aFileType == TYPE_PPS) {
            return "PPS";
        } else if (aFileType == TYPE_PPSX) {
            return "PPSX";
        } else if (aFileType == TYPE_XLS) {
            return "XLS";
        } else if (aFileType == TYPE_XLSX) {
            return "XLSX";
        } else {
            return AppConst.getApp().getResources().getString(R.string.time_unknow_ago);
        }
    }

    /**
     * getFileTypeNoDirName
     * @param aFileType aFileType
     * @return String
     */
    public static String getFileTypeNoDirName(int aFileType) {
        if (aFileType == TYPE_DIR) {
            return AppConst.getApp().getResources().getString(R.string.time_unknow_ago);
        }
        return getFileTypeName(aFileType);
    }

    /**
     * fileToLocalBean
     * @param context context
     * @param file file
     * @param ignoreStr ignoreStr
     * @return LocalFileBean
     */
    public static LocalFileBean fileToLocalBean(Context context, File file, String ignoreStr) {
        int fileType = LocalFileBean.getFileType(file, ignoreStr);
        if (fileType == LocalFileBean.TYPE_UN_SUPPORT) {
            return null;
        }
        String absolutePath = file.getAbsolutePath();
        if (TextUtils.isEmpty(absolutePath)) {
            return null;
        }
        LocalFileBean localFileBean = new LocalFileBean();
        localFileBean.fileName = file.getName();
        localFileBean.filePath = absolutePath;
        localFileBean.lastModified = file.lastModified();
        localFileBean.lastModifiedType = TimeUtils.getShowTimeByFile(localFileBean.lastModified);
        localFileBean.lastModifiedDesc = getTimeDesc(localFileBean.lastModifiedType);
        localFileBean.firstLetter = TypefaceUtils.getFirstLetter(localFileBean.fileName);
        localFileBean.fileType = fileType;
        if (fileType == LocalFileBean.TYPE_DIR) {
            String[] list = file.list();
            if (list == null || list.length == 0) {
                return null;
            }
            localFileBean.childNum = list.length + "";
            return localFileBean;
        } else if (isAcceptFile(fileType)) {
            localFileBean.size = file.length();
            String bookId;
            if (localFileBean.fileType == TYPE_EPUB) {
                bookId = String.valueOf(localFileBean.filePath.hashCode());
            } else {
                bookId = absolutePath;
            }
            BookInfo bookInfo = DBUtils.findByBookId(context, bookId);
            if (bookInfo != null) {
                localFileBean.isAdded = true;
            }
            return localFileBean;
        }
        return null;
    }

    /**
     * getTimeDesc
     * @param type type
     * @return String
     */
    public static String getTimeDesc(int type) {
        String timeDesc = "";
        switch (type) {
            case TimeUtils.TYPE_TIME_TODAY:
                timeDesc = "一天以内";
                break;
            case TimeUtils.TYPE_TIME_WEEK:
                timeDesc = "一周以内";
                break;
            case TimeUtils.TYPE_TIME_MONTH:
                timeDesc = "一月以内";
                break;
            case TimeUtils.TYPE_TIME_OTHER:
                timeDesc = "一月以前";
                break;
            default:
                break;
        }
        return timeDesc;
    }

    /**
     * getCompareType
     * @param type type
     * @return Comparator
     */
    public static Comparator<LocalFileBean> getCompareType(int type) {
        Comparator<LocalFileBean> comparator;
        if (type == TYPE_SORT_TIEM) {
            comparator = new Comparator<LocalFileBean>() {
                @Override
                public int compare(LocalFileBean o1, LocalFileBean o2) {
                    if (o1.fileType != o2.fileType && (o1.fileType == TYPE_DIR || o2.fileType == TYPE_DIR)) {
                        return o1.fileType - o2.fileType;
                    } else {
                        if (o1.lastModified == o2.lastModified) {
                            return o1.firstLetter.compareTo(o2.firstLetter);
                        }
                        return o1.lastModified < o2.lastModified ? 1 : -1;
                    }
                }
            };
        } else {
            comparator = new Comparator<LocalFileBean>() {
                @Override
                public int compare(LocalFileBean o1, LocalFileBean o2) {
                    if (o1.fileType != o2.fileType && (o1.fileType == TYPE_DIR || o2.fileType == TYPE_DIR)) {
                        return o1.fileType - o2.fileType;
                    } else {
                        return o1.firstLetter.compareTo(o2.firstLetter);
                    }
                }
            };
        }
        return comparator;
    }

    public boolean isCheckedFile() {
        return isAcceptFile() && !isAdded && isChecked;
    }

    public boolean isAcceptFile() {
        return isAcceptFile(fileType);
    }

    private static boolean isAcceptFile(int fileType) {
        boolean result = fileType == TYPE_TXT || fileType == TYPE_EPUB || fileType == TYPE_DOC
                || fileType == TYPE_DOCX || fileType == TYPE_PDF || fileType == TYPE_PPT;
        return  result || fileType == TYPE_PPTX || fileType == TYPE_PPS || fileType == TYPE_PPSX
                || fileType == TYPE_XLS || fileType == TYPE_XLSX;
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    public void setSortType(int sortType) {
        this.sortType = sortType;
    }
}
