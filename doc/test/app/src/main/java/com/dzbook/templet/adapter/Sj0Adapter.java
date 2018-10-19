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

public class Sj0Adapter {

    private Context mContext;
    private TempletPresenter mPresenter;

    private int templetPosition;

    private int hasFlOrBannerTab;

    Sj0Adapter(Context context, TempletPresenter templetPresenter, int templetPosition, int hasFlOrBannerTab) {
        mContext = context;
        mPresenter = templetPresenter;
        this.templetPosition = templetPosition;
        this.hasFlOrBannerTab = hasFlOrBannerTab;
    }

    /**
     * 添加Adapter
     *
     * @param adapters adapters
     * @param info     info
     */
    public void addSj0Adapter(List<DelegateAdapter.Adapter> adapters, BeanTempletInfo info) {
        if (hasFlOrBannerTab == 0) {
            adapters.add(new SjMoreTitleAdapter(mContext, info, mPresenter, TempletPresenter.LOG_CLICK_ACTION_SJ0, TempletPresenter.LOG_CLICK_MORE, templetPosition, true));
        } else if (hasFlOrBannerTab == 2) {
            adapters.add(new SjMoreTitleAdapter(mContext, info, mPresenter, TempletPresenter.LOG_CLICK_ACTION_SJ0, TempletPresenter.LOG_CLICK_MORE, templetPosition, true));
        } else if (hasFlOrBannerTab == 1) {
            if (templetPosition == 1) {
                adapters.add(new SjMoreTitleAdapter(mContext, info, mPresenter, TempletPresenter.LOG_CLICK_ACTION_SJ0, TempletPresenter.LOG_CLICK_MORE, templetPosition, false));
            } else {
                adapters.add(new SjMoreTitleAdapter(mContext, info, mPresenter, TempletPresenter.LOG_CLICK_ACTION_SJ0, TempletPresenter.LOG_CLICK_MORE, templetPosition, true));
            }
        } else {
            if (templetPosition == 0) {
                adapters.add(new SjMoreTitleAdapter(mContext, info, mPresenter, TempletPresenter.LOG_CLICK_ACTION_SJ0, TempletPresenter.LOG_CLICK_MORE, templetPosition, false));
            } else {
                adapters.add(new SjMoreTitleAdapter(mContext, info, mPresenter, TempletPresenter.LOG_CLICK_ACTION_SJ0, TempletPresenter.LOG_CLICK_MORE, templetPosition, true));
            }
        }
        adapters.add(new SjSingleBookViewVAdapter(mContext, mPresenter, info, false, TempletPresenter.LOG_CLICK_ACTION_SJ0, templetPosition));
    }

}
