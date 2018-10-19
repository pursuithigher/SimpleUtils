package com.dzbook.view.reader;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.dzbook.activity.reader.BookNoteAdapter;
import com.dzbook.database.bean.BookMarkNew;
import com.ishugui.R;

import java.util.List;

/**
 * 阅读笔记
 *
 * @author wxliao on 17/12/8.
 */

public class ReaderNoteView extends FrameLayout {
    private ListView listviewNote;
    private BookNoteAdapter adapterNote;
    private LinearLayout linearlayoutEmpty;

    /**
     * 构造
     *
     * @param context context
     */
    public ReaderNoteView(Context context) {
        this(context, null);
    }

    /**
     * 构造
     *
     * @param context context
     * @param attrs   attrs
     */
    public ReaderNoteView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    private void initView(Context context) {
        LayoutInflater.from(context).inflate(R.layout.view_reader_note, this, true);

        listviewNote = findViewById(R.id.listView_note);
        linearlayoutEmpty = findViewById(R.id.rl_note_empty);

        adapterNote = new BookNoteAdapter(getContext(), linearlayoutEmpty);
        listviewNote.setAdapter(adapterNote);
    }

    /**
     * 添加数据
     *
     * @param list  list
     * @param clear clear
     */
    public void addItem(List<BookMarkNew> list, boolean clear) {
        adapterNote.addItem(list, clear);
    }

    /**
     * 删除数据
     *
     * @param bookNote bookNote
     */
    public void deleteItem(BookMarkNew bookNote) {
        adapterNote.deleteItem(bookNote);
    }

    /**
     * 清除数据
     */
    public void clear() {
        adapterNote.clear();
    }
}
