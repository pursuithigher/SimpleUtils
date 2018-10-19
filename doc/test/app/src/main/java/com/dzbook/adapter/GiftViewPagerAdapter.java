package com.dzbook.adapter;

import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

/**
 * 通用FragmentPager
 */
public class GiftViewPagerAdapter extends PagerAdapter {

    private List<View> viewDatas;

    /**
     * 构造
     *
     * @param datas datas
     */
    public GiftViewPagerAdapter(List<View> datas) {
        viewDatas = datas;
    }

    @Override
    public boolean isViewFromObject(View a, Object b) {
        return a == b;
    }

    @Override
    public int getCount() {
        return viewDatas == null ? 0 : viewDatas.size();
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView(viewDatas.get(position));
    }

    @Override
    public int getItemPosition(Object object) {
        return super.getItemPosition(object);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return "礼品兑换";
            case 1:
                return "已领取";
            default:
                return "";
        }
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        container.addView(viewDatas.get(position));
        return viewDatas.get(position);
    }

}
