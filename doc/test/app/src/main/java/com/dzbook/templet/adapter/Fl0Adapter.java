package com.dzbook.templet.adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.view.ViewGroup;

import com.alibaba.android.vlayout.LayoutHelper;
import com.alibaba.android.vlayout.layout.GridLayoutHelper;
import com.dzbook.mvp.presenter.TempletPresenter;
import com.dzbook.utils.DimensionPixelUtil;
import com.dzbook.view.store.ModuleItemView;

import java.util.List;

import hw.sdk.net.bean.store.BeanSubTempletInfo;
import hw.sdk.net.bean.store.BeanTempletInfo;

/**
 * Adapter
 *
 * @author dongdianzhou on 2018/2/4.
 */

public class Fl0Adapter extends DzAdapter<MainStoreViewHolder> {

    private Context mContext;
    private Fragment mFragment;
    private TempletPresenter mPresenter;
    private GridLayoutHelper mLayoutHelper;
    private List<BeanSubTempletInfo> list;

    private int templetPosition;

    /**
     * 构造函数
     *
     * @param templetInfo      templetInfo
     * @param context          context
     * @param fragment         fragment
     * @param templetPresenter templetPresenter
     * @param templetPosition  templetPosition
     */
    public Fl0Adapter(BeanTempletInfo templetInfo, Context context, Fragment fragment, TempletPresenter templetPresenter, int templetPosition) {
        super(templetInfo);
        this.templetPosition = templetPosition;
        mContext = context;
        mFragment = fragment;
        mPresenter = templetPresenter;
        List<BeanSubTempletInfo> list1 = templetInfo.items.subList(0, Math.min(5, templetInfo.items.size()));
        mLayoutHelper = new GridLayoutHelper(list1.size());
        this.list = list1;
    }

    @Override
    public LayoutHelper onCreateLayoutHelper() {
        //        mLayoutHelper.setPaddingTop(DimensionPixelUtil.dip2px(mContext, 8));
        mLayoutHelper.setPaddingBottom(DimensionPixelUtil.dip2px(mContext, 7));
        int paddingLeft = DimensionPixelUtil.dip2px(mContext, 28);
        mLayoutHelper.setPaddingLeft(paddingLeft);
        mLayoutHelper.setPaddingRight(paddingLeft);
        mLayoutHelper.setHGap(DimensionPixelUtil.dip2px(mContext, 8));
        return mLayoutHelper;
    }

    @Override
    public MainStoreViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MainStoreViewHolder(new ModuleItemView(mContext, templetPosition));
    }

    @Override
    public void onBindViewHolder(MainStoreViewHolder holder, int position) {
        if (position < list.size()) {
            BeanSubTempletInfo sub = list.get(position);
            if (sub != null) {
                holder.bindFl0Data(templetInfo, sub, mFragment, mPresenter);
            }
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    @Override
    public int getItemViewType(int position) {
        return StoreAdapterConstant.VIEW_TYPE_MDIV;
    }
}
