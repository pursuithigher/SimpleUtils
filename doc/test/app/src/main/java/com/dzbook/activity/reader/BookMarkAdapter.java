package com.dzbook.activity.reader;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.dzbook.database.bean.BookMarkNew;
import com.dzbook.view.reader.ReaderMarkItemView;

import java.util.ArrayList;

/**
 * BookMarkAdapter
 *
 * @author wangjianchen
 */
public class BookMarkAdapter extends BaseMarkAndNoteAdapter<BookMarkNew> {

    /**
     * 构造
     *
     * @param context context
     * @param layout  layout
     */
    public BookMarkAdapter(Context context, LinearLayout layout) {
        mContext = context;
        linearLayoutEmpty = layout;
        beanList = new ArrayList<>();
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ReaderMarkItemView view;
        if (convertView == null) {
            view = new ReaderMarkItemView(mContext);
        } else {
            view = (ReaderMarkItemView) convertView;
        }
        BookMarkNew bookMark = getItem(position);
        view.setData(bookMark);
        return view;
    }
}
