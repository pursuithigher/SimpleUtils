package com.dzbook.templet.adapter;

import android.content.Context;
import android.view.ViewGroup;

import com.alibaba.android.vlayout.DelegateAdapter;
import com.alibaba.android.vlayout.LayoutHelper;
import com.alibaba.android.vlayout.layout.LinearLayoutHelper;
import com.dzbook.mvp.presenter.TempletPresenter;
import com.dzbook.view.store.SjMoreTitleView;

import hw.sdk.net.bean.store.BeanTempletInfo;

/**
 * Adapter
 *
 * @author dongdianzhou on 2018/3/21.
 */

public class SjMoreTitleAdapter extends DelegateAdapter.Adapter<MainStoreViewHolder> {

    private Context mContext;
    private BeanTempletInfo info;
    private TempletPresenter mPresenter;
    private int clickAction;
    private int clickType;

    private int templetPosition;
    private boolean hasFlTab = false;

    /**
     * 构造函数
     *
     * @param context          context
     * @param info             info
     * @param templetPresenter templetPresenter
     * @param clickAction      clickAction
     * @param clickType        clickType
     * @param templetPosition  templetPosition
     * @param hasFlTab         hasFlTab
     */
    public SjMoreTitleAdapter(Context context, BeanTempletInfo info, TempletPresenter templetPresenter, int clickAction, int clickType, int templetPosition, boolean hasFlTab) {
        mContext = context;
        this.templetPosition = templetPosition;
        this.info = info;
        mPresenter = templetPresenter;
        this.clickAction = clickAction;
        this.clickType = clickType;
        this.hasFlTab = hasFlTab;
    }

    @Override
    public LayoutHelper onCreateLayoutHelper() {
        LinearLayoutHelper layoutHelper = new LinearLayoutHelper();
        return layoutHelper;
    }

    @Override
    public MainStoreViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MainStoreViewHolder(new SjMoreTitleView(mContext, hasFlTab));
    }

    @Override
    public void onBindViewHolder(MainStoreViewHolder holder, int position) {
        holder.bindSjMoreTitleData(info, mPresenter, clickAction, clickType, templetPosition);
    }

    @Override
    public int getItemCount() {
        return 1;
    }

    @Override
    public int getItemViewType(int position) {
        if (hasFlTab) {
            return StoreAdapterConstant.VIEW_TYPE_SJMORETITLE;
        } else {
            return StoreAdapterConstant.VIEW_TYPE_SJMORETITLE_NOFL;
        }
    }
}
