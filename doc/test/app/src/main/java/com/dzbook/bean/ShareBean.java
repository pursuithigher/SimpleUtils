package com.dzbook.bean;

import android.graphics.drawable.Drawable;

/**
 * ShareBean
 */
public class ShareBean {
    /**
     * title
     */
    public String title;
    /**
     * drawable
     */
    public Drawable drawable;
    /**
     * 类型：1.qq好友2.qq空间3.微信好友4.微信朋友圈5.微博
     */
    public int type;

    /**
     * ShareBean
     * @param title title
     * @param drawable drawable
     * @param type type
     */
    public ShareBean(String title, Drawable drawable, int type) {
        this.title = title;
        this.drawable = drawable;
        this.type = type;
    }
}