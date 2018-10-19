package com.dzbook.utils;

import android.util.SparseArray;
import android.view.View;

/**
 * ViewHolder
 *
 * @author gavin
 */
public class ViewHolder {
    /**
     * 通用ViewHolder
     *
     * @param view view
     * @param id   id
     * @param <T>  <T>
     * @return <T>
     */
    @SuppressWarnings("unchecked")
    public static <T extends View> T get(View view, int id) {
        SparseArray<View> viewHolder = (SparseArray<View>) view.getTag();
        if (viewHolder == null) {
            viewHolder = new SparseArray<View>();
            view.setTag(viewHolder);
        }
        View childView = viewHolder.get(id);
        if (childView == null) {
            childView = view.findViewById(id);
            viewHolder.put(id, childView);
        }
        return (T) childView;
    }
}