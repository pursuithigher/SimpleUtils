package com.dzbook.vip.adapter;

import android.content.Context;
import android.view.ViewGroup;

import com.alibaba.android.vlayout.DelegateAdapter;
import com.alibaba.android.vlayout.LayoutHelper;
import com.alibaba.android.vlayout.layout.GridLayoutHelper;
import com.dzbook.templet.adapter.StoreAdapterConstant;
import com.dzbook.utils.DeviceUtils;
import com.dzbook.utils.DimensionPixelUtil;
import com.dzbook.view.vip.SigleBooKVipView;

import java.util.List;

import hw.sdk.net.bean.vip.VipBookInfo;


/**
 * VipSigleBookAdapter
 *
 * @author gavin
 */
public class VipSigleBookAdapter extends DelegateAdapter.Adapter<VipViewHolder> {

    private Context mContext;

    private List<VipBookInfo.BookBean> list;

    /**
     * 构造
     *
     * @param context context
     * @param list    list
     */
    public VipSigleBookAdapter(Context context, List<VipBookInfo.BookBean> list) {
        mContext = context;
        this.list = list;
    }


    @Override
    public void onViewRecycled(VipViewHolder holder) {
        super.onViewRecycled(holder);
    }

    @Override
    public LayoutHelper onCreateLayoutHelper() {
        GridLayoutHelper gridLayoutHelper = getGridLayoutHelper();
        return gridLayoutHelper;
    }

    private GridLayoutHelper getGridLayoutHelper() {
        GridLayoutHelper gridLayoutHelper = new GridLayoutHelper(3);
        int padding = DimensionPixelUtil.dip2px(mContext, 16);
        gridLayoutHelper.setPadding(padding, padding, padding, 0);
        if (DeviceUtils.isPad(mContext)) {
            handlePad(gridLayoutHelper, mContext, 11, 21);
        } else {
            handlePad(gridLayoutHelper, mContext, 8, 16);
        }
        gridLayoutHelper.setAutoExpand(false);
        return gridLayoutHelper;
    }

    private void handlePad(GridLayoutHelper gridLayoutHelper, Context mContext1, int dipLength, int dipLength2) {
        int hgap = DimensionPixelUtil.dip2px(mContext1, dipLength);
        gridLayoutHelper.setHGap(hgap);
        int vgap = DimensionPixelUtil.dip2px(mContext1, dipLength2);
        gridLayoutHelper.setVGap(vgap);
    }

    @Override
    public VipViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new VipViewHolder(new SigleBooKVipView(mContext));
    }

    @Override
    public void onBindViewHolder(VipViewHolder holder, int position) {
        if (position < list.size()) {
            VipBookInfo.BookBean bookBean = list.get(position);
            if (bookBean != null) {
                holder.bindBookData(bookBean);
            }
        }
    }

    @Override
    public int getItemCount() {
        if (list != null) {
            return Math.min(6, list.size());
        }
        return 0;
    }

    @Override
    public int getItemViewType(int position) {
        return StoreAdapterConstant.VIEW_TYPE_SBVV;
    }
}
