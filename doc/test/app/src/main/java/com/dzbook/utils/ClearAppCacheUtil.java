package com.dzbook.utils;

import com.dzbook.lib.utils.ALog;

import java.io.File;

/**
 * 删除应用 程序缓存目录
 *
 * @author dllik
 * 2013-11-23
 */
public class ClearAppCacheUtil {
    /**
     * 删除制定目录列表的文件
     *
     * @param dir 删除目录,可以传递多个
     */
    public static void deleteFilesByDir(File... dir) {
        try {
            for (int i = 0; i < dir.length; i++) {
                // 防止指定删除目录不存在,报空指针异常
                if (!dir[i].exists()) {
                    continue;
                }
                File[] files = dir[i].listFiles();
                if (files != null) {
                    for (File file : files) {
                        if (file.isDirectory()) {
                            deleteFilesByDir(file);
                        } else {
                            if (file.delete()) {
                                ALog.dWz("file delete success!" + file);
                            }
                        }
                    }

                    boolean delete = dir[i].delete();
                    if (delete) {
                        ALog.dWz("file delete success!" + dir[i]);
                    }
                }
            }
        } catch (Exception e) {
            ALog.printExceptionWz(e);
        }
    }

}
