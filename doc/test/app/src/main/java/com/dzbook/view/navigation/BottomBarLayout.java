package com.dzbook.view.navigation;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.RelativeLayout;

import com.dzbook.utils.DimensionPixelUtil;
import com.ishugui.R;

/**
 * BottomBarLayout
 *
 * @author wxliao on 17/3/28.
 */

public class BottomBarLayout extends RelativeLayout implements View.OnClickListener {
    private static final float SHADOW_SIZE = 4f;
    SparseArray<View> sparseArray = new SparseArray<View>();
    private NavigationListener mListener;
    private ViewGroup tabContainer;
    private int tabContainerId;

    private int defaultCheckedPosition = 0;
    private int checkedPosition = -1;
    private long clickTime = 0;


    /**
     * 构造
     *
     * @param context context
     */
    public BottomBarLayout(Context context) {
        this(context, null);
    }

    /**
     * 构造
     *
     * @param context context
     * @param attrs   attrs
     */
    public BottomBarLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    /**
     * 构造
     *
     * @param context      context
     * @param attrs        attrs
     * @param defStyleAttr defStyleAttr
     */
    public BottomBarLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context, attrs);
    }


    private void initView(Context context, AttributeSet attrs) {

        if (attrs != null) {
            TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.BottomBarLayout, 0, 0);
            tabContainerId = a.getResourceId(R.styleable.BottomBarLayout_tab_containerId, 0);
            a.recycle();
        }

        float elevation = DimensionPixelUtil.dip2px(context, SHADOW_SIZE);
        ViewCompat.setElevation(this, elevation);
        setClipToPadding(false);

        getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {

                getViewTreeObserver().removeOnGlobalLayoutListener(this);

                if (tabContainerId != 0) {
                    tabContainer = findViewById(tabContainerId);
                    int count = tabContainer.getChildCount();
                    sparseArray.clear();
                    for (int i = 0; i < count; i++) {
                        View view = tabContainer.getChildAt(i);
                        if (view instanceof NavigationAble) {
                            if (checkedPosition < 0) {
                                checkedPosition = defaultCheckedPosition;
                            }
                            if (i == checkedPosition) {
                                ((NavigationAble) view).select();
                            } else {
                                ((NavigationAble) view).unSelect();
                            }
                            //                            ((NavigationAble) view).hideNewMessage();
                        }
                        //                        view.setTag(i);
                        sparseArray.put(i, view);
                        view.setOnClickListener(BottomBarLayout.this);
                    }
                }

            }
        });
    }

    @Override
    public void onClick(View view) {
        int position = sparseArray.indexOfValue(view);
        if (mListener != null) {
            mListener.onTabClick(view, position, checkedPosition);
            if (position == checkedPosition) {
                if ((System.currentTimeMillis() - clickTime) > 500) {
                    clickTime = System.currentTimeMillis();
                    mListener.onReClick(view, position);
                } else {
                    mListener.onDoubleClick(view, position);
                }
            }
        }
        setSelect(position);
    }

    public void setNavigationListener(NavigationListener listener) {
        mListener = listener;
    }

    /**
     * 事件接口
     */
    public interface NavigationListener {
        /**
         * 选中tab
         *
         * @param view             view
         * @param viewPosition     viewPosition
         * @param selectedPosition selectedPosition
         */
        void onTabSelect(View view, int viewPosition, int selectedPosition);

        /**
         * 点击tab
         *
         * @param view             view
         * @param viewPosition     viewPosition
         * @param selectedPosition selectedPosition
         */
        void onTabClick(View view, int viewPosition, int selectedPosition);

        /**
         * 选中状态下再次点击
         *
         * @param view         view
         * @param viewPosition viewPosition
         */
        void onReClick(View view, int viewPosition);

        /**
         * 双击
         *
         * @param view         view
         * @param viewPosition viewPosition
         */
        void onDoubleClick(View view, int viewPosition);
    }


    /**
     * 设置选中
     *
     * @param position 位置索引
     */
    public void setSelect(int position) {
        View view = sparseArray.get(position);
        if ((view != null) && (view instanceof NavigationAble)) {
            View checkedView = sparseArray.get(checkedPosition);
            if ((checkedView != null) && (checkedView instanceof NavigationAble)) {
                ((NavigationAble) checkedView).unSelect();
            }

            ((NavigationAble) view).select();
            if (mListener != null) {
                mListener.onTabSelect(view, position, checkedPosition);
            }
        }
        checkedPosition = position;
    }

    //    private ViewPropertyAnimatorCompat mTranslationAnimator;
    //    private static final int DEFAULT_ANIMATION_DURATION = 200;
    //    private static final Interpolator INTERPOLATOR = new LinearOutSlowInInterpolator();
    //    private boolean mAutoHideEnabled;
    //    private boolean mIsHidden = false;
    //
    //    @Override
    //    public void hide() {
    //        hide(true);
    //    }
    //
    //    @Override
    //    public void show() {
    //        show(true);
    //    }
    //
    //    @Override
    //    public boolean isAutoHideEnable() {
    //        return mAutoHideEnabled;
    //    }
    //
    //    @Override
    //    public boolean isHidden() {
    //        return mIsHidden;
    //    }

    //    public void hide(boolean animate) {
    //        mIsHidden = true;
    //        setTranslationY(this.getHeight(), animate);
    //    }
    //
    //    public void show(boolean animate) {
    //        mIsHidden = false;
    //        setTranslationY(0, animate);
    //    }

    //    private void setTranslationY(int offset, boolean animate) {
    //        if (animate) {
    //            animateOffset(offset);
    //        } else {
    //            if (mTranslationAnimator != null) {
    //                mTranslationAnimator.cancel();
    //            }
    //            this.setTranslationY(offset);
    //        }
    //    }

    //    private void animateOffset(final int offset) {
    //        if (mTranslationAnimator == null) {
    //            mTranslationAnimator = ViewCompat.animate(this);
    //            mTranslationAnimator.setDuration(DEFAULT_ANIMATION_DURATION);
    //            mTranslationAnimator.setInterpolator(INTERPOLATOR);
    //        } else {
    //            mTranslationAnimator.cancel();
    //        }
    //        mTranslationAnimator.translationY(offset).start();
    //    }


    //    public void setAutoHideEnabled(boolean mAutoHideEnabled) {
    //        this.mAutoHideEnabled = mAutoHideEnabled;
    //    }
}
