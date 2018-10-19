package com.dzbook.view.swipeBack;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;

import com.dzbook.lib.utils.CompatUtils;
import com.ishugui.R;


/**
 * SwipeBackActivityHelper
 *
 * @author Yrom
 */
public class SwipeBackActivityHelper {
    private Activity mActivity;

    private SwipeBackLayout mSwipeBackLayout;

    /**
     * 构造
     *
     * @param activity activity
     */
    public SwipeBackActivityHelper(Activity activity) {
        mActivity = activity;
    }

    /**
     * onActivityCreate
     */
    @SuppressWarnings("deprecation")
    public void onActivityCreate() {
        mActivity.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        CompatUtils.setBackgroundDrawable(mActivity.getWindow().getDecorView(), null);
        mSwipeBackLayout = (SwipeBackLayout) LayoutInflater.from(mActivity).inflate(R.layout.swipeback_layout, null);
        mSwipeBackLayout.addSwipeListener(new SwipeBackLayout.SwipeListener() {
            @Override
            public void onScrollStateChange(int state, float scrollPercent) {
            }

            @Override
            public void onEdgeTouch(int edgeFlag) {
                Utils.convertActivityToTranslucent(mActivity);
            }

            @Override
            public void onScrollOverThreshold() {

            }
        });
    }

    /**
     * 绑定
     * activity
     */
    public void onPostCreate() {
        mSwipeBackLayout.attachToActivity(mActivity);
    }

    /**
     * findViewById
     *
     * @param id  id
     * @param <T> T
     * @return T
     */
    public <T extends View> T findViewById(int id) {
        if (mSwipeBackLayout != null) {
            return mSwipeBackLayout.findViewById(id);
        }
        return null;
    }

    public SwipeBackLayout getSwipeBackLayout() {
        return mSwipeBackLayout;
    }
}
