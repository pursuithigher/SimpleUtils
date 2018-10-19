/**
 * DTS2014031908712 yinwenshuai/00211458 20140320 created
 * RIGO_UI Modification
 */
package com.dzbook.adapter;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;

import com.dzbook.lib.utils.ALog;

import java.util.ArrayList;

import huawei.widget.HwSubTabWidget;

/* < DTS2014050906855 yinwenshuai/00211458 20140516 begin */
/* DTS2014050906855 yinwenshuai/00211458 20140516 end > */

/**
 * BaseSubTabAdapter
 *
 * @author caimt 2018-06-20
 */
abstract class BaseSubTabAdapter extends FragmentPagerAdapter implements HwSubTabWidget.SubTabListener, ViewPager.OnPageChangeListener {

    /* < DTS2015011206490 yinwenshuai/00211458 20150121 begin */
    // delete useless mContext
    /* DTS2015011206490 yinwenshuai/00211458 20150121 end > */
    protected final HwSubTabWidget mSubTabWidget;
    protected final ViewPager mViewPager;
    protected final ArrayList<SubTabInfo> mSubTabs = new ArrayList<SubTabInfo>();
    protected Activity mActivity;


    /**
     * SubTabInfo
     */
    protected static final class SubTabInfo {
        protected Fragment fragment;
        protected final Bundle args;

        SubTabInfo(Fragment fragment1, Bundle bundle) {
            fragment = fragment1;
            args = bundle;
        }

        public void setFragmentItem(Fragment fm) {
            fragment = fm;
        }

        /* < DTS2015011206490 yinwenshuai/00211458 20150121 begin */
        public Bundle getArgs() {
            return args;
        }
        /* DTS2015011206490 yinwenshuai/00211458 20150121 end > */
    }

    public BaseSubTabAdapter(FragmentActivity activity, ViewPager pager, HwSubTabWidget subTabWidget) {
        super(activity.getSupportFragmentManager());
        this.mActivity = activity;
        /* < DTS2015011206490 yinwenshuai/00211458 20150121 begin */
        // delete useless mContext
        /* DTS2015011206490 yinwenshuai/00211458 20150121 end > */
        mSubTabWidget = subTabWidget;
        mViewPager = pager;
        mViewPager.setAdapter(this);
        mViewPager.setOnPageChangeListener(this);
    }

    /* < DTS2014050906855 yinwenshuai/00211458 20140516 begin */
    public BaseSubTabAdapter(FragmentManager fm, Context context, ViewPager pager, HwSubTabWidget subTabWidget) {
        super(fm);
        /* < DTS2015011206490 yinwenshuai/00211458 20150121 begin */
        // delete useless mContext
        /* DTS2015011206490 yinwenshuai/00211458 20150121 end > */
        mSubTabWidget = subTabWidget;
        mViewPager = pager;
        mViewPager.setAdapter(this);
        mViewPager.setOnPageChangeListener(this);
    }
    /* DTS2014050906855 yinwenshuai/00211458 20140516 end > */

    public void addSubTab(HwSubTabWidget.SubTab subTab, Fragment frag, Bundle args, boolean selected) {
        /*< DTS2014052002969 taolan/00264981 20140522 begin*/

        if (!frag.isAdded() && !frag.isDetached()) {
            frag.setArguments(args);
        }
        /* DTS2014052002969 taolan/00264981 20140522 end>*/
        SubTabInfo info = new SubTabInfo(frag, args);
        subTab.setTag(info);
        /* < DTS2014092605573 yinwenshuai/00211458 20141115 begin */
        if (null == subTab.getCallback()) {
            subTab.setSubTabListener(this);
        }
        /* DTS2014092605573 yinwenshuai/00211458 20141115 end > */
        mSubTabs.add(info);
        notifyDataSetChanged();
        mSubTabWidget.addSubTab(subTab, selected);
    }


    /* < DTS2014092605573 yinwenshuai/00211458 20141115 begin */
    public void addSubTab(HwSubTabWidget.SubTab subTab, int position, Fragment frag, Bundle args, boolean selected) {

        if (!frag.isAdded() && !frag.isDetached()) {
            frag.setArguments(args);
        }
        SubTabInfo info = new SubTabInfo(frag, args);
        subTab.setTag(info);
        if (null == subTab.getCallback()) {
            subTab.setSubTabListener(this);
        }
        /* < DTS2014122907906 tianjing/102012 20150109 begin */
        mSubTabs.add(position, info);
        /* DTS2014122907906 tianjing/102012 20150109 end> */
        mSubTabWidget.addSubTab(subTab, position, selected);
        notifyDataSetChanged();
    }
    /* DTS2014092605573 yinwenshuai/00211458 20141115 end > */

    @Override
    public Fragment getItem(int position) {
        /* < DTS2014032603617 yinwenshuai/00211458 20140326 begin */
        int subTabSize = mSubTabs.size();
        if (position >= 0 && position < subTabSize) {
            return mSubTabs.get(position).fragment;
        }
        return null;
        /* DTS2014032603617 yinwenshuai/00211458 20140326 end > */
    }

    public void setItem(Fragment fm, int position) {
        SubTabInfo info = mSubTabs.get(position);
        info.setFragmentItem(fm);
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mSubTabs.size();
    }

    // ViewPager selected callback implement
    @Override
    public void onPageScrollStateChanged(int state) {
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        mSubTabWidget.setSubTabScrollingOffsets(position, positionOffset);
    }


    public void setSelect(int select) {
        mSubTabWidget.setSubTabSelected(select);
    }

    @Override
    public void onPageSelected(int position) {
        ALog.cmtDebug("onPageSelected:" + position);
        mSubTabWidget.setSubTabSelected(position);
        onSubTabSelected(position);
    }

    protected abstract void onSubTabSelected(int position);

    // SubTabWidget.SubTab selected callback implement
    @Override
    public void onSubTabReselected(HwSubTabWidget.SubTab subTab, FragmentTransaction ft) {
        ALog.cmtDebug("onSubTabReselected");
    }

    @Override
    public void onSubTabSelected(HwSubTabWidget.SubTab subTab, FragmentTransaction ft) {
        ALog.cmtDebug("onSubTabSelected");
        if (subTab.getTag() instanceof SubTabInfo) {
            SubTabInfo tag = (SubTabInfo) subTab.getTag();
            for (int i = 0; i < mSubTabs.size(); i++) {
                if (mSubTabs.get(i) == tag) {
                    mViewPager.setCurrentItem(i);
                    if (i == 0) {
                        onSubTabSelected(0);
                    }
                }
            }
        }
    }

    @Override
    public void onSubTabUnselected(HwSubTabWidget.SubTab subTab, FragmentTransaction ft) {
        ALog.cmtDebug("onSubTabUnselected");
    }
}
