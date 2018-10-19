/**
 * DTS2014031908712 yinwenshuai/00211458 20140320 created
 * RIGO_UI Modification
 */
package com.dzbook.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;

import com.dzbook.templet.ReaderChapterFragment;
import com.dzbook.templet.ReaderMarkFragment;
import com.dzbook.templet.ReaderNoteFragment;

import huawei.widget.HwSubTabWidget;

/**
 * SubTabReaderCatalogAdapter
 *
 * @author caimt  2018-06-20
 */
public class SubTabReaderCatalogAdapter extends BaseSubTabAdapter {

    /**
     * 构造
     *
     * @param activity     activity
     * @param pager        pager
     * @param subTabWidget subTabWidget
     */
    public SubTabReaderCatalogAdapter(FragmentActivity activity, ViewPager pager, HwSubTabWidget subTabWidget) {
        super(activity, pager, subTabWidget);
    }

    @Override
    protected void onSubTabSelected(int position) {

    }

    /**
     * 获取章节fragment
     *
     * @return ReaderChapterFragment
     */
    public ReaderChapterFragment getReaderChapterFragment() {
        Fragment item = getItem(0);
        if (null != item && item instanceof ReaderChapterFragment) {
            return (ReaderChapterFragment) item;
        }
        return null;
    }

    /**
     * 获取书签fragment
     *
     * @return ReaderMarkFragment
     */
    public ReaderMarkFragment getReaderMarkFragment() {
        Fragment item = getItem(1);
        if (null != item && item instanceof ReaderMarkFragment) {
            return (ReaderMarkFragment) item;
        }
        return null;
    }

    /**
     * 获取笔记fragment
     *
     * @return ReaderNoteFragment
     */
    public ReaderNoteFragment getReaderNoteFragment() {
        Fragment item = getItem(2);
        if (null != item && item instanceof ReaderNoteFragment) {
            return (ReaderNoteFragment) item;
        }
        return null;
    }
}
