package com.dzbook.view.reader;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dzbook.activity.reader.ReaderCatalogActivity;
import com.dzbook.database.bean.BookMarkNew;
import com.dzbook.r.util.ConvertUtils;
import com.ishugui.R;

import java.text.SimpleDateFormat;

/**
 * 书签item
 *
 * @author gavin
 */
public class ReaderMarkItemView extends RelativeLayout {
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
    private TextView textviewContent;
    private TextView textviewRate;
    private TextView textviewTime;

    private BookMarkNew mBookMark;


    /**
     * 构造
     *
     * @param context context
     */
    public ReaderMarkItemView(Context context) {
        this(context, null);
    }

    /**
     * 构造
     *
     * @param context context
     * @param attrs   attrs
     */
    public ReaderMarkItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    /**
     * 初始化
     *
     * @param context context
     */
    public void init(Context context) {
        setBackgroundResource(R.drawable.selector_hw_list_item);
        int paddingTop = ConvertUtils.dp2px(context, 13f);
        int padding = ConvertUtils.dp2px(context, 16f);
        int paddingBottom = ConvertUtils.dp2px(context, 12f);
        setPadding(padding, paddingTop, padding, paddingBottom);
        LayoutInflater.from(context).inflate(R.layout.a_item_mark, this);
        textviewContent = findViewById(R.id.textView_content);
        textviewRate = findViewById(R.id.textView_rate);
        textviewTime = findViewById(R.id.textView_time);
        setLayoutParams(new AbsListView.LayoutParams(AbsListView.LayoutParams.MATCH_PARENT, AbsListView.LayoutParams.WRAP_CONTENT));
        setListener();
    }

    private void setListener() {
        setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                ((ReaderCatalogActivity) getContext()).onBookMarkItemClick(mBookMark);
            }
        });

        setOnLongClickListener(new OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                ((ReaderCatalogActivity) getContext()).onBookMarkItemLongClick(mBookMark);
                return true;
            }
        });
    }

    /**
     * 设置数据
     *
     * @param bean bean
     */
    public void setData(BookMarkNew bean) {
        mBookMark = bean;

        textviewContent.setText(mBookMark.showText);

        textviewRate.setText(mBookMark.percent);
        String timeStr = sdf.format(mBookMark.updateTime);
        textviewTime.setText(timeStr);
    }


}
