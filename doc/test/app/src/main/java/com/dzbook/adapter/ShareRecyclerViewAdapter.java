package com.dzbook.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.dzbook.bean.ShareBean;
import com.dzbook.utils.ListUtils;
import com.ishugui.R;

import java.util.ArrayList;

/**
 * ShareRecyclerViewAdapter
 */
public class ShareRecyclerViewAdapter extends RecyclerView.Adapter<ShareRecyclerViewAdapter.ItemViewHolder> {
    private Context context;

    private ArrayList<ShareBean> datas;

    private OnItemClickListener mOnItemClickListener;

    /**
     * 构造
     *
     * @param context context
     */
    public ShareRecyclerViewAdapter(Context context) {
        super();
        this.context = context;
        datas = new ArrayList<>();
    }

    @Override
    public ItemViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = View.inflate(context, R.layout.dialog_horinazal_item, null);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ItemViewHolder viewHolder, final int pos) {
        final ShareBean shareBean = datas.get(pos);
        viewHolder.imageIcon.setImageDrawable(shareBean.drawable);
        viewHolder.textTitle.setText(shareBean.title);
        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mOnItemClickListener) {
                    mOnItemClickListener.onitemclick(v, shareBean);
                }
            }
        });
    }

    public void setOnItemClickListener(OnItemClickListener clickListener) {
        this.mOnItemClickListener = clickListener;
    }

    /**
     * 接口
     */
    public interface OnItemClickListener {
        /**
         * 点击
         *
         * @param view      view
         * @param shareBean shareBean
         */
        void onitemclick(View view, ShareBean shareBean);
    }

    @Override
    public int getItemCount() {
        return datas == null ? 0 : datas.size();
    }

    /**
     * 设置数据
     *
     * @param beans beans
     */
    public void setData(ArrayList<ShareBean> beans) {
        if (!ListUtils.isEmpty(beans)) {
            this.datas.clear();
            this.datas.addAll(beans);
        }
        notifyDataSetChanged();
    }

    /**
     * ViewHolde
     */
    class ItemViewHolder extends RecyclerView.ViewHolder {
        public ImageView imageIcon;
        public TextView textTitle;

        public ItemViewHolder(View itemView) {
            super(itemView);
            imageIcon = itemView.findViewById(R.id.icon);
            textTitle = itemView.findViewById(R.id.title);
        }
    }
}
