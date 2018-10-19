package com.dzbook.activity.reader;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;

import com.dzbook.BaseLoadActivity;
import com.dzbook.r.c.AkReaderView;
import com.dzbook.utils.SpUtil;
import com.ishugui.R;

/**
 * BaseReaderActivity
 *
 * @author liaowx
 */
public abstract class BaseReaderActivity extends BaseLoadActivity {

    /**
     * 不显示状态栏和导航栏，全屏布局（3846）
     */
    private static final int UI_FULL_SCREEN_LAYOUT_ALL = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
            | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;

    /**
     * 不显示状态栏，全屏布局（3844）
     */
    private static final int UI_HIDE_STATE_LAYOUT_ALL = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;

    /**
     * 显示状态栏和导航栏，全屏布局（3840）
     */
    private static final int UI_NORMAL_LAYOUT_ALL = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;

    /**
     * 显示状态栏和导航栏，正常布局（256）
     */
    private static final int UI_NORMAL_LAYOUT_NORMAL = View.SYSTEM_UI_FLAG_LAYOUT_STABLE;

    /**
     * 获取阅读器view
     *
     * @return reader view
     */
    protected abstract AkReaderView getReader();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            if (Build.VERSION.SDK_INT >= 18) {
                //api 18以上的 开启硬件加速
                getWindow().setFlags(
                        WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED,
                        WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED);
            }
        } catch (Exception e) {
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    /**
     * 如果有需要，显示阅读引导
     *
     * @param viewGroup viewGroup
     */
    public void showUserGuideIfNeed(ViewGroup viewGroup) {
        if (!SpUtil.getinstance(this).getBoolean(SpUtil.READER_IS_OPEN, false)) {
            //新手提示。
            final View noviceTips = getLayoutInflater().inflate(R.layout.ac_reader_novice_tips, null);
            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
            viewGroup.addView(noviceTips, params);
            SpUtil.getinstance(this).setBoolean(SpUtil.READER_IS_OPEN, true);
            noviceTips.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (noviceTips != null && noviceTips.getParent() != null) {
                        ViewGroup paret = (ViewGroup) noviceTips.getParent();
                        paret.removeView(noviceTips);
                    }
                }
            });
        }
    }

    /**
     * applyFullscreen
     *
     * @param showSys 0,隐藏顶部状态栏,底部虚拟按键；1，显示顶部状态栏，显示底部虚拟按键; 2，隐藏顶部状态栏，显示底部虚拟按键；
     */
    public void applyFullscreen(int showSys) {
        Window window = getWindow();
        if (window == null) {
            return;
        }

        boolean isInMultiWindow = false;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            isInMultiWindow = ((Activity) getContext()).isInMultiWindowMode();
        }

        View decorView = window.getDecorView();
        if (isInMultiWindow) {
            decorView.setSystemUiVisibility(UI_NORMAL_LAYOUT_NORMAL);
            return;
        }
        if (showSys == 0) {
            decorView.setSystemUiVisibility(UI_FULL_SCREEN_LAYOUT_ALL);
        } else if (showSys == 1) {
            decorView.setSystemUiVisibility(UI_NORMAL_LAYOUT_ALL);
        } else {
            decorView.setSystemUiVisibility(UI_HIDE_STATE_LAYOUT_ALL);
        }

    }

    /**
     * applyColorStyle
     *
     * @param index index
     */
    public void applyColorStyle(int index) {
        getReader().setColorStyle(index);
    }

    /**
     * applyLayoutStyle
     *
     * @param index index
     */
    public void applyLayoutStyle(int index) {
        getReader().setLayoutStyle(index);
    }


    /**
     * applyScreenOrientation
     *
     * @param orientation 横屏：ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE；
     *                    竖屏：ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
     * @return 是否已配置
     */
    public boolean applyScreenOrientation(int orientation) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N && isInMultiWindowMode()) {
            return false;
        }
        setRequestedOrientation(orientation);
        Window window = getWindow();
        if (window != null) {
            WindowManager.LayoutParams attrs = window.getAttributes();
            attrs.screenOrientation = orientation;
            window.setAttributes(attrs);
            return true;
        }
        return false;
    }

    public boolean isPortrait() {
        return getRequestedOrientation() == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
    }


    /**
     * apply font path
     *
     * @param fontPath 字体的路径
     * @return 是否apply
     */
    public boolean applyFont(String fontPath) {
        boolean result = getReader().setFonts(fontPath);
        getReader().update(true);
        return result;
    }

    /**
     * apply font size
     *
     * @param index index
     */
    public void applyFontSize(int index) {
        getReader().setFontSize(index);
    }

    /**
     * apply 阅读进度
     *
     * @param percent percent
     */
    public void applyProgress(float percent) {
        getReader().goToPercent(percent);
        getReader().postInvalidate();
    }

    /**
     * 配置版权icon
     *
     * @param bitmap bitmap
     */
    public void applyCopyrightImg(Bitmap bitmap) {
//        if (bitmap == null) {
//            BVConfig.copyrightImg = bitmap;
//            return;
//        }
//        // 定义矩阵对象
//        Matrix matrix = new Matrix();
//        // 缩放原图
////        float size = DimensionPixelUtil.dip2px(getActivity(), BVConfig.textHeaderDpSize + 1);
//        float size = DimensionPixelUtil.dip2px(getActivity(), 14f);
//        int it = (int) ((size) % bitmap.getHeight());
//        BigDecimal bg = new BigDecimal("0." + it);
//        float f1 = bg.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
//        matrix.postScale(1 - f1, 1 - f1);
//        Bitmap dstbmp = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
//        BVConfig.copyrightImg = dstbmp;
    }

    /**
     * 配置翻页动画模式
     *
     * @param index index
     */
    public void applyAnim(int index) {
        getReader().setAnimStyle(index);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
