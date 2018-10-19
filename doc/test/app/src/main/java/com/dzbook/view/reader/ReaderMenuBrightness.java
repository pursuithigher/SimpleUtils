package com.dzbook.view.reader;

import android.animation.Animator;
import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.dzbook.AppConst;
import com.dzbook.event.EventBusUtils;
import com.dzbook.event.EventConstant;
import com.dzbook.r.c.SettingManager;
import com.dzbook.utils.ScreenUtils;
import com.dzbook.view.DzSwitchButton;
import com.dzbook.view.SwitchButton;
import com.ishugui.R;

/**
 * 亮度
 *
 * @author wxliao on 18/4/18.
 */

public class ReaderMenuBrightness extends FrameLayout implements View.OnClickListener, Menuable {
    /**
     * 根布局
     */
    public LinearLayout layoutBrightness;
    private TextView textviewBrightnesspercent;
    private SeekBar seekbarBrightness;
    private DzSwitchButton switchbuttonEyemode;
    private DzSwitchButton switchbuttonSyslight;
    private SettingManager manager;

    /**
     * 构造
     *
     * @param context context
     */
    public ReaderMenuBrightness(Context context) {
        this(context, null);
    }

    /**
     * 构造
     *
     * @param context context
     * @param attrs   attrs
     */
    public ReaderMenuBrightness(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    private void initView(Context context) {
        LayoutInflater.from(context).inflate(R.layout.view_reader_menu_brightness, this, true);
        manager = SettingManager.getInstance(context);
        layoutBrightness = findViewById(R.id.layout_brightness);

        switchbuttonEyemode = findViewById(R.id.switchButton_eyeMode);
        switchbuttonSyslight = findViewById(R.id.switchButton_sysLight);

        seekbarBrightness = findViewById(R.id.seekBar_brightness);
        textviewBrightnesspercent = findViewById(R.id.textView_brightnessPercent);

        findViewById(R.id.imageView_brightnessDown).setOnClickListener(this);
        findViewById(R.id.imageView_brightnessUp).setOnClickListener(this);

        switchbuttonSyslight.setOnCheckedChangeListener(new SwitchButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(SwitchButton view, boolean isChecked) {
                manager.setSystemBrightSystem(isChecked);
                if (isChecked) {
                    setSystemBrightness();
                } else {
                    //根据黑白模式修改屏幕
                    int brightness = manager.getBrightnessPercent();
                    setBrightness(brightness);
                }
            }
        });

        switchbuttonEyemode.setOnCheckedChangeListener(new SwitchButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(SwitchButton view, boolean isChecked) {
                manager.setReaderEyeMode(isChecked);
                EventBusUtils.sendMessage(EventConstant.REQUESTCODE_EYE_MODE_CHANGE);
            }
        });

        // 获取亮度seekbar
        seekbarBrightness.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                int progress = seekBar.getProgress();
                manager.setBrightnessPercent(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                switchbuttonSyslight.setChecked(false);
            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    setBrightness(progress);
                }
            }
        });
    }

    /**
     * 显示
     */
    public void show() {
        layoutBrightness.setTranslationY(layoutBrightness.getMeasuredHeight());
        layoutBrightness.animate().translationY(0).setListener(null);
        refreshData();
    }


    /**
     * 隐藏
     *
     * @param runnable runnable
     */
    public void hide(final Runnable runnable) {
        layoutBrightness.setTranslationY(0);
        layoutBrightness.animate().translationY(layoutBrightness.getMeasuredHeight()).setListener(new Animator.AnimatorListener() {
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
        //@ progress取值范围：[0, 100]，brightness显示范围：[0, 100];
        seekbarBrightness.setMax(100);
        // 是否保存了系统亮度模式
        boolean isSystemBrightnessMode = manager.getBrightnessSystem();
        switchbuttonSyslight.setChecked(isSystemBrightnessMode);

        boolean isEyeModeOpen = manager.getReaderEyeMode();
        switchbuttonEyemode.setChecked(isEyeModeOpen);

        //初始化不触发 系统CheckBox
        if (isSystemBrightnessMode) {
            setSystemBrightness();
        } else {
            int progress = manager.getBrightnessPercent();
            setBrightness(progress);
        }
    }

    private void setSystemBrightness() {
        float num1 = 100;
        float num2 = 255;
        int systemBrightness = ScreenUtils.getSystemScreenBrightness(AppConst.getApp());
        int progress = (int) ((systemBrightness * num1) / num2);

        if (progress > 100) {
            progress = 100;
        } else if (progress < 0) {
            progress = 0;
        }

        seekbarBrightness.setProgress(progress);
        textviewBrightnesspercent.setText(progress + "%");
        ScreenUtils.setAppScreenBrightnes((Activity) getContext(), -1);
    }

    private void setBrightness(int progress) {
        //修改亮度进度条
        seekbarBrightness.setProgress(progress);
        //根据黑白模式修改屏幕
        ScreenUtils.updateScreenBrightnessMask((Activity) getContext(), progress);
        //修改亮度进度Text
        textviewBrightnesspercent.setText(progress + "%");
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.imageView_brightnessDown) {
            int brightness = SettingManager.getInstance(getContext()).getBrightnessPercent();
            switchbuttonSyslight.setChecked(false);

            brightness = brightness - 5;
            if (brightness < 0) {
                brightness = 0;
            }
            setBrightness(brightness);
            SettingManager.getInstance(getContext()).setBrightnessPercent(brightness);
        } else if (id == R.id.imageView_brightnessUp) {
            int brightness = SettingManager.getInstance(getContext()).getBrightnessPercent();
            switchbuttonSyslight.setChecked(false);

            brightness = brightness + 5;
            if (brightness > 100) {
                brightness = 100;
            }
            setBrightness(brightness);
            SettingManager.getInstance(getContext()).setBrightnessPercent(brightness);
        } else if (id == R.id.textView_sysLight) {
            switchbuttonSyslight.toggle();
        }
    }
}
