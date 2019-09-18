
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
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;

import com.huawei.hvi.ability.component.log.Logger;
import com.huawei.reader.utils.log.TagPrefix;

public class OverScrollRecyclerView extends NestedScrollView {

    private static final String TAG = TagPrefix.HRWIDGET + "RefreshLayout";

    private RecyclerView mRecyclerView;

    private int offSet = 0;

    private final static int MAX_OFFSET = 180;

    private ValueAnimator animator;

    private VelocityTracker mVelocityTracker;

    /**
     * 子View所在的矩形区域
     */
    private Rect rect = new Rect();

    private int mActivePointerId = -1;

    private int mMinFlingVelocity;

    private int mMaxFlingVelocity;

    float yvel;

    public OverScrollRecyclerView(@NonNull Context context) {
        super(context);
        addChild();
    }

    public OverScrollRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        addChild();
    }

    public OverScrollRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        addChild();
    }

    private void addChild() {
        ViewConfiguration vc = ViewConfiguration.get(getContext());
        this.mMinFlingVelocity = vc.getScaledMinimumFlingVelocity();
        this.mMaxFlingVelocity = vc.getScaledMaximumFlingVelocity();
        mRecyclerView = new RecyclerView(getContext());
        mVelocityTracker = VelocityTracker.obtain();
        this.addView(mRecyclerView,
            new NestedScrollView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
    }

    public RecyclerView getRecyclerView() {
        return mRecyclerView;
    }

    @Override
    public void onNestedScroll(View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed,
        int type) {
        super.onNestedScroll(target, dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed, type);
    }

    @Override
    public boolean dispatchNestedScroll(int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed,
        int[] offsetInWindow, int type) {
        if (dyUnconsumed == 0) {
            return super.dispatchNestedScroll(dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed, offsetInWindow, type);
        } else {
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
                ViewCompat.offsetTopAndBottom(mRecyclerView, consumed);
            }
            return true;
        }
    }

    @Override
    public void onNestedPreScroll(@NonNull View target, int dx, int dy, @NonNull int[] consumed, int type) {
        if (offSet == 0) {
            super.onNestedPreScroll(target, dx, dy, consumed, type);
        } else {
            if (dy < 0 && offSet > 0) {// 向下滑动
                int max = Math.max(dy, -offSet);
                consumed[1] = max;
                offSet += max;
                ViewCompat.offsetTopAndBottom(mRecyclerView, -max);
            } else if (dy > 0 && offSet < 0) {// 向上滑动
                int max = Math.min(dy, -offSet);
                consumed[1] = max;
                offSet += max;
                ViewCompat.offsetTopAndBottom(mRecyclerView, -max);
            }
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        final int width = getMeasuredWidth();
        final int height = getMeasuredHeight();
        if (getChildCount() == 0) {
            Logger.w(TAG, "onLayout childCount is 0");
            return;
        }

        // target铺满屏幕
        final View child = mRecyclerView;
        final int childLeft = getPaddingLeft();
        final int childTop = getPaddingTop() - offSet;
        final int childWidth = width - getPaddingLeft() - getPaddingRight();
        final int childHeight = height - getPaddingTop() - getPaddingBottom();
        child.layout(childLeft, childTop, childLeft + childWidth, childTop + childHeight);
    }

    @Override
    public void onStopNestedScroll(@NonNull View target, int type) {
        super.onStopNestedScroll(target, type);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        int action = ev.getAction();
        if (ev.getAction() != ev.getActionMasked()) {
            return true;
        }
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                this.mActivePointerId = ev.getPointerId(0);
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                this.mVelocityTracker.clear();
                this.mActivePointerId = -1;
                break;
            case MotionEvent.ACTION_MOVE:
                if (mActivePointerId != -1) {
                    mVelocityTracker.addMovement(ev);
                    this.mVelocityTracker.computeCurrentVelocity(1000, (float) this.mMaxFlingVelocity);
                    yvel = -this.mVelocityTracker.getYVelocity(this.mActivePointerId);
                    if (Math.abs(yvel) < this.mMinFlingVelocity) {
                        yvel = 0;
                    }
                }
                break;
        }
        return super.dispatchTouchEvent(ev);
    }

    private ValueAnimator.AnimatorUpdateListener listener = new ValueAnimator.AnimatorUpdateListener() {
        @Override
        public void onAnimationUpdate(ValueAnimator animation) {
            if (null != mRecyclerView) {
                int animatedValue = (int) animation.getAnimatedValue();

            }
        }
    };

    private void stopScroll() {
        if (offSet == 0) {
            return;
        }
        if (animator == null) {
            animator = ValueAnimator.ofInt(offSet, 0).setDuration(1500);
            animator.setInterpolator(new LinearInterpolator());
            animator.setTarget(mRecyclerView);
            animator.addUpdateListener(listener);
            animator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationCancel(Animator animation) {
                    super.onAnimationCancel(animation);
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                }

                @Override
                public void onAnimationEnd(Animator animation, boolean isReverse) {
                }
            });
            animator.setEvaluator(new IntEvaluator());
            animator.start();
        } else {
            if (animator.isStarted()) {
                animator.cancel();
            } else {
                animator.start();
            }
        }
    }
}
