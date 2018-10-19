package com.dzbook.view.reader;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.dzbook.activity.reader.BookMarkAdapter;
import com.dzbook.database.bean.BookMarkNew;
import com.ishugui.R;

import java.util.List;

/**
 * 书签
 *
 * @author wxliao on 17/8/17.
 */
public class ReaderMarkView extends FrameLayout {
    private ListView listviewMark;
    private BookMarkAdapter adapterMark;
    private LinearLayout linearlayoutEmpty;

    /**
     * 构造
     *
     * @param context context
     */
    public ReaderMarkView(Context context) {
        this(context, null);
    }

    /**
     * 构造
     *
     * @param context context
     * @param attrs   attrs
     */
    public ReaderMarkView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    private void initView(Context context) {
        LayoutInflater.from(context).inflate(R.layout.view_reader_mark, this, true);
        listviewMark = findViewById(R.id.listView_mark);
        linearlayoutEmpty = findViewById(R.id.rl_mark_empty);

        adapterMark = new BookMarkAdapter(getContext(), linearlayoutEmpty);
        listviewMark.setAdapter(adapterMark);
    }

    /**
     * 添加数据
     *
     * @param list  list
     * @param clear clear
     */
    public void addItem(List<BookMarkNew> list, boolean clear) {
        adapterMark.addItem(list, clear);
    }

    /**
     * 删除数据
     *
     * @param bookMark bookMark
     */
    public void deleteItem(BookMarkNew bookMark) {
        adapterMark.deleteItem(bookMark);
    }

    /**
     * 清除数据
     */
    public void clearItem() {
        adapterMark.clear();
    }
}
