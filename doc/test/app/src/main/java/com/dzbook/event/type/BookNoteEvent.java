package com.dzbook.event.type;


import com.dzbook.database.bean.BookMarkNew;

/**
 * 笔记
 *
 * @author wxliao on 17/12/13.
 */

public class BookNoteEvent {

    /**
     * 添加
     */
    public static final int TYPE_ADD = 0x01;
    /**
     * 删除
     */
    public static final int TYPE_DELETE = 0x02;
    /**
     * 清空
     */
    public static final int TYPE_CLEAR = 0x03;

    /**
     * 类型
     */
    private int type;

    /**
     * 笔记
     */
    private BookMarkNew bookNote;

    /**
     * 构造
     *
     * @param type     type
     * @param bookNote bookNote
     */
    public BookNoteEvent(int type, BookMarkNew bookNote) {
        this.type = type;
        this.bookNote = bookNote;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public BookMarkNew getBookNote() {
        return bookNote;
    }

    public void setBookNote(BookMarkNew bookNote) {
        this.bookNote = bookNote;
    }
}
