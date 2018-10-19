package com.dzbook.view.reader;

import android.content.Context;
import android.graphics.Typeface;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dzbook.activity.reader.ReaderCatalogActivity;
import com.dzbook.activity.reader.ReaderUtils;
import com.dzbook.database.bean.BookInfo;
import com.dzbook.database.bean.CatalogInfo;
import com.dzbook.lib.utils.CompatUtils;
import com.dzbook.utils.DBUtils;
import com.ishugui.R;

/**
 * 图书详情目录的item
 *
 * @author zhenglk
 */
public class ReaderChapterItemView extends LinearLayout {

    private TextView textViewName;
    private CatalogInfo mBookMark;
    private TextView textviewIsfree;
    private View mLine;

    /**
     * 构造
     *
     * @param context context
     */
    public ReaderChapterItemView(Context context) {
        this(context, null);
    }

    /**
     * 构造
     *
     * @param context context
     * @param attrs   attrs
     */
    public ReaderChapterItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
        setListener();
    }

    private void init() {
        setOrientation(VERTICAL);
        setBackgroundResource(R.drawable.selector_hw_list_item);
        LayoutInflater.from(getContext()).inflate(R.layout.a_item_catalog, this);
        textViewName = findViewById(R.id.textView_name);
        textviewIsfree = findViewById(R.id.textView_isFree);
        mLine = findViewById(R.id.v_end_line);
        setLayoutParams(new AbsListView.LayoutParams(AbsListView.LayoutParams.MATCH_PARENT, AbsListView.LayoutParams.WRAP_CONTENT));
    }

    private void setListener() {
        setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                ((ReaderCatalogActivity) getContext()).onChapterItemClick(mBookMark);
            }
        });
    }

    /**
     * 设置数据
     *
     * @param bean          bean
     * @param bookInfo      bookInfo
     * @param isShowEndLine isShowEndLine
     */
    public void setData(CatalogInfo bean, BookInfo bookInfo, boolean isShowEndLine) {
        mBookMark = bean;
        CatalogInfo chapter = DBUtils.getCatalog(getContext(), bean.bookid, bean.catalogid);
        if (chapter != null) {
            mBookMark = chapter;
        }

        textviewIsfree.setVisibility(View.GONE);
        textViewName.setText(bean.catalogname);
        if (mBookMark.catalogid.equals(bookInfo.currentCatalogId)) {
            textViewName.setTextColor(CompatUtils.getColor(getContext(), R.color.color_100_CD2325));
            textViewName.setTextSize(15);
        } else {
            int color = CompatUtils.getColor(getContext(), R.color.black) & 0x00ffffff;
            if (ReaderUtils.allowOpenDirect(chapter)) {
                color |= 0xff000000;
            } else {
                color |= 0x80000000;
            }

            textViewName.setTextColor(color);
            textViewName.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));
            textViewName.setTextSize(14);
        }
        if (isShowEndLine) {
            mLine.setVisibility(GONE);
        } else {
            mLine.setVisibility(VISIBLE);
        }

        if (!TextUtils.isEmpty(bean.ispay)) {
            // 根据需要，显示目录的免费标记。
            final String isPay = "1";
            if (isPay.equals(bean.ispay)) {
                textviewIsfree.setVisibility(View.VISIBLE);
            }
        }
    }
}
