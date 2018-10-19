/**
 * DTS2014031908712 yinwenshuai/00211458 20140320 created
 * RIGO_UI Modification
 */
package com.dzbook.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;

import com.dzbook.templet.UpLoadBaseFragment;

import huawei.widget.HwSubTabWidget;

/**
 * Adapter
 *
 * @author caimt  2018-06-20
 */
public class SubTabUpLoadPagerAdapter extends BaseSubTabAdapter {


    /**
     * 构造
     *
     * @param activity     activity
     * @param pager        pager
     * @param subTabWidget subTabWidget
     */
    public SubTabUpLoadPagerAdapter(FragmentActivity activity, ViewPager pager, HwSubTabWidget subTabWidget) {
        super(activity, pager, subTabWidget);
    }

    @Override
    protected void onSubTabSelected(int position) {

    }

    /**
     * 获取当亲啊fragment
     *
     * @return UpLoadBaseFragment
     */
    public UpLoadBaseFragment getCurrentFragment() {
        HwSubTabWidget.SubTab selectedSubTab = mSubTabWidget.getSelectedSubTab();
        Object tag = selectedSubTab.getTag();
        if (null != tag && tag instanceof SubTabInfo) {
            Fragment fragment = ((SubTabInfo) tag).fragment;
            if (null != fragment && fragment instanceof UpLoadBaseFragment) {
                return (UpLoadBaseFragment) fragment;
            }
        }
        return null;
    }
}
