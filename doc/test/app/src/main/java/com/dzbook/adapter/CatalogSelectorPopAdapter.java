package com.dzbook.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.dzbook.utils.ListUtils;
import com.dzbook.utils.ViewHolder;
import com.ishugui.R;

import java.util.ArrayList;
import java.util.List;

import hw.sdk.net.bean.BeanBlock;

/**
 * CatalogSelectorPopAdapter
 *
 * @author gavin
 */
public class CatalogSelectorPopAdapter extends BaseAdapter {
    /**
     * 上下文对象
     */
    public Context context;
    private final ArrayList<BeanBlock> list;

    /**
     * 构造
     *
     * @param context context
     */
    public CatalogSelectorPopAdapter(Context context) {
        this.context = context;
        list = new ArrayList<>();
    }

    @Override
    public BeanBlock getItem(int position) {
        if (!ListUtils.isEmpty(list)) {
            return list.get(position);
        }
        return null;
    }


    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getCount() {
        if (!ListUtils.isEmpty(list)) {
            return list.size();
        }
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = View.inflate(context, R.layout.dialog_pop_select_item, null);
        }
        TextView tvChapterName = ViewHolder.get(convertView, R.id.tv_chapter_name);
        TextView mLine = ViewHolder.get(convertView, R.id.tv_chapter_end);
        BeanBlock bean = list.get(position);
        tvChapterName.setText(bean.tip);

        if (position == list.size() - 1) {
            mLine.setVisibility(View.GONE);
        } else {
            mLine.setVisibility(View.VISIBLE);
        }
        if (position == 0) {
            tvChapterName.setBackgroundResource(R.drawable.com_pop_item_selector3);
        } else if (position == list.size() - 1) {
            tvChapterName.setBackgroundResource(R.drawable.com_pop_item_selector4);
        } else {
            tvChapterName.setBackgroundResource(R.drawable.com_pop_item_selector);
        }
        return convertView;
    }

    /**
     * 重置数据
     *
     * @param myBlockStrings myBlockStrings
     */
    public void resetData(List<BeanBlock> myBlockStrings) {
        if (!ListUtils.isEmpty(list)) {
            if (list.size() > 0) {
                list.clear();
            }
        }
        list.addAll(myBlockStrings);
        notifyDataSetChanged();
    }

}
