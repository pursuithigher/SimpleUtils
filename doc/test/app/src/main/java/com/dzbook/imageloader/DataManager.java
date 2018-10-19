package com.dzbook.imageloader;

import android.content.Context;

import com.dzbook.AppConst;

/**
 * DataManager
 *
 * @author wxliao on 17/7/21.
 */

public class DataManager {
    private static volatile ImageHelper imageHelper;

    /**
     * init
     * @param context context
     */
    public static void init(Context context) {
        imageHelper = new ImageHelper(context);
    }

    /**
     * getImageHelper
     * @return ImageHelper
     */
    public static ImageHelper getImageHelper() {
        if (imageHelper == null) {
            synchronized (DataManager.class) {
                if (imageHelper == null) {
                    init(AppConst.getApp());
                }
            }
        }
        return imageHelper;
    }

}
