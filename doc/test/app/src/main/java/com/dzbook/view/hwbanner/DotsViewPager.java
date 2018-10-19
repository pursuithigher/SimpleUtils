package com.dzbook.view.hwbanner;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.dzbook.view.common.loading.RefreshLayout;

/**
 * 自定义viewpager实现错层动效
 *
 * @author lwx464719
 * @version [V8.0.4.1, 2018/3/1]
 */
public class DotsViewPager extends ViewPager {

    private static final String TAG = "DotsViewPager";

    private RefreshLayout refreshLayout;

    private int x = 0;
    private int y = 0;
    private int dealtX = 0;
    private int dealtY = 0;

    /**
     * 构造
     *
     * @param context Context
     */
    public DotsViewPager(Context context) {
        super(context);
    }

    /**
     * 构造
     *
     * @param context Context
     * @param attrs   attrs
     */
    public DotsViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    /**
     * {@inheritDoc}
     */
    @SuppressLint("WrongCall")
    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();

        //1. 防止第一次滚动时跳跃 2.防止滑动时立即滑动底部时，再次返回显示一半的banner
        onLayout(false, getLeft(), getTop(), getRight(), getBottom());
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {

        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                x = (int) ev.getRawX();
                y = (int) ev.getRawY();

                // 保证子View能够接收到Action_move事件
                getParent().requestDisallowInterceptTouchEvent(true);
                break;
            case MotionEvent.ACTION_MOVE:
                dealtX = (int) ev.getRawX();
                dealtY = (int) ev.getRawY();
                // 左右滑动请求父 View 不要拦截
                if (Math.abs(dealtX - x) > Math.abs(dealtY - y)) {
                    setParentCanRefresh(false);
                } else if (Math.abs(dealtY - y) > getMeasuredHeight() / 4) {
                    getParent().requestDisallowInterceptTouchEvent(false);
                    setParentCanRefresh(true);
                }

                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                getParent().requestDisallowInterceptTouchEvent(false);
                setParentCanRefresh(true);
                break;
            default:
                break;

        }
        return super.dispatchTouchEvent(ev);
    }

    private void setParentCanRefresh(boolean isCanRefresh) {
        if (refreshLayout == null) {
            refreshLayout = getRefreshLayout();
        }
        if (refreshLayout != null) {
            refreshLayout.setCanRefresh(isCanRefresh);
        }
    }


    /**
     * 获取RefreshLayout
     *
     * @return RefreshLayout
     */
    public RefreshLayout getRefreshLayout() {
        View parent = this;
        int index = 0;

        while (parent != null && parent.getParent() != null && index < 7) {
            parent = (View) parent.getParent();
            index++;
            if (parent instanceof RefreshLayout) {
                return (RefreshLayout) parent;
            }
        }
        return null;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        try {
            return super.onInterceptTouchEvent(ev);
        } catch (Exception ex) {

        }
        return true;
    }

    /**
     * [获取最近一次点击的X位置,屏幕绝对坐标 ]<BR>
     *
     * @return 最近一次点击的X位置, 屏幕绝对坐标
     */
    public int getLastDownRawX() {
        return x;
    }

}
