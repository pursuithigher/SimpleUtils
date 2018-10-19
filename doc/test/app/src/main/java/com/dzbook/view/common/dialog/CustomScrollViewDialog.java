package com.dzbook.view.common.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.dzbook.activity.hw.PrivacyActivity;
import com.dzbook.lib.utils.CompatUtils;
import com.dzbook.model.ModelAction;
import com.dzbook.net.hw.RequestCall;
import com.dzbook.utils.DeviceInfoUtils;
import com.dzbook.utils.DimensionPixelUtil;
import com.dzbook.utils.SpUtil;
import com.dzbook.utils.TypefaceUtils;
import com.dzbook.utils.hw.CustomLinkMovementMethod;
import com.ishugui.R;

/**
 * 滚动dialog
 *
 * @author winzows 2018/5/9
 */
public class CustomScrollViewDialog extends Dialog {


    private static final int MIN_HEIGHT_DP = 300;
    private OnDialogClickCallBack listener;
    private TextView fourSection;
    private Context context;
    private TextView cancel;
    private TextView confirm;
    private Window activityWindow;
    private int screenHeight = 0;
    private int minHeight = 0;
    private DeviceInfoUtils infoUtils;

    /**
     * CustomScrollViewDialog
     * @param context context
     * @param window window
     */
    public CustomScrollViewDialog(Context context, Window window) {
        super(context, R.style.cmt_dialog);
        this.context = context;
        this.activityWindow = window;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        infoUtils = DeviceInfoUtils.getInstanse();
        screenHeight = infoUtils.getHeightReturnInt();
        minHeight = DimensionPixelUtil.dip2px(getContext(), MIN_HEIGHT_DP);
        setContentView(R.layout.splash_dialog_layout);
        fourSection = findViewById(R.id.four_section);
        TextView tvTitle = findViewById(R.id.tv_title);
        cancel = findViewById(R.id.cancel);
        confirm = findViewById(R.id.confirm);
        TypefaceUtils.setHwChineseMediumFonts(tvTitle);
        TypefaceUtils.setHwChineseMediumFonts(cancel);
        TypefaceUtils.setHwChineseMediumFonts(confirm);
        initSpan();
        setCanceledOnTouchOutside(false);
        setCancelable(false);
        initListener();
    }


    /**
     * 初始化文字 点击效果等
     */
    private void initSpan() {
        final Typeface hwChineseMedium = TypefaceUtils.getHwChineseMedium();
        Resources mResource = context.getResources();

        String agreeTips = mResource.getString(R.string.splash_agree_content9);
        final String userAgreeTxt = mResource.getString(R.string.splash_agree_content10);
        final String readAgreeTxt = mResource.getString(R.string.splash_agree_content12);
        String allTips = agreeTips + userAgreeTxt + "、" + readAgreeTxt + "。";

        int agreeTipsLength = agreeTips.length();
        int userAgreeTxtLength = userAgreeTxt.length();
        int readAgreeTxtLength = readAgreeTxt.length();

        int index1 = agreeTipsLength + userAgreeTxtLength;

        SpannableString s = new SpannableString(allTips);
        final int color = CompatUtils.getColor(context, R.color.color_100_CD2325);
        s.setSpan(new ForegroundColorSpan(color), agreeTipsLength, index1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        s.setSpan(new ForegroundColorSpan(color), index1 + 1, index1 + 1 + readAgreeTxtLength, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        s.setSpan(new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                PrivacyActivity.show(context, RequestCall.getUrlAgreement(), userAgreeTxt);
            }


            @Override
            public void updateDrawState(TextPaint ds) {
                super.updateDrawState(ds);
                ds.setColor(color);
                if (hwChineseMedium != null) {
                    ds.setTypeface(hwChineseMedium);
                }
                ds.setUnderlineText(false);
            }
        }, agreeTipsLength, index1, 0);


        s.setSpan(new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                PrivacyActivity.show(context, RequestCall.getUrlPrivacyPolicy(), readAgreeTxt);
            }

            @Override
            public void updateDrawState(TextPaint ds) {
                super.updateDrawState(ds);
                ds.setColor(color);
                if (hwChineseMedium != null) {
                    ds.setTypeface(hwChineseMedium);
                }
                ds.setUnderlineText(false);
            }
        }, index1 + 1, index1 + 1 + readAgreeTxtLength, 0);

        fourSection.setText(s);
        CustomLinkMovementMethod instance = CustomLinkMovementMethod.getInstance();
        fourSection.setMovementMethod(instance);
        fourSection.setHighlightColor(Color.TRANSPARENT);
    }

    private void initListener() {
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                ModelAction.exitApp((Activity) context, false);
            }
        });

        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SpUtil.getinstance(context).setSignAgreement(true);
                dismiss();
                if (listener != null) {
                    listener.onClickConfirm();
                }
            }
        });
    }

    @Override
    public void show() {
        try {
            super.show();
            if (activityWindow != null) {
                View decorView = activityWindow.getDecorView();
                if (decorView != null) {
                    int decorViewHeight = decorView.getMeasuredHeight();
                    setWindowHeight(decorViewHeight);
                } else {
                    setWindowHeight(screenHeight);
                }
            } else {
                setWindowHeight(screenHeight);
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    private void setWindowHeight(int windowHeight) {
        Window window = getWindow();
        if (window != null) {
            if (windowHeight >= screenHeight - infoUtils.getStatusBarHeight() * 5) {
                windowHeight = screenHeight;
            }
            if (windowHeight <= minHeight) {
                windowHeight = minHeight;
            }

            WindowManager.LayoutParams attributes = window.getAttributes();
            int margin = DimensionPixelUtil.dip2px(context, 4);
            attributes.width = infoUtils.getWidthReturnInt() - (margin * 2);
            attributes.height = windowHeight * 2 / 3;
            Log.d("tag_wz", "decorView height-->" + windowHeight);
            attributes.gravity = Gravity.BOTTOM;
            attributes.y = margin;
            window.setAttributes(attributes);
        }
    }


    @Override
    public void dismiss() {
        try {
            super.dismiss();
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    /**
     * onConfigurationChanged
     * @param screenHeightDp screenHeightDp
     */
    public void onConfigurationChanged(int screenHeightDp) {
        setWindowHeight(DimensionPixelUtil.dip2px(getContext(), screenHeightDp));
    }

    /**
     * 接口
     */
    public interface OnDialogClickCallBack {
        /**
         * 当点击确定按钮
         */
        void onClickConfirm();
    }

    public void setOnClickCallback(OnDialogClickCallBack listener1) {
        this.listener = listener1;
    }

}
