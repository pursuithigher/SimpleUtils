package com.dzbook.view.hwbanner;

import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * [BannerPagerAdapter]<BR>
 *
 * @author hwx492570
 * @version [V8.0.4.1, 2018/2/27]
 * @since V6.16.2.0
 */
public class BannerPagerAdapter extends PagerAdapter {

    /**
     * 刷新数据位置
     */
    public static final int REFRESH_POSITION = 2;
    //View集合
    private final List<View> viewList = new ArrayList<View>();

    /**
     * 构造函数
     *
     * @param viewList bannerview集合
     */
    public BannerPagerAdapter(List<View> viewList) {
        setViewList(viewList);
    }


    /**
     * 设置banner数据集合
     *
     * @param bannerViewList banner view集合
     */
    public void setViewList(List<View> bannerViewList) {
        viewList.clear();
        if (bannerViewList != null) {
            viewList.addAll(bannerViewList);
        }
    }

    @Override
    public int getCount() {
        return viewList.size();
    }

    /**
     * 得到单个view
     *
     * @param position position
     * @return view
     */
    public View getItemView(int position) {
        if (viewList != null && position < viewList.size()) {
            return viewList.get(position);
        }
        return null;
    }

    @Override
    public boolean isViewFromObject(View arg0, Object arg1) {
        return arg0 == arg1;
    }


    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        if (viewList == null || viewList.size() == 0) {
            return null;
        }
        if (position < 0 || position >= viewList.size()) {
            return null;
        }

        //防止重复添加
        View view = viewList.get(position);
        if (null != view && view.getParent() != null && view.getParent() instanceof ViewGroup) {
            ViewGroup parent = (ViewGroup) view.getParent();
            if (parent != null) {
                parent.removeAllViews();
            }
        }

        if (null != view && null != container) {
            view.setTag(position);
            container.addView(view);
        }

        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        if (null != container) {
            container.removeView((View) object);
        }

    }

    @Override
    public int getItemPosition(Object object) {

        View view = (View) object;
        int position = -1;
        if (view != null && view.getTag() instanceof Integer) {
            position = (int) view.getTag();
        }
        //防止加载网络数据时闪烁
        if (position == REFRESH_POSITION) {
            return POSITION_NONE;
        }

        return super.getItemPosition(object);

    }
}
