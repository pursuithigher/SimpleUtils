package com.dzbook.view.common.loading;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.Scroller;

import com.dzbook.utils.DeviceInfoUtils;
import com.dzbook.utils.DimensionPixelUtil;
import com.ishugui.R;

import huawei.widget.HwProgressBar;

/**
 * RefreshLayout
 */
public class RefreshLayout extends ViewGroup {
    private static final String TAG = "RefreshLayout";
    private static final float DRAG_RATE = 0.5f;
    private static final int INVALID_POINTER = 0;

    // scroller duration
    private static final int SCROLL_TO_TOP_DURATION = 350;
    private static final int SCROLL_TO_REFRESH_DURATION = 250;
    private static final long SHOW_COMPLETED_TIME = 500;
    private static final int START_POSITION = 0;

    private boolean isCanRefresh = true;
    private HwProgressBar refreshHeader;
    private View target;
    /**
     * target/header偏移距离
     */
    private int currentTargetOffsetTop;
    private int lastTargetOffsetTop;

    /**
     * 是否已经计算头部高度
     */
    private boolean hasMeasureHeader;
    private int touchSlop;

    /**
     * 需要下拉这个距离才进入松手刷新状态，默认和header高度一致
     */
    private int totalDragDistance;
    private int maxDragDistance;
    private int activePointerId;
    private boolean isTouch;
    private boolean hasSendCancelEvent;

    private float lastMotionY;
    private float initDownY;

    private MotionEvent lastEvent;
    private boolean mIsBeginDragged;
    private AutoScroll autoScroll;
    private State state = State.RESET;
    private OnRefreshListener refreshListener;
    private boolean isAutoRefresh;

    private int scaleHeight10;
    private int scaleHeight15;
    private int scaleHeight20;
    private int scaleHeight25;

    /**
     * 刷新成功，显示500ms成功状态再滚动回顶部
     */
    private Runnable delayToScrollTopRunnable = new Runnable() {
        @Override
        public void run() {
            autoScroll.scrollTo(START_POSITION, SCROLL_TO_TOP_DURATION);
        }
    };

    private Runnable autoRefreshRunnable = new Runnable() {
        @Override
        public void run() {
            // 标记当前是自动刷新状态，finishScroll调用时需要判断
            // 在actionDown事件中重新标记为false
            isAutoRefresh = true;
            changeState(State.PULL);
            autoScroll.scrollTo(totalDragDistance, SCROLL_TO_REFRESH_DURATION);
        }
    };


    /**
     * 构造
     *
     * @param context context
     */
    public RefreshLayout(Context context) {
        this(context, null);
    }

    /**
     * 构造
     *
     * @param context context
     * @param attrs   attrs
     */
    public RefreshLayout(Context context, AttributeSet attrs) {
        super(context, attrs);

        touchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
        autoScroll = new AutoScroll();

        HwProgressBar hwProgressBar = (HwProgressBar) View.inflate(getContext(), R.layout.view_loading_middle, null);
        hwProgressBar.setVisibility(GONE);
        setRefreshHeader(hwProgressBar);
    }

    /**
     * 设置自定义header
     *
     * @param view view
     */
    public void setRefreshHeader(HwProgressBar view) {
        if (view != null && view != refreshHeader) {
            removeView(refreshHeader);

            // 为header添加默认的layoutParams
            LayoutParams layoutParams = view.getLayoutParams();
            if (layoutParams == null) {
                int size = DimensionPixelUtil.dip2px(getContext(), 40);
                layoutParams = new LayoutParams(size, size);
                view.setLayoutParams(layoutParams);
            }
            refreshHeader = view;
            addView(refreshHeader);
        }
    }

    /**
     * setRefreshing
     * @param isTrue isTrue
     */
    public void setRefreshing(Boolean isTrue) {
        if (isTrue) {
            autoRefresh();
        } else {
            refreshComplete();
        }
    }

    public void setRefreshListener(OnRefreshListener refreshListener) {
        this.refreshListener = refreshListener;
    }

