package com.dzbook.view.reader;

import android.app.Activity;
import android.content.Context;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import com.dzbook.activity.reader.ReaderActivity;
import com.dzbook.lib.utils.ALog;
import com.dzbook.r.util.HwUtils;
import com.ishugui.R;

/**
 * 菜单
 *
 * @author wxliao on 18/4/18.
 */

public class ReaderNewPanel extends RelativeLayout implements Menuable {
    /**
     * MAIN
     */
    public static final int STATE_MAIN = 0x01;
    /**
     * 亮度
     */
    public static final int STATE_BRIGHTNESS = 0x02;
    /**
     * 语音
     */
    public static final int STATE_VOICE = 0x03;
    /**
     * 设置
     */
    public static final int STATE_SETTING = 0x04;
    /**
     * ANIM
     */
    public static final int STATE_ANIM = 0x05;
    /**
     * 自动阅读
     */
    public static final int STATE_AUTO_READ = 0x06;
    /**
     * 朗读时长
     */
    public static final int STATE_VOICE_TIME = 0x07;

    /**
     * menuState
     */
    public int menuState = STATE_MAIN;

    private FrameLayout layoutMenucontainer;

    private ReaderMenuMain readerMenuMain;
    private ReaderMenuBrightness readerMenuBrightness;
    private ReaderMenuVoice readerMenuVoice;
    private ReaderMenuSetting readerMenuSetting;
    private ReaderMenuAnim readerMenuAnim;
    private ReaderMenuAutoRead readerMenuAutoRead;
    private ReaderMenuVoiceTime readerMenuVoiceTime;

    private View leftPaddingView;
    private View rightPaddingView;
    private View bottomPaddingView;
    private int navigationBarSize;
    private Uri navigationBarUri;

    private int[] notchSize;

    private ContentObserver mNavigationStatusObserver = new ContentObserver(new Handler()) {
        @Override
        public void onChange(boolean selfChange) {
            resetPadding();
        }
    };


    /**
     * 构造
     *
     * @param context context
     */
    public ReaderNewPanel(@NonNull Context context) {
        this(context, null);
    }

