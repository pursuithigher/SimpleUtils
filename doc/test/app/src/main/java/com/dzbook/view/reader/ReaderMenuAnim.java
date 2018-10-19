package com.dzbook.view.reader;

import android.animation.Animator;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dzbook.activity.reader.ReaderActivity;
import com.dzbook.r.c.SettingManager;
import com.ishugui.R;

/**
 * ReaderMenuAnim
 *
 * @author wxliao on 18/4/19.
 */

public class ReaderMenuAnim extends FrameLayout implements View.OnClickListener, Menuable {
    private LinearLayout layoutAnim;

    private TextView textviewAnim1;
    private TextView textviewAnim2;
    private TextView textviewAnim5;
    private TextView textviewAnim0;

    private SettingManager manager;

    /**
     * 构造
     *
     * @param context context
     */
    public ReaderMenuAnim(@NonNull Context context) {
        this(context, null);
    }

    /**
     * 构造
     *
     * @param context context
     * @param attrs   attrs
     */
    public ReaderMenuAnim(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    private void initView(Context context) {
        LayoutInflater.from(context).inflate(R.layout.view_reader_menu_anim, this, true);
        layoutAnim = findViewById(R.id.layout_anim);
        textviewAnim1 = findViewById(R.id.textView_anim1);
        textviewAnim2 = findViewById(R.id.textView_anim2);
        textviewAnim5 = findViewById(R.id.textView_anim5);
        textviewAnim0 = findViewById(R.id.textView_anim0);

        textviewAnim1.setOnClickListener(this);
        textviewAnim2.setOnClickListener(this);
        textviewAnim5.setOnClickListener(this);
        textviewAnim0.setOnClickListener(this);

        manager = SettingManager.getInstance(context);
    }

    /**
     * 显示
     */
    public void show() {
        layoutAnim.setTranslationY(layoutAnim.getMeasuredHeight());
        layoutAnim.animate().translationY(0).setListener(null);
        refreshData();
    }

    /**
     * 隐藏
     *
     * @param runnable runnable
     */
    public void hide(final Runnable runnable) {
        layoutAnim.setTranslationY(0);
        handleAnimate(runnable);
    }

    private void handleAnimate(final Runnable runnable) {
        layoutAnim.animate().translationY(layoutAnim.getMeasuredHeight()).setListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {
                runnable.run();
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });
    }

    @Override
    public void refreshData() {
        int animStyleIndex = manager.getAnimStyleIndex();
        switch (animStyleIndex) {
            case 1:
                resetAnimStyleView(textviewAnim1);
                break;
            case 2:
                resetAnimStyleView(textviewAnim2);
                break;
            case 5:
                resetAnimStyleView(textviewAnim5);
                break;
            case 0:
                resetAnimStyleView(textviewAnim0);
                break;
            default:
                break;
        }
    }

    private void resetAnimStyleView(View view) {
        textviewAnim1.setEnabled(true);
        textviewAnim2.setEnabled(true);
        textviewAnim5.setEnabled(true);
        textviewAnim0.setEnabled(true);
        view.setEnabled(false);
    }

    private ReaderActivity getActivity() {
        return (ReaderActivity) getContext();
    }

    private void applyAnim(int index, View view) {
        resetAnimStyleView(view);
        manager.setAnimStyleIndex(index);
        getActivity().applyAnim(index);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.textView_anim1) {
            applyAnim(1, view);
        } else if (id == R.id.textView_anim2) {
            applyAnim(2, view);
        } else if (id == R.id.textView_anim5) {
            applyAnim(5, view);
        } else if (id == R.id.textView_anim0) {
            applyAnim(0, view);
        }
    }
}
