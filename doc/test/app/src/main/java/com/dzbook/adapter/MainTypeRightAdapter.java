package com.dzbook.adapter;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.dzbook.activity.MainTypeDetailActivity;
import com.dzbook.lib.utils.StringUtil;
import com.dzbook.log.DzLog;
import com.dzbook.log.LogConstants;
import com.dzbook.utils.GlideImageLoadUtils;
import com.dzbook.utils.HwLog;
import com.ishugui.R;
import com.iss.view.common.ToastAlone;

import java.util.ArrayList;

import hw.sdk.net.bean.type.BeanMainTypeRight;

/**
 * MainTypeRightAdapter
 *
 * @author Winzows 2018/3/1
 */

public class MainTypeRightAdapter extends RecyclerView.Adapter<MainTypeRightAdapter.ViewHolder> {

    private static final long MIN_DELAY_TIME = 1000L;
    private long lastClickTime = 0;
    private ArrayList<BeanMainTypeRight> list = new ArrayList<>();
    private Context mContext;
    private String categoryId;
    private String categoryName;
    private int leftPosition;


    /**
     * 构造
     *
     * @param mContext mContext
     */
    public MainTypeRightAdapter(Context mContext) {
        this.mContext = mContext;
    }

    /**
     * 设置数据
     *
     * @param typeRights typeRights
     * @param id         id
     * @param name       name
     * @param pos        pos
     */
    public void putData(ArrayList<BeanMainTypeRight> typeRights, String id, String name, int pos) {
        this.list.clear();
        this.categoryId = id;
        this.categoryName = name;
        this.list.addAll(typeRights);
        this.leftPosition = pos;
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_native_type_index_right, parent, false);
        return new ViewHolder(convertView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if (null != list && list.size() > 0) {
            BeanMainTypeRight bean = list.get(position);
            setData(bean, holder, position);
        }
    }

    @Override
    public void onViewRecycled(ViewHolder holder) {
        Glide.with(mContext).clear(holder.imageView);
        GlideImageLoadUtils.getInstanse().glideImageLoadFromUrl(mContext, holder.imageView, null, 0);
        super.onViewRecycled(holder);
    }

    private void setData(final BeanMainTypeRight bean, final ViewHolder holder, final int position) {
        initData(holder);
        holder.tvName.setText(bean.title);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                long thisClickTime = System.currentTimeMillis();
                if (thisClickTime - lastClickTime < MIN_DELAY_TIME) {
                    return;
                }
                lastClickTime = thisClickTime;

                if (StringUtil.isEmpty(bean.cid, bean.title)) {
                    ToastAlone.showLong(R.string.load_data_failed);
                    return;
                }
                if (TextUtils.isEmpty(categoryId)) {
                    categoryId = "";
                }
                DzLog.getInstance().logClick(LogConstants.MODULE_FLYJ, categoryId, bean.cid, null, "");

                /**
                 * tabId 一级导航栏ID
                 * tabName 一级导航栏名称
                 * tabPos 一级导航栏位置顺序
                 * <p>
                 * pageId 频道ID
                 * pageName 频道名称
                 * pagePos  频道位置
                 * <p>
                 * columeID 栏目ID
                 * columeName 栏目名称
                 * columePos 栏目位置
                 * columeTemp 栏目类型
                 * <p>
                 * contentID 内容ID
                 * contentName 内容名称
                 * contentType 内容类型
                 */
                HwLog.columnClick(HwLog.getLogLinkedHashMap().get("type"), categoryId, categoryName + "", leftPosition + "", bean.cid, holder.tvName.getText().toString(), position + "", "type", "", "", "");

                MainTypeDetailActivity.launch(mContext, bean.title, bean.cid, categoryId);

            }
        });
        GlideImageLoadUtils.getInstanse().glideImageLoadFromUrlDefaultBookResSkipMemoryCache((Activity) mContext, holder.imageView, bean.imgUrl);
    }

    private void initData(ViewHolder holder) {
        holder.tvName.setText("");
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
         * imageview
         */
        public ImageView imageView;
        /**
         * textview
         */
        public TextView tvName;

        /**
         * ViewHolder构造
         *
         * @param itemView itemView
         */
        public ViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageView);
            tvName = itemView.findViewById(R.id.tv_name);
        }
    }

}
