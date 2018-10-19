package com.dzbook.view.common.dialog.base;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dzbook.AppConst;
import com.dzbook.lib.utils.CompatUtils;
import com.dzbook.r.c.SettingManager;
import com.dzbook.utils.DimensionPixelUtil;
import com.dzbook.utils.ImageUtils;
import com.dzbook.utils.TypefaceUtils;
import com.ishugui.R;

import hw.sdk.utils.Tools;

/**
 * DialogBase： View相关封装
 *
 * @author wangjianchen
 */
public class CustomDialogBase extends CustomAlertDialogParent {

    private TextView titleView;
    private LinearLayout centerLayout;
    private View bottomLayout;
    private TextView confirmView;
    private TextView cancelView;

    private String title;
    private String confirmTxt;
    private String cancelTxt;
    private boolean hideConfirm;
    private boolean hideBottomLayout;
    private OnBaseCheckListener checkListener;

    private View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (checkListener != null) {
                switch (v.getId()) {
                    case R.id.confirm:
                        checkListener.baseClickConfirm();
                        break;
                    case R.id.cancel:
                        checkListener.baseClickCancel();
                        break;
                    default:
                        break;
                }
            }
            dismiss();
        }
    };

    /**
     * 构造
     *
     * @param context context
     */
    public CustomDialogBase(Context context) {
        super(context);
        init();
    }

    /**
     * 构造
     *
     * @param context  context
     * @param resStyle resStyle
     */
    public CustomDialogBase(Context context, int resStyle) {
        super(context, resStyle);
        init();
    }

    /**
     * 初始化
     */
    protected void init() {
        this.hideConfirm = false;
        this.hideBottomLayout = false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.item_dialog_view);
        bindData();
        setOnCancelListener(new OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                if (checkListener != null) {
                    checkListener.baseClickCancel();
                }
            }
        });
    }

    private void bindData() {
        titleView = findViewById(R.id.tv_title);
        centerLayout = findViewById(R.id.dialog_center_layout);
        bottomLayout = findViewById(R.id.tv_bottom);
        confirmView = findViewById(R.id.confirm);
        cancelView = findViewById(R.id.cancel);
        TypefaceUtils.setHwChineseMediumFonts(titleView);
        TypefaceUtils.setHwChineseMediumFonts(confirmView);
        TypefaceUtils.setHwChineseMediumFonts(cancelView);
        // 横屏模式margin适配
        if (Tools.isLandscape(getContext())) {
            int marginDimen = DimensionPixelUtil.dip2px(getContext(), 8);
            int marginPad = DimensionPixelUtil.dip2px(getContext(), 52);
            FrameLayout frameLayout = findViewById(R.id.rootView);
            FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) frameLayout.getLayoutParams();
            layoutParams.setMargins(marginPad, marginDimen, marginPad, marginDimen);
            findViewById(R.id.rootView).setLayoutParams(layoutParams);
        }
        // 参数适配
        if (!TextUtils.isEmpty(title)) {
            titleView.setVisibility(View.VISIBLE);
            titleView.setText(title);
        } else {
            centerLayout.setPadding(0, DimensionPixelUtil.dip2px(getContext(), 24), 0, DimensionPixelUtil.dip2px(getContext(), 56));
        }
        if (hideConfirm) {
            confirmView.setVisibility(View.GONE);
            int padding = DimensionPixelUtil.dip2px(getContext(), 24);
            cancelView.setPadding(padding, 0, padding, 0);
        } else {
            int padding = DimensionPixelUtil.dip2px(getContext(), 12);
            cancelView.setPadding(padding, 0, padding, 0);
        }
        if (!TextUtils.isEmpty(confirmTxt)) {
            confirmView.setText(confirmTxt);
        }
        if (!TextUtils.isEmpty(cancelTxt)) {
            cancelView.setText(cancelTxt);
        }
        if (hideBottomLayout) {
            bottomLayout.setVisibility(View.GONE);
            centerLayout.setPadding(0, centerLayout.getPaddingTop(), 0, DimensionPixelUtil.dip2px(getContext(), 24));
        } else {
            bottomLayout.setVisibility(View.VISIBLE);
            centerLayout.setPadding(0, centerLayout.getPaddingTop(), 0, DimensionPixelUtil.dip2px(getContext(), 56));
        }
        confirmView.setOnClickListener(clickListener);
        cancelView.setOnClickListener(clickListener);
        // 护眼模式适配
        if (SettingManager.getInstance(AppConst.getApp()).getReaderEyeMode()) {
            int readerEyeColor = ImageUtils.getBlueFilterColor();
            GradientDrawable drawable = new GradientDrawable();
            drawable.setColor(ImageUtils.mixColor(Color.parseColor("#ffffff"), readerEyeColor));
            drawable.setCornerRadius(DimensionPixelUtil.dip2px(getContext(), 11));
            findViewById(R.id.rootView).setBackground(drawable);
            confirmView.setTextColor(ImageUtils.mixColor(CompatUtils.getColor(getContext(), R.color.color_100_D0021B), readerEyeColor));
            cancelView.setTextColor(ImageUtils.mixColor(CompatUtils.getColor(getContext(), R.color.color_100_D0021B), readerEyeColor));
            titleView.setTextColor(ImageUtils.mixColor(CompatUtils.getColor(getContext(), R.color.color_100_1A1A1A), readerEyeColor));
        }
    }


    /**
     * 设置标题
     *
     * @param title title
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * 设置中心内容
     *
     * @param view view
     */
    public void setCenterView(View view) {
        if (view != null) {
            centerLayout.removeAllViews();
            centerLayout.addView(view);
        }
    }

    /**
     * 隐藏确定
     */
    public void hideConfim() {
        hideConfirm = true;
    }

    /**
     * 隐藏底部控制
     */
    public void hideBottomLayout() {
        hideBottomLayout = true;
    }

    /**
     * 设置确认文案
     *
     * @param txt txt
     */
    public void setConfirmTxt(String txt) {
        confirmTxt = txt;
    }

    /**
     * 设置取消文案
     *
     * @param txt txt
     */
    public void setCancelTxt(String txt) {
        cancelTxt = txt;
    }

    /**
     * 设置确认，取消监听
     *
     * @param listener listener
     */
    public void setBaseCheckListener(final OnBaseCheckListener listener) {
        if (listener != null) {
            this.checkListener = listener;
        }
    }

    /**
     * 监听接口
     */
    public interface OnBaseCheckListener {
        /**
         * 确认
         */
        void baseClickConfirm();

        /**
         * 取消
         */
        void baseClickCancel();
    }
}

