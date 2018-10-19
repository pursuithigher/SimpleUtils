package com.dzbook.view.reader;

import android.animation.Animator;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dzbook.activity.reader.ReaderActivity;
import com.dzbook.lib.utils.CompatUtils;
import com.dzbook.r.c.SettingManager;
import com.ishugui.R;

/**
 * 阅读器设置
 *
 * @author wxliao on 18/4/18.
 */
public class ReaderMenuSetting extends FrameLayout implements View.OnClickListener, Menuable {
    private LinearLayout layoutSetting, layoutTextSize;
    private TextView textViewTextSize;
    private TextView textSizeUpView, textSizeDownView;

    private ImageView imageViewLayoutStyle0, imageViewLayoutStyle1, imageViewLayoutStyle2, imageViewLayoutStyle3;
    private TextView textViewColorStyle0, textViewColorStyle1, textViewColorStyle2, textViewColorStyle3, textViewSizeSmall, textViewSizeDef, textViewSizeBig;

    private TextView textViewOrientation;

    private SettingManager manager;

    /**
     * 构造
     *
     * @param context context
     */
    public ReaderMenuSetting(Context context) {
        this(context, null);
    }

    /**
     * 构造
     *
     * @param context context
     * @param attrs   attrs
     */
    public ReaderMenuSetting(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    private void initView(Context context) {
        LayoutInflater.from(context).inflate(R.layout.view_reader_menu_setting, this, true);
        layoutSetting = findViewById(R.id.layout_setting);

        textViewColorStyle0 = findViewById(R.id.textView_colorStyle0);
        textViewColorStyle1 = findViewById(R.id.textView_colorStyle1);
        textViewColorStyle2 = findViewById(R.id.textView_colorStyle2);
        textViewColorStyle3 = findViewById(R.id.textView_colorStyle3);

        textViewTextSize = findViewById(R.id.textView_textSize);
        textSizeDownView = findViewById(R.id.textView_textSizeDown);
        textSizeUpView = findViewById(R.id.textView_textSizeUp);

        imageViewLayoutStyle0 = findViewById(R.id.imageView_layoutStyle0);
        imageViewLayoutStyle1 = findViewById(R.id.imageView_layoutStyle1);
        imageViewLayoutStyle2 = findViewById(R.id.imageView_layoutStyle2);
        imageViewLayoutStyle3 = findViewById(R.id.imageView_layoutStyle3);

        textViewOrientation = findViewById(R.id.textView_orientation);

        textSizeDownView.setOnClickListener(this);
        textSizeUpView.setOnClickListener(this);

        layoutTextSize = findViewById(R.id.layout_textSize);
        textViewSizeSmall = findViewById(R.id.textView_sizeSmall);
        textViewSizeDef = findViewById(R.id.textView_sizeDef);
        textViewSizeBig = findViewById(R.id.textView_sizeBig);

        textViewSizeSmall.setOnClickListener(this);
        textViewSizeDef.setOnClickListener(this);
        textViewSizeBig.setOnClickListener(this);

        imageViewLayoutStyle0.setOnClickListener(this);
        imageViewLayoutStyle1.setOnClickListener(this);
        imageViewLayoutStyle2.setOnClickListener(this);
        imageViewLayoutStyle3.setOnClickListener(this);

        textViewColorStyle0.setOnClickListener(this);
        textViewColorStyle1.setOnClickListener(this);
        textViewColorStyle2.setOnClickListener(this);
        textViewColorStyle3.setOnClickListener(this);

        findViewById(R.id.menu_animStyle).setOnClickListener(this);
        findViewById(R.id.menu_autoRead).setOnClickListener(this);
        findViewById(R.id.menu_orientation).setOnClickListener(this);
        textViewOrientation.setOnClickListener(this);

        manager = SettingManager.getInstance(context);
    }

    /**
     * 显示
     *
     * @param runnable runnable
     */
    public void hide(final Runnable runnable) {
        layoutSetting.setTranslationY(0);
        hanlde(runnable);
    }

    /**
     * 隐藏
     */
    public void show() {
        layoutSetting.setTranslationY(layoutSetting.getMeasuredHeight());
        layoutSetting.animate().translationY(0).setListener(null);
        refreshData();
    }


    private void hanlde(final Runnable runnable) {
        layoutSetting.animate().translationY(layoutSetting.getMeasuredHeight()).setListener(new Animator.AnimatorListener() {
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

    private void resetColorStyleView(View view) {
        textViewColorStyle0.setEnabled(true);
        textViewColorStyle1.setEnabled(true);
        textViewColorStyle2.setEnabled(true);
        textViewColorStyle3.setEnabled(true);
        view.setEnabled(false);
    }

    private void resetLayoutStyleView(View view) {
        imageViewLayoutStyle0.setEnabled(true);
        imageViewLayoutStyle1.setEnabled(true);
        imageViewLayoutStyle2.setEnabled(true);
        imageViewLayoutStyle3.setEnabled(true);
        view.setEnabled(false);
    }

    private void resetTextSizeStyleView(View view) {
        textViewSizeBig.setSelected(false);
        textViewSizeDef.setSelected(false);
        textViewSizeSmall.setSelected(false);
        layoutTextSize.setSelected(false);
        textViewSizeDef.setTextColor(CompatUtils.getColor(getContext(), R.color.reader_menu_text_color_1));
        textViewSizeBig.setTextColor(CompatUtils.getColor(getContext(), R.color.reader_menu_text_color_1));
        textViewSizeSmall.setTextColor(CompatUtils.getColor(getContext(), R.color.reader_menu_text_color_1));
        view.setSelected(true);
        if (view instanceof TextView) {
            ((TextView) view).setTextColor(Color.parseColor("#F2B61F21"));
        }
    }

    @Override
    public void refreshData() {
        int fontIndex = manager.getFontSizeIndex();
        textViewTextSize.setText(String.valueOf(fontIndex + 1));
        setFontSizeStyleView();
        setLayoutStyleView();
        setColorStyleView();
    }

    private void setFontSizeStyleView() {
        int fontSizeStyle = manager.getFontSizeStyle();
        switch (fontSizeStyle) {
            case 0:
                resetTextSizeStyleView(layoutTextSize);
                break;
            case 1:
                resetTextSizeStyleView(textViewSizeSmall);
                break;
            case 2:
                resetTextSizeStyleView(textViewSizeDef);
                break;
            case 3:
                resetTextSizeStyleView(textViewSizeBig);
                break;
            default:
                break;
        }
    }

    private void setLayoutStyleView() {
        int layoutStyleIndex = manager.getLayoutStyleIndex();
        switch (layoutStyleIndex) {
            case 0:
                resetLayoutStyleView(imageViewLayoutStyle0);
                break;
            case 1:
                resetLayoutStyleView(imageViewLayoutStyle1);
                break;
            case 2:
                resetLayoutStyleView(imageViewLayoutStyle2);
                break;
            case 3:
                resetLayoutStyleView(imageViewLayoutStyle3);
                break;
            default:
                break;
        }
    }

    private void setColorStyleView() {
        int colorStyleIndex = manager.getColorStyleIndex();
        switch (colorStyleIndex) {
            case 0:
                resetColorStyleView(textViewColorStyle0);
                break;
            case 1:
                resetColorStyleView(textViewColorStyle1);
                break;
            case 2:
                resetColorStyleView(textViewColorStyle2);
                break;
            case 3:
                resetColorStyleView(textViewColorStyle3);
                break;
            default:
                break;
        }
    }

    /**
     * 字号增大
     *
     * @return new size index
     */
    private int setTextSizeUp() {
        int textSizeIndex = manager.getFontSizeIndex() + 1;
        if (textSizeIndex < 0) {
            textSizeIndex = 0;
        } else if (textSizeIndex >= SettingManager.getFontSizeIndexRange()) {
            textSizeIndex = SettingManager.getFontSizeIndexRange() - 1;
        }
        applyTextSize(textSizeIndex, 0);
        return textSizeIndex;
    }

    /**
     * 字号减小
     *
     * @return new size index
     */
    private int setTextSizeDown() {
        int textSizeIndex = manager.getFontSizeIndex() - 1;
        if (textSizeIndex < 0) {
            textSizeIndex = 0;
        } else if (textSizeIndex >= SettingManager.getFontSizeIndexRange()) {
            textSizeIndex = SettingManager.getFontSizeIndexRange() - 1;
        }
        applyTextSize(textSizeIndex, 0);
        return textSizeIndex;
    }


    private void applyTextSize(int textSizeIndex, int fontSizeStyle) {
        manager.setFontSizeIndex(textSizeIndex);
        manager.setFontSizeStyle(fontSizeStyle);
        ReaderActivity activity = (ReaderActivity) getContext();
        activity.applyFontSize(textSizeIndex);
    }

    private ReaderActivity getActivity() {
        return (ReaderActivity) getContext();
    }

    private void showAnimMenu() {
        ReaderActivity activity = (ReaderActivity) getContext();
        activity.setMenuState(ReaderNewPanel.STATE_ANIM);
        activity.showMenuPanel();
    }

    private void startAutoRead() {
        int animIndex = manager.getAutoReadIndex();
        int animSpeed = manager.getAutoReadSpeed();
        getActivity().startAutoRead(animIndex, animSpeed, true);
    }

    private void applyColorStyle(int index, View view) {
        resetColorStyleView(view);
        getActivity().applyColorStyle(index);
        manager.setReaderNightMode(false);
        manager.setColorStyleIndex(index);
    }

    private void applyLayoutStyle(int index, View view) {
        resetLayoutStyleView(view);
        getActivity().applyLayoutStyle(index);
        manager.setLayoutStyleIndex(index);
    }

    private void applyOrientation() {
        ReaderActivity activity = (ReaderActivity) getContext();
        if (activity.isPortrait()) {
            boolean success = activity.applyScreenOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            if (success) {
                textViewOrientation.setText(R.string.reader_orientation_portrait);
            }
        } else {
            boolean success = activity.applyScreenOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            if (success) {
                textViewOrientation.setText(R.string.reader_orientation_landscape);
            }
        }
        activity.hideMenuPanel(true);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (isTextSizeId(id)) {
            dealTextSize(id);
        } else if (isReaderBg(id)) {
            dealReaderBg(view, id);
        } else if (isLayoutStyle(id)) {
            dealLayoutStyle(view, id);
        } else if (id == R.id.menu_animStyle) {
            showAnimMenu();
        } else if (id == R.id.menu_autoRead) {
            startAutoRead();
        } else if (id == R.id.menu_orientation) {
            applyOrientation();
        }
    }

    private boolean isLayoutStyle(int id) {
        // 样式：
        return id == R.id.imageView_layoutStyle0 || id == R.id.imageView_layoutStyle1 || id == R.id.imageView_layoutStyle2 || id == R.id.imageView_layoutStyle3;
    }


    private void dealLayoutStyle(View view, int id) {
        // 样式：
        if (id == R.id.imageView_layoutStyle0) {
            applyLayoutStyle(0, view);
        } else if (id == R.id.imageView_layoutStyle1) {
            applyLayoutStyle(1, view);
        } else if (id == R.id.imageView_layoutStyle2) {
            applyLayoutStyle(2, view);
        } else if (id == R.id.imageView_layoutStyle3) {
            applyLayoutStyle(3, view);
        }
    }

    private boolean isTextSizeId(int id) {
        return id == R.id.textView_textSizeDown || id == R.id.textView_textSizeUp || id == R.id.textView_sizeSmall || id == R.id.textView_sizeDef || id == R.id.textView_sizeBig;
    }

    /**
     * 设置字号
     *
     * @param id id
     */
    private void dealTextSize(int id) {
        // 字号：
        textSizeUpView.setTextColor(getResources().getColor(R.color.reader_menu_text_color_1));
        textSizeDownView.setTextColor(getResources().getColor(R.color.reader_menu_text_color_1));
        if (id == R.id.textView_textSizeDown) {
            int value = setTextSizeDown() + 1;
            textViewTextSize.setText(String.valueOf(value));
            resetTextSizeStyleView(layoutTextSize);
            textSizeUpView.setTextColor(getResources().getColor(R.color.reader_menu_style_checked));
            textSizeDownView.setTextColor(getResources().getColor(R.color.reader_menu_style_checked));
        } else if (id == R.id.textView_textSizeUp) {
            int value = setTextSizeUp() + 1;
            textViewTextSize.setText(String.valueOf(value));
            resetTextSizeStyleView(layoutTextSize);
            textSizeUpView.setTextColor(getResources().getColor(R.color.reader_menu_style_checked));
            textSizeDownView.setTextColor(getResources().getColor(R.color.reader_menu_style_checked));
        } else if (id == R.id.textView_sizeSmall) {
            int index = manager.getSmallTextIndex();
            textViewTextSize.setText(String.valueOf(index + 1));
            applyTextSize(index, 1);
            resetTextSizeStyleView(textViewSizeSmall);
        } else if (id == R.id.textView_sizeDef) {
            int index = manager.getDefaultTextIndex();
            textViewTextSize.setText(String.valueOf(index + 1));
            applyTextSize(index, 2);
            resetTextSizeStyleView(textViewSizeDef);
        } else if (id == R.id.textView_sizeBig) {
            int index = manager.getBigTextIndex();
            textViewTextSize.setText(String.valueOf(index + 1));
            applyTextSize(index, 3);
            resetTextSizeStyleView(textViewSizeBig);
        }
    }

    private boolean isReaderBg(int id) {
        return id == R.id.textView_colorStyle0 || id == R.id.textView_colorStyle1 || id == R.id.textView_colorStyle2 || id == R.id.textView_colorStyle3;
    }

    /**
     * 设置背景
     *
     * @param view view
     * @param id   id
     */
    private void dealReaderBg(View view, int id) {
        // 背景：
        if (id == R.id.textView_colorStyle0) {
            applyColorStyle(0, view);
        } else if (id == R.id.textView_colorStyle1) {
            applyColorStyle(1, view);
        } else if (id == R.id.textView_colorStyle2) {
            applyColorStyle(2, view);
        } else if (id == R.id.textView_colorStyle3) {
            applyColorStyle(3, view);
        }
    }
}
