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
import com.dzbook.mvp.presenter.ReaderPresenter;
import com.dzbook.r.c.SettingManager;
import com.ishugui.R;

/**
 * ReaderMenuVoiceTime
 *
 * @author wxliao on 18/4/19.
 */

public class ReaderMenuVoiceTime extends FrameLayout implements View.OnClickListener, Menuable {

    private LinearLayout layoutVoicetime;
    private SettingManager manager;
    private TextView textviewTime1, textviewTime2, textviewTime3, textviewTime4, textviewTime0;

    /**
     * 构造
     *
     * @param context context
     */
    public ReaderMenuVoiceTime(@NonNull Context context) {
        this(context, null);
    }

    /**
     * 构造
     *
     * @param context context
     * @param attrs   attrs
     */
    public ReaderMenuVoiceTime(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    private void initView(Context context) {
        LayoutInflater.from(context).inflate(R.layout.view_reader_menu_voice_time, this, true);
        layoutVoicetime = findViewById(R.id.layout_voiceTime);

        textviewTime1 = findViewById(R.id.textView_time1);
        textviewTime2 = findViewById(R.id.textView_time2);
        textviewTime3 = findViewById(R.id.textView_time3);
        textviewTime4 = findViewById(R.id.textView_time4);
        textviewTime0 = findViewById(R.id.textView_time0);

        textviewTime1.setOnClickListener(this);
        textviewTime2.setOnClickListener(this);
        textviewTime3.setOnClickListener(this);
        textviewTime4.setOnClickListener(this);
        textviewTime0.setOnClickListener(this);

        setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                resetMenuState();
                getActivity().hideMenuPanel(false);
            }
        });

        manager = SettingManager.getInstance(context);
    }


    private void handleAnimate(final Runnable runnable) {
        layoutVoicetime.animate().translationY(layoutVoicetime.getMeasuredHeight()).setListener(new Animator.AnimatorListener() {
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
     * 显示
     */
    public void show() {
        layoutVoicetime.setTranslationY(layoutVoicetime.getMeasuredHeight());
        layoutVoicetime.animate().translationY(0).setListener(null);
        refreshData();
    }


    @Override
    public void refreshData() {
        int voiceTimeIndex = manager.getVoiceTimeIndex();
        switch (voiceTimeIndex) {
            case 0:
                resetVoiceTimeView(textviewTime0);
                break;
            case 1:
                resetVoiceTimeView(textviewTime1);
                break;
            case 2:
                resetVoiceTimeView(textviewTime2);
                break;
            case 3:
                resetVoiceTimeView(textviewTime3);
                break;
            case 4:
                resetVoiceTimeView(textviewTime4);
                break;
            default:
                break;
        }
    }

    /**
     * 隐藏
     *
     * @param runnable runnable
     */
    public void hide(final Runnable runnable) {
        layoutVoicetime.setTranslationY(0);
        handleAnimate(runnable);
    }

    private void resetVoiceTimeView(View view) {
        textviewTime1.setEnabled(true);
        textviewTime2.setEnabled(true);
        textviewTime3.setEnabled(true);
        textviewTime4.setEnabled(true);
        textviewTime0.setEnabled(true);
        view.setEnabled(false);
    }

    private ReaderActivity getActivity() {
        return (ReaderActivity) getContext();
    }

    private void resetMenuState() {
        ReaderActivity activity = getActivity();
        activity.setMenuState(ReaderNewPanel.STATE_VOICE);
    }

    private void applyVoiceTime(int index, View view) {
        resetVoiceTimeView(view);
        manager.setVoiceTimeIndex(index);
        resetMenuState();
        ReaderPresenter presenter = getActivity().getPresenter();
        if (presenter != null) {
            presenter.resetVoiceTime(index);
        }
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.textView_time0) {
            applyVoiceTime(0, view);
        } else if (id == R.id.textView_time1) {
            applyVoiceTime(1, view);
        } else if (id == R.id.textView_time2) {
            applyVoiceTime(2, view);
        } else if (id == R.id.textView_time3) {
            applyVoiceTime(3, view);
        } else if (id == R.id.textView_time4) {
            applyVoiceTime(4, view);
        }
    }
}
