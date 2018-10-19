/**
 * DTS2014031908712 yinwenshuai/00211458 20140320 created
 * RIGO_UI Modification
 */
package com.dzbook.adapter;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;

import com.dzbook.lib.utils.ALog;
import com.dzbook.mvp.presenter.MainStorePresenter;
import com.dzbook.templet.ChannelPageFragment;
import com.dzbook.templet.ChannelWebPageFragment;

import java.util.List;

import huawei.widget.HwSubTabWidget;
import hw.sdk.net.bean.store.BeanSubTempletInfo;

/**
 * SubTabFragmentPagerAdapter
 *
 * @author caimt 2018-06-20
 */
public class SubTabFragmentPagerAdapter extends BaseSubTabAdapter {

    private List<BeanSubTempletInfo> channels;
    private String templetID;
    private String pageType;
    private MainStorePresenter presenter;

    /**
     * 构造
     *
     * @param activity     activity
     * @param pager        pager
     * @param subTabWidget subTabWidget
     */
    public SubTabFragmentPagerAdapter(FragmentActivity activity, ViewPager pager, HwSubTabWidget subTabWidget) {
        super(activity, pager, subTabWidget);
    }

    /**
     * 添加tab
     *
     * @param subTab             subTab
     * @param frag               frag
     * @param args               args
     * @param selected           selected
     * @param infos              channels
     * @param id                 templetID
     * @param mainStorePresenter presenter
     * @param pagetype           pageType
     */
    public void addSubTab(HwSubTabWidget.SubTab subTab, Fragment frag, Bundle args, boolean selected, List<BeanSubTempletInfo> infos, String id, MainStorePresenter mainStorePresenter, String pagetype) {
        this.channels = infos;
        this.templetID = id;
        this.presenter = mainStorePresenter;
        this.pageType = pagetype;
        if (!frag.isAdded() && !frag.isDetached()) {
            frag.setArguments(args);
        }
        SubTabInfo info = new SubTabInfo(frag, args);
        subTab.setTag(info);
        if (null == subTab.getCallback()) {
            subTab.setSubTabListener(this);
        }
        mSubTabs.add(info);
        notifyDataSetChanged();
        mSubTabWidget.addSubTab(subTab, selected);
    }


    @Override
    public void onSubTabSelected(int position) {
        ALog.cmtDebug("onSubTabSelected:" + pageType);
        ALog.cmtDebug("position:" + position);
        BeanSubTempletInfo beanSubTempletInfo = channels.get(position);
        HwSubTabWidget.SubTab selectedSubTab = mSubTabWidget.getSelectedSubTab();
        if (null == beanSubTempletInfo || null == selectedSubTab) {
            return;
        }
        Object tag = selectedSubTab.getTag();
        if (null == tag || !(tag instanceof SubTabInfo)) {
            return;
        }
        SubTabInfo subTabInfo = (SubTabInfo) tag;
        if (subTabInfo.fragment instanceof ChannelPageFragment) {
            ((ChannelPageFragment) subTabInfo.fragment).referenceData(templetID, beanSubTempletInfo, pageType, position);
        } else if (subTabInfo.fragment instanceof ChannelWebPageFragment) {
            ((ChannelWebPageFragment) subTabInfo.fragment).referenceData(beanSubTempletInfo, position);
        }
        if (null != presenter) {
            presenter.logChannel(beanSubTempletInfo);
        }
    }

}
