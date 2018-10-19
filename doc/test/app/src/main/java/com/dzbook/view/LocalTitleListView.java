package com.dzbook.view;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.dzbook.adapter.LocalFileAdapter;
import com.dzbook.bean.LocalFileBean;
import com.ishugui.R;

/**
 * 文件索引自定义ListView
 */
public class LocalTitleListView extends ListView {
    private View layoutTitle;
    private TextView textviewTitle;

    private int labelHeight = -1;//获取到导航条目的高度

    /**
     * 构造
     *
     * @param context context
     */
    public LocalTitleListView(Context context) {
        super(context);
        init(context);
    }

    /**
     * 构造
     *
     * @param context context
     * @param attrs   attrs
     */
    public LocalTitleListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    /**
     * 构造
     *
     * @param context  context
     * @param attrs    attrs
     * @param defStyle defStyle
     */
    public LocalTitleListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    private void init(Context context) {
        LayoutInflater aInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (aInflater != null) {
            layoutTitle = aInflater.inflate(R.layout.item_local_title, null);
            textviewTitle = layoutTitle.findViewById(R.id.local_title);
        }
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        if (getChildCount() <= 0) {
            return;
        }
        if (layoutTitle == null) {
            return;
        }
        try {
            int textTop;
            int textLeft;
            int textRight;
            int textButtom;
            int textOffsetY;
            int tmp;

            LocalFileBean fileItem;
            LocalFileAdapter localFileAdapter;
            View tempView = null;
            localFileAdapter = (LocalFileAdapter) getAdapter();
            int postion = getFirstVisiblePosition();
            fileItem = localFileAdapter.getItem(postion);
            int count = getChildCount();
            View child;
            for (int i = 0; i < count; i++) {
                child = getChildAt(i);
                if (child.getTag() instanceof LocalFileAdapter.TitleViewHolder) {
                    tempView = child;
                    labelHeight = child.getMeasuredHeight();
                    break;
                }
            }
            textLeft = getLeft() + getLeftPaddingOffset();
            textTop = getTop() + getTopPaddingOffset();
            textRight = getRight() - getRightPaddingOffset() - getPaddingRight();
            textButtom = textTop + labelHeight;
            textOffsetY = 0;
            tmp = 0;
            if (tempView != null) {
                tmp = tempView.getTop();
            }
            if (tmp > 0 && tmp < labelHeight) {
                textOffsetY = tmp - labelHeight;
            }
            if (fileItem != null) {
                String titleDesc;
                if (fileItem.fileType == LocalFileBean.TYPE_DIR) {
                    titleDesc = "文件夹";
                } else if (fileItem.sortType == LocalFileBean.TYPE_SORT_NAME) {
                    titleDesc = fileItem.firstLetter;
                } else {
                    titleDesc = fileItem.lastModifiedDesc;
                }
                textviewTitle.setText(titleDesc);
            }
            if (!(postion == 0 && tmp > 0)) {
                layoutTitle.measure(textRight - textLeft, labelHeight);
                layoutTitle.layout(textLeft, textTop, textRight, textButtom);
                canvas.save();
                canvas.translate(0, textOffsetY);
                layoutTitle.draw(canvas);
                canvas.restore();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
