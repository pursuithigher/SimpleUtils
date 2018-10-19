package com.dzbook.dialog.common;

import android.content.Context;
import android.database.ContentObserver;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.dzbook.AppConst;
import com.dzbook.lib.utils.ALog;
import com.dzbook.lib.utils.CompatUtils;
import com.dzbook.r.c.SettingManager;
import com.dzbook.r.util.HwUtils;
import com.dzbook.utils.DimensionPixelUtil;
import com.dzbook.utils.ImageUtils;
import com.dzbook.view.common.dialog.base.CustomAlertDialogParent;
import com.ishugui.R;

import huawei.widget.HwProgressBar;

/**
 * loading dialog
 *
 * @author wangjc
 */
public class DialogLoading extends CustomAlertDialogParent {
    private static final long MAX_SHOW_INTERVAL = 1000;

    private TextView loadingText;

    private View rootLayout;
    private View conentLayout;

    private long lastDetailTime;

    private HwProgressBar loadingView;

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
    public DialogLoading(Context context) {
        super(context, R.style.dialog_normal);
        setContentView(R.layout.dialog_load_bottom);
        setProperty();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
    }

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
        initView();
        initData();
        setListener();
    }

    private void initView() {
        loadingText = findViewById(R.id.loading_text);
        rootLayout = findViewById(R.id.layout);
        conentLayout = findViewById(R.id.layout_content_view);
        loadingView = findViewById(R.id.loadingview);

        if (getWindow() != null) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
        }

    }

    private void initData() {
        this.setCancelable(true);
        this.setCanceledOnTouchOutside(true);
    }

    private void setListener() {
    }

    @Override
    public void show() {
        superShow();
    }

    /**
     * 显示
     */
    public void showLight() {
        superShow();
    }

    /**
     * 显示
     */
    public void showTransparent() {
        superShow();
    }


    @Override
    public void dismiss() {
        super.dismiss();
        loadingView.setVisibility(View.GONE);
    }

    /**
     * 修改 dialog 提示语。
     *
     * @param text 提示语文字。
     */
    public void setShowMsg(CharSequence text) {
        loadingText.setText(text);
    }

    /**
     * 封装show代码
     */
    private void superShow() {
        setReaderEyeMode();
        try {
            final long thisTime = System.currentTimeMillis();
            if (thisTime - lastDetailTime > MAX_SHOW_INTERVAL) {
                super.show();
                lastDetailTime = thisTime;
                loadingView.setVisibility(View.VISIBLE);
            }
        } catch (Exception e) {
            ALog.printStackTrace(e);
        }
    }

    private void setReaderEyeMode() {
        // 护眼模式适配
        if (SettingManager.getInstance(AppConst.getApp()).getReaderEyeMode()) {
            int readerEyeColor = ImageUtils.getBlueFilterColor();
            GradientDrawable drawable = new GradientDrawable();
            drawable.setColor(ImageUtils.mixColor(Color.parseColor("#ffffff"), readerEyeColor));
            drawable.setCornerRadius(DimensionPixelUtil.dip2px(getContext(), 11));
            conentLayout.setBackground(drawable);
            loadingText.setTextColor(ImageUtils.mixColor(CompatUtils.getColor(getContext(), R.color.color_100_1A1A1A), readerEyeColor));
        }
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        getContext().getContentResolver().registerContentObserver(HwUtils.getNavigationBarUri(), true, mNavigationStatusObserver);

    }

    @Override
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        getContext().getContentResolver().unregisterContentObserver(mNavigationStatusObserver);
    }



    /**
     * 重置Padding
     */
    private void resetPadding() {
        boolean isNavigationBarHide = HwUtils.isNavigationBarHide(getContext());
        if (isNavigationBarHide) {
            // 隐藏虚拟控制
            rootLayout.setPadding(0, 0, 0, 0);
        } else {
            // 展开虚拟控制
            rootLayout.setPadding(0, 0, 0, DimensionPixelUtil.dip2px(getContext(), 30));
        }
    }
}
