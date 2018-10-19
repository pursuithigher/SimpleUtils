package com.dzbook.utils;

/**
 * FileUtils
 *
 * @author Winzows 2018/4/3
 */
public class AbsFileUtils {

    /**
     * utf-8
     */
    public static final String DEFAULT_CHARSET = "utf-8";
    /**
     * 程序创建的文件夹路径（sd卡的相对路径）
     */
    public static final String APP_ROOT_DIR_PATH = ".ishugui/";

    /**
     * 阅读器文摘分享图片缓冲目录（sd卡的相对路径）
     */
    public static final String APP_BOOK_IMAGE_CACHE_PATH = APP_ROOT_DIR_PATH + "Cache/";

    /**
     * Glide图片缓存目录（sd卡的相对路径）
     */
    public static final String APP_BOOK_IMAGE_GLIDE_CACHE = APP_ROOT_DIR_PATH + ".glide_cache/";

    /**
     * 书籍存放文件夹路径（sd卡的相对路径）
     */
    public static final String APP_BOOK_DIR_PATH = APP_ROOT_DIR_PATH + "books/";

    /**
     * 创建一个指定大小的文件路径
     */
    public static final String APP_ASSIGN_FILE_SZIE_PATH = APP_ROOT_DIR_PATH + "empty.system";

    /**
     * 本地崩溃log
     */
    public static final String APP_LOG_DIR_PATH = APP_ROOT_DIR_PATH + ".log/";

    /**
     * 下载目录
     */
    public static final String APP_DOWNLOAD = APP_ROOT_DIR_PATH + "download/";

}
