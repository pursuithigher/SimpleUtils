package com.dzbook.activity.reader;

import android.content.Context;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;

import com.iss.bean.BaseBean;

import java.util.ArrayList;
import java.util.List;

/**
 * BaseMarkAndNoteAdapter
 *
 * @param <T> BaseBean
 * @author winzows 2018/6/27
 */
public abstract class BaseMarkAndNoteAdapter<T extends BaseBean> extends BaseAdapter {

    /**
     * 数据集合
     */
    public ArrayList<T> beanList;
    /**
     * 上下文对象
     */
    public Context mContext;

    /**
     * 空布局
     */
    public LinearLayout linearLayoutEmpty;


    /**
     * 添加数据
     *
     * @param list  list
     * @param clear clear
     */
    public void addItem(List<T> list, boolean clear) {
        if (clear) {
            beanList.clear();
        }
        beanList.addAll(list);
        notifyDataSetChanged();
    }

    /**
     * 删除数据
     *
     * @param bean bean
     */
    public void deleteItem(T bean) {
        beanList.remove(bean);
        notifyDataSetChanged();
    }

    /**
     * 清空
     */
    public void clear() {
        beanList.clear();
        notifyDataSetChanged();
    }

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
        if (beanList.size() > 0) {
            linearLayoutEmpty.setVisibility(View.GONE);
        } else {
            linearLayoutEmpty.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public int getCount() {
        return beanList.size();
    }

    @Override
    public T getItem(int position) {
        return beanList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

}
