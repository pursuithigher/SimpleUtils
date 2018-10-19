package com.dzbook.loader;

import java.io.File;

/**
 * author lizhongzhong 2017/12/25.
 * 下载文件封装bean
 */

public class LoadFileBean {

    /**
     * responseCode
     */
    public int responseCode = -1;

    /**
     * netFileSize
     */
    public long netFileSize = -1;

    /**
     * downloadFileSize
     */
    public long downloadFileSize = -1;

    /**
     * saveFile
     */
    public File saveFile;

    /**
     * message
     */
    public String message;
}