    /**
     * 构造
     *
     * @param context context
     * @param attrs   attrs
     */
    public ReaderNewPanel(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    private void initView(Context context) {
        LayoutInflater.from(context).inflate(R.layout.view_reader_new_panel, this, true);
        layoutMenucontainer = findViewById(R.id.layout_menuContainer);

        leftPaddingView = findViewById(R.id.leftPaddingView);
        rightPaddingView = findViewById(R.id.rightPaddingView);
        bottomPaddingView = findViewById(R.id.bottomPaddingView);

        notchSize = HwUtils.getNotchSize();
        if (notchSize == null) {
            notchSize = new int[2];
            notchSize[0] = 0;
            notchSize[1] = 0;
        }

        navigationBarUri = HwUtils.getNavigationBarUri();
        navigationBarSize = HwUtils.getNavigationBarHeight(context);

        setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                getActivity().hideMenuPanel(true);
            }
        });


    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        getContext().getContentResolver().unregisterContentObserver(mNavigationStatusObserver);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        getContext().getContentResolver().registerContentObserver(navigationBarUri, true, mNavigationStatusObserver);
    }


    public void setState(int state) {
        menuState = state;
    }

    public int getState() {
        return menuState;
    }

    /**
     * 显示主菜单
     */
    public void showMainMenu() {
        setVisibility(View.VISIBLE);
        resetPadding();

        switch (menuState) {
            case STATE_MAIN:
                addReaderMenuMain();
                break;
            case STATE_BRIGHTNESS:
                addReaderMenuBrightness();
                break;
            case STATE_VOICE:
                addReaderMenuVoice();
                break;
            case STATE_SETTING:
                addReaderMenuSetting();
                break;
            case STATE_ANIM:
                addReaderMenuAnim();
                break;
            case STATE_AUTO_READ:
                addReaderMenuAutoRead();
                break;
            case STATE_VOICE_TIME:
                addReaderMenuVoiceTime();
                break;
            default:
                break;
        }
    }

    private void addReaderMenuMain() {
        applyScreen(1);
        if (readerMenuMain == null) {
            readerMenuMain = new ReaderMenuMain(getContext());
        }
        addMenu(readerMenuMain);
        readerMenuMain.show();
    }

    private void addReaderMenuBrightness() {
        applyScreen(2);
        if (readerMenuBrightness == null) {
            readerMenuBrightness = new ReaderMenuBrightness(getContext());
        }
        addMenu(readerMenuBrightness);
        readerMenuBrightness.show();
    }

    private void addReaderMenuVoice() {
        applyScreen(2);
        if (readerMenuVoice == null) {
            readerMenuVoice = new ReaderMenuVoice(getContext());
        }
        addMenu(readerMenuVoice);
        readerMenuVoice.show();
    }

    private void addReaderMenuSetting() {
        applyScreen(2);
        if (readerMenuSetting == null) {
            readerMenuSetting = new ReaderMenuSetting(getContext());
        }
        addMenu(readerMenuSetting);
        readerMenuSetting.show();
    }

    private void addReaderMenuAnim() {
        applyScreen(2);
        if (readerMenuAnim == null) {
            readerMenuAnim = new ReaderMenuAnim(getContext());
        }
        addMenu(readerMenuAnim);
        readerMenuAnim.show();
    }

    private void addReaderMenuAutoRead() {
        applyScreen(2);
        if (readerMenuAutoRead == null) {
            readerMenuAutoRead = new ReaderMenuAutoRead(getContext());
        }
        addMenu(readerMenuAutoRead);
        readerMenuAutoRead.show();
    }

    private void addReaderMenuVoiceTime() {
        applyScreen(2);
        if (readerMenuVoiceTime == null) {
            readerMenuVoiceTime = new ReaderMenuVoiceTime(getContext());
        }
        addMenu(readerMenuVoiceTime);
        readerMenuVoiceTime.show();
    }

    private void addMenu(View view) {
        layoutMenucontainer.removeAllViews();
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        layoutMenucontainer.addView(view, params);
        view.measure(0, 0);
    }

    /**
     * 全屏
     *
     * @param type type
     */
    public void applyScreen(int type) {
        ReaderActivity activity = (ReaderActivity) getContext();
        activity.applyFullscreen(type);
    }

    /**
     * 隐藏住菜单
     *
     * @param resetState resetState
     */
    public void hideMainMenu(boolean resetState) {
        if (getVisibility() != View.VISIBLE) {
            if (resetState) {
                setState(STATE_MAIN);
            }
            return;
        }
        if (layoutMenucontainer.getChildCount() == 0) {
            new HideRunnable(resetState).run();
            return;
        }
        View child = layoutMenucontainer.getChildAt(0);
        if (child instanceof ReaderMenuMain) {
            readerMenuMain.hide(new HideRunnable(resetState));
        } else if (child instanceof ReaderMenuBrightness) {
            readerMenuBrightness.hide(new HideRunnable(resetState));
        } else if (child instanceof ReaderMenuVoice) {
            readerMenuVoice.hide(new HideRunnable(resetState));
        } else if (child instanceof ReaderMenuSetting) {
            readerMenuSetting.hide(new HideRunnable(resetState));
        } else if (child instanceof ReaderMenuAnim) {
            readerMenuAnim.hide(new HideRunnable(resetState));
        } else if (child instanceof ReaderMenuAutoRead) {
            readerMenuAutoRead.hide(new HideRunnable(resetState));
        } else if (child instanceof ReaderMenuVoiceTime) {
            readerMenuVoiceTime.hide(new HideRunnable(resetState));
        }
    }

    private ReaderActivity getActivity() {
        return (ReaderActivity) getContext();
    }


    /**
     * 重置Padding
     */
    public void resetPadding() {
        boolean hasNavigationBar = HwUtils.hasNavigationBar(getContext());
        boolean isNavigationBarHide = HwUtils.isNavigationBarHide(getContext());
        boolean isInMultiWindow = false;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            isInMultiWindow = ((Activity) getContext()).isInMultiWindowMode();
        }
        ALog.eLwx("reset padding | hasNavigationBar:" + hasNavigationBar + ",isNavigationBarHide:" + isNavigationBarHide + ",isInMultiWindow:" + isInMultiWindow + ",navigationBarSize:" + navigationBarSize);
        ReaderActivity activity = (ReaderActivity) getContext();

        if (isInMultiWindow) {
            ViewGroup.LayoutParams paramsLeft = leftPaddingView.getLayoutParams();
            paramsLeft.width = 0;
            leftPaddingView.setLayoutParams(paramsLeft);

            ViewGroup.LayoutParams paramsRight = rightPaddingView.getLayoutParams();
            paramsRight.width = 0;
            rightPaddingView.setLayoutParams(paramsRight);

            ViewGroup.LayoutParams paramsBottom = bottomPaddingView.getLayoutParams();
            paramsBottom.height = 0;
            bottomPaddingView.setLayoutParams(paramsBottom);
        } else if (activity.isPortrait()) {
            ViewGroup.LayoutParams paramsLeft = leftPaddingView.getLayoutParams();
            paramsLeft.width = 0;
            leftPaddingView.setLayoutParams(paramsLeft);

            ViewGroup.LayoutParams paramsRight = rightPaddingView.getLayoutParams();
            paramsRight.width = 0;
            rightPaddingView.setLayoutParams(paramsRight);

            if (hasNavigationBar && !isNavigationBarHide) {
                ViewGroup.LayoutParams paramsBottom = bottomPaddingView.getLayoutParams();
                paramsBottom.height = navigationBarSize;
                bottomPaddingView.setLayoutParams(paramsBottom);
            } else {
                ViewGroup.LayoutParams paramsBottom = bottomPaddingView.getLayoutParams();
                paramsBottom.height = 0;
                bottomPaddingView.setLayoutParams(paramsBottom);
            }
        } else {
            ViewGroup.LayoutParams paramsBottom = bottomPaddingView.getLayoutParams();
            paramsBottom.height = 0;
            bottomPaddingView.setLayoutParams(paramsBottom);

            ViewGroup.LayoutParams paramsLeft = leftPaddingView.getLayoutParams();
            paramsLeft.width = notchSize[1];
            leftPaddingView.setLayoutParams(paramsLeft);

            if (hasNavigationBar && !isNavigationBarHide) {
                ViewGroup.LayoutParams paramsRight = rightPaddingView.getLayoutParams();
                paramsRight.width = navigationBarSize;
                rightPaddingView.setLayoutParams(paramsRight);
            } else {
                ViewGroup.LayoutParams paramsRight = rightPaddingView.getLayoutParams();
                paramsRight.width = 0;
                rightPaddingView.setLayoutParams(paramsRight);
            }
        }
    }

    @Override
    public void refreshData() {
        int childCount = layoutMenucontainer.getChildCount();
        if (childCount > 0) {
            for (int i = 0; i < childCount; i++) {
                View view = layoutMenucontainer.getChildAt(i);
                if (view instanceof Menuable) {
                    try {
                        ((Menuable) view).refreshData();
                    } catch (Exception e) {
                        ALog.printStackTrace(e);
                    }
                }
            }
        }
    }

    /**
     * HideRunnable
     */
    private class HideRunnable implements Runnable {
        private boolean resetState;

        public HideRunnable(boolean reset) {
            resetState = reset;
        }

        @Override
        public void run() {
            if (resetState) {
                setState(STATE_MAIN);
            }
            setVisibility(View.INVISIBLE);
            applyScreen(0);
        }
    }
}
