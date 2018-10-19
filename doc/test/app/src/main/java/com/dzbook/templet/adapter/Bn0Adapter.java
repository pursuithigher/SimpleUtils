package com.dzbook.templet.adapter;

import android.support.v4.app.Fragment;
import android.view.ViewGroup;

import com.alibaba.android.vlayout.LayoutHelper;
import com.alibaba.android.vlayout.layout.LinearLayoutHelper;
import com.dzbook.mvp.presenter.TempletPresenter;
import com.dzbook.view.store.Bn0View;

import hw.sdk.net.bean.store.BeanTempletInfo;

/**
 * Adapter
 *
 * @author dongdianzhou on 2018/3/15.
 */

public class Bn0Adapter extends DzAdapter<MainStoreViewHolder> {

    private Fragment mFragment;
    private TempletPresenter mPresenter;

    private boolean isReference;
    private int templetPosition;

    /**
     * 构造
     *
     * @param templetInfo templetInfo
     */
    public Bn0Adapter(BeanTempletInfo templetInfo) {
        super(templetInfo);
    }

    /**
     * 构造
     *
     * @param templetInfo      templetInfo
     * @param fragment         fragment
     * @param templetPresenter templetPresenter
     * @param isReference      isReference
     * @param templetPosition  templetPosition
     */
    public Bn0Adapter(BeanTempletInfo templetInfo, Fragment fragment, TempletPresenter templetPresenter, boolean isReference, int templetPosition) {
        super(templetInfo);
        this.isReference = isReference;
        this.templetPosition = templetPosition;
        mFragment = fragment;
        mPresenter = templetPresenter;
    }

    @Override
    public LayoutHelper onCreateLayoutHelper() {
        return new LinearLayoutHelper();
    }

    @Override
    public MainStoreViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case StoreAdapterConstant.VIEW_TYPE_BN0:
                return new MainStoreViewHolder(new Bn0View(parent.getContext(), mFragment, mPresenter, templetPosition));
            default:
                break;
        }
        return null;
    }

    @Override
    public void onBindViewHolder(MainStoreViewHolder holder, int position) {
        switch (getItemViewType(position)) {
            case StoreAdapterConstant.VIEW_TYPE_BN0:
                if (templetInfo != null) {
                    holder.bindBn0Data(templetInfo, isReference);
                    isReference = false;
                }
                break;
            default:
                break;
        }
    }

    @Override
    public int getItemCount() {
        return 1;
    }

    @Override
    public int getItemViewType(int position) {
        return StoreAdapterConstant.VIEW_TYPE_BN0;
    }
}