    /**
     * refreshComplete
     */
    public void refreshComplete() {
        changeState(State.COMPLETE);
        // if refresh completed and the target at top, change state to reset.
        if (currentTargetOffsetTop == START_POSITION) {
            changeState(State.RESET);
        } else {
            // waiting for a time to show refreshView completed state.
            // at next touch event, remove this runnable
            if (!isTouch) {
                postDelayed(delayToScrollTopRunnable, SHOW_COMPLETED_TIME);
            }
        }
    }

    /**
     * autoRefresh
     */
    public void autoRefresh() {
        autoRefresh(500);
    }

    /**
     * 在onCreate中调用autoRefresh，此时View可能还没有初始化好，需要延长一段时间执行。
     *
     * @param duration 延时执行的毫秒值
     */
    public void autoRefresh(long duration) {
        if (state != State.RESET) {
            return;
        }
        postDelayed(autoRefreshRunnable, duration);
    }

    public boolean isRefreshing() {
        return state == State.LOADING;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (target == null) {
            ensureTarget();
        }

        if (target == null) {
            return;
        }

        // ----- measure target -----
        // target占满整屏
        target.measure(MeasureSpec.makeMeasureSpec(getMeasuredWidth() - getPaddingLeft() - getPaddingRight(), MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(getMeasuredHeight() - getPaddingTop() - getPaddingBottom(), MeasureSpec.EXACTLY));

        // ----- measure refreshView-----
        measureChild(refreshHeader, widthMeasureSpec, heightMeasureSpec);
        // 防止header重复测量
        if (!hasMeasureHeader) {
            hasMeasureHeader = true;
            //            // header高度
            //            headerHeight = refreshHeader.getMeasuredHeight();
            // 需要pull这个距离才进入松手刷新状态
            int screenHeight = DeviceInfoUtils.getInstanse().getHeightReturnInt();
            int barHeight = DeviceInfoUtils.getInstanse().getStatusBarHeight() + DimensionPixelUtil.dip2px(getContext(), 48);
            scaleHeight10 = (int) (screenHeight * 0.1f) - barHeight;
            if (scaleHeight10 < 0) {
                scaleHeight10 = 0;
            }
            scaleHeight15 = (int) (screenHeight * 0.15f) - barHeight;
            if (scaleHeight15 < 0) {
                scaleHeight15 = 0;
            }
            scaleHeight20 = (int) (screenHeight * 0.2f) - barHeight;
            scaleHeight25 = (int) (screenHeight * 0.25f) - barHeight;

            // 默认最大下拉距离指定
            if (maxDragDistance == 0) {
                maxDragDistance = scaleHeight25;
            }
            totalDragDistance = scaleHeight20;

        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        final int width = getMeasuredWidth();
        final int height = getMeasuredHeight();
        if (getChildCount() == 0) {
            return;
        }

        if (target == null) {
            ensureTarget();
        }
        if (target == null) {
            return;
        }

        // target铺满屏幕
        final View child = target;
        final int childLeft = getPaddingLeft();
        final int childTop = getPaddingTop() + currentTargetOffsetTop;
        final int childWidth = width - getPaddingLeft() - getPaddingRight();
        final int childHeight = height - getPaddingTop() - getPaddingBottom();
        child.layout(childLeft, childTop, childLeft + childWidth, childTop + childHeight);

        // header放到target的上方，水平居中
        int refreshViewWidth = refreshHeader.getMeasuredWidth();
        int headerHeight = DimensionPixelUtil.dip2px(getContext(), 40);
        refreshHeader.layout(width / 2 - refreshViewWidth / 2, (currentTargetOffsetTop - headerHeight) / 2, width / 2 + refreshViewWidth / 2, (currentTargetOffsetTop + headerHeight) / 2);
    }

    /**
     * 将第一个Child作为target
     */
    private void ensureTarget() {
        // Don't bother getting the parent height if the parent hasn't been laid
        // out yet.
        if (target == null) {
            for (int i = 0; i < getChildCount(); i++) {
                View child = getChildAt(i);
                if (!child.equals(refreshHeader)) {
                    target = child;
                    break;
                }
            }
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {

        if (!isCanRefresh || !isEnabled() || target == null) {
            return super.dispatchTouchEvent(ev);
        }
        // support Multi-touch
        final int actionMasked = ev.getActionMasked();
        touchEvent(target.getTop());
        switch (actionMasked) {
            case MotionEvent.ACTION_DOWN:
                activePointerId = ev.getPointerId(0);
                isAutoRefresh = false;
                isTouch = true;
                hasSendCancelEvent = false;
                mIsBeginDragged = false;
                lastTargetOffsetTop = currentTargetOffsetTop;
                currentTargetOffsetTop = target.getTop();
                initDownY = lastMotionY = ev.getY(0);
                autoScroll.stop();
                removeCallbacks(delayToScrollTopRunnable);
                removeCallbacks(autoRefreshRunnable);

                super.dispatchTouchEvent(ev);
                // return true，否则可能接受不到move和up事件
                return true;

            case MotionEvent.ACTION_MOVE:
                Object x = dispatchActionMove(ev);
                if (x != null && x instanceof Boolean) {
                    return (Boolean) x;
                }
                break;

            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                isTouch = false;
                if (currentTargetOffsetTop > START_POSITION) {
                    finishSpinner();
                }
                activePointerId = INVALID_POINTER;
                break;

            case MotionEvent.ACTION_POINTER_DOWN:
                int pointerIndex = ev.getActionIndex();
                if (pointerIndex < 0) {
                    Log.e(TAG, "Got ACTION_POINTER_DOWN event but have an invalid action index.");
                    return super.dispatchTouchEvent(ev);
                }
                lastMotionY = ev.getY(pointerIndex);
                lastEvent = ev;
                activePointerId = ev.getPointerId(pointerIndex);
                break;

            case MotionEvent.ACTION_POINTER_UP:
                onSecondaryPointerUp(ev);
                lastMotionY = ev.getY(ev.findPointerIndex(activePointerId));
                break;
            default:
                break;
        }
        return super.dispatchTouchEvent(ev);
    }

    /**
     * 伴随着手指一动，距离顶部的高度间距
     */
    private void touchEvent(int top) {
        if (top < scaleHeight15) {
            // 1、开始出现，渐变
            refreshHeader.setVisibility(INVISIBLE);
            refreshHeader.setAlpha(0);
            refreshHeader.setScaleX(0.5f);
            refreshHeader.setScaleY(0.5f);
        } else if (top < scaleHeight20) {
            // 2、开始滚动位置
            refreshHeader.setVisibility(VISIBLE);
            float alphaSize = (top - scaleHeight15) * 1.0f / (scaleHeight20 - scaleHeight15);
            refreshHeader.setAlpha(alphaSize);
            refreshHeader.setScaleX(0.5f + alphaSize * 0.5f);
            refreshHeader.setScaleY(0.5f + alphaSize * 0.5f);
        } else if (top <= scaleHeight25) {
            // 3、 放大缩放过程
            refreshHeader.setVisibility(VISIBLE);
            refreshHeader.setAlpha(1);
            float alphaSize = (top - scaleHeight20) * 1.0f / (scaleHeight25 - scaleHeight20);
            refreshHeader.setScaleX(1.0f + alphaSize * 0.1f);
            refreshHeader.setScaleY(1.0f + alphaSize * 0.1f);
        } else {
            refreshHeader.setVisibility(VISIBLE);
            refreshHeader.setAlpha(1);
            refreshHeader.setScaleX(1.1f);
            refreshHeader.setScaleY(1.1f);
        }
        int offset = (top - refreshHeader.getMeasuredHeight()) / 2;
        offset = offset - refreshHeader.getTop();
        refreshHeader.offsetTopAndBottom(offset);
    }

    private Object dispatchActionMove(MotionEvent ev) {
        if (activePointerId == INVALID_POINTER - 1) {
            Log.e(TAG, "Got ACTION_MOVE event but don't have an active pointer id.");
            return super.dispatchTouchEvent(ev);
        }
        lastEvent = ev;
        float y = ev.getY(ev.findPointerIndex(activePointerId));
        float yDiff = y - lastMotionY;
        float offsetY = yDiff * DRAG_RATE;
        lastMotionY = y;

        if (!mIsBeginDragged && Math.abs(y - initDownY) > touchSlop) {
            mIsBeginDragged = true;
        }

        if (mIsBeginDragged) {
            // ↓
            boolean moveDown = offsetY > 0;
            boolean canMoveDown = canChildScrollUp();
            // ↑
            boolean moveUp = !moveDown;
            boolean canMoveUp = currentTargetOffsetTop > START_POSITION;

            // 判断是否拦截事件
            if ((moveDown && !canMoveDown) || (moveUp && canMoveUp)) {
                moveSpinner(offsetY);
                return true;
            }
        }
        return null;
    }


    private void moveSpinner(float diff) {
        int offset = Math.round(diff);
        if (offset == 0) {
            return;
        }

        // 发送cancel事件给child
        if (!hasSendCancelEvent && isTouch && currentTargetOffsetTop > START_POSITION) {
            sendCancelEvent();
            hasSendCancelEvent = true;
        }

        // target不能移动到小于0的位置……
        int targetY = Math.max(0, currentTargetOffsetTop + offset);
        // y = x - (x/2)^2
        float extraOS = targetY - totalDragDistance;
        float slingshotDist = totalDragDistance;
        float tensionSlingshotPercent = Math.max(0, Math.min(extraOS, slingshotDist * 2) / slingshotDist);
        float tensionPercent = (float) (tensionSlingshotPercent - Math.pow(tensionSlingshotPercent / 2, 2));

        // 下拉的时候才添加阻力
        if (offset > 0) {
            offset = (int) (offset * (1f - tensionPercent));
            targetY = Math.max(0, currentTargetOffsetTop + offset);
        }

        // 1. 在RESET状态时，第一次下拉出现header的时候，设置状态变成PULL
        dealPull(targetY);

        // 2. 在PULL或者COMPLETE状态时，header回到顶部的时候，状态变回RESET
        dealReset(targetY);

        // 3. 如果是从底部回到顶部的过程(往上滚动)，并且手指是松开状态, 并且当前是PULL状态，状态变成LOADING，这时候我们需要强制停止autoScroll
        offset = dealLoading(offset, targetY);

        setTargetOffsetTopAndBottom(offset);

        // 别忘了回调header的位置改变方法。
        if (refreshHeader instanceof RefreshHeader) {
            ((RefreshHeader) refreshHeader).onPositionChange(currentTargetOffsetTop, lastTargetOffsetTop, totalDragDistance, isTouch, state);

        }

    }

    private void dealPull(int targetY) {
        if (state == State.RESET && currentTargetOffsetTop == START_POSITION && targetY > 0) {
            changeState(State.PULL);
        }
    }

    private void dealReset(int targetY) {
        if (currentTargetOffsetTop > START_POSITION && targetY <= START_POSITION) {
            if (state == State.PULL || state == State.COMPLETE) {
                changeState(State.RESET);
            }
        }
    }

    private int dealLoading(int offset, int targetY) {
        if (state == State.PULL && !isTouch && currentTargetOffsetTop > totalDragDistance && targetY <= totalDragDistance) {
            autoScroll.stop();
            changeState(State.LOADING);
            if (refreshListener != null) {
                refreshListener.onRefresh();
            }
            // 因为判断条件targetY <= totalDragDistance，会导致不能回到正确的刷新高度（有那么一丁点偏差），调整change
            int adjustOffset = totalDragDistance - targetY;
            offset += adjustOffset;
        }
        return offset;
    }

    private void finishSpinner() {
        if (state == State.LOADING) {
            if (currentTargetOffsetTop > totalDragDistance) {
                autoScroll.scrollTo(totalDragDistance, SCROLL_TO_REFRESH_DURATION);
            }
        } else {
            autoScroll.scrollTo(START_POSITION, SCROLL_TO_TOP_DURATION);
        }
    }

    /**
     * setShelfEnabled
     * @param enabled enabled
     */
    public void setShelfEnabled(boolean enabled) {
        super.setEnabled(enabled);
        if (!enabled) {
            isTouch = false;
            moveSpinner(0);
            canChildScrollUp();
        } else {
            changeState(State.RESET);
        }
    }

    private void changeState(State state1) {
        this.state = state1;

        RefreshHeader aRefreshHeader = this.refreshHeader instanceof RefreshHeader ? ((RefreshHeader) this.refreshHeader) : null;
        if (aRefreshHeader != null) {
            switch (state1) {
                case RESET:
                    aRefreshHeader.reset();
                    break;
                case PULL:
                    aRefreshHeader.pull();
                    break;
                case LOADING:
                    aRefreshHeader.refreshing();
                    break;
                case COMPLETE:
                    aRefreshHeader.complete();
                    break;
                default:
                    break;
            }
        }
    }

    private void setTargetOffsetTopAndBottom(int offset) {
        if (offset == 0) {
            return;
        }
        if (currentTargetOffsetTop > maxDragDistance && offset > 0) {
            onScrollFinish(true);
            return;
        }
        target.offsetTopAndBottom(offset);
        lastTargetOffsetTop = currentTargetOffsetTop;
        currentTargetOffsetTop = target.getTop();
        if (currentTargetOffsetTop < 0) {
            setTargetOffsetTopAndBottom(-currentTargetOffsetTop);
        }
        invalidate();
        touchEvent(target.getTop());
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (!isCanRefresh) {
            return false;
        }
        return super.onInterceptTouchEvent(ev);
    }

    private void sendCancelEvent() {
        if (lastEvent == null) {
            return;
        }
        MotionEvent ev = MotionEvent.obtain(lastEvent);
        ev.setAction(MotionEvent.ACTION_CANCEL);
        super.dispatchTouchEvent(ev);
    }

    private void onSecondaryPointerUp(MotionEvent ev) {
        final int pointerIndex = ev.getActionIndex();
        final int pointerId = ev.getPointerId(pointerIndex);
        if (pointerId == activePointerId) {
            // This was our active pointer going up. Choose a new
            // active pointer and adjust accordingly.
            final int newPointerIndex = pointerIndex == 0 ? 1 : 0;
            lastMotionY = ev.getY(newPointerIndex);
            //lastMotionX = ev.getX(newPointerIndex);
            activePointerId = ev.getPointerId(newPointerIndex);
        }
    }

    /**
     * canChildScrollUp
     * @return boolean
     */
    public boolean canChildScrollUp() {
        if (android.os.Build.VERSION.SDK_INT < 14) {
            if (target instanceof AbsListView) {
                final AbsListView absListView = (AbsListView) target;
                return absListView.getChildCount() > 0 && (absListView.getFirstVisiblePosition() > 0 || absListView.getChildAt(0).getTop() < absListView.getPaddingTop());
            } else {
                return target.canScrollVertically(-1) || target.getScrollY() > 0;
            }
        } else {
            return target.canScrollVertically(-1);
        }
    }

    /**
     * AutoScroll
     */
    private class AutoScroll implements Runnable {
        private Scroller scroller;
        private int lastY;

        public AutoScroll() {
            scroller = new Scroller(getContext());
        }

        @Override
        public void run() {
            boolean finished = !scroller.computeScrollOffset() || scroller.isFinished();
            if (!finished) {
                int currY = scroller.getCurrY();
                int offset = currY - lastY;
                lastY = currY;
                moveSpinner(offset);
                post(this);
                onScrollFinish(false);
            } else {
                stop();
                onScrollFinish(true);
            }
        }

        public void scrollTo(int to, int duration) {
            int from = currentTargetOffsetTop;
            int distance = to - from;
            stop();
            if (distance == 0) {
                return;
            }
            scroller.startScroll(0, 0, 0, distance, duration);
            post(this);
        }

        private void stop() {
            removeCallbacks(this);
            if (!scroller.isFinished()) {
                scroller.forceFinished(true);
            }
            lastY = 0;
        }
    }

    /**
     * 在scroll结束的时候会回调这个方法
     *
     * @param isForceFinish 是否是强制结束的
     */
    private void onScrollFinish(boolean isForceFinish) {
        if (isAutoRefresh && !isForceFinish) {
            isAutoRefresh = false;
            changeState(State.LOADING);
            if (refreshListener != null) {
                refreshListener.onRefresh();
            }
            finishSpinner();
        }
    }

    /**
     * OnRefreshListener
     */
    public interface OnRefreshListener {
        /**
         * onRefresh
         */
        void onRefresh();
    }

    /**
     * State
     */
    public enum State {
        /**
         * RESET
         */
        RESET,
        /**
         * PULL
         */
        PULL,
        /**
         * LOADING
         */
        LOADING,
        /**
         * COMPLETE
         */
        COMPLETE
    }

    public void setCanRefresh(boolean aIsCanRefresh) {
        this.isCanRefresh = aIsCanRefresh;
    }

}
