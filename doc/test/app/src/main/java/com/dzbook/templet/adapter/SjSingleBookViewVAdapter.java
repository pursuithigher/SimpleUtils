package com.dzbook.templet.adapter;

import android.content.Context;
import android.view.ViewGroup;

import com.alibaba.android.vlayout.DelegateAdapter;
import com.alibaba.android.vlayout.LayoutHelper;
import com.alibaba.android.vlayout.layout.GridLayoutHelper;
import com.dzbook.mvp.presenter.TempletPresenter;
import com.dzbook.utils.DeviceUtils;
import com.dzbook.utils.DimensionPixelUtil;
import com.dzbook.view.store.SigleBooKViewV;

import java.util.List;

import hw.sdk.net.bean.store.BeanSubTempletInfo;
import hw.sdk.net.bean.store.BeanTempletInfo;

/**
 * Adapter
 *
 * @author dongdianzhou on 2018/3/22.
 */

public class SjSingleBookViewVAdapter extends DelegateAdapter.Adapter<MainStoreViewHolder> {

    private Context mContext;
    private TempletPresenter mPresenter;
    private BeanTempletInfo info;
    private boolean isLimitFree;
    private int clickAction;

    private List<BeanSubTempletInfo> list;

    private int templetPosition;

    /**
     * 构造函数
     *
     * @param context          context
     * @param templetPresenter templetPresenter
     * @param templetInfo      templetInfo
     * @param isLimitFree      isLimitFree
     * @param clickAction      clickAction
     * @param templetPosition  templetPosition
     */
    public SjSingleBookViewVAdapter(Context context, TempletPresenter templetPresenter, BeanTempletInfo templetInfo, boolean isLimitFree, int clickAction, int templetPosition) {
        this.templetPosition = templetPosition;
        mContext = context;
        mPresenter = templetPresenter;
        info = templetInfo;
        list = templetInfo.items;
        this.isLimitFree = isLimitFree;
        this.clickAction = clickAction;
    }

    /**
     * 设置数据
     *
     * @param list1       list
     * @param isReference isReference
     */
    public void setList(List<BeanSubTempletInfo> list1, boolean isReference) {
        this.list = list1;
        if (isReference) {
            notifyDataSetChanged();
        }
    }

    @Override
    public void onViewRecycled(MainStoreViewHolder holder) {
        holder.clearVImageView();
        super.onViewRecycled(holder);
    }

    @Override
    public LayoutHelper onCreateLayoutHelper() {
        GridLayoutHelper gridLayoutHelper = new GridLayoutHelper(3);
        int padding = DimensionPixelUtil.dip2px(mContext, 16);
        int topPadding = DimensionPixelUtil.dip2px(mContext, 8);
        gridLayoutHelper.setPadding(padding, topPadding, padding, 0);
        if (DeviceUtils.isPad(mContext)) {
            int hgap = DimensionPixelUtil.dip2px(mContext, 11);
            gridLayoutHelper.setHGap(hgap);
            int vgap = DimensionPixelUtil.dip2px(mContext, 21);
            gridLayoutHelper.setVGap(vgap);
        } else {
            int hgap = DimensionPixelUtil.dip2px(mContext, 8);
            gridLayoutHelper.setHGap(hgap);
            int vgap = DimensionPixelUtil.dip2px(mContext, 16);
            gridLayoutHelper.setVGap(vgap);
        }
        gridLayoutHelper.setAutoExpand(false);
        return gridLayoutHelper;
    }

    @Override
    public MainStoreViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MainStoreViewHolder(new SigleBooKViewV(mContext));
    }

    @Override
    public void onBindViewHolder(MainStoreViewHolder holder, int position) {
        if (position < list.size()) {
            BeanSubTempletInfo sub = list.get(position);
            if (sub != null) {
                holder.bindSingleBookViewVData(sub, info, mPresenter, isLimitFree, clickAction, templetPosition);
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
