/**
 * DTS2014031908712 yinwenshuai/00211458 20140320 created
 * RIGO_UI Modification
 */
package com.dzbook.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;

import com.dzbook.fragment.GiftReceiveFragment;
import com.dzbook.log.DzLog;
import com.dzbook.log.LogConstants;
import com.dzbook.utils.ViewUtils;

import huawei.widget.HwSubTabWidget;

/**
 * SubTabGiftFragmentAdapter
 *
 * @author caimt  2018-06-20
 */
public class SubTabGiftFragmentAdapter extends BaseSubTabAdapter {
    /**
     * 构造
     *
     * @param activity     activity
     * @param pager        pager
     * @param subTabWidget subTabWidget
     */
    public SubTabGiftFragmentAdapter(FragmentActivity activity, ViewPager pager, HwSubTabWidget subTabWidget) {
        super(activity, pager, subTabWidget);
    }

    @Override
    public void onSubTabSelected(int position) {
        DzLog.getInstance().logPv(position == 0 ? LogConstants.PV_LPDH : LogConstants.PV_WDLW, null, null);

        ViewUtils.hideInputKeyboard(mActivity, "GiftCenterActivity");
        if (position == 1) {
            Fragment item = getItem(1);
            if (null != item && item instanceof GiftReceiveFragment) {
                ((GiftReceiveFragment) item).refreshListView();
            }
        }
    }

}
