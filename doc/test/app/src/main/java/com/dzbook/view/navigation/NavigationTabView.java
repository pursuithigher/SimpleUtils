package com.dzbook.view.navigation;

import android.app.Activity;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dzbook.bean.MainTabBean;
import com.dzbook.lib.utils.CompatUtils;
import com.dzbook.utils.GlideImageLoadUtils;
import com.dzbook.utils.TypefaceUtils;
import com.ishugui.R;


/**
 * NavigationTabView
 *
 * @author wxliao on 2016/7/18.
 */
public class NavigationTabView extends LinearLayout implements NavigationAble {
    static final float NUM = 0.5f;
    ScaleAnimation animation = new ScaleAnimation(0.0f, 1.0f, 0.0f, 1.0f, Animation.RELATIVE_TO_SELF, NUM, Animation.RELATIVE_TO_SELF, NUM);

    private ImageView imageView;
    private TextView textView;
    private ImageView imageviewDot;
    private RelativeLayout layoutContainer;
    private boolean textGone;
    private MainTabBean bean;


    /**
     * 构造
     *
     * @param context context
     */
    public NavigationTabView(Context context) {
        this(context, null);
    }

    /**
     * 构造
     *
     * @param context context
     * @param attrs   attrs
     */
    public NavigationTabView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context, attrs);
    }
    //    ColorStateList list1 = getResources().getColorStateList(R.color.menu_text);


    /**
     * NavigationTabView
     * @param context context
     * @param bean    bean
     * @param isGone  isGone
     */
    public NavigationTabView(Context context, MainTabBean bean, boolean isGone) {
        super(context);
        this.bean = bean;
        textGone = isGone;
        initView(context, null);
        setBackgroundResource(R.drawable.selector_hw_list_item_04_cornor);
    }
    //    }
    //
    //        super(context, attrs, defStyleAttr);
    //    public NavigationTabView(Context context, AttributeSet attrs, int defStyleAttr) {


    private void initView(Context context, AttributeSet attrs) {
        setOrientation(LinearLayout.VERTICAL);
        setGravity(Gravity.CENTER);

        LayoutInflater.from(context).inflate(R.layout.view_navigation_tab, this);
        imageView = findViewById(R.id.imageView);
        textView = findViewById(R.id.textView);
        imageviewDot = findViewById(R.id.imageView_dot);
        layoutContainer = findViewById(R.id.layout_container);
        TypefaceUtils.setHwChineseMediumFonts(textView);

        //        if (tabIconRes != 0) {
        //            Drawable drawable = ContextCompat.getDrawable(context, tabIconRes);
        //            if (drawable instanceof StateListDrawable) {
        //            imageView.setImageDrawable(drawable);
        //            } else {
        //                if (tabStateColorRes != 0) {
        //
        //                    Drawable wrapDrawable = DrawableCompat.wrap(drawable);
        //                    ColorStateList colorList = ContextCompat.getColorStateList(context, tabStateColorRes);
        //                    DrawableCompat.setTintList(wrapDrawable, colorList);
        //                    imageView.setImageDrawable(wrapDrawable);
        //                } else {
        //                    imageView.setImageDrawable(drawable);
        //                }
        //            }
        //        }

        if (bean != null) {
            textView.setText(bean.title);
            textView.setTextColor(createTabTextColor());

            Drawable drawable = ContextCompat.getDrawable(context, bean.res);
            imageView.setImageDrawable(drawable);
        }
    }
    //    private static final float textScaleRate = 0.95f;
    //    private static final int ANIMATION_DURATION = 200;
    //    private int paddingTop = 10;


    @Override
    public void select() {
        TypefaceUtils.setHwChineseMediumFonts(textView);
//        textView.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
        setSelected(true);
        setImageViewIcon(true);
        hideNewMessage();
        if (textGone) {
            textView.setVisibility(GONE);
            ViewGroup.LayoutParams layoutParams = layoutContainer.getLayoutParams();
            layoutParams.width = getResources().getDimensionPixelOffset(R.dimen.dp_40);
            layoutParams.height = getResources().getDimensionPixelOffset(R.dimen.dp_40);
            layoutContainer.setLayoutParams(layoutParams);
        }
        //        textView.animate().scaleX(1).scaleY(1).setDuration(ANIMATION_DURATION).start();
        //
        //        ValueAnimator animator = ValueAnimator.ofInt(layoutContainer.getPaddingTop(), 0);
        //        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
        //            @Override
        //            public void onAnimationUpdate(ValueAnimator valueAnimator) {
        //                layoutContainer.setPadding(layoutContainer.getPaddingLeft(), (Integer) valueAnimator.getAnimatedValue(),
        //                        layoutContainer.getPaddingRight(),
        //                        layoutContainer.getPaddingBottom());
        //            }
        //        });
        //        animator.setDuration(ANIMATION_DURATION);
        //        animator.start();
    }

    /**
     * startAnimation
     */
    public void startAnimation() {
        animation.cancel();
        animation.setDuration(300);//设置动画持续时间
        layoutContainer.startAnimation(animation);
    }


    @Override
    public void unSelect() {
        TypefaceUtils.setHwChineseMediumFonts(textView);
        setSelected(false);
        setImageViewIcon(false);
        if (textGone) {
            textView.setVisibility(VISIBLE);
            ViewGroup.LayoutParams layoutParams = layoutContainer.getLayoutParams();
            layoutParams.width = getResources().getDimensionPixelOffset(R.dimen.dp_26);
            layoutParams.height = getResources().getDimensionPixelOffset(R.dimen.dp_26);
            layoutContainer.setLayoutParams(layoutParams);
        }

        //        textView.animate().scaleX(textScaleRate).scaleY(textScaleRate).setDuration(ANIMATION_DURATION).start();
        //
        //        ValueAnimator animator = ValueAnimator.ofInt(layoutContainer.getPaddingTop(), paddingTop);
        //        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
        //            @Override
        //            public void onAnimationUpdate(ValueAnimator valueAnimator) {
        //                layoutContainer.setPadding(layoutContainer.getPaddingLeft(),
        //                        (Integer) valueAnimator.getAnimatedValue(),
        //                        layoutContainer.getPaddingRight(),
        //                        layoutContainer.getPaddingBottom());
        //            }
        //        });
        //        animator.setDuration(ANIMATION_DURATION);
        //        animator.start();
    }

    @Override
    public void showNewMessage() {
        if (!isSelected()) {
            imageviewDot.setVisibility(View.VISIBLE);
            //            imageviewDot.animate().scaleX(1).scaleY(1).setDuration(ANIMATION_DURATION).start();
        }
    }

    @Override
    public void hideNewMessage() {
        //        imageviewDot.animate().scaleX(0).scaleY(0).setDuration(ANIMATION_DURATION).start();
        imageviewDot.setVisibility(View.INVISIBLE);
    }


    private ColorStateList createTabTextColor() {
        if (bean != null && !TextUtils.isEmpty(bean.color) && !TextUtils.isEmpty(bean.colorPressed)) {

            try {
                return createColorStateList(bean.colorPressed, bean.colorPressed, bean.color);
            } catch (Throwable e) {
                e.printStackTrace();
                return CompatUtils.getColorStateList(getContext(), R.color.menu_text);
            }
        } else {
            return CompatUtils.getColorStateList(getContext(), R.color.menu_text);
        }
    }

    private static ColorStateList createColorStateList(String selected, String pressed, String normal) {
        int[] colors = new int[]{Color.parseColor(selected), Color.parseColor(pressed), Color.parseColor(normal)};
        int[][] states = new int[3][];
        states[0] = new int[]{android.R.attr.state_selected};
        states[1] = new int[]{android.R.attr.state_pressed};
        states[2] = new int[]{};
        return new ColorStateList(states, colors);
    }

    private void setImageViewIcon(boolean isSelect) {
        try {
            if (bean != null && !TextUtils.isEmpty(bean.iconNormal) && !TextUtils.isEmpty(bean.iconPressed)) {
                if (isSelect) {
                    GlideImageLoadUtils.getInstanse().glideImageLoadFromUrl((Activity) getContext(), imageView, bean.iconPressed, bean.res);
                } else {
                    GlideImageLoadUtils.getInstanse().glideImageLoadFromUrl((Activity) getContext(), imageView, bean.iconNormal, bean.res);
                }
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }
}
