package com.dzbook.imageloader;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * AttachMode
 *
 * @author wxliao on 17/5/6.
 */
public class AttachMode {
    /**
     * ACTIVITY MODE
     */
    public static final int ACTIVITY = 0x01;
    /**
     * FRAGMENT MODE
     */
    public static final int FRAGMENT = 0x02;
    /**
     * CONTEXT MODE
     */
    public static final int CONTEXT = 0x03;

    /**
     * 构造
     */
    @IntDef({ACTIVITY, FRAGMENT, CONTEXT})
    @Retention(RetentionPolicy.SOURCE)
    public @interface AttachModes {
    }
}
