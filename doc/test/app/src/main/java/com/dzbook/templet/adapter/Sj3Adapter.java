package com.dzbook.templet.adapter;

import android.content.Context;

import com.alibaba.android.vlayout.DelegateAdapter;
import com.dzbook.mvp.presenter.TempletPresenter;

import java.util.List;

import hw.sdk.net.bean.store.BeanTempletInfo;

/**
 * Adapter
 *
 * @author dongdianzhou on 2018/3/16.
 */

public class Sj3Adapter {

    private Context mContext;
    private TempletPresenter mPresenter;

    private int templetPosition;

    private int hasFlOrBannerTab;

    /**
     * 构造函数
     *
     * @param context          context
     * @param templetPresenter templetPresenter
     * @param templetPosition  templetPosition
     * @param hasFlOrBannerTab hasFlOrBannerTab
     */
    public Sj3Adapter(Context context, TempletPresenter templetPresenter, int templetPosition, int hasFlOrBannerTab) {
        mContext = context;
        mPresenter = templetPresenter;
        this.hasFlOrBannerTab = hasFlOrBannerTab;
        this.templetPosition = templetPosition;
    }

    /**
     * 添加数据
     *
     * @param adapters adapters
     * @param info     info
     */
    public void addSj3Adapter(List<DelegateAdapter.Adapter> adapters, BeanTempletInfo info) {
        if (hasFlOrBannerTab == 0) {
            adapters.add(new SjMoreTitleAdapter(mContext, info, mPresenter, TempletPresenter.LOG_CLICK_ACTION_SJ0, TempletPresenter.LOG_CLICK_MORE, templetPosition, true));
        } else if (hasFlOrBannerTab == 1) {
            if (templetPosition == 1) {
                adapters.add(new SjMoreTitleAdapter(mContext, info, mPresenter, TempletPresenter.LOG_CLICK_ACTION_SJ0, TempletPresenter.LOG_CLICK_MORE, templetPosition, false));
            } else {
                adapters.add(new SjMoreTitleAdapter(mContext, info, mPresenter, TempletPresenter.LOG_CLICK_ACTION_SJ0, TempletPresenter.LOG_CLICK_MORE, templetPosition, true));
            }
        } else if (hasFlOrBannerTab == 2) {
            adapters.add(new SjMoreTitleAdapter(mContext, info, mPresenter, TempletPresenter.LOG_CLICK_ACTION_SJ0, TempletPresenter.LOG_CLICK_MORE, templetPosition, true));
        } else {
            if (templetPosition == 0) {
                adapters.add(new SjMoreTitleAdapter(mContext, info, mPresenter, TempletPresenter.LOG_CLICK_ACTION_SJ0, TempletPresenter.LOG_CLICK_MORE, templetPosition, false));
            } else {
                adapters.add(new SjMoreTitleAdapter(mContext, info, mPresenter, TempletPresenter.LOG_CLICK_ACTION_SJ0, TempletPresenter.LOG_CLICK_MORE, templetPosition, true));
            }
        }
        SjSingleBookViewHAdapter singleBookViewHAdapter = new SjSingleBookViewHAdapter(mContext, mPresenter, info, TempletPresenter.LOG_CLICK_ACTION_SJ3, templetPosition);
        adapters.add(singleBookViewHAdapter);
    }

}
