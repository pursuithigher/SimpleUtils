package com.dzbook.templet.adapter;

import android.content.Context;
import android.view.ViewGroup;

import com.alibaba.android.vlayout.LayoutHelper;
import com.alibaba.android.vlayout.layout.LinearLayoutHelper;
import com.dzbook.mvp.presenter.TempletPresenter;
import com.dzbook.utils.DimensionPixelUtil;
import com.dzbook.view.store.Xslb0ImageView;

import hw.sdk.net.bean.store.BeanTempletInfo;

/**
 * Adapter
 */
public class Xhlb0Adapter extends DzAdapter<MainStoreViewHolder> {

    private TempletPresenter templetPresenter;
    private Context mContext;

    /**
     * 构造函数
     *
     * @param context          context
     * @param templetInfo      templetInfo
     * @param templetPresenter templetPresenter
     */
    public Xhlb0Adapter(Context context, BeanTempletInfo templetInfo, TempletPresenter templetPresenter) {
        super(templetInfo);
        this.templetPresenter = templetPresenter;
        mContext = context;
    }

    @Override
    public LayoutHelper onCreateLayoutHelper() {
        LinearLayoutHelper linearLayoutHelper = new LinearLayoutHelper();
        int padding = DimensionPixelUtil.dip2px(mContext, 16);
        linearLayoutHelper.setPadding(padding, padding, padding, 0);
        return linearLayoutHelper;
    }

    @Override
    public MainStoreViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MainStoreViewHolder(new Xslb0ImageView(mContext));
    }

    @Override
    public void onBindViewHolder(MainStoreViewHolder holder, int position) {
        holder.bindXslb0Data(templetInfo, templetPresenter);
    }

    @Override
    public int getItemViewType(int position) {
        return StoreAdapterConstant.VIEW_TYPE_XSLB0;
    }

    @Override
    public int getItemCount() {
        return 1;
    }
}
