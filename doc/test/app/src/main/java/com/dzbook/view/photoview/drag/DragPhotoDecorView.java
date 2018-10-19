package com.dzbook.view.photoview.drag;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.dzbook.view.common.BlackBackView;
import com.dzbook.view.photoview.PhotoView;

import java.io.File;

/**
 * 图片放大浏览Activity
 *
 * @author wangjianchen
 */
public class DragPhotoDecorView extends FrameLayout {
    private static final float NUM = 0.5f;

    ExitListener listener;

    private PhotoView contentImageView;
    private ImageView dragImagView;
    private BlackBackView blackBackView;
    private Drawable drawable;

    private float scale;
    private float enterTransY;
    private float enterTransX;


    /**
     * 构造
     *
     * @param context context
     */
    public DragPhotoDecorView(Context context) {
        super(context);
    }

    /**
     * 构造
     *
     * @param context context
     * @param attrs   attrs
     */
    public DragPhotoDecorView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    /**
     * 开始
     *
     * @param rootWidth  rootWidth
     * @param rootHeight rootHeight
     * @param left       left
     * @param top        top
     * @param right      right
     * @param bottom     bottom
     * @param url        url
     */
    public void start(int rootWidth, int rootHeight, int left, int top, int right, int bottom, String url) {
        if (blackBackView == null) {
            blackBackView = new BlackBackView(getContext());
            blackBackView.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            addView(blackBackView);
        }
        FrameLayout.LayoutParams layoutParams;
        if (dragImagView == null) {
            int width = right - left;
            int height = bottom - top;
            if (width == 0 || height == 0) {
                if (listener != null) {
                    listener.exitEvent();
                }
                return;
            }
            float scaleX = rootWidth * 1.0f / width;
            float scaleY = rootHeight * 1.0f / height;
            // 计算出最小的scale缩放量
            if (scaleX < scaleY) {
                scale = scaleX;
            } else {
                scale = scaleY;
            }
            dragImagView = new ImageView(getContext());
            dragImagView.setScaleType(ImageView.ScaleType.FIT_XY);
            layoutParams = new FrameLayout.LayoutParams(width, height);
            layoutParams.setMargins(left, top, 0, 0);
            dragImagView.setLayoutParams(layoutParams);
            addView(dragImagView);
            // 偏移 Y
            float finalTop = rootHeight * NUM - height * scale * NUM;
            float scaleHeight = (height * scale - height) * NUM;
            enterTransY = (finalTop - top) + scaleHeight;
            // 偏移 X
            float finalLeft = rootWidth * NUM - width * scale * NUM;
            float scaleWidth = (width * scale - width) * NUM;
            enterTransX = (finalLeft - left) + scaleWidth;
        }
        // 内容操控view
        if (contentImageView == null) {
            contentImageView = new PhotoView(getContext());
            contentImageView.setVisibility(GONE);
            contentImageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
            layoutParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            contentImageView.setLayoutParams(layoutParams);
            addView(contentImageView);
        }
        // 加载图片，开始动画
        Glide.with(this).load(new File(url)).listener(new RequestListener<Drawable>() {
            @Override
            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                if (listener != null) {
                    listener.exitEvent();
                }
                return false;
            }

            @Override
            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                drawable = resource;
                performEnterAnimation();
                return false;
            }
        }).into(dragImagView);
        contentImageView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                performExitAnimation();
            }
        });
        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });
    }

    /**
     * 进入动画
     */
    private void performEnterAnimation() {
        // 平移
        ObjectAnimator animator = ObjectAnimator.ofFloat(dragImagView, "translationY", 0, enterTransY);
        animator.setDuration(500);
        animator.start();
        ObjectAnimator animatorX = ObjectAnimator.ofFloat(dragImagView, "translationX", 0, enterTransX);
        animatorX.setDuration(500);
        animatorX.start();
        // 缩放
        ValueAnimator scaleYAnimator = ValueAnimator.ofFloat(1, scale);
        scaleYAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                if (dragImagView != null) {
                    dragImagView.setScaleY((Float) valueAnimator.getAnimatedValue());
                    dragImagView.setScaleX((Float) valueAnimator.getAnimatedValue());
                }
            }
        });
        scaleYAnimator.setDuration(500);
        scaleYAnimator.start();
        // 渐变
        ValueAnimator alphaAnimator = ValueAnimator.ofFloat(0, 255);
        alphaAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                if (blackBackView != null) {
                    blackBackView.setAlpha((int) (float) (Float) valueAnimator.getAnimatedValue());
                }
            }
        });
        alphaAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                if (contentImageView != null) {
                    contentImageView.setVisibility(VISIBLE);
                    contentImageView.setImageDrawable(drawable);
                }
                if (dragImagView != null) {
                    dragImagView.setVisibility(GONE);
                }
            }

            @Override
            public void onAnimationCancel(Animator animation) {
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
            }
        });
        alphaAnimator.setDuration(500);
        alphaAnimator.start();
    }

    /**
     * 退出动画
     */
    public void performExitAnimation() {
        if (contentImageView != null) {
            contentImageView.setVisibility(GONE);
        }
        if (dragImagView != null) {
            dragImagView.setVisibility(VISIBLE);
        }
        // 移动
        ObjectAnimator animator = ObjectAnimator.ofFloat(dragImagView, "translationY", enterTransY, 0);
        animator.setDuration(500);
        animator.start();
        ObjectAnimator animatorX = ObjectAnimator.ofFloat(dragImagView, "translationX", enterTransX, 0);
        animatorX.setDuration(500);
        animatorX.start();
        // 缩放
        ValueAnimator scaleYAnimator = ValueAnimator.ofFloat(scale, 1);
        scaleYAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                if (dragImagView != null) {
                    dragImagView.setScaleY((Float) valueAnimator.getAnimatedValue());
                    dragImagView.setScaleX((Float) valueAnimator.getAnimatedValue());
                }
            }
        });
        scaleYAnimator.setDuration(500);
        scaleYAnimator.start();
        // 渐变
        ValueAnimator alphaAnimator = ValueAnimator.ofFloat(255, 0);
        alphaAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                if (blackBackView != null) {
                    blackBackView.setAlpha((int) (float) (Float) valueAnimator.getAnimatedValue());
                }
            }
        });
        alphaAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                if (listener != null) {
                    listener.exitEvent();
                }
            }

            @Override
            public void onAnimationCancel(Animator animation) {
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
            }
        });
        alphaAnimator.setDuration(500);
        alphaAnimator.start();
    }

    /**
     * 销毁
     */
    public void destroyDragView() {
        dragImagView = null;
        blackBackView = null;
        contentImageView = null;
    }


    public void setExitListener(ExitListener listener1) {
        this.listener = listener1;
    }

    /**
     * 退出监听
     */
    public interface ExitListener {
        /**
         * 退出
         */
        void exitEvent();
    }
}
