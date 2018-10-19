package com.dzbook.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dzbook.utils.TypefaceUtils;
import com.ishugui.R;

import java.util.ArrayList;

import hw.sdk.net.bean.type.BeanMainTypeLeft;

/**
 * MainTypeLeftAdapter
 *
 * @author Winzows 2018/3/1
 */

public class MainTypeLeftAdapter extends RecyclerView.Adapter<MainTypeLeftAdapter.ViewHolder> {

    private ArrayList<BeanMainTypeLeft> list = new ArrayList<>();
    private OnItemClickListener onItemClickListener;
    private int selectedItem = 0;


    /**
     * 构造
     *
     * @param mContext mContext
     */
    public MainTypeLeftAdapter(Context mContext) {
    }

    /**
     * 设置数据
     *
     * @param leftArrayList leftArrayList
     */
    public void putData(ArrayList<BeanMainTypeLeft> leftArrayList) {
        this.list.clear();
        this.list.addAll(leftArrayList);
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_native_type_index_left, parent, false);
        return new ViewHolder(convertView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if (null != list && list.size() > 0) {
            BeanMainTypeLeft itemName = list.get(position);
            setData(itemName, holder, position);
        }

    }

    private void setData(final BeanMainTypeLeft itemName, ViewHolder holder, final int position) {
        if (itemName != null) {
            holder.tvName.setText(itemName.categoryName);
            holder.rlType.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onItemClickListener != null && selectedItem != position) {
                        onItemClickListener.onItemClickListener(itemName, position);
                        selectedItem = position;
                        notifyDataSetChanged();
                    }
                }
            });
            if (position == selectedItem) {
                holder.rlType.setSelected(true);
                holder.rlType.setBackgroundResource(R.color.color_full_white);
            } else {
                holder.rlType.setSelected(false);
                holder.rlType.setBackgroundResource(R.color.transparent);
            }
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    /**
     * ViewHolder
     */
    public class ViewHolder extends RecyclerView.ViewHolder {

        /**
         * TextView
         */
        public TextView tvName;
        /**
         * RelativeLayout
         */
        public RelativeLayout rlType;

        /**
         * 构造
         *
         * @param itemView itemView
         */
        public ViewHolder(View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tv_name);
            rlType = itemView.findViewById(R.id.rl_type);
            TypefaceUtils.setHwChineseMediumFonts(tvName);
        }
    }

    /**
     * 点击
     */
    public interface OnItemClickListener {
        /**
         * 点击事件接口
         *
         * @param listName     listName
         * @param leftPosition leftPosition
         */
        void onItemClickListener(BeanMainTypeLeft listName, int leftPosition);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }

    /**
     * 设置选中项
     *
     * @param item item
     */
    public void setSelectItem(int item) {
        if (item < 0) {
            item = 0;
        }
        if (item > list.size() - 1) {
            item = list.size() - 1;
        }
        this.selectedItem = item;
        notifyDataSetChanged();
    }
}
