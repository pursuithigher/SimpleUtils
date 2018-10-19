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
import android.widget.SeekBar;
import android.widget.TextView;

import com.dzbook.activity.reader.ReaderActivity;
import com.dzbook.r.c.SettingManager;
import com.ishugui.R;

/**
 * 自动阅读
 *
 * @author wxliao on 18/4/19.
 */

public class ReaderMenuAutoRead extends FrameLayout implements View.OnClickListener, Menuable {
    private SettingManager manager;

    private LinearLayout layoutAutoread;
    private SeekBar seekbarAutospeed;
    private TextView textviewAnim4;
    private TextView textviewAnim3;

    /**
     * 构造
     *
     * @param context context
     */
    public ReaderMenuAutoRead(@NonNull Context context) {
        this(context, null);
    }

    /**
     * 构造
     *
     * @param context context
     * @param attrs   attrs
     */
    public ReaderMenuAutoRead(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    private void initView(Context context) {
        LayoutInflater.from(context).inflate(R.layout.view_reader_menu_auto_read, this, true);
        layoutAutoread = findViewById(R.id.layout_autoRead);
        seekbarAutospeed = findViewById(R.id.seekBar_autoSpeed);
        //        textView_speedNum = findViewById(R.id.textView_speedNum);
        textviewAnim4 = findViewById(R.id.textView_anim4);
        textviewAnim3 = findViewById(R.id.textView_anim3);

        findViewById(R.id.textView_speedDown).setOnClickListener(this);
        findViewById(R.id.textView_speedUp).setOnClickListener(this);
        findViewById(R.id.layout_finishAuto).setOnClickListener(this);

        textviewAnim3.setOnClickListener(this);
        textviewAnim4.setOnClickListener(this);

        seekbarAutospeed.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                int autoSpeed = progress + 1;
                //                textView_speedNum.setText(String.valueOf(autoSpeed));
                manager.setAutoReadSpeed(autoSpeed);
                int animIndex = manager.getAutoReadIndex();
                getActivity().startAutoRead(animIndex, autoSpeed, false);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
        manager = SettingManager.getInstance(context);

        setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                getActivity().hideMenuPanel(false);
            }
        });
    }

    /**
     * 显示
     */
    public void show() {
        layoutAutoread.setTranslationY(layoutAutoread.getMeasuredHeight());
        layoutAutoread.animate().translationY(0).setListener(null);
        refreshData();
    }


    private void handleAnimate(final Runnable runnable) {
        layoutAutoread.animate().translationY(layoutAutoread.getMeasuredHeight()).setListener(new Animator.AnimatorListener() {
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

    /**
     * 隐藏
     *
     * @param runnable runnable
     */
    public void hide(final Runnable runnable) {
        layoutAutoread.setTranslationY(0);
        handleAnimate(runnable);
    }

    @Override
    public void refreshData() {
        int animSpeed = manager.getAutoReadSpeed();
        seekbarAutospeed.setProgress(animSpeed - 1);
        int animIndex = manager.getAutoReadIndex();
        switch (animIndex) {
            case 3:
                resetAnimStyleView(textviewAnim3);
                break;
            case 4:
                resetAnimStyleView(textviewAnim4);
                break;
            default:
                break;
        }
    }

    private void resetAnimStyleView(View view) {
        textviewAnim3.setEnabled(true);
        textviewAnim4.setEnabled(true);
        view.setEnabled(false);
    }

    private ReaderActivity getActivity() {
        return (ReaderActivity) getContext();
    }

    private void applyAnim(int index, View view) {
        resetAnimStyleView(view);
        manager.setAutoReadIndex(index);
        //        getActivity().applyAnim(index);
        int speed = manager.getAutoReadSpeed();
        getActivity().startAutoRead(index, speed, true);
    }

    private void finishAutoRead() {
        getActivity().finishAutoRead();
        //        ToastAlone.showShort(R.string.toast_quit_auto_read);
        //        getActivity().hideMenuPanel();
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.textView_speedDown) {
            seekbarAutospeed.setProgress(seekbarAutospeed.getProgress() - 1);
        } else if (id == R.id.textView_speedUp) {
            seekbarAutospeed.setProgress(seekbarAutospeed.getProgress() + 1);
        } else if (id == R.id.layout_finishAuto) {
            finishAutoRead();
        } else if (id == R.id.textView_anim3) {
            applyAnim(3, view);
        } else if (id == R.id.textView_anim4) {
            applyAnim(4, view);
        }
    }
}
