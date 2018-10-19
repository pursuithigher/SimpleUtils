package com.dzbook.view.reader;

import android.content.Context;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dzbook.activity.reader.ReaderCatalogActivity;
import com.dzbook.database.bean.BookMarkNew;
import com.dzbook.r.util.ConvertUtils;
import com.ishugui.R;

/**
 * 阅读笔记item
 *
 * @author wxliao on 17/12/8.
 */
public class ReaderNoteItemView extends LinearLayout {
    private TextView textviewChaptername;
    private TextView textviewShowtext;
    private TextView textviewNotetext;
    private LinearLayout layoutNote;

    private BookMarkNew mBookNote;

    /**
     * 构造
     *
     * @param context context
     */
    public ReaderNoteItemView(Context context) {
        this(context, null);
    }

    /**
     * 构造
     *
     * @param context context
     * @param attrs   attrs
     */
    public ReaderNoteItemView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    private void initView(Context context) {
        float num1 = 16;
        float num2 = 17;
        setOrientation(VERTICAL);
        setBackgroundResource(R.drawable.selector_hw_list_item);
        int padding = ConvertUtils.dp2px(context, num1);
        int paddingTop = ConvertUtils.dp2px(context, num2);
        setPadding(padding, paddingTop, padding, 0);
        LayoutInflater.from(context).inflate(R.layout.a_item_note, this, true);
        textviewChaptername = findViewById(R.id.textView_chapterName);
        textviewShowtext = findViewById(R.id.textView_showText);
        textviewNotetext = findViewById(R.id.textView_noteText);
        layoutNote = findViewById(R.id.layout_note);
        setLayoutParams(new AbsListView.LayoutParams(AbsListView.LayoutParams.MATCH_PARENT, AbsListView.LayoutParams.WRAP_CONTENT));
        setListener();
    }

    private void setListener() {
        setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                ((ReaderCatalogActivity) getContext()).onBookNoteItemClick(mBookNote);
            }
        });

        setOnLongClickListener(new OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                ((ReaderCatalogActivity) getContext()).onBookNoteItemLongClick(mBookNote);
                return true;
            }
        });
    }

    /**
     * 设置数据
     *
     * @param bookNote bookNote
     */
    public void bindData(BookMarkNew bookNote) {
        mBookNote = bookNote;

        textviewChaptername.setText(bookNote.chapterName);
        textviewShowtext.setText(bookNote.showText);
        if (TextUtils.isEmpty(bookNote.noteText)) {
            layoutNote.setVisibility(View.GONE);
        } else {
            layoutNote.setVisibility(View.VISIBLE);
            textviewNotetext.setText(bookNote.noteText);
        }
    }
}
