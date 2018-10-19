package com.dzbook.event.engine;

import android.content.Context;

import com.dzbook.lib.net.DzSchedulers;
import com.dzbook.service.CheckBookshelfUpdateRunnable;
import com.dzbook.utils.NetworkUtils;

/**
 * DBEngine
 * @author caimantang on 16/8/6.
 */
public class DBEngine {
    private static DBEngine instance;

    /**
     * getInstance
     * @return DBEngine
     */
    public static DBEngine getInstance() {
        if (null == instance) {
            instance = new DBEngine();
        }
        return instance;
    }

    /**
     * updataBook
     * @param context context
     * @param bookid  bookid
     */
    public void updataBook(final Context context, final String bookid) {
        if (NetworkUtils.getInstance().checkNet()) {
            DzSchedulers.execute(new CheckBookshelfUpdateRunnable(context, bookid));
        }
    }
}
