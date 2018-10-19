package com.dzbook.view.reader;

import android.animation.Animator;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.dzbook.activity.reader.ReaderActivity;
import com.dzbook.mvp.presenter.ReaderPresenter;
import com.dzbook.r.c.SettingManager;
import com.ishugui.R;

/**
 * 语音菜单
 *
 * @author wxliao on 18/4/18.
 */

public class ReaderMenuVoice extends FrameLayout implements View.OnClickListener {
    private LinearLayout layoutVoice;

    private SeekBar seekBar;
    private LinearLayout layoutFinishvoice;

    private TextView textviewVoiceplus0, textviewVoiceplus1, textviewVoiceplus2, textviewVoiceplus3;

    private TextView textviewVoicelocal0, textviewVoicelocal1, textviewVoicelocal2, textviewVoicelocal3;

    private SettingManager manager;

    /**
     * 构造
     *
     * @param context context
     */
    public ReaderMenuVoice(Context context) {
        this(context, null);
    }

    /**
     * 构造
     *
     * @param context context
     * @param attrs   attrs
     */
    public ReaderMenuVoice(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    private void initView(Context context) {
        LayoutInflater.from(context).inflate(R.layout.view_reader_menu_voice, this, true);
        layoutVoice = findViewById(R.id.layout_voice);
        seekBar = findViewById(R.id.seekBar_voiceSpeed);

        layoutFinishvoice = findViewById(R.id.layout_finishVoice);

        textviewVoicelocal0 = findViewById(R.id.textView_voiceLocal0);
        textviewVoicelocal1 = findViewById(R.id.textView_voiceLocal1);
        textviewVoicelocal2 = findViewById(R.id.textView_voiceLocal2);
        textviewVoicelocal3 = findViewById(R.id.textView_voiceLocal3);

        textviewVoiceplus0 = findViewById(R.id.textView_voicePlus0);
        textviewVoiceplus1 = findViewById(R.id.textView_voicePlus1);
        textviewVoiceplus2 = findViewById(R.id.textView_voicePlus2);
        textviewVoiceplus3 = findViewById(R.id.textView_voicePlus3);


        textviewVoiceplus0.setOnClickListener(this);
        textviewVoiceplus1.setOnClickListener(this);
        textviewVoiceplus2.setOnClickListener(this);
        textviewVoiceplus3.setOnClickListener(this);

        textviewVoicelocal0.setOnClickListener(this);
        textviewVoicelocal1.setOnClickListener(this);
        textviewVoicelocal2.setOnClickListener(this);
        textviewVoicelocal3.setOnClickListener(this);

        layoutFinishvoice.setOnClickListener(this);

        findViewById(R.id.layout_voiceTime).setOnClickListener(this);

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seek, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seek) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seek) {
                int progress = seek.getProgress();
                applyVoiceSpeed(progress);
            }
        });

        setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                getActivity().hideMenuPanel(false);
            }
        });

        manager = SettingManager.getInstance(context);
    }

    /**
     * 隐藏
     *
     * @param runnable runnable
     */
    public void hide(final Runnable runnable) {
        layoutVoice.setTranslationY(0);
        handeAnimate(runnable);
    }

    /**
     * 显示
     */
    public void show() {
        layoutVoice.setTranslationY(layoutVoice.getMeasuredHeight());
        layoutVoice.animate().translationY(0).setListener(null);
        refreshData();
    }


    private void handeAnimate(final Runnable runnable) {
        layoutVoice.animate().translationY(layoutVoice.getMeasuredHeight()).setListener(new Animator.AnimatorListener() {
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

    private void resetVoicePlusView(View view) {
        textviewVoiceplus0.setEnabled(true);
        textviewVoiceplus1.setEnabled(true);
        textviewVoiceplus2.setEnabled(true);
        textviewVoiceplus3.setEnabled(true);
        view.setEnabled(false);
    }

    private void resetVoiceLocalView(View view) {
        textviewVoicelocal0.setEnabled(true);
        textviewVoicelocal1.setEnabled(true);
        textviewVoicelocal2.setEnabled(true);
        textviewVoicelocal3.setEnabled(true);
        view.setEnabled(false);
    }

    /**
     * 刷新数据
     */
    public void refreshData() {
        int progress = manager.getVoiceSpeed();
        seekBar.setMax(90);
        seekBar.setProgress(progress);

        int plusIndex = manager.getVoicePlusIndex();
        switch (plusIndex) {
            case 0:
                resetVoicePlusView(textviewVoiceplus0);
                break;
            case 1:
                resetVoicePlusView(textviewVoiceplus1);
                break;
            case 2:
                resetVoicePlusView(textviewVoiceplus2);
                break;
            case 3:
                resetVoicePlusView(textviewVoiceplus3);
                break;
            default:
                break;
        }

        int localIndex = manager.getVoiceLocalIndex();
        switch (localIndex) {
            case 0:
                resetVoiceLocalView(textviewVoicelocal0);
                break;
            case 1:
                resetVoiceLocalView(textviewVoicelocal1);
                break;
            case 2:
                resetVoiceLocalView(textviewVoicelocal2);
                break;
            case 3:
                resetVoiceLocalView(textviewVoicelocal3);
                break;
            default:
                break;
        }
    }

    private void applyVoiceSpeed(int progress) {
        manager.setVoiceSpeed(progress);
        int voiceSpeed = progress / 10;
        ReaderPresenter presenter = getActivity().getPresenter();
        if (presenter != null) {
            presenter.resetVoiceSpeed(voiceSpeed + "");
        }
    }

    private void applyVoicePlusIndex(int index, View view) {
        manager.setVoicePlusIndex(index);
        resetVoicePlusView(view);
        ReaderPresenter presenter = getActivity().getPresenter();
        if (presenter != null) {
            presenter.resetVoicePlusType(index);
        }
    }

    private void applyVoiceLocalIndex(int index, View view) {
        manager.setVoiceLocalIndex(index);
        resetVoiceLocalView(view);
        ReaderPresenter presenter = getActivity().getPresenter();
        if (presenter != null) {
            presenter.resetVoiceLocalType(index);
        }
    }

    private ReaderActivity getActivity() {
        return (ReaderActivity) getContext();
    }

    private void finishVoice() {
        ReaderPresenter presenter = getActivity().getPresenter();
        if (presenter != null) {
            presenter.finishVoice(ReaderPresenter.VFT_FROM_MENU);
        }
    }

    private void showVoiceTimeMenu() {
        ReaderActivity activity = getActivity();
        activity.setMenuState(ReaderNewPanel.STATE_VOICE_TIME);
        activity.showMenuPanel();
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();

        //在线声音
        if (id == R.id.textView_voicePlus0) {
            applyVoicePlusIndex(0, view);
        } else if (id == R.id.textView_voicePlus1) {
            applyVoicePlusIndex(1, view);
        } else if (id == R.id.textView_voicePlus2) {
            applyVoicePlusIndex(2, view);
        } else if (id == R.id.textView_voicePlus3) {
            applyVoicePlusIndex(3, view);
        } else if (id == R.id.textView_voiceLocal0) {
            //离线声音
            applyVoiceLocalIndex(0, view);
        } else if (id == R.id.textView_voiceLocal1) {
            applyVoiceLocalIndex(1, view);
        } else if (id == R.id.textView_voiceLocal2) {
            applyVoiceLocalIndex(2, view);
        } else if (id == R.id.textView_voiceLocal3) {
            applyVoiceLocalIndex(3, view);
        } else if (id == R.id.layout_finishVoice) {
            finishVoice();
        } else if (id == R.id.layout_voiceTime) {
            showVoiceTimeMenu();
        }
    }
}
