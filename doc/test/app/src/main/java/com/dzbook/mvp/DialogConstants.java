package com.dzbook.mvp;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * DialogConstants
 *
 * @author wxliao on 17/8/15.
 */

public class DialogConstants {
    /**
     * NIT_PAGE
     */
    public static final int TYPE_INIT_PAGE = 0x01;
    /**
     * GET_DATA
     */
    public static final int TYPE_GET_DATA = 0x02;
    /**
     * GET_DATA_TRANSPARENT
     */
    public static final int TYPE_GET_DATA_TRANSPARENT = 0x03;

    /**
     * NO_DIALOG
     */
    public static final int TYPE_NO_DIALOG = 0x10;

    /**
     * IntDef
     */
    @IntDef({TYPE_INIT_PAGE, TYPE_GET_DATA, TYPE_GET_DATA_TRANSPARENT, TYPE_NO_DIALOG})
    @Retention(RetentionPolicy.SOURCE)
    public @interface DialogType {

    }
}
