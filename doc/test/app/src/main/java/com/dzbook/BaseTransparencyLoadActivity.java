package com.dzbook;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;

import com.dzbook.utils.DeviceInfoUtils;
import com.dzbook.view.swipeBack.SwipeBackActivityBase;
import com.dzbook.view.swipeBack.SwipeBackActivityHelper;
import com.dzbook.view.swipeBack.SwipeBackLayout;
import com.dzbook.view.swipeBack.Utils;

/**
 * BaseTransparencyLoadActivity
 *
 * @author zhenglk
 */
public abstract class BaseTransparencyLoadActivity extends BaseLoadActivity implements SwipeBackActivityBase {

    protected SwipeBackLayout mSwipeBackLayout;
    private SwipeBackActivityHelper mHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mHelper = new SwipeBackActivityHelper(this);
        mHelper.onActivityCreate();

//        AppContext.setStatusAndNavigation(this, getWindow());
        mSwipeBackLayout = getSwipeBackLayout();
        mSwipeBackLayout.setScrimColor(Color.TRANSPARENT);
        //设置滑动方向，可设置EDGE_LEFT, EDGE_RIGHT, EDGE_ALL, EDGE_BOTTOM
        mSwipeBackLayout.setEdgeTrackingEnabled(SwipeBackLayout.EDGE_LEFT);
        mSwipeBackLayout.setEdgeSize(DeviceInfoUtils.getInstanse().getWidthReturnInt() / 4);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mHelper.onPostCreate();
    }

    @Override
    public <T extends View> T findViewById(int id) {
        T v = super.findViewById(id);
        if (v == null && mHelper != null) {
            return mHelper.findViewById(id);
        }
        return v;
    }

    @Override
    public SwipeBackLayout getSwipeBackLayout() {
        return mHelper.getSwipeBackLayout();
    }

    @Override
    public void setSwipeBackEnable(boolean enable) {
        getSwipeBackLayout().setEnableGesture(enable);
    }

    @Override
    public void scrollToFinishActivity() {
        Utils.convertActivityToTranslucent(this);
        getSwipeBackLayout().scrollToFinishActivity();
    }


}
