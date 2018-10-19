package com.dzbook.imageloader;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * ResourceMode
 *
 * @author wxliao on 17/5/6.
 */

public class ResourceMode {
    /**
     * 类型-string
     */
    public static final int STRING = 0x01;
    /**
     * 类型-url
     */
    public static final int URI = 0x02;
    /**
     * 类型-file
     */
    public static final int FILE = 0x03;
    /**
     * 类型-res
     */
    public static final int RES = 0x04;

    /**
     * IntDef
     */
    @IntDef({STRING, URI, FILE, RES})
    @Retention(RetentionPolicy.SOURCE)
    public @interface ResourceModes {
    }
}
