package com.dzbook.activity.reader;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.dzbook.database.bean.BookMarkNew;
import com.dzbook.view.reader.ReaderNoteItemView;

import java.util.ArrayList;

/**
 * 笔记 Adapter
 *
 * @author gavin
 */
public class BookNoteAdapter extends BaseMarkAndNoteAdapter<BookMarkNew> {

    /**
     * 构造
     *
     * @param context context
     * @param layout  layout
     */
    public BookNoteAdapter(Context context, LinearLayout layout) {
        mContext = context;
        linearLayoutEmpty = layout;
        beanList = new ArrayList<>();
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ReaderNoteItemView view;
        if (convertView == null) {
            view = new ReaderNoteItemView(mContext);
        } else {
            view = (ReaderNoteItemView) convertView;
        }
        BookMarkNew bookNote = getItem(position);
        view.bindData(bookNote);
        return view;
    }
}
