package com.dzbook.view;

import android.content.Context;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * 由于 下拉刷新会跟书城的轮播图抢夺焦点 在这里重写一个可以设置灵敏度的refreshLayout
 *
 * @author Winzows on 2016/11/16.
 */
public class CustomSwipeRefreshLayout extends SwipeRefreshLayout {
    private float mInitialDownY;
    private float mInitialDownX;
    //    private float mTouchSlop;

    /**
     * 构造
     *
     * @param context context
     */
    public CustomSwipeRefreshLayout(Context context) {
        this(context, null);
    }

    /**
     * 构造
     *
     * @param context context
     * @param attrs   attrs
     */
    public CustomSwipeRefreshLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        //        mTouchSlop = DimensionPixelUtil.dip2px(context, 85f);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {

        final int action = ev.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                mInitialDownY = ev.getY();
                mInitialDownX = ev.getX();
                break;
            case MotionEvent.ACTION_MOVE:
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                final float yDiff = Math.abs(ev.getY() - mInitialDownY);
                final float xDiff = Math.abs(ev.getX() - mInitialDownX);
                if (xDiff > 0 && xDiff > yDiff) {
                    return false;
                }
            default:
                break;
        }
        return super.onInterceptTouchEvent(ev);
    }


    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        try {
            return super.onTouchEvent(ev);
        } catch (Exception e) {
            return true;
        }
    }
}