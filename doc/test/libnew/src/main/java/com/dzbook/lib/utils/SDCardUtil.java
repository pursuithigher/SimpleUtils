package com.dzbook.lib.utils;

import android.content.Context;
import android.os.Environment;
import android.os.StatFs;
import android.text.TextUtils;

import java.io.File;

/**
 * SDCardUtil
 *
 * @author zhenglk
 */
public class SDCardUtil {

    private static volatile SDCardUtil sInstance;
    private Context mContext;
    private String externalStorageState = "";
    private String fileContextDir, externalDir;

    private SDCardUtil() {
    }

    /**
     * 初始化
     *
     * @param context context
     */
    public void init(Context context) {
        mContext = context;
    }


    /**
     * 获取单例
     *
     * @return 实例
     */
    public static SDCardUtil getInstance() {
        if (sInstance == null) {
            synchronized (SDCardUtil.class) {
                if (sInstance == null) {
                    sInstance = new SDCardUtil();
                }
            }
        }

        return sInstance;
    }

    /**
     * 判断sd卡是否可用
     *
     * @return boolean
     */
    public boolean isSDCardAvailable() {
        if (TextUtils.isEmpty(externalStorageState)) {
            externalStorageState = Environment.getExternalStorageState();
        }
        return externalStorageState.equals(Environment.MEDIA_MOUNTED);
    }

    /**
     * 大于10M返回true;
     *
     * @return boolean
     */
    public boolean isSDCardCanWrite() {
        return isSDCardCanWrite(10 * 1024 * 1024);
    }

    /**
     * 传入指定大小，可用空间小于指定大小，或sd卡不可用时，返回false
     *
     * @param minSize minSize
     * @return boolean
     */
    public boolean isSDCardCanWrite(long minSize) {

        try {
            if (isSDCardAvailable()) {
                StatFs statfs;
                File path;
                try {
                    path = Environment.getExternalStorageDirectory();
                    statfs = new StatFs(path.getPath());
                } catch (Exception e) {
                    statfs = new StatFs("/mnt/sdcard/");
                }
                long blockSize = statfs.getBlockSize();
                long availaBlock = statfs.getAvailableBlocks();
                if (blockSize * availaBlock > minSize) {
                    return true;
                }
            }

        } catch (Exception e) {
            ALog.printStackTrace(e);
        }
        return false;
    }

    /**
     * 获取Android路径
     *
     * @return 路径
     */
    public String getSDCardAndroidRootDir() {
        if (mContext != null) {
            if (TextUtils.isEmpty(fileContextDir)) {
                File file = mContext.getExternalFilesDir("");
                if (null != file) {
                    fileContextDir = file.getPath();
                }
            }
            if (!TextUtils.isEmpty(fileContextDir)) {
                return fileContextDir;
            }
        }
        if (TextUtils.isEmpty(externalDir)) {
            File file = Environment.getExternalStorageDirectory();
            if (null != file) {
                externalDir = file.getPath();
            }
        }
        return externalDir;
    }

    public File getSDCardRootDir() {
        return Environment.getExternalStorageDirectory();
    }

}
