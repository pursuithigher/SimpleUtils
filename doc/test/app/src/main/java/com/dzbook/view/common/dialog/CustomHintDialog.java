package com.dzbook.view.common.dialog;

import android.content.Context;
import android.graphics.Color;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.dzbook.AppConst;
import com.dzbook.r.c.SettingManager;
import com.dzbook.utils.DimensionPixelUtil;
import com.dzbook.utils.ImageUtils;
import com.dzbook.view.common.dialog.base.CustomDialogBusiness;

/**
 * Dialog： 默认提示文案
 */
public class CustomHintDialog extends CustomDialogBusiness {

    private TextView descView;

    /**
     * 构造
     *
     * @param context context
     */
    public CustomHintDialog(Context context) {
        super(context);
    }

    /**
     * 构造
     *
     * @param context  context
     * @param isCancel isCancel
     */
    public CustomHintDialog(Context context, boolean isCancel) {
        super(context, isCancel);
    }

    /**
     * 构造
     *
     * @param context context
     * @param style   style
     */
    public CustomHintDialog(Context context, int style) {
        super(context, style);
    }

    @Override
    protected View getView() {
        descView = new TextView(context);
        descView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        descView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 15);
        // 护眼模式适配
        if (SettingManager.getInstance(AppConst.getApp()).getReaderEyeMode()) {
            int readerEyeColor = ImageUtils.getBlueFilterColor();
            descView.setTextColor(ImageUtils.mixColor(Color.parseColor("#1A1A1A"), readerEyeColor));
        } else {
            descView.setTextColor(Color.parseColor("#1A1A1A"));
        }
        return descView;
    }

    @Override
    protected Object getConfirmEvent() {
        return descView.getText().toString();
    }

    @Override
    protected void getCancelEvent() {

    }

    /**
     * 描述文字
     *
     * @param desc desc
     */
    public void setDesc(CharSequence desc) {
        descView.setText(desc);
    }

    /**
     * 隐藏标题
     */
    public void hideTitleStyle() {
        descView.setPadding(0, DimensionPixelUtil.dip2px(context, 25), 0, 0);
    }

    public TextView getTextViewMessage() {
        return descView;
    }
}