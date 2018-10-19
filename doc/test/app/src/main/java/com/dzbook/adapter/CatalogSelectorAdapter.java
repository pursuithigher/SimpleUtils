package com.dzbook.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.dzbook.utils.ListUtils;
import com.dzbook.utils.ViewHolder;
import com.ishugui.R;

import java.util.ArrayList;
import java.util.List;

import hw.sdk.net.bean.BeanBlock;

/**
 * CatalogSelectorAdapter
 *
 * @author gavin
 */
public class CatalogSelectorAdapter extends BaseAdapter {
    /**
     * 上下文对象
     */
    public Context context;
    private final ArrayList<BeanBlock> list;
    private int checked = 0;

    /**
     * 构造
     *
     * @param context context
     */
    public CatalogSelectorAdapter(Context context) {
        this.context = context;
        list = new ArrayList<>();
    }

    @Override
    public int getCount() {
        if (!ListUtils.isEmpty(list)) {
            return list.size();
        }
        return 0;
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
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = View.inflate(context, R.layout.dialog_catalog_select_item, null);
        }
        ImageView chapterIv = ViewHolder.get(convertView, R.id.chapter_iv);
        TextView tvChapterName = ViewHolder.get(convertView, R.id.tv_chapter_name);
        TextView mLine = ViewHolder.get(convertView, R.id.tv_chapter_end);
        if (position == checked) {
            chapterIv.setImageResource(R.drawable.circle_red_bg);
        } else {
            chapterIv.setImageResource(R.drawable.ic_hw_single_default);
        }

        BeanBlock bean = list.get(position);
        tvChapterName.setText(bean.tip);

        if (position == list.size() - 1) {
            mLine.setVisibility(View.GONE);
        } else {
            mLine.setVisibility(View.VISIBLE);
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

    /**
     * 设置选中
     *
     * @param checkedItem checkedItem
     */
    public void setChecked(int checkedItem) {
        checked = checkedItem;
        notifyDataSetChanged();
    }

}
