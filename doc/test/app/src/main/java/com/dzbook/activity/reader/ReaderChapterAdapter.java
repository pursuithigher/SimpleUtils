package com.dzbook.activity.reader;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.dzbook.database.bean.BookInfo;
import com.dzbook.database.bean.CatalogInfo;
import com.dzbook.utils.DBUtils;
import com.dzbook.view.reader.ReaderChapterItemView;
import com.ishugui.R;

import java.util.ArrayList;
import java.util.List;

/**
 * 阅读目录adapter
 *
 * @author liaowx
 */
public class ReaderChapterAdapter extends BaseAdapter {
    private Context mContext;
    private ArrayList<CatalogInfo> beanList;
    private TextView mTextView;
    private BookInfo bookInfo;

    /**
     * 构造
     *
     * @param context  context
     * @param textView textView
     */
    public ReaderChapterAdapter(Context context, TextView textView) {
        mContext = context;
        mTextView = textView;
        beanList = new ArrayList<>();
    }

    /**
     * 添加数据
     *
     * @param list  list
     * @param clear 添加前，是否清空
     */
    public void addItem(List<CatalogInfo> list, boolean clear) {
        if (clear) {
            beanList.clear();
        }
        if (list != null) {
            beanList.addAll(list);
        }
        notifyDataSetChanged();
    }

    /**
     * id 换 index
     *
     * @param id id
     * @return int
     */
    public int getIndex(String id) {
        for (int i = 0; i < beanList.size(); i++) {
            if (TextUtils.equals(id, beanList.get(i).catalogid)) {
                return i;
            }
        }
        return -1;
    }

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
        if (beanList.size() > 0) {
            mTextView.setVisibility(View.GONE);
        } else {
            mTextView.setText(R.string.no_catalogues);
            mTextView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public int getCount() {
        return beanList.size();
    }

    @Override
    public CatalogInfo getItem(int position) {
        return beanList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ReaderChapterItemView view;
        if (convertView == null) {
            view = new ReaderChapterItemView(mContext);
        } else {
            view = (ReaderChapterItemView) convertView;
        }
        CatalogInfo bean = getItem(position);
        if (null == bookInfo) {
            bookInfo = DBUtils.findByBookId(mContext, bean.bookid);
        }
        view.setData(bean, bookInfo, position == beanList.size() - 1);
        return view;
    }
}
