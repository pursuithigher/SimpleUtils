
package com.huawei.reader.user.impl.download.view;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.IntEvaluator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.animation.LinearInterpolator;

import com.huawei.reader.utils.log.TagPrefix;

public class OverScrollRecycler extends RecyclerView {

    private static final String TAG = TagPrefix.HRWIDGET + "RefreshLayout";

    private int offSet = 0;

    private final static int MAX_OFFSET = 180;

    private final static int MAX_TIME = 300;

    private ValueAnimator animator;

    /**
     * 子View所在的矩形区域
     */
    private Rect rect = new Rect();

    private boolean enableRect = true;

    /**
     * 可以控制是否有回弹效果
     */
    private boolean enableBackBounce = true;

    public OverScrollRecycler(@NonNull Context context) {
        super(context);
    }

    public OverScrollRecycler(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public OverScrollRecycler(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean dispatchNestedScroll(int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed,
        int[] offsetInWindow, int type) {
        if (dyUnconsumed == 0 || !enableBackBounce) {
            return super.dispatchNestedScroll(dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed, offsetInWindow, type);
        } else if (type == 0) {
            int v = (int) (dyUnconsumed * 0.25);
            int consumed;
            if (offSet + v > MAX_OFFSET) {
                consumed = offSet - MAX_OFFSET;
                offSet = MAX_OFFSET;
            } else if (offSet + v < -MAX_OFFSET) {
                consumed = offSet + MAX_OFFSET;
                offSet = -MAX_OFFSET;
            } else {
                offSet += v;
                consumed = -v;
            }
            if (consumed != 0) {
                // 需要取反操作
                ViewCompat.offsetTopAndBottom(this, consumed);
            }
        }
        return true;
    }

    @Override
    public boolean dispatchNestedPreScroll(int dx, int dy, int[] consumed, int[] offsetInWindow, int type) {
        if (offSet != 0 && enableBackBounce) {
            if (dy < 0 && offSet > 0) {// 手指向下滑动
                int max = Math.max(dy, -offSet);
                consumed[1] = max;
                offSet += max;
                ViewCompat.offsetTopAndBottom(this, -max);
                return true;
            } else if (dy > 0 && offSet < 0) {// 手指向上滑动
                int max = Math.min(dy, -offSet);
                consumed[1] = max;
                offSet += max;
                ViewCompat.offsetTopAndBottom(this, -max);
                return true;
            }
        }
        return super.dispatchNestedPreScroll(dx, dy, consumed, offsetInWindow, type);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        if (enableRect && enableBackBounce) {
            final int width = getMeasuredWidth();
            final int height = getMeasuredHeight();
            // target铺满屏幕
            final int childLeft = getPaddingLeft();
            final int childTop = getPaddingTop() - offSet;
            final int childWidth = width - getPaddingLeft() - getPaddingRight();
            final int childHeight = height - getPaddingTop() - getPaddingBottom();
            rect.set(childLeft, childTop, childLeft + childWidth, childTop + childHeight);
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (!enableBackBounce) {
            return super.dispatchTouchEvent(ev);
        }
        int action = ev.getAction();
        if (ev.getAction() != ev.getActionMasked()) {
            return true;
        }
        switch (action) {
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                stopBoundScroll();
                break;
            default:
                break;
        }
        return super.dispatchTouchEvent(ev);
    }

    private ValueAnimator.AnimatorUpdateListener listener = new ValueAnimator.AnimatorUpdateListener() {
        @Override
        public void onAnimationUpdate(ValueAnimator animation) {
            final int animatedValue = (int) animation.getAnimatedValue();
            offSet = animatedValue;
            post(new Runnable() {
                @Override
                public void run() {
                    layout(rect.left, rect.top - animatedValue, rect.right, rect.bottom - animatedValue);
                }
            });
        }
    };

    private void stopBoundScroll() {
        if (offSet == 0) {
            return;
        }
        if (animator == null) {
            animator = initAnimator();
            animator.start();
        } else {
            if (animator.isStarted()) {
                animator.cancel();
                enableRect = true;
            } else {
                animator = initAnimator();
                animator.start();
            }
        }
    }

    private ValueAnimator initAnimator() {
        ValueAnimator animator =
            ValueAnimator.ofInt(offSet, 0).setDuration(Math.abs((int) (offSet * 1f / MAX_OFFSET * MAX_TIME)));
        animator.setInterpolator(new LinearInterpolator());
        animator.setTarget(this);
        animator.addUpdateListener(listener);
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationCancel(Animator animation) {
                super.onAnimationCancel(animation);
                enableRect = true;
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                enableRect = true;
            }

            @Override
            public void onAnimationEnd(Animator animation, boolean isReverse) {
                enableRect = true;
            }

            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
                enableRect = false;
            }

            @Override
            public void onAnimationStart(Animator animation, boolean isReverse) {
                enableRect = false;
            }
        });
        animator.setEvaluator(new IntEvaluator());
        return animator;
    }
}
