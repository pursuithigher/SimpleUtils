package com.dzbook.event.type;


import com.dzbook.database.bean.BookMarkNew;

/**
 * 书签
 *
 * @author wxliao on 17/12/13.
 */

public class BookMarkEvent {
    /**
     * 添加
     */
    public static final int TYPE_ADD = 0x01;
    /**
     * 删除
     */
    public static final int TYPE_DELETE = 0x02;
    /**
     * 清除
     */
    public static final int TYPE_CLEAR = 0x03;

    /**
     * 类型
     */
    public int type;

    /**
     * 书签
     */
    public BookMarkNew bookMark;

    /**
     * 构造
     *
     * @param type     type
     * @param bookMark bookMark
     */
    public BookMarkEvent(int type, BookMarkNew bookMark) {
        this.type = type;
        this.bookMark = bookMark;
    }
}
