package com.dzbook.templet.adapter;

import android.content.Context;
import android.view.ViewGroup;

import com.alibaba.android.vlayout.DelegateAdapter;
import com.alibaba.android.vlayout.LayoutHelper;
import com.alibaba.android.vlayout.layout.LinearLayoutHelper;
import com.dzbook.mvp.presenter.TempletPresenter;
import com.dzbook.view.store.LimitFreeTitleView;

import java.util.List;

import hw.sdk.net.bean.store.BeanSubTempletInfo;
import hw.sdk.net.bean.store.BeanTempletInfo;

/**
 * Adapter
 *
 * @author dongdianzhou on 2018/3/16.
 */

public class Xm0Adapter {

    private Context mContext;
    private TempletPresenter mPresenter;
    private BeanTempletInfo info;

    private int templetPosition;
    private int hasFlOrBannerTab;
    private SjSingleBookViewVAdapter singleBookViewVAdapter;

    Xm0Adapter(Context context, TempletPresenter templetPresenter, int templetPosition, int hasFlOrBannerTab) {
        mContext = context;
        mPresenter = templetPresenter;
        this.templetPosition = templetPosition;
        this.hasFlOrBannerTab = hasFlOrBannerTab;
    }

    /**
     * 添加数据
     *
     * @param adapters adapters
     * @param info1    info
     */
    public void addXM0Adapter(List<DelegateAdapter.Adapter> adapters, BeanTempletInfo info1) {
        if (info1 == null || info1.items == null) {
            return;
        }
        this.info = info1;
        if (hasFlOrBannerTab == 0) {
            adapters.add(new Xm01Adapter());
        } else if (hasFlOrBannerTab == 1) {
            if (templetPosition == 1) {
                adapters.add(new Xm01NoFlTabAdapter());
            } else {
                adapters.add(new Xm01Adapter());
            }
        } else if (hasFlOrBannerTab == 2) {
            adapters.add(new Xm01Adapter());
        } else {
            if (templetPosition == 0) {
                adapters.add(new Xm01NoFlTabAdapter());
            } else {
                adapters.add(new Xm01Adapter());
            }
        }

        //        if (info.items.size() > 1) {
        //            for (int i = 0; i < info.items.size(); i++) {
        //                BeanSubTempletInfo subTempletInfo = info.items.get(i);
        //                if (null != subTempletInfo) {
        //                    subTempletInfo.isXm0Selected = i == 0;
        //                }
        //            }
        //            xm02Adapter = new Xm02Adapter();
        //            adapters.add(xm02Adapter);
        //        }
        if (info1.items.size() > 0) {
            BeanSubTempletInfo subTempletInfo = info1.items.get(0);
            if (subTempletInfo != null) {
                singleBookViewVAdapter = new SjSingleBookViewVAdapter(mContext, mPresenter, info1, true, TempletPresenter.LOG_CLICK_ACTION_XM0, templetPosition);
                singleBookViewVAdapter.setList(subTempletInfo.items, false);
                adapters.add(singleBookViewVAdapter);
            }
        }
    }

    /**
     * Adapter
     */
    class Xm01Adapter extends DelegateAdapter.Adapter<MainStoreViewHolder> {

        @Override
        public LayoutHelper onCreateLayoutHelper() {
            return new LinearLayoutHelper();
        }

        @Override
        public MainStoreViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new MainStoreViewHolder(new LimitFreeTitleView(mContext, mPresenter, templetPosition, true));
        }

        @Override
        public void onBindViewHolder(MainStoreViewHolder holder, int position) {
            holder.bindLfTitleView(info);
        }

        @Override
        public int getItemCount() {
            return 1;
        }

        @Override
        public int getItemViewType(int position) {
            return StoreAdapterConstant.VIEW_TYPE_LIMITFREETITLE;
        }
    }

    /**
     * Adapter
     */
    class Xm01NoFlTabAdapter extends DelegateAdapter.Adapter<MainStoreViewHolder> {

        @Override
        public LayoutHelper onCreateLayoutHelper() {
            return new LinearLayoutHelper();
        }

        @Override
        public MainStoreViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new MainStoreViewHolder(new LimitFreeTitleView(mContext, mPresenter, templetPosition, false));
        }

        @Override
        public void onBindViewHolder(MainStoreViewHolder holder, int position) {
            holder.bindLfTitleView(info);
        }

        @Override
        public int getItemCount() {
            return 1;
        }

        @Override
        public int getItemViewType(int position) {
            return StoreAdapterConstant.VIEW_TYPE_LIMITFREETITLE_NOFL;
        }
    }


}
