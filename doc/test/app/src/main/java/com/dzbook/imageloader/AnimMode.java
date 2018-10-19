package com.dzbook.imageloader;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * 动画mode
 *
 * @author wxliao on 17/5/8.
 */
public class AnimMode {
    /**
     * 无
     */
    public static final int NULL = 0x01;
    /**
     * CROSS_FADE
     */
    public static final int CROSS_FADE = 0x02;

    /**
     * 构造
     */
    @IntDef({NULL, CROSS_FADE})
    @Retention(RetentionPolicy.SOURCE)
    public @interface AnimModes {
    }
}
