package com.dzbook.templet.adapter;

import android.content.Context;
import android.view.ViewGroup;

import com.alibaba.android.vlayout.DelegateAdapter;
import com.alibaba.android.vlayout.LayoutHelper;
import com.alibaba.android.vlayout.layout.GridLayoutHelper;
import com.dzbook.mvp.presenter.TempletPresenter;
import com.dzbook.view.store.SigleBooKViewH;

import java.util.List;

import hw.sdk.net.bean.store.BeanSubTempletInfo;
import hw.sdk.net.bean.store.BeanTempletInfo;

/**
 * Adapter
 *
 * @author dongdianzhou on 2018/3/22.
 */

public class SjSingleBookViewHAdapter extends DelegateAdapter.Adapter<MainStoreViewHolder> {

    private Context mContext;
    private TempletPresenter mPresenter;
    private BeanTempletInfo info;
    private int clickAction;

    private List<BeanSubTempletInfo> list;

    private int templetPosition;

    /**
     * 构造函数
     *
     * @param context          context
     * @param templetPresenter templetPresenter
     * @param templetInfo      templetInfo
     * @param clickAction      clickAction
     * @param templetPosition  templetPosition
     */
    public SjSingleBookViewHAdapter(Context context, TempletPresenter templetPresenter, BeanTempletInfo templetInfo, int clickAction, int templetPosition) {
        mContext = context;
        mPresenter = templetPresenter;
        info = templetInfo;
        list = info.items;
        this.clickAction = clickAction;
        this.templetPosition = templetPosition;
    }

    @Override
    public void onViewRecycled(MainStoreViewHolder holder) {
        holder.clearHImageView();
        super.onViewRecycled(holder);
    }

    @Override
    public LayoutHelper onCreateLayoutHelper() {
        GridLayoutHelper gridLayoutHelper = new GridLayoutHelper(1);
        gridLayoutHelper.setAutoExpand(false);
        return gridLayoutHelper;
    }

    @Override
    public MainStoreViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MainStoreViewHolder(new SigleBooKViewH(mContext));
    }

    @Override
    public void onBindViewHolder(MainStoreViewHolder holder, int position) {
        if (position < list.size()) {
            BeanSubTempletInfo sub = list.get(position);
            if (sub != null) {
                if (position == getItemCount() - 1) {
                    holder.bindSingleBookViewHData(sub, info, mPresenter, clickAction, templetPosition, false);
                } else {
                    holder.bindSingleBookViewHData(sub, info, mPresenter, clickAction, templetPosition, true);
                }
            }
        }
    }

    @Override
    public int getItemCount() {
        if (list != null) {
            return Math.min(3, list.size());
        }
        return 0;
    }

    @Override
    public int getItemViewType(int position) {
        return StoreAdapterConstant.VIEW_TYPE_SBVH;
    }
}
